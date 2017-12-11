Feature: US0089:Edit Fee Schedule- Verify 'Edit Fee Schedule'  
  As an adviser
  I want to have the capability to edit Fee Schedule
  @Functional @feeschedule
  Scenario: Editable fee template screen
  Given I am on Fee Schedule screen with fee PENDING
  When I click on Edit fees PENDING
  Then I see editable fee template screen PENDING
  
  @Manual @feeschedule
  Scenario:No access for Adviser
  Given I am on Fee Schedule screen  with no access Manual
  Then I do not see the Edit Fee link Manual
	
  @Manual @feeschedule
  Scenario:Verify Error for Max Percentage Value
  Given I am on Fee Schedule screen with fee Manual
  When I click on Edit fees Manual
  And I enter percentage value as "101" Manual
  And I click on FeeNext button Manual
  Then Verify error for Maximum value as "The value entered exceeds the maximum allowed for this fee. Please adjust accordingly." Manual	
 
  @Functional @feeschedule
  Scenario:Verify Error for Max Percentage Value of Percentage Component
  Given I am on Fee Schedule screen with fee PENDING
  When I click on Edit fees PENDING
  And I enter value for percentage component as "101" PENDING
  And I click on FeeNext button PENDING
  Then Verify error for Maximum value as "The value entered exceeds the maximum allowed for this fee. Please adjust accordingly." PENDING
 	
  @Functional @feeschedule
  Scenario:Verify Error for non numeric value for Sliding Scale
  Given I am on Fee Schedule screen with fee PENDING
  When I click on Edit fees PENDING
  And I enter value for percentage component as "@#$" PENDING
  And I click on FeeNext button PENDING
  Then Verify error for non numeric value as "Enter an amount in the format '1.00'" PENDING
  
  @Functional @feeschedule
  Scenario:Verify Error for blank value for Sliding Scale
  Given I am on Fee Schedule screen with fee PENDING
  When I click on Edit fees PENDING
  And I enter value for dollar component as " " PENDING
  And I click on FeeNext button PENDING
  Then Verify error for blank value as "Enter an amount" PENDING
  
  
  @Functional @feeschedule
  Scenario:Verify Error for negative value for Sliding Scale
  Given I am on Fee Schedule screen with fee PENDING
  When I click on Edit fees PENDING
  And I enter value for percentage component as "-12" PENDING
  And I click on FeeNext button PENDING
  Then Verify error for negative as "Enter a number greater than 0" PENDING
  
  @Manual @feeschedule
  Scenario:Compare tier values for Sliding Scale
  Given I am on Fee Schedule screen with fee Manual
  When I click on Edit fees Manual
  And I enter value for a tier lower than previous tier Manual
  Then Verify error for tier as  "Please enter a value that is greater than the previous tier." Manual
  
  @Functional @feeschedule
  Scenario:Verify Error for both non numeric and negative value for Sliding Scale
  Given I am on Fee Schedule screen with fee PENDING
  When I click on Edit fees PENDING
  And I enter value for percentage component as "@#$" PENDING
  And I enter value for dollar component as "-12" PENDING
  Then Verify error for non numeric value as "Enter an amount in the format '1.00'" PENDING
  Then Verify error for negative as "Enter a number greater than 0" PENDING
  
  @Manual @feeschedule
  Scenario:Verify the count of tiers for Sliding Scale
  Given I am on Fee Schedule screen with fee PENDING
  When I click on Edit fees PENDING
  Then I see those tiers that made up the sliding scale PENDING
  
  @Functional @feeschedule
  Scenario:Verify the 2 decimal check for Dollar fee
  Given I am on Fee Schedule screen with fee PENDING
  When I click on Edit fees PENDING
  And I enter value for dollar fee component as "12.235" PENDING
  Then Verify the value in dollar fee component as "12.23" PENDING
  
  @Functional @feeschedule
  Scenario:Verify the 2 decimal check for percentage value of Sliding Scale 
  Given I am on Fee Schedule screen with fee PENDING
  When I click on Edit fees PENDING
  And I enter value for percentage component as "10.236" PENDING
  And I click on FeeNext button PENDING
  Then I see the value for percentage value as "10.23" PENDING
  
   @Functional @feeschedule
   Scenario:Verify the 2 decimal check for dollar component of Sliding Scale
   Given I am on Fee Schedule screen with fee PENDING
   When I click on Edit fees PENDING
   And I enter value for dollar component as "100.25" PENDING
   And I click on FeeNext button PENDING
   Then I see value of dollar for Sliding as "100" PENDING
  
  