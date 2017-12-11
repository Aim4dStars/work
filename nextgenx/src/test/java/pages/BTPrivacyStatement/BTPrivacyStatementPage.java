package pages.BTPrivacyStatement;

//import static org.junit.Assert.*;
import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class BTPrivacyStatementPage extends PageObject
{

	//@FindBy(css = ".heading-five.panel-header")
	@FindBy(xpath = "//div[@class='mvc-headerpanel']/div/h1")
	private WebElement isHeaderPrivacyStatement;

	//Verifying page header
	public WebElement getHeaderPrivacyStatement()
	{
		return isHeaderPrivacyStatement;
	}

	//Verifying sub headers
	@FindBy(xpath = "//div[@class='columns-42']/h3")
	private WebElement isPrivacySubHeader1;

	public WebElement getPrivacySubHeader1()
	{
		return isPrivacySubHeader1;
	}

	@FindBy(xpath = "//div[@class='columns-42']/h3[2]")
	private WebElement isPrivacySubHeader2;

	public WebElement getPrivacySubHeader2()
	{
		return isPrivacySubHeader2;
	}

	@FindBy(xpath = "//div[@class='columns-42']/h3[3]")
	private WebElement isPrivacySubHeader3;

	public WebElement getPrivacySubHeader3()
	{
		return isPrivacySubHeader3;
	}

	@FindBy(xpath = "//div[@class='columns-42']/h3[4]")
	private WebElement isPrivacySubHeader4;

	public WebElement getPrivacySubHeader4()
	{
		return isPrivacySubHeader4;
	}

	@FindBy(xpath = "//div[@class='columns-42']/h3[5]")
	private WebElement isPrivacySubHeader5;

	public WebElement getPrivacySubHeader5()
	{
		return isPrivacySubHeader5;
	}

	@FindBy(xpath = "//div[@class='columns-42']/h3[6]")
	private WebElement isPrivacySubHeader6;

	public WebElement getPrivacySubHeader6()
	{
		return isPrivacySubHeader6;
	}

	@FindBy(xpath = "//div[@class='columns-42']/h3[7]")
	private WebElement isPrivacySubHeader7;

	public WebElement getPrivacySubHeader7()
	{
		return isPrivacySubHeader7;
	}

	@FindBy(xpath = "//div[@class='columns-42']/h3[8]")
	private WebElement isPrivacySubHeader8;

	public WebElement getPrivacySubHeader8()
	{
		return isPrivacySubHeader8;
	}

	@FindBy(xpath = "//div[@class='columns-42']/h3[9]")
	private WebElement isPrivacySubHeader9;

	public WebElement getPrivacySubHeader9()
	{
		return isPrivacySubHeader9;
	}

	@FindBy(xpath = "//div[@class='columns-42']/h3[10]")
	private WebElement isPrivacySubHeader10;

	public WebElement getPrivacySubHeader10()
	{
		return isPrivacySubHeader10;
	}

	@FindBy(xpath = "//div[@class='columns-42']/h3[11]")
	private WebElement isPrivacySubHeader11;

	public WebElement getPrivacySubHeader11()
	{
		return isPrivacySubHeader11;
	}

	@FindBy(xpath = "//div[@class='columns-42']/h3[12]")
	private WebElement isPrivacySubHeader12;

	public WebElement getPrivacySubHeader12()
	{
		return isPrivacySubHeader12;
	}

	@FindBy(xpath = "//div[@class='columns-42']/h3[13]")
	private WebElement isPrivacySubHeader13;

	public WebElement getPrivacySubHeader13()
	{
		return isPrivacySubHeader13;
	}

	@FindBy(css = ".text-link")
	private WebElement isLinkContactUs;

	public WebElement getLinkContactUs()
	{
		return isLinkContactUs;

	}

	@FindBy(css = ".text-link")
	private WebElement islinkAnyWestpacBranch;

	public WebElement getLinkAnyWespacBranch()
	{
		return islinkAnyWestpacBranch;

	}

	@FindBy(css = ".global-footer nav a:nth-child(7)")
	private WebElement islinkAccessibility;

	public WebElement getLinkAccessibility()

	{
		return islinkAccessibility;

	}

	@FindBy(css = ".heading-five.panel-header")
	private WebElement isAccessibilityText;

	public WebElement getAccessibilityText()
	{
		return isAccessibilityText;

	}

	public BTPrivacyStatementPage(WebDriver driver)
	{
		super(driver);
	}

}
