Narrative: US008921:Edit Fee Schedule- Sliding Scale Max Checks
As an adviser
I want to have the capability to Check that amended fee schedule conforms with product limits

Meta:
@userstory US008921SlidingScaleMaxChecks

Scenario:Verify the error message for exceeding the maximum rate
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I enter a rate for a tier greater than the maximum allowed AVALOG
Then I see an error message AVALOG


Scenario:Verify the error message for exceeding the percentage value for flat dollar component
Meta:
@categories fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I get an account fee schedule with flat dollar component and Percentage fee AVALOG
When I enter a percentage value more than the maximum allowed AVALOG
Then Verify error message as value entered exceeds the product maximum. Please adjust accordingly.AVALOG


Scenario:Verify the error message for exceeding the percentage value for Sliding Scale
Meta:
@categories fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I get an account fee schedule with Sliding Scale AVALOG
When I enter a percentage value for sliding more than the maximum allowed AVALOG
Then Verify error message as value entered exceeds the product maximum. Please adjust accordingly. AVALOG