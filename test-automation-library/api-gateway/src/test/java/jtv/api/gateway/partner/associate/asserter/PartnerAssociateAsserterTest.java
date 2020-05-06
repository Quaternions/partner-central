package jtv.api.gateway.partner.associate.asserter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtv.test.db.entity.DbEmailAddress;
import com.jtv.test.db.entity.DbPhoneNumber;
import com.jtv.test.db.entity.partner.DbAssociate;
import com.jtv.test.db.entity.partner.DbPartner;
import com.jtv.test.db.fixtures.DbEmailAddressDataBuilder;
import com.jtv.test.db.fixtures.DbPhoneNumberDataBuilder;
import com.jtv.test.db.fixtures.partner.DbAssociateDataBuilder;
import com.jtv.test.db.fixtures.partner.DbContactPointDataBuilder;
import com.jtv.test.db.query.DbEmailAddressQueryBuilder;
import com.jtv.test.db.query.DbPhoneNumberQueryBuilder;
import com.jtv.test.db.query.partner.DbAssociateQueryBuilder;
import com.jtv.test.db.query.partner.DbContactPointQueryBuilder;
import com.jtv.test.db.query.partner.DbContactPointTypeQueryBuilder;
import jtv.api.gateway.partner.associate.entity.request.create.CreatePartnerAssociate;
import jtv.api.gateway.partner.associate.entity.request.create.CreatePartnerAssociateRequest;
import jtv.api.gateway.partner.entity.request.Phone;
import jtv.api.gateway.test.client.model.PartnerAssociateServiceCreatePartnerAssociateResponse;
import jtv.api.gateway.test.client.model.PartnerAssociateServiceGetPartnerAssociatesResponse;
import jtv.api.gateway.test.client.model.PartnerAssociateServicePartnerAssociate;
import jtv.api.gateway.test.client.model.PartnerAssociateServicePhone;
import jtv.assertion.Assertion;
import jtv.assertion.ObjectAssertion;
import jtv.builder.partner.PartnerBuilder;
import jtv.dao.entity.partner.PartnerStatus;
import jtv.dao.partner.PartnerDao;
import jtv.data.generator.DataGenerator;
import jtv.exception.KeywordNotDefinedException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;

public class PartnerAssociateAsserterTest {
    private String associateUuid = "cacac129-aa2e-4f63-bcb4-0a79eecf5854";
    private final PartnerDao partnerDao;
    private final PartnerBuilder partnerBuilder;

    private final PartnerAssociateAsserter partnerAssociateAsserter;

    PartnerAssociateAsserterTest(){
        partnerDao = new PartnerDao();
        partnerBuilder = new PartnerBuilder();
        partnerAssociateAsserter = new PartnerAssociateAsserter(partnerDao.getJtvJdbcTemplate());
    }

    @Test
    void testAssertAssociate() {
        DbAssociate expectAssociate = new DbAssociate();
        DbAssociate actualAssociate = new DbAssociate();

        actualAssociate.setUuid(associateUuid);
        actualAssociate.setFirstName("AssociateFirstName");
        actualAssociate.setLastName("AssociateLastName");
        actualAssociate.setTitle("Associate Title");
        actualAssociate.setAssociateTypeId(Long.valueOf("2"));

        expectAssociate.setUuid(associateUuid);
        expectAssociate.setFirstName("AssociateFirstName");
        expectAssociate.setLastName("AssociateLastName");
        expectAssociate.setTitle("Associate Title");
        expectAssociate.setAssociateTypeId(Long.valueOf("2"));

        Assertion assertion = partnerAssociateAsserter.assertAssociate(expectAssociate, actualAssociate, associateUuid);

        Assert.assertEquals("Partner Associate (" + associateUuid + ")", assertion.getAssertionHeader());
        Assert.assertEquals(5, assertion.getObjectAssertions().size());
        Assert.assertTrue(assertion.getIsEqual());
        Assert.assertTrue(assertion.getObjectAssertions().get(0).assertionPassed());
        Assert.assertEquals("Partner Associate (" + associateUuid + ") - First Name", ((ObjectAssertion) assertion.getObjectAssertions().get(0)).getAssertionMessage());
        Assert.assertTrue(assertion.getObjectAssertions().get(1).assertionPassed());
        Assert.assertEquals("Partner Associate (" + associateUuid + ") - Last Name", ((ObjectAssertion) assertion.getObjectAssertions().get(1)).getAssertionMessage());
        Assert.assertTrue(assertion.getObjectAssertions().get(2).assertionPassed());
        Assert.assertEquals("Partner Associate (" + associateUuid + ") - Title", ((ObjectAssertion) assertion.getObjectAssertions().get(2)).getAssertionMessage());
        Assert.assertTrue(assertion.getObjectAssertions().get(3).assertionPassed());
        Assert.assertEquals("Partner Associate (" + associateUuid + ") - UUID", ((ObjectAssertion) assertion.getObjectAssertions().get(3)).getAssertionMessage());
        Assert.assertTrue(assertion.getObjectAssertions().get(4).assertionPassed());
        Assert.assertEquals("Partner Associate (" + associateUuid + ") - Associate Type ID", ((ObjectAssertion) assertion.getObjectAssertions().get(4)).getAssertionMessage());
    }

