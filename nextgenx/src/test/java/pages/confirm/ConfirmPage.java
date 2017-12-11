package pages.confirm;

import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

//@DefaultUrl("/secure/app/#ng/fees/chargeadvicefees")
public class ConfirmPage extends PageObject
{

	public final WebDriver driver;

	@FindBy(xpath = "//form/div/div[2]/div")
	private WebElement isConfirmMsg;

	@FindBy(xpath = "//button[@title='Next']")
	private WebElement isNextButton;

	@FindBy(className = "checkbox-label")
	private WebElement isAuthentication;

	@FindBy(className = "error-min-value")
	private WebElement errReqAmount;

	@FindBy(xpath = "//button[@title='Cancel']")
	private WebElement isConfirmCancel;

	@FindBy(xpath = "//button[@type='submit']")
	private WebElement isSubmitButton;

	@FindBy(xpath = "(//button[@type='button'])[2]")
	private WebElement isconfirmcancelyes;

	@FindBy(xpath = "//button[@title='Cancel']")
	private WebElement isconfirmrecancel;

	public ConfirmPage(WebDriver driver)
	{
		this.driver = driver;
	}

	public WebElement getIsConfirmMsg()
	{
		return isConfirmMsg;
	}

	public WebElement getIsNextButton()
	{
		return isNextButton;
	}

	public void setIsNextButton(WebElement isNextButton)
	{
		this.isNextButton = isNextButton;

	}

	public void Authentication()
	{
		this.isAuthentication = isAuthentication;

	}

	public WebElement getIsAuthentication()
	{
		return isAuthentication;
	}

	public void ConfirmCancel()
	{
		this.isConfirmCancel = isConfirmCancel;

	}

	public WebElement getIsConfirmCancel()
	{
		return isConfirmCancel;
	}

	public void confirmcancelyes()
	{
		this.isconfirmcancelyes = isconfirmcancelyes;

	}

	public WebElement getIsconfirmcancelyes()
	{
		return isconfirmcancelyes;
	}

	public void isconfirmrecancel()
	{
		this.isconfirmrecancel = isconfirmrecancel;

	}

	public WebElement getIsisconfirmrecancel()
	{
		return isconfirmrecancel;
	}

	public void SubmitButtonPage()
	{
		this.isSubmitButton = isSubmitButton;
	}

	public WebElement getIsSubmitButton()
	{
		return isSubmitButton;
	}

	public WebElement getErrReqAmount()
	{
		return errReqAmount;
	}

	public void setErrReqAmount(WebElement errReqAmount)
	{
		this.errReqAmount = errReqAmount;
	}

	public boolean check_message(String message)
	{
		String errorMsg = errReqAmount.getText();
		return message.equals(errorMsg);

	}

	public String getFee()
	{

		//String msgFee = this.getIsConfirmMsg().getText();
		String msgFee = driver.findElement(By.cssSelector("div.columns-28 > div")).getText();

		return msgFee;
	}

	public boolean isElementPresent(By by)
	{
		try
		{
			driver.findElement(by);
			return true;
		}
		catch (NoSuchElementException e)
		{
			return false;
		}
	}

}
