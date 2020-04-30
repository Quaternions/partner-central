package jtv.builder.partner;

import com.jtv.test.db.entity.DbEmailAddress;
import com.jtv.test.db.entity.DbPhoneNumber;
import com.jtv.test.db.entity.entitlement.DbUserAccount;
import com.jtv.test.db.entity.partner.*;
import com.jtv.test.db.query.DbEmailAddressQueryBuilder;
import com.jtv.test.db.query.DbPhoneNumberQueryBuilder;
import com.jtv.test.db.query.partner.*;
import com.jtv.test.utils.jdbc.JtvJdbcTemplate;
import jtv.builder.user.UserAccountRunnableBuilder;
import jtv.dao.entity.partner.AssociateType;
import jtv.dao.entity.partner.PartnerStatus;
import jtv.dao.entity.partner.SiteUsageType;
import jtv.dao.partner.PartnerDao;
import jtv.exception.KeywordNotDefinedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class PartnerBuilderTest {
    private static final Logger log = LoggerFactory.getLogger(PartnerBuilderTest.class);

    private final PartnerBuilder partnerBuilder;
    private final PartnerDao partnerDao;
    private final JtvJdbcTemplate jdbcTemplate;

    PartnerBuilderTest(){
        this.partnerBuilder = new PartnerBuilder(new PartnerDao(), new PartnerAddressBuilder());
        this.partnerDao = new PartnerDao();
        this.jdbcTemplate = partnerDao.getJtvJdbcTemplate();
    }


    @Test
    public void testBuildPartnerInvalid() throws KeywordNotDefinedException{

        String partnerStatus = "NADA";
        log.info(String.format("Testing BuildPartner with PatnerStatus code [%s].", partnerStatus));
        //Method under test
        try {
            DbPartner dbPartnerExpected = partnerBuilder.buildPartner(partnerStatus);
        }catch(KeywordNotDefinedException keywordException){
            return;
        }
        Assert.fail("Should have failed with a keyword exception.");
    }

    @Test
    public void testBuildPartnerValid() throws KeywordNotDefinedException{

        for(PartnerStatus ps: PartnerStatus.values()) {
            String partnerStatus = ps.name();
            log.info(String.format("Testing BuildPartner with PatnerStatus code [%s].", partnerStatus));
            executeAndVerifyPartnerBuilder(partnerStatus);
        }
    }

    private void executeAndVerifyPartnerBuilder(String partnerStatus) throws KeywordNotDefinedException{
        //Method under test
        DbPartner dbPartnerExpected = partnerBuilder.buildPartner(partnerStatus);

        //Verification
        assertNotNull(dbPartnerExpected,"Partner builder failed to return a partner.");

        /*
        cardal1: This looks and is possibly silly since test-fixture is building the partner then I am turning around and
        using test-fixture to fetch the partner. Nevertheless, I am including this to ensure the partner actually got created successfully on
        the off chance that is possible.
         */
        DbPartner dbPartnerActual = DbPartnerQueryBuilder.defaultInstance(jdbcTemplate).withPartnerId(dbPartnerExpected.getPartnerId()).queryForObject();
        assertNotNull(dbPartnerActual,"Partner builder failed to return a partner.");
        Assert.assertEquals(dbPartnerActual.getPartnerStatusId(), DbPartnerStatusQueryBuilder.defaultInstance(jdbcTemplate)
                .withCode(partnerStatus).queryForObject().getPartnerStatusId());
        Assert.assertEquals(dbPartnerActual.getPartnerStatusId(), dbPartnerExpected.getPartnerStatusId());
        Assert.assertEquals(dbPartnerActual.getLegalName(), dbPartnerExpected.getLegalName());
        Assert.assertEquals(dbPartnerActual.getOperatingName(), dbPartnerExpected.getOperatingName());
        assertNotNull(dbPartnerActual.getUuid());
        Assert.assertEquals(dbPartnerActual.getUuid(), dbPartnerExpected.getUuid());
    }

    @Test
    public void testBuildPartnerSiteInvalid() throws KeywordNotDefinedException{
        String siteUsageCode = "USE_THIS_SUCKER";
        log.info(String.format("Testing BuildPartnerSite for siteUsageType [%s].", siteUsageCode));

        DbPartner dbPartner = partnerBuilder.buildPartner(PartnerStatus.ACTIVE.name());
        //Method under test
        try {
            partnerBuilder.buildPartnerSite(siteUsageCode, dbPartner);
        }catch(KeywordNotDefinedException keywordException){
            return;
        }
        Assert.fail("Should have thrown a keyword exception.");

    }

    @Test
    public void testBuildPartnerSiteValid() throws KeywordNotDefinedException{
        for(SiteUsageType sut: SiteUsageType.values()){
            String siteUsageCode = sut.name();
            log.info(String.format("Testing BuildPartnerSite for siteUsageType [%s].", siteUsageCode));
            executeAndVerifySiteBuilder(null, siteUsageCode);
        }
    }

    private void executeAndVerifySiteBuilder(DbPartner dbPartner, String siteUsageCode) throws KeywordNotDefinedException {
        //Setup
        if(dbPartner == null) {
            dbPartner = partnerBuilder.buildPartner(PartnerStatus.ACTIVE.name());
        }

        //Method under test
        partnerBuilder.buildPartnerSite(siteUsageCode, dbPartner);

        //Verification
        DbSite dbSiteActual = DbSiteQueryBuilder.defaultInstance(jdbcTemplate).withPartnerId(dbPartner.getPartnerId())
                .queryForObject();

        assertNotNull(dbSiteActual);
        Assert.assertEquals(dbSiteActual.getActiveFlag(), "Y");

        DbSiteUsage dbSiteUsage = DbSiteUsageQueryBuilder.defaultInstance(jdbcTemplate).withSiteId(dbSiteActual.getSiteId())
                .queryForObject();
        Assert.assertEquals(dbSiteUsage.getSiteUsageTypeId(), DbSiteUsageTypeQueryBuilder.defaultInstance(jdbcTemplate)
                .withCode(siteUsageCode).queryForObject().getSiteUsageTypeId());
    }

    @Test
    public void testBuildPartnerAssociateInvalid() throws KeywordNotDefinedException{
        String associateType = "BOGUS";
        //Need a user
        UserAccountRunnableBuilder uarb = new UserAccountRunnableBuilder("valid");
        uarb.run();
        DbUserAccount dbUserAccount = uarb.getFinalDbUserAccount();

        //Need a partner
        DbPartner dbPartner = partnerBuilder.buildPartner(PartnerStatus.ACTIVE.name());

        //Method under test
        try {
            DbAssociate dbAssociateExpected = partnerBuilder.buildPartnerAssociate(associateType, dbPartner, dbUserAccount);
        }catch (KeywordNotDefinedException keywordException){
            //Expecting this exception
            return;
        }
        Assert.fail("Invalid parameter should have caused Keyword exception.");
    }
    @Test
    public void testBuildPartnerAssociateValid() throws KeywordNotDefinedException{
        for(AssociateType at: AssociateType.values()) {
            String associateType = at.name();
            log.info(String.format("Testing BuildPartnerAssociate for associateType [%s].", associateType));
            executeAndValidateBuildPartnerAssociate(associateType);
        }
    }

    private void executeAndValidateBuildPartnerAssociate(String associateType) throws KeywordNotDefinedException {
        //Need a user
        UserAccountRunnableBuilder uarb = new UserAccountRunnableBuilder("valid");
        uarb.run();
        DbUserAccount dbUserAccount = uarb.getFinalDbUserAccount();

        //Need a partner
        DbPartner dbPartner = partnerBuilder.buildPartner(PartnerStatus.ACTIVE.name());

        //Method under test
        DbAssociate dbAssociateExpected = partnerBuilder.buildPartnerAssociate(associateType, dbPartner, dbUserAccount);

        //Validation
        assertNotNull(dbAssociateExpected);
        Assert.assertEquals(dbAssociateExpected.getDeleted(), "N");
        Assert.assertEquals(dbAssociateExpected.getPartnerId(), dbPartner.getPartnerId());

        DbAssociate dbAssociateActual = DbAssociateQueryBuilder.defaultInstance(jdbcTemplate)
                .withAssociateId(dbAssociateExpected.getAssociateId()).queryForObject();
        assertNotNull(dbAssociateActual);
        Assert.assertEquals(dbAssociateActual.getAssociateTypeId(),
                DbAssociateTypeQueryBuilder.defaultInstance(jdbcTemplate).withCode(associateType).queryForObject().getAssociateTypeId());
        Assert.assertEquals(dbAssociateActual.getAssociateTypeId(), dbAssociateExpected.getAssociateTypeId());

        Assert.assertEquals(dbAssociateActual.getFirstName(), dbAssociateExpected.getFirstName());
        Assert.assertEquals(dbAssociateActual.getLastName(), dbAssociateExpected.getLastName());
        Assert.assertEquals(dbAssociateActual.getDeleted(), dbAssociateExpected.getDeleted());
        Assert.assertEquals(dbAssociateActual.getTitle(), dbAssociateExpected.getTitle());
        Assert.assertEquals(dbAssociateActual.getUuid(), dbAssociateExpected.getUuid());
    }

    @Test
    public void testCreatePartnerAssociationForUserPrimaryAccountManager() throws KeywordNotDefinedException {
        String keyword = "the primary account manager";

        //Need a user
        UserAccountRunnableBuilder uarb = new UserAccountRunnableBuilder("valid");
        uarb.run();
        DbUserAccount dbUserAccount = uarb.getFinalDbUserAccount();

        //Need a partner
        DbPartner dbPartner = partnerBuilder.buildPartner(PartnerStatus.ACTIVE.name());

        //Method under test
        DbAssociate dbAssociate = partnerBuilder.createPartnerAssociationForUser(dbPartner, dbUserAccount, keyword);

        //Verification
//        DbAssociate dbAssociate = DbAssociateQueryBuilder.defaultInstance(jdbcTemplate).withPartnerId(dbPartner.getPartnerId())
//                .queryForObject();

        assertNotNull(dbAssociate);
        Assert.assertEquals(dbAssociate.getFirstName(), dbUserAccount.getFirstName());
        Assert.assertEquals(dbAssociate.getLastName(), dbUserAccount.getLastName());

        //Verify phone and email for associate
        Long associateId = dbAssociate.getAssociateId();

        verifyEmailPhoneForAssociate(associateId);

    }

    @Test
    public void testCreatePartnerAssociationForUserAssociate() throws KeywordNotDefinedException {
        String keyword = "an associate";

        //Need a user
        UserAccountRunnableBuilder uarb = new UserAccountRunnableBuilder("valid");
        uarb.run();
        DbUserAccount dbUserAccount = uarb.getFinalDbUserAccount();

        //Need a partner
        DbPartner dbPartner = partnerBuilder.buildPartner(PartnerStatus.ACTIVE.name());

        //Method under test
        DbAssociate nonPAMAssociate = partnerBuilder.createPartnerAssociationForUser(dbPartner, dbUserAccount, keyword);


        //Verification
        assertNotNull(nonPAMAssociate, "Associate not created");
        assertEquals(dbPartner.getPartnerId(), nonPAMAssociate.getPartnerId());
        assertEquals(DbAssociateTypeQueryBuilder.associateInstance(jdbcTemplate).getAssociateTypeId(), nonPAMAssociate.getAssociateTypeId());

        List<DbAssociate> dbAssociateList = DbAssociateQueryBuilder.defaultInstance(jdbcTemplate).withPartnerId(dbPartner.getPartnerId())
                .queryForList();

        assertNotNull(dbAssociateList);
        Assert.assertEquals(dbAssociateList.size(), 2);

        for(DbAssociate dbAssociate: dbAssociateList) {
            if(dbAssociate.getAssociateTypeId().equals(DbAssociateTypeQueryBuilder.primaryAccountManagerInstance(jdbcTemplate).getAssociateTypeId())){
                Assert.assertNotEquals(dbAssociate.getFirstName(), dbUserAccount.getFirstName());
                Assert.assertNotEquals(dbAssociate.getLastName(), dbUserAccount.getLastName());
            }
            if(dbAssociate.getAssociateTypeId().equals(DbAssociateTypeQueryBuilder.associateInstance(jdbcTemplate).getAssociateTypeId())) {
                Assert.assertEquals(dbAssociate.getFirstName(), dbUserAccount.getFirstName());
                Assert.assertEquals(dbAssociate.getLastName(), dbUserAccount.getLastName());
            }
            //Verify phone and email for associate
            verifyEmailPhoneForAssociate(dbAssociate.getAssociateId());
        }


    }

    @Test
    public void testCreatePartnerAssociationForNotUserAssociate() throws KeywordNotDefinedException {
        String keyword = "not an associate";

        //Need a user
        UserAccountRunnableBuilder uarb = new UserAccountRunnableBuilder("valid");
        uarb.run();
        DbUserAccount dbUserAccount = uarb.getFinalDbUserAccount();

        //Need a partner
        DbPartner dbPartner = partnerBuilder.buildPartner(PartnerStatus.ACTIVE.name());

        //Method under test
        DbAssociate unassociatedAssociate = partnerBuilder.createPartnerAssociationForUser(dbPartner, dbUserAccount, keyword);

        //Verification
        assertNotNull(unassociatedAssociate, "associate not created");
        assertNotEquals(dbPartner.getPartnerId(), unassociatedAssociate.getPartnerId());

        //verify the pam created by the builder
        List<DbAssociate> dbAssociateList = DbAssociateQueryBuilder.defaultInstance(jdbcTemplate).withPartnerId(dbPartner.getPartnerId())
                .queryForList();

        assertNotNull(dbAssociateList);
        Assert.assertEquals(dbAssociateList.size(), 1);

        for(DbAssociate dbAssociate: dbAssociateList) {
            if(dbAssociate.getAssociateTypeId().equals(DbAssociateTypeQueryBuilder.primaryAccountManagerInstance(jdbcTemplate).getAssociateTypeId())){
                Assert.assertNotEquals(dbAssociate.getFirstName(), dbUserAccount.getFirstName());
                Assert.assertNotEquals(dbAssociate.getLastName(), dbUserAccount.getLastName());
            }
            if(dbAssociate.getAssociateTypeId().equals(DbAssociateTypeQueryBuilder.associateInstance(jdbcTemplate).getAssociateTypeId())) {
                Assert.assertEquals(dbAssociate.getFirstName(), dbUserAccount.getFirstName());
                Assert.assertEquals(dbAssociate.getLastName(), dbUserAccount.getLastName());
            }
            //Verify phone and email for associate
            verifyEmailPhoneForAssociate(dbAssociate.getAssociateId());
        }



    }

    private void verifyEmailPhoneForAssociate(Long associateId) {
        List<DbContactPoint> dbContactPointList = DbContactPointQueryBuilder.defaultInstance(jdbcTemplate).withAssociateId(associateId)
                .queryForList();

        Assert.assertEquals(dbContactPointList.size(), 2);

        DbContactPointType emailType = DbContactPointTypeQueryBuilder.emailAddressInstance(jdbcTemplate);
        DbContactPointType phoneType = DbContactPointTypeQueryBuilder.phoneNumberInstance(jdbcTemplate);
        DbEmailAddress dbEmailAddress = null;
        DbPhoneNumber dbPhoneNumber = null;
        for (DbContactPoint dbContactPoint : dbContactPointList){
            if(dbContactPoint.getContactPointTypeId().equals(emailType.getContactPointTypeId())){
                dbEmailAddress = DbEmailAddressQueryBuilder.defaultInstance(jdbcTemplate).withEmailAddressId(dbContactPoint.getEmailAddressId())
                        .queryForObject();
            }
            if(dbContactPoint.getContactPointTypeId().equals(phoneType.getContactPointTypeId())){
                dbPhoneNumber = DbPhoneNumberQueryBuilder.defaultInstance(jdbcTemplate).withPhoneNumberId(dbContactPoint.getPhoneNumberId())
                        .queryForObject();
            }
        }
        assertNotNull(dbEmailAddress);
        assertNotNull(dbPhoneNumber);

        assertNotNull(dbEmailAddress.getEmailAddress());
        assertNotNull(dbPhoneNumber.getCountryCode());
        assertNotNull(dbPhoneNumber.getSubscriberNumber());
        assertNotNull(dbPhoneNumber.getExtension());
    }

    @Test
    public void testSetEmailAndPhoneForAssociate() throws KeywordNotDefinedException {
        //Need a user
        UserAccountRunnableBuilder uarb = new UserAccountRunnableBuilder("valid");
        uarb.run();
        DbUserAccount dbUserAccount = uarb.getFinalDbUserAccount();

        //Need a partner
        DbPartner dbPartner = partnerBuilder.buildPartner(PartnerStatus.ACTIVE.name());

        // need a partner associate
        DbAssociate dbAssociate =  partnerBuilder.buildPartnerAssociate(AssociateType.PRIMARY_ACCOUNT_MANAGER.name(),dbPartner, dbUserAccount);

        // method under test
        partnerBuilder.setEmailAndPhoneForAssociate(dbAssociate, dbUserAccount.getUserName());

        verifyEmailPhoneForAssociate(dbAssociate.getAssociateId());
    }

    @Test
    public void testLinkAssociateToUserAccount() throws KeywordNotDefinedException {
        //Need a user
        UserAccountRunnableBuilder uarb = new UserAccountRunnableBuilder("valid");
        uarb.run();
        DbUserAccount dbUserAccount = uarb.getFinalDbUserAccount();

        //Need a partner
        DbPartner dbPartner = partnerBuilder.buildPartner(PartnerStatus.ACTIVE.name());

        // need a partner associate
        DbAssociate dbAssociate =  partnerBuilder.buildPartnerAssociate(AssociateType.PRIMARY_ACCOUNT_MANAGER.name(),dbPartner, dbUserAccount);

        partnerBuilder.linkAssociateToUserAccount(dbUserAccount, dbAssociate);

        verifyAssociateAccount(dbUserAccount.getUserAccountId(), dbAssociate);
    }

    private void verifyAssociateAccount(Long userAccountId, DbAssociate dbAssociate) {
        Assert.assertEquals(DbAssociateAccountQueryBuilder.defaultInstance(jdbcTemplate)
                .withAssociateId(dbAssociate.getAssociateId())
                .withUserAccountId(userAccountId)
                .queryForCount(), 1);
    }
}