    @Test
    void testAssertAssociatePhone() {
        DbPhoneNumber actualPhone = new DbPhoneNumber();
        DbPhoneNumber expectedPhone = new DbPhoneNumber();

        actualPhone.setSubscriberNumber("123456789012");
        actualPhone.setExtension("54321");
        actualPhone.setCountryCode(345);

        expectedPhone.setSubscriberNumber("123456789012");
        expectedPhone.setExtension("54321");
        expectedPhone.setCountryCode(345);
        Assertion assertion = partnerAssociateAsserter.assertAssociatePhone(expectedPhone, actualPhone, associateUuid);

        Assert.assertEquals("Partner Associate (" + associateUuid +") Phone", assertion.getAssertionHeader());
        Assert.assertEquals(3, assertion.getObjectAssertions().size());
        Assert.assertTrue(assertion.getIsEqual());
        Assert.assertTrue(assertion.getObjectAssertions().get(0).assertionPassed());
        Assert.assertEquals("Partner Associate (" + associateUuid + ") - Phone: Subscriber Number", ((ObjectAssertion) assertion.getObjectAssertions().get(0)).getAssertionMessage());
        Assert.assertTrue(assertion.getObjectAssertions().get(1).assertionPassed());
        Assert.assertEquals("Partner Associate (" + associateUuid + ") - Phone: Extension", ((ObjectAssertion) assertion.getObjectAssertions().get(1)).getAssertionMessage());
        Assert.assertTrue(assertion.getObjectAssertions().get(2).assertionPassed());
        Assert.assertEquals("Partner Associate (" + associateUuid + ") - Phone: Country Code", ((ObjectAssertion) assertion.getObjectAssertions().get(2)).getAssertionMessage());
    }

    @Test
    void testAssertAssociateEmail() {
        DbEmailAddress actualEmail = new DbEmailAddress();
        DbEmailAddress expectedEmail = new DbEmailAddress();

        actualEmail.setEmailAddress("email.address@email.com");

        expectedEmail.setEmailAddress("email.address@email.com");

        Assertion assertion = partnerAssociateAsserter.assertAssociateEmail(expectedEmail, actualEmail, associateUuid);

        Assert.assertEquals("Partner Associate (" + associateUuid +") Email Address", assertion.getAssertionHeader());
        Assert.assertEquals(1, assertion.getObjectAssertions().size());
        Assert.assertTrue(assertion.getIsEqual());
        Assert.assertTrue(assertion.getObjectAssertions().get(0).assertionPassed());
        Assert.assertEquals("Partner Associate (" + associateUuid + ") - Email Address", ((ObjectAssertion) assertion.getObjectAssertions().get(0)).getAssertionMessage());
    }

    @Test
    void testAssertGetPartnerAssociate() throws IOException, KeywordNotDefinedException {
        PartnerAssociateServiceGetPartnerAssociatesResponse response;

        List<String> partnerAssociateUuids = new ArrayList<>();
        // add two random UUIDs to the list
        partnerAssociateUuids.add(UUID.randomUUID().toString());
        partnerAssociateUuids.add(UUID.randomUUID().toString());

        // insert the records into the db so we can make a GET call
        int firstUuid = 0;
        for ( String currentUuid : partnerAssociateUuids ) {
            if (firstUuid == 0 ) {
                insertPartnerAssociateRecordsForGetPartnerAssociateList(currentUuid, 1L); // 1 = PRIMARY_ACCOUNT_MANAGER
            } else {
                insertPartnerAssociateRecordsForGetPartnerAssociateList(currentUuid, 2L); // 2 = ASSOCIATE
            }
            firstUuid++;
        }

        response = createGetPartnerAssociatesResponse(partnerAssociateUuids);
        ObjectMapper objectMapper = new ObjectMapper();
        String responseString = objectMapper.writeValueAsString(response);


        for (String currentUuid : partnerAssociateUuids ) {
            String prefix = "Partner Associate (" + currentUuid + ")";
            List<Assertion> assertions = new ArrayList<>(partnerAssociateAsserter.assertGetPartnerAssociate(responseString, currentUuid));

            System.out.println(assertions.toString());
            evaluateAssertions(assertions, prefix);
        }


    }

