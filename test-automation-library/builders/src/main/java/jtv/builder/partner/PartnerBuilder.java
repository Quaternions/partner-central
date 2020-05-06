package jtv.builder.partner;

import com.jtv.test.db.entity.DbAddress;
import com.jtv.test.db.entity.DbEmailAddress;
import com.jtv.test.db.entity.DbPhoneNumber;
import com.jtv.test.db.entity.entitlement.DbUserAccount;
import com.jtv.test.db.entity.partner.*;
import com.jtv.test.db.entity.partner.platform.DbSrvMgmtPlatform;
import com.jtv.test.db.fixtures.DbEmailAddressDataBuilder;
import com.jtv.test.db.fixtures.DbPhoneNumberDataBuilder;
import com.jtv.test.db.fixtures.entitlement.DbUserAccountDataBuilder;
import com.jtv.test.db.fixtures.entitlement.DbUserAccountIdpXrefDataBuilder;
import com.jtv.test.db.fixtures.partner.*;
import com.jtv.test.db.query.DbEmailAddressQueryBuilder;
import com.jtv.test.db.query.partner.*;
import com.jtv.test.db.query.partner.platform.DbSrvMgmtPlatformQueryBuilder;
import com.jtv.test.utils.jdbc.JtvJdbcTemplate;
import com.sun.xml.internal.ws.wsdl.writer.document.Part;
import jline.internal.Log;
import jtv.builder.user.UserAccountRunnableBuilder;
import jtv.dao.DatabaseConfig;
import jtv.dao.entity.partner.AssociateStatus;
import jtv.dao.entity.partner.AssociateType;
import jtv.dao.entity.partner.PartnerStatus;
import jtv.dao.entity.partner.SiteUsageType;
import jtv.dao.partner.PartnerDao;
import jtv.data.generator.DataGenerator;
import jtv.exception.KeywordNotDefinedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class PartnerBuilder {
    private final PartnerDao partnerDao;
    private final PartnerAddressBuilder partnerAddressBuilder;

    private final static Logger log = LoggerFactory.getLogger(PartnerBuilder.class);

    protected JtvJdbcTemplate jdbcTemplate;

    public PartnerBuilder() {
        partnerDao = new PartnerDao();
        partnerAddressBuilder = new PartnerAddressBuilder();
        jdbcTemplate  = partnerDao.getJtvJdbcTemplate();
    }

    public PartnerBuilder(PartnerDao partnerDao, PartnerAddressBuilder partnerAddressBuilder){
        this.partnerDao = partnerDao;
        this.partnerAddressBuilder = partnerAddressBuilder;
        this.jdbcTemplate = partnerDao.getJtvJdbcTemplate();
    }

    public PartnerBuilder(DatabaseConfig databaseConfig) {
        this.partnerDao = new PartnerDao(databaseConfig);
        this.partnerAddressBuilder = new PartnerAddressBuilder(databaseConfig);
        this.jdbcTemplate = partnerDao.getJtvJdbcTemplate();
    }

    public DbPartner createPartnerWithKeyword(String keyword) throws KeywordNotDefinedException {
        if (keyword == null || keyword.isEmpty()) {
            throw new KeywordNotDefinedException(keyword, "Keyword is empty or null!");
        }

        DbPartner dbPartner = new DbPartner();

        //TODO: Once all the various partner status have been implemented, uncomment the code in each of the case statements below.
        switch (keyword.toLowerCase()) {
            case "is active" : {
                dbPartner = buildPartner(PartnerStatus.ACTIVE.name());
                break;
            }
            case "is inactive": {
                dbPartner = buildPartner(PartnerStatus.INACTIVE.name());
                break;
            }
            case "platform enrollment is active" : {
                log.info("[createPartnerWithKeyword] Within the `platform enrollment is active` case statement");
                // currently this status is not implemented, but the database has been seeded
                //dbPartner = buildPartner(PartnerStatus.PLATFORM_ENROLLMENT_ACTIVE.name());
                break;
            }
            case "platform enrollment was rejected" : {
                log.info("[createPartnerWithKeyword] Within the `platform enrollment was rejected` case statement");
                // currently this status is not implemented, but the database has been seeded
                //dbPartner = buildPartner(PartnerStatus.PLATFORM_ENROLLMENT_ACTIVE.name());
                break;
            }
            case "platform enrollment was suspended" : {
                log.info("[createPartnerWithKeyword] Within the `platform enrollment was suspended` case statement");
                // currently this status is not implemented, but the database has been seeded
                //dbPartner = buildPartner(PartnerStatus.PLATFORM_ENROLLMENT_SUSPENDED.name());
                break;
            }
            case "platform enrollment was terminated" : {
                log.info("[createPartnerWithKeyword] Within the `platform enrollment was terminated` case statement");
                // currently this status is not implemented, but the database has been seeded
                //dbPartner = buildPartner(PartnerStatus.PLATFORM_ENROLLMENT_TERMINATED.name());
                break;
            }
            case "has completed registration": {
                dbPartner = buildPartner(PartnerStatus.REGISTRATION_COMPLETED.name());
                break;
            }
            case "is currently registering": {
                dbPartner = buildPartner(PartnerStatus.REGISTRATION_IN_PROGRESS.name());
                break;
            }
            default: {
                throw new KeywordNotDefinedException(keyword, "Partner Status Keyword");
            }
        }
        return dbPartner;
    }

    public Long createPartnerSiteWithUsage(DbPartner partner,String usage) throws KeywordNotDefinedException{
        switch (usage.toLowerCase()) {
            case "registered office": {
                return buildPartnerSite(SiteUsageType.REGISTERED_OFFICE.name(), partner);
            }
            case "business": {
                return buildPartnerSite(SiteUsageType.BUSINESS.name(), partner);
            }
            case "billing": {
                return buildPartnerSite(SiteUsageType.BILLING.name(), partner);
            }
            case "deposit": {
//                return buildPartnerSite(SiteUsageType.DEPOSIT.name(), partner);
                throw new KeywordNotDefinedException(usage, "partner site usage type /'deposit/' keyword isn't implemented");
//                break;
            }
            case "returns": {
                return buildPartnerSite(SiteUsageType.RETURNS.name(), partner);
            }
            case "shipping": {
                return buildPartnerSite(SiteUsageType.SHIPPING.name(), partner);
            }
            default:
                throw new KeywordNotDefinedException(usage, "This keyword for a Partner Site Usage is not defined");
        }
    }

    public DbAssociate createPartnerAssociationForUser(DbPartner partner, DbUserAccount currentUser, String keyword) throws KeywordNotDefinedException {
        String pamKcUuid = UUID.randomUUID().toString();
        String firstname = DataGenerator.NameGenerator.generateFirstName();
        String lastname = DataGenerator.NameGenerator.generateLastName();
        String email = DataGenerator.NameGenerator.generateEmailAddressGivenName(firstname, lastname);
        log.info("[createPartnerAssociationForUser] firstname = " + firstname + ", lastname = " + lastname);
        UserAccountRunnableBuilder uarb = null;
        switch (keyword.toLowerCase()) {
            case "the primary account manager": {
                DbAssociate primaryAccountManager = buildPartnerAssociate(AssociateType.PRIMARY_ACCOUNT_MANAGER.name(), partner, currentUser);
                setEmailAndPhoneForAssociate(primaryAccountManager, currentUser.getUserName());
                linkAssociateToUserAccount(currentUser, primaryAccountManager);

                return primaryAccountManager;
            }
            case "an associate": {
                // build the primary account manager for the partner (who isn't the current user)
                uarb = new UserAccountRunnableBuilder("valid");
                uarb.run();
                DbUserAccount pamDbUserAccount = uarb.getFinalDbUserAccount();

                log.info("[createPartnerAssociationForUser] pamDbUserAccount = " + pamDbUserAccount.toString());
                log.info("[createPartnerAssociationForUser] currentUser" + currentUser.toString());
                DbAssociate primaryAccountManager = buildPartnerAssociate(AssociateType.PRIMARY_ACCOUNT_MANAGER.name(), partner, pamDbUserAccount);

                setEmailAndPhoneForAssociate(primaryAccountManager, email);
                linkAssociateToUserAccount(pamDbUserAccount, primaryAccountManager);

                // Now build the associate for the current user
                DbAssociate associate = buildPartnerAssociate(AssociateType.ASSOCIATE.name(), partner, currentUser);

                setEmailAndPhoneForAssociate(associate, currentUser.getUserName());
                linkAssociateToUserAccount(currentUser, associate);

                return associate;
            }
            case "not an associate" : {
                // create the PAM for the partner
                uarb = new UserAccountRunnableBuilder("valid");
                uarb.run();
                DbUserAccount pamDbUserAccount = uarb.getFinalDbUserAccount();

                DbAssociate primaryAccountManager = buildPartnerAssociate(AssociateType.PRIMARY_ACCOUNT_MANAGER.name(), partner, pamDbUserAccount);

                setEmailAndPhoneForAssociate(primaryAccountManager, email);
                linkAssociateToUserAccount(pamDbUserAccount, primaryAccountManager);

                // Now build the associate for the current user (that is not linked with the current partner
                log.info("Now building partner and associate that is not linked to the partner under test.\n");
                DbPartner anotherPartner = buildPartner(PartnerStatus.REGISTRATION_COMPLETED.name());
                DbAssociate associate = buildPartnerAssociate(AssociateType.ASSOCIATE.name(), anotherPartner, currentUser);

                setEmailAndPhoneForAssociate(associate, currentUser.getUserName());
                linkAssociateToUserAccount(currentUser, associate);

                return associate;
            }
            default:
                throw new KeywordNotDefinedException(keyword, "Partner Associate Type");
        }
    }

    public void setEmailAndPhoneForAssociate(DbAssociate associate, String userName) {
        // check that the email address exists for the current associate
        DbEmailAddress dbEmailAddress;
        try {
            dbEmailAddress = DbEmailAddressQueryBuilder.defaultInstance(jdbcTemplate).withEmailAddress(userName).queryForObject();
        } catch (AssertionError e) {
            dbEmailAddress = DbEmailAddressDataBuilder.defaultInstance(jdbcTemplate).withEmailAddress(userName).build();
        }

        DbContactPointDataBuilder.foreignKeyInstance(jdbcTemplate, associate, DbContactPointTypeQueryBuilder.emailAddressInstance(jdbcTemplate))
                .withEmailAddressId(dbEmailAddress.getEmailAddressId()).build();

        DbPhoneNumber dbPhoneNumber = DbPhoneNumberDataBuilder.defaultInstance(jdbcTemplate)
                .withCountryCode(Integer.parseInt(DataGenerator.PhoneGenerator.generateCountryCode()))
                .withSubscriberNumber(DataGenerator.PhoneGenerator.generatePhoneNumber())
                .withExtension(DataGenerator.PhoneGenerator.generatePhoneExtension()).build();

        DbContactPointDataBuilder.foreignKeyInstance(jdbcTemplate, associate, DbContactPointTypeQueryBuilder.phoneNumberInstance(jdbcTemplate))
                .withPhoneNumberId(dbPhoneNumber.getPhoneNumberId()).build();

    }

    public void linkAssociateToUserAccount(DbUserAccount userAccount, DbAssociate associate) {
        DbAssociateAccountDataBuilder.foreignKeyInstance(jdbcTemplate, associate, userAccount, DbAssocAccountStatusQueryBuilder.activeInstance(jdbcTemplate))
                .build();

    }

    /**
     * Builds a partner with the status passed into the method
     *
     * @param partnerStatus = INACTIVE, REGISTRATION_IN_PROGRESS, REGISTRATION_COMPLETED
     * @return a DatabasePartner object
     */
    public DbPartner buildPartner(String partnerStatus) throws KeywordNotDefinedException {

        try {
            PartnerStatus.valueOf(partnerStatus);
        } catch (IllegalArgumentException exception) {
            throw new KeywordNotDefinedException(partnerStatus, "PartnerStatus: " + PartnerStatus.values().toString());
        }

        String legal = DataGenerator.NameGenerator.generateCompanyName();
        String operating = legal + " " + UUID.randomUUID().toString().substring(0, 12);
        String partnerUuid = UUID.randomUUID().toString();
        log.info(String.format("Creating partner [%s] with status [%s] and uuid [%s].\n", legal, partnerStatus, partnerUuid));
        DbSrvMgmtPlatform dbSrvMgmtPlatform = DbSrvMgmtPlatformQueryBuilder.jtvPartnerCentralInstance(jdbcTemplate);
        DbPartnerStatus dbPartnerStatus = DbPartnerStatusQueryBuilder.defaultInstance(jdbcTemplate).withCode(partnerStatus).queryForObject();

        return DbPartnerDataBuilder.noParentForeignKeyInstance(jdbcTemplate, dbPartnerStatus, dbSrvMgmtPlatform)
                .withLegalName(legal)
                .withOperatingName(operating)
                .withUuid(partnerUuid)
                .build();
    }

    /**
     * Builds a partner site for the partner with the usage type passed into the method
     *
     * @param siteUsage = BUSINESS, BILLING, SHIPPING, RETURNS
     * @param dbPartner partner that the partner site is being created for
     */
    public Long buildPartnerSite(String siteUsage, DbPartner dbPartner) throws KeywordNotDefinedException {

        try {
            SiteUsageType.valueOf(siteUsage);
        } catch (IllegalArgumentException exception) {
            throw new KeywordNotDefinedException(siteUsage, "SiteUsageType: " + SiteUsageType.values().toString());
        }
        String partnerSiteUuid = UUID.randomUUID().toString();

        DbAddress dbAddress = partnerAddressBuilder.buildRandomAddress();

        log.info(String.format("Creating site [%s] for partner [%s] with usage type [%s].\n", partnerSiteUuid, dbPartner.getUuid(), siteUsage));

        DbSite dbSite = DbSiteDataBuilder.foreignKeyInstance(jdbcTemplate, dbAddress, dbPartner)
                .withSiteName(DataGenerator.NameGenerator.generateCompanyName())
                .withUuid(partnerSiteUuid)
                .build();

        DbSiteUsageDataBuilder.foreignKeyInstance(jdbcTemplate, DbSiteUsageTypeQueryBuilder.defaultInstance(jdbcTemplate).withCode(siteUsage).queryForObject(), dbSite)
                .build();

        return dbSite.getSiteId();
    }

    /**
     * Creates a partner associate for the partner & enterprise user being passed into the method
     * @param associateType = PRIMARY_ACCOUNT_MANAGER, ASSOCIATE
     * @param dbPartner partner that the associate is going to be a member of
     * @param dbUserAccount enterprise user account for the associate being created
     * @return a DatabasePartnerAssociate object
     */
    public DbAssociate buildPartnerAssociate(String associateType, DbPartner dbPartner, DbUserAccount dbUserAccount) throws KeywordNotDefinedException {
        try {
            AssociateType.valueOf(associateType);
        } catch (IllegalArgumentException exception) {
            throw new KeywordNotDefinedException(associateType, "AssociateType: " + AssociateType.values().toString());
        }
        String associateUuid = UUID.randomUUID().toString();
        String title = DataGenerator.NameGenerator.generateTitle();
        String associateStatus = AssociateStatus.ACTIVE.name();

        DbAssociateType dbAssociateType = DbAssociateTypeQueryBuilder.defaultInstance(jdbcTemplate).withCode(associateType).queryForObject();
        DbAssociate associate = DbAssociateDataBuilder.foreignKeyInstance(jdbcTemplate, dbPartner, dbAssociateType)
                .withUuid(associateUuid)
                .withFirstName(dbUserAccount.getFirstName())
                .withLastName(dbUserAccount.getLastName())
                .withTitle(title)
                .build();


        log.info(String.format("Building an associate [%s] named [%s %s %s].\n", associate.getUuid(), associate.getTitle(), associate.getFirstName(), associate.getLastName()));
        return associate;
    }

    public void addUsageToPartnerSite(DbPartner dbPartner, Long siteId, String keyword) throws KeywordNotDefinedException {
        DbSite site = DbSiteQueryBuilder.defaultInstance(jdbcTemplate)
                .withPartnerId(dbPartner.getPartnerId())
                .withSiteId(siteId)
                .queryForObject();

        if (keyword == null) {
            throw new KeywordNotDefinedException(keyword, "Partner Site Usage keyword is null");
        }

        log.info("Adding the follow usage to the partner site: " + keyword);
        switch (keyword.toLowerCase()) {
            case "registered office" : {
                DbSiteUsageDataBuilder.defaultInstance(jdbcTemplate)
                        .withSiteId(site.getSiteId())
                        .withSiteUsageTypeId(DbSiteUsageTypeQueryBuilder.defaultInstance(jdbcTemplate)
                                .withCode(SiteUsageType.REGISTERED_OFFICE.name())
                                .queryForObject()
                                .getSiteUsageTypeId()
                        ).build();
                break;
            }
            case "business" : {
                DbSiteUsageDataBuilder.defaultInstance(jdbcTemplate)
                        .withSiteId(site.getSiteId())
                        .withSiteUsageTypeId(DbSiteUsageTypeQueryBuilder.defaultInstance(jdbcTemplate)
                                .withCode(SiteUsageType.BUSINESS.name())
                                .queryForObject()
                                .getSiteUsageTypeId()
                        ).build();
                break;
            }
            case "billing" : {
                DbSiteUsageDataBuilder.defaultInstance(jdbcTemplate)
                        .withSiteId(site.getSiteId())
                        .withSiteUsageTypeId(DbSiteUsageTypeQueryBuilder.defaultInstance(jdbcTemplate)
                                .withCode(SiteUsageType.BILLING.name())
                                .queryForObject()
                                .getSiteUsageTypeId()
                        ).build();
                break;
            }
            case "shipping" : {
                DbSiteUsageDataBuilder.defaultInstance(jdbcTemplate)
                        .withSiteId(site.getSiteId())
                        .withSiteUsageTypeId(DbSiteUsageTypeQueryBuilder.defaultInstance(jdbcTemplate)
                                .withCode(SiteUsageType.SHIPPING.name())
                                .queryForObject()
                                .getSiteUsageTypeId()
                        ).build();
                break;
            }
            case "returns" : {
                DbSiteUsageDataBuilder.defaultInstance(jdbcTemplate)
                        .withSiteId(site.getSiteId())
                        .withSiteUsageTypeId(DbSiteUsageTypeQueryBuilder.defaultInstance(jdbcTemplate)
                                .withCode(SiteUsageType.RETURNS.name())
                                .queryForObject()
                                .getSiteUsageTypeId()
                        ).build();
                break;
            }
            default: {
                throw new KeywordNotDefinedException(keyword, "Keyword is not \"registered office\", \"business\", \"billing\", \"returns\", or \"shipping\"");
            }
        }

        log.info("[PartnerBuilder.addUsageToPartnerSite] site = " + site.toString());
    }
}