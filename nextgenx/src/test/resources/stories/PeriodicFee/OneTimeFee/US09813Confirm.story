Narrative:US1152:Confirm- Verify 'Confirm Screen' for advice fee
In order to charge a client a once off advice fee
As an adviser
I want to have the capability to charge an account a one-off advice fee

Meta:
@userstory US09813Confirm

Scenario: Confirm Page validation
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to fees screen
And I enter fees amount $12.00
And I click on Next button
Then I am on confirm page with amount as $12.00
And I am on confirm page with description as Test Description

Scenario: Confirm Page validation 2
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to fees screen
And I enter fees amount $12.00
And I enter Description text as Test Description
And I click on Next button
Then I am on confirm page with amount as $12.00
And I am on confirm page with description as Test Description

Scenario: PDF document -  Under Development
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to fees screen
And I enter fees amount $12.00
And I click on Next button
Then I am on confirm page with amount as $12.00
And I am on confirm page with description as Test Description
And I click on download

Scenario: Validation of agreement checkbox Text
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to fees screen
And I enter fees amount $12.00
And I click on Next button
Then I see agreement text I agree to provide my client(s) with the services associated with this fee and confirm I have received their written authorisation to charge the fee.

Scenario: Validation of agreement checkbox
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to fees screen
And I enter fees amount $12.00
And I click on Next button
And I click on Submit button
Then Agreement box get highlighted on same screen

Scenario: Available cash check validation insufficient cash - Manual
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to fees screen
When I enter fees amount $2000.00
And I click on Next button
Then verify error for insufficient cash- Manual

Scenario: technical issue validation - Manual
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to fees screen
And I enter fees amount "150.00" 
And I click on Next button
Then verify error for technical issues MANUAL

Scenario: Receipt page
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to fees screen
And I enter fees amount $100.00
And I click on Next button
And I check client agreement box in the Confirm Screen
And I click on Submit button
Then I am on Receipt Page with header name as Receipt

Scenario: Validation after one successful transaction - Last steps pending for Avaloq
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to fees screen
And I enter fees amount <amount>
And I enter Description text as <description>
And I click on Next button
Then I am on confirm page with amount as <amount>
And I am on confirm page with description as <description>
When I check client agreement box in the Confirm Screen
And I click on Submit button
Then I am on Receipt Page with header name as Receipt
Given I am on Login Page
When I navigate to fees screen
Then I verify the updated amount for Available cash and one-off advice fee charged
Examples:
|amount|description|
|$12.00|Test Description|