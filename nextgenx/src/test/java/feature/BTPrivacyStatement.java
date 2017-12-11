package feature;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import steps.BTPrivacyStatementSteps;

public class BTPrivacyStatement
{
	@Steps
	BTPrivacyStatementSteps onBTPrivacyStatementStep;

	@When("I click on Privacy statement link on global footer")
	public void isPrivacyStatementPage() throws Throwable
	{
		onBTPrivacyStatementStep.navigate_To_PrivacyStatement();

	}

	@Then("I see page header Privacy statement")
	public void verifyHeaderPrivacyStatement()
	{
		onBTPrivacyStatementStep.privacy_HeaderText();
	}

	@Then("I see sub header Respecting your privacy and the law")
	public void verifyPrivacySubHeader1()
	{
		onBTPrivacyStatementStep.privacy_sub_Header1Text();
	}

	@Then("I see sub header About this privacy policy")
	public void verifyPrivacySubHeader2()
	{
		onBTPrivacyStatementStep.privacy_sub_Header2Text();

	}

	@Then("I see sub header What information we collect")
	public void verifyPrivacySubHeader3()
	{
		onBTPrivacyStatementStep.privacy_sub_Header3Text();
	}

	@Then("I see sub header How we collect your information")
	public void verifyPrivacySubHeader4()
	{
		onBTPrivacyStatementStep.privacy_sub_Header4Text();
	}

	@Then("I see sub header Collecting information from BT web sites")
	public void verifyPrivacySubHeader5()
	{
		onBTPrivacyStatementStep.privacy_sub_Header5Text();
	}

	@Then("I see sub header Using and disclosing your information")
	public void verifyPrivacySubHeader6()
	{
		onBTPrivacyStatementStep.privacy_sub_Header6Text();
	}

	@Then("I see sub header Marketing products and services to you")
	public void verifyPrivacySubHeader7()
	{
		onBTPrivacyStatementStep.privacy_sub_Header7Text();
	}

	@Then("I see sub header Protecting your information and web site security")
	public void verifyPrivacySubHeader8()
	{
		onBTPrivacyStatementStep.privacy_sub_Header8Text();
	}

	@Then("I see sub header Using government identifiers, such as Tax File Numbers and Medicare numbers")
	public void verifyPrivacySubHeader9()
	{
		onBTPrivacyStatementStep.privacy_sub_Header9Text();
	}

	@Then("I see sub header Keeping your information accurate and up-to-date")
	public void verifyPrivacySubHeader10()
	{
		onBTPrivacyStatementStep.privacy_sub_Header10Text();
	}

	@Then("I see sub header Accessing your information")
	public void verifyPrivacySubHeader11()
	{
		onBTPrivacyStatementStep.privacy_sub_Header11Text();
	}

	@Then("I see sub header Changes to the privacy policy")
	public void verifyPrivacySubHeader12()
	{
		onBTPrivacyStatementStep.privacy_sub_Header12Text();
	}

	@Then("I see sub header Resolving your privacy issues")
	public void verifyPrivacySubHeader13()
	{
		onBTPrivacyStatementStep.privacy_sub_Header13Text();
	}

	@Then("I see Contact us link")
	public void verifyContactUsLink()
	{
		onBTPrivacyStatementStep.link_Contact_Us();
	}

	@Then("I see Any Westpac branch link")
	public void verifyAnyWestapcBranchLink()
	{
		onBTPrivacyStatementStep.link_Any_Westpac_Branch();
	}

	@Then("I see Any Westpac branch link gets open in new tab")
	public void verifyLinkOpenInNewTab()
	{
		onBTPrivacyStatementStep.handlingLinkAnyWestpacBranch();
	}

	@When("I click on any navigation item from Privacy statement page")
	public void clickingNavigationItemAccessibilty()
	{
		onBTPrivacyStatementStep.click_Accessibility_Link();
	}

	@Then("I see page gets navigated from Privacy Statement page")
	public void VerifyPageGetsNavigatedToItemAccessibilty()
	{
		onBTPrivacyStatementStep.acceessibility_Page_Header();
	}
}
