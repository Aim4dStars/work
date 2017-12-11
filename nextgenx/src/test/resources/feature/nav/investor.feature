Feature: Investor navigation

  Background: A logged in investor
    Given the investor enters the site

Scenario: Navigate to transactions history
    Given I'm looking at the dashboard
    When select accounts on left nav
    Then should see the overview page
