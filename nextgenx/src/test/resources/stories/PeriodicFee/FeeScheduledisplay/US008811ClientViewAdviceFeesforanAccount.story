Narrative: US1166:Client View-Advice fees for an account- Verify 'Fee Schedule' for client
As a Client
I want to view my accounts current fee arrangements

Meta:
@userstory US008811ClientViewAdviceFeesforanAccount

Scenario:Viewing the account’s current fee schedule
Meta:
@categories miniregression fullregression
Given I am on logon page screen MANUAL
When I navigate to the fee schedule screen with fee MANUAL
Then I see current fee schedule for the advice fees MANUAL
And I see current fee schedule for the Licensee advice fees MANUAL
And I see current fee schedule for the Administration fees MANUAL
And I see current fee schedule for the Investment Management fees MANUAL


Scenario:Viewing the Advice fees
Meta:
@categories miniregression fullregression
Given I am on logon page screen MANUAL
When I navigate to the fee schedule screen with fee MANUAL
Then I see Ongoing advice fee and Licensee advice fee MANUAL


Scenario:Verify the Printer icon for client view
Meta:
@categories miniregression fullregression
Given I am on logon page screen MANUAL
When I navigate to the fee schedule screen with fee MANUAL
Then I see icon getting clicked MANUAL


Scenario:Verify the download icon for client view
Meta:
@categories miniregression fullregression
Given I am on logon page screen MANUAL
When I navigate to the fee schedule screen with fee MANUAL
And I click on the download icon MANUAL
Then I see icon getting clicked MANUAL


Scenario:Verify no Edit Fee link for Client
Meta:
@categories miniregression fullregression
Given I am on logon page screen MANUAL
When I navigate to the fee schedule screen with fee MANUAL
Then I did not see Edit Fee link MANUAL


Scenario:Verify the error message for screen with no fee for client view
Meta:
@categories miniregression fullregression
Given I am on logon page screen MANUAL
When I navigate to the fee schedule screen with no fee MANUAL
Then I see error message for ongoing and licenses fee as No fee applied MANUAL


Scenario:Verify the Fee Schedule link for client view
Meta:
@categories miniregression fullregression
Given I am on logon page screen MANUAL
When I navigate to the fee schedule screen with no fee MANUAL
Then I see Fee Schedule link MANUAL
When I click on Fee Schedule link MANUAL
Then I am on Fee Schedule screen with Fee MANUAL


Scenario:Verify the Tax invoice link for client view
Meta:
@categories miniregression fullregression
Given I am on logon page screen MANUAL
When I navigate to the fee schedule screen with no fee MANUAL
Then I see Tax Invoices link MANUAL
When I click on Tax Invoices MANUAL
Then I am on Tax Invoice Screen MANUAL


Scenario:Verify the fee Schedule for amount $0.00 for client view
Meta:
@categories fullregression
Given I am on logon page screen MANUAL
When I navigate to the fee schedule screen with no fee MANUAL
And I see the account with $0.00 flat dollar advice fee MANUAL
Then I see the fee schedule for the advice fee the $0.00 amount MANUAL