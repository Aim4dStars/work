package pages.BTTransactions;

import java.util.List;

import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class BTScheduledTransactionsPage extends PageObject
{
	public BTScheduledTransactionsPage(WebDriver driver)
	{
		super(driver);
	}

	//verifying scheduled transaction
	@FindBy(css = ".heading-five.panel-header")
	private WebElement isScheduledTransactionHeaderTitle;

	public WebElement getScheduledTransactionHeaderTitle()
	{
		return isScheduledTransactionHeaderTitle;
	}

	@FindBy(css = ".view-messagealert div .message strong p")
	private WebElement isScheduledTransactionHeaderMessage;

	public WebElement getScheduledTransactionHeaderMessage()
	{
		return isScheduledTransactionHeaderMessage;
	}

	@FindBy(css = ".mvc-scheduledtransaction div:nth-child(3) table thead tr th:nth-child(1)")
	private WebElement isTableColumnNextdue;

	public WebElement getTableColumnNextDue()
	{
		return isTableColumnNextdue;
	}

	@FindBy(css = ".mvc-scheduledtransaction div:nth-child(3) table thead tr th:nth-child(3)")
	private WebElement isTableDescriptiondue;

	public WebElement getTableDescriptiondue()
	{
		return isTableDescriptiondue;
	}

	@FindBy(css = ".mvc-scheduledtransaction div:nth-child(3) table thead tr th:nth-child(4)")
	private WebElement isTableColumnRepeat;

	public WebElement getTableColumnRepeat()
	{
		return isTableColumnRepeat;
	}

	@FindBy(css = ".mvc-scheduledtransaction div:nth-child(3) table thead tr th:nth-child(5)")
	private WebElement isTableColumnCredit;

	public WebElement getTableColumnCredit()
	{
		return isTableColumnCredit;
	}

	@FindBy(css = ".mvc-scheduledtransaction div:nth-child(3) table thead tr th:nth-child(6)")
	private WebElement isTableColumnDebit;

	public WebElement getTableColumnDebit()
	{
		return isTableColumnDebit;
	}

	//Verifying the result show format
	@FindBy(css = ".filter-pagination-label")
	private WebElement isShowingResultcount;

	public WebElement getShowingResultcount()
	{
		return isShowingResultcount;
	}

	//verfying see more link
	@FindBy(css = ".btn-more-results span:nth-child(2)")
	private WebElement isSeeMoreLink;

	public WebElement getSeeMoreLink()
	{
		return isSeeMoreLink;
	}

	//Click on see more link
	@FindBy(css = ".btn-more-results span:nth-child(2)")
	private WebElement isShowMoreLinkClickable;

	public WebElement getShowMoreLinkClickable()
	{
		return isShowMoreLinkClickable;
	}

	//Expanded the first record
	@FindBy(css = ".mvc-scheduledtransaction div:nth-child(3) table tbody tr:nth-child(1) td:nth-child(1) a")
	private WebElement isExpandingTheFirstRecord;

	public WebElement getFirstRowExpanded()
	{
		return isExpandingTheFirstRecord;
	}

	// Verifying wether the correct page expanded
	@FindBy(css = ".mvc-termdeposits table tbody .expanded-row-data.hidden .expandable-table-container.hidden .grid .columns-30 h1")
	private WebElement checkingCorrectPage;

	public WebElement checkingCorrectPageExpanded()
	{
		return checkingCorrectPage;
	}

	@FindBy(css = ".mvc-scheduledtransaction div:nth-child(3) table tbody tr:nth-child(1) td:nth-child(1) a")
	private WebElement checkingWhenCollapsed;

	public WebElement checkingDataWhenCollapsed()
	{
		return checkingCorrectPage;
	}

	//Checking the text From and To in expanded state of Scheduled Transaction record
	@FindBy(css = ".mvc-scheduledtransaction div:nth-child(3) table tbody tr:nth-child(2) td div .def-list-style-1 dl dt")
	private List <WebElement> subTitle;

	public List <WebElement> checkingSubTitleOfRecordAfterExpansionListOne()
	{
		return subTitle;
	}

	//Checking the text in expanded state of Scheduled Transaction record
	@FindBy(css = ".mvc-scheduledtransaction div:nth-child(3) table tbody tr:nth-child(2) td div .def-list-style-1 dl dt")
	private List <WebElement> subTitle1;

	public List <WebElement> checkingSubTitleOfRecordAfterExpansionListTwo()
	{
		return subTitle1;
	}

	//Verifying text deposit of schedule
	@FindBy(xpath = ".mvc-termdeposits table tbody .expanded-row-data.hidden .expandable-table-container.hidden .grid .columns-30 h1")
	private WebElement isDepositOfScheduleFormat;

	public WebElement getDepositOfScheduleFormat()
	{
		return isDepositOfScheduleFormat;
	}

	//Verifying button Stop Schedule
	@FindBy(xpath = "//div[@class='mvc-scheduledtransaction']/div[3]/table/tbody/tr[2]/td/div/div/div[2]/div/button")
	private WebElement isStopScheduleButton;

	public WebElement getStopScheduleButton()
	{
		return isStopScheduleButton;
	}

	//Verify button Download
	@FindBy(xpath = "//div[@class='mvc-scheduledtransaction']/table/tbody/tr[2]/td/div/div/div[4]/div[2]/button")
	private WebElement isDownloadButton;

	public WebElement getDownloadButton()
	{
		return isDownloadButton;
	}

	//Expanded the Second failed record
	@FindBy(xpath = "//div[@class='mvc-termdeposits']/table/tbody/tr[3]/td/a/span[2]")
	private WebElement isExpandingTheSecondRecord;

	public WebElement getSecondRecordExpanded()
	{
		return isExpandingTheSecondRecord;
	}

	//Verifying failed Transaction header
	@FindBy(xpath = "//div[@class='mvc-termdeposits']/table/tbody/tr[4]/td/div/div/div/div[2]/h1")
	private WebElement isFailedTransactionHeader;

	public WebElement getFailedTransactionHeader()
	{
		return isFailedTransactionHeader;
	}

	//Verifying failed Transaction header
	@FindBy(xpath = "//div[@class='mvc-termdeposits']/table/tbody/tr[4]/td/div/div/div/div[2]/div/div/span[2]/p")
	private WebElement isFailedTransactionMessage;

	public WebElement getFailedTransactionMessage()
	{
		return isFailedTransactionMessage;
	}

	//Validating date format
	@FindBy(xpath = "//div[@class='mvc-termdeposits']/table/tbody/tr[2]/td/div/div/div[3]/div[2]/p")
	private WebElement isDateText;

	public WebElement getDateText()
	{
		return isDateText;
	}

	@FindBy(css = ".modal-header .icon-close")
	private WebElement isCrossIconOnStopSchedule;

	public WebElement getCrossIconOnStopSchedule()
	{
		return isCrossIconOnStopSchedule;
	}

	@FindBy(css = ".modal-body div div:nth-child(1) h1 span")
	private WebElement isStopScheduleModalHeader;

	public WebElement getStopScheduleModalHeader()
	{
		return isStopScheduleModalHeader;
	}

	@FindBy(css = ".modal-body div div:nth-child(2) p")
	private WebElement isStopScheduleMessage;

	public WebElement getStopScheduleMessage()
	{
		return isStopScheduleMessage;
	}

	@FindBy(css = ".modal-body div div:nth-child(3) .view-stopscheduleyes button")
	private WebElement isYesButton;

	public WebElement getYesButton()
	{
		return isYesButton;
	}

	@FindBy(css = ".modal-body div div:nth-child(3) .view-stopscheduleno button")
	private WebElement isNoButton;

	public WebElement getNoButton()
	{
		return isNoButton;
	}

}
