package pages.receipt;

import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

public class FeeDetailsReceiptPage extends PageObject
{

	@CacheLookup
	@FindBy(xpath = "//div[@class='color-successful']")
	private WebElement feeDetailsReceiptMsg;

	@CacheLookup
	@FindBy(xpath = "//div[@id='rootContainer']/div/div/div/div/article/div/div/div/div[2]/div/div/div/div/div[3]/a")
	private WebElement isRtrntoclntlist;

	public FeeDetailsReceiptPage(WebDriver driver)
	{
		super(driver);
	}

	public WebElement getIsFeeDetailsReceiptPage()
	{
		return feeDetailsReceiptMsg;
	}

	public WebElement getIsRtrntoclntlist()
	{
		return isRtrntoclntlist;
	}

	public void setIsRtrntoclntlist(WebElement isRtrntoclntlist)
	{
		this.isRtrntoclntlist = isRtrntoclntlist;
	}

}
