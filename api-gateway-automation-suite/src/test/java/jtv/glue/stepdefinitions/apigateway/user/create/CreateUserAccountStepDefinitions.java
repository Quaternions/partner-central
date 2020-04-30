package jtv.glue.stepdefinitions.apigateway.user.create;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import jtv.api.gateway.user.asserter.UserAccountAsserter;
import jtv.context.apigateway.ApiGatewayTestContext;
import jtv.glue.BaseGlue;

public class CreateUserAccountStepDefinitions extends BaseGlue {
    public CreateUserAccountStepDefinitions(ApiGatewayTestContext apiGatewayTestContext) {
        super(apiGatewayTestContext);
    }

    @When("^a request is made to create an enterprise user account$")
    public void requestCreateUserAccount() {
        makeRequest();
    }

    @Then("^the Keycloak user information should be saved as a user account in the enterprise$")
    public void assertUserAccount() {
        logAndAssert(UserAccountAsserter.assertCreateUserAccount(getApiGatewayResponse().getResponseBody(), getAccessToken()));
    }

    private void makeRequest() {
        setApiGatewayResponse(getApiGatewayClient().user().createUserAccount(getAccessToken(), getContentType()));
        logAndReportRequest();
    }
}