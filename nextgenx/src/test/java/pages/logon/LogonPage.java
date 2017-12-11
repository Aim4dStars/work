package pages.logon;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@DefaultUrl("/public/page/logon")
public class LogonPage extends PageObject
{

	@FindBy(css = "#login_username")
	private WebElement usernameField;

	@FindBy(css = "#login_entered_password")
	private WebElement passwordField;

	@FindBy(css = "#login_username")
	private WebElement usernameFieldLocal;

	@FindBy(css = "#login_entered_password")
	private WebElement passwordFieldLocal;

	@FindBy(css = ".primaryButton.jq-formSubmit")
	private WebElement submit;

	public LogonPage(WebDriver driver)
	{
		super(driver);
	}

	public void enter_username(String username)
	{
		usernameField.sendKeys(username);
	}

	public void enter_password(String password)
	{
		passwordField.sendKeys(password);
	}

	public void enter_usernameLocal(String username)
	{
		usernameFieldLocal.sendKeys(username);
	}

	public void enter_passwordLocal(String password)
	{
		passwordFieldLocal.sendKeys(password);
	}

	public void login() throws Exception
	{
		submit.click();
		Thread.sleep(10000);
	}

	public void gotopage(String linkname) throws Throwable
	{
		Thread.sleep(5000);

		this.waitForRenderedElements(By.cssSelector("div:nth-child(3) > ul > li:nth-child(1) > a > span.label"));

		this.element(By.linkText(linkname)).click();
		Thread.sleep(5000);

	}

	public void doLogon() throws Throwable
	{
		String host = getDriver().getCurrentUrl();
		getDriver().get(host);
		this.enter_username("adviser");
		this.enter_password("adviser");

		this.login();
		Thread.sleep(5000);

		String currUrl = getDriver().getCurrentUrl();
		currUrl = currUrl.substring(0, currUrl.lastIndexOf("ng/") + 3);
		this.getDriver().get(currUrl);

		getDriver().navigate().refresh();
		Thread.sleep(3000);
	}

	/*public boolean success()
	{
		return messageBox.getAttribute("class").contains("successBox");
	}

	public void open()
	{
		driver.get(TestHarnessCucumber.CONTEXT_PATH + "/public/page/logon");
		//driver.get("http://localhost:9080/ng/public/page/logon");
	}*/
}
