package jtv.dao.user;

import com.jtv.test.db.entity.entitlement.DbUserAccount;
import com.jtv.test.db.entity.entitlement.DbUserAccountIdpXref;
import com.jtv.test.db.query.entitlement.DbUserAccountIdpXrefQueryBuilder;
import com.jtv.test.db.query.entitlement.DbUserAccountQueryBuilder;
import jtv.dao.DatabaseConfig;
import jtv.dao.entity.user.DatabaseUserAccount;
import jtv.dao.X5BaseDao;

import java.util.HashMap;
import java.util.Map;

public class UserAccountDao extends X5BaseDao {

    public UserAccountDao() {
        super();
    }
    
    public UserAccountDao(DatabaseConfig databaseConfig) {
        super(databaseConfig);
    }

    private void insertUserAccount(String firstName, String lastName, String username, String keycloakUuid) {
        String sql;
        Map<String, String> params = new HashMap<>();
        params.put("firstName", firstName);
        params.put("lastName", lastName);
        params.put("username", username);
        params.put("kcUuid", keycloakUuid);

//        sql =   "insert into ENTITLEMENT_SCHEMA.user_account(FIRST_NAME, LAST_NAME, USER_NAME)\n" +
//                "values(:firstName, :lastName, :username)";

        sql =    "DECLARE\n"
                +"  v_acct_id entitlement_schema.user_account.user_account_id%type;\n"
                +"  v_uuid entitlement_schema.user_account.uuid%type;\n"
                +"BEGIN\n"
                +"INSERT INTO entitlement_schema.user_account\n"
                +"  (first_name, last_name, user_name)\n"
                +"  VALUES (:firstName, :lastName,:username)\n"
                +"RETURNING user_account_id, uuid into v_acct_id, v_uuid;\n"
                +"insert into entitlement_schema.user_account_idp_xref (user_account_id, idp_uuid, uuid_source)\n"
                +"values(v_acct_id, :kcUuid, 'foo');\n"
                +"END;\n";

        update(sql, params);
    }

    public void createNewUserAccount(String firstName, String lastName, String username, String keycloakUuid) {
        insertUserAccount(firstName, lastName, username, keycloakUuid);
    }

    public DatabaseUserAccount getUserAccountByJtvUuid(String jtUuid) {
        String sql;
        Map<String, String> params = new HashMap<>();

        params.put("uuid", jtUuid);

        sql = "select * from ENTITLEMENT_SCHEMA.USER_ACCOUNT where uuid = :uuid";

        return queryForObject(sql, params, DatabaseUserAccount.getRowMapperInstance());
    }

    public DatabaseUserAccount getUserAccountByKeycloakUuid(String kcUuid) {
        String sql;
        Map<String, String> params = new HashMap<>();

        params.put("uuid", kcUuid);

        sql =    "select\n"
                +"    u.user_account_id,\n"
                +"    u.uuid as uuid,\n"
                +"    u.first_name,\n"
                +"    u.last_name,\n"
                +"    u.user_name\n"
                +"from entitlement_schema.user_account u\n"
                +"    join entitlement_schema.user_account_idp_xref xref on (u.user_account_id = xref.user_account_id)\n"
                +"where\n" +
                "    xref.idp_uuid = :uuid";

        return queryForObject(sql, params, DatabaseUserAccount.getRowMapperInstance());
    }

    public DbUserAccount getDbUserAccountByKeycloakUuid(String kcUuid) {
        DbUserAccountIdpXref dbUserAccountIdpXref = DbUserAccountIdpXrefQueryBuilder.defaultInstance(getJtvJdbcTemplate()).withIdpUuid(kcUuid).queryForObject();
        return DbUserAccountQueryBuilder.defaultInstance(getJtvJdbcTemplate()).withUserAccountId(dbUserAccountIdpXref.getUserAccountId()).queryForObject();
    }

}
