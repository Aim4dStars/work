Narrative:US2400:Saved Orders Section- Verify saved Order   
In order to take actions on my clients' recent priority requests and effectively manage their investments
As an adviser, adviser's paraplanner or assistant and practice manager
I want to view all my client's account recent Saved orders

Meta:
@userstory US2400AdviserDashboardActSavedOrder

Scenario: Verify data for Saved Order section
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
Then I see header and result count as text for saved order
And I see account name as hyperlink for saved order
And I see description text below account name for saved order


Scenario: Verify Saved Order section Result Count and date format - Last Step not developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
Then I see valid result count format for saved order
And I see valid date format for saved order
When I click on account name for saved order
Then I see Saved Order page not developed


Scenario: Verify Saved Order section with zero status -Avaloq
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard - remove this while testing with avaloq
Then I see header and no results message as text for saved order
Then I see place an order as link for saved order
When I click on place an order for saved order
Then I see Place an Order page not developed


Scenario: Verify Saved Order section sort order
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
Then I see upto 3 most recent saved orders are shown in the descending date

Scenario: Verify Account Name not displaying as hyperlink - Permission not developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
Then I see header and result count as text for saved order
And I see account name as text if not have Start entering a trade permission

Scenario: Verify Saved Order section with zero status - Permission not developed
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
Then I see header and no results message as text for saved order
Then I do not see place an order link if not have Start entering a trade permission