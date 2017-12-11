Narrative:Story from Antony Quade   
In order to BT Panorama performance
As an user, 
I want to load the screens in 5 seconds

Meta:
@userstory US1Performancelogin

Scenario: Verify user login time
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Clients List screen
Then I should load the screen within 5 seconds 

