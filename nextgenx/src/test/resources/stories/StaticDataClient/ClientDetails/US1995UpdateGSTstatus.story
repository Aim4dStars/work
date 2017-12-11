Narrative: US1995:Update GST status- Verify 'Registered for GST'
In order to help the client entity with their tax needs
As an adviser or adviser assistant
I want to update the GST registration status for my client (not for an Individual or Joint)

Meta:
@userstory US1995UpdateGSTstatus

Scenario: Default Value Trust Ind
Meta:
@categories miniregression fullregression
Given I am on Trust detail Individual page
When I click on edit icon for Registered for GST
Then I am on Edit Register for GST screen

Scenario: Approval Checkbox Trust Ind
Meta:
@categories miniregression fullregression
Given I am on Trust detail Individual page
When I click on edit icon for Registered for GST
Then I see disabled updated button
When I check the approval checkbox
Then I see enabled updated button


Scenario: Update Registration for GST
Meta:
@categories miniregression fullregression
Given I am on Trust detail Individual page
When I click on edit icon for Registered for GST
And I update Registration for GST
And I check the approval checkbox
And I click on Update button
Then I see updated value for Registration for GST


Scenario: Cancel Button for Trust Ind
Meta:
@categories miniregression fullregression
Given I am on Trust detail Individual page
When I click on edit icon for Registered for GST
And I update Registration for GST
And I click on cancel button gst
Then I see no changes in value for Registered for GST



Scenario: Close Button for Trust Ind
Meta:
@categories miniregression fullregression
Given I am on Trust detail Individual page
When I click on edit icon for Registered for GST
And I update Registration for GST
And I click on close button gst
Then I see no changes in value for Registered for GST

Scenario: Avaloq Integration Trust
Meta:
@categories fullregression
Given I am on Trust detail Individual page Avaloq
When I click on edit icon for Registered for GST Avaloq
Then I am on Edit Register for GST screen Avaloq