Narrative:Login page


!-- ######################################### SIGN IN #####################################

Scenario: Verify Sign in functionality
Meta:
@categories SignInsaya Automation

When I am on the sign in page
Then I see text Sign in on top of the page
And I see text Username with blank textbox field on sign in page
And I see text Password with blank textbox field ons sign in page
And I see static text Password are case sensitive
And I see button Sign in
And I see link Register and Forgotten password
And I see static text Forgotten your username?
And I see text Call 1300 784 207. in the next line
When I click on the Sign in button
Then I see page gets navigated to Dashboard or status approver page

Scenario: Verify error message validations for Sign in screen
Meta:
@categories SignInErrorMessageValidation Manual 

When I am on the sign in page
Then I see in "username" field I can enter upto 250 characters which is not limited by any chatacter set
And I see error message will be displayed on tab out below user name field "Please enter your username" if username is not entered or blank
And I see in "Password" field I can enter upto 250 characters which is not limited by any chatacter set
And I see error message will be displayed on tab out below password field "Please enter your a password" if password is not entered
And I see password is masked and shown in Asterisk[*]
When I click on "Sign in" button
Then I see error message will be displayed if the username and password is blank
When If I click on "Sign in" button
Then I see error message "Some of the details entered don't match our records. Please check they're correct before trying again, otherwise your access may be locked." above the sign in box if status returned by EAM/Web-Seal is auth_failure
And I see error message "Your access is now BLOCKED. Please contact us on [CONTACT_NUMBER_PLACEHOLDER]" above the sign in box if status returned by EAM/Web-Seal is auth_info
And I see error message "We were unable to sign you in. Please try again." if status returned by EAM/Web-Seal is auth_timeout
And I see error message "Sorry, we may be having some technical difficulties. Please try again in a minute." above the sign in box if status returned by EAM/Web-Seal is eai_auth_error
And I see error message "We were unable to sign you in. <link to Sign out"> Please click here and then try signing in again. if status returned by EAM/Web-Seal is Error
And I will see Logout screen if status returned by EAM/Web-Seal is Logout
And I see error message "Your temporary password has expired. Please call us on [CONTACT_NUMBER_PLACEHOLDER]" above the Sign in box if status returned by EAM/Web-Seal is passwd_warn 
And I see error message "Your temporary password has expired. Please call us on [CONTACT_NUMBER_PLACEHOLDER]" above the Sign in box if status returned by EAM/Web-Seal is passwd_exp 
And I see error message "Your temporary password has expired. Please call us on [CONTACT_NUMBER_PLACEHOLDER]" above the Sign in box if status returned by EAM/Web-Seal is temp_passwd_exp
And I see error message "Sorry, we may be having some technical difficulties. Please try again in a minute." above the Sign in box if status returned by EAM/Web-Seal is stepup


Scenario: Verify UI of the Sign in screen
Meta:
@categories SignInVerifyUI Manual 

When I am on the sign in page
Then I see text "Sign in" and larger and bold font
And I see text "Username" and "Password" in bold font
And I see "Sign in" button in blue colour
And I see links "Register" and "Forgotten password" with blue icon to the left
And I see content in the Sign in box as left aligned
And I see Sign in box surrounded with blue background central aligned
When I click on "Sign in" button
And If I see error message on submitting the form
Then I see "Username" and "Password" text field highlighted with blue outline 


!-- ##################################### FORGOT PASSWORD ############################################


Scenario: Verify forgot password functionality
Meta:
@categories ForgotPassword Automation

When I click on the Forgotten password link on Sign in page
Then I see static text Forgotten password on top of Step 1
And I see static text Step 1 of 2
And I see text Username with blank textbox field
And I see text Last name with blank textbox field
And I see text Postcode with blank textbox field
And I see text Enter SMS code for your security below the Postcode textbox
And I see button Get SMS code
When I click the Get SMS code button on forgot password screen
Then I see the button is replaced with text box to enter the SMS code on forgot password screen
And I see Next button is disabled by default on forgot password screen
And I see maximum length allowed for SMS code on forgot password page is 6 digits
And I see static text Code sent registered mobile number. If the code is not received in a few minutes, try again
When I enter 6 digit sms code on forgot password screen
Then I see Next button gets enabled on forgot password screen
And I see link Cancel on Forgotten password step 1
When I click on Cancel link of Forgotten password step 1
Then I See page gets navigated from Forgotten password step 1 to Sign in screen
When I click Next button
Then I see page gets navigated to Forgot password Step 2 if all fields are verified
And I see static text Forgotten password on top of Step 2
And I see static text Step 2 of 2
And I see text Create password with blank textbox field
And I see text Repeat password with blank textbox field
And I see Sign in button and Cancel link on Forgot password step2
And I see help text to the right side of the screen displaying password policies
When I click on Cancel link of Forgotten password step 2
Then I See page gets navigated from Forgotten password step 2 to Sign in screen
When I click on the Sign in button
Then I see page gets navigated to Dashboard or status approver page

