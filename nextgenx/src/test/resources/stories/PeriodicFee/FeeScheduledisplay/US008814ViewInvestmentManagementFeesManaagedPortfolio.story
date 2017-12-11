Narrative: US008814:Adviser View - View Investment Management Fees- Verify 'Investment Management Fees'
As a Adviser
I want to view an account’s Investment Management fee

Meta:
@userstory US008814ViewInvestmentManagementFeesManaagedPortfolio

Scenario:View the Investment Management fee
Meta:
@categories fullregression
Given I am on logon page screen
When I navigate to the fee schedule screen with fee
Then I see current fee schedule for each of the SMA as
|Code|Investment name|pa|
|EQR_CONC_CORE_EQ|EQR Concentrated Core Equities|8.40|
|BT_CORE|BTIM Core|0.00|
And I see Investment management fees – Managed portfolios heading below the Administration fees section


Scenario:Verify printer functionality for Investment Management Fees
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
When I click on the printer icon for printing Investment Management fee PENDING
Then I see Management fee file getting printed PENDING


Scenario:Verify download functionality for Investment Management Fees
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
When I click on the download icon for downloading Investment Management fee PENDING
Then I see Management fee file getting download PENDING


Scenario:When user has no current account holdings
Meta:
@categories fullregression
Given I am on logon page screen PENDING
When I navigate to the fee schedule screen with fee PENDING
And I do not have any holdings PENDING
Then I do not see Investment management fees section PENDING