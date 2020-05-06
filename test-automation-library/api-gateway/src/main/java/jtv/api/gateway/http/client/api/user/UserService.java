package jtv.api.gateway.http.client.api.user;

import jtv.api.gateway.http.client.utils.ApiGatewayClientUtils;
import jtv.http.client.HttpResponse;
import static jtv.api.gateway.http.client.constants.ApiGatewayPathConstants.USER;

public class UserService {
    private final String host;
    private final String userIdentifier;
    private final String basePath;

    public UserService(String host) {
        this.userIdentifier = null;
        this.host = host;
        this.basePath = USER;
    }

    public UserService(String host, String userIdentifier) {
        this.userIdentifier = userIdentifier;
        this.host = host;
        this.basePath = USER + "/" + userIdentifier;
    }

    public HttpResponse createUserAccount(String accessToken, String contentType) {
        return ApiGatewayClientUtils.makePostRequest(host, "user/me/", contentType, accessToken, null);
    }

    public HttpResponse getUserAccountByJtvUuid(String jtvUuid, String accessToken, String contentType) {
        String path = "user/" + jtvUuid;
        return ApiGatewayClientUtils.makeGetRequest(host, path, contentType, accessToken);
    }

    public HttpResponse getUserAccount(String accessToken, String contentType) {
        return ApiGatewayClientUtils.makeGetRequest(host, "user/me/", contentType, accessToken);
    }

    public HttpResponse updateUserAccount(String jtvUuid, String accessToken, String contentType, String requestBody) {
        String path = "user/" + jtvUuid;
        return ApiGatewayClientUtils.makePostRequest(host, path, contentType, accessToken, requestBody);
    }
}
