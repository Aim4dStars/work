package steps;

import static junit.framework.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import pages.BTTransactions.BTScheduledTransactionsPage;
import pages.logon.LogonPage;

public class BTScheduledTransactionsSteps extends ScenarioSteps
{

	LogonPage logonpage;
	BTScheduledTransactionsPage btScheduledTransactionsPage;

	public BTScheduledTransactionsSteps(Pages pages)
	{
		super(pages);
	}

	public void openScheduledTransactionsPage() throws Throwable
	{
		logonpage.gotopage("Scheduled Transactions");
	}

	public void waitForScheduleTransactionTextToBePresent()
	{
		btScheduledTransactionsPage.shouldBeVisible(By.cssSelector(".heading-five.panel-header"));
	}

	//verifying scheduled transaction
	@Step
	public void Scheduled_Transaction_HeaderText() throws Throwable

	{
		String ScheduledTransactionHeader = btScheduledTransactionsPage.getScheduledTransactionHeaderTitle().getText();
		assertEquals(ScheduledTransactionHeader, "Scheduled transactions");
		assertTrue(btScheduledTransactionsPage.getScheduledTransactionHeaderTitle().isDisplayed());

	}

	@Step
	public void Scheduled_Transaction_Message_Text()
	{
		String ScheduledTransactionMessage = btScheduledTransactionsPage.getScheduledTransactionHeaderMessage().getText();
		assertEquals(ScheduledTransactionMessage, "Please note that scheduled transactions are executed daily at 5:00pm AEST");
		assertTrue(btScheduledTransactionsPage.getScheduledTransactionHeaderMessage().isDisplayed());

	}

	@Step
	public void Table_column_Nextdue_Text()
	{
		String TableColumnNextdue = btScheduledTransactionsPage.getTableColumnNextDue().getText();
		assertEquals(TableColumnNextdue, "Next due");
		assertTrue(btScheduledTransactionsPage.getTableColumnNextDue().isDisplayed());

	}

	@Step
	public void Table_column_Description_Text()
	{
		String TableColumnDescription = btScheduledTransactionsPage.getTableDescriptiondue().getText();
		assertEquals(TableColumnDescription, "Description");
		assertTrue(btScheduledTransactionsPage.getTableDescriptiondue().isDisplayed());

	}

	@Step
	public void Table_column_Repeat_Text()
	{
		String TableColumnRepeat = btScheduledTransactionsPage.getTableColumnRepeat().getText();
		assertEquals(TableColumnRepeat, "Repeats");
		assertTrue(btScheduledTransactionsPage.getTableColumnRepeat().isDisplayed());

	}

	@Step
	public void Table_column_Credit_Text()
	{
		String TableColumnCredit = btScheduledTransactionsPage.getTableColumnCredit().getText();
		assertTrue(btScheduledTransactionsPage.getTableColumnCredit().getText().contains("Credit"));
		assertTrue(btScheduledTransactionsPage.getTableColumnCredit().getText().contains("$"));
		assertTrue(btScheduledTransactionsPage.getTableColumnCredit().isDisplayed());

	}

	@Step
	public void Table_column_Debit_Text()
	{
		String TableColumnDebit = btScheduledTransactionsPage.getTableColumnDebit().getText();
		assertTrue(btScheduledTransactionsPage.getTableColumnDebit().getText().contains("Debit"));
		assertTrue(btScheduledTransactionsPage.getTableColumnDebit().getText().contains("$"));
		assertTrue(btScheduledTransactionsPage.getTableColumnDebit().isDisplayed());

	}

	//Verifying the result show format
	@Step
	public void ShowingResultcount()
	{
		String ResCount = btScheduledTransactionsPage.getShowingResultcount().getText();
		String temp[] = ResCount.split(" ");
		String partOfText1 = temp[0];
		String partOfText2 = temp[2];
		assertEquals(partOfText1, "Showing");
		assertEquals(partOfText2, "of");
		String Num1 = temp[1];
		String Num2 = temp[3];
		assertTrue(temp[1].contains("-"));
	}

	//verfying see more link
	@Step
	public void Show_More_Link()
	{
		String showMoreLink = btScheduledTransactionsPage.getSeeMoreLink().getText();
		assertEquals(showMoreLink, "Show More");
		assertTrue(btScheduledTransactionsPage.getSeeMoreLink().isDisplayed());

	}

	//Click on see more link
	@Step
	public void Clicking_See_More_Link()
	{
		btScheduledTransactionsPage.getShowMoreLinkClickable().click();

	}

	//Expanded the first record
	@Step
	public void Expanding_Record()
	{
		btScheduledTransactionsPage.getFirstRowExpanded().click();

	}

	// Verifying wether the correct page expanded
	@Step
	public void Verifying_Page_Expanded()
	{
		assertTrue(btScheduledTransactionsPage.checkingCorrectPageExpanded().getText().contains("Deposit"));

	}

	public void Verifying_Page_Collapsed()
	{
		assertFalse(btScheduledTransactionsPage.checkingDataWhenCollapsed().getText().contains("Deposit"));

	}

	//Checking the text From and To in expanded state of Scheduled Transaction record
	@Step
	public void Checking_Subtitle_From_To_Of_Record_After_Expansion() throws Throwable
	{

		List <String> expectedSubtitle = Arrays.asList("From", "To");
		List <String> actualSubtitle = new ArrayList <String>();
		List <WebElement> isDropdownOption = btScheduledTransactionsPage.checkingSubTitleOfRecordAfterExpansionListOne();
		for (WebElement option : isDropdownOption)
		{

			actualSubtitle.add(option.getText());

		}

		assert actualSubtitle.containsAll(expectedSubtitle);
	}

