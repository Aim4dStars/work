Narrative: US2404:Dashboard disclaimers Section- Verify Dashboard disclaimers   
In order to take actions on my clients’ recent priority requests and effectively manage their investments 
As an adviser, adviser’s paraplanner or assistant and practice manager 
I want to view all my client’s account recent Dashboard disclaimers

Meta:
@userstory US2404AdviserDashboardActDashboarddisclaimers

Scenario: Verify text for Dashboard disclaimers section
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
Then I see Dashboard disclaimers text
