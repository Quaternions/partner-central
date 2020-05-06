package jtv.api.gateway.partner.builder;

import com.jtv.test.db.entity.entitlement.DbUserAccount;
import com.jtv.test.db.entity.partner.DbAssociate;
import com.jtv.test.db.entity.partner.DbPartner;
import com.jtv.test.db.entity.partner.DbSite;
import com.jtv.test.db.query.partner.DbAssociateQueryBuilder;
import com.jtv.test.db.query.partner.DbSiteQueryBuilder;
import jtv.api.gateway.partner.entity.request.update.UpdatePartnerRequest;
import jtv.api.gateway.partner.entity.request.update.UpdatePartnerSite;
import jtv.builder.partner.PartnerBuilder;
import jtv.builder.user.UserAccountRunnableBuilder;
import jtv.dao.entity.partner.PartnerStatus;
import jtv.dao.entity.partner.SiteUsageType;
import jtv.dao.partner.PartnerDao;
import jtv.exception.KeywordNotDefinedException;
import org.testng.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class UpdatePartnerRequestBuilderTest {
    private final PartnerBuilder partnerBuilder = new PartnerBuilder();
    private final PartnerDao partnerDao = new PartnerDao();
    private static final Logger log = LoggerFactory.getLogger(UpdatePartnerRequestBuilderTest.class);


    private DbUserAccount createDbUserAccount() {
        //Need a user
        UserAccountRunnableBuilder uarb = new UserAccountRunnableBuilder("valid");
        uarb.run();
        return uarb.getFinalDbUserAccount();
    }

    private UpdatePartnerRequest buildBaseUpdatePartnerRequest(DbUserAccount dbUserAccount, DbPartner partner) throws KeywordNotDefinedException {
        String pamUuid;
        DbSite site;
        List<DbSite> sites = new ArrayList<>();

        // build the partner & partner sites
        site = DbSiteQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                .withSiteId(partnerBuilder.buildPartnerSite(SiteUsageType.BUSINESS.name(), partner))
                .queryForObject();

        sites.add(site);

        // build the primary account manager for the partner
        DbAssociate pamAssociate = partnerBuilder.createPartnerAssociationForUser(partner, dbUserAccount, "the primary account manager");
//        pamUuid = DbAssociateQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate()).withFirstName(dbUserAccount.getFirstName()).withLastName(dbUserAccount.getLastName()).queryForObject().getUuid();
        pamUuid = pamAssociate.getUuid();
        // build request
        return UpdatePartnerRequestBuilder.buildRequest(partner.getUuid(), pamUuid, sites);
    }

    private UpdatePartnerRequest buildBaseUpdatePartnerRequestWithTwoSites(DbUserAccount dbUserAccount, DbPartner partner) throws KeywordNotDefinedException {
        DbSite site;
        Long siteId;
        String pamUuid;
        // reset variables for the next test
        List<DbSite> sites = new ArrayList<>();

        // build the partner & partner sites
        siteId = partnerBuilder.createPartnerSiteWithUsage(partner, SiteUsageType.BUSINESS.name());

        site = DbSiteQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                .withSiteId(siteId)
                .withPartnerId(partner.getPartnerId())
                .queryForObject();
        sites.add(site);
        site = DbSiteQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                .withSiteId(partnerBuilder.createPartnerSiteWithUsage(partner, SiteUsageType.RETURNS.name()))
                .withPartnerId(partner.getPartnerId())
                .queryForObject();

        sites.add(site);
        // build the primary account manager for the partner
        DbAssociate pamAssociate = partnerBuilder.createPartnerAssociationForUser(partner, dbUserAccount, "the primary account manager");
//        pamUuid = DbAssociateQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate()).withFirstName(dbUserAccount.getFirstName()).withLastName(dbUserAccount.getLastName()).queryForObject().getUuid();
        pamUuid = pamAssociate.getUuid();
        // build request
        return UpdatePartnerRequestBuilder.buildRequest(partner.getUuid(), pamUuid, sites);

    }

    private UpdatePartnerRequest buildBaseUpdatePartnerRequestWithTwoUsages(DbUserAccount dbUserAccount, DbPartner partner) throws KeywordNotDefinedException {
        DbSite site;
        String pamUuid;
        List<DbSite> sites = new ArrayList<>();
        List<String> siteUsages = new ArrayList<>();

        // build the partner & partner sites
        siteUsages.add(SiteUsageType.BUSINESS.name());
        log.info("siteUsages.size (should be 1) = " + siteUsages.size());
        log.info("siteUsages (should contain 'BUSINESS') = " + siteUsages.toString());

        site = DbSiteQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                .withSiteId(partnerBuilder.createPartnerSiteWithUsage(partner, siteUsages.get(0)))
                .withPartnerId(partner.getPartnerId())
                .queryForObject();

        siteUsages.add(SiteUsageType.BILLING.name());
        partnerBuilder.addUsageToPartnerSite(partner, site.getSiteId(), siteUsages.get(1));
        sites.add(site);

        // build the primary account manager for the partner
        DbAssociate pamAssociate = partnerBuilder.createPartnerAssociationForUser(partner, dbUserAccount, "the primary account manager");
//        pamUuid = DbAssociateQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate()).withFirstName(dbUserAccount.getFirstName()).withLastName(dbUserAccount.getLastName()).queryForObject().getUuid();
        pamUuid = pamAssociate.getUuid();
        // build request
        return  UpdatePartnerRequestBuilder.buildRequest(partner.getUuid(), pamUuid, sites);
    }

    private void assertPrimaryContact(DbUserAccount dbUserAccount, DbAssociate associate, UpdatePartnerRequest request) {
        // The Primary Contact is always the same entity regardless of how many sites or usages we have.  Created a separate method to do these assertions.
        Assert.assertTrue(request.toString().contains("primaryContact"));
        Assert.assertTrue(request.toString().contains("firstName"));
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getFirstName(), "firstName is not null");
        Assert.assertEquals(associate.getFirstName(), request.getUpdatePartner().getPrimaryContact().getFirstName());
        Assert.assertTrue(request.toString().contains("lastName"));
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getLastName(), "lastName is not null");
        Assert.assertEquals(associate.getLastName(), request.getUpdatePartner().getPrimaryContact().getLastName());
        Assert.assertTrue(request.toString().contains("email"));
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getEmail(), "email is not null");
        Assert.assertEquals(dbUserAccount.getUserName(), request.getUpdatePartner().getPrimaryContact().getEmail());
        Assert.assertTrue(request.toString().contains("title"));
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getTitle(), "title is not null");
        Assert.assertTrue(request.toString().contains("phone"));
        Assert.assertTrue(request.toString().contains("countryCode"));
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getPhone().getCountryCode(), "phone.countryCode is not null");
        Assert.assertTrue(request.toString().contains("subscriberNumber"));
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getPhone().getCountryCode(), "phone.subscriberNumber is not null");
        Assert.assertTrue(request.toString().contains("extension"));
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getPhone().getExtension(), "phone.extension is not null");
        Assert.assertTrue(request.toString().contains("partnerAssociateId"));
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getPartnerAssociateId(), "partnerAssociateId is not null");
        Assert.assertEquals(associate.getUuid(), request.getUpdatePartner().getPrimaryContact().getPartnerAssociateId());

    }

    private void assertPartnerProfile(DbPartner partner, UpdatePartnerRequest request) {
        // The Partner Profile is always the same entity regardless of how many sites or usages we have.  Created a separate method to do these assertions.
        Assert.assertTrue(request.toString().contains("updatePartner"));
        Assert.assertTrue(request.getUpdatePartner().toString().contains("partnerAccountStatus"));
        Assert.assertNotNull(request.getUpdatePartner().getPartnerAccountStatus());
        Assert.assertEquals(PartnerStatus.REGISTRATION_COMPLETED.name(), request.getUpdatePartner().getPartnerAccountStatus());
        Assert.assertTrue(request.getUpdatePartner().toString().contains("legalEntityName"));
        Assert.assertNotNull(request.getUpdatePartner().getLegalEntityName());
        Assert.assertEquals(partner.getLegalName(), request.getUpdatePartner().getLegalEntityName());
        Assert.assertTrue(request.getUpdatePartner().toString().contains("operatingName"));
        Assert.assertNotNull(request.getUpdatePartner().getOperatingName());
        Assert.assertEquals(partner.getOperatingName(), request.getUpdatePartner().getOperatingName());
    }

    private void assertPartnerSites(List<DbSite> sites, List<String> siteUsages, UpdatePartnerRequest request) {
        for (int siteIndex = 0; siteIndex < sites.size(); siteIndex++) {
            Assert.assertTrue(request.getUpdatePartner().getPartnerSites().get(siteIndex).toString().contains("siteName"));
            Assert.assertNotNull(request.getUpdatePartner().getPartnerSites().get(siteIndex).getSiteName(), "siteName for the site is not null");
            Assert.assertEquals(sites.get(siteIndex).getSiteName(), request.getUpdatePartner().getPartnerSites().get(siteIndex).getSiteName());
            Assert.assertTrue(request.getUpdatePartner().getPartnerSites().get(siteIndex).toString().contains("partnerSiteUsageTypes"));
            Assert.assertTrue(request.getUpdatePartner().getPartnerSites().get(siteIndex).toString().contains("siteId"));
            Assert.assertNotNull(request.getUpdatePartner().getPartnerSites().get(siteIndex).getSiteId(), "siteId for the site is not null");
            Assert.assertEquals(sites.get(siteIndex).getUuid(), request.getUpdatePartner().getPartnerSites().get(siteIndex).getSiteId());
            Assert.assertTrue(request.getUpdatePartner().getPartnerSites().get(siteIndex).toString().contains("address"));
            Assert.assertNotNull(request.getUpdatePartner().getPartnerSites().get(siteIndex).getAddress(), "address for the site is not null");
            Assert.assertTrue(request.getUpdatePartner().getPartnerSites().get(siteIndex).getAddress().toString().contains("addressLineOne"));
            Assert.assertNotNull(request.getUpdatePartner().getPartnerSites().get(siteIndex).getAddress().getAddressLineOne());
            Assert.assertTrue(request.getUpdatePartner().getPartnerSites().get(siteIndex).getAddress().toString().contains("addressLineTwo"));
            Assert.assertNotNull(request.getUpdatePartner().getPartnerSites().get(siteIndex).getAddress().getAddressLineTwo());
            Assert.assertTrue(request.getUpdatePartner().getPartnerSites().get(siteIndex).getAddress().toString().contains("city"));
            Assert.assertNotNull(request.getUpdatePartner().getPartnerSites().get(siteIndex).getAddress().getCity());
            Assert.assertTrue(request.getUpdatePartner().getPartnerSites().get(siteIndex).getAddress().toString().contains("subdivisionIsoCode"));
            Assert.assertNotNull(request.getUpdatePartner().getPartnerSites().get(siteIndex).getAddress().getSubdivisionIsoCode());
            Assert.assertTrue(request.getUpdatePartner().getPartnerSites().get(siteIndex).getAddress().toString().contains("countryIso3Code"));
            Assert.assertNotNull(request.getUpdatePartner().getPartnerSites().get(siteIndex).getAddress().getCountryIso3Code());
            Assert.assertTrue(request.getUpdatePartner().getPartnerSites().get(siteIndex).getAddress().toString().contains("postalCode"));
            Assert.assertNotNull(request.getUpdatePartner().getPartnerSites().get(siteIndex).getAddress().getPostalCode());
        }
    }

    @Test
    void testBuildRequest() throws KeywordNotDefinedException {
        List<String> siteUsages = new ArrayList<>();

        DbUserAccount dbUserAccount = createDbUserAccount();
        DbPartner partner = partnerBuilder.buildPartner(PartnerStatus.REGISTRATION_COMPLETED.name());
        UpdatePartnerRequest request = buildBaseUpdatePartnerRequest(dbUserAccount, partner);

        List<DbSite> sites = new ArrayList<>(DbSiteQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                .withPartnerId(partner.getPartnerId())
                .queryForList());

        // using BUSINESS as the site usage, add it to the list
        siteUsages.add(SiteUsageType.BUSINESS.name());

        Assert.assertNotNull(request);

        DbAssociate associate = DbAssociateQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                .withFirstName(dbUserAccount.getFirstName())
                .withLastName(dbUserAccount.getLastName())
                .queryForObject();

        // Now we start asserting that the request is valid
        assertPrimaryContact(dbUserAccount, associate, request);
        assertPartnerProfile(partner, request);

        Assert.assertTrue(request.getUpdatePartner().toString().contains("partnerSites"));
        Assert.assertEquals(sites.size(), request.getUpdatePartner().getPartnerSites().size());
        Assert.assertNotNull(request.getUpdatePartner().getPartnerSites(), "partnerSites is not null");


        // base request only builds one site & one usage for that site.
        assertPartnerSites(sites, siteUsages, request);
    }

    @Test
    void testTwoSitesRequest () throws KeywordNotDefinedException {
        List<String> site1Usages = new ArrayList<>();
        site1Usages.add(SiteUsageType.BUSINESS.name());

        List<String> site2Usages = new ArrayList<>();
        site2Usages.add(SiteUsageType.RETURNS.name());

        UpdatePartnerRequest request;
        DbUserAccount dbUserAccount = createDbUserAccount();
        DbPartner partner = partnerBuilder.buildPartner(PartnerStatus.REGISTRATION_COMPLETED.name());

        List<DbSite> sites = new ArrayList<>(DbSiteQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                .withPartnerId(partner.getPartnerId())
                .queryForList());

        request = buildBaseUpdatePartnerRequestWithTwoSites(dbUserAccount, partner);
        Assert.assertNotNull(request);

        DbAssociate associate = DbAssociateQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                .withFirstName(dbUserAccount.getFirstName())
                .withLastName(dbUserAccount.getLastName())
                .queryForObject();

        // Now we start asserting that the request is valid
        assertPrimaryContact(dbUserAccount, associate, request);
        assertPartnerProfile(partner, request);

        Assert.assertTrue(request.getUpdatePartner().toString().contains("partnerSites"));
        Assert.assertEquals(2, request.getUpdatePartner().getPartnerSites().size());
        Assert.assertNotNull(request.getUpdatePartner().getPartnerSites(), "partnerSites is not null");

        // assert on each partner site
        int i = 1;
        for (DbSite ignored : sites ) {
            if (i == 1) {
                assertPartnerSites(sites, site1Usages, request);
                i++;
            } else {
                assertPartnerSites(sites, site2Usages, request);
            }
        }
    }

    @Test
    void testTwoSiteUsagesRequest() throws KeywordNotDefinedException {
        UpdatePartnerRequest request;
        DbUserAccount dbUserAccount = createDbUserAccount();
        DbPartner partner = partnerBuilder.buildPartner(PartnerStatus.REGISTRATION_COMPLETED.name());
        List<String> siteUsages = new ArrayList<>();

        request = buildBaseUpdatePartnerRequestWithTwoUsages(dbUserAccount, partner);
        Assert.assertNotNull(request);

        List<DbSite> sites = new ArrayList<>(DbSiteQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                .withPartnerId(partner.getPartnerId())
                .queryForList());

        // Add the two usages that are used when creating the request for this test
        siteUsages.add(SiteUsageType.BUSINESS.name());
        siteUsages.add(SiteUsageType.BILLING.name());

        DbAssociate associate = DbAssociateQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                .withFirstName(dbUserAccount.getFirstName())
                .withLastName(dbUserAccount.getLastName())
                .queryForObject();

        assertPrimaryContact(dbUserAccount, associate, request);
        assertPartnerProfile(partner, request);

        Assert.assertTrue(request.getUpdatePartner().toString().contains("partnerSites"));
        Assert.assertEquals(sites.size(), request.getUpdatePartner().getPartnerSites().size());
        Assert.assertNotNull(request.getUpdatePartner().getPartnerSites(), "partnerSites is not null");

        // Assert on both of the usages for the site
        assertPartnerSites(sites, siteUsages, request);
    }

    @Test
    void testUpdatePartnerProfileParameter() throws KeywordNotDefinedException {
        UpdatePartnerRequest request;
        DbUserAccount dbUserAccount = createDbUserAccount();
        DbPartner partner = partnerBuilder.buildPartner(PartnerStatus.REGISTRATION_COMPLETED.name());

        String currentValue;
        request = buildBaseUpdatePartnerRequest(dbUserAccount, partner);
        Assert.assertNotNull(request);

        currentValue = request.getUpdatePartner().getLegalEntityName();
        UpdatePartnerRequestBuilder.updatePartnerProfileParameter("legal entity name", request);
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getLegalEntityName(),"legalEntityName field value has been updated");

        currentValue = request.getUpdatePartner().getOperatingName();
        UpdatePartnerRequestBuilder.updatePartnerProfileParameter("operating name", request);
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getOperatingName(),"operatingName field value has been updated");

        currentValue = request.getUpdatePartner().getPartnerAccountStatus();
        UpdatePartnerRequestBuilder.updatePartnerProfileParameter("partner account status", request);
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPartnerAccountStatus(),"partnerAccountStatus field value has been updated");
    }

    @Test
    void testUpdatePrimaryContactParameter() throws KeywordNotDefinedException {
        UpdatePartnerRequest request;
        DbUserAccount dbUserAccount = createDbUserAccount();
        DbPartner partner = partnerBuilder.buildPartner(PartnerStatus.REGISTRATION_COMPLETED.name());

        String currentValue;
        request = buildBaseUpdatePartnerRequest(dbUserAccount, partner);
        Assert.assertNotNull(request);

        currentValue = request.getUpdatePartner().getPrimaryContact().getFirstName();
        UpdatePartnerRequestBuilder.updatePrimaryContactParameter("firstName", request);
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getFirstName(), "firstName field value has been updated");

        currentValue = request.getUpdatePartner().getPrimaryContact().getLastName();
        UpdatePartnerRequestBuilder.updatePrimaryContactParameter("lastName", request);
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getLastName(), "lastName field value has been updated");

        currentValue = request.getUpdatePartner().getPrimaryContact().getEmail();
        UpdatePartnerRequestBuilder.updatePrimaryContactParameter("email", request);
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getEmail(), "lastName field value has been updated");

        currentValue = request.getUpdatePartner().getPrimaryContact().getTitle();
        UpdatePartnerRequestBuilder.updatePrimaryContactParameter("title", request);
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getTitle(), "title field value has been updated");

        currentValue = request.getUpdatePartner().getPrimaryContact().getPhone().getSubscriberNumber();
        UpdatePartnerRequestBuilder.updatePrimaryContactParameter("subscriber number", request);
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getPhone().getSubscriberNumber(), "phone subscriberNumber field value has been updated");

        currentValue = request.getUpdatePartner().getPrimaryContact().getPhone().getCountryCode();
        UpdatePartnerRequestBuilder.updatePrimaryContactParameter("phone country code", request);
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getPhone().getCountryCode(), "phone countryCode field value has been updated");

        currentValue = request.getUpdatePartner().getPrimaryContact().getPhone().getExtension();
        UpdatePartnerRequestBuilder.updatePrimaryContactParameter("phone extension", request);
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getPhone().getExtension(), "phone extension field value has been updated");
    }

    @Test
    void testUpdatePartnerSiteParameter() throws KeywordNotDefinedException {
        UpdatePartnerRequest request;
        DbUserAccount dbUserAccount = createDbUserAccount();
        DbPartner partner = partnerBuilder.buildPartner(PartnerStatus.REGISTRATION_COMPLETED.name());

        String currentValue;
        request = buildBaseUpdatePartnerRequest(dbUserAccount, partner);
        Assert.assertNotNull(request);

        // we are only building one site for the update request
        currentValue = request.getUpdatePartner().getPartnerSites().get(0).getSiteName();
        UpdatePartnerRequestBuilder.updatePartnerSiteParameter("site name",request.getUpdatePartner().getPartnerSites().get(0));
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPartnerSites().get(0).getSiteName(), "siteName field value has been updated");

        currentValue = request.getUpdatePartner().getPartnerSites().get(0).getAddress().getAddressLineOne();
        UpdatePartnerRequestBuilder.updatePartnerSiteParameter("address line one",request.getUpdatePartner().getPartnerSites().get(0));
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPartnerSites().get(0).getAddress().getAddressLineOne(), "addressLineOne field value has been updated");

        currentValue = request.getUpdatePartner().getPartnerSites().get(0).getAddress().getAddressLineTwo();
        UpdatePartnerRequestBuilder.updatePartnerSiteParameter("address line two",request.getUpdatePartner().getPartnerSites().get(0));
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPartnerSites().get(0).getAddress().getAddressLineTwo(), "addressLineTwo field value has been updated");

        currentValue = request.getUpdatePartner().getPartnerSites().get(0).getAddress().getCity();
        UpdatePartnerRequestBuilder.updatePartnerSiteParameter("city",request.getUpdatePartner().getPartnerSites().get(0));
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPartnerSites().get(0).getAddress().getCity(), "city field value has been updated");

        currentValue = request.getUpdatePartner().getPartnerSites().get(0).getAddress().getSubdivisionIsoCode();
        UpdatePartnerRequestBuilder.updatePartnerSiteParameter("state or province",request.getUpdatePartner().getPartnerSites().get(0));
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPartnerSites().get(0).getAddress().getSubdivisionIsoCode(), "subdivisionIsoCode field value has been updated");

        currentValue = request.getUpdatePartner().getPartnerSites().get(0).getAddress().getPostalCode();
        UpdatePartnerRequestBuilder.updatePartnerSiteParameter("postal code",request.getUpdatePartner().getPartnerSites().get(0));
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPartnerSites().get(0).getAddress().getPostalCode(), "postalCode field value has been updated");

        currentValue = request.getUpdatePartner().getPartnerSites().get(0).getAddress().getCountryIso3Code();
        UpdatePartnerRequestBuilder.updatePartnerSiteParameter("country code",request.getUpdatePartner().getPartnerSites().get(0));
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPartnerSites().get(0).getAddress().getCountryIso3Code(), "CountryIso3Code field value has been updated");

    }

    @Test
    void testLegalEntityNameValidation() throws KeywordNotDefinedException {
        UpdatePartnerRequest request;
        DbUserAccount dbUserAccount = createDbUserAccount();
        DbPartner partner = partnerBuilder.buildPartner(PartnerStatus.REGISTRATION_COMPLETED.name());

        String currentValue;

        request = buildBaseUpdatePartnerRequest(dbUserAccount, partner);
        Assert.assertNotNull(request);

        currentValue = request.getUpdatePartner().getLegalEntityName();
        UpdatePartnerRequestBuilder.legalEntityNameValidation("is null", request);
        Assert.assertNull(request.getUpdatePartner().getLegalEntityName());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getLegalEntityName());
        request.getUpdatePartner().setLegalEntityName(currentValue);

        UpdatePartnerRequestBuilder.legalEntityNameValidation("is empty", request);
        Assert.assertTrue(request.getUpdatePartner().getLegalEntityName().isEmpty());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getLegalEntityName());
        request.getUpdatePartner().setLegalEntityName(currentValue);

        UpdatePartnerRequestBuilder.legalEntityNameValidation("is valid", request);
        Assert.assertNotNull(request.getUpdatePartner().getLegalEntityName());
        Assert.assertEquals(currentValue, request.getUpdatePartner().getLegalEntityName());
        request.getUpdatePartner().setLegalEntityName(currentValue);

        UpdatePartnerRequestBuilder.legalEntityNameValidation("is at maximum allowed character limit", request);
        Assert.assertNotNull(request.getUpdatePartner().getLegalEntityName());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getLegalEntityName());
        Assert.assertEquals(100, request.getUpdatePartner().getLegalEntityName().length());
        request.getUpdatePartner().setLegalEntityName(currentValue);

        UpdatePartnerRequestBuilder.legalEntityNameValidation("exceeds maximum allowed character limit", request);
        Assert.assertNotNull(request.getUpdatePartner().getLegalEntityName());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getLegalEntityName());
        Assert.assertEquals(101, request.getUpdatePartner().getLegalEntityName().length());
        request.getUpdatePartner().setLegalEntityName(currentValue);

        UpdatePartnerRequestBuilder.legalEntityNameValidation("contains special characters", request);
        Assert.assertNotNull(request.getUpdatePartner().getLegalEntityName());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getLegalEntityName());
        Assert.assertEquals(100, request.getUpdatePartner().getLegalEntityName().length());
        Assert.assertFalse(request.getUpdatePartner().getLegalEntityName().matches("[^A-Za-z0-9]")); // returns false if the string contains anything other than A-Z, a-z or 0-9
    }

    @Test
    void testOperatingNameValidation() throws KeywordNotDefinedException {
        UpdatePartnerRequest request;
        DbUserAccount dbUserAccount = createDbUserAccount();
        DbPartner partner = partnerBuilder.buildPartner(PartnerStatus.REGISTRATION_COMPLETED.name());

        String currentValue;

        request = buildBaseUpdatePartnerRequest(dbUserAccount, partner);
        Assert.assertNotNull(request);

        currentValue = request.getUpdatePartner().getOperatingName();
        UpdatePartnerRequestBuilder.operatingNameValidation("is null", request);
        Assert.assertNull(request.getUpdatePartner().getOperatingName());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getOperatingName());
        request.getUpdatePartner().setOperatingName(currentValue);

        UpdatePartnerRequestBuilder.operatingNameValidation("is empty", request);
        Assert.assertTrue(request.getUpdatePartner().getOperatingName().isEmpty());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getOperatingName());
        request.getUpdatePartner().setOperatingName(currentValue);

        UpdatePartnerRequestBuilder.operatingNameValidation("is valid", request);
        Assert.assertNotNull(request.getUpdatePartner().getOperatingName());
        Assert.assertEquals(currentValue, request.getUpdatePartner().getOperatingName());
        request.getUpdatePartner().setOperatingName(currentValue);

        UpdatePartnerRequestBuilder.operatingNameValidation("is at maximum allowed character limit", request);
        Assert.assertNotNull(request.getUpdatePartner().getOperatingName());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getOperatingName());
        Assert.assertEquals(100, request.getUpdatePartner().getOperatingName().length());
        request.getUpdatePartner().setOperatingName(currentValue);

        UpdatePartnerRequestBuilder.operatingNameValidation("exceeds maximum allowed character limit", request);
        Assert.assertNotNull(request.getUpdatePartner().getOperatingName());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getOperatingName());
        Assert.assertEquals(101, request.getUpdatePartner().getOperatingName().length());
        request.getUpdatePartner().setOperatingName(currentValue);

        UpdatePartnerRequestBuilder.operatingNameValidation("contains special characters", request);
        Assert.assertNotNull(request.getUpdatePartner().getOperatingName());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getOperatingName());
        Assert.assertEquals(100, request.getUpdatePartner().getOperatingName().length());
        Assert.assertFalse(request.getUpdatePartner().getOperatingName().matches("[^A-Za-z0-9]")); // returns false if the string contains anything other than A-Z, a-z or 0-9
    }

    @Test
    void testFirstNameValidation() throws KeywordNotDefinedException {
        UpdatePartnerRequest request;
        DbUserAccount dbUserAccount = createDbUserAccount();
        DbPartner partner = partnerBuilder.buildPartner(PartnerStatus.REGISTRATION_COMPLETED.name());

        String currentValue;

        request = buildBaseUpdatePartnerRequest(dbUserAccount, partner);
        Assert.assertNotNull(request);

        currentValue = request.getUpdatePartner().getPrimaryContact().getFirstName();
        UpdatePartnerRequestBuilder.firstNameValidation("is null", request);
        Assert.assertNull(request.getUpdatePartner().getPrimaryContact().getFirstName());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getFirstName());
        request.getUpdatePartner().getPrimaryContact().setFirstName(currentValue);

        UpdatePartnerRequestBuilder.firstNameValidation("is empty", request);
        Assert.assertTrue(request.getUpdatePartner().getPrimaryContact().getFirstName().isEmpty());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getFirstName());
        request.getUpdatePartner().getPrimaryContact().setFirstName(currentValue);

        UpdatePartnerRequestBuilder.firstNameValidation("is valid", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getFirstName());
        Assert.assertEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getFirstName());
        request.getUpdatePartner().getPrimaryContact().setFirstName(currentValue);

        UpdatePartnerRequestBuilder.firstNameValidation("is at maximum allowed character limit", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getFirstName());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getFirstName());
        Assert.assertEquals(50, request.getUpdatePartner().getPrimaryContact().getFirstName().length());
        request.getUpdatePartner().getPrimaryContact().setFirstName(currentValue);

        UpdatePartnerRequestBuilder.firstNameValidation("exceeds maximum allowed character limit", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getFirstName());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getFirstName());
        Assert.assertEquals(51, request.getUpdatePartner().getPrimaryContact().getFirstName().length());
        request.getUpdatePartner().getPrimaryContact().setFirstName(currentValue);

        UpdatePartnerRequestBuilder.firstNameValidation("contains special characters", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getFirstName());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getFirstName());
        Assert.assertEquals(50, request.getUpdatePartner().getPrimaryContact().getFirstName().length());
        Assert.assertFalse(request.getUpdatePartner().getPrimaryContact().getFirstName().matches("[^A-Za-z0-9]")); // returns false if the string contains anything other than A-Z, a-z or 0-9
    }

    @Test
    void testLastNameValidation() throws KeywordNotDefinedException {
        UpdatePartnerRequest request;
        DbUserAccount dbUserAccount = createDbUserAccount();
        DbPartner partner = partnerBuilder.buildPartner(PartnerStatus.REGISTRATION_COMPLETED.name());

        String currentValue;

        request = buildBaseUpdatePartnerRequest(dbUserAccount, partner);
        Assert.assertNotNull(request);

        currentValue = request.getUpdatePartner().getPrimaryContact().getLastName();
        UpdatePartnerRequestBuilder.lastNameValidation("is null", request);
        Assert.assertNull(request.getUpdatePartner().getPrimaryContact().getLastName());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getLastName());
        request.getUpdatePartner().getPrimaryContact().setLastName(currentValue);

        UpdatePartnerRequestBuilder.lastNameValidation("is empty", request);
        Assert.assertTrue(request.getUpdatePartner().getPrimaryContact().getLastName().isEmpty());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getLastName());
        request.getUpdatePartner().getPrimaryContact().setLastName(currentValue);

        UpdatePartnerRequestBuilder.lastNameValidation("is valid", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getLastName());
        Assert.assertEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getLastName());
        request.getUpdatePartner().getPrimaryContact().setLastName(currentValue);

        UpdatePartnerRequestBuilder.lastNameValidation("is at maximum allowed character limit", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getLastName());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getLastName());
        Assert.assertEquals(50, request.getUpdatePartner().getPrimaryContact().getLastName().length());
        request.getUpdatePartner().getPrimaryContact().setLastName(currentValue);

        UpdatePartnerRequestBuilder.lastNameValidation("exceeds maximum allowed character limit", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getLastName());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getLastName());
        Assert.assertEquals(51, request.getUpdatePartner().getPrimaryContact().getLastName().length());
        request.getUpdatePartner().getPrimaryContact().setLastName(currentValue);

        UpdatePartnerRequestBuilder.lastNameValidation("contains special characters", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getLastName());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getLastName());
        Assert.assertEquals(50, request.getUpdatePartner().getPrimaryContact().getLastName().length());
        Assert.assertFalse(request.getUpdatePartner().getPrimaryContact().getLastName().matches("[^A-Za-z0-9]")); // returns false if the string contains anything other than A-Z, a-z or 0-9
    }

    @Test
    void testTitleValidation() throws KeywordNotDefinedException {
        UpdatePartnerRequest request;
        DbUserAccount dbUserAccount = createDbUserAccount();
        DbPartner partner = partnerBuilder.buildPartner(PartnerStatus.REGISTRATION_COMPLETED.name());

        String currentValue;

        request = buildBaseUpdatePartnerRequest(dbUserAccount, partner);
        Assert.assertNotNull(request);

        currentValue = request.getUpdatePartner().getPrimaryContact().getTitle();
        UpdatePartnerRequestBuilder.titleValidation("is null", request);
        Assert.assertNull(request.getUpdatePartner().getPrimaryContact().getTitle());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getTitle());
        request.getUpdatePartner().getPrimaryContact().setTitle(currentValue);

        UpdatePartnerRequestBuilder.titleValidation("is empty", request);
        Assert.assertTrue(request.getUpdatePartner().getPrimaryContact().getTitle().isEmpty());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getTitle());
        request.getUpdatePartner().getPrimaryContact().setTitle(currentValue);

        UpdatePartnerRequestBuilder.titleValidation("is valid", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getTitle());
        Assert.assertEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getTitle());
        request.getUpdatePartner().getPrimaryContact().setTitle(currentValue);

        UpdatePartnerRequestBuilder.titleValidation("is at maximum allowed character limit", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getTitle());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getTitle());
        Assert.assertEquals(50, request.getUpdatePartner().getPrimaryContact().getTitle().length());
        request.getUpdatePartner().getPrimaryContact().setTitle(currentValue);

        UpdatePartnerRequestBuilder.titleValidation("exceeds maximum allowed character limit", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getTitle());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getTitle());
        Assert.assertEquals(51, request.getUpdatePartner().getPrimaryContact().getTitle().length());
        request.getUpdatePartner().getPrimaryContact().setTitle(currentValue);

        UpdatePartnerRequestBuilder.titleValidation("contains special characters", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getTitle());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getTitle());
        Assert.assertEquals(50, request.getUpdatePartner().getPrimaryContact().getTitle().length());
        Assert.assertFalse(request.getUpdatePartner().getPrimaryContact().getTitle().matches("[^A-Za-z0-9]"));
    }

    @Test
    void testEmailValidation() throws KeywordNotDefinedException {
        UpdatePartnerRequest request;
        DbUserAccount dbUserAccount = createDbUserAccount();
        DbPartner partner = partnerBuilder.buildPartner(PartnerStatus.REGISTRATION_COMPLETED.name());

        String currentValue;

        request = buildBaseUpdatePartnerRequest(dbUserAccount, partner);
        Assert.assertNotNull(request);

        currentValue = request.getUpdatePartner().getPrimaryContact().getEmail();
        UpdatePartnerRequestBuilder.emailValidation("is null", request);
        Assert.assertNull(request.getUpdatePartner().getPrimaryContact().getEmail());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getEmail());
        request.getUpdatePartner().getPrimaryContact().setEmail(currentValue);

        UpdatePartnerRequestBuilder.emailValidation("is empty", request);
        Assert.assertTrue(request.getUpdatePartner().getPrimaryContact().getEmail().isEmpty());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getEmail());
        request.getUpdatePartner().getPrimaryContact().setEmail(currentValue);

        UpdatePartnerRequestBuilder.emailValidation("is valid", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getEmail());
        Assert.assertEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getEmail());
        request.getUpdatePartner().getPrimaryContact().setEmail(currentValue);

        UpdatePartnerRequestBuilder.emailValidation("is at maximum allowed character limit", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getEmail());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getEmail());
        Assert.assertEquals(45, request.getUpdatePartner().getPrimaryContact().getEmail().length());
        request.getUpdatePartner().getPrimaryContact().setEmail(currentValue);

        UpdatePartnerRequestBuilder.emailValidation("exceeds maximum allowed character limit", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getEmail());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getEmail());
        Assert.assertEquals(46, request.getUpdatePartner().getPrimaryContact().getEmail().length());
        request.getUpdatePartner().getPrimaryContact().setEmail(currentValue);

        UpdatePartnerRequestBuilder.emailValidation("contains minimum requirements for an email", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getEmail());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getEmail());
        Assert.assertEquals(3, request.getUpdatePartner().getPrimaryContact().getEmail().length());
        Assert.assertTrue(request.getUpdatePartner().getPrimaryContact().getEmail().matches("^[A-Za-z]@[A-Za-z]$"));
    }

    @Test
    void testPhoneSubscriberNumberValidation() throws KeywordNotDefinedException {
        UpdatePartnerRequest request;
        DbUserAccount dbUserAccount = createDbUserAccount();
        DbPartner partner = partnerBuilder.buildPartner(PartnerStatus.REGISTRATION_COMPLETED.name());

        String currentValue;

        request = buildBaseUpdatePartnerRequest(dbUserAccount, partner);
        Assert.assertNotNull(request);

        currentValue = request.getUpdatePartner().getPrimaryContact().getPhone().getSubscriberNumber();
        UpdatePartnerRequestBuilder.phoneSubscriberNumberValidation("is null", request);
        Assert.assertNull(request.getUpdatePartner().getPrimaryContact().getPhone().getSubscriberNumber());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getPhone().getSubscriberNumber());
        request.getUpdatePartner().getPrimaryContact().getPhone().setSubscriberNumber(currentValue);

        UpdatePartnerRequestBuilder.phoneSubscriberNumberValidation("is empty", request);
        Assert.assertTrue(request.getUpdatePartner().getPrimaryContact().getPhone().getSubscriberNumber().isEmpty());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getPhone().getSubscriberNumber());
        request.getUpdatePartner().getPrimaryContact().getPhone().setSubscriberNumber(currentValue);

        UpdatePartnerRequestBuilder.phoneSubscriberNumberValidation("is valid", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getPhone().getSubscriberNumber());
        Assert.assertEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getPhone().getSubscriberNumber());
        request.getUpdatePartner().getPrimaryContact().getPhone().setSubscriberNumber(currentValue);

        UpdatePartnerRequestBuilder.phoneSubscriberNumberValidation("is at maximum allowed character limit", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getPhone().getSubscriberNumber());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getPhone().getSubscriberNumber());
        Assert.assertEquals(12, request.getUpdatePartner().getPrimaryContact().getPhone().getSubscriberNumber().length());
        request.getUpdatePartner().getPrimaryContact().getPhone().setSubscriberNumber(currentValue);

        UpdatePartnerRequestBuilder.phoneSubscriberNumberValidation("exceeds maximum allowed character limit", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getPhone().getSubscriberNumber());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getPhone().getSubscriberNumber());
        Assert.assertEquals(13, request.getUpdatePartner().getPrimaryContact().getPhone().getSubscriberNumber().length());
        request.getUpdatePartner().getPrimaryContact().getPhone().setSubscriberNumber(currentValue);

        UpdatePartnerRequestBuilder.phoneSubscriberNumberValidation("contains non-numeric characters", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getPhone().getSubscriberNumber());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getPhone().getSubscriberNumber());
        Assert.assertTrue(request.getUpdatePartner().getPrimaryContact().getPhone().getSubscriberNumber().matches(".*?[^0-9].*"));  // return true if the string contains alphabetic characters, false if only numeric
    }

    @Test
    void testPhoneCountryCodeValidation() throws KeywordNotDefinedException {
        UpdatePartnerRequest request;
        DbUserAccount dbUserAccount = createDbUserAccount();
        DbPartner partner = partnerBuilder.buildPartner(PartnerStatus.REGISTRATION_COMPLETED.name());

        String currentValue;

        request = buildBaseUpdatePartnerRequest(dbUserAccount, partner);
        Assert.assertNotNull(request);

        currentValue = request.getUpdatePartner().getPrimaryContact().getPhone().getCountryCode();
        UpdatePartnerRequestBuilder.phoneCountryCodeValidation("is null", request);
        Assert.assertNull(request.getUpdatePartner().getPrimaryContact().getPhone().getCountryCode());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getPhone().getCountryCode());
        request.getUpdatePartner().getPrimaryContact().getPhone().setCountryCode(currentValue);

        UpdatePartnerRequestBuilder.phoneCountryCodeValidation("is empty", request);
        Assert.assertTrue(request.getUpdatePartner().getPrimaryContact().getPhone().getCountryCode().isEmpty());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getPhone().getCountryCode());
        request.getUpdatePartner().getPrimaryContact().getPhone().setCountryCode(currentValue);

        UpdatePartnerRequestBuilder.phoneCountryCodeValidation("is valid", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getPhone().getCountryCode());
        Assert.assertEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getPhone().getCountryCode());
        request.getUpdatePartner().getPrimaryContact().getPhone().setCountryCode(currentValue);

        UpdatePartnerRequestBuilder.phoneCountryCodeValidation("is at maximum allowed character limit", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getPhone().getCountryCode());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getPhone().getCountryCode());
        Assert.assertEquals(3, request.getUpdatePartner().getPrimaryContact().getPhone().getCountryCode().length());
        request.getUpdatePartner().getPrimaryContact().getPhone().setCountryCode(currentValue);

        UpdatePartnerRequestBuilder.phoneCountryCodeValidation("exceeds maximum allowed character limit", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getPhone().getCountryCode());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getPhone().getCountryCode());
        Assert.assertEquals(4, request.getUpdatePartner().getPrimaryContact().getPhone().getCountryCode().length());
        request.getUpdatePartner().getPrimaryContact().getPhone().setCountryCode(currentValue);

        UpdatePartnerRequestBuilder.phoneCountryCodeValidation("contains non-numeric characters", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getPhone().getCountryCode());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getPhone().getCountryCode());
        Assert.assertTrue(request.getUpdatePartner().getPrimaryContact().getPhone().getCountryCode().matches(".*?[^0-9].*"));  // return true if the string contains alphabetic characters, false if only numeric
    }

    @Test
    void testPhoneExtensionValidation() throws KeywordNotDefinedException {
        UpdatePartnerRequest request;
        DbUserAccount dbUserAccount = createDbUserAccount();
        DbPartner partner = partnerBuilder.buildPartner(PartnerStatus.REGISTRATION_COMPLETED.name());

        String currentValue;

        request = buildBaseUpdatePartnerRequest(dbUserAccount, partner);
        Assert.assertNotNull(request);

        currentValue = request.getUpdatePartner().getPrimaryContact().getPhone().getExtension();
        UpdatePartnerRequestBuilder.phoneExtensionValidation("is null", request);
        Assert.assertNull(request.getUpdatePartner().getPrimaryContact().getPhone().getExtension());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getPhone().getExtension());
        request.getUpdatePartner().getPrimaryContact().getPhone().setExtension(currentValue);

        UpdatePartnerRequestBuilder.phoneExtensionValidation("is empty", request);
        Assert.assertTrue(request.getUpdatePartner().getPrimaryContact().getPhone().getExtension().isEmpty());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getPhone().getExtension());
        request.getUpdatePartner().getPrimaryContact().getPhone().setExtension(currentValue);

        UpdatePartnerRequestBuilder.phoneExtensionValidation("is valid", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getPhone().getExtension());
        Assert.assertEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getPhone().getExtension());
        request.getUpdatePartner().getPrimaryContact().getPhone().setExtension(currentValue);

        UpdatePartnerRequestBuilder.phoneExtensionValidation("is at maximum allowed character limit", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getPhone().getExtension());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getPhone().getExtension());
        Assert.assertEquals(5, request.getUpdatePartner().getPrimaryContact().getPhone().getExtension().length());
        request.getUpdatePartner().getPrimaryContact().getPhone().setExtension(currentValue);

        UpdatePartnerRequestBuilder.phoneExtensionValidation("exceeds maximum allowed character limit", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getPhone().getExtension());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getPhone().getExtension());
        Assert.assertEquals(6, request.getUpdatePartner().getPrimaryContact().getPhone().getExtension().length());
        request.getUpdatePartner().getPrimaryContact().getPhone().setExtension(currentValue);

        UpdatePartnerRequestBuilder.phoneExtensionValidation("contains non-numeric characters", request);
        Assert.assertNotNull(request.getUpdatePartner().getPrimaryContact().getPhone().getExtension());
        Assert.assertNotEquals(currentValue, request.getUpdatePartner().getPrimaryContact().getPhone().getExtension());
        Assert.assertEquals(5, request.getUpdatePartner().getPrimaryContact().getPhone().getExtension().length());
        Assert.assertTrue(request.getUpdatePartner().getPrimaryContact().getPhone().getExtension().matches(".*?[^0-9].*"));  // return true if the string contains alphabetic characters, false if only numeric
    }

    @Test
    void testSiteNameValidation() throws KeywordNotDefinedException {
        DbUserAccount dbUserAccount = createDbUserAccount();
        DbPartner partner = partnerBuilder.buildPartner(PartnerStatus.REGISTRATION_COMPLETED.name());
        UpdatePartnerRequest request = buildBaseUpdatePartnerRequest(dbUserAccount, partner);

        Assert.assertNotNull(request);

        for (UpdatePartnerSite site: request.getUpdatePartner().getPartnerSites() ) {
            String currentValue;

            currentValue = site.getSiteName();
            UpdatePartnerRequestBuilder.siteNameValidation("is null", request);
            Assert.assertNull(site.getSiteName());
            Assert.assertNotEquals(currentValue, site.getSiteName());
            site.setSiteName(currentValue);

            UpdatePartnerRequestBuilder.siteNameValidation("is empty", request);
            Assert.assertTrue(site.getSiteName().isEmpty());
            Assert.assertNotEquals(currentValue, site.getSiteName());
            site.setSiteName(currentValue);

            UpdatePartnerRequestBuilder.siteNameValidation("is valid", request);
            Assert.assertNotNull(site.getSiteName());
            Assert.assertEquals(currentValue, site.getSiteName());
            site.setSiteName(currentValue);

            UpdatePartnerRequestBuilder.siteNameValidation("is at maximum allowed character limit", request);
            Assert.assertNotNull(site.getSiteName());
            Assert.assertNotEquals(currentValue, site.getSiteName());
            Assert.assertEquals(50, site.getSiteName().length());
            site.setSiteName(currentValue);

            UpdatePartnerRequestBuilder.siteNameValidation("exceeds maximum allowed character limit", request);
            Assert.assertNotNull(site.getSiteName());
            Assert.assertNotEquals(currentValue, site.getSiteName());
            Assert.assertEquals(51, site.getSiteName().length());
            site.setSiteName(currentValue);

            UpdatePartnerRequestBuilder.siteNameValidation("contains special characters", request);
            Assert.assertNotNull(site.getSiteName());
            Assert.assertNotEquals(currentValue, site.getSiteName());
            Assert.assertFalse(site.getSiteName().matches("[^A-Za-z0-9]")); // returns false if the string contains anything other than A-Z, a-z or 0-9
        }
    }

    @Test
    void testAddressLineOneValidation() throws KeywordNotDefinedException {
        DbUserAccount dbUserAccount = createDbUserAccount();
        DbPartner partner = partnerBuilder.buildPartner(PartnerStatus.REGISTRATION_COMPLETED.name());
        UpdatePartnerRequest request = buildBaseUpdatePartnerRequest(dbUserAccount, partner);

        Assert.assertNotNull(request);

        for (UpdatePartnerSite site: request.getUpdatePartner().getPartnerSites() ) {
            String currentValue;

            currentValue = site.getAddress().getAddressLineOne();
            UpdatePartnerRequestBuilder.addressLineOneValidation("is null", request);
            Assert.assertNull(site.getAddress().getAddressLineOne());
            Assert.assertNotEquals(currentValue, site.getAddress().getAddressLineOne());
            site.getAddress().setAddressLineOne(currentValue);

            UpdatePartnerRequestBuilder.addressLineOneValidation("is empty", request);
            Assert.assertTrue(site.getAddress().getAddressLineOne().isEmpty());
            Assert.assertNotEquals(currentValue, site.getSiteName());
            site.getAddress().setAddressLineOne(currentValue);

            UpdatePartnerRequestBuilder.addressLineOneValidation("is valid", request);
            Assert.assertNotNull(site.getAddress().getAddressLineOne());
            Assert.assertEquals(currentValue, site.getAddress().getAddressLineOne());
            site.getAddress().setAddressLineOne(currentValue);

            UpdatePartnerRequestBuilder.addressLineOneValidation("is at maximum allowed character limit", request);
            Assert.assertNotNull(site.getAddress().getAddressLineOne());
            Assert.assertNotEquals(currentValue, site.getAddress().getAddressLineOne());
            Assert.assertEquals(240, site.getAddress().getAddressLineOne().length());
            site.getAddress().setAddressLineOne(currentValue);

            UpdatePartnerRequestBuilder.addressLineOneValidation("exceeds maximum allowed character limit", request);
            Assert.assertNotNull(site.getAddress().getAddressLineOne());
            Assert.assertNotEquals(currentValue, site.getAddress().getAddressLineOne());
            Assert.assertEquals(241, site.getAddress().getAddressLineOne().length());
            site.getAddress().setAddressLineOne(currentValue);

            UpdatePartnerRequestBuilder.addressLineOneValidation("contains special characters", request);
            Assert.assertNotNull(site.getAddress().getAddressLineOne());
            Assert.assertNotEquals(currentValue, site.getAddress().getAddressLineOne());
            Assert.assertEquals(240, site.getAddress().getAddressLineOne().length());
            Assert.assertFalse(site.getAddress().getAddressLineOne().matches("[^A-Za-z0-9]")); // returns false if the string contains anything other than A-Z, a-z or 0-9
        }
    }

    @Test
    void testAddressLineTwoValidation() throws KeywordNotDefinedException {
        DbPartner partner = partnerBuilder.buildPartner(PartnerStatus.REGISTRATION_COMPLETED.name());
        DbUserAccount dbUserAccount = createDbUserAccount();
        UpdatePartnerRequest request= buildBaseUpdatePartnerRequest(dbUserAccount, partner);

        Assert.assertNotNull(request);

        for (UpdatePartnerSite site: request.getUpdatePartner().getPartnerSites() ) {
            String currentValue;

            currentValue = site.getAddress().getAddressLineTwo();
            UpdatePartnerRequestBuilder.addressLineTwoValidation("is null", request);
            Assert.assertNull(site.getAddress().getAddressLineTwo());
            Assert.assertNotEquals(currentValue, site.getAddress().getAddressLineTwo());
            site.getAddress().setAddressLineTwo(currentValue);

            UpdatePartnerRequestBuilder.addressLineTwoValidation("is empty", request);
            Assert.assertTrue(site.getAddress().getAddressLineTwo().isEmpty());
            Assert.assertNotEquals(currentValue, site.getSiteName());
            site.getAddress().setAddressLineTwo(currentValue);

            UpdatePartnerRequestBuilder.addressLineTwoValidation("is valid", request);
            Assert.assertNotNull(site.getAddress().getAddressLineTwo());
            Assert.assertEquals(currentValue, site.getAddress().getAddressLineTwo());
            site.getAddress().setAddressLineTwo(currentValue);

            UpdatePartnerRequestBuilder.addressLineTwoValidation("is at maximum allowed character limit", request);
            Assert.assertNotNull(site.getAddress().getAddressLineTwo());
            Assert.assertNotEquals(currentValue, site.getAddress().getAddressLineTwo());
            Assert.assertEquals(240, site.getAddress().getAddressLineTwo().length());
            site.getAddress().setAddressLineTwo(currentValue);

            UpdatePartnerRequestBuilder.addressLineTwoValidation("exceeds maximum allowed character limit", request);
            Assert.assertNotNull(site.getAddress().getAddressLineTwo());
            Assert.assertNotEquals(currentValue, site.getAddress().getAddressLineTwo());
            Assert.assertEquals(241, site.getAddress().getAddressLineTwo().length());
            site.getAddress().setAddressLineTwo(currentValue);

            UpdatePartnerRequestBuilder.addressLineTwoValidation("contains special characters", request);
            Assert.assertNotNull(site.getAddress().getAddressLineTwo());
            Assert.assertNotEquals(currentValue, site.getAddress().getAddressLineTwo());
            Assert.assertEquals(240, site.getAddress().getAddressLineTwo().length());
            Assert.assertFalse(site.getAddress().getAddressLineTwo().matches("[^A-Za-z0-9]")); // returns false if the string contains anything other than A-Z, a-z or 0-9
        }
    }

    @Test
    void testCityValidation() throws KeywordNotDefinedException {
        DbPartner partner = partnerBuilder.buildPartner(PartnerStatus.REGISTRATION_COMPLETED.name());
        DbUserAccount dbUserAccount = createDbUserAccount();
        UpdatePartnerRequest request= buildBaseUpdatePartnerRequest(dbUserAccount, partner);

        Assert.assertNotNull(request);

        for (UpdatePartnerSite site: request.getUpdatePartner().getPartnerSites() ) {
            String currentValue;

            currentValue = site.getAddress().getCity();
            UpdatePartnerRequestBuilder.cityValidation("is null", request);
            Assert.assertNull(site.getAddress().getCity());
            Assert.assertNotEquals(currentValue, site.getAddress().getCity());
            site.getAddress().setCity(currentValue);

            UpdatePartnerRequestBuilder.cityValidation("is empty", request);
            Assert.assertTrue(site.getAddress().getCity().isEmpty());
            Assert.assertNotEquals(currentValue, site.getSiteName());
            site.getAddress().setCity(currentValue);

            UpdatePartnerRequestBuilder.cityValidation("is valid", request);
            Assert.assertNotNull(site.getAddress().getCity());
            Assert.assertEquals(currentValue, site.getAddress().getCity());
            site.getAddress().setCity(currentValue);

            UpdatePartnerRequestBuilder.cityValidation("is at maximum allowed character limit", request);
            Assert.assertNotNull(site.getAddress().getCity());
            Assert.assertNotEquals(currentValue, site.getAddress().getCity());
            Assert.assertEquals(30, site.getAddress().getCity().length());
            site.getAddress().setCity(currentValue);

            UpdatePartnerRequestBuilder.cityValidation("exceeds maximum allowed character limit", request);
            Assert.assertNotNull(site.getAddress().getCity());
            Assert.assertNotEquals(currentValue, site.getAddress().getCity());
            Assert.assertEquals(31, site.getAddress().getCity().length());
            site.getAddress().setCity(currentValue);

            UpdatePartnerRequestBuilder.cityValidation("contains special characters", request);
            Assert.assertNotNull(site.getAddress().getCity());
            Assert.assertNotEquals(currentValue, site.getAddress().getCity());
            Assert.assertEquals(30, site.getAddress().getCity().length());
            Assert.assertFalse(site.getAddress().getCity().matches("[^A-Za-z0-9]")); // returns false if the string contains anything other than A-Z, a-z or 0-9
        }
    }

    @Test
    void testStateProvinceValidation() throws KeywordNotDefinedException {
        DbPartner partner = partnerBuilder.buildPartner(PartnerStatus.REGISTRATION_COMPLETED.name());
        DbUserAccount dbUserAccount = createDbUserAccount();
        UpdatePartnerRequest request= buildBaseUpdatePartnerRequest(dbUserAccount, partner);

        Assert.assertNotNull(request);

        for (UpdatePartnerSite site: request.getUpdatePartner().getPartnerSites() ) {
            String currentValue = site.getAddress().getSubdivisionIsoCode();

            UpdatePartnerRequestBuilder.stateProvinceValidation("is null", request);
            Assert.assertNull(site.getAddress().getSubdivisionIsoCode());
            Assert.assertNotEquals(currentValue, site.getAddress().getSubdivisionIsoCode());
            site.getAddress().setSubdivisionIsoCode(currentValue);

            UpdatePartnerRequestBuilder.stateProvinceValidation("is empty", request);
            Assert.assertTrue(site.getAddress().getSubdivisionIsoCode().isEmpty());
            Assert.assertNotEquals(currentValue, site.getSiteName());
            site.getAddress().setSubdivisionIsoCode(currentValue);

            UpdatePartnerRequestBuilder.stateProvinceValidation("is valid", request);
            Assert.assertNotNull(site.getAddress().getSubdivisionIsoCode());
            Assert.assertEquals(currentValue, site.getAddress().getSubdivisionIsoCode());
            site.getAddress().setSubdivisionIsoCode(currentValue);

            UpdatePartnerRequestBuilder.stateProvinceValidation("is at maximum allowed character limit", request);
            Assert.assertNotNull(site.getAddress().getSubdivisionIsoCode());
            Assert.assertNotEquals(currentValue, site.getAddress().getSubdivisionIsoCode());
            Assert.assertEquals(2, site.getAddress().getSubdivisionIsoCode().length());
            site.getAddress().setSubdivisionIsoCode(currentValue);

            UpdatePartnerRequestBuilder.stateProvinceValidation("exceeds maximum allowed character limit", request);
            Assert.assertNotNull(site.getAddress().getSubdivisionIsoCode());
            Assert.assertNotEquals(currentValue, site.getAddress().getSubdivisionIsoCode());
            Assert.assertEquals(3, site.getAddress().getSubdivisionIsoCode().length());
            site.getAddress().setSubdivisionIsoCode(currentValue);

            UpdatePartnerRequestBuilder.stateProvinceValidation("contains special characters", request);
            Assert.assertNotNull(site.getAddress().getSubdivisionIsoCode());
            Assert.assertNotEquals(currentValue, site.getAddress().getSubdivisionIsoCode());
            Assert.assertEquals(2, site.getAddress().getSubdivisionIsoCode().length());
            Assert.assertFalse(site.getAddress().getSubdivisionIsoCode().matches("[^A-Za-z0-9]")); // returns false if the string contains anything other than A-Z, a-z or 0-9
        }
    }
}