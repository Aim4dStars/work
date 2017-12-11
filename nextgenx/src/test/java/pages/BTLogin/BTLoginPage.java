package pages.BTLogin;

import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class BTLoginPage extends PageObject
{
	public BTLoginPage(WebDriver driver)
	{
		super(driver);
	}

	@FindBy(xpath = "//form[@class='jq-logonForm']/fieldset/h1/span")
	private WebElement isHeaderSignIn;

	public WebElement getHeaderSignIn()
	{
		return isHeaderSignIn;
	}

	@FindBy(xpath = "//form[@class='jq-logonForm']/fieldset/ul/li/div/div/span")
	private WebElement isUsernameLabel;

	public WebElement getUsernameLabel()
	{
		return isUsernameLabel;
	}

	@FindBy(xpath = "//form[@class='jq-logonForm']/fieldset/ul/li/div/div/input")
	private WebElement isUsernameTextField;

	public WebElement getUsernameTextField()
	{
		return isUsernameTextField;
	}

	@FindBy(xpath = "//form[@class='jq-logonForm']/fieldset/ul/li[2]/div/div/span")
	private WebElement isPasswordLabel;

	public WebElement getPasswordLabel()
	{
		return isPasswordLabel;
	}

	@FindBy(xpath = "//form[@class='jq-logonForm']/fieldset/ul/li[2]/div/div/input")
	private WebElement isPasswordTextField;

	public WebElement getPasswordTextField()
	{
		return isPasswordTextField;
	}

	@FindBy(xpath = "//form[@class='jq-logonForm']/fieldset/ul/li[3]/div/a/span")
	private WebElement isSignInButton;

	public WebElement getSignInButton()
	{
		return isSignInButton;
	}

	@FindBy(css = "h1.heading-four.columns-8.margin-bottom-2")
	private WebElement isDashboardPage;

	public WebElement getDashboardPage()
	{
		return isDashboardPage;
	}

	@FindBy(xpath = "//form[@class='jq-logonForm']/fieldset/ul/li[4]/ul/li/div/a/span/span[2]")
	private WebElement isRegisterLink;

	public WebElement getRegisterLink()
	{
		return isRegisterLink;
	}

	@FindBy(xpath = "//form[@class='jq-logonForm']/fieldset/ul/li[4]/ul/li/div[2]/a/span/span[2]")
	private WebElement isForgotPasswordLink;

	public WebElement getForgotPasswordLink()
	{
		return isForgotPasswordLink;
	}

	@FindBy(xpath = "//form[@class='jq-logonForm']/div/div")
	private WebElement isForgottenUsernameText;

	public WebElement getForgottenUsernameText()
	{
		return isForgottenUsernameText;
	}

	@FindBy(xpath = "//form[@class='jq-logonForm']/div/span")
	private WebElement isCallText;

	public WebElement getCallText()
	{
		return isCallText;
	}

	@FindBy(xpath = "//div[@class='ui-tabs-panel ui-widget-content ui-corner-bottom']/footer/div")
	private WebElement isDisclaimer;

	public WebElement getDisclaimer()
	{
		return isDisclaimer;
	}

	@FindBy(xpath = "//h1[@class='header-statement heading-two']/span[contains(text(), 'Forgotten password')]")
	private WebElement isHeaderForgottenPassword;

	public WebElement getHeaderForgottenPassword()
	{
		return isHeaderForgottenPassword;
	}

	@FindBy(xpath = "//div[@id='jq-passwordReset1']/descendant::h1[@class='heading-seven' and contains(text(), 'Step 1 of 2')]")
	private WebElement isForgotPswdStep1Of2;

	public WebElement getForgotPswdStep1Of2()
	{
		return isForgotPswdStep1Of2;
	}

	@FindBy(xpath = "//div[@id='jq-passwordReset1']/descendant::span[@class='label' and contains(text(), 'Username')]")
	private WebElement isFPUsernameLabel;

	public WebElement getFPUsernameLabel()
	{
		return isFPUsernameLabel;
	}

	@FindBy(xpath = "//div[@id='jq-passwordReset1']/descendant::input[@id='login_username']")
	private WebElement isFPUsernameTextField;

	public WebElement getFPUsernameTextField()
	{
		return isFPUsernameTextField;
	}

	@FindBy(xpath = "//div[@id='jq-passwordReset1']/descendant::span[@class='label' and contains(text(), 'Last name')]")
	private WebElement isFPLastNameLabel;

	public WebElement getFPLastNameLabel()
	{
		return isFPLastNameLabel;
	}

	@FindBy(xpath = "//div[@id='jq-passwordReset1']/descendant::span[@class='label' and contains(text(), 'Last name')]/span[contains(text(), 'case sensitive')]")
	private WebElement isSubLblCaseSensetive;

	public WebElement getSubLblCaseSensetive()
	{
		return isSubLblCaseSensetive;
	}

	@FindBy(xpath = "//div[@id='jq-passwordReset1']/descendant::input[@id='passwordResetLastName']")
	private WebElement isFPLastNameTextField;

	public WebElement getFPLastNameTextField()
	{
		return isFPLastNameTextField;
	}

	@FindBy(xpath = "//div[@id='jq-passwordReset1']/descendant::span[@class='label' and contains(text(), 'Postcode')]")
	private WebElement isFPPostcodeLabel;

	public WebElement getFPPostcodeLabel()
	{
		return isFPPostcodeLabel;
	}

	@FindBy(xpath = "//div[@id='jq-passwordReset1']/descendant::input[@id='passwordResetPostcode']")
	private WebElement isFPPoscodeTextField;

	public WebElement getFPPoscodeTextField()
	{
		return isFPPoscodeTextField;
	}

	@FindBy(xpath = "//div[@id='jq-passwordReset1']/descendant::span[@class='label' and contains(text(), 'Enter SMS code for your security')]")
	private WebElement isFPEnterSMSCodeLabel;

	public WebElement getFPEnterSMSCodeLabel()
	{
		return isFPEnterSMSCodeLabel;
	}

	@FindBy(xpath = "//div[@id='jq-passwordReset1']/descendant::span[@class='label-content jq-smsButtonTextHolder' and contains(text(), 'Get SMS Code')]")
	private WebElement btnGetSMSCode;

	public WebElement btnGetSMSCode()
	{
		return btnGetSMSCode;
	}

	@FindBy(xpath = "//div[@id='jq-passwordReset1']/descendant::span[@class='button-inner']/span[contains(text(), 'Next')]")
	private WebElement isFPbtnNext;

	public WebElement getBtnNext()
	{
		return isFPbtnNext;
	}

	@FindBy(xpath = "//div[@id='jq-passwordReset1']/descendant::span[@class='button-inner']/span[contains(text(), 'Cancel')]")
	private WebElement isFPCancelLink;

	public WebElement getFPCancelLink()
	{
		return isFPCancelLink;
	}

	@FindBy(xpath = "//div[@class='jq-passwordResetStepTwo']/form/fieldset/h1/span")
	private WebElement isHeaderForgotPaswordStep2;

	public WebElement getHeaderForgotPaswordStep2()
	{
		return isHeaderForgotPaswordStep2;
	}

	@FindBy(xpath = "//div[@class='jq-passwordResetStepTwo']/form/fieldset/h1[2]")
	private WebElement isForgotPswdStep2Of2;

	public WebElement getForgotPswdStep2Of2()
	{
		return isForgotPswdStep1Of2;
	}

	@FindBy(xpath = "//div[@class='jq-passwordResetStepTwo']/form/fieldset/ul/li/div[2]/div/span")
	private WebElement isFPCreatePasswordLabel;

	public WebElement getFPCreatePasswordLabel()
	{
		return isFPCreatePasswordLabel;
	}

	@FindBy(xpath = "//div[@class='jq-passwordResetStepTwo']/form/fieldset/ul/li/div[2]/div/input")
	private WebElement isFPCreatePasswordTextField;

	public WebElement getFPCreatePasswordTextField()
	{
		return isFPCreatePasswordTextField;
	}

	@FindBy(xpath = "//div[@class='jq-passwordResetStepTwo']/form/fieldset/ul/li[2]/div/div/span")
	private WebElement isFPRepeatPasswordLabel;

	public WebElement getFPRepeatPasswordLabel()
	{
		return isFPRepeatPasswordLabel;
	}

	@FindBy(xpath = "//div[@class='jq-passwordResetStepTwo']/form/fieldset/ul/li[2]/div/div/input")
	private WebElement isFPRepeatPasswordTextField;

	public WebElement getFPRepeatPasswordTextField()
	{
		return isFPRepeatPasswordTextField;
	}

	@FindBy(xpath = "//div[@class='jq-passwordResetStepTwo']/form/fieldset/ul/li[3]/span/a")
	private WebElement isFPSignInBtn;

	public WebElement getFPSignInBtn()
	{
		return isFPSignInBtn;
	}

	@FindBy(xpath = "//div[@class='jq-passwordResetStepTwo']/form/fieldset/ul/li[3]/span[2]/a/span")
	private WebElement isFPStep2CancelLink;

	public WebElement getFPStep2CancelLink()
	{
		return isFPStep2CancelLink;
	}

	@FindBy(xpath = "//div[@class='jq-passwordResetStepTwo']/div")
	private WebElement isFPPasswordPolicyBox;

	public WebElement getFPPasswordPolicyBox()
	{
		return isFPPasswordPolicyBox;
	}

	@FindBy(xpath = "//div[@class='jq-passwordResetStepTwo']/form/fieldset/h1/span")
	private WebElement isResetPasswordHeader;

	public WebElement getResetPasswordHeader()
	{
		return isResetPasswordHeader;
	}

	@FindBy(xpath = "//div[@class='jq-passwordResetStepTwo']/form/fieldset/ul/li/div[2]/div/span")
	private WebElement isRPCreatePasswordLabel;

	public WebElement getRPCreatePasswordLabel()
	{
		return isRPCreatePasswordLabel;
	}

	@FindBy(xpath = "//div[@class='jq-passwordResetStepTwo']/form/fieldset/ul/li/div[2]/div/input")
	private WebElement isRPCreatePasswordTextField;

	public WebElement getRPCreatePasswordTextField()
	{
		return isRPCreatePasswordTextField;
	}

	@FindBy(xpath = "//div[@class='jq-passwordResetStepTwo']/form/fieldset/ul/li[2]/div/div/span")
	private WebElement isRPRepeatPasswordLabel;

	public WebElement getRPRepeatPasswordLabel()
	{
		return isRPRepeatPasswordLabel;
	}

	@FindBy(xpath = "//div[@class='jq-passwordResetStepTwo']/form/fieldset/ul/li[2]/div/div/input")
	private WebElement isRPRepeatPasswordTextField;

	public WebElement getRPRepeatPasswordTextField()
	{
		return isRPRepeatPasswordLabel;
	}

	@FindBy(css = "button.btn-.btn-action-primary")
	private WebElement isRPSignInButton;

	public WebElement getRPSignInButton()
	{
		return isRPSignInButton;
	}

	@FindBy(xpath = "//div[@class='jq-passwordResetStepTwo']/form/fieldset/ul/li[3]/span[2]/a/span/span[2]")
	private WebElement isRPCanelLink;

	public WebElement getRPCanelLink()
	{
		return isRPCanelLink;
	}

	@FindBy(xpath = "//div[@class='jq-passwordResetStepTwo']/div")
	private WebElement isRPPrivacyPolicyBox;

	public WebElement getRPPrivacyPolicyBox()
	{
		return isRPPrivacyPolicyBox;
	}

	@FindBy(xpath = "//div[@id='jq-logout']/div/div/a/span/span[2]")
	private WebElement isSignInLinkOnLoggedoutPage;

	public WebElement getSignInLinkOnLoggedoutPage()
	{
		return isSignInLinkOnLoggedoutPage;
	}

	@FindBy(xpath = "//div[@id='jq-logout']/footer/div")
	private WebElement isDisclaimerOnLoggedOutPage;

	public WebElement getDisclaimerOnLoggedOutPage()
	{
		return isDisclaimerOnLoggedOutPage;
	}

	@FindBy(xpath = "//a[contains(text(), 'try again')]")
	private WebElement isStaticMessageBelowEnterSMSCode;

	public WebElement getStaticMessageBelowEnterSMSCode()
	{
		return isStaticMessageBelowEnterSMSCode;
	}

	@FindBy(css = "h1.header-statement.heading-one")
	private WebElement isLoggedOutText;

	public WebElement getLoggedOutText()
	{
		return isLoggedOutText;
	}

}
