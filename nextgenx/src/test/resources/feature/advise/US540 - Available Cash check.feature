Feature: US540:AVAILABLE CASH CHECK- Verify 'Fees Amount' error scenario
  In order to charge a client a once off advice fee
  As an adviser
  I want to have the capability to charge an account a one-off advice fee
  
  @Regression @advicefee
  Scenario: Fee entered greater than Available cash
	Given I am on fees screen
    When I enter fees amount "9500000"
    And I click on Next button 
    Then Error message PENDING "The one-off advice fee entered exceeds the available cash amount. Please adjust accordingly.The one-off advice fee entered exceeds the maximum annual cap allowed for this fee. Please adjust accordingly."
    
    