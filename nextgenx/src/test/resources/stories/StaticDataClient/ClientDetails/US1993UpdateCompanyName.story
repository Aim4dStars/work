Narrative: US1993:Update Company name- Verify 'Company Name'
In order to maintain the company details
As an adviser or adviser assistant
I want to update the company name for my client

Meta:
@userstory US1993UpdateCompanyName

Scenario: Valid Company Name
Meta:
@categories miniregression fullregression
Given I am on Company details page
When I click on edit icon for company name
Then I am on Edit Company name screen
And I see previously recorded Company name
When I enter valid Company name TestWa5'- .&,/WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWTTTTTTTTT
Then I see only first 50 characters for Company


Scenario: Approval Checkbox Company
Meta:
@categories miniregression fullregression
Given I am on Company details page
When I click on edit icon for company name
Then I see disabled updated button for Company
When I check the approval checkbox for Company
Then I see enabled updated button for Company


Scenario: Update Company Preferred Name
Meta:
@categories miniregression fullregression
Given I am on Company details page
When I click on edit icon for company name
And I enter valid Company name Test Company Name
And I check the approval checkbox for Company
And I click on Update button for Company
Then I see updated name for Company as Test Company Name


Scenario: Error for Company Preferred Name when provided blank value
Meta:
@categories miniregression fullregression
Given I am on Company details page
When I click on edit icon for company name
And I enter valid Company name Test Company Name
And I clear the name
And I check the approval checkbox for Company
And I click on Update button for Company
Then I see Error Message


Scenario: Cancel Button Company
Meta:
@categories miniregression fullregression
Given I am on Company details page
When I click on edit icon for company name
And I enter valid Company name Test First Name
And I click on cancel button for Company
Then I see no changes in preferred name for Company


Scenario: Close Button Company
Meta:
@categories miniregression fullregression
Given I am on Company details page
When I click on edit icon for company name
And I enter valid Company name Test First Name
And I click on close button for Company
Then I see no changes in preferred name

Scenario: Avaloq Integration Company
Meta:
@categories fullregression
Given I am on Company details page Avaloq
When I click on edit icon for company name Avaloq
Then I am on Edit Company name screen Avaloq
And I see previously recorded Company name Avaloq
When I enter valid Company name TestWa5'- .&,/WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWTTTTTTTTT Avaloq
Then I see only first 50 characters for Company Avaloq