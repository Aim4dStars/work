Feature: US1793:Fee Revenue Statements- Verify Calendar
In order to view a fee revenue statement
As an Dealer group (or an adviser) 
I want to be able to download a pre-generated fee revenue statement

@FeeStatement 
Scenario: Fee statement screen
Given I am on Fee Revenue Statements screen
When I click on calendar
And I select the last 7 days
Then My date selected is for last 7 days  
@FeeStatement 
Scenario: Fee statement screen
Given I am on Fee Revenue Statements screen
When I click on calendar 
And I select the last 30 days
Then My date selected is for last 30 days
@FeeStatement 
Scenario: Fee statement screen
Given I am on Fee Revenue Statements screen
When I click on calendar 
And I select Current quarter 
Then My date selected is for Current quarter
@FeeStatement 
Scenario: Fee statement screen
Given I am on Fee Revenue Statements screen
When I click on calendar 
And I select Previous quarter 
Then My date selected is for Previous quarter
@FeeStatement 
Scenario: Fee statement screen
Given I am on Fee Revenue Statements screen
When I click on calendar 
And I select current financial year
Then My date selected is for current financial year 
@FeeStatement 
Scenario: Fee statement screen
Given I am on Fee Revenue Statements screen
When I click on calendar 
And I select Specific date 
Then My date selected is a default date
@FeeStatement 
Scenario: Fee statement screen
Given I am on Fee Revenue Statements screen
When I click on calendar 
And I select Date range
Then My date range selected is a default date

