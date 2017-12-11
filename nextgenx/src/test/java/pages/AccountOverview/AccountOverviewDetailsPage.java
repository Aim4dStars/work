package pages.AccountOverview;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import com.bt.nextgen.core.exception.ParseException;

//@DefaultUrl("secure/app/#ng/account/overview?c=FC622CC21BFFE81A42F70BB718A6A91D1D9470BB4FB2631D&p=975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0")
public class AccountOverviewDetailsPage extends PageObject
{

	public List <Map> arrayList = new ArrayList <Map>();

	@FindBy(xpath = "//div[@class='mvc-overview']/div/div/div/div[@class='heading-seven margin-bottom-1']/a")
	private WebElement RecentTransactionslink;

	@FindBy(how = How.XPATH, using = "//div[@data-component-name='recenttransactions']/table/tbody")
	private WebElement tableRecAccountRecentTrans;

	@FindBy(how = How.XPATH, using = "//div[@class='mvc-cashdeposits']/table/tbody")
	private WebElement tableRecAccountScheduleTrans;

	@FindBy(how = How.XPATH, using = "//div[@data-component-name='recenttransactions']/table/tbody")
	private WebElement tableRecAccountDate;

	@FindBy(xpath = "//div[@class='mvc-maturingtermdeposits']/div/table[@class='data-table-default table-grid-layout  not-fluid ']/tbody/tr/td[@class='push-left-1  column-5  ']/a")
	private List <WebElement> Numberofdaysleftlink;

	@FindBy(xpath = "//div[@class='mvc-recenttransactions']/div/table[@class='data-table-default table-grid-layout  not-fluid ']/tbody/tr/td[@class='push-left-1  column-6  '][1]")
	private List <WebElement> RecentDateformat;

	@FindBy(xpath = "//div[@class='mvc-maturingtermdeposits']/div/table[@class='data-table-default table-grid-layout  not-fluid ']/tbody/tr/td[@class='push-left-1  column-3  ']/img")
	private List <WebElement> icondisplay;

	@FindBy(xpath = "//div[@class='heading-seven margin-bottom-half']/a")
	private WebElement OrderInProgresslink;

	@FindBy(xpath = "//div[@class='mvc-ordersinprogress']/div/table/tbody/tr[@data-row-index='0']/td[@class='push-left-1  ']/span[@class='order-type']/a")
	private WebElement Buyslink;

	@FindBy(xpath = "//div[@class='mvc-ordersinprogress']/div/table/tbody/tr[@data-row-index='1']/td[@class='push-left-1  ']/span[@class='order-type']/a")
	private WebElement Sellslink;

	@FindBy(xpath = "//div[@class='message-box box-secondary']")
	private WebElement MessageSection;

	@FindBy(xpath = "//div[@class='heading-seven margin-bottom-2']/a")
	private WebElement Scheduledtransactionslink;

	@FindBy(xpath = "//div[@class='mvc-recenttransactions']/div/table/tbody/tr[5]/td[2]/span/span[@class='icon icon-refresh amber ']")
	private WebElement IconrecentTransaction;

	@FindBy(xpath = "//div[@class='mvc-cashdeposits']/div/table/tbody/tr/td[@class='push-left-1  column-6  ']")
	private List <WebElement> ScheduletransactionCashDate;

	@FindBy(xpath = "//div[@class='mvc-cashdeposits']/table/caption")
	private WebElement CashDepositheader;

	@FindBy(xpath = "//div[@class='mvc-cashdeposits']/div/table/tbody/tr/td[@class='push-left-1   align-right ']/span")
	private List <WebElement> Cashdeposits;

	@FindBy(xpath = "//div[@class='mvc-cashpayments']/div/table/tbody/tr/td[@class='push-left-1   align-right ']/span")
	private List <WebElement> CashPayments;

	@FindBy(xpath = "//div[@class='mvc-cashdeposits']/div/table/tbody/tr[2]/td")
	private WebElement Nocashtext;

	@FindBy(xpath = "//div[@class='mvc-cashpayments']/div/table/tbody/tr[2]/td")
	private WebElement NocashPayments;

