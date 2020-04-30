@partner-central @update-user-account @user-account @api-gateway @smoke @wip
Feature: Update User Account API
    Updating an enterprise user account
########################################
#    POST /user/{user-id}
########################################

@common-path
Scenario: POST changes to the enterprise user - common path
    Given a Keycloak user role of ROLE_ManageMyUserAccount
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    When a request is made to update the enterprise user account
    Then a response code of 200 should be returned
    And the updated user account information should be persistent in the database

Scenario: POST changes to the enterprise user with a null access token
    Given a Keycloak user role of ROLE_ManageMyUserAccount
    And an origin of jtv-partner-central
    And a Keycloak user created
    And a null access token
    When a request is made to update the enterprise user account
    Then a response code of 401 should be returned

Scenario: POST changes to the enterprise user with a bad Keycloak user role
    Given a Keycloak user role of ROLE_UpdateAnyUserProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    When a request is made to update the enterprise user account
    Then a response code of 403 should be returned
    And an error message of AccessDeniedException: Access is denied

Scenario Outline: POST changes to the enterprise user - UUID validation
    Given a Keycloak user role of ROLE_ManageMyUserAccount
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    When a request is made to update the user with <uuidKeyword> uuid in the path
    Then a response code of <code> should be returned
    Examples:
    | uuidKeyword | code |
    | an invalid  | 400  |
    | an unknown  | 404  |
    | an empty    | 404  |
    | a null      | 400  |

Scenario Outline: POST changes to the enterprise user account - Keycloak Role Validation
    Given a Keycloak user role of <role>
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    When a request is made to update the <keyword> for the user account
    Then a response code of <code> should be returned
    Examples:
    | role                      | keyword    | code |
    | ROLE_ManageMyUserAccount  | firstname  | 200  |
    | ROLE_ManageMyUserAccount  | lastname   | 200  |
    | ROLE_ManageMyUserAccount  | username   | 200  |
    | ROLE_ManageAnyUserAccount | firstname  | 403  |
    | ROLE_ManageAnyUserAccount | lastname   | 403  |
    | ROLE_ManageAnyUserAccount | username   | 403  |
    | ROLE_UpdateMyUserAccount  | firstname  | 200  |
    | ROLE_UpdateMyUserAccount  | lastname   | 200  |
    | ROLE_UpdateMyUserAccount  | username   | 200  |
    | ROLE_UpdateAnyUserAccount | firstname  | 403  |
    | ROLE_UpdateAnyUserAccount | lastname   | 403  |
    | ROLE_UpdateAnyUserAccount | username   | 403  |

Scenario Outline: POST changes to the enterprise user using null or empty values
    Given a Keycloak user role of <role>
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    When a request is made to update the user account with <keyword>
    Then a response code of 400 should be returned
    Examples:
    | role                      | keyword         |
    | ROLE_ManageMyUserAccount  | null firstname  |
    | ROLE_ManageMyUserAccount  | empty firstname |
    | ROLE_ManageMyUserAccount  | null lastname   |
    | ROLE_ManageMyUserAccount  | empty lastname  |
    | ROLE_ManageMyUserAccount  | null username   |
    | ROLE_ManageMyUserAccount  | empty username  |
    | ROLE_ManageAnyUserAccount | null firstname  |
    | ROLE_ManageAnyUserAccount | empty firstname |
    | ROLE_ManageAnyUserAccount | null lastname   |
    | ROLE_ManageAnyUserAccount | empty lastname  |
    | ROLE_ManageAnyUserAccount | null username   |
    | ROLE_ManageAnyUserAccount | empty username  |
    | ROLE_UpdateMyUserAccount  | null firstname  |
    | ROLE_UpdateMyUserAccount  | empty firstname |
    | ROLE_UpdateMyUserAccount  | null lastname   |
    | ROLE_UpdateMyUserAccount  | empty lastname  |
    | ROLE_UpdateMyUserAccount  | null username   |
    | ROLE_UpdateMyUserAccount  | empty username  |
    | ROLE_UpdateAnyUserAccount | null firstname  |
    | ROLE_UpdateAnyUserAccount | empty firstname |
    | ROLE_UpdateAnyUserAccount | null lastname   |
    | ROLE_UpdateAnyUserAccount | empty lastname  |
    | ROLE_UpdateAnyUserAccount | null username   |
    | ROLE_UpdateAnyUserAccount | empty username  |

Scenario: POST changes to the enterprise user using existing first & last names
    Given a Keycloak user role of ROLE_ManageMyUserAccount
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And an existing user account with the same first and last names
    When a request is made to update the enterprise user account
    Then a response code of 200 should be returned