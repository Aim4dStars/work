Narrative: US1998:Update Registration state- Verify 'Registration State'
In order to maintain a client’s registration details
As an adviser or adviser assistant
I want to update my the registration state for Trusts and SMSF

Meta:
@userstory US1998UpdateRegistrationstate


Scenario: Default Value Registration State
Meta:
@categories miniregression fullregression
Given I am on Trust detail Individual page
When I click on edit icon for Registration State
Then I am on Edit Registration State screen
And I see default selected state during onboarding
When I click on the dropdown icon
Then I see complete list of eight austalian states


Scenario: Approval Checkbox Trust Ind Registration State
Meta:
@categories fullregression
Given I am on Trust detail Individual page
When I click on edit icon for Registration State
Then I see disabled updated button
When I check the approval checkbox
Then I see enabled updated button


Scenario: Update Trust Ind Registration State
Meta:
@categories miniregression fullregression
Given I am on Trust detail Individual page
When I click on edit icon for Registration State
And I update Registration State as AST
Then I see disabled updated button
When I check the approval checkbox
And I click on Update button
Then I see updated value for Registration State as AST


Scenario: Cancel Button for Trust Ind Registration State
Meta:
@categories miniregression fullregression
Given I am on Trust detail Individual page
When I click on edit icon for Registration State
And I update Registration State as AST
And I click on registraion state cancel button
Then I see no changes in value for Registration State



Scenario: Close Button for Trust Ind Registration State
Meta:
@categories miniregression fullregression
Given I am on Trust detail Individual page
When I click on edit icon for Registration State
And I update Registration State as AST
And I click on registraion state close button
Then I see no changes in value for Registration State

Scenario: Avaloq Integration Trust
Meta:
@categories fullregression
Given I am on Trust detail Individual page Avaloq
When I click on edit icon for Registration State Avaloq
Then I am on Edit Registration State screen Avaloq