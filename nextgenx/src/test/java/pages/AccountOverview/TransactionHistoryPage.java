package pages.AccountOverview;

import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

//@DefaultUrl("/secure/app/#ng/transactionhistory?c=FC622CC21BFFE81A42F70BB718A6A91D1D9470BB4FB2631D&p=975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0")
public class TransactionHistoryPage extends PageObject
{
	@FindBy(xpath = "//h1[@class='heading-five panel-header']")
	private WebElement Transactionhistory;

	public TransactionHistoryPage(WebDriver driver)
	{
		super(driver);
	}

	public WebElement getTransactionHistory()
	{
		return Transactionhistory;
	}

}