Feature: US1156:One Off Advice Fee Progress Bar- Verify 'No Progress Bar' state as per CR)
  In order to charge a client a one off advice fee
  As an adviser
  I want to have the capability to charge an account a one-off advice fee
@Test @advicefee
  Scenario: To be Completed Progress bar state on Confirm page
    Given I am on Fee screen PENDING
    When I click on Progress bar on Confirm PENDING
    Then progress bar cannot move forward PENDING
  