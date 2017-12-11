package feature;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Then;

import steps.BTPaymentsSteps;
import cucumber.api.java.en.When;

public class BTPayments
{

	@Steps
	BTPaymentsSteps onbtpaymentstep;

	@When("I click on Move money link on left panel")
	public void navigateToPayment() throws Throwable
	{
		onbtpaymentstep.navigate_To_Payments();
	}

	@Then("I see Page gets navigated to Move money page with header Move money")
	public void moveMoneyHeder()
	{
		onbtpaymentstep.chechHeaderMoveMoney();
	}

	@Then("I see static label Make payment to with list box")
	private void pub()
	{
		onbtpaymentstep.makeAPaymentFieldWithLabel();
	}

	@Then("I see link Add account or Biller in line with the drop down list")
	public void checkLinkAddNewAccOrBillerLnk()
	{
		onbtpaymentstep.checkLinkAddNewAccOrBillerLink();
	}

	@When("I select End date")
	public void selectEndDate()
	{
		onbtpaymentstep.selectEndDt();
	}

	@Then("I see date picker will appear")
	public void checkDatePicker()
	{
		onbtpaymentstep.checkDtPicker();
	}

	@When("I select Set number")
	public void selectSetNumber()
	{
		onbtpaymentstep.selectSetNo();
	}

	@Then("I see editable field will appear to specify the no of transactions ")
	public void checkNumberField()
	{
		onbtpaymentstep.checkFldSetNo();
	}

	@Then("I see optional Description text field")
	public void checkDescreptionFieldAndLbl()
	{
		onbtpaymentstep.checkDescriptionTextField();
	}

	@Then("I see Maximum 18 letters and numbers can be entered in the Description text field")
	public void max18CharCanBeEnteredInDescription()
	{
		onbtpaymentstep.max18CharCanBeEntered();
	}

	@Then("I see help text 'Maximum 18 letters and numbers' below the Description text field")
	public void checkHelpOfDescription()
	{
		onbtpaymentstep.checkLinkAddNewAccOrBillerLink();
	}

	@Then("I see Amount field")
	public void checkAmntField1()
	{
		onbtpaymentstep.makeAmountFieldWithLabel();
	}

	@Then("I see date picker")
	public void checkDateField()
	{
		onbtpaymentstep.checkDatePicker();
	}

	@Then("I see Repeat payment check box for recursive schedule payment")
	public void verifyCheckBox()
	{
		onbtpaymentstep.repeatCheckBox();
	}

	@When("I check the Repeat payment field")
	public void clickRepeatPayment()
	{
		onbtpaymentstep.clickRepeatCheckBox();
	}

	@Then("I see two more fields Repeat every & end repeats will appear")
	public void verifyRpeatEveryAndEndRepeat()
	{
		onbtpaymentstep.checkRepeatEveryAndEndRepeat();
	}

	@When("I unchecked the Repeat payment field")
	public void uncheckRepeatPayment()
	{
		onbtpaymentstep.clickRepeatCheckBox();
	}

	@Then("I see Repeat every & end repeats will disappear")
	public void verifyRepeatEveryEndRepeatDisappear()
	{
		onbtpaymentstep.checkRepeatEveryAndEndRepeatNotDisplayed();
	}

	@Then("I see Repeat every filed have defined values as weekly, fortnight, Monthly, Quarterly or yearly with default value as Monthly")
	public void verifyRepeatEveryFieldInput()
	{
		onbtpaymentstep.checkRepeatEveryAndEndRepeatNotDisplayed();
	}

	@Then("I see End repeat have defined values as End date, No end date, set number with default value as No end date")
	public void verifyEndRepeatFieldInput()
	{
		onbtpaymentstep.vrifyEndRepeatFieldInput();
	}

	@Then("I see maximum 18 character can be entered in Description field")
	public void check18CharLimit()
	{
		onbtpaymentstep.max18CharCanBeEntered();
	}

	@Then("I see Next button")
	public void checkNextBtn()
	{
		onbtpaymentstep.checkNextButton();
	}

	@Then("I see Clear all link")
	public void checkClearAllLink()
	{
		onbtpaymentstep.checkClearAllLnk();
	}

	@When("I click Move money link on left panel")
	public void navigateToPaymentForError()
	{
		//onbtpaymentstep.navigate_To_Payments();
	}

	@Then("I see Make a payment page with header Move money")
	public void moveMoneyHederForError()
	{
		onbtpaymentstep.chechHeaderMoveMoney();
	}

	@When("I click on Clear all")
	public void clickClearAllBtn()
	{
		onbtpaymentstep.clickClearAll();
	}

	@Then("I see all fields are empty")
	public void checkAllFieldsAreEmpty()
	{
		onbtpaymentstep.ckeckAllFieldsAreEmpty();
	}

	@When("I click on Next button without any data in fields")
	public void clickNextBtnWithoutData()
	{
		onbtpaymentstep.clickNextButton();
	}

	@Then("I see error messages Please enter or select an account under Make a Payment from field, Please enter an amount under Amount field, Payment date is required under Date field")
	public void checkErrMessages()
	{
		onbtpaymentstep.checkErrorMesages();
	}

