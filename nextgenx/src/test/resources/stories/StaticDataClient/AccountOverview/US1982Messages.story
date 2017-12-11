Narrative: US1982:Messages Section
In order to review my client’s account key investment information
As an adviser paraplanner or Adviser Assistant
I want to view Orders in Messages Section

Meta:
@userstory US1982Messages

Scenario:Verify the Message Section
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see a Message Section

Scenario:Verify the historical messages on message centre
Meta:
@categories miniregresion fullregression
Given I am on Login Page
When I navigate to Account overview screen
When I click on Message section link PENDING
Then I see all historical messages on message centre PENDING


Scenario:Verify the Message Section Mouse Hovering Message
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see CMS message Help-IP-0063 PENDING