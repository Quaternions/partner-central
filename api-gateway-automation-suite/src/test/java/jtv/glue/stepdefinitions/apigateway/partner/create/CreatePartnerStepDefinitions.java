package jtv.glue.stepdefinitions.apigateway.partner.create;

import com.jtv.test.db.entity.entitlement.DbUserAccount;
import com.jtv.test.db.fixtures.entitlement.DbUserAccountDataBuilder;
import com.jtv.test.db.fixtures.partner.DbPartnerInviteDataBuilder;
import com.jtv.test.db.query.entitlement.DbUserAccountQueryBuilder;
import com.jtv.test.db.query.partner.DbPartnerInviteStatusQueryBuilder;
import cucumber.api.java.en.*;
import jtv.api.gateway.partner.asserter.PartnerProfileAsserter;
import jtv.api.gateway.partner.builder.CreatePartnerProfileRequestBuilder;
import jtv.api.gateway.partner.entity.request.create.CreatePartnerProfileRequest;
import jtv.api.gateway.partner.utils.PartnerInvitationUtils;
import jtv.context.apigateway.ApiGatewayPartnerTestContext;
import jtv.context.apigateway.ApiGatewayTestContext;
import jtv.dao.user.UserAccountDao;
import jtv.data.generator.DataGenerator;
import jtv.exception.KeywordNotDefinedException;
import jtv.glue.stepdefinitions.apigateway.partner.BasePartnerGlue;
import jtv.keycloak.utility.KeycloakUtilityFunctions;
import jtv.parameters.ParameterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public class CreatePartnerStepDefinitions extends BasePartnerGlue {
    private static final Logger log = LoggerFactory.getLogger(CreatePartnerStepDefinitions.class);
    private KeycloakUtilityFunctions keycloakUtilityFunctions = new KeycloakUtilityFunctions();

    private static UserAccountDao userAccountDao = new UserAccountDao();
    private CreatePartnerProfileRequest createPartnerRequestBody = null;
    private PartnerInvitationUtils partnerInvitationUtils = new PartnerInvitationUtils();
    private DbUserAccount inviter;

    private String partnerInviteToken = null;
    private String legalEntity = DataGenerator.NameGenerator.generateCompanyName();
    private String inviterFirstname = DataGenerator.NameGenerator.generateFirstName();
    private String inviterLastname = DataGenerator.NameGenerator.generateLastName();
    private String inviterEmail = DataGenerator.NameGenerator.generateEmailAddressGivenName(inviterFirstname, inviterLastname);
    private String inviterJtvUuid = UUID.randomUUID().toString();

    public CreatePartnerStepDefinitions(ApiGatewayTestContext apiGatewayTestContext, ApiGatewayPartnerTestContext apiGatewayPartnerTestContext) throws IOException {
        super(apiGatewayTestContext, apiGatewayPartnerTestContext);
        buildRequest();
    }

    @When("^a request is made to create a partner$")
    public void createPartner() throws IOException {
        if (createPartnerRequestBody == null || createPartnerRequestBody.toString().isEmpty()) {
            buildRequest();
            makeRequest();
        } else {
            makeRequest();
        }
    }

    //<editor-fold desc="already created steps">
    @Then("^create partner request values should be persisted correctly in the database$")
    public void assertCreatePartnerRequest() throws IOException {
        logAndAssert(PartnerProfileAsserter.assertCreatePartnerRequest(createPartnerRequestBody.toString(), getApiGatewayResponse().getResponseBody()));
    }

    @Given("^a legal name that (.*)$")
    public void setLegalNameKeyword(String keyword) throws KeywordNotDefinedException, IOException {
        if (createPartnerRequestBody == null) {
            buildRequest();
        }

        createPartnerRequestBody.getCreatePartnerProfile().setLegalEntityName(
            ParameterUtils.generateParameterFromKeyword(
                "legalEntityName"
                , keyword
                , createPartnerRequestBody.getCreatePartnerProfile().getLegalEntityName()
                , 100
                , "create partner legal name"
            )
        );
    }

    @Given("^the operating name that (.*)$")
    public void setOperatingNameKeyword(String keyword) throws KeywordNotDefinedException, IOException {
        if (createPartnerRequestBody == null) {
            buildRequest();
        }
        createPartnerRequestBody.getCreatePartnerProfile().setOperatingName(
            ParameterUtils.generateParameterFromKeyword(
                "operatingName"
                , keyword
                , createPartnerRequestBody.getCreatePartnerProfile().getOperatingName()
                , 100
                , "create partner operating name"
            )
        );
    }

    @Given("^a primary contact firstname that (.*)$")
    public void setPrimaryContactFirstnameKeyword(String keyword) throws KeywordNotDefinedException, IOException {
        if (createPartnerRequestBody == null) {
            buildRequest();
        }
        createPartnerRequestBody.getCreatePartnerProfile().getPrimaryContact().setFirstName(
            ParameterUtils.generateParameterFromKeyword(
                "firstName"
                , keyword
                , createPartnerRequestBody.getCreatePartnerProfile().getPrimaryContact().getFirstName()
                , 50
                , "create primary contact firstname"
            )
        );
    }

    @Given("^a primary contact lastname that (.*)$")
    public void setPrimaryContactLastnameKeyword(String keyword) throws KeywordNotDefinedException, IOException {
        if (createPartnerRequestBody == null) {
            buildRequest();
        }
        createPartnerRequestBody.getCreatePartnerProfile().getPrimaryContact().setLastName(
            ParameterUtils.generateParameterFromKeyword(
                "lastName"
                , keyword
                , createPartnerRequestBody.getCreatePartnerProfile().getPrimaryContact().getLastName()
                , 50
                , "create primary contact lastname"
            )
        );
    }

    @Given("^a primary contact title that (.*)$")
    public void setPrimaryContactTitleKeyword(String keyword) throws KeywordNotDefinedException, IOException {
        if (createPartnerRequestBody == null) {
            buildRequest();
        }
        createPartnerRequestBody.getCreatePartnerProfile().getPrimaryContact().setTitle(
            ParameterUtils.generateParameterFromKeyword(
                "title"
                , keyword
                , createPartnerRequestBody.getCreatePartnerProfile().getPrimaryContact().getTitle()
                , 50
                , "create primary contact title"
            )
        );
    }

    @Given("^a primary contact subscriber number that (.*)$")
    public void setPrimaryContactSubscriberKeyword(String keyword) throws KeywordNotDefinedException, IOException {
        if (createPartnerRequestBody == null) {
            buildRequest();
        }
        createPartnerRequestBody.getCreatePartnerProfile().getPrimaryContact().getPhone().setSubscriberNumber(
            ParameterUtils.generateParameterFromKeyword(
                "phone"
                , keyword
                , createPartnerRequestBody.getCreatePartnerProfile().getPrimaryContact().getPhone().getSubscriberNumber()
                , 12
                , "create primary contact phone"
            )
        );
    }

    @Given("^a primary contact phone extension that (.*)$")
    public void setPrimaryContactPhoneExtKeyword(String keyword) throws KeywordNotDefinedException, IOException {
        if (createPartnerRequestBody == null) {
            buildRequest();
        }
        createPartnerRequestBody.getCreatePartnerProfile().getPrimaryContact().getPhone().setExtension(
            ParameterUtils.generateParameterFromKeyword(
                "phoneExt"
                , keyword
                , createPartnerRequestBody.getCreatePartnerProfile().getPrimaryContact().getPhone().getExtension()
                , 5
                , "create primary contact phone extension"
            )
        );
    }

    @Given("^a primary contact phone country code that (.*)$")
    public void setPrimaryContactPhoneCountryCodeKeyword(String keyword) throws KeywordNotDefinedException, IOException {
        if (createPartnerRequestBody == null) {
            buildRequest();
        }
        createPartnerRequestBody.getCreatePartnerProfile().getPrimaryContact().getPhone().setCountryCode(
                ParameterUtils.generateParameterFromKeyword(
                        "countryCode"
                        , keyword
                        , createPartnerRequestBody.getCreatePartnerProfile().getPrimaryContact().getPhone().getCountryCode().toString()
                        , 3
                        , "create primary contact phone country code"
                )
        );
    }

    @Given("^a primary contact email that (.*)$")
    public void setPrimaryContactEmailKeyword(String keyword) throws KeywordNotDefinedException, IOException {
        if (createPartnerRequestBody == null) {
            buildRequest();
        }
        createPartnerRequestBody.getCreatePartnerProfile().getPrimaryContact().setEmail(
            ParameterUtils.generateParameterFromKeyword(
                "email"
                , keyword
                , createPartnerRequestBody.getCreatePartnerProfile().getPrimaryContact().getEmail()
                , 45
                , "create primary contact email"
            )
        );
    }

    @Given("^a partner site name that (.*)$")
    public void setPartnerSiteNameKeyword(String keyword) throws KeywordNotDefinedException, IOException {
        if (createPartnerRequestBody == null) {
            buildRequest();
        }
        createPartnerRequestBody.getCreatePartnerProfile().getPartnerSites().get(0).setSiteName(
            ParameterUtils.generateParameterFromKeyword(
                "siteName"
                , keyword
                , createPartnerRequestBody.getCreatePartnerProfile().getPartnerSites().get(0).getSiteName()
                , 50
                , "create site name"
            )
        );
    }

    @Given("^a partner address line one that (.*)$")
    public void setPartnerAddressLineOneKeyword(String keyword) throws KeywordNotDefinedException, IOException {
        if (createPartnerRequestBody == null) {
            buildRequest();
        }
        createPartnerRequestBody.getCreatePartnerProfile().getPartnerSites().get(0).getAddress().setAddressLineOne(
            ParameterUtils.generateParameterFromKeyword(
                "addressLineOne"
                , keyword
                , createPartnerRequestBody.getCreatePartnerProfile().getPartnerSites().get(0).getAddress().getAddressLineOne()
                , 240
                , "create partner address line one"
            )
        );
    }

    @Given("^a partner address line two that (.*)$")
    public void setPartnerAddressLineTwoKeyword(String keyword) throws KeywordNotDefinedException, IOException {
        if (createPartnerRequestBody == null) {
            buildRequest();
        }
        createPartnerRequestBody.getCreatePartnerProfile().getPartnerSites().get(0).getAddress().setAddressLineTwo(
                ParameterUtils.generateParameterFromKeyword(
                        "addressLineTwo"
                        , keyword
                        , createPartnerRequestBody.getCreatePartnerProfile().getPartnerSites().get(0).getAddress().getAddressLineTwo()
                        , 240
                        , "create partner address line two"
                )
        );
    }

    @Given("^a partner account status of (.*)$")
    public void setPartnerAccountStatus(String status) {
        createPartnerRequestBody.getCreatePartnerProfile().setPartnerAccountStatus(status);
    }

    private void makeRequest() {
        setApiGatewayResponse(getApiGatewayClient().partner().createPartner(createPartnerRequestBody.toString(), getAccessToken(), getContentType()));
        logAndReportRequest();
    }

    private void buildRequest() throws IOException {
        if (partnerInviteToken == null) {
            log.info("[CreatePartnerStepDefinitions.buildRequest] invite token is null, generating a new invite token");
            generateInviteToken();
        }
        this.createPartnerRequestBody = CreatePartnerProfileRequestBuilder.buildRequest(partnerInviteToken, getAccessToken());
    }
    //</editor-fold>

    public void generateInviteToken() {
        if ( DbUserAccountQueryBuilder.defaultInstance(userAccountDao.getJtvJdbcTemplate())
                .withFirstName(inviterFirstname)
                .withLastName(inviterLastname)
                .withUserName(inviterEmail)
                .withUuid(inviterJtvUuid)
                .queryForCount() == 0 ) {
            DbUserAccountDataBuilder.defaultInstance(userAccountDao.getJtvJdbcTemplate())
                    .withFirstName(inviterFirstname)
                    .withLastName(inviterLastname)
                    .withUserName(inviterEmail)
                    .withUuid(inviterJtvUuid)
                    .build();
        }

        inviter = DbUserAccountQueryBuilder.defaultInstance(userAccountDao.getJtvJdbcTemplate()).withUuid(inviterJtvUuid).queryForObject();
        partnerInviteToken = partnerInvitationUtils.generatePartnerInviteToken(legalEntity,inviter.getUuid());
    }

    @And("^the request contains (.*) email address as the partner invite$")
    public void theRequestContainsKeywordEmail(String keyword) throws KeywordNotDefinedException, IOException {
        if (keyword == null) {
            throw new KeywordNotDefinedException(null, "Keyword cannot be null");
        }

        if (createPartnerRequestBody == null) {
            buildRequest();
        }
        switch (keyword.toLowerCase()) {
            case "the same" : {
                createPartnerRequestBody.getCreatePartnerProfile().getPrimaryContact().setEmail(keycloakUtilityFunctions.getPreferredUsernameFromToken(getAccessToken()));
                break;
            }
            case "a different" : {
                createPartnerRequestBody.getCreatePartnerProfile().getPrimaryContact().setEmail(DataGenerator.NameGenerator.generateEmailAddress());
                break;
            }
            default: {
                throw new KeywordNotDefinedException(keyword,"Invite Token Email Address Keyword");
            }
        }
    }

    @And("^the request contains (.*) legal entity name as the partner invite$")
    public void theRequestContainsKeywordLegalName(String keyword) throws KeywordNotDefinedException, IOException {
        if (keyword == null) {
            throw new KeywordNotDefinedException(null, "Keyword cannot be null");
        }

        if (createPartnerRequestBody == null) {
            buildRequest();
        }
        switch (keyword.toLowerCase()) {
            case "the same" : {
                createPartnerRequestBody.getCreatePartnerProfile().setLegalEntityName(legalEntity);
                createPartnerRequestBody.getCreatePartnerProfile().setOperatingName(legalEntity + " " + UUID.randomUUID().toString().substring(0, 12));
                break;
            }
            case "a different" : {
                String legal = DataGenerator.NameGenerator.generateCompanyName();
                createPartnerRequestBody.getCreatePartnerProfile().setLegalEntityName(legal);
                createPartnerRequestBody.getCreatePartnerProfile().setOperatingName(legal + " " + UUID.randomUUID().toString().substring(0,12));
                break;
            }
            default: {
                throw new KeywordNotDefinedException(keyword,"Invite Token Entity Legal Name Keyword");
            }
        }
    }

    @And("^the partner invite token (.*)$")
    public void partnerInviteTokenKeyword(String keyword) throws KeywordNotDefinedException, IOException {
        if (keyword == null) {
            throw new KeywordNotDefinedException(null, "Keyword cannot be null");
        }

        Long partnerInviteId;

        switch (keyword.toLowerCase()) {
            case "is pending" : {
                log.info("[partnerInviteTokenKeyword] Adding invite token to DB with status \"PENDING\" (token = " + partnerInviteToken + ")");
                partnerInviteId = DbPartnerInviteDataBuilder.defaultInstance(userAccountDao.getJtvJdbcTemplate())
                        .withFirstName(keycloakUtilityFunctions.getGivenNameFromToken(getAccessToken()))
                        .withLastName(keycloakUtilityFunctions.getFamilyNameFromToken(getAccessToken()).replaceAll("'",""))
                        .withEmailAddress(keycloakUtilityFunctions.getPreferredUsernameFromToken(getAccessToken()).replaceAll("'",""))
                        .withLegalName(legalEntity.replaceAll("'",""))
                        .withPartnerInviteStatusId(DbPartnerInviteStatusQueryBuilder.defaultInstance(userAccountDao.getJtvJdbcTemplate()).withCode("PENDING").queryForObject().getPartnerInviteStatusId())
                        .withUuid(UUID.randomUUID().toString())
                        .withInvitedByUserAccountId(inviter.getUserAccountId())
                        .withInviteJwtToken(partnerInviteToken)
                        .build()
                        .getPartnerInviteId();

                log.info("[partnerInviteTokenKeyword] Invite Token Insertion Complete (partnerInviteId = " + partnerInviteId + ")");
                break;
            }
            case "has been accepted" : {
                log.info("[partnerInviteTokenKeyword] Adding invite token to DB with status \"ACCEPTED\" (token = " + partnerInviteToken + ")");
                partnerInviteId = DbPartnerInviteDataBuilder.defaultInstance(userAccountDao.getJtvJdbcTemplate())
                        .withFirstName(keycloakUtilityFunctions.getGivenNameFromToken(getAccessToken()))
                        .withLastName(keycloakUtilityFunctions.getFamilyNameFromToken(getAccessToken()))
                        .withEmailAddress(keycloakUtilityFunctions.getPreferredUsernameFromToken(getAccessToken()))
                        .withLegalName(legalEntity)
                        .withPartnerInviteStatusId(DbPartnerInviteStatusQueryBuilder.defaultInstance(userAccountDao.getJtvJdbcTemplate()).withCode("ACCEPTED").queryForObject().getPartnerInviteStatusId())
                        .withUuid(UUID.randomUUID().toString())
                        .withInvitedByUserAccountId(inviter.getUserAccountId())
                        .withInviteJwtToken(partnerInviteToken)
                        .build()
                        .getPartnerInviteId();

                log.info("[partnerInviteTokenKeyword] Invite Token Insertion Complete (partnerInviteId = " + partnerInviteId + ")");
                break;
            }
            case "has expired" : {
                //generate a token that has expired
                partnerInviteToken = partnerInvitationUtils.generateExpiredPartnerInviteToken(legalEntity,inviter.getUuid());

                log.info("[partnerInviteTokenKeyword] Adding invite token to DB with status \"EXPIRED\" (token = " + partnerInviteToken + ")");
                partnerInviteId = DbPartnerInviteDataBuilder.defaultInstance(userAccountDao.getJtvJdbcTemplate())
                        .withFirstName(keycloakUtilityFunctions.getGivenNameFromToken(getAccessToken()))
                        .withLastName(keycloakUtilityFunctions.getFamilyNameFromToken(getAccessToken()))
                        .withEmailAddress(keycloakUtilityFunctions.getPreferredUsernameFromToken(getAccessToken()))
                        .withLegalName(legalEntity)
                        .withPartnerInviteStatusId(DbPartnerInviteStatusQueryBuilder.defaultInstance(userAccountDao.getJtvJdbcTemplate()).withCode("EXPIRED").queryForObject().getPartnerInviteStatusId())
                        .withUuid(UUID.randomUUID().toString())
                        .withInvitedByUserAccountId(inviter.getUserAccountId())
                        .withInviteJwtToken(partnerInviteToken)
                        .build()
                        .getPartnerInviteId();

                log.info("[partnerInviteTokenKeyword] Invite Token Insertion Complete (partnerInviteId = " + partnerInviteId + ")");
                break;
            }
            case "has been cancelled" : {
                log.info("[partnerInviteTokenKeyword] Adding invite token to DB with status \"CANCELLED\" (token = " + partnerInviteToken + ")");
                partnerInviteId = DbPartnerInviteDataBuilder.defaultInstance(userAccountDao.getJtvJdbcTemplate())
                        .withFirstName(keycloakUtilityFunctions.getGivenNameFromToken(getAccessToken()))
                        .withLastName(keycloakUtilityFunctions.getFamilyNameFromToken(getAccessToken()))
                        .withEmailAddress(keycloakUtilityFunctions.getPreferredUsernameFromToken(getAccessToken()))
                        .withLegalName(legalEntity)
                        .withPartnerInviteStatusId(DbPartnerInviteStatusQueryBuilder.defaultInstance(userAccountDao.getJtvJdbcTemplate()).withCode("CANCELLED").queryForObject().getPartnerInviteStatusId())
                        .withUuid(UUID.randomUUID().toString())
                        .withInvitedByUserAccountId(inviter.getUserAccountId())
                        .withInviteJwtToken(partnerInviteToken)
                        .build()
                        .getPartnerInviteId();

                log.info("[partnerInviteTokenKeyword] Invite Token Insertion Complete (partnerInviteId = " + partnerInviteId + ")");
                break;
            }
            default: {
                throw new KeywordNotDefinedException(keyword,"Invite Token Keyword");
            }
        }
    }
}
