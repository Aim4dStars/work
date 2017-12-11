Feature: Logon

  Scenario: Log onto site as an investor
    Given a user enters site
    When the "investor" enters credentials
    And clicks sign in
    Then they should see the investor dashboard

  Scenario: Log onto site as an adviser
    Given a user enters site
    When the "adviser" enters credentials
    And clicks sign in
    Then they should see the adviser dashboard