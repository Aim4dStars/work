Narrative: Accounts and billers for Investor 

!-- #########################View account and biller screen ##############

Scenario: Verify general access deny page while accessing account and biller home page with a user not having 'View account reports' permission
Meta:
@categories Automation

Given I successfully login into panorama system as Investor
And I DO NOT have required View account reports permission to access 'accounts and billers' tab
When I go to the Move money section
Then I do not see accounts and billers tab
When I try to access the accounts and biller tab through a previously saved bookmark
Then I see general access deny page


Scenario: Verify account and biller home page standard message and collapse/expand toggle button
Meta:
@categories Automation

Given I successfully login into panorama system as Investor
And I have required View account reports permissions to access 'accounts and billers' tab
When I click on tab Accounts and billers in the Move money section
Then I see standard message on top: Linked accounts can be used to transfer money to and from your Panorama account. Pay anyone accounts can be used by authorised users to make payments to others
And I see a toggle link to expand all sections [Linked accounts, Pay Anyone accounts, and BPay biller] on the screen at the same time
When I click on the expand toggle link
Then I see all the three sections [Linked accounts, Pay Anyone accounts, and BPay biller] has been expanded
And the toggle link icon is change to collapse mode
When I click on the collapse toggle link
Then I see all the three sections [Linked accounts, Pay Anyone accounts, and BPay biller] has been collapsed again


Scenario: Verify functioning of expand/collapse links for all the three sections [Linked accounts, Pay Anyone accounts, and BPay biller]
Meta:
@categories Automation

Given I successfully login into panorama system as Investor
And I have required View account reports permissions to access 'accounts and billers' tab
When I click on tab Accounts and billers in the Move money section
Then I see expand right-arrow icon in front of each section [Linked accounts, Pay Anyone accounts, and BPay biller]
When I click on the expand right-arrow icon for all the three sections one by one
Then I see the sections are expanded and the expand right-arrow icon is changed to collapse down-arrow icon for all three sections
When I click on the collapse down-arrow icon for all the three sections one by one
Then I see the sections are collapsed again and the collapse down-arrow icon is changed to expand right-arrow icon for all three sections


Scenario: Verify static headings of each section and availability of modal links for users with Update payees & payment limits permission
Meta:
@categories Automation

Given I successfully login into panorama system as Investor
And I have required View account reports permission to access 'accounts and billers' tab and Update payees & payment limits permission
When I click on tab Accounts and billers in the Move money section
Then I see three sections with static heading Linked account, Pay anyone account and BPay biller
And I see add linked account link against linked account section
And I see change daily limit and add account link against Pay anyone account section
And I see change daily limit and add biller link against BPay biller section


Scenario: Verify availability of menu button users not having any permissions [Update payees & payment limits, Make a payment or deposit - linked accounts, Make a BPAY/Pay Anyone Payment]

Meta:
@categories manual

Given I successfully login into panorama system as Investor
And I have required View account reports permission to access 'accounts and billers' tab and Update payees & payment limits permission
When I click on tab Accounts and billers in the Move money section
When I click on the expand all toggle link
Then I do not see any menu button against any of the added accounts for all the three linked account, pay anyone account and Bpay billers section


!-- ####################Linked account section#########################


Scenario: Verifying 'Linked account' section for user having 'Update payees & payment limits' permission
Meta:
@categories Manual

Given I successfully login into panorama system as Investor
And I have required View account reports permission to access 'accounts and billers' tab and Update payees & payment limits permission
When I click on tab Accounts and billers in the Move money section
And I click on expander icon for linked account section
Then I see static heading Account details and Account nickname
And I see details of the added accounts such as account name of the linked account below the Account details heading
And I see BSB number of the added account separated by hyphen after 3 numbers below the Account details heading
And I see account number of the added account below the Account details heading
And I see linked account icon close to below the Account nickname heading
And I see account nickname of the added account if provided by user under heading Account nickname
And I see menu button against each of the added accounts


Scenario: Verifying 'Primary linked account' for linked account section
Meta:
@categories Manual

Given I successfully login into panorama system as Investor
And I have required View account reports permission to access 'accounts and billers' tab 
When I click on tab Accounts and billers in the Move money section
And I click on expander icon for linked account section
Then I see details of the primary linked account under heading account details and account nickname
And I see primary linked account icon displayed just before the name of the primary linked account
And I see there is only one primary linked account in the linked account section and primary linked account is on top
And I see all the other linked accounts are sorted on the basis of ascending order of their account name
When I hover my mouse on the primary linked account icon
Then I see a callout help box with text Primary Linked account
When I remove my mouse from the primary linked account icon 
Then the callout help text disappears
When I hover my mouse on the left and right arrow of the linked account icon one by one
Then I see an alt tag Money in and Money out displayed respectively


