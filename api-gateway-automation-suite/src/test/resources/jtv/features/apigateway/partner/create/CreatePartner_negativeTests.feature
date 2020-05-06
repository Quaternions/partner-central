@partner-central @create-partner-profile @partner-profile @api-gateway @smoke @wip
Feature: Create partner API Tests - Negative Tests
  Creating a partner in Partner Central - negative tests
########################################
#    POST /partner
########################################

Scenario Outline: POST to create the enterprise partner while using a different email or legal entity name
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains <email-keyword> email address as the partner invite
    And the request contains <legal-name-keyword> legal entity name as the partner invite
    When a request is made to create a partner
    Then a response code of 422 should be returned
    And an error message of partner invite does not match given partner
    Examples:
    | email-keyword | legal-name-keyword |
    | a different   | the same           |
    | a different   | a different        |

Scenario Outline: POST to create the enterprise partner - User tries to create a partner where the invite token <keyword>
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token <keyword>
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    When a request is made to create a partner
    Then a response code of 404 should be returned
    And an error message of the resource requested could not be found
    Examples:
    | keyword            |
    | has been accepted  |
    | has expired        |
    | has been cancelled |

Scenario Outline: POST to create the enterprise partner - Keycloak Role tests
    Given a Keycloak user role of <role-keyword>
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    When a request is made to create a partner
    Then a response code of 403 should be returned
    And an error code of Forbidden
    And an error message like AccessDeniedException: Access is denied
    Examples:
    | role-keyword                 |
    | ROLE_CreateAnyPartnerProfile |
    | ROLE_ManageAnyPartnerProfile |
    | ROLE_ViewMyPartnerProfile    |

Scenario Outline: POST to create the enterprise partner - Operating Name Validation
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    And the operating name that <legal-name-keyword>
    When a request is made to create a partner
    Then a response code of 400 should be returned
    And an error message of <error-message>
    Examples:
    | legal-name-keyword                      | error-message                    |
    | is empty                                | partner operating name must not be empty |
    | is null                                 | partner operating name must not be null  |
    | exceeds maximum allowed character limit | partner operating name is too long       |

Scenario Outline: POST to create the enterprise partner - Primary Contact Firstname Validation
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    And a primary contact firstname that <firstname-keyword>
    When a request is made to create a partner
    Then a response code of 400 should be returned
    And an error message of <error-message>
    Examples:
    | firstname-keyword                       | error-message                |
    | is empty                                | primary contact firstname must not be empty |
    | is null                                 | primary contact firstname must not be null  |
    | exceeds maximum allowed character limit | primary contact firstname is too long       |

Scenario Outline: POST to create the enterprise partner - Primary Contact Lastname Validation
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    And a primary contact lastname that <lastname-keyword>
    When a request is made to create a partner
    Then a response code of 400 should be returned
    And an error message of <error-message>
    Examples:
    | lastname-keyword                        | error-message               |
    | is empty                                | primary contact lastname must not be empty |
    | is null                                 | primary contact lastname must not be null  |
    | exceeds maximum allowed character limit | primary contact lastname is too long       |

Scenario: POST to create the enterprise partner - Primary Contact Title Validation
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    And a primary contact title that exceeds maximum allowed character limit
    When a request is made to create a partner
    Then a response code of 400 should be returned
    And an error message of primary contact title is too long

Scenario Outline: POST to create the enterprise partner - Primary Contact Phone Validation
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    And a primary contact subscriber number that <phone-keyword>
    When a request is made to create a partner
    Then a response code of 400 should be returned
    And an error message of <error-message>
    Examples:
    | phone-keyword                           | error-message                       |
    | exceeds maximum allowed character limit | primary contact phone subscriber number is too long |
    | contains non-numeric characters         | primary contact phone subscriber number is invalid  |

Scenario Outline: POST to create the enterprise partner - Primary Contact Phone Extension Validation
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    And a primary contact phone extension that <extension-keyword>
    When a request is made to create a partner
    Then a response code of 400 should be returned
    And an error message of <error-message>
    Examples:
    | extension-keyword                       | error-message                |
    | is empty                                | primary contact phone extension is too short |
    | exceeds maximum allowed character limit | primary contact phone extension is too long  |
    | contains non-numeric characters         | primary contact phone extension is invalid   |

Scenario Outline: POST to create the enterprise partner - Primary Contact Phone Country Code Validation
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    And a primary contact phone country code that <extension-keyword>
    When a request is made to create a partner
    Then a response code of 400 should be returned
    And an error message of <error-message>
    Examples:
    | extension-keyword                       | error-message                   |
    | is empty                                | primary contact phone country code is too short |
    | exceeds maximum allowed character limit | primary contact phone country code is too long  |
    | contains non-numeric characters         | primary contact phone country code is invalid   |

Scenario: POST to create the enterprise partner - Partner Site Name Validation
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    And a partner site name that exceeds maximum allowed character limit
    When a request is made to create a partner
    Then a response code of 400 should be returned
    And an error message of partner site name is too long

Scenario Outline: POST to create the enterprise partner - Partner Site Address (Line One) Validation
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    And a partner site address line one that <address-line-one-keyword>
    When a request is made to create a partner
    Then a response code of 400 should be returned
    And an error message of <error-message>
    Examples:
    | address-line-one-keyword                | error-message                      |
    | is empty                                | partner site address line one must not be empty |
    | is null                                 | partner site address line one must not be null  |
    | exceeds maximum allowed character limit | partner site address line one is too long       |

Scenario: POST to create the enterprise partner - Partner Site Address (Line Two) Validation
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    And a partner site address line two that exceeds maximum allowed character limit
    When a request is made to create a partner
    Then a response code of 400 should be returned
    And an error message of partner site address line two is too long

Scenario: POST to create the enterprise partner - Partner Site Address (Line Three) Validation
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    And a partner site address line three that  exceeds maximum allowed character limit
    When a request is made to create a partner
    Then a response code of 400 should be returned
    And an error message of partner site address line three is too long

#Scenario: POST to create an enterprise partner with duplicate site usage types
#    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
#    And an origin of jtv-partner-central
#    And a Keycloak user created
#    And an enterprise user already created that belongs to the current keycloak user
#    And the partner invite token is pending
#    And the request contains the same email address as the partner invite
#    And the request contains the same legal entity name as the partner invite
#    And the request also contains a BUSINESS site
#    And the request also contains a SHIPPING site
#    And the request also contains a RETURNS site
#    When a request is made to create a partner
#    Then a response code of 422 should be returned
#    And an error message of partner cannot have multiple business sites
