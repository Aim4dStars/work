package feature;

import static junit.framework.Assert.*;

import java.util.HashMap;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.iphone.IPhoneDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gargoylesoftware.htmlunit.BrowserVersion;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class Invfdb
{

	String context = "/";

	//these are the different browsers we are willing to run against
	public enum Browsers
	{
		Chrome, InternetExplorer, Android, Ipad, Iphone, Opera, Safari
	};

	private static final String TIMEOUT = "5";
	//this hashmap will keep our users that are active in the system
	private HashMap <String, String> users = new HashMap <String, String>();
	public static WebDriver driver;

	@Before
	//any steps we want to perform before we start our tests
	public void setup()
	{
		//initializing our system by adding our users

		driver = new PhantomJSDriver();

	}

	@After
	//any steps we want to perform after our tests
	public void cleanUp()
	{
		//close our browser, and finalize our driver instance
		driver.quit();

	}

	//our statement for choosing a browser to test in
	@Given("^I want to use the browser (.*)$")
	public void chooseBrowser(Browsers browser) throws Exception
	{
		DesiredCapabilities dCaps;

		//instantiate a new browser based on the choice of browsers
		switch (browser)
		{
			case Chrome:
			{
				driver = new ChromeDriver();
				break;
			}
			case InternetExplorer:
			{
				driver = new InternetExplorerDriver();
				break;
			}
			case Android:
			{
				DesiredCapabilities capabilities = DesiredCapabilities.htmlUnit();
				capabilities.setBrowserName("Mozilla/5.0 (X11; Linux x86_64; rv:24.0) Gecko/20100101 Firefox/24.0");
				capabilities.setVersion("24.0");
				capabilities.setJavascriptEnabled(true);
				driver = new HtmlUnitDriver(BrowserVersion.CHROME_16);
				break;
			}
			case Iphone:
			{
				driver = new IPhoneDriver();
				java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
				break;
			}
			//case Safari:				{ driver = new PhantomJSDriver(dCaps); 		break; }
			default:
			{
				throw new Exception();
			}
		}

	}

	@Then("^I am the (.*) page$")
	public void feedback(String page1) throws Exception
	{
		if (page1.equalsIgnoreCase("feedback"))
		{ //settings for the login page

			driver.get(context + "spa/#ng/contactus/feedback");

		}
		Thread.sleep(10000);
	}

	//which user have we already logged in as
	@Given("^I have logged in as (.*)$")
	public void loginAs(String user) throws Exception
	{
		//webdrivers select element by id functionality
		driver.get("/public/page/logon?TAM_OP=auth_info#ng/contactus/feedback");
		By byElement = By.id("login_username");
		//locate our element
		WebElement webElement = driver.findElement(byElement);
		//setup an action
		Actions selAction = new Actions(driver);
		//send keys to the element selected
		selAction.sendKeys(webElement, user).perform();
		//webdrivers select element By.id functionality
		byElement = By.id("login_entered_password");
		//locate our element
		webElement = driver.findElement(byElement);
		//setup an action
		Actions selAction1 = new Actions(driver);
		//send keys to the element selected
		selAction1.sendKeys(webElement, user).perform();
		//webdrivers select element by id functionality
		byElement = By.id("signinButton");
		//locate our element
		WebElement webElement1 = driver.findElement(By.xpath("(//a[contains(@href, '#')])[3]"));
		//setup an action
		Actions selAction2 = new Actions(driver);
		//click the element selected
		selAction2.click(webElement1).perform();
		driver.findElement(By.id("login_username")).clear();
		driver.findElement(By.id("login_username")).sendKeys("investor");
		driver.findElement(By.id("login_entered_password")).clear();
		driver.findElement(By.id("login_entered_password")).sendKeys("investor");
		driver.findElement(By.name("logon")).click();

	}

	@Given("^Navigate to (.*)$")
	public void nav(String user) throws Exception
	{
		//webdrivers select element by id functionality

		driver.get("/spa/#ng/contactus/feedback");

	}

	//////////////////////////////////
	// Login Definitions
	//////////////////////////////////

	//type in our username
	@When("^I type (.*) in the username input field$")
	public void enterUsername(String user) throws Exception
	{
		//webdrivers select element by id functionality
		driver.get("/public/page/logon?TAM_OP=auth_info#ng/contactus/feedback");
		By byElement = By.id("login_username");
		//locate our element
		WebElement webElement = driver.findElement(byElement);
		//setup an action
		Actions selAction = new Actions(driver);
		//send keys to the element selected
		selAction.sendKeys(webElement, user).perform();
	}

	//type in our password
	@When("^I type (.*) in the password input field$")
	public void enterPassword(String password) throws Exception
	{
		//webdrivers select element by id functionality
		By byElement = By.id("login_entered_password");
		//locate our element
		WebElement webElement = driver.findElement(byElement);
		//setup an action
		Actions selAction = new Actions(driver);
		//send keys to the element selected
		selAction.sendKeys(webElement, password).perform();
	}

	//click the login button
	@When("^I click the login button$")
	public void clickLogin() throws Exception
	{
		//webdrivers select element by id functionality
		//By byElement = By.id("submitLogin");
		//locate our element
		//WebElement webElement = driver.findElement( byElement );
		WebElement webElement = driver.findElement(By.xpath("(//a[contains(@href, '#')])[3]"));
		//setup an action
		Actions selAction = new Actions(driver);
		//click the element selected
		selAction.click(webElement).perform();
	}

	//click the submit button
	@When("^I click the submit button$")
	public void subbtn() throws Exception
	{

		driver.findElement(By.cssSelector("div.select-box")).click();
		driver.findElement(By.cssSelector("li.result")).click();
		WebElement textArea = driver.findElement(By.xpath("//textarea[@name='feedback']"));
		textArea.sendKeys("Complaint is about BT");

		//setup an action
		Actions selAction = new Actions(driver);
		//click the element selected
		WebElement webElement = driver.findElement(By.xpath("//button[@class='btn-action btn-action-primary']"));
		selAction.click(webElement).perform();
		Thread.sleep(10000);
		assertEquals("successfully submitted", driver.findElement(By.cssSelector("span.green")).getText());
	}

	@When("^I entered all the fields$")
	public void entrflds() throws Exception
	{

		Thread.sleep(5000);
		driver.findElement(By.cssSelector("div.select-box")).click();
		driver.findElement(By.cssSelector("li.result")).click();
		WebElement textArea = driver.findElement(By.xpath("//textarea[@name='feedback']"));
		textArea.sendKeys("Complaint is about BT");

	}

	@When("^I submit the feedback$")
	public void submitfdb() throws Exception
	{

		Thread.sleep(5000);
		//setup an action
		Actions selAction = new Actions(driver);
		//click the element selected

		assertEquals("Feedback", driver.findElement(By.cssSelector("span.BTBlue")).getText());

		WebElement webElement = driver.findElement(By.xpath("//button[@type='submit']"));
		selAction.click(webElement).perform();

		driver.findElement(By.xpath("//button[@type='submit']")).click();

	}

	@Then("^I should receive successful message$")
	public void msgrd() throws Exception
	{

		Thread.sleep(5000);
		assertEquals("successfully submitted", driver.findElement(By.cssSelector("span.green")).getText());
	}

	@Then("^I should see the Unique reference Number$")
	public void msgno() throws Exception
	{

		assertEquals("Reference No. 922115",
			driver.findElement(By.cssSelector("div.header.view-headerpanel > h1.heading-five.panel-header")).getText());
		//driver.quit();
	}

	@Then("^I should get error message for required fields$")
	public void msgerr() throws Exception
	{

		Thread.sleep(3000);
		assertEquals("Please select a feedback type", driver.findElement(By.cssSelector("span.error-required")).getText());
		assertEquals("Please enter feedback", driver.findElement(By.cssSelector("div.error.margin-error > span.error-required"))
			.getText());
		//driver.quit();

	}

	//check our error messages
	@Then("^I see the login error message \"(.*)\"$")
	public void checkLoginErrorMessage(String errorMessage) throws Exception
	{
		//webdrivers select element by id functionality
		By byElement = By.id("overError");
		WebElement errorElement = null;
		//wait for up to 5 seconds for our error message
		long end = System.currentTimeMillis() + 5000;
		while (System.currentTimeMillis() < end)
		{
			errorElement = driver.findElement(byElement);
			// If results have been returned, the results are displayed in a drop down.
			if (!errorElement.getText().equals(""))
			{
				break;
			}
		}
		//ensure we got our expected error message
		assertEquals(errorMessage, errorElement.getText());
		//if we have a bad username
		if (errorMessage.contains("username"))
		{
			byElement = By.id("userError");
			errorElement = driver.findElement(byElement);
			//ensure username is marked as the problem
			assertEquals("*", errorElement.getText());
		}
		//if we got a bad password
		if (errorMessage.contains("password"))
		{
			byElement = By.id("passError");
			errorElement = driver.findElement(byElement);
			//ensure password is marked as the problem
			assertEquals("*", errorElement.getText());
		}
	}

	//check the page we are on
	@Then("^I am on the (.*) page$")
	public void checkPage(String page) throws Exception
	{
		String title = null; //the page title
		String url = null; //the page url
		Thread.sleep(5000);
		if (page.equalsIgnoreCase("login"))
		{ //settings for the login page
			title = "NextGen";
			url = "index.html";
		}
		if (page.equalsIgnoreCase("home"))
		{//settings for the launcher page
			title = "NextGen - Home";
			url = "/secure/page/dashboard";
		}
		if (page.equalsIgnoreCase("feedback"))
		{//settings for the launcher page
			title = "NextGen - Home";
			url = "/spa/#ng/contactus/feedback";
		}
		Thread.sleep(2000);

	}

}
