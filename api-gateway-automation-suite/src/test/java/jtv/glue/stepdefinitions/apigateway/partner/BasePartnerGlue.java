package jtv.glue.stepdefinitions.apigateway.partner;

import jtv.context.apigateway.ApiGatewayPartnerTestContext;
import jtv.context.apigateway.ApiGatewayTestContext;
import jtv.glue.BaseGlue;

public class BasePartnerGlue extends BaseGlue {

    private ApiGatewayPartnerTestContext partnerContext;

    public BasePartnerGlue(ApiGatewayTestContext context, ApiGatewayPartnerTestContext partnerTestContext) {
        super(context);
        this.partnerContext = partnerTestContext;
    }

    public ApiGatewayPartnerTestContext getPartnerContext() {
        return this.partnerContext;
    }

    protected void setCoreUserRole(String coreUserRole) {

    }


}
