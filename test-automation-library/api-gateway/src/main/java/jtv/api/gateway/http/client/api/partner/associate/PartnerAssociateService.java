package jtv.api.gateway.http.client.api.partner.associate;

import jtv.api.gateway.http.client.utils.ApiGatewayClientUtils;
import jtv.http.client.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static jtv.api.gateway.http.client.constants.ApiGatewayPathConstants.PARTNER_ASSOCIATE;

public class PartnerAssociateService {
    private final String basePath;
    private final String host;
    private static final Logger log = LoggerFactory.getLogger(PartnerAssociateService.class);

    public PartnerAssociateService(String host, String parentPath) {
        this.host = host;
        this.basePath = parentPath + "/" + PARTNER_ASSOCIATE;
    }

    public HttpResponse createAssociate(String requestBody, String accessToken, String contentType) {
        return ApiGatewayClientUtils.makePostRequest(host, basePath, contentType, accessToken, requestBody);
    }

    public HttpResponse getAssociate(String accessToken, String contentType) {
        return ApiGatewayClientUtils.makeGetRequest(host, basePath, contentType, accessToken);
    }

    public HttpResponse getAssociate(String accessToken, String contentType, String filter) {
        String path = basePath + "?associate-filter="+filter;
        return ApiGatewayClientUtils.makeGetRequest(host, path, contentType, accessToken);
    }

}
