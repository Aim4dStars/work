 Feature: US1993:Update Company name- Verify 'Company Name'  
	In order to maintain the company details
	As an adviser or adviser assistant
	I want to update the company name for my client


  @Functional @clientdetails
  Scenario: Valid Company Name
    Given I am on Company details page
    When I click on edit icon for company name
    Then I am on Edit Company name screen	
	And I see previously recorded Company name
	When I enter valid Company name "TestWa5'- .&,/WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWTTTTTTTTT"
	Then I see only first 50 characters for Company

  @Functional @clientdetails
  Scenario: Approval Checkbox
    Given I am on Company details page
    When I click on edit icon for company name
    Then I see disabled updated button for Company
    And I check the approval checkbox for Company
    Then I see enabled updated button for Company
    
  @Functional @clientdetails
  Scenario: Update Company Preferred Name
    Given I am on Company details page
    When I click on edit icon for company name
    And  I enter valid Company name "Test Company Name"
    And I check the approval checkbox for Company    
    And I click on Update button for Company
    Then I see updated name for Company as "Test Company Name"
    
   @Functional @clientdetails
  Scenario: Error for Company Preferred Name when provided blank value
    Given I am on Company details page
    When I click on edit icon for company name
    And  I enter valid Company name ""
    And I check the approval checkbox for Company   
    And I click on Update button for Company
    Then I see Error Message
        
   @Functional @clientdetails
  Scenario: Cancel Button
    Given I am on Company details page
    When I click on edit icon for company name    	
	And  I enter valid Company name "Test First Name"
	And I click on cancel button for Company
	Then I see no changes in preferred name for Company
	
	@Functional @clientdetails
  Scenario: Cancel Button
    Given I am on Client details and preferences
	When I click on edit icon for preferred name    	
	And  I enter valid name "Test First Name"
	And I click on close button
	Then I see no changes in preferred name