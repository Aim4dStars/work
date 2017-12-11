package steps;

import static junit.framework.Assert.*;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

import org.openqa.selenium.Keys;

import pages.BTPayments.BTPaymentsPage;
import pages.logon.LogonPage;

public class BTPaymentsSteps extends ScenarioSteps
{
	LogonPage logonpage;
	BTPaymentsPage btpaymentspage;

	public BTPaymentsSteps(Pages pages)
	{
		super(pages);
	}

	@Step
	public void navigate_To_Payments() throws Throwable
	{
		logonpage.gotopage("Payments");
	}

	@Step
	public void chechHeaderMoveMoney()
	{
		assertTrue(btpaymentspage.getHeaderMoveMoney().getText().equals("Move money"));
	}

	@Step
	public void makeAPaymentFieldWithLabel()
	{
		assertTrue(btpaymentspage.getMakePaymentToText().isDisplayed());
		assertTrue(btpaymentspage.getMakePaymentToTextField().isDisplayed());
		assertTrue(btpaymentspage.getTextSearchOrSelect().isDisplayed());

		assertTrue(btpaymentspage.getClearAllBtn().isDisplayed());
	}

	@Step
	public void makeAmountFieldWithLabel()
	{
		assertTrue(btpaymentspage.getAmountLable().isDisplayed());
		assertTrue(btpaymentspage.getAmountTextField().isDisplayed());
	}

	@Step
	public void checkLinkAddNewAccOrBillerLink()
	{
		assertTrue(btpaymentspage.getLinkAddNewAccOrBiller().isDisplayed());
	}

	@Step
	public void checkDatePicker()
	{
		assertTrue(btpaymentspage.getDateLable().isDisplayed());
		assertTrue(btpaymentspage.getDateSearchElement().isDisplayed());
	}

	@Step
	public void repeatCheckBox()
	{
		assertTrue(btpaymentspage.getRepeatPayCheckBox().isDisplayed());
	}

	@Step
	public void clickRepeatCheckBox()
	{
		btpaymentspage.getRepeatPayCheckBox().click();
	}

	@Step
	public void checkRepeatEveryAndEndRepeat()
	{
		assertTrue(btpaymentspage.getSelectMonthlyInRepeatEvery().isDisplayed());
		assertTrue(btpaymentspage.getSelectSetEndDateInEndRepeat().isDisplayed());
	}

	@Step
	public void checkRepeatEveryAndEndRepeatNotDisplayed()
	{
		assertTrue(!btpaymentspage.getSelectMonthlyInRepeatEvery().isDisplayed());
		assertTrue(!btpaymentspage.getSelectSetEndDateInEndRepeat().isDisplayed());
	}

	@Step
	public void vrifyRepeatEveryFieldInput()
	{
		assertTrue(btpaymentspage.getSelectMonthlyInRepeatEvery().isDisplayed());
		assertTrue(btpaymentspage.getSelectWeeklyInRepeatEvery().isDisplayed());
		assertTrue(btpaymentspage.getSelectFortnightInRepeatEvery().isDisplayed());
		assertTrue(btpaymentspage.getSelectQuarterlyInRepeatEvery().isDisplayed());
		assertTrue(btpaymentspage.getSelectYearlyInRepeatEvery().isDisplayed());
	}

	@Step
	public void vrifyEndRepeatFieldInput()
	{
		assertTrue(btpaymentspage.getSelectSetEndDateInEndRepeat().isDisplayed());
		assertTrue(btpaymentspage.getSelectNoEndDateInEndRepeat().isDisplayed());
		assertTrue(btpaymentspage.getFieldDatePicker().isDisplayed());
	}

	@Step
	public void selectEndDt()
	{
		btpaymentspage.getSelectSetEndDateInEndRepeat().click();
	}

	@Step
	public void checkDtPicker()
	{
		assertTrue(btpaymentspage.getFieldDatePicker().isDisplayed());
	}

	public void selectSetNo()
	{
		btpaymentspage.getSelectSetNumberInEndRepeat().click();
	}

	public void checkFldSetNo()
	{
		btpaymentspage.getFldSetNo().click();
	}

