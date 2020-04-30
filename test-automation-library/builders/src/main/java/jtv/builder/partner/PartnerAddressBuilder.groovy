package jtv.builder.partner

import com.jtv.test.db.composite.PartnerAddressCompositeDataBuilder
import com.jtv.test.db.entity.DbAddress
import com.jtv.test.db.entity.partner.DbPartner
import com.jtv.test.db.entity.partner.DbSite
import com.jtv.test.db.fixtures.partner.DbSiteDataBuilder
import com.jtv.test.db.fixtures.partner.DbSiteUsageDataBuilder
import com.jtv.test.db.query.partner.DbPartnerQueryBuilder
import com.jtv.test.db.query.partner.DbSiteQueryBuilder
import com.jtv.test.db.query.partner.DbSiteUsageTypeQueryBuilder
import com.jtv.test.utils.jdbc.JtvJdbcTemplate
import jtv.dao.DatabaseConfig
import jtv.dao.entity.partner.SiteUsageType
import jtv.dao.partner.payment.PartnerPaymentDao
import jtv.data.generator.DataGenerator
import jtv.exception.KeywordNotDefinedException

class PartnerAddressBuilder {

    private final PartnerPaymentDao partnerPaymentDao;
    protected final JtvJdbcTemplate jdbcTemplate;

    public PartnerAddressBuilder() {
        this.partnerPaymentDao = new PartnerPaymentDao();
        this.jdbcTemplate = partnerPaymentDao.getJtvJdbcTemplate();
    }

    public PartnerAddressBuilder(DatabaseConfig databaseConfig) {
        this.partnerPaymentDao = new PartnerPaymentDao(databaseConfig);
        this.jdbcTemplate = partnerPaymentDao.getJtvJdbcTemplate();
    }

    public DbAddress buildLocalAddress(){
       PartnerAddressCompositeDataBuilder partnerAddressCompositeDataBuilder =
               PartnerAddressCompositeDataBuilder.localValidInstance(jdbcTemplate)
                       .withAddressLine1(DataGenerator.AddressGenerator.generateAddressLineOne())
                       .withAddressLine2(DataGenerator.AddressGenerator.generateAddressLineTwo())
        .withCountryIsoAlpha3Code("USA")
        DbAddress dbAddress = partnerAddressCompositeDataBuilder.build()

        return dbAddress;
    }

    public DbAddress buildJtvHqAddress(){
       PartnerAddressCompositeDataBuilder partnerAddressCompositeDataBuilder =
               PartnerAddressCompositeDataBuilder.jtvHQInstance(jdbcTemplate)
        DbAddress dbAddress = partnerAddressCompositeDataBuilder.build()

        return dbAddress;
    }

    public DbAddress buildRandomAddress(){
        PartnerAddressCompositeDataBuilder partnerAddressCompositeDataBuilder =
                PartnerAddressCompositeDataBuilder.randomValidInstance(jdbcTemplate)
                        .withAddressLine1(DataGenerator.AddressGenerator.generateAddressLineOne())
                        .withAddressLine2(DataGenerator.AddressGenerator.generateAddressLineTwo())
        return partnerAddressCompositeDataBuilder.build()
    }

    public void buildPartnerSiteBillingUsageForPartner(String partnerUuid){
        // insert a partner site usage record for BILLING for the existing address
        DbPartner dbPartner = DbPartnerQueryBuilder.defaultInstance(jdbcTemplate).withUuid(partnerUuid).queryForObject()
        DbSite dbSite = DbSiteQueryBuilder.defaultInstance(jdbcTemplate).withPartnerId(dbPartner.getPartnerId()).queryForObject()
        DbSiteUsageDataBuilder.foreignKeyInstance(jdbcTemplate, DbSiteUsageTypeQueryBuilder.billingInstance(jdbcTemplate), dbSite).build()

    }

    public DbSite buildPartnerSiteForPartner(String partnerUuid, String usageTypeCode) throws KeywordNotDefinedException{
        // insert a partner site record and associate to provided usage type

        try{
            SiteUsageType.valueOf(usageTypeCode.toUpperCase())
        }catch(IllegalArgumentException exception){
               throw new KeywordNotDefinedException(usageTypeCode, "SiteUsageType: " + SiteUsageType.values())
        }

        DbPartner dbPartner = DbPartnerQueryBuilder.defaultInstance(jdbcTemplate).withUuid(partnerUuid).queryForObject()
        DbAddress dbAddress = buildRandomAddress()
        DbSite dbSite = DbSiteDataBuilder.foreignKeyInstance(jdbcTemplate, dbAddress, dbPartner).build()

        DbSiteUsageDataBuilder.foreignKeyInstance(jdbcTemplate,
                DbSiteUsageTypeQueryBuilder.defaultInstance(jdbcTemplate).withCode(usageTypeCode.toUpperCase()).queryForObject(),
                dbSite).build()

        return dbSite;
    }

}
