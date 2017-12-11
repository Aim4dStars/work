Narrative: US1985:Scheduled transactions Verify - Scheduled transactions Section
In order to review my client’s account key investment information
As an adviser paraplanner or Adviser Assistant
I want to view Scheduled transactions

Meta:
@userstory US1985Scheduledtransactions

Scenario:Verify the scheduled transactions screen for Fee Schedule
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see Cash Deposit header under Schedule transactions as CASH DEPOSITS
Then I see cash deposit transactions in ascending order as
|Date|Description|cash|
|22 Dec 2014|Monthly New Standing Order (Pay Anyone) from 51725|$7,856.36|



Scenario:Verify the scheduled transactions screen on clicking Scheduled transactions link
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see Scheduled transactions header as hyperlink PENDING
When I click on Scheduled transactions link PENDING
Then I see Scheduled transactions screen PENDING


Scenario: Verify Cash Deposit header
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see Cash Deposit header under Schedule transactions


Scenario: Verify format for scheduled deposit transaction date
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see scheduled deposit transaction date in valid format


Scenario: Verify Scheduled transactions screen Next due date 
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see scheduled deposit transaction date same as Scheduled transactions screen Next due date PENDING


Scenario: Verify frequency, transaction type and from/to Account name text for scheduled deposit transaction
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see frequency transaction type and account name under Schedule transaction as
|Description|
|Monthly New Standing Order (Pay Anyone) from 51725|




Scenario: Verify frequency for scheduled deposit transaction
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see frequency of a scheduled deposit transaction same as Scheduled transactions screen Repeats PENDING



Scenario:Verify the positive value of Dollar amount for scheduled transaction
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see the positive value of Dollar amount for Cash Deposits Schedule transactions


Scenario:Verify the Dollar amount for scheduled transaction same as on Scheduled transactions screen
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see the Dollar amount for scheduled transaction same as Credit amount on Scheduled transactions screen PENDING


Scenario:Add a deposit button for cash deposits
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Account overview screen
And I have permissions to Make a payment PENDING
Then I see Add a deposit button PENDING
When I click on Add a deposit button for cash deposits PENDING
Then I see Deposits screen PENDING


Scenario:Verify the Upcoming cash payments
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see date of a scheduled payment transaction same as Upcoming cash deposits PENDING
And I see Frequency of a scheduled payment transaction same as Upcoming cash deposits PENDING
And I see Transaction type text next to Frequency PENDING


Scenario:Verify the negative value of Dollar amount for scheduled payment transaction
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see the negative value of Dollar amount for scheduled Payment transaction



Scenario:Verify the Dollar amount for scheduled payment transaction same as Debit amount on Scheduled transactions screen
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see the Dollar amount for scheduled transaction same as Debit amount on Scheduled transactions screen PENDING

Scenario:Add a deposit button for cash payments
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Account overview screen
And I have permissions to Make a payment PENDING
Then I see Add a deposit button PENDING
When I click on Add a deposit button for cash payments PENDING
Then I see Payments screen PENDING


Scenario: No scheduled cash deposit transactions
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see error message as There are no upcoming cash deposits



Scenario: No scheduled cash payment transactions
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see error message as There are no upcoming cash payments


