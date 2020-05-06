package jtv.glue.apigateway.user;

import jtv.context.apigateway.ApiGatewayTestContext;
import jtv.context.apigateway.user.UserAccountTestContext;
import jtv.glue.BaseGlue;

public class BaseUserAccountGlue extends BaseGlue {

    private UserAccountTestContext userAccountContext;

    public BaseUserAccountGlue(ApiGatewayTestContext context, UserAccountTestContext userAccountContext) {
        super(context);
        this.userAccountContext = userAccountContext;
    }

    protected UserAccountTestContext getUserAccountContext() {
        return this.userAccountContext;
    }

}
