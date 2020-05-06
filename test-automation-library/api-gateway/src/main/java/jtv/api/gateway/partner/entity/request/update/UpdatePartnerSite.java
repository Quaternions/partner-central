package jtv.api.gateway.partner.entity.request.update;

import jtv.api.gateway.partner.entity.request.SiteAddress;
import jtv.entity.BaseEntity;
import java.util.List;

public class UpdatePartnerSite extends BaseEntity {
    private String siteName;
    private List<String> partnerSiteUsageTypes;
    private String siteId; // partner site UUID
    private SiteAddress address;

    public UpdatePartnerSite() {}

    public UpdatePartnerSite(String siteName, List<String> partnerSiteUsageTypes, String siteId, SiteAddress address) {
        this.siteName = siteName;
        this.partnerSiteUsageTypes = partnerSiteUsageTypes;
        this.siteId = siteId;
        this.address = address;
    }

    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) { this.siteName = siteName; }

    public List<String> getPartnerSiteUsageTypes() { return partnerSiteUsageTypes; }
    public void setPartnerSiteUsageTypes(List<String> partnerSiteUsageTypes) { this.partnerSiteUsageTypes = partnerSiteUsageTypes; }
    public void addSiteUsage(String usage) {
        partnerSiteUsageTypes.add(usage);
    }

    public String getSiteId() {
        return siteId;
    }
    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public SiteAddress getAddress() {
        return address;
    }
    public void setAddress(SiteAddress address) {
        this.address = address;
    }
}
