package jtv.api.gateway.partner.entity.request.update;

import jtv.entity.BaseEntity;

public class UpdatePartnerRequest extends BaseEntity {
    UpdatePartnerProfile updatePartner;

    public UpdatePartnerRequest() {}

    public UpdatePartnerRequest(UpdatePartnerProfile updatePartner) {
        this.updatePartner = updatePartner;
    }

    public UpdatePartnerProfile getUpdatePartner() { return updatePartner; }
    public void setUpdatePartner(UpdatePartnerProfile updatePartner) { this.updatePartner = updatePartner; }
}
