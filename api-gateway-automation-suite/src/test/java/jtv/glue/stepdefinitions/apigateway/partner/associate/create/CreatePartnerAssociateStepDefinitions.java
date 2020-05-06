package jtv.glue.stepdefinitions.apigateway.partner.associate.create;

import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import jtv.api.gateway.partner.associate.asserter.PartnerAssociateAsserter;
import jtv.api.gateway.partner.associate.builder.CreatePartnerAssociateRequestBuilder;
import jtv.api.gateway.partner.associate.entity.request.create.CreatePartnerAssociateRequest;
import jtv.context.apigateway.ApiGatewayPartnerTestContext;
import jtv.context.apigateway.ApiGatewayTestContext;
import jtv.exception.KeywordNotDefinedException;
import jtv.glue.stepdefinitions.apigateway.partner.BasePartnerGlue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CreatePartnerAssociateStepDefinitions extends BasePartnerGlue {
    private static final Logger log = LoggerFactory.getLogger(CreatePartnerAssociateStepDefinitions.class);
    private CreatePartnerAssociateRequest createPartnerAssociateRequest = null;
    
    public CreatePartnerAssociateStepDefinitions(ApiGatewayTestContext context, ApiGatewayPartnerTestContext partnerTestContext) {
        super(context, partnerTestContext);
        buildBaseRequest();
    }
    
    private void buildBaseRequest() {
        this.createPartnerAssociateRequest = CreatePartnerAssociateRequestBuilder.buildRequest(); 
    }

    @When("^a request is made to create a partner associate$")
    public void sendCreatePartnerAssociateRequest() {
        if (createPartnerAssociateRequest == null) {
            buildBaseRequest();
            makeRequest();
        } else {
            makeRequest();
        }
    }

    private void makeRequest() {
        setApiGatewayResponse(
                getApiGatewayClient()
                        .partner(getPartnerContext().getPartnerUuid())
                        .associate()
                        .createAssociate(createPartnerAssociateRequest.toString(), getAccessToken(), getContentType())
        );
        logAndReportRequest();
    }

    @And("^the created associate should be persistent in the database$")
    public void assertCreatePartnerAssociate() throws IOException {
        PartnerAssociateAsserter partnerAssociateAsserter = new PartnerAssociateAsserter();
        logAndAssert(partnerAssociateAsserter.assertCreatePartnerAssociate(createPartnerAssociateRequest.toString(),getApiGatewayResponse().getResponseBody()));
    }

    @When("^a request is made to create a partner associate without partner UUID$")
    public void sendCreatePartnerWithoutPartnerUuid() {
        setApiGatewayResponse(getApiGatewayClient().partner(null).associate().createAssociate(createPartnerAssociateRequest.toString(), getAccessToken(), getContentType()));
        logAndReportRequest();
    }

    @And("^the first name (.*) in the create partner associate request body$")
    public void theFirstNameValidation(String keyword) throws KeywordNotDefinedException {
        CreatePartnerAssociateRequestBuilder.parameterValidation("firstName", keyword, createPartnerAssociateRequest);
    }

    @And("^the last name (.*) in the create partner associate request body$")
    public void theLastNameValidation(String keyword) throws KeywordNotDefinedException {
        CreatePartnerAssociateRequestBuilder.parameterValidation("lastName", keyword, createPartnerAssociateRequest);
    }

    @And("^the email (.*) in the create partner associate request body$")
    public void theEmailValidation(String keyword) throws KeywordNotDefinedException {
        CreatePartnerAssociateRequestBuilder.parameterValidation("email", keyword, createPartnerAssociateRequest);
    }

    @And("^the title (.*) in the create partner associate request body$")
    public void theTitleValidation(String keyword) throws KeywordNotDefinedException {
        CreatePartnerAssociateRequestBuilder.parameterValidation("title", keyword, createPartnerAssociateRequest);
    }

    @And("^the phone subscriber number (.*) in the create partner associate request body$")
    public void theSubscriberValidation(String keyword) throws KeywordNotDefinedException {
        CreatePartnerAssociateRequestBuilder.parameterValidation("phone.subscriberNumber", keyword, createPartnerAssociateRequest);
    }

    @And("^the phone country code (.*) in the create partner associate request body$")
    public void theCountryCodeValidation(String keyword) throws KeywordNotDefinedException {
        CreatePartnerAssociateRequestBuilder.parameterValidation("phone.countryCode", keyword, createPartnerAssociateRequest);
    }

    @And("^the phone extension (.*) in the create partner associate request body$")
    public void theExtensionValidation(String keyword) throws KeywordNotDefinedException {
        CreatePartnerAssociateRequestBuilder.parameterValidation("phone.extension", keyword, createPartnerAssociateRequest);
    }

    @And("^the partner associate type (.*) in the create partner associate request body$")
    public void theAssociateTypeValidation(String keyword) throws KeywordNotDefinedException {
        CreatePartnerAssociateRequestBuilder.parameterValidation("associate type", keyword, createPartnerAssociateRequest);
    }

    @When("^a request is made to create a second primary account manager$")
    public void createSecondPrimaryAccountManager() {
        if (createPartnerAssociateRequest == null) {
            buildBaseRequest();
            createPartnerAssociateRequest.getCreatePartnerAssociate().setPartnerAssociateType("PRIMARY_ACCOUNT_MANAGER");
            makeRequest();
        } else {
            createPartnerAssociateRequest.getCreatePartnerAssociate().setPartnerAssociateType("PRIMARY_ACCOUNT_MANAGER");
            makeRequest();
        }
    }
}
