Feature: Adviser navigation

  Background: A logged in adviser
    Given the adviser enters the site

  Scenario: Navigate to clients
    Given I'm looking at the dashboard
    When select clients on left nav
    Then should see the clients page
