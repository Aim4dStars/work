Narrative: Account and billers


!--#####################################Add linked accounts link on accounts and billers page######################################

Scenario: Verify 'Add linked account' functionality when more than 5 linked accounts are already present
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I click on tab 'Accounts and billers' in the Move money section
And I see 'Add linked account' link available to user having Update payees & payment limits' permission.
When I click on the 'Add linked account' link when already 5 linked accounts are added
Then I see error message 'You can link up to 5 accounts, if you want to add a new account please delete any other account and then try again.' displayed on top of screen


Scenario: Verify 'Account name' field validations and heading static text in 'Add linked account' modal
Meta:
@categories  AccAndBiller Automation1

Given I login into panorama system as Investor
!-- And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I go to the Move money
And I navigate to 'Accounts and billers' tab
Then I see Add linked account link will be available to user having Update payees & payment limits permission
When I click on link 'Add linked account'
Then I see a modal window come up
And I see static heading text 'Add payment and deposit account'
And I see that 'Account name' text box field has maximum limit of 32 letters or numbers along with following special characters(& - < > , + space ( ) /)
When I leave the 'Account name' field blank and tab out
Then I see error message Please enter a valid account name using letters or numbers or special characters & - < > , + space ( )
When I enter valid Account name in the field and tab out
Then I see no error messages


Scenario: Verify 'BSB' field validations in 'Add linked account' modal
Meta:
@categories AccAndBiller Automation1

Given I login into panorama system as Investor
!-- And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I go to the Move money
And I navigate to 'Accounts and billers' tab
Then I see Add linked account link will be available to user having Update payees & payment limits permission
When I click on link 'Add linked account'
Then I see a modal window come up
Then I see 'BSB' field has maximum limit of 7 characters
When I leave BSB field blank and tab out
Then I see error message Please enter a 6-digit BSB number
When I enter valid BSB number in the field and tab out
Then I see no error messages on BSB field


Scenario: Verify 'Account number' field validations in 'Add linked account' modal
Meta:
@categories AccAndBiller Automation1

Given I login into panorama system as Investor
!-- And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I go to the Move money
And I navigate to 'Accounts and billers' tab
Then I see Add linked account link will be available to user having Update payees & payment limits permission
When I click on link 'Add linked account'
Then I see a modal window come up
Then I see that account number field has minimum limit of 6 digits and maximum 10 digits
When I leave Account number field blank and tab out
Then I see error message Enter an account number
When I enter valid Account number in the field and tab out
Then I see no error messages on account number field


Scenario: Verify 'Account nickname' field validations in 'Add linked account' modal
Meta:
@categories AccAndBiller Automation1

Given I login into panorama system as Investor
!-- And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I go to the Move money
And I navigate to 'Accounts and billers' tab
Then I see Add linked account link will be available to user having Update payees & payment limits permission
When I click on link 'Add linked account'
Then I see a modal window come up
Then I see 'Account nickname' field with 30 characters limit only
When I enter any non allowed characters other than accepted: letters, numbers, hyphens, or spaces and tab out
Then I see error message Please only use 'letters, numbers, hyphens or spaces'
When I enter valid characters in the field and tab out
Then I see no error message on Account nickname field


Scenario: Verify 'Terms and conditions' field validations in 'Add linked account' modal
Meta:
@categories AccAndBiller Automation1

Given I login into panorama system as Investor
!-- And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I go to the Move money
And I navigate to 'Accounts and billers' tab
Then I see Add linked account link will be available to user having Update payees & payment limits permission
When I click on link 'Add linked account'
Then I see a modal window come up
Then I see terms and conditions check box unchecked with text 'You agree to the Terms and Conditions of adding this account. You can only add payments and deposits account if you are the owner of, or authorised signatory for, the account'
When I click on the 'terms and conditions' hyperlink
Then I see a PDF is displayed in a new tab in the browser displaying the correct terms and conditions


Scenario: Verify enable and disable state of Get SMS Code button
Meta:
@categories AccAndBiller Automation1

Given I login into panorama system as Investor
!-- And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I go to the Move money
And I navigate to 'Accounts and billers' tab
Then I see Add linked account link will be available to user having Update payees & payment limits permission
When I click on link 'Add linked account'
Then I see a modal window come up
Then I see static text 'SMS code for your security'
And I see 'Get SMS code' button in disabled state
When I check the 'Terms and conditions' checkbox
Then I see 'Get SMS code' button has changed state to enabled