	@FindBy(how = How.XPATH, using = "//div[@data-component-name='maturingtermdeposits']/div/table/tbody/tr")
	private WebElement Maturitytermtable;

	@FindBy(css = "div[class='heading-four orders-in-progress-heading'] a[class='text-link']")
	private WebElement isOrdersInProgressHeader;

	@FindBy(css = "tbody > tr:nth-child(1) > td:nth-child(2) > span > a")
	private WebElement isBuyApplicationHeader;

	@FindBy(css = "tbody > tr:nth-child(2) > td:nth-child(2) > span > a")
	private WebElement isSellsRedemptionHeader;

	@FindBy(css = "div.mvc-ordersinprogress > table > tbody > tr:nth-child(1) > td.align-right > span")
	private WebElement isBuyApplicationsAmount;

	@FindBy(css = "div.mvc-ordersinprogress > table > tbody > tr:nth-child(2) > td.align-right > span")
	private WebElement isSellApplicationsAmount;

	public AccountOverviewDetailsPage(WebDriver driver)
	{
		super(driver);
	}

	public WebElement getIsOrdersInProgressHeader()
	{

		return isOrdersInProgressHeader;

	}

	public WebElement getIsBuyApplicationHeader()
	{

		return isBuyApplicationHeader;

	}

	public WebElement getIsSellsRedemptionHeader()
	{

		return isSellsRedemptionHeader;
	}

	public WebElement getIsBuyApplicationsAmount()
	{

		return isBuyApplicationsAmount;
	}

	public WebElement getIsSellApplicationsAmount()
	{

		return isSellApplicationsAmount;
	}

	public WebElement getScheduledtransactionslink()
	{
		return Scheduledtransactionslink;
	}

	public WebElement getMessageSection()
	{
		return MessageSection;
	}

	public WebElement getOrdersInProgresslink()
	{
		return OrderInProgresslink;
	}

	public WebElement getBuyslink()
	{
		return Buyslink;
	}

	public WebElement getSellslink()
	{
		return Sellslink;
	}

	public WebElement getRecentTransactions()
	{
		return RecentTransactionslink;
	}

	public WebElement getTableRecAccountRecentTrans()
	{
		return tableRecAccountRecentTrans;
	}

	public WebElement getTableRecAccountDate()
	{
		return tableRecAccountDate;
	}

	public WebElement getTableRecAccountScheduleTrans()
	{
		return tableRecAccountScheduleTrans;
	}

	public List <WebElement> getdayslink()
	{
		return Numberofdaysleftlink;
	}

	public List <WebElement> getTableRecAccountformat()

	{
		return RecentDateformat;
	}

	public List <WebElement> geticondisplay()
	{
		return icondisplay;
	}

	public List <WebElement> getTableRecordsRecentCash()
	{
		WebElement table = this.tableRecAccountRecentTrans;

		return table.findElements(By.xpath("//div[@data-component-name='recenttransactions']/table/tbody/tr"));
	}

	public List <WebElement> getMaturitytermtable()
	{
		WebElement table = this.Maturitytermtable;
		return table.findElements(By.xpath("//div[@data-component-name='maturingtermdeposits']/div/table/tbody/tr"));

	}

	public List <WebElement> getScheduletransactionCashDate()
	{
		return ScheduletransactionCashDate;
	}

	public WebElement getIconrecentTransaction()
	{
		return IconrecentTransaction;
	}

	public WebElement getCashDepositheader()
	{
		return CashDepositheader;
	}

	public List <WebElement> getCashdeposits()
	{
		return Cashdeposits;
	}

	public List <WebElement> getCashPayments()
	{
		return CashPayments;
	}

	public WebElement getnocashtext()
	{
		return Nocashtext;
	}

	public WebElement getNocashPayments()
	{
		return NocashPayments;
	}

	public List <Map> tablecount() throws Throwable

