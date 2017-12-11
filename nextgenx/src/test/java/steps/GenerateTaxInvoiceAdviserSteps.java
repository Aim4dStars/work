package steps;

import static org.junit.Assert.*;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import pages.clientdetails.LightBoxComponentPage;
import pages.logon.LoginPage;
import pages.taxinvoice.GenerateTaxInvoiceAdviserPage;

public class GenerateTaxInvoiceAdviserSteps extends ScenarioSteps
{

	LoginPage loginPage;
	GenerateTaxInvoiceAdviserPage generateTaxInvoiceAdviserPage;
	LightBoxComponentPage lightBoxComponentPage;

	public GenerateTaxInvoiceAdviserSteps(Pages pages)
	{
		super(pages);
	}

	@Step
	public void starts_logon_taxInvoice() throws Throwable
	{
		loginPage.open();
		loginPage.doLogon();
	}

	@Step
	public void openTaxInvoice() throws Throwable
	{

		loginPage.gotopage("Tax Invoice Adviser");

		lightBoxComponentPage.getPageRefresh();
	}

	@Step
	public void checkNecessaryAttributes()
	{

		assertTrue(generateTaxInvoiceAdviserPage.getIsTaxInvoiceScreenHeading().isDisplayed());

		assertTrue(generateTaxInvoiceAdviserPage.getIsTaxInvoiceScreenSubHeading().isDisplayed());

		assertTrue(generateTaxInvoiceAdviserPage.getIsHelpIcon().isDisplayed());

		assertTrue(generateTaxInvoiceAdviserPage.getIsGenerateInvoiceButton().isDisplayed());

		//assertTrue(generateTaxInvoiceAdviserPage.getIsInstructionalText().isDisplayed());

	}

}
