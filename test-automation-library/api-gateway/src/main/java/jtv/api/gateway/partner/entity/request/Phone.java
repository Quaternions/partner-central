package jtv.api.gateway.partner.entity.request;

import jtv.entity.BaseEntity;

public class Phone extends BaseEntity {
    private String countryCode;
    private String subscriberNumber;
    private String extension;

    public Phone() { }

    public Phone(String countryCode, String subscriberNumber, String extension) {
        this.countryCode = countryCode;
        this.subscriberNumber = subscriberNumber;
        this.extension = extension;
    }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getSubscriberNumber() { return subscriberNumber; }
    public void setSubscriberNumber(String subscriberNumber) { this.subscriberNumber = subscriberNumber; }

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }
}
