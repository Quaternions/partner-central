package jtv.dao.address;

import jtv.dao.DatabaseConfig;
import jtv.dao.X5BaseDao;
import jtv.dao.entity.address.*;
import jtv.dao.entity.address.mapper.DatabaseAddressMapper;
import jtv.dao.entity.address.mapper.DatabaseCustomerAddressMapper;
import jtv.dao.entity.customer.address.DatabaseCustomerAddress;
import jtv.dao.entity.customer.address.DatabaseCustomerAddressRowMapper;
import jtv.dao.entity.customer.defaults.DatabaseCustomerDefaultAddress;
import jtv.data.generator.DataGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains all methods that touch CUSTOMER.CUST_ADDRESSES
 * @author chrrai1
 */
public class AddressDao extends X5BaseDao {
    private final static Logger log = LoggerFactory.getLogger(AddressDao.class);

    public AddressDao() {
        super();
    }

    public AddressDao(DatabaseConfig databaseConfig) {
        super(databaseConfig);
    }

    public DatabaseAddress insertAddress(String addressUuid, String addressLineOne, String addressLineTwo, DatabaseCityState cityState) {
        String sql;
        Map<String, String> params = new HashMap<>();

        params.put("addressLineOne", addressLineOne);
        params.put("addressLineTwo", addressLineTwo);
        params.put("cityPostalCodeId", cityState.getCityPostalCodeID());
        params.put("stateProvinceId", cityState.getStateProvinceId());
        params.put("uuid", addressUuid);
        params.put("uuidSource", "JTV");

        sql =   "insert into\n"
              + "address_schema.address(address_line_1, address_line_2, city_postal_code_id, state_province_id, uuid, uuid_source)\n"
              + "values(:addressLineOne, :addressLineTwo, :cityPostalCodeId, :stateProvinceId, :uuid, :uuidSource)";

        update(sql, params);

        return getFullAddressByUuid(addressUuid);
    }

    private DatabaseAddress getFullAddressByUuid(String addressUuid) {
        String sql;
        Map<String, String> params = new HashMap<>();

        params.put("uuid", addressUuid);

        sql = "select\n" +
                "  a.address_id,\n" +
                "  a.uuid,\n" +
                "  a.uuid_source,\n" +
                "  a.address_line_1,\n" +
                "  a.address_line_2,\n" +
                "  c.name as city,\n" +
                "  cou.name as county,\n" +
                "  sp.iso_alpha2_code as state_iso_2,\n" +
                "  sp.name as state,\n" +
                "  pc.postal_code,\n" +
                "  zr.range_begin as zip_four,\n" +
                "  co.name as country,\n" +
                "  co.iso_alpha2_code as country_code\n" +
                "from\n" +
                "  address_schema.address a\n" +
                "  join address_schema.city_postal_code cpc on cpc.city_postal_code_id = a.city_postal_code_id\n" +
                "  join address_schema.city c on c.city_id = cpc.city_id\n" +
                "  join address_schema.county cou on cou.county_id = c.county_id\n" +
                "  join address_schema.state_province sp on sp.state_province_id = a.state_province_id\n" +
                "  left join address_schema.zip4_range zr on zr.city_postal_code_id = cpc.city_postal_code_id\n" +
                "  left join address_schema.postal_code pc on pc.postal_code_id = cpc.postal_code_id\n" +
                "  left join address_schema.country co on co.country_id = sp.country_id\n" +
                "where\n" +
                "  a.uuid = :uuid";

        return queryForObject(sql,params, DatabaseAddress.rowMapper());
    }

    public DatabaseCityState getRandomRealCityState() {
        String sql;

        log.info("Getting a real city state pairing from ADDRESS_SCHEMA");

        sql =   "select * from (\n" +
                "  select\n" +
                "    cpc.city_postal_code_id\n" +
                "  , state.state_province_id\n" +
                "  from address_schema.postal_code postal_code\n" +
                "  join address_schema.city_postal_code cpc on postal_code.postal_code_id = cpc.postal_code_id\n" +
                "  join address_schema.city city on city.city_id = cpc.city_id\n" +
                "  join address_schema.county county on county.county_id = city.county_id\n" +
                "  join address_schema.state_province state on state.state_province_id = county.state_province_id\n" +
                "  join address_schema.country country on country.country_id = state.country_id\n" +
                "  left join address_schema.zip4_range zip_four on zip_four.city_postal_code_id = cpc.city_postal_code_id\n" +
                "  where postal_code.postal_code_id = \n" +
                "  (\n" +
                "    select postal_code_id from (\n" +
                "    select\n" +
                "    *\n" +
                "    from\n" +
                "    address_schema.postal_code\n" +
                "    order by SYS.DBMS_RANDOM.VALUE\n" +
                "    )\n" +
                "    where rownum <= 1\n" +
                "  ) order by SYS.DBMS_RANDOM.VALUE\n" +
                ") where rownum <= 1\n";

        return queryForObject(sql, DatabaseCityState.rowMapper());
    }

