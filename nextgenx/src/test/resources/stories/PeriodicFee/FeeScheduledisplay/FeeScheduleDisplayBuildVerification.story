Narrative:Smoke Test while deploying build  
In order to Ensure build is not causing issue on existing functionality
As an user
I want to do smoke testing


Scenario:Fee Schedule for ongoing fee
Meta:
@categories build shakedown
Given I am on logon page screen
When I navigate to the fee schedule screen with fee from client list with adviser Coonan
Then I see header text for Advice fees and Ongoing advice fee
And I see header text for Dollar fee component for ongoing fee
And I see current fee schedule for ongoing Dollar fee as <amount>
And I see text for ongoing Sliding scale fee component
And I see current fee schedule for ongoing Sliding Scale tiers as
|Tiers|Pa|
|$0 - $100|4.00|
|$100 - $5000|3.00|
|$5000 and above|2.00|


Examples:
|amount|
|$380.00 per annum|