Narrative:Past transaction


!-- #######################################EMPTY PAST TRANSACTIONS####################################

Scenario: Verify empty state of past transaction screen for cash account
Meta:
@categories EmptyTransaction Automation

Given I login into panorama system as adviser
When I navigates to Past transaction page
Then I see page header Transactions
And I see tab All transactions
And I see tab Cash statement
And I see empty screen with message No records were found. Please check your criteria, try a different date range, or different filters if  number of records returned by Avaloq is zero 
And I see message No transactions have been made on this account for the period of time selected If no record has been returned by Avaloq for default state of 3 months 


Scenario: Verify focus on BT Cash Transactions tab and verify permission to access the screen - manual
Meta:
@categories EmptyTransaction Manual

When I navigates to Past transaction page
Then I see "Cash statement" in bold font
And I see Cash statement" tab in focus with underneath blue colour
And I see any user having permission "View account reports" will be able to access this screen


!-- ########################################### PAST TRANSACTIONS FOR CASH ACCOUNT ########################


Scenario: Verify past transactions screen for cash account
Meta:
@categories PastTransaction Automation

Given I login into panorama system as adviser
When I navigates to Past transaction page
Then I see page header Transactions
And I see tab All transactions
And I see tab Cash statement
And I see search option to select date range
And I see Date pickers as To and From
And I see Last update time in AEST
And I see rows in the table above the table in the format Showing <<range of rows>> of <<Total number of rows>>
And I see past transaction table header as Date, Description, Credit $, Debit $ and Balance $
And I see Disclaimer


Scenario: Verify past transaction records in the transaction list - manual
Meta:
@categories PastTransaction Manual

When I navigates to Past transaction page
Then I see transaction records on screen if the no. of records returned by avaloq are not zero
When I select date range and click on search button
Then I see transaction records from Avaloq based on "from" & "to" date
And I see message "No transactions have been made on this account for the period of time selected" if no record returned by Avaloq for the selected criteria
And I see standard technical error message if any error occurred while fetching records
And I see "Date" field in the Reverse chronological order and in format of "DD Mmm YYYY"
And I see system description up to 200 characters 
And I see user can enter description up to 18 characters
And I see "Uncleared" text below credit amount if the amount is still unclear 
And I see "Credit", "Debit" and "Balance" amount in the format of 2,000.00
And I see by default 50 transaction records can be displayed on screen
When I see no. of records are less than or equal to 50
Then I see no "See more" link
When I see no. of records greater than 50
Then I see "See more" link
When I click on "See more"
Then I see next 50 transaction records
And I see no.of rows getting upadated above the table in the format Showing <<range of rows>> of <<Total number of rows>>
And I see if there are no records
And I see no "See more" link
When I see while paging 1000 records have already been shown
Then I see message "Up to 1000 rows can be displayed, please change your filters or search criteria"
And I see any user having permission "View account reports" will be able to access this screen


Scenario: Verify date search validations of past transactions - manual
Meta:
@categories DateValidations Manual

When I navigates to Past transaction page
Then I see "Date" search field
And I see message "Please select a period of time no greater than three months" on screen
And I see default "To" date as current date and "From" date as current date minus 3 months
And I see date selection enabled for last 7 years to current date for both From & To date
When I tab out of "To" and "From" field
Then I see date changed in the format "DD Month YYYY"
When I enter incorrect date format 
Then I see error displayed on tab out below "From" and "To "date field as "Invalid date format"
When I select from date greater than "To" date
Then I see error message
When I filled "From" date 
Then I see "To" field auto populated with difference of 3 months 
When I filled "To" date 
Then I see "From" field auto populated with difference of 3 months 
When I filled current date in "From" date 
Then I see "To" date field populated with current date
When I try to enter "From" and "To" date as future date
And I see I cannot enter or select a date in the future for both "to" & "from" date


Scenario: Verify UI of the past transaction page for cash account - manual
Meta:
@categories PastTransactionUI Manual

