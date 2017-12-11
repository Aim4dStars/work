Narrative:Smoke Test while deploying build  
In order to Ensure build is not causing issue on existing functionality
As an user
I want to do smoke testing


Scenario: Adviser Dashboard - Verify data for Key activity section
Meta:
@categories build shakedown
Given I am on Login Page
When I navigate to Adviser Dashboard
Then I see activity type and client account name as hyperlink
And I see description as text
And I see priority indicator
And I see activity date and activity time stamp
And I see On-boarding tracking and Order status as action icon