Narrative: US008913:Edit Fee Schedule- Removing a Sliding Scale Tier
As an adviser
I want to have the capability to remove a Sliding Scale Tier

Meta:
@userstory US008913FeeScheduleRemovingaSlidingScaleTier

Scenario:Removal of a tier
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I click on the delete tier icon for the first tier
Then I see lower limit for first tier now is 0 PENDING
And I see no more delete icons now PENDING 
Then I see current fee schedule for an account PENDING
And I see last sliding scale tier with a remove button PENDING
When I click on Remove button PENDING
Then I see the tier get removed PENDING
And I see the last tier’s lower limit get changed PENDING
When I click on Next button PENDING
Then I am Confirm Page PENDING


Scenario:Verify Fee Schedule screen after removal of a tier
Meta:
@categories fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I click on the delete tier icon for the first tier
And I click on Cancel button PENDING
And I select yes for cancellation in the popup menu PENDING
Then I see the same first tier PENDING


Scenario:Verify the removal of Remove button
Meta:
@categories fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I get an account with a sliding scale that has only two tiers PENDING
Then I do no see the Remove tier button PENDING