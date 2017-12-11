package steps;

import static junit.framework.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import pages.clientdetails.LightBoxComponentPage;
import pages.clients.ClientsListPage;
import pages.confirm.ConfirmPage;
import pages.fees.FeeDetailsPage;
import pages.logon.LoginPage;

//@UseTestDataFrom("C:/Sprint/simple-data.csv")
public class ClientsListSteps extends ScenarioSteps
{

	LoginPage loginPage;
	FeeDetailsPage FeeDetailsPage;
	ConfirmPage ConfirmPage;
	ClientsListPage ClientsListPage;
	LightBoxComponentPage lightBoxComponentPage;

	public ClientsListSteps(Pages pages)
	{
		super(pages);
	}

	@Step
	public void navFeeStmt() throws Throwable
	{

		loginPage.gotopage("Client list");
		Thread.sleep(5000);

	}

	@Step
	public void navLoginPage() throws Throwable
	{
		loginPage.open();
		loginPage.doLogon();

	}

	@Step
	public void navMenu() throws Throwable
	{
		loginPage.gotomenu();
		Thread.sleep(5000);

	}

	@Step
	public void clickShowMore()
	{
		ClientsListPage.clickShowMore().click();
	}

	@Step
	public void displayRecords(String name1, String name2) throws Throwable
	{

		List <WebElement> tr_collection = ClientsListPage.getTableRecords();
		int row_num, col_num;
		row_num = 1;
		for (WebElement trElement : tr_collection)
		{
			List <WebElement> td_collection = trElement.findElements(By.xpath("td"));
			col_num = 1;
			boolean brecord = false;
			for (WebElement tdElement : td_collection)
			{
				int i = 0;
				brecord = true;
				if (brecord)
				{
					String[] parts = name1.split(";");
					assertEquals(tdElement.getText(), parts[i]);

				}
				else
				{
					String[] parts = name2.split(";");
					assertEquals(tdElement.getText(), parts[i]);
				}
				i++;
				col_num++;
			}
			row_num++;
		}

	}

	@Step
	public void noRecords() throws Throwable
	{
		ClientsListPage.notableCont();

	}

	@Step
	public void timeRecord() throws Throwable
	{
		//loginPage.gotopage("Client list");
		ClientsListPage.loadTimeCont();

	}

	@Step
	public void printRec() throws Throwable
	{
		//ClientsListPage.prTable();

	}

	@Step
	public List <Map> testCount(List <String> headerName) throws Throwable
	{
		WebElement tableProp = ClientsListPage.getTableRow();
		return loginPage.tableContTest(headerName, tableProp);

	}

	@Step
	public List <Map> testCount1(List <String> headerName) throws Throwable
	{
		WebElement tableProp = ClientsListPage.getTableRowsInsideRow();
		return ClientsListPage.tableContTest1(headerName, tableProp);

	}

	@Step
	public void showRowCount() throws Throwable
	{
		assertTrue(ClientsListPage.getIsRowCount().isDisplayed());
	}

	@Step
	public void showClientSearchField() throws Throwable
	{
		assertTrue(ClientsListPage.getIsClientSearchBox().isDisplayed());
		assertEquals(ClientsListPage.getIsClientSearchBox().getText(), "Search (client or account name)");
	}

	@Step
	public void enterValidSearchClientNameInitials(String validNameInitials) throws Throwable
	{
		ClientsListPage.getIsClientSearchBox().clear();
		ClientsListPage.getIsClientSearchBox().sendKeys(validNameInitials);
	}

	@Step
	public void showRowCountPatternMatch() throws Throwable
	{

		String rowCountText = ClientsListPage.getIsRowCount().getText();

		assertTrue(rowCountText.matches("Showing [1-d] of \\d+?"));
	}

