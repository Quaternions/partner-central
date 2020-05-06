package jtv.builder.user

import com.jtv.test.db.entity.entitlement.DbUserAccount
import com.jtv.test.db.entity.entitlement.DbUserAccountIdpXref
import com.jtv.test.db.query.entitlement.DbUserAccountIdpXrefQueryBuilder
import com.jtv.test.db.query.entitlement.DbUserAccountQueryBuilder
import jtv.data.generator.DataGenerator
import jtv.dao.entity.user.DatabaseUserAccount
import jtv.dao.user.UserAccountDao
import jtv.data.generator.DataGenerator
import jtv.runnable.ContextRunnable

class UserAccountRunnableBuilder extends ContextRunnable {
    String keyword
    DatabaseUserAccount finalUserAccount
    DbUserAccount finalDbUserAccount
    String keycloakUuid

    UserAccountRunnableBuilder(String keyword) {
        super()
        this.keyword = keyword
    }

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
}
