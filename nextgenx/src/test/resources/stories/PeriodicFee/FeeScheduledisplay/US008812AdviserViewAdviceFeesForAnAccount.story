Narrative: US1166:Adviser View - Advice Fees for an account- Verify 'Fee Schedule' for advisor
As a Adviser
I want to view my client's account current fee arrangements

Meta:
@userstory US008812AdviserViewAdviceFeesForAnAccount

Scenario:Fee Schedule for ongoing fee
Meta:
@categories miniregression fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
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


Scenario:Fee Schedule for licensee fee
Meta:
@categories miniregression fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
Then I see header text for Licensee advice fee
And I see header text for Dollar fee component for Licensee fee
And I see current fee schedule for Licensee Dollar fee as <amount> <text>
And I see text for licensee Percentage fee component
And I see current fee schedule for licensee Percentage fee tiers as
|Type|Pa|
|Managed portfolio|2.00|
|Term deposit|3.00|
|Cash|0.00|

Examples:
|amount|text|
|$460.00 per annum|Indexed annually to CPI. Next indexation 01 Jan 2016|

Scenario:Fee Schedule Edit Screen for adviser
Meta:
@categories miniregression fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
Then I see fees Schedule edit screen


Scenario: Verify Error Message for adviser
Meta:
@categories miniregression fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
Then Verify no fee error No fee applied


Scenario:Fee Schedule Screen for adviser
Meta:
@categories miniregression fullregression
Given I am on logon page screen PENDING
When I navigate to Fee Schedule Screen with no fee PENDING
And I click on Fee Schedule link PENDING
Then I am on Fee Schedule screen page PENDING


Scenario:Fee Schedule - One-off advice fee page for adviser
Meta:
@categories miniregression fullregression
Given I am on logon page screen
When I navigate to Fee Schedule Screen with no fee
And I click on Charge one-off advice link
Then I am on Charge One off Advice Fee screen with header name as Charge one-off advice fee


Scenario:Verify the fee Schedule for amount $0.00
Meta:
@categories miniregression fullregression
Given I am on logon page screen avaloq
When I navigate to the fee schedule screen with fee avaloq
And I see the account with $0.00 flat dollar advice fee avaloq
Then I see the fee schedule for the advice fee the $0.00 amount avaloq