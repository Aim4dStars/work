Narrative:Roles and Permission
In order to setup the security for BT panorama
As a user who does not have permission to view an account
I want to blocked from viewing an account
Scenario: Permission for Periodic Fee
Given I login as <role>
When I navigate to menu
Then my permission for <role> on <screen> is as per <permission>

Examples:     
|role|screen|permission|
|adviser|Charge one-off advice fee|view|
|client|Charge one-off advice fee|deny|
|Paraplanner|Charge one-off advice fee|view|
|practicemanager|Charge one-off advice fee|view|
|dealergroupmanager|Charge one-off advice fee|view|
|ivestmentmanager|Charge one-off advice fee|view|


Scenario: Permission for Fee Schedule
Given I login as <role>
When I navigate to menu
Then my permission for <role> on <screen> is as per <permission>

Examples:     
|role|screen|permission|
|adviser|View Fee schedule Adviser|view|
|client|View Fee schedule Adviser|deny|
|Paraplanner|View Fee schedule Adviser|view|
|practicemanager|View Fee schedule Adviser|view|
|dealergroupmanager|View Fee schedule Adviser|view|
|ivestmentmanager|View Fee schedule Adviser|view|
