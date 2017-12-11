Narrative: US1981:Orders in progress Section
In order to review my client’s account key investment information
As an adviser paraplanner or Adviser Assistant
I want to view Orders in progress Section

Meta:
@userstory US1981Ordersinprogress


Scenario: Verify data for Orders in progress Section
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see Orders in progress header as hyperlink
When I click on Orders in progress link
Then I see AC order status page-not developed


Scenario:Verify the order status page on clicking Buys Applications header link
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see Buys Applications as hyperlink
When I click on Buys Applications link
Then I see AC order status filtered by order status as in progress, order type as sells Buys Applications, orders as In progress status-not developed


Scenario:Verify the order status page on clicking Sells Redemptions link
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see Sells Redemptions header as hyperlink
When I click on Sells Redemptions link
Then I see AC order status filtered by order status as in progress, order type as sells Sells Redemptions , orders as In progress status -not developed


Scenario:Verify the Buys application total amount-Avaloq
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see Buys application total amount as sum of all Active buys applications orders - Avaloq


Scenario:Verify the Sells redemptions total amount-Avaloq
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see Sells redemptions total amount as sum of all Active sells applications orders-Avaloq


Scenario: Verify Orders In Progress Alternate Scenario - Zero State
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Account overview screen
Then I see In Progress order amounts for order types Buys Applications as $0.00
And I see In Progress order amounts for order types Sells Redemptions as $0.00

