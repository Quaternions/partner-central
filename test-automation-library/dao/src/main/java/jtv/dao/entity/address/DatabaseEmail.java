package jtv.dao.entity.address;

import jtv.entity.BaseEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseEmail extends BaseEntity {
    private Integer emailId;
    private String emailAddress;

    public static RowMapper<DatabaseEmail> rowMapper() {
        return new DatabaseEmailRowMapper();
    }

    private static class DatabaseEmailRowMapper implements RowMapper<DatabaseEmail> {
        public DatabaseEmail mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            DatabaseEmail email = new DatabaseEmail();

            email.setEmailId(resultSet.getInt("EMAIL_ADDRESS_ID"));
            email.setEmailAddress(resultSet.getString("EMAIL_ADDRESS"));
            return email;
        }
    }

    public Integer getEmailId() { return emailId; }
    public void setEmailId(Integer emailId) { this.emailId = emailId; }

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

}


/*
public class DatabasePhone {
    Integer phoneId;
    String subscriberNumber;
    String extention;
    String countryCode;
    String isLandline;

    public static RowMapper<DatabasePhone> rowMapper() {
        return new DatabasePhoneRowMapper();
    }

    private static class DatabasePhoneRowMapper implements RowMapper<DatabasePhone> {
        public DatabasePhone mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            DatabasePhone phone = new DatabasePhone();

            phone.setPhoneId(resultSet.getInt("PHONE_NUMBER_ID"));
            phone.setSubscriberNumber(resultSet.getString("SUBSCRIBER_NUMBER"));
            phone.setExtension(resultSet.getString("EXTENSION"));
            phone.setCountryCode(resultSet.getString("COUNTRY_CODE"));
            phone.setIsLandline(resultSet.getString("IS_LANDLINE"));

            return phone;
        }
    }

    public Integer getPhoneId() { return phoneId; }
    public void setPhoneId(Integer phoneId) { this.phoneId = phoneId; }

    public String getSubscriberNumber() { return subscriberNumber; }
    public void setSubscriberNumber(String subscriberNumber) { this.subscriberNumber = subscriberNumber; }

    public String getExtension() { return extention; }
    public void setExtension(String extention) { this.extention = extention; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getIsLandline() { return isLandline; }
    public void setIsLandline(String isLandline) { this.isLandline = isLandline; }

}
 */