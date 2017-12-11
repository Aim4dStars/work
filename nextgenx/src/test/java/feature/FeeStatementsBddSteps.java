package feature;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Pending;
import org.jbehave.core.annotations.When;

import steps.FeeStatementsSteps;

public class FeeStatementsBddSteps
{

	@Steps
	FeeStatementsSteps feestatementssteps;

	@When("I navigate to FeeRevenue Statements screen")
	public void i_navigate_on_fee_statements_screen() throws Throwable
	{
		feestatementssteps.openFeeStatements();
	}

	@Pending
	@When("I click on calendar")
	public void i_click_on_calender_fee_statements_screen() throws Throwable
	{
		feestatementssteps.clickOnCalender();
	}

	@Pending
	@When("I give start date 01 Jan 2014")
	public void i_give_start_date_fee_statements_screen() throws Throwable
	{
		feestatementssteps.enterStartdate();
	}

	@When("I give end date 01 Jun 2014")
	public void i_give_end_date_fee_statements_screen() throws Throwable
	{
		feestatementssteps.enterEnddate();
	}

	@When("I click on search button")
	public void i_click_search_button_fee_statements_screen() throws Throwable
	{
		feestatementssteps.clickSearch();
	}

}