Scenario: Verify 'Get SMS Code' button functionality with valid and invalid data and when analyse call made to SAFI on opening modal does not results in challenge
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I navigate to 'Accounts and billers' tab
And I click on link 'Add linked account'
And I enter invalid values in each of the 'Account name, 'BSB', 'Account number' and 'Account nickname' fields
And I click on 'Get SMS Code' button 
Then I see error message below each of the 'Account name, 'BSB', 'Account number' and 'Account nickname' fields
When I click on 'Get SMS Code' with duplicate combination of BSB and account number already present
Then I see error message 'Account details already exists' 
When I click on 'Get SMS Code' button with valid 'Account name, 'BSB', 'Account number' and 'Account nickname' field
Then I see get SMS code button get replaced with SMS code field with max length of it is 6 digit 
And I see static text 'Code sent to <XX## ### XXX>. If the code is not received in a few minutes try again' with 'try again' text being a link.
And I see that except for first two digit and last three digit of mobile number in static text 'Code sent to <XX## ### XXX>' are masked with rest of numbers replaced by #

Scenario: Verify 'Get SMS Code' button functionality with valid and invalid data and analyse call made to SAFI on opening modal results in challenge
Meta:
@categories AccAndBiller Manual
Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I navigate to 'Accounts and billers' tab
And I click on link 'Add linked account'
Then I do not see get SMS code button and SMS code for your security text
And I enter invalid values in each of the 'Account name, 'BSB', 'Account number' and 'Account nickname' fields
When I click on the 'Add' button
Then I see error message below each of the 'Account name, 'BSB', 'Account number' and 'Account nickname' fields
When I click on 'Add' button with valid 'Account name, 'BSB', 'Account number' and 'Account nickname' field
Then I see the modal is navigated to 'Account and biller' page
And I see message '<Account> has been added to account and biller



Scenario: Verify 'Add' button functionality when 2FA is required from SAFI
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab And I know that 2FA authentication is required from SAFI
When I click on tab 'Accounts and billers' in the Move money section
And I see Add linked account link will be available to user having Update payees & payment limits permission.
When I click on link 'Add linked account' 
And I see a a modal window comes up
And I see 'Add' button in disabled state till the 6 digit SMS code is not filled
When I click on 'Get SMS Code' button after filling valid values in 'Account name, 'BSB', 'Account number' and 'Account nickname' fields
And I enter wrong SMS code in 'SMS code' field
Then I see 'Add' button to change itself to enable state
When I click on the 'Add' button
Then I see error message 'SMS code entered is incorrect. Please try again'  below SMS code text box
When I enter valid SMS code in 'SMS code' field
Then I see 'Add' button to change itself to enable state
When I click on the 'Add' button
Then I see the modal is navigated to 'Account and biller' page
And I see message '<Account> has been added to account and biller
And I see the details of the newly added linked account is displayed in the list


Scenario: Verify 'Add' button functionality when 2FA authentication is not required
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab And I know that 2FA authentication is not required from SAFI
When I click on tab 'Accounts and billers' in the Move money section
And I see Add linked account link will be available to user having Update payees & payment limits permission.
When I click on link 'Add linked account' 
And I see a a modal window comes up
And I see 'Add' button in enabled state
And I do not see static text 'SMS code for your security' and 'Get SMS code' button in the modal
When I click on Add' button after filling invalid values in 'Account name, 'BSB', 'Account number' and 'Account nickname' fields
Then I see that all the field level validations are executed and I see error message below each of the 'Account name, 'BSB', 'Account number' and 'Account nickname' fields
When I click on Add' button after filling valid values in 'Account name, 'BSB', 'Account number' and 'Account nickname' fields
Then I see the modal is navigated to 'Account and biller' page
And I see message '<Account> has been added to account and biller
And I see the details of the newly added linked account is displayed in the list


Scenario: Verify 'Try again' link functionality
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I click on tab 'Accounts and billers' in the Move money section
And I see Add linked account link will be available to user having Update payees & payment limits permission.
When I click on link 'Add linked account' 
And I see a a modal window comes up
When I click on 'Get SMS Code' button after filling valid values in 'Account name, 'BSB', 'Account number' and 'Account nickname' fields
Then I see text 'Code sent to <XX## ### XXX>. If the code is not received in a few minutes, <try again> with try again being a link.
When I click on 'try again' link
And I enter the new valid SMS code in 'SMS code' field
And I click on the 'Add' button
Then I see the modal is navigated to 'account and biller' page
And I see message '<Account> has been added to account and biller


