Feature: US1152:Confirm- Verify 'Confirm Screen' for advice fee
  In order to charge a client a once off advice fee
  As an adviser
  I want to have the capability to charge an account a one-off advice fee


  @test @advicefee
  Scenario: Receipt page
    Given I am on fees screen
    When I enter fees amount "100.00"
    And I click on Next button
    Then I am on confirm page
    When I check client agreement box in the Confirm Screen
    And I click on Submit button
    Then I am on Receipt Page


  @test @advicefee
  Scenario: Confirm Screen Validation
    Given I am on fees screen
    When I enter fees amount "100.00"
    And I click on Next button
    Then I am on confirm page
    And I click on Submit button
    And Client agreement text will appear


  @test @advicefee
  Scenario: Available cash check validation insufficient cash
    Given I am on fees screen PENDING
    When I enter fees amount "100.00" PENDING
    And I click on Next button PENDING
    Then I am on confirm page PENDING
    And I click on Submit button PENDING


  @test @advicefee
  Scenario: Available cash check validation insufficient cash
    Given I am on fees screen PENDING
    When I enter fees amount "1120000.00" PENDING
    And I click on Next button PENDING
    Then I am on confirm page PENDING
    And I click on Submit button PENDING


  @test @advicefee
  Scenario: PDF document
    Given I am on fees screen PENDING
    When I enter fees amount "1000.00" PENDING
    And I click on Next button PENDING
    Then I am on confirm page PENDING
    And I click on download PENDING
