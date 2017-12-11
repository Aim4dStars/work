package feature;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Pending;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import steps.TrustDetailsSteps;

public class TrustDetailsBddSteps
{

	public String registerGSTValue;
	public String nameGSTCancel;

	@Steps
	TrustDetailsSteps trustdetailssteps;

	@When("I navigate to Trust Individual screen")
	public void i_am_on_trust_details_screen() throws Throwable
	{

		trustdetailssteps.openTrustDetail();
	}

	@Then("I see edit icon next to editable item for Trust and Linked Client and Beneficiary details")
	public void edittrustIconPresent() throws Throwable
	{
		trustdetailssteps.checkTrustDetailIndEditIcon();

	}

	@Given("I am on Trust detail Individual page")
	public void i_am_on_trust_ind_screen() throws Throwable
	{
		trustdetailssteps.navigateTrustInd();

	}

	@When("I click on edit icon for Registered for GST")
	public void i_click_edit_icon_gst_trust_ind() throws Throwable
	{
		trustdetailssteps.clickEditClientName();

	}

	@When("I click on edit icon for Registration State")
	public void i_click_edit_icon_regstate_trust_ind() throws Throwable
	{
		trustdetailssteps.clickRegistrationState();

	}

	@Then("I am on Edit Registration State screen")
	public void i_am_on_edit_reg() throws Throwable
	{
		trustdetailssteps.checkEditRegistrationStateScreen();
	}

	@Then("I see complete list of eight austalian states")
	public void i_see_complete_list_states() throws Throwable
	{
		trustdetailssteps.checkEditRegistrationStateValues();
	}

	@Then("I am on Edit Register for GST screen")
	public void i_am_on_edit_gst() throws Throwable
	{
		trustdetailssteps.checkEditRegisteredGSTTitle();
	}

	@Then("I see disabled updated button")
	public void i_see_disabled_updated_button_for_gst() throws Throwable
	{
		trustdetailssteps.checkGSTDisabledButton();
	}

	@When("I check the approval checkbox")
	public void i_check_approval_checkbox_gst() throws Throwable
	{
		trustdetailssteps.checkGSTApprovalCheckbox();
	}

	@Then("I see enabled updated button")
	public void i_see_enabled_button_gst() throws Throwable
	{
		trustdetailssteps.checkGSTEnabledButton();
	}

	@When("I update Registration for GST")
	public void i_update_reg_gst() throws Throwable
	{
		trustdetailssteps.updateRegistrationGST();

	}

	@When("I click on Update button")
	public void i_click_update_button_gst() throws Throwable
	{
		trustdetailssteps.clickGSTUpdateButton();
	}

	@Then("I see updated value for Registration for GST")
	public void i_see_updated_value_gst() throws Throwable
	{
		trustdetailssteps.verifyGSTUpdated();
	}

	@When("I click on cancel button gst")
	public void i_click_on_cancel_gst() throws Throwable
	{
		trustdetailssteps.clickGSTCancel();
	}

	@When("I click on registraion state cancel button")
	public void i_click_on_cancel_registraionState() throws Throwable
	{
		trustdetailssteps.clickRegistrationStateCancel();
	}

	@When("I click on close button gst")
	public void i_click_on_close_gst() throws Throwable
	{
		trustdetailssteps.clickGSTClose();
	}

	@When("I click on registraion state close button")
	public void i_click_on_close_registraionState() throws Throwable
	{
		trustdetailssteps.clickRegistrationStateClose();
	}

	@Then("I see no changes in value for Registered for GST")
	public void verifyGSTNameAfterCanButton() throws Throwable
	{
		trustdetailssteps.verifyGSTNameAfterCanButton();
	}

	@When("I click on the dropdown icon")
	public void i_click_on_dropdown_icon() throws Throwable
	{
		trustdetailssteps.clickDropDownIcon();
	}

	@Then("I see no changes in value for Registration State")
	public void I_see_no_change_in_value() throws Throwable
	{
		trustdetailssteps.verifyRegStateNameAfterCanButton();
	}

	@When("I update Registration State as $RegistrationState")
	public void I_update_registraion_state(String validState) throws Throwable
	{

		trustdetailssteps.updateRegistraionState(validState);
	}

	@Then("I see updated value for Registration State as $RegistrationState")
	public void I_see_registraion_state(String validState) throws Throwable
	{
		trustdetailssteps.updatedRegistraionStateValue(validState);
	}

