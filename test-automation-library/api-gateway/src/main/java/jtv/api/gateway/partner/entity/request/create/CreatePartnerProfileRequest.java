package jtv.api.gateway.partner.entity.request.create;

import jtv.entity.BaseEntity;

public class CreatePartnerProfileRequest extends BaseEntity {
    public CreatePartnerProfileRequest(CreatePartnerProfile createPartnerProfile) {
        this.createPartnerProfile = createPartnerProfile;
    }

    public CreatePartnerProfileRequest() {
    }

    public static CreatePartnerProfileRequest unmarshal(String jsonString) {
        return BaseEntity.unmarshal(jsonString, CreatePartnerProfileRequest.class);
    }

    public CreatePartnerProfile getCreatePartnerProfile() {
        return createPartnerProfile;
    }

    public void setCreatePartnerProfile(CreatePartnerProfile createPartnerProfile) {
        this.createPartnerProfile = createPartnerProfile;
    }

    private CreatePartnerProfile createPartnerProfile;
}
