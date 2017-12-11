package feature;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Then;

import steps.BTLoginSteps;
import cucumber.api.java.en.When;

public class BTLogin
{

	@Steps
	BTLoginSteps onBTLoginSteps;

	@When("I am on the sign in page")
	public void openSignInPage()
	{
		onBTLoginSteps.navigate_To_SignInPage();
	}

	@Then("I see text Sign in on top of the page")
	public void verify_SignInHeader()
	{
		onBTLoginSteps.signIn_header();
	}

	@Then("I see text Username with blank textbox field on sign in page")
	public void verifySignInUsername()
	{
		onBTLoginSteps.signin_Username_Label();
		onBTLoginSteps.signin_Username_TextField();
	}

	@Then("I see text Password with blank textbox field ons sign in page")
	public void verifySignInPassword()
	{
		onBTLoginSteps.signin_Password_Label();
		onBTLoginSteps.signin_Password_TextField();
	}

	@Then("I see static text Password are case sensitive")
	public void verifySignInPasswordHelpText()
	{
		onBTLoginSteps.signin_Password_Label();
		onBTLoginSteps.signin_Password_TextField();
	}

	@Then("I see button Sign in")
	public void verifySignInButton()
	{
		onBTLoginSteps.signin_Button();
	}

	@Then("I see link Register and Forgotten password")
	public void verifyRegisterNForgottenPwdLink()
	{
		onBTLoginSteps.link_Register();
		onBTLoginSteps.link_ForgottenPassword();

	}

	@Then("I see static text Forgotten your username?")
	public void verifyForgottenUsername()
	{
		onBTLoginSteps.forgotten_UsernameText();
	}

	@Then("I see text Call 1300 784 207. in the next line")
	public void verifyCall()
	{
		onBTLoginSteps.callText();
	}

	@Then("I see Disclaimer text below the Sign in box")
	public void verifyDisclaimer()
	{
		onBTLoginSteps.disclaimerText();
	}

	@When("I click on the Sign in button")
	public void clickSignInButton()
	{
		onBTLoginSteps.click_On_signInButton();
	}

	@Then("I see page gets navigated to Dashboard or status approver page")
	public void verifyNavigationToDashboard()
	{
		onBTLoginSteps.DashboardScreen();
	}

	@When("I click on the Forgotten password link on Sign in page")
	public void clickForgotPasswordLink()
	{
		onBTLoginSteps.click_On_ForgotPasswordLink();
	}

	@Then("I see static text Forgotten password on top of Step 1")
	public void verifyForgotPasswordStep1Header()
	{
		onBTLoginSteps.navigate_To_ForgotPasswordStep1Page();
	}

	@Then("I see static text Step 1 of 2")
	public void verifyForgotPasswordStep1Of2()
	{
		onBTLoginSteps.forgotPassword_Step1Of2();
	}

	@Then("I see text Username with blank textbox field")
	public void verifyForgotPasswordUserName()
	{
		onBTLoginSteps.forgotPassword_UsernameLabel();
		onBTLoginSteps.forgotPassword_UsernameTextField();
	}

	@Then("I see text Last name with blank textbox field")
	public void verifyForgotPasswordLastName()
	{
		onBTLoginSteps.forgotPassword_LastnameLabel();
		//onBTLoginSteps.forgotPassword_SubLabel();
		onBTLoginSteps.forgotPassword_LastnameTextField();
	}

	@Then("I see text Postcode with blank textbox field")
	public void verifyForgotPasswordPostCode()
	{
		onBTLoginSteps.forgotPassword_PostcodeLabel();
		onBTLoginSteps.forgotPassword_PostcodeTextField();
	}

	@Then("I see text Enter SMS code for your security below the Postcode textbox")
	public void verifyForgotPasswordSMSCodeText()
	{
		onBTLoginSteps.forgotPassword_SMSCodeText();
	}

	@Then("I see button Get SMS code")
	public void verifyForgotPasswordSMSCodeButton()
	{
		onBTLoginSteps.forgotPassword_SMSCodeBtn();
	}

	@When("I click the Get SMS code button on forgot password screen")
	public void clickOnGetSMSCodeBtn()
	{
		onBTLoginSteps.click_On_Get_SMS_Code_Button();
	}

	@Then("I see the button is replaced with text box to enter the SMS code on forgot password screen")
	public void verifyGetSMSCodeField()
	{
		onBTLoginSteps.verify_Get_SMS_Code_Field();
	}

	@Then("I see Next button is disabled by default on forgot password screen")
	public void verifyNextButtonDisabled()
	{
		onBTLoginSteps.verify_Next_Button_Disabled();
	}

	@Then("I see maximum length allowed for SMS code on forgot password page is 6 digits")
	public void verifyMAXDigitAllowedInSMSCodeField()
	{
		onBTLoginSteps.verify_Max_6_Digit_Allowed_In_SMS_Code_Field();
	}

	@Then("I see static text Code sent registered mobile number. If the code is not received in a few minutes, try again")
	public void verifyStaticMsgBelowGetSMSCodeField()
	{
		onBTLoginSteps.verify_Static_Message_Below_Enter_SMS_Code();
	}

	@When("I enter 6 digit sms code on forgot password screen")
	public void enterSixDigitSmsCode()
	{
		onBTLoginSteps.enter_Six_Digit_SMS_Code();
	}

