@partner-central @partner-profile @update-partner-profile @api-gateway @smoke @wip
Feature: POST the update of an enterprise partner
########################################
#    POST /partner/{partner-id}
########################################

@common-path
Scenario: POST the update of an enterprise partner - common path
    Given a Keycloak user role of ROLE_ManageMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
    When a request is made to update the current partner profile
    Then a response code of 200 should be returned
    And the database should reflect the data from the update partner profile request

Scenario Outline: POST the update of an enterprise partner - validate keycloak user roles
    Given a Keycloak user role of <kc-role>
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
    When a request is made to update the current partner profile
    Then a response code of <code> should be returned
    And the database should reflect the data from the update partner profile request
    Examples:
    | kc-role                      | code |
    | ROLE_ManageMyPartnerProfile  | 200  |
    | ROLE_ManageAnyPartnerProfile | 200  |
    | ROLE_UpdateMyPartnerProfile  | 200  |
    | ROLE_UpdateAnyPartnerProfile | 200  |

Scenario Outline: POST the update of an enterprise partner - updating the <parameter> for the Partner Profile
    Given a Keycloak user role of ROLE_ManageMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the <parameter> is being updated for the partner profile
    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
    When a request is made to update the current partner profile
    Then a response code of 200 should be returned
    And the database should reflect the data from the update partner profile request
    Examples:
    | parameter              |
    | legal entity name      |
    | operating name         |
    | partner account status |

Scenario Outline: POST the update of an enterprise partner - updating the <parameter> for the Primary Contact
    Given a Keycloak user role of ROLE_ManageMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
    And the <parameter> is being updated for the primary contact of the partner profile
    When a request is made to update the current partner profile
    Then a response code of 200 should be returned
    And the database should reflect the data from the update partner profile request
    Examples:
    | parameter          |
    | firstname          |
    | lastname           |
    | email              |
    | title              |
    | subscriber number  |
    | phone extension    |
    | phone country code |

Scenario Outline: POST the update of an enterprise partner - updating the <parameter> for the Partner Site(s)
    Given a Keycloak user role of ROLE_ManageMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the <parameter> is being updated for each site of the partner profile
    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
    When a request is made to update the current partner profile
    Then a response code of 200 should be returned
    And the database should reflect the data from the update partner profile request
    Examples:
    | parameter           |
    | site name           |
    | usages              |
    | address line one    |
    | address line two    |
    | city                |
    | state or province   |
    | postal code         |

Scenario: POST the update of an enterprise partner - permission validation
    Given a Keycloak user role of ROLE_ManageMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
    When a request is made to update the current partner profile
    Then a response code of 200 should be returned
    And the database should reflect the data from the update partner profile request

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
    And the database should reflect the data from the update partner profile request
    Examples:
    | validation-keyword                      | code |
    | is valid                                | 200  |
    | is at maximum allowed character limit   | 200  |
    | contains special characters             | 200  |

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
    And the database should reflect the data from the update partner profile request
    Examples:
    | validation-keyword                      | code |
    | is valid                                | 200  |
    | is at maximum allowed character limit   | 200  |
    | contains special characters             | 200  |

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
    And the database should reflect the data from the update partner profile request
    Examples:
    | validation-keyword                      | code |
    | is valid                                | 200  |
    | is at maximum allowed character limit   | 200  |
    | contains special characters             | 200  |

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
    And the database should reflect the data from the update partner profile request
    Examples:
    | validation-keyword                      | code |
    | is valid                                | 200  |
    | is at maximum allowed character limit   | 200  |
    | contains special characters             | 200  |

Scenario Outline: POST the update of an enterprise partner - Field validation Partner Profile - Primary Contact (title)
    Given a Keycloak user role of ROLE_ManageMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
    And the title <validation-keyword> for the partner profile primary contact
    When a request is made to update the current partner profile
    Then a response code of <code> should be returned
    And the database should reflect the data from the update partner profile request
    Examples:
    | validation-keyword                      | code |
    | is null                                 | 200  |
    | is empty                                | 200  |
    | is valid                                | 200  |
    | is at maximum allowed character limit   | 200  |
    | contains special characters             | 200  |

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
    And the database should reflect the data from the update partner profile request
    Examples:
    | validation-keyword                         | code |
    | is valid                                   | 200  |
    | is at maximum allowed character limit      | 200  |
    | contains minimum requirements for an email | 200  |

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
    And the database should reflect the data from the update partner profile request
    Examples:
    | validation-keyword                      | code |
    | is null                                 | 200  |
    | is empty                                | 200  |
    | is valid                                | 200  |
    | is at maximum allowed character limit   | 200  |

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
    And the database should reflect the data from the update partner profile request
    Examples:
    | validation-keyword                      | code |
    | is null                                 | 200  |
    | is empty                                | 200  |
    | is valid                                | 200  |
    | is at maximum allowed character limit   | 200  |

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
    And the database should reflect the data from the update partner profile request
    Examples:
    | validation-keyword                      | code |
    | is null                                 | 200  |
    | is empty                                | 200  |
    | is valid                                | 200  |
    | is at maximum allowed character limit   | 200  |

Scenario Outline: POST the update of an enterprise partner - Field validation Partner Site - Site Name
    Given a Keycloak user role of ROLE_ManageMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the partner site also has a usage of billing
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
    And the site name <validation-keyword> for the partner profile site
    When a request is made to update the current partner profile
    Then a response code of <code> should be returned
    And the database should reflect the data from the update partner profile request
    Examples:
    | validation-keyword                      | code |
    | is null                                 | 200  |
    | is empty                                | 200  |
    | is valid                                | 200  |
    | is at maximum allowed character limit   | 200  |
    | contains special characters             | 200  |

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
    And the database should reflect the data from the update partner profile request
    Examples:
    | validation-keyword                      | code |
    | is valid                                | 200  |
    | is at maximum allowed character limit   | 200  |
    | contains special characters             | 200  |

Scenario Outline: POST the update of an enterprise partner - Field validation Partner Site - Address Line Two
    Given a Keycloak user role of ROLE_ManageMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And a partner already created that has completed registration
    And has a partner site with usage type business
    And the partner site also has a usage of billing
    And the current user is the primary account manager for the partner
    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
    And the address line two <validation-keyword> for the partner profile site
    When a request is made to update the current partner profile
    Then a response code of <code> should be returned
    And the database should reflect the data from the update partner profile request
    Examples:
    | validation-keyword                      | code |
    | is null                                 | 200  |
    | is empty                                | 200  |
    | is valid                                | 200  |
    | is at maximum allowed character limit   | 200  |
    | contains special characters             | 200  |

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
    And the database should reflect the data from the update partner profile request
    Examples:
    | validation-keyword                      | code |
    | is valid                                | 200  |
    | is at maximum allowed character limit   | 200  |

