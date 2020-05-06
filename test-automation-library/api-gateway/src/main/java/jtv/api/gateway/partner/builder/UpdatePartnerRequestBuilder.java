package jtv.api.gateway.partner.builder;

import com.jtv.test.db.entity.DbAddress;
import com.jtv.test.db.entity.DbPhoneNumber;
import com.jtv.test.db.entity.partner.*;
import com.jtv.test.db.query.*;
import com.jtv.test.db.query.partner.*;
import jtv.api.gateway.partner.entity.request.*;
import jtv.api.gateway.partner.entity.request.update.*;
import jtv.dao.entity.partner.SiteUsageType;
import jtv.dao.partner.PartnerDao;

import jtv.data.generator.DataGenerator;
import jtv.exception.KeywordNotDefinedException;
import jtv.parameters.ParameterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class UpdatePartnerRequestBuilder {
    private static final PartnerDao partnerDao= new PartnerDao();
    private static final Logger log = LoggerFactory.getLogger(UpdatePartnerRequestBuilder.class);

    public UpdatePartnerRequestBuilder() {}

    public static void updatePartnerProfileParameter(String parameter, UpdatePartnerRequest request) throws KeywordNotDefinedException {
        String currentValue;

        switch (parameter.toLowerCase()) {
            case "legal entity name" : {
                currentValue = request.getUpdatePartner().getLegalEntityName();
                request.getUpdatePartner().setLegalEntityName(DataGenerator.NameGenerator.generateCompanyName());
                log.info("Updating the legalEntityName to: " + request.getUpdatePartner().getLegalEntityName() + " from: " + currentValue);
                break;
            }
            case "operating name" : {
                currentValue = request.getUpdatePartner().getOperatingName();
                request.getUpdatePartner().setOperatingName(request.getUpdatePartner().getLegalEntityName() + " " + UUID.randomUUID().toString().substring(0,12));
                log.info("Updating the operatingName to: " + request.getUpdatePartner().getOperatingName() + " from: " + currentValue);
                break;
            }
            case "partner account status" : {
                currentValue = request.getUpdatePartner().getPartnerAccountStatus();
                if (request.getUpdatePartner().getPartnerAccountStatus().equals("REGISTRATION_COMPLETED")) {
                    request.getUpdatePartner().setPartnerAccountStatus("REGISTRATION_IN_PROGRESS");
                } else {
                    request.getUpdatePartner().setPartnerAccountStatus("REGISTRATION_COMPLETED");
                }

                log.info("Updating the partner account status to: " + request.getUpdatePartner().getPartnerAccountStatus() + " from: " + currentValue);
                break;
            }
            default:
                throw new KeywordNotDefinedException(parameter, "Keyword is not a valid parameter for the Partner Profile");
        }
    }

    public static void updatePrimaryContactParameter(String parameter, UpdatePartnerRequest request) throws KeywordNotDefinedException {
        String currentValue;

        switch (parameter.toLowerCase()) {
            case "firstname" : {
                currentValue = request.getUpdatePartner().getPrimaryContact().getFirstName();
                request.getUpdatePartner().getPrimaryContact().setFirstName(DataGenerator.NameGenerator.generateFirstName());
                log.info("Updating the firstname to: " + request.getUpdatePartner().getPrimaryContact().getFirstName() + " from: " + currentValue);
                break;
            }
            case "lastname" : {
                currentValue = request.getUpdatePartner().getPrimaryContact().getLastName();
                request.getUpdatePartner().getPrimaryContact().setLastName(DataGenerator.NameGenerator.generateLastName());
                log.info("Updating the lastname to: " + request.getUpdatePartner().getPrimaryContact().getLastName() + " from: " + currentValue);
                break;
            }
            case "email" : {
                currentValue = request.getUpdatePartner().getPrimaryContact().getEmail();
                request.getUpdatePartner().getPrimaryContact().setEmail(DataGenerator.NameGenerator.generateEmailAddressGivenName(
                        request.getUpdatePartner().getPrimaryContact().getFirstName()
                        , request.getUpdatePartner().getPrimaryContact().getLastName())
                );
                log.info("Updating the email address to: " + request.getUpdatePartner().getPrimaryContact().getEmail() + " from: " + currentValue);
                break;
            }
            case "title" : {
                currentValue = request.getUpdatePartner().getPrimaryContact().getTitle();
                request.getUpdatePartner().getPrimaryContact().setTitle(DataGenerator.NameGenerator.generateTitle());
                log.info("Updating the subscriber number to: " + request.getUpdatePartner().getPrimaryContact().getTitle() + " from: " + currentValue);
                break;
            }
            case "subscriber number" : {
                currentValue = request.getUpdatePartner().getPrimaryContact().getPhone().getSubscriberNumber();
                request.getUpdatePartner().getPrimaryContact().getPhone().setSubscriberNumber(DataGenerator.PhoneGenerator.generatePhoneNumber());
                log.info("Updating the subscriber number to: " + request.getUpdatePartner().getPrimaryContact().getPhone().getSubscriberNumber() + " from: " + currentValue);
                break;
            }
            case "phone country code" : {
                currentValue = request.getUpdatePartner().getPrimaryContact().getPhone().getCountryCode();
                request.getUpdatePartner().getPrimaryContact().getPhone().setCountryCode(DataGenerator.PhoneGenerator.generateCountryCode());
                log.info("Updating the country code to: " + request.getUpdatePartner().getPrimaryContact().getPhone().getCountryCode() + " from: " + currentValue);
                break;
            }
            case "phone extension" : {
                currentValue = request.getUpdatePartner().getPrimaryContact().getPhone().getExtension();
                request.getUpdatePartner().getPrimaryContact().getPhone().setExtension(DataGenerator.PhoneGenerator.generatePhoneExtension());
                log.info("Updating the extension number to: " + request.getUpdatePartner().getPrimaryContact().getPhone().getExtension() + " from: " + currentValue);
                break;
            }
            default:
                throw new KeywordNotDefinedException(parameter, "Keyword is not a valid parameter for the Partner Primary Contact");
        }
    }

    public static void updatePartnerSiteParameter(String parameter, UpdatePartnerSite site) throws KeywordNotDefinedException {
        String currentValue = null;

        switch (parameter.toLowerCase()){
            case "site name" : {
                currentValue = site.getSiteName();
                site.setSiteName(DataGenerator.NameGenerator.generateCompanyName());
                log.info("Updating the partner site name to: " + site.getSiteName() + " from: " + currentValue);
                break;
            }
            case "usages" : {
                List<String> usages = new ArrayList<>();
                for (String usage : site.getPartnerSiteUsageTypes()) {
                    switch (usage) {
                        case "BUSINESS" : {
                            usages.set(usages.indexOf(SiteUsageType.BUSINESS.name()), SiteUsageType.BILLING.name());
                            currentValue = usage;
                            break;
                        }
                        case "BILLING" : {
                            usages.set(usages.indexOf(SiteUsageType.BILLING.name()), SiteUsageType.RETURNS.name());
                            currentValue = usage;
                            break;
                        }
                        case "RETURNS" : {
                            usages.set(usages.indexOf(SiteUsageType.RETURNS.name()), SiteUsageType.SHIPPING.name());
                            currentValue = usage;
                            break;
                        }
                        case "SHIPPING" : {
                            usages.set(usages.indexOf(SiteUsageType.SHIPPING.name()), SiteUsageType.BUSINESS.name());
                            currentValue = usage;
                            break;
                        }
                    }
                    if (!usages.contains("BUSINESS")) {
                        usages.add("BUSINESS");
                        // remove the first element if adding the business usage.  This will keep the number of usages on the site the same.
                        usages.remove(0);
                    }
                    site.setPartnerSiteUsageTypes(usages);
                    log.info("Updating the partner site usages to: " + site.getPartnerSiteUsageTypes().toString() + " from: " + currentValue);
                }
                break;
            }
            case "address line one" : {
                currentValue = site.getAddress().getAddressLineOne();
                site.getAddress().setAddressLineOne(DataGenerator.AddressGenerator.generateAddressLineOne());
                log.info("Updating the partner site address line one to: " + site.getAddress().getAddressLineOne() + " from: " + currentValue);
                break;
            }
            case "address line two" : {
                currentValue = site.getAddress().getAddressLineTwo();
                site.getAddress().setAddressLineTwo(DataGenerator.AddressGenerator.generateAddressLineTwo());
                log.info("Updating the partner site address line two to: " + site.getAddress().getAddressLineTwo() + " from: " + currentValue);
                break;
            }
            case "city" : {
                currentValue = site.getAddress().getCity();
                site.getAddress().setCity(DataGenerator.AddressGenerator.generateCity());
                log.info("Updating the partner site city to: " + site.getAddress().getCity() + " from: " + currentValue);
                break;
            }
            case "state or province" : {
                currentValue = site.getAddress().getSubdivisionIsoCode();
                site.getAddress().setSubdivisionIsoCode(DataGenerator.AddressGenerator.generateState());
                log.info("Updating the partner site state/province to: " + site.getAddress().getSubdivisionIsoCode() + " from: " + currentValue);
                break;
            }
            case "postal code" : {
                currentValue = site.getAddress().getPostalCode();
                site.getAddress().setPostalCode(DataGenerator.AddressGenerator.generatePostalCode());
                log.info("Updating the partner site postal code to: " + site.getAddress().getPostalCode() + " from: " + currentValue);
                break;
            }
            case "country code" : {
                currentValue = site.getAddress().getCountryIso3Code();
                site.getAddress().setCountryIso3Code(DataGenerator.AddressGenerator.generateCountryCode());
                log.info("Updating the partner site country code to: " + site.getAddress().getCountryIso3Code() + " from: " + currentValue);
                break;
            }
            default:
                throw new KeywordNotDefinedException(parameter, "Keyword is not a valid parameter for the Partner Site");
        }
    }

    public static UpdatePartnerRequest buildRequest(String partnerUuid, String pamUuid, List<DbSite> sites) {
        return buildBaseRequest(partnerUuid, pamUuid, sites);
    }

    public static UpdatePartnerRequest buildBaseRequest(String partnerUuid, String pamUuid, List<DbSite> sites) {
        UpdatePartnerRequest requestBody;

        DbPartner partner = DbPartnerQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate()).withUuid(partnerUuid).queryForObject();
        DbAssociate primaryAccountManager = DbAssociateQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate()).withUuid(pamUuid).queryForObject();
        DbPhoneNumber phone = DbPhoneNumberQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                .withPhoneNumberId(DbContactPointQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                        .withAssociateId(DbAssociateQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                                .withUuid(pamUuid)
                                .queryForObject()
                                .getAssociateId()
                        ).withContactPointTypeId(new Long(2))
                        .queryForObject()
                        .getPhoneNumberId()
                ).queryForObject();

        String email = DbEmailAddressQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                .withEmailAddressId(DbContactPointQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                        .withAssociateId(DbAssociateQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                                .withUuid(pamUuid)
                                .queryForObject()
                                .getAssociateId()
                        ).withContactPointTypeId(new Long(1)) // email = 1; phone = 2
                        .queryForObject()
                        .getEmailAddressId()
                ).queryForObject()
                .getEmailAddress();

        // build the UpdatePartnerSite entity based on the DbSite list
        List<UpdatePartnerSite> updatePartnerSiteList = new ArrayList<>();

        for (DbSite site: sites) {
            List<String> usageTypeStrings = new ArrayList<>();

            // get a list of all the site usages for current site which only contains the usage type ID, but not the CODES.
            List<DbSiteUsage> usageTypes = new ArrayList<>(DbSiteUsageQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                    .withSiteId(site.getSiteId())
                    .queryForList());

            for (DbSiteUsage currentUsage: usageTypes) {
                // use the usage type IDs to get the codes which will be used in the request builder.
                String usageTypeString = DbSiteUsageTypeQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                        .withSiteUsageTypeId(currentUsage.getSiteUsageTypeId())
                        .queryForObject()
                        .getCode();

                usageTypeStrings.add(usageTypeString);
//                log.info("adding " + usageTypeString + " as a usage type to the current partner site (" + site.getUuid() + ")");
            }

            DbAddress address = DbAddressQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                    .withAddressId(site.getAddressId())
                    .queryForObject();

            SiteAddress siteAddress = new SiteAddress(
                    address.getAddressLine1()
                    , address.getAddressLine2()
                    , address.getAddressLine3()
                    , address.getCity()
                    , DbStateProvinceQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                            .withStateProvinceId(address.getStateProvinceId())
                            .queryForObject()
                            .getIsoAlpha2Code()
                    , DbCountryQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate()).withCountryId(address.getCountryId()).queryForObject().getIsoAlpha2Code()
                    , address.getPostalCode()
            );
            //UpdatePartnerSite(String siteName, List<String> partnerSiteUsageTypes, String siteId, SiteAddress address) {
            UpdatePartnerSite currentPartnerSite = new UpdatePartnerSite(
                    site.getSiteName()
                    , usageTypeStrings
                    , site.getUuid()
                    , siteAddress
            );

            updatePartnerSiteList.add(currentPartnerSite);
        }

        requestBody = new UpdatePartnerRequest(
                              //UpdatePartnerProfile(UpdatePrimaryPartnerAssociate primaryContact, List<UpdatePartnerSite> partnerSites, String legalEntityName, String operatingName, String partnerAccountStatus)
                              new UpdatePartnerProfile(
                                      new UpdatePrimaryPartnerAssociate(
                                              primaryAccountManager.getFirstName()
                                              , primaryAccountManager.getLastName()
                                              , email
                                              , primaryAccountManager.getTitle()
                                              , new Phone(
                                                      phone.getCountryCode().toString(),
                                                      phone.getSubscriberNumber(),
                                                      phone.getExtension()
                                              )
                                              , pamUuid
                                      )
                                      , updatePartnerSiteList
                                      , partner.getLegalName()
                                      , partner.getOperatingName()
                                      , /*partnerAccountStatus field:*/ DbPartnerStatusQueryBuilder.defaultInstance(partnerDao.getJtvJdbcTemplate())
                                                .withPartnerStatusId(partner.getPartnerStatusId())
                                                .queryForObject()
                                                .getCode()
                              )
                      );

        log.info("[buildBaseRequest] Finished building the request body for the POST /partner/{partner-id} API call \nrequestBody = \n" + requestBody);
        return requestBody;
    }

    public static void legalEntityNameValidation(String keyword, UpdatePartnerRequest request) throws KeywordNotDefinedException {
        request.getUpdatePartner().setLegalEntityName(ParameterUtils.generateParameterFromKeyword(
                "legalEntityName"
                , keyword
                , request.getUpdatePartner().getLegalEntityName()
                , 100
                , "update partner legal entity name"
            )
        );
    }

    public static void operatingNameValidation(String keyword, UpdatePartnerRequest request) throws KeywordNotDefinedException {
        request.getUpdatePartner().setOperatingName(ParameterUtils.generateParameterFromKeyword(
                "operatingName"
                , keyword
                , request.getUpdatePartner().getOperatingName()
                , 100
                , "update partner operating name"
                )
        );
    }

    public static void firstNameValidation(String keyword, UpdatePartnerRequest request) throws KeywordNotDefinedException {
        request.getUpdatePartner().getPrimaryContact().setFirstName(ParameterUtils.generateParameterFromKeyword(
                "firstName"
                , keyword
                , request.getUpdatePartner().getPrimaryContact().getFirstName()
                , 50
                , "firstName for the primary contact of the partner profile"
                )
        );
    }

    public static void lastNameValidation(String keyword, UpdatePartnerRequest request) throws KeywordNotDefinedException {
        request.getUpdatePartner().getPrimaryContact().setLastName(ParameterUtils.generateParameterFromKeyword(
                "firstName"
                , keyword
                , request.getUpdatePartner().getPrimaryContact().getLastName()
                , 50
                , "lastName for the primary contact of the partner profile"
                )
        );
    }

    public static void titleValidation(String keyword, UpdatePartnerRequest request) throws KeywordNotDefinedException {
        request.getUpdatePartner().getPrimaryContact().setTitle(ParameterUtils.generateParameterFromKeyword(
                "title"
                , keyword
                , request.getUpdatePartner().getPrimaryContact().getTitle()
                , 50
                , "Title for the primary contact of the partner profile"
                )
        );
    }

    public static void emailValidation(String keyword, UpdatePartnerRequest request) throws KeywordNotDefinedException {
        request.getUpdatePartner().getPrimaryContact().setEmail(ParameterUtils.generateParameterFromKeyword(
                "email"
                , keyword
                , request.getUpdatePartner().getPrimaryContact().getEmail()
                , 50
                , "Email for the primary contact of the partner profile"
                )
        );
    }

    public static void phoneSubscriberNumberValidation(String keyword, UpdatePartnerRequest request) throws KeywordNotDefinedException {
        request.getUpdatePartner().getPrimaryContact().getPhone().setSubscriberNumber(ParameterUtils.generateParameterFromKeyword(
                "phone"
                , keyword
                , request.getUpdatePartner().getPrimaryContact().getPhone().getSubscriberNumber()
                , 12
                , "Phone Subscriber Number for the primary contact of the partner profile"
                )
        );
    }

    public static void phoneCountryCodeValidation(String keyword, UpdatePartnerRequest request) throws KeywordNotDefinedException {
        request.getUpdatePartner().getPrimaryContact().getPhone().setCountryCode(ParameterUtils.generateParameterFromKeyword(
                "countryCode"
                , keyword
                , request.getUpdatePartner().getPrimaryContact().getPhone().getCountryCode()
                , 3
                , "Phone Country Code for the primary contact of the partner profile"
                )
        );
    }

    public static void phoneExtensionValidation(String keyword, UpdatePartnerRequest request) throws KeywordNotDefinedException {
        request.getUpdatePartner().getPrimaryContact().getPhone().setExtension(ParameterUtils.generateParameterFromKeyword(
                "phoneExt"
                , keyword
                , request.getUpdatePartner().getPrimaryContact().getPhone().getExtension()
                , 5
                , "Phone Extension for the primary contact of the partner profile"
                )
        );
    }

    public static void siteNameValidation(String keyword, UpdatePartnerRequest request) throws KeywordNotDefinedException {
        for (UpdatePartnerSite site: request.getUpdatePartner().getPartnerSites()) {
            site.setSiteName(ParameterUtils.generateParameterFromKeyword(
                    "siteName"
                    , keyword
                    , site.getSiteName()
                    , 50
                    , "Update Partner Site Name"
                    )
            );
        }
    }

    public static void addressLineOneValidation(String keyword, UpdatePartnerRequest request) throws KeywordNotDefinedException {
        for (UpdatePartnerSite site: request.getUpdatePartner().getPartnerSites()) {
            site.getAddress().setAddressLineOne(ParameterUtils.generateParameterFromKeyword(
                    "address line one"
                    , keyword
                    , site.getAddress().getAddressLineOne()
                    , 240
                    , "Update Partner Site Address Line One"
                    )
            );
        }
    }

    public static void addressLineTwoValidation(String keyword, UpdatePartnerRequest request) throws KeywordNotDefinedException {
        for (UpdatePartnerSite site: request.getUpdatePartner().getPartnerSites()) {
            site.getAddress().setAddressLineTwo(ParameterUtils.generateParameterFromKeyword(
                    "address line two"
                    , keyword
                    , site.getAddress().getAddressLineTwo()
                    , 240
                    , "Update Partner Site Address Line Two"
                    )
            );
        }
    }

    public static void cityValidation(String keyword, UpdatePartnerRequest request) throws KeywordNotDefinedException {
        for (UpdatePartnerSite site: request.getUpdatePartner().getPartnerSites()) {
            site.getAddress().setCity(ParameterUtils.generateParameterFromKeyword(
                    "city"
                    , keyword
                    , site.getAddress().getCity()
                    , 30
                    , "Update Partner Site City"
                    )
            );
        }
    }

    public static void stateProvinceValidation(String keyword, UpdatePartnerRequest request) throws KeywordNotDefinedException {
        for (UpdatePartnerSite site: request.getUpdatePartner().getPartnerSites()) {
            String state = ParameterUtils.generateParameterFromKeyword(
                    "state province"
                    , keyword
                    , site.getAddress().getSubdivisionIsoCode()
                    , 2
                    , "Update Partner Site State Province"
            );
            if (state == null) {
                site.getAddress().setSubdivisionIsoCode(null);
            } else {
                site.getAddress().setSubdivisionIsoCode(state.toUpperCase());
            }

        }
    }

}
