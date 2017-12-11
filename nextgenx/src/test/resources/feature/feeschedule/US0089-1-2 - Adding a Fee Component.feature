Feature: US0089:Edit Fee Schedule- Verify 'Component Tab'  
  As an adviser
  I want to have the capability to add a Fee Component
  
  @Functional @feeschedule
  Scenario:Verify the three components on screen for ongoing fee
  Given I am on Fee Schedule screen with fee PENDING
  When I click on Edit fees PENDING
  Then I see Dollar Fee, Percentage Fee and Sliding Scale components for ongoing fee PENDING
  
  @Functional @feeschedule
  Scenario:Verify the three components on screen for licensee fee
  Given I am on Fee Schedule screen with fee PENDING
  When I click on Edit fees PENDING
  Then I see Dollar Fee, Percentage Fee and Sliding Scale components for licensee fee PENDING
  
  @Functional @feeschedule
  Scenario:Dollar fee button is active for an account having no fee setup
  Given I am on Fee Schedule screen with no fee
  When I click on Edit fees link
  Then I see Dollar fee component for ongoing is active
  
  @Functional @feeschedule
  Scenario:Dollar fee button is inactive on clicking
  Given I am on Fee Schedule screen with no fee
  When I click on Edit fees link
  And I clicks on the Dollar fee component button for ongoing fee
  Then verify that Dollar button for ongoing fee is inactive
  
  @Functional @feeschedule
  Scenario:Percentage fee button is active on clicking dollar fee button
  Given I am on Fee Schedule screen with no fee
  When I click on Edit fees link
  And I clicks on the Dollar fee component button for ongoing fee
  Then verify that Percentage fee component for ongoing fee is active