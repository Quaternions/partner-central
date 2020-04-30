package jtv.glue.stepdefinitions.apigateway.partner;

import com.jtv.test.db.composite.PartnerDepositAccountCompositeDataBuilder;
import com.jtv.test.db.composite.PartnerPayAccountCompositeDataBuilder;
import com.jtv.test.db.entity.DbCreditCard;
import com.jtv.test.db.composite.AssociatePermissionCompositeDataBuilder;
import com.jtv.test.db.entity.entitlement.DbSysFeatCompOper;
import com.jtv.test.db.entity.entitlement.DbSysFeature;
import com.jtv.test.db.entity.entitlement.DbUserAccount;
import com.jtv.test.db.entity.partner.DbAssociate;
import com.jtv.test.db.entity.partner.DbAssociateAccount;
import com.jtv.test.db.entity.partner.DbPartner;
import com.jtv.test.db.entity.partner.payment.DbPayAccntConfirm;
import com.jtv.test.db.entity.partner.payment.DbPayAccount;
import com.jtv.test.db.entity.partner.payment.DbPayAccountAssocDevice;
import com.jtv.test.db.fixtures.DbCreditCardDataBuilder;
import com.jtv.test.db.fixtures.partner.payment.DbPayAccntConfirmDataBuilder;
import com.jtv.test.db.query.entitlement.DbSysFeatCompOperQueryBuilder;
import com.jtv.test.db.query.entitlement.DbSysFeatureQueryBuilder;
import com.jtv.test.db.query.partner.DbAssociateAccountQueryBuilder;
import com.jtv.test.db.query.partner.DbAssociateQueryBuilder;
import com.jtv.test.db.query.partner.DbPartnerQueryBuilder;
import com.jtv.test.db.query.partner.payment.DbPayAccountActionTypeQueryBuilder;
import com.jtv.test.db.query.partner.payment.DbPayAccountAssocDeviceQueryBuilder;
import com.jtv.test.db.query.partner.payment.DbPayAccountStatusQueryBuilder;
import com.jtv.test.db.query.partner.payment.DbPayConfirmStateQueryBuilder;
import com.jtv.test.utils.jdbc.JtvJdbcTemplate;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import jtv.assertion.Assertion;
import jtv.assertion.utils.AssertionUtilityFunctions;
import jtv.builder.partner.PartnerBuilder;
import jtv.builder.partner.payment.PartnerPaymentBuilder;
import jtv.builder.user.UserAccountRunnableBuilder;
import jtv.cde.api.gateway.partner.payment.account.UsageTypeKeyword;
import jtv.cde.api.gateway.partner.payment.account.asserter.PartnerCreditCardAsserter;
import jtv.cde.api.gateway.partner.payment.account.asserter.PayAccountAsserter;
import jtv.builder.partner.payment.util.CreditCardUtil;
import jtv.context.apigateway.ApiGatewayPartnerTestContext;
import jtv.context.apigateway.ApiGatewayTestContext;
import jtv.dao.entity.partner.AssociateType;
import jtv.dao.entity.partner.payment.PayAccountStatus;
import jtv.dao.entity.partner.payment.UsageType;
import jtv.dao.partner.PartnerDao;
import jtv.dao.partner.payment.PartnerPaymentDao;
import jtv.data.generator.DataGenerator;
import jtv.exception.KeywordNotDefinedException;
import jtv.keycloak.utility.KeycloakUtilityFunctions;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

public class CommonPartnerStepDefinitions extends BasePartnerGlue {
    private static final Logger log = LoggerFactory.getLogger(CommonPartnerStepDefinitions.class);

    private KeycloakUtilityFunctions keycloakUtilityFunctions = new KeycloakUtilityFunctions();

    private final PartnerBuilder partnerBuilder = new PartnerBuilder();
    private PartnerDao partnerDao = new PartnerDao();
    private PartnerPaymentDao partnerPaymentDao = new PartnerPaymentDao();
    private JtvJdbcTemplate jdbcTemplate = new JtvJdbcTemplate(partnerDao.getDataSource());

    private SimpleDateFormat ccDateFormat = new SimpleDateFormat("MM/yyyy");

    public CommonPartnerStepDefinitions(ApiGatewayTestContext apiGatewayTestContext, ApiGatewayPartnerTestContext apiGatewayPartnerTestContext) {
        super(apiGatewayTestContext, apiGatewayPartnerTestContext);
    }

