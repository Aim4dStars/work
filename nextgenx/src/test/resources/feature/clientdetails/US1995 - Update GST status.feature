 Feature: US1995:Update GST status- Verify 'Registered for GST'
	In order to help the client entity with their tax needs
	As an adviser or adviser assistant
	I want to update the GST registration status for my client (not for an Individual or Joint)

  @Functional @clientdetails
  Scenario: Default Value
    Given I am on Trust detail Individual page
    When I click on edit icon for Registered for GST
    Then I am on Edit Register for GST screen	

  @Functional @clientdetails
  Scenario: Approval Checkbox
    Given I am on Trust detail Individual page
    When I click on edit icon for Registered for GST
    Then I see disabled updated button for Registered for GST
    And I check the approval checkbox for Registered for GST
    Then I see enabled updated button for Registered for GST
    
  @Functional @clientdetails
  Scenario: Update Registration for GST
    Given I am on Trust detail Individual page
    When I click on edit icon for Registered for GST
    And  I update Registration for GST
    And I check the approval checkbox for Registered for GST  
    And I click on Update button for Registered for GST
	Then I see updated value for Registration for GST          
    
  @Functional @clientdetails
  Scenario: Cancel Button
    Given I am on Trust detail Individual page
    When I click on edit icon for Registered for GST    	
	And  I update Registration for GST
	And I click on cancel button for Registered for GST
	Then I see no changes in value for Registered for GST
	
	
  @Functional @clientdetails
  Scenario: Close Button
    Given I am on Trust detail Individual page
    When I click on edit icon for Registered for GST    	
	And  I update Registration for GST
	And I click on close button for Registered for GST
	Then I see no changes in value for Registered for GST