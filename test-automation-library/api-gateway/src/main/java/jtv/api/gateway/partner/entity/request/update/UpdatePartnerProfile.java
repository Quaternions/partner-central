package jtv.api.gateway.partner.entity.request.update;

import jtv.entity.BaseEntity;
import java.util.List;

public class UpdatePartnerProfile extends BaseEntity {
    UpdatePrimaryPartnerAssociate primaryContact;
    List<UpdatePartnerSite> partnerSites;
    String legalEntityName;
    String operatingName;
    String partnerAccountStatus;

    public UpdatePartnerProfile() {}

    public UpdatePartnerProfile(UpdatePrimaryPartnerAssociate primaryContact, List<UpdatePartnerSite> partnerSites, String legalEntityName, String operatingName, String partnerAccountStatus) {
        setPrimaryContact(primaryContact);
        setPartnerSites(partnerSites);
        setLegalEntityName(legalEntityName);
        setOperatingName(operatingName);
        setPartnerAccountStatus(partnerAccountStatus);
    }

    public UpdatePrimaryPartnerAssociate getPrimaryContact() { return primaryContact; }
    public void setPrimaryContact(UpdatePrimaryPartnerAssociate primaryContact) { this.primaryContact = primaryContact; }

    public List<UpdatePartnerSite> getPartnerSites() { return partnerSites; }
    public void setPartnerSites(List<UpdatePartnerSite> partnerSites) { this.partnerSites = partnerSites; }
    public void addPartnerSite(UpdatePartnerSite partnerSite) {
        partnerSites.add(partnerSite);
    }

    public String getLegalEntityName() { return legalEntityName; }
    public void setLegalEntityName(String legalEntityName) { this.legalEntityName = legalEntityName; }

    public String getOperatingName() { return operatingName; }
    public void setOperatingName(String operatingName) { this.operatingName = operatingName; }

    public String getPartnerAccountStatus() { return partnerAccountStatus; }
    public void setPartnerAccountStatus(String partnerAccountStatus) { this.partnerAccountStatus = partnerAccountStatus; }
}
