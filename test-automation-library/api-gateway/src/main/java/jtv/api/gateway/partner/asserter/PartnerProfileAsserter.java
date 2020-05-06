package jtv.api.gateway.partner.asserter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtv.test.db.entity.DbAddress;
import com.jtv.test.db.entity.DbEmailAddress;
import com.jtv.test.db.entity.DbPhoneNumber;
import com.jtv.test.db.entity.partner.*;
import com.jtv.test.db.query.*;
import com.jtv.test.db.query.partner.*;
import com.jtv.test.utils.jdbc.JtvJdbcTemplate;
import jtv.api.gateway.partner.entity.request.create.CreatePartnerProfileRequest;
import jtv.api.gateway.partner.entity.request.create.CreatePartnerSite;
import jtv.api.gateway.partner.entity.request.SiteAddress;
import jtv.api.gateway.partner.entity.request.update.UpdatePartnerRequest;
import jtv.api.gateway.partner.entity.request.update.UpdatePartnerSite;
import jtv.api.gateway.test.client.model.*;
import jtv.assertion.Assertion;
import jtv.assertion.utils.AssertionUtilityFunctions;
import jtv.dao.partner.PartnerDao;
import org.omg.CORBA.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PartnerProfileAsserter {
    private static final Logger log = LoggerFactory.getLogger(PartnerProfileAsserter.class);
    private static String partnerUuid;

    public static String getPartnerUuid() { return partnerUuid; }
    public static void setPartnerUuid(String partnerUuid) { PartnerProfileAsserter.partnerUuid = partnerUuid; }

    public static List<Assertion> assertGetPartner(String responseBody) throws IOException {
        List<Assertion> assertions = new ArrayList<>();
        PartnerProfileServiceGetPartnerResponse response;
        try {
            response = new ObjectMapper().readValue(responseBody,PartnerProfileServiceGetPartnerResponse.class);
        } catch (IOException e) {
            log.error("responseBody = " + responseBody);
            throw e;
        }

        PartnerProfileServicePartnerProfile partnerProfile = response.getPartner();
        partnerUuid = partnerProfile.getPartnerId();

        // assert the partner profile
        assertions.add(mapAndAssertPartnerProfile(null, null, partnerProfile));
        // assert the primary contact for the partner
        assertions.add(mapAndAssertPrimaryContact(null,null, partnerProfile));
        // assert the phone number for the primary contact
        assertions.add(mapAndAssertPrimaryContactPhone(null, null, partnerProfile));
        // assert the email address for the primary contact
        assertions.add(mapAndAssertPrimaryContactEmail(null, null, partnerProfile));
        // assert the partner site
        assertions.add(mapAndAssertPartnerSite(null, null, partnerProfile));
        // Assert Partner Site Usage
        for (PartnerProfileServicePartnerSite site:partnerProfile.getPartnerSites()) {
            // assert each usage type for each site
            for (PartnerProfileServicePartnerSite.PartnerSiteUsageTypesEnum usage:site.getPartnerSiteUsageTypes()) {
                assertions.add(mapAndAssertPartnerSiteUsage(null, usage));
            }
            // assert the address for each site
            assertions.add(mapAndAssertPartnerSiteAddress(null, site.getAddress()));
        }
        return assertions;
}

    public static List<Assertion> assertGetPartnerProfiles(String responseBody) throws IOException{
        List<Assertion> assertions = new ArrayList<>();
        PartnerProfileServiceGetPartnerProfilesResponse response;

        try {
            response = new ObjectMapper().readValue(responseBody, PartnerProfileServiceGetPartnerProfilesResponse.class);
        } catch (IOException e) {
            log.error("[assertGetPartnerProfiles] responseBody = " + responseBody);
            throw e;
        }

        for (PartnerProfileServicePartnerProfile partnerProfile: response.getPartnerProfiles()) {
            partnerUuid = partnerProfile.getPartnerId();
            // assert the partner profile
            assertions.add(mapAndAssertPartnerProfile(null, null, partnerProfile));
            // assert the primary contact for the partner
            assertions.add(mapAndAssertPrimaryContact(null, null, partnerProfile));
            // assert the phone number for the primary contact
            assertions.add(mapAndAssertPrimaryContactPhone(null, null, partnerProfile));
            // assert the email address for the primary contact
            assertions.add(mapAndAssertPrimaryContactEmail(null, null, partnerProfile));
            // assert the partner site
            assertions.add(mapAndAssertPartnerSite(null, null, partnerProfile));
            // Assert Partner Site Usage
            for (PartnerProfileServicePartnerSite site:partnerProfile.getPartnerSites()) {
                // assert each usage type for each site
                for (PartnerProfileServicePartnerSite.PartnerSiteUsageTypesEnum usage:site.getPartnerSiteUsageTypes()) {
                    assertions.add(mapAndAssertPartnerSiteUsage(null, usage));
                }
                // assert the address for each site
                assertions.add(mapAndAssertPartnerSiteAddress(null, site.getAddress()));
            }

        }

        return assertions;
    }

    public static List<Assertion> assertCreatePartnerRequest(String requestString, String responseString) throws IOException {
        List<Assertion>  assertions = new ArrayList<>();

        PartnerProfileServiceCreatePartnerResponse response;
        CreatePartnerProfileRequest request;

        try {
            response = new ObjectMapper().readValue(responseString, PartnerProfileServiceCreatePartnerResponse.class);
            request = new ObjectMapper().readValue(requestString, CreatePartnerProfileRequest.class);
        } catch (IOException e) {
            log.error("[assertCreatePartnerRequest] responseString = " + responseString);
            log.error("[assertCreatePartnerRequest] requestString = " + requestString);
            throw e;
        }
        partnerUuid = response.getPartnerId();

        // Assert Partner Profile
        assertions.add(mapAndAssertPartnerProfile(null, request, null));
        // Assert Partner Primary Contact
        assertions.add(mapAndAssertPrimaryContact(null, request, null));
        // Assert the phone number for the Primary Contact
        assertions.add(mapAndAssertPrimaryContactPhone(null, request, null));
        // Assert the email address for the Primary Contact
        assertions.add(mapAndAssertPrimaryContactEmail(null, request, null));
        // Assert Partner Site
        assertions.add(mapAndAssertPartnerSite(null, request, null));
        // Assert Partner Site Usage
        for (CreatePartnerSite site : request.getCreatePartnerProfile().getPartnerSites()) {
            for (String usage : site.getPartnerSiteUsageTypes()) {
                assertions.add(mapAndAssertPartnerSiteUsage(usage, null));
            }
            // Assert Partner Site Address

            assertions.add(mapAndAssertPartnerSiteAddress(site.getAddress(),null));
        }

        return assertions;
    }

    public static List<Assertion> assertUpdatePartnerRequest(String requestString, String partnerUuid) throws IOException{
        List<Assertion>  assertions = new ArrayList<>();
        UpdatePartnerRequest request;

        try {
            request = new ObjectMapper().readValue(requestString, UpdatePartnerRequest.class);
        } catch (IOException e) {
            log.error("[assertCreatePartnerRequest] Failed to parse requestString (" + requestString + ")");
            throw e;
        }

        setPartnerUuid(partnerUuid);
        // Assert Partner Profile
        assertions.add(mapAndAssertPartnerProfile(request, null, null));
        // assert the primary contact for the partner
        assertions.add(mapAndAssertPrimaryContact(request,null, null));
        // assert the phone number for the primary contact
        assertions.add(mapAndAssertPrimaryContactPhone(request, null, null));
        // assert the email address for the primary contact
        assertions.add(mapAndAssertPrimaryContactEmail(request, null, null));
        // assert the partner site
        assertions.add(mapAndAssertPartnerSite(request, null, null));
        // Assert Partner Site Usage
        for (UpdatePartnerSite site:request.getUpdatePartner().getPartnerSites()) {
            // assert each usage type for each site
            for (String usage:site.getPartnerSiteUsageTypes()) {
                assertions.add(mapAndAssertPartnerSiteUsage(usage, null));
            }
            // assert the address for each site
            assertions.add(mapAndAssertPartnerSiteAddress(site.getAddress(),null));
        }

        return assertions;
    }

    //<editor-fold desc="mapAndAssert...methods">
    private static Assertion mapAndAssertPartnerProfile(UpdatePartnerRequest updatePartnerRequest, CreatePartnerProfileRequest createPartnerProfileRequest, PartnerProfileServicePartnerProfile getPartnerProfileResponse) {
        DbPartner expectedPartner = new DbPartner();
        DbPartner actualPartner;

        if (createPartnerProfileRequest == null && updatePartnerRequest == null) {
            expectedPartner = mapGetPartnerResponseToDbPartner(getPartnerProfileResponse);
        } else if (getPartnerProfileResponse == null && updatePartnerRequest == null) {
            expectedPartner = mapCreatePartnerRequestToDbPartner(createPartnerProfileRequest);
        } else if (createPartnerProfileRequest == null && getPartnerProfileResponse == null) {
            expectedPartner = mapUpdatePartnerRequestToDbPartner(updatePartnerRequest);
        }

        actualPartner = DbPartnerQueryBuilder.defaultInstance(getJdbcTemplate()).withUuid(getPartnerUuid()).queryForObject();

        return assertPartner(expectedPartner, actualPartner);
    }

    private static Assertion mapAndAssertPrimaryContact(UpdatePartnerRequest updatePartnerRequest, CreatePartnerProfileRequest createPartnerProfileRequest, PartnerProfileServicePartnerProfile getPartnerProfileResponse) {
        DbAssociate actualPrimaryContact;
        DbAssociate expectedPrimaryContact = new DbAssociate();

        if (updatePartnerRequest == null && createPartnerProfileRequest == null) {
            expectedPrimaryContact = mapGetPartnerResponseToDbAssociate(getPartnerProfileResponse);
        } else if (updatePartnerRequest == null && getPartnerProfileResponse == null ) {
            expectedPrimaryContact = mapCreatePartnerRequestToDbAssociate(createPartnerProfileRequest);
        } else if (createPartnerProfileRequest == null && getPartnerProfileResponse == null) {
            expectedPrimaryContact = mapUpdatePartnerRequestToDbAssociate(updatePartnerRequest);
        }
        actualPrimaryContact = DbAssociateQueryBuilder.defaultInstance(getJdbcTemplate())
                .withAssociateTypeId(DbAssociateTypeQueryBuilder.defaultInstance(getJdbcTemplate()).withCode("PRIMARY_ACCOUNT_MANAGER").queryForObject().getAssociateTypeId())
                .withPartnerId(DbPartnerQueryBuilder.defaultInstance(getJdbcTemplate()).withUuid(partnerUuid).queryForObject().getPartnerId())
                .queryForObject();

        return assertPartnerAssociate(expectedPrimaryContact, actualPrimaryContact);
    }

    private static Assertion mapAndAssertPrimaryContactPhone(UpdatePartnerRequest updatePartnerRequest, CreatePartnerProfileRequest createPartnerProfileRequest, PartnerProfileServicePartnerProfile getPartnerProfileResponse) {
        DbPhoneNumber expectedPhone = new DbPhoneNumber();
        DbPhoneNumber actualPhone = new DbPhoneNumber();

        // mapping the createPartnerProfile request
        if (getPartnerProfileResponse == null && updatePartnerRequest == null) {
            // if the extension is null or empty, then get the phone number without using the extension
            if (createPartnerProfileRequest.getCreatePartnerProfile().getPrimaryContact().getPhone().getExtension() == null || createPartnerProfileRequest.getCreatePartnerProfile().getPrimaryContact().getPhone().getExtension().isEmpty() ) {
                expectedPhone = mapCreatePartnerRequestToDbPhone(createPartnerProfileRequest);
                actualPhone = DbPhoneNumberQueryBuilder.defaultInstance(getJdbcTemplate())
                        .withSubscriberNumber(createPartnerProfileRequest.getCreatePartnerProfile().getPrimaryContact().getPhone().getSubscriberNumber())
                        .withCountryCode(Integer.decode(createPartnerProfileRequest.getCreatePartnerProfile().getPrimaryContact().getPhone().getCountryCode()))
                        .queryForObject();
            }
            // if the country code is null or empty get the phone number without using the country code
            else if (createPartnerProfileRequest.getCreatePartnerProfile().getPrimaryContact().getPhone().getCountryCode() == null || createPartnerProfileRequest.getCreatePartnerProfile().getPrimaryContact().getPhone().getCountryCode().isEmpty() ) {
                expectedPhone = mapCreatePartnerRequestToDbPhone(createPartnerProfileRequest);
                actualPhone = DbPhoneNumberQueryBuilder.defaultInstance(getJdbcTemplate())
                        .withSubscriberNumber(createPartnerProfileRequest.getCreatePartnerProfile().getPrimaryContact().getPhone().getSubscriberNumber())
                        .withExtension(createPartnerProfileRequest.getCreatePartnerProfile().getPrimaryContact().getPhone().getExtension())
                        .queryForObject();
            }
            // if the subscriber number is null or empty, then no phone is created in the database, but the partner is created.  Set both expected & actual to null;
            else if (createPartnerProfileRequest.getCreatePartnerProfile().getPrimaryContact().getPhone().getSubscriberNumber() == null || createPartnerProfileRequest.getCreatePartnerProfile().getPrimaryContact().getPhone().getSubscriberNumber().isEmpty() ) {
               log.info("SubscriberNumber was null/empty in the createPartnerProfileRequest body, no phone number is created in the database for the primary contact even though the partner was created.  Just assert on the initialized variables for the expected & actual phone contacts.");
            }
            //otherwise get the actual values for the phone number
            else {
                expectedPhone = mapCreatePartnerRequestToDbPhone(createPartnerProfileRequest);
                actualPhone = DbPhoneNumberQueryBuilder.defaultInstance(getJdbcTemplate())
                        .withSubscriberNumber(createPartnerProfileRequest.getCreatePartnerProfile().getPrimaryContact().getPhone().getSubscriberNumber())
                        .withCountryCode(Integer.decode(createPartnerProfileRequest.getCreatePartnerProfile().getPrimaryContact().getPhone().getCountryCode()))
                        .withExtension(createPartnerProfileRequest.getCreatePartnerProfile().getPrimaryContact().getPhone().getExtension())
                        .queryForObject();
            }
        }
        // mapping the update response
        if (createPartnerProfileRequest == null && updatePartnerRequest == null) {
            expectedPhone = mapGetPartnerResponseToDbPhone(getPartnerProfileResponse);
            // if the subscriber number is null, then there is no phone number defined in the database, just use the null values from the ini
            if (getPartnerProfileResponse.getPrimaryContact().getPhone().getSubscriberNumber() == null || getPartnerProfileResponse.getPrimaryContact().getPhone().getSubscriberNumber().equals("") ) {
                // since the subscriber number is null or empty, there is no contact type created in the database, so just use the blank initialized values (null/empty) for the assertions
            }
            // if the extension is null or empty, then get the phone number without using the extension
            else if (getPartnerProfileResponse.getPrimaryContact().getPhone().getExtension() == null || getPartnerProfileResponse.getPrimaryContact().getPhone().getExtension().equals("") ) {
                actualPhone = DbPhoneNumberQueryBuilder.defaultInstance(getJdbcTemplate())
                        .withSubscriberNumber(getPartnerProfileResponse.getPrimaryContact().getPhone().getSubscriberNumber())
                        .withCountryCode(Integer.parseInt(getPartnerProfileResponse.getPrimaryContact().getPhone().getCountryCode()))
                        .queryForObject();
            }
            // if the country code is null or empty get the phone number without using the country code
            else if (getPartnerProfileResponse.getPrimaryContact().getPhone().getCountryCode() == null || getPartnerProfileResponse.getPrimaryContact().getPhone().getCountryCode().equals("") ) {
                actualPhone = DbPhoneNumberQueryBuilder.defaultInstance(getJdbcTemplate())
                        .withSubscriberNumber(getPartnerProfileResponse.getPrimaryContact().getPhone().getSubscriberNumber())
                        .withExtension(getPartnerProfileResponse.getPrimaryContact().getPhone().getExtension())
                        .queryForObject();
            }
            //otherwise get the actual values for the phone number
            else {
                actualPhone = DbPhoneNumberQueryBuilder.defaultInstance(getJdbcTemplate())
                        .withSubscriberNumber(getPartnerProfileResponse.getPrimaryContact().getPhone().getSubscriberNumber())
                        .withCountryCode(Integer.parseInt(getPartnerProfileResponse.getPrimaryContact().getPhone().getCountryCode()))
                        .withExtension(getPartnerProfileResponse.getPrimaryContact().getPhone().getExtension())
                        .queryForObject();
            }
        }
        // mapping the updatePartnerRequest
        else if (createPartnerProfileRequest == null && getPartnerProfileResponse == null) {
            log.info("need to add code for update partner profile mapping in order to perform assertions against the request.");
        }
        return assertAssociatePhone(expectedPhone, actualPhone);
    }

    private static Assertion mapAndAssertPrimaryContactEmail(UpdatePartnerRequest updatePartnerRequest, CreatePartnerProfileRequest createPartnerProfileRequest, PartnerProfileServicePartnerProfile getPartnerProfileResponse) {
        DbEmailAddress expectedEmail = new DbEmailAddress();
        DbEmailAddress actualEmail = new DbEmailAddress();

        if (updatePartnerRequest == null && getPartnerProfileResponse == null) {
            expectedEmail = mapCreatePartnerRequestToDbEmail(createPartnerProfileRequest);
            actualEmail = DbEmailAddressQueryBuilder.defaultInstance(getJdbcTemplate())
                    .withEmailAddress(createPartnerProfileRequest.getCreatePartnerProfile().getPrimaryContact().getEmail())
                    .queryForObject();
        } else if (updatePartnerRequest == null && createPartnerProfileRequest == null) {
            expectedEmail = mapGetPartnerResponseToDbEmailAddress(getPartnerProfileResponse);
            actualEmail = DbEmailAddressQueryBuilder.defaultInstance(getJdbcTemplate())
                    .withEmailAddress(getPartnerProfileResponse.getPrimaryContact().getEmail())
                    .queryForObject();
        } else if (createPartnerProfileRequest == null && getPartnerProfileResponse == null) {
            expectedEmail = mapUpdatePartnerRequestToDbEmailAddress(updatePartnerRequest);
            actualEmail = DbEmailAddressQueryBuilder.defaultInstance(getJdbcTemplate())
                    .withEmailAddress(updatePartnerRequest.getUpdatePartner().getPrimaryContact().getEmail())
                    .queryForObject();
        }

        return assertAssociateEmail(expectedEmail, actualEmail);
    }

    private static Assertion mapAndAssertPartnerSite(UpdatePartnerRequest updatePartnerRequest, CreatePartnerProfileRequest createPartnerProfileRequest, PartnerProfileServicePartnerProfile getPartnerProfileResponse) {
        DbSite expectedSite = new DbSite();
        DbSite actualSite = new DbSite();

        if (updatePartnerRequest== null && getPartnerProfileResponse == null) {
            for (CreatePartnerSite site:createPartnerProfileRequest.getCreatePartnerProfile().getPartnerSites()) {
                // if the site name in the request is either empty or null, null is entered in the database.  Set the expected site name to null as well
                if (site.getSiteName() == null || site.getSiteName().isEmpty()) {
                    expectedSite.setSiteName(null);
                    actualSite.setSiteName(null);
                } else {
                    expectedSite = mapCreatePartnerRequestToDbSite(site);
                    actualSite.setSiteName(
                        DbSiteQueryBuilder.defaultInstance(getJdbcTemplate())
                            .withPartnerId(
                                DbPartnerQueryBuilder.defaultInstance(getJdbcTemplate())
                                    .withUuid(partnerUuid)
                                    .queryForObject()
                                    .getPartnerId()
                            )
                            .withSiteName(site.getSiteName())
                            .queryForObject()
                            .getSiteName()
                    );

                }
            }
        } else if (updatePartnerRequest == null && createPartnerProfileRequest == null) {
            for (PartnerProfileServicePartnerSite site : getPartnerProfileResponse.getPartnerSites()) {
                // Test fixture doesn't support using null as a value within a *.with method call.  That will make pulling the database a bit complicated.  So instead, set both names to null and assert
                if (site.getSiteName() == null || site.getSiteName().isEmpty()) {
                    expectedSite.setSiteName(null);
                    actualSite.setSiteName(null);

                } else {
                    expectedSite.setSiteName(site.getSiteName());
                    actualSite.setSiteName(
                        DbSiteQueryBuilder.defaultInstance(getJdbcTemplate())
                            .withPartnerId(
                                DbPartnerQueryBuilder.defaultInstance(getJdbcTemplate())
                                    .withUuid(partnerUuid)
                                    .queryForObject()
                                    .getPartnerId()
                            )
                            .withSiteName(site.getSiteName())
                            .queryForObject()
                            .getSiteName()
                    );
                }
            }
        } else if (createPartnerProfileRequest == null && getPartnerProfileResponse == null) {
            for (UpdatePartnerSite site : updatePartnerRequest.getUpdatePartner().getPartnerSites()) {
                // if the site name in the request is either empty or null, null is entered in the database.  Set the expected site name to null as well
                if (site.getSiteName() == null || site.getSiteName().isEmpty()) {
                    expectedSite.setSiteName(null);
                    actualSite.setSiteName(null);
                } else {
                    expectedSite = mapUpdatePartnerRequestToDbSite(site);
                    actualSite.setSiteName(DbSiteQueryBuilder.defaultInstance(getJdbcTemplate())
                            .withPartnerId(DbPartnerQueryBuilder.defaultInstance(getJdbcTemplate())
                                    .withUuid(partnerUuid)
                                    .queryForObject()
                                    .getPartnerId()
                            ).withSiteName(site.getSiteName())
                            .queryForObject()
                            .getSiteName()
                    );
                }
            }
        }

        return assertPartnerSite(expectedSite, actualSite);
    }

    private static Assertion mapAndAssertPartnerSiteUsage(String createOrUpdatePartnerSiteUsage, PartnerProfileServicePartnerSite.PartnerSiteUsageTypesEnum getPartnerSiteUsage) {
        String expectedUsage = null;
        DbSiteUsageType actualUsage = new DbSiteUsageType();
        if (getPartnerSiteUsage == null) {
            expectedUsage = createOrUpdatePartnerSiteUsage;
            actualUsage.setCode(DbSiteUsageTypeQueryBuilder.defaultInstance(getJdbcTemplate())
                    .withCode(createOrUpdatePartnerSiteUsage)
                    .queryForObject()
                    .getCode()
            );
        } else if (createOrUpdatePartnerSiteUsage == null || createOrUpdatePartnerSiteUsage.isEmpty()) {
            expectedUsage = getPartnerSiteUsage.getValue();
            actualUsage.setCode(DbSiteUsageTypeQueryBuilder.defaultInstance(getJdbcTemplate())
                    .withCode(getPartnerSiteUsage.getValue())
                    .queryForObject()
                    .getCode()
            );
        }
        return assertPartnerSiteUsage(expectedUsage, actualUsage);
    }

    private static Assertion mapAndAssertPartnerSiteAddress(SiteAddress createOrUpdatePartnerAddress, PartnerProfileServiceSiteAddress getPartnerAddress) {
        DbAddress actualAddress = new DbAddress();
        DbAddress expectedAddress = new DbAddress();
        boolean addressLineTwoEmpty;
        boolean addressLineThreeEmpty;

        // we are asserting on the createOrUpdatePartnerAddress entity
        if (getPartnerAddress == null) {
            addressLineTwoEmpty = createOrUpdatePartnerAddress.getAddressLineTwo() == null || createOrUpdatePartnerAddress.getAddressLineTwo().isEmpty();
            addressLineThreeEmpty = createOrUpdatePartnerAddress.getAddressLineThree() == null || createOrUpdatePartnerAddress.getAddressLineThree().isEmpty();
            expectedAddress = mapCreateOrUpdatePartnerRequestToDbAddress(createOrUpdatePartnerAddress);

            if (addressLineTwoEmpty) {
                // if the address line two is empty or null, then query the db without address line 2.
                actualAddress = DbAddressQueryBuilder.defaultInstance(getJdbcTemplate())
                        .withAddressLine1(createOrUpdatePartnerAddress.getAddressLineOne())
                        .withAddressLine3(createOrUpdatePartnerAddress.getAddressLineThree())
                        .withCity(createOrUpdatePartnerAddress.getCity())
                        .withPostalCode(createOrUpdatePartnerAddress.getPostalCode())
                        .withStateProvinceId(DbStateProvinceQueryBuilder.defaultInstance(getJdbcTemplate())
                                .withFullIsoCode(createOrUpdatePartnerAddress.getSubdivisionIsoCode())
                                .queryForObject()
                                .getStateProvinceId()
                        ).withCountryId(DbCountryQueryBuilder.defaultInstance(getJdbcTemplate())
                                .withIsoAlpha3Code(createOrUpdatePartnerAddress.getCountryIso3Code())
                                .queryForObject()
                                .getCountryId()
                        ).queryForObject();

            } else if (addressLineThreeEmpty) {
                // if the address line three is empty or null, don't use it in the query
                actualAddress = DbAddressQueryBuilder.defaultInstance(getJdbcTemplate())
                        .withAddressLine1(createOrUpdatePartnerAddress.getAddressLineOne())
                        .withAddressLine2(createOrUpdatePartnerAddress.getAddressLineTwo())
                        .withCity(createOrUpdatePartnerAddress.getCity())
                        .withPostalCode(createOrUpdatePartnerAddress.getPostalCode())
                        .withStateProvinceId(DbStateProvinceQueryBuilder.defaultInstance(getJdbcTemplate())
                                .withFullIsoCode(createOrUpdatePartnerAddress.getSubdivisionIsoCode())
                                .queryForObject()
                                .getStateProvinceId()
                        ).withCountryId(DbCountryQueryBuilder.defaultInstance(getJdbcTemplate())
                                .withIsoAlpha3Code(createOrUpdatePartnerAddress.getCountryIso3Code())
                                .queryForObject()
                                .getCountryId()
                        ).queryForObject();
            } else {
                // otherwise use all the address lines in the query
                actualAddress = DbAddressQueryBuilder.defaultInstance(getJdbcTemplate())
                        .withAddressLine1(createOrUpdatePartnerAddress.getAddressLineOne())
                        .withAddressLine2(createOrUpdatePartnerAddress.getAddressLineTwo())
                        .withAddressLine3(createOrUpdatePartnerAddress.getAddressLineThree())
                        .withCity(createOrUpdatePartnerAddress.getCity())
                        .withPostalCode(createOrUpdatePartnerAddress.getPostalCode())
                        .withStateProvinceId(DbStateProvinceQueryBuilder.defaultInstance(getJdbcTemplate())
                                .withFullIsoCode(createOrUpdatePartnerAddress.getSubdivisionIsoCode())
                                .queryForObject()
                                .getStateProvinceId()
                        ).withCountryId(DbCountryQueryBuilder.defaultInstance(getJdbcTemplate())
                                .withIsoAlpha3Code(createOrUpdatePartnerAddress.getCountryIso3Code())
                                .queryForObject()
                                .getCountryId()
                        ).queryForObject();
            }
        } else if (createOrUpdatePartnerAddress == null) {
            addressLineTwoEmpty = getPartnerAddress.getAddressLineTwo() == null || getPartnerAddress.getAddressLineTwo().isEmpty();
            addressLineThreeEmpty = getPartnerAddress.getAddressLineThree() == null || getPartnerAddress.getAddressLineThree().isEmpty();
            expectedAddress = mapGetPartnerResponseToDbAddress(getPartnerAddress);

            if (addressLineTwoEmpty) {
                // if the address line 2 is empty or null, then query the db without it
                actualAddress = DbAddressQueryBuilder.defaultInstance(getJdbcTemplate())
                        .withAddressLine1(getPartnerAddress.getAddressLineOne())
                        .withAddressLine3(getPartnerAddress.getAddressLineThree())
                        .withCity(getPartnerAddress.getCity())
                        .withPostalCode(getPartnerAddress.getPostalCode())
                        .queryForObject();
            } else if (addressLineThreeEmpty) {
                // if the address line 3 is empty or null, then query the db without it
                actualAddress = DbAddressQueryBuilder.defaultInstance(getJdbcTemplate())
                        .withAddressLine1(getPartnerAddress.getAddressLineOne())
                        .withAddressLine2(getPartnerAddress.getAddressLineTwo())
                        .withCity(getPartnerAddress.getCity())
                        .withPostalCode(getPartnerAddress.getPostalCode())
                        .queryForObject();
            } else {
                // otherwise us it in the query
                actualAddress = DbAddressQueryBuilder.defaultInstance(getJdbcTemplate())
                        .withAddressLine1(getPartnerAddress.getAddressLineOne())
                        .withAddressLine2(getPartnerAddress.getAddressLineTwo())
                        .withAddressLine3(getPartnerAddress.getAddressLineThree())
                        .withCity(getPartnerAddress.getCity())
                        .withPostalCode(getPartnerAddress.getPostalCode())
                        .queryForObject();
            }
        }
        log.info("expectedAddress = " + expectedAddress.toString());
        log.info("actualAddress = " + actualAddress.toString());
        return assertPartnerSiteAddress(expectedAddress, actualAddress);
    }
    //</editor-fold>

    //<editor-fold desc="mapGetPartnerResponseTo...methods">
    private static DbPartner mapGetPartnerResponseToDbPartner(PartnerProfileServicePartnerProfile partnerProfile) {
        DbPartner partner = new DbPartner();

        partner.setUuid(partnerProfile.getPartnerId());
        partner.setPartnerStatusId(
                DbPartnerStatusQueryBuilder.defaultInstance(getJdbcTemplate())
                        .withCode(partnerProfile.getPartnerAccountStatus().name())
                        .queryForObject().getPartnerStatusId()
        );
        partner.setOperatingName(partnerProfile.getOperatingName());
        partner.setLegalName(partnerProfile.getLegalEntityName());

        return partner;
    }

    private static DbAssociate mapGetPartnerResponseToDbAssociate(PartnerProfileServicePartnerProfile partnerProfile) {
        DbAssociate associate = new DbAssociate();

        associate.setFirstName(partnerProfile.getPrimaryContact().getFirstName());
        associate.setLastName(partnerProfile.getPrimaryContact().getLastName());
        associate.setTitle(partnerProfile.getPrimaryContact().getTitle());

        return associate;
    }

    private static DbPhoneNumber mapGetPartnerResponseToDbPhone(PartnerProfileServicePartnerProfile partnerProfile) {
        DbPhoneNumber phone = new DbPhoneNumber();

        phone.setSubscriberNumber(partnerProfile.getPrimaryContact().getPhone().getSubscriberNumber());
        phone.setCountryCode(Integer.parseInt(partnerProfile.getPrimaryContact().getPhone().getCountryCode()));
        phone.setExtension(partnerProfile.getPrimaryContact().getPhone().getExtension());

        return phone;
    }

    private static DbEmailAddress mapGetPartnerResponseToDbEmailAddress(PartnerProfileServicePartnerProfile partnerProfile) {
        DbEmailAddress email = new DbEmailAddress();

        email.setEmailAddress(partnerProfile.getPrimaryContact().getEmail());

        return email;
    }

    private static DbAddress mapGetPartnerResponseToDbAddress(PartnerProfileServiceSiteAddress partnerSiteAddress) {
        DbAddress address = new DbAddress();

        address.setAddressLine1(partnerSiteAddress.getAddressLineOne());
        address.setAddressLine2(partnerSiteAddress.getAddressLineTwo());
        address.setAddressLine3(partnerSiteAddress.getAddressLineThree());
        address.setCity(partnerSiteAddress.getCity());
        address.setCountryId(DbCountryQueryBuilder.defaultInstance(getJdbcTemplate())
                    .withIsoAlpha2Code(partnerSiteAddress.getCountryIso3Code())
                    .queryForObject()
                    .getCountryId()
        );
        address.setStateProvinceId(DbStateProvinceQueryBuilder.defaultInstance(getJdbcTemplate())
                    .withIsoAlpha2Code(partnerSiteAddress.getSubdivisionIsoCode())
                    .withCountryId(address.getCountryId())
                    .queryForObject()
                    .getStateProvinceId()
        );
        address.setPostalCode(partnerSiteAddress.getPostalCode());

        return address;
    }
    //</editor-fold>

    //<editor-fold desc="mapCreatePartnerRequestTo...methods">
    private static DbPartner mapCreatePartnerRequestToDbPartner(CreatePartnerProfileRequest request) {
        DbPartner partner = new DbPartner();

        partner.setUuid(partnerUuid);
        partner.setLegalName(request.getCreatePartnerProfile().getLegalEntityName());
        partner.setOperatingName(request.getCreatePartnerProfile().getOperatingName());
        partner.setPartnerStatusId(DbPartnerStatusQueryBuilder.defaultInstance(getJdbcTemplate()).withCode(request.getCreatePartnerProfile().getPartnerAccountStatus()).queryForObject().getPartnerStatusId());

        return partner;
    }

    private static DbAssociate mapCreatePartnerRequestToDbAssociate(CreatePartnerProfileRequest request) {
        DbAssociate associate = new DbAssociate();

        associate.setFirstName(request.getCreatePartnerProfile().getPrimaryContact().getFirstName());
        associate.setLastName(request.getCreatePartnerProfile().getPrimaryContact().getLastName());
        associate.setTitle(request.getCreatePartnerProfile().getPrimaryContact().getTitle());
        associate.setAssociateTypeId(DbAssociateTypeQueryBuilder.defaultInstance(getJdbcTemplate()).withCode("PRIMARY_ACCOUNT_MANAGER").queryForObject().getAssociateTypeId());

        return associate;
    }

    private static DbPhoneNumber mapCreatePartnerRequestToDbPhone(CreatePartnerProfileRequest request) {
        DbPhoneNumber phone = new DbPhoneNumber();

        phone.setSubscriberNumber(request.getCreatePartnerProfile().getPrimaryContact().getPhone().getSubscriberNumber());
        try {
            phone.setCountryCode(Integer.decode(request.getCreatePartnerProfile().getPrimaryContact().getPhone().getCountryCode()));
        } catch (NullPointerException e) {
            log.info("CountryCode is null in the request, '1' is inserted as the default value, setting the expected value to 1 for assertion.");
            phone.setCountryCode(1);
        }
        phone.setExtension(request.getCreatePartnerProfile().getPrimaryContact().getPhone().getExtension());

        return phone;
    }

    private static DbEmailAddress mapCreatePartnerRequestToDbEmail(CreatePartnerProfileRequest request) {
        DbEmailAddress email = new DbEmailAddress();
        email.setEmailAddress(request.getCreatePartnerProfile().getPrimaryContact().getEmail());
        return email;
    }

    private static DbSite mapCreatePartnerRequestToDbSite(CreatePartnerSite partnerSite) {
        DbSite site = new DbSite();

        site.setSiteName(partnerSite.getSiteName());
        return site;
    }

    private static DbAddress mapCreateOrUpdatePartnerRequestToDbAddress(SiteAddress siteAddress) {
        DbAddress address = new DbAddress();
        address.setAddressLine1(siteAddress.getAddressLineOne());
        address.setAddressLine2(siteAddress.getAddressLineTwo());
        address.setAddressLine3(siteAddress.getAddressLineThree());
        address.setCity(siteAddress.getCity());
        address.setStateProvinceId(
                DbStateProvinceQueryBuilder.defaultInstance(getJdbcTemplate())
                        .withFullIsoCode(siteAddress.getSubdivisionIsoCode())
                        .queryForObject()
                        .getStateProvinceId()
        );
        address.setPostalCode(siteAddress.getPostalCode());
        address.setCountryId(DbCountryQueryBuilder.defaultInstance(getJdbcTemplate())
                .withIsoAlpha3Code(siteAddress.getCountryIso3Code())
                .queryForObject()
                .getCountryId()
        );

        return address;
    }
    //</editor-fold>

    //<editor-fold desc="mapUpdatePartnerRequestTo...methods">
    private static DbPartner mapUpdatePartnerRequestToDbPartner(UpdatePartnerRequest request) {
        DbPartner partner = new DbPartner();

        partner.setUuid(partnerUuid);
        partner.setLegalName(request.getUpdatePartner().getLegalEntityName());
        partner.setOperatingName(request.getUpdatePartner().getOperatingName());
        partner.setPartnerStatusId(DbPartnerStatusQueryBuilder.defaultInstance(getJdbcTemplate())
                .withCode(request.getUpdatePartner().getPartnerAccountStatus())
                .queryForObject()
                .getPartnerStatusId()
        );

        return partner;
    }

    private static DbAssociate mapUpdatePartnerRequestToDbAssociate(UpdatePartnerRequest request) {
        DbAssociate associate = new DbAssociate();

        associate.setFirstName(request.getUpdatePartner().getPrimaryContact().getFirstName());
        associate.setLastName(request.getUpdatePartner().getPrimaryContact().getLastName());
        associate.setTitle(request.getUpdatePartner().getPrimaryContact().getTitle());
        associate.setAssociateTypeId(DbAssociateTypeQueryBuilder.defaultInstance(getJdbcTemplate()).withCode("PRIMARY_ACCOUNT_MANAGER").queryForObject().getAssociateTypeId());

        return associate;
    }

    private static DbEmailAddress mapUpdatePartnerRequestToDbEmailAddress(UpdatePartnerRequest request) {
        DbEmailAddress email = new DbEmailAddress();

        email.setEmailAddress(request.getUpdatePartner().getPrimaryContact().getEmail());

        return email;
    }

    private static DbSite mapUpdatePartnerRequestToDbSite(UpdatePartnerSite partnerSite) {
        DbSite site = new DbSite();

        site.setSiteName(partnerSite.getSiteName());

        return site;
    }
    //</editor-fold>

    //<editor-fold desc="assertion methods">
    private static Assertion assertPartner(DbPartner expectedPartner, DbPartner actualPartner) {
        Assertion assertions;

        assertions = new Assertion("Partner (" + partnerUuid + ") Assertions");

        assertions = AssertionUtilityFunctions.assertValues(assertions, "Partner (" + partnerUuid + ") UUID", expectedPartner.getUuid(), actualPartner.getUuid());
        assertions = AssertionUtilityFunctions.assertValues(assertions, "Partner (" + partnerUuid + ") Legal Name", expectedPartner.getLegalName(), actualPartner.getLegalName());
        assertions = AssertionUtilityFunctions.assertValues(assertions, "Partner (" + partnerUuid + ") Operating Name", expectedPartner.getOperatingName(), actualPartner.getOperatingName());
        assertions = AssertionUtilityFunctions.assertValues(assertions, "Partner (" + partnerUuid + ") Partner Status Id", expectedPartner.getPartnerStatusId(), actualPartner.getPartnerStatusId());

        return assertions;
    }
    
    private static Assertion assertPartnerAssociate(DbAssociate expectedAssociate, DbAssociate actualAssociate) {
        Assertion assertions = new Assertion("Associate for Partner (" + partnerUuid + ")");

        assertions = AssertionUtilityFunctions.assertValues(assertions, "Associate for Partner (" + partnerUuid + ") - First Name", expectedAssociate.getFirstName(), actualAssociate.getFirstName());
        assertions = AssertionUtilityFunctions.assertValues(assertions, "Associate for Partner (" + partnerUuid + ") - Last Name", expectedAssociate.getLastName(), actualAssociate.getLastName());
        // if a field is either null or empty ("") in the request, a null is entered into the database.  Since the Title is an optional field, if either is null or empty, just compare them as being empty
        try  {
            assertions = AssertionUtilityFunctions.assertValues(assertions, "Associate for Partner (" + partnerUuid + ") - Title", expectedAssociate.getTitle().equals("")? null : expectedAssociate.getTitle(), actualAssociate.getTitle());
        } catch (NullPointerException e) {
            assertions = AssertionUtilityFunctions.assertValues(assertions, "Associate for Partner (" + partnerUuid + ") - Title", null, null);
        }

        return assertions;
    }

    private static Assertion assertAssociatePhone(DbPhoneNumber expectedPhone, DbPhoneNumber actualPhone) throws NullPointerException{
        Assertion assertions = new Assertion("Associate Phone Number for Partner (" + partnerUuid + ")");

        assertions = AssertionUtilityFunctions.assertValues(assertions, "Associate Phone Number for Partner (" + partnerUuid + ") - Subscriber Number", expectedPhone.getSubscriberNumber(), actualPhone.getSubscriberNumber());
        assertions = AssertionUtilityFunctions.assertValues(assertions, "Associate Phone Number for Partner (" + partnerUuid + ") - Country Code", expectedPhone.getCountryCode(), actualPhone.getCountryCode());

        try  {
            assertions = AssertionUtilityFunctions.assertValues(assertions, "Associate Phone Number for Partner (" + partnerUuid + ") - Extension", expectedPhone.getExtension().equals("")? null : expectedPhone.getExtension(), actualPhone.getExtension());
        } catch (NullPointerException e) {
            assertions = AssertionUtilityFunctions.assertValues(assertions, "Associate Phone Number for Partner (" + partnerUuid + ") - Extension", null, null);
        }

        return assertions;
    }

    private static Assertion assertAssociateEmail(DbEmailAddress expectedEmail, DbEmailAddress actualEmail) {
        Assertion assertions = new Assertion("Associate Email Address for Partner (" + partnerUuid + ")");

        assertions = AssertionUtilityFunctions.assertValues(assertions, "Associate Email Address for Partner (" + partnerUuid + ") ", expectedEmail.getEmailAddress(), actualEmail.getEmailAddress());

        return assertions;
    }

    private static Assertion assertPartnerSite(DbSite expectedSite, DbSite actualSite) {
        Assertion assertions = new Assertion("Site Name for Partner (" + partnerUuid + ")");

        assertions = AssertionUtilityFunctions.assertValues(assertions, "Site Name for Partner (" + partnerUuid + ")", expectedSite.getSiteName(), actualSite.getSiteName());

        return assertions;
    }

    private static Assertion assertPartnerSiteUsage(String expectedUsage, DbSiteUsageType actualUsage) {
        Assertion assertions = new Assertion("Site Usage for Partner (" + partnerUuid + ")");

        assertions = AssertionUtilityFunctions.assertValues(assertions, "Site Usage for Partner (" + partnerUuid + ")", expectedUsage, actualUsage.getCode());

        return assertions;
    }

    private static Assertion assertPartnerSiteAddress(DbAddress expectedAddress, DbAddress actualAddress) {
        Assertion assertions = new Assertion("Site Address for Partner (" + partnerUuid + ")");

        assertions = AssertionUtilityFunctions.assertValues(assertions, "Site Address for Partner (" + partnerUuid + ") - Address Line One", expectedAddress.getAddressLine1(), actualAddress.getAddressLine1());

        try  {
            assertions = AssertionUtilityFunctions.assertValues(assertions, "Site Address for Partner (" + partnerUuid + ") - Address Line Two", expectedAddress.getAddressLine2().equals("")? null : expectedAddress.getAddressLine2(), actualAddress.getAddressLine2());
        } catch (NullPointerException e) {
            assertions = AssertionUtilityFunctions.assertValues(assertions, "Site Address for Partner (" + partnerUuid + ") - Address Line Two", null, null);
        }

        try  {
            assertions = AssertionUtilityFunctions.assertValues(assertions, "Site Address for Partner (" + partnerUuid + ") - Address Line Three", expectedAddress.getAddressLine3().equals("")? null : expectedAddress.getAddressLine3(), actualAddress.getAddressLine3());
        } catch (NullPointerException e) {
            assertions = AssertionUtilityFunctions.assertValues(assertions, "Site Address for Partner (" + partnerUuid + ") - Address Line Three", null, null);
        }

        assertions = AssertionUtilityFunctions.assertValues(assertions, "Site Address for Partner (" + partnerUuid + ") - City", expectedAddress.getCity(), actualAddress.getCity());
        assertions = AssertionUtilityFunctions.assertValues(assertions, "Site Address for Partner (" + partnerUuid + ") - State ID", expectedAddress.getStateProvinceId(), actualAddress.getStateProvinceId());
        assertions = AssertionUtilityFunctions.assertValues(assertions, "Site Address for Partner (" + partnerUuid + ") - Country ID", expectedAddress.getCountryId(), actualAddress.getCountryId());
        assertions = AssertionUtilityFunctions.assertValues(assertions, "Site Address for Partner (" + partnerUuid + ") - Postal Code", expectedAddress.getPostalCode(), actualAddress.getPostalCode());

        return assertions;
    }
    //</editor-fold>

    private static PartnerDao getPartnerDao() {
        return new PartnerDao();
    }

    public static JtvJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public static void setJdbcTemplate(JtvJdbcTemplate jdbcTemplate) {
        PartnerProfileAsserter.jdbcTemplate = jdbcTemplate;
    }

    private static JtvJdbcTemplate jdbcTemplate = new JtvJdbcTemplate(getPartnerDao().getDataSource());

}