	@Step
	public void showHeaderClientListTable(String Name, String Adviser, String AvailableCash, String PortfolioValue)
		throws Throwable
	{
		assertEquals(Name, ClientsListPage.getIsClientListHeadersList().get(0).getText());
		assertEquals(Adviser, ClientsListPage.getIsClientListHeadersList().get(1).getText());

		assertTrue(ClientsListPage.getIsClientListHeadersList().get(2).getText().contains(AvailableCash));
		//	assertEquals(AvailableCash, ClientsListPage.getIsClientListHeadersList().get(2).getText());
		assertTrue(ClientsListPage.getIsClientListHeadersList().get(3).getText().contains(PortfolioValue));
		//assertEquals(PortfolioValue, ClientsListPage.getIsClientListHeadersList().get(3).getText());
	}

	@Step
	public void clickOnRowToggleIcon() throws Throwable
	{
		ClientsListPage.getIsClientListExpandToggleIcon().get(1).click();
	}

	@Step
	public void clickOnAccountName() throws Throwable
	{
		ClientsListPage.getIsClientListExpandedClientAccount1().click();
	}

	@Step
	public void clickOnClientActionIcon() throws Throwable
	{
		ClientsListPage.getIsClientListActionIconAccountRowExpanded().click();
	}

	@Step
	public void onAccountOverviewScreen(String name) throws Throwable
	{
		//assertTrue(ClientsListPage.getIsAccountOverviewScreen().isDisplayed());
		assertEquals(name, ClientsListPage.getIsAccountOverviewScreen().getText());
	}

	@Step
	public void clickOnCollapsedStateFilterIcon() throws Throwable
	{
		ClientsListPage.getIsCollapsedFilterIcon().click();
	}

	@Step
	public void updatePortfolioValuation(String validPortfolioValuationValue) throws Throwable
	{

		List <WebElement> isDropdownOption = ClientsListPage.getIsDropdownValuesPortfolioValuation();
		for (WebElement option : isDropdownOption)
		{

			if (option.getText().contains(validPortfolioValuationValue))
			{
				option.click();
			}

		}
	}

	@Step
	public void clickUpdateButtonClientList() throws Throwable
	{
		ClientsListPage.getIsUpdateButtonClientList().click();
		//ClientsListPage.waitForRenderedElementsToBePresent(By.cssSelector("a[class='btn-tool btn-filter']"));
		Thread.sleep(5000);
	}

	@Step
	public void seeErrorMessagePortfolio(String Error) throws Throwable
	{
		Thread.sleep(1000);
		assertEquals(Error, ClientsListPage.getIsErrorMessagePortfolio().getText());
	}

	@Step
	public void updateStateValueClientList(String validStateValueClientList) throws Throwable
	{
		List <WebElement> isDropdownOption = ClientsListPage.getIsDropdownValuesStatesClientList();
		for (WebElement option : isDropdownOption)
		{

			if (option.getText().contains(validStateValueClientList))
			{
				option.click();
			}
		}
	}

	@Step
	public void updateAvailableCash(String validAvailableCashValueClientList) throws Throwable
	{

		List <WebElement> isDropdownOption = ClientsListPage.getIsDropdownValuesAvailableCash();
		for (WebElement option : isDropdownOption)
		{

			if (option.getText().contains(validAvailableCashValueClientList))
			{
				option.click();
			}

		}
	}

	@Step
	public void clickOnClientListResetButton() throws Throwable
	{
		ClientsListPage.getIsResetButtonClientList().click();
	}

	@Step
	public void seeFieldsResetClientList() throws Throwable
	{

		assertEquals(ClientsListPage.getIsResetDefaultSelectionPortfolio().getText(), "All ranges");
		assertEquals(ClientsListPage.getIsResetDefaultSelectionAvailableCash().getText(), "All ranges");
		assertEquals(ClientsListPage.getIsResetDefaultSelectionProduct().getText(), "All");
		assertEquals(ClientsListPage.getIsResetDefaultSelectionStates().getText(), "All");
	}

	@Step
	public void clickOnClientListCancelButton() throws Throwable
	{
		ClientsListPage.getIsCancelButtonClientList().click();
	}

