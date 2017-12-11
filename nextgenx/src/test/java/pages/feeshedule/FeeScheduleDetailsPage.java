package pages.feeshedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

//@DefaultUrl("/secure/app/#ng/fees?r=adviser&c=AA8614A18D2DF1E2AAAE1E96C3297644BC2C34B6E1E01804&p=C19C51E3452002F0DC6749AF7DEDEAAD4719006302A377BF")
public class FeeScheduleDetailsPage extends PageObject
{

	public List <Map> arrayList = new ArrayList <Map>();
	@CacheLookup
	@FindBy(css = "span[class='label-content']")
	private WebElement isEditFees;

	@CacheLookup
	@FindBy(css = "a[class='link-item'][title='Contact us']")
	private WebElement isContactUs;

	@FindBy(xpath = "//div[@class='mvc-schedule']/div[4]/hgroup/h2[@class='heading-four']")
	private WebElement Administrationfeesheader;

	@FindBy(xpath = "//div[@class='mvc-schedule']/div[4]/div[@class='columns-13']")
	private WebElement Administrationfee;

	@FindBy(xpath = "//div[@class='mvc-schedule']/div[4]/div[3]/h3[1]")
	private WebElement Dollaramountheader;

	@FindBy(xpath = "//div[@class='mvc-schedule']/div[4]/div[3]/h4[1]")
	private WebElement Dollaramount;

	@FindBy(xpath = "//div[@class='mvc-schedule']/div[4]/div[3]/h3[2]")
	private WebElement slidingscalecomponentheader;

	@FindBy(xpath = "//div[@class='mvc-schedule']/div[4]/div[3]/div[2]")
	private WebElement Minimumfee;

	@FindBy(xpath = "//div[@class='mvc-schedule']/div[4]/div[3]/div[3]")
	private WebElement Maximumfee;

	@FindBy(how = How.XPATH, using = "//div[@class='mvc-administrationfeeslidingscalefee']/table[@class='data-table-default']/tbody/tr")
	private WebElement SlidingScaletable;

	@FindBy(how = How.XPATH, using = "//div[@class='mvc-ongoingadvicefeeslidingscalefee']/table/tbody/tr")
	private WebElement OngoingSlidingtiers;

	@FindBy(how = How.XPATH, using = "//div[@class='mvc-managementfee']/table/tbody/tr")
	private WebElement SMAfeetable;

	@FindBy(xpath = "//div[@class='mvc-fees']/div/div/div[@class='grid margin-bottom-0']/div/hgroup/h2")
	private WebElement Advicefeetext;

	@FindBy(xpath = "//div[@class='mvc-fees']/div/div/div[2]/div[@class='columns-13']/h3")
	private WebElement Ongoingfeetext;

	@FindBy(xpath = "//div[@class='mvc-fees']/div/div/div[2]/div[@class='columns-28']/h3[1]")
	private WebElement Dollarfeeongoingtext;

	@FindBy(xpath = "//div[@class='mvc-fees']/div/div/div[2]/div[@class='columns-28']/h4[1]")
	private WebElement Dollarfeeongoing;

	@FindBy(xpath = "//div[@class='mvc-fees']/div/div/div[2]/div[@class='columns-28']/h3[2]")
	private WebElement Slidingongoingtext;

	@FindBy(xpath = "//div[@class='mvc-fees']/div/div/div[3]/div[@class='columns-13']/h3[1]")
	private WebElement Licenseefeetext;

	@FindBy(xpath = "//div[@class='mvc-fees']/div/div/div[3]/div[@class='columns-28']/h3[1]")
	private WebElement Dollarfeelicenseetext;

	@FindBy(xpath = "//div[@class='mvc-fees']/div/div/div[3]/div[@class='columns-28']/h4[1]")
	private WebElement Dollarfeelicensee;

	@FindBy(xpath = "//div[@class='mvc-fees']/div/div/div[3]/div[@class='columns-28']/h4[1]/span")
	private WebElement dollarfeelicenseetext;

	@FindBy(xpath = "//div[@class='mvc-fees']/div/div/div[3]/div[@class='columns-28']/h3[2]")
	private WebElement Percentagefeelicenseetext;

	@FindBy(xpath = "//div[@class='mvc-licenseeadvicefeepercentagefee']/table/tbody/tr")
	private WebElement LicenseePercentagefee;

	@FindBy(xpath = "//div[@class='mvc-schedule']/div[6]/hgroup/h2")
	private WebElement Investmentfeeheader;

	@FindBy(xpath = "//div[@class='mvc-schedule']/div[7]/div/h3")
	private WebElement ManagedPortfolios;

	public FeeScheduleDetailsPage(WebDriver driver)
	{
		super(driver);
	}

	public WebElement getInvestmentfeeheader()
	{
		return Investmentfeeheader;
	}

	public WebElement getManagedPortfolios()
	{
		return ManagedPortfolios;
	}

	public WebElement getIsEditFees()
	{
		return isEditFees;
	}

