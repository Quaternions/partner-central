package jtv.api.gateway.partner.entity.request.create;

import jtv.api.gateway.partner.entity.request.Phone;
import jtv.entity.BaseEntity;

public class CreatePrimaryPartnerAssociate extends BaseEntity {
    private String firstName;
    private String lastName;
    private String title;
    private Phone phone;
    private String email;

    public CreatePrimaryPartnerAssociate(String firstName, String lastName, String title, Phone phone, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.phone = phone;
        this.email = email;
    }

    public CreatePrimaryPartnerAssociate() {
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public Phone getPhone() {
        return phone;
    }
    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

}
