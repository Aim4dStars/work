Narrative: US1653:Client List Filter
In order to limit the number of clients displayed 
As an adviser, adviser assistant, para planner, practice manager or dealer group manager  
I want to able to refine my search criteria using a search filter

Meta:
@userstory US1653ClientListFilter

Scenario: Client list filter – initial state
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
And I click on collapsed state filter button
Then I see Portfolio valuation dropdown list with the necessary 7 options as All ranges and Under $100K and $100K to $300K and $300K to $500K and $500K to $1M and $1M to $2M and More than $2M
And I see Available cash dropdown list with the necessary 7 options as All ranges and Under $100K and $100K to $300K and $300K to $500K and $500K to $1M and $1M to $2M and More than $2M
And I see State drop down list with necessary eleven options as All states and All International addresses and All Australian addresses and ACT and NSW and NT and QLD and SA and TAS and VIC and WA
And I see Status check boxes with options Active and Closed accounts and Pending registration
And I see Active option is checked
And I see Portfolio valuation dropdown default value as All ranges
And I see Available cash dropdown default value as All ranges
And I see State drop down list with default value as All States
And I see Update button and Reset button and Clear button
Then I see open filter section with Close'X' button
And I see Adviser search field Containing a magnifying glass icon followed by ghost text of ‘Search'


Scenario: Client list filter – initial state-Alternate Scneario
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
And I click on collapsed state filter button
And I select Portfolio Valuation value as More than $2M
And I select Available cash value as More than $2M
And I click on Update Button for Client List
Then I see Filtered by message Available cash,Portfolio valuation,Status



Scenario: Client list filter – Update Button
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
And I click on collapsed state filter button
And I select State value as All
And I select Portfolio Valuation value as More than $2M
And I select Available cash value as All Ranges
And I click on Update Button for Client List
Then I get client records is:
|name|adviser|cash|portfolioval|
|Smith, Adrian||5,002,309.73|100,530,910.44|


Scenario: Client list filter – Update Button-Alternate Scenario
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
And I click on collapsed state filter button
And I select State value as All
And I select Portfolio Valuation value as $300K to $500K
And I select Available cash value as All Ranges
And I click on Update Button for Client List
Then I see the error message for no records displayed for client list No results have been found that match the search criteria you have entered. Please check the search criteria you have entered and amend if required. Alternatively you can enter in search criteria for a new client account.


Scenario: Client list filter – Update Button Scenario-2
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
And I click on collapsed state filter button
And I select State value as NSW
And I select Available cash value as All Ranges
And I select Portfolio Valuation value as All Ranges
And I click on Update Button for Client List
Then I get client records is:
|name|adviser|cash|portfolioval|
|Smith, Adrian||5,002,309.73|100,530,910.44|

Scenario: Client list filter – Update Button Alternate Scenario-2
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
And I click on collapsed state filter button
And I select State value as ACT
And I select Available cash value as All Ranges
And I select Portfolio Valuation value as All Ranges
And I click on Update Button for Client List
Then I see the error message for no records displayed for client list No results have been found that match the search criteria you have entered. Please check the search criteria you have entered and amend if required. Alternatively you can enter in search criteria for a new client account.


Scenario: Client list filter – Update Button Scneaio-3
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
And I click on collapsed state filter button
And I select State value as All
And I select Available cash value as More than $2M
And I select Portfolio Valuation value as All Ranges
And I click on Update Button for Client List
Then I get client records is:
|name|adviser|cash|portfolioval|
|Smith, Adrian||5,002,309.73|100,530,910.44|


Scenario: Client list filter – Update Button-Alternate Scenario-3
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
And I click on collapsed state filter button
And I select State value as All
And I select Available cash value as $100K to $300K
And I select Portfolio Valuation value as All Ranges
And I click on Update Button for Client List
Then I see the error message for no records displayed for client list No results have been found that match the search criteria you have entered. Please check the search criteria you have entered and amend if required. Alternatively you can enter in search criteria for a new client account.

Scenario: Client list filter – Update Button Scneaio-4
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
And I click on collapsed state filter button
And I select State value as All
And I select Available cash value as Under $100K
And I select Portfolio Valuation value as All Ranges
And I click on Update Button for Client List
Then I get client records is:
|name|adviser|cash|portfolioval|
|Steve, Waugh||12,345.67|4,545.67|


Scenario: Client list filter – Update Button Scneaio-5
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
And I click on collapsed state filter button
And I select State value as WA
And I select Portfolio Valuation value as Under $100K
And I select Available cash value as All Ranges
And I click on Update Button for Client List
Then I get client records is:
|name|adviser|cash|portfolioval|
|Steve, Waugh||12,345.67|4,545.67|

Scenario: Client list filter – Update Button Scneaio-6
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
And I click on collapsed state filter button
And I select State value as NT
And I select Available cash value as Under $100K
And I select Portfolio Valuation value as All Ranges
And I click on Update Button for Client List
Then I see the error message for no records displayed for client list No results have been found that match the search criteria you have entered. Please check the search criteria you have entered and amend if required. Alternatively you can enter in search criteria for a new client account.


