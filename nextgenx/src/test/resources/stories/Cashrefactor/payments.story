eNarrative: Make a payment

!-- ################################################## Make a payment ############################################################      

Scenario: verify Make payment page
Meta:
@categories payments Automation

Given I login into panorama system as Investor
When I click on Move money link on left panel
Then I see Page gets navigated to Move money page with header Move money
And I see static label Make payment to with list box
And I see link Add account or Biller to the use who has permission
And I see Amount field
And I see date picker
And I see Repeat payment check box for recursive schedule payment
When I check the Repeat payment field
Then I see two more fields Repeat every & end repeats will appear
And I see Repeat every filed have defined values as weekly, fortnight, Monthly, Quarterly or yearly with default value as Monthly
And I see End repeat have defined values as End date, No end date, set number with default value as No end date
When I select End date
Then I see date picker will appear
When I select Set number
Then I see editable field will appear to specify the no of transactions 
And I see optional Description text field
And I see Maximum 18 letters and numbers can be entered in the Description text field
And I see help text 'Maximum 18 letters and numbers' below the Description text field
And I see Next button
When I unchecked the Repeat payment field
Then I see Repeat every & end repeats will disappear



Scenario: verify Make payment to field functionality-manual
Meta:
@categories payments Manual

Given I login into panorama system as adviser
Then I see by default "Make a payment" tab selected underneath blue line
And I see Make payment to list box contains list of all BPAY, Pay Anyone and Linked accounts investor has already added in his address book
And I see by default watermark text "Search or select"
When clicked within the list box
Then I see list of all BPAY, Pay Anyone and Linked account will be displayed to the user who has permission rights
And I see I can type into the box to search for the required BPAY, Pay Anyone, and Linked account
And I see records will be filtered and displayed in the list based on entered characters after searching for the input text Begins in field Account name, Nick name (If any), BSB & account number, biller code and CRN number if its fixed CRN
When I click on the cross icon of list box
Then input text will be cleared
And I see Linked account details in the list box as  Nick Name/ Account Name of the linked account, BSB code and account number.
And I see if account name if nickname is not present than account name is displayed
And I see Primary linked account will be identified by an icon
And I see Pay anyone details in the list box as Nick Name/ Pay anyone Name of the Pay anyone[entered by Investor while adding in address book], BSB and account number 
And I see BPay details in the list box as Nick Name/ Pay anyone of the BPAY [entered by Investor while adding in address book], Biller code, and CRN number only in case fixed CRN number
And I see records will be displayed in the priority order of Primary linked account on top and linked, pay anyone accounts and BPay biller sorted in alphabetical order
When I select record in the drop down list
Then I see selected record will be displayed in text box as Name of linked account/biller/Pay anyone <Hyphen > BSB/Biller code <Space> Account number/CRN number [Only when fixed] 
And I see error message "Please enter or select an account" if no account is selected on tab out
And I see CRN field will be displayed if BPay biller is selected
And I see fixed CRN will be displayed in non editable mode
And I see ICRN and VCRN field will be blank and in editable mode for the user who has higher permission rights
When I unchecked the Repeat payment field
Then I see Repeat every & end repeats will disappear
And I see all fields for repeat payment will be reset to default
And All filled information will be cleared

Scenario: verify Amount field functionality - manual
Meta:
@categories payments Manual

Given I login into panorama system as adviser
Then I see error message displayed on tab out below the field "Please enter an amount" if the field is kept empty or contains non number
And I see error message on tab out "Please enter or select an account" below the amount field if the "Make payment to" field is kept blank
And I see amount will be checked against the remaining daily limit if the account is selected and amount field is not empty
And I see amount will be displayed in proper decimal format


Scenario: verify Date and Repeat checkbox functionality - manual
Meta:
@categories payments Manual

