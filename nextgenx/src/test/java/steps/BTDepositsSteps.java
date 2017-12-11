package steps;

import static junit.framework.Assert.*;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import pages.BTDeposits.BTDepositsPage;
import pages.logon.LogonPage;

public class BTDepositsSteps extends ScenarioSteps
{

	LogonPage logonpage;
	BTDepositsPage btdepositspage;

	public BTDepositsSteps(Pages pages)
	{
		super(pages);
	}

	@Step
	public void navigate_To_Deposit() throws Throwable
	{
		logonpage.gotopage("Payments");
		btdepositspage.gettbMakeADeposit().click();

	}

	@Step
	public void headerMoveMoney()
	{
		btdepositspage.getheaderMoveMoney();

	}

	@Step
	public void makeADepositField()
	{
		assertTrue(btdepositspage.gettxtMakeDepositFrom().isDisplayed());
	}

	@Step
	public void amountField()
	{
		assertTrue(btdepositspage.gettxtAmount().isDisplayed());
	}

	@Step
	public void dateField()
	{
		assertTrue(btdepositspage.gettxtDate().isDisplayed());
	}

	@Step
	public void descriptionField()
	{
		assertTrue(btdepositspage.gettxtDescription().isDisplayed());
		String lblDescription = btdepositspage.getlblDescription().getText();
		assertEquals(lblDescription, "Optional");

	}

	@Step
	public void conditionalStringOf18Char()
	{

		String lblDescriptionTextLimit = btdepositspage.gettxt18Char().getText();
		assertEquals(lblDescriptionTextLimit, "Maximum 18 letters and numbers");
	}

	@Step
	public void repeatDepositCheckBoxAndNextButton()
	{
		assertTrue(btdepositspage.getchkRepeatDeposit().isDisplayed());
		assertTrue(btdepositspage.getbtnNext().isDisplayed());
	}

