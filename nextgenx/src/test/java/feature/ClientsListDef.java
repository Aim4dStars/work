package feature;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;

import steps.ClientsListSteps;

public class ClientsListDef
{

	public String feeVal;
	private ExamplesTable table;
	int tableStart = 0;
	int tableEnd = 4;
	String[] expectedResult = new String[4];

	@Steps
	ClientsListSteps adviser;

	@When("I navigate to Clients List screen")
	public void i_am_on_fees_screen() throws Throwable
	{
		adviser.navFeeStmt();

	}

	@Given("I am logged in as adviser")
	public void i_am_on_login_screen() throws Throwable
	{
		adviser.navLoginPage();
	}

	@When("I navigate to client list")
	public void navMenu() throws Throwable
	{

		adviser.navMenu();
		Thread.sleep(3000);

	}

	@Then("I get client records $name and $value")
	public void disRecords(String name, String value) throws Throwable
	{

		adviser.displayRecords(name, value);

	}

	@Then("I should not get records")
	public void noRecords() throws Throwable
	{

		adviser.noRecords();

	}

	@Then("I should load the screen within 5 seconds")
	public void loadTime() throws Throwable
	{

		adviser.timeRecord();

	}

	@Then("I get client records is: $activityTable")
	public void theTraderActivityIs(ExamplesTable activityTable) throws Throwable
	{

		int rownum = activityTable.getRowCount();

		List <String> headerName = activityTable.getHeaders();
		List <Map> tableActual = adviser.testCount(headerName);
		for (int i = 0; i < rownum; i++)
		{
			Map <String, String> expected = activityTable.getRow(i);
			Map <String, String> actual = tableActual.get(i);

			for (String key : expected.keySet())
			{
				if (key.equalsIgnoreCase("name"))
				{

					assertTrue(actual.get(key).contains(expected.get("name")));

				}
				else
				{

					assertEquals(actual.get(key), expected.get(key));
				}
			}
		}

	}

	@Then("I get expanded row details is: $activityTable")
	public void theExpandedRowDetailsIs(ExamplesTable activityTable) throws Throwable
	{

		int rownum = activityTable.getRowCount();

		List <String> headerName = activityTable.getHeaders();
		List <Map> tableActual = adviser.testCount1(headerName);
		for (int i = 0; i < rownum; i++)
		{
			Map <String, String> expected = activityTable.getRow(i);
			Map <String, String> actual = tableActual.get(i);

			for (String key : expected.keySet())
			{
				assertEquals(actual.get(key), expected.get(key));

			}
		}

	}

	@Then("print records")
	public void showRecords() throws Throwable
	{
		adviser.printRec();

	}

	@Then("I see Row counter showing n-m of r")
	public void showRowCount() throws Throwable
	{
		adviser.showRowCount();
	}

	@Then("I see only one search field having Search text input area and ghost text Search client or account name")
	public void showClientSearchField() throws Throwable
	{
		adviser.showClientSearchField();
	}

	@When("I type in the search field as $validNameInitials")
	public void i_enter_valid_name(String validNameInitials) throws Throwable
	{
		adviser.enterValidSearchClientNameInitials(validNameInitials);
	}

	@Then("I see row number indicator Showing n-m of r pattern")
	public void showRowCountPatternMatch() throws Throwable
	{
		adviser.showRowCountPatternMatch();
	}

	@Then("I see Client List table headers as $Name and $Adviser and $AvailableCash and $PortfolioValue")
	public void showHeaderClientListTable(String Name, String Adviser, String AvailableCash, String PortfolioValue)
		throws Throwable
	{
		adviser.showHeaderClientListTable(Name, Adviser, AvailableCash, PortfolioValue);
	}

	@When("I click on the row expand toggle icon")
	public void i_click_on_row_toggle_icon() throws Throwable
	{
		adviser.clickOnRowToggleIcon();
	}

	@When("I click on the account name")
	public void i_click_on_account_name() throws Throwable
	{
		adviser.clickOnAccountName();
	}

