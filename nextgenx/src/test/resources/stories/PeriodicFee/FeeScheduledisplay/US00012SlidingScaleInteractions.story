Narrative: US00012:SlidingScaleInteractions
As an adviser
I want to be able to add/remove a sliding scale component

Meta:
@userstory US00012SlidingScaleInteractions

Scenario:Tax Invoice Cancel  
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I enter upper limits for first tier as 1000
Then I see lower limit of the next tier as 1000
When I enter value in the percentage field as .1
And I enter upper limits for first tier as 2000
Then I see value reflected in the percentage field is 0.10
 

Scenario:Tax Invoice Cancel More Scenario 1
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I enter upper limits for first tier as @3$%
Then I see lower limit of the next tier as 

Scenario:Tax Invoice Cancel More Scenario 1-Alternate-1
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I enter value in the percentage field as .12
And I enter upper limits for first tier as 1000
Then I see value reflected in the percentage field is 0.12

Scenario:Tax Invoice Cancel More Scenario 1-Alternate-2
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I enter value in the percentage field as 1
And I enter upper limits for first tier as 1000
Then I see value reflected in the percentage field is 1.00

Scenario:Tax Invoice Cancel More Scenario 1-Alternate-3
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I enter value in the percentage field as 1.2
And I enter upper limits for first tier as 1000
Then I see value reflected in the percentage field is 1.20


Scenario:Tax Invoice Cancel More Scenario 1-Alternate-4
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I enter value in the percentage field as 1.23
And I enter upper limits for first tier as 1000
Then I see value reflected in the percentage field is 1.23


Scenario:Tax Invoice Cancel More Scenario 1-Alternate-5
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I enter value in the percentage field as 12
And I enter upper limits for first tier as 1000
Then I see value reflected in the percentage field is 12.00


Scenario:Tax Invoice Cancel More Scenario 1-Alternate-6
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I enter value in the percentage field as 12.3
And I enter upper limits for first tier as 1000
Then I see value reflected in the percentage field is 12.30


Scenario:Tax Invoice Cancel More Scenario 1-Alternate-6
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I enter value in the percentage field as 123
Then I see Error Message being displayed PENDING


Scenario:Tax Invoice Cancel More Scenario 1-Alternate-7
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I enter value in the percentage field as 1234
Then I see Error Message being displayed PENDING


Scenario:Tax Invoice Cancel More Scenario 1-Alternate-8
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I enter value in the percentage field as 12345
Then I see Error Message being displayed PENDING


Scenario:Tax Invoice Cancel More Scenario 1-Alternate-9
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I enter value in the percentage field as 123456
Then I see value reflected in the percentage field is 12345
And I see Error Message being displayed PENDING


Scenario:Tax Invoice Cancel More Scenario 2
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I enter upper limits for first tier as 1000
And I enter value in the percentage field as .1
Then I see lower limit of the next tier as 1,000
And I see value of upper limit for first tier as 1,000


Scenario:Tax Invoice Cancel More Scenario 3
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I enter upper limits for first tier as 0
Then I see error message as Err.IP.0097 PENDING


Scenario:Tax Invoice Cancel More Scenario 3-Alternate
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I enter upper limits for first tier as -10
Then I see error message as Err.IP.0097 PENDING


Scenario:Tax Invoice Cancel More Scenario 4
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I click on add a sliding scale for 10 times PENDING
Then I do not see the add tier icon PENDING
And I see the dollar field for the 10th tier as uneditable PENDING


Scenario:Tax Invoice Cancel More Scenario 5
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I enter X digit in the dollar fee component PENDING
Then I see digits reflected on the screen as PENDING


Scenario:Tax Invoice Cancel More Scenario 6
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I enter value in the percentage field as $%^
Then I see the error message Err.IP.0095 PENDING

Scenario:Tax Invoice Cancel More Scenario 6 -Alternate
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I enter value in the percentage field as 
And I enter upper limits for first tier as 1000
Then I see the error message Err.IP.0095


Scenario:Tax Invoice Cancel More Scenario 7
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I enter value in the percentage field as -12
And I enter upper limits for first tier as 1000
Then I see the error message Err.IP.0096


Scenario:Tax Invoice Cancel More Scenario 8
Meta:
@categories miniregression fullregressionbc
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I enter value in the percentage field as 12
And I enter value in the percentage field of second tier as 
And I click on Next Button PENDING
Then I am on the Confirmations screen PENDING
And I see values reflected for percentage component as 0.00 PENDING

Scenario:Tax Invoice Cancel More Scenario 9
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I enter upper limits for first tier as 1000
And I enter value in the percentage field as 12
And I enter upper limits for second tier as 2000
And I enter value in the percentage field of second tier as 22
And I enter upper limits for first tier as $%^
And I enter upper limits for second tier as ^&*(
Then I see the error message Err.IP-0095


Scenario:Tax Invoice Cancel More Scenario 10
Meta:
@categories miniregression fullregression
Given I am on fee schedule screen with fee
When I click on Edit fees link
And I enter upper limits for first tier as 12.34
Then I see value of upper limit for first tier as 12