	@Then("I see Next button gets enabled on forgot password screen")
	public void verifyNextButtonEnabled()
	{
		onBTLoginSteps.verify_Next_Button_Enabled();
	}

	@Then("I see link Cancel on Forgotten password step 1")
	public void verifyForgotPasswordStep1CancelLink()
	{
		onBTLoginSteps.forgotPasswordStep1_CancelLink();
	}

	@When("I click on Cancel link of Forgotten password step 1")
	public void verifyForgotPasswordStep1ClickOnCancelLink()
	{
		onBTLoginSteps.forgotPasswordStep1_Click_CancelLink();
	}

	@Then("I See page gets navigated from Forgotten password step 1 to Sign in screen")
	public void verifyNavigationForgotPasswordStep1ToSignInPage()
	{
		onBTLoginSteps.signIn_header();
	}

	@When("I click Next button")
	public void verifyForgotPasswordClickOnNextButton()
	{
		onBTLoginSteps.forgotPassword_Click_NextButton();
	}

	@Then("I see page gets navigated to Forgot password Step 2 if all fields are verified")
	public void verifyNavigationForgotPasswordStep1ToStep2()
	{
		onBTLoginSteps.navigate_To_ForgotPasswordStep2Page();
	}

	@Then("I see static text Forgotten password on top of Step 2")
	public void verifyForgotPasswordStep2Header()
	{
		onBTLoginSteps.navigate_To_ForgotPasswordStep2Page();
	}

	@Then("I see static text Step 2 of 2")
	public void verifyForgotPasswordStep2Of2()
	{
		onBTLoginSteps.forgotPassword_Step2Of2();
	}

	@Then("I see text Create password with blank textbox field")
	public void verifyForgotPasswordCreatePassword()
	{
		onBTLoginSteps.forgotPassword_CreatePasswordLabel();
		onBTLoginSteps.forgotPassword_CreatePasswordTextField();
	}

	@Then("I see text Repeat password with blank textbox field")
	public void verifyForgotPasswordRepeatPassword()
	{
		onBTLoginSteps.forgotPassword_RepeatPasswordLabel();
		onBTLoginSteps.forgotPassword_RepeatPasswordTextField();
	}

	@Then("I see Sign in button and Cancel link on Forgot password step2")
	public void verifyForgotPasswordSigninAndCancel()
	{
		onBTLoginSteps.forgotPassword_SignInButton();
		onBTLoginSteps.forgotPasswordStep2_CancelLink();
	}

	@Then("I see help text to the right side of the screen displaying password policies")
	public void verifyForgotPasswordPolicyBox()
	{
		onBTLoginSteps.forgotPassword_PasswordPolicyBox();
	}

	@When("I click on Cancel link of Forgotten password step 2")
	public void verifyForgotPasswordStep2ClickOnCancelLink()
	{
		onBTLoginSteps.forgotPasswordStep2_Click_CancelLink();
	}

	@Then("I See page gets navigated from Forgotten password step 2 to Sign in screen")
	public void verifyNavigationForgotPasswordStep2ToSignInPage()
	{
		onBTLoginSteps.signIn_header();
	}

	@Then("I see static text Reset password on top")
	public void verifyResetPasswordHeader()
	{
		onBTLoginSteps.resetPassword_HeaderText();
	}

	@Then("I see text Create password with blank textbox field on Reset password screen")
	public void verifyResetPasswordCreatPasswordField()
	{
		onBTLoginSteps.resetPassword_CreatePasswordLabel();
		onBTLoginSteps.resetPassword_CreatePasswordTextField();
	}

	@Then("I see text Repeat password with blank textbox field on Reset password screen")
	public void verifyResetPasswordResetPasswordField()
	{
		onBTLoginSteps.resetPassword_RepeatPasswordLabel();
		onBTLoginSteps.resetPassword_RepeatPasswordTextField();
	}

	@Then("I see Sign in button and Cancel link on Reset password screen")
	public void verifyResetPasswordSignInButtonAndCancleLink()
	{
		onBTLoginSteps.resetPassword_SignInButton();
		onBTLoginSteps.resetPassword_CancelLink();
	}

	@Then("I see help text to the right side of the screen displaying password policies on Reset password screen")
	public void verifyResetPasswordPrivacyPolicyBox()
	{
		onBTLoginSteps.resetPassword_PasswordPolicyBox();
	}

	@When("I click on Cancel of Reset password screen")
	public void verifyResetPasswordClickOnCancelLink()
	{

		onBTLoginSteps.resetPassword_Click_CancelLink();
	}

	@Then("I see page gets navigated from Reset password to Sign in screen")
	public void verifyNavigationFromResetPasswordToSignInPage()
	{
		onBTLoginSteps.signIn_header();
	}

	@Then("I see static text You are no longer signed in.")
	public void verifyLoggedOutHeader()
	{
		onBTLoginSteps.loggedOutHeader();
	}

	@Then("I see Sign in link")
	public void verifySignInLinkOnLoggedOutScreen()
	{
		onBTLoginSteps.signInLinkOnLoggedOutScreen();
	}

	@Then("I see Disclaimer text below Logout box")
	public void verifyDisclaimerOnLoggedOutScreen()
	{
		onBTLoginSteps.disclaimerOnLoggedOutScreen();
	}

}
