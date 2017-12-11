package pages.fees;

import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

//@DefaultUrl("http://dwgps0022.btfin.com:8120/nextgen/secure/app/#ng/account/fees/chargeoneoff?c=3EBF40671FE2F335D4C7CDE112402296A9393DE0FB941442&p=BB8FE88383B20FDD59ED5E2DD0231C347EED094FC7DF2098")
//@DefaultUrl("http://dwgps0022.btfin.com:8120/nextgen/secure/app/#ng/fees/advicefees")
public class FeeDetailsPage extends PageObject
{

	@FindBy(xpath = "//input[contains(@name,'feesAmount')]")
	private WebElement isAdviceFee;

	@FindBy(css = "input[class='text-input']")
	private WebElement isDescriptionText;

	@FindBy(xpath = "//button[@type='submit']")
	private WebElement isNextButton;

	@FindBy(xpath = "//span[@class='view-next']/button[contains(@class,'btn')]")
	private WebElement isSubmitButton;

	@FindBy(className = "error-min-value")
	private WebElement errMinAmount;

	@FindBy(className = "error-custom-format")
	private WebElement errMoreThanTwoDecimal;

	@FindBy(className = "error-regexp")
	private WebElement errNegAmount;

	@FindBy(className = "error-regexp")
	private WebElement errRegexAmount;

	@FindBy(className = "error-required")
	private WebElement errNullAmount;

	@FindBy(xpath = "//form/div/div")
	private WebElement avlCashChk;

	@FindBy(xpath = "//p")
	private WebElement caperrAmount;

	@FindBy(xpath = "//div[@class='columns-30']")
	private WebElement totalAmount;

	@FindBy(xpath = "//div[@class='nav-page-title default']/div/div[2]/div/h1[@class='heading-five panel-header']")
	private WebElement oneOffText;

	@FindBy(css = "a[class='link-item'][title='Contact us']")
	private WebElement isContactUs;

	@FindBy(css = "div[class='mvc-confirmation'] div[class='columns-28']")
	private WebElement isConfirmPageFeesAmount;

	@FindBy(css = "div > div:nth-child(2) > div.columns-28")
	private WebElement isReceiptPageFeesAmount;

	@FindBy(css = "div[class='mvc-confirmation'] div[class='columns-28 margin-bottom-1']")
	private WebElement isConfirmPageFeesDescription;

	@FindBy(css = "div[class='mvc-receipt'] div[class='grid margin-bottom-2'] div[class='columns-28']")
	private WebElement isReceiptPageFeesDescription;

	@FindBy(xpath = "//div[contains(@class,'view-termsandconditions')]/div[1]")
	private WebElement isConfirmPageHighlightedText;

	@FindBy(css = "div[class='view-termsandconditions'] label")
	private WebElement isConfirmPageText;

	@FindBy(css = "span[class='view-button'] button[class=' btn- btn-action-tertiary'] span[class='label-content']")
	private WebElement isChargeFeesCancel;

	@FindBy(css = "div[class='mvc-confirmation'] button[class=' btn- btn-action-tertiary'] span[class='label-content']")
	private WebElement isConfirmChargeFeesCancel;

	@FindBy(css = "div[class='button-action-bar'] button[class=' btn- btn-action-tertiary'] span[class='label-content']")
	private WebElement isChargeFeesHeaderPanelCancel;

	@FindBy(css = "div > span.view-next > button > span > span.label-content")
	private WebElement isChargeFeesCancelPopUpYesButton;

	@FindBy(css = "div > span.view-button > button > span > span.label-content")
	private WebElement isChargeFeesCancelPopUpNoButton;

	@FindBy(css = "h1[class='heading-five panel-header']")
	private WebElement isChargeFeesPageHeader;

	@FindBy(css = "input[class='text-input']")
	private WebElement isChargeFeesTestDescriptionText;