    public List<String> getCurrentTaxableStates() {
        String sql;

        sql =  "select distinct(sp.iso_alpha2_code) as tax_state\n" +
                "from address_schema.state_province sp\n" +
                "join tax_schema.state_tax_xref x\n" +
                "on sp.state_province_id = x.state_province_id\n" +
                "join tax_schema.tax_schedule ts\n" +
                "on x.tax_schedule_id = ts.tax_schedule_id\n" +
                "where (ts.inactive_date is null or ts.inactive_date > sysdate)";

        return queryForList(sql, String.class);
    }

    public DatabasePhone insertPhoneNumber(String subscriberNumber, String countryCode, String extension) {
        String sql;

        Map<String, String> params = new HashMap<>();

        params.put("subscriberNumber", subscriberNumber);
        params.put("countryCode", countryCode);
        params.put("extension", extension);
        params.put("isLandline", "N");

        sql = "insert into address_schema.phone_number (COUNTRY_CODE, SUBSCRIBER_NUMBER, EXTENSION, IS_LANDLINE) values (:countryCode, :subscriberNumber, :extension, :isLandline)";

        update(sql, params);

        return getPhoneNumber(subscriberNumber);
    }

    private DatabasePhone getPhoneNumber(String subscriberNumber) {
        String sql;
        Map<String, String> params = new HashMap<>();

        params.put("subscriberNumber", subscriberNumber);

        sql = "select\n" +
                "phone_number_id,\n" +
                "country_code,\n" +
                "subscriber_number,\n" +
                "extension,\n" +
                "is_landline\n" +
                "from address_schema.phone_number where subscriber_number = :subscriberNumber";

        return queryForObject(sql, params, DatabasePhone.rowMapper());
    }

    public DatabaseEmail insertEmailAddress(String emailAddress) {
        String sql;

        Map<String, String> params = new HashMap<>();

        params.put("emailAddress", emailAddress);

        sql = "insert into address_schema.email_address (EMAIL_ADDRESS) values (:emailAddress)";

        update(sql, params);

        return getEmailAddress(emailAddress);
    }

    private DatabaseEmail getEmailAddress(String emailAddress) {
        String sql;

        Map<String, String> params = new HashMap<>();

        params.put("emailAddress", emailAddress);

        sql = "select EMAIL_ADDRESS_ID, EMAIL_ADDRESS from address_schema.email_address where EMAIL_ADDRESS = :emailAddress";

        return queryForObject(sql, params, DatabaseEmail.rowMapper());
    }


    public DatabaseBaseAddress getRandomRealAddressWithNonTaxableState() {
        String sql;

        log.info("Getting address with non taxable state");

        sql =   "select * from (\n" +
                "  select\n" +
                "    city.name as city\n" +
                "  , county.name as county\n" +
                "  , state.iso_alpha2_code as state_iso_2\n" +
                "  , state.name as state\n" +
                "  , postal_code.postal_code as postal_code\n" +
                "  , zip_four.range_begin as zip_four\n" +
                "  , country.iso_alpha2_code as country_code\n" +
                "  , country.name as country\n" +
                "  from\n" +
                "  address_schema.postal_code postal_code\n" +
                "  join address_schema.city_postal_code cpc on postal_code.postal_code_id = cpc.postal_code_id\n" +
                "  join address_schema.city city on city.city_id = cpc.city_id\n" +
                "  join address_schema.county county on county.county_id = city.county_id\n" +
                "  join ADDRESS_SCHEMA.state_province state on state.state_province_id = county.state_province_id\n" +
                "  join address_schema.country country on country.country_id = state.country_id\n" +
                "  left join ADDRESS_SCHEMA.zip4_range zip_four on zip_four.city_postal_code_id = cpc.city_postal_code_id\n" +
                "  where state.state_province_id not in\n" +
                "(\n" +
                "    select distinct(sp.state_province_id) as tax_state\n" +
                "    from address_schema.state_province sp\n" +
                "    join tax_schema.state_tax_xref x\n" +
                "    on sp.state_province_id = x.state_province_id\n" +
                "    join tax_schema.tax_schedule ts\n" +
                "    on x.tax_schedule_id = ts.tax_schedule_id\n" +
                "    where (ts.inactive_date is null or ts.inactive_date > sysdate)\n" +
                "\n" +
                ")\n" +
                "  order by SYS.DBMS_RANDOM.VALUE\n" +
                "  )\n" +
                "  where rownum <= 1";

        return queryForObject(sql, DatabaseBaseAddress.rowMapper());
    }

