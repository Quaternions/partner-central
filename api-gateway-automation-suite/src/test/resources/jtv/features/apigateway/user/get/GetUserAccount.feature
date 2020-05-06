@partner-central @get-user-account @user-account @api-gateway @smoke @wip
Feature: Get User Account API
    Retrieving an enterprise user account
########################################
#    GET /user/me
########################################
@common-path
Scenario: GET /user/me - common path
    Given a Keycloak user role of ROLE_ViewMyUserAccount
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    When a request is made to get the user's enterprise account
    Then a response code of 200 should be returned
    And the user account information returned should be equal to what is persisted in the enterprise

Scenario: GET /user/me - without ROLE_ViewMyUserAccount
    Given a Keycloak user role of ROLE_ViewMyUserProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    When a request is made to get the user's enterprise account
    Then a response code of 403 should be returned
    And an error message of AccessDeniedException: Access is denied

Scenario: GET /user/me - an account that has not been created yet
    Given a Keycloak user role of ROLE_ViewMyUserAccount
    And an origin of jtv-partner-central
    And a Keycloak user created
    When a request is made to get the user's enterprise account
    Then a response code of 404 should be returned
    And an error message of user account not found

#######################################################
# GET /user/{jtvUUID}
#######################################################
@common-path
Scenario: GET /user/{user-id} - common path
    Given a Keycloak user role of ROLE_ViewMyUserAccount
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    When a request is made to get user account by JTV UUID
    Then a response code of 200 should be returned
    And the user account information returned should be equal to what is persisted in the enterprise

Scenario: GET /user/{user-id} - another user account
  Given a Keycloak user role of ROLE_ViewMyUserAccount
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that does not belong to the current keycloak user
    When a request is made to get user account by JTV UUID
    Then a response code of 404 should be returned
    And an error message of user account does not exist

Scenario: GET /user/{user-id} - unknown UUID
    Given a Keycloak user role of ROLE_ViewMyUserAccount
    And an origin of jtv-partner-central
    And a Keycloak user created
    When a request is made to get user account by JTV UUID with a uuid that is unknown
    Then a response code of 404 should be returned
    And an error message of user account does not exist

Scenario: GET /user/{user-id} - invalid uuid
    Given a Keycloak user role of ROLE_ViewMyUserAccount
    And an origin of jtv-partner-central
    And a Keycloak user created
    When a request is made to get user account by JTV UUID with a uuid that is invalid
    Then a response code of 400 should be returned
    And an error message of user account uuid is invalid

Scenario: GET /user/{user-id} - without the correct keycloak role
    Given a Keycloak user role of ROLE_ViewMyUserProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    When a request is made to get user account by JTV UUID
    Then a response code of 403 should be returned
    And an error message of AccessDeniedException: Access is denied


