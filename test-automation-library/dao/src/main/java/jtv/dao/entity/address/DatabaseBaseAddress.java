package jtv.dao.entity.address;

import jtv.entity.BaseEntity;
import org.springframework.jdbc.core.RowMapper;

public class DatabaseBaseAddress extends BaseEntity {
    private String city;
    private String county;
    private String state;
    private String stateIsoTwo;
    private String postalCode;
    private String zipFour;
    private String country;
    private String countryCode;

    public static RowMapper<? extends DatabaseBaseAddress> rowMapper() {
        return new DatabaseBaseAddressRowMapper();
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public String getStateIsoTwo() {
        return stateIsoTwo;
    }

    public void setStateIsoTwo(String stateIsoTwo) {
        this.stateIsoTwo = stateIsoTwo;
    }

    public String getCounty() {
        return county;
    }
    public void setCounty(String county) {
        this.county = county;
    }

    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getZipFour() {
        return zipFour;
    }
    public void setZipFour(String zipFour) {
        this.zipFour = zipFour;
    }

    public String getCountry(){
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

}
