package jtv.builder.user;

import com.jtv.test.db.entity.entitlement.DbUserAccount;
import com.jtv.test.db.fixtures.entitlement.DbUserAccountDataBuilder;
import com.jtv.test.db.fixtures.entitlement.DbUserAccountIdpXrefDataBuilder;
import com.jtv.test.utils.jdbc.JtvJdbcTemplate;
import jtv.dao.user.UserAccountDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserAccountBuilder {
    private final UserAccountDao userAccountDao;
    private static final Logger log = LoggerFactory.getLogger(UserAccountBuilder.class);
    protected JtvJdbcTemplate jdbcTemplate;

    public UserAccountBuilder() {
        userAccountDao = new UserAccountDao();
        setJdbcTemplate(userAccountDao.getJtvJdbcTemplate());
    }

    public UserAccountBuilder(UserAccountDao userAccountDao) {
        this.userAccountDao = userAccountDao;
        this.setJdbcTemplate(userAccountDao.getJtvJdbcTemplate());
    }

    public JtvJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JtvJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public DbUserAccount createUserAccount(String first, String last, String username, String idpUuid) {
        DbUserAccount user = DbUserAccountDataBuilder.defaultInstance(getJdbcTemplate())
                .withFirstName(first)
                .withLastName(last)
                .withUserName(username)
                .build();

        DbUserAccountIdpXrefDataBuilder.foreignKeyInstance(getJdbcTemplate(), user)
                .withIdpUuid(idpUuid)
                .build();

        log.info("Created the following user in DB: " + user.toString());
        return user;
    }
}

/*
    void run() {
        UserAccountDao userAccountDao = new UserAccountDao()
        String uuid = UUID.randomUUID().toString()
        keycloakUuid = uuid
        switch(keyword.toLowerCase()) {
            case ["valid"]:
                userAccountDao.createNewUserAccount(DataGenerator.NameGenerator.generateFirstName(), DataGenerator.NameGenerator.generateLastName(), "user-${UUID.randomUUID().toString().substring(0, 13)}@test.com", uuid)
                finalUserAccount = userAccountDao.getUserAccountByKeycloakUuid(uuid)

                DbUserAccountIdpXref dbUserAccountIdpXref = DbUserAccountIdpXrefQueryBuilder.defaultInstance(userAccountDao.getJtvJdbcTemplate())
                .withIdpUuid(uuid).queryForObject()
                finalDbUserAccount = DbUserAccountQueryBuilder.defaultInstance(userAccountDao.getJtvJdbcTemplate())
                        .withUserAccountId(dbUserAccountIdpXref.userAccountId).queryForObject()
                break
        }
    }
 */


