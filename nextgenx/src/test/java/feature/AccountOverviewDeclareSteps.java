package feature;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;

import steps.AccountOverviewDetailsSteps;

public class AccountOverviewDeclareSteps
{
	@Steps
	AccountOverviewDetailsSteps account;

	@Given("I am on Login Page default")
	public void loginAccountOverview() throws Throwable
	{
		account.starts_logon_fee();

	}

	@When("I navigate to Account overview screen")
	public void openAccountOverviewPage() throws Throwable
	{
		account.navigate_account_screen();
	}

	@Then("I see Recent cash transactions hyperlink")
	public void verify_recentcash() throws Throwable
	{
		account.Verify_recentcash_hyperlink();
	}

	@When("I click on Recent cash transaction hyperlink")
	public void click_recent_transactions() throws Throwable
	{
		account.clickRecentTransactions();
	}

	@Then("I see Transaction history report screen")
	public void verify_transaction_history_screen() throws Throwable
	{
		account.verifytransactionscreen();
	}

	@Then("I see records of the 10 most recent cash transactions as: $transaction")
	public void displayrecords(ExamplesTable activityTable) throws Throwable
	{
		Thread.sleep(4000);
		account.displayRecentRecords();

		int rownum = activityTable.getRowCount();

		List <String> headerName = activityTable.getHeaders();
		List <Map> tableActual = account.testCount(headerName);
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

	@Then("I see valid cash transaction effective date $transaction")
	public void Verify_cashtransaction_date(ExamplesTable transaction) throws Throwable
	{
		List <Map> actual = account.testCount1();
		for (int i = 0; i < transaction.getRowCount(); i++)
		{
			Map <String, String> Exmap = transaction.getRow(i);
			Map <String, String> Axmap = actual.get(i);
			assertEquals(Axmap.get("Date"), Exmap.get("Date"));
		}

	}

	@Then("I see a valid date format for cash transaction effective date")
	public void verify_dateformat_Recenttransaction() throws Throwable
	{
		account.Verify_dateformat_RecentTransaction();
	}

	@Then("I see cash deposit transactions in ascending order as $transaction")
	public void displaySchedule(ExamplesTable transaction) throws Throwable
	{

		List <Map> actual = account.testCount1();
		for (int i = 0; i < transaction.getRowCount(); i++)
		{
			Map <String, String> Exmap = transaction.getRow(i);
			Map <String, String> Axmap = actual.get(i);
			assertEquals(Axmap.get("Date"), Exmap.get("Date"));
			assertEquals(Axmap.get("Description"), Exmap.get("Description"));
			assertEquals(Axmap.get("cash"), Exmap.get("cash"));
		}

	}

	@Then("I see an icon for uncleared transaction")
	public void Verify_icon_transaction() throws Throwable
	{
		account.verify_icon_transaction();
	}

	@When("I hover on the icon for the uncleared transaction")
	public void hover_icon_transaction() throws Throwable
	{
		account.hover_icon();
	}

	@Then("I see text displayed as Uncleared")
	public void Verify_text_hovericon() throws Throwable
	{
		account.verify_text_iconhover();
	}

	@Then("I see scheduled deposit transaction date in valid format")
	public void Verify_deposit_transactiondate() throws Throwable
	{
		account.verify_deposit_date();
	}

	@Then("I see Cash Deposit header under Schedule transactions as $header")
	public void Verify_header_Cashdeposit(String header) throws Throwable
	{
		account.verify_header_cashdeposit(header);
	}

	@Then("I see frequency transaction type and account name under Schedule transaction as $transaction")
	public void displaydata(ExamplesTable transaction) throws Throwable
	{
		List <Map> actual = account.testCount1();
		for (int i = 0; i < transaction.getRowCount(); i++)
		{
			Map <String, String> Exmap = transaction.getRow(i);
			Map <String, String> Axmap = actual.get(i);
			assertEquals(Axmap.get("Description"), Exmap.get("Description"));
		}

	}

	@Then("I see the positive value of Dollar amount for Cash Deposits Schedule transactions")
	public void Dollar_cash_positive() throws Throwable
	{
		account.verify_cashdeposit_positive();
	}

	@Then("I see the negative value of Dollar amount for scheduled Payment transaction")
	public void verify_negative_cashpayment() throws Throwable
	{
		account.verify_cashpayment_negative();
	}

	@Then("I see logo icon of Term deposit")
	public void verify_icon_maturityterm() throws Throwable
	{
		account.Verify_icon_maturityterm();
	}

	@Then("I see error message as There are no upcoming cash deposits")
	public void Verify_nocash() throws Throwable
	{
		account.Verify_nocash();
	}

	@Then("I see error message as There are no upcoming cash payments")
	public void Verify_nocashPayments() throws Throwable
	{
		account.Verify_nocashpayments();
	}

	@Then("I see No days left text for a maturing term deposit")
	public void Verify_daystext_maturityterm()
	{
		account.Verify_daystext_maturityterm();
	}

	@Then("Verify the hyperlinks for Orders in progress Section")
	public void verify_links_Orders() throws Throwable
	{
		account.verifyLinksOrders();
	}

	@Then("I see a Message Section")
	public void verify_Message_Section() throws Throwable
	{
		account.verifyMessageSection();
	}

	@Then("I see Scheduled transactions hyperlink")
	public void verify_Scheduledtransactio_link() throws Throwable
	{
		account.verifyscheduledtransactionlink();
	}

	@Then("I see records of term deposits maturing in the next 90 days in ascending order as $transaction")
	public void display_maturityterm(ExamplesTable transaction) throws Throwable
	{
		List <Map> actual = account.testMaturitycount();
		for (int i = 0; i < transaction.getRowCount(); i++)
		{
			Map <String, String> Exmap = transaction.getRow(i);
			Map <String, String> Axmap = actual.get(i);
			assertEquals(Axmap.get("Date"), Exmap.get("Date"));
			assertEquals(Axmap.get("Cash"), Exmap.get("Cash"));
		}

		account.verifyascendingmaturitydate();
	}

	@Then("I see Orders in progress header as hyperlink")
	public void verify_ordersInProgressHyperlink() throws Throwable
	{
		account.verify_ordersInProgressHyperlink();
	}

	@When("I click on Orders in progress link")
	public void click_order_in_progress() throws Throwable
	{
		account.clickOrderInProgressHyperlink();
	}

	@Then("I see Buys Applications as hyperlink")
	public void verify_buyApplicationsHyperlink() throws Throwable
	{
		account.verify_buyApplicationsHyperlink();
	}

	@When("I click on Buys Applications link")
	public void click_buy_application_link() throws Throwable
	{
		account.clickBuyApplicationHyperlink();
	}

	@Then("I see Sells Redemptions header as hyperlink")
	public void verify_sellsRedemptionHyperlink() throws Throwable
	{
		account.verify_sellsRedemptionHyperlink();
	}

	@When("I click on Sells Redemptions link")
	public void click_sells_redemption_link() throws Throwable
	{
		account.clickSellsRedemptionHyperlink();
	}

	@Then("I see In Progress order amounts for order types Buys Applications as $Amount")
	public void zeroStateOrdersInProgress(String Amount) throws Throwable
	{
		account.zeroStateOrdersInProgress(Amount);
	}

	@Then("I see In Progress order amounts for order types Sells Redemptions as $Amount")
	public void zeroStateOrdersInProgressRedemption(String Amount) throws Throwable
	{
		account.zeroStateOrdersInProgressRedemption(Amount);
	}

}
