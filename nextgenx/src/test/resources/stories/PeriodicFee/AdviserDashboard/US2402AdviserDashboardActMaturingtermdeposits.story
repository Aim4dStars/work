Narrative: US2402:Maturing term deposits Section- Verify Maturing term deposits
In order to take actions on my clients’ recent priority requests and effectively manage their investments
As an adviser, adviser’s paraplanner or assistant and practice manager
I want to view all my client’s account recent Maturing term deposits

Meta:
@userstory US2402AdviserDashboardActMaturingtermdeposits

Scenario: Verify data for Maturing term deposits section
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
Then I see header as hyperlink for Maturing term deposits
And I see account name as hyperlink for Maturing term deposits
And I see result count text for Maturing term deposits
And I see maturing term deposit text and TD calculator


Scenario: Verify Maturing term deposits section Header
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
And I click on header for Maturing term deposits
Then I see deposit maturity report page not developed


Scenario: Verify Maturing term deposits section Result Count
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
Then I see valid result count format for Maturing term deposits
When I click on account name for Maturing term deposits
Then I see Portfolio valuation page not developed


Scenario: Verify Maturing term deposits section TD calculator
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
And I click on TD calculator
Then I see TD calculator screen not developed


Scenario: Verify Maturing term deposits section TD records
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
Then I see valid Maturity date format
Then I see valid days left format
Then I see amount with valid 2 decimal


Scenario: Verify Maturing term deposits section with no TD for next 30 days -Avaloq
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard -remove this while testing with Avaloq
Then I see header as hyperlink for Maturing term deposits
Then I see no result message2 as text for Maturing term deposits


Scenario: Verify Maturing term deposits section with zero status -Avaloq
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard-remove this while testing with Avaloq
Then I see header and no results message as text for Maturing term deposits


Scenario: Verify Maturing term deposits section sort order
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
Then I see 3 of the earliest maturing TDs in ascending order of their maturity