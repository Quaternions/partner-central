package jtv.api.gateway.partner.associate.builder;

import jtv.api.gateway.partner.associate.entity.request.create.CreatePartnerAssociate;
import jtv.api.gateway.partner.associate.entity.request.create.CreatePartnerAssociateRequest;
import jtv.api.gateway.partner.entity.request.Phone;
import jtv.data.generator.DataGenerator;
import jtv.exception.KeywordNotDefinedException;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatePartnerAssociateRequestBuilder {
private static final Logger log = LoggerFactory.getLogger(CreatePartnerAssociateRequestBuilder.class);

    public static CreatePartnerAssociateRequest buildRequest() {
        return buildBaseRequest();
    }

    private static CreatePartnerAssociateRequest buildBaseRequest() {
        CreatePartnerAssociateRequest request;

        String first = DataGenerator.NameGenerator.generateFirstName();
        String last = DataGenerator.NameGenerator.generateLastName();

        request = new CreatePartnerAssociateRequest(
                new CreatePartnerAssociate(
                        first,
                        last,
                        DataGenerator.NameGenerator.generateEmailAddressGivenName(first, last),
                        new Phone(
                                DataGenerator.PhoneGenerator.generateCountryCode(),
                                DataGenerator.PhoneGenerator.generatePhoneNumber(),
                                DataGenerator.PhoneGenerator.generatePhoneExtension()
                        ),
                        DataGenerator.NameGenerator.generateTitle(),
                        "ASSOCIATE"
                )
        );
        return request;
    }

    public static void parameterValidation(String parameter, String keyword, CreatePartnerAssociateRequest request) throws KeywordNotDefinedException{
        if (keyword == null || keyword.isEmpty()) {
            throw new KeywordNotDefinedException(keyword, "Keyword is either empty or null!");
        }
        switch (parameter) {
            case "firstName" : {
                switch (keyword.toLowerCase()) {
                    case "is null": {
                        request.getCreatePartnerAssociate().setFirstName(null);
                        break;
                    }
                    case "is empty": {
                        request.getCreatePartnerAssociate().setFirstName("");
                        break;
                    }
                    case "is at maximum allowed character limit": {
                        request.getCreatePartnerAssociate().setFirstName(RandomStringUtils.randomAlphabetic(50));
                        break;
                    }
                    case "exceeds maximum allowed character limit": {
                        request.getCreatePartnerAssociate().setFirstName(RandomStringUtils.randomAlphabetic(51));
                        break;
                    }
                    default: {
                        throw new KeywordNotDefinedException(keyword, "Keyword is not defined for the firstName field");
                    }
                }
            break;
            }
            case "lastName" : {
                switch (keyword.toLowerCase()) {
                    case "is null": {
                        request.getCreatePartnerAssociate().setLastName(null);
                        break;
                    }
                    case "is empty": {
                        request.getCreatePartnerAssociate().setLastName("");
                        break;
                    }
                    case "is at maximum allowed character limit": {
                        request.getCreatePartnerAssociate().setLastName(RandomStringUtils.randomAlphabetic(50));
                        break;
                    }
                    case "exceeds maximum allowed character limit": {
                        request.getCreatePartnerAssociate().setLastName(RandomStringUtils.randomAlphabetic(51));
                        break;
                    }
                    default: {
                        throw new KeywordNotDefinedException(keyword, "Keyword is not defined for the lastName field");
                    }
                }
                break;
            }
            case "title" : {
                switch (keyword.toLowerCase()) {
                    case "is null": {
                        request.getCreatePartnerAssociate().setTitle(null);
                        break;
                    }
                    case "is empty": {
                        request.getCreatePartnerAssociate().setTitle("");
                        break;
                    }
                    case "is at maximum allowed character limit": {
                        request.getCreatePartnerAssociate().setTitle(RandomStringUtils.randomAlphabetic(50));
                        break;
                    }
                    case "exceeds maximum allowed character limit": {
                        request.getCreatePartnerAssociate().setTitle(RandomStringUtils.randomAlphabetic(51));
                        break;
                    }
                    default: {
                        throw new KeywordNotDefinedException(keyword, "Keyword is not defined for the title field");
                    }
                }
                break;
            }
            case "email" : {
                switch (keyword.toLowerCase()) {
                    case "is null": {
                        request.getCreatePartnerAssociate().setEmail(null);
                        break;
                    }
                    case "is empty": {
                        request.getCreatePartnerAssociate().setEmail("");
                        break;
                    }
                    case "is at maximum allowed character limit": {
                        request.getCreatePartnerAssociate().setEmail(RandomStringUtils.randomAlphabetic(35) + "@email.com"); // contains 45 characters
                        break;
                    }
                    case "exceeds maximum allowed character limit": {
                        request.getCreatePartnerAssociate().setEmail(RandomStringUtils.randomAlphabetic(36) + "@email.com"); // contains 46 characters
                        break;
                    }
                    case "contains minimum requirements for an email": {
                        request.getCreatePartnerAssociate().setEmail(RandomStringUtils.randomAlphabetic(1) + "@" + RandomStringUtils.randomAlphabetic(1)); // in the form 'a@b'
                        break;
                    }
                    case "is not a valid email address": {
                        request.getCreatePartnerAssociate().setEmail(RandomStringUtils.randomAlphabetic(45));
                        break;
                    }
                    default: {
                        throw new KeywordNotDefinedException(keyword, "Keyword is not defined for the email field");
                    }
                }
                break;
            }
            case "phone.subscriberNumber" : {
                switch (keyword.toLowerCase()) {
                    case "is null": {
                        request.getCreatePartnerAssociate().getPhone().setSubscriberNumber(null);
                        break;
                    }
                    case "is empty": {
                        request.getCreatePartnerAssociate().getPhone().setSubscriberNumber("");
                        break;
                    }
                    case "is at maximum allowed character limit": {
                        request.getCreatePartnerAssociate().getPhone().setSubscriberNumber(RandomStringUtils.randomNumeric(12));
                        break;
                    }
                    case "exceeds maximum allowed character limit": {
                        request.getCreatePartnerAssociate().getPhone().setSubscriberNumber(RandomStringUtils.randomNumeric(13));
                        break;
                    }
                    case "contains non-numeric characters": {
                        request.getCreatePartnerAssociate().getPhone().setSubscriberNumber(RandomStringUtils.randomAlphabetic(12));
                        break;
                    }
                    default: {
                        throw new KeywordNotDefinedException(keyword, "Keyword is not defined for the subscriberNumber field");
                    }
                }
                break;
            }
            case "phone.countryCode" : {
                switch (keyword.toLowerCase()) {
                    case "is null": {
                        request.getCreatePartnerAssociate().getPhone().setCountryCode(null);
                        break;
                    }
                    case "is empty": {
                        request.getCreatePartnerAssociate().getPhone().setCountryCode("");
                        break;
                    }
                    case "is at maximum allowed character limit": {
                        request.getCreatePartnerAssociate().getPhone().setCountryCode(RandomStringUtils.randomNumeric(3));
                        break;
                    }
                    case "exceeds maximum allowed character limit": {
                        request.getCreatePartnerAssociate().getPhone().setCountryCode(RandomStringUtils.randomNumeric(4));
                        break;
                    }
                    case "contains non-numeric characters": {
                        request.getCreatePartnerAssociate().getPhone().setCountryCode(RandomStringUtils.randomAlphabetic(3));
                        break;
                    }
                    default: {
                        throw new KeywordNotDefinedException(keyword, "Keyword is not defined for the countryCode field");
                    }
                }
                break;
            }
            case "phone.extension" : {
                switch (keyword.toLowerCase()) {
                    case "is null": {
                        request.getCreatePartnerAssociate().getPhone().setExtension(null);
                        break;
                    }
                    case "is empty": {
                        request.getCreatePartnerAssociate().getPhone().setExtension("");
                        break;
                    }
                    case "is at maximum allowed character limit": {
                        request.getCreatePartnerAssociate().getPhone().setExtension(RandomStringUtils.randomNumeric(5));
                        break;
                    }
                    case "exceeds maximum allowed character limit": {
                        request.getCreatePartnerAssociate().getPhone().setExtension(RandomStringUtils.randomNumeric(6));
                        break;
                    }
                    case "contains non-numeric characters": {
                        request.getCreatePartnerAssociate().getPhone().setExtension(RandomStringUtils.randomAlphabetic(5));
                        break;
                    }
                    default: {
                        throw new KeywordNotDefinedException(keyword, "Keyword is not defined for the extension field");
                    }
                }
                break;
            }
            case "associateType" : {
                switch (keyword.toLowerCase()) {
                    case "is empty" : {
                        request.getCreatePartnerAssociate().setPartnerAssociateType("");
                        break;
                    }
                    case "is null" : {
                        request.getCreatePartnerAssociate().setPartnerAssociateType(null);
                        break;
                    }
                    case "is not valid" : {
                        request.getCreatePartnerAssociate().setPartnerAssociateType("Not a valid associate type");
                        break;
                    }
                    default: {
                        throw new KeywordNotDefinedException(keyword, "Keyword is not defined for the partnerAssociateType field");
                    }
                }
                break;
            }
        }
    }
}

