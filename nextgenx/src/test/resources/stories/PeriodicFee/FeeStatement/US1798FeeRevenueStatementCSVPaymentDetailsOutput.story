Narrative: US1798:Fee Revenue Statements
In order to view a fee revenue statement
As an Dealer group (or an adviser) 
I want to be able to download a pre-generated fee revenue statement

Meta:
@userstory US1798FeeRevenueStatementCSVPaymentDetailsOutput

Scenario: Fee statement screen CSV Validation
Meta:
@categories fullregression
Given I am on Fee Revenue Statements download
When I give start date 01 Jan 2014
And I give end date 01 Jun 2014
And I click on search
Then I get records
And I download the CSV

Scenario: Fee statement screen CSV revenue validation
Meta:
@categories fullregression
Given I am on Fee Revenue Statements download
When I open the CSV document
Then I get 3 sections as per document
And I verified the content