Scenario: Verify 'Cancel or 'X' button functionality in Add linked account modal for linked section
Meta:
@categories  AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I click on tab 'Accounts and billers' in the Move money section
And I click on Add linked account against the 'Linked accounts' section 
Then I see a a modal window come up
When I click on the 'Cancel' button
Then I see the modal is navigated to 'accounts and billers' page without adding any account
When I click on Add linked account against the 'Linked accounts' section 
Then I see a a modal window come up
When I click on the 'X' button
Then I see the modal is navigated to 'accounts and billers' page without adding any account


!-- #####################################Add pay anyone account on accounts and billers page######################################

Scenario: Verify 'Account name' field validations and heading static text in 'Add account' modal
Meta:
@categories  AccAndBiller Automation

Given I login into panorama system as Investor
!-- And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I go to the Move money
And I navigate to 'Accounts and billers' tab
Then I see Add linked account link will be available to user having Update payees & payment limits permission
When I click on link 'Add linked account'
Then I see a modal window come up
Then I see static heading text 'Add payments account'
And I see that 'Account name' text box field has maximum limit of 32 letters or numbers along with following special characters(& - < > , + space ( ) /)
When I leave the 'Account name' field blank or enter any other characters except allowed ones and tab out
Then I see error message Please enter a valid account name using letters or numbers or special characters & - < > , + space ( ) �
When I enter valid Account name in the field and tab out
Then I see no error messages 


Scenario: Verify 'BSB' field validations in 'Add account' modal
Meta:
@categories AccAndBiller Automation

Given I login into panorama system as Investor
!-- And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I go to the Move money
And I navigate to 'Accounts and billers' tab
Then I see Add linked account link will be available to user having Update payees & payment limits permission
When I click on link 'Add linked account'
Then I see a a modal window come up
Then I see that 'BSB' field has maximum limit of 7 characters with condition that the user can input BSB number with hyphen(123-123) or only number (123123)
When I enter the incorrect BSB number in the field or leave it blank and tab out
Then I see error message 'Please enter a 6-digit BSB number' 
When I enter valid BSB number in the field and tab out
Then I see no error messages 


Scenario: Verify 'Account number' field validations in 'Add account' modal
Meta:
@categories AccAndBiller Automation

Given I login into panorama system as Investor
!-- And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I go to the Move money
And I navigate to 'Accounts and billers' tab
Then I see Add linked account link will be available to user having Update payees & payment limits permission
When I click on link 'Add linked account'
Then I see a a modal window come up
And I see that account number field has minimum limit of 6 digits and maximum limit 10 digits only
And I enter the invalid Account number in the field or leave it blank and tab out
Then I see error message 'Enter an account number' 
When I enter valid Account number in the field and tab out
Then I see no error messages 


Scenario: Verify 'Account nickname' field validations in 'Add account' modal
Meta:
@categories AccAndBiller Automation

Given I login into panorama system as Investor
!-- And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I go to the Move money
And I navigate to 'Accounts and billers' tab
Then I see Add linked account link will be available to user having Update payees & payment limits permission
When I click on link 'Add linked account'
Then I see a a modal window come up
And I see that the max limit of 'Account nickname' field is 30 characters only
When I enter any non allowed characters other than accepted: letters, numbers, hyphens, or spaces and tab out
Then I see error message Please only use 'letters, numbers, hyphens or spaces' 
When I enter valid characters in the field and tab out
Then I see no error messages 


