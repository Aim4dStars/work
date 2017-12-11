Narrative:
In order to charge a client a once off advice fee
As an adviser
I want to have the capability to charge an account a one-off advice fee

Meta:
@userstory US09812AvailableCashCheck

Scenario: Fee entered correctly to submit
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to fees screen
And I enter fees amount 1000
And I click on Next button
Then I am on confirm_page with header name as Confirm


Scenario: Fee entered greater than Available cash - Avaloq
Meta:
@categories avaloq
Given I am on Login Page
When I navigate to fees screen
And I enter fees amount 123456789101112131
And I click on Next button
Then I see Error Message for one off as The one-off advice fee entered exceeds the available cash amount. Please adjust accordingly.
When I enter fees amount 12345678910111213141516
Then ensure error message removed
When I click on Next button
Then I see Error Message for one off as The one-off advice fee entered exceeds the available cash amount. Please adjust accordingly.

Scenario: Fee entered greater than max cap fee - Avaloq
Meta:
@categories avaloq
Given I am on Login Page
When I navigate to fees screen
And I enter fees amount 123456789101112131
And I click on Next button
Then I see Error Message for one off as The one-off advice fee entered exceeds the maximum annual cap allowed for this fee. Please adjust accordingly.


Scenario: Fee entered greater than Available cash and Fee entered greater than max cap fee - Avaloq
Meta:
@categories avaloq
Given I am on Login Page
When I navigate to fees screen
And I enter fees amount 123456789101112131
And I click on Next button
Then I see Error Message for one off as The one-off advice fee entered exceeds the available cash amount. Please adjust accordingly.
Then I see Error Message for one off as The one-off advice fee entered exceeds the maximum annual cap allowed for this fee. Please adjust accordingly.