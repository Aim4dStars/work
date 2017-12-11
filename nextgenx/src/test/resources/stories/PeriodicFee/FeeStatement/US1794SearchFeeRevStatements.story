Narrative: US1794:Fee Revenue Statements
In order to view a fee revenue statement
As an Dealer group (or an adviser) 
I want to be able to download a pre-generated fee revenue statement

Meta:
@userstory US1794SearchFeeRevStatements

Scenario: Fee statement screen list records
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to FeeRevenue Statements screen
Then I see default list all Fee Revenue Statements
And I see displayed date in the list is last day of the disbursement period
And I verfiy the correct format of the dispalyed date
And I verify the descending order sort fot the displayed list
And I see the name description dates in the correct format representing the start and end of the disbursement period
And I see action button for each fee revenue statement
And I see Row Returns Statements in the proper format 


Scenario: Fee statement Payment goes directly to the Dealer Group-Avaloq
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to FeeRevenue Statements screen PENDING
Then I am not able to navigate to the FeeRevenue Statements screen


Scenario: Fee statement unavailable date
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to FeeRevenue Statements screen
And I give start date 01 Jan 2014
And I give end date 01 Jun 2014
And I click on search
Then I see Error message Err.IP-013

Scenario: Fee statement screen menu display
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to FeeRevenue Statements screen
And I give start date 01 Jan 2014
And I give end date 01 Jun 2014
And I click on search
Then I see 50 fee revenue statements
And I see progressive button at the bottom
When I click on the progressive button
Then I see more records


Scenario: Fee statement screen More Scenario 4


Scenario: Fee statement screen - Adviser/Dealer group that gets paid client fees directly 
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to FeeRevenue Statements screen
And I give start date 01 Jan 2014
And I give end date 01 Jun 2014
And I click on search
Then I see records
When I click on the download icon
Then I see the action menu with options download in PDF or to download in CSV
And I see Size of the PDF or CSV rounded to the nearest whole measurement without decimal places


Scenario: Fee statement screen - Sorting Icon 
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to FeeRevenue Statements screen
And I give start date 01 Jan 2014
And I give end date 01 Jun 2014
And I click on search
Then I see records
When I click on the date sort icon
Then I see list sorted in the reverse order as ascending and the sort icon changing its direction
And I click on the date sort icon
Then I see records