    @Given("^the user is granted (.*?) access to (.*?) in core$")
    public void userIsGrantedCoreAccess(String accessLevel, String accessType) throws IOException {
        DbUserAccount dbUserAccount= getPartnerContext().getDbUserAccount();
        DbAssociate dbAssociate = null;
        DbAssociateAccount dbAssociateAccount = null;
        if(dbUserAccount != null){
            //Find associate by user
            dbAssociateAccount = DbAssociateAccountQueryBuilder.defaultInstance(jdbcTemplate)
                    .withUserAccountId(dbUserAccount.getUserAccountId()).queryForObject();

        }else {
            //Find associate by partner and name
            if(getPartnerContext() == null || getPartnerContext().getPartnerUuid() == null){
                log.warn("No partner is set, so core permissions cannot be set.");
                return;
            }
            String partnerUuid = getPartnerContext().getPartnerUuid();

            DbPartner dbPartner = DbPartnerQueryBuilder.defaultInstance(jdbcTemplate).withUuid(partnerUuid).queryForObject();
            dbAssociate = DbAssociateQueryBuilder.defaultInstance(jdbcTemplate)
                    .withPartnerId(dbPartner.getPartnerId())
                    .withFirstName(keycloakUtilityFunctions.getGivenNameFromToken(getAccessToken()))
                    .withLastName(keycloakUtilityFunctions.getFamilyNameFromToken(getAccessToken()))
                    .queryForObject();

            dbAssociateAccount = DbAssociateAccountQueryBuilder.defaultInstance(jdbcTemplate)
                    .withAssociateId(dbAssociate.getAssociateId())
                    .queryForObject();
        }


        DbSysFeature dbSysFeature = DbSysFeatureQueryBuilder.defaultInstance(jdbcTemplate).withCode(accessType).queryForObject();
        DbSysFeatCompOper dbSysFeatCompOper = DbSysFeatCompOperQueryBuilder.defaultInstance(jdbcTemplate).withCode(accessLevel).queryForObject();

        AssociatePermissionCompositeDataBuilder permissionCompositeDataBuilder = AssociatePermissionCompositeDataBuilder
                .defaultJTVPCInstance(jdbcTemplate, dbAssociateAccount, dbSysFeature, dbSysFeatCompOper).build();
    }


    @Then("^the expiration date (.*) updated$")
    public void expirationDateUpdated(String expUpdatedKeyword) throws KeywordNotDefinedException{
        DbCreditCard originalExistingCreditCard = getPartnerContext().getOriginalExistingCreditCard();
        String originalExpirationDate = ccDateFormat.format(originalExistingCreditCard.getExpDate());

        PartnerCreditCardAsserter.assertCreditCardDateGivenMopId(originalExistingCreditCard.getMopId(), originalExpirationDate, expUpdatedKeyword);

    }


    @Given("^has (.*) billing partner payment method with a (.*) card$")
    public void createBillingPayment(String statusKeyword, String cardTypeKeyword) throws KeywordNotDefinedException {
        createPaymentAccount(statusKeyword, UsageType.BILLING.name(), cardTypeKeyword);
    }

