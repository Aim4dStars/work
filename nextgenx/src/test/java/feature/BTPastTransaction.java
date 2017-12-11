package feature;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import steps.BTPastTransactionSteps;

public class BTPastTransaction
{
	@Steps
	BTPastTransactionSteps onBTPastTransactionStep;

	@When("I navigates to Past transaction page")
	public void isPastTransactionPage() throws Throwable
	{
		onBTPastTransactionStep.navigate_To_PastTransaction();

	}

	@Then("I see page header Transactions")
	public void verify_header_transactions()
	{
		onBTPastTransactionStep.past_Transaction_HeaderText();
	}

	@Then("I see tab All transactions")
	public void verify_tab_all_transactions()
	{
		onBTPastTransactionStep.tab_All_Transactions();
	}

	@Then("I see tab Cash statement")
	public void verify_tab_bt_cash_transactions()
	{
		onBTPastTransactionStep.tab_BT_Cash_Transactions();
	}

	@Then("I see search option to select date range")
	public void verify_date_seach_field()
	{
		onBTPastTransactionStep.date_Search_Element();
	}

	@Then("I see Date pickers as To and From")
	public void verify_date_pickers()
	{
		onBTPastTransactionStep.date_Element_From_Field();
		onBTPastTransactionStep.date_Element_To_Field();
	}

	@Then("I see Last update time in AEST")
	public void verify_last_update_time()
	{
		onBTPastTransactionStep.time_Update_Text();
	}

	@Then("I see rows in the table above the table in the format Showing <<range of rows>> of <<Total number of rows>>")
	public void verify_row_count()
	{
		onBTPastTransactionStep.show_Result_Count();
	}

	@Then("I see past transaction table header as Date, Description, Credit $, Debit $ and Balance $")
	public void verify_table_headers()
	{
		onBTPastTransactionStep.table_Header_Date();
		onBTPastTransactionStep.table_Header_Description();
		onBTPastTransactionStep.table_Header_Credit();
		onBTPastTransactionStep.table_Header_Debit();
		onBTPastTransactionStep.table_Header_Balance();
	}

	@Then("I see Disclaimer")
	public void verify_disclaimer()
	{
		onBTPastTransactionStep.disclaimer_Text();
	}

	@When("I click on arrow of the past transaction record to see expanded state")
	public void click_to_expand_row()
	{
		onBTPastTransactionStep.click_Expand_Record();
	}

	@Then("I see header Payment of along with amount if its debit transaction")
	public void verify_header_payment()
	{
		onBTPastTransactionStep.payment_Of_Text();
	}

	@Then("I see header Deposit of along with amount if its credit transaction")
	public void verify_header_deposit()
	{
		onBTPastTransactionStep.deposit_Of_Text();
	}

	@Then("I see fields as To, From, Date, Receipt no. and Download button")
	public void verify_expenaded_fields()
	{
		onBTPastTransactionStep.expand_From_Text();
		onBTPastTransactionStep.expand_To_Text();
		onBTPastTransactionStep.expand_Date_Text();
		onBTPastTransactionStep.expand_Receipt_No_Text();
		onBTPastTransactionStep.button_Download_Text();
	}

	@When("I click on arrow of the transaction record again")
	public void click_to_collaps_row()
	{
		onBTPastTransactionStep.click_Close_Expand_Record();
	}

	@Then("I see detailed view will be closed")
	public void verify_closed_expanded_view()
	{
		onBTPastTransactionStep.collapsed_Record_Closed();
	}
}
