Narrative:US1157:ONE OFF ADVICE FEE DISCLAIMERSFOOTNOTES- Verify
In order to charge a client a once off advice fee
As an adviser
I want to have the capability to charge an account a one-off advice fee

Meta:
@userstory US09817OneOffAdviceFeeDisclaimersFootnotes

Scenario: Disclaimer in page - Not Developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I navigate to fees screen
When I enter fees amount "100.00"
Then verify disclaimer "wording goes here"
When I click on Next button
Then I am on confirm page
Then verify disclaimer "wording goes here"
When I check client agreement box in the Confirm Screen
And I click on Submit button
Then I am on Receipt Page with header name as Receipt
Then verify disclaimer "wording goes here"

Scenario: Disclaimer in PDF - Not Developed
Meta:
@categories fullregression
Given I am on Login Page
When I navigate to fees screen
When I enter fees amount $100.00
And I click on Next button
Then I am on confirm_page with header name as Confirm
And I download the PDF
Then verify disclaimer "wording goes here"

