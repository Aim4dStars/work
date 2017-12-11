Narrative:
In order to charge a client a once off advice fee
As an adviser
I want to have the capability to charge an account a one-off advice fee

Meta:
@userstory US09811EnterOneOffAdviceFee


Scenario: total one-off advice fee for 12 months and available cash
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to fees screen
Then Verify Available cash is displayed
And Verify one off advice fee attribute is blank
And Verify total one-off advice fee charged in past 12 months has 2 decimals

Scenario: Help Icon
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to fees screen
And I mousehover on help icon for one off description
And I mousehover on help icon for one off
Then I see the help message text for one off as We'll deduct  this fee from your client's BT Cash account at the time you submit your request.
Then I see the help message text for one off Desc as TBD

Scenario: max 30 char Validation for description
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to fees screen
And I enter description MMMMMMMWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWTTTTTTTTT
Then I see only first 30 characters

Scenario: decimal Amount one off Validation
Meta:
@categories miniregression fullregression test123
Given I am on Login Page
When I navigate to fees screen
When I enter fees amount 101.1234
And I click on Next button
Then Check error message Enter an amount in the format '1.00'


Scenario: Zero Amount Client Validation
Meta:
@categories miniregression fullregressionn
Given I am on Login Page
When I navigate to fees screen
When I enter fees amount 0
And I click on Next button
Then Check error message Enter an amount greater than 0


Scenario: Negative Amount Client Validation
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to fees screen
When I enter fees amount -123
And I click on Next button
Then Check error message Enter an amount greater than 0

Scenario: Null Amount Client Validation
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to fees screen
When I click on Next button
Then Check error message Enter an amount

Scenario: Invalid Character Amount Client Validation
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to fees screen
When I enter fees amount @#$%
And I click on Next button
Then Check error message Enter an amount in the format '1.00'


Scenario: Read only access - Not Developed
Meta:
@categories fullregression
Given I am on Login Page manual
When I navigate to fees screen manual
Then one of charge one-off advice should not appear manual


Scenario: No available Cash Scenario - Avaloq
Meta:
@categories avalog
Given I am on Login Page
When I navigate to fees screen
Then I see Available cash as $0.00

Scenario: No one-off advice fee charged - Avaloq
Meta:
@categories avalog
Given I am on Login Page
When I navigate to fees screen
Then I see Total one off Advice Fee charged in past 12 months should as $0.00
 