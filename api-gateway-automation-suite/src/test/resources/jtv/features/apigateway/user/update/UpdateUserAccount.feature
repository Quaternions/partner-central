@partner-central @update-user-account @user-account @api-gateway @wip
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
    And an error message of <message>
    Examples:
    | uuidKeyword | code | message                               |
    | an invalid  | 400  | user account uuid is invalid          |
    | an empty    | 404  | NotFoundException: HTTP 404 Not Found |
    | a null      | 400  | user account uuid is invalid          |

    #The following scenario is returning a nonstandard response body for this particular 404.  Until I am able to create some sort of asserter, currently leaving it alone.
Scenario: POST changes to the enterprise user - Unknown UUID
    Given a Keycloak user role of ROLE_ManageMyUserAccount
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    When a request is made to update the user with an unknown uuid in the path
    Then a response code of 404 should be returned

Scenario Outline: POST changes to the enterprise user account - Valid Keycloak Roles
    Given a Keycloak user role of <role>
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    When a request is made to update the enterprise user account
    Then a response code of <code> should be returned
    And the updated user account information should be persistent in the database
    Examples:
    | role                      | code |
    | ROLE_ManageMyUserAccount  | 200  |
    | ROLE_UpdateMyUserAccount  | 200  |

Scenario Outline: POST changes to the enterprise user account - Invalid Keycloak Roles
    Given a Keycloak user role of <role>
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    When a request is made to update the enterprise user account
    Then a response code of <code> should be returned
    And an error message of <error-message>
    Examples:
    | role                      | code | error-message |
    | ROLE_UpdateAnyUserAccount | 403  | something     |
    | ROLE_ManageAnyUserAccount | 403  | soemthing     |

Scenario Outline: POST changes to the enterprise user account - Updating firstname
    Given a Keycloak user role of ROLE_ManageMyUserAccount
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the user needs to update their user account first name with <keyword>
    #    When a request is made to update the user account with <keyword>
    When a request is made to update the enterprise user account
    Then a response code of 400 should be returned
    Examples:
    | keyword             |
#    | a null              |
#    | an empty string     |
    | too many characters |

#    | role                      | keyword         |
#    | ROLE_ManageMyUserAccount  | null firstname  |
#    | ROLE_ManageMyUserAccount  | empty firstname |
#    | ROLE_ManageMyUserAccount  | null lastname   |
#    | ROLE_ManageMyUserAccount  | empty lastname  |
#    | ROLE_ManageMyUserAccount  | null username   |
#    | ROLE_ManageMyUserAccount  | empty username  |
#    | ROLE_ManageAnyUserAccount | null firstname  |
#    | ROLE_ManageAnyUserAccount | empty firstname |
#    | ROLE_ManageAnyUserAccount | null lastname   |
#    | ROLE_ManageAnyUserAccount | empty lastname  |
#    | ROLE_ManageAnyUserAccount | null username   |
#    | ROLE_ManageAnyUserAccount | empty username  |
#    | ROLE_UpdateMyUserAccount  | null firstname  |
#    | ROLE_UpdateMyUserAccount  | empty firstname |
#    | ROLE_UpdateMyUserAccount  | null lastname   |
#    | ROLE_UpdateMyUserAccount  | empty lastname  |
#    | ROLE_UpdateMyUserAccount  | null username   |
#    | ROLE_UpdateMyUserAccount  | empty username  |
#    | ROLE_UpdateAnyUserAccount | null firstname  |
#    | ROLE_UpdateAnyUserAccount | empty firstname |
#    | ROLE_UpdateAnyUserAccount | null lastname   |
#    | ROLE_UpdateAnyUserAccount | empty lastname  |
#    | ROLE_UpdateAnyUserAccount | null username   |
#    | ROLE_UpdateAnyUserAccount | empty username  |

Scenario: POST changes to the enterprise user using existing first & last names
    Given a Keycloak user role of ROLE_ManageMyUserAccount
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And an existing user account with the same first and last names
    When a request is made to update the enterprise user account
    Then a response code of 200 should be returned