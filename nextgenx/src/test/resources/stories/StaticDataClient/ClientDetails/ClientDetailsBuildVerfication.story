Narrative:Smoke Test while deploying build  
In order to Ensure build is not causing issue on existing functionality
As an user
I want to do smoke testing

Scenario: Client Details - Update Preferred Name
Meta:
@categories build
Given I am on Client details and preferences
When I click on edit icon for preferred name
And I enter valid name Test First Name
And I check the approval checkbox
And I click on Update button
Then I see updated name as Test First Name

Scenario: Client Details - Update Trust Ind tax options to Enter TFN Number
Meta:
@categories build
Given I am on Trust detail Individual page
When I click on edit icon for tax options
And I update tax options to first option as Enter Tax File Number (TFN)
Then I see the input field for TFN Number
When I enter valid 8 or 9 digit TFN number in the input field 123456782
And I check the approval checkbox
And I click on Update button
Then I see Tax File Number provided for tax options