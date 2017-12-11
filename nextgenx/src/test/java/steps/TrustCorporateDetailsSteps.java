package steps;

import static junit.framework.Assert.*;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import pages.clientdetails.CompanyDetailsPage;
import pages.clientdetails.LightBoxComponentPage;
import pages.clientdetails.TrustCorpDetailsPage;
import pages.clientdetails.TrustIndDetailsPage;
import pages.logon.LoginPage;

public class TrustCorporateDetailsSteps extends ScenarioSteps
{
	LoginPage loginPage;
	TrustCorpDetailsPage trustCorpDetailsPage;
	TrustIndDetailsPage trustIndDetailsPage;
	CompanyDetailsPage companyDetailsPage;
	LightBoxComponentPage lightBoxComponentPage;

	public String feeVal;

	public TrustCorporateDetailsSteps(Pages pages)
	{
		super(pages);
	}

	@Step
	public void starts_logon_clientdetails() throws Throwable
	{
		loginPage.open();
		loginPage.doLogon();
	}

	@Step
	public void openTrustCorporateDetail() throws Throwable
	{

		loginPage.gotopage("Client details (Trust Corporate)");
		lightBoxComponentPage.getPageRefresh();

	}

	@Step
	public void checkTrustDetailCorpEditIcon()
	{
		assertTrue(trustIndDetailsPage.getIsTrustRegStateEditIcon().isDisplayed());
		assertTrue(trustIndDetailsPage.getIsTrustTaxOptEditIcon().isDisplayed());
		assertTrue(trustIndDetailsPage.getIsTrustRegForGSTEditIcon().isDisplayed());
		assertTrue(trustIndDetailsPage.getIsTrustAddressEditIcon().isDisplayed());
		assertTrue(trustIndDetailsPage.getisTrustLinkedName());
		assertTrue(trustIndDetailsPage.getisTrustLinkedRole());

		assertTrue(companyDetailsPage.getIsCompanyNameIcon().isDisplayed());
		assertTrue(companyDetailsPage.getIsTaxOptEditIcon().isDisplayed());
		assertTrue(companyDetailsPage.getIsRegForGSTEditIcon().isDisplayed());
		assertTrue(companyDetailsPage.getIsRegComOfficeEditIcon().isDisplayed());
		assertTrue(companyDetailsPage.getIsPrinPlaceEditIcon().isDisplayed());
	}
}
