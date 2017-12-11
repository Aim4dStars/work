package pages.dashboard;

import static org.junit.Assert.assertThat;

import org.hamcrest.core.Is;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.pages.PageObject;

@DefaultUrl("/secure/page/home")
public class DashboardPage extends PageObject
{
	@Managed
	private final WebDriver driver;

	@CacheLookup
	@FindBy(xpath = "//article[@data-ng-flag='investor']")
	private WebElement isInvestorPage;

	@CacheLookup
	@FindBy(xpath = "//header[@data-ng-flag='adviser']")
	private WebElement isAdviserPage;

	@CacheLookup
	@FindBy(xpath = "//table[@data-ng-key='savedDrafts']//a[@data-ng-key='tableTitle']")
	private WebElement draftHeading;

	@CacheLookup
	@FindBy(css = "a[class='link-item'][title='Contact us']")
	private WebElement isContactUs;

	public DashboardPage(WebDriver driver)
	{
		this.driver = driver;
	}

	public void is_investor_homepage()
	{
		isHomePage();
		assertThat(isInvestorPage.isDisplayed(), Is.is(true));
	}

	public void is_adviser_homepage()
	{
		isHomePage();
		assertThat(isAdviserPage.isDisplayed(), Is.is(true));
	}

	public void isHomePage()
	{
		assertThat(driver.getCurrentUrl().contains("dashboard"), Is.is(true));
	}

	public void select_draft_applications()
	{
		draftHeading.click();
	}

}
