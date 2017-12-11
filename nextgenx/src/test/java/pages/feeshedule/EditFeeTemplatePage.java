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

//@DefaultUrl("/secure/app/#ng/fees/periodicfees/editfees")
public class EditFeeTemplatePage extends PageObject
{
	public List <Map> arrayList = new ArrayList <Map>();

	@CacheLookup
	@FindBy(xpath = "//div[@class='mvc-headerpanel']/div/h1")
	private WebElement Editfeetext;

	@CacheLookup
	@FindBy(xpath = "//input[@name='ongoingpercentage0']")
	private WebElement PercentageSliding;

	@FindBy(xpath = "//input[@name='managedportfolios']")
	private WebElement Percentagefee;

	@CacheLookup
	@FindBy(xpath = "//button[@class=' btn- btn-action-primary']")
	private WebElement FeeNextbutton;

	@CacheLookup
	@FindBy(xpath = "//div[@class='grid margin-bottom-0']/div/table/tbody/tr[1]/td[2]//div/div/div/div[2]")
	private WebElement MaxErrorforSliding;

	@CacheLookup
	@FindBy(xpath = "//div[@class='grid margin-bottom-0']/div/div[@class='view-forminputtext_2']/div/div[2]/span[@class='error-custom-format']")
	private WebElement NonnumericErrorforSliding;

	@CacheLookup
	@FindBy(xpath = "//div[@class='grid margin-bottom-0']/div/div[@class='view-forminputtext_2']/div/div[2]/span[2]")
	private WebElement NegativeErrorforSliding;

	@CacheLookup
	@FindBy(xpath = "//input[@name='ongoingupperBound0']")
	private WebElement DollarSliding;

	@CacheLookup
	@FindBy(xpath = "//div[@class='input-wrap view-forminputtext']/div/div[2]/span[2]")
	private WebElement MinreqErrorforSliding;

	@CacheLookup
	@FindBy(xpath = "//input[@name='dollar']")
	private WebElement Dollarfee;

	@CacheLookup
	@FindBy(xpath = "//div[@class='mvc-ongoing']/section/div[2]/div/nav/span/button[@title='Dollar']")
	private WebElement ongoingdollarfeebutton;

	@CacheLookup
	@FindBy(xpath = "//div[@class='mvc-ongoing']/section/div[2]/div/nav/span[2]/button[@title='Percentage']")
	private WebElement ongoingpercentagefeebutton;

	@CacheLookup
	@FindBy(xpath = "//div[@class='mvc-ongoing']/section/div[2]/div/nav/span[3]/button[@title='Sliding scale']")
	private WebElement ongoingslidingbutton;

	@CacheLookup
	@FindBy(xpath = "//div[@class='mvc-licensee']/section/div/div/nav/span/button[@title='Dollar']")
	private WebElement licenseedollarfeebutton;

	@CacheLookup
	@FindBy(xpath = "//div[@class='mvc-licensee']/section/div[2]/div/nav/span/button[@title='Percentage']")
	private WebElement licenseepercentagefeebutton;

	@CacheLookup
	@FindBy(xpath = "//div[@class='mvc-licensee']/section/div[2]/div/nav/span/button[@title='Sliding scale']")
	private WebElement licenseeslidingfeebutton;

	@CacheLookup
	@FindBy(css = "a[class='link-item'][title='Contact us']")
	private WebElement isContactUs;

	@FindBy(css = "div[class='view-upperbound0'] input[class='text-input ']")
	private WebElement isSlidingScaleFirstTier;

	@FindBy(css = "div[class='view-upperbound1'] input[class='text-input ']")
	private WebElement isSlidingScaleSecondTier;

	@FindBy(css = "div[class='view-percentage0'] input[class='text-input col-4']")
	private WebElement isPercentageValueSlidingScaleFirstTier;

	@FindBy(css = "div[class='view-percentage1'] input[class='text-input col-4']")
	private WebElement isPercentageValueSlidingScaleSecondTier;

	@FindBy(css = "label[class='inline fromVal1']")
	private WebElement isSlidingScaleSecondTierLimitValue;

	@FindBy(css = "span[class='view-next'] button[class=' btn- btn-action-primary'] span[class='label-content']")
	private WebElement isNextButton;

	@FindBy(css = "h1[class='heading-five panel-header']")
	private WebElement isConfirmationScreenSlidingScale;

	@FindBy(xpath = "//div[@class='mvc-ongoing']/section/div[@class='mvc-dollarfee']/div/div/div[@class='fieldset gutter-bottom-1']/fieldset/legend")
	private WebElement Dollarfeecomponent;