    @Given("^has (.*) partner payment method of type (.*) with a (.*) card$")
    public void createPaymentAccount(String statusKeyword, String usageTypeKeyword, String cardTypeKeyword) throws KeywordNotDefinedException{

        DbPartner dbPartner = partnerDao.getDbPartner(getPartnerContext().getPartnerUuid());
            PartnerPayAccountCompositeDataBuilder builder = PartnerPaymentBuilder.getPartnerPayAccountCompositeDataBuilder();
            builder.withDbPartner(dbPartner);
            switch(statusKeyword){
                case "no":
                    //Don't create any payments
                    log.info(String.format("%s pay account created.\n", statusKeyword));
                    builder = null;
                    return;
                case "a valid":
                case "an active":
                case "a visible":
                    builder.withPayAccountStatusCode(PayAccountStatus.ACTIVE.name());
                    break;
                case "an inactive":
                case "an invisible":
                case "a disabled":
                    builder.withPayAccountStatusCode(PayAccountStatus.INACTIVE.name());
                    break;
                case "a purged":
                    builder.withPayAccountStatusCode(PayAccountStatus.PURGED.name());
                    break;
                case "an unverified":
                    boolean verified = false;
                    builder.withIsVerified(verified);
                    break;
                case "an expired":
                    //Create a new expired credit card
                    String cardNumber = DataGenerator.MopGenerator.CreditCardGenerator.generateCreditCardNumber(CreditCardUtil.normalizeCardType(cardTypeKeyword.toUpperCase()));
                    Date currentDate = new Date();

                    Date expiredDate = new Date(currentDate.getTime() - 30*24*60*60*1000l);

                    DbCreditCard dbCreditCard = DbCreditCardDataBuilder.defaultDiscoverInstance(jdbcTemplate)
                            .withExpDate(expiredDate).withCardNumber(cardNumber)
                            .withCardType(CreditCardUtil.normalizeCardType(cardTypeKeyword.toUpperCase())).withLast4Display(cardNumber.substring(12,16)).build();
                    builder.withDbCreditCard(dbCreditCard);
                    break;
                default:
                    throw new KeywordNotDefinedException(statusKeyword, "String");
            }

            switch(usageTypeKeyword.toLowerCase()){
                case "billing":
                    builder.withAccountUsageTypeCode(UsageType.BILLING.name());
                    break;
                case "deposit":
                    builder.withAccountUsageTypeCode(UsageType.DEPOSIT.name());
                    break;
                default:
                    throw new KeywordNotDefinedException("usageTypeKeyword", "String");
            }

            builder.withCardTypeCode(CreditCardUtil.normalizeCardType(cardTypeKeyword.toUpperCase())).build();

            //Push the card onto the list
            getPartnerContext().getDbPayAccounts().add(builder.getDbPayAccount());

            getPartnerContext().getDbCardPayAccounts().add(builder.getDbPayAccount());
            getPartnerContext().getDbSites().add(builder.getDbSite());
            getPartnerContext().getDbPayAccountMops().add(builder.getDbPayAccountMop());
            getPartnerContext().getDbPayAccountMopCards().add(builder.getDbPayAccountMopCard());
            getPartnerContext().getDbCreditCards().add(builder.getDbCreditCard());

            //Push the payment account onto the original placeholder
            getPartnerContext().setOriginalExistingDbPayAccount(builder.getDbPayAccount());
            getPartnerContext().setDbSiteForOriginalExistingPayAccount(builder.getDbSite());
            getPartnerContext().setDbPayAccountMopForOriginalExistingPayAccount(builder.getDbPayAccountMop());
            getPartnerContext().setDbPayAccountMopCardForOriginalExistingPayAccount(builder.getDbPayAccountMopCard());
            getPartnerContext().setOriginalExistingCreditCard(builder.getDbCreditCard());

            //Set current here so it will be populated at all times.  It will change if a new account is created.
            getPartnerContext().setCurrentDbPayAccount(builder.getDbPayAccount());
            log.info(String.format("%s %s pay account created [%s] with mop id [%d] and db encrypted card number [%s].\n"
                    , statusKeyword, cardTypeKeyword, builder.getDbPayAccount().getUuid(), builder.getDbCreditCard().getMopId(), builder.getDbCreditCard().getCardNumber()));
//        }else{
//            if(getPartnerContext().getOriginalExistingCreditCard() == null){
//                //GAR TODO set site, paMop, paMopCard, and origCC here if necessary
//            }
//        }
    }



        @Given("^the billing payment account is changed to not default$")
    public void billingPaymentAccountNotDefault(){
        if(getPartnerContext().getCurrentDbPayAccount() != null){
            partnerPaymentDao.removeBillingPaymentAccountDefault(getPartnerContext().getCurrentDbPayAccount().getPartnerId(), getPartnerContext().getCurrentDbPayAccount().getPayAccountId());
        }
    }

    @Given("^the billing payment account is changed to inactive$")
    public void billingPaymentAccountDeactivated(){
        if(getPartnerContext().getCurrentDbPayAccount() != null){
            partnerPaymentDao.deactivateBillingPaymentAccount(getPartnerContext().getCurrentDbPayAccount().getPayAccountId());
        }
    }

