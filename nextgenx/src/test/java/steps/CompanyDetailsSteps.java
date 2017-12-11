package steps;

import static junit.framework.Assert.*;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import pages.clientdetails.CompanyDetailsPage;
import pages.clientdetails.LightBoxComponentPage;
import pages.logon.LoginPage;

public class CompanyDetailsSteps extends ScenarioSteps
{

	LoginPage loginPage;
	CompanyDetailsPage companyDetailPage;
	public String feeVal;
	public String nameTextCancelCompany;
	LightBoxComponentPage lightBoxComponentPage;

	public CompanyDetailsSteps(Pages pages)
	{
		super(pages);
	}

	@Step
	public void starts_logon_companydetails() throws Throwable
	{
		loginPage.open();
		loginPage.doLogon();

	}

	@Step
	public void opencompanyDetail() throws Throwable
	{

		loginPage.gotopage("Client details (company)");
		lightBoxComponentPage.getPageRefresh();

	}

	@Step
	public void checkClientDetailEditIcon()
	{
		assertTrue(companyDetailPage.getIsCompanyNameIcon().isDisplayed());
		assertTrue(companyDetailPage.getIsTaxOptEditIcon().isDisplayed());
		assertTrue(companyDetailPage.getIsRegForGSTEditIcon().isDisplayed());
		assertTrue(companyDetailPage.getIsRegComOfficeEditIcon().isDisplayed());
		assertTrue(companyDetailPage.getIsPrinPlaceEditIcon().isDisplayed());
		assertTrue(companyDetailPage.getisLinkedName());
		assertTrue(companyDetailPage.getisLinkedRole());

	}

	@Step
	public void navigatecompanyDetail() throws Throwable
	{
		loginPage.open();
		loginPage.doLogon();
		loginPage.gotopage("Client details (company)");
		lightBoxComponentPage.getPageRefresh();
	}

	@Step
	public void clickEditCompanyName() throws Throwable
	{

		companyDetailPage.getIsCompanyNameIcon().click();

	}

	@Step
	public void checkEditClienttitle() throws Throwable
	{

		assertEquals("name", lightBoxComponentPage.getIsEditTitle().getText());
		assertTrue(lightBoxComponentPage.getIsCheckBox().isDisplayed());
		assertTrue(lightBoxComponentPage.getIsCancelButton().isDisplayed());
		assertTrue(lightBoxComponentPage.getIsCancel().isDisplayed());
	}

	@Step
	public void checkPreferredName() throws Throwable
	{
		String nameText = companyDetailPage.getIsCompanyPreferredName().getText();

		String editName = companyDetailPage.getIsCompanyEditPreferredName().getAttribute("data-view-value");

		assertEquals(nameText, editName);
	}

	@Step
	public void enterValidComName(String validName) throws Throwable
	{

		companyDetailPage.getIsCompanyEditPreferredName().clear();
		companyDetailPage.getIsCompanyEditPreferredName().sendKeys(validName);

	}

	@Step
	public void clearValidComName() throws Throwable
	{

		companyDetailPage.getIsCompanyEditPreferredName().clear();

	}

	@Step
	public void checkValidComNameLength() throws Throwable
	{

		String editNameText = companyDetailPage.getIsCompanyEditPreferredName().getAttribute("data-view-value");
		int editNameLength = editNameText.length();
		Thread.sleep(100);
		assertEquals(50, editNameLength);

	}

	@Step
	public void checkDisabledButton() throws Throwable
	{

		assertTrue(lightBoxComponentPage.getIsUpdateDisabled().isDisplayed());
	}

	@Step
	public void checkApprovalCheckbox() throws Throwable
	{

		lightBoxComponentPage.getIsCheckBox().click();
	}

	@Step
	public void checkEnabledButton() throws Throwable
	{

		assertTrue(lightBoxComponentPage.getIsUpdateEnabled().isDisplayed());
	}

	@Step
	public void clickUpdateButton() throws Throwable
	{

		lightBoxComponentPage.getIsUpdateEnabled().click();
	}

	@Step
	public void verifyUpdateName(String updatedName) throws Throwable
	{

		String nameText = companyDetailPage.getIsCompanyPreferredName().getText();

		assertEquals(nameText, updatedName);
	}

	@Step
	public void verifyErrorText() throws Throwable
	{

		String nameText = companyDetailPage.getIsCompanyErrorMessage().getText();
		assertEquals("Company name is required", nameText);
	}

	@Step
	public void clickCancel() throws Throwable
	{

		nameTextCancelCompany = companyDetailPage.getIsCompanyPreferredName().getText();
		lightBoxComponentPage.getIsCancelButton().click();
	}

	@Step
	public void clickClose() throws Throwable
	{

		nameTextCancelCompany = companyDetailPage.getIsCompanyPreferredName().getText();
		lightBoxComponentPage.getIsCancel().click();
	}

	@Step
	public void verifyNameAfterCanButton() throws Throwable
	{

		assertEquals(nameTextCancelCompany, companyDetailPage.getIsCompanyPreferredName().getText());
	}

}
