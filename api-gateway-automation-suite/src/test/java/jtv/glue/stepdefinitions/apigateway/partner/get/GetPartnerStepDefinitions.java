package jtv.glue.stepdefinitions.apigateway.partner.get;

import com.jtv.test.db.entity.entitlement.DbUserAccount;
import com.jtv.test.db.entity.partner.DbAssociate;
import com.jtv.test.db.entity.partner.DbPartner;
import com.jtv.test.db.entity.partner.DbSite;
import com.jtv.test.db.query.partner.DbAssociateQueryBuilder;
import com.jtv.test.db.query.partner.DbAssociateTypeQueryBuilder;
import com.jtv.test.db.query.partner.DbSiteQueryBuilder;
import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import jtv.api.gateway.partner.asserter.PartnerProfileAsserter;
import jtv.builder.partner.PartnerBuilder;
import jtv.context.apigateway.ApiGatewayPartnerTestContext;
import jtv.context.apigateway.ApiGatewayTestContext;
import jtv.dao.user.UserAccountDao;
import jtv.exception.KeywordNotDefinedException;
import jtv.glue.stepdefinitions.apigateway.partner.BasePartnerGlue;
import jtv.keycloak.utility.KeycloakUtilityFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class GetPartnerStepDefinitions extends BasePartnerGlue {
    private static final Logger log = LoggerFactory.getLogger(GetPartnerStepDefinitions.class);
    private Long currentSiteId;
    private final PartnerBuilder partnerBuilder = new PartnerBuilder();
    private final UserAccountDao userDao = new UserAccountDao();
    private final KeycloakUtilityFunctions keycloakUtilityFunctions = new KeycloakUtilityFunctions();

    private DbPartner dbPartner = new DbPartner();

    public GetPartnerStepDefinitions(ApiGatewayTestContext apiGatewayTestContext, ApiGatewayPartnerTestContext apiGatewayPartnerTestContext) {
        super(apiGatewayTestContext, apiGatewayPartnerTestContext);
    }

    @And("^a partner already created that (.*)$")
    public void aPartnerAlreadyCreated(String keyword) throws KeywordNotDefinedException {
        dbPartner = partnerBuilder.createPartnerWithKeyword(keyword);
        if(dbPartner != null){
            getPartnerContext().setPartnerUuid(dbPartner.getUuid());
            embedInScenario(String.format("Partner Uuid: [%s] was created.\n", dbPartner.getUuid()));
        }
    }

    @And("^has a partner site with usage type (.*)$")
    public void aPartnerSite(String usage) throws KeywordNotDefinedException {

        if ( usage == null || usage.isEmpty()) {
            throw new KeywordNotDefinedException(usage, "Keyword is either null or empty");
        }
        currentSiteId = partnerBuilder.createPartnerSiteWithUsage(dbPartner,usage);

        DbSite site = DbSiteQueryBuilder.defaultInstance(userDao.getJtvJdbcTemplate()).withSiteId(currentSiteId).queryForObject();
        getPartnerContext().addSite(site);
        embedInScenario(String.format("Site Uuid: [%s] was created for partner [%s].", site.getUuid(), dbPartner.getUuid()));
     }

    @And("^the partner site also has a usage of (.*)$")
    public void addUsageToPartnerSite(String keyword) throws KeywordNotDefinedException{
        partnerBuilder.addUsageToPartnerSite(dbPartner, currentSiteId , keyword);
    }

    @And("the current user is (.*) for the partner")
    public void createPartnerAssociationForUser(String keyword) throws KeywordNotDefinedException, IOException {
        if (keyword == null || keyword.isEmpty()) {
            throw new KeywordNotDefinedException(keyword, "Keyword is either null or empty");
        }

        DbUserAccount currentUser = userDao.getDbUserAccountByKeycloakUuid(keycloakUtilityFunctions.getSubjectFromToken(getAccessToken()));
        DbAssociate associateForUser =  partnerBuilder.createPartnerAssociationForUser(dbPartner, currentUser, keyword);
        // get the UUID for the primary account manager and save it in the partner test context
        getPartnerContext().setPamUuid(
                DbAssociateQueryBuilder.defaultInstance(userDao.getJtvJdbcTemplate())
                .withPartnerId(dbPartner.getPartnerId())
                .withAssociateTypeId(
                        DbAssociateTypeQueryBuilder.defaultInstance(userDao.getJtvJdbcTemplate())
                        .withCode("PRIMARY_ACCOUNT_MANAGER")
                        .queryForObject()
                        .getAssociateTypeId()
                )
                .queryForObject()
                .getUuid()
        );
        getPartnerContext().setDbUserAccount(currentUser);
        getPartnerContext().setMyAssociateUuid(associateForUser.getUuid());
        getPartnerContext().addAssociateUuid(associateForUser.getUuid());

        // get all of the associates for the partner and add it to the list of associate uuids in the partner context
//        List<DbAssociate> associateList = DbAssociateQueryBuilder.defaultInstance(userDao.getJtvJdbcTemplate())
//                                                .withPartnerId(dbPartner.getPartnerId())
//                                                .queryForList();
//        for (DbAssociate currentAssociate: associateList ) {
//            getPartnerContext().addAssociateUuid(currentAssociate.getUuid());
//        }
    }

    @When("^a request is made to get a partner using the current user's access token$")
    public void getPartnerUsingAccessToken() {
        setApiGatewayResponse(getApiGatewayClient().partner().getPartner(getAccessToken(), getContentType()));
        logAndReportRequest();
    }

    @And("^the response body contains the correct partner information$")
    public void assertGetPartnerProfiles() throws IOException {
        if(getApiGatewayResponse().getResponseBody().contains("partnerProfiles")) {
            logAndAssert(PartnerProfileAsserter.assertGetPartnerProfiles(getApiGatewayResponse().getResponseBody()));
        } else if (getApiGatewayResponse().getResponseBody().contains("partner")) {
            logAndAssert(PartnerProfileAsserter.assertGetPartner(getApiGatewayResponse().getResponseBody()));
        }

    }

    @When("^a request is made to get the partner using the partner uuid$")
    public void getPartnerProfileWithPartnerUuid() {
        // If the UUID doesn't exist, then create one to use for the API GET /partner/{partner-id} call
        if (dbPartner.getUuid() == null) {
            dbPartner.setUuid(UUID.randomUUID().toString());
            getPartnerContext().setPartnerUuid(dbPartner.getUuid());
        }
        setApiGatewayResponse(getApiGatewayClient().partner(getPartnerContext().getPartnerUuid()).getPartner(getAccessToken(), getContentType()));
        logAndReportRequest();
    }
}
