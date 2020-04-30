package jtv.api.gateway.partner.associate.entity.request.create;

import jtv.entity.BaseEntity;

public class CreatePartnerAssociateRequest extends BaseEntity {
    CreatePartnerAssociate createPartnerAssociate;

    public CreatePartnerAssociateRequest() {}

    public CreatePartnerAssociateRequest(CreatePartnerAssociate createPartnerAssociate) {
        this.createPartnerAssociate = createPartnerAssociate;
    }

    public CreatePartnerAssociate getCreatePartnerAssociate() { return createPartnerAssociate; }
    public void setCreatePartnerAssociate(CreatePartnerAssociate createPartnerAssociate) { this.createPartnerAssociate = createPartnerAssociate; }

    public static CreatePartnerAssociateRequest unmarshal(String jsonString) {
        return BaseEntity.unmarshal(jsonString, CreatePartnerAssociateRequest.class);
    }

}
