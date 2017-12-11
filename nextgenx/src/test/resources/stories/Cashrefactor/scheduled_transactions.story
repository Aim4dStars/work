Narrative: Scheduled Transaction

!--############################################ EMPTY STATE ##################
Scenario: Verify user wants to see empty state of scheduled transaction screen if no transaction has been scheduled till now
Meta:
@categories ScheduledTransaction Automation

Given I login into panorama system as adviser
When I navigate to Schedule transaction screen
Then I see Scheduled Transaction screen
And I see message Please note that scheduled transactions are executed daily at 5:00pm AEST


Scenario: Verify UI of user wants to see empty state of scheduled transaction screen if no transaction has been scheduled till now --Manual
Meta:
@categories ScheduledTransaction Manual

Given I login into panorama system as adviser
When I navigate to Schedule transaction screen
Then I see tab Scheduled transaction is highlighted with bluestrip in left
And I see static text Please note that scheduled transactions are executed daily at 5:00pm AEST in blue colour
And I see no transaction record table
And I see user having permission "View account reports" will be able to access and do operations


!-- ########################################### SCHEDULED TRANSACTION PAGE #####

Scenario: Verify Scheduled transactions screen he or she has done 
Meta:
@categories ScheduledTransaction Automation

Given I login into panorama system as adviser
When I navigate to Schedule transaction screen
Then I see Scheduled Transaction screen
And I see message Please note that scheduled transactions are executed daily at 5:00pm AEST
And I see number of records in format Showing 1-3 of 3
And I see table column Next due, Description, Repeats, Credit, Debit
!-- And I see See More link if records are more than 50


Scenario: Verify priority order of showing Scheduled transactions record --Manual
Meta:
@categories ScheduledTransaction Manual

Given I login into panorama system as adviser
When I navigate to Schedule transaction screen
Then I see Failed transaction as priority1, Failed & Retry transactions as priority2, Scheduled Transactions yet to be executed as priority3
And I see indicator showing transactions those have been failed & in retry mode or has already been failed. 
And I see Next due date in format "DDMMMYYYY"
And I see Description section which have two parts "description showing type of payment" and "user decription"
And I see in user description maximum upto 18 characters
And I see Repeat section with the frequency set by user while scheduling transactions eg. Quarterly, Fortnightly, Once 
And I see credit section with the amount not cleared then i see text "uncleared"
And I see credit amount in format "1,000.00"
And I see debit section when amount to be debited from cash account once transaction is completed
And I see debit amount in format "1,000.00"
And I see default 50 records
And I see "See More" link if records are more than "50"
And I see user having permission "View account reports" will be able to access and do operations
When I click on "See More" link
Then I see another 50 records 
And I see record count gets changed on header
And I see no "See More" link if records are not more than 50


!-- ########################################### SCHEDULED TRANSACTION EXPANDED STATE #######

Scenario: Verify user wants to see scheduled transaction expand state he or she has done
Meta:
@categories ScheduledTransaction Automation1

Given I login into panorama system as adviser
When I navigate to Schedule transaction screen
And I click on arrow of scheduled transaction record
Then I see detailed view of record
!-- And I see heading text for scheduled debit transaction text display as Payment of $2,000.00 scheduled.
!-- And I see heading text for scheduled credit transaction text display as Deposit of $2,000.00 scheduled.
And I see detailed record with Deposit of, scheduled, From, To, Date, Repeats, First payment, Last payment, Description, Receipt no., Stop schedule, download
When I click again on arrow of scheduled transaction
Then I see no detailed view of record when collapse


Scenario: Verify user wants to see detail of scheduled transaction expanded state he or she has done --Manual
Meta:
@categories ScheduledTransaction Manual

