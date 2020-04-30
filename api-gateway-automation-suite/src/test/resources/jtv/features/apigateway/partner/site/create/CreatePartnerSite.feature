@partner-central @partner-site @create-partner-site @api-gateway @smoke @wip
Feature: POST request to create a partner site
###############################################################
#    POST /partner/{partner-id}/site
###############################################################

# TODO: Need to validate all of these response code.  Currently these are just place holders until I actually writing the step definitions for these tests.
#@common-path
#Scenario: POST request to create a partner site - Common Path
#    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
#    And an origin of jtv-partner-central
#    And a Keycloak user created
#    And an enterprise user already created that belongs to the current keycloak user
#    And a partner already created that has completed registration
#    And has a partner site with usage type business
#    And the current user is the primary account manager for the partner
#    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
#    When a request is made to create a new site with usage BILLING for the partner
#    Then a response code of 200 should be returned
#    And the database should reflect the data from the create partner site request
#
#Scenario Outline: POST request to create a partner site - System Permissions Validation
#    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
#    And an origin of jtv-partner-central
#    And a Keycloak user created
#    And an enterprise user already created that belongs to the current keycloak user
#    And a partner already created that has completed registration
#    And has a partner site with usage type business
#    And the current user is an associate for the partner
#    And the user is granted <access-level> access to <access-type> in core
#    When a request is made to create a new site with usage BILLING for the partner
#    Then a response code of <response-code> should be returned
#
#    #TODO: Need to validate the access level for each of these system permissions.  I don't think they all have the VIEW_EDIT_DELETE level.
#    # For now, just leave them as placeholders
#    Examples: PC_BUSINESS_PROFILE system permission validation
#    | access-level     | access-type         | response-code |
#    | VIEW             | PC_BUSINESS_PROFILE | 422           |
#    | VIEW_EDIT        | PC_BUSINESS_PROFILE | 200           |
#    | VIEW_EDIT_DELETE | PC_BUSINESS_PROFILE | 200           |
#
#    Examples: PC_PAYMENT_METHODS system permission validation
#    | access-level     | access-type        | response-code |
#    | VIEW             | PC_PAYMENT_METHODS | 422           |
#    | VIEW_EDIT        | PC_PAYMENT_METHODS | 200           |
#    | VIEW_EDIT_DELETE | PC_PAYMENT_METHODS | 200           |
#
#    Examples: PC_SERVICES_ENROLLMENT system permission validation
#    | access-level     | access-type            | response-code |
#    | VIEW             | PC_SERVICES_ENROLLMENT | 422           |
#    | VIEW_EDIT        | PC_SERVICES_ENROLLMENT | 200           |
#    | VIEW_EDIT_DELETE | PC_SERVICES_ENROLLMENT | 200           |
#
#    Examples: PC_TAXPAYER_INFORMATION system permission validation
#    | access-level     | access-type             | response-code |
#    | VIEW             | PC_TAXPAYER_INFORMATION | 422           |
#    | VIEW_EDIT        | PC_TAXPAYER_INFORMATION | 200           |
#    | VIEW_EDIT_DELETE | PC_TAXPAYER_INFORMATION | 200           |
#
#    Examples: PC_ASSOCIATES system permission validation
#    | access-level     | access-type   | response-code |
#    | VIEW             | PC_ASSOCIATES | 422           |
#    | VIEW_EDIT        | PC_ASSOCIATES | 200           |
#    | VIEW_EDIT_DELETE | PC_ASSOCIATES | 200           |
#
#    Examples: PC_USER_ACCOUNTS system permission validation
#    | access-level     | access-type      | response-code |
#    | VIEW             | PC_USER_ACCOUNTS | 422           |
#    | VIEW_EDIT        | PC_USER_ACCOUNTS | 200           |
#    | VIEW_EDIT_DELETE | PC_USER_ACCOUNTS | 200           |
#
#    Examples: PC_EMPLOYEES system permission validation
#    | access-level     | access-type  | response-code |
#    | VIEW             | PC_EMPLOYEES | 422           |
#    | VIEW_EDIT        | PC_EMPLOYEES | 200           |
#    | VIEW_EDIT_DELETE | PC_EMPLOYEES | 200           |
#
#    Examples: PC_PARTNER_INVITATIONS system permission validation
#    | access-level     | access-type            | response-code |
#    | VIEW             | PC_PARTNER_INVITATIONS | 422           |
#    | VIEW_EDIT        | PC_PARTNER_INVITATIONS | 200           |
#    | VIEW_EDIT_DELETE | PC_PARTNER_INVITATIONS | 200           |
#
#    Examples: PC_PRODUCT_LISTING_PRICING system permission validation
#    | access-level     | access-type                | response-code |
#    | VIEW             | PC_PRODUCT_LISTING_PRICING | 422           |
#    | VIEW_EDIT        | PC_PRODUCT_LISTING_PRICING | 200           |
#    | VIEW_EDIT_DELETE | PC_PRODUCT_LISTING_PRICING | 200           |
#
#Scenario: POST request to create a partner site - No Permissions
#    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
#    And an origin of jtv-partner-central
#    And a Keycloak user created
#    And an enterprise user already created that belongs to the current keycloak user
#    And a partner already created that has completed registration
#    And has a partner site with usage type business
#    And the current user is an associate for the partner
#    When a request is made to create a new site with usage BILLING for the partner
#    Then a response code of 404 should be returned
#
#Scenario: POST request to create a BUSINESS usage partner site - partner already has a site with usage BUSINESS
#    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
#    And an origin of jtv-partner-central
#    And a Keycloak user created
#    And an enterprise user already created that belongs to the current keycloak user
#    And a partner already created that has completed registration
#    And has a partner site with usage type business
#    And the current user is the primary account manager for the partner
#    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
#    When a request is made to create a new site with usage BUSINESS for the partner
#    Then a response code of 201 should be returned
#    And the database should reflect the data from the create partner site request
#
#    # NOTE: The below step will be used for the scenarios where we are attempting to create a site with duplicate site names (null, empty or some valid string)
#    # NOTE: null in the scenario step below is going to be a keyword
##    And the partner site has a site name of null
#
#Scenario Outline: POST request to create a partner site - Site Name Validation
#    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
#    And an origin of jtv-partner-central
#    And a Keycloak user created
#    And an enterprise user already created that belongs to the current keycloak user
#    And a partner already created that has completed registration
#    And has a partner site with usage type business
#    And the current user is the primary account manager for the partner
#    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
#    When a request is made to create a new site with site name that <site-name-keyword>
#    Then a response code of <code> should be returned
#    Examples:
#    | site-name-keyword               | code |
#    | is null                            | 201  |
#    | is empty                           | 201  |
#    | is at maximum string length        | 201  |
#    | is exceeding maximum string length | 409  |
#    | is not unique                      | TBD  |
#    | contains special characters        | TBD  |
#
#Scenario: POST request to create a partner site - same address
#    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
#    And an origin of jtv-partner-central
#    And a Keycloak user created
#    And an enterprise user already created that belongs to the current keycloak user
#    And a partner already created that has completed registration
#    And has a partner site with usage type business
#    And the current user is the primary account manager for the partner
#    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
#    When a request is made to create a new site with the same address
#    Then a response code of 409 should be returned
#
#Scenario: POST request to create a partner site - reactivating a deleted site
#    Given a Keycloak user role of ROLE_ManageMyPartnerAssociate
#    And an origin of jtv-partner-central
#    And a Keycloak user created
#    And an enterprise user already created that belongs to the current keycloak user
#    And a partner already created that has completed registration
#    And has a partner site with usage type business
#    # NOTE: the "that has been deleted" portion is not part of the keyword, this will require a new step definition
#    And has a partner site with usage type billing
#    And the partner site has been inactivated
#    And the current user is the primary account manager for the partner
#    And the user is granted VIEW_EDIT access to PC_BUSINESS_PROFILE in core
#    When a request is made to reactive the deleted site
#    Then a response code of 201 should be returned
#    And the deleted site is reactivated
