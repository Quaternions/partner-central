package jtv.glue.stepdefinitions.apigateway.partner.associate.get;

import com.jtv.test.db.query.partner.DbPartnerQueryBuilder;
import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import jtv.api.gateway.partner.associate.asserter.PartnerAssociateAsserter;
import jtv.context.apigateway.ApiGatewayPartnerTestContext;
import jtv.context.apigateway.ApiGatewayTestContext;
import jtv.dao.partner.PartnerDao;
import jtv.exception.KeywordNotDefinedException;
import jtv.glue.stepdefinitions.apigateway.partner.BasePartnerGlue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public class GetPartnerAssociateStepDefinitions extends BasePartnerGlue {
    private String associateFilter = "";
    private String pathParameterUuid = "";
    private PartnerDao partnerDao = new PartnerDao();

    private static final Logger log = LoggerFactory.getLogger(GetPartnerAssociateStepDefinitions.class);

    public GetPartnerAssociateStepDefinitions(ApiGatewayTestContext context, ApiGatewayPartnerTestContext partnerTestContext) {
        super(context, partnerTestContext);
    }

    private void makeRequest() {
        if (associateFilter.equals("NO")) {
            setApiGatewayResponse(
                    getApiGatewayClient()
                            .partner(pathParameterUuid)
                            .associate()
                            .getAssociate(getAccessToken(), getContentType())
            );
        } else {
            setApiGatewayResponse(
                    getApiGatewayClient()
                            .partner(pathParameterUuid)
                            .associate()
                            .getAssociate(getAccessToken(), getContentType(), associateFilter)
            );
        }
        logAndReportRequest();
    }

    @When("^a request is made to get a list of associates for the partner with (.*) filter$")
    public void sendGetPartnerAssociateList(String filter) throws KeywordNotDefinedException {
        if (filter.isEmpty()) {
            throw new KeywordNotDefinedException(filter, "GET partnerAssociates filter");
        }
        associateFilter = filter;
        if (pathParameterUuid.isEmpty()) {
            // check to see if pathParameterUuid has not be set
            pathParameterUuid = getPartnerContext().getPartnerUuid();
        } else if (pathParameterUuid.equals("empty")) {
            // if the pathParameterUuid is supposed to be empty...
            pathParameterUuid = "";
        }

        makeRequest();
    }

    @And("^the get associate response body persists the data from the database$")
    public void assertGetPartnerAssociate() throws IOException {
        PartnerAssociateAsserter partnerAssociateAsserter = new PartnerAssociateAsserter();
        if (associateFilter.equals("ME")) {
            // assert only on my associate
            logAndAssert(partnerAssociateAsserter.assertGetPartnerAssociate(getApiGatewayResponse().getResponseBody(), getPartnerContext().getMyAssociateUuid()));
        } else {
            // assert the response body for each associate for the partner
            for ( String currentUuid : getPartnerContext().getAssociateUuids() ) {
                log.info("[assertGetPartnerAssociate] asserting on Associate (" + currentUuid + ")");

                logAndAssert(partnerAssociateAsserter.assertGetPartnerAssociate(getApiGatewayResponse().getResponseBody(), currentUuid));
            }
        }
    }

    @And("^the partner-id path parameter has (.*) UUID assigned to it$")
    public void partnerIdPathParameterKeyword(String uuidKeyword) throws KeywordNotDefinedException {
        try {
            log.info("[partnerIdPathParameterKeyword] uuidKeyword = '" + uuidKeyword + "'");
        } catch (Exception e) {
            throw new KeywordNotDefinedException(uuidKeyword, "uuidKeyword is not defined!");
        }

        switch (uuidKeyword) {
            case "another partner's" : {
                log.info("Am I here?!?");
                //int partnerListSize = DbPartnerQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate()).queryForList().size();
                pathParameterUuid = DbPartnerQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate()).queryForList().subList(0,10).get(10).getUuid();
//                pathParameterUuid =  DbPartnerQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate()).queryForList().get(partnerListSize-5).getUuid();
                log.info("[partnerIdPathParameterKeyword] (uuidKeyword = " + uuidKeyword + ") pathParameterUuid = " + pathParameterUuid);
                break;
            }
            case "an unknown" : {
                pathParameterUuid = UUID.randomUUID().toString();
                log.info("[partnerIdPathParameterKeyword] (uuidKeyword = " + uuidKeyword + ") pathParameterUuid = " + pathParameterUuid);
                break;
            }
            case "an invalid formatted" : {
                pathParameterUuid = "fea7f88d-75bd-41da-a810";  // just removed the last 12 grouping from the UUID
                log.info("[partnerIdPathParameterKeyword] (uuidKeyword = " + uuidKeyword + ") pathParameterUuid = " + pathParameterUuid);
                break;
            }
            case "no" : {
                pathParameterUuid = "empty";
                log.info("[partnerIdPathParameterKeyword] (uuidKeyword = " + uuidKeyword + ") pathParameterUuid = " + pathParameterUuid);
                break;
            }
            default : {
                throw new KeywordNotDefinedException(uuidKeyword, "uuidKeyword is not a valid keyword!");
            }
        }
    }
}