Scenario: Verify 'SMS code for your security' functionality in 'Add account' modal and analyse call made to SAFI on opening modal does not results in challenge
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I navigate to 'Accounts and billers' tab
And I see 'Add account' link will be available to user having Update payees & payment limits permission.
And I click on link 'Add account' 
Then I see a modal window comes up
And I see 'Get SMS code' button in enabled state
When I enter invalid values in each of the 'Account name, 'BSB', 'Account number' and 'Account nickname' fields
And I click on 'Get SMS Code' button 
Then I see error message below each of the 'Account name, 'BSB', 'Account number' and 'Account nickname' fields
When I click on 'Get SMS Code' with duplicate combination of BSB and account number already present
Then I see error message 'Account details already exists' 
When I click on 'Get SMS Code' button with valid 'Account name, 'BSB', 'Account number' and 'Account nickname' field
Then I see get SMS code button get replaced with SMS code field with max length of it is 6 digit 
And I see static text 'Code sent to <XX## ### XXX>. If the code is not received in a few minutes try again' with 'try again' text being a link.
And I see that except for first two digit and last three digit of mobile number in static text 'Code sent to <XX## ### XXX>' are masked with rest of numbers replaced by #


Scenario: Verify 'Get SMS Code' button functionality with valid and invalid data and analyse call made to SAFI on opening modal results in challenge
Meta:
@categories AccAndBiller Manual
Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I navigate to 'Accounts and billers' tab
And I click on link 'Add account'
Then I do not see get SMS code button and SMS code for your security text
And I enter invalid values in each of the 'Account name, 'BSB', 'Account number' and 'Account nickname' fields
When I click on the 'Add' button
Then I see error message below each of the 'Account name, 'BSB', 'Account number' and 'Account nickname' fields
When I click on 'Add' button with valid 'Account name, 'BSB', 'Account number' and 'Account nickname' field
Then I see the modal is navigated to 'Account and biller' page
And I see message '<Account> has been added to account and biller



Scenario: Verify 'Add' button functionality for add account modal when 2FA is required from SAFI
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
And I know that 2FA authentication is required from SAFI
When I click on tab 'Accounts and billers' in the Move money section
And I see 'Add account' link will be available to user having Update payees & payment limits permission.
When I click on link 'Add account' 
And I see a a modal window comes up
And I see 'Add' button in disabled state till the 6 digit SMS code is not filled
When I click on 'Get SMS Code' button after filling valid values in 'Account name, 'BSB', 'Account number' and 'Account nickname' fields
And I enter wrong SMS code in 'SMS code' field
Then I see 'Add' button to change itself to enable state
When I click on the 'Add' button
Then I see error message 'SMS code entered is incorrect. Please try again' 
When I enter valid SMS code in 'SMS code' field
Then I see 'Add' button to change itself to enable state
When I click on the 'Add' button
Then I see the modal is navigated to 'Account and biller' page
And I see message '<Account> has been added to account and biller
And I see the details of the newly added account is displayed in the list


Scenario: Verify 'Add' button functionality when 2FA authentication is not required
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
And I know that 2FA authentication is not required from SAFI
When I click on tab 'Accounts and billers' in the Move money section
And I see 'Add account' link will be available to user having Update payees & payment limits permission.
When I click on link 'Add account' 
And I see a a modal window comes up
And I see 'Add' button in enabled state
And I do not see static text 'SMS code for your security' and 'Get SMS code' button in the modal
When I click on Add' button after filling invalid values in 'Account name, 'BSB', 'Account number' and 'Account nickname' fields
Then I see that all the field level validations are executed and I see error message below each of the 'Account name, 'BSB', 'Account number' and 'Account nickname' fields
When I click on Add' button after filling valid values in 'Account name, 'BSB', 'Account number' and 'Account nickname' fields
Then I see the modal is navigated to 'Account and biller' page
And I see message '<Account> has been added to account and biller
And I see the details of the newly added linked account is displayed in the list


Scenario: Verify 'Try again' link functionality for add account modal
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I click on tab 'Accounts and billers' in the Move money section
And I see Add account link will be available to user having Update payees & payment limits permission.
When I click on link 'Add account' 
And I see a a modal window comes up
When I click on 'Get SMS Code' button after filling valid values in 'Account name, 'BSB', 'Account number' and 'Account nickname' fields
Then I see text 'Code sent to <XX## ### XXX>. If the code is not received in a few minutes, <try again> with try again being a link.
When I click on 'try again' link
And I enter the new valid SMS code in 'SMS code' field
And I click on the 'Add' button
Then I see the modal is navigated to 'account and biller' page
And I see message '<Account> has been added to account and biller


Scenario: Verify 'Cancel or 'X' button functionality in Add linked account modal for linked section
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I click on tab 'Accounts and billers' in the Move money section
And I click on Add account against the 'Pay anyone accounts' section 
Then I see a a modal window come up
When I click on the 'Cancel' button
Then I see the modal is navigated to 'accounts and billers' page without adding any account
When I click on Add account account against the 'Pay anyone accounts' section 
Then I see a a modal window come up
When I click on the 'X' button
Then I see the modal is navigated to 'accounts and billers' page without adding any account


