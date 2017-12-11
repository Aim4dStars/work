package feature;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import steps.BTDepositsSteps;

public class BTDeposits
{

	BTDepositsSteps btDepositsSteps;

	@When("I select Make a deposit tab")
	public void isMakeDepositPage() throws Throwable
	{
		btDepositsSteps.navigate_To_Deposit();
	}

	@Then("I see list box as Make deposit from, fields as Amount, Date populated with current date, description as optional field")
	public void verifyMakeADepositElements()
	{
		btDepositsSteps.makeADepositField();
		btDepositsSteps.amountField();
		btDepositsSteps.dateField();
		btDepositsSteps.descriptionField();
	}

	@Then("I see maximum 18 character can be entered in Optional Description field")
	public void verifyTextasMax18Char()
	{
		btDepositsSteps.max18CharCanBeEntered();
	}

	@Then("I see Repeat deposit check box, Next button")
	public void verifyRepeatCheckBoxAndNextButton()
	{
		btDepositsSteps.repeatDepositCheckBoxAndNextButton();
	}

	@Then("I see text as Maximum 18 letters and numbers, under Description field")
	public void conditionOf18Char()
	{
		btDepositsSteps.conditionalStringOf18Char();
	}

	@Then("I see clear all link")
	public void verifyClearAllLink()
	{
		btDepositsSteps.conditionalStringOf18Char();
	}

	@When("I click on Move money link on left side panel")
	public void NavigateToDepositPage()
	{
		//btDepositsSteps.navigate_To_Deposit();
	}

	@Then("I see header Move money")
	public void verifyHeaderMoveMoney()
	{
		btDepositsSteps.headerMoveMoney();
	}

	//Verify error messages

	@When("I click on Clear all link")
	private void clickOnClearAll()
	{
		btDepositsSteps.clickClearAll();
	}

	@Then("I see all fields are empty under deposit tab")
	private void checkAllFields()
	{
		btDepositsSteps.ckeckAllFieldsAreEmpty();
	}

	@When("I click on Next button without any data in field")
	private void clickOnNextBtn()
	{
		btDepositsSteps.clickNextButton();
	}

	@Then("I see error messages 'Please enter or select an account' under Make a deposit from field,'Please enter an amount' under Amount field, 'Deposit date is required.' under Date field")
	private void checkErrorMessages()
	{
		btDepositsSteps.checkErrorMesages();
	}

	@When("I click on Deposit button on deposit confirm modal window")
	public void clickOnDepositButton()
	{
		btDepositsSteps.click_On_DepositButton();

	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	@When("I click on Next button with all valid data")
	public void fillValidDataIn()
	{
		btDepositsSteps.enterValidDataInFieldsMakeDepositAmountDate();
		btDepositsSteps.checkRepeatDepositEnterDateRepeatEndDateOrNumber();
	}

	@Then("I see model window with header Confirm and deposit")
	public void checkPayConfirmationScreen()
	{
		btDepositsSteps.checkHeaderOfConfirmAndPay();
	}

	@Then("I see details as Account name, BSB number, Account number for 'from' on confirmation Page")
	public void checkPaymentConfirmationFromDetails()
	{
		btDepositsSteps.checkFromDetails();
	}

	@Then("I see details as Account name, BSB number, Account number for 'To' on confirmation Page")
	public void checkPaymentConfirmationToDetails()
	{
		btDepositsSteps.checkToDetails();
	}

	@Then("I see date, repeat frequency, repeat date, Pay button, change button")
	public void checkFieldsAndbtnsOnPaymentConfirmationScreen()
	{
		btDepositsSteps.checkDescriptionFieldAndBtnDepositAndChange();
	}

	/////////////////////////////////////////////////////////////////////////////////////////////

	@Then("I see page get navigated to the deposit successfully submit screen")
	public void openDepositSuccessfulSubmit()
	{
		btDepositsSteps.navigate_To_DepositSuccessfulSubmit();
	}

	@Then("I see message Deposit of <Amount> submitted successfully")
	public void verifyHeaderDepositSubmit()
	{
		btDepositsSteps.deposit_Successful_SubmitHeader();
	}

	@Then("I see text Date as a Transaction start date for deposit screen")
	public void verifyReceiptDateField()
	{
		btDepositsSteps.deposit_Successful_dateField();
	}

	@Then("I see text Repeats for deposit screen")
	public void verifyReceiptRepeatsField()
	{
		btDepositsSteps.deposit_Successful_RepeatsField();
	}

	@Then("I see text Description as entered by user while doing deposit")
	public void verifyReceiptDescriptionField()
	{
		btDepositsSteps.deposit_Successful_DescriptionField();
	}

	@Then("I see text Receipt no. with number returned by Avaloq for deposit screen")
	public void verifyReceiptNoField()
	{
		btDepositsSteps.deposit_Successful_ReceiptNoField();
	}

	@Then("I see Download button to download the PDF receipt for deposit screen")
	public void verifyDownloadButton()
	{
		btDepositsSteps.deposit_Successful_DownloadButton();
	}

	@Then("I see link See all transaction in deposit screen")
	public void verifySeeAllTransactionLink()
	{
		btDepositsSteps.link_Deposit_See_All_Transactions();
	}

	@When("I click on See all transaction in deposit screen")
	public void verifyClickonSeeAllTransactions()
	{
		btDepositsSteps.click_Deposit_See_All_Transactions();
	}

	@Then("I see page gets navigated to Past transaction screen from deposit screen")
	public void verifyNavigationToPastTransaction()
	{
		btDepositsSteps.open_Deposit_See_All_Transactions();
	}

	@Then("I see link Make another deposit")
	public void verifyMakeAnotherDepositLink()
	{
		btDepositsSteps.link_Make_Another_Deposit();
	}

	@When("I click on Make another deposit")
	public void verifyClickOnMakeAnotherDeposit()
	{
		btDepositsSteps.click_Make_Another_Deposit();
	}

	@Then("I see page gets navigated to Make deposit screen")
	public void verifyNavigationToMakeDeposit()
	{
		btDepositsSteps.open_Make_Another_Deposit();
	}

	@Then("I see link See scheduled payment in deposit screen")
	public void verifySeeScheduledTransaction()
	{
		btDepositsSteps.link_Deposit_See_Scheduled_Transactions();
	}

	@When("I click on See scheduled payment in deposit screen")
	public void verifyClickOnSeeScheduledTransactions()
	{
		btDepositsSteps.click_Deposit_See_Scheduled_Transactions();
	}

	@Then("I see page gets navigated to Scheduled transaction screen from deposit screen")
	public void verifyNavigationToScheduledTransaction()
	{
		btDepositsSteps.open_Deposit_See_Scheduled_Transactions();
	}
}
