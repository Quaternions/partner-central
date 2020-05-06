package jtv.api.gateway.partner.entity.request.update;

import jtv.api.gateway.partner.entity.request.Phone;
import jtv.entity.BaseEntity;

public class UpdatePrimaryPartnerAssociate extends BaseEntity {
    String firstName;
    String lastName;
    String email;
    String title;
    Phone phone;
    String partnerAssociateId; //UUID of the Associate (Primary Account Manager)

    public UpdatePrimaryPartnerAssociate() {}

    public UpdatePrimaryPartnerAssociate(String firstName, String lastName, String email, String title, Phone phone, String partnerAssociateId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.title = title;
        this.phone = phone;
        this.partnerAssociateId = partnerAssociateId;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Phone getPhone() { return phone; }
    public void setPhone(Phone phone) { this.phone = phone; }

    public String getPartnerAssociateId() { return partnerAssociateId; }
    public void setPartnerAssociateId(String partnerAssociateId) { this.partnerAssociateId = partnerAssociateId; }
}
