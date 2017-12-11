Narrative:US1158ONE OFF ADVICE FEE CAP CHECK- Verify
In order to charge a client a once off advice fee
As a adviser
I want to have the capability to charge an account a one-off advice fee
 
Meta:
@userstory US0982OneOffAdviceFeeCapCheck
  
Scenario: product cap check avaloq side - Manual
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to fees screen
When I enter fees amount 10000.00
And I click on Next button
Then I am on confirm page
When I check client agreement box in the Confirm Screen
And I click on Submit button
Then I see Error Message for one off as The one-off advice fee entered exceeds the maximum annual cap allowed for this fee. Please adjust accordingly.

Scenario: product cap check client side - Avaloq
Meta:
@categories fullregression avaloq
Given I am on Login Page
When I navigate to fees screen
When I enter fees amount 10000.00
And I click on Next button
Then I see Error Message for one off as The one-off advice fee entered exceeds the maximum annual cap allowed for this fee. Please adjust accordingly.
