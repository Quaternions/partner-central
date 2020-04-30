package jtv.glue.stepdefinitions.apigateway.user;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import jtv.api.gateway.user.asserter.UserAccountAsserter;
import jtv.context.apigateway.ApiGatewayTestContext;
import jtv.context.apigateway.user.UserAccountTestContext;
import jtv.dao.entity.user.DatabaseUserAccount;
import jtv.dao.user.UserAccountDao;
import jtv.data.generator.DataGenerator;
import jtv.exception.KeywordNotDefinedException;
import jtv.exception.NoKeywordProvidedException;
import jtv.glue.apigateway.user.BaseUserAccountGlue;
import jtv.keycloak.utility.KeycloakUtilityFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public class CommonUserAccountStepDefinitions extends BaseUserAccountGlue {
    private static final Logger log = LoggerFactory.getLogger(CommonUserAccountStepDefinitions.class);
    private static UserAccountDao userAccountDao = new UserAccountDao();
    private static String keycloakUuid;

    private DatabaseUserAccount userAccount = new DatabaseUserAccount();

    private final KeycloakUtilityFunctions keycloakUtilityFunctions = new KeycloakUtilityFunctions();

    public CommonUserAccountStepDefinitions(ApiGatewayTestContext context, UserAccountTestContext userAccountContext) {
        super(context, userAccountContext);
    }

    @Given("^a Keycloak user role of (.*?)$")
    public void setKeycloakUserRoleKeyword(String userRoleKeyword) {
        setKeycloakUserRole(userRoleKeyword);
    }

    @And("^a Keycloak user created$")
    public void aKeycloakUserCreated() throws IOException, NoKeywordProvidedException, KeywordNotDefinedException {
        createKeycloakUserAndSetAccessToken();
    }

    @And("^an enterprise user already created that (.*)$")
    public void createUserAccount(String keyword) throws KeywordNotDefinedException, IOException {
        String first, last, username;

        if (keyword == null) {
            throw new KeywordNotDefinedException(keyword, "Create User keyword is null");
        }

        switch (keyword.toLowerCase()) {
            case "belongs to the current keycloak user" : {
                first = keycloakUtilityFunctions.getGivenNameFromToken(getAccessToken());
                last = keycloakUtilityFunctions.getFamilyNameFromToken(getAccessToken());
                username = keycloakUtilityFunctions.getPreferredUsernameFromToken(getAccessToken());
                keycloakUuid = keycloakUtilityFunctions.getSubjectFromToken(getAccessToken());
                break;
            }
            case "does not belong to the current keycloak user" : {
                first = DataGenerator.NameGenerator.generateFirstName();
                last = DataGenerator.NameGenerator.generateLastName();
                username = DataGenerator.NameGenerator.generateEmailAddressGivenName(first, last);
                keycloakUuid = UUID.randomUUID().toString();
                break;
            }
            default:
                throw new KeywordNotDefinedException(keyword, "Create User Keyword is not defined");
        }

        userAccountDao.createNewUserAccount(
                first,
                last,
                username,
                keycloakUuid
        );

        userAccount = userAccountDao.getUserAccountByKeycloakUuid(keycloakUuid);
    }

    @When("^a request is made to get user account by JTV UUID with a uuid that is (.*)$")
    public void makeGetUserWithJtvUuidKeyword(String keyword) {
        log.info("\nkeyword: " + keyword);
        switch (keyword.toLowerCase()) {
            case "known": {
                setApiGatewayResponse(getApiGatewayClient().user().getUserAccountByJtvUuid(userAccount.getJtvUuid(), getContext().getAccessToken(), getContentType()));
                break;
            }
            case "null": {
                setApiGatewayResponse(getApiGatewayClient().user().getUserAccountByJtvUuid(null, getContext().getAccessToken(), getContentType()));
                break;
            }
            case "unknown": {
                setApiGatewayResponse(getApiGatewayClient().user().getUserAccountByJtvUuid(UUID.randomUUID().toString(), getContext().getAccessToken(), getContentType()));
                break;
            }
            case "invalid": {
                setApiGatewayResponse(getApiGatewayClient().user().getUserAccountByJtvUuid("abcdf012-gggg-abcd-abcd-abcdef012345", getContext().getAccessToken(), getContentType()));
                break;
            }
        }
        logAndReportRequest();
    }

    @When("a request is made to get user account by JTV UUID")
    public void getUserByJtvUuid() {
        setApiGatewayResponse(getApiGatewayClient().user().getUserAccountByJtvUuid(userAccount.getJtvUuid(), getContext().getAccessToken(), getContentType()));
        logAndReportRequest();
    }

    @Then("^the user account information returned should be equal to what is persisted in the enterprise$")
    public void assertGetUserAccountRequest() {
        logAndAssert(UserAccountAsserter.assertGetUserAccount(keycloakUuid, getApiGatewayResponse().getResponseBody()));
    }
}