@Manual
Scenario: Verify error message validations of Forgotten password page
Meta:
@categories ForgotPasswordErrorMsgValidation Manual 

When I click on the "Forgotten password" link on Sign in page
Then I see in "username" field I can enter upto 250 characters which is not limited by any chatacter set
And I see error message will be displayed on tab out below user name field "Please enter your username" if username is not entered or blank
And I see in "Lastname" field I can enter upto 256 characters
And I see error message will be displayed on tab out below last name field "Please enter your lastname" if lastname is not entered or blank
And I see special characters -()'space<>+& are allowed
And I see error message "Please enter valid postcode" if post code is blank
And I see maximum length allowed for postcode is 10 digits and it accepts alphanumeric characters
When I click on the "Get SMS code" button
Then I see error message will be displayed if "Username" and "Last name" is blank
And I see error message "SMS code entered is incorrect. Please try again." if non numeric characters have been entered
And I see "try again" as link which will resend the code to registered mobile number
And I see there is no restriction on clicking "try again" link
And I see "Get SMS code" button will be disabled for emulator user
And I see button "Next" by default disabled until 6 digit SMS code is not filled in SMS text box
When I click on "Next" button
Then I see error message will be displayed "SMS code entered is incorrect. Please try again" below the field if verification failed from SAFI
And I see user name, last name and post code will be validated from EAM & Avaloq respectively
And I see error message "Some of the details entered don't match our records. Please check they're correct before trying again, otherwise your access may be locked" if not matched
When I click Next button
Then I see page gets navigated to next screen-Step 2 if all fields are verified
And I see in "Create password" field I can enter upto 50 characters which is not limited by any character set
When I click on the "Create password" textbox
Then callout box will come up in the left side with The validations mentioned 
And I see validations will be marked checked once filled password passes the validations
And I see callout box with heading "Must be at least" 
And I see validations as "One Letter", "One number or special character", "8 Characters", "Cannot include" as section heading and "Your user name"
And I see error message "Please enter a new password fulfilling criteria’s mentioned" if the entered password does not satisfy the criteria
And I see error message will be displayed on tab out "Please enter a password" if password is not entered
And I see in "Repeat password" field I can enter upto 50 characters which is not limited by any character set
And I see error message will be displayed on tab out "Please enter your new password again" if Repeat password is not entered
And I see error message "Enter the same password as above" below the repeat password field if the values in "Create password" and "Repeat password" does not match
When I click on Sign in button
Then I see EAM service will be called for change password
And I see "Failed to reset password, please try again" if the response by EAM service is failedResetPassword
And I see "Sorry, we may be having some technical difficulties. Please try again in a minute" if the response by EAM service is MSG_WS_PWD_REP_FAILURE 
And I see "Password has been used previously please try again with a new password" if the response by EAM service is MSG_WS_PWD_POLICY_INHIST
And I see "You can't use a number more than 3 times in a row" if the response by EAM service is MSG_WS_PWD_MAX_CON_REP_CHAR 
And I see "Your password must be between 8 and 32 characters long, with at least 1 letter and a number or special character" if the response by EAM service is MSG_WS_PWD_MIN_OTHER  
And I see "Your password must be between 8 and 32 characters long, with at least 1 letter and a number or special character" if the response by EAM service is MSG_WS_PWD_MIN_ALPHA  
And I see "Your password must be between 8 and 32 characters long, with at least 1 letter and a number or special character" if the response by EAM service is MSG_WS_PWD_MIN_LENGTH 


Scenario: Verify UI of Forgotten password page
Meta:
@categories ForgotPasswordUIVerify Manual 

