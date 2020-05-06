package jtv.glue.stepdefinitions.apigateway.user.get;

import cucumber.api.java.en.When;
import jtv.context.apigateway.ApiGatewayTestContext;
import jtv.context.apigateway.user.UserAccountTestContext;
import jtv.data.generator.DataGenerator;
import jtv.glue.apigateway.user.BaseUserAccountGlue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class GetUserAccountStepDefinitions extends BaseUserAccountGlue {
    private static final Logger log = LoggerFactory.getLogger(GetUserAccountStepDefinitions.class);

    private static String firstname = DataGenerator.NameGenerator.generateFirstName();
    private static String lastname = DataGenerator.NameGenerator.generateLastName();
    private static String username = DataGenerator.NameGenerator.generateEmailAddressGivenName(firstname,lastname);
    private static String uuid = UUID.randomUUID().toString();

    public GetUserAccountStepDefinitions(ApiGatewayTestContext context, UserAccountTestContext userAccountContext) {
        super(context, userAccountContext);
    }

    @When("^a request is made to get the user's enterprise account$")
    public void makeGetUserAccountRequest() {
        log.info("[makeGetUserAccountRequest] getContext.getAccessToken = " + getContext().getAccessToken());
        setApiGatewayResponse(getApiGatewayClient().user().getUserAccount(getContext().getAccessToken(), getContentType()));
        logAndReportRequest();
    }

}