	//
	@Step
	public void checkDescriptionTextField()
	{
		assertTrue(btpaymentspage.getDescriptionLabel().isDisplayed());
		assertTrue(btpaymentspage.getDescriptionTextField().isDisplayed());
		assertTrue(btpaymentspage.getDescriptionHelpText().isDisplayed());
	}

	@Step
	public void max18CharCanBeEntered()
	{
		btpaymentspage.getDescriptionTextField().sendKeys("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		btpaymentspage.getDescriptionTextField().sendKeys(Keys.TAB);
		boolean bLengthOfDescriptionCharacters = btpaymentspage.getDescriptionTextField().getText().length() == 18;
		assertTrue(bLengthOfDescriptionCharacters);
	}

	@Step
	public void checkNextButton()
	{
		assertTrue(btpaymentspage.getButtonNext().isDisplayed());
	}

	@Step
	public void checkClearAllLnk()
	{
		assertTrue(btpaymentspage.getClearAllBtn().isDisplayed());
	}

	//////////////////////////

	@Step
	public void clickClearAll()
	{
		btpaymentspage.getClearAllBtn().click();
	}

	@Step
	public void ckeckAllFieldsAreEmpty()
	{
		assertTrue(btpaymentspage.getAmountTextField().getText().length() < 1);
		assertTrue(btpaymentspage.getMakePaymentToTextField().getText().length() < 1);
		assertTrue(btpaymentspage.getDateSearchElement().getText().length() < 1);
	}

	@Step
	public void clickNextButton()
	{
		btpaymentspage.getButtonNext().click();
	}

	@Step
	public void checkErrorMesages()
	{
		assertEquals(btpaymentspage.getTextMakePaymentFromFieldErrorMsg().getText(), "Please enter or select an account");
		assertEquals(btpaymentspage.getTextAmountFieldErrorMsg().getText(), "Please enter an amount");
		assertEquals(btpaymentspage.getTextDateFieldErrorMsg().getText(), "Payment date is required.");
	}

	@Step
	public void enterValidDataInFieldsMakePaymentAmountDate()
	{
		btpaymentspage.getMakePaymentToTextField().sendKeys("linkedNick, 12006, 2222222");
		btpaymentspage.getAmountTextField().sendKeys("10");
		btpaymentspage.getDateSearchElement().sendKeys("20.02.20");
	}

	@Step
	public void checkRepeatPaymentEnterDateRepeatEndDateOrNumberAndClickNext()
	{
		btpaymentspage.getRepeatPayCheckBox().click();
		btpaymentspage.getSelectMonthlyInRepeatEvery().click();
		btpaymentspage.getSelectSetEndDateInEndRepeat().click();
		btpaymentspage.getDtRepeatEndDate().sendKeys("20.2.20");
		btpaymentspage.getTextDescription().sendKeys("Description Auto");
		btpaymentspage.getButtonNext().click();
	}

	//textConfirmAndPay
	@Step
	public void checkHeaderOfConfirmAndPay()
	{
		assertEquals(btpaymentspage.getTextConfirmAndPay().getText(), "Confirm and pay");
		assertEquals(btpaymentspage.getTextAmountEnteredInForm().getText(), "10.00");
	}

	@Step
	public void checkFromDetails()
	{
		assertEquals(btpaymentspage.getTextAccountNameFrom().getText(), "Adrian Demo Smith");
		assertEquals(btpaymentspage.getTextBSBNumberFrom().getText(), "BSB 262-786");
		assertEquals(btpaymentspage.getTextAccountNumberFrom().getText(), "Account no.36846");
	}

	@Step
	public void checkToDetails()
	{
		assertEquals(btpaymentspage.getTextAccountNameTo().getText(), "linkedAcc");
		assertEquals(btpaymentspage.getTextBSBNumberTo().getText(), "BSB 12006");
		assertEquals(btpaymentspage.getTextAccountNumberTo().getText(), "Account no.2222222");
	}

	@Step
	public void checkDateAndRepeatDetails()
	{
		assertEquals(btpaymentspage.getTextDate().getText(), "20 Feb 2020	");
		assertEquals(btpaymentspage.getTextRepeatFrequency().getText(), "Monthly Ends On");
		assertEquals(btpaymentspage.getTextRepeatDate().getText(), "20 Feb 2020 ");
	}

	@Step
	public void checkDescriptionFieldAndBtnPayAndChangeAndChangeLimit()
	{
		assertTrue(btpaymentspage.getBtnPay().isDisplayed());
		assertTrue(btpaymentspage.getBtnChangeLimit().isDisplayed());
		assertTrue(btpaymentspage.getTextRepeatFrequency().isDisplayed());
		assertEquals(btpaymentspage.getTextDescription().getText(), "Description Auto");
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Step
	public void click_On_PayButton()
	{

		btpaymentspage.getButtonPay().click();

	}

	@Step
	public void navigate_To_PaymentSuccessfulSubmit()
	{

		//getDriver().get("http://localhost:9080/ng/secure/app/#ng/account/movemoney/payments/receipt");
		//getDriver().get("http://dwgps0022.btfin.com:8120/nextgen/secure/app/#ng/privacystatement");
		btpaymentspage.getPaymentSuccessfulSubmitHeader().getText().contains("submitted successfully");

	}

	@Step
	public void payment_Successful_SubmitHeader()
	{
		String paySuccessgulText = btpaymentspage.getPaymentSuccessfulSubmitHeader().getText();
		String temp[] = paySuccessgulText.split(" ");
		String paySuccessfulSplitText = temp[0] + " " + temp[1] + " " + temp[3] + " " + temp[4];
		System.out.println("Payment successful sumbit text:" + paySuccessfulSplitText);
		assertEquals(paySuccessfulSplitText, "Payment of submitted successfully");
		assertTrue(btpaymentspage.getPaymentSuccessfulSubmitHeader().isDisplayed());

	}

	@Step
	public void payment_Successful_dateField()
	{
		String dateText = btpaymentspage.getReceiptTextDate().getText();
		assertEquals(dateText, "Date");
		assertTrue(btpaymentspage.getReceiptTextDate().isDisplayed());
	}

	@Step
	public void payment_Successful_RepeatsField()
	{
		String repeatsText = btpaymentspage.getReceiptTextRepeats().getText();
		assertEquals(repeatsText, "Repeats");
		assertTrue(btpaymentspage.getReceiptTextRepeats().isDisplayed());
	}

	@Step
	public void payment_Successful_DescriptionField()
	{
		String descriptionText = btpaymentspage.getReceiptTextDescription().getText();
		assertEquals(descriptionText, "Description");
		assertTrue(btpaymentspage.getReceiptTextDescription().isDisplayed());
	}

	@Step
	public void payment_Successful_ReceiptNoField()
	{
		String receiptNoText = btpaymentspage.getReceiptTextReceiptNo().getText();
		assertEquals(receiptNoText, "Receipt no");
		assertTrue(btpaymentspage.getReceiptTextReceiptNo().isDisplayed());
	}

	@Step
	public void payment_Successful_DownloadButton()
	{
		String downloadBtn = btpaymentspage.getReceiptTextReceiptNo().getText();
		assertEquals(downloadBtn, "Download");
		assertTrue(btpaymentspage.getReceiptTextReceiptNo().isDisplayed());
	}

	@Step
	public void link_See_All_Transactions()
	{
		String seeAllTransactionsLink = btpaymentspage.getlinkSeeAllTransactions().getText();
		assertEquals(seeAllTransactionsLink, "See all transactions");
		assertTrue(btpaymentspage.getlinkSeeAllTransactions().isDisplayed());
	}

	@Step
	public void click_See_All_Transactions()
	{
		btpaymentspage.getlinkSeeAllTransactions().click();

	}

	@Step
	public void open_See_All_Transactions()
	{
		assertTrue(getDriver().getCurrentUrl().contains(""));
		getDriver().navigate().back();

	}

	@Step
	public void link_Make_Another_Payment()
	{
		String makeAnotherPaymentLink = btpaymentspage.getLinkMakeAnotherPayment().getText();
		assertEquals(makeAnotherPaymentLink, "Make another payment");
		assertTrue(btpaymentspage.getLinkMakeAnotherPayment().isDisplayed());
	}

	@Step
	public void click_Make_Another_Payment()
	{
		btpaymentspage.getLinkMakeAnotherPayment().click();

	}

	@Step
	public void open_Make_Another_Payment()
	{
		assertTrue(getDriver().getCurrentUrl().contains(""));
		getDriver().navigate().back();

	}

	@Step
	public void link_See_Scheduled_Transactions()
	{
		String scheduledTransactionLink = btpaymentspage.getLinkSeeScheduledTransactions().getText();
		assertEquals(scheduledTransactionLink, "See scheduled payments");
		assertTrue(btpaymentspage.getLinkSeeScheduledTransactions().isDisplayed());
	}

	@Step
	public void click_See_Scheduled_Transactions()
	{
		btpaymentspage.getLinkSeeScheduledTransactions().click();

	}

	@Step
	public void open_See_Scheduled_Transactions()
	{
		assertTrue(getDriver().getCurrentUrl().contains(""));
		getDriver().navigate().back();

	}

	///modal

	@Step
	public void navigate_To_Payment_Page()
	{

		getDriver().get("http://localhost:9080/ng/secure/app/#ng/account/movemoney/payments?c=FC622CC21BFFE81A42F70BB718A6A91D1D9470BB4FB2631D&a=975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0");

	}

	@Step
	public void click_On_Add_New_Account_Or_Biller()
	{

		btpaymentspage.getLinkAddNewAccOrBiller().click();

	}

	@Step
	public void account_Tab_Displayed()
	{
		btpaymentspage.getAccountTab().isDisplayed();
	}

	@Step
	public void click_account_Tab()
	{
		btpaymentspage.getAccountTab().click();
	}

	@Step
	public void new_Account_And_Biller_Modal()
	{
		String modalHeaderText = btpaymentspage.getAccountTabStaticHeaderText2().getText();
		assertEquals(modalHeaderText, "payment account");
	}

	@Step
	public void account_Static_Add_Text()
	{
		String modalHeaderText1 = btpaymentspage.getAccountTabStaticHeaderText1().getText();
		assertEquals(modalHeaderText1, "Add");
	}

	@Step
	public void account_Static_Payments_Account_Text()
	{
		String modalHeaderText2 = btpaymentspage.getAccountTabStaticHeaderText2().getText();
		assertEquals(modalHeaderText2, "payment account");
	}

	@Step
	public void biller_Tab_Displayed()
	{
		btpaymentspage.getBillerTab().isDisplayed();
	}

	@Step
	public void click_biller_Tab()
	{
		btpaymentspage.getBillerTab().click();
	}

	@Step
	public void account_Name_Text()
	{
		String modalHeaderText2 = btpaymentspage.getAccountNameText().getText();
		assertEquals(modalHeaderText2, "payment account");
		btpaymentspage.getAccountNameText().isDisplayed();

	}

	@Step
	public void account_Name_field_validation_for_32_char()
	{
		btpaymentspage.getAccountNameField().sendKeys("1234567890qwertyuiop1234567890123");
		String feildValue = btpaymentspage.getAccountNameField().getText();
		assertEquals(btpaymentspage.getAccountNameField().getText(), "1234567890qwertyuiop123456789012");
		btpaymentspage.getAccountNameField().clear();
		btpaymentspage.getAccountNameField().sendKeys("12432462@");

	}

	@Step
	public void account_Name_field_validation_Invalid_And_Blank_Field()
	{
		btpaymentspage.getAccountNameField().clear();
		btpaymentspage.getAccountNameField().sendKeys("12432462@");
		btpaymentspage.getAccountNameField().clear();
		btpaymentspage.getAccountNameField().sendKeys("");
		btpaymentspage.getAccountNumberField().click();

	}

	@Step
	public void account_Name_field_error_message_verification()
	{

		assertEquals(btpaymentspage.getAccountNameFieldErrorMessage().getText(), "Please enter a name using letters or numbers");
	}

	@Step
	public void bSB_field_validation_for_7_char()
	{
		btpaymentspage.getBSBField().clear();
		btpaymentspage.getBSBField().sendKeys("123456");
		String feildValue = btpaymentspage.getBSBField().getText();
		assertEquals(btpaymentspage.getBSBField().getText(), "123456");
		btpaymentspage.getBSBField().clear();
		btpaymentspage.getBSBField().sendKeys("123-456");
		assertEquals(btpaymentspage.getBSBField().getText(), "123456");
		btpaymentspage.getBSBField().clear();
	}

	@Step
	public void bSB_Error_Message()
	{
		btpaymentspage.getBSBField().clear();
		btpaymentspage.getAccountNameField().click();
		assertEquals(btpaymentspage.getBSBErrorMessage().getText(), "Please enter a 6-digit BSB number");
	}

	@Step
	public void account_Number_Validation_For_10_Char()
	{
		btpaymentspage.getAccountNumberField().clear();
		btpaymentspage.getBSBField().sendKeys("1234568901");
		String fieldValue = btpaymentspage.getBSBField().getText();
		assertEquals(btpaymentspage.getBSBField().getText(), "1234567890");

	}

	@Step
	public void account_Number_Error_Message()
	{
		btpaymentspage.getAccountNumberField().clear();
		btpaymentspage.getAccountNameField().click();
		assertEquals(btpaymentspage.getAccountNumberFieldErrorMessage().getText(), "Enter an account number");

	}

	@Step
	public void account_Nickname_Option_Text_Field()
	{
		assertEquals(btpaymentspage.getAccountNicknameText().getText(), "Account nickname");
		assertEquals(btpaymentspage.getAccountNicknameOptionalText().getText(), "Optional");
		assertTrue(btpaymentspage.getAccountNicknameOptionalField().isDisplayed());

	}

	@Step
	public void account_Nickname_Field_Validation_Max_30_Char()
	{
		btpaymentspage.getAccountNicknameOptionalField().clear();
		btpaymentspage.getAccountNicknameOptionalField().sendKeys("123456890qwertyuiop---- ------");
		String fieldValue = btpaymentspage.getAccountNicknameOptionalField().getText();
		assertEquals(btpaymentspage.getAccountNicknameOptionalField().getText(), "123456890qwertyuiop---- -----");

	}

	@Step
	public void account_Nickname_Error_Message()
	{
		btpaymentspage.getAccountNumberField().clear();
		btpaymentspage.getAccountNicknameOptionalField().sendKeys("123456890@@####");
		assertEquals(btpaymentspage.getAccountNicknameErrorMessage().getText(),
			"Please only use letters, numbers, hyphens or spaces");

	}

	@Step
	public void checkbox_Enabled_By_Default_For_Account_And_Biller()
	{
		assertTrue(btpaymentspage.getCheckboxAccountAndBiller().isEnabled());

	}

	@Step
	public void verify_Text_SMS_Code_For_Your_Security()
	{
		assertEquals(btpaymentspage.getSMSCodeForYourSecurityText().getText(), "SMS code for your security");
		assertTrue(btpaymentspage.getSMSCodeForYourSecurityText().isDisplayed());
	}

	@Step
	public void verify_Get_SMS_Code_Button_Displayed()
	{
		assertTrue(btpaymentspage.getSMSCodeButton().isDisplayed());
	}

	@Step
	public void click_On_Get_SMS_Code_Button()
	{
		btpaymentspage.getSMSCodeButton().click();

	}

	@Step
	public void verify_Get_SMS_Code_Field()
	{
		assertTrue(btpaymentspage.getSMSCodefield().isDisplayed());

	}

	@Step
	public void verify_Add_Button()
	{
		assertFalse(btpaymentspage.getAddButton().isEnabled());

	}

	@Step
	public void verify_Max_6_Digit_Allowed_In_SMS_Code_Field()
	{
		btpaymentspage.getSMSCodefield().clear();
		btpaymentspage.getSMSCodefield().sendKeys("1234567");
		String fieldValue = btpaymentspage.getSMSCodefield().getText();
		assertEquals(btpaymentspage.getSMSCodefield().getText(), "123456");

	}

	@Step
	public void verify_Static_Message_Below_Enter_SMS_Code()
	{
		assertEquals(btpaymentspage.getStaticMessageBelowEnterSMSCode().getText(),
			"Code sent to 04## ### 664. If the code is not received in a few minutes, try again");
		assertTrue(btpaymentspage.getStaticMessageBelowEnterSMSCode().isDisplayed());
	}

	@Step
	public void enter_Six_Digit_SMS_Code()
	{
		btpaymentspage.getSMSCodefield().sendKeys("123456");

	}

	@Step
	public void verify_Add_Button_Enable()
	{
		assertTrue(btpaymentspage.getAddButton().isEnabled());

	}

}
