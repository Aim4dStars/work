package steps;

import static junit.framework.Assert.*;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import pages.BTLogin.BTLoginPage;

public class BTLoginSteps extends ScenarioSteps
{

	BTLoginPage btloginpage;

	public BTLoginSteps(Pages pages)
	{
		super(pages);
	}

	@Step
	public void navigate_To_SignInPage()
	{
		getDriver().get("http://localhost:9080/ng/public/page/logon?TAM_OP=login");

	}

	@Step
	public void signIn_header()
	{

		String SignInText = btloginpage.getHeaderSignIn().getText();
		assertEquals(SignInText, "Sign in");
		assertTrue(btloginpage.getHeaderSignIn().isDisplayed());

	}

	@Step
	public void signin_Username_Label()
	{
		String userNameLabel = btloginpage.getUsernameLabel().getText();
		assertEquals(userNameLabel, "Username");
		assertTrue(btloginpage.getHeaderSignIn().isDisplayed());
	}

	@Step
	public void signin_Password_Label()
	{
		String passwordLabel = btloginpage.getPasswordLabel().getText();
		assertEquals(passwordLabel, "Password");
		assertTrue(btloginpage.getPasswordLabel().isDisplayed());
	}

	@Step
	public void signin_Username_TextField()
	{

		assertTrue(btloginpage.getUsernameTextField().isDisplayed());
	}

	@Step
	public void signin_Password_TextField()
	{

		assertTrue(btloginpage.getPasswordTextField().isDisplayed());
	}

	@Step
	public void signin_Button()
	{
		String signInButton = btloginpage.getSignInButton().getText();
		assertEquals(signInButton, "Sign in");
		assertTrue(btloginpage.getSignInButton().isDisplayed());
	}

	@Step
	public void click_On_signInButton()
	{
		btloginpage.getSignInButton().click();
	}

	@Step
	public void DashboardScreen()
	{
		String dashboard = btloginpage.getDashboardPage().getText();
		assertEquals(dashboard, "Key activity");
		assertTrue(btloginpage.getDashboardPage().isDisplayed());
	}

	@Step
	public void link_Register()
	{
		String regsiterLink = btloginpage.getRegisterLink().getText();
		assertEquals(regsiterLink, "Register");
		assertTrue(btloginpage.getRegisterLink().isDisplayed());
	}

	@Step
	public void link_ForgottenPassword()
	{
		String forgottenPaswordLink = btloginpage.getForgotPasswordLink().getText();
		assertEquals(forgottenPaswordLink, "Forgotten password");
		assertTrue(btloginpage.getForgotPasswordLink().isDisplayed());
	}

	@Step
	public void forgotten_UsernameText()
	{
		String forgottenUsernameText = btloginpage.getForgottenUsernameText().getText();
		assertEquals(forgottenUsernameText, "Forgotten your usename?");
		assertTrue(btloginpage.getForgottenUsernameText().isDisplayed());

	}

	@Step
	public void callText()
	{
		String callText = btloginpage.getCallText().getText();
		assertEquals(callText, "Call 1300 784 207.");
		assertTrue(btloginpage.getCallText().isDisplayed());
	}

	@Step
	public void disclaimerText()
	{
		assertTrue(btloginpage.getDisclaimer().isDisplayed());

	}

	@Step
	public void click_On_ForgotPasswordLink()
	{
		btloginpage.getForgotPasswordLink().click();

	}

	@Step
	public void navigate_To_ForgotPasswordStep1Page()
	{

		String forgottenPasswordText = btloginpage.getHeaderForgottenPassword().getText();
		assertEquals(forgottenPasswordText, "Forgotten password");
		assertTrue(btloginpage.getHeaderForgottenPassword().isDisplayed());
	}

	@Step
	public void forgotPassword_Step1Of2()
	{
		String forgottenPasswordStep1Of2Text = btloginpage.getForgotPswdStep1Of2().getText();
		assertEquals(forgottenPasswordStep1Of2Text, "Step 1 of 2");
		assertTrue(btloginpage.getForgotPswdStep1Of2().isDisplayed());

	}

	@Step
	public void forgotPassword_UsernameLabel()
	{
		String forgottenPasswordUsernameLbl = btloginpage.getFPUsernameLabel().getText();
		assertEquals(forgottenPasswordUsernameLbl, "Username");
		assertTrue(btloginpage.getFPUsernameLabel().isDisplayed());

	}

	@Step
	public void forgotPassword_UsernameTextField()
	{
		assertTrue(btloginpage.getFPUsernameTextField().isDisplayed());

	}

	@Step
	public void forgotPassword_LastnameLabel()
	{
		String forgottenPasswordLastNameLbl = btloginpage.getFPLastNameLabel().getText();
		assertEquals(forgottenPasswordLastNameLbl, "Last name");
		assertTrue(btloginpage.getFPLastNameLabel().isDisplayed());

	}

