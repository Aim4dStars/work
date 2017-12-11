package pages.fees;

import static org.junit.Assert.*;
import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

//@DefaultUrl("/secure/app/#ng/feerevenuestatement?c=3EBF40671FE2F335D4C7CDE112402296A9393DE0FB941442&a=55A518AF8B95484038D39B5D776C45EA0CD8471F4C3838A0")
public class FeeRevenueStmtPage extends PageObject
{

	@FindBy(xpath = "//input[@name='startdate']")
	private WebElement isStartDate;

	@FindBy(xpath = "//input[@name='enddate']")
	private WebElement isEnddate;

	@FindBy(xpath = "//button[@type='submit']")
	private WebElement isSearch;

	@FindBy(xpath = "/html/body/div/div/div/div[2]/article/div/div[2]/div/div/div[3]/div/table")
	private WebElement isRecords;

	@FindBy(className = "error-min-value")
	private WebElement isLast7Days;

	@FindBy(className = "error-regexp")
	private WebElement isLast30Days;

	@FindBy(className = "error-regexp")
	private WebElement isCurrentQ;

	@FindBy(className = "error-required")
	private WebElement isPreviousQ;

	@FindBy(xpath = "//form/div/div")
	private WebElement isThisFinYr;

	@FindBy(xpath = "//p")
	private WebElement isLastFinYr;

	@FindBy(xpath = "//div[@class='columns-30']")
	private WebElement isSpecDate;

	@FindBy(xpath = "//div[@class='columns-30']")
	private WebElement isDateRange;

	@FindBy(how = How.XPATH, using = "//td/div/div/span[2]/p")
	private WebElement isMessage;

	@FindBy(how = How.CSS, using = "#table#")
	private WebElement isRecord;

	@FindBy(how = How.CLASS_NAME, using = "data-table-default")
	private WebElement tablerecords;

	@FindBy(how = How.CLASS_NAME, using = "data-table-default")
	private WebElement tablerows;

	public FeeRevenueStmtPage(WebDriver driver)
	{
		super(driver);
	}

	public WebElement getIsStartDate()
	{
		return isStartDate;
	}

	public WebElement getIsEndDate()
	{
		return isEnddate;
	}

	public WebElement getSearch()
	{
		return isSearch;
	}

	public WebElement getRecords()
	{
		return isRecords;
	}

	public WebElement getLast7()
	{
		return isLast7Days;
	}

	public WebElement getLast30()
	{
		return isLast30Days;
	}

	public WebElement getCurrentQ()
	{
		return isCurrentQ;
	}

	public WebElement getPreviousQ()
	{
		return isPreviousQ;
	}

	public WebElement getThisFnYr()
	{
		return isThisFinYr;
	}

	public WebElement getPrevFinYr()
	{
		return isLastFinYr;
	}

	public WebElement getSpecDate()
	{
		return isSpecDate;
	}

	public WebElement getDateRange()
	{
		return isDateRange;
	}

	public WebElement getMessage()
	{
		return isMessage;
	}

	public void tableCont() throws Throwable
	{

		WebElement table = this.tablerecords;
		java.util.List <WebElement> tr_collection = table.findElements(By.xpath("/html/body/div/div/div/div[2]/article/div/div[2]/div/div/div[5]/div/table/tbody/tr"));

		if (tr_collection.size() < 3)
		{

			assertEquals("Records", "But no records displayed");
		}
		Thread.sleep(5000);
		int row_num, col_num;
		row_num = 1;
		for (WebElement trElement : tr_collection)
		{
			java.util.List <WebElement> td_collection = trElement.findElements(By.xpath("td"));

			col_num = 1;
			for (WebElement tdElement : td_collection)
			{

				col_num++;
			}
			row_num++;

		}

	}

	public void notableCont() throws Throwable
	{
		assertEquals("No statements available for this period",
			this.find(By.cssSelector("td > div.view-messagealert > div.response-message.helpful-information  > span.message > p"))
				.getText());

	}

}
