package feature;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;

import steps.ActAdviserDashboardSteps;

public class ActAdviserDashboardBddSteps
{

	@Steps
	ActAdviserDashboardSteps actadviserdashboardsteps;

	@When("I navigate to Adviser Dashboard")
	public void openDraftApplication() throws Throwable
	{

		actadviserdashboardsteps.openDraftApplication();
	}

	@Then("I see header and account name as hyperlink for Draft Application")
	public void headerAndAccountNamePresent() throws Throwable
	{
		actadviserdashboardsteps.headerAndAccountNamePresent();

	}

	@Then("I see result count and client account type as text for Draft Application")
	public void resultCountAndAccountTypeTextPresent123() throws Throwable
	{
		actadviserdashboardsteps.resultCountAndAccountTypeTextPresent();

	}

	@Then("I see Draft application date")
	public void draftApplicationDatePresent() throws Throwable
	{

		actadviserdashboardsteps.draftApplicationDatePresent();

	}

	@When("I click on header for Draft Application")
	public void draftApplicationHeaderClick() throws Throwable
	{
		actadviserdashboardsteps.draftApplicationHeaderClick();

	}

	@Then("I see valid result count format for Draft Application")
	public void draftValidCountFormat() throws Throwable
	{

		actadviserdashboardsteps.draftValidCountFormat();

	}

	@Then("I see valid date format for Draft Application")
	public void dateValidFormat() throws Throwable
	{

		actadviserdashboardsteps.dateValidFormat();

	}

	@When("I click on account name for Draft Application")
	public void draftApplicationAccountNameClick() throws Throwable
	{
		actadviserdashboardsteps.draftApplicationAccountNameClick();

	}

	@Then("I see 3 most recent draft applications are shown in the descending date")
	public void descendingDateVerify() throws Throwable
	{

		actadviserdashboardsteps.descendingDateVerify();

	}

	@Then("I see header and no results message as text for Draft Application")
	public void alternateScenarioErrorTextSavedOrder() throws Throwable
	{
		actadviserdashboardsteps.alternateScenarioErrorTextDraftApplication();

	}

	@When("I click on add account for Draft Application")
	public void alternateScenarioButtonDraftApplicationClick() throws Throwable
	{

		actadviserdashboardsteps.alternateScenarioButtonDraftApplicationClick();

	}

	@Then("I see header and result count as text for saved order")
	public void headerAndResultCountSavedOrdersPresent() throws Throwable
	{
		actadviserdashboardsteps.headerAndResultCountSavedOrdersPresent();

	}

	@Then("I see account name as hyperlink for saved order")
	public void accountNameHyperlinkSavedOrdersPresent() throws Throwable
	{
		actadviserdashboardsteps.accountNameHyperlinkSavedOrdersPresent();

	}

	@Then("I see description text below account name for saved order")
	public void descriptionTextSavedOrdersPresent() throws Throwable
	{
		actadviserdashboardsteps.descriptionTextSavedOrdersPresent();

	}

	@Then("I see saved order date")
	public void dateSavedOrdersPresent() throws Throwable
	{
		actadviserdashboardsteps.dateSavedOrdersPresent();

	}

	@Then("I see valid result count format for saved order")
	public void savedOrderShowingPatternMatch() throws Throwable
	{
		actadviserdashboardsteps.savedOrderShowingPatternMatch();

	}

	@Then("I see valid date format for saved order")
	public void savedOrderDatePatternMatch() throws Throwable
	{
		actadviserdashboardsteps.savedOrderDatePatternMatch();

	}

	@When("I click on account name for saved order")
	public void accountNameSavedOrderClick() throws Throwable
	{

		actadviserdashboardsteps.accountNameSavedOrderClick();

	}

	@Then("I see upto 3 most recent saved orders are shown in the descending date")
	public void descendingDateSavedOrdersdVerify() throws Throwable
	{

		actadviserdashboardsteps.descendingDateSavedOrdersVerify();

	}

	@Then("I see header and no results message as text for saved order")
	public void alternateScenarionErrorTextSavedOrder() throws Throwable
	{
		actadviserdashboardsteps.alternateScenarioErrorTextSavedOrder();

	}

	@Then("I see place an order as link for saved order")
	public void altScnSoButtonVerify() throws Throwable
	{
		actadviserdashboardsteps.alternateScenarioSavedOrdersButtonVerify();

	}

	@When("I click on place an order for saved order")
	public void altScnButtonSavedOrderClick() throws Throwable
	{

		actadviserdashboardsteps.alternateScenarioButtonSavedOrderClick();

	}

	@Then("I see header as hyperlink for Maturing term deposits")
	public void headerMaturingTermDepositsPresent() throws Throwable
	{
		actadviserdashboardsteps.headerMaturingTermDepositsPresent();

	}

	@Then("I see account name as hyperlink for Maturing term deposits")
	public void accountNameHyperlinkMaturingTermPresent() throws Throwable
	{
		actadviserdashboardsteps.accountNameHyperlinkMaturingTermPresent();

	}

	@Then("I see result count text for Maturing term deposits")
	public void resultCountMaturingTermPresent() throws Throwable
	{
		actadviserdashboardsteps.resultCountMaturingTermPresent();

	}

	@Then("I see maturing term deposit text and TD calculator")
	public void descriptionTextMaturingTermAndTDCPresent() throws Throwable
	{
		actadviserdashboardsteps.descriptionTextMaturingTermAndTDCPresent();

	}

	@Then("I see valid result count format for Maturing term deposits")
	public void maturingTermShowingPatternMatch() throws Throwable
	{
		actadviserdashboardsteps.maturingTermShowingPatternMatch();

	}

