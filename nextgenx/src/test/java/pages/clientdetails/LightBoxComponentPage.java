package pages.clientdetails;

import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LightBoxComponentPage extends PageObject
{

	private WebDriver driver;

	@FindBy(css = "div[Class='modal'] [class='grid margin-bottom-1']:nth-of-type(1) [class='color-highlight']")
	private WebElement isEditTitle;

	@FindBy(css = "div[class='view-inputcheckbox'] span")
	private WebElement isCheckBox;

	@FindBy(css = "button[class=' btn- btn-action-tertiary'] span[class='label-content']")
	private WebElement isCancelButton;

	@FindBy(css = "div[class='modal'] span[class='icon-close']")
	private WebElement isCancel;

	@FindBy(css = "button[class='btn- btn-action-primary']")
	private WebElement isUpdateEnabled;

	@FindBy(css = "button[class='btn- btn-action-primary disabled']")
	private WebElement isUpdateDisabled;

	public LightBoxComponentPage(WebDriver driver)
	{
		super(driver);
	}

	public void getPageRefresh() throws Throwable
	{
		Thread.sleep(3000);
		getDriver().navigate().refresh();

	}

	public WebElement getIsEditTitle()
	{

		return isEditTitle;
	}

	public WebElement getIsCheckBox()
	{

		return isCheckBox;
	}

	public WebElement getIsCancelButton()
	{

		return isCancelButton;
	}

	public WebElement getIsCancel()
	{

		return isCancel;
	}

	public WebElement getIsUpdateEnabled()
	{

		return isUpdateEnabled;
	}

	public WebElement getIsUpdateDisabled()
	{

		return isUpdateDisabled;
	}

}