    public DatabaseBaseAddress getRandomRealAddressWithTaxableState() {
        String sql;

        log.info("Getting address with taxable state");

        sql =   "select * from (\n" +
                "  select\n" +
                "    city.name as city\n" +
                "  , county.name as county\n" +
                "  , state.iso_alpha2_code as state_iso_2\n" +
                "  , state.name as state\n" +
                "  , postal_code.postal_code as postal_code\n" +
                "  , zip_four.range_begin as zip_four\n" +
                "  , country.iso_alpha2_code as country_code\n" +
                "  , country.name as country\n" +
                "  from\n" +
                "  address_schema.postal_code postal_code\n" +
                "  join address_schema.city_postal_code cpc on postal_code.postal_code_id = cpc.postal_code_id\n" +
                "  join address_schema.city city on city.city_id = cpc.city_id\n" +
                "  join address_schema.county county on county.county_id = city.county_id\n" +
                "  join ADDRESS_SCHEMA.state_province state on state.state_province_id = county.state_province_id\n" +
                "  join address_schema.country country on country.country_id = state.country_id\n" +
                "  left join ADDRESS_SCHEMA.zip4_range zip_four on zip_four.city_postal_code_id = cpc.city_postal_code_id\n" +
                "  where state.state_province_id =\n" +
                "(\n" +
                "select * from (\n" +
                "  select * from (\n" +
                "    select distinct(sp.state_province_id) as tax_state\n" +
                "    from address_schema.state_province sp\n" +
                "    join tax_schema.state_tax_xref x\n" +
                "    on sp.state_province_id = x.state_province_id\n" +
                "    join tax_schema.tax_schedule ts\n" +
                "    on x.tax_schedule_id = ts.tax_schedule_id\n" +
                "    where (ts.inactive_date is null or ts.inactive_date > sysdate)\n" +
                "  ) order by SYS.DBMS_RANDOM.VALUE\n" +
                ") where rownum <= 1\n" +
                ")\n" +
                "  order by SYS.DBMS_RANDOM.VALUE\n" +
                "  )\n" +
                "  where rownum <= 1";

        return queryForObject(sql, DatabaseBaseAddress.rowMapper());
    }

    public DatabaseBaseAddress getRandomRealAddress() {
        String sql;

        log.info("Getting real address from ADDRESS_SCHEMA");

        sql =   "select * from (\n" +
                "  select\n" +
                "    city.name as city\n" +
                "  , county.name as county\n" +
                "  , state.iso_alpha2_code as state_iso_2\n" +
                "  , state.name as state\n" +
                "  , postal_code.postal_code as postal_code\n" +
                "  , zip_four.range_begin as zip_four\n" +
                "  , country.iso_alpha2_code as country_code\n" +
                "  , country.name as country\n" +
                "  from address_schema.postal_code postal_code\n" +
                "  join address_schema.city_postal_code cpc on postal_code.postal_code_id = cpc.postal_code_id\n" +
                "  join address_schema.city city on city.city_id = cpc.city_id\n" +
                "  join address_schema.county county on county.county_id = city.county_id\n" +
                "  join ADDRESS_SCHEMA.state_province state on state.state_province_id = county.state_province_id\n" +
                "  join address_schema.country country on country.country_id = state.country_id and country.iso_alpha2_code = 'US'\n" +
                "  left join ADDRESS_SCHEMA.zip4_range zip_four on zip_four.city_postal_code_id = cpc.city_postal_code_id\n" +
                "  where postal_code.postal_code_id = \n" +
                "  (\n" +
                "    select postal_code_id from (\n" +
                "    select\n" +
                "    *\n" +
                "    from\n" +
                "    address_schema.postal_code\n" +
                "    order by SYS.DBMS_RANDOM.VALUE\n" +
                "    )\n" +
                "    where rownum <= 1\n" +
                "  ) order by SYS.DBMS_RANDOM.VALUE\n" +
                ") where rownum <= 1";

        return queryForObject(sql, DatabaseBaseAddress.rowMapper());
    }