Scenario: Client list filter – Update Button Scneaio-7
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
And I click on collapsed state filter button
And I select State value as All
And I select Product value as Model 1
And I select Available cash value as All Ranges
And I select Portfolio Valuation value as All Ranges
And I click on Update Button for Client List
Then I get client records is:
|name|adviser|cash|portfolioval|
|Steve, Waugh||12,345.67|4,545.67|

Scenario: Client list filter – Update Button Scneaio-7 Alternate
Meta:
@categories miniregression fullregression anand
Given I am on Login Page
When I navigate to Clients List screen
When I click on collapsed state filter button
And I select Product value as Model 1
And I select State value as All
And I select Available cash value as All Ranges
And I select Portfolio Valuation value as More than $2M
And I click on Update Button for Client List
Then I see the error message for no records displayed for client list No results have been found that match the search criteria you have entered. Please check the search criteria you have entered and amend if required. Alternatively you can enter in search criteria for a new client account.

Scenario: Client list filter – ‘Remember current selection’ -Scenario 2- Out Of Scope
Meta:
@categories miniregression fullregression
Given I am on Client Lists OUT OF SCOPE
Then I see filter section is collapsed OUT OF SCOPE
When I click on filter button OUT OF SCOPE
And I select Portfolio Valuation value OUT OF SCOPE as under $100K
And I select Remember current selection checkbox OUT OF SCOPE
And I navigate to some other page OUT OF SCOPE
And I navigate to Client lists screen again OUT OF SCOPE
Then I see the filtered by message and fields populated with data previously filled PENDING OUT OF SCOPE


Scenario: Client list filter – ‘Remember current selection’ -Scenario 3-Out Of Scope
Meta:
@categories miniregression fullregression
Given I am on Client Lists OUT OF SCOPE
Then I see filter section is collapsed OUT OF SCOPE
When I click on filter button OUT OF SCOPE
And I select Portfolio Valuation value as under $100K OUT OF SCOPE
And I select Remember current selection checkbox OUT OF SCOPE
And I login to some other session OUT OF SCOPE
And I navigate to Client lists screen again OUT OF SCOPE
Then I see the filtered by message and fields populated with data previously filled OUT OF SCOPE


Scenario: Client list filter – ‘Remember current selection’ -Scenario 4- Out Of Scope
Meta:
@categories miniregression fullregression
Given I am on Client Lists OUT OF SCOPE
Then I see filter section is collapsed OUT OF SCOPE
When I click on filter button OUT OF SCOPE
And I select Portfolio Valuation value as under $100K OUT OF SCOPE
And I navigate to some other page OUT OF SCOPE
And I navigate to Client lists screen again OUT OF SCOPE
Then I see blank fields for all OUT OF SCOPE
And I see No filters applied message OUT OF SCOPE


Scenario: Client list filter – ‘Remember current selection’ - Scenario 5-Out Of Scope
Meta:
@categories miniregression fullregression
Given I am on Client Lists OUT OF SCOPE
Then I see filter section is collapsed OUT OF SCOPE
When I click on filter button OUT OF SCOPE
And I select Portfolio Valuation value as under $100K OUT OF SCOPE
And I login to some other session OUT OF SCOPE
And I navigate to Client lists screen again OUT OF SCOPE 
Then I see blank fields for all OUT OF SCOPE
And I see No filters applied message OUT OF SCOPE


Scenario: Client list filter – Adviser search - Not Developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
And I enter in the adviser search field as Ha
Then I see each search string separated by spaces is found with First name or Last name starts with one of the exact search strings


Scenario: Client list filter – Adviser search Alternate Scenario-1 - - Not Developed
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Clients List screen
And I enter in the adviser search field as ZZ Not Developed
Then I see error message Err.IP-0127 No results found Not Developed


Scenario: Client list filter – Reset Button
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
And I click on collapsed state filter button
And I select Portfolio Valuation value as under $100K
And I click on the Reset Button
Then I see fields reset to system default
And I get client records is:
|name|adviser|cash|portfolioval|
|Glenn, Maxwell||678,445.00|198.34|
|Ricky, Pointing||1,234,567.12|12.00|
|Shane, Warne||678,445.00|198.34|
|Smith, Adrian||5,002,309.73|100,530,910.44|
|Steve, Waugh||12,345.67|4,545.67|


Scenario: Client list filter – Cancel Button
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Clients List screen
And I click on collapsed state filter button
And I select Available cash value as More than $2M
And I click on the Cancel Button
And I click on collapsed state filter button
And I click on the Cancel Button
Then I get client records is:
|name|adviser|cash|portfolioval|
|Glenn, Maxwell||678,445.00|198.34|
|Ricky, Pointing||1,234,567.12|12.00|
|Shane, Warne||678,445.00|198.34|
|Smith, Adrian||5,002,309.73|100,530,910.44|
|Steve, Waugh||12,345.67|4,545.67|


