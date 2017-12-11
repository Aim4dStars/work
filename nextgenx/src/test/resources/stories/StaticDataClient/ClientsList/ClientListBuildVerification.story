Narrative:Smoke Test while deploying build  
In order to Ensure build is not causing issue on existing functionality
As an user
I want to do smoke testing


Scenario: Client list filter – Update Button
Meta:
@categories build shakedown
Given I am on Login Page
When I navigate to Clients List screen
And I click on collapsed state filter button
And I select State value as All
And I select Portfolio Valuation value as More than $2M
And I select Available cash value as All Ranges
And I click on Update Button for Client List
Then I get client records is:
|name|adviser|cash|portfolioval|
|Smith, Adrian||50,023,093.72|100,530,910.44|