    public void insertOrUpdateDefaultAddressForCustomerAndSalesChannel(String customerId, String addressId, String salesChannel) {
        try {
            getDefaultAddressForCustomerAndSalesChannel(customerId, salesChannel);
            updateDefaultAddressForCustomerAndSalesChannel(customerId, addressId, salesChannel);
        } catch (EmptyResultDataAccessException e) {
            log.info("No existing default. Inserting instead", e);
            insertDefaultAddressForCustomerAndSalesChannel(customerId, addressId, salesChannel);
        }
    }

    public void insertDefaultAddressForCustomerAndSalesChannel(String customerId, String addressId, String salesChannelCode) {
        String sql;
        Map<String, String> params = new HashMap<>();

        params.put("customerId", customerId);
        params.put("salesChannelCode", salesChannelCode);
        params.put("addressId", addressId);

        sql =   "insert into\n" +
                "customer.cust_default_address(cust_id, address_id, sales_channel_id)\n" +
                "values(:customerId, :addressId, (select sales_channel_id from jtv_schema.sales_channel where code = :salesChannelCode))";

        update(sql, params);
    }

    public void updateDefaultAddressForCustomerAndSalesChannel(String customerId, String addressId, String salesChannelCode) {
        String sql;
        Map<String, String> params = new HashMap<>();

        params.put("customerId", customerId);
        params.put("salesChannelCode", salesChannelCode);
        params.put("addressId", addressId);

        sql =   "update customer.cust_default_address\n" +
                "set address_id = :addressId\n" +
                "where cust_id = :customerId\n" +
                "and sales_channel_id = (select sales_channel_id from jtv_schema.sales_channel where code = :salesChannelCode)";

        update(sql, params);
    }

    public List<DatabaseCustomerDefaultAddress> getDefaultAddressesForCustomer(String customerId) {
        String sql;
        Map<String, String> params = new HashMap<>();

        params.put("customerId", customerId);

        sql =   "select \n" +
                "  ca.uuid as address_id\n" +
                ", cda.cust_id\n" +
                ", sc.code as sales_channel_code\n" +
                "from customer.cust_default_address cda\n" +
                "left join jtv_schema.sales_channel sc on sc.sales_channel_id = cda.sales_channel_id\n" +
                "left join customer.cust_addresses ca on ca.address_id = cda.address_id and ca.cust_id = cda.cust_id\n" +
                "where \n" +
                "cda.cust_id = :customerId\n" +
                "and is_active = 1\n" +
                "and approved_status <> 'B'";

        return query(sql, params, DatabaseCustomerDefaultAddress.getRowMapperInstance());
    }

    public DatabaseCustomerDefaultAddress getDefaultAddressForCustomerAndSalesChannel(String customerId, String salesChannelCode) {
        String sql;
        Map<String, String> params = new HashMap<>();

        params.put("customerId", customerId);
        params.put("salesChannelCode", salesChannelCode);

        sql =   "select \n" +
                "  ca.uuid as address_id\n" +
                ", cda.cust_id\n" +
                ", sc.code as sales_channel_code\n" +
                "from customer.cust_default_address cda\n" +
                "left join jtv_schema.sales_channel sc on sc.sales_channel_id = cda.sales_channel_id\n" +
                "left join customer.cust_addresses ca on ca.address_id = cda.address_id and ca.cust_id = cda.cust_id\n" +
                "where \n" +
                "cda.cust_id = :customerId\n" +
                "and sc.code = :salesChannelCode\n" +
                "and is_active = 1\n" +
                "and approved_status <> 'B'";

        return queryForObject(sql, params, DatabaseCustomerDefaultAddress.getRowMapperInstance());
    }

    public List<DatabaseCustomerAddress> getCustomerActiveAddresses(String customerId) {
        String sql;
        Map<String, String> params = new HashMap<>();

        params.put("customerId", customerId);

        sql =   "select ca.*,\n" +
                "decode(ca.ZIP5, '00000', null, ca.zip5) as ZIP_FIVE\n" +
                "from customer.cust_addresses ca\n" +
                "where cust_id = :customerId\n" +
                "and is_active = 1\n" +
                "and approved_status <> 'B'";

        try {
            return query(sql, params, new DatabaseCustomerAddressRowMapper());
        } catch (EmptyResultDataAccessException e) {
            log.info("Customer has no addresses. Returning empty list", e);
            return new ArrayList<>();
        }

    }