And I see past dates are disabled in the date picker
And I see current date will be disabled If current time is greater later than 5:00pm [Sydney time] 
And error message will be displayed "Payment can’t be made for current date after 5:00PM  - AETZ" if entered manually
And I see on tab out date format changes to "DD Mmm YYYY"
And I see error message below the Date field if the date format is incorrect "Please select a current or past date"
And I see "Repeat payment" by default unchecked
And I see based on the values selected for "Repeat every" field dates will be available to select through date picker
And I see error message "The date you've entered doesn't match the frequency you've selected" if I enters the date other than allowed date manually 
And I see Allowed dates will be calculated by adding 7 days to selected date if I select "Weekly" frequency
And I see Allowed dates will be calculated by adding 14 days to selected start date if I select "Fortnight" frequency
And I see Allowed dates will be calculated by adding 1 Month to selected date if I select "Monthly" frequency
And I see Allowed dates will be calculated by adding 3 months to selected date if I select "Quarterly" frequency
And I see Allowed dates will be calculated by adding 1 year to selected date if I select "Yearly" frequency
And I see maximum number allowed for "Set number" is 999
And I see error message "Please select a number" will be displayed on tab out if non number character is entered
And I see end date of the recursive scheduled payment will be displayed below the field in the format "DD Mmm YYYY"
And I see checking/unchecking the checkbox will not retain the same values
And I see maximum 18 charters(letters & numbers) are allowed in Description text field


Scenario: verify Next button functionality - manual
Meta:
@categories payments Manual

When I click on Next button
Then I see error message will be displayed "The amount entered is more than your available balance. Please reduce the amount or schedule this payment for a future date, and move money into the account beforehand" if transaction amount is more than cash amount available balance
And I see if a payment is being  done to a linked account then Avaloq will check regarding the daily limit. No UI check required.


Scenario: verify Permission - manual
Meta:
@categories payments Manual

And I see linked accounts can make payments to linked accounts only with link accounts displayed in the list box
And I see BPay biller and pay anyone account can make payments to linked account, pay anyone and BPay biller with all accounts displayed in the list box
And I see iCRN and vCRN numbers can be viewed by if I have permission to "Update payees & payment limits” 
And I see I cannot make payment if I don't have linked, BPay or Pay anyone account
And I see error message as per global pattern on the top of the screen "Account has been blocked. Please contact XXXXX". if If my account is blocked is blocked in avaloq
And I see "Add account or Biller" will be available to the user having permission of "payees & payment limits"
And I see dynamic link "change daily limit" will appear below the amount field if I have permission of "Update payees & payment limits"
		
!-- ################################################## Make a payment confirmation ############################################################
        

Scenario: verify payment confirmation page
Meta:
@categories payments Automation

Given I login into panorama system as advisers
When I click on Move money link on left panel
Then I see header Move money
When I click on Next button with all valid data
Then I see model window with header Confirm and pay
And I see details as Account name, BSB number, Account number for 'from'
And I see details as Account name, BSB number, Account number for 'To' 
And I see date, repeat frequency, repeat date, Pay button, change button, change limit button


Scenario: verify Make a payment confirmation page - manual
Meta:
@categories payments Manaul

Given I wants to make a payment to listed accounts [BPAY, Pay Anyone accounts, or Linked accounts]
When I push "Next" on make payment screen after filling mandatory fields
Then I see modal screen with heading Confirm and pay
And I see amount <000,000.00> same as filled in form
And I see From details of the debited account
And I see name of the cash account
And I see BSB number of the cash account
And I see account number of the cash account
And I see To details of the credited account
And I see name of the account to be credited
And I see BSB number of the credited account
And I see account number of the credited account
And I see Date
And I see date of transaction as start date mentioned by user while making this payment on make payment screen in DD Mmm YYY format
And I see Repeats
And I see it shows the frequency & end date of the scheduled transaction selected while scheduling payment from make payment screen
And I see frequency field have values weekly, fortnight, monthly, quarterly, or yearly
When I select no end date in form
Then I see End date field shows text no end date
When I select end date in form
Then I see end date when recursive transaction ends with prefix ends on
And I see Description
And I see description entered by user while doing payment, of 18 characters or numbers
And I see Pay button
When I click Pay button
When the payment made successfully
Then I see it navigates to Make Payment Successful Submit
When the payment not made successfully
Then I see modal window is closed and error is displayed on make payment screen
And I see Pay button is disable for emulator user
And I see Change link
When I click Change link
Then I navigated back to make payment screen with filled details
And I see disclaimer
	
		
!-- ################################################## Make Payment Successful Submit ############################################################
        

