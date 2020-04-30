@partner-central @get-partner-profile @partner-profile @api-gateway @smoke @wip @marketplace
Feature: Get enterprise partner profile
########################################
#    GET /partner/{partner-id}
########################################

@common-path
Scenario: GET the enterprise partner
    Given a Keycloak user role of ROLE_ViewMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    When a request is made to get the partner using the partner uuid
    Then a response code of 200 should be returned
    And the response body contains the correct partner information

Scenario: GET the enterprise partner without an authentication token
    Given a Keycloak user role of ROLE_ViewMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a null access token
    When a request is made to get the partner using the partner uuid
    Then a response code of 401 should be returned

Scenario Outline: GET the enterprise partner using various keycloak roles - <roles>
    Given a Keycloak user role of <roles>
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    When a request is made to get the partner using the partner uuid
    Then a response code of <code> should be returned
    Examples:
        | roles                      | code |
        | ROLE_ViewMyPartnerProfile  | 200  |
        | ROLE_ViewAnyPartnerProfile | 200  |
        | ROLE_ViewMyUserAccount     | 403  |

Scenario: GET the enterprise partner that does not exist
    Given a Keycloak user role of ROLE_ViewMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    When a request is made to get the partner using the partner uuid
    Then a response code of 404 should be returned

Scenario Outline: GET the enterprise partner that have different partner statuses
    Given a Keycloak user role of ROLE_ViewMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that <partner-status>
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    When a request is made to get the partner using the partner uuid
    Then a response code of 200 should be returned
    And the response body contains the correct partner information
    Examples:
        | partner-status                     |
        | is active                          |
#        | platform enrollment is active      |
#        | platform enrollment was rejected   |
#        | platform enrollment was suspended  |
#        | platform enrollment was terminated |
        | is currently registering           |
        | has completed registration         |

Scenario Outline: GET the enterprise partner for a user has a different association with the partner
    Given a Keycloak user role of <kc_role>
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is <associate-type> for the partner
    When a request is made to get the partner using the partner uuid
    Then a response code of 200 should be returned
    And the response body contains the correct partner information
    Examples:
        | associate-type              | kc_role                    |
        | the primary account manager | ROLE_ViewMyPartnerProfile  |
        | the primary account manager | ROLE_ViewAnyPartnerProfile |
        | an associate                | ROLE_ViewMyPartnerProfile  |
        | an associate                | ROLE_ViewAnyPartnerProfile |

Scenario Outline: GET the enterprise partner that has no association with the current user that has <kc_role>
    Given a Keycloak user role of <kc_role>
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is not an associate for the partner
    When a request is made to get the partner using the partner uuid
    Then a response code of 404 should be returned
    And an error message of NotFoundException: The resource requested could not be found.
    Examples:
        | kc_role                    |
        | ROLE_ViewMyPartnerProfile  |
        | ROLE_ViewAnyPartnerProfile |

Scenario Outline: GET the enterprise partner that has multiple site usages for a single site
    Given a Keycloak user role of ROLE_ViewMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that is currently registering
    And has a partner site with usage type business
    And the partner site also has a usage of <new-usage>
    And the current user is the primary account manager for the partner
    When a request is made to get the partner using the partner uuid
    Then a response code of 200 should be returned
    And the response body contains the correct partner information
    Examples:
        | new-usage |
        | billing   |
        | shipping  |
        | returns   |

Scenario: GET the enterprise partner that has multiple sites
    Given a Keycloak user role of ROLE_ViewMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that is currently registering
    And has a partner site with usage type business
    And has a partner site with usage type shipping
    And the current user is the primary account manager for the partner
    When a request is made to get the partner using the partner uuid
    Then a response code of 200 should be returned
    And the response body contains the correct partner information

Scenario: GET the enterprise partner that has multiple sites with multiple usages
    Given a Keycloak user role of ROLE_ViewMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that is currently registering
    And has a partner site with usage type business
    And the partner site also has a usage of billing
    And has a partner site with usage type Shipping
    And the partner site also has a usage of Returns
    And the current user is the primary account manager for the partner
    When a request is made to get the partner using the partner uuid
    Then a response code of 200 should be returned
    And the response body contains the correct partner information
