  Feature: US0088:Adviser View - View Investment Management Fees- Verify 'Investment Management Fees'  
  As a Adviser
  I want to view an account’s Investment Management fee
 
  @Manual @feeschedule
  Scenario:View the Investment Management fee 
  Given I am on Fee Schedule screen with fee Manual
  Then I see current fee schedule for each of the SMA Manual
  
  @Manual @feeschedule
  Scenario:Verify printer functionality
  Given I am on Fee Schedule screen with fee Manual
  When I click on the printer icon for printing Investment Management fee Manual
  Then I see Management fee file getting printed Manual
  
  @Manual @feeschedule
  Scenario:Verify download functionality
  Given I am on Fee Schedule screen with fee Manual
  When I click on the download icon for downloading Investment Management fee Manual
  Then I see Management fee file getting download Manual
  
  @Manual @feeschedule
  Scenario:When user has no current account holdings
  Given I am on Fee Schedule screen with fee Manual
  When I do not have any holdings Manual
  Then I do not see Investment management fees section Manual