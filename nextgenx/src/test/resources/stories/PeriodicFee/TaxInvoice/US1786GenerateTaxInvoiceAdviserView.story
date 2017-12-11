Narrative: US1786:Generate Tax Invoice screen – Adviser View  
In order to have a snapshot view of the fees charged to an account for a specified period
As an Adviser
I want to be able to generate my (or my client’s) tax invoice

Meta:
@userstory US1786GenerateTaxInvoiceAdviserView

Scenario: Verify Tax Invoice Screen
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Tax Invoice screen
Then I see necessary attributes displayed on the screen


Scenario:Error for generate a tax invoice when provided future period
Meta:
@categories miniregression fullregression
Given I am on Tax Invoice screen PENDING
When I select future period in months drop down PENDING
Then I see Error Message PENDING
Scenario: Adviser to generate a tax invoice for an account that has terminated
Meta:
@categories miniregression fullregression
Given I am on Tax Invoice screen PENDING
When I try to to produce a tax invoice for an account that has terminated PENDING
Then I am not able to select that account to generate any tax invoices PENDING


Scenario: Adviser to generate a tax invoice for an account that has terminated Error
Meta:
@categories fullregression
Given I am on Tax Invoice screen PENDING
When I try to to produce a tax invoice a tax invoice for a period that contained no transactions and date is not in future PENDING
Then I see error message Err.IP-0135 PENDING