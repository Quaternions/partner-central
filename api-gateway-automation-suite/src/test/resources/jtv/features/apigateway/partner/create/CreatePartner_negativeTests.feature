@partner-central @create-partner-profile @partner-profile @api-gateway @smoke @wip
Feature: Create partner API Tests
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
    And an error message of Data exception - resource requested could not be found
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
    Examples:
    | role-keyword                 |
    | ROLE_CreateAnyPartnerProfile |
    | ROLE_ManageAnyPartnerProfile |
    | ROLE_ViewMyPartnerProfile    |

#Scenario Outline: POST to create the enterprise partner - User does have the appropriate role but does NOT have the appropriate permission
#        Given a Keycloak user role of ROLE_CreateMyPartnerProfile
#        And an origin of jtv-partner-central
#        And a Keycloak user created
#        And an enterprise user already created that belongs to the current keycloak user
#        And the partner invite token is pending
#        And the request contains the same email address as the partner invite
#        And the request contains the same legal entity name as the partner invite
#        And the request contains <permissions_keyword> user NOT have the appropriate permission
#        When a request is made to create a partner
#        Then a response code of 404 should be returned

#        Examples:
#        | permissions_keyword              |
#        | PC_BUSINESS_PROFILE - VIEW_EDIT  |
#        | PC_PAYMENT_BILLING_METHODS - VIEW|


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
    Examples:
    | legal-name-keyword                      |
    | is empty                                |
    | is null                                 |
    | exceeds maximum allowed character limit |

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
    Examples:
    | firstname-keyword                       |
    | is empty                                |
    | is null                                 |
    | exceeds maximum allowed character limit |

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
    Examples:
    | lastname-keyword                        |
    | is empty                                |
    | is null                                 |
    | exceeds maximum allowed character limit |

Scenario: POST to create the enterprise partner - Primary Contact Title Validation - exceeds maximum allowed character limit
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
    Examples:
    | phone-keyword                           |
    | exceeds maximum allowed character limit |
    | contains non-numeric characters         |

Scenario Outline: POST to create the enterprise partner - Primary Contact Phone Extension Validation
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
    | extension-keyword                       | error-message                |
    | is empty                                | phone extension is too short |
    | exceeds maximum allowed character limit | phone extension is too long  |
    | contains non-numeric characters         | phone extension is invalid   |

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
    And an error message of <error>
    Examples:
    | extension-keyword                       | error                           |
    | is empty                                | phone country code is too short |
    | exceeds maximum allowed character limit | phone country code is too long  |
    | contains non-numeric characters         | phone country code is invalid   |

Scenario: POST to create the enterprise partner - Partner Site Name Validation - Exceeds allowed character limit (51)
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

Scenario Outline: POST to create the enterprise partner - Partner Site Address (Line One) Validation
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    And a partner address line one that <address-line-one-keyword>
    When a request is made to create a partner
    Then a response code of 400 should be returned
    And an error message of <error>
    Examples:
    | address-line-one-keyword                | error     |
    | is empty                                | something |
    | is null                                 | something |
    | exceeds maximum allowed character limit | something |

Scenario: POST to create the enterprise partner - Partner Site Address (Line Two) Validation
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    And a partner address line two that exceeds maximum allowed character limit
    When a request is made to create a partner
    Then a response code of 400 should be returned