	@Step
	public void forgotPassword_LastnameTextField()
	{
		assertTrue(btloginpage.getFPLastNameTextField().isDisplayed());

	}

	@Step
	public void forgotPassword_PostcodeLabel()
	{
		String forgottenPasswordPostcodeLbl = btloginpage.getFPPostcodeLabel().getText();
		assertEquals(forgottenPasswordPostcodeLbl, "Post code");
		assertTrue(btloginpage.getFPPostcodeLabel().isDisplayed());

	}

	@Step
	public void forgotPassword_PostcodeTextField()
	{
		assertTrue(btloginpage.getFPPoscodeTextField().isDisplayed());

	}

	@Step
	public void forgotPassword_SMSCodeText()
	{
		String forgottenPasswordSMSCodeTxt = btloginpage.getFPEnterSMSCodeLabel().getText();
		assertEquals(forgottenPasswordSMSCodeTxt, "SMS code for your security");
		assertTrue(btloginpage.getFPEnterSMSCodeLabel().isDisplayed());

	}

	@Step
	public void forgotPassword_SMSCodeBtn()
	{
		String forgottenPasswordSMSCodeBtn = btloginpage.btnGetSMSCode().getText();
		assertEquals(forgottenPasswordSMSCodeBtn, "Get SMS code");

	}

	@Step
	public void click_On_Get_SMS_Code_Button()
	{
		btloginpage.btnGetSMSCode().click();

	}

	@Step
	public void verify_Get_SMS_Code_Field()
	{
		assertTrue(btloginpage.btnGetSMSCode().isDisplayed());
	}

	@Step
	public void verify_Next_Button_Disabled()
	{
		assertFalse(btloginpage.getBtnNext().isEnabled());

	}

	@Step
	public void verify_Max_6_Digit_Allowed_In_SMS_Code_Field()
	{
		btloginpage.btnGetSMSCode().clear();
		btloginpage.btnGetSMSCode().sendKeys("1234567");
		String fieldValue = btloginpage.btnGetSMSCode().getText();
		assertEquals(btloginpage.btnGetSMSCode().getText(), "123456");

	}

	@Step
	public void verify_Static_Message_Below_Enter_SMS_Code()
	{
		assertEquals(btloginpage.getStaticMessageBelowEnterSMSCode().getText(),
			"Code sent registered mobile number. If not received in a few minutes, <try again>");
		assertTrue(btloginpage.getStaticMessageBelowEnterSMSCode().isDisplayed());
	}

	@Step
	public void enter_Six_Digit_SMS_Code()
	{
		btloginpage.btnGetSMSCode().sendKeys("123456");

	}

	@Step
	public void verify_Next_Button_Enabled()
	{
		assertTrue(btloginpage.getBtnNext().isEnabled());

	}

	@Step
	public void forgotPassword_NextBtn()
	{
		String forgottenPasswordNextBtn = btloginpage.getBtnNext().getText();
		assertEquals(forgottenPasswordNextBtn, "Next");
		assertTrue(btloginpage.getBtnNext().isDisplayed());

	}

	@Step
	public void forgotPassword_Click_NextButton()
	{
		btloginpage.getBtnNext().click();

	}

	@Step
	public void forgotPasswordStep1_CancelLink()
	{
		String forgottenPasswordNextLink = btloginpage.getFPCancelLink().getText();
		assertEquals(forgottenPasswordNextLink, "Cancel");
		assertTrue(btloginpage.getFPCancelLink().isDisplayed());

	}

	@Step
	public void forgotPasswordStep1_Click_CancelLink()
	{
		btloginpage.getFPCancelLink().click();

	}

	@Step
	public void navigate_To_ForgotPasswordStep2Page()
	{
		String forgottenPasswordTxt = btloginpage.getHeaderForgotPaswordStep2().getText();
		assertEquals(forgottenPasswordTxt, "Forgotten password");
		assertTrue(btloginpage.getHeaderForgotPaswordStep2().isDisplayed());

	}

	@Step
	public void forgotPassword_Step2Of2()
	{
		String forgottenPasswordStep2Of2Text = btloginpage.getForgotPswdStep2Of2().getText();
		assertEquals(forgottenPasswordStep2Of2Text, "Step 2 of 2");
		assertTrue(btloginpage.getForgotPswdStep2Of2().isDisplayed());

	}

	@Step
	public void forgotPassword_CreatePasswordLabel()
	{
		String forgottenPasswordCreatePasswordLbl = btloginpage.getFPCreatePasswordLabel().getText();
		assertEquals(forgottenPasswordCreatePasswordLbl, "Create password");
		assertTrue(btloginpage.getFPCreatePasswordLabel().isDisplayed());

	}

