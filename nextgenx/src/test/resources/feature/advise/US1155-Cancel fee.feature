Feature: US1155:Cancel One-Off Advice Fee- Verify 'Cancel' functionality
  In order to charge a client a once off advice fee
  As an adviser
  I want to have the capability to charge an account a one-off advice fee
  @Functional @advicefee
  Scenario: Fee Details Screen - Cancel - Yes button
    Given I am on fees screen PENDING
    When I enter fees amount "100.00" PENDING
    And I click on Cancel button PENDING
    And I click on Yes button on the model popup PENDING
    Then I am on client account's overview page PENDING
  @Functional @advicefee
  Scenario: Fee Details Screen - Cancel -No button
    Given I am on fees screen PENDING
    When I enter fees amount "100.00" PENDING
    And I click on Cancel button PENDING
    And I click on Yes button on the model popup PENDING
    Then I am on Fee Detail screen with pre populated details PENDING
  @Regression @advicefee
  Scenario: Confirm Screen - Cancel - Yes button
    Given I am on fees screen PENDING
    When I enter fees amount "100.00" PENDING
    And I click on Next button PENDING
    Then I am on confirm page PENDING
    When I click on Cancel button PENDING
    When I click on Yes button on the model popup PENDING
    Then I am on Fee Detail screen with pre populated details PENDING
  @Regression @advicefee
  Scenario: Confirm Screen - Cancel -No button
    Given I am on fees screen PENDING
    When I enter fees amount "100.00" PENDING
    And I click on Next button PENDING
    Then I am on confirm page PENDING
    When I click on Cancel button PENDING
    When I click on Cancel button on the model popup PENDING
    Then I am on Confirm screen with pre populated details PENDING
