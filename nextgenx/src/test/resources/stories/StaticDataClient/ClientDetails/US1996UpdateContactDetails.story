Narrative: US1994:Update contact details - Verify 'contact details'
In order to maintain a client’s personal details 
As an adviser or adviser assistant
I want to change a client’s contact details

Meta:
@userstory US1996UpdateContactDetails

Scenario:Initial State Contact Details Client Ind
Meta:
@categories fullregression
Given I am on Client details and preferences
When I click on edit icon for Contact Details
Then I am on Edit Contact Details screen
And I see uneditable primary email address and primary mobile number field and editable secondary email address field
Then I see a dropdown containing a list of four to add more field types


Scenario: Update Client Ind Contact Details Secondary E-mail Address
Meta:
@categories fullregression
Given I am on Client details and preferences
When I click on edit icon for Contact Details
And I enter a valid secondary email address as abc92@xyz.com
And I check the approval checkbox
And I click on Update button
Then I see updated value for secondary email address as abc92@xyz.com


Scenario: Update Client Ind Contact Details Secondary E-mail Address Alternate Scenario
Meta:
@categories miniregression fullregression
Given I am on Client details and preferences
When I click on edit icon for Contact Details
And I enter a invalid secondary email address as abc92@xy#z.abcdefghij
And I check the approval checkbox
And I click on Update button
Then I see Error message Please enter a valid email address


Scenario: Update Client Ind Contact Details Home Phone Number Scenario 1
Meta:
@categories miniregression fullregression
Given I am on Client details and preferences
When I click on edit icon for Contact Details
And I select Home number in the add field dropdown as Home
And I enter Valid 8 digit Home number in the input field 02345678
And I check the approval checkbox
And I click on Update button
Then I see updated value for Home number as 02345678


Scenario: Update Client Ind Contact Details Home Phone Number Scenario 2
Meta:
@categories miniregression fullregression
Given I am on Client details and preferences
When I click on edit icon for Contact Details
And I select Home number in the add field dropdown as Home
And I enter number which is 10 characters AND first two characters are valid area codes 02,03,07,08 as 0212345678
And I check the approval checkbox
And I click on Update button
Then I see updated value for Home number as 0212345678


Scenario: Update Client Ind Contact Details Home Phone Number Error Scenario 1
Meta:
@categories fullregression
Given I am on Client details and preferences
When I click on edit icon for Contact Details
And I select Home number in the add field dropdown as Home
And I enter invalid number with less than 8 characters after removing spaces as 1234%67
Then I see the error message Please enter a valid telephone number (including area code) PENDING


Scenario: Update Client Ind Contact Details Home Phone Number Error Scenario 2
Meta:
@categories fullregression
Given I am on Client details and preferences
When I click on edit icon for Contact Details
And I select Home number in the add field dropdown as Home
And I enter Invalid number which is 8 characters AND doesn’t start with 0 12345678
Then I see the error message Number must include an area code PENDING


Scenario: Update Client Ind Contact Details Home Phone Number Error Scenario 3
Meta:
@categories miniregression fullregression
Given I am on Client details and preferences
When I click on edit icon for Contact Details
And I select Home number in the add field dropdown as Home
And I enter number which is 10 characters AND first two characters are not valid area codes as 0512345678
Then I see the error message Number must include an area code PENDING


Scenario: Update Client Ind Contact Details Work Phone Number Scenario 1
Meta:
@categories miniregression fullregression
Given I am on Client details and preferences
When I click on edit icon for Contact Details
And I select Home number in the add field dropdown as Home
And I select Home number in the add field dropdown as Work
And I enter Valid 8 digit Home number in the input field 02345678
And I enter Valid 8 digit Work number in the input field as 02345678
And I check the approval checkbox
And I click on Update button
Then I see updated value for Work number as 02345678


Scenario: Update Client Ind Contact Details Work Phone Number Scenario 2
Meta:
@categories miniregression fullregression
Given I am on Client details and preferences
When I click on edit icon for Contact Details
And I select Home number in the add field dropdown as Home
And I select Home number in the add field dropdown as Work
And I enter Valid 8 digit Home number in the input field 02345678
And I enter work number which is 10 characters AND first two characters are valid area codes as 0212345678
And I check the approval checkbox
And I click on Update button
Then I see updated value for Work number as 0212345678


