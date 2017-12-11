package pages.clientdetails;

import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

//@DefaultUrl("http://dwgps0022.btfin.com:8120/nextgen/secure/app/#ng/clients/details?c=9BFDF9948AFC795D9C27D240F683328428D50883A85A3BDF")
public class CompanyDetailsPage extends PageObject
{

	@FindBy(css = "div[class='mvc-company'] [class='view-button_2'] [class='icon icon-edit']")
	private WebElement isCompNameEditIcon;

	@FindBy(css = "div[class='mvc-company'] [class='view-button_3'] [class='icon icon-edit']")
	private WebElement isTaxOptEditIcon;

	@FindBy(css = "div[class='mvc-company'] [class='view-button_4'] [class='icon icon-edit']")
	private WebElement isRegForGSTEditIcon;

	@FindBy(css = "div[class='mvc-company'] [class='view-button_5'] [class='icon icon-edit']")
	private WebElement isRegComOfficeEditIcon;

	@FindBy(css = "div[class='mvc-company'] [class='view-button_6'] [class='icon icon-edit']")
	private WebElement isPrinPlaceEditIcon;

	@FindBy(xpath = "//div[@class='columns-20']/following::div[1]/div/a")
	private WebElement isLinkedName;

	@FindBy(xpath = "//div[@class='columns-20']/following::div[1]/div/span")
	private WebElement isLinkedNameRole;

	@FindBy(css = "div[class='mvc-company'] div:nth-child(2) h3")
	private WebElement isCompanyPreferredName;

	@FindBy(css = "input[name='clientName']")
	private WebElement isCompanyEditPreferredName;

	@FindBy(css = "span[class='error-required']")
	private WebElement isCompanyErrorMessage;

	@FindBy(css = "a[class='link-item'][title='Contact us']")
	private WebElement isContactUs;

	public WebElement getIsCompanyNameIcon()
	{

		return isCompNameEditIcon;

	}

	public WebElement getIsTaxOptEditIcon()
	{

		return isTaxOptEditIcon;

	}

	public WebElement getIsRegForGSTEditIcon()
	{

		return isRegForGSTEditIcon;

	}

	public WebElement getIsRegComOfficeEditIcon()
	{

		return isRegComOfficeEditIcon;

	}

	public WebElement getIsPrinPlaceEditIcon()
	{

		return isPrinPlaceEditIcon;

	}

	public boolean getisLinkedName()
	{
		if (isLinkedName.getAttribute("href").isEmpty())
			return false;
		else

			return true;
	}

	public boolean getisLinkedRole()
	{
		if (isLinkedNameRole.getText().isEmpty())
			return false;
		else

			return true;
	}

	public WebElement getIsCompanyPreferredName()
	{

		return isCompanyPreferredName;
	}

	public WebElement getIsCompanyEditPreferredName()
	{

		return isCompanyEditPreferredName;
	}

	public WebElement getIsCompanyErrorMessage()
	{

		return isCompanyErrorMessage;
	}

	public CompanyDetailsPage(WebDriver driver)
	{
		super(driver);
	}

}
