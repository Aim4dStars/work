Narrative: US1994:Update tax options- Verify 'tax options'
In order to capture the client’s tax option to best reflect their situation 
As an adviser or adviser assistant
I want to update my client’s tax option

Meta:
@userstory US1994UpdateTaxOptions

Scenario: Default Value tax options
Meta:
@categories miniregression fullregression
Given I am on Trust detail Individual page
When I click on edit icon for tax options
Then I am on Edit tax options screen
And I see default client's current tax option and default input field values
When I click on the dropdown icon
Then I see complete list of three options which I can select


Scenario: Approval Checkbox Trust Ind tax options
Meta:
@categories fullregression
Given I am on Trust detail Individual page
When I click on edit icon for tax options
Then I see disabled updated button
When I check the approval checkbox
Then I see enabled updated button


Scenario: Update Trust Ind tax options to Enter TFN Number
Meta:
@categories fullregression
Given I am on Trust detail Individual page
When I click on edit icon for tax options
And I update tax options to first option as Enter Tax File Number (TFN)
Then I see the input field for TFN Number
When I enter valid 8 or 9 digit TFN number in the input field 123456782
And I check the approval checkbox
And I click on Update button
Then I see Tax File Number provided for tax options


Scenario: Update Trust Ind tax options to Enter TFN Number Alternate Scenario
Meta:
@categories miniregression fullregression
Given I am on Trust detail Individual page
When I click on edit icon for tax options
And I update tax options to first option as Enter Tax File Number (TFN)
Then I see the input field for TFN Number
When I enter invalid TFN number in the input field 12$%^&*
And I check the approval checkbox
And I click on Update button
Then I see error message ERR.0093 Please enter a valid 8 or 9-digit tax file number


Scenario: Update Trust Ind tax options to Provide Exemption Reason
Meta:
@categories miniregression fullregression qwqwr
Given I am on Trust detail Individual page
When I click on edit icon for tax options
And I update tax options to second option as Provide Exemption Reason
And I click on the second dropdown icon
Then I see the input field dropdown having complete list of eight exemption reasons
When I update the Exemption Reason from the second dropdown as Investor under sixteen
And I check the approval checkbox
And I click on Update button
Then I see Exemption reason provided for tax options


Scenario: Update Trust Ind tax options to Do not quote TFN or exemption
Meta:
@categories miniregression fullregression 
Given I am on Trust detail Individual page
When I click on edit icon for tax options
And I update tax options to third option as Do not quote TFN or exemption
Then I see the necessary display message on the screen
When I check the approval checkbox
And I click on Update button
Then I see Tax File Number or exemption not provided for tax options


Scenario: Cancel Button for Trust Ind tax options
Meta:
@categories miniregression fullregression
Given I am on Trust detail Individual page
When I click on edit icon for tax options
And I update tax options to third option as Do not quote TFN or exemption
And I click on Tax Options cancel button
Then I see no changes in value for tax options


Scenario: Close Button for Trust Ind tax options
Meta:
@categories miniregression fullregression
Given I am on Trust detail Individual page
When I click on edit icon for tax options
And I update tax options to third option as Do not quote TFN or exemption
And I click on Tax Options close button
Then I see no changes in value for tax options


Scenario: Avaloq Integration Trust
Meta:
@categories fullregression
Given I am on Trust detail Individual page Avaloq
When I click on edit icon for tax options Avaloq
Then I am on Edit tax options screen Avaloq