	@When("I click on the client action icon on the account row")
	public void i_click_on_action_icon_row() throws Throwable
	{
		adviser.clickOnClientActionIcon();
	}

	@Then("I am on Account Overview screen wih header name as $name")
	public void onAccountOverviewScreen(String name) throws Throwable
	{
		adviser.onAccountOverviewScreen(name);
	}

	@Then("I see dropdown containing list of available 4 necessary options")
	public void dropdownContainingCompleteList() throws Throwable
	{
		adviser.dropdownContainingCompleteList();
	}

	@When("I select dropdown as $Options")
	public void updateSecondDropdownValue(String Options) throws Throwable
	{
		adviser.updateSecondDropdownValue(Options);
	}

	@When("I click on collapsed state filter button")
	public void i_click_on_collapsed_state_filter_icon() throws Throwable
	{
		adviser.clickOnCollapsedStateFilterIcon();
	}

	@When("I select Portfolio Valuation value as $PortfolioValuation")
	public void updatePortfolioValuation(String validPortfolioValuationValue) throws Throwable
	{
		adviser.updatePortfolioValuation(validPortfolioValuationValue);
	}

	@When("I click on Update Button for Client List")
	public void i_click_update_button_client_list() throws Throwable
	{
		adviser.clickUpdateButtonClientList();
	}

	@Then("I see the error message for no records displayed for client list $Error")
	public void seeErrorMessagePortfolio(String Error) throws Throwable
	{
		adviser.seeErrorMessagePortfolio(Error);
	}

	@When("I select State value as $validStateValueClientList")
	public void updateStateValueClientList(String validStateValueClientList) throws Throwable
	{
		adviser.updateStateValueClientList(validStateValueClientList);
	}

	@When("I select Available cash value as $AvailableCash")
	public void updateAvailableCash(String validAvailableCashValueClientList) throws Throwable
	{
		adviser.updateAvailableCash(validAvailableCashValueClientList);
	}

	@When("I click on the Reset Button")
	public void i_click_on_reset_button() throws Throwable
	{
		adviser.clickOnClientListResetButton();
	}

	@Then("I see fields reset to system default")
	public void seeFieldsResetClientList() throws Throwable
	{
		adviser.seeFieldsResetClientList();
	}

	@When("I click on the Cancel Button")
	public void i_click_on_cancel_button() throws Throwable
	{
		adviser.clickOnClientListCancelButton();
	}

	@When("I select Product value as $ProductValue")
	public void updateProductValue(String validProductValueClientList) throws Throwable
	{
		adviser.updateProductValue(validProductValueClientList);
	}

	@Then("I see only one search field having Search text input area and ghost text as $text")
	public void seeGostTextSearchClient(String text) throws Throwable
	{
		adviser.seeGostTextSearchClient(text);
	}

	@Then("I see Filtered by message $Message")
	public void seeFilteredByMessage(String Message) throws Throwable
	{
		adviser.seeFilteredByMessage(Message);
	}

	@Then("I am on the client list page")
	public void on_client_list_page() throws Throwable
	{
		adviser.checkOnClientListPage();
	}

	@When("I uncheck all the status in the client list")
	public void i_uncheck_status_client_list() throws Throwable
	{
		adviser.uncheckStatusClientList();
	}

	@Then("I see filtered by message as $message")
	public void filterd_bymessage_client_list_page(String noFilterMessage) throws Throwable
	{
		adviser.checkNoFilterMessage(noFilterMessage);
	}

	@Then("I see 'Add Client' link")
	public void i_see_add_client_link() throws Throwable
	{
		adviser.checkAddClientLink();
	}

	@Then("I see number of client records displayed as $Count")
	public void i_see_number_of_clinet_records(int Count) throws Throwable
	{
		adviser.checkCountClientListRecords(Count);
	}

	@When("I click on 'SEE MORE' button")
	public void i_click_see_more_button_client_list() throws Throwable
	{
		adviser.clickSeeMoreButtonClientList();
	}

