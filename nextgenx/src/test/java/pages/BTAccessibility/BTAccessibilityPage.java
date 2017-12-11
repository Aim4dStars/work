package pages.BTAccessibility;

//import static org.junit.Assert.*;
import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class BTAccessibilityPage extends PageObject
{

	public BTAccessibilityPage(WebDriver driver)
	{
		super(driver);
	}

	//Verifying Accesibility header
	@FindBy(css = ".heading-five.panel-header")
	private WebElement isAccessibilityText;

	public WebElement getAccessibilityText()
	{
		return isAccessibilityText;
	}

	//Verifying text Image  header
	@FindBy(xpath = "//div[@class='mvc-accessibility']/div/div/h2[1]")
	private WebElement isImagesText;

	public WebElement getImageText()
	{
		return isImagesText;
	}

	//Verifying text header Table and Forms
	@FindBy(xpath = "//div[@class='mvc-accessibility']/div/div/h2[2]")
	private WebElement isTablesandformsText;

	public WebElement getTablesandformsText()
	{
		return isTablesandformsText;
	}

	//Verifying text header Javascript Text
	@FindBy(xpath = "//div[@class='mvc-accessibility']/div/div/h2[3]")
	private WebElement isJavascriptText;

	public WebElement getJavascriptText()
	{
		return isJavascriptText;
	}

	//Verify text header pdf
	@FindBy(xpath = "//div[@class='mvc-accessibility']/div/div/h2[4]")
	private WebElement isPdfText;

	public WebElement getPdfText()
	{
		return isPdfText;
	}

	//Verify text header Converting Pdfs
	@FindBy(xpath = "//div[@class='mvc-accessibility']/div/div/h2[5]")
	private WebElement isConvertingPDFsText;

	public WebElement getConvertingPdfsText()
	{
		return isConvertingPDFsText;
	}

	@FindBy(xpath = "//div[@class='mvc-accessibility']/div/div/div[5]/div/p/a")
	private WebElement islinkiUnderPdf;

	public WebElement getLinkUnderPdfText()
	{
		return islinkiUnderPdf;
	}

	@FindBy(xpath = "//div[@class='mvc-accessibility']/div/div/div[6]/div/p/a")
	private WebElement islinkiUnderConvertingPDFs;

	public WebElement getLinkUnderConveringPdfText()
	{
		return islinkiUnderConvertingPDFs;
	}

	@FindBy(css = ".global-footer nav a:nth-child(3)")
	private WebElement isTextTermsOfUse;

	public WebElement getTermsofUseText()
	{
		return isTextTermsOfUse;
	}

	@FindBy(css = ".heading-five.panel-header")
	private WebElement isSeeHeaderTextTermsOfUse;

	public WebElement getTextHeaderTermsOfUseText()
	{
		return isSeeHeaderTextTermsOfUse;
	}

	/*public void waitForElementPresence()
	{
		waitForPresenceOf(isAccessibilityText);
		//waitFor(xpathOrCssSelector);
		//waitABit(10000);
	}*/
}