	@Step
	public void forgotPassword_CreatePasswordTextField()
	{
		assertTrue(btloginpage.getFPCreatePasswordTextField().isDisplayed());

	}

	@Step
	public void forgotPassword_RepeatPasswordLabel()
	{
		String forgottenPasswordRepeatPasswordLbl = btloginpage.getFPRepeatPasswordLabel().getText();
		assertEquals(forgottenPasswordRepeatPasswordLbl, "Repeat password");
		assertTrue(btloginpage.getFPRepeatPasswordLabel().isDisplayed());

	}

	@Step
	public void forgotPassword_RepeatPasswordTextField()
	{
		assertTrue(btloginpage.getFPRepeatPasswordTextField().isDisplayed());

	}

	@Step
	public void forgotPassword_SignInButton()
	{
		String forgottenPasswordSignInBtn = btloginpage.getFPSignInBtn().getText();
		assertEquals(forgottenPasswordSignInBtn, "Sign in");
		assertTrue(btloginpage.getFPSignInBtn().isDisplayed());

	}

	@Step
	public void forgotPasswordStep2_CancelLink()
	{
		String forgottenPasswordStep2CancelLink = btloginpage.getFPCancelLink().getText();
		assertEquals(forgottenPasswordStep2CancelLink, "Cancel");
		assertTrue(btloginpage.getFPCancelLink().isDisplayed());

	}

	@Step
	public void forgotPassword_PasswordPolicyBox()
	{
		assertTrue(btloginpage.getFPPasswordPolicyBox().isDisplayed());

	}

	@Step
	public void forgotPasswordStep2_Click_CancelLink()
	{
		btloginpage.getFPCancelLink().click();

	}

	@Step
	public void resetPassword_HeaderText()
	{
		String resetPasswordHeaderTxt = btloginpage.getResetPasswordHeader().getText();
		assertEquals(resetPasswordHeaderTxt, "Reset password");
		assertTrue(btloginpage.getResetPasswordHeader().isDisplayed());
	}

	@Step
	public void resetPassword_CreatePasswordLabel()
	{
		String createPasswordLbl = btloginpage.getRPCreatePasswordLabel().getText();
		assertEquals(createPasswordLbl, "Create password");
		assertTrue(btloginpage.getResetPasswordHeader().isDisplayed());
	}

	@Step
	public void resetPassword_CreatePasswordTextField()
	{
		assertTrue(btloginpage.getRPCreatePasswordTextField().isDisplayed());
	}

	@Step
	public void resetPassword_RepeatPasswordLabel()
	{
		String resetPasswordLbl = btloginpage.getRPCreatePasswordLabel().getText();
		assertEquals(resetPasswordLbl, "Repeat password");
		assertTrue(btloginpage.getResetPasswordHeader().isDisplayed());
	}

	@Step
	public void resetPassword_RepeatPasswordTextField()
	{
		assertTrue(btloginpage.getRPRepeatPasswordTextField().isDisplayed());
	}

	@Step
	public void resetPassword_SignInButton()
	{
		String repeatPasswordSignInBtn = btloginpage.getRPSignInButton().getText();
		assertEquals(repeatPasswordSignInBtn, "Sign in");
		assertTrue(btloginpage.getRPSignInButton().isDisplayed());

	}

	@Step
	public void resetPassword_CancelLink()
	{
		String repeatPasswordCancelLink = btloginpage.getRPCanelLink().getText();
		assertEquals(repeatPasswordCancelLink, "Cancel");
		assertTrue(btloginpage.getRPCanelLink().isDisplayed());

	}

	@Step
	public void resetPassword_Click_CancelLink()
	{
		btloginpage.getRPCanelLink().click();

	}

	@Step
	public void resetPassword_PasswordPolicyBox()
	{
		assertTrue(btloginpage.getRPPrivacyPolicyBox().isDisplayed());

	}

	@Step
	public void loggedOutHeader()
	{
		String loggedOutText = btloginpage.getLoggedOutText().getText();
		assertEquals(loggedOutText, "Yor are no longer signed in.");
		assertTrue(btloginpage.getLoggedOutText().isDisplayed());

	}

	@Step
	public void signInLinkOnLoggedOutScreen()
	{
		String signInLink = btloginpage.getSignInLinkOnLoggedoutPage().getText();
		assertEquals(signInLink, "Sign in");
		assertTrue(btloginpage.getSignInLinkOnLoggedoutPage().isDisplayed());
	}

	@Step
	public void disclaimerOnLoggedOutScreen()
	{
		assertTrue(btloginpage.getDisclaimerOnLoggedOutPage().isDisplayed());
	}

}
