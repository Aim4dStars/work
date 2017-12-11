Narrative:Smoke Test while deploying build  
In order to Ensure build is not causing issue on existing functionality
As an user
I want to do smoke testing

Scenario: One Off Advise - Return to Account Overview
Meta:
@categories build shakedown
Given I am on Login Page
When I navigate to fees screen
When I enter fees amount $12.00
And I enter Description text as Test Description
And I click on Next button
Then I am on confirm page