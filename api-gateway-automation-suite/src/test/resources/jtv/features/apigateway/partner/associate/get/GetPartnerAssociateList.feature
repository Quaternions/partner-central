@partner-central @partner-associate @get-partner-associate-list @api-gateway @smoke @wip @positive
Feature: GET a list of partner associates
###############################################################
#    GET a list of partner associates                         #
#    GET /partner/{partner-id}/associate                      #
#    GET /partner/{partner-id}/associate?associate-filter=ME  #
#    GET /partner/{partner-id}/associate?associate-filter=ALL #
###############################################################

@common-path
Scenario: GET a list of partner associates - common path
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the partner has another associate
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    When a request is made to get a list of associates for the partner with NO filter
    Then a response code of 200 should be returned
    And the get associate response body persists the data from the database

Scenario Outline: GET a list of partner associates - using the associate-filter
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the partner has another associate
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    When a request is made to get a list of associates for the partner with <filter-keyword> filter
    Then a response code of <code> should be returned
    And the get associate response body persists the data from the database
    Examples:
    | filter-keyword | code |
    | NO             | 200  |
    | ALL            | 200  |
    | ME             | 200  |

Scenario Outline: GET a list of partner associates - Keycloak roles
    Given a Keycloak user role of <kc-role>
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is <associate-type> for the partner
    And the partner has another associate
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    When a request is made to get a list of associates for the partner with NO filter
    Then a response code of <code> should be returned
    And the get associate response body persists the data from the database
    Examples:
    | associate-type              | kc-role                        | code |
    | the primary account manager | ROLE_ManageMyPartnerAssociate  | 200  |
    | the primary account manager | ROLE_ViewMyPartnerAssociate    | 200  |
    | an associate                | ROLE_ManageMyPartnerAssociate  | 200  |
    | an associate                | ROLE_ViewMyPartnerAssociate    | 200  |

Scenario Outline: GET a list of partner associates - Associate with permission
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is <associate-type> for the partner
    And the partner has another associate
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    When a request is made to get a list of associates for the partner with NO filter
    Then a response code of 200 should be returned
    And the get associate response body persists the data from the database
    Examples:
    | associate-type              |
    | the primary account manager |
    | an associate                |
