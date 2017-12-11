package steps;

import static junit.framework.Assert.*;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import pages.clientdetails.CompanyDetailsPage;
import pages.clientdetails.LightBoxComponentPage;
import pages.clientdetails.SMSFCorpDetailsPage;
import pages.clientdetails.SMSFDetailsPage;
import pages.logon.LoginPage;

public class SMSFCorpDetailsSteps extends ScenarioSteps
{

	public String feeVal;

	LoginPage loginPage;
	SMSFCorpDetailsPage smsfCorpDetailPage;
	SMSFDetailsPage smsftDetailPage;
	CompanyDetailsPage companyDetailsPage;
	LightBoxComponentPage lightBoxComponentPage;

	public SMSFCorpDetailsSteps(Pages pages)
	{
		super(pages);
	}

	@Step
	public void starts_logon_smsfcorpdetails() throws Throwable
	{
		loginPage.open();
		loginPage.doLogon();
	}

	@Step
	public void openSmsfCorpDetail() throws Throwable
	{

		loginPage.gotopage("Client details (SMSF Corporte)");
		lightBoxComponentPage.getPageRefresh();

	}

	@Step
	public void checkSmsfCorpDetailEditIcon()
	{

		assertTrue(smsftDetailPage.getIsSMSFRegStateEditIcon().isDisplayed());
		assertTrue(smsftDetailPage.getIsSMSFTaxOptEditIcon().isDisplayed());
		assertTrue(smsftDetailPage.getIsSMSFRegForGSTEditIcon().isDisplayed());
		assertTrue(smsftDetailPage.getIsSMSFAddressEditIcon().isDisplayed());
		assertTrue(smsftDetailPage.getisSMSFLinkedName());
		assertTrue(smsftDetailPage.getisSMSFLinkedRole());

		assertTrue(companyDetailsPage.getIsCompanyNameIcon().isDisplayed());
		assertTrue(companyDetailsPage.getIsTaxOptEditIcon().isDisplayed());
		assertTrue(companyDetailsPage.getIsRegForGSTEditIcon().isDisplayed());
		assertTrue(companyDetailsPage.getIsRegComOfficeEditIcon().isDisplayed());
		assertTrue(companyDetailsPage.getIsPrinPlaceEditIcon().isDisplayed());

	}
}
