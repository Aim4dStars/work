Narrative: Make a deposit

!-- ################################################## Make a deposit ############################################################
        
Scenario: verify Make a deposit page
Meta:
@categories deposits Automation

Given I login into panorama system as adviser
When I click on Move money link on left panel
Then I see header Move money
And I see tab  Make a deposit
And I see tab  Make a payment
And I see tab  Accounts and billers
When I select Make a deposit tab
Then I see list box as Make deposit from, fields as Amount, Date populated with current date, description as optional field
And I see maximum 18 character can be entered in Optional Description field
And I see Repeat deposit check box, Next button
And I see text as Maximum 18 letters and numbers, under Description field
And I see clear all link

Scenario: verify empty tab out error messages on make a deposit page, click on next after filling all valid data
Meta:
@categories deposits Automation

Given I login into panorama system as adviser
When I click on Move money link on left panel
Then I see header Move money
When I click on Clear all link
Then I see all fields are empty under deposit tab
When I click on Next button without any data in field
Then I see error messages 'Please enter or select an account' under Make a deposit from field,'Please enter an amount' under Amount field, 'Deposit date is required.' under Date field
When I click on Next button with all valid data
Then I see model window with header Confirm and deposit

Scenario: verify deposit confirmation page
Meta:
@categories deposit Automation

Given I login into panorama system as adviser
When I click on Move money link on left panel
Then I see header Move money
When I click on Next button with all valid data
Then I see model window with header Confirm and deposit
And I see details as Account name, BSB number, Account number for 'from' on confirmation Page
And I see details as Account name, BSB number, Account number for 'To' on confirmation Page
And I see date, repeat frequency, repeat date, Pay button, change button

Scenario: verify functionality of 'Make a deposit from' field on 'Make a deposit' page
Meta:
@categories deposits Manual

Given I login into panorama system to Make a deposit
When I push Move money link on left panel
Then I see tab "Make a payment" tab by default selected, in bold,underlined by blue line
When I select "Make a deposit" tab
Then I see tab "Make a deposit" tab by default selected, in bold,underlined by blue line
And I see list box "Make deposit from"
And I see list box contains list of all Linked accounts investor has already added in his address book
When I click the list box "Make deposit from"
Then I see list of all Linked account is displayed
And I can type into the box to search for the required Linked account
And I see records are filtered and displayed in the list based on entered characters after searching for the input text in field account name, account number, and nick name
And I see a cross icon is appear in edit mode
When I click on cross icon
Then Input text is clear so as all the filters
When I clicked on the arrow - button  within list box
Then I see complete list of Linked account is displayed
And I see field is in non editable mode
And I see for linked account: name of the linked account, BSB code and account number
And I see primary linked account is identified by an icon
And I see for Pay anyone: Name of the Pay anyone, BSB and account number
And I see records are shown as per priority order as Primary Linked accounts on top sorted in alphabetic order on account name or nick name
When account is Make a deposit - linked account
Then I can make deposit from linked accounts only
When I have permissions of 'Make payments- linked accounts'
Then I see only linked accounts are listed down in "Make a deposit" list box
When blocked in Avaloq
Then I see screen is Gray out 
And I see message is displayed on top of the screen as Account has been blocked. Please contact XXXXX
And I see error message
When a record is selected 
Then I see information is displayed in text box
When account is Linked
Then I see nick name if its there, BSB code and account number
When no account is selected 
Then on tab out error message is displayed as 'Please enter or select an account' below the field

Scenario: verify functionality of 'Date' field on 'Make a deposit' page
Meta:
@categories deposits Manual

Given I login into panorama system to Make a deposit
When I push Move money link on left panel and click on 'Make a deposit' tab
And I see Date field with Date picker or manual input of date
When I click on date picker
Then I see past dates are not allowed in this field, disable selection of past date
And I see current or future date can be selected or entered
When current time is later than 5:00pm [Sydney time]
Then I am unable select current date, current date is disable from date picker
When I entered current date manually
Then Error message is displayed as 'Payment can't be made for current date after 5:00PM'
When I enter date manually in allowed format
Then I see change in date format to DD Mmm YYYY
When I enter in correct date format
Then I see error is displayed on tab out below date fields as 'Please select a current or past date'

Scenario: verify functionality of ' Repeats Every' check box on 'Make a deposit' page
Meta:
@categories deposits Manual

Given I login into panorama system to Make a deposit
When I push Move money link on left panel and click on 'Make a deposit' tab
And I see Repeat deposit check box
And I see this is used to define the recursive scheduled payments
And I see by default its state is "unchecked"
When I checked this check box
Then I see two more fields are appear "Repeat every" & "End repeats" with "month" & "No end date" auto populated
When I 	Un-check the check box
Then I see "Repeat every" & "End repeats" disappear
And I see "Repeat every" list box have pre defined values weekly, fortnight, Monthly, Quarterly or yearly which defines the frequency of the recursive scheduled payments
And I see "End repeat" list box have pre defined values "End date", "No end date", "set number" which defines end date of the recursive scheduled payments


Scenario: verify functionality of Repeat payment 'End Date' field on 'Make a deposit' page
Meta:
@categories deposits Manual

Given I login into panorama system to Make a deposit
When I push Move money link on left panel and click on 'Make a deposit' tab
When I select "End date"
Then I see date picker is appear, based on the value selected for "repeat every" field
And I see dates is available to select through date picker
And I see Other dates are disable, can not select other dates
When I input other than allowed date manually 
Then I see it display the error message as 'The date you've entered doesn't match the frequency you've selected. Please select again'
When I select Weekly
Then I see allowed dates is calculated by adding 7 days to selected date
When I select Fortnight
Then I see allowed dates is calculated by adding 14 days to selected start date
When I select Monthly
Then I see allowed dates is calculated by adding 1 month to selected date
When I select Quarterly
Then I see allowed dates is calculated by adding 3 months to selected date
When I select Yearly
Then I see allowed dates is calculated by adding 1 year to selected date


