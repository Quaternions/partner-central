package jtv.api.gateway.partner.associate.builder;

import jtv.api.gateway.partner.associate.entity.request.create.CreatePartnerAssociateRequest;
import jtv.exception.KeywordNotDefinedException;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

class CreatePartnerAssociateRequestBuilderTest {

    @Test
    void testBuildRequest() {
        CreatePartnerAssociateRequest request = CreatePartnerAssociateRequestBuilder.buildRequest();

        Assert.assertNotNull(request);
        Assert.assertTrue(request.toString().contains("firstName"));
        Assert.assertNotNull(request.getCreatePartnerAssociate().getFirstName());
        Assert.assertTrue(request.toString().contains("lastName"));
        Assert.assertNotNull(request.getCreatePartnerAssociate().getLastName());
        Assert.assertTrue(request.toString().contains("firstName"));
        Assert.assertNotNull(request.getCreatePartnerAssociate().getFirstName());
        Assert.assertTrue(request.toString().contains("email"));
        Assert.assertNotNull(request.getCreatePartnerAssociate().getEmail());
        Assert.assertTrue(request.toString().contains("title"));
        Assert.assertNotNull(request.getCreatePartnerAssociate().getTitle());
        Assert.assertTrue(request.toString().contains("phone"));
        Assert.assertTrue(request.toString().contains("countryCode"));
        Assert.assertNotNull(request.getCreatePartnerAssociate().getPhone().getCountryCode());
        Assert.assertTrue(request.toString().contains("subscriberNumber"));
        Assert.assertNotNull(request.getCreatePartnerAssociate().getPhone().getSubscriberNumber());
        Assert.assertTrue(request.toString().contains("extension"));
        Assert.assertNotNull(request.getCreatePartnerAssociate().getPhone().getExtension());
        Assert.assertTrue(request.toString().contains("partnerAssociateType"));
        Assert.assertNotNull(request.getCreatePartnerAssociate().getPartnerAssociateType());
    }

    @Test
    void testFirstNameValidation() throws KeywordNotDefinedException {
        CreatePartnerAssociateRequest request = CreatePartnerAssociateRequestBuilder.buildRequest();
        Assert.assertNotNull(request,"request body object cannot be null");

        CreatePartnerAssociateRequestBuilder.parameterValidation("firstName", "is null", request);
        Assert.assertNull(request.getCreatePartnerAssociate().getFirstName(), "firstName field value is null");

        CreatePartnerAssociateRequestBuilder.parameterValidation("firstName", "is empty", request);
        Assert.assertTrue(request.getCreatePartnerAssociate().getFirstName().isEmpty(), "firstName field value is empty");

        CreatePartnerAssociateRequestBuilder.parameterValidation("firstName", "is at maximum allowed character limit", request);
        Assert.assertEquals(request.getCreatePartnerAssociate().getFirstName().length(), 50, "firstName field value contains 50 characters (maximum allowed)");

        CreatePartnerAssociateRequestBuilder.parameterValidation("firstName", "exceeds maximum allowed character limit", request);
        Assert.assertEquals(request.getCreatePartnerAssociate().getFirstName().length(), 51, "firstName field value contains 51 characters (exceeds maximum allowed)");
    }

    @Test
    void testLastNameValidation() throws KeywordNotDefinedException {
        CreatePartnerAssociateRequest request = CreatePartnerAssociateRequestBuilder.buildRequest();
        Assert.assertNotNull(request,"request body object cannot be null");

        CreatePartnerAssociateRequestBuilder.parameterValidation("lastName", "is null", request);
        Assert.assertNull(request.getCreatePartnerAssociate().getLastName(), "lastName field value is null");

        CreatePartnerAssociateRequestBuilder.parameterValidation("lastName", "is empty", request);
        Assert.assertTrue(request.getCreatePartnerAssociate().getLastName().isEmpty(), "lastName field value is empty");

        CreatePartnerAssociateRequestBuilder.parameterValidation("lastName", "is at maximum allowed character limit", request);
        Assert.assertEquals(request.getCreatePartnerAssociate().getLastName().length(), 50, "lastName field value contains 50 characters (maximum allowed)");

        CreatePartnerAssociateRequestBuilder.parameterValidation("lastName", "exceeds maximum allowed character limit", request);
        Assert.assertEquals(request.getCreatePartnerAssociate().getLastName().length(), 51, "lastName field value contains 51 characters (exceeds maximum allowed)");
    }

