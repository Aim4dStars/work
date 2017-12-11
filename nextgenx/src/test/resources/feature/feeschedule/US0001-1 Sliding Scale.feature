Feature: US1153:Adviser add a sliding scale- Verify 'Sliding Scale' screen  
  As an adviser
  I want to have the capability to add a Sliding Scale for Ongoing Fee and License Fee
  
  @Test @feeschedule
  Scenario: Fee details for Fee Schedule
  Given I am on Fee Schedule screen with fee
  When I click on Edit fees 
  Then I see the fee details for Ongoing advice fee and licensee advice fee

  @Test @feeschedule
  Scenario: Verify Sliding Scale Component
  Given I am on Fee Schedule screen with fee
  When I click on Edit fees
  Then I see sliding scale component for ongoing advice fee and Licence fee

  @Test @feeschedule
  Scenario: Verify Sliding Panel
  Given I am on Edit Fee Schedule Screen
  When I click on Sliding Scale button  
  Then I see a Sliding Panel

  @Test @feeschedule
  Scenario: Verify cluster section details to be checked
  Given I am on Edit Fee Schedule Screen
  When I click on sliding Scale component 
  Then I see cluster section with details checked

  @Test @feeschedule
  Scenario: Confirm Fee Page
  Given I am on Edit Fee Schedule Screen
  When I click on Sliding Scale button
  And I enter dollar amount "200"
  And I enter a per number "10"
  And I click on Next button
  Then I am on ConfirmFee Page
