package pages.feeshedule;

import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

//@DefaultUrl("/secure/app/#ng/fees?c=AA8614A18D2DF1E2AAAE1E96C3297644BC2C34B6E1E01804&p=C19C51E3452002F0DC6749AF7DEDEAAD4719006302A377BF")
public class FeeSchedulePage extends PageObject
{

	@FindBy(xpath = "//div[@class='mvc-schedule']/div/div[2]/div/button[@class=' btn- btn-action-tertiary']/span")
	private WebElement Editfeelink;

	@FindBy(xpath = "//div[@class='mvc-periodicfees']/div/div/div[3]/div[2]/pre")
	private WebElement errMessage1;

	@FindBy(xpath = "//div[@class='mvc-menutabs']/div/div/div/a")
	private WebElement feeSchedulelink;

	@FindBy(xpath = "//div[@class='mvc-menutabs']/div/div/div/a[2]")
	private WebElement Chargeoneoff;

	@FindBy(xpath = "//div[@class='mvc-viewfees']/div[6]/div[2]/pre")
	private WebElement errMessage2;

	@FindBy(css = "a[class='link-item'][title='Contact us']")
	private WebElement isContactUs;

	public FeeSchedulePage(WebDriver driver)
	{
		super(driver);
		//		this.driver = driver;
	}

	public WebElement getIsEditfeelink()
	{
		return Editfeelink;
	}

	public WebElement getIserrMessage1()
	{
		return errMessage1;
	}

	public WebElement getIsFeelink()
	{
		return feeSchedulelink;

	}

	public WebElement getIsChargeoneoff()
	{
		return Chargeoneoff;
	}

	public WebElement getIserrMessage2()
	{

		return errMessage2;
	}

	public boolean check_message_error(String message)
	{
		String errorMsg = errMessage1.getText();
		System.out.println("page error----------------" + errorMsg);
		System.out.println("passsed error----------------" + message);
		return message.equals(errorMsg);
	}

	public boolean check_message_error2(String message)
	{
		String errorMsg = errMessage2.getText();
		System.out.println("page error----------------" + errorMsg);
		System.out.println("passsed error----------------" + message);
		return message.equals(errorMsg);
	}

}