    public DatabaseCustomerAddress getAddressbyUuidOrAddressId(String addressId) {
        String sql;
        Map<String, String> params = new HashMap<>();
        String whereClause;

        whereClause = "where ca.address_id = :addressId";

        try {
            Integer.parseInt(addressId);
        } catch(NumberFormatException e) {
            whereClause = "where ca.uuid = :addressId";
        }

        params.put("addressId", addressId);

        sql =   "select ca.*,\n" +
                "decode(ca.ZIP5, '00000', null, ca.zip5) as ZIP_FIVE\n" +
                "from customer.cust_addresses ca\n" +
                whereClause;

        return queryForObject(sql, params, new DatabaseCustomerAddressRowMapper());
    }

    public DatabaseCustomerAddress getAddressByUuid(String uuid) {
        String sql;
        Map<String, String> params = new HashMap<>();

        params.put("uuid", uuid);

        sql =   "select ca.*,\n" +
                "decode(ca.ZIP5, '00000', null, ca.zip5) as ZIP_FIVE\n" +
                "from customer.cust_addresses ca\n" +
                "where uuid = :uuid";

        return queryForObject(sql, params, new DatabaseCustomerAddressRowMapper());
    }

    /** Returns the address ID that matches the address in ACNTV.CUST
     */
    public String getPrimaryCustomerAddress(String customerId) {
        String sql;
        Map<String, String> params = new HashMap<>();

        params.put("customerId", customerId);

        sql =   "select\n" +
                "ca.address_id\n" +
                "from\n" +
                "customer.cust_addresses ca\n" +
                "join acntv.cust c on c.cust_id = ca.cust_id\n" +
                "WHERE c.cust_id = :customerId\n" +
                "       AND ca.address = TRIM(c.address1)\n" +
                "       AND nvl(ca.address2,   '0') = nvl(TRIM(c.address2),   '0')\n" +
                "       AND ca.city = TRIM(c.city)\n" +
                "       AND nvl(ca.state,   '0') = nvl(TRIM(c.state),   '0')\n" +
                "       AND nvl(ca.zip5,   '0') = nvl(TRIM(c.zip5),   '0')\n" +
                "       AND nvl(ca.zip4, '0') = NVL(TRIM(c.zip4), '0')\n" +
                "       AND nvl(ca.postal_code,   '0') = nvl(TRIM(c.postal_code),   '0')\n" +
                "       AND nvl(ca.country_code,   '0') = nvl(TRIM(c.country),   '0')\n" +
                "       AND nvl(ca.ship_first_name,   '#$%$') = nvl(TRIM(c.first_name),   '#$%$')\n" +
                "       AND nvl(ca.ship_last_name,   '#$%$') = nvl(TRIM(c.last_name),   '#$%$')";

        return queryForObject(sql, params, String.class);
    }

    /**
     * Looks for the given address ID
     * @param   addressId   The address ID to look for
     * @return              A DatabaseCustomerAdress object. Returns null if address
     *                      not found
     */
    public DatabaseCustomerAddress getCustomerAddressByAddressId(Integer addressId) {
        String sqlQuery;
        Map<String, Integer> params = new HashMap<>();

        params.put("addressId", addressId);

        sqlQuery = "select ca.*, decode(ca.ZIP5, '00000', null, ca.zip5) as ZIP_FIVE from customer.cust_addresses ca where address_id = :addressId";

        return queryForObject(sqlQuery, params, new DatabaseCustomerAddressRowMapper());
    }
    
    /**
     * Gets a random address for the given customer. The address must be active 
     * and unblocked to be returned
     * @param   customerId  Customer ID to return an address for
     * @return              A DatabaseCustomerAddress object. Returns null if no
     *                      address is found
     */
    public DatabaseCustomerAddress getRandomActiveAddressForCustomer(String customerId) {
        String sqlQuery;
        Map<String, String> params = new HashMap<>();

        params.put("customerId", customerId);
        
        sqlQuery = "select * from (select ca.*, decode(ca.ZIP5, '00000', null, ca.zip5) as ZIP_FIVE from CUSTOMER.CUST_ADDRESSES ca where ca.cust_id = :customerId and is_active = 1 and approved_status <> 'B' order by SYS.DBMS_RANDOM.VALUE ) where rownum <= 1";

        return queryForObject(sqlQuery, params, new DatabaseCustomerAddressRowMapper());
    }
    
