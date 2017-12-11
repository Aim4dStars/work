package steps;

//import static org.junit.Assert.*;
import static junit.framework.Assert.*;

import java.util.Set;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import pages.BTPrivacyStatement.BTPrivacyStatementPage;
import pages.logon.LogonPage;

public class BTPrivacyStatementSteps extends ScenarioSteps
{

	LogonPage logonpage;
	BTPrivacyStatementPage btprivacystatementpage;

	public BTPrivacyStatementSteps(Pages pages)
	{
		super(pages);
	}

	@Step
	public void navigate_To_PrivacyStatement() throws Throwable
	{
		logonpage.gotopage("Privacy statement");

	}

	//Verifying header text and visibilty
	@Step
	public void privacy_HeaderText()
	{
		String privacyHeaderText = btprivacystatementpage.getHeaderPrivacyStatement().getText();
		assertEquals(privacyHeaderText, "Privacy statement");
		assertTrue(btprivacystatementpage.getHeaderPrivacyStatement().isDisplayed());

	}

	//Verifying sub header text and visibility
	@Step
	public void privacy_sub_Header1Text()
	{
		String privacysubheader1Text = btprivacystatementpage.getPrivacySubHeader1().getText();
		assertEquals(privacysubheader1Text, "Respecting your privacy and the law");
		assertTrue(btprivacystatementpage.getPrivacySubHeader1().isDisplayed());

	}

	//Verifying text header Table and Forms
	@Step
	public void privacy_sub_Header2Text()
	{
		String privacysubheader2Text = btprivacystatementpage.getPrivacySubHeader2().getText();
		assertEquals(privacysubheader2Text, "About this privacy policy");
		assertTrue(btprivacystatementpage.getPrivacySubHeader2().isDisplayed());

	}

	//Verifying text header Javascript Text
	@Step
	public void privacy_sub_Header3Text()
	{
		String privacysubheader3Text = btprivacystatementpage.getPrivacySubHeader3().getText();
		assertEquals(privacysubheader3Text, "What information we collect");
		assertTrue(btprivacystatementpage.getPrivacySubHeader3().isDisplayed());

	}

	//Verify text header pdf
	@Step
	public void privacy_sub_Header4Text()
	{
		String privacysubheader4Text = btprivacystatementpage.getPrivacySubHeader4().getText();
		assertEquals(privacysubheader4Text, "How we collect your information");
		assertTrue(btprivacystatementpage.getPrivacySubHeader4().isDisplayed());

	}

	//Verify text header Converting Pdfs
	@Step
	public void privacy_sub_Header5Text()
	{
		String privacysubheader5Text = btprivacystatementpage.getPrivacySubHeader5().getText();
		assertEquals(privacysubheader5Text, "Collecting information from BT web sites");
		assertTrue(btprivacystatementpage.getPrivacySubHeader5().isDisplayed());

	}

	//Verify text link under pdf
	@Step
	public void privacy_sub_Header6Text()
	{
		String privacysubheader6Text = btprivacystatementpage.getPrivacySubHeader6().getText();
		assertEquals(privacysubheader6Text, "Using and disclosing your information");
		assertTrue(btprivacystatementpage.getPrivacySubHeader6().isDisplayed());

	}

	//
	@Step
	public void privacy_sub_Header7Text()
	{
		String privacysubheader7Text = btprivacystatementpage.getPrivacySubHeader7().getText();
		assertEquals(privacysubheader7Text, "Marketing products and services to you");
		assertTrue(btprivacystatementpage.getPrivacySubHeader7().isDisplayed());

	}

	@Step
	public void privacy_sub_Header8Text()
	{
		String privacysubheader8Text = btprivacystatementpage.getPrivacySubHeader8().getText();
		assertEquals(privacysubheader8Text, "Protecting your information and web site security");
		assertTrue(btprivacystatementpage.getPrivacySubHeader8().isDisplayed());

	}

	@Step
	public void privacy_sub_Header9Text()
	{
		String privacysubheader9Text = btprivacystatementpage.getPrivacySubHeader9().getText();
		assertEquals(privacysubheader9Text, "Using government identifiers, such as Tax File Numbers and Medicare numbers");
		assertTrue(btprivacystatementpage.getPrivacySubHeader9().isDisplayed());

	}

	@Step
	public void privacy_sub_Header10Text()
	{
		String privacysubheader10Text = btprivacystatementpage.getPrivacySubHeader10().getText();
		assertEquals(privacysubheader10Text, "Keeping your information accurate and up-to-date");
		assertTrue(btprivacystatementpage.getPrivacySubHeader10().isDisplayed());

	}

	@Step
	public void privacy_sub_Header11Text()
	{
		String privacysubheader11Text = btprivacystatementpage.getPrivacySubHeader11().getText();
		assertEquals(privacysubheader11Text, "Accessing your information");
		assertTrue(btprivacystatementpage.getPrivacySubHeader11().isDisplayed());

	}

	@Step
	public void privacy_sub_Header12Text()
	{
		String privacysubheader12Text = btprivacystatementpage.getPrivacySubHeader12().getText();
		assertEquals(privacysubheader12Text, "Changes to the privacy policy");
		assertTrue(btprivacystatementpage.getPrivacySubHeader12().isDisplayed());

	}

	@Step
	public void privacy_sub_Header13Text()
	{
		String privacysubheader13Text = btprivacystatementpage.getPrivacySubHeader13().getText();
		assertEquals(privacysubheader13Text, "Resolving your privacy issues");
		assertTrue(btprivacystatementpage.getPrivacySubHeader13().isDisplayed());

	}

	@Step
	public void link_Contact_Us()
	{
		String linkContactUsText = btprivacystatementpage.getLinkContactUs().getText();
		assertEquals(linkContactUsText, "Contact us");
		assertTrue(btprivacystatementpage.getLinkContactUs().isDisplayed());

	}

	@Step
	public void link_Any_Westpac_Branch()
	{
		String linkAnyWestpacBranch = btprivacystatementpage.getLinkAnyWespacBranch().getText();
		assertTrue(linkAnyWestpacBranch.contains("Any Westpac branch"));
		assertTrue(btprivacystatementpage.getLinkAnyWespacBranch().isDisplayed());

	}

	public void handlingLinkAnyWestpacBranch()
	{
		String parentWindow = getDriver().getWindowHandle();
		btprivacystatementpage.getLinkAnyWespacBranch().click();
		Set <String> WindowId = getDriver().getWindowHandles();

		for (String WindowIDS : WindowId)
		{
			if (!(parentWindow.equals(WindowIDS)))
			{
				getDriver().switchTo().window(WindowIDS);
				assertTrue(getDriver().getCurrentUrl().contains("locateus"));
				getDriver().close();
				getDriver().switchTo().window(parentWindow);

			}
			else
			{}
		}
	}

	@Step
	public void click_Accessibility_Link()
	{
		btprivacystatementpage.getLinkAccessibility().click();

	}

	@FindBy(css = ".heading-five.panel-header")
	private WebElement isAccessibilityText;

	public void acceessibility_Page_Header()
	{
		String accessibilityText = btprivacystatementpage.getAccessibilityText().getText();
		assertEquals(accessibilityText, "Accessibility");
		assertTrue(btprivacystatementpage.getAccessibilityText().isDisplayed());

	}

}
