@partner-central @create-user-account @user-account @api-gateway @smoke @wip
Feature: Create User Account API
    Creating an enterprise user account
########################################
#    POST /user/me
########################################
    
@common-path
Scenario: POST to create an enterprise user account - common path
    Given a Keycloak user role of ROLE_CreateMyUserAccount
    And an origin of jtv-partner-central
    And a Keycloak user created
    When a request is made to create an enterprise user account
    Then a response code of 201 should be returned
    And the Keycloak user information should be saved as a user account in the enterprise

Scenario Outline: POST to create an enterprise user account based on Keycloak Roles
    Given a Keycloak user role of <role>
    And an origin of jtv-partner-central
    And a Keycloak user created
    When a request is made to create an enterprise user account
    Then a response code of <code> should be returned
    Examples:
        | role                      | code |
        | ROLE_CreateMyUserAccount  | 201  |
        | ROLE_ManageMyUserAccount  | 201  |
        | ROLE_ViewMyUserAccount    | 403  |


Scenario: POST to create an enterprise user account with a null access token
    Given a Keycloak user role of ROLE_CreateMyUserAccount
    And an origin of jtv-partner-central
    And a Keycloak user created
    And a null access token
    When a request is made to create an enterprise user account
    Then a response code of 401 should be returned

Scenario: POST to create an enterprise user account that already exists
    Given a Keycloak user role of ROLE_CreateMyUserAccount
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    When a request is made to create an enterprise user account
    Then a response code of 409 should be returned

