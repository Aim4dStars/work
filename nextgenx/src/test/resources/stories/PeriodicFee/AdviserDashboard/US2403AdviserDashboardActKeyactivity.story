Narrative: US2403:Key activity Section- Verify Key activity
In order to take actions on my clients’ recent priority requests and effectively manage their investments
As an adviser, adviser’s paraplanner or assistant and practice manager
I want to view all my client’s account recent Key activity

Meta:
@userstory US2403AdviserDashboardActKeyactivity

Scenario: Verify data for Key activity section
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
Then I see help information icon
And I see activity type and client account name as hyperlink
And I see description as text
And I see priority indicator
And I see activity date and activity time stamp
And I see On-boarding tracking and Order status as action icon


Scenario: Verify data for Key activity section help
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
And I mousehover on help icon
Then I see help text PENDING


Scenario: Verify data for Key activity section activity type
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
And I click on account type for Key activity
Then I see Message center screen not developed


Scenario: Verify data for Key activity section client account name
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
And I click on client account name for Key activity
Then I see Message center screen not developed


Scenario: Verify Key activity section activity date
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
Then I see valid activity date format


Scenario: Verify Key activity section On-boarding
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
And I click on On-boarding tracking
Then I see On-boarding screen not developed


Scenario: Verify Key activity section Order status
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
And I click on Order status tracking
Then I see Order status screen not developed


Scenario: Verify Key activity section with zero status -Avaloq
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard -Remove this wile testing with avaloq
Then I see header and no results message as text for Key activity
Then I see On-boarding tracking and Order status as action icon


Scenario: Verify Key Activity section sort order
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
Then I see 10 of the most recent activities in descending time order
And I see 10 of the most recent activities as :
|Action|Icon|Type|Description|Time|
|Changes to accounts||120000674|Welcome to . Your account is open and ready to go.|6:10am AEST|
|Confirmed transactions||120000369|$amt will be pos_dir acct_name due to an interest ...|5:39am AEST|
|TD proceeds deposited||Two Birds Flying South Family Trust|$10,000 into BT Cash|5:14am AEST|
|Confirmed transactions||Two Birds Flying South Family Trust|$15,000 into BT Cash|3:14am AEST|
|Investment Proceeds Deposited||Christian Salzar|Sell VHA 30400 units at 8.90 Brokerage $34.00|6:14am AEST|
|Minimum cash breach||Mohammed Syed and Wazir Syed|Requested by Mohammed Syed|6:14am AEST|
|Confirmed transactions||Mohammed Syed and Wazir Syed|$10,000 into BT Cash|6:14am AEST|
|Confirmed transactions||Two Birds Flying South Family Trust|Requested by Mohammed Syed|6:14am AEST|
|Failed Deposit||Two Birds Flying South Family Trust|Cash minimum $10,000.00 (TBC)|6:14am AEST|
|Deposit Cleared||Two Birds Flying South Family Trust|$50,000 into BT Cash|5:39am AEST|


Scenario: Verify Key Activity section display rule-Avaloq
Meta:
@categories miniregresion fullregression
Given I am on Login Page
When I navigate to Adviser Dashboard
Then I see key activities if Adviser Dashboard flag is set to Yes and as per fulfillment rule not developed