Scenario: Make Payment Successful Submit
Meta:
@categories paymentsuccessfulsubmit Automation


When I click on pay button on payment confirm modal window
Then I see page get navigated to the payment successfully submit screen
And I see message Payment of <Amount> submitted successfully
And I see text Date as a Transaction start date
And I see text Repeats
And I see text Description as entered by user while doing payment
And I see text Receipt no. with number returned by Avaloq
And I see Download button to download the PDF receipt
And I see link See all transaction
When I click on See all transaction
Then I see page gets navigated to Past transaction screen
And I see link Make another payment
When I click on Make another payment
Then I see page gets navigated to Make payment screen
And I see link See scheduled transactions
When I click on See scheduled transactions
Then I see page gets navigated to Scheduled transaction screen


Scenario: Verify UI of Make Payment Successful Submit
Meta:
@categories paymentsuccessfulsubmit Manual

When I click on pay button on payment confirm modal window
Then I see page get navigated to the payment successfully submit screen
And I see "Payment" text in blue colour, "of" in grey colour, <<amount>> in bold and "submitted successfully" in green colour
And I see "From" and "To" in picture format with "To" encircled in green colour
And I see "From" details of debited account as "Name of the cash account", "BSB number of the cash account" and "Account number of the cash account"
And I see "To" details of credited account as "Name of the account to be credited account", "BSB number of the credited account" and "Account number of the credited account"
And I see "Name of the cash account" and "Name of the account to be credited account" in bold font
And I see date in the format of "DD MMM YYYY"
And I see "Repeat" field displaying frequency and end date of scheduled transaction selected while scheduling transaction for make payment screen
And I see Frequency field has values weekly, fortnight, Monthly, Quarterly or yearly
And I see End date field showing "No end date" if user has mentioned no end date
And I see End date field showing end date with prefix "ends on" when recursive transaction end
And I see text "Date", "Repeats", "Description", "Receipt no."  and "Download" in bold font
And I see "Downalod" button with icon
And I see links "See all transaction", "Make another payment" and "See scheduled payment" with blue link icon to the left
		
		
!-- ################################################## Make Payment Receipt download PDF ############################################################
        

Scenario: Make Payment Receipt download PDF
Meta:
@categories payments Manual

Given I wants to download the payment receipt in PDF format after making payment successfully to listed accounts

When I push download button on Make a payment Successful submit screen
Then I see it opens the receipt in PDF format or download the PDF on local machine
When setting is to open the PDF 
Then I see PDF opened in new tab
And I see message Payment of <000,000.00> submitted successfully
And I see From details of the debited account
And I see name of the cash account
And I see BSB number of the cash account
And I see account number of the cash account
And I see To details of the credited account
And I see name of the account to be credited
And I see BSB number of the credited account
And I see account number of the credited account
And I see Date
And I see date of transaction as start date mentioned by user while making this payment on make payment screen in DD Mmm YYY format
And I see Repeats
And I see it shows the frequency & end date of the scheduled transaction selected while scheduling payment from make payment screen
And I see frequency field have values weekly, fortnight, monthly, quarterly, or yearly
When I select no end date in form
Then I see End date field shows text no end date
When I select end date in form
Then I see end date when recursive transaction ends with prefix ends on
And I see Description
And I see description entered by user while doing payment, of 18 characters or numbers
And I see receipt number returned from Avaloq


!-- ################################################## Make Payment - Add BPAY biller during payment process ############################################################

Scenario: verify functionality of Biller code 
Meta:
@categories payments Automation

Given I login into panorama system as Investor
When I navigate to payment page
And I click on add account or biller link
Then I see modal window open
When I select biller tab
Then I see static heading as Add biller
And I see user can enter max upto 10 character of type numbers
And I see if the entered code is not found or on tab out an error message 'Please enter Biller Code numbers' is displayed
 

Scenario: verify functionality of Biller code -manual
Meta:
@categories payments Manual

