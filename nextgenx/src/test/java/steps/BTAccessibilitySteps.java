package steps;

//import static org.junit.Assert.*;
import static junit.framework.Assert.*;

import java.util.Set;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import pages.BTAccessibility.BTAccessibilityPage;
import pages.logon.LogonPage;

public class BTAccessibilitySteps extends ScenarioSteps
{

	LogonPage logonpage;
	BTAccessibilityPage btaccessibilitypage;

	public BTAccessibilitySteps(Pages pages)
	{
		super(pages);
	}

	@Step
	public void starts_logon() throws Throwable
	{
		logonpage.open();
		//logonpage.doLogon();
	}

	@Step
	public void navigate_To_Accessibility() throws Throwable
	{
		logonpage.gotopage("Accessibility");

	}

	@Step
	public void accessibilty_HeaderText()
	{
		String accessibilityHeaderText = btaccessibilitypage.getAccessibilityText().getText();
		assertEquals(accessibilityHeaderText, "Accessibility");
		assertTrue(btaccessibilitypage.getAccessibilityText().isDisplayed());

	}

	//Verifying text image
	@Step
	public void image_HeaderText()
	{
		String imageHeaderText = btaccessibilitypage.getImageText().getText();
		assertEquals(imageHeaderText, "Images");
		assertTrue(btaccessibilitypage.getImageText().isDisplayed());

	}

	//Verifying text header Table and Forms
	@Step
	public void Tables_and_forms_HeaderText()
	{
		String tableAndFormText = btaccessibilitypage.getTablesandformsText().getText();
		assertEquals(tableAndFormText, "Tables and forms");
		assertTrue(btaccessibilitypage.getTablesandformsText().isDisplayed());

	}

	//Verifying text header Javascript Text
	@Step
	public void JavaScript_Header_Text()
	{
		String javaScriptText = btaccessibilitypage.getJavascriptText().getText();
		assertEquals(javaScriptText, "JavaScript");
		assertTrue(btaccessibilitypage.getJavascriptText().isDisplayed());

	}

	//Verify text header pdf
	@Step
	public void PDF_Text()
	{
		String pdfText = btaccessibilitypage.getPdfText().getText();
		assertEquals(pdfText, "PDF");
		assertTrue(btaccessibilitypage.getPdfText().isDisplayed());

	}

	//Verify text header Converting Pdfs
	@Step
	public void ConvertingPDFs_Text()
	{
		String convertingPDFsText = btaccessibilitypage.getConvertingPdfsText().getText();
		assertEquals(convertingPDFsText, "Converting PDFs");
		assertTrue(btaccessibilitypage.getConvertingPdfsText().isDisplayed());

	}

	//Verify text link under pdf
	@Step
	public void link_Under_Pdf_Header()
	{
		String linkUnderPdfText = btaccessibilitypage.getLinkUnderPdfText().getText();
		assertEquals(linkUnderPdfText, "http://www.adobe.com/products/reader");
		assertTrue(btaccessibilitypage.getLinkUnderPdfText().isDisplayed());

	}

	//
	@Step
	public void Link_Under_ConvertingPDFs_Header()
	{
		String linkUnderConvertingPDFsText = btaccessibilitypage.getLinkUnderConveringPdfText().getText();
		assertEquals(linkUnderConvertingPDFsText, "http://www.adobe.com/products/acrobat/access_onlinetools.html");
		assertTrue(btaccessibilitypage.getLinkUnderConveringPdfText().isDisplayed());

	}

	@Step
	public void Click_Terms_Of_Use()
	{
		btaccessibilitypage.getTermsofUseText().click();

	}

	@FindBy(css = ".heading-five.panel-header")
	private WebElement isSeeTextTermsOfUse;

	public void Terms_Of_Use_Header()
	{
		String termsOfUseText = btaccessibilitypage.getTextHeaderTermsOfUseText().getText();
		assertEquals(termsOfUseText, "Terms of use");
		assertTrue(btaccessibilitypage.getTextHeaderTermsOfUseText().isDisplayed());

	}

	public void Handeling_Link_Under_PDF()
	{
		String parentWindow = getDriver().getWindowHandle();
		btaccessibilitypage.getLinkUnderPdfText().click();
		Set <String> WindowId = getDriver().getWindowHandles();

		for (String WindowIDS : WindowId)
		{
			if (!(parentWindow.equals(WindowIDS)))
			{
				getDriver().switchTo().window(WindowIDS);
				assertTrue(getDriver().getCurrentUrl().contains("reader"));
				getDriver().close();
				getDriver().switchTo().window(parentWindow);

			}
			else
			{}
		}
	}

	public void Handeling_Link_Under_ConvertingPDFs()
	{
		String parentWindow = getDriver().getWindowHandle();
		btaccessibilitypage.getLinkUnderConveringPdfText().click();
		Set <String> WindowId = getDriver().getWindowHandles();

		for (String WindowIDS : WindowId)
		{
			if (!(parentWindow.equals(WindowIDS)))
			{
				getDriver().switchTo().window(WindowIDS);
				assertTrue(getDriver().getCurrentUrl().contains("reader"));
				getDriver().close();
				getDriver().switchTo().window(parentWindow);

			}
			else
			{}
		}
	}

	public void waitForAccessibilityTextToBePresent()
	{
		btaccessibilitypage.shouldBeVisible(By.cssSelector(".heading-five.panel-header"));
	}
}
