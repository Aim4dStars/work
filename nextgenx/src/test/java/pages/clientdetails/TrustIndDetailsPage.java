package pages.clientdetails;

import java.util.List;

import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

//@DefaultUrl("/secure/app/#ng/clients/details?c=694F4F9ECD86807544FF968517B07729599DAB77B4BAC141")
public class TrustIndDetailsPage extends PageObject
{

	private WebDriver driver;

	@FindBy(css = "div[class='mvc-trust'] [class='view-button'] [class='icon icon-edit']")
	private WebElement isTrustRegStateEditIcon;

	@FindBy(css = "div[class='mvc-trust'] [class='view-button_2'] [class='icon icon-edit']")
	private WebElement isTrustTaxOptEditIcon;

	@FindBy(css = "div[class='mvc-trust'] [class='view-button_3'] [class='icon icon-edit']")
	private WebElement isTrustRegForGSTEditIcon;

	@FindBy(css = "div[class='mvc-trust'] [class='view-button_4'] [class='icon icon-edit']")
	private WebElement isTrustAddEditIcon;

	@FindBy(xpath = "//div[@class='columns-20']/following::div[1]/div/a")
	private WebElement isTrustLinkedName;

	@FindBy(xpath = "//div[@class='columns-20']/following::div[1]/div/span")
	private WebElement isTrustLinkedNameRole;

	@FindBy(how = How.TAG_NAME, using = "tr")
	private List <WebElement> istableRow;

	@FindBy(css = "label[class='radio-label selected']")
	private WebElement isRadioSelectedValue;

	@FindBy(css = "label[class='radio-label']")
	private WebElement isRadioSelect;

	@FindBy(css = "div[class='mvc-trust'] dd:nth-child(12) > ul > li > span")
	private WebElement isRegisteredGSTName;

	@FindBy(xpath = "//div[1]/div/div/div[2]/section/dl/dd[4]/ul/li/span")
	private WebElement isRegistrationStateName;

	@FindBy(css = "div[class='mvc-trust'] dd:nth-child(10) > ul > li > span")
	private WebElement isTaxOptionsName;

	@FindBy(css = "a[class='link-item'][title='Contact us']")
	private WebElement isContactUs;

	@FindBy(xpath = "//div[contains(@class,'ui-inputselect-menu dropdown-menu')]/ul/li")
	private List <WebElement> isRegisteredState;

	@FindBy(xpath = "//span[contains(@class,'ui-inputselect-icon ui-icon icon-arrow-expand-open')]")
	private WebElement isDropdownClick;

	@FindBy(css = "div[class='view-taxexemption'] span[class='ui-inputselect-icon ui-icon icon-arrow-expand-open ui-icon-triangle-1-s']")
	private WebElement isSecondDropdownClick;

	@FindBy(xpath = "//div[contains(@class,'ui-inputselect-menu dropdown-menu active ui-inputselect-open')]/ul/li")
	private List <WebElement> isTaxOptionsDropdownValues;

	@FindBy(xpath = "//div[contains(@class,'ui-inputselect-menu dropdown-menu active ui-inputselect-open')]/ul/li")
	private List <WebElement> isSecondDropdownValues;

	@FindBy(css = "div[class='mvc-taxoptions'] div[class='columns-9']>span")
	private WebElement isTaxOptionsDoNotQuoteMessage;

	@FindBy(css = "div[class='view-tfn'] input[class='text-input']")
	private WebElement isTFNNumberBox;

	@FindBy(css = "div[class='error'] span[class='error-regexp']")
	private WebElement isTFNInvalidNumberError;

	public WebElement getIsTrustRegStateEditIcon()
	{

		return isTrustRegStateEditIcon;

	}

	public WebElement getIsTrustTaxOptEditIcon()
	{

		return isTrustTaxOptEditIcon;

	}

	public WebElement getIsTrustRegForGSTEditIcon()
	{

		return isTrustRegForGSTEditIcon;

	}

	public WebElement getIsTrustAddressEditIcon()
	{

		return isTrustAddEditIcon;

	}

	public boolean getisTrustLinkedName()
	{
		if (isTrustLinkedName.getAttribute("href").isEmpty())
			return false;
		else

			return true;
	}

	public boolean getisTrustLinkedRole()
	{
		if (isTrustLinkedNameRole.getText().isEmpty())
			return false;
		else

			return true;
	}

	public List <WebElement> getisBeneficiaries()
	{

		return istableRow;

	}

	public WebElement getisRadioSelectedValue()
	{

		return isRadioSelectedValue;

	}

	public WebElement getisRadioSelect()
	{

		return isRadioSelect;

	}

	public WebElement getisRegisteredGSTName()
	{

		return isRegisteredGSTName;

	}

	public WebElement getisRegistrationStateName()
	{

		return isRegistrationStateName;

	}

	public List <WebElement> getisRegisteredState() throws Exception
	{

		isDropdownClick.click();
		return isRegisteredState;
	}

	public WebElement getisDropDownIcon()
	{

		return isDropdownClick;

	}

	public List <WebElement> getIsTaxOptionsDropdownValues() throws Exception
	{

		isDropdownClick.click();
		return isTaxOptionsDropdownValues;
	}

	public List <WebElement> getIsSecondDropdownValues() throws Exception
	{

		isSecondDropdownClick.click();
		return isSecondDropdownValues;
	}

	public WebElement getisSecondDropDownIcon()
	{

		return isSecondDropdownClick;

	}

	public WebElement getIsTaxOptionsDoNotQuoteMessage()
	{

		return isTaxOptionsDoNotQuoteMessage;

	}

	public WebElement getIsTFNNumberBox()
	{

		return isTFNNumberBox;

	}

	public WebElement getIsTaxOptionsName()
	{

		return isTaxOptionsName;

	}

	public WebElement getIsTFNInvalidNumberError()
	{

		return isTFNInvalidNumberError;

	}

	public TrustIndDetailsPage(WebDriver driver)
	{
		super(driver);
	}

}
