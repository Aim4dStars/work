package feature;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import steps.BTScheduledTransactionsSteps;

public class BTScheduledTransactions
{

	@Steps
	BTScheduledTransactionsSteps btScheduledTransactionStep;

	@When("I navigate to Schedule transaction screen")
	public void navigateToScheduledTransactionPage() throws Throwable
	{
		btScheduledTransactionStep.openScheduledTransactionsPage();

	}

	@Then("I see Scheduled Transaction screen")
	public void verifyScheduledTransactionsPage() throws Throwable
	{
		btScheduledTransactionStep.Scheduled_Transaction_HeaderText();

	}

	@Then("I see message Please note that scheduled transactions are executed daily at 5:00pm AEST")
	public void verifyScheduledTransactionsMessage()
	{
		btScheduledTransactionStep.Scheduled_Transaction_Message_Text();

	}

	@Then("I see number of records in format Showing 1-3 of 3")
	public void verifyPaginationFormat()
	{
		btScheduledTransactionStep.ShowingResultcount();

	}

	@Then("I see table column Next due, Description, Repeats, Credit, Debit")
	public void verifyScheduledTransactionsTableColumn()
	{
		btScheduledTransactionStep.Table_column_Nextdue_Text();
		btScheduledTransactionStep.Table_column_Description_Text();
		btScheduledTransactionStep.Table_column_Repeat_Text();
		btScheduledTransactionStep.Table_column_Credit_Text();
		btScheduledTransactionStep.Table_column_Debit_Text();

	}

	@Then("I see Show More link")
	public void verifyShowMoreLink()
	{
		btScheduledTransactionStep.Show_More_Link();

	}

	@When("I click on arrow of scheduled transaction record")
	public void clickOnRecordToExpand()
	{
		//btScheduledTransactionStep.Clicking_See_More_Link();
		btScheduledTransactionStep.Expanding_Record();

	}

	@Then("I see detailed view of record")
	public void verifyExpansionOfRecord()
	{
		btScheduledTransactionStep.Verifying_Page_Expanded();

	}

	@Then("I see detailed record with Deposit of, scheduled, From, To, Date, Repeats, First payment, Last payment, Description, Receipt no., Stop schedule, download")
	public void verifyingSubTitleOfTransactionRecord() throws Throwable
	{
		btScheduledTransactionStep.Deposit_Of_Schedule_Text_Format();
		btScheduledTransactionStep.Checking_Subtitle_From_To_Of_Record_After_Expansion();
		btScheduledTransactionStep.Checking_Subtitle_Of_Record_After_Expansion();
		btScheduledTransactionStep.Stop_Schedule_Button();
		btScheduledTransactionStep.Download_Button();

	}

	@When("I click again on arrow of scheduled transaction")
	public void clickingToCollapseTheRecord()
	{
		btScheduledTransactionStep.Expanding_Record();

	}

	@Then("I see no detailed view of record when collapse")
	public void clickingOnFailedTransaction()
	{
		btScheduledTransactionStep.Verifying_Page_Collapsed();

	}

	@When("I click on Stop schedule button")
	public void clickOnStopScheduleButton()
	{
		btScheduledTransactionStep.Click_On_StopSchedule_Button();

	}

	@Then("I see pop-up message with message text Stop schedule? Are you sure you want to stop this series of scheduled <payments/deposits>?")
	public void VerifyStopScheduleHeaderMessageAndTitle()
	{
		btScheduledTransactionStep.StopSchedule_Header_Text();
		btScheduledTransactionStep.StopSchedule_Message_Text();

	}

	@Then("I see button Yes")
	public void VerifyButtonYes()
	{
		btScheduledTransactionStep.Yes_Button_Enabled();

	}

	@Then("I see No button link")
	public void VerifyButtonNo()
	{
		btScheduledTransactionStep.Yes_Button_Enabled();

	}

	@Then("I see cross icon")
	public void VerifyCrossIcon()
	{
		btScheduledTransactionStep.CrossIcon_Icon_Displayed();

	}

	@When("I click on cross icon of stops Schedule pop-up")
	public void whenClickedOnCrossIcon()
	{
		btScheduledTransactionStep.Click_On_CrossIcon();

	}

	@Then("I see stops Schedule pop-up get closed")
	public void VerifyingStopScheduleModalGetsClosed()
	{
		btScheduledTransactionStep.StopSchedule_Header_Text_NotDisplayed();
	}

	@When("I click on No link of stops Schedule pop-up")
	public void whenClickedOnButtonNoLink()
	{
		btScheduledTransactionStep.Stop_Schedule_Button();
		btScheduledTransactionStep.Click_No_Button();

	}

	@When("I click on button Yes")
	public void whenClickedOnButtonYes()
	{
		btScheduledTransactionStep.Stop_Schedule_Button();
		btScheduledTransactionStep.Click_on_Yes_Button();

	}
}