Scenario: verify functionality of Repeat payment 'No End Date' and 'Set number' field on 'Make a deposit' page
Meta:
@categories deposits Manual

Given I login into panorama system to Make a deposit
When I push Move money link on left panel and click on 'Make a deposit' tab
When I select "No end date"
Then I see no end to the recursive scheduled deposit
When I select "Set number"
Then I see an editable field is appear to enter the number of transactions I want
And I see maximum number allowed for this field is 999
When any non number character is entered
Then I see on  tab out it display an error 'Please select a number'
And I see based on the number entered in "set number" field, end date of the recursive scheduled deposit is displayed


Scenario: verify  'Description', 'Next', 'Clear all' on 'Make a deposit' page
Meta:
@categories deposits Manual

Given I login into panorama system to Make a deposit
When I push Move money link on left panel and click on 'Make a deposit' tab
And I see description text field as an optional field
And I see description maximum of 18 characters can entered
And I see only letters & numbers are allowed in this text box
And I see below the field a help text is displayed “Maximum 18 letters and numbers.
And I see "Next" button in blue background colour
When I click on next button
Then I see it opens the deposit confirmation modal window 
And I see field level validations is executed before submitting the request
And I see error messages, if any is shown below field
And I see "Clear all" link
When I click on the link
Then I see it clear all filled date and bring the screen to default state
		
!-- ################################################## Make a deposit confirmation ############################################################
        

Scenario: verify Make a deposit confirmation page
Meta:
@categories deposits Manual

Given I wants to make a deposit from Linked accounts
When I push "Next" on make deposit screen after filling mandatory fields
Then I see modal screen with heading Confirm and deposit
And I see amount <000,000.00> same as filled in form
When I select no end date in form
Then I see End date field shows text no end date
When I select end date in form
Then I see end date when recursive transaction ends with prefix ends on
Then I see it navigates to Make Deposit Successful Submit
When the deposit not made successfully
Then I see modal window is closed and error is displayed on make deposit screen
And I see disclaimer

!-- ################################################## Make Deposit Successful Submit ############################################################


Scenario: Verify Make Deposit Successful Submit screen
Meta:
@categories depositsuccessfulsubmit Automation

When I click on Deposit button on deposit confirm modal window
Then I see page get navigated to the deposit successfully submit screen
And I see message Deposit of <Amount> submitted successfully
And I see text Date as a Transaction start date for deposit screen
And I see text Repeats for deposit screen
And I see text Description as entered by user while doing deposit
And I see text Receipt no. with number returned by Avaloq for deposit screen
And I see Download button to download the PDF receipt for deposit screen
And I see link See all transaction in deposit screen
When I click on See all transaction in deposit screen
Then I see page gets navigated to Past transaction screen from deposit screen
And I see link Make another deposit
When I click on Make another deposit
Then I see page gets navigated to Make deposit screen
And I see link See scheduled payment in deposit screen
When I click on See scheduled payment in deposit screen
Then I see page gets navigated to Scheduled transaction screen from deposit screen

Scenario: Verify UI of Make Deposit Successful Submit
Meta:
@categories depositsuccessfulsubmit Manual

When I click on Deposit button on deposit confirm modal window
Then I see page get navigated to the deposit successfully submit screen
And I see "Deposit of" text in grey colour, <<amount>> in bold and "submitted successfully" in green colour
And I see "From" and "To" in picture format with "To" encircled in green colour
And I see "From" details of debited account as "Name of linked account", "BSB number of linked account" and "Account number of linked account"
And I see "To" details of credited account as "Name of the cash account", "BSB number of the cash account" and "Account number of the cash account"
And I see "Name of linked account" and "Name of the cash account" in bold font
And I see date in the format of "DD MMM YYYY"
And I see "Repeat" field displaying frequency and end date of scheduled transaction selected while scheduling transaction for make deposit screen
And I see Frequency field has values weekly, fortnight, Monthly, Quarterly or yearly
And I see End date field showing "No end date" if user has mentioned no end date
And I see End date field showing end date with prefix "ends on" when recursive transaction end
And I see text "Date", "Repeats", "Description", "Receipt no."  and "Download" in bold font
And I see "Downalod" button with icon
And I see links "See all transaction", "Make another deposit" and "See scheduled payment" with blue link icon to the left
		
		
!-- ################################################## Make Deposit Receipt download  PDF ############################################################
        

Scenario: Make Deposit Receipt download PDF
Meta:
@categories deposits Manual

Given I wants to download the deposit receipt in PDF format after making deposit successfully to listed accounts
When I push download button on Make a deposit Successful submit screen
Then I see it opens the receipt in PDF format or download the PDF on local machine
When setting is to open the PDF 
Then I see PDF opened in new tab
And I see message Deposit of <000,000.00> submitted successfully
And I see From details of the debited account
And I see name of the linked account
And I see BSB number of the linked account
And I see account number of the linked account
And I see To details of the credited account
And I see name of cash account
And I see BSB number of cash account
And I see account number cash account
And I see Date
And I see date of transaction as start date mentioned by user while making this deposit on make deposit screen in DD Mmm YYY format
And I see Repeats
And I see it shows the frequency & end date of the scheduled transaction selected while scheduling deposit from make deposit screen
And I see frequency field have values weekly, fortnight, monthly, quarterly, or yearly
When I select no end date in form
Then I see End date field shows text no end date
When I select end date in form
Then I see end date when recursive transaction ends with prefix ends on
And I see Description
And I see description entered by user while doing deposit, of 18 characters or numbers
And I see receipt number returned from Avaloq