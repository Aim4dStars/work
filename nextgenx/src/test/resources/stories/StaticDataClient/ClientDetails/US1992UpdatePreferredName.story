Narrative: US1992:Update preferred name- Verify 'Client Preferred Name'
In order to maintain the client’s correct preferred name in the system
As an adviser or adviser assistant
I want to update my client’s preferred name

Meta:
@userstory US1992UpdatePreferredName

Scenario: Valid Name
Meta:
@categories miniregression fullregression popopo
Given I am on Client details and preferences
When I click on edit icon for preferred name
Then I am on Edit preferred name screen
And I see previously recorded name
When I enter valid name TestWa5'- .&,/WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWTTTTTTTTT
Then I see only first 50 characters


Scenario: Approval Checkbox Client
Meta:
@categories fullregression
Given I am on Client details and preferences
When I click on edit icon for preferred name
Then I see disabled updated button
When I check the approval checkbox
Then I see enabled updated button


Scenario: Update Preferred Name
Meta:
@categories fullregression
Given I am on Client details and preferences
When I click on edit icon for preferred name
And I enter valid name Test First Name
And I check the approval checkbox
And I click on Update button
Then I see updated name as Test First Name


Scenario: Update Preferred Name with blank value
Meta:
@categories miniregression fullregression
Given I am on Client details and preferences
When I click on edit icon for preferred name
And I enter valid name ''
And I check the approval checkbox
And I click on Update button
Then I see updated name as ''


Scenario: Cancel Button Client
Meta:
@categories miniregression fullregression
Given I am on Client details and preferences
When I click on edit icon for preferred name
And I enter valid name Test First Name
And I click on cancel button
Then I see no changes in preferred name


Scenario: Close Button Client
Meta:
@categories miniregression fullregression
Given I am on Client details and preferences
When I click on edit icon for preferred name
And I enter valid name Test First Name
And I click on close button
Then I see no changes in preferred name

Scenario: Avaloq Integration CLient
Meta:
@categories fullregression
Given I am on Client details and preferences Avaloq
When I click on edit icon for preferred name Avaloq
Then I am on Edit preferred name screen Avaloq
And I see previously recorded name Avaloq
When I enter valid name TestWa5'- .&,/WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWTTTTTTTTT Avaloq
Then I see only first 50 characters Avaloq