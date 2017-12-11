Narrative: US1645:Client Search
In order to view the details of a particular client
As an adviser, adviser assistant, para planner, practice manager or dealer group manager 
I want to directly search for that client

Meta:
@userstory US1645ClientSearch

Scenario: Client Search field – initial state – Not Developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
Then I see only one search field having Search text input area and ghost text as Search client or account name
And I see Clear icon (‘X’) and Magnifying glass


Scenario: Client Search field yields results – Not Developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
And I type in the search field as Ab
Then I see that each search string is found at the beginning of one or more words in the client or account name
And I see Client list refreshed to display the matching client or account name


Scenario: Client Search field yields results Alternate Scenario- No Matching Client - Not Developed
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Clients List screen
And I type in the search field as Ab
Then I see the error message Err.IP-0134 There are no clients matching your filter criteria


