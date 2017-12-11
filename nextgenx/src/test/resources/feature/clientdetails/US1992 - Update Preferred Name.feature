 Feature: US1992:Update preferred name- Verify 'Client Preferred Name' 
	In order to maintain the client’s correct preferred name in the system
	As an adviser or adviser assistant
	I want to update my client’s preferred name

  @Functional @clientdetails
  Scenario: Valid Name
    Given I am on Client details and preferences
    When I click on edit icon for preferred name
    Then I am on Edit preferred name screen	
	And I see previously recorded name
	When I enter valid name "TestWa5'- .&,/WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWTTTTTTTTT"
	Then I see only first 50 characters

  @Functional @clientdetails
  Scenario: Approval Checkbox
    Given I am on Client details and preferences
    When I click on edit icon for preferred name
    Then I see disabled updated button
    And I check the approval checkbox
    Then I see enabled updated button
    
  @Functional @clientdetails
  Scenario: Update Preferred Name
    Given I am on Client details and preferences
    When I click on edit icon for preferred name
    And  I enter valid name "Test First Name"
    And I check the approval checkbox    
    And I click on Update button
    Then I see updated name as "Test First Name"
    
   @Functional @clientdetails
  Scenario: Update Preferred Name with blank value
    Given I am on Client details and preferences
    When I click on edit icon for preferred name
    And  I enter valid name ""
    And I check the approval checkbox    
    And I click on Update button
    Then I see updated name as ""
     
    @Functional @clientdetails
  Scenario: Cancel Button
    Given I am on Client details and preferences
    When I click on edit icon for preferred name    	
	And  I enter valid name "Test First Name"
	And I click on cancel button
	Then I see no changes in preferred name
	
	@Functional @clientdetails
  Scenario: Cancel Button
    Given I am on Client details and preferences
	When I click on edit icon for preferred name    	
	And  I enter valid name "Test First Name"
	And I click on close button
	Then I see no changes in preferred name