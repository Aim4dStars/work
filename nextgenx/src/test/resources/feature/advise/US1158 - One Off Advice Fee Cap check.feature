Feature: US1158:ONE OFF ADVICE FEE CAP CHECK- Verify 'Fees Amount' cap exceed error
  In order to charge a client a once off advice fee
  As an adviser
  I want to have the capability to charge an account a one-off advice fee
  @Test @advicefee
  Scenario: Available Cash check
    Given I am on fees screen PENDING
    When I enter fees amount "10000.00" PENDING
    And I click on Next button PENDING
    Then I am on confirm page PENDING
    When I check client agreement box in the Confirm Screen PENDING
    And I click on Submit button PENDING
    Then Verify Cap exceeded.The one -off advice fee enter exceed your annual cap. Please adjust accordingly PENDING
  @Test @advicefee
  Scenario: Available Cash check
    Given I am on fees screen PENDING
    When I enter fees amount "10000.00" PENDING
    And I click on Next button PENDING
    Then Verify Cap exceeded.The one -off advice fee enter exceed your annual cap. Please adjust accordingly PENDING
