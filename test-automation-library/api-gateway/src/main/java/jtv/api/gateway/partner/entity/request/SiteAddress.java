package jtv.api.gateway.partner.entity.request;

import jtv.entity.BaseEntity;

public class SiteAddress extends BaseEntity {
    private String addressLineOne;
    private String addressLineTwo;
    private String addressLineThree;
    private String city;
    private String subdivisionIsoCode;
    private String countryIso3Code;
    private String postalCode;

    public SiteAddress(String addressLineOne, String addressLineTwo, String addressLineThree, String city, String subdivisionIsoCode, String countryIso3Code, String postalCode) {
        this.addressLineOne = addressLineOne;
        this.addressLineTwo = addressLineTwo;
        this.addressLineThree = addressLineThree;
        this.city = city;
        this.subdivisionIsoCode = subdivisionIsoCode;
        this.countryIso3Code = countryIso3Code;
        this.postalCode = postalCode;
    }


    public SiteAddress() { }

    public String getAddressLineOne() { return addressLineOne; }
    public void setAddressLineOne(String addressLineOne) { this.addressLineOne = addressLineOne; }

    public String getAddressLineTwo() { return addressLineTwo; }
    public void setAddressLineTwo(String addressLineTwo) { this.addressLineTwo = addressLineTwo; }

    public String getAddressLineThree() { return addressLineThree; }
    public void setAddressLineThree(String addressLineThree) { this.addressLineThree = addressLineThree; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getSubdivisionIsoCode() { return subdivisionIsoCode; }
    public void setSubdivisionIsoCode(String subdivisionIsoCode) { this.subdivisionIsoCode = subdivisionIsoCode; }

    public String getCountryIso3Code() { return countryIso3Code; }
    public void setCountryIso3Code(String countryIso3Code) { this.countryIso3Code = countryIso3Code; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
}