When I navigates to Past transaction page
Then I see tab "All transactions" focus underneath blue colour and text "All transactions" in bold font
And I see Date icon to the left of from field
And I see search icon in the blue colour to the right of To field
And I see table headers in bold font
And I see "Uncleared" text in orange colour
When I see no. of records greater than 50
Then I see "See more" link	in blue colour
And I see down arrow underneath "See more" link
And I see "Disclaimer" as left aligned


!-- ################################## PAST TRANSACTIONS EXPANDED STATE ################################


Scenario: Verify past transaction expanded state and collapsing records
Meta:
@categories ExpandCollapseState Automation

Given I login into panorama system as adviser
When I click on arrow of the past transaction record to see expanded state
Then I see header Payment of along with amount if its debit transaction
And I see header Deposit of along with amount if its credit transaction
And I see fields as To, From, Date, Receipt no. and Download button
When I click on arrow of the transaction record again
Then I see detailed view will be closed


Scenario: Verify details and UI of the expanded state - manual
Meta:
@categories ExpandStateUI Manual

When I navigates to Past transaction page
Then I see transaction records on screen if the no. of records returned by avaloq are not zero
When I click on arrow of the past transaction record to see expanded state
Then I see expanded view of the transaction record centrally aligned
And I see text "Payment of" or "Deposit of" in green colour
And I see "From" field with details "Name of the debited account", "BSB number of the debited account", "Account number of the debited account"
And I see "To" field with details "Name of the credited account", "BSB number of the debited account", "Account number of the debited account"
And I see "From" and "To" field as an image with "To" filed in green colour
And I see "Date" when transaction was done in the format of "DD Mmm YYYY"
And I see "Receipt no." when transaction was done
And I see "Download" button text in bold font
When I mouse hover "Download" button
Then I see button get highlighted with blue colour
And I see any user having permission "View account reports" will be able to access this screen
When I click on arrow of the transaction record again
Then I see detailed view will be closed


!-- ###################################### PAST TRANSACTIONS PDF RECEIPT ###############################


Scenario: Verify PDF receipt of the past transaction records - manual
Meta:
@categories PDFreceipt Manual

When I click on arrow of the past transaction record to see expanded state
Then I see expanded view of the transaction record	
When I click on download button 
Then I see the receipt in PDF format or downloaded PDF on local machine 
And I see header "Payment of" along with amount if its debit transaction in green colour
And I see header "Deposit of" along with amount if its credit transaction in green colour
And I see fields as "From", "To", "Date", "Description" and "Receipt no" in bold font
And I see "From" field with details "Name of the debited account", "BSB number of the debited account", "Account number of the debited account"
And I see "To" field with details "Name of the credited account", "BSB number of the debited account", "Account number of the debited account"
And I see "From" and "To" field as an image with To filed in green colour
And I see "Date" when transaction was done in the format of "DD Month YYYY"
And I see "Description" enetered by user while doing transaction
And I see Fields "Date","Description" and "Receipt no." in bold font
And I see "Disclaimer" as left aligned
And I see any user having permission "View account reports" will be able to do this operation
	
		
!-- ################################### PAST TRANSACTION DOWNLOAD PDF AND CSV #############################


Scenario: Verify download button and its select options
Meta:
@categories DownloadPDFandCSV Automation

Given I login into panorama system as adviser
When I click on download button on the global header
Then I see option to select PDF	
And I see option to select CSV
	
	
Scenario: Verify PDF or CSV download functionality of past transaction -  manual
Meta:
@categories DownloadPDFandCSVFunctionality Manual

When I click on Download
And I click on PDF
Then I see records will be fetched based on the dates filled in "From" & "To" date fields
And I see all fetched records will be displayed in PDF in new tab or file will be downloaded based on local browser setting
When I click on Download
And click on CSV
Then I see records will be fetched based on the dates filled in "From" & "To" date fields
And I see all the fetched records will be displayed in CSV will be downloaded.