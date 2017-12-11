Narrative: US1648:ClientListTable
In order to manage client’s details, open new accounts or perform client reporting
As an adviser, adviser assistant, para planner, practice manager or dealer group manager  
I want to view the list of clients to which I have access to

Meta:
@userstory US1648ClientListTable

Scenario: Client list table – initial state (collapsed)
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
Then I see Client List table headers as Name and Adviser and Available cash and Portfolio value
And I get client records is:
|name|adviser|cash|portfolioval|
|Glenn, Maxwell||678,445.00|198.34|
|Ricky, Pointing||1,234,567.12|12.00|
|Shane, Warne||678,445.00|198.34|
|Smith, Adrian||5,002,309.73|100,530,910.44|
|Steve, Waugh||12,345.67|4,545.67|


Scenario: - Client list table – Not Developed
Given I am on Login Page
When I navigate to Clients List screen
And I see Sort order indicator against the column headers arrow up for ascending and arrow down for descending
Then I see sorted records list


Scenario: Client list table – expanded details
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
And I click on the row expand toggle icon
Then I get expanded row details is:
|name|adviser|cash|portfolioval|
|Glenn Bond Maxwell • IDPS Individual Account ID 45284 |Sachin Tendulkar Full|678,445.00|198.34|
When I click on the account name
Then I am on Account Overview screen wih header name as Overview


Scenario: Client list table – expanded details-Permission -Not Developed
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Clients List screen
Then I get client records is:
|name|adviser|cash|portfolioval|
|Glenn, Maxwell||678,445.00|198.34|
|Ricky, Pointing||1,234,567.12|12.00|
|Shane, Warne||678,445.00|198.34|
|Smith, Adrian||5,002,309.73|100,530,910.44|
|Steve, Waugh||12,345.67|4,545.67|
And I see Adviser’ heading is hidden
And I see Adviser name and Adviser permission is displayed