    @Then("^the (.*) card (.*) set as the default billing payment account$")
    public void cardIsDefaultBillingPayAccount(String cardKeyword, String defaultKeyword) throws KeywordNotDefinedException{
        DbPayAccount dbPayAccount = null;
        if(cardKeyword.equalsIgnoreCase("current")){
            if(getPartnerContext().getCurrentDbPayAccount() == null){
                Assert.fail("No pay account is set as current.");
            }
            dbPayAccount = getPartnerContext().getCurrentDbPayAccount();
        }else if (cardKeyword.equalsIgnoreCase("previous")){
            if(getPartnerContext().getOriginalExistingDbPayAccount() == null){
                Assert.fail("No pay account is set as previous.");
            }
            dbPayAccount = getPartnerContext().getOriginalExistingDbPayAccount();

        }else{
            throw new KeywordNotDefinedException(cardKeyword, String.format("cardKeyword [%s] not found.", cardKeyword));
        }

        boolean assertIsDefault = true;
        if(defaultKeyword.toLowerCase().contains("not")) {
            assertIsDefault = false;
        }
        PayAccountAsserter paa = new PayAccountAsserter();
        paa.assertPartnerPayPrefBillingBillingCardDefault(dbPayAccount, assertIsDefault);
    }

    @Given("^the user successfully confirms the current deposit account$")
    public void depositAccountConfirmation(){
        DbPayAccount dbDepositPayAccount = getPartnerContext().getCurrentDepositAccount();
        DbPartner dbPartner = DbPartnerQueryBuilder.defaultInstance(jdbcTemplate).withUuid(getPartnerContext().getPartnerUuid()).queryForObject();
        DbAssociate dbAssociate = DbAssociateQueryBuilder.defaultInstance(jdbcTemplate).withPartnerId(dbPartner.getPartnerId()).queryForObject();
        DbPayAccntConfirm dbPayAccntConfirm = DbPayAccntConfirmDataBuilder.foreignKeyInstance(jdbcTemplate, dbDepositPayAccount, dbAssociate, DbPayConfirmStateQueryBuilder.succeededInstance(jdbcTemplate)).build();
        log.info(String.format("Successful deposit account confirmation for account [%s].", dbDepositPayAccount.getUuid()));
    }

    @Given("^has (.*) partner deposit account of type (.*) with a (.*) type$")
    public void createDepositPaymentAccount(String statusKeyword, String usageTypeKeyword, String cardTypeKeyword) throws KeywordNotDefinedException{

        DbPartner dbPartner = partnerDao.getDbPartner(getPartnerContext().getPartnerUuid());
        PartnerDepositAccountCompositeDataBuilder builder = PartnerPaymentBuilder.getPartnerDepositAccountCompositeDataBuilder();
        builder.withDbPartner(dbPartner);

        switch(statusKeyword){
            case "no":
                //Don't create any payments
                log.info(String.format("%s pay account created.\n", statusKeyword));
                builder = null;
                return;
            case "a valid":
            case "an active":
            case "a visible":
                builder.withPayAccountStatusCode(PayAccountStatus.ACTIVE.name());
                break;
            case "an inactive":
            case "an invisible":
            case "a disabled":
                builder.withPayAccountStatusCode(PayAccountStatus.INACTIVE.name());
                break;
            case "a purged":
                builder.withPayAccountStatusCode(PayAccountStatus.PURGED.name());
                break;
            default:
                throw new KeywordNotDefinedException(statusKeyword, "String");
        }

        switch(usageTypeKeyword.toLowerCase()){
            case "billing":
                builder.withAccountUsageTypeCode(UsageType.BILLING.name());
                break;
            case "deposit":
                builder.withAccountUsageTypeCode(UsageType.DEPOSIT.name());
                break;
            default:
                throw new KeywordNotDefinedException("usageTypeKeyword", "String");
        }

        builder.build();

        //Push the card onto the list
        getPartnerContext().getDbPayAccounts().add(builder.getDbPayAccount());

        getPartnerContext().getDbBankPayAccounts().add(builder.getDbPayAccount());
        getPartnerContext().getDbSites().add(builder.getDbSite());
        getPartnerContext().getDbPayAccountMops().add(builder.getDbPayAccountMop());
        getPartnerContext().getDbPayAccountMopBanks().add(builder.getDbPayAccountMopBank());
        getPartnerContext().getDbBanks().add(builder.getDbBank());
        getPartnerContext().getDbBankAccounts().add(builder.getDbBankAccount());

        //Push the payment account onto the original placeholder
        getPartnerContext().setOriginalExistingDepositAccount(builder.getDbPayAccount());
        getPartnerContext().setDbSiteForOriginalExistingDepositAccount(builder.getDbSite());
        getPartnerContext().setDbPayAccountMopForOriginalExistingDepositAccount(builder.getDbPayAccountMop());
        getPartnerContext().setDbPayAccountMopBankForOriginalExistingDepositAccount(builder.getDbPayAccountMopBank());
        getPartnerContext().setOriginalExistingBankAccount(builder.getDbBankAccount());
        getPartnerContext().setOriginalExistingBank(builder.getDbBank());


        //Set current here so it will be populated at all times.  It will change if a new account is created.
        getPartnerContext().setCurrentDepositAccount(builder.getDbPayAccount());
        log.info(String.format("%s %s deposit account created [%s] with mop id [%d] and account number [%s].\n"
                , statusKeyword, cardTypeKeyword, builder.getDbPayAccount().getUuid(), builder.getDbBankAccount().getMopId(), builder.getDbBankAccount().getAccntNo()));
    }