	@When("I click on edit icon for tax options")
	public void i_click_edit_icon_tax_options_trust_ind() throws Throwable
	{
		trustdetailssteps.clickEditTaxOption();
	}

	@Then("I am on Edit tax options screen")
	public void i_am_on_edit_tax_options_screen() throws Throwable
	{
		trustdetailssteps.checkEditTaxOptionsScreen();
	}

	@Then("I see complete list of three options which I can select")
	public void i_see_complete_list_tax_options() throws Throwable
	{
		trustdetailssteps.checkEditTaxOptionsValues();
	}

	@Then("I see the input field for TFN Number")
	public void i_see_input_field_for_TFN() throws Throwable
	{
		trustdetailssteps.checkInputFieldTFNNumber();
	}

	@When("I enter valid 8 or 9 digit TFN number in the input field $validTFNNumber")
	public void i_enter_valid_name(String validNumber) throws Throwable
	{
		trustdetailssteps.enterValidTFNNumber(validNumber);

	}

	@Pending
	@Then("I see Tax File Number provided for tax options")
	public void i_see_TFN_provided_on_screen() throws Throwable
	{
		trustdetailssteps.checkTFNNumberProvidedOnScreen();
	}

	@When("I enter invalid TFN number in the input field $invalidTFNNumber")
	public void i_enter_invalid_name(String invalidNumber) throws Throwable
	{
		trustdetailssteps.enterInvalidTFNNumber(invalidNumber);

	}

	@Then("I see error message ERR.0093 Please enter a valid 8 or 9-digit tax file number")
	public void i_see_error_message_ERR0093_screen() throws Throwable
	{
		trustdetailssteps.checkErrorMessageTFN();
	}

	@Then("I see the necessary display message on the screen")
	public void i_see_display_message_on_screen() throws Throwable
	{
		trustdetailssteps.checkDoNotQuoteMessageOnScreen();
	}

	@When("I update tax options to first option as $TFN")
	public void I_update_tax_options(String TFN) throws Throwable
	{

		trustdetailssteps.updateTaxOptonTFN(TFN);
	}

	@When("I update tax options to third option as $noTFN")
	public void I_update_tax_options_no_TFN(String noTFN) throws Throwable
	{

		trustdetailssteps.updateTaxOptonDoNotQuoteTFN(noTFN);
	}

	@Then("I see Tax File Number or exemption not provided for tax options")
	public void i_see_no_TFN_provided_on_screen() throws Throwable
	{
		trustdetailssteps.checkTFNNumberNotProvidedOnScreen();
	}

	@Then("I see no changes in value for tax options")
	public void I_see_no_change_in_value_for_tax_options() throws Throwable
	{
		trustdetailssteps.verifyTaxOptionsAfterCanButton();
	}

	@When("I click on Tax Options cancel button")
	public void i_click_on_cancel_Tax_Options() throws Throwable
	{
		trustdetailssteps.clickTaxOptionsCancel();
	}

	@When("I click on Tax Options close button")
	public void i_click_on_close_Tax_Options() throws Throwable
	{
		trustdetailssteps.clickTaxOptionsClose();
	}

	@When("I update tax options to second option as $Reason")
	public void I_update_tax_options_Reason(String Reason) throws Throwable
	{

		trustdetailssteps.updateTaxOptionReason(Reason);
	}

	@Then("I see Exemption reason provided for tax options")
	public void i_see_exemption_reason_provided_on_screen() throws Throwable
	{
		trustdetailssteps.checkExemptionReasonProvidedOnScreen();
	}

	@Then("I see the input field dropdown having complete list of eight exemption reasons")
	public void i_see_complete_list_exemption_reasons() throws Throwable
	{
		trustdetailssteps.checkSecondDropdownValues();
	}

	@When("I update the Exemption Reason from the second dropdown as $ExmReason")
	public void I_update_tax_options_Reason_Second_Dropdown(String ExmReason) throws Throwable
	{

		trustdetailssteps.updateTaxOptionReasonSecondDropdown(ExmReason);
	}

	@When("I click on the second dropdown icon")
	public void i_click_on_second_dropdown_icon() throws Throwable
	{
		trustdetailssteps.clickSecondDropDownIcon();
	}

}