@partner-central @create-partner-profile @partner-profile @api-gateway @smoke @wip
Feature: Create partner API Tests
    Creating a partner in Partner Central
########################################
#    POST /partner
########################################

@common-path
Scenario: POST to create enterprise partner for the current keycloak user - common path
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    When a request is made to create a partner
    Then a response code of 201 should be returned
    And create partner request values should be persisted correctly in the database

Scenario Outline: POST to create enterprise partner based keycloak roles
    Given a Keycloak user role of <role-keyword>
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    When a request is made to create a partner
    Then a response code of 201 should be returned
    And create partner request values should be persisted correctly in the database
    Examples:
    | role-keyword                  |
    | ROLE_CreateMyPartnerProfile   |
    | ROLE_ManageMyPartnerProfile   |

Scenario Outline: POST to create enterprise partner - Operating Name Validation
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    And the operating name that <legal-name-keyword>
    When a request is made to create a partner
    Then a response code of 201 should be returned
    And create partner request values should be persisted correctly in the database
    Examples:
    | legal-name-keyword                      |
    | is valid                                |
    | is at maximum allowed character limit   |

Scenario Outline: POST to create enterprise partner - Primary Contact Firstname Validation
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    And a primary contact firstname that <firstname-keyword>
    When a request is made to create a partner
    Then a response code of 201 should be returned
    And create partner request values should be persisted correctly in the database
    Examples:
    | firstname-keyword                       |
    | is valid                                |
    | is at maximum allowed character limit   |

Scenario Outline: POST to create enterprise partner - Primary Contact Lastname Validation
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    And a primary contact lastname that <lastname-keyword>
    When a request is made to create a partner
    Then a response code of 201 should be returned
    And create partner request values should be persisted correctly in the database
    Examples:
    | lastname-keyword                        |
    | is valid                                |
    | is at maximum allowed character limit   |

Scenario Outline: POST to create enterprise partner - Primary Contact Title Validation
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    And a primary contact title that <title-keyword>
    When a request is made to create a partner
    Then a response code of 201 should be returned
    And create partner request values should be persisted correctly in the database
    Examples:
    | title-keyword                           |
    | is empty                                |
    | is null                                 |
    | is valid                                |
    | is at maximum allowed character limit   |

Scenario Outline: POST to create enterprise partner -  Primary Contact Phone Validation
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    And a primary contact subscriber number that <phone-keyword>
    When a request is made to create a partner
    Then a response code of 201 should be returned
    And create partner request values should be persisted correctly in the database
    Examples:
    | phone-keyword                           |
    | is empty                                |
    | is null                                 |
    | is valid                                |
    | is at maximum allowed character limit   |

Scenario Outline: POST to create enterprise partner - Primary Contact Phone Extension Validation
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    And a primary contact phone extension that <extension-keyword>
    When a request is made to create a partner
    Then a response code of 201 should be returned
    And create partner request values should be persisted correctly in the database
    Examples:
    | extension-keyword                       |
    | is null                                 |
    | is valid                                |
    | is at maximum allowed character limit   |

Scenario Outline: POST to create enterprise partner - Primary Contact Phone Country Code Validation
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    And a primary contact phone country code that <extension-keyword>
    When a request is made to create a partner
    Then a response code of 201 should be returned
    And create partner request values should be persisted correctly in the database
    Examples:
    | extension-keyword                       |
    | is null                                 |
    | is valid                                |
    | is at maximum allowed character limit   |

Scenario Outline: POST to create enterprise partner - Partner Site Name Validation
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    And a partner site name that <site-name-keyword>
    When a request is made to create a partner
    Then a response code of 201 should be returned
    And create partner request values should be persisted correctly in the database
    Examples:
    | site-name-keyword                        |
    | is empty                                 |
    | is null                                  |
    | is valid                                 |
    | is at maximum allowed character limit    |

Scenario Outline: POST to create enterprise partner - Partner Site Address (Line One) Validation
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    And a partner address line one that <address-line-one-keyword>
    When a request is made to create a partner
    Then a response code of 201 should be returned
    And create partner request values should be persisted correctly in the database
    Examples:
    | address-line-one-keyword                |
    | is valid                                |
    | is at maximum allowed character limit   |

Scenario Outline: POST to create enterprise partner - Partner Site Address (Line Two) Validation
    Given a Keycloak user role of ROLE_CreateMyPartnerProfile
    And an origin of jtv-partner-central
    And a Keycloak user created
    And an enterprise user already created that belongs to the current keycloak user
    And the partner invite token is pending
    And the request contains the same email address as the partner invite
    And the request contains the same legal entity name as the partner invite
    And a partner address line two that <address-line-one-keyword>
    When a request is made to create a partner
    Then a response code of 201 should be returned
    And create partner request values should be persisted correctly in the database
    Examples:
    | address-line-one-keyword                |
    | is empty                                |
    | is null                                 |
    | is valid                                |
    | is at maximum allowed character limit   |
