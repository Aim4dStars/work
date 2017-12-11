Narrative: US008922:Edit Fee Schedule- Percentage Max Checks
As an adviser
I want to have the capability to test Percentage Max Checks

Meta:
@userstory US008922PercentageMaxChecks

Scenario:Verify the error message for exceeding the percentage value  for Percentage Max Checks
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
And I get an account fee schedule with flat dollar component and Percentage fee AVALOG
When I enter a percentage value more than the maximum allowed AVALOG
Then Verify error message as value entered exceeds the product maximum. Please adjust accordingly AVALOG


Scenario:Verify the error message for exceeding the percentage value for Percentage Max Checks
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
And I get an account fee schedule with flat dollar component and Percentage fee AVALOG
When I enter a percentage value more than the maximum allowed for corresponding fee AVALOG
Then Verify error message as value entered exceeds the product maximum. Please adjust accordingly AVALOG


