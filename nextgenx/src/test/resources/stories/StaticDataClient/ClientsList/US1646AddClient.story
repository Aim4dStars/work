Narrative: US1646:Add Client
In order to manage a client that doesn’t appear in the list
As an adviser, adviser assistant, para planner, practice manager or dealer group manager who has the permission to add new account 
I want to add a new account to the list

Meta:
@userstory US1646AddClient

Scenario: Add Client Scenario-1– Not Developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
And I select the Add Client link
Then I see Avoka Onboarding page to complete new account application for a new client


Scenario: Add Client Scenario-2– Out Of Scope
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
And I select the Add Client link
Then I see Avoka Onboarding page for existing clients, the new account application is pre-populated with the client’s details

Scenario: Add Client Alternate Scenario– Permission- Not Developed
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Clients List screen
Then I see the Add Client link as hidden