	@Then("I see valid Maturity date format")
	public void maturingTermDatePatternMatch() throws Throwable
	{

		actadviserdashboardsteps.maturingTermDatePatternMatch();

	}

	@Then("I see valid days left format")
	public void maturingTermDaysLeftPatternMatch() throws Throwable
	{

		actadviserdashboardsteps.maturingTermDaysLeftPatternMatch();

	}

	@When("I click on header for Maturing term deposits")
	public void headerMaturingTermClick() throws Throwable
	{

		actadviserdashboardsteps.headerMaturingTermClick();

	}

	@When("I click on account name for Maturing term deposits")
	public void accountNameMaturingTermClick() throws Throwable
	{

		actadviserdashboardsteps.accountNameMaturingTermClick();

	}

	@When("I click on TD calculator")
	public void calculatorMaturingTermClick() throws Throwable
	{

		actadviserdashboardsteps.calculatorMaturingTermClick();

	}

	@Then("I see amount with valid 2 decimal")
	public void maturingTermDecimalPatternMatch() throws Throwable
	{

		actadviserdashboardsteps.maturingTermDecimalPatternMatch();

	}

	@Then("I see 3 of the earliest maturing TDs in ascending order of their maturity")
	public void descendingDateMaturingTermDisplayVerify() throws Throwable
	{

		actadviserdashboardsteps.descendingDateMaturingTermDisplayVerify();

	}

	@Then("I see header and no results message as text for Maturing term deposits")
	public void alternateScenario1MaturingTdDisplayVerify() throws Throwable
	{

		actadviserdashboardsteps.alternateScenario1MaturingTdDisplayVerify();

	}

	@Then("I see no result message2 as text for Maturing term deposits")
	public void alternateScenario2MaturingTdDisplayVerify() throws Throwable
	{

		actadviserdashboardsteps.alternateScenario2MaturingTdDisplayVerify();

	}

	@Then("I see help information icon")
	public void helpIconKeyActivityPresent() throws Throwable
	{
		actadviserdashboardsteps.helpIconKeyActivityPresent();

	}

	@Then("I see activity type and client account name as hyperlink")
	public void activityTypeAndClientAccountNameHyperlinkKeyActivityPresent() throws Throwable
	{
		actadviserdashboardsteps.activityTypeAndClientAccountNameHyperlinkKeyActivityPresent();

	}

	@Then("I see description as text")
	public void descriptionKeyActivity() throws Throwable
	{
		actadviserdashboardsteps.descriptionKeyActivity();

	}

	@Then("I see priority indicator")
	public void priorityIndicatorKeyActivityPresent() throws Throwable
	{
		actadviserdashboardsteps.priorityIndicatorKeyActivityPresent();

	}

	@Then("I see activity date and activity time stamp")
	public void activityDateAndTimeStampKeyActivity() throws Throwable
	{
		actadviserdashboardsteps.activityDateAndTimeStampKeyActivity();

	}

	@Then("I see On-boarding tracking and Order status as action icon")
	public void actionIconPresenceVerify() throws Throwable
	{

		actadviserdashboardsteps.actionIconPresenceVerify();

	}

	@When("I click on account type for Key activity")
	public void accountTypeKeyActivityClick() throws Throwable
	{

		actadviserdashboardsteps.accountTypeKeyActivityClick();

	}

	@When("I click on client account name for Key activity")
	public void accountNameKeyActivityClick() throws Throwable
	{

		actadviserdashboardsteps.accountNameKeyActivityClick();

	}

	@Then("I see valid activity date format")
	public void keyActivityDatePatternMatch() throws Throwable
	{

		actadviserdashboardsteps.keyActivityDatePatternMatch();

	}

	@When("I click on On-boarding tracking")
	public void onboardingButtonClick() throws Throwable
	{

		actadviserdashboardsteps.onboardingButtonClick();

	}

	@When("I click on Order status tracking")
	public void orderStatusButtonClick() throws Throwable
	{

		actadviserdashboardsteps.orderStatusButtonClick();

	}

	@Then("I see 10 of the most recent activities in descending time order")
	public void descendingDateKeyActivityDisplayVerify() throws Throwable
	{

		actadviserdashboardsteps.descendingDateKeyActivityDisplayVerify();

	}

	@When("I mousehover on help icon")
	public void mouseHoverKeyActivity() throws Throwable
	{

		actadviserdashboardsteps.mouseHoverKeyActivity();

	}

	@Then("I see header and no results message as text for Key activity")
	public void alternateScenarioKeyActivityDisplayVerify() throws Throwable
	{

		actadviserdashboardsteps.alternateScenarioKeyActivityDisplayVerify();

	}

	@Then("I see Dashboard disclaimers text")
	public void disclaimersTextVerify() throws Throwable
	{

		actadviserdashboardsteps.disclaimersTextVerify();
	}

	@Then("I see 10 of the most recent activities as :$keyActivities")
	public void displayKeyRecords(ExamplesTable activityTable) throws Throwable
	{

		int rownum = activityTable.getRowCount();

		List <String> headerName = activityTable.getHeaders();
		List <Map> tableActual = actadviserdashboardsteps.testCountKeyActivity(headerName);
		for (int i = 0; i < rownum; i++)
		{
			Map <String, String> expected = activityTable.getRow(i);
			Map <String, String> actual = tableActual.get(i);

			for (String key : expected.keySet())
			{

				assertEquals(actual.get(key), expected.get(key));
			}

		}

	}
}