	{

		WebElement table = this.tableRecAccountRecentTrans;
		java.util.List <WebElement> tr_collection = table.findElements(By.xpath("//div[@data-component-name='recenttransactions']/table/tbody/tr"));

		Thread.sleep(5000);
		int row_num, col_num;
		row_num = 0;

		for (WebElement trElement : tr_collection)
		{
			java.util.List <WebElement> td_collection = trElement.findElements(By.xpath("td"));

			col_num = 0;
			Map <String, String> actmap = new HashMap <String, String>();
			for (WebElement tdElement : td_collection)
			{

				switch (col_num)
				{
					case 0:

						actmap.put("Date", tdElement.getText());
						break;

					case 1:
						actmap.put("Icon", tdElement.getText());
						break;

					case 2:
						actmap.put("Description", tdElement.getText());
						break;

					case 3:
						actmap.put("cash", tdElement.getText());
						break;

				}

				col_num++;
			}

			arrayList.add(actmap);
			row_num++;

		}

		return arrayList;

	}

	public List <Map> tablecount1() throws Throwable

	{

		WebElement table = this.tableRecAccountScheduleTrans;
		java.util.List <WebElement> tr_collection = table.findElements(By.xpath("//div[@class='mvc-cashdeposits']/table/tbody/tr"));

		Thread.sleep(5000);
		int row_num, col_num;
		row_num = 0;

		for (WebElement trElement : tr_collection)
		{
			java.util.List <WebElement> td_collection = trElement.findElements(By.xpath("td"));

			col_num = 0;
			Map <String, String> actmap = new HashMap <String, String>();

			for (WebElement tdElement : td_collection)
			{

				switch (col_num)
				{
					case 0:

						actmap.put("Date", tdElement.getText());
						break;

					case 1:
						actmap.put("Icon", tdElement.getText());
						break;

					case 2:
						actmap.put("Description", tdElement.getText());
						break;

					case 3:
						actmap.put("cash", tdElement.getText());
						break;

				}

				col_num++;

			}

			arrayList.add(actmap);

			row_num++;

		}

		return arrayList;

	}

	public List <Map> tableMaturitycount() throws Throwable

	{

		WebElement table = this.Maturitytermtable;
		java.util.List <WebElement> tr_collection = table.findElements(By.xpath("//div[@data-component-name='maturingtermdeposits']/div/table/tbody/tr"));

		Thread.sleep(5000);
		int row_num, col_num;
		row_num = 0;

		for (WebElement trElement : tr_collection)
		{
			java.util.List <WebElement> td_collection = trElement.findElements(By.xpath("td"));

			col_num = 0;
			Map <String, String> actmap = new HashMap <String, String>();
			for (WebElement tdElement : td_collection)
			{

				switch (col_num)
				{
					case 0:

						actmap.put("Icon", tdElement.getText());
						break;

					case 1:
						actmap.put("Days Left", tdElement.getText());
						break;

					case 2:
						actmap.put("Date", tdElement.getText());
						break;

					case 3:
						actmap.put("Cash", tdElement.getText());
						break;

				}

				col_num++;
			}

			arrayList.add(actmap);

			row_num++;

		}

		return arrayList;

	}

	public void sortBy() throws Throwable

	{

		WebElement table = this.Maturitytermtable;
		java.util.List <WebElement> tr_collection = table.findElements(By.xpath("//div[@data-component-name='maturingtermdeposits']/div/table/tbody/tr/td[@class='push-left-1  column-7  ']"));
		ArrayList <String> storelist = new ArrayList <String>();
		ArrayList <String> storelist1 = new ArrayList <String>();
		for (int i = 0; i < tr_collection.size(); i++)
		{
			String d = tr_collection.get(i).getText();
			storelist.add(d);

		}
		storelist1.addAll(storelist);

		Collections.sort(storelist, new Comparator <String>()
		{

			@Override
			public int compare(String o1, String o2)
			{
				SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
				try
				{
					return format.parse(o1).compareTo(format.parse(o2));
				}
				catch (ParseException | java.text.ParseException e)
				{
					e.printStackTrace();
					return 0;
				}
			}

		});

		for (int i = 0; i < storelist.size(); i++)
		{

			assertEquals(storelist1.get(i), storelist.get(i));

		}

	}
}
