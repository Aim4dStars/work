  Feature: US0088:Adviser View-Administration Fees for an account- Verify 'Admin Fees'  
  As a Adviser
  I want to view my client's administration current fee arrangements
 
  
  @Manual @feeschedule
  Scenario:Able to see Administration fee
  Given I am on Fee Schedule screen with fee Manual
  Then I see accounts current fee schedule for the Administration fee Manual
  
  @Functional @feeschedule
  Scenario:Verify Error Message
  Given I am on Fee Schedule screen with no fee
  Then For administration fee Verify error "No fee applied"
  
  @Manual @feeschedule
   Scenario:Verify the printer functionality
   Given I am on Fee Schedule screen with fee Manual
   When I click on the printer icon Manual
   Then I see file getting printed Manual
   
   @Manual @feeschedule
   Scenario:Verify the download functionality
   Given I am on Fee Schedule screen with fee Manual
   When I click on the download icon Manual
   Then I see file getting download Manual
   