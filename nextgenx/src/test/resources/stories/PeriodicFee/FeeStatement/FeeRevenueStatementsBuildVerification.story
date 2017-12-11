Narrative:Smoke Test while deploying build  
In order to Ensure build is not causing issue on existing functionality
As an user
I want to do smoke testing

Scenario: Fee statement screen calendar 7 days
Meta:
@categories build
Given I am on Login Page
When I navigate to FeeRevenue Statements screen
And I click on calendar
And I select the last 7 days
Then I see revenue statements for last 7 days


Scenario: Fee statement screen menu display
Meta:
@categories build
Given I am on Login Page
When I navigate to FeeRevenue Statements screen
And I give start date 01 Jan 2014
And I give end date 01 Jun 2014
And I click on search button
Then I see 50 fee revenue statements
And I see progressive button at the bottom
When I click on the progressive button
Then I see more records