Feature: Investor Feedback Form
  In order to give feedback to BT
  As an Investor
  I want to submit feedback via an online form

  @Smoketest
  Scenario: Successful login
    Given I have logged in as investor PENDING
    And Navigate to feedback PENDING

  @test
  Scenario: Successful complaint submission
  Given I have logged in as investor PENDING
    And Navigate to feedback PENDING 
    When I entered all the fields PENDING
    And I submit the feedback PENDING
    Then I should receive successful message PENDING
    And I should see the Unique reference Number PENDING

  @test
  Scenario: Form validation without mandatory fields
   Given I have logged in as investor PENDING
    When Navigate to feedback PENDING
    And I submit the feedback PENDING
    Then I should get error message for required fields PENDING

  @test
  Scenario: Account balance for a user
    Given a User has no money in their account PENDING
    When AUD3000 is deposited in to the account PENDING
    Then the balance should be AUD1500 PENDING
    
 @test
  Scenario: Account balance for a adviser PENDING
    Given a User has no money in their account  PENDING
    When AUD5000 is deposited in to the account PENDING
    Then the balance should be AUD400  PENDING
    
