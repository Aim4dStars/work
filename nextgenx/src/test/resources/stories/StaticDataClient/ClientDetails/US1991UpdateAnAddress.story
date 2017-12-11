Narrative: US1994:Update an address - Verify 'an address'
In order to ensure the addresses are up to date for a client 
As an adviser or adviser assistant
I want to edit an address of my client

Meta:
@userstory US1991UpdateAnAddress

Scenario:Initial State Update An Address Ind
Meta:
@categories miniregression fullregression 
Given I am on Client details and preferences
When I click on edit icon for address
Then I am on Edit address screen
And I see Edit address details header and Client’s name in the subheading  
And I see Same as checkbox and drop down address list field displayed and disabled
And I see the client’s address details populated
And I see checkbox with a label I have authority to make these changes on behalf of my client
And I see Update button Update unchecked and disabled


Scenario:Update An Address Ind Change an Australian address
Meta:
@categories miniregression fullregression
Given I am on Client details and preferences
When I click on edit icon for address
Then I am on Edit address screen
And I select country India in dropdown for country
And I see Address line 1 Address line 2 Country Suburb Postcode
When I type valid address in address line 1
And I type valid address in address line 2
And I type valid Suburb and Postcode
And I click on checkbox
And I click on Update Button
Then I see Updated Address
 

Scenario:Update An Address Ind Change an International address
Meta:
@categories miniregression fullregression
Given I am on Client details and preferences
When I click on edit icon for address
Then I am on Edit address screen
And I select country India in dropdown for country
And I see Address line 1 Address line 2 Country City Postcode State/province/region
When I type valid address in address line 1
And I type valid address in address line 2
And I type valid City Postcode and State/province/region
And I click on checkbox
And I click on Update Button
Then I see Updated Address
 

Scenario:Update An Address Ind Switch from an Australian address to an international address
Meta:
@categories miniregression fullregression
Given I am on Client details and preferences
When I click on edit icon for address
Then I am on Edit address screen
And I select country India in dropdown for country
And I see Address line 1 Address line 2 Country City Postcode State/province/region
When I type valid address in address line 1
And I type valid address in address line 2
And I type valid City Postcode and State/province/region
And I click on checkbox
And I click on Update Button
Then I see Updated Address


Scenario:Update An Address Ind Switch from an international address to an Australian address
Meta:
@categories fullregression
Given I am on Client Details Individual page
When I click on edit icon for address
Then I am on Edit address screen
And I select country Australia in dropdown for country
And I see Address line 1 Address line 2 Country City Postcode State/province/region
When I type valid address in address line 1
And I type valid address in address line 2
And I type valid Suburb
And I type valid City Postcode
And I click on Australian State Dropdown Icon 
And I select ACT state
And I click on checkbox
And I click on Update Button
Then I see Updated Address


Scenario:Update An Address Ind selecting from existing addresses Same As
Meta:
@categories miniregression fullregression
Given I am on Client Details Individual page
When I click on edit icon for address
Then I am on Edit address screen
And I select the ‘same as’ checkbox
Then I see address fields collapsed and address list drop down enabled
When I select Changing Residential address
Then I see Postal address PO Box addresses is not displayed
When I select Changing Postal address
Then I see Residentail address PO Box addresses

Scenario: Approval Checkbox Ind Update An Address
Meta:
@categories miniregression fullregression
Given I am on Client Details Individual page
When I click on edit icon for address
Then I am on Edit address screen
Then I see disabled updated button and deselected checkbox for Ind address
When I check the approval checkbox for Ind address
Then I see enabled update button for Ind address


Scenario: Error Scenario Ind Update An Address Update Button
Meta:
@categories fullregression
Given I am on Client Details Individual page
When I click on edit icon for address
Then I am on Edit address screen
And I select country India in dropdown for country
And I see Address line 1 Address line 2 Country Suburb Postcode
When I type invalid address in address line 1
And I type invalid address in address line 2
And I type invalid Suburb and Postcode
And I click on checkbox
And I click on Update Button
Then I see error message ERR.00096
 

Scenario: Failed address validation – initial state Ind Update An Address
Meta:
@categories fullregression
Given I am on Client Details Individual page
When I click on edit icon for address
Then I am on Edit address screen
And I select country Australia in dropdown for country
And I see Address line 1 Address line 2 Country City Postcode State/province/region
When I type invalid address in address line 1
And I type invalid address in address line 2
And I type invalid Suburb
And I type invalid City Postcode
And I click on Australian State Dropdown Icon 
And I select ACT state
And I click on checkbox
And I click on Update Button
Then I see Error message that displays entered address
And I see Unit Street type Street name Suburb State fields empty
When I click on Update Button
Then I see Valid Error messages for all the mandatory fields
When I enter values for Street name Suburb State fields
And I click on Update Button
Then I see Updated Address

Scenario: Cancel Button for Ind Update An Address
Meta:
@categories miniregression fullregression
Given I am on Client Details Individual page
When I click on edit icon for address
Then I am on Edit address screen
And I click on cancel button for address
Then I see no changes in value for address


Scenario: Close Button for Ind Update An Address
Meta:
@categories miniregression fullregression
Given I am on Client Details Individual page
When I click on edit icon for address
Then I am on Edit address screen
And I click on close button for address
Then I see no changes in value for address


Scenario: Avaloq Integration Trust
Meta:
@categories fullregression
Given I am on Client Details Individual page
When I click on edit icon for address
Then I am on Edit address screen Avaloq