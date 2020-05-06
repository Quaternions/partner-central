package jtv.api.gateway.partner.entity.request.create;

import jtv.entity.BaseEntity;

import java.util.List;

public class CreatePartnerProfile extends BaseEntity {
    public CreatePartnerProfile(String inviteToken, CreatePrimaryPartnerAssociate primaryContact, List<CreatePartnerSite> partnerSites, String partnerAccountStatus, String legalEntityName, String operatingName) {
        this.inviteToken = inviteToken;
        this.primaryContact = primaryContact;
        this.partnerSites = partnerSites;
        this.partnerAccountStatus = partnerAccountStatus;
        this.legalEntityName = legalEntityName;
        this.operatingName = operatingName;
    }

    public CreatePartnerProfile() {
    }

    public String getInviteToken() {
        return inviteToken;
    }
    public void setInviteToken(String inviteToken) {
        this.inviteToken = inviteToken;
    }

    public CreatePrimaryPartnerAssociate getPrimaryContact() {
        return primaryContact;
    }
    public void setPrimaryContact(CreatePrimaryPartnerAssociate primaryContact) {
        this.primaryContact = primaryContact;
    }

    public List<CreatePartnerSite> getPartnerSites() {
        return partnerSites;
    }
    public void setPartnerSites(List<CreatePartnerSite> partnerSites) {
        this.partnerSites = partnerSites;
    }

    public String getPartnerAccountStatus() {
        return partnerAccountStatus;
    }
    public void setPartnerAccountStatus(String partnerAccountStatus) {
        this.partnerAccountStatus = partnerAccountStatus;
    }

    public String getLegalEntityName() {
        return legalEntityName;
    }
    public void setLegalEntityName(String legalEntityName) {
        this.legalEntityName = legalEntityName;
    }

    public String getOperatingName() {
        return operatingName;
    }
    public void setOperatingName(String operatingName) {
        this.operatingName = operatingName;
    }

    private String inviteToken;
    private CreatePrimaryPartnerAssociate primaryContact;
    private List<CreatePartnerSite> partnerSites;
    private String partnerAccountStatus;
    private String legalEntityName;
    private String operatingName;
}
