package pages.BTPayments;

import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class BTPaymentsPage extends PageObject
{
	public BTPaymentsPage(WebDriver driver)
	{
		super(driver);
	}

	@FindBy(css = "button.btn-.btn-action-primary.final-action")
	private WebElement isButtonPay;

	public WebElement getButtonPay()
	{
		return isButtonPay;
	}

	@FindBy(css = "h1[class='heading-five panel-header']")
	private WebElement isHeaderMoveMoney;

	public WebElement getHeaderMoveMoney()
	{
		return isHeaderMoveMoney;
	}

	@FindBy(xpath = "//div[@class='sub-navigation']/a")
	private WebElement isTabPayments;

	//Verifying page header
	public WebElement getTabPayments()
	{
		return isTabPayments;
	}

	@FindBy(xpath = "//div[@class='columns-20 margin-right-1 view-payeelist']/div/div/span")
	private WebElement isMakePaymentToLable;

	public WebElement getMakePaymentToText()
	{
		return isMakePaymentToLable;
	}

	@FindBy(xpath = "//div[@class='mvc-payeelist']/div/div/input")
	private WebElement isMakePaymentToTextField;

	public WebElement getMakePaymentToTextField()
	{
		return isMakePaymentToTextField;
	}

	@FindBy(xpath = "//div[@class='columns-20 view-amount']/div/div/span")
	private WebElement isAmountLable;

	public WebElement getAmountLable()
	{
		return isAmountLable;
	}

	@FindBy(xpath = "//input[@placeholder= 'Search or select']")
	private WebElement textSearchOrSelect;

	public WebElement getTextSearchOrSelect()
	{
		return textSearchOrSelect;
	}

	@FindBy(xpath = "//div[@class='view-amount_2']/span/input")
	private WebElement isAmountTextField;

	public WebElement getAmountTextField()
	{
		return isAmountTextField;
	}

	@FindBy(xpath = "//div[@class='columns-9 margin-bottom-half view-paymentdate']/div/div/span")
	private WebElement isDateLable;

	public WebElement getDateLable()
	{
		return isDateLable;
	}

	@FindBy(css = "div[class='date-select-element']")
	private WebElement isDateSearchElement;

	public WebElement getDateSearchElement()
	{
		return isDateSearchElement;
	}

	@FindBy(xpath = "//span[contains(text(), 'Clear all')]")
	private WebElement blnkClearAll;

	public WebElement getClearAllBtn()
	{
		return blnkClearAll;
	}

	@FindBy(xpath = "//div[@class='view-inputcheckbox']/span/label")
	private WebElement isRepeatPayCheckBox;

	public WebElement getRepeatPayCheckBox()
	{
		return isDateSearchElement;
	}

	@FindBy(xpath = "//div[@class='columns-20 margin-bottom-0 view-forminputtext']/div/div/span[@class='label']")
	private WebElement isDescriptionLabel;

	public WebElement getDescriptionLabel()
	{
		return isDateSearchElement;
	}

	@FindBy(xpath = "//div[@class='columns-20 margin-bottom-0 view-forminputtext']/div/div/span/span[@class='label-optional']")
	private WebElement isOptionalLabel;

	public WebElement getOptionalLabel()
	{
		return isOptionalLabel;
	}

	@FindBy(xpath = "//div[@class='date-select-element']/input[@name='repeatenddate']")
	private WebElement fieldDatePicker;

	public WebElement getFieldDatePicker()
	{
		return fieldDatePicker;
	}
	
	@FindBy(xpath = "//a[contains(text(),'Monthly')]")
	private WebElement selectMonthlyInRepeatEvery;

	public WebElement getSelectMonthlyInRepeatEvery()
	{
		return selectMonthlyInRepeatEvery;
	}
	
	@FindBy(xpath = "//a[contains(text(),'Weekly')]")
	private WebElement selectWeeklyInRepeatEvery;

	public WebElement getSelectWeeklyInRepeatEvery()
	{
		return selectWeeklyInRepeatEvery;
	}
	
	@FindBy(xpath = "//a[contains(text(),'Fortnight')]")
	private WebElement selectFortnightInRepeatEvery;

	public WebElement getSelectFortnightInRepeatEvery()
	{
		return selectFortnightInRepeatEvery;
	}
	
	@FindBy(xpath = "//a[contains(text(),'Quarterly')]")
	private WebElement selectQuarterlyInRepeatEvery;

	public WebElement getSelectQuarterlyInRepeatEvery()
	{
		return selectQuarterlyInRepeatEvery;
	}
	
	@FindBy(xpath = "//a[contains(text(),'Yearly')]")
	private WebElement selectYearlyInRepeatEvery;

	public WebElement getSelectYearlyInRepeatEvery()
	{
		return selectYearlyInRepeatEvery;
	}

	@FindBy(xpath = "//a[contains(text(),'Set end date')]")
	private WebElement selectSetEndDateInEndRepeat;

	public WebElement getSelectSetEndDateInEndRepeat()
	{
		return selectSetEndDateInEndRepeat;
	}
	
	@FindBy(xpath = "//a[contains(text(),'No end date')]")
	private WebElement selectNoEndDateInEndRepeat;

	public WebElement getSelectNoEndDateInEndRepeat()
	{
		return selectNoEndDateInEndRepeat;
	}
	
	@FindBy(xpath = "//a[contains(text(),'Set number')]")
	private WebElement selectSetNumberInEndRepeat;

	public WebElement getSelectSetNumberInEndRepeat()
	{
		return selectSetNumberInEndRepeat;
	}
	
	@FindBy(xpath = "//input[@name='repeatnumber']")
	private WebElement fldSetNo;

	public WebElement getFldSetNo()
	{
		return fldSetNo;
	}

	@FindBy(xpath = "//input[@name='repeatenddate']")
	private WebElement dtRepeatEndDate;

	public WebElement getDtRepeatEndDate()
	{
		return dtRepeatEndDate;
	}

	@FindBy(xpath = "//div[@class='view-description']/input")
	private WebElement isDescriptionTextField;

	public WebElement getDescriptionTextField()
	{
		return isDescriptionTextField;
	}
	
	@FindBy(xpath = "//input[@name='description']/ancestor::div[@class='grid']/following-sibling::div/p")
	private WebElement isDescriptionTextFieldHeplText;

	public WebElement getIsDescriptionTextFieldHeplText()
	{
		return isDescriptionTextFieldHeplText;
	}

	@FindBy(css = "p[class='margin-bottom-2']")
	private WebElement isDescriptionHelpText;

	public WebElement getDescriptionHelpText()
	{
		return isDescriptionHelpText;
	}

	@FindBy(css = "button.btn-.btn-action-primary")
	private WebElement isButtonNext;

	public WebElement getButtonNext()
	{
		return isButtonNext;
	}

	@FindBy(xpath = "//span[contains(text(),'Confirm and pay')]")
	private WebElement textConfirmAndPay;

	public WebElement getTextConfirmAndPay()
	{
		return textConfirmAndPay;
	}
	
	@FindBy(xpath = "//span[contains(text(),'Confirm and pay')]/following-sibling::span")
	private WebElement textAmountEnteredInForm;

	public WebElement getTextAmountEnteredInForm()
	{
		return textAmountEnteredInForm;
	}

	@FindBy(xpath = "")
	private WebElement textAccountNameFrom;

	public WebElement getTextAccountNameFrom()
	{
		return textAccountNameFrom;
	}

	@FindBy(xpath = "")
	private WebElement textBSBNumberFrom;

	public WebElement getTextBSBNumberFrom()
	{
		return textBSBNumberFrom;
	}

	@FindBy(xpath = "")
	private WebElement textAccountNumberFrom;

	public WebElement getTextAccountNumberFrom()
	{
		return textAccountNumberFrom;
	}

	@FindBy(xpath = "")
	private WebElement textAccountNameTo;

	public WebElement getTextAccountNameTo()
	{
		return textAccountNameTo;
	}

	@FindBy(xpath = "")
	private WebElement textBSBNumberTo;

	public WebElement getTextBSBNumberTo()
	{
		return textBSBNumberTo;
	}

	@FindBy(xpath = "")
	private WebElement textAccountNumberTo;

	public WebElement getTextAccountNumberTo()
	{
		return textAccountNumberTo;
	}
	
	@FindBy(xpath = "")
	private WebElement textDate;

	public WebElement getTextDate()
	{
		return textDate;
	}
	
	@FindBy(xpath = "")
	private WebElement textRepeatFrequency;

	public WebElement getTextRepeatFrequency()
	{
		return textRepeatFrequency;
	}
	
	@FindBy(xpath = "")
	private WebElement textRepeatDate;

	public WebElement getTextRepeatDate()
	{
		return textRepeatDate;
	}
	
	@FindBy(xpath = "")
	private WebElement textDescription;

	public WebElement getTextDescription()
	{
		return textDescription;
	}
	
	@FindBy(xpath = "")
	private WebElement btnPay;

	public WebElement getBtnPay()
	{
		return btnPay;
	}
	
	@FindBy(xpath = "")
	private WebElement btnChangeLimit;

	public WebElement getBtnChangeLimit()
	{
		return btnChangeLimit;
	}
	
	@FindBy(xpath = "")
	private WebElement btnChange;

	public WebElement getBtnChange()
	{
		return btnChange;
	}
	
	
	@FindBy(xpath = "//div[@class='view-button']/button/span/span[@class='label-content']")
	private WebElement isLinkAddNewAccOrBiller;

	public WebElement getLinkAddNewAccOrBiller()
	{
		return isLinkAddNewAccOrBiller;
	}

	@FindBy(css = ".header-statement")
	private WebElement isPaymentSuccessfulSubmitHeader;

	public WebElement getPaymentSuccessfulSubmitHeader()
	{
		return isPaymentSuccessfulSubmitHeader;
	}

	@FindBy(xpath = "")
	private WebElement textMakePaymentFromFieldErrorMsg;

	public WebElement getTextMakePaymentFromFieldErrorMsg()
	{
		return textMakePaymentFromFieldErrorMsg;
	}

	@FindBy(xpath = "")
	private WebElement textAmountFieldErrorMsg;

	public WebElement getTextAmountFieldErrorMsg()
	{
		return textAmountFieldErrorMsg;
	}

	@FindBy(xpath = "")
	private WebElement textDateFieldErrorMsg;

	public WebElement getTextDateFieldErrorMsg()
	{
		return textDateFieldErrorMsg;
	}

	@FindBy(xpath = "//div[@class='mvc-receipt']/div[4]/div/strong")
	private WebElement isReceiptTextDate;

	public WebElement getReceiptTextDate()
	{
		return isReceiptTextDate;
	}

	@FindBy(xpath = "//div[@class='mvc-receipt']/div[5]/div/strong")
	private WebElement isReceiptTextRepeats;

	public WebElement getReceiptTextRepeats()
	{
		return isReceiptTextRepeats;
	}

	@FindBy(xpath = "//div[@class='mvc-receipt']/div[7]/div/strong")
	private WebElement isReceiptTextDescription;

	public WebElement getReceiptTextDescription()
	{
		return isReceiptTextDescription;
	}

	@FindBy(xpath = "//div[@class='mvc-receipt']/div[7]/div/strong")
	private WebElement isReceiptTextReceiptNo;

	public WebElement getReceiptTextReceiptNo()
	{
		return isReceiptTextReceiptNo;
	}

	@FindBy(css = "button.btn-.btn-action-secondary")
	private WebElement isReceiptDownalodButton;

	public WebElement getReceiptDownalodButton()
	{
		return isReceiptDownalodButton;
	}

	@FindBy(xpath = "//div[@class='view-button']/a/span/span[2]")
	private WebElement islinkSeeAllTransactions;

	public WebElement getlinkSeeAllTransactions()
	{
		return islinkSeeAllTransactions;
	}

	@FindBy(xpath = "//div[@class='view-button_2']/a/span/span[2]")
	private WebElement isLinkMakeAnotherPayment;

	public WebElement getLinkMakeAnotherPayment()
	{
		return isLinkMakeAnotherPayment;
	}

	@FindBy(xpath = "//div[@class='view-button_3']/a/span/span[2]")
	private WebElement isLinkSeeScheduledTransactions;

	public WebElement getLinkSeeScheduledTransactions()
	{
		return isLinkSeeScheduledTransactions;
	}

	//#####modal

	@FindBy(css = ".modal-body .view-buttontoggle span a:nth-child(1) span")
	private WebElement isAccountTab;

	public WebElement getAccountTab()
	{
		return isAccountTab;
	}

	@FindBy(css = ".modal-body div:nth-child(2) .mvc-accounts .columns-25 h1 span:nth-child(1)")
	private WebElement isAccountTabStaticHeaderText1;

	public WebElement getAccountTabStaticHeaderText1()
	{
		return isAccountTabStaticHeaderText1;
	}

	@FindBy(css = ".modal-body div:nth-child(2) .mvc-accounts .columns-25 h1 span:nth-child(2)")
	private WebElement isAccountTabStaticHeaderText2;

	public WebElement getAccountTabStaticHeaderText2()
	{
		return isAccountTabStaticHeaderText2;
	}

	@FindBy(css = ".modal-body .view-buttontoggle span a:nth-child(2) span")
	private WebElement isBillerTab;

	public WebElement getBillerTab()
	{
		return isBillerTab;
	}

	@FindBy(xpath = "//label[contains(text(), 'Account name')]")
	private WebElement isAccountNameText;

	public WebElement getAccountNameText()
	{
		return isAccountNameText;
	}

	@FindBy(css = "#input-text-view152")
	private WebElement isAccountNameField;

	public WebElement getAccountNameField()
	{
		return isAccountNameField;
	}

	@FindBy(css = "//div[@class='mvc-accounts']/form/div/div/div/div[2]/span")
	private WebElement isAccountNameErrorMessage;

	public WebElement getAccountNameFieldErrorMessage()
	{
		return isAccountNameErrorMessage;
	}

	///////////////////////////////////////////////////////////////////////////////
	@FindBy(xpath = "//label[contains(text(), 'BSB')]")
	private WebElement isBSBText;

	public WebElement getBSBText()
	{
		return isBSBText;
	}

	@FindBy(css = "#input-text-view153")
	private WebElement isBSBField;

	public WebElement getBSBField()
	{
		return isBSBField;
	}

	@FindBy(xpath = "//div[@class='columns-5 margin-right-1 view-forminputtext_2']/div/div[2]/span")
	private WebElement isBSBErrorMessage;

	public WebElement getBSBErrorMessage()
	{
		return isAccountNameErrorMessage;
	}

	//////////////////////////////////////////////////////////////////////////////
	@FindBy(xpath = "//label[contains(text(), 'Account number')]")
	private WebElement isAccountNumberText;

	public WebElement getAccountNumberText()
	{
		return isAccountNumberText;
	}

	@FindBy(css = "#input-text-view154")
	private WebElement isAccountNumberField;

	public WebElement getAccountNumberField()
	{
		return isAccountNumberField;
	}

	@FindBy(css = "//div[@class='columns-6 view-forminputtext_3']/div/div[2]/span")
	private WebElement isAccountNumberErrorMessage;

	public WebElement getAccountNumberFieldErrorMessage()
	{
		return isAccountNameErrorMessage;
	}

	///////////////////////////////////////////////////////

	@FindBy(xpath = "//label[contains(text(), 'Account nickname')]")
	private WebElement isAccountNicknameText;

	public WebElement getAccountNicknameText()
	{
		return isAccountNicknameText;
	}

	@FindBy(xpath = "//label[contains(text(), 'Optional')]/span")
	private WebElement isAccountNicknameOptionalText;

	public WebElement getAccountNicknameOptionalText()
	{
		return isAccountNicknameOptionalText;
	}

	@FindBy(css = "#input-text-view155")
	private WebElement isAccountNicknameOptionalField;

	public WebElement getAccountNicknameOptionalField()
	{
		return isAccountNicknameOptionalField;
	}

	@FindBy(xpath = "//div[@class='columns-16 view-forminputtext_4']/div/div[2]/span")
	private WebElement isAccountNicknameErrorMessage;

	public WebElement getAccountNicknameErrorMessage()
	{
		return isAccountNicknameErrorMessage;
	}

	@FindBy(xpath = "//div[@class='mvc-accounts']/form/div[3]/div/div/div/div/span/label")
	private WebElement isCheckboxAccountAndBiller;

	public WebElement getCheckboxAccountAndBiller()
	{
		return isCheckboxAccountAndBiller;
	}

	@FindBy(xpath = "//strong[contains(text(), 'SMS code for your security')]")
	private WebElement isSMSCodeForYourSecurityText;

	public WebElement getSMSCodeForYourSecurityText()
	{
		return isSMSCodeForYourSecurityText;
	}

	@FindBy(xpath = "//div[@class='mvc-accounts']/form/div[5]/div/button")
	private WebElement isGetSMSCodeButton;

	public WebElement getSMSCodeButton()
	{
		return isGetSMSCodeButton;
	}

	@FindBy(css = "#input-text-view110")
	private WebElement isGetSMSCodefield;

	public WebElement getSMSCodefield()
	{
		return isGetSMSCodefield;
	}

	@FindBy(xpath = "//div[@class='mvc-accounts']/form/div[7]/div/span[1]/button")
	private WebElement isAddButton;

	public WebElement getAddButton()
	{
		return isAddButton;
	}

	@FindBy(xpath = "//a[contains(text(), 'try again')]")
	private WebElement isStaticMessageBelowEnterSMSCode;

	public WebElement getStaticMessageBelowEnterSMSCode()
	{
		return isStaticMessageBelowEnterSMSCode;
	}

}
