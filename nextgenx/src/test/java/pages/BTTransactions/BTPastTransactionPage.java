package pages.BTTransactions;

//import static org.junit.Assert.*;
import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class BTPastTransactionPage extends PageObject
{

	public BTPastTransactionPage(WebDriver driver)
	{
		super(driver);
	}

	@FindBy(xpath = "//div[@class='mvc-headerpanel']/div/h1")
	private WebElement isHeaderPastTransaction;

	//Verifying page header
	public WebElement getHeaderPastTransaction()
	{
		return isHeaderPastTransaction;
	}

	@FindBy(xpath = "//div[@class='sub-navigation']/a")
	private WebElement isTabAllTransaction;

	public WebElement getTabAllTransaction()
	{
		return isTabAllTransaction;
	}

	@FindBy(xpath = "//div[@class='sub-navigation']/a[2]")
	private WebElement isTabBTCashTransactions;

	public WebElement getTabBTCashTransactions()
	{
		return isTabBTCashTransactions;
	}

	@FindBy(css = "div[class='date-select-element']")
	private WebElement isDateSearchElement;

	public WebElement getDateSearchElement()
	{
		return isDateSearchElement;
	}

	@FindBy(xpath = "//div[@class='sub-navigation']/a[1]")
	private WebElement isDateFrom;

	public WebElement getDateFrom()
	{
		return isDateFrom;
	}

	@FindBy(xpath = "//div[@class='sub-navigation']/a[2]")
	private WebElement isDateTo;

	public WebElement getDateTo()
	{
		return isDateTo;
	}

	@FindBy(css = "div[class='view-timeupdated']")
	private WebElement isTimeUpdates;

	public WebElement getTimeUpdates()
	{
		return isTimeUpdates;
	}

	@FindBy(xpath = "//div[@class='expandable-table-container parent-table']/table/thead/tr/td/span")
	private WebElement isShowingResultcount;

	public WebElement getShowingResultcount()
	{
		return isShowingResultcount;
	}

	@FindBy(xpath = "//div[@class='margin-bottom-2 mvc-cashtransactions']/table/thead/tr/th[1]")
	private WebElement isTableHeaderDate;

	public WebElement getTableHeaderDate()
	{
		return isTableHeaderDate;
	}

	@FindBy(xpath = "//div[@class='margin-bottom-2 mvc-cashtransactions']/table/thead/tr/th[2]")
	private WebElement isTableHeaderDescription;

	public WebElement getTableHeaderDescription()
	{
		return isTableHeaderDescription;
	}

	@FindBy(xpath = "//div[@class='margin-bottom-2 mvc-cashtransactions']/table/thead/tr/th[3]")
	private WebElement isTableHeaderCredit;

	public WebElement getTableHeaderCredit()
	{
		return isTableHeaderCredit;
	}

	@FindBy(xpath = "//div[@class='margin-bottom-2 mvc-cashtransactions']/table/thead/tr/th[4]")
	private WebElement isTableHeaderDebit;

	public WebElement getTableHeaderDebit()
	{
		return isTableHeaderDebit;
	}

	@FindBy(xpath = "//div[@class='margin-bottom-2 mvc-cashtransactions']/table/thead/tr/th[5]")
	private WebElement isTableHeaderBalance;

	public WebElement getTableHeaderBalance()
	{
		return isTableHeaderBalance;
	}

	@FindBy(xpath = "//div[@class='columns-41']/p/strong")
	private WebElement isTextDisclaimer;

	public WebElement getTextDisclaimer()
	{
		return isTextDisclaimer;
	}

	//@FindBy(xpath = "//div[@class='columns-41']/div/table/tbody/tr")
	//@FindBy(xpath = "//div[@class='columns-41']/div/table/tbody/tr/td/a/span[2]")
	private WebElement expandRecord;

	public WebElement getExpandRecord()
	{
		return expandRecord;
	}

	@FindBy(xpath = "//div[@class='margin-bottom-2 mvc-cashtransactions']/table/tbody/tr[4]/td/div/div/h1/span")
	private WebElement isTextPaymentOf;

	public WebElement getTextPaymentOf()
	{
		return isTextPaymentOf;
	}

	@FindBy(xpath = "//div[@class='margin-bottom-2 mvc-cashtransactions']/table/tbody/tr[4]/td/div/div/h1/span")
	private WebElement isTextDepositOf;

	public WebElement getTextDepositOf()
	{
		return isTextDepositOf;
	}

	@FindBy(xpath = "//div[@class='margin-bottom-2 mvc-cashtransactions']/table/tbody/tr[4]/td/div/div/div/div/div/div/strong")
	private WebElement isTextFrom;

	public WebElement getExpandFromText()
	{
		return isTextFrom;
	}

	@FindBy(xpath = "//div[@class='margin-bottom-2 mvc-cashtransactions']/table/tbody/tr[4]/td/div/div/div/div/div[2]/div/strong")
	private WebElement isTextTo;

	public WebElement getExpandToText()
	{
		return isTextTo;
	}

	@FindBy(xpath = "//div[@class='margin-bottom-2 mvc-cashtransactions']/table/tbody/tr[4]/td/div/div/div/div/div[3]/div/strong")
	private WebElement isTextDateExpanded;

	public WebElement getExpandDateText()
	{
		return isTextDateExpanded;
	}

	@FindBy(xpath = "//div[@class='margin-bottom-2 mvc-cashtransactions']/table/tbody/tr[4]/td/div/div/div/div/div[5]/div/strong")
	private WebElement isTextReciptNo;

	public WebElement getExpandReciptNoText()
	{
		return isTextReciptNo;
	}

	@FindBy(xpath = "//div[@class='view-secaction_5']/button/span/span")
	private WebElement isButtonDownload;

	public WebElement getButtonDownload()
	{
		return isButtonDownload;
	}

	//@FindBy(xpath = "//div[@class='columns-41']/div/table/tbody/tr")
	@FindBy(xpath = "//div[@class='columns-41']/div/table/tbody/tr/td/a/span[2]")
	private WebElement clicktocollpaserecord;

	public WebElement getclicktocollpaserecord()
	{
		return clicktocollpaserecord;
	}

	@FindBy(xpath = "//div[@class='columns-10']/p")
	private WebElement isClosedCollapse;;

	public WebElement getClosedCollapse()
	{
		return isClosedCollapse;
	}

}