	@Then("I see more client records displayed as less than or equal to $Count")
	public void i_see_number_of_more_clientt_records(int Count) throws Throwable
	{
		adviser.checkAfterShowMoreClickCountClientListRecords(Count);
	}

	@When("I click on show more till all the records get displayed")
	public void i_click_see_more_button_client_list_till_present() throws Throwable
	{
		adviser.clickSeeMoreButtonClientListTillPresent();
	}

	@Then("I see message There are no more results to display at the bottom of the list")
	public void i_see_no_clientS_display_message_bottom() throws Throwable
	{
		adviser.checkMessageAfterRecordsClientList();
	}

	@When("I select the Add Client link")
	public void i_select_add_cleient_button() throws Throwable
	{
		adviser.clickAddClientButtonClientList();
	}

	@Then("I am on destination screen as $screenName")
	public void i_am_on_destination_screen(String screenName) throws Throwable
	{
		adviser.checkOnDestinationScreen(screenName);
	}

	@Then("I see sort order dropdown containing list of available 5 necessary options")
	public void firstDropdownContainingCompleteListFiveOptions() throws Throwable
	{
		adviser.dropdownContainingCompleteListFiveOptions();
	}

	@When("I select the sort by dropdown option as $Option")
	public void updateFirstDropdownValueClientList(String Option) throws Throwable
	{
		adviser.updateFirstDropdownValue(Option);
	}

	@Then("I see Portfolio valuation dropdown list with the necessary 7 options as $Value1 and $Value2 and $Value3 and $Value4 and $Value5 and $Value6 and $Value7")
	public void checkDropdownValuesPortfolioValuation(String Value1, String Value2, String Value3, String Value4, String Value5,
		String Value6, String Value7) throws Throwable
	{
		adviser.checkDropdownValuesPortfolioValuation(Value1, Value2, Value3, Value4, Value5, Value6, Value7);
	}

	@Then("I see Available cash dropdown list with the necessary 7 options as $Value1 and $Value2 and $Value3 and $Value4 and $Value5 and $Value6 and $Value7")
	public void checkDropdownValuesAvailableCash(String Value1, String Value2, String Value3, String Value4, String Value5,
		String Value6, String Value7) throws Throwable
	{
		adviser.checkDropdownValuesAvailableCash(Value1, Value2, Value3, Value4, Value5, Value6, Value7);
	}

	@Then("I see State drop down list with necessary eleven options as $Value1 and $Value2 and $Value3 and $Value4 and $Value5 and $Value6 and $Value7 and $Value8 and $Value9 and $Value10 and $Value11")
	public void checkDropdownValuesStates(String Value1, String Value2, String Value3, String Value4, String Value5,
		String Value6, String Value7, String Value8, String Value9, String Value10, String Value11) throws Throwable
	{
		adviser.checkDropdownValuesStates(Value1,
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
	}

	@Then("I see Status check boxes with options Active and Closed accounts and Pending registration")
	public void checBoxesCheckClientList() throws Throwable
	{
		adviser.checBoxesCheckClientList();
	}

	@Then("I see Active option is checked")
	public void checkActiveBoxChecked() throws Throwable
	{
		adviser.checkActiveBoxChecked();
	}

	@Then("I see Portfolio valuation dropdown default value as $defaultValue")
	public void checkPortfolioValuationDefault(String defaultValue) throws Throwable
	{
		adviser.checkPortfolioValuationDefault(defaultValue);
	}

	@Then("I see Available cash dropdown default value as $defaultValue")
	public void checkAvalibaleCashDefault(String defaultValue) throws Throwable
	{
		adviser.checkAvaliableCashDefault(defaultValue);
	}

	@Then("I see State drop down list with default value as $defaultValue")
	public void checkStateDefault(String defaultValue) throws Throwable
	{
		adviser.checkStateDefault(defaultValue);
	}

	@Then("I see that the Portfolio Valuation of the displayed record is More than $comparisonValue")
	public void checkPortfolioValueInRecords(String comparisonValue) throws Throwable
	{
		adviser.checkPortfolioValueInRecords(comparisonValue);
	}

}
