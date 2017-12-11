package steps;

//import static org.junit.Assert.*;
import static junit.framework.Assert.*;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

import org.openqa.selenium.By;

import pages.BTTransactions.BTPastTransactionPage;
import pages.logon.LogonPage;

public class BTPastTransactionSteps extends ScenarioSteps
{

	LogonPage logonpage;
	BTPastTransactionPage btpasttransactionpage;

	public BTPastTransactionSteps(Pages pages)
	{
		super(pages);
	}

	@Step
	public void navigate_To_PastTransaction() throws Throwable
	{
		logonpage.gotopage("Past Transactions");

	}

	@Step
	public void past_Transaction_HeaderText()
	{
		String pastTransactionHeaderText = btpasttransactionpage.getHeaderPastTransaction().getText();
		assertEquals(pastTransactionHeaderText, "Transactions");
		assertTrue(btpasttransactionpage.getHeaderPastTransaction().isDisplayed());

	}

	@Step
	public void tab_All_Transactions()
	{
		String tabAllTransactionsText = btpasttransactionpage.getTabAllTransaction().getText();
		assertEquals(tabAllTransactionsText, "All transactions");
		assertTrue(btpasttransactionpage.getTabAllTransaction().isDisplayed());

	}

	@Step
	public void tab_BT_Cash_Transactions()
	{
		String tabBTCashTransactionsText = btpasttransactionpage.getTabBTCashTransactions().getText();
		assertEquals(tabBTCashTransactionsText, "Cash statement");
		assertTrue(btpasttransactionpage.getTabBTCashTransactions().isDisplayed());

	}

	@Step
	public void date_Search_Element()
	{
		assertTrue(btpasttransactionpage.getDateSearchElement().isDisplayed());

	}

	@Step
	public void date_Element_From_Field()
	{

		assertTrue(btpasttransactionpage.getDateFrom().isDisplayed());

	}

	@Step
	public void date_Element_To_Field()
	{
		assertTrue(btpasttransactionpage.getDateTo().isDisplayed());

	}

	@Step
	public void time_Update_Text()
	{
		String timeUpdateText = btpasttransactionpage.getTimeUpdates().getText();
		String temp[] = timeUpdateText.split(" ");
		String TimeUpdateSplitText = temp[0] + " " + temp[1] + " " + temp[3];
		System.out.println("Time update text:" + TimeUpdateSplitText);
		assertEquals(timeUpdateText, "Last updated AEST");
		assertTrue(btpasttransactionpage.getTimeUpdates().isDisplayed());

	}

	@Step
	public void show_Result_Count()
	{
		String ResCount = btpasttransactionpage.getShowingResultcount().getText();
		String temp[] = ResCount.split(" ");
		System.out.println("temp var 0: " + temp[0]);
		System.out.println("temp var 2: " + temp[2]);
		String partOfText = temp[0] + " " + temp[2];
		System.out.println("Part of text :+" + partOfText);
		assertEquals(partOfText, "Showing of");
		String Num1 = temp[1];
		String Num2 = temp[3];
		assertTrue(temp[1].contains("-"));
	}

	@Step
	public void table_Header_Date()
	{
		String tableHeaderDateText = btpasttransactionpage.getTableHeaderDate().getText();
		assertEquals(tableHeaderDateText, "Date");
		assertTrue(btpasttransactionpage.getTableHeaderDate().isDisplayed());

	}

	@Step
	public void table_Header_Description()
	{
		String tableHeaderDescriptionText = btpasttransactionpage.getTableHeaderDescription().getText();
		assertEquals(tableHeaderDescriptionText, "Date");
		assertTrue(btpasttransactionpage.getTableHeaderDescription().isDisplayed());

	}

	@Step
	public void table_Header_Credit()
	{
		String tableHeaderCreditText = btpasttransactionpage.getTableHeaderCredit().getText();
		assertEquals(tableHeaderCreditText, "Credit");
		assertTrue(btpasttransactionpage.getTableHeaderCredit().isDisplayed());

	}

