package jtv.builder.partner.utils;

import com.jtv.test.db.composite.AssociatePermissionCompositeDataBuilder;
import com.jtv.test.db.entity.DbAcntvSecEmployee;
import com.jtv.test.db.entity.entitlement.DbSysFeatCompOper;
import com.jtv.test.db.entity.entitlement.DbSysFeature;
import com.jtv.test.db.entity.entitlement.DbUserAccount;
import com.jtv.test.db.entity.entitlement.DbUserAccountIdpXref;
import com.jtv.test.db.entity.partner.DbAssociateAccount;
import com.jtv.test.db.entity.partner.DbPartner;
import com.jtv.test.db.query.entitlement.DbSysFeatCompOperQueryBuilder;
import com.jtv.test.db.query.entitlement.DbSysFeatureQueryBuilder;
import com.jtv.test.db.query.entitlement.DbUserAccountIdpXrefQueryBuilder;
import com.jtv.test.db.query.entitlement.DbUserAccountQueryBuilder;
import com.jtv.test.db.query.partner.DbAssociateAccountQueryBuilder;
import jtv.builder.employee.EmployeeBuilder;
import jtv.builder.partner.PartnerBuilder;
import jtv.builder.partner.entity.PartnerCentralEmployee;
import jtv.builder.partner.entity.PartnerCentralUser;
import jtv.dao.DatabaseConfig;
import jtv.dao.entity.partner.PartnerStatus;
import jtv.dao.entity.partner.SiteUsageType;
import jtv.dao.entity.user.DatabaseUserAccount;
import jtv.dao.keycloak.KeycloakDao;
import jtv.dao.partner.PartnerDao;
import jtv.dao.user.UserAccountDao;
import jtv.data.generator.DataGenerator;
import jtv.exception.KeywordNotDefinedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class PartnerUtils {
    private static final Logger log = LoggerFactory.getLogger(PartnerUtils.class);

    private final UserAccountDao    userAccountDao;
    private final KeycloakDao       keycloakDao;
    private final PartnerBuilder    partnerBuilder;
    private final EmployeeBuilder   employeeBuilder;
    private final PartnerDao        partnerDao;

    public PartnerUtils() {
        this.userAccountDao     = new UserAccountDao();
        this.keycloakDao        = new KeycloakDao();
        this.partnerBuilder     = new PartnerBuilder();
        this.employeeBuilder    = new EmployeeBuilder();
        this.partnerDao         = new PartnerDao();
    }
    
    public PartnerUtils(DatabaseConfig x5DatabaseConfig, DatabaseConfig keycloakDatabaseConfig) {
        this.userAccountDao     = new UserAccountDao(x5DatabaseConfig);
        this.keycloakDao        = new KeycloakDao(keycloakDatabaseConfig);
        this.partnerBuilder     = new PartnerBuilder(x5DatabaseConfig);
        this.employeeBuilder    = new EmployeeBuilder(x5DatabaseConfig);
        this.partnerDao         = new PartnerDao(x5DatabaseConfig);
    }

    public PartnerCentralUser buildPartnerCentralPrimaryAccountManager() {
        return buildPartnerCentralUser("the primary account manager");
    }
    
    public PartnerCentralUser buildPartnerCentralAssociate() {
        return buildPartnerCentralUser("an associate");
    }
    
    private PartnerCentralUser buildPartnerCentralUser(String associateType) {
        String firstName = DataGenerator.NameGenerator.generateFirstName();
        String lastName = DataGenerator.NameGenerator.generateLastName();
        String email = DataGenerator.NameGenerator.generateEmailAddressGivenName(firstName, lastName);
        String keycloakUuid = UUID.randomUUID().toString();

        DbUserAccount finalDbUserAccount = createUserAccountAndKeycloakUserInPartnerGroup(firstName, lastName, email, keycloakUuid);

        DbPartner partner;
        Long siteId;

        try {
            partner = partnerBuilder.buildPartner(PartnerStatus.REGISTRATION_COMPLETED.name());
            siteId = partnerBuilder.buildPartnerSite(SiteUsageType.BUSINESS.name(), partner);
            partnerBuilder.createPartnerAssociationForUser(partner, finalDbUserAccount, associateType);

            //Find associate by user
            DbAssociateAccount dbAssociateAccount = DbAssociateAccountQueryBuilder.defaultInstance(userAccountDao.getJtvJdbcTemplate())
                    .withUserAccountId(finalDbUserAccount.getUserAccountId()).queryForObject();

            //DbSysFeature dbSysFeature = DbSysFeatureQueryBuilder.defaultInstance(userAccountDao.getJtvJdbcTemplate()).withCode(accessType).queryForObject();
            //DbSysFeatCompOper dbSysFeatCompOper = DbSysFeatCompOperQueryBuilder.defaultInstance(userAccountDao.getJtvJdbcTemplate()).withCode(accessLevel).queryForObject();

            //AssociatePermissionCompositeDataBuilder permissionCompositeDataBuilder = AssociatePermissionCompositeDataBuilder
            //        .defaultJTVPCInstance(userAccountDao.getJtvJdbcTemplate(), dbAssociateAccount, dbSysFeature, dbSysFeatCompOper).build();

            PartnerCentralUser partnerCentralUser = new PartnerCentralUser(email, "P@ssw0rd");

            log.info("Created user " + partnerCentralUser.getEmailAddress());

            return partnerCentralUser;

        } catch (KeywordNotDefinedException e) {
            log.error("Could not build partner", e);
            return null;
        }

    }
    
    public PartnerCentralEmployee buildPartnerCentralEmployee() {
        DbAcntvSecEmployee employee = employeeBuilder.createRandomEmployee();

        String email = DataGenerator.NameGenerator.generateEmailAddressGivenName(employee.getFirstname(), employee.getLastname());
        String keycloakUuid = UUID.randomUUID().toString();
        
        DbUserAccount userAccount = createUserAccountAndKeycloakUserInPartnerGroup(employee.getFirstname(), employee.getLastname(), email, keycloakUuid);
        
        keycloakDao.addUserToPartnerEmployeeGroup(keycloakUuid);
        
        partnerDao.provisionEmployeeRolesAuto(employee.getEmpId(), userAccount.getUserAccountId(), email, "1", DataGenerator.PhoneGenerator.generatePhoneNumber(), null);
        
        return new PartnerCentralEmployee(email, "P@ssw0rd", employee.getUserId());
    }

    
    private DbUserAccount createUserAccountAndKeycloakUserInPartnerGroup(String firstName, String lastName, String email, String keycloakUuid) {
        userAccountDao.createNewUserAccount(firstName, lastName, email, keycloakUuid);
        //DatabaseUserAccount databaseUserAccount = userAccountDao.getUserAccountByKeycloakUuid(keycloakUuid);

        DbUserAccountIdpXref dbUserAccountIdpXref = DbUserAccountIdpXrefQueryBuilder.defaultInstance(userAccountDao.getJtvJdbcTemplate())
                .withIdpUuid(keycloakUuid).queryForObject();
        DbUserAccount finalDbUserAccount = DbUserAccountQueryBuilder.defaultInstance(userAccountDao.getJtvJdbcTemplate())
                .withUserAccountId(dbUserAccountIdpXref.getUserAccountId()).queryForObject();

        keycloakDao.createKeycloakUserInPartnerGroup(email, firstName, lastName, keycloakUuid);
        keycloakDao.addUserToCustomerGroup(keycloakUuid);
        
        return finalDbUserAccount;
    }
}
