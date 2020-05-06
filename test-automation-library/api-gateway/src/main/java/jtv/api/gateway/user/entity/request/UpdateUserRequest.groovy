package jtv.api.gateway.user.entity.request

import jtv.entity.BaseEntity

class UpdateUserRequest extends BaseEntity {
    UpdateUserRequest(UpdateUser updateUserAccount) {
        this.updateUser = updateUserAccount
    }

    UpdateUserRequest() {}

    UpdateUser updateUser

    UpdateUserRequest withFirstName(String firstname) {
        if(this.updateUser) {
            this.updateUser = this.updateUser.withFirstName(firstname)
        } else {
            this.updateUser = new UpdateUser().withFirstName(firstname)
        }
        return this
    }

    UpdateUserRequest withLastName(String lastname) {
        if(this.updateUser) {
            this.updateUser = this.updateUser.withLastName(lastname)
        } else {
            this.updateUser = new UpdateUser().withLastName(lastname)
        }
        return this
    }

    UpdateUserRequest withUserName(String username) {
        if(this.updateUser) {
            this.updateUser = this.updateUser.withUserName(username)
        } else {
            this.updateUser = new UpdateUser().withUserName(username)
        }
        return this
    }

    static UpdateUserRequest unmarshal(String jsonString) {
        return unmarshal(jsonString, UpdateUserRequest.class)
    }
}