    @Test
    void testTitleValidation() throws KeywordNotDefinedException {
        CreatePartnerAssociateRequest request = CreatePartnerAssociateRequestBuilder.buildRequest();
        Assert.assertNotNull(request, "request body object cannot be null");

        CreatePartnerAssociateRequestBuilder.parameterValidation("title", "is null", request);
        Assert.assertNull(request.getCreatePartnerAssociate().getTitle(), "title field value is null");

        CreatePartnerAssociateRequestBuilder.parameterValidation("title", "is empty", request);
        Assert.assertTrue(request.getCreatePartnerAssociate().getTitle().isEmpty(), "title field value is empty");

        CreatePartnerAssociateRequestBuilder.parameterValidation("title", "is at maximum allowed character limit", request);
        Assert.assertEquals(request.getCreatePartnerAssociate().getTitle().length(), 50, "title field value contains 50 characters (maximum allowed)");

        CreatePartnerAssociateRequestBuilder.parameterValidation("title", "exceeds maximum allowed character limit", request);
        Assert.assertEquals(request.getCreatePartnerAssociate().getTitle().length(), 51, "title field value contains 51 characters (exceeds maximum allowed)");
    }

    @Test
    void testEmailValidation() throws KeywordNotDefinedException {
        CreatePartnerAssociateRequest request = CreatePartnerAssociateRequestBuilder.buildRequest();
        Assert.assertNotNull(request, "request body object cannot be null");

        CreatePartnerAssociateRequestBuilder.parameterValidation("email", "is null", request);
        Assert.assertNull(request.getCreatePartnerAssociate().getEmail(), "email field value is null");

        CreatePartnerAssociateRequestBuilder.parameterValidation("email", "is empty", request);
        Assert.assertTrue(request.getCreatePartnerAssociate().getEmail().isEmpty(), "email field value is empty");

        CreatePartnerAssociateRequestBuilder.parameterValidation("email", "is at maximum allowed character limit", request);
        Assert.assertEquals(request.getCreatePartnerAssociate().getEmail().length(), 45, "email field value contains 50 characters (maximum allowed)");

        CreatePartnerAssociateRequestBuilder.parameterValidation("email", "exceeds maximum allowed character limit", request);
        Assert.assertEquals(request.getCreatePartnerAssociate().getEmail().length(), 46, "email field value contains 51 characters (exceeds maximum allowed)");

        CreatePartnerAssociateRequestBuilder.parameterValidation("email", "contains minimum requirements for an email", request);
        Assert.assertEquals(request.getCreatePartnerAssociate().getEmail().length(), 3, "email field value contains 3 character (a@b form email)");

        CreatePartnerAssociateRequestBuilder.parameterValidation("email", "is not a valid email address", request);
        Assert.assertFalse(request.getCreatePartnerAssociate().getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}"), "email field value contains a non-valid email address"); // need some way to check that the string isn't in a valid email format.
    }

    @Test
    void testSubscriberNumberValidation() throws KeywordNotDefinedException {
        CreatePartnerAssociateRequest request = CreatePartnerAssociateRequestBuilder.buildRequest();
        Assert.assertNotNull(request, "request body object cannot be null");

        CreatePartnerAssociateRequestBuilder.parameterValidation("phone.subscriberNumber", "is null", request);
        Assert.assertNull(request.getCreatePartnerAssociate().getPhone().getSubscriberNumber(), "subscriberNumber field value is null");

        CreatePartnerAssociateRequestBuilder.parameterValidation("phone.subscriberNumber", "is empty", request);
        Assert.assertTrue(request.getCreatePartnerAssociate().getPhone().getSubscriberNumber().isEmpty(), "subscriberNumber field value is empty");

        CreatePartnerAssociateRequestBuilder.parameterValidation("phone.subscriberNumber", "is at maximum allowed character limit", request);
        Assert.assertEquals(request.getCreatePartnerAssociate().getPhone().getSubscriberNumber().length(), 12, "subscriberNumber field value contains 12 characters (maximum allowed)");

        CreatePartnerAssociateRequestBuilder.parameterValidation("phone.subscriberNumber", "exceeds maximum allowed character limit", request);
        Assert.assertEquals(request.getCreatePartnerAssociate().getPhone().getSubscriberNumber().length(), 13, "subscriberNumber field value contains 13 characters (exceeds maximum allowed)");

        CreatePartnerAssociateRequestBuilder.parameterValidation("phone.subscriberNumber", "contains non-numeric characters", request);
        Assert.assertFalse(request.getCreatePartnerAssociate().getPhone().getSubscriberNumber().matches("^[0-9]*$"), "subscriberNumber contains non-numeric characters");
    }

