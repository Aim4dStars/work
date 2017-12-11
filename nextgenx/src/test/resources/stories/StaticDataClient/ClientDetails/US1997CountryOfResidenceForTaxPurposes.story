Narrative: US1997:Update Country of residence for tax purposes- Verify 'Country of Residence'
In order to identify a client’s country of residence for tax purposes
As an adviser or adviser assistant
I want to update my client’s country of residence

Meta:
@userstory US1997CountryOfResidenceForTaxPurposes

Scenario: Default Value Country of residence
Meta:
@categories miniregression fullregression
Given I am on Client details and preferences
When I click on edit icon on the Australian resident for tax purposes field
Then I am on Edit Country of residence screen
And I see default selected country during onboarding
When I click on the dropdown icon
Then I see complete list of countries which I can select


Scenario: Approval Checkbox Ind Country of residence
Meta:
@categories miniregression fullregression
Given I am on Client details and preferences
When I click on edit icon on the Australian resident for tax purposes field
Then I see disabled updated button
When I check the approval checkbox
Then I see enabled updated button


Scenario: Update Ind Country of residence
Meta:
@categories fullregression
Given I am on Client details and preferences
When I click on edit icon on the Australian resident for tax purposes field
And I update Country of residence as India
And I check the approval checkbox
And I click on Update button
Then I see updated value for Country of residence as India


Scenario: Cancel Button for Ind Country of residence
Meta:
@categories miniregression fullregression
Given I am on Client details and preferences
When I click on edit icon on the Australian resident for tax purposes field
And I update Country of residence as India
And I click on cancel button for country of residence
Then I see no changes in value for Country of residence



Scenario: Close Button for Ind Country of residence
Meta:
@categories miniregression fullregression
Given I am on Client details and preferences
When I click on edit icon on the Australian resident for tax purposes field
And I update Country of residence as India
And I click on close button for country of residence
Then I see no changes in value for Country of residence

Scenario: Avaloq Integration Trust
Meta:
@categories fullregression
Given I am on Client details and preferences
When I click on edit icon for Country of residence Avaloq
Then I am on Edit Country of residence screen Avaloq