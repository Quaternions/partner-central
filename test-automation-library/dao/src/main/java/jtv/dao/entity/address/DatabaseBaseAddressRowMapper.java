package jtv.dao.entity.address;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseBaseAddressRowMapper implements RowMapper<DatabaseBaseAddress> {
        @Override
        public DatabaseBaseAddress mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            DatabaseBaseAddress baseAddress = new DatabaseBaseAddress();

            baseAddress.setCity(resultSet.getString("CITY"));
            baseAddress.setCounty(resultSet.getString("COUNTY"));
            baseAddress.setStateIsoTwo(resultSet.getString("STATE_ISO_2"));
            baseAddress.setState(resultSet.getString("STATE"));
            baseAddress.setPostalCode(resultSet.getString("POSTAL_CODE"));
            baseAddress.setZipFour(resultSet.getString("ZIP_FOUR"));
            baseAddress.setCountry(resultSet.getString("COUNTRY"));
            baseAddress.setCountryCode(resultSet.getString("COUNTRY_CODE"));

            return baseAddress;
        }
}
