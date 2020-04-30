package jtv.builder.partner.entity;

import jtv.builder.entity.KeycloakUser;

public class PartnerCentralUser extends KeycloakUser {

    public PartnerCentralUser() {}
    
    public PartnerCentralUser(String emailAddress, String password) {
        super(emailAddress, password);
    }
}
