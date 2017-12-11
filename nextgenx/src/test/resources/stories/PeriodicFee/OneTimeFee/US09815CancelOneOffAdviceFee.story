Narrative:US1155:Cancel One-Off Advice Fee- Verify 'Cancel' functionality
In order to charge a client a once off advice fee
As an adviser
I want to have the capability to charge an account a one-off advice fee
					 
Meta:
@userstory US09815CancelOneOffAdviceFee

Scenario: Fee Screen - Cancel - Yes button
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to fees screen
And I enter fees amount $100.00
And I click on Cancel button
And I click on Yes button on the model popup
Then I am on index page with title Home

Scenario: Fee Screen - Cancel -No button
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to fees screen
And I enter fees amount $100.00
And I click on Cancel button
And I click on No button on the model popup
Then I am on Fee Detail screen with pre populated details amount as 100.00

Scenario: Fee Screen - Cancel From Header Panel - Yes button
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to fees screen
And I enter fees amount $100.00
And I click on Cancel button from header panel
And I click on Yes button on the model popup
Then I am on index page with title Home

Scenario: Fee Screen - Cancel From Header Panel -No button
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to fees screen
And I enter fees amount $100.00
And I click on Cancel button from header panel
And I click on No button on the model popup
Then I am on Fee Detail screen with pre populated details amount as 100.00


Scenario: Confirm Screen - Cancel - Yes button
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to fees screen
And I enter fees amount 100.00
And I click on Next button
Then I am on confirm page
When I click on Cancel button on confirm page
And I click on Yes button on the model popup
Then I am on Charge One off Advice Fee screen with header name as Charge one-off advice fee

Scenario: Confirm Screen - Cancel - No button
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to fees screen
And I enter fees amount 100.00
And I click on Next button
Then I am on confirm page
When I click on Cancel button on confirm page
And I click on No button on the model popup
Then I am on confirm_page with header name as Confirm

Scenario: Confirm Screen - Cancel From Header Panel - Yes button
Meta:
@categories fullregression 
Given I am on Login Page
When I navigate to fees screen
And I enter fees amount 100.00
And I click on Next button
Then I am on confirm page
When I click on Cancel button from header panel
And I click on Yes button on the model popup
Then I am on Charge One off Advice Fee screen with header name as Charge one-off advice fee

Scenario: Confirm Screen - Cancel From Header Panel - No button
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to fees screen
And I enter fees amount 100.00
And I click on Next button
Then I am on confirm page
When I click on Cancel button from header panel
And I click on No button on the model popup
Then I am on confirm_page with header name as Confirm