	@Step
	public void updateProductValue(String validProductValueClientList) throws Throwable
	{

		List <WebElement> isDropdownOption = ClientsListPage.getIsDropdownValuesProduct();
		for (WebElement option : isDropdownOption)
		{

			if (option.getText().contains(validProductValueClientList))
			{
				option.click();
			}

		}
	}

	@Step
	public void seeGostTextSearchClient(String text) throws Throwable
	{
		assertEquals(text, ClientsListPage.getIsGhostTextClientSearch().getAttribute("placeholder"));
	}

	@Step
	public void dropdownContainingCompleteList() throws Throwable
	{
		List <String> expectedValues = Arrays.asList("Place an order",
			"Transaction history",
			"Account details",
			"Charge one off fee");
		List <String> actualValues = new ArrayList <String>();
		List <WebElement> isDropdownOption = ClientsListPage.getIsClientListBelowDropdownList();
		for (WebElement option : isDropdownOption)
		{

			actualValues.add(option.getText());

		}

		assert actualValues.containsAll(expectedValues);
	}

	@Step
	public void updateSecondDropdownValue(String selectValue) throws Throwable
	{
		List <WebElement> isDropdownOption = ClientsListPage.getIsClientListBelowDropdownList();
		for (WebElement option : isDropdownOption)
		{

			if (option.getText().contains(selectValue))
			{
				option.click();
			}
		}
	}

	@Step
	public void seeFilteredByMessage(String Message) throws Throwable
	{
		assertEquals(Message, ClientsListPage.getIsFilteredByMessage().getText());
	}

	@Step
	public void checkOnClientListPage() throws Throwable
	{
		Thread.sleep(3000);
		assertEquals("Client list", ClientsListPage.getIsClientListHeader().getText());
	}

	@Step
	public void uncheckStatusClientList() throws Throwable
	{
		/*if (null != ClientsListPage.getIsClientListActiveStatusBox()
			&& ClientsListPage.getIsClientListActiveStatusBox().isDisplayed())
		{
			System.out.println("asdawsd");
			System.out.println("asd" + ClientsListPage.getIsClientListActiveClosedAccountsBox().getSize());
			ClientsListPage.getIsClientListActiveStatusBox().click();
			System.out.println("sdfsda");
		}
		System.out.println("adfsqweqwqwa");
		if (null != ClientsListPage.getIsClientListActiveClosedAccountsBox())
		{
			System.out.println("aaaa");
			System.out.println("asd" + ClientsListPage.getIsClientListActiveClosedAccountsBox().getSize());

			//	if (ClientsListPage.getIsClientListActiveClosedAccountsBox().getSize())
			if (ClientsListPage.getIsClientListActiveClosedAccountsBox().isDisplayed())
			{
				System.out.println("a");
				ClientsListPage.getIsClientListActiveClosedAccountsBox().click();
				System.out.println("b");
			}
		}
		System.out.println("123");
		if (null != ClientsListPage.getIsClientListActivePendingRegistrationBox()
			&& ClientsListPage.getIsClientListActivePendingRegistrationBox().isDisplayed())
		{
			System.out.println("e");
			ClientsListPage.getIsClientListActivePendingRegistrationBox().click();
			System.out.println("f");
		}*/

		System.out.println("before 123455::");

		System.out.println("0000");
		ClientsListPage.getIsClientListActiveStatusBoxClick().click();
		ClientsListPage.getIsClientListActiveClosedAccountsBoxClick().click();
		ClientsListPage.getIsClientListActivePendingRegistrationBoxClick().click();
		System.out.println("rtrtrt");

		if (ClientsListPage.getIsClientListActiveStatusBox().getAttribute("aria-checked").equals("true"))
		{
			System.out.println("11111");
			ClientsListPage.getIsClientListActiveStatusBoxClick().click();
			System.out.println("2222");
		}

		if (ClientsListPage.getIsClientListActiveClosedAccountsBox().getAttribute("aria-checked").equals("true"))
		{
			System.out.println("33333");
			ClientsListPage.getIsClientListActiveClosedAccountsBoxClick().click();
			System.out.println("44444");
		}

		if (ClientsListPage.getIsClientListActivePendingRegistrationBox().getAttribute("aria-checked").equals("true"))
		{
			System.out.println("55555");
			ClientsListPage.getIsClientListActivePendingRegistrationBoxClick().click();
		}
		System.out.println("zzzz");

	}

