package jtv.dao.entity.user;

import jtv.entity.BaseEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUserAccount extends BaseEntity {
    private Integer userId;
    private String firstName;
    private String lastName;
    private String userName;
    private String jtvUuid;

    public DatabaseUserAccount() {
    }

    public DatabaseUserAccount(String firstName, String lastName, String username, String jtvUuid, String uuidSource) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = username;
        this.jtvUuid = jtvUuid;
    }

    public static DatabaseUserAccountRowMapper getRowMapperInstance() {
        return new DatabaseUserAccountRowMapper();
    }

    private static class DatabaseUserAccountRowMapper implements RowMapper<DatabaseUserAccount> {
        public DatabaseUserAccount mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            DatabaseUserAccount account = new DatabaseUserAccount();

            account.setUserId(resultSet.getInt("USER_ACCOUNT_ID"));
            account.setFirstName(resultSet.getString("FIRST_NAME"));
            account.setLastName(resultSet.getString("LAST_NAME"));
            account.setUserName(resultSet.getString("USER_NAME"));
            account.setJtvUuid(resultSet.getString("UUID"));

            return account;
        }
    }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getJtvUuid() {
        return jtvUuid;
    }
    public void setJtvUuid(String uuid) {
        this.jtvUuid = uuid;
    }

}
