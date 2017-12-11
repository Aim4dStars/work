Narrative: US1998:DetailsAndPreferencesScreenInReadOnlyMode
In order to view and/or update the details of an account
As an adviser, adviser assistant or client
I want to view the details of my client’s account or my account

Meta:
@userstory US1998DetailsAndPreferencesScreenInReadOnlyMode


Scenario: Verify initial state Read only mode-Not Developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I click on account details link from the global navigation - Not developed
Then I am on Account Details and preferences screen
When I click on Client's name
Then I am on Client details and preferences screen
And I see the respective permissions for the account
And I see help text for each permission type with respective correct contents


Scenario: Verify initial state -Company-Not Developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I click on account details link from the global navigation - Not developed
Then I am on Account Details and preferences screen
And I see Edit icons next to items that can be edited for Company
And I see each linked client with its details and role and account approvers and non approvers with online access for Company
When I click on Linked client name or Company name
Then I am on Client details and preferences screen
And I see adviser permission for the account for company
And I see the permissions for all linked clients for company


Scenario: Verify initial state -Trust Individual-Not Developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I click on account details link from the global navigation - Not developed
Then I am on Account Details and preferences screen
And I see Edit icons next to items that can be edited for Trust Individual
And I see each linked client with its details and role and account approvers and non approvers with online access for Trust Individual
When I click on Linked client name or trust name
Then I am on Client details and preferences screen
And I see adviser permission for the account for trust individual
And I see the permissions for all linked clients for trust individual
And I see all the beneficiaries for the trust 


Scenario: Verify initial state -Trust Corporate-Not Developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I click on account details link from the global navigation - Not developed
Then I am on Account Details and preferences screen
And I see Edit icons next to items that can be edited for Trust Corporate
And I see each linked client with its details and role and account approvers and non approvers with online access for Company
When I click on Linked client name or Company name
Then I am on Client details and preferences screen
And I see adviser permission for the account for company
And I see the permissions for all linked clients for company
And I see Edit icons next to items that can be edited for Trust Individual
And I see each linked client with its details and role and account approvers and non approvers with online access for Trust Individual
When I click on Linked client name or trust name
Then I am on Client details and preferences screen
And I see adviser permission for the account for trust individual
And I see the permissions for all linked clients for trust individual
And I see all the beneficiaries for the trust


Scenario: Verify initial state -SMSF Individual-Not Developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I click on account details link from the global navigation - Not developed
Then I am on Account Details and preferences screen
And I see Edit icons next to items that can be edited for SMSF Individual
And I see each linked client with its details and role and account approvers and non approvers with online access for SMSF Individual
When I click on Linked client name or SMSF name
Then I am on Client details and preferences screen
And I see adviser permission for the account for SMSF individual
And I see the permissions for all linked clients for SMSF individual
And I see all the beneficiaries for the trust 


Scenario: Verify initial state -SMSF Corporate-Not Developed
Meta:
@categories miniregression fullregression
Given I am on Login Page
When I click on account details link from the global navigation - Not developed
Then I am on Account Details and preferences screen
And I see Edit icons next to items that can be edited for Company
And I see each linked client with its details and role and account approvers and non approvers with online access for Company
When I click on Linked client name or Company name
Then I am on Client details and preferences screen
And I see adviser permission for the account for company
And I see the permissions for all linked clients for company
And I see Edit icons next to items that can be edited for SMSF Individual
And I see each linked client with its details and role and account approvers and non approvers with online access for SMSF Individual
When I click on Linked client name or SMSF name
Then I am on Client details and preferences screen
And I see adviser permission for the account for SMSF individual
And I see the permissions for all linked clients for SMSF individual
And I see all the beneficiaries for the trust 