When I click on the Forgotten password link on Sign in page
Then I see text "Forgotten password" in larger and bold font
And I see text "Step 1 of 2", "Username", "Last name", "Post code" and "Enter SMS code for your security" in bold font
And I see text "Case sensitive" in grey colour
And I see button text "Get SMS code" in bold font with mobile icon to the right side of the text
And I see link "Cancel" with blue icon to the left
And I see content in the step 1 Forgotten password as left aligned
When I hover over Button "Get SMS code"
Then I see button gets highlighted with blue outline
When I click on  Get SMS code
Then I see link Try again link in blue colour
When I click on enabled "Next" button
Then I get navigated to Step 2 of Forgotten password page
And I see "Reset password" in large and bold font
And I see text "Step 2 of 2", "Create password" and "Reset password"in bold font
When I enter text in to the "Create password" text field
Then I see callout box where header "Must be atleast" in blue colour
And I see password characters in the "*" format
And I see "Sign in" button in blue colour
And I see content in the Forgotten password box as left aligned
And I see Help text in white colour on blue background to the right side of the Forgotten password box
And I see Forgotten password box surrounded with blue background


!-- ##################################### RESET PASSWORD ############################################


Scenario: Verify reset password functionality
Meta:
@categories ResetPassword Automation

!-- When I click on sign in button after filling temporary password provided by service operator
Then I see static text Reset password on top
And I see text Create password with blank textbox field on Reset password screen
And I see text Repeat password with blank textbox field on Reset password screen
And I see Sign in button and Cancel link on Reset password screen
And I see help text to the right side of the screen displaying password policies on Reset password screen
When I click on Cancel of Reset password screen
Then I see page gets navigated from Reset password to Sign in screen
When I click on the Sign in button
Then I see page gets navigated to Dashboard or status approver page


Scenario: Verify error message validations for reset password screen
Meta:
@categories ResetPwdErrorMessageValidation Manual 

When I click on sign in button after filling temporary password provided by service operator
Then I see in password field I can enter upto 50 characters which is not limited by any character set
When I click on the "Create password" textbox
Then callout box will come up on the left side with The validations mentioned 
And I see validations will be marked checked once filled password passes the validations
And I see callout box with heading "Must be at least" 
And I see validations as "One Letter", "One number or special character", "8 Characters", "Cannot include" as section heading and "Your username"
And I see error message will be displayed on tab out "Please enter your new password" if password is not entered
And I see in "Repeat password" field I can enter upto 50 characters which is not limited by any character set
And I see error message will be displayed on tab out "Please enter your new password again" if Repeat password is not entered
And I see error message "Enter the same password as above" below the repeat password field if the values in "Create password" and "Repeat password" does not match
When I click on Sign in button
And values in "Create password" and "Repeat password" matches
The I see EAM service will eb called for Change password
Then I see "Failed to reset password" if the response by EAM service is failedResetPassword
And I see "Sorry, we may be having some technical difficulties. Please try again in a minute" if the response by EAM service isMSG_WS_PWD_REP_FAILURE  
And I see "Password has been used previously please try again with a new password" if the response by EAM service is MSG_WS_PWD_POLICY_INHIST
And I see "You can't use a number more than 3 times in a row" if the response by EAM service is MSG_WS_PWD_MAX_CON_REP_CHAR 
And I see "Your password must be between 8 and 32 characters long, with at least 1 letter and a number or special character" if the response by EAM service is MSG_WS_PWD_MIN_OTHER  
And I see "Your password must be between 8 and 32 characters long, with at least 1 letter and a number or special character" if the response by EAM service is MSG_WS_PWD_MIN_ALPHA  
And I see "Your password must be between 8 and 32 characters long, with at least 1 letter and a number or special character" if the response by EAM service is MSG_WS_PWD_MIN_LENGTH


Scenario: Verify UI of Reset password page
Meta:
@categories ResetPwdUIVerify Manual 

When I click on sign in button after filling temporary password provided by service operator
Then I see text "Reset password" in larger and bold font
And I see text "Create password" and "Reset password" in bold font
And I see "Sign in" button in blue colour
And I see link "Cancel" with blue icon to the left
And I see content in the Reset password as left aligned
When I enter text in to the "Create password" text field
Then I see callout box where header "Must be atleast" in blue colour
And I see password characters in the "*" format
And I see "Sign in" button in blue colour
And I see content in the Reset password box as left aligned
And I see Help text in white colour on blue background to the right side of the Reset password box
And I see Reset password box surrounded with blue background 


!-- ##################################################Logged out################################


Scenario: Verify logged out functionality
Meta:
@categories LoggedOut Automation

!--When I click on Sign out link 
Then I see static text You are no longer signed in.
And I see Sign in link
When I click on the Sign in button
Then I see page gets navigated to Dashboard or status approver page

@Manual
Scenario: Verify UI of the logged out page
Meta:
@categories LoggedOut Manual

When I click on Sign out link 
Then I see text "You are no longer" in bold font
And I see text "signed in." in green colour
And I see link Sign in with blue icon to the left
And I see Logged out box surround with blue colour











