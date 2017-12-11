package pages.feeshedule;

import java.util.List;

import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

//@DefaultUrl("/secure/app/#ng/fees/periodicfees/editfees")
public class EditFeeSchedulePage extends PageObject

{

	@FindBy(css = "span[data-component-name='ongoingdollarcomponent'] button[type='button']")
	private WebElement Dollarfeeongoingbutton;

	@FindBy(xpath = "//div[@class='mvc-dollarfee']")
	private WebElement Dollarfeeongoingpanel;

	@FindBy(xpath = "//div[@class='mvc-licensee']/section/div[2]/div/nav/span[@data-component-name='dollarcomponent']/button")
	private WebElement Dollarfeelicenseebutton;

	@FindBy(xpath = "//div[@class='mvc-licensee']")
	private WebElement Dollarfeelicenseepanel;

	@FindBy(css = "span[class='view-ongoingpercentagecomponent disabled'] button[type='button'] span[class='label-content']")
	private WebElement percentagefeeongoingbutton;

	@FindBy(css = "span[class='view-ongoingslidingscalecomponent disabled'] button[type='button'] span[class='label-content']")
	private WebElement isSlidingscaleOngoingButton;

	@FindBy(xpath = "//div[@class='mvc-percentagefee']")
	private WebElement Percentagefeeongoingpanel;

	@FindBy(xpath = "//div[@class='mvc-ongoing']/section/div[@class='mvc-percentagefee']/div/div/fieldset/div/div[2]/div/div/div/div[@class='view-name view-managedportfolios']")
	private WebElement Percentageportfolioongoing;

	@FindBy(xpath = "//div[@class='mvc-ongoing']/section/div[@class='mvc-percentagefee']/div/div/fieldset/div[2]/div[2]/div/div/div/div[@class='view-name view-termdeposits']")
	private WebElement Percentagetermdepositongoing;

	@FindBy(xpath = "//div[@class='mvc-ongoing']/section/div[@class='mvc-percentagefee']/div/div/fieldset/div[3]/div[2]/div/div/div/div[@class='view-name view-cash']")
	private WebElement Percentagecashongoing;

	@FindBy(xpath = "//div[@class='mvc-licensee']/section/div[2]/div/nav/span[@data-component-name='percentagecomponent']/button")
	private WebElement PercentagefeelicenseeButton;

	@FindBy(xpath = "//div[@class='mvc-percentagefee']")
	private WebElement PercentagefeelicenseePanel;

	@FindBy(xpath = "//div[@class='mvc-licensee']/section/div[@class='mvc-percentagefee']/div/div/fieldset/div/div[2]/div/div/div/div[@class='view-name view-managedportfolios']")
	private WebElement Percentageportfoliolicensee;

	@FindBy(xpath = "//div[@class='mvc-licensee']/section/div[@class='mvc-percentagefee']/div/div/fieldset/div[2]/div[2]/div/div/div/div[@class='view-name view-termdeposits']")
	private WebElement Percentagetermdepositlicensee;

	@FindBy(xpath = "//div[@class='mvc-licensee']/section/div[@class='mvc-percentagefee']/div/div/fieldset/div[3]/div[2]/div/div/div/div[@class='view-name view-cash']")
	private WebElement Percentagecashlicensee;

	@FindBy(xpath = "//div[@class='mvc-ongoing']/section/div[3]/div/div/fieldset/div[5]/div/table/tbody/tr")
	private List <WebElement> Slidingtiers;

	@FindBy(css = "div[class='view-inputcheckbox'] label")
	private WebElement isSlidingScaleManagedPortfolios;

	@FindBy(css = "div[class='view-inputcheckbox_2'] label")
	private WebElement isSlidingScaleTermDepsoits;

	@FindBy(css = "div[class='view-inputcheckbox_3'] label")
	private WebElement isSlidingScaleCash;

	@FindBy(xpath = "//div[@class='mvc-licensee']/section/div[3]/div/div/fieldset/div[5]/div/table/tbody/tr")
	private List <WebElement> Slidinglicenseetiers;

	@FindBy(xpath = "//input[@name='ongoingto0']")
	private WebElement isongoingto0;

	@FindBy(xpath = "//input[@name='ongoingpa0']")
	private WebElement isongoingpa0;

	@FindBy(xpath = "//input[@name='ongoingto1']")
	private WebElement isongoingto1;

	@FindBy(xpath = "//input[@name='ongoingpa1']")
	private WebElement isongoingpa1;

	@FindBy(xpath = "//div[@class='mvc-licensee']/section/div[2]/div/nav/span[@data-component-name='slidingscalecomponent']/button")
	private WebElement isSlidingscaleButtonlicensee;

	@FindBy(xpath = "//button[@type='submit']")
	private WebElement isSubmit;

	@FindBy(css = "h1[class='heading-five panel-header']")
	private WebElement isEditFeeSchedule;

	@FindBy(css = "a[class='link-item'][title='Contact us']")
	private WebElement isContactUs;

