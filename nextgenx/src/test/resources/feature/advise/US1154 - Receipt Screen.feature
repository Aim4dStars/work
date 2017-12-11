Feature: US1154:Receipt Screen- Verify 'Receipt Screen' for advice fee
  In order to charge a client a once off advice fee
  As an adviser
  I want to have the capability to charge an account a one-off advice fee
  
  @Test @advicefee
  Scenario: Receipt page
    Given I am on fees screen
    When I enter fees amount "100.00"
    And I click on Next button
    Then I am on confirm page
    When I check client agreement box in the Confirm Screen
    And I click on Submit button
    Then I am on Receipt Page
  @Test @advicefee
  Scenario: Return to Account Overview
    Given I am on fees screen PENDING
    When I enter fees amount "100.00" PENDING
    And I click on Next button PENDING
    Then I am on confirm page PENDING
    When I check client agreement box in the Confirm Screen PENDING
    And I click on Submit button PENDING
    Then I am on Receipt Page PENDING
    And I click on Return to Accounts Overview PENDING
    And I am on the client's overview page PENDING