package pages.clients;

import static org.junit.Assert.assertThat;

import org.hamcrest.core.Is;
import org.openqa.selenium.WebDriver;

import net.thucydides.core.pages.PageObject;
//@DefaultUrl("http://dwgps0022:8120/nextgen/secure/secure/page/clients");
public class ClientsPage extends PageObject
{

	private WebDriver driver;

	public ClientsPage(WebDriver driver)
	{
		this.driver = driver;
	}

	public void is_clients_page()
	{
		assertThat(driver.getPageSource().contains("Clients"), Is.is(true));
	}

}
