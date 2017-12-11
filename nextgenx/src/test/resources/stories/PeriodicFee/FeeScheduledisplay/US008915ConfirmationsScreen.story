Narrative: US008915:Edit Fee Schedule- Confirmations Screen
As an adviser
I want to have the capability to view Confirmations Screen

Meta:
@userstory US008915ConfirmationsScreen

Scenario:Veirfy the Confirmation Page
Meta:
@categories miniregression fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
And I click on Submit button PENDING
Then I am on  Confirmation Page PENDING


Scenario:Veirfy the download functionality on Confirmation Page
Meta:
@categories miniregression fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
And I click on Submit button PENDING
Then I am on  Confirmation Page PENDING
And I see the download icon on confirmation Page PENDING


Scenario:Verify the link on Confirmation Page
Meta:
@categories miniregression fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
Then I see a link titled as Go back and edit fees PENDING


Scenario: Verify the text on Confirmation Page
Meta:
@categories miniregression fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
And I click on Submit button PENDING
Then I see a text as Download this form for your clients to authorise PENDING


Scenario:Verify the Receipt Page for adviser
Meta:
@categories miniregression fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
And I click on Next button PENDING
And I check the client agreement checkbox PENDING
When I click on Submit button PENDING
Then I am on Receipt Page PENDING


Scenario:Verify the client agreement text
Meta:
@categories miniregression fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
And I click on Next button PENDING
When I click on Submit button PENDING
Then I see client agreement text in a box PENDING


Scenario:Verify the error message for no fee
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
And I click on remove buttons for all fee components PENDING
And I click on Next button PENDING
When I click on Submit button PENDING
Then Verify error for fee compoenents as No fee applied PENDING


Scenario:Verify no fee change
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I click on Edit fees link PENDING
And I click on any link on the page PENDING
Then I see no fee change is seen PENDING