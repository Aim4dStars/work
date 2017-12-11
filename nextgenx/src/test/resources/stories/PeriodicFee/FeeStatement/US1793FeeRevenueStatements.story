Narrative: US1793:Fee Revenue StatementsVerify Calendar
In order to view a fee revenue statement
As a Dealer group (or an adviser)
I want to be able to download a pre-generated fee revenue statement

Meta:
@userstory US1793FeeRevenueStatements

Scenario: Fee statement screen Default
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to FeeRevenue Statements screen
Then I see page header titled as Fee Revenue Statements
And I see I would like to option
And I see Introductory text as This list only contains reports where either fees have been charged or an outstanding balance exist
And I see date picker with default to date as current date
And I see date picker with default from date as 1st day of the previous month
When I select future date in the date picker
Then I see Future date not getting selected


Scenario: Fee statement screen calendar 7 days
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to FeeRevenue Statements screen
And I click on calendar
And I select the last 7 days
Then I see revenue statements for last 7 days

Scenario: Fee statement screen calendar 30 days
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to FeeRevenue Statements screen
And I click on calendar
And I select the last 30 days
Then I see revenue statements for last 30 days

Scenario: Fee statement screen calendar current quarter
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to FeeRevenue Statements screen
And I click on calendar
And I select Current quarter
Then I see revenue statements for Current quarter

Scenario: Fee statement screen calendar previous quarter
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to FeeRevenue Statements screen
And I click on calendar
And I select Previous quarter
Then I see revenue statements for Previous quarter

Scenario: Fee statement screen calendar financial year
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to FeeRevenue Statements screen
And I click on calendar
And I select current financial year
Then I see revenue statements for current financial year

Scenario: Fee statement screen calendar last financial year
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to FeeRevenue Statements screen
And I click on calendar
And I select last financial year
Then I see revenue statements for last financial year

Scenario: Fee statement screen calendar specific date
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to FeeRevenue Statements screen
And I click on calendar
And I select Specific date
Then I see revenue statements for that specific date

Scenario: Fee statement screen calendar default date
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to FeeRevenue Statements screen
And I click on calendar
And I select Date range
Then I see revenue statements for that particular range of dates