    /**
     * Gets a random address for a customer OTHER THAN the given customer ID. 
     * The address must be active and unblocked to be returned
     * @param   customerId  Customer ID to NOT return and address for
     * @return              A DatabaseCustomerAddress object. Returns null if no
     *                      address is found
     */
    public DatabaseCustomerAddress getRandomActiveAddressForDifferentCustomer(String customerId) {
        String sqlQuery;
        Map<String, String> params = new HashMap<>();

        params.put("customerId", customerId);
        
        sqlQuery = "select * from ( select ca.*, decode(ca.ZIP5, '00000', null, ca.zip5) as ZIP_FIVE from CUSTOMER.CUST_ADDRESSES ca where ca.cust_id <> :customerId and ca.LAST_MODIFIED_DATE > sysdate - 7 and is_active = 1 and approved_status = 'U' order by SYS.DBMS_RANDOM.VALUE ) where rownum <= 1";

        return queryForObject(sqlQuery, params, new DatabaseCustomerAddressRowMapper());
    }
    
    /**
     * Marks the given address as inactive
     * @param   addressId   The address ID to mark as inactive
     */
    public void inactivateAddress(Integer addressId) {
        String sqlQuery;
        Map<String, Integer> params = new HashMap<>();

        params.put("addressId", addressId);

        log.info("Inactivating address " + addressId.toString());
        
        sqlQuery = "update customer.cust_addresses set is_active = 0 where address_id = :addressId";
        
        update(sqlQuery, params);
    }
    
    /**
     * Marks the given address as active
     * @param   addressId   The address ID to mark as active
     */
    public void activateAddress(Integer addressId) {
        String sqlQuery;
        Map<String, Integer> params = new HashMap<>();

        params.put("addressId", addressId);

        log.info("Activating address " + addressId.toString());
        
        sqlQuery = "update customer.cust_addresses set is_active = 1 where address_id = :addressId";
        
        update(sqlQuery, params);
    }
    
    /**
     * Marks the given address as blocked
     * @param   addressId   The address ID to mark as blocked
     */
    public void blockAddress(Integer addressId) {
        String sqlQuery;
        Map<String, Integer> params = new HashMap<>();

        params.put("addressId", addressId);

        log.info("Blocking address " + addressId.toString());
        
        sqlQuery = "update customer.cust_addresses set approved_status = 'B' where address_id = :addressId";
        
        update(sqlQuery, params);
    }
    
    /**
     * Marks the given address as approved
     * @param   addressId   The address ID to mark as approved
     */
    public void approveAddress(Integer addressId) {
        String sqlQuery;
        Map<String, Integer> params = new HashMap<>();

        params.put("addressId", addressId);

        log.info("Approving address " + addressId.toString());

        sqlQuery = "update customer.cust_addresses set approved_status = 'A' where address_id = :addressId";
        
        update(sqlQuery, params);
    }
    
    /**
     * Inserts a new domestic address record for the given customer. All of the 
     * address info is dynamically generated
     * @param   customerId  The customer ID to create a new address for
     * @return              Returns the new address ID
     */
    public Integer createNewDomesticAddress(String customerId) throws UnsupportedOperationException {
        DatabaseCustomerAddress customerAddress;
        SimpleJdbcCall jdbcCall;
        Integer addressId;

        log.info("Creating new domestic address for customer " + customerId);

        // Generates a new DatabaseCustomerAddress object
        customerAddress = generateCustomerAddress(customerId, false);
        
        // Build the JDBC call and execute
        jdbcCall = buildCreateAddressJdbcCall();
        addressId = executeObject(jdbcCall, BigDecimal.class, buildCustomerAddressParameterMap(customerAddress)).intValue();

        return addressId;
    }

    public DatabaseCustomerAddress createAndReturnNewDomesticAddress(String customerId) throws UnsupportedOperationException {
        return createAndReturnNewDomesticAddress(customerId, false);
    }

    public DatabaseCustomerAddress createAndReturnNewDomesticAddress(String customerId, boolean useRealPostalCode) throws UnsupportedOperationException {
        return createAndReturnNewDomesticAddress(customerId, useRealPostalCode, null);
    }

    public DatabaseCustomerAddress createAndReturnNewDomesticAddress(String customerId, boolean useRealPostalCode, Boolean useTaxableState) throws UnsupportedOperationException {
        return createAndReturnNewDomesticAddress(customerId, useRealPostalCode, useTaxableState, false);
    }

