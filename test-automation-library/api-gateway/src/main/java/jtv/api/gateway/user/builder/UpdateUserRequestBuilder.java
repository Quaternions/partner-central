package jtv.api.gateway.user.builder;

import jtv.api.gateway.user.entity.request.UpdateUser;
import jtv.api.gateway.user.entity.request.UpdateUserRequest;
import jtv.keycloak.utility.KeycloakUtilityFunctions;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class UpdateUserRequestBuilder {
    private static final Logger log = LoggerFactory.getLogger(UpdateUserRequestBuilder.class);
    private static UpdateUserRequest requestBody = new UpdateUserRequest();
    private static final KeycloakUtilityFunctions keycloakUtilityFunctions = new KeycloakUtilityFunctions();

    public static UpdateUserRequest buildUpdateUserWithData(String firstname, String lastname, String username) {
        requestBody = new UpdateUserRequest(
                new UpdateUser(username
                        , firstname
                        , lastname
                )
        );
        return requestBody;
    }

    public static UpdateUserRequest buildUpdateUserRequest(String accessToken, String parameter, String value) throws IOException {
        if (parameter == null && value == null) {
            requestBody = buildBaseRequest(accessToken);
        } else {
            switch (parameter.toLowerCase()) {
                case "firstname" : {
                    requestBody = buildBaseRequest(accessToken).withFirstName(value);
                    break;
                }
                case "lastname" : {
                    requestBody = buildBaseRequest(accessToken).withLastName(value);
                    break;
                }
                case "username" : {
                    requestBody = buildBaseRequest(accessToken).withUserName(value);
                    break;
                }
                case "not null" : {
                    break; // currently don't do anything
                }
                default : {
                    throw new IllegalStateException("[updateUserByKeyword] Unexpected keyword: " + parameter.toLowerCase());
                }
            }
        }
        return requestBody;
    }

    private static UpdateUserRequest buildBaseRequest(String accessToken) throws IOException {
        return new UpdateUserRequest(
                new UpdateUser(keycloakUtilityFunctions.getPreferredUsernameFromToken(accessToken)
                        , keycloakUtilityFunctions.getGivenNameFromToken(accessToken)
                        , keycloakUtilityFunctions.getFamilyNameFromToken(accessToken)
                )
        );
    }
}
