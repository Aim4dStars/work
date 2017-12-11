package feature;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import steps.CompanyDetailsSteps;

public class CompanyDetailsbddSteps
{

	public String feeVal;
	public String nameTextCancelCompany;
	@Steps
	CompanyDetailsSteps companydetailsstpes;

	@Given("I am on Company details page")
	public void i_am_on_company_screen() throws Throwable
	{
		companydetailsstpes.navigatecompanyDetail();
	}

	@When("I navigate to company details screen")
	public void i_am_on_company_details_screen() throws Throwable
	{

		companydetailsstpes.opencompanyDetail();
	}

	@Then("I see edit icon next to editable item for Company and Linked Client")
	public void editCompIconPresent() throws Throwable
	{
		companydetailsstpes.checkClientDetailEditIcon();

	}

	@When("I click on edit icon for company name")
	public void i_click_edit_icon_name_for_company() throws Throwable
	{
		companydetailsstpes.clickEditCompanyName();
	}

	@Then("I am on Edit Company name screen")
	public void i_am_on_edit_name_for_company() throws Throwable
	{
		companydetailsstpes.checkEditClienttitle();
	}

	@Then("I see previously recorded Company name")
	public void i_see_previouse_name_for_company() throws Throwable
	{
		companydetailsstpes.checkPreferredName();
	}

	@When("I enter valid Company name $validName")
	public void i_enter_valid_companyname(String validName) throws Throwable
	{
		companydetailsstpes.enterValidComName(validName);
	}

	@When("I clear the name")
	public void i_clear_companyname() throws Throwable
	{
		companydetailsstpes.clearValidComName();
	}

	@Then("I see only first 50 characters for Company")
	public void i_verify_char_in_company_name() throws Throwable
	{
		companydetailsstpes.checkValidComNameLength();

	}

	@Then("I see disabled updated button for Company")
	public void i_see_disable_button_for_company() throws Throwable
	{
		companydetailsstpes.checkDisabledButton();
	}

	@When("I check the approval checkbox for Company")
	public void i_check_approval_checkbox_for_company() throws Throwable
	{
		companydetailsstpes.checkApprovalCheckbox();
	}

	@Then("I see enabled updated button for Company")
	public void i_see_checkEnabledButton_for_company() throws Throwable
	{
		companydetailsstpes.checkEnabledButton();
	}

	@When("I click on Update button for Company")
	public void i_click_on_update_for_company() throws Throwable
	{
		companydetailsstpes.clickUpdateButton();
	}

	@Then("I see updated name for Company as $updatedName")
	public void i_see_updated_name_for_company(String updatedName) throws Throwable
	{
		companydetailsstpes.verifyUpdateName(updatedName);
	}

	@Then("I see Error Message")
	public void i_see_error_for_company() throws Throwable
	{
		companydetailsstpes.verifyErrorText();
	}

	@When("I click on cancel button for Company")
	public void i_click_on_cancel_for_company() throws Throwable
	{
		companydetailsstpes.clickCancel();
	}

	@When("I click on close button for Company")
	public void i_click_on_close_for_company() throws Throwable
	{
		companydetailsstpes.clickClose();
	}

	@Then("I see no changes in preferred name for Company")
	public void i_see_no_change_for_company() throws Throwable
	{
		companydetailsstpes.verifyNameAfterCanButton();
	}

}