	@Step
	public void table_Header_Debit()
	{
		String tableHeaderDebitText = btpasttransactionpage.getTableHeaderDebit().getText();
		assertEquals(tableHeaderDebitText, "Debit");
		assertTrue(btpasttransactionpage.getTableHeaderDebit().isDisplayed());

	}

	@Step
	public void table_Header_Balance()
	{
		String tableHeaderBalanceText = btpasttransactionpage.getTableHeaderBalance().getText();
		assertEquals(tableHeaderBalanceText, "Balance");
		assertTrue(btpasttransactionpage.getTableHeaderBalance().isDisplayed());

	}

	@Step
	public void disclaimer_Text()
	{
		String disclaimerText = btpasttransactionpage.getTextDisclaimer().getText();
		assertEquals(disclaimerText, "[Disclaimer]");
		assertTrue(btpasttransactionpage.getTextDisclaimer().isDisplayed());

	}

	@Step
	public void click_Expand_Record()
	{
		//String arrowXpath = "//div[@class='columns-41']/div/table/tbody/tr/td/a/span[2]";

		//String arrowXpath = "//div[@class='margin-bottom-2 mvc-cashtransactions']/table/tbody/tr/td";
		//driver.findElement(By.xpath(arrowXpath)).click();
		//driver.findElement(By.className(".expandable-row")).click();
		//driver.findElement(By.cssSelector(".expandable-row")).click();
		getDriver().findElement(By.xpath("//div[@class='columns-41']/div/table/tbody/tr/td/a/span[2]")).click();

	}

	@Step
	public void payment_Of_Text()
	{
		String txtPaymentOf = btpasttransactionpage.getTextPaymentOf().getText();
		assertEquals(txtPaymentOf, "Payment of");
		assertTrue(btpasttransactionpage.getTextPaymentOf().isDisplayed());

	}

	@Step
	public void deposit_Of_Text()
	{
		String txtDepositOf = btpasttransactionpage.getTextDepositOf().getText();
		assertEquals(txtDepositOf, "Deposit of");
		assertTrue(btpasttransactionpage.getTextDepositOf().isDisplayed());

	}

	@Step
	public void expand_From_Text()
	{
		String expandFromText = btpasttransactionpage.getExpandFromText().getText();
		assertEquals(expandFromText, "From");
		assertTrue(btpasttransactionpage.getExpandFromText().isDisplayed());

	}

	@Step
	public void expand_To_Text()
	{
		String expandToText = btpasttransactionpage.getExpandToText().getText();
		assertEquals(expandToText, "To");
		assertTrue(btpasttransactionpage.getExpandToText().isDisplayed());

	}

	@Step
	public void expand_Date_Text()
	{
		String expandDateText = btpasttransactionpage.getExpandDateText().getText();
		assertEquals(expandDateText, "Date");
		assertTrue(btpasttransactionpage.getExpandDateText().isDisplayed());

	}

	@Step
	public void expand_Receipt_No_Text()
	{
		String expandReceiptNoText = btpasttransactionpage.getExpandReciptNoText().getText();
		assertEquals(expandReceiptNoText, "Receipt no.");
		assertTrue(btpasttransactionpage.getExpandReciptNoText().isDisplayed());

	}

	public void button_Download_Text()
	{
		String buttonDownlaodText = btpasttransactionpage.getButtonDownload().getText();
		assertEquals(buttonDownlaodText, "Download");
		assertTrue(btpasttransactionpage.getButtonDownload().isDisplayed());

	}

	public void click_Close_Expand_Record()
	{
		getDriver().findElement(By.className("expandable-row")).click();

	}

	public void collapsed_Record_Closed()
	{
		assertFalse(btpasttransactionpage.getClosedCollapse().isDisplayed());

	}

}
