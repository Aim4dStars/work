Narrative: US008912:Edit Fee Schedule- Adding a Fee Component
As an adviser
I want to have the capability to add a Fee Component

Meta:
@userstory US008912AddingAFeeComponent

Scenario:Verify the three components on screen for ongoing fee and licensee fee
Meta:
@categories miniregression fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
Then I see Dollar Fee Percentage Fee and Sliding Scale sections for Ongoing fee
And I see Dollar Fee Percentage Fee and Sliding Scale sections for licensee fee


Scenario:Verify the Dollar fee component is active for ongoing fee
Meta:
@categories miniregression fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
Then I see Dollar fee component is active for ongoing fee
When I clicks on Dollar component for ongoing fee
Then I see the active Dollar fee component panel for ongoing fee
And I see amount pa defaulted to be blank and CPI indexation box unchecked
And I see Dollar fee compoenent is inactive for ongoing fee


Scenario:Verify the Dollar fee component is active for licensee fee
Meta:
@categories miniregression fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
Then I see Dollar fee component for licensee fee is active
When I clicks on Dollar component for licensee fee
Then I see the active Dollar fee component panel for licensee fee
And I see Dollar fee compoenent is inactive for licensee fee


Scenario:Verify the Percentage fee component is active for ongoing fee
Meta:
@categories miniregression fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
Then I see Percentage fee component is active
When I clicks on Percentage component for ongoing fee
Then I see a Percentage fee panel with three empty cluster fields
And I see Percentage fee component is inactive for ongoing fee
And I see Sliding Scale component is inactive for ongoing fee


Scenario:Verify the Percentage fee component is active for licensee fee
Meta:
@categories miniregression fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
Then I see Percentage fee component is active for licensee fee
When I clicks on Percentage component for licensee fee
Then I see a Percentage fee panel with three empty cluster fields for licensee fee
And I see Percentage fee component is inactive for licensee fee
And I see Sliding Scale component is inactive for licensee fee


Scenario:Verify the Sliding Scale component is active for ongoing fee
Meta:
@categories miniregression fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
Then I see Sliding Scale component is active for ongoing fee
When I clicks on Sliding Scale for ongoing fee
Then I see a Sliding Scale with with two tiers for ongoing fee
And I see Sliding Scale component is inactive for ongoing fee
And I see Percentage fee component is inactive for ongoing fee


Scenario:Verify the Sliding Scale component is active for licensee fee
Meta:
@categories miniregression fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
Then I see Sliding Scale component is active for licensee fee
When I clicks on Sliding Scale for licensee fee
Then I see a Sliding Scale with with two tiers for licensee fee
And I see Sliding Scale component is inactive for licensee fee
And I see Percentage fee component is inactive for licensee fee


Scenario:Verify the Confirmation Screen
Meta:
@categories miniregression fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
And I clicks on Dollar component for ongoing fee
And I enter dollar value for dollar fee component as 50 PENDING
When I clicks on Next button on FeeSchedule Screen PENDING
Then I am on Confirmation screen PENDING


Scenario:Dollar fee is inactive in case of ngoing advice fee has a flat dollar component or a flat dollar component with percentage or sliding scale fee
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees PENDING
And I see Ongoing advice fee has either a flat dollar component or a flat dollar component with percentage or sliding scale fee PENDING
Then I see Dollar component for ongoing is inactive PENDING


Scenario:Percentage fee is inactive in case of Ongoing advice fee has a sliding scale fee with or without a flat dollar component
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees PENDING
And I see Ongoing advice fee has a sliding scale fee with or without a flat dollar component PENDING
Then I see Percentage component for ongoing is inactive PENDING
And I see Sliding Scale component is inactive PENDING


Scenario:Sliding Scale is inactive in case of Ongoing advice fee has a percentage fee with or without a flat dollar component
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees PENDING
And I see Ongoing advice fee has a percentage fee with or without a flat dollar component PENDING
Then I see Sliding Scale is inactive PENDING
And I see Percentage component is inactive PENDING


Scenario:All three components are inactive
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees PENDING
And I see Licensee advice fee has a percentage fee with flat dollar component PENDING
Then I see Dollar percentage and Sliding components are inactive PENDING


Scenario:All three components are active for Ongoing fee
Meta:
@categories miniregression fullregression
Given I am on logon page screen
When I navigate to Fee Schedule Screen with no fee
And I click on Edit fees link
Then I see Dollar fee component is active for ongoing fee
And I see Percentage fee component is active
And I see Sliding Scale component is active for ongoing fee


Scenario:All three components are active for licensee fee
Meta:
@categories miniregression fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
Then I see Dollar fee component for licensee fee is active
And I see Percentage fee component is active for licensee fee
And I see Sliding Scale component is active for ongoing fee


Scenario:Verify the text displayed while hovering on help icon
Meta:
@categories fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
When I hover over the help icon for ongoing fee PENDING
Then I see the text displayed for help icon PENDING
When I hover away PENDING
Then I see the test disappears PENDING