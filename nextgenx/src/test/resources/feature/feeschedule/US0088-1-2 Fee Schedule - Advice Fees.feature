 Feature: US1166:Adviser View - Advice Fees for an account- Verify 'Fee Schedule' for advisor  
  As a Adviser
  I want to view my client's account current fee arrangements

  @Functional @feeschedule
  Scenario:Fee Schedule Edit Screen
  Given I am on Fee Schedule screen with no fee
  When I click on Edit fees link
  Then I see fees Schedule edit screen
  
 @Functional @feeschedule
  Scenario: Verify Error Message
  Given I am on Fee Schedule screen with no fee
  Then Verify error for Fee "No fee applied"
  
  @Functional @feeschedule
  Scenario:Fee Schedule Screen
  Given I am on Fee Schedule screen with no fee
  When I click on Fee Schedule link
  Then I am on Fee Schedule screen page
  
  @Functional @feeschedule
  Scenario:One-off advice fee page
  Given I am on Fee Schedule screen with no fee
  When I click on Charge one-off advice link
  Then I am on one-off advice fee page
  
  @Manual @feeschedule
  Scenario:Verify the fee Schedule for amount $0.00
  Given I am on Fee Schedule screen with fee Manual
  When I see the account with $0.00 flat dollar advice fee Manual
  Then I see the fee schedule for the advice fee the $0.00 amount Manual
 