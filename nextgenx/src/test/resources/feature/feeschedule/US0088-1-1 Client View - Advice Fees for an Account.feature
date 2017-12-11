 Feature: US1166:Client View-Advice fees for an account- Verify 'Fee Schedule' for client  
  As a Client
  I want to view my accounts current fee arrangements
  
  @Manual @feeschedule
  Scenario:Verify the Printer icon
  Given I am on Fee Schedule screen with Fee Manual
  When I click on the printer icon Manual
  Then I see icon getting clicked Manual
   
  @Manual @feeschedule
  Scenario:Verify the download icon
  Given I am on Fee Schedule screen with Fee Manual
  When I click on the download icon Manual
  Then I see icon getting clicked Manual
    
  @Manual @feeschedule
  Scenario:Verify no Edit Fee link for Client
  Given I am on Fee Schedule screen with Fee Manual
  Then I did not see Edit Fee link Manual
    
  @Manual @feeschedule
  Scenario:Verify the error message for screen with no fee
  Given I am on Fee Schedule screen with no fee Manual
  Then I see error message as "No fee applied" Manual
  
  @Manual @feeschedule
  Scenario:Verify the Fee Schedule link
  Given I am on Fee Schedule screen with Fee Manual
  When I click on Fee Schedule  link Manual
  Then I am on Fee Schedule screen with Fee Manual
  
  @Manual @feeschedule
  Scenario:Verify the Tax invoice link
  Given I am on Fee Schedule screen with Fee Manual
  When I click on Tax Invoices Manual
  Then I am on Tax Invoice Screen Manual
  
  @Manual @feeschedule
  Scenario:Verify the fee Schedule for amount $0.00
  Given I am on Fee Schedule screen with Fee Manual 
  And I see the account with $0.00 flat dollar advice fee Manual
  Then I see the fee schedule for the advice fee the $0.00 amount Manual