Given I login into panorama system as adviser
When I navigate to Schedule transaction screen
And I click on arrow of scheduled transaction record
Then I see detailed view of record
And I see multiple records can be expanded
And I see From section with detail of "Name of the debited account", "BSB number of the debited account" and "Account number of the debited account"
And I see To section wit detail of "Name of the Credited account", "BSB number of the debited account" and "Account number of the debited account"
And I see Date section with next due date of scheduled transaction
And I see Date format as "DDMMMYYYY"
And I see Repeat field with frequency "weekly, fortnight, Monthly, Quarterly or yearly" and end date field can have "end date, no end date, set number"
And I see First Payment section with date showing first transaction date, This date can be past date
And I see in first payment section if payment type is single schedule then date can be future date
And I see Last Payment section in case it is a single scheduled payment then display "-" as the last transaction is yet to happen.
And I see Description section with maximum 18 character user can enter
And I see Receipt no section with receipt no if transaction is type recursive and BLANK if first transaction is yet to come
And I see user having permission "View account reports" will be able to access and do operations


Scenario: Verify Scheduled transactions PDF download of receipt --Manual
Meta:
@categories ScheduledTransaction Manual

Given I login into panorama system as adviser
When I navigate to Schedule transaction screen
And I click on arrow of scheduled transaction record
And I click on download button
Then I see detailed view of transaction receipt in pdf format
And I see From section with detail of "Name of the debited account", "BSB number of the debited account" and "Account number of the debited account"
And I see To section wit detail of "Name of the Credited account", "BSB number of the debited account" and "Account number of the debited account"
And I see Date section with next due date of scheduled transaction
And I see Date format as "DDMMMYYYY"
And I see Repeat field with frequency "weekly, fortnight, Monthly, Quarterly or yearly" and end date field can have "end date, no end date, set number"
And I see First Payment section with date showing first transaction date, This date can be past date
And I see in first payment section if payment type is single schedule then date can be future date
And I see Last Payment section in case it is a single scheduled payment then display "-" as the last transaction is yet to happen.
And I see Description section with maximum 18 character
And I see Receipt no section with receipt no if transaction is type recursive and BLANK if first transaction is yet to come

!-- ########################################### STOP SCHEDULE ###########################

Scenario: Verify functionality of stops Schedule button
Meta:
@categories ScheduledTransaction Automation

Given I login into panorama system as adviser
When I navigate to Schedule transaction screen
And I click on arrow of scheduled transaction record
And I click on Stop schedule button
Then I see pop-up message with message text Stop schedule? Are you sure you want to stop this series of scheduled <payments/deposits>?
And I see button Yes
And I see No button link
And I see cross icon
When I click on cross icon of stops Schedule pop-up
Then I see stops Schedule pop-up get closed
When I click on No link of stops Schedule pop-up
Then I see stops Schedule pop-up get closed
When I click on button Yes
Then I see stops Schedule pop-up get closed


Scenario: Verify when all scheduled transactions are stopped --Manual
Meta:
@categories ScheduledTransaction Manual

Given I login into panorama system as adviser
When I navigate to Scheduled transactions page by clicking on Scheduled transaction tab on left panel
And I click on arrow of scheduled transaction record
And I click on Stop schedule button
And I click on button Yes
Then I see selected transaction gets removed from the list
And I see if no more transaction is scheduled then empty state is displayed


Scenario: Verify stop scheduled transactions button --Manual
Meta:
@categories ScheduledTransaction Manual

Given I login into panorama system as adviser
When I navigate to Schedule transaction screen
And I click on arrow of scheduled transaction record
Then I see Stop schedule button enables only to the user having permissions to "Make a payment - linked accounts" or "Make a BPAY/Pay Anyone Payment" or both.

!-- ########################################### FAILED SCHEDULE ############################

Scenario: Verify user wants to see scheduled transaction he or she has done for failed record --Manual
Meta:
@categories ScheduledTransaction Manual