    public DatabaseCustomerAddress createAndReturnNewDomesticAddress(String customerId, boolean useRealPostalCode, Boolean useTaxableState, Boolean isPoBox) throws UnsupportedOperationException {
        DatabaseCustomerAddress customerAddress;
        SimpleJdbcCall jdbcCall;
        Integer addressId;

        log.info("Creating new domestic address for customer " + customerId);

        // Generates a new DatabaseCustomerAddress object
        customerAddress = generateCustomerAddress(customerId, false, useRealPostalCode, useTaxableState, isPoBox);

        // Build the JDBC call and execute
        jdbcCall = buildCreateAddressJdbcCall();
        addressId = executeObject(jdbcCall, BigDecimal.class, buildCustomerAddressParameterMap(customerAddress)).intValue();
        customerAddress.setAddressId(addressId);

        return customerAddress;
    }

    public DatabaseCustomerAddress createAndReturnNewBasicDomesticAddress(String customerId) throws UnsupportedOperationException {
        DatabaseCustomerAddress customerAddress;
        SimpleJdbcCall jdbcCall;
        Integer addressId;

        log.info("Creating new domestic address for customer " + customerId);

        // Generates a new DatabaseCustomerAddress object
        customerAddress = generateCustomerAddress(customerId, false);
        customerAddress.setAddressLineTwo(null);
        customerAddress.setZip4(null);

        // Build the JDBC call and execute
        jdbcCall = buildCreateAddressJdbcCall();
        addressId = executeObject(jdbcCall, BigDecimal.class, buildCustomerAddressParameterMap(customerAddress)).intValue();
        customerAddress.setAddressId(addressId);

        return customerAddress;
    }
    
    /**
     * Inserts a new interational address record for the given customer. All of 
     * the address info is dynamically generated
     * @param   customerId  The customer ID to create a new address for
     * @return              Returns the new address ID
     */
    public Integer createNewInternationalAddress(String customerId) throws UnsupportedOperationException {
        DatabaseCustomerAddress customerAddress;
        SimpleJdbcCall jdbcCall;
        //Integer addressId

        log.info("Creating new international address for customer " + customerId);
        
        customerAddress = generateCustomerAddress(customerId, true);
        jdbcCall = buildCreateAddressJdbcCall();

        return executeObject(jdbcCall, BigDecimal.class, buildCustomerAddressParameterMap(customerAddress)).intValue();
    }

    /**
     * Builds a SimpleJdbcCall object to create a new customer address
     * @return  A SimpleJdbcCall object
     */
    private SimpleJdbcCall buildCreateAddressJdbcCall() {
        SimpleJdbcCall jdbcCall = simpleJdbcCall()
            .withSchemaName("CUSTOMER")
            .withCatalogName("CUST_ADDR_PKG")
            .withProcedureName("UPDATE_CUST_ADDR");
        
        return jdbcCall;
        
    }
    
    /**
     * Maps a DatabaseCustomerAddress object to a Map for creating a new address
     * @param   customerAddress The address to be mapped
     * @return  An address Map
     */
    private static Map<String, Object> buildCustomerAddressParameterMap(DatabaseCustomerAddress customerAddress) {
        Map<String, Object> parameterMap = new HashMap<>();
        
        parameterMap.put("NEW_CUST_ID",         customerAddress.getCustomerId());
        parameterMap.put("NEW_ADDRESS",         StringUtils.upperCase(customerAddress.getAddress()));
        parameterMap.put("NEW_ADDRESS2",        StringUtils.upperCase(customerAddress.getAddressLineTwo()));
        parameterMap.put("NEW_CITY",            StringUtils.upperCase(customerAddress.getCity()));
        parameterMap.put("NEW_STATE",           StringUtils.upperCase(customerAddress.getState()));
        parameterMap.put("NEW_ZIP5",            StringUtils.upperCase(customerAddress.getZip5()));
        parameterMap.put("NEW_ZIP4",            StringUtils.upperCase(customerAddress.getZip4()));
        parameterMap.put("NEW_POSTAL_CODE",     StringUtils.upperCase(customerAddress.getPostalCode()));
        parameterMap.put("NEW_COUNTRY_CODE",    StringUtils.upperCase(customerAddress.getCountryCode()));
        parameterMap.put("NEW_SHIP_FIRST_NAME", StringUtils.upperCase(customerAddress.getFirstName()));
        parameterMap.put("NEW_SHIP_LAST_NAME",  StringUtils.upperCase(customerAddress.getLastName()));
        parameterMap.put("CALLING_FROM",        "CUSTOMER");
        parameterMap.put("NEW_SHIP_ADDR_ID",    null);
        
        return parameterMap;
    }