	@When("I enter valid data in Make a Payment, Amount, Date")
	public void fillValidDataIn()
	{
		onbtpaymentstep.enterValidDataInFieldsMakePaymentAmountDate();
	}

	@Then("I click on repeat Payment check box and fill valid data and click on Next button")
	public void checkRepeatCheckBoxAndFillDeatilsInFieldsAndClickNext()
	{
		onbtpaymentstep.checkRepeatPaymentEnterDateRepeatEndDateOrNumberAndClickNext();
	}

	@Then("I see a Payment confirmation page")
	public void checkPaymentConfirmationScreen()
	{
		onbtpaymentstep.checkHeaderOfConfirmAndPay();
	}

	@Then("I see model window with header Confirm and pay")
	public void checkPayConfirmationScreen()
	{
		onbtpaymentstep.checkHeaderOfConfirmAndPay();
	}

	@Then("I see details as Account name, BSB number, Account number for from")
	public void checkPaymentConfirmationFromDetails()
	{
		onbtpaymentstep.checkFromDetails();
	}

	@Then("I see details as Account name, BSB number, Account number for To")
	public void checkPaymentConfirmationToDetails()
	{
		onbtpaymentstep.checkToDetails();
	}

	@Then("I see date, repeat frequency, repeat date, Pay button, change button, change limit button")
	public void checkFieldsAndbtnsOnPaymentConfirmationScreen()
	{
		onbtpaymentstep.checkDescriptionFieldAndBtnPayAndChangeAndChangeLimit();
	}

	@When("I click on pay button on payment confirm modal window")
	public void clickOnPayButton()
	{
		onbtpaymentstep.click_On_PayButton();
	}

	@Then("I see page get navigated to the payment successfully submit screen")
	public void openPaymentSuccessfulSubmit()
	{
		onbtpaymentstep.navigate_To_PaymentSuccessfulSubmit();
	}

	@Then("I see message Payment of <Amount> submitted successfully")
	public void verifyHeaderPaySubmit()
	{
		onbtpaymentstep.payment_Successful_SubmitHeader();
	}

	@Then("I see text Date as a Transaction start date")
	public void verifyReceiptDateField()
	{
		onbtpaymentstep.payment_Successful_dateField();
	}

	@Then("I see text Repeats")
	public void verifyReceiptRepeatsField()
	{
		onbtpaymentstep.payment_Successful_RepeatsField();
	}

	@Then("I see text Description as entered by user while doing payment")
	public void verifyReceiptDscriptionField()
	{
		onbtpaymentstep.payment_Successful_DescriptionField();
	}

	@Then("I see text Receipt no. with number returned by Avaloq")
	public void verifyReceiptNoField()
	{
		onbtpaymentstep.payment_Successful_ReceiptNoField();
	}

	@Then("I see Download button to download the PDF receipt")
	public void verifyDownloadButton()
	{
		onbtpaymentstep.payment_Successful_DownloadButton();
	}

	@Then("I see link See all transaction")
	public void verifySeeAllTransactionLink()
	{
		onbtpaymentstep.link_See_All_Transactions();
	}

	@When("I click on See all transaction")
	public void verifyClickonSeeAllTransactions()
	{
		onbtpaymentstep.click_See_All_Transactions();
	}

	@Then("I see page gets navigated to Past transaction screen")
	public void verifyNavigationToPastTransaction()
	{
		onbtpaymentstep.open_See_All_Transactions();
	}

	@Then("I see link Make another payment")
	public void verifyMakeAnotherPaymentLink()
	{
		onbtpaymentstep.link_Make_Another_Payment();
	}

	@When("I click on Make another payment")
	public void verifyClickOnMakeAnotherPayment()
	{
		onbtpaymentstep.click_Make_Another_Payment();
	}

	@Then("I see page gets navigated to Make payment screen")
	public void verifyNavigationToMakePayment()
	{
		onbtpaymentstep.open_Make_Another_Payment();
	}

	@Then("I see link See scheduled transactions")
	public void verifySeeScheduledTransaction()
	{
		onbtpaymentstep.link_See_Scheduled_Transactions();
	}

	@When("I click on See scheduled transactions")
	public void verifyClickOnSeeScheduledTransactions()
	{
		onbtpaymentstep.click_See_Scheduled_Transactions();
	}

	@Then("I see page gets navigated to Scheduled transaction screen")
	public void verifyNavigationToScheduledTransaction()
	{
		onbtpaymentstep.open_See_Scheduled_Transactions();
	}

	//######## Account and biller modal
	@When("I navigate to payment page")
	public void navigateToPaymentPage()
	{
		onbtpaymentstep.navigate_To_Payment_Page();
	}

	@When("I click on add new account or biller link")
	public void clickOnAddNewAccountOrBillerLink()
	{
		onbtpaymentstep.click_On_Add_New_Account_Or_Biller();
	}

	@Then("I see modal window open")
	public void modalWindowAddNewAccountAndBillerOpen()
	{
		onbtpaymentstep.new_Account_And_Biller_Modal();
	}