	@FindBy(xpath = "//span[@data-component-name='ongoingdollarcomponent']")
	private WebElement OngoingDollarsection;

	@FindBy(xpath = "//span[@data-component-name='ongoingpercentagecomponent']")
	private WebElement OngoingPercentagesection;

	@FindBy(xpath = "//span[@data-component-name='ongoingpercentagecomponent']")
	private WebElement OngoingSlidingsection;

	@FindBy(xpath = "//span[@data-component-name='licenseedollarcomponent']")
	private WebElement LicenseeDollarsection;

	@FindBy(xpath = "//span[@data-component-name='licenseepercentagecomponent']")
	private WebElement LicenseePercentagesection;

	@FindBy(xpath = "//span[@data-component-name='licenseeslidingscalecomponent']")
	private WebElement LicenseeSlidingsection;

	@FindBy(css = "div[class='mvc-ongoing'] input[name='amount']")
	private WebElement DollaramountOngoing;

	@FindBy(css = "div[class='mvc-ongoing'] input[name='cpiindex']")
	private WebElement OngoingindexCPI;

	public EditFeeSchedulePage(WebDriver driver)
	{
		super(driver);
		//		this.driver = driver;
	}

	public WebElement getDollaramountOngoing()
	{
		return DollaramountOngoing;
	}

	public WebElement getOngoingindexCPI()
	{
		return OngoingindexCPI;
	}

	public WebElement getIsdollarfeeongoingbutton()
	{
		return Dollarfeeongoingbutton;
	}

	public WebElement getIsdollarfeeongoingpanel()
	{
		return Dollarfeeongoingpanel;
	}

	public WebElement getIsdollarfeelicenseebutton()
	{
		return Dollarfeelicenseebutton;
	}

	public WebElement getIsdollarfeelicenseepanel()
	{
		return Dollarfeelicenseebutton;
	}

	public WebElement getIspercentagefeeongoingbutton()
	{
		return percentagefeeongoingbutton;
	}

	public WebElement getIsSlidingscaleongoingButton()
	{
		return isSlidingscaleOngoingButton;

	}

	public WebElement getIsPercentagefeeongoingpanel()
	{
		return Percentagefeeongoingpanel;
	}

	public WebElement getIsPercentageportfolioongoing()
	{
		return Percentageportfolioongoing;
	}

	public WebElement getIsPercentagetermdepositongoing()
	{
		return Percentagetermdepositongoing;
	}

	public WebElement getIsPercentagecashongoing()
	{
		return Percentagecashongoing;
	}

	public WebElement getIsPercentagefeelicenseeButton()
	{
		return PercentagefeelicenseeButton;
	}

	public WebElement getIsPercentagefeelicenseePanel()
	{
		return PercentagefeelicenseePanel;
	}

	public WebElement getIsPercentageportfoliolicensee()
	{
		return Percentageportfoliolicensee;
	}

	public WebElement getIsPercentagetermdepositlicensee()

	{
		return Percentagetermdepositlicensee;
	}

	public WebElement getIsPercentagecashlicensee()

	{
		return Percentagecashlicensee;
	}

	public WebElement getIsSlidingscaleButtonlicensee()
	{
		return isSlidingscaleButtonlicensee;

	}

	public List <WebElement> getIsSlidingtiers()
	{
		return Slidingtiers;
	}

	public List <WebElement> getIsSlidinglicenseetiers()
	{
		return Slidinglicenseetiers;
	}

	public WebElement getIsOngoingto0()
	{

		return isongoingto0;
	}

	public WebElement getIsOngoingpa0()
	{

		return isongoingpa0;
	}

	public WebElement getIsOngoingto1()
	{

		return isongoingto1;
	}

	public WebElement getIsOngoingpa1()
	{
		return isongoingpa1;
	}

	public WebElement getIsEditSubmitButton()
	{
		return isSubmit;

	}

	public WebElement getIsEditFeeScheduleMsg()
	{
		return isEditFeeSchedule;

	}

	public WebElement getIsSlidingScaleManagedPortfolios()
	{
		return isSlidingScaleManagedPortfolios;

	}

	public WebElement getIsSlidingScaleTermDepsoits()
	{
		return isSlidingScaleTermDepsoits;

	}

	public WebElement getIsSlidingScaleCash()
	{
		return isSlidingScaleCash;

	}

	public WebElement getOngoingDollarsection()
	{
		return OngoingDollarsection;
	}

	public WebElement getOngoingPercentagesection()
	{
		return OngoingPercentagesection;
	}

	public WebElement getOngoingSlidingsection()
	{
		return OngoingSlidingsection;
	}

	public WebElement getLicenseeSlidingsection()
	{
		return LicenseeSlidingsection;
	}

	public WebElement getLicenseePercentagesection()
	{
		return LicenseePercentagesection;
	}

	public WebElement getLicenseeDollarsection()
	{
		return LicenseeDollarsection;
	}

}