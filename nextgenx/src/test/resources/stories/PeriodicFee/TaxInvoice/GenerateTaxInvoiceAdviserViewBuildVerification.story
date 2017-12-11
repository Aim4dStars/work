Narrative:Smoke Test while deploying build  
In order to Ensure build is not causing issue on existing functionality
As an user
I want to do smoke testing


Scenario: Verify Tax Invoice Screen
Meta:
@categories build
Given I am on Login Page
When I navigate to Tax Invoice screen
Then I see necessary attributes displayed on the screen