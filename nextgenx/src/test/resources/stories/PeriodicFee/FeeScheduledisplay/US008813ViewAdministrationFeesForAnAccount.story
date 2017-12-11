Narrative: US008813:Adviser View-Administration Fees for an account- Verify 'Admin Fees'
As a Adviser
I want to view my client's administration current fee arrangements

Meta:
@userstory US008813ViewAdministrationFeesForAnAccount

Scenario:Able to see Administration fee
Meta:
@categories fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
Then I see header text for Administration fees
And I see header text for Dollar fee component
And I see current fee schedule for Dollar fee as <amount>
And I see text for Sliding scale fee component
And I see current fee schedule for minimum fee as <Minfee>
And I see current fee schedule for maximum fee as <Maxfee>
And I see current fee schedule for Sliding Scale tiers as
|Tiers|Pa|
|$0 - $10000|7.50|
|$10000 - $50000|6.00|
|$50000 - $100000|5.00|
|$100000 and above|4.00|

Examples:
|amount|Minfee|Maxfee|
|$850.00 per annum|Minimum fee $500 pa|Maximum fee $99999 pa|


Scenario:Display special discount percentage
Meta:
@categories miniregression fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
Then I see a special discount on screen PENDING

Scenario:Display Relationship Pricing for an account
Meta:
@categories miniregression fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
Then I see line item Relationship pricing PENDING
And I see Relationship Household Name PENDING
And I see Relationship Pricing Method below the household name PENDING
And I see primary contact name PENDING

Scenario:Verify Error Message for Administration fee
Meta:
@categories miniregression fullregression
Given I am on logon page screen PENDING
When I navigate to Fee Schedule Screen with no fee PENDING
Then For administration fee Verify errorPENDING No fee applied PENDING


Scenario:Verify the printer functionality
Meta:
@categories miniregression fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on the printer icon Manual
Then I see file getting printed Manual

|
Scenario:Verify the download functionality
Meta:
@categories miniregression fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on the download icon Manual
Then I see file getting download Manual