Scenario: Verifying 'Account nickname' field for linked account section for user having Update payees & payment limits permission
Meta:
@categories Automation

Given I successfully login into panorama system as Investor
And I have required View account reports permission to access 'accounts and billers' tab and Update payees & payment limits permission
When I click on tab Accounts and billers in the Move money section
And I click on expander icon for linked account section
Then I see Account nickname of an added linked account is displayed in editable text box
And I see Save and Cancel button displayed below the corresponding record after I click on the nickname field
When I click on Save button after entering a new nickname
Then I see the new nickname is being displayed in the editable text box and Save and Cancel button disappears
When I click on cancel button after entering a new nickname
Then I see there is no change in the original nickname and Save and Cancel button disappears


Scenario: Verifying 'Account nickname' field for linked account section for user NOT having Update payees & payment limits permission
Meta:
@categories Automation

Given I successfully login into panorama system as Investor
And I have required View account reports permission to access 'accounts and billers' tab and DO NOT Update payees & payment limits permission
When I click on tab Accounts and billers in the Move money section
And I click on expander icon for linked account section
Then I see Account nickname of an added linked account is displayed in read only and non-editable text box


Scenario: Verifying Menu button for accounts added in linked account section for user having Make a payment - linked accounts permission
Meta:
@categories Automation

Given I successfully login into panorama system as Investor
And I have required View account reports permission to access 'accounts and billers' tab and Make a payment - linked accounts permission
When I click on tab Accounts and billers in the Move money section
And I click on expander icon for linked account section
And I click on menu button for any of the added accounts
Then I see two options in the dropdown 1.Make a payment 2. Make a deposit
When I select Make a payment option from the dropdown
Then I am navigated to Make a payment screen with selected linked account details pre-filled in the payment form
When I select Make a deposit option from the dropdown
Then I am navigated to Make a deposit screen with selected linked account details pre-filled in the payment form


Scenario: Verifying Menu button for accounts added in linked account section for user having Update payees & payment limits permission
Meta:
@categories Automation

Given I successfully login into panorama system as Investor
And I have required View account reports permission to access 'accounts and billers' tab and Update payees & payment limits permission
When I click on tab Accounts and billers in the Move money section
And I click on expander icon for linked account section
And I click on menu button for any of the added accounts other than primary account
Then I see two options in the dropdown 1.Set as primary account 2. Remove


Scenario: Verifying Menu options for accounts added in linked account section for user having Update payees & payment limits permission
Meta:
@categories manual

Given I successfully login into panorama system as Investor
And I have required View account reports permission to access 'accounts and billers' tab and Update payees & payment limits permission
When I click on tab Accounts and billers in the Move money section
And I click on expander icon for linked account section
And I click on menu button for any of the non primary added accounts
Then I see two options in the dropdown 1.Set as primary account 2. Remove
And I see the Set as primary account and Remove option is only available to non primary linked accounts
When I click the option Set as primary account for a selected non primary linked account
Then I see the selected account becomes the primary linked account and is displayed at the top of the list of linked accounts
And I see primary linked account icon starts appearing against the new primary linked account
When I click the option Remove for a selected non primary linked account
Then I see a confirmation modal displayed with text Remove from list? and buttons Yes, No and X
When I select the Yes button in the modal
Then I am redirected to accounts and billers page with selected record removed from the linked account list
And I see a confirmation message The account has been successfully removed
When I select either No or X button 
Then I am redirected to accounts and billers page with no changes to the linked account list


Scenario: Verifying Menu options for accounts added in linked account section for user having both Update payees & payment limits and Make a payment - linked accounts permission
Meta:
@categories manual

Given I successfully login into panorama system as Investor
And I have required View account reports permission to access 'accounts and billers' tab and Update payees & payment limits and Make a payment - linked accounts permission
When I click on tab Accounts and billers in the Move money section
And I click on expander icon for linked account section
And I click on menu button for any of the non primary added accounts
Then I see four options in the dropdown 1.Make a payment 2. Make a deposit 3.Set as primary account 4. Remove


!-- #########################Pay anyone accounts section#######################################

Scenario: Verifying 'Pay anyone account' section for user having 'Update payees & payment limits' permission
Meta:
@categories Manual


Given I successfully login into panorama system as Investor
And I have required View account reports permission to access 'accounts and billers' tab and Update payees & payment limits permission
When I click on tab Accounts and billers in the Move money section
And I click on expander icon for Pay anyone account section
Then I see static text The daily limit for pay anyone is $ <XX, XXX> to show the current Pay anyone daily limit
Then I see static heading Account details and Account nickname
And I see added accounts are sorted in ascending order of account name field.
And I see details of the added accounts such as account name of the Pay anyone account below the Account details heading
And I see BSB number of the added account separated by hyphen after 3 numbers below the Account details heading
And I see account number of the added account below the Account details heading
And I see pay anyone account icon close to below the Account nickname heading
And I see account nickname of the added account if provided by user under heading Account nickname
And I see menu button against each of the added accounts
When I hover my mouse on the right arrow in the Pay anyone accounts section
Then I see an alt tag Money out displayed 