Scenario: Update Client Ind Contact Details Work Phone Number Error Scenario 1
Meta:
@categories fullregression
Given I am on Client details and preferences
When I click on edit icon for Contact Details
And I select Home number in the add field dropdown as Home
And I select Home number in the add field dropdown as Work
And I enter invalid work number with less than 8 characters after removing spaces as 1234%4
Then I see the error message Please enter a valid telephone number (including area code) PENDING


Scenario: Update Client Ind Contact Details Work Phone Number Error Scenario 2
Meta:
@categories miniregression fullregression
Given I am on Client details and preferences
When I click on edit icon for Contact Details
And I select Home number in the add field dropdown as Home
And I select Home number in the add field dropdown as Work
And I enter Invalid number which is 8 characters AND doesn’t start with 0 12345678 
Then I see the error message Number is 8 characters AND doesn’t start with 0 PENDING


Scenario: Update Client Ind Contact Details Work Phone Number Error Scenario 3
Meta:
@categories fullregression
Given I am on Client details and preferences
When I click on edit icon for Contact Details
And I select Home number in the add field dropdown as Home
And I select Home number in the add field dropdown as Work
And I enter work number which is 10 characters AND first two characters are not valid area codes as 0212345678
Then I see the error message Number must include an area code PENDING


Scenario: Update Client Ind Contact Details Work Phone Number Alternate Scenario
Meta:
@categories miniregression fullregression
Given I am on Client details and preferences
When I click on edit icon for Contact Details
And I select Home number in the add field dropdown as Home
And I select Home number in the add field dropdown as Work
And I enter number which is 10 characters AND first two characters are valid area codes 02,03,07,08 as 0412345678
And I enter work number which is 10 characters AND first two characters are valid area codes as 0512345678
And I check the approval checkbox
And I click on Update button
Then I see the entered Work number reflected on the screen as Secondary Mobile number PENDING


Scenario: Update Client Ind Contact Details Home Phone Number Alternate Scenario Error
Meta:
@categories miniregression fullregression
Given I am on Client details and preferences
When I click on edit icon for Contact Details
And I select Home number in the add field dropdown as Home
And I enter number which is 10 characters AND first two characters are valid area codes 02,03,07,08 as 055045678
Then I see Error message Please enter a valid Australian mobile number PENDING


Scenario: Update Client Ind Contact Details Work Phone Number Alternate Scenario
Meta:
@categories miniregression fullregression
Given I am on Client details and preferences
When I click on edit icon for Contact Details
And I select Home number in the add field dropdown as Home
And I select Home number in the add field dropdown as Work
And I enter number which is 10 characters AND first two characters are valid area codes 02,03,07,08 as 0550345678
And I enter work number which is 10 characters AND first two characters are valid area codes as 0550345678
And I check the approval checkbox
And I click on Update button
Then I see the entered Home number reflected on the screen as Secondary Mobile number PENDING


Scenario: Update Client Ind Contact Details Select Additional Contact Detail
Meta:
@categories miniregression fullregression
Given I am on Client details and preferences
When I click on edit icon for Contact Details
And I select Home number in the add field dropdown as Home
And I select Home number in the add field dropdown as Work
And I select Home number in the add field dropdown as Mobile
Then I see a no add field option being available on the screen



Scenario: Update Client Ind Contact Details Preferred Contact Method
Meta:
@categories miniregression fullregression
Given I am on Client details and preferences
When I click on edit icon for Contact Details
When I select a Radio Button alongside a contact method
Then I see the client's preferred contact reflected on the screen


Scenario: Approval Checkbox Client Ind contact details
Meta:
@categories miniregression fullregression
Given I am on Client details and preferences
When I click on edit icon for Contact Details
Then I see disabled updated button
When I check the approval checkbox
Then I see enabled updated button


Scenario: Cancel Button for Client Ind contact details
Meta:
@categories miniregresion fullregression
Given I am on Client details and preferences
When I click on edit icon for Contact Details
And I enter a valid secondary email address as abc92@xyz.com
And I check the approval checkbox
And I click on cancel button
Then I see no changes in value for Contact Details


Scenario: Close Button for Client Ind contact details
Meta:
@categories miniregression fullregression
Given I am on Client details and preferences
When I click on edit icon for Contact Details
And I enter a valid secondary email address as abc92@xyz.com
And I check the approval checkbox
And I click on close button
Then I see no changes in value for Contact Details


Scenario: Avaloq Integration Client
Meta:
@categories fullregression
Given I am on Client details and preferences Avaloq
When I click on edit icon for Contact Details Avaloq
Then I am on Edit contact details screen Avaloq