    @Then("^an error code of (.*)$")
    public void errorCodeIs(String errorCode) {
        Assertion assertion = new Assertion();
        if(errorCode.equals("")){
            assertion = AssertionUtilityFunctions.assertValues(assertion, "Error code value.", null, getApiGatewayResponse().getResponseBodyMap().get("errorCode"));
        }else {
            assertion = AssertionUtilityFunctions.assertValues(assertion, "Error code value.", errorCode, getApiGatewayResponse().getResponseBodyMap().get("errorCode").toString());
        }
        logAndAssert(assertion);
    }

    @Then("^the payment account of type (.*) is persisted correctly on the database$")
    public void payAccountPersistedCorrectly(String payAccountUsageType){
        String type = UsageType.BILLING.name();
        DbPayAccount dbPayAccount = null;
        switch(UsageTypeKeyword.valueOf(payAccountUsageType)){
            case BILLING:
                type = UsageType.BILLING.name();
                dbPayAccount = getPartnerContext().getCurrentDbPayAccount();
                break;
            case DEPOSIT:
                type = UsageType.DEPOSIT.name();
                dbPayAccount = getPartnerContext().getCurrentDepositAccount();
                break;
            case INVALID:
                type = "FUDGE";
        }
        assertNotNull("No partner_pay_account record found", dbPayAccount);
        DbPartner dbPartner = DbPartnerQueryBuilder.defaultInstance(jdbcTemplate).withUuid(getPartnerContext().getPartnerUuid()).queryForObject();
        assertNotNull(dbPartner);

        PayAccountAsserter paa = new PayAccountAsserter();
        Assertion assertion = paa.assertPayAccount(dbPayAccount, dbPartner, type);

    }

    @Then("^the (.*) account device log information for the (.*) action is persisted correctly on the database$")
    public void deviceLogInfoPersistedCorrectly(String accountKeyword, String actionKeyword) throws Throwable {
        Assertion assertion;
        DbPayAccount dbPayAccount = null;
        if(accountKeyword.toLowerCase().contains("deposit")) {
            dbPayAccount = getPartnerContext().getCurrentDepositAccount();
        }else{
            dbPayAccount = getPartnerContext().getCurrentDbPayAccount();
        }
        assertNotNull(dbPayAccount);

        DbUserAccount dbUserAccount = getPartnerContext().getDbUserAccount();
        DbAssociateAccount dbAssociateAccount = DbAssociateAccountQueryBuilder.defaultInstance(jdbcTemplate)
                .withUserAccountId(dbUserAccount.getUserAccountId()).queryForObject();

        List<DbPayAccountAssocDevice> dbPayAccountAssocDeviceList = DbPayAccountAssocDeviceQueryBuilder.defaultInstance(jdbcTemplate)
                .withPayAccountId(dbPayAccount.getPayAccountId()).withOrderByClause("created_on desc").queryForList();
        DbPayAccountAssocDevice expectedDbPayAccountAssocDevice = new DbPayAccountAssocDevice();
        expectedDbPayAccountAssocDevice.setAssociateId(dbAssociateAccount.getAssociateId());
        expectedDbPayAccountAssocDevice.setPayAccountId(dbPayAccount.getPayAccountId());
        expectedDbPayAccountAssocDevice.setPayAccountActionTypeId(DbPayAccountActionTypeQueryBuilder.defaultInstance(jdbcTemplate).withCode(actionKeyword.toUpperCase()).queryForObject().getPayAccountActionTypeId());
        String ip;
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
        }

