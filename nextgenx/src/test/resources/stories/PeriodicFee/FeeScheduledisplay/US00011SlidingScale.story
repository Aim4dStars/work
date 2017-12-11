Narrative: US00011:SlidingScale
As an adviser
I want to be able to add/remove a sliding scale component

Meta:
@userstory US00011SlidingScale

Scenario:Adviser add a sliding scale
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I see the add fee component for sliding scale PENDING
And I verfiy that it has no fee calculation method associated and is made up of flat dollar fee component only PENDING
When I clicks on Sliding Scale for ongoing fee
Then I see Sliding Scale fee component with the cluster section headers Include term deposit and Include managed portfolios and Include cash and default unchecked checkboxes for all PENDING
And I see a Sliding Scale with with two tiers for ongoing fee
And I see default value in the 2 tiers as blank value for the first sliding scale upper limit tier and the text “and higher amounts” for the second sliding scale upper limit tier 
And I see blank value for each %pa and the starting lower limit and the last tier lower limit default to $0 
When I enter value in the percentage field as 12
And I enter upper limits for first tier as 1000
And I click on FeeNext button
Then I am on the Confirmations screen Confirm fees


Scenario:Adviser add a sliding scale More Scenarios-1
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
Then I see Sliding Scale component is inactive for ongoing fee
And I see Percentage fee component is inactive for ongoing fee


Scenario:Adviser add a sliding scale More Scenarios-2
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
Then I see Percentage fee component is inactive for ongoing fee
And I see Sliding Scale component is inactive for ongoing fee

Scenario:Adviser add a sliding scale More Scenarios-3
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
Then I see Percentage fee component is inactive for ongoing fee
And I see Sliding Scale component is inactive for ongoing fee
And I see Dollar fee compoenent is inactive for ongoing fee
And I see Sliding Scale fee component with the cluster section headers Include term deposit and Include managed portfolios and Include cash and dafault unchecked checkboxes for all PENDING

Scenario:Adviser add a sliding scale More Scenarios-4
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
Then I see active Percentage fee component and active sliding scale component and active dollar fee component PENDING

Scenario:Adviser add a sliding scale More Scenarios-5
Meta:
@categories fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
Then I see Sliding Scale component is inactive for ongoing fee
And I see Percentage fee component is inactive for ongoing fee
When I hover over the Sliding scale fee Component PENDING
Then I see text box displaying A sliding scale fee can be combined with a dollar fee PENDING


Scenario:Adviser add a sliding scale More Scenarios-5 Alternate Scenario
Meta:
@categories fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
Then I see active Percentage fee component and active sliding scale component and active dollar fee component PENDING
When I hover over the Sliding scale fee Component PENDING
Then I see text box displaying A sliding scale fee can be combined with a dollar fee PENDING

Scenario:Adviser add a sliding scale More Scenarios-6
Meta:
@categories fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
Then I see Sliding Scale fee component with the cluster section headers Include term deposit and Include managed portfolios and Include cash and dafault unchecked checkboxes for all
When I click on Next Button
Then I see error message Err.IP-0142