    @Test
    public void testAssertCreatePartnerAssociate() throws IOException {
        CreatePartnerAssociateRequest request;
        PartnerAssociateServiceCreatePartnerAssociateResponse response;
        String partnerAssociateUuid = UUID.randomUUID().toString();

        response = new PartnerAssociateServiceCreatePartnerAssociateResponse()
                .partnerAssociateId(partnerAssociateUuid);

        request = createRequestAndInsertRecords(partnerAssociateUuid);

        List<Assertion> assertions = partnerAssociateAsserter.assertCreatePartnerAssociate(request.toString(), new ObjectMapper().writeValueAsString(response));

        System.out.println(assertions.toString());
        String prefix = "Partner Associate (" + partnerAssociateUuid + ")";

        evaluateAssertions(assertions, prefix);
    }

    void evaluateAssertions(List<Assertion> assertions, String prefix) {
        Assert.assertEquals(3, assertions.size());

        Assert.assertEquals(prefix , assertions.get(0).getAssertionHeader());
        Assert.assertEquals(5, assertions.get(0).getObjectAssertions().size());
        Assert.assertTrue(assertions.get(0).getIsEqual());
        Assert.assertTrue(assertions.get(0).getObjectAssertions().get(0).assertionPassed());
        Assert.assertEquals(prefix + " - First Name", ((ObjectAssertion) assertions.get(0).getObjectAssertions().get(0)).getAssertionMessage());
        Assert.assertTrue(assertions.get(0).getObjectAssertions().get(1).assertionPassed());
        Assert.assertEquals(prefix + " - Last Name", ((ObjectAssertion) assertions.get(0).getObjectAssertions().get(1)).getAssertionMessage());
        Assert.assertTrue(assertions.get(0).getObjectAssertions().get(2).assertionPassed());
        Assert.assertEquals(prefix + " - Title", ((ObjectAssertion) assertions.get(0).getObjectAssertions().get(2)).getAssertionMessage());
        Assert.assertTrue(assertions.get(0).getObjectAssertions().get(3).assertionPassed());
        Assert.assertEquals(prefix + " - UUID", ((ObjectAssertion) assertions.get(0).getObjectAssertions().get(3)).getAssertionMessage());
        Assert.assertTrue(assertions.get(0).getObjectAssertions().get(4).assertionPassed());
        Assert.assertEquals(prefix + " - Associate Type ID", ((ObjectAssertion) assertions.get(0).getObjectAssertions().get(4)).getAssertionMessage());

        Assert.assertEquals(prefix + " Email Address", assertions.get(1).getAssertionHeader());
        Assert.assertEquals(1, assertions.get(1).getObjectAssertions().size());
        Assert.assertTrue(assertions.get(1).getIsEqual());
        Assert.assertTrue(assertions.get(1).getObjectAssertions().get(0).assertionPassed());
        Assert.assertEquals(prefix + " - Email Address", ((ObjectAssertion) assertions.get(1).getObjectAssertions().get(0)).getAssertionMessage());

        Assert.assertEquals(prefix + " Phone", assertions.get(2).getAssertionHeader());
        Assert.assertEquals(3, assertions.get(2).getObjectAssertions().size());
        Assert.assertTrue(assertions.get(2).getIsEqual());
        Assert.assertTrue(assertions.get(2).getObjectAssertions().get(0).assertionPassed());
        Assert.assertEquals(prefix + " - Phone: Subscriber Number", ((ObjectAssertion) assertions.get(2).getObjectAssertions().get(0)).getAssertionMessage());
        Assert.assertTrue(assertions.get(2).getObjectAssertions().get(1).assertionPassed());
        Assert.assertEquals(prefix + " - Phone: Extension", ((ObjectAssertion) assertions.get(2).getObjectAssertions().get(1)).getAssertionMessage());
        Assert.assertTrue(assertions.get(2).getObjectAssertions().get(2).assertionPassed());
        Assert.assertEquals(prefix + " - Phone: Country Code", ((ObjectAssertion) assertions.get(2).getObjectAssertions().get(2)).getAssertionMessage());

    }

