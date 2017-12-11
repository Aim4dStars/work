package pages.taxinvoice;

import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

//@DefaultUrl("/secure/app/#ng/fees/taxinvoice?r=adviser")
public class GenerateTaxInvoiceAdviserPage extends PageObject
{

	@FindBy(css = "div[class='headerpanel'] h1[class='heading-five panel-header']")
	private WebElement isTaxInvoiceScreenHeading;

	@FindBy(css = "div[class='mvc-taxinvoices'] h2")
	private WebElement isTaxInvoiceScreenSubHeading;

	@FindBy(css = "div span[class='view-tooltip'] span[class='tooltip-container']")
	private WebElement isHelpIcon;

	@FindBy(css = "button[class=' btn- btn-action-primary'] span[class='label-content']")
	private WebElement isGenerateInvoiceButton;

	@FindBy(css = "div[class='response-message helpful-information  message-alert-dynamic")
	private WebElement isInstructionalText;

	@FindBy(css = "select[name='month']")
	private WebElement isMonthDropDown;

	@FindBy(css = "span[class='icon-container  default ']")
	private WebElement isErrorMessageDisplay;

	@FindBy(css = "span[class='icon-arrow-expand-open']")
	private WebElement isDropDownIcon;

	public WebElement getIsTaxInvoiceScreenHeading()
	{

		return isTaxInvoiceScreenHeading;

	}

	public WebElement getIsTaxInvoiceScreenSubHeading()
	{

		return isTaxInvoiceScreenSubHeading;

	}

	public WebElement getIsHelpIcon()
	{

		return isHelpIcon;

	}

	public WebElement getIsGenerateInvoiceButton()
	{

		return isGenerateInvoiceButton;

	}

	public WebElement getIsInstructionalText()
	{

		return isInstructionalText;

	}

	public WebElement getIsMonthDropDown()
	{

		return isMonthDropDown;

	}

	public WebElement getIsErrorMessageDisplay()
	{

		return isErrorMessageDisplay;

	}

	public WebElement getIsDropDownIcon()
	{

		return isDropDownIcon;

	}

	public GenerateTaxInvoiceAdviserPage(WebDriver driver)
	{
		super(driver);
	}
}
