Feature: US1794:Fee Revenue Statements
In order to view a fee revenue statement
As an Dealer group (or an adviser) 
I want to be able to download a pre-generated fee revenue statement

@FeeStatement 
Scenario: Fee statement screen
Given I am on Fee Revenue Statements screen
When I give start date "01 Jan 2014"
And I give end date "01 May 2014"
And I click on search
Then I get records
@FeeStatement 
Scenario: Fee statement screen
Given I am on Fee Revenue Statements screen
When I give start date "01 Jan 2011"
And I give end date "01 Jan 2012"
Then I get message for no records
@FeeStatement 
Scenario: Fee statement screen menu display
Given I am on Fee Revenue Statements download
When I give start date "01 Jan 2014"
And I give end date "01 Jun 2014"
And I click on search
Then I get records
And I download the PDF
