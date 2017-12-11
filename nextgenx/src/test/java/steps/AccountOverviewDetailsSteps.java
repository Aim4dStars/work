package steps;

import static junit.framework.Assert.*;
import static org.fest.assertions.Assertions.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import pages.AccountOverview.AccountOverviewDetailsPage;
import pages.AccountOverview.TransactionHistoryPage;
import pages.logon.LoginPage;

public class AccountOverviewDetailsSteps extends ScenarioSteps
{

	public List <String> arrayList = new ArrayList <String>();

	LoginPage loginPage;
	AccountOverviewDetailsPage AccountOverviewDetailsPage;
	TransactionHistoryPage TransactionHistoryPage;

	public AccountOverviewDetailsSteps(Pages pages)
	{
		super(pages);
	}

	@Step
	public void enters(String keyword)
	{
		loginPage.enter_keywords(keyword);
	}

	@Step
	public void starts_logon_fee() throws Throwable
	{
		loginPage.open();
		loginPage.doLogon();
	}

	@Step
	public void should_see_definition()
	{
		assertThat(loginPage.getTitle()).contains("Panorama - Home");
	}

	@Step
	public void is_the_home_page()
	{
		loginPage.open();
	}

	@Step
	public void navigate_account_screen() throws Throwable
	{
		loginPage.gotopage("Overview");
	}

	@Step
	public void clickRecentTransactions()
	{
		AccountOverviewDetailsPage.getRecentTransactions().click();
	}

	@Step
	public void verifytransactionscreen()
	{
		TransactionHistoryPage.getTransactionHistory().isDisplayed();
	}

	@Step
	public List <Map> testCount(List <String> headerName) throws Throwable
	{
		WebElement tableProp = AccountOverviewDetailsPage.getTableRecAccountRecentTrans();
		return loginPage.tableContTest(headerName, tableProp);
	}

	@Step
	public List <Map> testCount1() throws Throwable
	{
		return AccountOverviewDetailsPage.tablecount1();
	}

	@Step
	public List <Map> testMaturitycount() throws Throwable
	{
		return AccountOverviewDetailsPage.tableMaturitycount();

	}

	@Step
	public void verifyascendingmaturitydate() throws Throwable
	{
		AccountOverviewDetailsPage.sortBy();
	}

	@Step
	public void Verify_dateformat_RecentTransaction() throws Throwable
	{
		for (WebElement m_datelistmaturingtd : AccountOverviewDetailsPage.getTableRecAccountformat())
		{
			String Str1 = m_datelistmaturingtd.getText();

			String Result = Str1.substring(6);

			DateFormat format = new SimpleDateFormat("dd MMM yyyy");

			Date d1;
			try
			{
				d1 = format.parse(Str1);

			}
			catch (ParseException e)
			{
				assertEquals("Date not matched", "Date Matched");
				e.printStackTrace();

			}

		}

	}

	@Step
	public void verify_deposit_date() throws Throwable
	{
		for (WebElement m_datelistmaturingtd : AccountOverviewDetailsPage.getScheduletransactionCashDate())
		{
			String Str1 = m_datelistmaturingtd.getText();

			String Result = Str1.substring(6);

			DateFormat format = new SimpleDateFormat("dd MMM yyyy");

			Date d1;
			try
			{
				d1 = format.parse(Str1);

			}
			catch (ParseException e)
			{
				assertEquals("Date not matched", "Date Matched");
				e.printStackTrace();

			}

		}
	}

	@Step
	public List <Map> testCountScheduleTrans(List <String> headerName) throws Throwable
	{
		WebElement tableProp = AccountOverviewDetailsPage.getTableRecAccountScheduleTrans();
		return loginPage.tableContTest(headerName, tableProp);
	}

	@Step
	public void displayRecentRecords() throws Throwable
	{
		List <WebElement> tr_collection = AccountOverviewDetailsPage.getTableRecordsRecentCash();

		if (tr_collection.size() <= 10)
		{

			assertFalse(AccountOverviewDetailsPage.getTableRecordsRecentCash().isEmpty());
		}
	}

	@Step
	public void verify_icon_transaction()
	{
		assertTrue(AccountOverviewDetailsPage.getIconrecentTransaction().isDisplayed());
	}

	@Step
	public void hover_icon()
	{
		Actions actions = new Actions(getDriver());
		WebElement mouseHovericon = AccountOverviewDetailsPage.getIconrecentTransaction();
		actions.moveToElement(mouseHovericon);
		actions.perform();
	}

