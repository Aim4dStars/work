Narrative: US1979:Maturing term deposits Verify - Maturing term deposits Section
In order to review my client’s account key investment information
As an adviser paraplanner or Adviser Assistant
I want to view Maturing term deposits

Meta:
@userstory US1979Maturingtermdeposits

Scenario: Verify data for Maturing term deposits Account Overview section
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see records of term deposits maturing in the next 90 days in ascending order as
|Icon|Days Left|Date|Cash|
||85 days left|30 Oct 2014|$300,000.00|
||85 days left|30 Oct 2014|$300,000.00|
||85 days left|30 Oct 2014|$300,000.00|
||85 days left|30 Oct 2014|$300,000.00|
||85 days left|30 Oct 2014|$300,000.00|
||85 days left|30 Oct 2014|$300,000.00|
||85 days left|30 Oct 2014|$300,000.00|


Scenario: Verify the screen attributes for Maturing term deposits
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see logo icon of Term deposit
And I see No days left text for a maturing term deposit
And I see maturity date in valid date format
And I see No days left text and maturity date as hyperlink


Scenario:Verify the Portfolio valuation report Term deposit section
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Account overview screen
And I click on number of days left link PENDING
Then I am on Portfolio valuation report Term deposit section PENDING


Scenario:Verify the positive value of dollar amount
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see that the TD dollar amount displayed is positive PENDING
And I see a valid format for the TD dollar amount PENDING


Scenario:Verify the Portfolio valuation report Term deposit section on clicking term deposits action link
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see term deposits action link PENDING
When I click on action icon PENDING
Then I am on Portfolio valuation report Term deposit section PENDING


Scenario:Verify the Maturing term deposits TD calculator screen
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see TD calculator action link  PENDING
When I click on TD calculator action icon PENDING
Then I see TD calculator screen PENDING


Scenario: Verify Maturing term deposits section with zero status (no TDs maturing in the next 90 days)
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Account overview screen
And I see no data for Maturing term in next 90 days PENDING
Then I see the error message as There are no term deposits maturing within next 90 days PENDING


Scenario: Verify Maturing term deposits section with zero status (no TDs at all )
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Account overview screen
And I see no data for Maturing term deposits at all PENDING
Then I see the error message as There are no term deposits PENDING