!-- ##################Change daily limit on accounts and billers page for pay anyone accounts section#####################################

Scenario: Verify 'Amount' field and heading text in Change daily limit modal for pay anyone accounts section
Meta:
@categories AccAndBiller Automation

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I click on tab 'Accounts and billers' in the Move money section
And I click on 'Change daily limit' link against the 'Pay anyone accounts' section 
Then I see a a modal window come up
And I see static text 'Change daily payment limit for Pay anyone payments'
And I see the 'Amount' list box with default value as current daily Pay Anyone limit set.
And I click on the list box to see the following daily limit options 10,000, 25,000, 50,000, 100,000, and 200,000
When I select a limit from the list box
Then I see the selected limit is displayed in the list box


Scenario: Verify 'SMS code for your security' functionality in Change daily limit modal for pay anyone accounts section when analyse call made to SAFI on opening modal does not results in challenge
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I click on tab 'Accounts and billers' in the Move money section
And I click on 'Change daily limit' link against the 'Pay anyone accounts' section 
Then I see a a modal window come up
And I see static text 'SMS code for your security
When I select a limit from the 'Amount' list box other than the already displayed limit
Then I see 'Get SMS  code' button in enabled state
And I click on 'Get SMS code' button
Then I see get SMS code button get replaced with SMS code field with max length of it is 6 digits
And I see static text 'Code sent to <XX## ### XXX>. If the code is not received in a few minutes try again' with 'try again' text being a link.
And I see that except for first two digit and last three digit of mobile number in static text 'Code sent to <XX## ### XXX>' are masked with rest of numbers replaced by #


Scenario: Verify 'SMS code for your security' functionality in Change daily limit modal for pay anyone accounts section when analyse call made to SAFI on opening modal results in challenge
Meta:
@categories AccAndBiller Manual
Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
And I click on 'Change daily limit' link against the 'Pay anyone accounts' section 
Then I see a a modal window come up
Then I do not see get SMS code button and SMS code for your security text



Scenario: Verify 'try again' functionality in Change daily limit modal for pay anyone accounts section 
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I click on tab 'Accounts and billers' in the Move money section
And I click on 'Change daily limit' link against the 'Pay anyone accounts' section 
Then I see a a modal window come up
When I click on 'Get SMS Code' button after selecting a limit from the 'Amount' list box
Then I see text 'Code sent to <XX## ### XXX>. If the code is not received in a few minutes, <try again> with try again being a link.
When I click on 'try again' link
And I enter the new valid SMS code in 'SMS code' field
And I click on the 'Save' button
Then I see the modal is navigated to 'account and biller' page
And I see message '<Account> has been added to account and biller'


Scenario: Verify 'Save button functionality when SAFI challenge is required' in Change daily limit modal for pay anyone accounts section 
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I click on tab 'Accounts and billers' in the Move money section
And I click on 'Change daily limit' link against the 'Pay anyone accounts' section 
Then I see a a modal window come up
And I see the 'Save' button in disabled state
And I select a limit from the 'Amount' list box other than the already displayed limit
And I click on 'Get SMS code' button
And I enter wrong SMS code in 'SMS code' field
Then I see 'Save' button to change itself to enable state
When I click on the 'Save' button
Then I see error message 'SMS code not correct. Please try again with a new code' 
When I enter valid SMS code in 'SMS code' field
When I click on the 'Save' button
Then I see the modal is navigated to 'accounts and billers' page
And I see message 'Pay anyone daily limit has been updated successfully. on top of the screen


Scenario: Verify 'Save button functionality when SAFI challenge is not required' in Change daily limit modal for pay anyone accounts section
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I click on tab 'Accounts and billers' in the Move money section
And I click on 'Change daily limit' link against the 'Pay anyone accounts' section 
Then I see a a modal window come up
And I see the 'Save' button in disabled state
And I select a limit from the 'Amount' list box other than the already displayed limit
Then I see the 'Save' button in enabled state
When I click on the 'Save' button
Then I see the modal is navigated to 'accounts and billers' page
And I see message 'Pay anyone daily limit has been updated successfully.on top of the screen