	@Step
	public void max18CharCanBeEntered()
	{
		btdepositspage.gettxtDescription().sendKeys("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		boolean bLengthOfDescriptionCharacters = btdepositspage.gettxtDescription().getText().length() == 18;
		assertTrue(bLengthOfDescriptionCharacters);
	}

	@Step
	public void lnkClearAll()
	{
		assertTrue(btdepositspage.getlnkClearAll().isDisplayed());
	}

	@Step
	public void clickClearAll()
	{
		btdepositspage.getlnkClearAll().click();
	}

	@Step
	public void ckeckAllFieldsAreEmpty()
	{
		assertTrue(btdepositspage.gettxtAmount().getText().length() < 1);
		assertTrue(btdepositspage.gettxtMakeDepositFrom().getText().length() < 1);
		assertTrue(btdepositspage.gettxtDate().getText().length() < 1);
	}

	@Step
	public void clickNextButton()
	{
		btdepositspage.getbtnNext().click();
	}

	@Step
	public void checkErrorMesages()
	{
		assertEquals(btdepositspage.gettextMakeDepositFromFieldErrorMsg().getText(), "Please enter or select an account");
		assertEquals(btdepositspage.gettextAmountFieldErrorMsg().getText(), "Please enter an amount");
		assertEquals(btdepositspage.gettextDateFieldErrorMsg().getText(), "Deposit date is required.");
	}

	@Step
	public void enterValidDataInFieldsMakeDepositAmountDate()
	{
		btdepositspage.gettxtMakeDepositFrom().sendKeys("linkedNick, 12006, 2222222");
		btdepositspage.gettxtAmount().sendKeys("10");
		btdepositspage.gettxtDate().sendKeys("20.02.20");
	}

	@Step
	public void checkRepeatDepositEnterDateRepeatEndDateOrNumber()
	{
		btdepositspage.gettxtMakeDepositFrom().sendKeys();
		btdepositspage.getselectMonthlyInRepeatEvery().click();
		btdepositspage.getselectSetEndDateInEndRepeat().click();
		btdepositspage.getdtRepeatEndDate().sendKeys("20.2.20");
		btdepositspage.gettxtDescription().sendKeys("Description Auto");
		btdepositspage.getbtnNext().click();
	}

	@Step
	public void checkHeaderOfConfirmAndPay()
	{
		assertEquals(btdepositspage.getTextConfirmAndDeposit().getText(), "Confirm and deposit");
		assertEquals(btdepositspage.getTextAmountEnteredInForm().getText(), "10.00");
	}

	@Step
	public void checkFromDetails()
	{
		assertEquals(btdepositspage.getTextAccountNameFrom().getText(), "linkedAcc");
		assertEquals(btdepositspage.getTextBSBNumberFrom().getText(), "BSB 12006");
		assertEquals(btdepositspage.getTextAccountNumberFrom().getText(), "Account no.2222222");
	}

	@Step
	public void checkToDetails()
	{
		assertEquals(btdepositspage.getTextAccountNameTo().getText(), "Adrian Demo Smith");
		assertEquals(btdepositspage.getTextBSBNumberTo().getText(), "BSB 262-786");
		assertEquals(btdepositspage.getTextAccountNumberTo().getText(), "Account no.36846");
	}

	@Step
	public void checkDateAndRepeatDetails()
	{
		assertEquals(btdepositspage.getTextDate().getText(), "20 Feb 2020	");
		assertEquals(btdepositspage.getTextRepeatFrequency().getText(), "Monthly Ends On");
		assertEquals(btdepositspage.getTextRepeatDate().getText(), "20 Feb 2020 ");
	}

	@Step
	public void checkDescriptionFieldAndBtnDepositAndChange()
	{
		assertTrue(btdepositspage.getBtnPay().isDisplayed());
		assertTrue(btdepositspage.getTextRepeatFrequency().isDisplayed());
		assertEquals(btdepositspage.getTextDescription().getText(), "Description Auto");
	}

	@Step
	public void click_On_DepositButton()
	{

		btdepositspage.getButtonDeposit().click();

	}

	@Step
	public void navigate_To_DepositSuccessfulSubmit()
	{

		//getDriver().get("http://localhost:9080/ng/secure/app/#ng/account/movemoney/payments/receipt");
		//getDriver().get("http://dwgps0022.btfin.com:8120/nextgen/secure/app/#ng/privacystatement");
		btdepositspage.getDepositSuccessfulSubmitHeader().getText().contains("submitted successfully");

	}

	@Step
	public void deposit_Successful_SubmitHeader()
	{
		String depositSuccessfulText = btdepositspage.getDepositSuccessfulSubmitHeader().getText();
		String temp[] = depositSuccessfulText.split(" ");
		String depositSuccessfulSplitText = temp[0] + " " + temp[1] + " " + temp[3] + " " + temp[4];
		System.out.println("Deposit successful sumbit text:" + depositSuccessfulSplitText);
		assertEquals(depositSuccessfulSplitText, "Deposit of submitted successfully");
		assertTrue(btdepositspage.getDepositSuccessfulSubmitHeader().isDisplayed());

	}

	@Step
	public void deposit_Successful_dateField()
	{
		String dateText = btdepositspage.getReceiptTextDate().getText();
		assertEquals(dateText, "Date");
		assertTrue(btdepositspage.getReceiptTextDate().isDisplayed());
	}

	@Step
	public void deposit_Successful_RepeatsField()
	{
		String repeatsText = btdepositspage.getReceiptTextRepeats().getText();
		assertEquals(repeatsText, "Repeats");
		assertTrue(btdepositspage.getReceiptTextRepeats().isDisplayed());
	}

	@Step
	public void deposit_Successful_DescriptionField()
	{
		String descriptionText = btdepositspage.getReceiptTextDescription().getText();
		assertEquals(descriptionText, "Description");
		assertTrue(btdepositspage.getReceiptTextDescription().isDisplayed());
	}

	@Step
	public void deposit_Successful_ReceiptNoField()
	{
		String receiptNoText = btdepositspage.getReceiptTextReceiptNo().getText();
		assertEquals(receiptNoText, "Receipt no");
		assertTrue(btdepositspage.getReceiptTextReceiptNo().isDisplayed());
	}

	@Step
	public void deposit_Successful_DownloadButton()
	{
		String downloadBtn = btdepositspage.getReceiptTextReceiptNo().getText();
		assertEquals(downloadBtn, "Download");
		assertTrue(btdepositspage.getReceiptTextReceiptNo().isDisplayed());
	}

	@Step
	public void link_Deposit_See_All_Transactions()
	{
		String seeAllTransactionsLink = btdepositspage.getlinkSeeAllTransactions().getText();
		assertEquals(seeAllTransactionsLink, "See all transactions");
		assertTrue(btdepositspage.getlinkSeeAllTransactions().isDisplayed());
	}

	@Step
	public void click_Deposit_See_All_Transactions()
	{
		btdepositspage.getlinkSeeAllTransactions().click();

	}

	@Step
	public void open_Deposit_See_All_Transactions()
	{
		assertTrue(getDriver().getCurrentUrl().contains(""));
		getDriver().navigate().back();

	}

	@Step
	public void link_Make_Another_Deposit()
	{
		String makeAnotherDepositLink = btdepositspage.getLinkMakeAnotherPayment().getText();
		assertEquals(makeAnotherDepositLink, "Make another deposit");
		assertTrue(btdepositspage.getLinkMakeAnotherPayment().isDisplayed());
	}

	@Step
	public void click_Make_Another_Deposit()
	{
		btdepositspage.getLinkMakeAnotherPayment().click();

	}

	@Step
	public void open_Make_Another_Deposit()
	{
		assertTrue(getDriver().getCurrentUrl().contains(""));
		getDriver().navigate().back();

	}

	@Step
	public void link_Deposit_See_Scheduled_Transactions()
	{
		String scheduledTransactionLink = btdepositspage.getLinkSeeScheduledTransactions().getText();
		assertEquals(scheduledTransactionLink, "See scheduled payments");
		assertTrue(btdepositspage.getLinkSeeScheduledTransactions().isDisplayed());
	}

	@Step
	public void click_Deposit_See_Scheduled_Transactions()
	{
		btdepositspage.getLinkSeeScheduledTransactions().click();

	}

	@Step
	public void open_Deposit_See_Scheduled_Transactions()
	{
		assertTrue(getDriver().getCurrentUrl().contains(""));
		getDriver().navigate().back();

	}
}