Scenario: Verifying 'Account nickname' field for Pay anyone section for user having Update payees & payment limits permission
Meta:
@categories Automation

Given I successfully login into panorama system as Investor
And I have required View account reports permission to access 'accounts and billers' tab and Update payees & payment limits permission
When I click on tab Accounts and billers in the Move money section
And I click on expander icon for Pay anyone account section
Then I see Account nickname of an added pay anyone account is displayed in editable text box
And I see Save and Cancel button displayed below the corresponding pay anyone account record after I click on the nickname field
When I click on Save button after entering a new nickname below the pay anyone account
Then I see the new nickname is being displayed in the editable text box and Save and Cancel button disappears for pay anyone account
When I click on cancel button after entering a new nickname for pay anyone account
Then I see there is no change in the original nickname and Save and Cancel button disappears below the pay anyone account


Scenario: Verifying 'Account nickname' field for Pay anyone account section for user NOT having Update payees & payment limits permission
Meta:
@categories Automation

Given I successfully login into panorama system as Investor
And I have required View account reports permission to access 'accounts and billers' tab and DO NOT Update payees & payment limits permission
When I click on tab Accounts and billers in the Move money section
And I click on expander icon for Pay anyone account section
Then I see Account nickname of an added Pay anyone account is displayed in read only and non-editable text box


Scenario: Verifying Menu button for accounts added in Pay anyone account section for user having Make a BPAY/Pay Anyone Payment permission
Meta:
@categories Automation

Given I successfully login into panorama system as Investor
And I have required View account reports permission to access 'accounts and billers' tab and Make a BPAY/Pay Anyone Payment permission
When I click on tab Accounts and billers in the Move money section
And I click on expander icon for Pay anyone account section
And I click on menu button for any of the added pay anyone accounts
Then I see one option in the dropdown 1.Make a payment for pay anyone account
When I select Make a payment option from the dropdown for pay anyone account
Then I am navigated to Make a payment screen with selected pay anyone account details pre-filled in the payment form


Scenario: Verifying Menu button for accounts added in Pay anyone account section for user having Update payees & payment limits permission
Meta:
@categories Manual

Given I successfully login into panorama system as Investor
And I have required View account reports permission to access 'accounts and billers' tab and Update payees & payment limits permission
When I click on tab Accounts and billers in the Move money section
And I click on expander icon for Pay anyone account section
And I click on menu button for any of the added accounts
Then I see one option in the dropdown 1.Remove
When I click the option Remove for a selected account
Then I see a confirmation modal displayed with text Remove from list? and buttons Yes, No and X
When I select the Yes button in the modal
Then I am redirected to accounts and billers page with selected record removed from the account list
And I see a confirmation message The account has been successfully removed
When I select either No or X button 
Then I am redirected to accounts and billers page with no changes to the account list


Scenario: Verifying Menu options for accounts added in Pay anyone account section for user having both Update payees & payment limits and Make a BPAY/Pay Anyone Payment permission
Meta:
@categories Automation


Given I successfully login into panorama system as Investor
And I have required View account reports permission to access 'accounts and billers' tab and Update payees & payment limits and Make a BPAY/Pay Anyone Payment permission
When I click on tab Accounts and billers in the Move money section
And I click on expander icon for Pay anyone account section
And I click on menu button for any of the added pay anyone accounts
Then I see two options in the dropdown 1.Make a payment 2.Remove for pay anyone account


Scenario: Verifying Empty state message in Pay anyone account section 
Meta:
@categories Manual


Given I successfully login into panorama system as Investor
And I have required View account reports permission to access 'accounts and billers' tab 
When I click on tab Accounts and billers in the Move money section
And I click on expander icon for Pay anyone account section
And I remove all the accounts added in the section
Then I see message No Pay anyone account added


!-- ########################BPay billers section#################################

Scenario: Verifying 'BPay billers' section for user having 'Update payees & payment limits' permission
Meta:
@categories Manual


