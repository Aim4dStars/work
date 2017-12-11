Narrative:Accessibility page


Scenario: Verify accessibility option
Meta:
@categories Accessibility Automation
 

Given I login into panorama system as adviser
When I click to accessibility link at the footer
Then I see accessibility options title Images, Tables and forms, JavaScript, PDF, Converting PDFs
And I see a link http://www.adobe.com/products/reader under PDF header
And I see a link http://www.adobe.com/products/acrobat/access_onlinetools.html under Converting PDFs
And I see link http://www.adobe.com/products/reader open in new tab
And I see link http://www.adobe.com/products/acrobat/access_onlinetools.html open in new tab
When I click on any navigation item from accessibility
Then I see page gets navigated to terms of use