	public WebElement getAdministrationfeesheader()
	{
		return Administrationfeesheader;
	}

	public WebElement getAdministrationfeeheader()
	{
		return Administrationfee;
	}

	public WebElement getDollaramountheader()
	{
		return Dollaramountheader;
	}

	public WebElement getDollaramount()
	{
		return Dollaramount;
	}

	public WebElement getslidingscalecomponentheader()
	{
		return slidingscalecomponentheader;
	}

	public WebElement getMinimumfee()
	{
		return Minimumfee;
	}

	public WebElement getMaximumfee()
	{
		return Maximumfee;
	}

	public WebElement getAdvicefeetext()
	{
		return Advicefeetext;
	}

	public WebElement getOngoingfeetext()
	{
		return Ongoingfeetext;
	}

	public WebElement getDollarfeeongoingtext()
	{
		return Dollarfeeongoingtext;
	}

	public WebElement getDollarfeeongoing()
	{
		return Dollarfeeongoing;
	}

	public WebElement getSlidingongoingtext()
	{
		return Slidingongoingtext;
	}

	public WebElement getLicenseefeetext()
	{
		return Licenseefeetext;
	}

	public WebElement getDollarfeelicenseetext()
	{
		return Dollarfeelicenseetext;
	}

	public WebElement getDollarfeelicensee()
	{
		return Dollarfeelicensee;
	}

	public WebElement getdollarfeelicenseetext()
	{
		return dollarfeelicenseetext;
	}

	public WebElement getPercentagefeelicenseetext()
	{
		return Percentagefeelicenseetext;
	}

	//public WebElement getDollaramountOngoing()
	//{
	//return DollaramountOngoing;
	//}

	public List <WebElement> getongoingSlidingtiers()

	{
		WebElement table = this.OngoingSlidingtiers;
		return table.findElements(By.xpath("//div[@class='mvc-ongoingadvicefeeslidingscalefee']/table/tbody/tr"));
	}

	public List <WebElement> SlidingScaletable()
	{
		WebElement table = this.SlidingScaletable;
		return table.findElements(By.xpath("//div[@class='mvc-administrationfeeslidingscalefee']/table[@class='data-table-default']/tbody/tr"));

	}

	public List <WebElement> getSMAfeetable()
	{
		WebElement table = this.SMAfeetable;
		return table.findElements(By.xpath("//div[@class='mvc-managementfee']/table/tbody/tr"));
	}

	public List <WebElement> getLicenseePercentagefee()
	{
		WebElement table = this.LicenseePercentagefee;
		return table.findElements(By.xpath("//div[@class='mvc-licenseeadvicefeepercentagefee']/table/tbody/tr"));
	}

	public List <Map> testCountslidingtiers() throws Throwable

	{

		WebElement table = this.SlidingScaletable;
		java.util.List <WebElement> tr_collection = table.findElements(By.xpath("//div[@class='mvc-administrationfeeslidingscalefee']/table[@class='data-table-default']/tbody/tr"));

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

						actmap.put("Tiers", tdElement.getText());
						break;

					case 1:
						actmap.put("Pa", tdElement.getText());
						break;

				}

				col_num++;
			}

			arrayList.add(actmap);

			row_num++;

		}

		return arrayList;

	}

	public List <Map> testCountSlidingongoingtiers() throws Throwable
	{
		WebElement table = this.OngoingSlidingtiers;
		java.util.List <WebElement> tr_collection = table.findElements(By.xpath("//div[@class='mvc-ongoingadvicefeeslidingscalefee']/table/tbody/tr"));

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

						actmap.put("Tiers", tdElement.getText());
						break;

					case 1:
						actmap.put("Pa", tdElement.getText());
						break;

				}

				col_num++;
			}

			arrayList.add(actmap);

			row_num++;

		}

		return arrayList;
	}

	public List <Map> testCountlicenseepercentage() throws Throwable

	{

		WebElement table = this.LicenseePercentagefee;
		java.util.List <WebElement> tr_collection = table.findElements(By.xpath("//div[@class='mvc-licenseeadvicefeepercentagefee']/table/tbody/tr"));

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

						actmap.put("Tiers", tdElement.getText());
						break;

					case 1:
						actmap.put("Pa", tdElement.getText());
						break;

				}

				col_num++;
			}

			arrayList.add(actmap);

			row_num++;

		}

		return arrayList;
	}

	public List <Map> testCountSMAfee() throws Throwable
	{

		WebElement table = this.SMAfeetable;
		java.util.List <WebElement> tr_collection = table.findElements(By.xpath("//div[@class='mvc-managementfee']/table/tbody/tr"));

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

						actmap.put("Code", tdElement.getText());
						break;

					case 1:
						actmap.put("Investment name", tdElement.getText());
						break;

					case 2:
						actmap.put("Pa", tdElement.getText());
						break;

				}

				col_num++;
			}

			arrayList.add(actmap);

			row_num++;

		}

		return arrayList;

	}
}
