package jtv.api.gateway.partner.associate.entity.request.create;

import jtv.api.gateway.partner.entity.request.Phone;
import jtv.entity.BaseEntity;

public class CreatePartnerAssociate extends BaseEntity {
    private String firstName;
    private String lastName;
    private String email;
    private Phone phone;
    private String title;
    private String partnerAssociateType;

    public CreatePartnerAssociate(String firstName, String lastName, String email, Phone phone, String title, String partnerAssociateType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.title = title;
        this.partnerAssociateType = partnerAssociateType;
    }

    public CreatePartnerAssociate() {}

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Phone getPhone() { return phone; }
    public void setPhone(Phone phone) { this.phone = phone; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPartnerAssociateType() { return partnerAssociateType; }
    public void setPartnerAssociateType(String partnerAssociateType) { this.partnerAssociateType = partnerAssociateType; }
}
