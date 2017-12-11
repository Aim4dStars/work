package feature;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Pending;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import steps.ClientDetailsSteps;

public class ClientDetailsbddSteps
{

	public String feeVal;

	@Steps
	ClientDetailsSteps clientdetailsstpes;

	@Given("I am on Login Page")
	public void i_am_on_Login_Page_screen() throws Throwable
	{
		clientdetailsstpes.starts_logon_clientdetails();

	}

	@When("I navigate to Client details and preferences")
	public void i_am_on_client_details_screen() throws Throwable
	{

		clientdetailsstpes.openClientDetail();

	}

	@Then("I see edit icon next to editable item")
	public void editIconPresent()
	{

		clientdetailsstpes.checkClientDetailEditIcon();

	}

	@Given("I am on Client details and preferences")
	public void i_am_on_client_screen() throws Throwable
	{
		clientdetailsstpes.navigateClientDetails();

	}

	@When("I click on edit icon for preferred name")
	public void i_click_edit_icon_name_for_client() throws Throwable
	{
		clientdetailsstpes.clickClientDetailEditIcon();

	}

	@Then("I am on Edit preferred name screen")
	public void i_am_on_edit_name() throws Throwable
	{
		clientdetailsstpes.checkEditClienttitle();

	}

	@Then("I see previously recorded name")
	public void i_see_previouse_name() throws Throwable
	{
		clientdetailsstpes.checkPreferredName();

	}

	@When("I enter valid name $validName")
	public void i_enter_valid_name(String validName) throws Throwable
	{
		clientdetailsstpes.enterValidName(validName);

	}

	@Then("I see only first 50 characters")
	public void i_verify_char_in_name() throws Throwable
	{
		clientdetailsstpes.checkValidNameLength();

	}

	@Pending
	@Then("I see updated name as $updatedName")
	public void i_see_updated_name_for_client(String updatedName) throws Throwable
	{
		clientdetailsstpes.verifyUpdateName(updatedName);

	}

	@When("I click on cancel button")
	public void i_click_on_cancel_for_client() throws Throwable
	{
		clientdetailsstpes.clickCancel();

	}

	@When("I click on close button")
	public void i_click_on_close_for_client() throws Throwable
	{
		clientdetailsstpes.clickClose();

	}

	@Then("I see no changes in preferred name")
	public void i_see_no_change_for_client() throws Throwable
	{
		clientdetailsstpes.verifyNameAfterCanButton();

	}

	@When("I click on edit icon on the Australian resident for tax purposes field")
	public void i_click_edit_icon_Country_Of_Residence() throws Throwable
	{
		clientdetailsstpes.clickClientDetailCountryOfResidenceEditIcon();

	}

	@Then("I am on Edit Country of residence screen")
	public void i_am_on_edit_country_of_residence() throws Throwable
	{
		clientdetailsstpes.checkEditCountryOfResidenceScreen();
	}

	@Then("I see complete list of countries which I can select")
	public void i_see_complete_list_country_of_residence() throws Throwable
	{
		clientdetailsstpes.checkEditCountryOfResidenceList();
	}

	@When("I click on cancel button for country of residence")
	public void i_click_on_cancel_Country() throws Throwable
	{
		clientdetailsstpes.clickCountryOfResidenceCancel();
	}

	@When("I click on close button for country of residence")
	public void i_click_on_close_Country() throws Throwable
	{
		clientdetailsstpes.clickCountryOfResidenceClose();
	}

	@When("I update Country of residence as $Country")
	public void I_update_country_of_residence(String validCountry) throws Throwable
	{

		clientdetailsstpes.updateCountryOfResidence(validCountry);
	}

	@Then("I see updated value for Country of residence as $Country")
	public void I_see_updated_value_country_of_residence(String validCountry) throws Throwable
	{
		clientdetailsstpes.updatedCountryOfResidenceValue(validCountry);
	}

	@Then("I see no changes in value for Country of residence")
	public void I_see_no_change_in_value_Country_Of_Residence() throws Throwable
	{
		clientdetailsstpes.verifyCountryOfResidenceNameAfterCanButton();
	}

	@When("I click on edit icon for Contact Details")
	public void I_click_edit_icon_Contact_Details() throws Throwable
	{
		clientdetailsstpes.clickContactDetailsEditIcon();

	}

	@Then("I am on Edit Contact Details screen")
	public void i_am_on_edit_contact_details_screen() throws Throwable
	{
		clientdetailsstpes.checkEditContactDetailsScreen();
	}

