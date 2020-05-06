@partner-central @partner-associate @create-partner-associate @api-gateway @smoke @wip
Feature: POST to create a partner associate
########################################
#    POST /partner/{partner-id}/associate
########################################
Scenario Outline: POST to create a partner associate based on keycloak roles
    Given a Keycloak user role of <kc-roles>
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    When a request is made to create a partner associate
    Then a response code of 403 should be returned
    Examples:
    | kc-roles                       |
    | ROLE_ManageAnyPartnerAssociate |
    | ROLE_ManageMyPartnerProfile    |

Scenario Outline: POST to create a partner associate for a different partner
    Given a Keycloak user role of <kc-role>
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is not an associate for the partner
    When a request is made to create a partner associate
    Then a response code of <response-code> should be returned
    Examples:
    | kc-role                        | response-code |
    | ROLE_ManageMyPartnerAssociate  | 404           |
    | ROLE_ManageAnyPartnerAssociate | 403           |

Scenario: POST to create a partner associate without partner UUID in path parameter
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    When a request is made to create a partner associate without partner UUID
    Then a response code of 400 should be returned

Scenario Outline: POST to create a partner associate - firstName validation
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    And the first name <keyword> in the create partner associate request body
    When a request is made to create a partner associate
    Then a response code of 400 should be returned
    Examples:
    | keyword                                 |
    | is empty                                |
    | is null                                 |
    | exceeds maximum allowed character limit |

Scenario Outline: POST to create a partner associate - lastName validation
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    And the last name <keyword> in the create partner associate request body
    When a request is made to create a partner associate
    Then a response code of 400 should be returned
    Examples:
    | keyword                                 |
    | is empty                                |
    | is null                                 |
    | exceeds maximum allowed character limit |

Scenario Outline: POST to create a partner associate - email validation
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    And the email <keyword> in the create partner associate request body
    When a request is made to create a partner associate
    Then a response code of 400 should be returned
    Examples:
    | keyword                                    |
    | is empty                                   |
    | is null                                    |
    | exceeds maximum allowed character limit    |
    | is not a valid email address               |

Scenario: POST to create a partner associate - title validation
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    And the title exceeds maximum allowed character limit in the create partner associate request body
    When a request is made to create a partner associate
    Then a response code of 400 should be returned

Scenario Outline: POST to create a partner associate - phone subscriber number validation
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    And the phone subscriber number <keyword> in the create partner associate request body
    When a request is made to create a partner associate
    Then a response code of 400 should be returned
    Examples:
    | keyword                                    |
    | is empty                                   |
    | is null                                    |
    | exceeds maximum allowed character limit    |
    | contains non-numeric characters            |

Scenario Outline: POST to create a partner associate - phone country code validation
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    And the phone country code <keyword> in the create partner associate request body
    When a request is made to create a partner associate
    Then a response code of 400 should be returned
    Examples:
    | keyword                                    |
    | is empty                                   |
    | is null                                    |
    | exceeds maximum allowed character limit    |
    | contains non-numeric characters            |

Scenario Outline: POST to create a partner associate - phone extension validation
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    And the phone extension <keyword> in the create partner associate request body
    When a request is made to create a partner associate
    Then a response code of 400 should be returned
    Examples:
    | keyword                                    |
    | exceeds maximum allowed character limit    |
    | contains non-numeric characters            |

Scenario Outline: POST to create a partner associate - partner associate type
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    And the partner associate type <keyword> in the create partner associate request body
    When a request is made to create a partner associate
    Then a response code of 400 should be returned
    Examples:
    | keyword                                    |
    | is empty                                   |
    | is null                                    |
    | is not valid                               |

Scenario: POST to create a partner associate as a second primary account manager
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    When a request is made to create a second primary account manager
    Then a response code of 409 should be returned

Scenario: POST to create a partner associate as an associate with VIEW for PC_ASSOCIATES core permissions
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is an associate for the partner
    And the user is granted VIEW access to PC_ASSOCIATES in core
    When a request is made to create a partner associate
    Then a response code of 404 should be returned

