package jtv.api.gateway.user.entity.request

import jtv.entity.BaseEntity

class UpdateUser extends BaseEntity{
    UpdateUser(String userName, String firstName, String lastName) {
        this.userName = userName
        this.firstName = firstName
        this.lastName = lastName
    }

    UpdateUser() {}

    String userName
    String firstName
    String lastName

    String getUserName() {
        return this.userName
    }

    String getFirstName() {
        return this.firstName
    }

    String getLastName() {
        return this.lastName
    }

    UpdateUser withUserName(String username) {
        this.userName = username
        return this
    }

    UpdateUser withFirstName(String firstname) {
        this.firstName = firstname
        return this
    }

    UpdateUser withLastName(String lastname) {
        this.lastName = lastname
        return this
    }

}
