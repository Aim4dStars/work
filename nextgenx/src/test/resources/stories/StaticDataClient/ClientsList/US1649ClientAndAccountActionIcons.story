Narrative: US1649:Client and account action icons
In order to manage a client’s details and their account(s)
As an adviser, adviser assistant, para planner, practice manager or dealer group manager  
I want to select an action for a specific client

Meta:
@userstory US1649ClientAndAccountActionIcons

Scenario: - Client Action - Out of Scope
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
Then I see the client action icon on the client row - Out Of Scope
When I click on the client action icon - Out Of Scope
Then I see Avoka Onboarding page - Out Of Scope

Scenario: - Client Action Alternate Scenario - Out of Scope
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
Then I see the client action icon on the client row as hidden - Out Of Scope


Scenario: - Account Actions – all actions available
Meta:
@categories fullregression asdf
Given I am on Login Page
When I navigate to Clients List screen
When I click on the row expand toggle icon
Then I see dropdown containing list of available 4 necessary options
When I select dropdown as <Options>
Then I am on destination screen as <screenName>
Examples:
|Options|screenName|
|Place an order|Trading order|
|Transaction history|Transaction tracking|
|Account details|Account details and preferences|
|Charge one off fee|Charge one off fee|


Scenario: - Account Actions – all actions available-Permission -Not Developed
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Clients List screen
When I click on the row expand toggle icon
Then I see dropdown containing list of available necessary options - as per permission -Not developed