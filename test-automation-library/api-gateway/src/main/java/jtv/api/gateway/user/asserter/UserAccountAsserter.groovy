package jtv.api.gateway.user.asserter

import groovy.json.JsonSlurper
import jtv.assertion.Assertion
import jtv.assertion.utils.AssertionUtilityFunctions
import jtv.dao.entity.user.DatabaseUserAccount
import jtv.dao.user.UserAccountDao
import org.apache.groovy.json.internal.LazyMap
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class UserAccountAsserter {
    private static final Logger log = LoggerFactory.getLogger(UserAccountAsserter.class)

    static Assertion assertGetUserAccount(String kcUuid, String responseBody) {
        DatabaseUserAccount expectedUserAccount
        DatabaseUserAccount actualUserAccount

        expectedUserAccount = getUserAccountFromX5byKeycloakUuid(kcUuid)
        actualUserAccount = mapResponseBodyToUserAccount(responseBody)

        return assertUserAccounts(expectedUserAccount, actualUserAccount)
    }

    static Assertion assertUpdateUserAccount(DatabaseUserAccount dbUserAccount) {

        DatabaseUserAccount expectedUserAccount
        DatabaseUserAccount actualUserAccount

        expectedUserAccount = dbUserAccount
        actualUserAccount = getUserAccountFromX5byJtvUuid(dbUserAccount.jtvUuid)

        return  assertUserAccounts(expectedUserAccount, actualUserAccount)
    }

    static Assertion assertCreateUserAccount(String responseBody, String accessToken) {
        DatabaseUserAccount expectedUserAccount
        DatabaseUserAccount actualUserAccount
        def responseMap

        responseMap = new JsonSlurper().parseText(responseBody)

        expectedUserAccount = mapUserAccountFromResponseAndAccessToken(responseBody, accessToken)
        actualUserAccount = getUserAccountFromX5byJtvUuid(responseMap.userId as String)

        return assertUserAccounts(expectedUserAccount, actualUserAccount)
    }

    private static Assertion assertUserAccounts(DatabaseUserAccount expectedUserAccount, DatabaseUserAccount actualUserAccount) {
        Assertion returnAssertion = new Assertion("User Account ${expectedUserAccount.jtvUuid} Assertions")

        returnAssertion = AssertionUtilityFunctions.assertValues(returnAssertion, "User Account ${expectedUserAccount.jtvUuid} First Name", expectedUserAccount.firstName, actualUserAccount.firstName)
        returnAssertion = AssertionUtilityFunctions.assertValues(returnAssertion, "User Account ${expectedUserAccount.jtvUuid} Last Name", expectedUserAccount.lastName, actualUserAccount.lastName)
        returnAssertion = AssertionUtilityFunctions.assertValues(returnAssertion, "User Account ${expectedUserAccount.jtvUuid} Username", expectedUserAccount.userName, actualUserAccount.userName)
        returnAssertion = AssertionUtilityFunctions.assertValues(returnAssertion, "User Account ${expectedUserAccount.jtvUuid} UUID", expectedUserAccount.jtvUuid, actualUserAccount.jtvUuid)

        return returnAssertion
    }

    static DatabaseUserAccount getUserAccountFromX5byKeycloakUuid(String kcUuid) {
        UserAccountDao userAccountDao = new UserAccountDao()
        return userAccountDao.getUserAccountByKeycloakUuid(kcUuid)
    }

    static DatabaseUserAccount getUserAccountFromX5byJtvUuid(String jtvUuid) {
        UserAccountDao userAccountDao = new UserAccountDao()
        return userAccountDao.getUserAccountByJtvUuid(jtvUuid)
    }

    static DatabaseUserAccount mapUserAccountFromResponseAndAccessToken(String responseBody, String token) {
        LazyMap decodedToken = decodeToken(token)
        def responseMap = new JsonSlurper().parseText(responseBody)

        return new DatabaseUserAccount(
                  firstName:    decodedToken.given_name
                , lastName:     decodedToken.family_name
                , userName:     decodedToken.preferred_username
                , jtvUuid:      responseMap?.userId
        )
    }

    static DatabaseUserAccount mapResponseBodyToUserAccount(String responseBody) {
        log.info("[mapResponseBodyToUserAccount] responseBody = " + responseBody)
        def responseMap = new JsonSlurper().parseText(responseBody)

        return new DatabaseUserAccount(
                firstName: responseMap.user.firstName
                , lastName: responseMap.user.lastName
                , userName: responseMap.user.userName
                , jtvUuid: responseMap.user.userId
        )
    }

    static LazyMap decodeToken(String tokenString) {
        def splitToken = tokenString.split("\\.")
        def tokenBody

        tokenBody = new JsonSlurper().parseText(new String(splitToken[1].decodeBase64())) as LazyMap

        return tokenBody
    }
}
