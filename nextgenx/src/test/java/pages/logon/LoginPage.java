package pages.logon;

import static ch.lambdaj.Lambda.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@DefaultUrl("/public/page/logon")
public class LoginPage extends PageObject
{
	public List <Map> arrayList = new ArrayList <Map>();
	@FindBy(name = "search")
	private WebElement searchTerms;

	@FindBy(name = "go")
	private WebElement lookupButton;

	@FindBy(css = "ol li")
	private List <WebElement> definitionList;

	@FindBy(className = "jq-logonUsername")
	private WebElement usernameField;

	@FindBy(className = "jq-logonPassword")
	private WebElement passwordField;

	@FindBy(id = "login_username")
	private WebElement usernameFieldDev2;

	@FindBy(name = "login_entered_password")
	private WebElement passwordFieldDev2;

	@FindBy(className = "jq-formSubmit")
	private WebElement submit;

	@FindBy(css = "nav[class='global-footer-links']>a:nth-child(1)")
	private WebElement contactus;

	@FindBy(css = "div[class='mvc-contactinfo']>div:nth-child(5) a")
	private WebElement Overview;

	boolean bLogin = false;

	public LoginPage(WebDriver driver)
	{
		super(driver);
	}

	public void enter_username(String username)
	{

		usernameField.sendKeys("adviser");
	}

	public void enter_password(String password)
	{
		passwordField.sendKeys(password);
	}

	public void enter_usernameDev2(String username)
	{
		usernameFieldDev2.sendKeys(username);
	}

	public void enter_passwordDev2(String password)
	{
		passwordFieldDev2.sendKeys(password);
	}

	public void login()
	{
		submit.click();

	}

	public void enter_keywords(String keyword)
	{
		$(searchTerms).type(keyword);
	}

	public void lookup_terms()
	{
		$(lookupButton).click();
	}

	public Iterable <String> getDefinitions()
	{
		return extract(definitionList, on(WebElement.class).getText());
	}

	public void gotopage(String linkname) throws Throwable
	{
		Thread.sleep(5000);

		this.waitForRenderedElements(By.cssSelector("div:nth-child(3) > ul > li:nth-child(1) > a > span.label"));

		this.element(By.linkText(linkname)).click();
		Thread.sleep(5000);

	}

	public void gotomenu() throws Throwable
	{
		Thread.sleep(2000);
		this.contactus.click();
		Thread.sleep(2000);
	}

	public boolean viewMenu(String linkname) throws Throwable
	{

		return (this.element(By.linkText(linkname)).isPresent());

	}

	@Deprecated
	public void openLoginPage() throws Throwable
	{

		String host = getDriver().getCurrentUrl();

		do
		{

			if (!bLogin)
			{

				if (host.contains("dwgps0022.btfin.com"))
				{
					getDriver().get(host);

					this.enter_username("adviser");
					this.enter_password("adviser");
					this.login();
				}
				else if (host.contains("dev2.panoramaadviser.srv.westpac.com.au"))
				{
					getDriver().get(host);

					this.enter_usernameDev2("201601388");
					this.enter_passwordDev2("nextgen02");
					this.login();

				}
			}
			Thread.sleep(3000);
		}
		while (getDriver().getCurrentUrl().contains("logon"));

		bLogin = true;

	}

	public List <Map> tableContTest(List <String> headerName, WebElement tableProp) throws Throwable
	{

		List <WebElement> tr_collection = tableProp.findElements(By.tagName("tr"));

		int row_num, col_num;
		row_num = 1;
		int no_of_rec = headerName.size();

		for (WebElement trElement : tr_collection)
		{
			java.util.List <WebElement> td_collection = trElement.findElements(By.xpath("td"));
			Map <String, String> actual = new HashMap <String, String>();

			col_num = 1;

			if (td_collection.size() > 1)
			{

				for (WebElement tdElement : td_collection)
				{

					if (no_of_rec == 4)
					{
						switch (col_num)
						{
							case 1:
								actual.put(headerName.get(0), tdElement.getText());
							case 2:
								actual.put(headerName.get(1), tdElement.getText());
								break;
							case 3:
								actual.put(headerName.get(2), tdElement.getText());
								break;
							case 4:
								actual.put(headerName.get(3), tdElement.getText());
								break;

						}
					}
					else if (no_of_rec == 5)
					{
						switch (col_num)
						{
							case 1:
								actual.put(headerName.get(0), tdElement.getText());
								break;
							case 2:
								actual.put(headerName.get(1), tdElement.getText());
								break;
							case 3:
								actual.put(headerName.get(2), tdElement.getText());
								break;
							case 4:
								actual.put(headerName.get(3), tdElement.getText());
								break;
							case 5:
								actual.put(headerName.get(4), tdElement.getText());
								break;

						}
					}
					else if (no_of_rec == 3)
					{
						switch (col_num)
						{
							case 1:
								actual.put(headerName.get(0), tdElement.getText());
								break;
							case 2:
								actual.put(headerName.get(1), tdElement.getText());
								break;
							case 3:
								actual.put(headerName.get(2), tdElement.getText());
								break;
						}
					}

					col_num++;

				}

				arrayList.add(actual);
			}

			row_num++;

		}

		return arrayList;

	}

	/**
	 * Quick helper function while we refactor
	 * @throws Throwable 
	 */
	public void doLogon() throws Throwable
	{
		String host = getDriver().getCurrentUrl();

		if (host.contains("dwgps0022"))
		{
			getDriver().get(host);
			this.enter_username("adviser");
			this.enter_password("adviser");
			this.login();
			Thread.sleep(5000);
			this.getDriver().get("http://dwgps0022.btfin.com:8120/nextgen/secure/app/#ng/");
			getDriver().navigate().refresh();
			Thread.sleep(3000);
		}
		else if (host.contains("dev2.panorama"))
		{
			//this.open();
			this.enter_usernameDev2("201601388");
			this.enter_passwordDev2("nextgen02");
			this.login();
			Thread.sleep(5000);
			this.getDriver().get("https://dev2.panoramaadviser.srv.westpac.com.au/ng/secure/app/");
		}
		else if (host.contains("localhost"))
		{
			//this.open();
			this.enter_username("AVALOQ_adviser");
			this.enter_password("adviser");
			this.login();
		}
		else if (host.contains("sit3"))
		{
			//this.open();
			this.enter_usernameDev2("201601388");
			this.enter_passwordDev2("nextgen02");
			this.login();
			Thread.sleep(5000);

			this.getDriver().get("https://sit3.btnextgenadvisor.com.au/ng/secure/app/");

		}

	}

	public void waitForPageToLoad() throws Throwable

	{

		for (int count = 0; count < 50; count++)
		{
			//Thread.sleep(2000);
			getDriver().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			if (this.element(By.linkText("Contact us")).isDisplayed())
			{
				break;
			}

			count++;
		}

	}
}
