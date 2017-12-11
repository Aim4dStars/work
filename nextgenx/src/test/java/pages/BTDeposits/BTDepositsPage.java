package pages.BTDeposits;

import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class BTDepositsPage extends PageObject
{
	public BTDepositsPage(WebDriver driver)
	{
		super(driver);
	}

	@FindBy(xpath = "h1[class='heading-five panel-header']")
	private WebElement headerMoveMoney;

	@FindBy(xpath = "//a[contains(text(), 'Deposits')]")
	private WebElement tbMakeADeposit;

	@FindBy(xpath = "//span[contains(text(), 'Make a deposit from')]")
	private WebElement lblMakeDepositFrom;

	@FindBy(xpath = "//input[@name='payeelist']")
	private WebElement txtMakeDepositFrom;

	@FindBy(xpath = "//span[contains(text(), 'Amount')]")
	private WebElement lblAmount;

	@FindBy(xpath = "//input[@name='amount']")
	private WebElement txtAmount;

	@FindBy(xpath = "//input[@name='depositdate']")
	private WebElement txtDate;

	@FindBy(xpath = "//label[contains(text(), 'Repeat deposit')]")
	private WebElement lblRepeatDeposit;

	@FindBy(xpath = "//input[@name='repeatdeposit']")
	private WebElement chkRepeatDeposit;

	@FindBy(xpath = "//span[contains(text(), 'Description ')]")
	private WebElement lblDescription;

	@FindBy(xpath = "//span[contains(text(), 'Optional')]")
	private WebElement lblOptional;

	@FindBy(xpath = "//input[@name='description']")
	private WebElement txtDescription;

	@FindBy(xpath = "//p[contains(text(), 'Maximum 18 letters and numbers')]")
	private WebElement txt18Char;

	@FindBy(xpath = "//span[contains(text(), 'Next')]")
	private WebElement btnNext;

	@FindBy(xpath = "//span[contains(text(), 'Clear all')]")
	private WebElement blnkClearAll;

	@FindBy(xpath = "//input[@placeholder= 'Search or select']")
	private WebElement textSearchOrSelect;

	public WebElement getheaderMoveMoney()
	{
		return headerMoveMoney;
	}

	public WebElement gettextSearchOrSelect()
	{
		return textSearchOrSelect;
	}

	public WebElement gettbMakeADeposit()
	{
		return tbMakeADeposit;
	}

	public WebElement getlblMakeDepositFrom()
	{
		return lblMakeDepositFrom;
	}

	public WebElement gettxtMakeDepositFrom()
	{
		return txtMakeDepositFrom;
	}

	public WebElement getlblAmount()
	{
		return lblAmount;
	}

	public WebElement gettxtAmount()
	{
		return txtAmount;
	}

	public WebElement gettxtDate()
	{
		return txtDate;
	}

	public WebElement getlblRepeatDeposit()
	{
		return lblRepeatDeposit;
	}

	public WebElement getchkRepeatDeposit()
	{
		return chkRepeatDeposit;
	}

	public WebElement getlblDescription()
	{
		return lblDescription;
	}

	public WebElement getlblOptional()
	{
		return lblOptional;
	}

	public WebElement gettxtDescription()
	{
		return txtDescription;
	}

	public WebElement gettxt18Char()
	{
		return txt18Char;
	}

	public WebElement getbtnNext()
	{
		return btnNext;
	}

	public WebElement getlnkClearAll()
	{
		return blnkClearAll;
	}

	//Empty tab out error messages

	@FindBy(xpath = "Defect is locked for this- currently no error message is displayed")
	private WebElement textMakeDepositFromFieldErrorMsg;

	public WebElement gettextMakeDepositFromFieldErrorMsg()
	{
		return textMakeDepositFromFieldErrorMsg;
	}

	@FindBy(xpath = "//div[@class='columns-10 margin-bottom-1 view-forminputtext']/div/div[2]/span[3]")
	private WebElement textAmountFieldErrorMsg;

	public WebElement gettextAmountFieldErrorMsg()
	{
		return textAmountFieldErrorMsg;
	}

	@FindBy(xpath = "//div[@class='columns-9 view-forminputdate']/div/div[2]/span[2]")
	private WebElement textDateFieldErrorMsg;

	public WebElement gettextDateFieldErrorMsg()
	{
		return textDateFieldErrorMsg;
	}

	@FindBy(xpath = "//a[contains(text(),'Monthly')]")
	private WebElement selectMonthlyInRepeatEvery;

	public WebElement getselectMonthlyInRepeatEvery()
	{
		return selectMonthlyInRepeatEvery;
	}

	//
	@FindBy(xpath = "//a[contains(text(),'Set end date')]")
	private WebElement selectSetEndDateInEndRepeat;

	public WebElement getselectSetEndDateInEndRepeat()
	{
		return selectSetEndDateInEndRepeat;
	}

	@FindBy(xpath = "//input[@name='repeatenddate']")
	private WebElement dtRepeatEndDate;

	public WebElement getdtRepeatEndDate()
	{
		return dtRepeatEndDate;
	}

	@FindBy(xpath = "//span[contains(text(),'Confirm and deposit')]")
	private WebElement textConfirmAndDeposit;

	public WebElement getTextConfirmAndDeposit()
	{
		return textConfirmAndDeposit;
	}

	@FindBy(xpath = "//span[contains(text(),'Confirm and pay')]/following-sibling::span")
	private WebElement textAmountEnteredInForm;

	public WebElement getTextAmountEnteredInForm()
	{
		return textAmountEnteredInForm;
	}

	@FindBy(xpath = "//div[@class='account-from']/div")
	private WebElement textAccountNameFrom;

	public WebElement getTextAccountNameFrom()
	{
		return textAccountNameFrom;
	}

	@FindBy(xpath = "//div[@class='account-from']/div[2]/span")
	private WebElement textBSBNumberFrom;

	public WebElement getTextBSBNumberFrom()
	{
		return textBSBNumberFrom;
	}

	@FindBy(xpath = "//div[@class='account-from']/div[2]/span[2]")
	private WebElement textAccountNumberFrom;

	public WebElement getTextAccountNumberFrom()
	{
		return textAccountNumberFrom;
	}

	@FindBy(xpath = "//div[@class='account-to']/div")
	private WebElement textAccountNameTo;

	public WebElement getTextAccountNameTo()
	{
		return textAccountNameTo;
	}

	@FindBy(xpath = "//div[@class='account-to']/div[2]/span")
	private WebElement textBSBNumberTo;

	public WebElement getTextBSBNumberTo()
	{
		return textBSBNumberTo;
	}

	@FindBy(xpath = "//div[@class='account-to']/div[2]/span[2]")
	private WebElement textAccountNumberTo;

	public WebElement getTextAccountNumberTo()
	{
		return textAccountNumberTo;
	}

	@FindBy(xpath = "//div[@id='md-modal-386']/div[2]/div[3]/div[2]")
	private WebElement textDate;

	public WebElement getTextDate()
	{
		return textDate;
	}

	@FindBy(xpath = "//div[@id='md-modal-386']/div[2]/div[4]/div[2]/div")
	private WebElement textRepeatFrequency;

	public WebElement getTextRepeatFrequency()
	{
		return textRepeatFrequency;
	}

	@FindBy(xpath = "//div[@id='md-modal-386']/div[2]/div[4]/div[2]/div[2]")
	private WebElement textRepeatDate;

	public WebElement getTextRepeatDate()
	{
		return textRepeatDate;
	}

	@FindBy(xpath = "//div[@id='md-modal-386']/div[2]/div[5]/div[2]")
	private WebElement textDescription;

	public WebElement getTextDescription()
	{
		return textDescription;
	}

	@FindBy(xpath = "//span[contains(text(),'Deposit')]")
	private WebElement btnPay;

	public WebElement getBtnPay()
	{
		return btnPay;
	}

	@FindBy(xpath = "//span[contains(text(),'Change limit')]")
	private WebElement btnChangeLimit;

	public WebElement getBtnChangeLimit()
	{
		return btnChangeLimit;
	}

	@FindBy(xpath = "//span[contains(text(),'Change')]")
	private WebElement btnChange;

	public WebElement getBtnChange()
	{
		return btnChange;
	}

	@FindBy(css = "button.btn-.btn-action-primary.final-action")
	private WebElement isButtonDeposit;

	public WebElement getButtonDeposit()
	{
		return isButtonDeposit;
	}

	@FindBy(css = ".header-statement")
	private WebElement isDepositSuccessfulSubmitHeader;

	public WebElement getDepositSuccessfulSubmitHeader()
	{
		return isDepositSuccessfulSubmitHeader;
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

	@FindBy(xpath = "//div[@class='mvc-receipt']/div[6]/div/strong")
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

}
