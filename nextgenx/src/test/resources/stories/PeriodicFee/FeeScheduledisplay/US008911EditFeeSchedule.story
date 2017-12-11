Narrative: US008911:Edit Fee Schedule- Verify 'Edit Fee Schedule'
As an adviser
I want to have the capability to edit Fee Schedule

Meta:
@userstory US008911EditFeeSchedule

Scenario: Verify Ongoing Advice fee for Edit fee Screen
Meta:
@categories miniregression fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
Then I see the fee components associated to the ongoing fee  as Dollarfee and Sliding Scale
And I see the Dollar fee amount as <amount>
And I see the Term deposits and Cash components checked
And I see value of Sliding tiers for Ongoing fee as:
|From|Icon|To|Pa|Icon1|
|0||100|4.00||
|100||5,000|3.00||
|5,000||and higher amounts|2.00||

Examples:
|amount|
|380.00|


Scenario: Verify Licensee Advice fee for Edit fee Screen
Meta:
@categories miniregression fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
Then I see the fee components associated to the Licensee fee as Dollarfee and Percentagefee
And I see the Dollar fee amount for licensee fee as <amount>
And I see the Index CPI box checked
And I see value of Percentage component for licensee fee as:
|Type|Pa|
|Managed portfolios|2.00|
|Term deposits|3.00|
|Cash|0.00|

Examples:
|amount|
|460.00|


Scenario:No access for Client
Meta:
@categories miniregression fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
Then I do not see the Edit Fee link Manual


Scenario:Verify Error for Max Percentage Value of Sliding Scale
Meta:
@categories fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
And I enter Percentage value for Sliding scale under Ongoing fee as "101"
And I click on FeeNext button
Then Verify error for Maximum value as "The value entered exceeds the maximum allowed for this fee. Please adjust accordingly."



Scenario:Verify Error for Max Percentage Value of Percentage fee
Meta:
@categories fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
And I enter percentage value for Percentage fee under Licensee fee as 101
And I click on FeeNext button
Then Verify error for Maximum value as "The value entered exceeds the maximum allowed for this fee. Please adjust accordingly."



Scenario:Verify Error for non numeric value for Sliding Scale
Meta:
@categories fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
And I enter Percentage value for Sliding scale under Ongoing fee as @#$
And I click on FeeNext button
Then Verify error for non numeric value as Enter an amount in the format '1.00'


Scenario:Verify Error for non numeric value Percentage fee
Meta:
@categories fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
And I enter percentage value for Percentage fee under Licensee fee as @#$
And I click on FeeNext button
Then Verify error for non numeric value as Enter an amount in the format '1.00'


Scenario:Verify Error for non numeric value for Dollar component
Meta:
@categories fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
And I enter value for dollar value of dollar component as @#$
And I click on FeeNext button
Then Verify error for non numeric value as Enter an amount in the format '1.00'


Scenario:Verify Error for blank value for Sliding Scale
Meta:
@categories fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
And I enter value for dollar amount for Sliding Scale as 
And I click on FeeNext button
Then Verify error for blank value as "Enter an amount"


Scenario:Verify Error for blank value for Percentage fee
Meta:
@categories fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
And I enter percentage value for Percentage fee under Licensee fee as 
And I click on FeeNext button
Then Verify error for blank value as "Enter an amount"


Scenario:Verify Error for blank value for Dollar component
Meta:
@categories fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
And I enter value for dollar value of dollar component as PENDING
And I click on FeeNext button
Then Verify error for blank value as "Enter an amount"


Scenario:Verify Error for negative value for Sliding Scale
Meta:
@categories fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
And I enter Percentage value for Sliding scale under Ongoing fee as -12
And I click on FeeNext button
Then Verify error for negative as Enter a number greater than 0


Scenario:Verify Error for negative value for Percentage fee
Meta:
@categories fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
And I enter percentage value for Percentage fee under Licensee fee as -12
And I click on FeeNext button
Then Verify error for negative as Enter a number greater than 0


Scenario:Verify Error for negative value for Dollar component
Meta:
@categories fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
And I enter value for dollar value of dollar component as -12
And I click on FeeNext button
Then Verify error for negative as Enter a number greater than 0


Scenario:Compare tier values for Sliding Scale
Meta:
@categories fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
And I click on Edit fees link
And I enter value for a tier lower than previous tier
Then Verify error for tier as Please enter a value that is greater than the previous tier.


Scenario:Verify Error for both non numeric and negative value for Sliding Scale
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees PENDING
And I enter value for percentage field as @#$ PENDING
And I enter value for dollar amount as -12 PENDING
Then Verify error for non numeric value as Enter an amount in the format '1.00' PENDING
And Verify error for negative as Enter a number greater than 0 PENDING


Scenario:Verify the count of tiers for Sliding Scale
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees PENDING
Then I see those tiers that made up the sliding scale PENDING
And I see maximum of 10 tiers PENDING


Scenario:Verify the 2 decimal check for Dollar fee
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees PENDING
And I enter value for dollar fee component PENDING as 12.235
Then Verify the value in dollar fee component PENDING as 12.23 


Scenario:Verify the 2 decimal check for percentage value of Sliding Scale
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees PENDING
And I enter value for percentage field as 10.236 PENDING
And I click on FeeNext button PENDING
Then I see the value for percentage value as 10.23 PENDING


Scenario:Verify the 2 decimal check for percentage value of Percentage fee
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees PENDING
And I enter value for percentage field of percentage fee as 10.236 PENDING
And I click on FeeNext button PENDING
Then I see the value for percentage value as 10.23 PENDING


Scenario:Verify the 2 decimal check for dollar component of Sliding Scale
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees PENDING
And I enter value for dollar amount as 100.25 PENDING
And I click on FeeNext button PENDING
Then I see value of dollar for Sliding as 100 PENDING