	@Step
	public void checkNoFilterMessage(String noFilterMessage) throws Throwable
	{
		assertEquals(noFilterMessage, ClientsListPage.getIsFilteredByMessage().getText());
	}

	@Step
	public void checkAddClientLink() throws Throwable
	{
		assertTrue(ClientsListPage.getIsAddClientButtonClientList().isDisplayed());
	}

	@Step
	public void checkCountClientListRecords(int Count) throws Throwable
	{

		/*System.out.println("123!!" + ClientsListPage.getIsClientListRecordsList().get(1));
		System.out.println("123!!" + ClientsListPage.getIsClientListRecordsList().get(2));
		System.out.println("123!!" + ClientsListPage.getIsClientListRecordsList().get(3));*/

		//System.out.println("hemakumar:" + ClientsListPage.getIsClientListRecordsList().size());
		assertEquals(Count, ClientsListPage.getIsClientListRecordsList().size());
	}

	@Step
	public void clickSeeMoreButtonClientList() throws Throwable
	{
		ClientsListPage.getIsSeeMoreButton().click();
	}

	@Step
	public void checkAfterShowMoreClickCountClientListRecords(int Count) throws Throwable
	{

		if (ClientsListPage.getIsClientListRecordsList().size() <= Count)
		{
			assertEquals("Matched", "Matched");
		}
		else
			assertEquals("Matched", "Not Matched");
	}

	@Step
	public void clickSeeMoreButtonClientListTillPresent() throws Throwable
	{
		System.out.println("ccc");
		String seeMoreText = ClientsListPage.getIsSeeMoreButton().getText();
		System.out.println("bbb");
		while (seeMoreText == "Show more")
		{
			System.out.println("aaa");
			ClientsListPage.getIsSeeMoreButtonClick().click();
		}

	}

	@Step
	public void checkMessageAfterRecordsClientList() throws Throwable
	{
		assertEquals("Matched", "Not Matched");
	}

	@Step
	public void clickAddClientButtonClientList() throws Throwable
	{
		ClientsListPage.getIsAddClientButton().click();
	}

	@Step
	public void checkOnDestinationScreen(String screenName) throws Throwable
	{
		assertEquals(screenName, ClientsListPage.getIsAccountOverviewScreen().getText());
	}

	@Step
	public void dropdownContainingCompleteListFiveOptions() throws Throwable
	{
		List <String> expectedValues = Arrays.asList("Client name",
			"Available cash ascending",
			"Available cash descending",
			"Portfolio value ascending",
			"Portfolio value descending");
		List <String> actualValues = new ArrayList <String>();

		List <WebElement> isDropdownOption = ClientsListPage.getisOptionsInDropdownValuesOfSortBy();

		for (WebElement option : isDropdownOption)
		{

			actualValues.add(option.getText());
		}

		assert actualValues.containsAll(expectedValues);
	}

	@Step
	public void updateFirstDropdownValue(String Option) throws Throwable
	{
		List <WebElement> isDropdownOption = ClientsListPage.getisOptionsInDropdownValuesOfSortBy();
		for (WebElement option : isDropdownOption)
		{

			if (option.getText().contains(Option))
			{
				option.click();
			}
		}
	}

