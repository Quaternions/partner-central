@partner-central @partner-profile @update-partner-profile @api-gateway @smoke @wip
Feature: POST the update of an enterprise partner
########################################
#    POST /partner/{partner-id}
########################################

Scenario: POST the update of an enterprise partner - validate keycloak user roles
    Given a Keycloak user role of ROLE_ViewMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
    When a request is made to update the current partner profile
    Then a response code of 403 should be returned

Scenario Outline: POST the update of an enterprise partner - permission validation
    Given a Keycloak user role of ROLE_ManageMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted <access-level> access to <access-type> in core
    When a request is made to update the current partner profile
    Then a response code of <response-code> should be returned
    Examples:
    | access-level | access-type         | response-code |
    | VIEW         | PC_BUSINESS_PROFILE | 404           |
    | VIEW_EDIT    | PC_PAYMENT_METHODS  | 404           |

Scenario Outline: POST the update of an enterprise partner - Field validation Partner Profile - legalEntityName
    Given a Keycloak user role of ROLE_ManageMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
    And the legal entity name <validation-keyword> for the partner profile
    When a request is made to update the current partner profile
    Then a response code of <code> should be returned
    Examples:
    | validation-keyword                      | code |
    | is null                                 | 400  |
    | is empty                                | 400  |
    | exceeds maximum allowed character limit | 400  |

Scenario Outline: POST the update of an enterprise partner - Field validation Partner Profile - operatingName
    Given a Keycloak user role of ROLE_ManageMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
    And the operating name <validation-keyword> for the partner profile
    When a request is made to update the current partner profile
    Then a response code of <code> should be returned
    Examples:
    | validation-keyword                      | code |
    | is null                                 | 400  |
    | is empty                                | 400  |
    | exceeds maximum allowed character limit | 400  |

Scenario Outline: POST the update of an enterprise partner - Field validation Partner Profile - Primary Contact (firstName)
    Given a Keycloak user role of ROLE_ManageMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
    And the firstName <validation-keyword> for the partner profile primary contact
    When a request is made to update the current partner profile
    Then a response code of <code> should be returned
    Examples:
    | validation-keyword                      | code |
    | is null                                 | 400  |
    | is empty                                | 400  |
    | exceeds maximum allowed character limit | 400  |

Scenario Outline: POST the update of an enterprise partner - Field validation Partner Profile - Primary Contact (lastName)
    Given a Keycloak user role of ROLE_ManageMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
    And the lastName <validation-keyword> for the partner profile primary contact
    When a request is made to update the current partner profile
    Then a response code of <code> should be returned
    Examples:
    | validation-keyword                      | code |
    | is null                                 | 400  |
    | is empty                                | 400  |
    | exceeds maximum allowed character limit | 400  |

Scenario: POST the update of an enterprise partner - Field validation Partner Profile - Primary Contact (title)
    Given a Keycloak user role of ROLE_ManageMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
    And the title exceeds maximum allowed character limit for the partner profile primary contact
    When a request is made to update the current partner profile
    Then a response code of 400 should be returned

Scenario Outline: POST the update of an enterprise partner - Field validation Partner Profile - Primary Contact (email)
    Given a Keycloak user role of ROLE_ManageMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
    And the email <validation-keyword> for the partner profile primary contact
    When a request is made to update the current partner profile
    Then a response code of <code> should be returned
    Examples:
    | validation-keyword                         | code |
    | is null                                    | 400  |
    | is empty                                   | 400  |
    | exceeds maximum allowed character limit    | 400  |

Scenario Outline: POST the update of an enterprise partner - Field validation Partner Profile - Primary Contact (phone subscriber number)
    Given a Keycloak user role of ROLE_ManageMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
    And the phone subscriber number <validation-keyword> for the partner profile primary contact
    When a request is made to update the current partner profile
    Then a response code of <code> should be returned
    Examples:
    | validation-keyword                      | code |
    | exceeds maximum allowed character limit | 400  |
    | contains non-numeric characters         | 400  |

Scenario Outline: POST the update of an enterprise partner - Field validation Partner Profile - Primary Contact (phone country code)
    Given a Keycloak user role of ROLE_ManageMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
    And the phone country code <validation-keyword> for the partner profile primary contact
    When a request is made to update the current partner profile
    Then a response code of <code> should be returned
    Examples:
    | validation-keyword                      | code |
    | exceeds maximum allowed character limit | 400  |
    | contains non-numeric characters         | 400  |

Scenario Outline: POST the update of an enterprise partner - Field validation Partner Profile - Primary Contact (phone extension)
    Given a Keycloak user role of ROLE_ManageMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
    And the phone extension <validation-keyword> for the partner profile primary contact
    When a request is made to update the current partner profile
    Then a response code of <code> should be returned
    Examples:
    | validation-keyword                      | code |
    | exceeds maximum allowed character limit | 400  |
    | contains non-numeric characters         | 400  |

Scenario: POST the update of an enterprise partner - Field validation Partner Site - Site Name
    Given a Keycloak user role of ROLE_ManageMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the partner site also has a usage of billing
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
    And the site name exceeds maximum allowed character limit for the partner profile site
    When a request is made to update the current partner profile
    Then a response code of 400 should be returned

Scenario Outline: POST the update of an enterprise partner - Field validation Partner Site - Address Line One
    Given a Keycloak user role of ROLE_ManageMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the partner site also has a usage of billing
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
    And the address line one <validation-keyword> for the partner profile site
    When a request is made to update the current partner profile
    Then a response code of <code> should be returned
    Examples:
    | validation-keyword                      | code |
    | is null                                 | 400  |
    | is empty                                | 400  |
    | exceeds maximum allowed character limit | 400  |

Scenario: POST the update of an enterprise partner - Field validation Partner Site - Address Line Two
    Given a Keycloak user role of ROLE_ManageMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the partner site also has a usage of billing
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
    And the address line two exceeds maximum allowed character limit for the partner profile site
    When a request is made to update the current partner profile
    Then a response code of 400 should be returned

Scenario Outline: POST the update of an enterprise partner - Field validation Partner Site - City
    Given a Keycloak user role of ROLE_ManageMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the partner site also has a usage of billing
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
    And the city <validation-keyword> for the partner profile site
    When a request is made to update the current partner profile
    Then a response code of <code> should be returned
    Examples:
    | validation-keyword                      | code |
    | is null                                 | 400  |
    | is empty                                | 400  |
    | exceeds maximum allowed character limit | 400  |
    | contains special characters             | 400  |