	@FindBy(css = "div[class='snapshot-container grid secondary'] div:nth-of-type(2) [class='heading']")
	private WebElement isChargeFeesDollarAmount;

	@FindBy(css = "div[class='snapshot-container grid secondary'] div:nth-of-type(3) [class='heading']")
	private WebElement isAvalCashDollarAmount;

	@FindBy(css = "div[class='view-tooltip'] [class='default-tooltip-trigger icon icon-support-help BTBlue ']")
	private WebElement isOneOffHelpText;

	@FindBy(css = "div[class='view-tooltip_2'] [class='default-tooltip-trigger icon icon-support-help BTBlue ']")
	private WebElement isDescriptionHelpText;

	@FindBy(css = "div[class='margin-bottom-2 view-messagealert'] span[class='message'] p")
	private WebElement errorMessageExceedsAvailableCash;

	@FindBy(css = "h1[class='heading-five panel-header']")
	private WebElement isReceiptScreenHeader;

	@FindBy(css = "h1[class='heading-five panel-header']")
	private WebElement isAccountOverviewScreenHeader;

	@FindBy(css = "div[class='view-button'] a[class='btn-action-tertiary']")
	private WebElement isReturnAccountOverviewButton;

	@FindBy(css = "div[class='view-button_2'] a[class='btn-action-tertiary']")
	private WebElement isReturnClientListButton;

	@FindBy(xpath = "//div[@class='view-tooltip']/span/div/p")
	private WebElement isOneOffTooltiptext;

	@FindBy(css = "div[class='view-tooltip_2'] [class='tooltip-container'] >div [class='tooltip-text']")
	private WebElement isOneOffDescTooltiptext;

	@FindBy(css = "div[class='view-inputcheckbox']")
	private WebElement isAgreementText;

	@FindBy(css = "div[class='view-inputcheckbox'] span")
	private WebElement isCheckbox;

	@FindBy(css = "div[class='mvc-receipt'] div[class='grid margin-bottom-0'] h1:nth-child(2)")
	private WebElement isReceiptPageDate;

	@FindBy(css = "div[class='mvc-receipt'] div[class='grid margin-bottom-0'] h1:nth-child(1)")
	private WebElement isReceiptPageConfirmText;

	@FindBy(css = "h1[class='heading-five panel-header']")
	private WebElement isClientListScreenHeader;

	public FeeDetailsPage(WebDriver driver)
	{
		super(driver);
	}

	public WebElement getIsClientListHeader()
	{
		return isClientListScreenHeader;
	}

	public WebElement getIsAdviceFee()
	{
		return isAdviceFee;
	}

	public WebElement getIsDescriptionText()
	{
		return isDescriptionText;
	}

	public void setIsAdviceFee(WebElement isAdviceFee)
	{
		this.isAdviceFee = isAdviceFee;
	}

	public WebElement getIsNextButton()
	{
		return isNextButton;
	}

	public WebElement getIsSubmitButton()
	{
		return isSubmitButton;
	}

	public void setIsNextButton(WebElement isNextButton)
	{
		this.isNextButton = isNextButton;
	}

	public WebElement getErrReqAmount()
	{
		return errMinAmount;
	}

	public void setErrReqAmount(WebElement errReqAmount)
	{
		this.errMinAmount = errReqAmount;
	}

	public WebElement geterrNegAmount()
	{
		return errNegAmount;
	}

	public void setErrNegAmount(WebElement errNegAmount)
	{
		this.errNegAmount = errNegAmount;
	}

	public WebElement getErrNullAmount()
	{
		return errNullAmount;

	}

	public void setErrnullAmount(WebElement errNullAmount)
	{
		this.errNullAmount = errNullAmount;
	}

	public WebElement geterrRegexAmount()
	{
		return errRegexAmount;

	}

	public WebElement getTtlCashChk()
	{
		return totalAmount;
	}

	public WebElement getAvlCashChk()
	{
		return avlCashChk;
	}

