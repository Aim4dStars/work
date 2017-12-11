Narrative: US1652:Table Sort
In order to view the client list in the order that suits my purpose 
As an adviser, adviser assistant, para planner, practice manager or dealer group manager  
I want to sort the client list table

Meta:
@userstory US1652TableSort

Scenario: Table Sort
Meta:
@categories fullregression hema
Given I am on Login Page
When I navigate to Clients List screen
Then I see sort order dropdown containing list of available 5 necessary options
When I select the sort by dropdown option as Available Cash Ascending
Then I get client records is:
|name|adviser|cash|portfolioval|
|Glenn, Maxwell||678,445.00|198.34|
|Ricky, Pointing||1,234,567.12|12.00|
|Shane, Warne||678,445.00|198.34|
|Smith, Adrian||5,002,309.73|100,530,910.44|
|Steve, Waugh||12,345.67|4,545.67|
When I select the sort by dropdown option as Available Cash Descending
Then I get client records is:
|name|adviser|cash|portfolioval|
|Glenn, Maxwell||678,445.00|198.34|
|Ricky, Pointing||1,234,567.12|12.00|
|Shane, Warne||678,445.00|198.34|
|Smith, Adrian||5,002,309.73|100,530,910.44|
|Steve, Waugh||12,345.67|4,545.67|
When I select the sort by dropdown option as Portfolio Valuation Ascending option
Then I get client records is:
|name|adviser|cash|portfolioval|
|Glenn, Maxwell||678,445.00|198.34|
|Ricky, Pointing||1,234,567.12|12.00|
|Shane, Warne||678,445.00|198.34|
|Smith, Adrian||5,002,309.73|100,530,910.44|
|Steve, Waugh||12,345.67|4,545.67|
When I select the sort by dropdown option as Portfolio Valuation Descending option
Then I get client records is:
|name|adviser|cash|portfolioval|
|Glenn, Maxwell||678,445.00|198.34|
|Ricky, Pointing||1,234,567.12|12.00|
|Shane, Warne||678,445.00|198.34|
|Smith, Adrian||5,002,309.73|100,530,910.44|
|Steve, Waugh||12,345.67|4,545.67|
When I select the sort by dropdown option as Client name
Then I get client records is:
|name|adviser|cash|portfolioval|
|Glenn, Maxwell||678,445.00|198.34|
|Ricky, Pointing||1,234,567.12|12.00|
|Shane, Warne||678,445.00|198.34|
|Smith, Adrian||5,002,309.73|100,530,910.44|
|Steve, Waugh||12,345.67|4,545.67|





