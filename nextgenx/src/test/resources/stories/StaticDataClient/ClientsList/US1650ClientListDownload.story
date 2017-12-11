Narrative: US1650:Client list download
In order to use the list within another application, e.g. spreadsheet
As an adviser, adviser assistant, para planner, practice manager or dealer group manager  
I want to download the list of clients in the table

Meta:
@userstory US1650ClientListDownload

Scenario: Client list download - Not developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
And I click on Download Icon - Not developed
Then I see the download section with PDF,CSV and XLS - Not developed

Scenario: Client list download values - - Not developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
And I click on Download Icon - Not developed
Then I see that the file has coloums - Not developed