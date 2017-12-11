Narrative: US1647:ClientRecordCount
In order to know which rows in the list of clients I can currently see
As an adviser, adviser assistant, para planner, practice manager or dealer group manager  
I want to see the row numbers and total row count of the client table

Meta:
@userstory US1647ClientRecordCount

Scenario: Client record count
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
Then I see row number indicator Showing n-m of r pattern