	public void setAvlCashChk(WebElement errAvlCashChk)
	{
		this.avlCashChk = errAvlCashChk;
	}

	public void seterrRegexAmount(WebElement errRegexAmount)
	{
		this.errRegexAmount = errRegexAmount;

	}

	public WebElement getIsOneOffText()
	{
		return oneOffText;

	}

	public WebElement getIsConfirmPageFeesAmount()
	{
		return isConfirmPageFeesAmount;

	}

	public WebElement getIsReceiptPageFeesAmount()
	{
		return isReceiptPageFeesAmount;

	}

	public WebElement getIsConfirmPageFeesDescription()
	{
		return isConfirmPageFeesDescription;

	}

	public WebElement getIsReceiptPageFeesDescription()
	{
		return isReceiptPageFeesDescription;

	}

	public WebElement getIsConfirmPageHighlightedText()
	{
		return isConfirmPageHighlightedText;

	}

	public WebElement getIsConfirmPageText()
	{
		return isConfirmPageText;

	}

	public WebElement getIsChargeFeesCancel()
	{
		return isChargeFeesCancel;

	}

	public WebElement getIsChargeFeesCancelPopUpYesButton()
	{
		return isChargeFeesCancelPopUpYesButton;

	}

	public WebElement getIsChargeFeesCancelPopUpNoButton()
	{
		return isChargeFeesCancelPopUpNoButton;

	}

	public WebElement getIsChargeFeesPageHeader()
	{
		return isChargeFeesPageHeader;

	}

	public WebElement getIsChargeFeesTestDescriptionText()
	{
		return isChargeFeesTestDescriptionText;

	}

	public void setErrcaperramount(WebElement errNullAmount)
	{
		this.caperrAmount = caperrAmount;
	}

	public WebElement getErrCapErrAmount()
	{
		return caperrAmount;
	}

	public WebElement getIsChargeFeesdollaramount()
	{
		return isChargeFeesDollarAmount;
	}

	public WebElement getisAvalCashdollaramount()
	{
		return isAvalCashDollarAmount;
	}

	public WebElement getIsDescriptionHelpText()
	{
		return isDescriptionHelpText;
	}

	public WebElement getIsOneOffHelpText()
	{
		return isOneOffHelpText;
	}

	public WebElement isErrMoreThanTwoDecimal()
	{
		return errMoreThanTwoDecimal;
	}

	public WebElement getErrMinAmount()
	{
		return errMinAmount;
	}

	public WebElement IsErrNullAmount()
	{
		return errNullAmount;
	}

	public WebElement IsErrorMessageExceedsAvailableCash()
	{
		return errorMessageExceedsAvailableCash;
	}

	public WebElement getIsReceiptScreenHeader()
	{
		return isReceiptScreenHeader;
	}

	public WebElement getIsAccountOverviewScreenHeader()
	{
		return isAccountOverviewScreenHeader;
	}

	public WebElement getIsReturnAccountOverviewButton()
	{
		return isReturnAccountOverviewButton;
	}

	public WebElement getIsReturnClientListButton()
	{
		return isReturnClientListButton;

	}

	public WebElement getIsOneOffToolTipText()
	{

		return isOneOffTooltiptext;
	}

	public WebElement getIsOneOffDescToolTipText()
	{
		return isOneOffDescTooltiptext;
	}

	public WebElement getIsAgreementText()
	{
		return isAgreementText;
	}

	public WebElement getIsCheckbox()
	{
		return isCheckbox;
	}

	public WebElement getIsReceiptPageDate()
	{
		return isReceiptPageDate;
	}

	public WebElement getIsReceiptPageConfirmText()
	{
		return isReceiptPageConfirmText;
	}

	public WebElement getIsChargeFeesHeaderPanelCancel()
	{
		return isChargeFeesHeaderPanelCancel;
	}

	public WebElement getIsConfirmChargeFeesCancel()
	{
		return isConfirmChargeFeesCancel;
	}

}
