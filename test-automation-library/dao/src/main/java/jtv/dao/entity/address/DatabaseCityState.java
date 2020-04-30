package jtv.dao.entity.address;

import jtv.entity.BaseEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseCityState extends BaseEntity {
    String cityPostalCodeID;
    String stateProvinceId;

    public static RowMapper<DatabaseCityState> rowMapper() {
        return new DatabaseCityStateRowMapper();
    }

    private static class DatabaseCityStateRowMapper implements RowMapper<DatabaseCityState> {
        public DatabaseCityState mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            DatabaseCityState cityState = new DatabaseCityState();

            cityState.setCityPostalCodeID(resultSet.getString("CITY_POSTAL_CODE_ID"));
            cityState.setStateProvinceId(resultSet.getString("STATE_PROVINCE_ID"));

            return cityState;
        }
    }

    public String getCityPostalCodeID() { return cityPostalCodeID; }
    public void setCityPostalCodeID(String cityPostalCodeID) { this.cityPostalCodeID = cityPostalCodeID; }

    public String getStateProvinceId() { return stateProvinceId; }
    public void setStateProvinceId(String stateProvinceId) { this.stateProvinceId = stateProvinceId; }

}
