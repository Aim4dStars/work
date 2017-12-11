Narrative: US2401:Draft Application Section- Verify Draft Application
In order to take actions on my clients’ recent priority requests and effectively manage their investments
As an adviser, adviser’s paraplanner or assistant and practice manager
I want to view all my client’s account recent Draft Application

Meta:
@userstory US2401AdviserDashboardActDraftApplication

Scenario: Verify data for Draft Application section
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
Then I see header and account name as hyperlink for Draft Application
And I see result count and client account type as text for Draft Application
And I see Draft application date


Scenario: Verify Draft Application section Header - Last Step not developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
And I click on header for Draft Application
Then I see on boarding page not developed


Scenario: Verify Draft Application section Result Count and date format - Last Step not developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
Then I see valid result count format for Draft Application
And I see valid date format for Draft Application
When I click on account name for Draft Application
Then I see Account Appliation page not developed


Scenario: Verify Draft Application section sort order
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
Then I see 3 most recent draft applications are shown in the descending date


Scenario: Verify Draft Application section with zero status -Avaloq
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard - remove this while testing with avaloq
Then I see header and no results message as text for Draft Application
And I see add account as link for Draft Application
When I click on add account for Draft Application
Then I see Account application page not developed