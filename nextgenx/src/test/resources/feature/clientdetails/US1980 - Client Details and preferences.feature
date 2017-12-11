 Feature: US1980:Details and preferences screen in read-only mode- Verify Default Screen  
	In order to view update the client details
	As an adviser or adviser assistant
	I want to view my client’s details and preferences


  @Functional @clientdetails
  Scenario: Verify Client Details Preference Screen
  Given I am on Login Page
  When I navigate to Client details and preferences
  Then I see edit icon next to editable item
  
  @Functional @clientdetails
  Scenario: Verify Company Details Preference Screen
  Given I am on Login Page
  When I navigate to company details screen
  Then I see edit icon next to editable item for Company and Linked Client
  
  @Functional @clientdetails
  Scenario: Verify Trust Individual Preference Screen
  Given I am on Login Page
  When I navigate to Trust Individual screen
  Then I see edit icon next to editable item for Trust and Linked Client and Beneficiary details
  
  @Functional @clientdetails
  Scenario: Verify Trust Corporate Preference Screen
  Given I am on Login Page
  When I navigate to Trust Corporate screen
  Then I see edit icon next to editable item for Trust and Company and Linked Client and Beneficiary details
  
  @Functional @clientdetails
  Scenario: Verify SMSF Individual Preference Screen
  Given I am on Login Page
  When I navigate to SMSF Individual screen
  Then I see edit icon next to editable item for SMSF and Linked Client
  
  @Functional @clientdetails
  Scenario: Verify SMSF Corporate Preference Screen
  Given I am on Login Page
  When I navigate to SMSF Corporate screen
  Then I see edit icon next to editable item for SMSF and Company and Linked Client
  