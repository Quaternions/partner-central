package jtv.api.gateway.partner.builder;

import com.jtv.test.db.composite.PartnerAddressCompositeDataBuilder;
import com.jtv.test.db.entity.DbAddress;
import com.jtv.test.db.query.DbCountryQueryBuilder;
import com.jtv.test.db.query.DbStateProvinceQueryBuilder;
import jtv.api.gateway.partner.entity.request.create.*;
import jtv.api.gateway.partner.entity.request.Phone;
import jtv.api.gateway.partner.entity.request.SiteAddress;
import jtv.dao.address.AddressDao;
import jtv.dao.entity.partner.PartnerStatus;
import jtv.dao.entity.partner.SiteUsageType;
import jtv.data.generator.DataGenerator;
import jtv.keycloak.utility.KeycloakUtilityFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CreatePartnerProfileRequestBuilder {
    private static final Logger log = LoggerFactory.getLogger(CreatePartnerProfileRequestBuilder.class);

    public static CreatePartnerProfileRequest buildRequest(String inviteToken, String accessToken) throws IOException {
        log.info("[CreatePartnerProfileRequest.buildRequest] inviteToken = " + inviteToken);
        return buildBaseRequest(inviteToken, accessToken);
    }

    private static CreatePartnerProfileRequest buildBaseRequest(String inviteToken, String accessToken) throws IOException {
        KeycloakUtilityFunctions keycloakUtilityFunctions = new KeycloakUtilityFunctions();

        AddressDao addressDao = new AddressDao();
        DbAddress address = PartnerAddressCompositeDataBuilder.randomValidInstance(addressDao.getJtvJdbcTemplate()).build();
        log.info("address (randomValidInstance) = " + address.toString());
        String subdivisionIsoCode = DbStateProvinceQueryBuilder.defaultInstance(addressDao.getJtvJdbcTemplate()).withStateProvinceId(address.getStateProvinceId()).queryForObject().getFullIsoCode();
        String countryIso3Code = DbCountryQueryBuilder.defaultInstance(addressDao.getJtvJdbcTemplate()).withCountryId(address.getCountryId()).queryForObject().getIsoAlpha3Code();
        String legalName = DataGenerator.NameGenerator.generateCompanyName();

        List<CreatePartnerSite> createPartnerSiteList = new ArrayList<>();
        createPartnerSiteList.add(new CreatePartnerSite("Partner Site Name " + UUID.randomUUID().toString().substring(0, 8)
                        , new ArrayList<>(Arrays.asList(SiteUsageType.BUSINESS.name()))
                        , new SiteAddress( address.getAddressLine1()
                        , address.getAddressLine2()
                        , address.getAddressLine3()
                        , address.getCity()
                        , subdivisionIsoCode
                        , countryIso3Code
                        , address.getPostalCode()
                        )
                ));
        CreatePartnerProfileRequest baseRequestBody = new CreatePartnerProfileRequest(
                new CreatePartnerProfile(
                        inviteToken
                        , new CreatePrimaryPartnerAssociate(keycloakUtilityFunctions.getGivenNameFromToken(accessToken)
                                , keycloakUtilityFunctions.getFamilyNameFromToken(accessToken)
                                , DataGenerator.NameGenerator.generateTitle()
                                , new Phone(DataGenerator.PhoneGenerator.generateCountryCode()
                                        , DataGenerator.PhoneGenerator.generatePhoneNumber()
                                        , DataGenerator.PhoneGenerator.generatePhoneExtension()
                                )
                                , keycloakUtilityFunctions.getPreferredUsernameFromToken(accessToken)
                        )
                        , createPartnerSiteList
                        , PartnerStatus.REGISTRATION_COMPLETED.name()
                        , legalName
                        , legalName + UUID.randomUUID().toString().substring(0, 12)
                        )
                );
        log.info("[CreatePartnerProfileRequestBuilder::buildBaseRequest] baseRequestBody = " + baseRequestBody.toJsonString());
        return baseRequestBody;
    }
}
