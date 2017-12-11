package feature;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import steps.BTAccountAndBillersModalSteps;
import steps.BTAccountAndBillersSteps;

public class BTAccountAndBillersModal
{

	@Steps
	BTAccountAndBillersModalSteps onBTAccountAndBillersModalSteps;
	BTAccountAndBillersSteps onBTAccountAndBillersSteps;

	@Given("I login into panorama system as Investor")
	public void navigateToAccountsAndBillers() throws Throwable
	{
		onBTAccountAndBillersSteps.navigate_To_Accounts_And_Billers();
	}

	@When("I go to the Move money")
	public void navigateToMoveMoney() throws Throwable
	{
		onBTAccountAndBillersSteps.navigateToMoveMoney();
	}

	@When("I navigate to 'Accounts and billers' tab")
	public void clickTabAccAndBillers() throws Throwable
	{
		onBTAccountAndBillersSteps.clickOnAccAndBillers();
	}

	@Then("I see Add linked account link will be available to user having Update payees & payment limits permission")
	public void checkAddLinkedAccLink() throws Throwable
	{
		onBTAccountAndBillersModalSteps.checkAddLinkedAccLnk();
	}

	@When("I click on link 'Add linked account'")
	public void clickAddLinkedAccLink() throws Throwable
	{
		onBTAccountAndBillersModalSteps.clickAddLinkedAccLnk();
	}

	@Then("I see a modal window come up")
	public void checkModelWindow() throws Throwable
	{
		onBTAccountAndBillersModalSteps.checkModelWin();
	}

	@Then("I see static heading text 'Add payment and deposit account'")
	public void checkHeadingAddPaymentAndDepo() throws Throwable
	{
		onBTAccountAndBillersModalSteps.checkHeadingAddPaymentAndDep();
	}

	@Then("I see that 'Account name' text box field has maximum limit of 32 letters or numbers along with following special characters(& - < > , + space ( ) /)")
	public void checkAccNameField() throws Throwable
	{
		onBTAccountAndBillersModalSteps.checkAccNameFld();
	}

	@When("I leave the 'Account name' field blank and tab out")
	public void clickAccNameFieldTabOut() throws Throwable
	{
		onBTAccountAndBillersModalSteps.clickAccNameFldTabOut();
	}

	@Then("I see error message Please enter a valid account name using letters or numbers or special characters & - < > , + space ( )")
	public void checkErrAccNameField() throws Throwable
	{
		onBTAccountAndBillersModalSteps.checkErrAccNameFld();
	}

	@When("I enter valid Account name in the field and tab out")
	public void enterValidDataInAccNoField() throws Throwable
	{
		onBTAccountAndBillersModalSteps.enterValidDataInAccNoFld();
	}

	@Then("I see no error messages")
	public void checkNoErrAccNameField() throws Throwable
	{
		onBTAccountAndBillersModalSteps.checkNoErrAccNameFld();
	}

	//Verify 'BSB' field validations in 'Add linked account' modal
	@Then("I see 'BSB' field has maximum limit of 7 characters")
	public void checkBSBField() throws Throwable
	{
		onBTAccountAndBillersModalSteps.checkBSBFld();
	}

	@When("I leave BSB field blank and tab out")
	public void clickBSBFieldAndTabout() throws Throwable
	{
		onBTAccountAndBillersModalSteps.clickBSBFldAndTabout();
	}

	@Then("I see error message Please enter a 6-digit BSB number")
	public void checkErrBSBField() throws Throwable
	{
		onBTAccountAndBillersModalSteps.checkErrBSBFld();
	}

	@When("I enter valid BSB number in the field and tab out")
	public void enterValidDataBSBField() throws Throwable
	{
		onBTAccountAndBillersModalSteps.enterValidDataBSBFld();
	}

	@Then("I see no error messages on BSB field")
	public void checkNoErrBSBField() throws Throwable
	{
		onBTAccountAndBillersModalSteps.checkNoErrBSBFld();
	}

	//Scenario: Verify 'Account number' field validations in 'Add linked account' modal

