package jtv.api.gateway.partner.entity.request.create;

import jtv.api.gateway.partner.entity.request.SiteAddress;
import jtv.entity.BaseEntity;

import java.util.List;

public class CreatePartnerSite extends BaseEntity {
    public CreatePartnerSite(String siteName, List<String> partnerSiteUsageTypes, SiteAddress address) {
        this.siteName = siteName;
        this.partnerSiteUsageTypes = partnerSiteUsageTypes;
        this.address = address;
    }

    public CreatePartnerSite() {
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public List<String> getPartnerSiteUsageTypes() {
        return partnerSiteUsageTypes;
    }

    public void setPartnerSiteUsageTypes(List<String> partnerSiteUsageTypes) {
        this.partnerSiteUsageTypes = partnerSiteUsageTypes;
    }

    public SiteAddress getAddress() {
        return address;
    }

    public void setAddress(SiteAddress address) {
        this.address = address;
    }

    private String siteName;
    private List<String> partnerSiteUsageTypes;
    private SiteAddress address;
}
