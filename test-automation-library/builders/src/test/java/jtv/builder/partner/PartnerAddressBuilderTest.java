package jtv.builder.partner;

import com.jtv.test.db.entity.DbAddress;
import com.jtv.test.db.entity.partner.DbPartner;
import com.jtv.test.db.entity.partner.DbSite;
import com.jtv.test.db.entity.partner.DbSiteUsage;
import com.jtv.test.db.query.DbAddressQueryBuilder;
import com.jtv.test.db.query.partner.DbSiteQueryBuilder;
import com.jtv.test.db.query.partner.DbSiteUsageQueryBuilder;
import com.jtv.test.db.query.partner.DbSiteUsageTypeQueryBuilder;
import com.jtv.test.utils.jdbc.JtvJdbcTemplate;
import jtv.config.DatabaseProperties;
import jtv.dao.DatabaseConfig;
import jtv.dao.entity.partner.PartnerStatus;
import jtv.dao.entity.partner.SiteUsageType;
import jtv.dao.partner.PartnerDao;
import jtv.exception.KeywordNotDefinedException;
import org.aeonbits.owner.ConfigCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class PartnerAddressBuilderTest {
    private static final Logger log = LoggerFactory.getLogger(PartnerAddressBuilderTest.class);

    private PartnerDao partnerDao;
    private JtvJdbcTemplate jdbcTemplate;

    public PartnerAddressBuilderTest(){
        this.partnerDao = new PartnerDao();
        this.jdbcTemplate = partnerDao.getJtvJdbcTemplate();
    }

    @Test
    public void testDatabaseConfigConstructor() {
        DatabaseProperties databaseProperties = ConfigCache.getOrCreate(DatabaseProperties.class);
        DatabaseConfig databaseConfig = new DatabaseConfig(databaseProperties.getX5JdbcUrl(), databaseProperties.getX5Username(), databaseProperties.getX5Password());

        DbAddress dbAddress = new PartnerAddressBuilder(databaseConfig).buildRandomAddress();

        Assert.assertNotNull(dbAddress);
    }


    @Test
    public void testRandomAddress(){
        DbAddress dbAddress = new PartnerAddressBuilder().buildRandomAddress();
        verifyRandomAddress(dbAddress);
    }

    @Test
    public void testJtvHqAddress(){
        DbAddress dbAddress = new PartnerAddressBuilder().buildJtvHqAddress();
        Assert.assertNotNull(dbAddress);
        Assert.assertEquals(dbAddress.getAddressLine1(), "9600 Parkside Dr.");
        Assert.assertNull(dbAddress.getAddressLine2());
        Assert.assertEquals(dbAddress.getCity(), "Knoxville");
        Assert.assertEquals(dbAddress.getCountryId(), (Integer) 1);
        Assert.assertEquals(dbAddress.getPostalCode(), "37922");

    }
    @Test
    public void testLocalAddress(){
        DbAddress dbAddress = new PartnerAddressBuilder().buildLocalAddress();
        verifyRandomAddress(dbAddress);

    }

    private void verifyRandomAddress(DbAddress dbAddress) {
        Assert.assertNotNull(dbAddress);
        Assert.assertNotNull(dbAddress.getAddressLine1());
        Assert.assertNotNull(dbAddress.getCity());
        Assert.assertNotNull(dbAddress.getPostalCode());
        Assert.assertNotNull(dbAddress.getCountryId());
        Assert.assertNotNull(dbAddress.getStateProvinceId());
        Assert.assertNotNull(dbAddress.getUuid());
    }

    @Test
    public void testBuildPartnerSiteBillingUsageForPartner() throws KeywordNotDefinedException {
        PartnerBuilder pb = new PartnerBuilder();
        DbPartner dbPartner = pb.buildPartner(PartnerStatus.ACTIVE.name());
        pb.createPartnerSiteWithUsage(dbPartner, "BUSINESS");

        new PartnerAddressBuilder().buildPartnerSiteBillingUsageForPartner(dbPartner.getUuid());

        DbSite dbSite = DbSiteQueryBuilder.defaultInstance(jdbcTemplate).withPartnerId(dbPartner.getPartnerId()).queryForObject();
        Assert.assertNotNull(dbSite);
        List<DbSiteUsage> dbSiteUsageList = DbSiteUsageQueryBuilder.defaultInstance(jdbcTemplate).withSiteId(dbSite.getSiteId()).queryForList();
        for (DbSiteUsage dbSiteUsage : dbSiteUsageList){
            if(dbSiteUsage.getSiteUsageTypeId().equals(DbSiteUsageTypeQueryBuilder
                    .defaultInstance(jdbcTemplate).withCode("BILLING").queryForObject().getSiteUsageTypeId())){
                return;
            }
        }
        Assert.fail("Didn't find a BILLING site usage.");

    }

    @Test
    public void testBuildPartnerSiteForPartner() throws KeywordNotDefinedException{
        String usageTypeCode = "INVALID";

        PartnerBuilder pb = new PartnerBuilder();
        DbPartner dbPartner = pb.buildPartner(PartnerStatus.ACTIVE.name());

        //Method under test
        try {
            DbSite dbSite = new PartnerAddressBuilder().buildPartnerSiteForPartner(dbPartner.getUuid(), usageTypeCode);
        }catch(KeywordNotDefinedException keywordException){
            return;
        }
        Assert.fail("Expected a KeywordNotDefinedException.");

    }

    @Test
    public void testBuildPartnerSiteForPartnerValid() throws KeywordNotDefinedException{
        for(SiteUsageType usageType:  SiteUsageType.values()) {
            String usageTypeCode = usageType.name();
            log.info(String.format("***********Testing buildPartnerSiteForPartner for valid usage type code: [%s].********\n\n", usageTypeCode));
            buildPartnerSiteAndVerify(usageTypeCode);
        }
    }

    private void buildPartnerSiteAndVerify(String usageTypeCode) throws KeywordNotDefinedException {
        PartnerBuilder pb = new PartnerBuilder();
        DbPartner dbPartner = pb.buildPartner(PartnerStatus.ACTIVE.name());

        //Method under test
        DbSite dbSite = new PartnerAddressBuilder().buildPartnerSiteForPartner(dbPartner.getUuid(), usageTypeCode);

        //Verification
        Assert.assertNotNull(dbSite);
        Assert.assertNotNull(dbSite.getAddressId());

        DbAddress dbAddress = DbAddressQueryBuilder.defaultInstance(jdbcTemplate).withAddressId(dbSite.getAddressId()).queryForObject();
        verifyRandomAddress(dbAddress);

        DbSiteUsage dbSiteUsage = DbSiteUsageQueryBuilder.defaultInstance(jdbcTemplate).withSiteId(dbSite.getSiteId()).queryForObject();
        Assert.assertNotNull(dbSiteUsage);
        Assert.assertEquals(dbSiteUsage.getSiteUsageTypeId(),
                DbSiteUsageTypeQueryBuilder.defaultInstance(jdbcTemplate).withCode(usageTypeCode).queryForObject().getSiteUsageTypeId());
    }

}