Given I login into panorama system as Investor
When I navigate to payment page
And I click on add account or biller link
Then I see modal window open
When I select biller tab
And I enter biller code
Then I see Predictive search being executed as the user enter the biller code
And I see Search condition is executed with Begin Clause 
And I see records are filtered based on the input characters. 
And I see on selection of biller list the biller code and the name of the biller will start appearing in the list


Scenario: verify functionality of CRN number
Meta:
@categories payments Automation

Given I login into panorama system as Investor
When I navigate to payment page
And I click on add account or biller link
Then I see modal window open
When I select biller tab
Then I see CRN Field which accept max upto 20 character
And I see only letters and number are allowed in field


Scenario: verify functionality of CRN number -manual
Meta:
@categories payments Manual

Given I login into panorama system as Investor
When I navigate to payment page
And I click on add account or biller link
Then I see modal window open
When I select biller tab
Then I see if the entered CRN is fixed then entered number will be saved for further references for the user who has permission rights
And I see In case of vCRN & iCRN it will not be stored in address book when checkbox is selected for the user who has permission
And I see BPAY biller icon adjacent to CRN field


Scenario: verify functionality of Biller nickname
Meta:
@categories payments Automation

Given I login into panorama system as Investor
When I navigate to payment page
And I click on add account or biller link
Then I see modal window open
When I select biller tab
Then I see Biller nickname field which accept max upto 30 character
And I see it allowed characters are letters, numbers, hyphens, or spaces
And I see error message 'Please only use letters, numbers, hyphens or spaces' is displayed for invalid character
And I see Optional text


Scenario: verify the functionality of Save to account and biller list check box -manual
Meta:
@categories payments Manual

Given I login into panorama system as Investor
When I navigate to modal add account or biller in payment page
And I select biller tab
Then I see by default 'Save to account and biller list' check box is selected
And I see if checked then record will start appearing in ‘Make a payment’ list box after successfully adding the account details and details added will be saved in address book for further references after successfully adding the biller details
And I see if unchecked then record will start appearing in ‘Make a payment’ list box after successfully adding the biller details and will not be stored in address book for further reference
And I see SMS code for your security Static text 


Scenario: verify functionality Get SMS Code button -manual
Meta:
@categories payments Manual

Given I login into panorama system as Investor
When I navigate to payment page
And I click on add account or biller link
Then I see modal window open
When I select biller tab
And I click on Get SMS code button
Then I see 'Biller Code' and CRN number gets validated
And I see error message displayed if there is any error in 'Biller code' or 'CRN number' or 'Biller nickname' 
And I see if CRN number is fix then duplicate CRN number is checked, and if duplicate is found then then error message 'Biller already exists with same customer reference number'
And I see if nick name already exist then error message 'Biller nick name already exists' is displayed
When I click on Get SMS code with valid input entered
Then I see Get SMS code button is replaced by SMS code and it accept max 6 digit
And I see static text Code sent to <XX## ### XXX>. If the code is not received in a few minutes, try again
And I see Link to resend the SMS code to registered mobile number. There is not restriction on clicking 'try again' link
When I click on the cancel button
Then I see the modal window is closed and the page is redirected to 'make payment screen'


Scenario: verify functionality of Add button
Meta:
@categories payments Automation

Given I login into panorama system as Investor
When I navigate to payment page
And I click on add account or biller link
Then I see modal window open
When I select biller tab
Then I see Add button is disabled by default
When I enter 6 digit sms code
Then I see Add button gets enabled


Scenario: verify functionality of Add button -manual
Meta:
@categories payments Manual

Given I login into panorama system as Investor
When I navigate to payment page
And I click on add account or biller link
Then I see modal window open
When I select biller tab
And I enter SMS code
Then I see entered SMS code is verified
And I see error message 'SMS code not correct. Please try again with a new code' if entered SMS code is not valid
When I enter valid SMS code
And I click on add button
Then I see page gets navigated to 'Make a Payment'
And I see message on 'Make a Payment' page '<Biller Name> has been added to account and biller'
And I see details of the newly added biller will be selected in 'Make payment to' list box
And I see CRN number gets populated in CRN txt box
And I see if CRN Number is fix then it can't be edited
And I see if it is iCRN & vCRN then it can be edited


!-- ################################################## Make Payment - Add Payment account during payment process ############################################################