	//Checking the text in expanded state of Scheduled Transaction record
	@Step
	public void Checking_Subtitle_Of_Record_After_Expansion() throws Throwable
	{

		List <String> expectedSubtitle = Arrays.asList("Date",
			"Repeats",
			"First payment",
			"Last payment",
			"Description",
			"Receipt no.");
		List <String> actualSubtitle = new ArrayList <String>();
		List <WebElement> isDropdownOption = btScheduledTransactionsPage.checkingSubTitleOfRecordAfterExpansionListTwo();
		for (WebElement option : isDropdownOption)
		{

			actualSubtitle.add(option.getText());

		}

		assert actualSubtitle.containsAll(expectedSubtitle);
	}

	//Verifying text deposit of schedule
	@Step
	public void Deposit_Of_Schedule_Text_Format()
	{
		assertTrue(btScheduledTransactionsPage.getDepositOfScheduleFormat().getText().contains("Deposit of"));
		assertTrue(btScheduledTransactionsPage.getDepositOfScheduleFormat().getText().contains("scheduled"));
	}

	//Verifying button Stop Schedule
	@Step
	public void Stop_Schedule_Button()
	{
		assertTrue(btScheduledTransactionsPage.getStopScheduleButton().isDisplayed());

	}

	//Verify button Download
	@Step
	public void Download_Button()
	{
		assertTrue(btScheduledTransactionsPage.getDownloadButton().isDisplayed());

	}

	//Expanded the Second failed record
	@Step
	public void Expanding_Second_Record()
	{
		btScheduledTransactionsPage.getSecondRecordExpanded().click();

	}

	//Verifying failed Transaction header
	@FindBy(xpath = "//div[@class='mvc-termdeposits']/table/tbody/tr[4]/td/div/div/div/div[2]/h1")
	private WebElement isFailedTransactionHeader;

	public void Failed_Transaction_Header()
	{
		assertTrue(btScheduledTransactionsPage.getFailedTransactionHeader().getText().contains("Deposit of"));
		assertTrue(btScheduledTransactionsPage.getFailedTransactionHeader().getText().contains("failed and will retry"));

	}

	//Verifying failed Transaction header
	@Step
	public void Failed_Transaction_Message()
	{
		String TableColumnRepeat = btScheduledTransactionsPage.getFailedTransactionMessage().getText();
		assertEquals(TableColumnRepeat,
			"This transaction failed and will retry. Please make sure there are enough funds in the account to cover the next scheduled transaction.");
		assertTrue(btScheduledTransactionsPage.getFailedTransactionMessage().isDisplayed());

	}

	@Step
	public void Click_On_StopSchedule_Button()
	{
		btScheduledTransactionsPage.getStopScheduleButton().click();
	}

	@Step
	public void StopSchedule_Header_Text()
	{
		String StopScheduleHeader = btScheduledTransactionsPage.getStopScheduleModalHeader().getText();
		assertEquals(StopScheduleHeader, "Are you sure you want to stop this series of scheduled ?");
		assertTrue(btScheduledTransactionsPage.getStopScheduleModalHeader().isDisplayed());
	}

	@Step
	public void StopSchedule_Header_Text_NotDisplayed()
	{
		assertFalse(btScheduledTransactionsPage.getStopScheduleModalHeader().isDisplayed());
	}

	@Step
	public void StopSchedule_Message_Text()
	{
		String StopScheduleMessage = btScheduledTransactionsPage.getStopScheduleMessage().getText();
		assertEquals(StopScheduleMessage, "Are you sure you want to stop this series of scheduled ?");
		assertTrue(btScheduledTransactionsPage.getStopScheduleMessage().isDisplayed());
	}

	@Step
	public void Yes_Button_Enabled()
	{
		assertTrue(btScheduledTransactionsPage.getYesButton().isEnabled());
		assertTrue(btScheduledTransactionsPage.getYesButton().isDisplayed());
	}

	@Step
	public void Yes_Button_Disable()
	{
		assertFalse(btScheduledTransactionsPage.getYesButton().isEnabled());
		assertTrue(btScheduledTransactionsPage.getYesButton().isDisplayed());
	}

	@Step
	public void Click_on_Yes_Button()
	{
		btScheduledTransactionsPage.getYesButton().click();
	}

	@Step
	public void No_Button_Visible()
	{
		btScheduledTransactionsPage.getYesButton().isDisplayed();
	}

	@Step
	public void Click_No_Button()
	{
		btScheduledTransactionsPage.getYesButton().click();
	}

	@Step
	public void Click_On_CrossIcon()
	{
		btScheduledTransactionsPage.getCrossIconOnStopSchedule().click();
	}

	@Step
	public void CrossIcon_Icon_Displayed()
	{
		btScheduledTransactionsPage.getCrossIconOnStopSchedule().isDisplayed();
	}

	//Validating date format
	@Step
	public void Date_Format_Checker()
	{

		String Str1 = btScheduledTransactionsPage.getDateText().getText();
		//String Str2 = Str1.replace("am", "AM").replace("pm", "PM");

		DateFormat dateeFormater = new SimpleDateFormat("dd MMM yyyy");

		Date d1;
		try
		{
			d1 = dateeFormater.parse(Str1);

		}
		catch (ParseException e)
		{
			assertEquals("Date not matched", "Date Matched");
			e.printStackTrace();
		}
	}

}
