package pages.nav;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class NavPage
{

	@FindBy(xpath = "//a[@data-ng-key='logoutLink']")
	private WebElement logoutLink;

	@FindBy(xpath = "//a[@data-ng-key='accountNavLeft']")
	private WebElement accountsLink;

	@FindBy(xpath = "//a[@data-ng-key='clientsNavLeft']")
	private WebElement clientsLink;

	public NavPage(WebDriver driver)
	{}

	public void do_logout()
	{
		logoutLink.click();
	}

	public void select_accounts()
	{
		accountsLink.click();
	}

	public void select_clients()
	{
		clientsLink.click();
	}
}
