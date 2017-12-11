package feature;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import pages.logon.LogonPage;
import steps.BTRegisterSteps;

public class BTRegister
{
	@Steps
	BTRegisterSteps bTRegisterSteps;
	LogonPage logonpage;

	//Scenario: Verify the error of Registration page 1 on empty tab outs

	@Given("I navigate to Sign in page")
	public void isNavigateToSignIn()
	{
		bTRegisterSteps.navigateToSigninPage();
	}

	@When("I click on Register link")
	public void clickOnRegisterLnk() throws InterruptedException
	{
		bTRegisterSteps.clickOnRegisterLink();
	}

	@When("I click on Get SMS Code with all fields are empty")
	public void checkAllFieldEmpty()
	{
		bTRegisterSteps.clickOnGetSMSCodeButton();
		bTRegisterSteps.ckeckAllFieldsAreEmpty();

	}

	@Then("I see message 'Please enter your registration number' below 'Registration number' field")
	public void verifyErrorMsgOnRegistrationNo()
	{
		bTRegisterSteps.checkErrorMesageOnRegistrationNo();
	}

	@Then("I see message 'Please enter your last name' below 'Last name' field")
	public void verifyErrorMsgOnLastName()
	{
		bTRegisterSteps.checkErrorMesageOnLastName();
	}

	@Then("I see message 'Please enter your postcode' below 'Post Code' field")
	public void verifyErrorMsgOnPostCode()
	{
		bTRegisterSteps.checkErrorMesageOnPostcode();
	}

	//Scenario: Verify the functionality of Registration number, Last name of Post Code register page1

	@Then("I see Investor/Adviser registration step1 page with static text 'Step 1 of 2'")
	public void checkTxtStep1Of2()
	{
		bTRegisterSteps.checkTextStep1Of2();
	}

	@Then("I see 'Registration number' text with text box field")
	public void checkRegistrationNoLabelAndField() throws InterruptedException
	{
		bTRegisterSteps.checkRegistrationNoLabelAndField();
	}

	@Then("I see user can enter maximum 12 character/special characters/ Letters")
	public void checkMax12CharInRegistrationNoField() throws InterruptedException
	{
		bTRegisterSteps.checkMax12CharInRegistrationNoField();
	}

	@Then("I see 'Last name' text with text box field")
	public void checkLastNameLblAndField()
	{
		bTRegisterSteps.checkLastNameLabelAndField();
	}

	@Then("I see special characters -()'space<>,+& is allowed else it displays error message 'Please enter your last name' below the last name field")
	public void checkLastNameFieldForSpecialChars() throws InterruptedException
	{
		bTRegisterSteps.checkLastNameFieldForSpecialChar();
	}

	@Then("I see user can enter maximum 256 characters")
	public void checkMax256CharactersInLastName()
	{
		bTRegisterSteps.checkMax256CharInLastName();
	}

	@Then("I see 'Postcode' text with text box field")
	public void checkPostCodeLblAndField()
	{
		bTRegisterSteps.checkPostCodeLabelAndField();
	}

	//Scenario: Verify the functionality of cancel button on Register Step 1 page
	@Then("I see static text Need help? Call 1300881716 for investor")
	public void checkTextNeedHelpAndCallNo()
	{
		bTRegisterSteps.checkLnkNeedHelpAndCallNo();
	}

	@When("I click on Cancel link of Register Step 1 of 2 page")
	public void clickOnCancel()
	{
		bTRegisterSteps.clickCancel();
	}

	@Then("I see Sign-in page")
	public void checkLoginPageDisplayed()
	{
		bTRegisterSteps.checkLoginPgDisplayed();
	}

	//Scenario: Verify the functionality of Get SMS Code and Next button
	@Then("I see Get SMS code button on register page")
	public void checkGetSMSCodeBtn()
	{
		bTRegisterSteps.checkGetSMSCodeBtn();
	}

	@When("I click on Get SMS Code button")
	public void clickOnGetSMSCodeBtn()
	{
		bTRegisterSteps.clickOnGetSMSCodeButton();
	}

	@Then("I see error message displayed as 'Some of the details you've entered don't match our records. Please check they're correct before trying again' if any mismatch occur")
	public void checkErrorMesageOnMismatchOfData()
	{
		bTRegisterSteps.enterInvalidDataInFields();
		bTRegisterSteps.checkErrOnMismatchOfDataOnRegister();
	}

	@When("I enter valid 'registration number', 'Last name' & 'postcode' and click on 'Get SMS Code'")
	public void enterValidDataInAllFields() throws InterruptedException
	{
		bTRegisterSteps.enterValidDataInFields();
		bTRegisterSteps.clickOnGetSMSCodeButton();
		Thread.sleep(10000);
	}

	@Then("I see get SMS code button get replaced via SMS code field and max length of it is 6 digit")
	public void checkSMSCodeFieldOf6DigitLen()
	{
		bTRegisterSteps.checkSMSCodeFieldOf6DigitLenght();
	}

	@Then("I see static text 'Code sent. If the code is not received in a few minutes, Try again'")
	public void checkTxtIfCodeNotReceived()
	{
		bTRegisterSteps.checkTextIfCodeNotReceived();
	}

	@Then("I see Next button is disabled till the 6 digit SMS code is not filled")
	public void checkNextBtnIsDisable()
	{
		bTRegisterSteps.checkNextButtonIsDisable();
	}

	@Then("I enter SMS code in SMS code field on registration")
	public void enter6DigitDataInSMSField()
	{
		bTRegisterSteps.enterDataInSMSField();
	}

	@When("I click on next button")
	public void clickOnNextBtn()
	{
		bTRegisterSteps.clickOnNextButton();
	}

