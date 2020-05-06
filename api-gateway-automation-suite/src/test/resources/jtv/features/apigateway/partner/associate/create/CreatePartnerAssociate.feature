@partner-central @partner-associate @create-partner-associate @api-gateway @smoke @wip
Feature: POST to create a partner associate
########################################
#    POST /partner/{partner-id}/associate
########################################

@common-path
Scenario: POST to create a partner associate - common path
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    When a request is made to create a partner associate
    Then a response code of 201 should be returned
    And the created associate should be persistent in the database

Scenario: POST to create a partner associate based on keycloak roles
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    When a request is made to create a partner associate
    Then a response code of 201 should be returned
    And the created associate should be persistent in the database

Scenario: POST to create a partner associate - firstName validation
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    And the first name is at maximum allowed character limit in the create partner associate request body
    When a request is made to create a partner associate
    Then a response code of 201 should be returned
    And the created associate should be persistent in the database

Scenario: POST to create a partner associate - lastName validation
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    And the last name is at maximum allowed character limit in the create partner associate request body
    When a request is made to create a partner associate
    Then a response code of 201 should be returned
    And the created associate should be persistent in the database

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
    Then a response code of 201 should be returned
    And the created associate should be persistent in the database
    Examples:
    | keyword                                    |
    | is at maximum allowed character limit      |
    | contains minimum requirements for an email |

Scenario Outline: POST to create a partner associate - title validation
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    And the title <keyword> in the create partner associate request body
    When a request is made to create a partner associate
    Then a response code of 201 should be returned
    And the created associate should be persistent in the database
    Examples:
    | keyword                                    |
    | is empty                                   |
    | is null                                    |
    | is at maximum allowed character limit      |

Scenario: POST to create a partner associate - phone subscriber number validation
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    And the phone subscriber number is at maximum allowed character limit in the create partner associate request body
    When a request is made to create a partner associate
    Then a response code of 201 should be returned
    And the created associate should be persistent in the database

Scenario: POST to create a partner associate - phone country code validation
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    And the phone country code is at maximum allowed character limit in the create partner associate request body
    When a request is made to create a partner associate
    Then a response code of 201 should be returned
    And the created associate should be persistent in the database

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
    Then a response code of 201 should be returned
    And the created associate should be persistent in the database
    Examples:
    | keyword                                    |
    | is empty                                   |
    | is null                                    |
    | is at maximum allowed character limit      |

Scenario: POST to create a partner associate as an associate with VIEW_EDIT for PC_ASSOCIATES core permissions
    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is an associate for the partner
    And the user is granted VIEW_EDIT access to PC_ASSOCIATES in core
    When a request is made to create a partner associate
    Then a response code of 201 should be returned
    And the created associate should be persistent in the database

