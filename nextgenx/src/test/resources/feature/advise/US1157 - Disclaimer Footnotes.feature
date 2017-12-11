Feature: US1157:ONE OFF ADVICE FEE DISCLAIMERS/FOOTNOTES- Verify 'Disclaimer' text
  In order to charge a client a once off advice fee
  As an adviser
  I want to have the capability to charge an account a one-off advice fee
  @Test @advicefee
  Scenario: Disclaimer in page
    Given I am on fees screen PENDING
    When I enter fees amount "100.00" PENDING
    Then verify disclaimer "wording goes here" PENDING
    And I click on Next button PENDING
    Then I am on confirm page PENDING
    Then verify disclaimer "wording goes here" PENDING
    When I check client agreement box in the Confirm Screen PENDING
    And I click on Submit button PENDING
    Then I am on Receipt Page PENDING
    Then verify disclaimer "wording goes here" PENDING
  @Test @advicefee
  Scenario: Disclaimer in PDF
    Given I am on fees screen PENDING
    When I enter fees amount "100.00" PENDING
    And I click on Next button PENDING
    Then I am on confirm page PENDING
    And I download the PDF PENDING
    Then verify disclaimer "wording goes here" PENDING
