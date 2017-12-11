Narrative: US1978:Recent cash transaction-Verify Recent Transaction Section
In order to review my client’s account key investment information
As an adviser paraplanner or Adviser Assistant
I want to view Recent cash transaction

Meta:
@userstory US1978Recentcashtransactions

Scenario: Verify Cash Transaction section records
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see records of the 10 most recent cash transactions as:
|Date|Icon|Description|cash|
|19 Jan 2015||Direct Debit Deposit from Linked Account Name 01|$50,000,000.00|
|19 Jan 2015||Direct Debit Deposit from Linked Account Name 01|$50,000.00|
|19 Jan 2015||BPAY payment to TELSTRA MOBILE|-$1,500.00|
|19 Jan 2015||BPAY payment to ZAL LIFE INSURANCE|-$2,750.00|
|19 Jan 2015||Payment to|-$1,750.00|
|19 Jan 2015||Opened St.George Term Deposit|-$450,000.00|
|10 Sep 2014||Direct Credit|$2,345.87|
|31 Jul 2014||Income payment for BT Cash|$1,200.00|
|25 Jul 2014||Good Value Payment|$50.00|
|20 Jul 2014||Migration|$50,000,000.00|


Scenario: Verify Transaction history report screen
Meta:
@categories miniregression fullregression

Given I am on Login Page
When I navigate to Account overview screen
Then I see Recent cash transactions hyperlink
When I click on Recent cash transaction hyperlink
Then I see Transaction history report screen PENDING


Scenario:Verify the format of transaction value date
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see valid cash transaction effective date
|Date|
|02 Jul 2014|
|13 May 2014|
|13 May 2014|
|12 May 2014|
|12 May 2014|
|12 May 2014|
|11 May 2014|
|11 May 2014|
|11 May 2014|
|11 May 2014|
And I see a valid date format for cash transaction effective date


Scenario:
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see the definition of the date consistent with Transaction history report screen PENDING


Scenario:Verify the Text Displayed as Uncleared transactions
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see an icon for uncleared transaction
When I hover on the icon for the uncleared transaction
Then I see text displayed as Uncleared


Scenario:Verify the Cash Transaction type
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see cash transaction type same as on Transaction history report PENDING
And I see cash transaction type truncated in case it goes beyond 2nd line PENDING


Scenario:Verify the Cash Transaction description
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see cash transaction description same as on Transaction history report PENDING
And I see cash transaction description truncated in case it goes beyond 2nd line PENDING


Scenario: Verify Cash Transaction section with zero status
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Account overview screen
And I see no records for recent transactions PENDING
Then I see the error meassage as There are no transactions PENDING

