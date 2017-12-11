package feature;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import pages.BTAccessibility.BTAccessibilityPage;
import steps.BTAccessibilitySteps;

public class BTAccessibility
{
	@Steps
	BTAccessibilitySteps onAccessibilitySteps;
	BTAccessibilityPage btaccessibilitypage;

	@Given("I login into panorama system as adviser")
	public void loginIntoPanorma() throws Throwable
	{
		onAccessibilitySteps.starts_logon();

	}

	@When("I click to accessibility link at the footer")
	public void isAccessibilityPage() throws Throwable
	{
		onAccessibilitySteps.navigate_To_Accessibility();

	}

	@Then("I see accessibility options title Images, Tables and forms, JavaScript, PDF, Converting PDFs")
	public void verifyAccessibilityOption() throws InterruptedException
	{
		//Thread.sleep(10000);
		onAccessibilitySteps.waitForAccessibilityTextToBePresent();
		onAccessibilitySteps.accessibilty_HeaderText();
		onAccessibilitySteps.image_HeaderText();
		onAccessibilitySteps.Tables_and_forms_HeaderText();
		onAccessibilitySteps.JavaScript_Header_Text();
		onAccessibilitySteps.PDF_Text();
		onAccessibilitySteps.ConvertingPDFs_Text();

	}

	@Then("I see a link http://www.adobe.com/products/reader under PDF header")
	public void verifyingLinkUnderPdfHeader()
	{
		onAccessibilitySteps.link_Under_Pdf_Header();

	}

	@Then("I see a link http://www.adobe.com/products/acrobat/access_onlinetools.html under Converting PDFs")
	public void verifyingLinkUnderConvertingPDFsHeader()
	{
		onAccessibilitySteps.Link_Under_ConvertingPDFs_Header();

	}

	@Then("I see link http://www.adobe.com/products/reader open in new tab")
	public void verifyLinkUnderPDFOpenInNewTab()
	{
		onAccessibilitySteps.Handeling_Link_Under_PDF();
	}

	@Then("I see link http://www.adobe.com/products/acrobat/access_onlinetools.html open in new tab")
	public void verifyLinkUnderConvertingPDFOpenInNewTab()
	{
		onAccessibilitySteps.Handeling_Link_Under_ConvertingPDFs();
	}

	@When("I click on any navigation item from accessibility")
	public void clickingNavigationItemTermsOfUse()
	{
		onAccessibilitySteps.Click_Terms_Of_Use();
	}

	@Then("I see page gets navigated to terms of use")
	public void VerifyPageGetsNavigatedToItemTermsOfUse()
	{
		onAccessibilitySteps.Terms_Of_Use_Header();
	}

}