Scenario: Verify 'Cancel or 'X' button functionality in Change daily limit modal for pay anyone accounts section
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I click on tab 'Accounts and billers' in the Move money section
And I click on 'Change daily limit' link against the 'Pay anyone accounts' section 
Then I see a a modal window come up
When I click on the 'Cancel' button
Then I see the modal is navigated to 'accounts and billers' page without updating any limit
When I click on Change daily limit link against the 'Pay anyone accounts' section 
Then I see a a modal window come up
When I click on the 'X' button
Then I see the modal is navigated to 'accounts and billers' page without updating any limit


!-- #####################################Add biller on accounts and billers page######################################


Scenario: Verify 'Biller code' field validations, BPay icon and heading static text in 'Add biller' modal
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I click on tab 'Accounts and billers' in the Move money section
And I see 'Add biller' link will be available to user having Update payees & payment limits permission.
When I click on link 'Add biller' 
Then I see a a modal window come up
And I see static text 'Add biller'
And I see BPAY icon
And I see max limit of 'Biller code' field is 10 digits
And I see predictive search executed as soon as I type at least one number in biller code field
And If I enter biller code which is not available 
Then error message 'Please enter Biller Code numbers' is displayed below the field
And I see if the valid biller code is selected from the predictive search
Then the biller code and the name of the biller appears in the field


Scenario: Verify 'CRN' field validations in 'Add biller' modal
Meta:
@categories AccAndBiller manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I click on tab 'Accounts and billers' in the Move money section
And I see 'Add biller' link will be available to user having �Update payees & payment limits permission.
When I click on link 'Add biller' 
Then I see a a modal window come up
And I see max limit of 'CRN' field is 20 characters and only letters and numbers are allowed in the field
When I leave the 'CRN' field blank or enter any other non allowed characters and tab out
Then I see error message 'Please enter a Customer Reference Number (CRN)'
When I enter valid CRN number in the field and tab out
Then I see that validation is passed and there are no error messages 
When CRN number is fixed CRN and save to account and biller list checkbox is checked
Then I see CRn number is saved in address book for further references
When CRN number is vCRN and iCRN
Then it will not be stored in address book


Scenario: Verify 'Biller nickname' field validations in 'Add biller' modal
Meta:
@categories AccAndBiller Automation

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I click on tab 'Accounts and billers' in the Move money section
And I see 'Add biller link' will be available to user having �Update payees & payment limits permission.
When I click on link 'Add biller' 
Then I see a a modal window come up
And I see max limit of 'Biller nickname' field is 30 characters
When I enter any non allowed characters other than accepted: letters, numbers, hyphens, or spaces
Then I see error message 'Please only use 'letters, numbers, hyphens or spaces'
When I enter valid Biller nickname in the field and tab out
Then I see that validation is passed and there are no error messages 


Scenario: Verify 'SMS code for your security' functionality in 'Add biller' modal and analyse call made to SAFI on opening modal does not results in challenge
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I click on tab 'Accounts and billers' in the Move money section
And I see 'Add biller' link will be available to user having �Update payees & payment limits� permission.
When I click on link 'Add biller' 
And I see a modal window comes up
And I see static text 'SMS code for your security'
And I enter invalid values in each of the 'Biller code�, 'CRN number' and 'Biller nickname' fields
And I click on 'Get SMS Code' button 
Then I see error message below each of the 'Biller code�, 'CRN number' and 'Biller nickname' fields
When I click on 'Get SMS Code' with the CRN number entered is fixed CRN and if there is duplicate combination of biller code and CRN present
Then I see error message 'Biller already exists with same customer reference number' 
When I click on 'Get SMS Code' with entered nickname is already existing
Then I see error message 'Biller nick name already exists'
When I click on 'Get SMS Code' button with valid 'Biller code, 'CRN number' and 'Biller nickname' entered
Then I see get SMS code button get replaced with SMS code field with max length of it is 6 digit 
And I see static text 'Code sent to <XX## ### XXX>. If the code is not received in a few minutes try again' with 'try again' text being a link.
And I see that except for first two digit and last three digit of mobile number in static text 'Code sent to <XX## ### XXX>' are masked with rest of numbers replaced by #