	@FindBy(xpath = "//div[@class='mvc-ongoing']/section/div[@class='mvc-slidingscalefee']/div/div/div[@class='fieldset gutter-bottom-1']/fieldset/legend")
	private WebElement SlidingScalecomponent;

	@FindBy(css = "div[class='mvc-ongoing'] div[class='mvc-dollarfee'] input[name='amount']")
	private WebElement DollarfeeamountOngoing;

	@FindBy(css = "div[class='view-inputcheckbox_2'] label[class='selected']")
	private WebElement Slidingtermcheckbox;

	@FindBy(css = "div[class='view-inputcheckbox_3'] label[class='selected']")
	private WebElement SlidingCashcheckbox;

	@FindBy(how = How.XPATH, using = "//div[@class='mvc-administrationfeeslidingscalefee']/table[@class='data-table-default']/tbody/tr")
	private WebElement SlidingScaletable;

	@FindBy(how = How.XPATH, using = "//div[@class='mvc-slidingscalefee']/div/div/div/fieldset/div[6]/div/table/tbody/tr")
	private WebElement SlidingScaletiersOngoing;

	@FindBy(css = "div[class='mvc-licensee'] div[class='dollar-form'] legend[class='heading-six']")
	private WebElement licenseeDollarfeecomponent;

	@FindBy(css = "div[class='mvc-licensee'] div[class='mvc-percentagefee'] legend[class='heading-six']")
	private WebElement licenseePercentagecomponent;

	@FindBy(css = "div[class='mvc-licensee'] div[class='mvc-dollarfee'] input[class='text-input ']")
	private WebElement DollarfeeamountLicensee;

	@FindBy(css = "div[class='mvc-licensee'] div[class='view-inputcheckbox'] label[class='selected']")
	private WebElement Indexcheckbox;

	@FindBy(how = How.XPATH, using = "//div[@class='mvc-percentagefee']/div/div/div/fieldset/div/div/table/tbody/tr")
	private WebElement PercentageLicenseetable;

	@FindBy(css = "div[class='columns-10 view-forminputtext_3'] div[class='validation-container'] div:nth-child(2) span:nth-child(1)")
	private WebElement MinerrorSliding;

	@FindBy(xpath = "//input[@name='ongoingupperBound0']")
	private WebElement LowertierSliding;

	@FindBy(xpath = "//input[@name='ongoingupperBound1']")
	private WebElement HighertierSliding;

	public EditFeeTemplatePage(WebDriver driver)
	{
		super(driver);
	}

	public WebElement getIndexcheckbox()
	{
		return Indexcheckbox;
	}

	public WebElement getMinerrorSliding()
	{
		return MinerrorSliding;
	}

	public WebElement getIsPercentagefee()
	{
		return Percentagefee;
	}

	public WebElement getDollarfeeamountLicensee()
	{
		return DollarfeeamountLicensee;
	}

	public WebElement getlicenseePercentagecomponent()
	{
		return licenseePercentagecomponent;
	}

	public WebElement getHighertierSliding()
	{
		return HighertierSliding;

	}

	public WebElement getLowertierSliding()
	{
		return LowertierSliding;

	}

	public WebElement getlicenseeDollarfeecomponent()
	{
		return licenseeDollarfeecomponent;
	}

	public WebElement getIsEditfeetext()
	{
		return Editfeetext;
	}

	public WebElement getSlidingtermcheckbox()
	{
		return SlidingCashcheckbox;
	}

	public WebElement getSlidingCashcheckbox()
	{
		return SlidingCashcheckbox;
	}

	public WebElement getDollarfeecomponent()
	{
		return Dollarfeecomponent;

	}

	public WebElement getDollarfeeamountOngoing()
	{
		return DollarfeeamountOngoing;
	}

	public WebElement getSlidingScalecomponent()
	{
		return SlidingScalecomponent;
	}

	public WebElement getIsongoingdollarfeebutton()
	{
		return ongoingdollarfeebutton;

	}

	public WebElement getIsongoingpercentagefeebutton()
	{
		return ongoingpercentagefeebutton;

	}

	public WebElement getIsongoingslidingbutton()
	{
		return ongoingslidingbutton;
	}

	public WebElement getIslicenseedollarfeebutton()
	{
		return licenseedollarfeebutton;
	}

	public WebElement getIslicenseepercentagefeebutton()
	{
		return licenseepercentagefeebutton;
	}

	public WebElement getIslicenseeslidingfeebutton()
	{
		return licenseeslidingfeebutton;
	}