    private DatabaseCustomerAddress generateCustomerAddress(String customerId, boolean isInternationalAddress) throws UnsupportedOperationException {
        return generateCustomerAddress(customerId, isInternationalAddress, false);
    }

    private DatabaseCustomerAddress generateCustomerAddress(String customerId, boolean isInternationalAddress, boolean useRealPostalCode) throws UnsupportedOperationException {
        return generateCustomerAddress(customerId, isInternationalAddress, useRealPostalCode, null);
    }

    private DatabaseCustomerAddress generateCustomerAddress(String customerId, boolean isInternationalAddress, boolean useRealPostalCode, Boolean useTaxableState) throws UnsupportedOperationException {
        return generateCustomerAddress(customerId, isInternationalAddress, useRealPostalCode, useTaxableState, false);
    }

    /**
     * Generates a new customer address from random values in a list
     * @param   customerId              The customer ID for the new address
     * @param   isInternationalAddress  Specifies whether the generated address 
     *                                  should be international or not
     * @return                          The generated DatabaseCustomerAddress object
     */
    private DatabaseCustomerAddress generateCustomerAddress(String customerId, boolean isInternationalAddress, boolean useRealPostalCode, Boolean useTaxableState, Boolean isPoBox) throws UnsupportedOperationException {
        DatabaseCustomerAddress customerAddress;
        DatabaseAddress realAddress;
        String firstName;
        String lastName;
        String addressLineOne;

        if (isInternationalAddress && useRealPostalCode) {
            throw new UnsupportedOperationException("Ability to get real international address not yet implemented");
        }

        firstName = DataGenerator.NameGenerator.generateFirstName();
        lastName = DataGenerator.NameGenerator.generateLastName();

        if (isPoBox) {
            addressLineOne = DataGenerator.AddressGenerator.generatePoBox();
        } else {
            addressLineOne = DataGenerator.AddressGenerator.generateAddressLineOne();
        }

        if (useRealPostalCode) {
            if (useTaxableState != null) {
                if (useTaxableState) {
                    realAddress = DatabaseAddressMapper.mapDatabaseBaseAddressToDatabaseAddress(getRandomRealAddressWithTaxableState());
                } else {
                    realAddress = DatabaseAddressMapper.mapDatabaseBaseAddressToDatabaseAddress(getRandomRealAddressWithNonTaxableState());
                }
            } else {
                realAddress = DatabaseAddressMapper.mapDatabaseBaseAddressToDatabaseAddress(getRandomRealAddress());
            }

            realAddress.setFirstName(firstName);
            realAddress.setLastName(lastName);
            realAddress.setAddressLineOne(addressLineOne);
            realAddress.setAddressLineTwo(DataGenerator.AddressGenerator.generateAddressLineTwo());
            customerAddress = DatabaseCustomerAddressMapper.mapDatabaseAddressToDatabaseCustomerAddress(realAddress);
            customerAddress.setCustomerId(Integer.parseInt(customerId));
        } else {
            if (useTaxableState != null && useTaxableState) {
                throw new UnsupportedOperationException("If not using a real postal code, you can't use a taxable state");
            }

            customerAddress = new DatabaseCustomerAddress();

            customerAddress.setFirstName(firstName);
            customerAddress.setLastName(lastName);
            customerAddress.setAddress(addressLineOne);
            customerAddress.setAddressLineTwo(DataGenerator.AddressGenerator.generateAddressLineTwo());
            customerAddress.setCity(DataGenerator.AddressGenerator.generateCity());
            customerAddress.setState(DataGenerator.AddressGenerator.generateState());
            customerAddress.setCustomerId(Integer.parseInt(customerId));

            if (isInternationalAddress) {
                customerAddress.setPostalCode(DataGenerator.AddressGenerator.generatePostalCode());
                customerAddress.setCountryCode(DataGenerator.AddressGenerator.generateCountryCode());
                // Zip5 has to be '00000' because that's what Core does for address creation in the call center
                customerAddress.setZip5("00000");
            } else {
                customerAddress.setZip5(DataGenerator.AddressGenerator.generateZipFive());
                customerAddress.setZip4(DataGenerator.AddressGenerator.generateZipFour());
                customerAddress.setCountryCode("US");
            }
        }

        customerAddress.setCity(StringUtils.left(customerAddress.getCity(), 20));

        return customerAddress;
    }

}

