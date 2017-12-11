Narrative:Smoke Test while deploying build  
In order to Ensure build is not causing issue on existing functionality
As an user
I want to do smoke testing

Scenario:Verify the scheduled transactions Cash Deposits
Meta:
@categories build shakedown
Given I am on Login Page
When I navigate to Account overview screen
Then I see Cash Deposit header under Schedule transactions as CASH DEPOSITS
Then I see cash deposit transactions in ascending order as
|Date|Description|cash|
|22 Dec 2014|Monthly New Standing Order (Pay Anyone) from 51725|$7,856.36|