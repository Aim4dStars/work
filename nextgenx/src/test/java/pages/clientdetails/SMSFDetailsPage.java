package pages.clientdetails;

import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

public class SMSFDetailsPage extends PageObject
{

	@CacheLookup
	@FindBy(css = "div[class='mvc-smsf'] [class='view-button'] [class='icon icon-edit']")
	private WebElement isSMSFRegStateEditIcon;

	@CacheLookup
	@FindBy(css = "div[class='mvc-smsf'] [class='view-button_2'] [class='icon icon-edit']")
	private WebElement isSMSFTaxOptEditIcon;

	@CacheLookup
	@FindBy(css = "div[class='mvc-smsf'] [class='view-button_3'] [class='icon icon-edit']")
	private WebElement isSMSFRegForGSTEditIcon;

	@CacheLookup
	@FindBy(css = "div[class='mvc-smsf'] [class='view-button_7'] [class='icon icon-edit']")
	private WebElement isSMSFAddEditIcon;

	@CacheLookup
	@FindBy(xpath = "//div[@class='columns-20']/following::div[1]/div/a")
	private WebElement isSMSFLinkedName;

	@CacheLookup
	@FindBy(xpath = "//div[@class='columns-20']/following::div[1]/div/span")
	private WebElement isSMSFLinkedNameRole;

	@CacheLookup
	@FindBy(css = "a[class='link-item'][title='Contact us']")
	private WebElement isContactUs;

	public WebElement getIsSMSFRegStateEditIcon()
	{

		return isSMSFRegStateEditIcon;

	}

	public WebElement getIsSMSFTaxOptEditIcon()
	{

		return isSMSFTaxOptEditIcon;

	}

	public WebElement getIsSMSFRegForGSTEditIcon()
	{

		return isSMSFRegForGSTEditIcon;

	}

	public WebElement getIsSMSFAddressEditIcon()
	{

		return isSMSFAddEditIcon;

	}

	public boolean getisSMSFLinkedName()
	{
		if (isSMSFLinkedName.getAttribute("href").isEmpty())
			return false;
		else

			return true;
	}

	public boolean getisSMSFLinkedRole()
	{
		if (isSMSFLinkedNameRole.getText().isEmpty())
			return false;
		else

			return true;
	}

	public SMSFDetailsPage(WebDriver driver)
	{
		super(driver);
	}

}