Scenario: Make Payment - Add Payment account during payment process
Meta:
@categories payments Automation1

Given I login into panorama system as Investor
When I navigate to payment page
And I click on add account or biller link
Then I see modal window open
And I see Account tab & Biller tab
When I select Account tab
Then I see static heading as Add payment account
And I see Account Name text box can accept maximum upto 32 letters, numbers and following special characters & - < > , + space ( ) /
When I enter other than allowed characters or keep it blank
Then I see error message Please enter a valid account name using letters or numbers or special characters & - < > , + space ( ) /  
And I see BSB textbox accept maximum 7 number
And I see error message 'Please enter a 6-digit BSB number' for character less than 6 and BSB number format
And I see Account number text box with minimum 6 and maximum length 10 with only numbers allowed
And I see error message displayed for invalid or blank account number 'Enter an account number'
And I see Account nickname it's optional field and text
And I see its maximum length is 30 characters and allows letters, numbers, hyphens, or spaces
And I see error message 'Please only use letters, numbers, hyphens or spaces' if any wrong input is added
And I see Save to account and biller list Check box checked by default
And I see static text SMS code for your security
And I see Get SMS code button
When I click the Get SMS code button
Then I see the button is replaced with text box to enter the SMS code
And I see Add button is disabled by default
And I see maximum length allowed is 6 digits
And I see static text appeared below the text code 'Code sent to <XX## ### XXX>. If the code is not received in a few minutes, <try again>'
When I fill 6 digit SMS code
Then I see Add button is active

Scenario: Verify functionality of BSB Field -Manual
Meta:
@categories payments Manual

Given I wants to add Payment Account while on make payment screen as an Investor
When push add account or biller link
When I have permission to Update payees & payment limits
Then I see modal window
And I see BSB number text box
When I enter BSB number
Then I see it is validated against the data in biller file by removing Hyphen
And I see validations carried out on tab out
When it is blank
Then I see error as "Please enter a 6-digit BSB number"
When the BSB and account number combination already exists in address
Then display error Account details already exists


Scenario: verify functionality of account and biller list checkbox button -manual
Meta:
@categories payments Manual

Given I login into panorama system as Investor
And I wants to add Payment Account while on make payment screen
When push add account or biller link
And I have permission to Update payees & payment limits
And I click on add account or biller link
Then I see modal window
And I see checkbox is selected by default
When I uncheck checkbox
Then I see record start appearing in Make a payment list box after successfully adding the biller details
When I check checkbox
Then I see details added are saved in address book for further references after successfully adding the biller details
And I see records not stored in address book for further reference


Scenario: verify functionality of GetSMS Code button -manual
Meta:
@categories payments Manual

Given I login into panorama system as Investor
And I wants to add Payment Account while on make payment screen
When push add account or biller link
And I have permission to Update payees & payment limits
And I click on add account or biller link
Then I see modal window
And I see "Get SMS code" button
When I click on Get SMS Code button
Then I see it validates the value for Account name, BSB, Account number and Account nickname
When details entered are not valid
Then I see error
When I click on Get SMS Code button
Then I see it validates the value for Account name, BSB, Account number and Account nickname
And I see SMS code field with digits to be entered
And the SMS is sent to registered mobile number
And I see mobile number is displayed as masked mobile number of the investor where the SMS code has been sent
And I see mobile number displayed as, first two and last three numbers, rest of the numbers is replaced by "#" eg. <XX## ### XXX>
And I see "Try again link" to resend SMS code
And I see there is not restriction on clicking try again link
And I see "Add" button in white with blue background 
When click on the button
Then I see SMS code is verified
When SMS does not match
Then I see it displays the error message SMS code not correct. Please try again with a new code below the "SMS code" text box
When the SMS code is validated successfully
Then user is navigated to "Make a Payment" page
And I see message is shown on the screen "<Biller Name> has been added to account and biller"
And I see the details of the newly added biller is selected in "Make payment to list box


				
!-- ################################################## Make PaymentChange daily BPAY limit during payment process ############################################################
        

Scenario: Make PaymentChange daily BPAY limit during payment process
Meta:
@categories payments Automation

