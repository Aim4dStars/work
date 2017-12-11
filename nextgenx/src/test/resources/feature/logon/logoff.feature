Feature: Logoff

Background: A logged in adviser
    Given the adviser enters the site

  Scenario: Log off site as an adviser
    Given I'm looking at the dashboard
    When the user selects logout
    Then they should be logged out