Scenario: Verify 'Get SMS Code' button functionality with valid and invalid data and analyse call made to SAFI on opening modal results in challenge
Meta:
@categories AccAndBiller Manual
Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I navigate to 'Accounts and billers' tab
And I click on link 'Add biller'
Then I do not see get SMS code button and SMS code for your security text
And I enter invalid values in each of the 'biller code', 'CRN', and 'biller nickname' fields
When I click on the 'Add' button
Then I see error message below each of the 'biller code', 'CRN', and 'biller nickname' fields
When I click on 'Add' button with valid 'biller code', 'CRN', and 'biller nickname' fields 
Then I see the modal is navigated to 'Account and biller' page
And I see message '<Account> has been added to account and biller



Scenario: Verify 'Add' button functionality when 2FA is required from SAFI
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
And I know that 2FA authentication is required from SAFI
When I click on tab 'Accounts and billers' in the Move money section
And I see 'Add biller' link will be available to user having �Update payees & payment limits� permission.
When I click on link 'Add biller' 
And I see a modal window comes up
And I see 'Add' button is disabled till the 6 digit SMS code is not filled
When I click on 'Get SMS Code' button after filling valid values in 'Biller code�, 'CRN number' and 'Biller nickname' field
And I enter wrong SMS code in 'SMS code' field
Then I see 'Add' button to change itself to enable state
When I click on the 'Add' button
Then I see error message 'SMS code entered is incorrect. Please try again' below SMS code text box
When I enter valid SMS code in 'SMS code' field
Then I see 'Add' button to change itself to enable state
When I click on the 'Add' button
Then I see the modal is navigated to 'accounts and biller' page
And I see message '<Account> has been added to account and biller�
And I see the details of the newly added biller is displayed in the 'account and billers' page


Scenario: Verify 'Add' button functionality when 2FA authentication is not required
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
And I know that 2FA authentication is not required from SAFI
When I click on tab 'Accounts and billers' in the Move money section
And I see 'Add biller' link will be available to user having �Update payees & payment limits� permission.
When I click on link 'Add biller' 
And I see a a modal window comes up
And I see 'Add' button in enabled state
And I do not see static text 'SMS code for your security' and 'Get SMS code' button in the modal
When I click on Add' button after filling invalid values in 'Biller code, 'CRN number' and 'Biller nickname' fields
Then I see that all the field level validations are executed and I see error message below each of the 'Biller code, 'CRN number' and 'Biller nickname' field
When I click on 'Add' button after filling valid values in 'Biller code, 'CRN number' and 'Biller nickname' fields
Then I see the modal is navigated to 'Account and biller' page
And I see message '<Account> has been added to account and biller
And I see the details of the newly added biller account is displayed in the list


Scenario: Verify 'Try again' link functionality
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I click on tab 'Accounts and billers' in the Move money section
And I see 'Add biller' link will be available to user having Update payees & payment limits permission.
When I click on link 'Add biller' 
And I see a modal window comes up
And I see 'Get SMS code' button
And I see 'Add' button is disabled till the 6 digit SMS code is not filled
When I click on 'Get SMS Code' button after filling valid values in 'Biller code, 'CRN number' and 'Biller nickname' fields
Then I see text 'Code sent to <XX## ### XXX>. If the code is not received in a few minutes, <try again> with try again being a link.
When I click on 'try again' link
And I enter the new valid SMS code in 'SMS code' field
And I click on the 'Add' button
Then I see the modal is navigated to 'accounts and billers' page
And I see message '<Account> has been added to account and biller

Scenario: Verify 'Cancel or 'X' button functionality in Add biller modal for BPay billers section
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I click on tab 'Accounts and billers' in the Move money section
And I click on Add biller against the 'BPay billers' section 
Then I see a a modal window come up
When I click on the 'Cancel' button
Then I see the modal is navigated to 'accounts and billers' page without adding any account
When I click on Add biller against the 'BPay billers' section 
Then I see a a modal window come up
When I click on the 'X' button
Then I see the modal is navigated to 'accounts and billers' page without adding any account


!-- ##################Change daily limit on accounts and billers page for BPay billers section#####################################


Scenario: Verify Amount field and static heading text in Change daily limit modal for pay BPay billers section 
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I click on tab 'Accounts and billers' in the Move money section
And I click on 'Change daily limit' link against the 'BPay billers' section 
Then I see a a modal window come up
And I see static text 'Change daily payment limit for BPay payments'
And I see BPay icon
And I see the 'Amount' list box with default value as current daily Pay Anyone limit set.
And I click on the list box to see the following daily limit options 10,000, 25,000, 50,000, 100,000, and 200,000
When I select a limit from the list box
Then I see the selected limit is displayed in the list box


