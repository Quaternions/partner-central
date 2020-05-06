package jtv.dao.partner;

import com.jtv.test.db.entity.partner.DbAssociate;
import com.jtv.test.db.entity.partner.DbPartner;
import com.jtv.test.db.entity.partner.DbSite;
import com.jtv.test.db.query.partner.DbAssociateQueryBuilder;
import com.jtv.test.db.query.partner.DbPartnerQueryBuilder;
import com.jtv.test.db.query.partner.DbSiteQueryBuilder;
import com.jtv.test.utils.jdbc.JtvJdbcTemplate;
import jtv.dao.DatabaseConfig;
import jtv.dao.X5BaseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartnerDao extends X5BaseDao {
    private static final Logger log = LoggerFactory.getLogger(PartnerDao.class);

    public PartnerDao() {
        super();
    }

    public PartnerDao(DatabaseConfig databaseConfig) {
        super(databaseConfig);
    }

    public DbPartner getDbPartner(String partnerUuid){
        return DbPartnerQueryBuilder.defaultInstance(getJtvJdbcTemplate()).withUuid(partnerUuid).queryForObject();
    }

    /**
     * Returns the first site found for the partner.  If no site is found, returns null.
     * @param partnerUuid
     * @return
     */
    public DbSite getDbSite(String partnerUuid){
        DbPartner dbPartner = getDbPartner(partnerUuid);
        //There could be multiple sites, returning first
        List<DbSite> dbSiteList = DbSiteQueryBuilder.defaultInstance(getJtvJdbcTemplate()).withPartnerId(dbPartner.getPartnerId()).queryForList();
        if(dbSiteList == null || dbSiteList.isEmpty()){
            return null;
        }
        return dbSiteList.get(0);
    }

    /**
     * Returns the first associate found for the partner. If no associate is found, returns null.
     * @param partnerUuid
     * @return
     */
    public DbAssociate getDbAssociate(String partnerUuid){
        DbPartner dbPartner = getDbPartner(partnerUuid);
        List<DbAssociate> associateList = DbAssociateQueryBuilder.defaultInstance(getJtvJdbcTemplate()).withPartnerId(dbPartner.getPartnerId()).queryForList();
        if(associateList == null || associateList.isEmpty()){
            return null;
        }
        return associateList.get(0);
    }

    public void provisionEmployeeRolesAuto(Integer employeeId, Long userAccountId, String emailAddress, String phoneCountryCode, String phoneNumber, String phoneExtension) {
        String sql;
        Map<String, Object> params = new HashMap<>();
        
        params.put("employeeId", employeeId);
        params.put("userAccountId", userAccountId);
        params.put("emailAddress", emailAddress);
        params.put("phoneCountryCode", phoneCountryCode);
        params.put("phoneNumber", phoneNumber);
        params.put("phoneExtension", phoneExtension);
        
        sql = "declare\n" +
                "p_request partner_sec_emp_schema.pkg_provision_emp_roles.provisionEmpRolesAutoRequest;\n" +
                "begin\n" +
                "-- LOAD THE REQUEST RECORD\n" +
                "p_request.p_srv_plat_code := 'JTV_PARTNER_CENTRAL'; /* JTV_PARTNER_CENTRAL */\n" +
                "p_request.p_emp_id := :employeeId; /* acntv.acntv_sec_employee.emp_id */\n" +
                "p_request.p_employee_type_code := upper( 'PARTNER_SUCCESS_MANAGER' ); /* partner_sec_emp_schema.employee_type.code - PARTNER_SUCCESS_MANAGER , MERCHANDISING_ADMINISTRATOR , FINANCE_ADMINISTRATOR , COMPLIANCE_ADMINISTRATOR */\n" +
                "p_request.p_user_account_id := :userAccountId; /* entitlement_schema.user_account.user_account_id */\n" +
                "p_request.p_email_address := :emailAddress; /* an email address - if one already exists in address_schema.email_address.email_address it will select that record */\n" +
                "p_request.p_phone_country_code := :phoneCountryCode; /* if the country code, number, and extension already exist in address_schema.phone_number, it will select that record */\n" +
                "p_request.p_phone_number := :phoneNumber;\n" +
                "p_request.p_phone_extension := :phoneExtension;\n" +
                "-- CALL THE PACKAGE TO ADD THE PERMISSION\n" +
                "partner_sec_emp_schema.pkg_provision_emp_roles.provision_emp_roles_auto( p_request );\n" +
                "commit;\n" +
                "exception\n" +
                "when others then\n" +
                "dbms_output.put_line('Exception Caught ' || sqlerrm );\n" +
                "raise;\n" +
                "end;";


        update(sql, params);
    }
}