    @Test
    void testCountryCodeValidation() throws KeywordNotDefinedException {
        CreatePartnerAssociateRequest request = CreatePartnerAssociateRequestBuilder.buildRequest();
        Assert.assertNotNull(request, "request body object cannot be null");

        CreatePartnerAssociateRequestBuilder.parameterValidation("phone.countryCode", "is null", request);
        Assert.assertNull(request.getCreatePartnerAssociate().getPhone().getCountryCode(), "countryCode field value is null");

        CreatePartnerAssociateRequestBuilder.parameterValidation("phone.countryCode", "is empty", request);
        Assert.assertTrue(request.getCreatePartnerAssociate().getPhone().getCountryCode().isEmpty(), "countryCode field value is empty");

        CreatePartnerAssociateRequestBuilder.parameterValidation("phone.countryCode", "is at maximum allowed character limit", request);
        Assert.assertEquals(request.getCreatePartnerAssociate().getPhone().getCountryCode().length(), 3, "countryCode field value contains 3 characters (maximum allowed)");

        CreatePartnerAssociateRequestBuilder.parameterValidation("phone.countryCode", "exceeds maximum allowed character limit", request);
        Assert.assertEquals(request.getCreatePartnerAssociate().getPhone().getCountryCode().length(), 4, "countryCode field value contains 4 characters (exceeds maximum allowed)");

        CreatePartnerAssociateRequestBuilder.parameterValidation("phone.countryCode", "contains non-numeric characters", request);
        Assert.assertFalse(request.getCreatePartnerAssociate().getPhone().getCountryCode().matches("^[0-9]*$"), "countryCode contains non-numeric characters");
    }

    @Test
    void testExtensionValidation() throws KeywordNotDefinedException {
        CreatePartnerAssociateRequest request = CreatePartnerAssociateRequestBuilder.buildRequest();
        Assert.assertNotNull(request, "request body object cannot be null");

        CreatePartnerAssociateRequestBuilder.parameterValidation("phone.extension", "is null", request);
        Assert.assertNull(request.getCreatePartnerAssociate().getPhone().getExtension(), "extension field value is null");

        CreatePartnerAssociateRequestBuilder.parameterValidation("phone.extension", "is empty", request);
        Assert.assertTrue(request.getCreatePartnerAssociate().getPhone().getExtension().isEmpty(), "extension field value is empty");

        CreatePartnerAssociateRequestBuilder.parameterValidation("phone.extension", "is at maximum allowed character limit", request);
        Assert.assertEquals(request.getCreatePartnerAssociate().getPhone().getExtension().length(), 5, "extension field value contains 5 characters (maximum allowed)");

        CreatePartnerAssociateRequestBuilder.parameterValidation("phone.extension", "exceeds maximum allowed character limit", request);
        Assert.assertEquals(request.getCreatePartnerAssociate().getPhone().getExtension().length(), 6, "extension field value contains 6 characters (exceeds maximum allowed)");

        CreatePartnerAssociateRequestBuilder.parameterValidation("phone.extension", "contains non-numeric characters", request);
        Assert.assertFalse(request.getCreatePartnerAssociate().getPhone().getExtension().matches("^[0-9]*$"), "extension contains non-numeric characters");
    }

    @Test
    void testAssociateTypeValidation() throws KeywordNotDefinedException {
        CreatePartnerAssociateRequest request = CreatePartnerAssociateRequestBuilder.buildRequest();
        Assert.assertNotNull(request, "request body object cannot be null");

        CreatePartnerAssociateRequestBuilder.parameterValidation("associateType", "is null", request);
        Assert.assertNull(request.getCreatePartnerAssociate().getPartnerAssociateType(), "partnerAssociateType field value is null");

        CreatePartnerAssociateRequestBuilder.parameterValidation("associateType", "is empty", request);
        Assert.assertTrue(request.getCreatePartnerAssociate().getPartnerAssociateType().isEmpty(), "partnerAssociateType field value is empty");

        CreatePartnerAssociateRequestBuilder.parameterValidation("associateType", "is not valid", request);
        Assert.assertNotEquals(request.getCreatePartnerAssociate().getPartnerAssociateType(), "ASSOCIATE", "partnerAssociateType field is not ASSOCIATE");
    }
}
