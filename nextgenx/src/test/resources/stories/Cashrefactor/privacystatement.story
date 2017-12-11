Narrative:Privacy Statement


Scenario: Verify privacy statement page
Meta:
@categories Privacy Automation


Given I login into panorama system as adviser
When I click on Privacy statement link on global footer
Then I see page header Privacy statement
And I see sub header Respecting your privacy and the law
And I see sub header About this privacy policy
And I see sub header What information we collect
And I see sub header How we collect your information
And I see sub header Collecting information from BT web sites
And I see sub header Using and disclosing your information
And I see sub header Marketing products and services to you
And I see sub header Protecting your information and web site security
And I see sub header Using government identifiers, such as Tax File Numbers and Medicare numbers
And I see sub header Keeping your information accurate and up-to-date
And I see sub header Accessing your information
And I see sub header Changes to the privacy policy
And I see sub header Resolving your privacy issues
And I see Contact us link
And I see Any Westpac branch link
And I see Any Westpac branch link gets open in new tab
When I click on any navigation item from Privacy statement page
Then I see page gets navigated from Privacy Statement page


Scenario: Verify functionality of contact us link  - manual
Meta:
@categories Privacy Manual


When I click on "Privacy statement" link on global footer
Then I get navigated to Privacy statement page in the same tab
When I click on "Contact us" link
Then I see default email application opens up with "support@panorama.com.au" as pre-populated email address in the "To" field
When I see no default email application is configured on local machine
Then I see no operation happening on click 


Scenario: Verify UI of Privacy statement page  - manual
Meta:
@categories Privacy Manual


When I click on "Privacy statement" link on global footer
Then I get navigated to Privacy statement page in the same tab
	And I see privacy headers and sub headers in bold font
	And I see bullets for the points under sub headers
	And I see "contact us" and "Any Westpac branch" link in blue colour