	@Then("I see Investor/Adviser registration step 2 page with static text Investo/Adviser registration 'Step 2 of 2'")
	public void checkRegistrationStep2Pg()
	{
		bTRegisterSteps.checkRegistrationStep2Page();
	}

	@When("I click on next button to go to Step 2 page")
	public void clickOnNextBtnForStep2() throws InterruptedException
	{
		//bTRegisterSteps.clickOnNextButton();
		bTRegisterSteps.directlyNavigateToStep2();
	}

	//Scenario: Verify the functionality of Create username, Create password, Repeat password of register page2
	@Then("I see Register step 2 page with static text 'Register Step 2 of 2'")
	public void checkTextStep2Of2()
	{
		bTRegisterSteps.checkRegistrationStep2Page();
	}

	@Then("I see 'Create username' text with text box field")
	public void checkUsernameTextBxWithlabel()
	{
		bTRegisterSteps.checkUserNameFldAndLabel();
	}

	@Then("I see max character limit of 'Create username' field is 50")
	public void check50CharLimitOfUserNameFld()
	{
		bTRegisterSteps.checkUserNameFieldOf50CharLenght();
	}

	@Then("I see error message 'Please enter your username' if field is left blank")
	public void checkErrMsgOnUserNameWhenEmptyTabOut()
	{
		bTRegisterSteps.checkErrMsgOnUserNameFld();
	}

	@Then("I click on 'Create username' field")
	public void clickOnCreateUserName()
	{
		bTRegisterSteps.clickUserNameFld();
	}

	@Then("I see callout box with validation message 'Must be', 'Between 8-50 character', 'A combination of letters and numbers', 'Cannot include', 'An email address', 'One of these character '&^%$#@!'")
	public void checkHelpTextOfUserName()
	{
		bTRegisterSteps.checkHelpTextOfUsernameFld();
	}

	@Then("I see 'Create password' text with text field")
	public void checkCreatePasswordFld()
	{
		bTRegisterSteps.checkCreatePasswordFldWithLbl();
	}

	@Then("I see if password is not entered and on tab out displays an error message 'Please enter password'")
	public void checkErrMsgOnPasswordFld()
	{
		bTRegisterSteps.checkErrMsgOnPasswordFld();
	}

	@Then("I see 'Create password' field allow 250 characters")
	public void checkPasswordFld250CharLimit()
	{
		bTRegisterSteps.checkPasswordField250CharLimit();
	}

	@Then("I see callout box with validation message 'Must be at least', 'One Letter', 'One number or special character', '8 Characters', 'Cannot include', 'Your user name'")
	public void checkHelpTextOfPassword()
	{
		bTRegisterSteps.checkHelpTextOfPasswordFld();
	}

	@Then("I see 'Repeat Password' text with text field")
	public void checkRepeatPasswordLblAndFld()
	{
		bTRegisterSteps.checkCreatePasswordFldWithLbl();
	}

	@Then("I see 'Repeat Password' text field accept max 250 characters")
	public void checkRepeatPasswordField250CharLimit()
	{
		//bTRegisterSteps.checkErrMsgOnRepeatPasswordFld();
	}

	@Then("I see If repeat password is not entered then on tab out display error below user name field 'Repeat password cannot be empty'")
	public void checkRepeatPasswordFieldEmptyError()
	{
		bTRegisterSteps.checkErrMsgOnRepeatPasswordFld();
	}

	//Scenario: Verify the functionality of Terms and Condition
	@Then("I see Term & conditions check box and by default it is unchecked")
	public void checkTermsAndConditionCheckBox()
	{
		bTRegisterSteps.checkBoxTermsAndCondition();
	}

	@Then("I see static text 'I agree to the term and conditions'")
	public void checkTxtIAgreeToTheTermAndConditions()
	{
		bTRegisterSteps.checkTextAgreeToTheTermAndConditions();
	}

	@When("I click on 'term and conditions' link")
	public void clickTermsConditionLink()
	{
		bTRegisterSteps.clickTermsAndConditionLnk();
	}

	@Then("I see terms and conditions open in new tab")
	public void checkNewTabOfTermsCondition()
	{
		bTRegisterSteps.checSwitchToTermsAndConditionWindow();
	}

	//Scenario: Verify the functionality of Sign In button
	@Then("I see Sign In button is disabled by default")
	public void checkSignInBtn()
	{
		bTRegisterSteps.checkSignInButton();
	}

	@Then("I enter invalid input in Create username, Create password, Repeat password and select terms and condition")
	public void enterInvalidDataInAllFieldsInRegister()
	{
		bTRegisterSteps.enterInvalidDataInAllFieldsIn();
	}

	@Then("I see Sign in button gets enable")
	public void checkSignInButtonEnable()
	{
		//temp
		bTRegisterSteps.enterInvalidDataInAllFieldsIn();
		System.out.println("1");
		bTRegisterSteps.checkSignInBtnEnable();
		System.out.println("2");
		bTRegisterSteps.clickSignInButton();
		System.out.println("3");
	}

	@Then("I click on Sign In button with invalid password and username")
	public void clickOnSignInBtn()
	{
		bTRegisterSteps.clickSignInButton();
	}

	@Then("I see error message 'Too many consecutive repeated characters' or 'Not enough special characters' below password field")
	public void checkErrUnderPasswordField()
	{
		bTRegisterSteps.checkErrUnderPasswordFld();
	}

	@Then("I see error message 'Entered user name is not available' below username")
	public void checkErrUnderUserNmaeField()
	{
		bTRegisterSteps.checkErrUnderUserNmaeFld();
	}

	@Then("I see if re-entered password is different it will display error below the field 'Enter the same password as above'")
	public void checkErrUnderResetPasswordField()
	{
		bTRegisterSteps.checkErrUnderResetPasswordFld();
	}
}
