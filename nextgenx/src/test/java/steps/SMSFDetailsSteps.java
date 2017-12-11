package steps;

import static junit.framework.Assert.*;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import pages.clientdetails.LightBoxComponentPage;
import pages.clientdetails.SMSFDetailsPage;
import pages.logon.LoginPage;

public class SMSFDetailsSteps extends ScenarioSteps
{

	public String feeVal;
	LoginPage loginPage;
	SMSFDetailsPage smsftDetailPage;
	LightBoxComponentPage lightBoxComponentPage;

	public SMSFDetailsSteps(Pages pages)
	{
		super(pages);
	}

	@Step
	public void starts_logon_smsfdetails() throws Throwable
	{
		loginPage.open();
		loginPage.doLogon();
	}

	@Step
	public void openSmsfDetail() throws Throwable
	{

		loginPage.gotopage("Client details (SMSF Individual)");
		lightBoxComponentPage.getPageRefresh();

	}

	@Step
	public void checkSmsfDetailEditIcon()
	{

		assertTrue(smsftDetailPage.getIsSMSFRegStateEditIcon().isDisplayed());
		assertTrue(smsftDetailPage.getIsSMSFTaxOptEditIcon().isDisplayed());
		assertTrue(smsftDetailPage.getIsSMSFRegForGSTEditIcon().isDisplayed());
		assertTrue(smsftDetailPage.getIsSMSFAddressEditIcon().isDisplayed());
		assertTrue(smsftDetailPage.getisSMSFLinkedName());
		assertTrue(smsftDetailPage.getisSMSFLinkedRole());
	}

}
