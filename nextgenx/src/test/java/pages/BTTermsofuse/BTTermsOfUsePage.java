package pages.BTTermsofuse;

import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class BTTermsOfUsePage extends PageObject
{

	public BTTermsOfUsePage(WebDriver driver)
	{
		super(driver);
	}

	//verifying header
	@FindBy(xpath = "//div[@class='mvc-headerpanel']/div/h1")
	private WebElement isHeaderTermsOfUse;

	public WebElement getHeaderTermsOfUse()
	{
		return isHeaderTermsOfUse;
	}

	//verifying sub-headers
	@FindBy(xpath = "//div[@class='columns-41']/h1[1]")
	private WebElement isTermsSubHeader1;

	public WebElement getTermsSubHeader1()
	{
		return isTermsSubHeader1;
	}

	@FindBy(xpath = "//div[@class='columns-41']/h1[2]")
	private WebElement isTermsSubHeader2;

	public WebElement getTermsSubHeader2()
	{
		return isTermsSubHeader2;
	}

	@FindBy(xpath = "//div[@class='columns-41']/h1[3]")
	private WebElement isTermsSubHeader3;

	public WebElement getTermsSubHeader3()
	{
		return isTermsSubHeader3;
	}

	@FindBy(xpath = "//div[@class='columns-41']/h1[4]")
	private WebElement isTermsSubHeader4;

	public WebElement getTermsSubHeader4()
	{
		return isTermsSubHeader4;
	}

	@FindBy(xpath = "//div[@class='columns-41']/h1[5]")
	private WebElement isTermsSubHeader5;

	public WebElement getTermsSubHeader5()
	{
		return isTermsSubHeader5;
	}

	@FindBy(css = ".global-footer nav a:nth-child(7)")
	private WebElement isTextAccessibility;

	public WebElement getTextAccessibility()
	{
		return isTextAccessibility;
	}

	@FindBy(css = ".heading-five.panel-header")
	private WebElement isSeeHeaderTextAccessibility;

	public WebElement getVisibleAccessibility()
	{
		return isSeeHeaderTextAccessibility;
	}
}
