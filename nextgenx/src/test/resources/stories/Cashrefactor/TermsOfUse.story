Narrative: Terms of Use


Scenario: Verify terms of use page 
Meta:
@categories TermsOfUseAutomation 

Given I login into panorama system as adviser
When I click on the Terms of use link on global footer
Then I see page header Terms of use
And I see sub header Australian investors only
And I see sub header Value of your investments
And I see sub header Disclosure documents
And I see sub header Important disclaimers
And I see sub header Systems
When I click on any navigation item from Terms of use
Then I see page gets navigated from Terms of use


Scenario: Verify UI of Terms of use page - manual
Meta:
@categories TermsOfUseManual

Given I login into panorama system as adviser
When I click on the "Terms of use" link on global footer
Then I get navigated to "Terms of use" screen in the same tab
And I see Terms headers and sub headers in bold font