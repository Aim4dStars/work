Narrative:US1154:Receipt Screen- Verify 'Receipt Screen' for advice fee
In order to charge a client a once off advice fee
As an adviser
I want to have the capability to charge an account a one-off advice fee
					 
Meta:
@userstory US09814RecieptScreen

Scenario: Receipt page with Description
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to fees screen
And I enter fees amount $12.00
And I enter Description text as Test Description
And I click on Next button
Then I am on confirm page
When I check client agreement box in the Confirm Screen
And I click on Submit button
Then I am on Receipt Page with fees charged as $12.00
And I am on Receipt page with description as Test Description
And I see message text as Advice fee successfully charged
And Ensure date displays in correct format

Scenario: Receipt page with no Description
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to fees screen
And I enter fees amount $12.00
And I click on Next button
Then I am on confirm page
When I check client agreement box in the Confirm Screen
And I click on Submit button
Then I am on Receipt Page with fees charged as $12.00
And I am on Receipt page with description as -
And I see message text as Advice fee successfully charged
And Ensure date displays in correct format

Scenario: Return to Account Overview
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to fees screen
When I enter fees amount $12.00
And I click on Next button
Then I am on confirm page
When I check client agreement box in the Confirm Screen
And I click on Submit button
Then I am on Receipt Page with fees charged as $12.00
And I see message text as Advice fee successfully charged
And Ensure date displays in correct format
When I click on Return to Accounts Overview
Then I am on the client's overview page

Scenario: Return to client list
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to fees screen
When I enter fees amount $12.00
And I click on Next button
Then I am on confirm page
When I check client agreement box in the Confirm Screen
And I click on Submit button
Then I am on Receipt Page with fees charged as $12.00
And I see message text as Advice fee successfully charged
And Ensure date displays in correct format
When I click on return to client list button
Then I am on the client list page

Scenario: Receipt page PDF download - Not Developed
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to fees screen
When I enter fees amount $100.00
And I click on Next button
Then I am on confirm_page with header name as Confirm
When I check client agreement box in the Confirm Screen
And I click on Submit button
Then I am on Receipt Page with header name as Receipt
And I click download pdf
