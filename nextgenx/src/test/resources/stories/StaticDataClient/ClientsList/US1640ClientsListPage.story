Narrative: US1640:Clients List
In order to view list of clients
As an adviser, adviser assistant, para planner, practice manager or dealer group manager 
I want to use the Client List page

Meta:
@userstory US1640ClientsListPage

Scenario: - Client list filter – initial state Scenario- Filtered records
Meta:
@categories miniregression fullregression rere
Given I am on Login Page
When I navigate to Clients List screen
Then I see Row counter showing n-m of r
And I get client records is:
|name|adviser|cash|portfolioval|
|Glenn, Maxwell||678,445.00|198.34|
|Ricky, Pointing||1,234,567.12|12.00|
|Shane, Warne||678,445.00|198.34|
|Smith, Adrian||5,002,309.73|100,530,910.44|
|Steve, Waugh||12,345.67|4,545.67|
And I see filtered by message as Status
And I see Sort order drop-down again all 4 fields


Scenario: - Client list filter – initial state Scenario- Filtered records 2
Meta:
@categories miniregression fullregression ghgh
Given I am on Login Page
When I navigate to Clients List screen
And I click on collapsed state filter button
And I select Available cash value as All Ranges
And I select Portfolio Valuation value as All Ranges
And I select Product value as All
And I uncheck all the status in the client list
And I click on Update Button for Client List
Then I see filtered by message as No filters applied


Scenario: - Client list filter – Header data and filter data displayed-Not Developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
Then I see 'Add Client' link
Then I see Client or account name search field with clear icon and search icon
And I see download icon,print icon and collapsed filter icon


Scenario: - Client list filter – See more Scenario
Meta:
@categories fullregression 
Given I am on Login Page
When I navigate to Clients List screen
Then I see Row counter showing n-m of r
And I see number of client records displayed as <Count>
When I click on 'SEE MORE' button
Then I see more client records displayed as less than or equal to <Count>
When I click on show more till all the records get displayed
Then I see message There are no more results to display at the bottom of the list
Examples:
|Count|
|5|


Scenario: - Client list filter – initial state - No Client Exist
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Clients List screen
And I see the message No results have been found that match the search criteria you have entered. Please check the search criteria you have entered and amend if required.  Alternatively you can enter in search criteria for a new client account
