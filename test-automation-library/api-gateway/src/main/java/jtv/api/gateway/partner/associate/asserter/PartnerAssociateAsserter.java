package jtv.api.gateway.partner.associate.asserter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtv.test.db.entity.DbEmailAddress;
import com.jtv.test.db.entity.DbPhoneNumber;
import com.jtv.test.db.entity.partner.DbAssociate;
import com.jtv.test.db.query.DbEmailAddressQueryBuilder;
import com.jtv.test.db.query.DbPhoneNumberQueryBuilder;
import com.jtv.test.db.query.partner.DbAssociateQueryBuilder;
import com.jtv.test.db.query.partner.DbAssociateTypeQueryBuilder;
import com.jtv.test.db.query.partner.DbContactPointQueryBuilder;
import com.jtv.test.utils.jdbc.JtvJdbcTemplate;
import jline.internal.Log;
import jtv.api.gateway.partner.associate.entity.request.create.CreatePartnerAssociateRequest;
import jtv.api.gateway.test.client.model.PartnerAssociateServiceCreatePartnerAssociateResponse;
import jtv.api.gateway.test.client.model.PartnerAssociateServiceGetPartnerAssociatesResponse;
import jtv.api.gateway.test.client.model.PartnerAssociateServicePartnerAssociate;
import jtv.assertion.Assertion;
import jtv.assertion.utils.AssertionUtilityFunctions;
import jtv.dao.partner.PartnerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PartnerAssociateAsserter {
    private static final Logger log = LoggerFactory.getLogger(PartnerAssociateAsserter.class);

    JtvJdbcTemplate jdbcTemplate;

    public PartnerAssociateAsserter(){
        jdbcTemplate = new PartnerDao().getJtvJdbcTemplate();
    }

    public PartnerAssociateAsserter(JtvJdbcTemplate jtvJdbcTemplate){
        jdbcTemplate = jtvJdbcTemplate;
    }

    public List<Assertion> assertGetPartnerAssociate(String responseBody, String associateUuid) throws IOException {
        List<Assertion> assertions = new ArrayList<>();
        PartnerAssociateServiceGetPartnerAssociatesResponse response;

        try {
            response = new ObjectMapper().readValue(responseBody, PartnerAssociateServiceGetPartnerAssociatesResponse.class);
        } catch (IOException e) {
            Log.error("[assertGetPartnerAssociate] Failed to parse responseBody ( responseBody = " + responseBody + ")");
            throw e;
        }

        // Map and assert the Partner Associate
        // Since this is the get partner associate list request, send null for request
        assertions.add(mapAndAssertPartnerAssociate(null, response, associateUuid));
        // Map and assert the Associate Email Address
        assertions.add(mapAndAssertAssociateEmail(null, response, associateUuid));
        // Map and assert the Associate Phone Number
        assertions.add(mapAndAssertAssociatePhone(null, response, associateUuid));

        return assertions;
    }

    public List<Assertion> assertCreatePartnerAssociate(String requestBody, String responseBody) throws IOException {
        List<Assertion> assertions = new ArrayList<>();
        PartnerAssociateServiceCreatePartnerAssociateResponse response;
        CreatePartnerAssociateRequest request;

        try {
            response = new ObjectMapper().readValue(responseBody, PartnerAssociateServiceCreatePartnerAssociateResponse.class);
            request = new ObjectMapper().readValue(requestBody, CreatePartnerAssociateRequest.class);
        } catch (IOException e) {
            log.error("[assertCreatePartnerAssociate] Failed to parse responseBody ( responseBody = " + responseBody + ")" );
            log.error("[assertCreatePartnerAssociate] Failed to parse requestBody ( requestBody = " + requestBody + ")" );
            throw e;
        }

        // Map and assert the Partner Associate
        // Since this is the create partner associate request, send null for responseBody
        assertions.add(mapAndAssertPartnerAssociate(request, null, response.getPartnerAssociateId()));
        // Map and assert the Associate Email Address
        assertions.add(mapAndAssertAssociateEmail(request, null, response.getPartnerAssociateId()));
        // Map and assert the Associate Phone Number
        assertions.add(mapAndAssertAssociatePhone(request, null, response.getPartnerAssociateId()));

        return assertions;
    }

    private Assertion mapAndAssertPartnerAssociate(CreatePartnerAssociateRequest request, PartnerAssociateServiceGetPartnerAssociatesResponse response, String partnerAssociateUuid) {
        DbAssociate expectedAssociate;
        DbAssociate actualAssociate;

        // if we have a POST, then the expected associate is whatever is in the request body, otherwise it is from the database since we are doing a GET
        if (request != null) {
            actualAssociate = DbAssociateQueryBuilder.defaultInstance(jdbcTemplate).withUuid(partnerAssociateUuid).queryForObject();
            expectedAssociate = mapCreatePartnerAssociateRequestToDbAssociate(request, partnerAssociateUuid);
        } else {
            expectedAssociate = DbAssociateQueryBuilder.defaultInstance(jdbcTemplate).withUuid(partnerAssociateUuid).queryForObject();
            actualAssociate= mapGetPartnerAssociateListResponseToDbAssociate(response, partnerAssociateUuid);
        }

        return assertAssociate(expectedAssociate, actualAssociate, partnerAssociateUuid);
    }

    private Assertion mapAndAssertAssociateEmail(CreatePartnerAssociateRequest requestBody, PartnerAssociateServiceGetPartnerAssociatesResponse responseBody, String partnerAssociateUuid) {
        DbEmailAddress expectedEmail = null;
        DbEmailAddress actualEmail = null;

        // if we have a POST, then the expected associate is whatever is in the request body, otherwise it is from the database since we are doing a GET
        if (requestBody != null) {
            expectedEmail = mapCreatePartnerAssociateRequestToDbEmailAddress(requestBody);
            actualEmail  = DbEmailAddressQueryBuilder.defaultInstance(jdbcTemplate)
                    .withEmailAddressId(DbContactPointQueryBuilder.defaultInstance(jdbcTemplate)
                            .withAssociateId(DbAssociateQueryBuilder.defaultInstance(jdbcTemplate)
                                    .withUuid(partnerAssociateUuid)
                                    .queryForObject()
                                    .getAssociateId()
                            ).withContactPointTypeId(1L)// 1 = email, 2 = phone
                            .queryForObject()
                            .getEmailAddressId()
                    ).queryForObject();
        } else if (responseBody != null) {
//            log.info("[mapAndAssertAssociateEmail] response" + responseBody.getPartnerAssociates().);
            actualEmail = mapGetPartnerAssociateListResponseToDbEmailAddress(responseBody, partnerAssociateUuid);
            expectedEmail  = DbEmailAddressQueryBuilder.defaultInstance(jdbcTemplate)
                            .withEmailAddressId(DbContactPointQueryBuilder.defaultInstance(jdbcTemplate)
                                .withAssociateId(DbAssociateQueryBuilder.defaultInstance(jdbcTemplate)
                                    .withUuid(partnerAssociateUuid)
                                    .queryForObject()
                                    .getAssociateId()
                            ).withContactPointTypeId(1L)// 1 = email, 2 = phone
                            .queryForObject()
                            .getEmailAddressId()
                    ).queryForObject();
        }

        assert expectedEmail != null;
        assert actualEmail != null;
        return assertAssociateEmail(expectedEmail, actualEmail, partnerAssociateUuid);

    }

    private Assertion mapAndAssertAssociatePhone(CreatePartnerAssociateRequest requestBody, PartnerAssociateServiceGetPartnerAssociatesResponse responseBody, String partnerAssociateUuid) {
        DbPhoneNumber expectedPhone = null;
        DbPhoneNumber actualPhone = null;

        // if we have a POST, then the expected associate is whatever is in the request body, otherwise it is from the database since we are doing a GET
        if ( requestBody != null) {
            expectedPhone = mapCreatePartnerAssociateRequestToDbPhoneNumber(requestBody);
            actualPhone = DbPhoneNumberQueryBuilder.defaultInstance(jdbcTemplate)
                         .withSubscriberNumber(requestBody.getCreatePartnerAssociate().getPhone().getSubscriberNumber())
                         .withCountryCode(Integer.decode(requestBody.getCreatePartnerAssociate().getPhone().getCountryCode()))
                         .queryForObject();
        } else if ( responseBody != null ) {
            expectedPhone = mapGetPartnerAssociateListResponseToDbPhoneNumber(responseBody, partnerAssociateUuid);
            actualPhone = DbPhoneNumberQueryBuilder.defaultInstance(jdbcTemplate)
                          .withPhoneNumberId(DbContactPointQueryBuilder.defaultInstance(jdbcTemplate)
                                  .withAssociateId(DbAssociateQueryBuilder.defaultInstance(jdbcTemplate)
                                          .withUuid(partnerAssociateUuid)
                                          .queryForObject()
                                          .getAssociateId()
                                  ).withContactPointTypeId(2L) //1 = email, 2 = phone
                                  .queryForObject()
                                  .getPhoneNumberId()
                          ).queryForObject();
        }

        // since the extension is an optional field, a null value is always entered in the database regardless of what is entered in the request body.  Let's assert on actual strings depending on whether the request body
        // an empty string or a null value
        if ( expectedPhone.getExtension() == null) {
            expectedPhone.setExtension("isNull");
            actualPhone.setExtension("isNull");
        } else if (expectedPhone.getExtension().isEmpty()) {
            expectedPhone.setExtension("isEmpty");
            actualPhone.setExtension("isEmpty");
        }


        return assertAssociatePhone(expectedPhone, actualPhone, partnerAssociateUuid);
    }

    private DbAssociate mapCreatePartnerAssociateRequestToDbAssociate(CreatePartnerAssociateRequest requestBody, String partnerAssociateUuid) {
        DbAssociate associate = new DbAssociate();

        associate.setFirstName(requestBody.getCreatePartnerAssociate().getFirstName());
        associate.setLastName(requestBody.getCreatePartnerAssociate().getLastName());
        associate.setTitle(requestBody.getCreatePartnerAssociate().getTitle());
        associate.setUuid(partnerAssociateUuid);
        associate.setAssociateTypeId(DbAssociateTypeQueryBuilder.defaultInstance(jdbcTemplate)
                .withCode(requestBody.getCreatePartnerAssociate().getPartnerAssociateType())
                .queryForObject()
                .getAssociateTypeId()
        );

        return associate;
    }

    private DbAssociate mapGetPartnerAssociateListResponseToDbAssociate(PartnerAssociateServiceGetPartnerAssociatesResponse responseBody, String partnerAssociateUuid) {
        DbAssociate associate = new DbAssociate();

         for ( PartnerAssociateServicePartnerAssociate currentAssociate : responseBody.getPartnerAssociates() ) {
            if (currentAssociate.getPartnerAssociateId().equals(partnerAssociateUuid)) {
                associate.setFirstName(currentAssociate.getFirstName());
                associate.setLastName(currentAssociate.getLastName());
                associate.setTitle(currentAssociate.getTitle());
                associate.setUuid(currentAssociate.getPartnerAssociateId());
                associate.setAssociateTypeId(DbAssociateTypeQueryBuilder.defaultInstance(jdbcTemplate)
                        .withCode(currentAssociate.getPartnerAssociateType().name())
                        .queryForObject()
                        .getAssociateTypeId()
                );
            }
        }

        return associate;
    }

    private DbEmailAddress mapCreatePartnerAssociateRequestToDbEmailAddress(CreatePartnerAssociateRequest request) {
        DbEmailAddress email = new DbEmailAddress();

        email.setEmailAddress(request.getCreatePartnerAssociate().getEmail());

        return email;
    }

    private DbEmailAddress mapGetPartnerAssociateListResponseToDbEmailAddress(PartnerAssociateServiceGetPartnerAssociatesResponse responseBody, String partnerAssociateUuid) {
    DbEmailAddress email = new DbEmailAddress();

        for ( PartnerAssociateServicePartnerAssociate currentAssociate : responseBody.getPartnerAssociates() ) {
            if (currentAssociate.getPartnerAssociateId().equals(partnerAssociateUuid)) {
                email.setEmailAddress(currentAssociate.getEmail());
            }
        }
                return email;
    }

    private DbPhoneNumber mapCreatePartnerAssociateRequestToDbPhoneNumber(CreatePartnerAssociateRequest request) {
        DbPhoneNumber phone = new DbPhoneNumber();

        phone.setSubscriberNumber(request.getCreatePartnerAssociate().getPhone().getSubscriberNumber());
        phone.setCountryCode(Integer.decode(request.getCreatePartnerAssociate().getPhone().getCountryCode()));
        phone.setExtension(request.getCreatePartnerAssociate().getPhone().getExtension());

        return phone;
    }

    private DbPhoneNumber mapGetPartnerAssociateListResponseToDbPhoneNumber(PartnerAssociateServiceGetPartnerAssociatesResponse response, String partnerAssociateUuid) {
        DbPhoneNumber phone = new DbPhoneNumber();

        for ( PartnerAssociateServicePartnerAssociate currentAssociate : response.getPartnerAssociates() ) {
            if ( currentAssociate.getPartnerAssociateId().equals(partnerAssociateUuid)) {
                assert currentAssociate.getPhone() != null;
                phone.setSubscriberNumber(currentAssociate.getPhone().getSubscriberNumber());

                assert currentAssociate.getPhone().getCountryCode() != null;
                phone.setCountryCode(Integer.decode(currentAssociate.getPhone().getCountryCode()));
                phone.setExtension(currentAssociate.getPhone().getExtension());
            }
        }

        return phone;
    }

    public Assertion assertAssociate(DbAssociate expectedAssociate, DbAssociate actualAssociate, String partnerAssociateUuid) {
        Assertion assertions = new Assertion("Partner Associate (" + partnerAssociateUuid + ")");

        assertions = AssertionUtilityFunctions.assertValues(assertions, "Partner Associate (" + partnerAssociateUuid + ") - First Name", expectedAssociate.getFirstName(), actualAssociate.getFirstName());
        assertions = AssertionUtilityFunctions.assertValues(assertions, "Partner Associate (" + partnerAssociateUuid + ") - Last Name", expectedAssociate.getLastName(), actualAssociate.getLastName());

        try {
            assertions = AssertionUtilityFunctions.assertValues(assertions, "Partner Associate (" + partnerAssociateUuid + ") - Title", expectedAssociate.getTitle().equals("")? null : expectedAssociate.getTitle(), actualAssociate.getTitle());
        } catch (NullPointerException e) {
            assertions = AssertionUtilityFunctions.assertValues(assertions, "Partner Associate (" + partnerAssociateUuid + ") - Title", null, null);
        }

        assertions = AssertionUtilityFunctions.assertValues(assertions, "Partner Associate (" + partnerAssociateUuid + ") - UUID", expectedAssociate.getUuid(), actualAssociate.getUuid());
        assertions = AssertionUtilityFunctions.assertValues(assertions, "Partner Associate (" + partnerAssociateUuid + ") - Associate Type ID", expectedAssociate.getAssociateTypeId(), actualAssociate.getAssociateTypeId());

        return assertions;
    }

    public Assertion assertAssociateEmail(DbEmailAddress expectedEmail, DbEmailAddress actualEmail, String partnerAssociateUuid) {
        Assertion assertions = new Assertion("Partner Associate (" + partnerAssociateUuid +") Email Address");

        assertions = AssertionUtilityFunctions.assertValues(assertions, "Partner Associate (" + partnerAssociateUuid + ") - Email Address", expectedEmail.getEmailAddress(), actualEmail.getEmailAddress());

        return assertions;
    }

    public Assertion assertAssociatePhone(DbPhoneNumber expectedPhone, DbPhoneNumber actualPhone, String partnerAssociateUuid) {
        Assertion assertions = new Assertion("Partner Associate (" + partnerAssociateUuid +") Phone");

        assertions = AssertionUtilityFunctions.assertValues(assertions, "Partner Associate (" + partnerAssociateUuid + ") - Phone: Subscriber Number", expectedPhone.getSubscriberNumber(), actualPhone.getSubscriberNumber());
        try {
            assertions = AssertionUtilityFunctions.assertValues(assertions, "Partner Associate (" + partnerAssociateUuid + ") - Phone: Extension", expectedPhone.getExtension().equals("")?null : expectedPhone.getExtension(), actualPhone.getExtension());
        } catch (NullPointerException e) {
            assertions = AssertionUtilityFunctions.assertValues(assertions, "Partner Associate (" + partnerAssociateUuid + ") - Phone: Extension", null, null);
        }

        assertions = AssertionUtilityFunctions.assertValues(assertions, "Partner Associate (" + partnerAssociateUuid + ") - Phone: Country Code", expectedPhone.getCountryCode(), actualPhone.getCountryCode());

        return assertions;
    }

}
