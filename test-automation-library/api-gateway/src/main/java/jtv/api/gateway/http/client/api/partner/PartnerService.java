package jtv.api.gateway.http.client.api.partner;

import jtv.api.gateway.http.client.api.partner.associate.PartnerAssociateService;
import jtv.api.gateway.http.client.cde.partner.product.label.PartnerProductLabelService;
import jtv.api.gateway.http.client.utils.ApiGatewayClientUtils;
import jtv.http.client.HttpResponse;

import static jtv.api.gateway.http.client.constants.ApiGatewayPathConstants.PARTNER;

public class PartnerService {
    private final String basePath;
    private final String host;

    public PartnerService(String host) {
        this.host = host;
        this.basePath = PARTNER;
    }

    public PartnerService(String host, String partnerUuid) {
        this.host = host;
        this.basePath = PARTNER + "/" + partnerUuid;
    }

    public PartnerAssociateService associate() {
        return new PartnerAssociateService(this.host, this.basePath);
    }


    public PartnerProductLabelService productLabel() {
        return new PartnerProductLabelService(this.host, this.basePath);
    }

    public HttpResponse getPartner(String accessToken, String contentType) {
        return ApiGatewayClientUtils.makeGetRequest(host, basePath, contentType, accessToken);
    }

    public HttpResponse updatePartner(String requestBody, String accessToken, String contentType) {
        return ApiGatewayClientUtils.makePostRequest(host, basePath, contentType, accessToken, requestBody);
    }

    public HttpResponse createPartner(String requestBody, String accessToken, String contentType) {
        return ApiGatewayClientUtils.makePostRequest(host, basePath, contentType, accessToken, requestBody);
    }
}
