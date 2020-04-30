@partner-central @partner-associate @get-partner-associate-list @api-gateway @smoke @wip
Feature: GET a list of partner associates - negative test cases
###############################################################
#    GET a list of partner associates                         #
#    GET /partner/{partner-id}/associate                      #
#    GET /partner/{partner-id}/associate?associate-filter=ME  #
#    GET /partner/{partner-id}/associate?associate-filter=ALL #
###############################################################

Scenario: GET a list of partner associates - using the associate-filter
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the partner has another associate
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    When a request is made to get a list of associates for the partner with BLAH filter
    Then a response code of 400 should be returned
    And an error message of associate-filter is invalid

Scenario Outline: GET a list of partner associates - Keycloak roles
    Given a Keycloak user role of ROLE_UpdateAnyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is <associate-type> for the partner
    And the partner has another associate
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    When a request is made to get a list of associates for the partner with NO filter
    Then a response code of 403 should be returned
    Examples:
    | associate-type              |
    | the primary account manager |
    | an associate                |
    | not an associate            |

Scenario: GET a list of partner associates - With a null access token
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    And a null access token
    When a request is made to get a list of associates for the partner with NO filter
    Then a response code of 401 should be returned

Scenario Outline: GET a list of partner associates - with no permissions
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is <associate-type> for the partner
    And the partner has another associate
    When a request is made to get a list of associates for the partner with NO filter
    Then a response code of 400 should be returned
    Examples:
    | associate-type              |
    | the primary account manager |
    | an associate                |
    | not an associate            |

@current
Scenario Outline: GET a list of partner associates - <uuid-keyword> UUID in the path parameter
    Given a Keycloak user role of <kc-roles>
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    And the partner-id path parameter has <uuid-keyword> UUID assigned to it
    When a request is made to get a list of associates for the partner with NO filter
    Then a response code of <code> should be returned
    Examples:
    | kc-roles                       | uuid-keyword         | code |
    | ROLE_ManageMyPartnerAssociate  | an unknown           | 404  |
    | ROLE_ManageAnyPartnerAssociate | an unknown           | 404  |
    | ROLE_ManageMyPartnerAssociate  | an invalid formatted | 400  |
    | ROLE_ManageAnyPartnerAssociate | an invalid formatted | 400  |
    | ROLE_ManageMyPartnerAssociate  | no                   | 403  |
    | ROLE_ManageAnyPartnerAssociate | no                   | 403  |
    | ROLE_ManageMyPartnerAssociate  | another partner's    | 404  |
    | ROLE_ManageAnyPartnerAssociate | another partner's    | 404  |