Given I login into panorama system as adviser
When I navigate to Scheduled transactions page by clicking on Scheduled transaction tab on left panel
And I click on arrow of failed red indicator transaction record
Then I see static text "Payment of $1,000.00 fail" and text "failed" in red colour
And I see error message "There wasn’t enough money in this account when the transaction was attempted. This is the second attempt. This schedule will now be cancelled." in red colour
And I see Date section with next due date of recursive scheduled transaction in case if it has failed
And I see Date format as "DDMMMYYYY"
And I see From section with detail of "Name of the debited account", "BSB number of the debited account" and "Account number of the debited account"
And I see To section with detail of "Name of the Credited account", "BSB number of the debited account" and "Account number of the debited account"
And I see Repeat field with frequency "weekly, fortnight, Monthly, Quarterly or yearly" and end date field can have "end date, no end date, set number"
And I see First Payment section with date showing first transaction date, This date can be past date
And I see in First payment section if payment type is single schedule then date for which it had been scheduled should be displayed
And I see Last Payment section with Status of the last transaction, Failed or success date
And I see Description section with maximum 18 character
And I see Receipt no section with receipt no of failed transaction
And I see Stop schedule button enables only to the user having permissions to "Make a payment - linked accounts" or "Make a BPAY/Pay Anyone Payment" or both.
When I click on arrow of failed and retry orange indicator transaction record
Then I see static text "Payment of $1,000.00 failed and will retry" and text "failed and will retry" in amber colour
And I see error message "The transaction failed and will retry. Please make sure there are enough funds in the account to cover the next scheduled transaction." in amber colour
And I see Date section with next date when transaction will be retried
And I see Date format as "DDMMMYYYY"
And I see From section with detail of "Name of the debited account", "BSB number of the debited account" and "Account number of the debited account"
And I see To section with detail of "Name of the Credited account", "BSB number of the debited account" and "Account number of the debited account"
And I see Repeat field with frequency "weekly, fortnight, Monthly, Quarterly or yearly" and end date field can have "end date, no end date, set number"
And I see First Payment section with date showing first transaction date, This date can be past date
And I see in First payment section if payment type is single schedule then date can be future date
And I see Last Payment section with Status of the last transaction, Failed or success 
And I see Description section with maximum 18 character
And I see Receipt no section with receipt no with receipt no of failed transaction
And I see Stop schedule button enables only to the user having permissions to "Make a payment - linked accounts" or "Make a BPAY/Pay Anyone Payment" or both.


Scenario: Verify user wants to download failed scheduled transaction he or she has done in PDF format --Manual
Meta:
@categories ScheduledTransaction Manual

Given I login into panorama system as adviser
When I navigate to Scheduled transactions page by clicking on Scheduled transaction tab on left panel
And I click on arrow of failed red indicator transaction record
And I click on download button
Then I see detailed view of transaction receipt in pdf format
And I see static text "Payment of $1,000.00 fail" and text "failed" in red colour
And I see error message "There wasn’t enough money in this account when the transaction was attempted. This is the second attempt. This schedule will now be cancelled." in red colour
And I see Date section with next due date of recursive scheduled transaction in case if it has failed
And I see Date format as "DDMMMYYYY"
And I see From section with detail of "Name of the debited account", "BSB number of the debited account" and "Account number of the debited account"
And I see To section with detail of "Name of the Credited account", "BSB number of the debited account" and "Account number of the debited account"
And I see Repeat field with frequency "weekly, fortnight, Monthly, Quarterly or yearly" and end date date field can have "end date, no end date, set number"
And I see First Payment section with date showing first transaction date, This date can be past date
And I see in First payment section if payment type is single schedule then date for which it had been scheduled should be displayed
And I see Last Payment section with Status of the last transaction, Failed or success date
And I see Description section with maximum 18 character
And I see Receipt no section with receipt no of failed transaction
When I click on arrow of failed and retry orange indicator transaction record
And I see on download button
Then I see static text "Payment of $1000.00 failed and will retry" and text "failed and will retry" in amber colour
And I see error message "The transaction failed and will retry. Please make sure there are enough funds in the account to cover the next scheduled transaction." in amber colour
And I see Date section with next date when transaction will be retried
And I see Date format as "DDMMMYYYY"
And I see From section with detail of "Name of the debited account", "BSB number of the debited account" and "Account number of the debited account"
And I see To section with detail of "Name of the Credited account", "BSB number of the debited account" and "Account number of the debited account"
And I see Repeat field with frequency "weekly, fortnight, Monthly, Quarterly or yearly" and end date date field can have "end date, no end date, set number"
And I see First Payment section with date showing first transaction date, This date can be past date
And I see in First payment section if payment type is single schedule then date can be future date
And I see Last Payment section with Status of the last transaction, Failed or success 
And I see Description section with maximum 18 character
And I see Receipt no section with receipt no with receipt no of failed transaction