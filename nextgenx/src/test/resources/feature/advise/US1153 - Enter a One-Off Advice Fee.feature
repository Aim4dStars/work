Feature: US1153:Enter a One-Off Advice Fee- Verify 'Errors' for advice fee
  In order to charge a client a once off advice fee
  As an adviser
  I want to have the capability to charge an account a one-off advice fee
  @Smoketest @advicefee
  Scenario: fees amount "-"
    Given I am on fees screen
    When I enter fees amount "-"
    And I click on Next button
    Then Verify error "Please enter a positive value"
  @Functional @advicefee
  Scenario: Fee entered = NULL
    Given I am on fees screen
    When I enter fees amount ""
    And I click on Next button
    Then Verify error "Please enter an amount"
  @Regression @advicefee
  Scenario: Fee entered not Number
    Given I am on fees screen
    When I enter fees amount "al"
    And I click on Next button
    Then Verify error "Please enter an amount"
  @Functional @advicefee
  Scenario: Fee entered greater than Product Cap Fee
    Given I am on fees screen
    When I enter fees amount greater than Total Product Level Cap "20000"
    And I click on Next button
    Then Error Message "The one-off advice fee entered exceeds your annual cap. Please adjust accordingly" is displayed
  @test @advicefee
  Scenario: Fee entered greater than Available cash
    Given I am on fees screen
    When I enter fees amount "9500000"
    And I click on Next button
    Then Error message "The one-off advice fee entered exceeds the available cash amount. Please adjust accordingly.The one-off advice fee entered exceeds the maximum annual cap allowed for this fee. Please adjust accordingly." PENDING
  @test @advicefee
  Scenario: Total one off Advice Fee charged in past 12 months amount displayed
    Given I am on fees screen
    Then Verify message "Total one-off advice fees charged in past 12 months  "
  @test @advicefee
  Scenario: Available Cash
    Given I am on fees screen
    Then Verify "Available Cash 1235232.22" is displayed PENDING
  @test @advicefee
  Scenario: fees amount Null or Blank
    Given I am on fees screen
    When I enter fees amount ""
    And I click on Next button
    Then Verify error "Please enter an amount"
  @test @advicefee
  Scenario: Fee Amount greater than 2 decimal places
    Given I am on fees screen PENDING
    When I enter fees amount "2.3344"
    And I click on Next button
    Then Verify error "Please use only 2 numbers after the decimal point"
