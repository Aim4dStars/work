Narrative: US2000:UpdatePrimaryAccountHolder
In order to select the most appropriate person to be the primary account holder for an account
As an adviser or adviser assistant
I want to edit the primary account holder of a client account


Meta:
@userstory US2000UpdatePrimaryAccountHolder


Scenario: Verify initial state for primary account holder-Not Developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I click on account details link from the global navigation - Not developed
Then I am on Account Details and preferences screen
When I click on the edit icon on the primary account holder field
Then I see Primary the primary account holder for the account
And I see default as primary account holder specified during onboarding
And I see apporoval checkbox deselected as default
And I see Update button as disabled
When I check the apporoval checkbox
Then I see enabled Update button
And I see Cancel Link and close button


Scenario: Update data for primary account holder-Not Developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I account details link from the global navigation - Not developed
Then I am on Account Details and preferences screen
When I select a primary account holder radio button
And I check the apporoval checkbox
And I see enabled Update Button


Scenario: Update button for primary account holder-Not Developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I account details link from the global navigation - Not developed
Then I am on Account Details and preferences screen
When I select a primary account holder radio button
And I check the apporoval checkbox
And I click on Update Button
Then I see updated value on the home screen for primary account holder


Scenario: Cancel lightbox for primary account holder-Not Developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I account details link from the global navigation - Not developed
Then I am on Account Details and preferences screen
When I select a primary account holder radio button
And I click on the Cancel link
Then I see the changes are discarded for primary account holder


Scenario: Close lightbox for primary account holder-Not Developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I account details link from the global navigation - Not developed
Then I am on Account Details and preferences screen
When I select a primary account holder radio button
And I click on the Close icon
Then I see the changes are discarded for primary account holder