Given I login into panorama system as adviser
And I want to change daily BPAY limit while doing a payment to BPAY biller
When I clicked on Change limit link on Make a payment screen 
Then I see window with heading Change daily payment limit for BPay payments
And I see text box Amount with $ sign
And I see default value is the next limit available to the current daily BPay limit set
And I see list box is showing higher daily limits available
And I see BPAY icon
And I see static text SMS code for your security
And I see Get SMS code button
When change in daily limit value
Then I see Get SMS code button
When I click on button
Then I see button is replaced with text box to enter the SMS code
And I see maximum length allowed is 6 digits
And I see static text appear below the text code "Code sent to <XX## ### XXX>. If the code is not received in a few minutes, <try again>"
And I see Save button in disabled state
When I entered 6 digit SMS code in text box
And I see Save button is enabled
When I click Save button with wrong SMS code
Then I see the error message SMS code not correct. Please try again with a new code Error Code below the "SMS code" text box.
When I click Save button with correct SMS code
Then I  navigated to Make a Payment page, showing message that BPAY daily limit has been updated successfully. Error message:  Err.IP-0294


Scenario: Make Payment Change daily BPAY limit during payment process -manual
Meta:
@categories payments Manual


Given I want to change daily BPAY limit while doing a payment to BPAY biller
When I clicked on "Change limit link" on Make a payment screen 
Then I see window with heading "Change daily payment limit for BPay payments"
And I see text "Change" in blue colour
And I see text "daily payment limit for" in Gray colour
And I see text "BPay payments" in black colour
And I see BPAY icon after "Amount" field
And I see static text "SMS code for your security" in bold
When I click on this button
Then the SMS is sent to registered mobile number
And I see mobile number is displayed as masked mobile number of the investor where the SMS code has been sent
And I see mobile number displayed as, first two and last three numbers, rest of the numbers is replaced by "#" eg. <XX## ### XXX>
And I see "Try again link" to resend SMS code
And I see  there is not restriction on clicking try again link
		
		
!-- ################################################## Make Payment Change daily Pay anyone during payment process ############################################################
        

Scenario: Make Payment Change daily Pay anyone during payment process
Meta:
@categories payments Automation

Given I want to change daily Pay anyone limits while doing a payment to Pay anyone account
When I clicked on Change limit link on Make a payment screen 
Then I see window with heading Change daily payment limit for Pay Anyone payments
And I see text box Amount with $ sign
And I see default value is the next limit available to the current daily BPay limit set
And I see list box is showing higher daily limits available
And I see BPAY icon
And I see static text SMS code for your security
And I see Get SMS code button
When change in daily limit value
Then I see Get SMS code button
When I click on button
Then I see button is replaced with text box to enter the SMS code
And I see maximum length allowed is 6 digits
And I see static text appear below the text code "Code sent to <XX## ### XXX>. If the code is not received in a few minutes, <try again>"
And I see Save button in disabled state
When I entered 6 digit SMS code in text box
And I see Save button is enabled
When I click Save button with wrong SMS code
Then I see the error message SMS code not correct. Please try again with a new code Error Code below the "SMS code" text box.
When I click Save button with correct SMS code
Then I  navigated to Make a Payment page, showing message that BPAY daily limit has been updated successfully. Error message:  Err.IP-0294
			
			

Scenario: Make Payment Change daily BPAY limit during payment process -manual
Meta:
@categories payments Manual

Given I want to change daily BPAY limit while doing a payment to BPAY biller
When I clicked on "Change limit link" on Make a payment screen 
Then I see window with heading "Change daily payment limit for Pay Anyone payments"
And I see text "Change" in blue colour
And I see text "daily payment limit for" in Gray colour
And I see text "Pay Anyone payments" in black colour
And I see BPAY icon after "Amount" field
And I see static text "SMS code for your security" in bold
When I click on this button
Then the SMS is sent to registered mobile number
And I see mobile number is displayed as masked mobile number of the investor where the SMS code has been sent
And I see mobile number displayed as, first two and last three numbers, rest of the numbers is replaced by "#" eg. <XX## ### XXX>
And I see "Try again link" to resend SMS code
And I see  there is not restriction on clicking try again link