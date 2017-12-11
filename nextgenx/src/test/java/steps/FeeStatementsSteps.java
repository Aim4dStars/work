package steps;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import pages.clientdetails.LightBoxComponentPage;
import pages.confirm.ConfirmPage;
import pages.fees.FeeDetailsPage;
import pages.fees.FeeRevenueStmtPage;
import pages.logon.LoginPage;

public class FeeStatementsSteps extends ScenarioSteps
{

	LoginPage loginPage;
	FeeDetailsPage FeeDetailsPage;
	ConfirmPage ConfirmPage;
	FeeRevenueStmtPage FeeRevenueStmtPage;
	LightBoxComponentPage lightBoxComponentPage;

	public FeeStatementsSteps(Pages pages)
	{
		super(pages);
	}

	@Step
	public void openFeeStatements() throws Throwable
	{
		loginPage.gotopage("Fee revenue statement");
	}

	@Step
	public void clickOnCalender() throws Throwable
	{
		FeeRevenueStmtPage.getIsStartDate().clear();

	}

	@Step
	public void navFeeStmt() throws Throwable
	{
		loginPage.open();
		loginPage.doLogon();

		Thread.sleep(5000);
		loginPage.gotopage("Fee revenue statement");
		lightBoxComponentPage.getPageRefresh();

	}

	@Step
	public void enterStartdate()
	{
		FeeRevenueStmtPage.getIsStartDate().clear();
		FeeRevenueStmtPage.getIsStartDate().sendKeys("01 Jan 2014");
	}

	@Step
	public void enterEnddate()
	{

		FeeRevenueStmtPage.getIsEndDate().clear();
		FeeRevenueStmtPage.getIsEndDate().sendKeys("31 May 2014");

	}

	@Step
	public void enterStartdateOld()
	{
		FeeRevenueStmtPage.getIsStartDate().clear();
		FeeRevenueStmtPage.getIsStartDate().sendKeys("01 Jan 2011");
	}

	@Step
	public void enterEnddateOld()
	{

		FeeRevenueStmtPage.getIsEndDate().clear();
		FeeRevenueStmtPage.getIsEndDate().sendKeys("31 May 2011");

	}

	@Step
	public void clickSearch()
	{

		FeeRevenueStmtPage.getSearch().click();

	}

	@Step
	public void displayRecords() throws Throwable
	{
		FeeRevenueStmtPage.tableCont();

	}

	@Step
	public void noRecords() throws Throwable
	{
		FeeRevenueStmtPage.notableCont();

	}

}
