Feature: Dashboard

  Background: A logged in adviser
    Given the adviser enters the site

  Scenario: Navigate from dashboard to draft applications
    Given I'm looking at the dashboard
    When I select draft applications
    Then should see the task page
    And with "Draft" tab selected
