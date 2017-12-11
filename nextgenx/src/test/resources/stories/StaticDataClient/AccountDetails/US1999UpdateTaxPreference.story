Narrative: US1999:UpdateTaxPreference
In order to select the most appropriate tax method for an account
As an adviser, adviser assistant or client
I want to edit the tax preference of my account (client) or my client’s account (adviser)

Meta:
@userstory US1999UpdateTaxPreference


Scenario: Verify data preference for tax Initial State - Not Developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I account details link from the global navigation - Not developed
Then I am on Account Details and preferences screen
And I click on the edit icon on the tax preference field
And I see Tax preference radio buttons displaying the tax preference for the account
And I see default as Minimum gain/maximum loss
And I see apporoval checkbox deselected as default
And I see Update button as disabled
When I check the apporoval checkbox
Then I see enabled Update button
And I see Cancel Link and close button


Scenario: Update tax preference - Not Developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I account details link from the global navigation - Not developed
Then I am on Account Details and preferences screen
When I select a tax preference radio button
And I check the apporoval checkbox
And I see enabled Update Button


Scenario: Update tax preference -Permissions- Not Developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I account details link from the global navigation - Not developed
Then I am on Account Details and preferences screen
When I select a tax preference radio button
Then I donot see the apporoval checkbox
And I see enabled Update Button


Scenario: Update button - Not Developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I account details link from the global navigation - Not developed
Then I am on Account Details and preferences screen
When I select a tax preference radio button
And I check the apporoval checkbox
And I click on Update Button
Then I see updated value on the home screen

Scenario: Update button - Permissions - Not Developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I account details link from the global navigation - Not developed
Then I am on Account Details and preferences screen
When I select a tax preference radio button
Then I donot see the apporoval checkbox
When I click on Update Button
Then I see updated value on the home screen


Scenario: Cancel lightbox - Not Developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I account details link from the global navigation - Not developed
Then I am on Account Details and preferences screen
When I select a tax preference radio button
And I click on the Cancel link
Then I see the changes are discarded


Scenario: Close lightbox - Not Developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I account details link from the global navigation - Not developed
Then I am on Account Details and preferences screen
When I select a tax preference radio button
And I click on the Close icon
Then I see the changes are discarded