	@Step
	public void checkDropdownValuesPortfolioValuation(String Value1, String Value2, String Value3, String Value4, String Value5,
		String Value6, String Value7) throws Throwable
	{
		List <String> expectedValues = Arrays.asList(Value1, Value2, Value3, Value4, Value5, Value6, Value7);
		List <String> actualValues = new ArrayList <String>();

		List <WebElement> isDropdownOption = ClientsListPage.getIsDropdownValuesPortfolioValuation();

		for (WebElement option : isDropdownOption)
		{

			actualValues.add(option.getText());

		}

		//assert actualValues.containsAll(expectedValues);
		assertEquals(expectedValues, actualValues);

	}

	@Step
	public void checkDropdownValuesAvailableCash(String Value1, String Value2, String Value3, String Value4, String Value5,
		String Value6, String Value7) throws Throwable
	{
		List <String> expectedValues = Arrays.asList(Value1, Value2, Value3, Value4, Value5, Value6, Value7);
		List <String> actualValues = new ArrayList <String>();

		List <WebElement> isDropdownOption = ClientsListPage.getIsDropdownValuesAvailableCash();

		for (WebElement option : isDropdownOption)
		{

			actualValues.add(option.getText());

		}

		//assert actualValues.containsAll(expectedValues);
		assertEquals(expectedValues, actualValues);

	}

	@Step
	public void checkDropdownValuesStates(String Value1, String Value2, String Value3, String Value4, String Value5,
		String Value6, String Value7, String Value8, String Value9, String Value10, String Value11) throws Throwable
	{
		List <String> expectedValues = Arrays.asList(Value1,
			Value2,
			Value3,
			Value4,
			Value5,
			Value6,
			Value7,
			Value8,
			Value9,
			Value10,
			Value11);
		List <String> actualValues = new ArrayList <String>();

		List <WebElement> isDropdownOption = ClientsListPage.getIsDropdownValuesStatesClientList();

		for (WebElement option : isDropdownOption)
		{

			actualValues.add(option.getText());

		}

		//assert actualValues.containsAll(expectedValues);
		assertEquals(expectedValues, actualValues);

	}

	@Step
	public void checBoxesCheckClientList() throws Throwable
	{
		assertTrue(ClientsListPage.getIsClientListActiveStatusBoxClick().isDisplayed());
		assertTrue(ClientsListPage.getIsClientListActiveClosedAccountsBoxClick().isDisplayed());
		assertTrue(ClientsListPage.getIsClientListActivePendingRegistrationBoxClick().isDisplayed());
	}

	@Step
	public void checkActiveBoxChecked() throws Throwable
	{
		assertTrue(ClientsListPage.getIsDefaultClientListActiveStatusBox().isDisplayed());

	}

	@Step
	public void checkPortfolioValuationDefault(String defaultValue) throws Throwable
	{

		WebElement isDefaultValuePortfolio2 = ClientsListPage.getIsDropdownValuesPortfolioValuation().get(1);
		assertEquals(defaultValue, isDefaultValuePortfolio2);
	}

	@Step
	public void checkAvaliableCashDefault(String defaultValue) throws Throwable
	{

		WebElement isDefaultAvailableCash = ClientsListPage.getIsDropdownValuesAvailableCash().get(1);
		assertEquals(defaultValue, isDefaultAvailableCash);
	}

	@Step
	public void checkStateDefault(String defaultValue) throws Throwable
	{

		WebElement isDefaulState = ClientsListPage.getIsDropdownValuesStatesClientList().get(1);
		assertEquals(defaultValue, isDefaulState);
	}

	@Step
	public void checkPortfolioValueInRecords(String comparisonValue) throws Throwable
	{

		String Value = ClientsListPage.getIsClientListTableFirstRecordDetails().get(3).getText();
		String trimmedValue = Value.replaceAll(",", "");

		BigDecimal decimal = new BigDecimal(trimmedValue);
		BigDecimal compareValue = new BigDecimal(comparisonValue);

		if (decimal.compareTo(compareValue) == 0 | decimal.compareTo(compareValue) == -1)
		{
			assertEquals("Matched", "UnMatched");
		}
		else
			assertTrue(decimal.compareTo(compareValue) == 1);
	}

}
