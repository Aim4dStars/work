package feature;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import steps.BTTermsOfUseSteps;

public class BTTermsOfUse
{

	@Steps
	BTTermsOfUseSteps onBTTermsOfUseSteps;

	@When("I click on the Terms of use link on global footer")
	public void is_click_terms_of_use_link() throws Throwable
	{
		onBTTermsOfUseSteps.openTermsOfUsePage();
	}

	@Then("I see page header Terms of use")
	public void verify_header_terms_of_use()
	{
		onBTTermsOfUseSteps.isHeaderTermsOfUse();
	}

	@Then("I see sub header Australian investors only")
	public void verify_sub_header1()
	{
		onBTTermsOfUseSteps.isTermsSubHeader1();
	}

	@Then("I see sub header Value of your investments")
	public void verify_sub_header2()
	{
		onBTTermsOfUseSteps.isTermsSubHeader2();
	}

	@Then("I see sub header Disclosure documents")
	public void verify_sub_header3()
	{
		onBTTermsOfUseSteps.isTermsSubHeader3();
	}

	@Then("I see sub header Important disclaimers")
	public void verify_sub_header4()
	{
		onBTTermsOfUseSteps.isTermsSubHeader4();
	}

	@Then("I see sub header Systems")
	public void verify_sub_header5()
	{
		onBTTermsOfUseSteps.isTermsSubHeader5();
	}

	@When("I click on any navigation item from Terms of use")
	public void is_click_accessibility_link()
	{
		onBTTermsOfUseSteps.clickOnAccessibilty();
	}

	@Then("I see page gets navigated from Terms of use")
	public void open_accessibilty_page()
	{
		onBTTermsOfUseSteps.isHeaderAccessibilty();
	}

}
