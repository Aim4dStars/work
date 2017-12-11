package pages.feeshedule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import net.thucydides.core.pages.PageObject;

public class ConfirmFeeShedulePage extends PageObject
{
	public final WebDriver driver;

	@FindBy(xpath = "//h1[@class='heading-five panel-header']")
	private WebElement ConfirmFeeMsg;

	public ConfirmFeeShedulePage(WebDriver driver)
	{
		this.driver = driver;
	}

	public WebElement getIsConfirmFeeMsg()
	{
		return ConfirmFeeMsg;
	}
}
