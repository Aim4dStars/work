Narrative: US008913:Edit Fee Schedule- Removing a Fee Component
As an adviser
I want to have the capability to remove a Fee Component

Meta:
@userstory US008913RemovingaFeeComponent

Scenario:Remove a fee component
Meta:
@categories miniregression fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
Then I see current fee schedule for an account PENDING
And I see X button for each fee component PENDING
When I click on Remove button for a fee component PENDING
Then I see the fee component is removed PENDING
When I click on Next button PENDING
Then I am Confirm Page PENDING


Scenario:Dollar component deleted when fee setup is flat dollar only
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
Then I see current fee schedule for an account with flat dollar only PENDING
When I click on Remove button for a Dollar fee component PENDING
Then I see Dollar fee component is active PENDING
And I see percentage fee component is active PENDING
And I see Sliding scale fee component is active PENDING


Scenario:Dollar component deleted when fee setup is flat dollar and percentage
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
Then I see current fee schedule for an account with flat dollar and percentage PENDING
When I click on Remove button for a Dollar fee component PENDING
Then I see Dollar fee component is active PENDING
And I see percentage fee component is inactive PENDING
And I see Sliding scale fee component is inactive PENDING


Scenario:Dollar component deleted when fee setup is flat dollar and sliding scale
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
Then I see current fee schedule for an account with flat dollar and sliding scale PENDING
When I click on Remove button for a Dollar fee component PENDING
Then I see Dollar fee component is active PENDING
And I see percentage fee component is inactive PENDING
And I see Sliding scale fee component is inactive PENDING


Scenario:Percentage component  deleted when fee setup is percentage only
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
Then I see current fee schedule for an account with percentage only PENDING
When I click on Remove button for a percentage fee component PENDING
Then I see Dollar fee component is active PENDING
And I see percentage fee component is active PENDING
And I see Sliding scale fee component is active PENDING


Scenario:Percentage component  deleted when fee setup is percentage fee and flat dollar
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
Then I see current fee schedule for an account with percentage and flat dollar PENDING
When I click on Remove button for a percentage fee component PENDING
Then I see Dollar fee component is inactive PENDING
And I see percentage fee component is active PENDING
And I see Sliding scale fee component is active PENDING


Scenario:Sliding scale component deleted when fee setup is sliding scale only
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
Then I see current fee schedule for an account with sliding scale only PENDING
When I click on Remove button for a sliding scale  PENDING
Then I see Dollar fee component is active PENDING
And I see percentage fee component is active PENDING
And I see Sliding scale fee component is active PENDING


Scenario:Sliding scale component deleted when fee setup is sliding scale and flat dollar
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
And I see X button for each fee component PENDING
When I click on Remove button for a sliding scale PENDING
Then I see Dollar fee component is inactive PENDING
And I see percentage fee component is active PENDING
And I see Sliding scale fee component is active PENDING


Scenario:Remove a fee component for a newly added component
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
When I click on Dollar fee button PENDING
Then I see X button for Dollar fee component PENDING
When I click on Remove button for a Dollar fee component PENDING
Then I see Dollar fee component is removed PENDING


Scenario:Verify error message after removing all fee components
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
And I click on remove buttons for all fee components PENDING
Then I see all fee components are active for ongoing fee PENDING
When I click on Submit button PENDING
Then Verify error for fee compoenents as No fee applied PENDING


Scenario:Verify Percentage fee component or the sliding scale component getting displayed
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
And I see an account fee schedule with flat dollar together and a Percentage fee component PENDING
And I click on Remove button for flat dollar component PENDING
Then I see Percentage fee component or the sliding scale component displayed PENDING