Given I successfully login into panorama system as Investor
And I have required View account reports permission to access 'accounts and billers' tab and Update payees & payment limits permission
When I click on tab Accounts and billers in the Move money section
And I click on expander icon for BPay billers section
Then I see static text The daily limit for BPay biller is $ <XX, XXX> to show the current biller daily limit
Then I see static heading Biller details and Biller nickname
And I see added accounts are sorted in ascending order of biller name field.
And I see details of the added accounts such as account name of the Bpay account below the biller details heading
And I see BPay icon against the added BPay account
And I see biller code of the added account separated by hyphen after 3 numbers below the Account details heading
And I see CRN number of the added account below the biller details heading
And I see in case of iCRN and vCRN the CRN number is blank
And I see linked account icon close to below the biller nickname heading
And I see biller nickname of the added account if provided by user under heading biller nickname
And I see menu button against each of the added accounts


Scenario: Verifying 'biller nickname' field for BPay billers section for user having Update payees & payment limits permission
Meta:
@categories Automation

Given I successfully login into panorama system as Investor
And I have required View account reports permission to access 'accounts and billers' tab and Update payees & payment limits permission
When I click on tab Accounts and billers in the Move money section
And I click on expander icon for Bpay billers section
Then I see biller nickname of an added account is displayed in editable text box
And I see Save and Cancel button displayed below the corresponding record for BPay biller after I click on the nickname field
When I click on Save button after entering a new nickname for BPay biller
Then I see the new nickname is being displayed in the editable text box and Save and Cancel button disappears for BPay biller
When I click on cancel button after entering a new nickname for BPay biller
Then I see there is no change in the original nickname and Save and Cancel button disappears for BPay biller


Scenario: Verifying 'biller nickname' field for BPay billers section for user NOT having Update payees & payment limits permission
Meta:
@categories Automation

Given I successfully login into panorama system as Investor
And I have required View account reports permission to access 'accounts and billers' tab and DO NOT Update payees & payment limits permission
When I click on tab Accounts and billers in the Move money section
And I click on expander icon for Bpay billers section
Then I see biller nickname of an added account is displayed in read only and non-editable text box



Scenario: Verifying Menu button for accounts added in BPay billers section for user having Make a BPAY/Pay Anyone Payment permission
Meta:
@categories Automation


Given I successfully login into panorama system as Investor
And I have required View account reports permission to access 'accounts and billers' tab and Make a BPAY/Pay Anyone Payment permission
When I click on tab Accounts and billers in the Move money section
And I click on expander icon for Bpay billers section
And I click on menu button for any of the added BPay accounts
Then I see one option in the dropdown 1.Make a payment for BPay Billers
When I select Make a payment option from the dropdown for BPay Billers
Then I am navigated to Make a payment screen with selected biller account details pre-filled in the payment form


Scenario: Verifying Menu button for accounts added in BPay billers section for user having Update payees & payment limits permission
Meta:
@categories Manual


Given I successfully login into panorama system as Investor
And I have required View account reports permission to access 'accounts and billers' tab and Update payees & payment limits permission
When I click on tab Accounts and billers in the Move money section
And I click on expander icon for BPay billers section
And I click on menu button for any of the added BPay accounts
Then I see one option in the dropdown 1.Remove
When I click the option Remove for a selected account
Then I see a confirmation modal displayed with text Remove from list? and buttons Yes, No and X
When I select the Yes button in the modal
Then I am redirected to accounts and billers page with selected record removed from the account list
And I see a confirmation message The account has been successfully removed
When I select either No or X button 
Then I am redirected to accounts and billers page with no changes to the account list


Scenario: Verifying Menu options for accounts added in BPay billers section for user having both Update payees & payment limits and Make a BPAY/Pay Anyone Payment permission
Meta:
@categories Automation


Given I successfully login into panorama system as Investor
And I have required View account reports permission to access 'accounts and billers' tab and Update payees & payment limits and Make a BPAY/Pay Anyone Payment permission
When I click on tab Accounts and billers in the Move money section
And I click on expander icon for Bpay billers section
And I click on menu button for any of the added BPay accounts
Then I see two options in the dropdown 1.Make a payment 2.Remove for Bpay biller account


Scenario: Verifying Empty state message in BPay billers section 
Meta:
@categories Manual


Given I successfully login into panorama system as Investor
And I have required View account reports permission to access 'accounts and billers' tab 
When I click on tab Accounts and billers in the Move money section
And I click on expander icon for BPay billers section
And I remove all the accounts added in the section
Then I see message No BPay biller added

!-- Accounts and billers for Adviser 
!-- #########################View account and biller screen ##############

Scenario: Verify Unavailability of modal links for an Adviser user
Meta:
@categories Manual

Given I successfully login into panorama system as Adviser
And I have required View account reports permission to access 'accounts and billers' tab
When I click on tab Accounts and billers in the Move money section
Then I see all the three Linked accounts, Pay Anyone accounts, and BPay biller sections
And I DO NOT see add linked account link against Linked account section
And I DO NOT see change daily limit and add account link against Pay anyone account section
And I DO NOT see change daily limit and add biller link against BPay billers section