	public WebElement getIsPercentageSliding()
	{
		return PercentageSliding;
	}

	public WebElement getIsDollarSliding()
	{
		return DollarSliding;
	}

	public WebElement getIsDollarfee()
	{
		return Dollarfee;
	}

	public WebElement getIsFeeNextbutton()
	{
		return FeeNextbutton;
	}

	public WebElement getIsMaxErrorforSliding()
	{
		return MaxErrorforSliding;
	}

	public WebElement getIsNonnumericErrorforSliding()
	{
		return NonnumericErrorforSliding;
	}

	public WebElement getIsNegativeErrorforSliding()
	{
		return NegativeErrorforSliding;
	}

	public WebElement getIsMinreqErrorforSliding()
	{
		return MinreqErrorforSliding;

	}

	public WebElement getIsSlidingScaleFirstTier()
	{
		return isSlidingScaleFirstTier;

	}

	public WebElement getIsSlidingScaleSecondTier()
	{
		return isSlidingScaleSecondTier;

	}

	public WebElement getIsPercentageValueSlidingScaleFirstTier()
	{
		return isPercentageValueSlidingScaleFirstTier;

	}

	public WebElement getIsPercentageValueSlidingScaleSecondTier()
	{
		return isPercentageValueSlidingScaleSecondTier;

	}

	public WebElement getIsNextButton()
	{
		return isNextButton;

	}

	public WebElement getIsConfirmationScreenSlidingScale()
	{
		return isConfirmationScreenSlidingScale;

	}

	public WebElement getIsSlidingScaleSecondTierLimitValue()
	{
		return isSlidingScaleSecondTierLimitValue;

	}

	public boolean check_Maxerrormsg_Sliding(String message)
	{
		String errorMsg = MaxErrorforSliding.getText();

		return message.equals(errorMsg);
	}

	public boolean check_non_numeric_errmsg_sliding(String message)
	{
		String errorMsg = NonnumericErrorforSliding.getText();

		return message.equals(errorMsg);
	}

	public boolean check_negative_errmsg_sliding(String message)
	{
		String errorMsg = NegativeErrorforSliding.getText();

		return message.equals(errorMsg);
	}

	public boolean check_blank_errmsg_sliding(String message)
	{
		String errorMsg = MinreqErrorforSliding.getText();

		return message.equals(errorMsg);
	}

	public List <WebElement> getongoingSlidingtiers()

	{
		WebElement table = this.SlidingScaletiersOngoing;
		return table.findElements(By.xpath("//div[@class='mvc-slidingscalefee']/div/div/div/fieldset/div[6]/div/table/tbody/tr"));
	}

	public List <WebElement> getPercentageLicenseetable()

	{
		WebElement table = this.PercentageLicenseetable;
		return table.findElements(By.xpath("//div[@class='mvc-percentagefee']/div/div/div/fieldset/div/div/table/tbody/tr"));
	}

	public List <Map> testCounttOngoingSliding() throws Throwable
	{
		WebElement table = this.SlidingScaletiersOngoing;
		java.util.List <WebElement> tr_collection = table.findElements(By.xpath("//div[@class='mvc-slidingscalefee']/div/div/div/fieldset/div[6]/div/table/tbody/tr"));

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

						actmap.put("From", tdElement.getText());
						break;

					case 1:
						actmap.put("Icon", tdElement.getText());
						break;

					case 2:
						actmap.put("To", tdElement.findElement(By.xpath("div/div/div/div/input")).getAttribute("data-view-value"));
						break;

					case 3:
						actmap.put("Pa", tdElement.findElement(By.xpath("div/div/div/div/input")).getAttribute("data-view-value"));
						break;

					case 4:
						actmap.put("Icon1", tdElement.getText());
						break;

				}

				col_num++;
			}

			arrayList.add(actmap);

			row_num++;

		}

		System.out.println("arrayList" + arrayList);
		return arrayList;

	}

	public List <Map> testCountlicenseePercentage() throws Throwable
	{
		WebElement table = this.PercentageLicenseetable;
		java.util.List <WebElement> tr_collection = table.findElements(By.xpath("//div[@class='mvc-percentagefee']/div/div/div/fieldset/div/div/table/tbody/tr"));

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

						actmap.put("Type", tdElement.getText());
						break;

					case 1:
						actmap.put("Pa", tdElement.findElement(By.xpath("div/div/div/div/input")).getAttribute("data-view-value"));
						break;

				}

				col_num++;
			}

			arrayList.add(actmap);

			row_num++;

		}

		System.out.println("arrayList" + arrayList);
		return arrayList;

	}
}