	@Step
	public void verify_text_iconhover()
	{
		String titleText = AccountOverviewDetailsPage.getIconrecentTransaction().getAttribute("title");
		assertEquals(titleText, "Uncleared");
	}

	@Step
	public void verify_header_cashdeposit(String header)
	{
		assertEquals(header, AccountOverviewDetailsPage.getCashDepositheader().getText());
	}

	@Step
	public void verify_cashdeposit_positive()
	{
		for (WebElement m_datelistmaturingtd : AccountOverviewDetailsPage.getCashdeposits())
		{
			String Str1 = m_datelistmaturingtd.getText();

			String Str2 = Str1.replaceAll("[\\$\\,\\,]", "").trim();

			double n = Double.parseDouble(Str2);

			assertTrue(n > 0);

		}
	}

	@Step
	public void verify_cashpayment_negative()
	{
		for (WebElement m_datelistmaturingtd : AccountOverviewDetailsPage.getCashPayments())
		{
			String Str1 = m_datelistmaturingtd.getText();

			String Str2 = Str1.replaceAll("[\\$\\,\\,]", "").trim();

			double n = Double.parseDouble(Str2);

			assertTrue(n < 0);

		}
	}

	@Step
	public void Verify_recentcash_hyperlink()
	{
		assertTrue(AccountOverviewDetailsPage.getRecentTransactions().isDisplayed());
	}

	@Step
	public void Verify_icon_maturityterm()
	{
		for (WebElement m_iconlistmaturingtd : AccountOverviewDetailsPage.geticondisplay())
		{
			String Str1 = m_iconlistmaturingtd.getTagName();
			assertEquals("img", Str1);
			assertTrue(m_iconlistmaturingtd.isDisplayed());
		}
	}

	@Step
	public void Verify_daystext_maturityterm()
	{
		for (WebElement m_iconlistmaturingtd : AccountOverviewDetailsPage.getdayslink())
		{
			assertTrue(m_iconlistmaturingtd.isDisplayed());
		}
	}

	@Step
	public void verifyLinksOrders()
	{
		assertTrue(AccountOverviewDetailsPage.getOrdersInProgresslink().isDisplayed());
		assertTrue(AccountOverviewDetailsPage.getBuyslink().isDisplayed());
		assertTrue(AccountOverviewDetailsPage.getSellslink().isDisplayed());
	}

	@Step
	public void verifyMessageSection()
	{
		assertTrue(AccountOverviewDetailsPage.getMessageSection().isDisplayed());
	}

	@Step
	public void verifyscheduledtransactionlink()
	{
		assertTrue(AccountOverviewDetailsPage.getScheduledtransactionslink().isDisplayed());
	}

	@Step
	public void Verify_nocash()
	{
		String Str1;
		Str1 = AccountOverviewDetailsPage.getnocashtext().getText();
		assertEquals(Str1, "There are no upcoming cash deposits");

	}

	@Step
	public void Verify_nocashpayments()
	{

		String Str1;
		Str1 = AccountOverviewDetailsPage.getNocashPayments().getText();
		assertEquals(Str1, "There are no upcoming cash payments");
	}

	@Step
	public void verify_ordersInProgressHyperlink()
	{
		assertTrue(!AccountOverviewDetailsPage.getIsOrdersInProgressHeader().getAttribute("href").isEmpty());
	}

	@Step
	public void clickOrderInProgressHyperlink()
	{
		AccountOverviewDetailsPage.getIsOrdersInProgressHeader().click();
	}

	@Step
	public void verify_buyApplicationsHyperlink()
	{
		assertTrue(!AccountOverviewDetailsPage.getIsBuyApplicationHeader().getAttribute("href").isEmpty());
	}

	@Step
	public void clickBuyApplicationHyperlink()
	{
		AccountOverviewDetailsPage.getIsBuyApplicationHeader().click();
	}

	@Step
	public void verify_sellsRedemptionHyperlink()
	{
		assertTrue(!AccountOverviewDetailsPage.getIsSellsRedemptionHeader().getAttribute("href").isEmpty());
	}

	@Step
	public void clickSellsRedemptionHyperlink()
	{
		AccountOverviewDetailsPage.getIsSellsRedemptionHeader().click();
	}

	@Step
	public void zeroStateOrdersInProgress(String Amount)
	{
		assertEquals(Amount, AccountOverviewDetailsPage.getIsBuyApplicationsAmount().getText());

	}

	@Step
	public void zeroStateOrdersInProgressRedemption(String Amount)
	{

		assertEquals(Amount, AccountOverviewDetailsPage.getIsSellApplicationsAmount().getText());

	}

}
