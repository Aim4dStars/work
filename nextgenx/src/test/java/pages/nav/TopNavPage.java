package pages.nav;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

import net.thucydides.core.pages.PageObject;

public class TopNavPage extends PageObject
{

	@CacheLookup
	@FindBy(xpath = "//a[@data-ng-key='logoutLink']")
	private WebElement logoutLink;

	@CacheLookup
	@FindBy(xpath = "//a[@data-ng-key='accountNavLeft']")
	private WebElement accountsLink;

	@CacheLookup
	@FindBy(xpath = "//a[@data-ng-key='clientsNavLeft']")
	private WebElement clientsLink;

	public TopNavPage(WebDriver driver)
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
