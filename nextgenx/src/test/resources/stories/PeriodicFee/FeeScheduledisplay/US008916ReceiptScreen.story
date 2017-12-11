Narrative: US008916:Edit Fee Schedule- Receipt Screen
As an adviser
I want to have the capability to view Receipt Screen

Meta:
@userstory US008916ReceiptScreen

Scenario:Verify the advice fee details
Meta:
@categories miniregression fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
And I makes amendments to the advice fee PENDING
And I click on Next button PENDING
And I click on Submit button PENDING
Then I see advice fee details PENDING


Scenario:Verify the links on Receipt Page account
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
And I makes amendments to the advice fee PENDING
And I click on Next button PENDING
And I click on Submit button PENDING
Then I see Return to fee schedule hyperlinked PENDING
When I click on Return to fee schedule link PENDING
Then I am on fee schedule screen PENDING


Scenario:Verify the links on Receipt Page client
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
When I click on Edit fees PENDING
And I makes amendments to the advice fee PENDING
And I click on Next button PENDING
And I click on Submit button PENDING
Then I see Return to account overview link PENDING
When I click on Return to account overview link PENDING
Then I am on client overview page PENDING


Scenario:Verify the Fee Scedule report after removal of tiers
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
And I click on remove buttons for all fee components PENDING
And I click on Next button PENDING
And I click on Submit button PENDING
Then I see fee schedule with tiers removed PENDING