    @Test(expectedExceptions = JsonParseException.class)
    public void testAssertCreatePartnerAssociateBadJson() throws IOException {
        partnerAssociateAsserter.assertCreatePartnerAssociate("fdf","{}");
    }

    @Test
    public void testAssertCreatePartnerAssociateNullTitleAndExtension() throws IOException {
        String partnerAssociateUuid = UUID.randomUUID().toString();

        CreatePartnerAssociateRequest request = new CreatePartnerAssociateRequest();
        CreatePartnerAssociate createPartnerAssociate = new CreatePartnerAssociate();

        Phone phone = new Phone();

        phone.setCountryCode("1");
        phone.setSubscriberNumber(DataGenerator.PhoneGenerator.generatePhoneNumber());
        phone.setExtension(null);
        createPartnerAssociate.setPhone(phone);
        createPartnerAssociate.setFirstName("first");
        createPartnerAssociate.setLastName("last");
        createPartnerAssociate.setTitle(null);
        createPartnerAssociate.setEmail(DataGenerator.NameGenerator.generateEmailAddress());
        createPartnerAssociate.setPartnerAssociateType("ASSOCIATE");
        request.setCreatePartnerAssociate(createPartnerAssociate);

        //Check if the phone number already exists before trying to insert
        List<DbPhoneNumber> phoneNumbers = DbPhoneNumberQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate()).withCountryCode(Integer.parseInt(phone.getCountryCode()))
                .withSubscriberNumber(phone.getSubscriberNumber()).withExtension(phone.getExtension()).queryForList();
        int tries = 0;
        while(phoneNumbers.size() > 0 && tries < 5){
            createPartnerAssociate.getPhone().setSubscriberNumber(DataGenerator.PhoneGenerator.generatePhoneNumber());
            phoneNumbers = DbPhoneNumberQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate()).withCountryCode(Integer.parseInt(phone.getCountryCode()))
                    .withSubscriberNumber(phone.getSubscriberNumber()).withExtension(phone.getExtension()).queryForList();
            tries++;
        }

        insertPartnerAssociateRecordsForCreatePartnerAssociateRequest(request, partnerAssociateUuid);

        PartnerAssociateServiceCreatePartnerAssociateResponse response = new PartnerAssociateServiceCreatePartnerAssociateResponse()
                .partnerAssociateId(partnerAssociateUuid);

        List<Assertion> assertions = partnerAssociateAsserter.assertCreatePartnerAssociate(request.toString(), new ObjectMapper().writeValueAsString(response));

        Assert.assertEquals(3, assertions.size());
        Assert.assertTrue(assertions.get(0).getIsEqual());
        Assert.assertTrue(assertions.get(1).getIsEqual());
        Assert.assertTrue(assertions.get(2).getIsEqual());
    }

    @Test
    public void testAssertCreatePartnerAssociateEmptyTitleAndExtension() throws IOException {
        String partnerAssociateUuid = UUID.randomUUID().toString();
        CreatePartnerAssociateRequest request = new CreatePartnerAssociateRequest();
        CreatePartnerAssociate createPartnerAssociate = new CreatePartnerAssociate();

        Phone phone = new Phone();

        phone.setCountryCode("1");
        phone.setSubscriberNumber(DataGenerator.PhoneGenerator.generatePhoneNumber());
        phone.setExtension("");
        createPartnerAssociate.setPhone(phone);
        createPartnerAssociate.setFirstName("first");
        createPartnerAssociate.setLastName("last");
        createPartnerAssociate.setTitle("");
        createPartnerAssociate.setEmail(DataGenerator.NameGenerator.generateEmailAddress());
        createPartnerAssociate.setPartnerAssociateType("ASSOCIATE");
        request.setCreatePartnerAssociate(createPartnerAssociate);

        //Check if the phone number already exists before trying to insert
        List<DbPhoneNumber> phoneNumbers = DbPhoneNumberQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                .withCountryCode(Integer.parseInt(phone.getCountryCode()))
                .withSubscriberNumber(phone.getSubscriberNumber())
                .withExtension(null)
                .queryForList();
        int tries = 0;
        while(phoneNumbers.size() > 0 && tries < 5){
            createPartnerAssociate.getPhone().setSubscriberNumber(DataGenerator.PhoneGenerator.generatePhoneNumber());
            phoneNumbers = DbPhoneNumberQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate()).withCountryCode(Integer.parseInt(phone.getCountryCode()))
                    .withSubscriberNumber(phone.getSubscriberNumber()).withExtension(null).queryForList();
            tries++;
        }

        insertPartnerAssociateRecordsForCreatePartnerAssociateRequest(request, partnerAssociateUuid);
        PartnerAssociateServiceCreatePartnerAssociateResponse response = new PartnerAssociateServiceCreatePartnerAssociateResponse()
                .partnerAssociateId(partnerAssociateUuid);

        request.getCreatePartnerAssociate().setTitle("");
        request.getCreatePartnerAssociate().getPhone().setExtension("");
        List<Assertion> assertions = partnerAssociateAsserter.assertCreatePartnerAssociate(request.toString(), new ObjectMapper().writeValueAsString(response));

        Assert.assertEquals(3, assertions.size());
        Assert.assertTrue(assertions.get(0).getIsEqual());
        Assert.assertTrue(assertions.get(1).getIsEqual());
        Assert.assertTrue(assertions.get(2).getIsEqual());
    }

    private CreatePartnerAssociateRequest createRequestAndInsertRecords(String partnerAssociateUuid) {
        CreatePartnerAssociateRequest request = createRequest();
        insertPartnerAssociateRecordsForCreatePartnerAssociateRequest(request, partnerAssociateUuid);
        return request;
    }

    private PartnerAssociateServiceGetPartnerAssociatesResponse createGetPartnerAssociatesResponse(List<String> partnerAssociateUuids) throws IOException {
        PartnerAssociateServiceGetPartnerAssociatesResponse response = new PartnerAssociateServiceGetPartnerAssociatesResponse();

        for ( String currentUuid : partnerAssociateUuids ) {
            PartnerAssociateServicePartnerAssociate currentAssociate = new PartnerAssociateServicePartnerAssociate();
            // get the associate from the database
            DbAssociate associate = DbAssociateQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                    .withUuid(currentUuid)
                    .queryForObject();

            // get the phone & email from the db
            Integer emailId = DbContactPointQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                    .withAssociateId(associate.getAssociateId())
                    .withContactPointTypeId(1L) // 1 = email, 2 = phone
                    .queryForObject()
                    .getEmailAddressId();

            DbEmailAddress email = DbEmailAddressQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                    .withEmailAddressId(emailId)
                    .queryForObject();

            currentAssociate.setEmail(email.getEmailAddress());

            Integer phoneId = DbContactPointQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                    .withAssociateId(associate.getAssociateId())
                    .withContactPointTypeId(2L) // 1 = email, 2 = phone
                    .queryForObject()
                    .getPhoneNumberId();

            DbPhoneNumber phone = DbPhoneNumberQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                    .withPhoneNumberId(phoneId)
                    .queryForObject();

            PartnerAssociateServicePhone responsePhone = new PartnerAssociateServicePhone();
            responsePhone.setSubscriberNumber(phone.getSubscriberNumber());
            responsePhone.setExtension(phone.getExtension());
            responsePhone.setCountryCode(phone.getCountryCode().toString());

            currentAssociate.setPhone(responsePhone);

            currentAssociate.setFirstName(associate.getFirstName());
            currentAssociate.setLastName(associate.getLastName());
            currentAssociate.setTitle(associate.getTitle());

            if (associate.getAssociateTypeId().equals(1L) ) {
                currentAssociate.setPartnerAssociateType(PartnerAssociateServicePartnerAssociate.PartnerAssociateTypeEnum.PRIMARY_ACCOUNT_MANAGER);
            } else {
                currentAssociate.setPartnerAssociateType(PartnerAssociateServicePartnerAssociate.PartnerAssociateTypeEnum.ASSOCIATE);
            }
            currentAssociate.setPartnerAssociateId(associate.getUuid());

            response.addPartnerAssociatesItem(currentAssociate);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String responseString = objectMapper.writeValueAsString(response);
        return response;
    }

    private CreatePartnerAssociateRequest createRequest() {
        CreatePartnerAssociateRequest request = new CreatePartnerAssociateRequest();
        CreatePartnerAssociate createPartnerAssociate = new CreatePartnerAssociate();

        Phone phone = new Phone();

        phone.setCountryCode("1");
        phone.setSubscriberNumber(DataGenerator.PhoneGenerator.generatePhoneNumber());
        phone.setExtension(DataGenerator.PhoneGenerator.generatePhoneExtension());
        createPartnerAssociate.setPhone(phone);
        createPartnerAssociate.setFirstName("first");
        createPartnerAssociate.setLastName("last");
        createPartnerAssociate.setTitle("mr");
        createPartnerAssociate.setEmail(DataGenerator.NameGenerator.generateEmailAddress());
        createPartnerAssociate.setPartnerAssociateType("ASSOCIATE");
        request.setCreatePartnerAssociate(createPartnerAssociate);

        return request;
    }

    private void insertPartnerAssociateRecordsForGetPartnerAssociateList(String partnerAssociateUuid, Long associateType) throws KeywordNotDefinedException {
        String first = DataGenerator.NameGenerator.generateFirstName();
        String last = DataGenerator.NameGenerator.generateLastName();

        DbPartner partner = partnerBuilder.buildPartner(PartnerStatus.REGISTRATION_COMPLETED.name());

        DbAssociate associate = DbAssociateDataBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                .withFirstName(first)
                .withLastName(last)
                .withTitle(DataGenerator.NameGenerator.generateTitle())
                .withAssociateTypeId(associateType) // 1 = PRIMARY_ACCOUNT_MANAGER; 2 = ASSOCIATE
                .withUuid(partnerAssociateUuid)
                .withPartnerId(partner.getPartnerId())
                .build();

        DbEmailAddress email = DbEmailAddressDataBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                .withEmailAddress(DataGenerator.NameGenerator.generateEmailAddressGivenName(first, last))
                .build();

        //insert the contact point for the email address
        DbContactPointDataBuilder.foreignKeyInstance(partnerDao.getJtvJdbcTemplate(), associate, DbContactPointTypeQueryBuilder.emailAddressInstance(partnerDao.getJtvJdbcTemplate()))
                .withEmailAddressId(email.getEmailAddressId())
                .build();

        DbPhoneNumber phone = DbPhoneNumberDataBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                .withCountryCode(Integer.decode(DataGenerator.PhoneGenerator.generateCountryCode()))
                .withSubscriberNumber(DataGenerator.PhoneGenerator.generatePhoneNumber())
                .withExtension(DataGenerator.PhoneGenerator.generatePhoneExtension())
                .build();

        DbContactPointDataBuilder.foreignKeyInstance(partnerDao.getJtvJdbcTemplate(), associate, DbContactPointTypeQueryBuilder.phoneNumberInstance(partnerDao.getJtvJdbcTemplate()))
                .withPhoneNumberId(phone.getPhoneNumberId())
                .build();
    }

    private void insertPartnerAssociateRecordsForCreatePartnerAssociateRequest(CreatePartnerAssociateRequest request, String partnerAssociateUuid) {
        DbAssociate associate = DbAssociateDataBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                .withFirstName(request.getCreatePartnerAssociate().getFirstName())
                .withLastName(request.getCreatePartnerAssociate().getLastName())
                .withTitle(request.getCreatePartnerAssociate().getTitle())
                .withUuid(partnerAssociateUuid)
                .withPartnerId(1L)
                .withAssociateTypeId(2L)
                .build();

        DbEmailAddress emailAddress = DbEmailAddressDataBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                .withEmailAddress(request.getCreatePartnerAssociate().getEmail())
                .build();

        // insert the contact point for the email address
        DbContactPointDataBuilder.foreignKeyInstance(partnerDao.getJtvJdbcTemplate(), associate, DbContactPointTypeQueryBuilder.emailAddressInstance(partnerDao.getJtvJdbcTemplate()))
                .withEmailAddressId(emailAddress.getEmailAddressId())
                .build();

        DbPhoneNumberDataBuilder dbPhoneNumberDataBuilder = DbPhoneNumberDataBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                .withCountryCode(Integer.parseInt(request.getCreatePartnerAssociate().getPhone().getCountryCode()))
                .withSubscriberNumber(request.getCreatePartnerAssociate().getPhone().getSubscriberNumber());

        if(request.getCreatePartnerAssociate().getPhone().getExtension() != null && !request.getCreatePartnerAssociate().getPhone().getExtension().isEmpty()){
            dbPhoneNumberDataBuilder.withExtension(request.getCreatePartnerAssociate().getPhone().getExtension());
        }

        DbPhoneNumber phoneNumber = dbPhoneNumberDataBuilder.build();

        // insert the contact point for the phone number
        DbContactPointDataBuilder.foreignKeyInstance(partnerDao.getJtvJdbcTemplate(), associate, DbContactPointTypeQueryBuilder.phoneNumberInstance(partnerDao.getJtvJdbcTemplate()))
                .withPhoneNumberId(phoneNumber.getPhoneNumberId())
                .build();
    }
}