	@Then("I see Account tab & Biller tab")
	public void accountAndBillerTabDisplayed()
	{
		onbtpaymentstep.account_Tab_Displayed();
		onbtpaymentstep.biller_Tab_Displayed();
	}

	@When("I select Account tab")
	public void selectAccountTab()
	{
		onbtpaymentstep.click_account_Tab();
	}

	@When("I select biller tab")
	public void selectBillerTab()
	{
		onbtpaymentstep.click_biller_Tab();
	}

	@Then("I see static heading as Add payment account")
	public void verifyStaticHeadingTextAddPaymentAccount()
	{
		onbtpaymentstep.account_Static_Add_Text();
		onbtpaymentstep.account_Static_Payments_Account_Text();
	}

	@Then("I see Account Name text box can accept maximum upto 32 letters or numbers only")
	public void verifyingAccountNameFieldFor32Char()
	{
		onbtpaymentstep.account_Name_field_validation_for_32_char();
	}

	@When("I enter other than number or letters or keep it blank")
	public void verifyingAccountNameFieldForBlankFieldAndInvalidChar()
	{
		onbtpaymentstep.account_Name_field_validation_Invalid_And_Blank_Field();
	}

	@Then("I see error message Please enter a name using letters or numbers")
	public void verifyingAccountNameErrorMessage()
	{
		onbtpaymentstep.account_Name_field_error_message_verification();
	}

	@Then("I see BSB textbox accept maximum 7 number")
	public void verifyingBSBTextBoxAcceptMaximum7Number()
	{
		onbtpaymentstep.bSB_field_validation_for_7_char();
	}

	@Then("I see error message 'Please enter a 6-digit BSB number' for character less than 6 and BSB number format")
	public void verifyingBSBErrorMessage()
	{
		onbtpaymentstep.bSB_Error_Message();
	}

	@Then("I see Account number text box of maximum length 10 with only numbers allowed")
	public void verifyingAccountNumberAcceptMaximum10Number()
	{
		onbtpaymentstep.account_Number_Validation_For_10_Char();
	}

	@Then("I see error message displayed for invalid or blank account number 'Enter an account number'")
	public void verifyingAccountNumberErrorMessage()
	{
		onbtpaymentstep.account_Number_Error_Message();
	}

	@Then("I see Account nickname it's optional field and text")
	public void verifyingAccountNicknameOptionalText()
	{
		onbtpaymentstep.account_Nickname_Option_Text_Field();
	}

	@Then("I see its maximum length is 30 characters and allows letters, numbers, hyphens, or spaces")
	public void verifyingAccountNicknameAcceptMax30CharAndAllowsLetterNumHypensSpace()
	{
		onbtpaymentstep.account_Nickname_Field_Validation_Max_30_Char();
	}

	@Then("I see error message 'Please only use letters, numbers, hyphens or spaces' if any wrong input is added")
	public void verifyingAccountNicknameErrorMessage()
	{
		onbtpaymentstep.account_Nickname_Error_Message();
	}

	@Then("I see Save to account and biller list Check box checked by default")
	public void verifyingCheckboxEnabledByDefaultForAccountAndBiller()
	{
		onbtpaymentstep.checkbox_Enabled_By_Default_For_Account_And_Biller();
	}

	@Then("I see static text SMS code for your security")
	public void verifyingStaticTextSMSCodeForYourSecurity()
	{
		onbtpaymentstep.verify_Text_SMS_Code_For_Your_Security();
	}

	@Then("I see Get SMS code button")
	public void verifyGetSMSCodeButton()
	{
		onbtpaymentstep.verify_Get_SMS_Code_Button_Displayed();
	}

	@When("I click the Get SMS code button")
	public void clickOnGetSMSButton()
	{
		onbtpaymentstep.click_On_Get_SMS_Code_Button();
	}

	@Then("I see the button is replaced with text box to enter the SMS code")
	public void verifyGetSMSCodeField()
	{
		onbtpaymentstep.verify_Get_SMS_Code_Field();
	}

	@Then("I see Add button is disabled by default")
	public void verifyAddButtonDisabledByDefault()
	{
		onbtpaymentstep.verify_Add_Button();
	}

	@Then("I see maximum length allowed is 6 digits")
	public void verifyMaxLengthOfGetSmsCodeField()
	{
		onbtpaymentstep.verify_Max_6_Digit_Allowed_In_SMS_Code_Field();
	}

	@Then("I see static text appear below the text Code sent to <XX## ### XXX>. If the code is not received in a few minutes, <try again>")
	public void verifyStatic_Message_Below_Enter_Sms_Code()
	{
		onbtpaymentstep.verify_Static_Message_Below_Enter_SMS_Code();
	}

	@When("I enter 6 digit sms code")
	public void enterSixDigitSmsCode()
	{
		onbtpaymentstep.enter_Six_Digit_SMS_Code();
	}

	@Then("I see Add button gets enabled")
	public void verifyAddButtonEnable()
	{
		onbtpaymentstep.verify_Add_Button_Enable();
	}
}