        expectedDbPayAccountAssocDevice.setIpAddress(ip);
        expectedDbPayAccountAssocDevice.setUserAgent(null);

        //TODO could be better if expected action date was closer to the time of the request.
        expectedDbPayAccountAssocDevice.setActionDate(new Date());
        PayAccountAsserter paa = new PayAccountAsserter();
        assertion = paa.assertPayAccountAssocDeviceTable(expectedDbPayAccountAssocDevice, dbPayAccountAssocDeviceList.get(0));
        logAndAssert(assertion);
        org.testng.Assert.assertTrue(assertion.getIsEqual());

    }

    @And("^the partner has another associate$")
    public void createNewPartnerAssociate() throws KeywordNotDefinedException{
        DbPartner partner = DbPartnerQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                .withUuid(getPartnerContext().getPartnerUuid())
                .queryForObject();

        DbUserAccount user;
        UserAccountRunnableBuilder uarb = new UserAccountRunnableBuilder("valid");
        uarb.run();
        user =  uarb.getFinalDbUserAccount();

        DbAssociate associate = partnerBuilder.buildPartnerAssociate(AssociateType.ASSOCIATE.name(), partner, user);
        getPartnerContext().addAssociateUuid(associate.getUuid());

        // need to link the user account to the associate & create contact points for the associate
        partnerBuilder.linkAssociateToUserAccount(user, associate);
        partnerBuilder.setEmailAndPhoneForAssociate(associate, user.getUserName());

    }

    @Then("^it is verified that the (.*) account is active$")
    public void payAccountVerifiedActive(String accountType) throws KeywordNotDefinedException{
        DbPayAccount dbPayAccount = null;
        switch(accountType){
            case "payment":
                dbPayAccount = getPartnerContext().getCurrentDbPayAccount();
                break;
            case "deposit":
                dbPayAccount = getPartnerContext().getCurrentDepositAccount();
                break;
            default:
                throw new KeywordNotDefinedException("accountType", "String");
        }
        assertEquals("Pay account should be active.", DbPayAccountStatusQueryBuilder.activeInstance(jdbcTemplate).getPayAccountStatusId(), dbPayAccount.getPayAccountStatusId());
    }

    @Then("^the (.*) account (.*) set as the default deposit account$")
    public void accountIsDefaultDepositPayAccount(String accountKeyword, String defaultKeyword) throws KeywordNotDefinedException{
        DbPayAccount dbPayAccount = null;
        if(accountKeyword.equalsIgnoreCase("current")){
            if(getPartnerContext().getCurrentDepositAccount() == null){
                org.junit.Assert.fail("No pay account is set as current.");
            }
            dbPayAccount = getPartnerContext().getCurrentDepositAccount();
        }else if (accountKeyword.equalsIgnoreCase("previous")){
            if(getPartnerContext().getOriginalExistingDbPayAccount() == null){
                org.junit.Assert.fail("No pay account is set as previous.");
            }
            dbPayAccount = getPartnerContext().getOriginalExistingDbPayAccount();

        }else{
            throw new KeywordNotDefinedException(accountKeyword, String.format("accountKeyword [%s] not found.", accountKeyword));
        }

        boolean assertIsDefault = true;
        if(defaultKeyword.toLowerCase().contains("not")) {
            assertIsDefault = false;
        }
        PayAccountAsserter paa = new PayAccountAsserter();
        paa.assertPartnerPayPrefDepositAccountDefault(dbPayAccount, assertIsDefault);
    }

    @Then("^the payment card accounts result list is (.*)$")
    public void assertCardAccountsResultListEmpty(String emptyKeyword) {
        Map<String, Object> responseMap = getApiGatewayResponse().getResponseBodyMap();
        if(emptyKeyword.toLowerCase().contains("not")){
            assertNotNull(responseMap.get("cardAccounts"));
        }else {
            assertNull(responseMap.get("cardAccounts"));
        }
    }



}