	@Then("I see that account number field has minimum limit of 6 digits and maximum 10 digits")
	public void checkAccNumberField() throws Throwable
	{
		onBTAccountAndBillersModalSteps.checkAccountNumberFld();
	}

	@When("I leave Account number field blank and tab out")
	public void clickAccNumberFieldAndTabout() throws Throwable
	{
		onBTAccountAndBillersModalSteps.clickAccountNumberFldAndTabout();
	}

	@Then("I see error message Enter an account number")
	public void checkErrAccNumberField() throws Throwable
	{
		onBTAccountAndBillersModalSteps.checkErrAccountNumberFld();
	}

	@When("I enter valid Account number in the field and tab out")
	public void enterValidDataAccNumberField() throws Throwable
	{
		onBTAccountAndBillersModalSteps.enterValidDataAccountNumberFld();
	}

	@Then("I see no error messages on account number field")
	public void checkNoErrAccNumberField() throws Throwable
	{
		onBTAccountAndBillersModalSteps.checkNoErrAccountNumberFld();
	}

	//Scenario: Verify 'Account nickname' field validations in 'Add linked account' modal

	@Then("I see 'Account nickname' field with 30 characters limit only")
	public void checkAccNickNameField() throws Throwable
	{
		onBTAccountAndBillersModalSteps.checkAccountNickNameFld();
	}

	@When("I enter any non allowed characters other than accepted: letters, numbers, hyphens, or spaces and tab out")
	public void clickAccNickNameFieldAndTabout() throws Throwable
	{
		onBTAccountAndBillersModalSteps.clickAccountNickNameFldAndTabout();
	}

	@Then("I see error message Please only use 'letters, numbers, hyphens or spaces'")
	public void checkErrAccNickNameField() throws Throwable
	{
		onBTAccountAndBillersModalSteps.checkErrAccountNickNameFld();
	}

	@When("I enter valid characters in the field and tab out")
	public void enterValidDataAccNickNameField() throws Throwable
	{
		onBTAccountAndBillersModalSteps.enterValidDataAccountNickNameFld();
	}

	@Then("I see no error message on Account nickname field")
	public void checkNoErrAccNickNameField() throws Throwable
	{
		onBTAccountAndBillersModalSteps.checkNoErrAccountNickNameFld();
	}

	//Scenario: Verify 'Terms and conditions' field validations in 'Add linked account' modal

	@Then("I see terms and conditions check box unchecked with text 'You agree to the Terms and Conditions of adding this account. You can only add payments and deposits account if you are the owner of, or authorised signatory for, the account'")
	public void checkTermsAndConditionCheckBox() throws Throwable
	{
		onBTAccountAndBillersModalSteps.checkTermsAndConditionCheckBx();
	}

	@When("I click on the 'terms and conditions' hyperlink")
	public void clickOnTermsAndConditionLink() throws Throwable
	{
		onBTAccountAndBillersModalSteps.clickOnTermsAndConditionLnk();
	}

	@Then("I see a PDF is displayed in a new tab in the browser displaying the correct terms and conditions")
	public void checkNewTabForTermsAndCondition() throws Throwable
	{
		onBTAccountAndBillersModalSteps.checkNewTbForTermsAndCondition();
	}

	//Scenario: Verify enable and disable state of Get SMS Code button

	@Then("I see static text 'SMS code for your security'")
	public void checkTextSMSCodeForyourSecurity() throws Throwable
	{
		onBTAccountAndBillersModalSteps.checkTxtSMSCodeForyourSecurity();
	}

	@Then("I see 'Get SMS code' button in disabled state")
	public void checkSMSCodeButtonStateDisable() throws Throwable
	{
		onBTAccountAndBillersModalSteps.checkSMSCodeBtnStateDisable();
	}

	@When("I check the 'Terms and conditions' checkbox")
	public void clickOnTermsAndConditionCheckBox() throws Throwable
	{
		onBTAccountAndBillersModalSteps.clickOnTermsAndConditionCheckBox();
	}

	@Then("I see 'Get SMS code' button has changed state to enabled")
	public void checkSMSCOdeButtonStateEnable() throws Throwable
	{
		onBTAccountAndBillersModalSteps.checkSMSCOdeButtonStateEnable();
	}
}