	@Then("I see uneditable primary email address and primary mobile number field and editable secondary email address field")
	public void i_see_uneditable_primary_email_primary_number_secondaryemail() throws Throwable
	{
		clientdetailsstpes.checkUneditableMobileAndEmail();

	}

	@Then("I see a dropdown containing a list of four to add more field types")
	public void i_see_dropdown_list_add_fields() throws Throwable
	{
		clientdetailsstpes.checkAddFieldContactDetailsList();
	}

	@When("I enter a valid secondary email address as $ValidEmail")
	public void I_update_secondary_email_address(String validEmail) throws Throwable
	{

		clientdetailsstpes.updateSecondaryEmailContactDetails(validEmail);
	}

	@Then("I see updated value for secondary email address as $ValidEmail")
	public void I_see_updated_value_secondary_email_address(String validEmail) throws Throwable
	{
		clientdetailsstpes.updatedSecondaryEmailContactDetails(validEmail);
	}

	@When("I enter a invalid secondary email address as $InvalidEmail")
	public void I_update_secondary_email_address_invalid(String InvalidEmail) throws Throwable
	{

		clientdetailsstpes.updateSecondaryEmailContactDetailsInvalid(InvalidEmail);
	}

	@Then("I see Error message Please enter a valid email address")
	public void i_see_error_message_invalid_email_screen() throws Throwable
	{
		clientdetailsstpes.checkErrorMessageInvalidEmailContactDetails();
	}

	@When("I select Home number in the add field dropdown as $Home")
	public void I_select_home_number_dropdown_options(String homenumber) throws Throwable
	{

		clientdetailsstpes.updateAddFieldHomeSelectContactDetails(homenumber);
	}

	@When("I enter Valid 8 digit Home number in the input field $validHomeNumber")
	public void i_enter_valid_home_number(String validHomeNumber) throws Throwable
	{
		clientdetailsstpes.enterValidHomeNumber(validHomeNumber);

	}

	@Then("I see updated value for Home number as $ValidHomeNumber")
	public void I_see_updated_value_home_number(String validHomeNumber) throws Throwable
	{
		clientdetailsstpes.updatedHomeNumberContactDetails(validHomeNumber);
	}

	@When("I enter number which is 10 characters AND first two characters are valid area codes 02,03,07,08 as $valid10DigitHomeNumber")
	public void i_enter_valid_home_number_10_charachter_long(String valid10DigitHomeNumber) throws Throwable
	{
		clientdetailsstpes.enterValid10DigitHomeNumber(valid10DigitHomeNumber);

	}

	@When("I enter invalid number with less than 8 characters after removing spaces as $invalidHomeNumber")
	public void i_enter_invalid_home_number_lessthan_8charachter_long(String invalidHomeNumber) throws Throwable
	{
		clientdetailsstpes.enterInvalidHomeNumber(invalidHomeNumber);

	}

	@When("I enter Valid 8 digit Work number in the input field as $validWorkNumber")
	public void i_enter_valid_8digit_work_number(String validWorkNumber) throws Throwable
	{
		clientdetailsstpes.enterValidWorkNumber(validWorkNumber);
	}

	@Then("I see updated value for Work number as $ValidWorkNumber")
	public void I_see_updated_value_work_number(String validWorkNumber) throws Throwable
	{
		clientdetailsstpes.updatedWorkNumberContactDetails(validWorkNumber);
	}

	@When("I enter work number which is 10 characters AND first two characters are valid area codes as $valid10DigitWorkNumber")
	public void i_enter_valid_work_number_10_charachter_long(String valid10DigitWorkNumber) throws Throwable
	{
		clientdetailsstpes.enterValid10DigitWorkNumber(valid10DigitWorkNumber);

	}

	@Then("I see a no add field option being available on the screen")
	public void i_see_no_add_field() throws Throwable
	{
		clientdetailsstpes.checkAddFieldNotDisplayed();
	}

	@Then("I see no changes in value for Contact Details")
	public void I_see_no_change_in_value_for_contact_details() throws Throwable
	{
		clientdetailsstpes.verifyContactDetailsAfterCanButton();
	}

	@When("I click on edit icon for address")
	public void I_click_edit_icon_address() throws Throwable
	{
		clientdetailsstpes.clickResidentialAddressEditIcon();
	}

	@Then("I am on Edit address screen")
	public void i_am_on_edit_address_screen() throws Throwable
	{
		clientdetailsstpes.checkEditAddressScreen();
	}

}
