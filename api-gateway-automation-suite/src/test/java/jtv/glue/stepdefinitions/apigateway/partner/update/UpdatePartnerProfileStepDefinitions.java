package jtv.glue.stepdefinitions.apigateway.partner.update;

import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import jtv.api.gateway.partner.asserter.PartnerProfileAsserter;
import jtv.api.gateway.partner.builder.UpdatePartnerRequestBuilder;
import jtv.api.gateway.partner.entity.request.update.UpdatePartnerRequest;
import jtv.api.gateway.partner.entity.request.update.UpdatePartnerSite;
import jtv.context.apigateway.ApiGatewayPartnerTestContext;
import jtv.context.apigateway.ApiGatewayTestContext;
import jtv.exception.KeywordNotDefinedException;
import jtv.glue.stepdefinitions.apigateway.partner.BasePartnerGlue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class UpdatePartnerProfileStepDefinitions extends BasePartnerGlue {
    private static final Logger log = LoggerFactory.getLogger(UpdatePartnerProfileStepDefinitions.class);
    private static UpdatePartnerRequest updatePartnerRequest = null;

    public UpdatePartnerProfileStepDefinitions(ApiGatewayTestContext context, ApiGatewayPartnerTestContext partnerContext) {
        super(context, partnerContext);
    }

    public void buildUpdatePartnerRequest() {
        updatePartnerRequest = UpdatePartnerRequestBuilder.buildRequest(getPartnerContext().getPartnerUuid(), getPartnerContext().getPamUuid(), getPartnerContext().getSiteList());
    }

    @And("^the (.*) has been updated for the current partner profile$")
    public void updateFieldForThePartner(String keyword) throws KeywordNotDefinedException {
        if (keyword == null || keyword.isEmpty()) {
            throw new KeywordNotDefinedException(keyword, "Keyword is either empty or null");
        }

//        getPartnerContext().getPartnerUuid()
    }

    @When("^a request is made to update the current partner profile$")
    public void sendUpdatePartnerProfileRequest() {
        if (updatePartnerRequest == null) {
            buildUpdatePartnerRequest();
        }
        makeRequest();
    }

    @And("^the database should reflect the data from the update partner profile request$")
    public void assertUpdatePartnerRequest() throws IOException {
        logAndAssert(PartnerProfileAsserter.assertUpdatePartnerRequest(getApiGatewayResponse().getRequestBody(), getPartnerContext().getPartnerUuid()));
    }

    private void makeRequest() {
        setApiGatewayResponse(getApiGatewayClient().partner(getPartnerContext().getPartnerUuid()).updatePartner(updatePartnerRequest.toString(), getAccessToken(), getContentType()));
        logAndReportRequest();

        updatePartnerRequest = null;
    }

    @And("^the (.*) is being updated for the partner profile$")
    public void updatePartnerProfileParameter(String updateParameter) throws KeywordNotDefinedException {
        if (updateParameter == null || updateParameter.isEmpty()) {
            throw new KeywordNotDefinedException(updateParameter, "updateParameter is not defined or is empty");
        }
        if (updatePartnerRequest == null) {
            buildUpdatePartnerRequest();
        }

        UpdatePartnerRequestBuilder.updatePartnerProfileParameter(updateParameter, updatePartnerRequest);
    }

    @And("^the (.*) is being updated for the primary contact of the partner profile$")
    public void updatePrimaryContactParameter(String updateParameter) throws KeywordNotDefinedException{
        if (updateParameter == null || updateParameter.isEmpty()) {
            throw new KeywordNotDefinedException(updateParameter, "updateParameter is not defined or is empty!");
        }

        // build the base request first
        buildUpdatePartnerRequest();
        UpdatePartnerRequestBuilder.updatePrimaryContactParameter(updateParameter, updatePartnerRequest);
    }

    @And("^the (.*) is being updated for each site of the partner profile$")
    public void updatePartnerSiteParameter(String updateParameter) throws KeywordNotDefinedException{
        if (updateParameter == null || updateParameter.isEmpty()) {
            throw new KeywordNotDefinedException(updateParameter, "updateParameter is not defined or is empty!");
        }

        // build the base request first
        buildUpdatePartnerRequest();
        for (UpdatePartnerSite site: updatePartnerRequest.getUpdatePartner().getPartnerSites()) {
            UpdatePartnerRequestBuilder.updatePartnerSiteParameter(updateParameter, site);
        }
    }

    @And("^the legal entity name (.*) for the partner profile$")
    public void legalEntityNameValidation(String keyword) throws KeywordNotDefinedException {
        if (keyword == null || keyword.isEmpty()) {
            throw new KeywordNotDefinedException(keyword, "Keyword is not defined or is empty!");
        }
        if (updatePartnerRequest == null){
            buildUpdatePartnerRequest();
        }
        UpdatePartnerRequestBuilder.legalEntityNameValidation(keyword, updatePartnerRequest);
    }

    @And("^the operating name (.*) for the partner profile$")
    public void operatingNameValidation(String keyword) throws KeywordNotDefinedException {
        if (keyword == null || keyword.isEmpty()) {
            throw new KeywordNotDefinedException(keyword, "Keyword is not defined or is empty!");
        }
        if (updatePartnerRequest == null){
            buildUpdatePartnerRequest();
        }
        UpdatePartnerRequestBuilder.operatingNameValidation(keyword, updatePartnerRequest);
    }

    @And("^the firstName (.*) for the partner profile primary contact$")
    public void firstNameValidation(String keyword) throws KeywordNotDefinedException {
        if (keyword == null || keyword.isEmpty()) {
            throw new KeywordNotDefinedException(keyword, "Keyword is not defined or is empty!");
        }
        if (updatePartnerRequest == null){
            buildUpdatePartnerRequest();
        }
        UpdatePartnerRequestBuilder.firstNameValidation(keyword, updatePartnerRequest);
    }

    @And("^the lastName (.*) for the partner profile primary contact$")
    public void lastNameValidation(String keyword) throws KeywordNotDefinedException {
        if (keyword == null || keyword.isEmpty()) {
            throw new KeywordNotDefinedException(keyword, "Keyword is not defined or is empty!");
        }
        if (updatePartnerRequest == null){
            buildUpdatePartnerRequest();
        }
        UpdatePartnerRequestBuilder.lastNameValidation(keyword, updatePartnerRequest);
    }

    @And("^the title (.*) for the partner profile primary contact$")
    public void titleValidation(String keyword) throws KeywordNotDefinedException {
        if (keyword == null || keyword.isEmpty()) {
            throw new KeywordNotDefinedException(keyword, "Keyword is not defined or is empty!");
        }
        if (updatePartnerRequest == null){
            buildUpdatePartnerRequest();
        }
        UpdatePartnerRequestBuilder.titleValidation(keyword, updatePartnerRequest);
    }

    @And("^the email (.*) for the partner profile primary contact$")
    public void emailValidation(String keyword) throws KeywordNotDefinedException {
        if (keyword == null || keyword.isEmpty()) {
            throw new KeywordNotDefinedException(keyword, "Keyword is not defined or is empty!");
        }
        if (updatePartnerRequest == null){
            buildUpdatePartnerRequest();
        }
        UpdatePartnerRequestBuilder.emailValidation(keyword, updatePartnerRequest);
    }

    @And("^the phone subscriber number (.*) for the partner profile primary contact$")
    public void phoneSubscriberNumberValidation(String keyword) throws KeywordNotDefinedException {
        if (keyword == null || keyword.isEmpty()) {
            throw new KeywordNotDefinedException(keyword, "Keyword is not defined or is empty!");
        }
        if (updatePartnerRequest == null){
            buildUpdatePartnerRequest();
        }
        UpdatePartnerRequestBuilder.phoneSubscriberNumberValidation(keyword, updatePartnerRequest);
    }

    @And("^the phone country code (.*) for the partner profile primary contact$")
    public void phoneCountryCodeValidation(String keyword) throws KeywordNotDefinedException {
        if (keyword == null || keyword.isEmpty()) {
            throw new KeywordNotDefinedException(keyword, "Keyword is not defined or is empty!");
        }
        if (updatePartnerRequest == null){
            buildUpdatePartnerRequest();
        }
        UpdatePartnerRequestBuilder.phoneCountryCodeValidation(keyword, updatePartnerRequest);
    }

    @And("^the phone extension (.*) for the partner profile primary contact$")
    public void phoneExtensionValidation(String keyword) throws KeywordNotDefinedException {
        if (keyword == null || keyword.isEmpty()) {
            throw new KeywordNotDefinedException(keyword, "Keyword is not defined or is empty!");
        }
        if (updatePartnerRequest == null){
            buildUpdatePartnerRequest();
        }
        UpdatePartnerRequestBuilder.phoneExtensionValidation(keyword, updatePartnerRequest);
    }

    @And("^the site name (.*) for the partner profile site$")
    public void siteNameValidation(String keyword) throws KeywordNotDefinedException {
        if (keyword == null || keyword.isEmpty()) {
            throw new KeywordNotDefinedException(keyword, "Keyword is not defined or is empty!");
        }
        if (updatePartnerRequest == null){
            buildUpdatePartnerRequest();
        }
        UpdatePartnerRequestBuilder.siteNameValidation(keyword, updatePartnerRequest);
    }

    @And("^the address line one (.*) for the partner profile site$")
    public void addressLineOneValidation(String keyword) throws KeywordNotDefinedException {
        if (keyword == null || keyword.isEmpty()) {
            throw new KeywordNotDefinedException(keyword, "Keyword is not defined or is empty!");
        }
        if (updatePartnerRequest == null){
            buildUpdatePartnerRequest();
        }
        UpdatePartnerRequestBuilder.addressLineOneValidation(keyword, updatePartnerRequest);
    }

    @And("^the address line two (.*) for the partner profile site$")
    public void addressLineTwoValidation(String keyword) throws KeywordNotDefinedException {
        if (keyword == null || keyword.isEmpty()) {
            throw new KeywordNotDefinedException(keyword, "Keyword is not defined or is empty!");
        }
        if (updatePartnerRequest == null){
            buildUpdatePartnerRequest();
        }
        UpdatePartnerRequestBuilder.addressLineTwoValidation(keyword, updatePartnerRequest);
    }

    @And("^the city (.*) for the partner profile site$")
    public void cityValidation(String keyword) throws KeywordNotDefinedException {
        if (keyword == null || keyword.isEmpty()) {
            throw new KeywordNotDefinedException(keyword, "Keyword is not defined or is empty!");
        }
        if (updatePartnerRequest == null){
            buildUpdatePartnerRequest();
        }
        UpdatePartnerRequestBuilder.cityValidation(keyword, updatePartnerRequest);
    }

    @And("^the state province (.*) for the partner profile site$")
    public void stateProvinceValidation(String keyword) throws KeywordNotDefinedException {
        if (keyword == null || keyword.isEmpty()) {
            throw new KeywordNotDefinedException(keyword, "Keyword is not defined or is empty!");
        }
        if (updatePartnerRequest == null){
            buildUpdatePartnerRequest();
        }
        UpdatePartnerRequestBuilder.stateProvinceValidation(keyword, updatePartnerRequest);
    }

}
