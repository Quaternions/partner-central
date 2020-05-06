package jtv.glue.stepdefinitions.apigateway.user.update;

import com.jtv.test.db.entity.entitlement.DbUserAccount;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import jtv.api.gateway.user.asserter.UserAccountAsserter;
import jtv.api.gateway.user.builder.UpdateUserRequestBuilder;
import jtv.api.gateway.user.entity.request.UpdateUser;
import jtv.api.gateway.user.entity.request.UpdateUserRequest;
import jtv.context.apigateway.ApiGatewayTestContext;
import jtv.context.apigateway.user.UserAccountTestContext;
import jtv.dao.entity.user.DatabaseUserAccount;
import jtv.dao.user.UserAccountDao;
import jtv.data.generator.DataGenerator;
import jtv.exception.KeywordNotDefinedException;
import jtv.glue.apigateway.user.BaseUserAccountGlue;
import jtv.keycloak.utility.KeycloakUtilityFunctions;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public class UpdateUserAccountStepDefinitions extends BaseUserAccountGlue {
    private UpdateUserRequest updateUserRequestBody;
    private UpdateUser updatedUser;
    private static String parameter;
    private static String value;
    private static UserAccountDao userAccountDao = new UserAccountDao();
    DatabaseUserAccount databaseUserAccount = new DatabaseUserAccount();

    private final KeycloakUtilityFunctions keycloakUtilityFunctions = new KeycloakUtilityFunctions();

    private static final Logger log = LoggerFactory.getLogger(UpdateUserAccountStepDefinitions.class);

    public UpdateUserAccountStepDefinitions(ApiGatewayTestContext context, UserAccountTestContext userAccountContext) {
        super(context, userAccountContext);
    }

    @Before
    public void initializeLocalVariables() {
        updateUserRequestBody = new UpdateUserRequest();
        updatedUser = new UpdateUser();

        parameter = null;
        value = null;
    }

    @When("^a request is made to update the (.*) for the user account$")
    public void updateUserByKeyword(String keyword) throws IOException {
        buildBaseRequest();
        switch (keyword.toLowerCase()) {
            case "firstname": {
                parameter = keyword;
                value = DataGenerator.NameGenerator.generateFirstName();
                databaseUserAccount.setFirstName(value);
                updateUserRequestBody = UpdateUserRequestBuilder.buildUpdateUserRequest(getAccessToken(),parameter, value);
                setApiGatewayResponse(getApiGatewayClient().user().updateUserAccount(databaseUserAccount.getJtvUuid(), getAccessToken(), getContentType(), updateUserRequestBody.toString()));
                break;
            }
            case "lastname": {
                parameter = keyword;
                value = DataGenerator.NameGenerator.generateLastName();
                databaseUserAccount.setLastName(value);
                updateUserRequestBody = UpdateUserRequestBuilder.buildUpdateUserRequest(getAccessToken(),parameter, value);
                setApiGatewayResponse(getApiGatewayClient().user().updateUserAccount(databaseUserAccount.getJtvUuid(), getAccessToken(), getContentType(), updateUserRequestBody.toString()));
                break;
            }
            case "username": {
                parameter = keyword;
                value = DataGenerator.NameGenerator.generateEmailAddress();
                databaseUserAccount.setUserName(value);
                updateUserRequestBody = UpdateUserRequestBuilder.buildUpdateUserRequest(getAccessToken(),parameter, value);
                setApiGatewayResponse(getApiGatewayClient().user().updateUserAccount(databaseUserAccount.getJtvUuid(), getAccessToken(), getContentType(), updateUserRequestBody.toString()));
                break;
            }
            default: {
                throw new IllegalStateException("[updateUserByKeyword] Unexpected keyword: " + keyword.toLowerCase());
            }
        }
        logAndReportRequest();
    }

    @And("^the user needs to update their user account first name with (.*)$")
    public void updateUserFirstName(String keyword) throws KeywordNotDefinedException, IOException {
        DbUserAccount dbUserAccount;
        dbUserAccount = getPartnerContext().getDbUserAccount();

        if(keyword.isEmpty() || keyword == null) {
            throw new KeywordNotDefinedException(keyword,"Keyword for updating the first name");
        }

        parameter = "firstname";

        switch (keyword.toLowerCase()) {
            case "a null": {
                dbUserAccount.setFirstName(null);
                break;
            }
            case "an empty string": {
                databaseUserAccount.setFirstName("");
                break;
            }
            case "too many characters": {
                databaseUserAccount.setFirstName(RandomString.make(51));
                log.info("dbUserAccount.getFirstName = " + databaseUserAccount.getFirstName());
                break;
            }

        }
        updateUserRequestBody = UpdateUserRequestBuilder.buildUpdateUserRequest(getAccessToken(),parameter, databaseUserAccount.getFirstName());


        /*
            case "firstname": {
                parameter = keyword;
                value = DataGenerator.NameGenerator.generateFirstName();
                dbUserAccount.setFirstName(value);
                updateUserRequestBody = UpdateUserRequestBuilder.buildUpdateUserRequest(getAccessToken(),parameter, value);
                setApiGatewayResponse(getApiGatewayClient().user().updateUserAccount(dbUserAccount.getJtvUuid(), getAccessToken(), getContentType(), updateUserRequestBody.toString()));
                break;
            }

         */
    }

    @And("the updated user account information should be persistent in the database")
    public void assertUpdatedUserAccount() {
        logAndAssert(UserAccountAsserter.assertUpdateUserAccount(databaseUserAccount));
    }

    @When("^a request is made to update the enterprise user account$")
    public void sendUpdateUserAccountRequest() throws IOException {
        if (getAccessToken() == null) {
            // since we don't want to rebuild the request body, set the parameter & value variables to something other than null
            parameter = "not null";
            value = "not null";

            log.info("[updateEnterpriseUserAccount] access token is null, building request body with random data since it doesn't matter");
            updateUserRequestBody = UpdateUserRequestBuilder.buildUpdateUserWithData(
                    DataGenerator.NameGenerator.generateFirstName(),
                    DataGenerator.NameGenerator.generateLastName(),
                    DataGenerator.NameGenerator.generateEmailAddress()
            );
        }

        // if both the parameter & value are null, then I need to build a request
        if (parameter == null && value == null) {
            buildBaseRequest();
        }

        setApiGatewayResponse(getApiGatewayClient().user().updateUserAccount(databaseUserAccount.getJtvUuid(), getAccessToken(), getContentType(),updateUserRequestBody.toString()));
        logAndReportRequest();
    }

    @When("^a request is made to update the user account with (.*)$")
    public void updateUserAccountWithKeyword(String keyword) throws IOException {
        buildBaseRequest();
        switch (keyword.toLowerCase()) {
            case "null firstname": {
                parameter = "firstname";
                value = null;
                updateUserRequestBody = UpdateUserRequestBuilder.buildUpdateUserRequest(getAccessToken(),parameter, value);
                setApiGatewayResponse(getApiGatewayClient().user().updateUserAccount(databaseUserAccount.getJtvUuid(), getAccessToken(), getContentType(), updateUserRequestBody.toString()));
                break;
            }
            case "empty firstname": {
                parameter = "firstname";
                value = "";
                updateUserRequestBody = UpdateUserRequestBuilder.buildUpdateUserRequest(getAccessToken(),parameter, value);
                setApiGatewayResponse(getApiGatewayClient().user().updateUserAccount(databaseUserAccount.getJtvUuid(), getAccessToken(), getContentType(), updateUserRequestBody.toString()));
                break;
            }
            case "null lastname": {
                parameter = "lastname";
                value = null;
                updateUserRequestBody = UpdateUserRequestBuilder.buildUpdateUserRequest(getAccessToken(),parameter, value);
                setApiGatewayResponse(getApiGatewayClient().user().updateUserAccount(databaseUserAccount.getJtvUuid(), getAccessToken(), getContentType(), updateUserRequestBody.toString()));
                break;
            }
            case "empty lastname": {
                parameter = "lastname";
                value = "";
                updateUserRequestBody = UpdateUserRequestBuilder.buildUpdateUserRequest(getAccessToken(),parameter, value);
                setApiGatewayResponse(getApiGatewayClient().user().updateUserAccount(databaseUserAccount.getJtvUuid(), getAccessToken(), getContentType(), updateUserRequestBody.toString()));
                break;
            }
            case "null username": {
                parameter = "username";
                value = null;
                updateUserRequestBody = UpdateUserRequestBuilder.buildUpdateUserRequest(getAccessToken(),parameter, value);
                setApiGatewayResponse(getApiGatewayClient().user().updateUserAccount(databaseUserAccount.getJtvUuid(), getAccessToken(), getContentType(), updateUserRequestBody.toString()));
                break;
            }
            case "empty username": {
                parameter = "username";
                value = "";
                updateUserRequestBody = UpdateUserRequestBuilder.buildUpdateUserRequest(getAccessToken(),parameter, value);
                setApiGatewayResponse(getApiGatewayClient().user().updateUserAccount(databaseUserAccount.getJtvUuid(), getAccessToken(), getContentType(), updateUserRequestBody.toString()));
                break;
            }
        }
        logAndReportRequest();
    }

    @When("^a request is made to update the user with (.*) uuid in the path$")
    public void updateUserWithUnknownUuid(String uuidKeyword) throws KeywordNotDefinedException, IOException {
        if (uuidKeyword == null || uuidKeyword.isEmpty()) {
            throw new KeywordNotDefinedException(uuidKeyword, "uuidKeyword is either empty or null");
        }

        buildBaseRequest();
        switch (uuidKeyword.toLowerCase()) {
            case "a null" : {
                setApiGatewayResponse(getApiGatewayClient().user().updateUserAccount(null, getAccessToken(), getContentType(),updateUserRequestBody.toString()));
                break;
            }
            case "an empty" : {
                setApiGatewayResponse(getApiGatewayClient().user().updateUserAccount("", getAccessToken(), getContentType(),updateUserRequestBody.toString()));
                break;
            }
            case "an unknown" : {
                setApiGatewayResponse(getApiGatewayClient().user().updateUserAccount(UUID.randomUUID().toString(), getAccessToken(), getContentType(),updateUserRequestBody.toString()));
                break;
            }
            case "an invalid" : {
                setApiGatewayResponse(getApiGatewayClient().user().updateUserAccount("abcdf012-gggg-abcd-abcd-abcdef012345", getAccessToken(), getContentType(),updateUserRequestBody.toString()));
                break;
            }
        }
        logAndReportRequest();
    }

    private void buildBaseRequest() throws IOException {
        updateUserRequestBody = UpdateUserRequestBuilder.buildUpdateUserRequest(getAccessToken(), parameter, value);
        databaseUserAccount = userAccountDao.getUserAccountByKeycloakUuid(getKeycloakUuid());
    }

    @And("^an existing user account with the same first and last names$")
    public void anExistingUserAccountWithTheSameFirstAndLastNames() throws IOException {
        String firstname = keycloakUtilityFunctions.getFamilyNameFromToken(getAccessToken());
        String lastname = keycloakUtilityFunctions.getGivenNameFromToken(getAccessToken());
        String username = DataGenerator.NameGenerator.generateEmailAddress();
        String jtvUuid = UUID.randomUUID().toString();

        userAccountDao.createNewUserAccount(
                        firstname,
                        lastname,
                        username,
                        jtvUuid
                        );

        log.info("Creating existing user with the following data:");
        log.info("existing user (firstname): " + firstname);
        log.info("existing user (lastname): " + lastname);
        log.info("existing user (username): " + username);
        log.info("existing user (jtvUuid): " + jtvUuid);

        updateUserRequestBody = UpdateUserRequestBuilder.buildUpdateUserWithData(
                                                        keycloakUtilityFunctions.getFamilyNameFromToken(getAccessToken()),
                                                        keycloakUtilityFunctions.getGivenNameFromToken(getAccessToken()),
                                                        keycloakUtilityFunctions.getPreferredUsernameFromToken(getAccessToken())
                                                        );

        databaseUserAccount = userAccountDao.getUserAccountByKeycloakUuid(getKeycloakUuid());

        // set the parameter & value variables to something other than null so the request body isn't rebuilt in the updateEnterpriseUserAccount step definitions
        parameter = "not null";
        value = "not null";
    }
}
