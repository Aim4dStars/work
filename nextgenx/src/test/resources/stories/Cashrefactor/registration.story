Narrative: Registration
       
Scenario: Verify the error of Registration page 1 on empty tab outs
Meta:
@categories Registration Automation1

Given I navigate to Sign in page
When I click on Register link
When I click on Get SMS Code with all fields are empty
Then I see message 'Please enter your registration number' below 'Registration number' field
And I see message 'Please enter your last name' below 'Last name' field
And I see message 'Please enter your postcode' below 'Post Code' field

Scenario: Verify the functionality of Registration number, Last name of Post Code register page1
Meta:
@categories Registration Automation1

Given I navigate to Sign in page
When I click on Register link
Then I see Investor/Adviser registration step1 page with static text 'Step 1 of 2'
And I see 'Registration number' text with text box field
!-- And I see user can enter maximum 12 character/special characters/ Letters
!-- verifying Last name
And I see 'Last name' text with text box field
!-- And I see user can enter maximum 256 characters
And I see special characters -()'space<>,+& is allowed else it displays error message 'Please enter your last name' below the last name field
!-- verifying Post code
And I see 'Postcode' text with text box field


Scenario: Verify the functionality of cancel button on Register Step 1 page
Meta:
@categories Registration Automation1

Given I navigate to Sign in page
When I click on Register link
Then I see static text Need help? Call 1300881716 for investor
!-- And I see static text Need help? call 1300784207 for Adviser
When I click on Cancel link of Register Step 1 of 2 page
Then I see Sign-in page


Scenario: Verify the functionality of Get SMS Code and Next button
Meta:
@categories Registration Automation11

Given I navigate to Sign in page
When I click on Register link
Then I see Get SMS code button on register page
When I enter valid 'registration number', 'Last name' & 'postcode' and click on 'Get SMS Code'
Then I see get SMS code button get replaced via SMS code field and max length of it is 6 digit
And I see static text 'Code sent. If the code is not received in a few minutes, Try again'
And I see Next button is disabled till the 6 digit SMS code is not filled
When I enter SMS code in SMS code field on registration
When I click on next button
!-- Then I see Investor/Adviser registration step 2 page with static text Investo/Adviser registration 'Step 2 of 2'


Scenario: Verify the functionality of Get SMS Code and Next button -manual
Meta:
@categories Registration Manual

Given I navigate to Sign in page
When I click on Register link
Then I see by default Get SMS code button is enabled
When I click on Get SMS Code button 
Then I see button check for validation for 'registration number', 'Last name' & 'postcode' for input entered
And I see if the input are correct a SMS is sent to registered mobile number of investor
And I see error message displayed 'Some of the details you've entered don't match our records. Please check they're correct before trying again' if any mismatch occur
When I enter valid 'registration number', 'Last name' & 'postcode'
And I click on get SMS code button
Then I see try again link
When I enter SMS code in SMS code field
Then I see SMS code is verified from SAFI
And I see if SMS code does not match, error message is 'please enter valid SMS code' displayed
When 2FA authentication is not required
Then I see Next button is enabled by default
And I see all field level validation happen on click of next button
 

Scenario: Verify the functionality of Create username, Create password, Repeat password of register page2
Meta:
@categories Registration Automation11

Given I navigate to Sign in page
When I click on Register link
And I click on next button to go to Step 2 page
Then I see Investor/Adviser registration step 2 page with static text Investo/Adviser registration 'Step 2 of 2'
And I see 'Create username' text with text box field
!-- And I see max character limit of 'Create username' field is 50
And I see error message 'Please enter your username' if field is left blank
When I click on 'Create username' field
Then I see callout box with validation message 'Must be', 'Between 8-50 character', 'A combination of letters and numbers', 'Cannot include', 'An email address', 'One of these character '&^%$#@!'
!-- verify create password field
And I see 'Create password' text with text field
And I see if password is not entered and on tab out displays an error message 'Please enter password'
And I see 'Create password' field allow 250 characters
Then I see callout box with validation message 'Must be at least', 'One Letter', 'One number or special character', '8 Characters', 'Cannot include', 'Your user name'
!-- verify repeat password
And I see 'Repeat Password' text with text field
!-- And I see 'Repeat Password' text field accept max 250 characters
And I see If repeat password is not entered then on tab out display error below user name field 'Repeat password cannot be empty'


 

