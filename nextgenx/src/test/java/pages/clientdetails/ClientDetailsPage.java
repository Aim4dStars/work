package pages.clientdetails;

import java.util.List;

import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

//@DefaultUrl("http://dwgps0022.btfin.com:8120/nextgen/secure/app/#ng/clients/details?c=AA8614A18D2DF1E2AAAE1E96C3297644BC2C34B6E1E01804")
public class ClientDetailsPage extends PageObject
{

	@FindBy(css = "span[class='view-button_5']>button>span>span>span")
	private WebElement isNameEditIcon;

	@FindBy(css = "span[class='view-button_6']>button>span>span>span")
	private WebElement isContactEditIcon;

	@FindBy(css = "span[class='view-button_7']>button>span>span>span")
	private WebElement isResAddEditIcon;

	@FindBy(css = "span[class='view-button_8']>button>span>span>span")
	private WebElement isPosAddEditIcon;

	@FindBy(css = "span[class='view-button_9']>button>span>span>span")
	private WebElement isTaxOptEditIcon;

	@FindBy(css = "span[class='view-button_10']>button>span>span>span")
	private WebElement isCountryEditIcon;

	@FindBy(css = "div.view-definitionlist_5 > section > dl > dd > ul > li > span")
	private WebElement isPreferredName;

	@FindBy(css = "div.view-definitionlist_8 > section > dl > dd > ul > li > span")
	private WebElement isEditedPreferredNameOnScreen;

	@FindBy(css = "input[name='preferredName']")
	private WebElement isEditPreferredName;

	@FindBy(css = "a[class='link-item'][title='Contact us']")
	private WebElement isContactUs;

	@FindBy(xpath = "//div[contains(@class,'ui-inputselect-menu dropdown-menu')]/ul/li")
	private List <WebElement> isCountriesInDropdownCountryOfResidence;

	@FindBy(css = "dd:nth-child(22) > ul > li > span")
	private WebElement isCountryOfResidenceName;

	@FindBy(xpath = "//span[contains(@class,'ui-inputselect-icon ui-icon icon-arrow-expand-open')]")
	private WebElement isDropdownClick;

	@FindBy(css = "div[class='view-email0 disabled']")
	private WebElement isContactDetailsEmail0;

	@FindBy(css = "div[class='view-email1']>input[class='text-input']")
	private WebElement isContactDetailsEmail1;

	@FindBy(css = "ul > li:nth-child(2) > span")
	private WebElement isOnScreenContactDetailsEmail1;

	@FindBy(css = "div[class='view-phonenumber0 disabled']>input[class='text-input disabled']")
	private WebElement isContactDetailsMobile0;

	@FindBy(xpath = "//div[contains(@class,'ui-inputselect-menu dropdown-menu')]/ul/li")
	private List <WebElement> isOptionsInAddDropdown;

	@FindBy(css = "div[class='view-phonenumber1']>input[class='text-input']")
	private WebElement isContactDetailsMobile1;

	@FindBy(css = "div:nth-child(2) > div:nth-child(2) > div > div:nth-child(2) > span")
	private WebElement isOnScreenContactDetailsMobile1;

	@FindBy(css = "div[class='view-phonenumber2']>input[class='text-input']")
	private WebElement isContactDetailsPhoneNumber2;

	@FindBy(css = "div:nth-child(3) > div:nth-child(2) > div > div:nth-child(2) > span")
	private WebElement isOnScreenContactDetailsPhoneNumber2;

	@FindBy(css = "div[class='view-phonenumber3']>input[class='text-input']")
	private WebElement isContactDetailsPhoneNumber3;

	@FindBy(css = "ol[class='radio-group']")
	private WebElement isRadioButtons;

	@FindBy(css = "div[class='view-forminputtext_2'] span[class='error-regexp']")
	private WebElement isEmailErrorMessage;

	@FindBy(css = "div:nth-child(5) > div > div:nth-child(2) > div > div.columns-10 > span")
	private WebElement DisplayedContactDetailsNumber0;

	@FindBy(css = "div:nth-child(5) > div > div:nth-child(2) > div > div:nth-of-type(2).columns-1 > span")
	private WebElement DisplayedContactDetailsHomeNumber;

	public WebElement getIsClientNameIcon()
	{
		return isNameEditIcon;
	}

	public WebElement getIsContactEditIcon()
	{

		return isContactEditIcon;

	}

	public WebElement getIsResAddEditIcon()
	{

		return isResAddEditIcon;

	}

	public WebElement getIsPosAddEditIcon()
	{

		return isPosAddEditIcon;

	}

	public WebElement getIsTaxOptEditIcon()
	{

		return isTaxOptEditIcon;

	}

	public WebElement getIsCountryOptEditIcon()
	{

		return isCountryEditIcon;

	}

	public ClientDetailsPage(WebDriver driver)
	{
		super(driver);
	}

	public WebElement getIsPreferredName()
	{

		return isPreferredName;
	}

	public WebElement getIsEditedPreferredNameOnScreen()
	{

		return isEditedPreferredNameOnScreen;
	}

	public WebElement getIsEditPreferredName()
	{

		return isEditPreferredName;
	}

	public WebElement getIsContactDetailsEmail0()
	{

		return isContactDetailsEmail0;
	}

	public WebElement getIsContactDetailsMobile0()
	{

		return isContactDetailsMobile0;
	}

	public WebElement getIsOnScreenContactDetailsMobile1()
	{

		return isOnScreenContactDetailsMobile1;
	}

	public WebElement getIsContactDetailsEmail1()
	{

		return isContactDetailsEmail1;
	}

	public WebElement getIsOnScreenContactDetailsEmail1()
	{

		return isOnScreenContactDetailsEmail1;
	}

	public List <WebElement> getIsCountriesInDropdownCountryOfResidence()
	{
		isDropdownClick.click();
		return isCountriesInDropdownCountryOfResidence;
	}

	public List <WebElement> getIsOptionsInAddDropdown()
	{
		isDropdownClick.click();
		return isOptionsInAddDropdown;
	}

	public WebElement getIsDropdownClick()
	{

		return isDropdownClick;
	}

	public WebElement getIsContactDetailsMobile1()
	{

		return isContactDetailsMobile1;
	}

	public WebElement getIsContactDetailsPhoneNumber2()
	{

		return isContactDetailsPhoneNumber2;
	}

	public WebElement getIsOnScreenContactDetailsPhoneNumber2()
	{

		return isOnScreenContactDetailsPhoneNumber2;
	}

	public WebElement getIsContactDetailsPhoneNumber3()
	{

		return isContactDetailsPhoneNumber3;
	}

	public WebElement getIsCountryOfResidenceName()
	{

		return isCountryOfResidenceName;

	}

	public WebElement getIsEmailErrorMessage()
	{

		return isEmailErrorMessage;

	}

}
