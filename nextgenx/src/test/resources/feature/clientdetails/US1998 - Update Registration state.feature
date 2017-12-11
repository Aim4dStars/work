 Feature: US1998:Update Registration state- Verify 'Registration State'  
	In order to maintain a client’s registration details
	As an adviser or adviser assistant
	I want to update my the registration state for Trusts and SMSF

  @Functional @clientdetails
  Scenario: Default Value
    Given I am on Trust detail Individual page
    When I click on edit icon for Registration State
    Then I am on Edit Registration State screen