Scenario: Verify 'SMS code for your security' functionality in Change daily limit modal for BPay biller accounts section when analyse call made to SAFI on opening modal does not results in challenge
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I click on tab 'Accounts and billers' in the Move money section
And I click on 'Change daily limit' link against the 'BPay billers' section 
Then I see a a modal window come up
And I see static text 'SMS code for your security
When I select a limit from the 'Amount' list box other than the already selected limit
Then I see 'Get SMS  code' button in enabled state
And I click on 'Get SMS code' button
Then I see get SMS code button get replaced with SMS code field with max length of it is 6 digits
And I see static text 'Code sent to <XX## ### XXX>. If the code is not received in a few minutes try again' with 'try again' text being a link.
And I see that except for first two digit and last three digit of mobile number in static text 'Code sent to <XX## ### XXX>' are masked with rest of numbers replaced by #


Scenario: Verify 'SMS code for your security' functionality in Change daily limit modal for BPay biller section when analyse call made to SAFI on opening modal results in challenge
Meta:
@categories AccAndBiller Manual
Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
And I click on 'Change daily limit' link against the 'BPay billers' section 
Then I see a a modal window come up
Then I do not see get SMS code button and SMS code for your security text

Scenario: Verify 'try again' functionality in Change daily limit modal for BPay biller accounts section 
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I click on tab 'Accounts and billers' in the Move money section
And I click on 'Change daily limit' link against the 'BPay billers' section 
Then I see a a modal window come up
When I click on 'Get SMS Code' button after selecting a limit from the 'Amount' list box
Then I see text 'Code sent to <XX## ### XXX>. If the code is not received in a few minutes, <try again> with try again being a link.
When I click on 'try again' link
And I enter the new valid SMS code in 'SMS code' field
And I click on the 'Save' button
Then I see the modal is navigated to 'account and biller' page
And I see message 'BPay daily limit has been updated successfully.


Scenario: Verify 'Save button functionality when SAFI challenge is required' in Change daily limit modal for BPay biller section 
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I click on tab 'Accounts and billers' in the Move money section
And I click on 'Change daily limit' link against the 'BPay biller' section 
Then I see a a modal window come up
And I see the 'Save' button in disabled state
And I select a limit from the 'Amount' list box
And I click on 'Get SMS code' button
And I enter wrong SMS code in 'SMS code' field
Then I see 'Save' button to change itself to enable state
When I click on the 'Save' button
Then I see error message 'SMS code not correct. Please try again with a new code' 
When I enter valid SMS code in 'SMS code' field
Then I see 'Save' button to change itself to enable state
When I click on the 'Save' button
Then I see the modal is navigated to 'accounts and billers' page
And I see message 'BPay daily limit has been updated successfully. on top of the screen


Scenario: Verify 'Save button functionality when SAFI challenge is not required' in Change daily limit modal for BPay biller section
Meta:
@categories AccAndBiller Manual

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I click on tab 'Accounts and billers' in the Move money section
And I click on 'Change daily limit' link against the 'BPay biller' section 
Then I see a a modal window come up
And I see the 'Save' button in disabled state
And I select a limit from the 'Amount' list box other than the already selected limit
Then I see the 'Save' button in enabled state
When I click on the 'Save' button
Then I see the modal is navigated to 'accounts and billers' page
And I see message 'Pay anyone daily limit has been updated successfully. on top of the screen


Scenario: Verify 'Cancel or 'X' button functionality in Change daily limit modal for BPay biller section
Meta:
@categories AccAndBiller Automation

Given I login into panorama system as Investor
And I have required 'View account reports' permissions to access 'accounts and billers' tab
When I click on tab 'Accounts and billers' in the Move money section
And I click on 'Change daily limit' link against the 'BPay biller' section 
Then I see a a modal window come up
When I click on the 'Cancel' button
Then I see the modal is navigated to 'accounts and billers' page without updating any limit
When I click on 'Change daily limit' link against the 'BPay billers' section 
Then I see a a modal window come up
When I click on the 'X' button
Then I see the modal is navigated to 'accounts and billers' page without updating any limit