Scenario: Verify the functionality of Create username, Create password, Repeat password of register page2 - manual
Meta:
@categories Registration Manual

Given I navigate to Sign in page
When I click on Register link
And I click on next button to go to Step 2 page
And I click on 'Create username' field
Then I see callout box with validation message 'Must be', 'Between 8-50 character', 'A combination of letters and numbers', 'Cannot include', 'An email address', 'One of these character '&^%$#@!'
And I see if password is not entered and on tabout displays an error message 'Please enter password'
And I see on tabout username will be validated from EAM, if username is available it will show with green check mark on Rightside of textbox
And I see if username is not available i see error message 'Entered user name is not available'
And I see if entered password meet all conditions then green checkmark appears else error message 'Please enter password'


Scenario: Verify the functionality of Terms and Condition
Meta:
@categories Registration Automation1

Given I navigate to Sign in page
When I click on Register link
And I click on next button to go to Step 2 page
Then I see Term & conditions check box and by default it is unchecked
And I see static text 'I agree to the term and conditions'
When I click on 'term and conditions' link
Then I see terms and conditions open in new tab


Scenario: Verify the functionality of Sign In button
Meta:
@categories Registration Automation123

Given I navigate to Sign in page
When I click on Register link
And I click on next button to go to Step 2 page
Then I see Sign In button is disabled by default
Then I see Sign in button gets enable
Then I see error message 'Too many consecutive repeated characters' or 'Not enough special characters' below password field
And I see error message 'Entered user name is not available' below username
And I see if re-entered password is different it will display error below the field 'Enter the same password as above'



Scenario: Verify when user tries to exit from registration
Meta:
@categories Registration Automation

Given I navigate to Sign in page
When I click on Register link
And I click on next button to go to Step 2 page
And I close the browser
Then I see Exit Registration modal with header text Exit registration? 
And I see message 'If you leave this page you will have to call the service centre to complete your registration.'
When I click on 'Stay on this' page button
Then I navigate back to base screen
When I click on Leave Page link 
Then I navigate back to Sign-in page


Scenario: Verify Non approver T&C
Meta:
@categories Registration Manual

Given I navigate to Sign in page
When I click on Register link
And I click on next button to go to Step 2 page
And I click on Sign-In button 
Then I see T&C page opens in new tab
And I see heading text Terms and conditions 
When I click on I agree button when all check box are selected
Then I see Account Application status page if there is only one pending approval
And I see Dashboard if all approvers have already approved the application


Scenario: Verify approval for on-boarding is from Avoka
Meta:
@categories Registration Manual

Given I navigate to Sign in page
When I click on Register link
And I click on next button to go to Step 2 page
And I click on Sign-In button
Then I see text While label placement static text on top left corner
And I see static heading text 'Account application summary'
And I see static message 'Welcome to Panorama. Please review the account details below before approving your account. If anything is incorrect please contact your adviser'
And I see place holder will show avoka page for approval 
When I click Signout link on top right corner
Then I see user gets logged out


Scenario: Verify application status page
Meta:
@categories Registration Manual

Given Investor want to see the account application status page 
When non approver registered or approver approves the application 
Then I see heading Account application status 
And I see message Your account application is approved. Once all applications have completed registration the account will become active.
And I see static message 'Once all the approver approves the application the account will become active '
And I see Type of application: the values can be Individual, Joint, company, SMSF, Trust
And I see Application ref no.: Display application ref no sourced from Avoka or Thought works
And I see Applied on: display date when application was submitted by Adviser 
And I see Adviser: display the name of the adviser attached to the application. First name & Last name
And I see Phone no: Display contact number of the adviser
And I see Email: display email address of the adviser
And I see Table which have list of all investors those are approvers
And I see table header Name, Mobile and Email column
And I see primary contact identifier to show primary contact
When I click on it 
Then I see help text
And I see name column displays the name of the approver  
And I see mobile column displays the Preferred mobile number of the approver
And I see Email Id column displays preferred email id of the approver 
And I see three status of approver Not Registered , Registered and Approve