@partner-central @get-partner-profile-list @partner-profile @api-gateway @smoke @wip @marketplace
Feature: Get partner profile list API
   Get partner profile list associated with the authenticated user
########################################
#    GET /partner
########################################

@common-path
Scenario: GET the partner profile list for the current user - common path
    Given a Keycloak user role of ROLE_ViewMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    When a request is made to get a partner using the current user's access token
    Then a response code of 200 should be returned
    And the response body contains the correct partner information

Scenario:  GET the partner profile list without an authentication token
    Given a Keycloak user role of ROLE_ViewMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a null access token
    When a request is made to get a partner using the current user's access token
    Then a response code of 401 should be returned

Scenario Outline: GET the partner profile list without ROLE_ViewMyPartnerProfile role in token
    Given a Keycloak user role of <roles>
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    When a request is made to get a partner using the current user's access token
    Then a response code of 403 should be returned
    And an error message of AccessDeniedException: Access is denied
    Examples:
        | roles                      |
        | ROLE_ViewAnyPartnerProfile |
        | ROLE_ViewMyUserAccount     |

Scenario Outline: GET the partner profile list that <partner-status>
    Given a Keycloak user role of ROLE_ViewMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that <partner-status>
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    When a request is made to get a partner using the current user's access token
    Then a response code of 200 should be returned
    And the response body contains the correct partner information
    Examples:
        | partner-status                     |
        | is active                          |
        | platform enrollment is active      |
        | platform enrollment was rejected   |
        | platform enrollment was suspended  |
        | platform enrollment was terminated |
        | is currently registering           |
        | has completed registration         |

Scenario: GET the partner profile list for a user that has no association with a partner
    Given a Keycloak user role of ROLE_ViewMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    When a request is made to get a partner using the current user's access token
    Then a response code of 404 should be returned
    And an error message of the resource requested could not be found

Scenario Outline: GET the partner profile list that have multiple site usages for a single site
    Given a Keycloak user role of ROLE_ViewMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the partner site also has a usage of <new-usage>
    And the current user is the primary account manager for the partner
    When a request is made to get a partner using the current user's access token
    Then a response code of 200 should be returned
    And the response body contains the correct partner information
    Examples:
        | new-usage |
        | billing   |
        | shipping  |
        | returns   |

Scenario: GET the partner profile list for a partner that has multiple sites
    Given a Keycloak user role of ROLE_ViewMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And has a partner site with usage type shipping
    And the current user is the primary account manager for the partner
    When a request is made to get a partner using the current user's access token
    Then a response code of 200 should be returned
    And the response body contains the correct partner information

Scenario: GET the partner profile list for a partner that has multiple sites with multiple usages
    Given a Keycloak user role of ROLE_ViewMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the partner site also has a usage of billing
    And has a partner site with usage type Shipping
    And the partner site also has a usage of Returns
    And the current user is the primary account manager for the partner
    When a request is made to get a partner using the current user's access token
    Then a response code of 200 should be returned
    And the response body contains the correct partner information

Scenario Outline: GET the partner profile list for user that is associated with multiple partners
    Given a Keycloak user role of ROLE_ViewMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is <first-associate-type> for the partner
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is <second-associate-type> for the partner
    When a request is made to get a partner using the current user's access token
    Then a response code of 200 should be returned
    And the response body contains the correct partner information
    Examples:
        | first-associate-type        | second-associate-type       |
        | the primary account manager | the primary account manager |
        | the primary account manager | an associate                |
        | an associate                | the primary account manager |
        | an associate                | an associate                |
