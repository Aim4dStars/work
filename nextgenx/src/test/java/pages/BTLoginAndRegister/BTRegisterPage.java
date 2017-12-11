package pages.BTLoginAndRegister;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class BTRegisterPage extends PageObject
{
	public BTRegisterPage(WebDriver driver)
	{
		super(driver);
	}

	@FindBy(xpath = "//h1[@class='header-statement heading-two']/span[contains(text(), 'Sign in')]")
	private WebElement textHeaderSighIn;

	public WebElement getTextHeaderSighIn()
	{
		return textHeaderSighIn;
	}

	@FindBy(xpath = "//h1[@class='header-statement heading-two']/span[contains(text(), 'Register')]")
	private WebElement textHeaderRegister;

	public WebElement getTextHeaderRegister()
	{
		return textHeaderRegister;
	}

	@FindBy(xpath = "//div[@class='jq-registerStepOne']/descendant::h1[@class='heading-seven']")
	private WebElement lblStep1Of2;

	public WebElement getLblStep1Of2()
	{
		return lblStep1Of2;
	}

	@FindBy(xpath = "//div[@class='validation-container']/input[@id='registrationCode']/preceding-sibling::span[@class='label']")
	private WebElement lblRegistrationNumber;

	public WebElement getLblRegistrationNumber()
	{
		return lblRegistrationNumber;
	}

	@FindBy(xpath = "//div[@class='validation-container']/input[@id='registrationCode']")
	private WebElement tBxRegistrationNo;

	public WebElement getTBxRegistrationNo()
	{
		return tBxRegistrationNo;
	}

	@FindBy(xpath = "//div[@class='jq-registerStepOne']/descendant::input[@id='registrationCode']/parent::div/following-sibling::em[@role= 'alert']")
	private WebElement errOnRegistrationNo;

	public WebElement getErrOnRegistrationNo()
	{
		return errOnRegistrationNo;
	}

	@FindBy(xpath = "//input[@id='lastName']/preceding-sibling::span")
	private WebElement lblLastName;

	public WebElement getLblLastName()
	{
		return lblLastName;
	}

	@FindBy(xpath = "//div[@class='validation-container']/span[contains(text(), 'Last name')]/following-sibling::input[@id='registrationCode']/preceding-sibling::span/span")
	private WebElement subLblCaseSensetive;

	public WebElement getSubLblCaseSensetive()
	{
		return subLblCaseSensetive;
	}

	@FindBy(xpath = "//div[@class='validation-container']/input[@id='lastName']")
	private WebElement tBxLastName;

	public WebElement getTBxLastName()
	{
		return tBxLastName;
	}

	@FindBy(xpath = "//input[@id='lastName']/parent::div/following-sibling::em[@role= 'alert']")
	private WebElement errOnLastName;

	public WebElement getErrOnLastName()
	{
		return errOnLastName;
	}

	@FindBy(xpath = "//input[@id='postcode']/preceding-sibling::span")
	private WebElement lblPostcode;

	public WebElement getLblPostcode()
	{
		return lblPostcode;
	}

	@FindBy(xpath = "//div[@class='validation-container']/input[@id='postcode']")
	private WebElement tBxPostcode;

	public WebElement getTBxPostcode()
	{
		return tBxPostcode;
	}

	@FindBy(xpath = "//input[@id='postcode']/parent::div/following-sibling::em[@role= 'alert']")
	private WebElement errOnPostCode;

	public WebElement getErrOnPostCode()
	{
		return errOnPostCode;
	}

	@FindBy(xpath = "//span[@class='label' and contains(text(), 'Enter SMS code for your security')]")
	private WebElement lblEnterSMSCode;

	public WebElement getLblEnterSMSCode()
	{
		return lblEnterSMSCode;
	}

	@FindBy(xpath = "//span[contains(text(), 'Get SMS code')]")
	private WebElement btnGetSMSCode;

	public WebElement getBtnGetSMSCode()
	{
		return btnGetSMSCode;
	}

	@FindBy(xpath = "//span[contains(text(), 'Register')]")
	private WebElement lnkRegisterLink;

	public WebElement getRegisterLink()
	{
		return lnkRegisterLink;
	}

	@FindBy(xpath = "//input[@id='smsCode']")
	private WebElement tBxSMSCode;

	public WebElement getTBxSMSCode()
	{
		return tBxSMSCode;
	}

	@FindBy(xpath = "//div[@class='codeSentText']")
	private WebElement textIfCodeNotReceived;

	public WebElement getTextIfCodeNotReceived()
	{
		return textIfCodeNotReceived;
	}

	@FindBy(xpath = "//div[@class='codeSentText']/a")
	private WebElement textIfCodeNotReceivedTryagain;

	public WebElement getTextIfCodeNotReceivedTryagain()
	{
		return textIfCodeNotReceivedTryagain;
	}

	@FindBy(xpath = "//span[@class='button-inner']/span[contains(text(), 'Next')]")
	private WebElement btnNext;

	public WebElement getBtnNext()
	{
		return btnNext;
	}

	@FindBy(xpath = "//span[@class='button-inner']/span[contains(text(), 'Cancel')]")
	private WebElement btnCancel;

	public WebElement getBtnCancel()
	{
		return btnCancel;
	}

	@FindBy(xpath = "//li[@class='margin-bottom-2']/div[contains(text(), 'Need help?')]")
	private WebElement lnkNeedHelp;

	public WebElement getLnkNeedHelp()
	{
		return lnkNeedHelp;
	}

	@FindBy(xpath = "//li[@class='margin-bottom-2']/div[contains(text(), 'Need help?')]/following-sibling::span")
	private WebElement lblContactNo;

	public WebElement getLblContactNo()
	{
		return lblContactNo;
	}

	//Page 2 of 2
	@FindBy(xpath = "//aside[@class='signinContainerWrap']/descendant::h1[@class='header-statement heading-two']/span")
	private WebElement lblHeadingRegister;

	public WebElement getLblRegister()
	{
		return lblHeadingRegister;
	}

	@FindBy(xpath = "//div[@class='jq-registerStepOne']/descendant::h1[@class='heading-seven']")
	private WebElement lblHeadingStep2of2;

	public WebElement getLblStep2of2()
	{
		return lblHeadingStep2of2;
	}

	@FindBy(xpath = "//aside[@class='layoutContentAlpha']/descendant::input[@id='username']/preceding-sibling::span")
	private WebElement lblCreateUserName;

	public WebElement getLblCreateUserName()
	{
		return lblCreateUserName;
	}

	@FindBy(xpath = "//input[@id='username']")
	private WebElement tBxUserName;

	public WebElement getTBxUserName()
	{
		return tBxUserName;
	}

	@FindBy(xpath = "//input[@id='username']/parent::div/following-sibling::em[@class='jq-inputError formFieldMessageError']")
	private WebElement tBxErrOnUserName;

	public WebElement getTBxErrOnUserName()
	{
		return tBxErrOnUserName;
	}

	//Must text
	@FindBy(xpath = "//div[@id='nameCheck']/div/h3")
	private WebElement txtHelpTextMustBe;

	public WebElement getTxtHelpTextMayBe()
	{
		return txtHelpTextMustBe;
	}

	//Between 8 and 50 characters
	@FindBy(xpath = "//div[@id='nameCheck']/div/ul/li/span")
	private WebElement txtHelpTextBetween8to50;

	public WebElement getTxtHelpTextBetween8to50()
	{
		return txtHelpTextBetween8to50;
	}

	//A combination of letters and numbers
	@FindBy(xpath = "//div[@id='nameCheck']/div/ul/li[2]/span")
	private WebElement txtHelpTextLettersAndNo;

	public WebElement getTxttxtHelpTextLettersAndNo()
	{
		return txtHelpTextLettersAndNo;
	}

	//Cannot include
	@FindBy(xpath = "//div[@id='nameCheck']/div[2]/h3")
	private WebElement txtHelpTextCanNotInclude;

	public WebElement getTxtHelpTextCanNotInclude()
	{
		return txtHelpTextCanNotInclude;
	}

	//An email address
	@FindBy(xpath = "//div[@id='nameCheck']/div[2]/ul/li[2]/span")
	private WebElement txtHelpTextEmail;

	public WebElement getTxtHelpTextEmail()
	{
		return txtHelpTextEmail;
	}

	//One of these character '&^%$#@!
	@FindBy(xpath = "//div[@id='nameCheck']/div[2]/ul/li[3]/span")
	private WebElement txtHelpTextSpecialChars;

	public WebElement getTxtHelpTextSpecialChars()
	{
		return txtHelpTextSpecialChars;
	}

	@FindBy(xpath = "//input[@id='username']/parent::div/following-sibling::em[@role='alert']")
	private WebElement errOnCreateUserName;

	public WebElement getErrOnCreateUserName()
	{
		return errOnCreateUserName;
	}

	@FindBy(xpath = "//input[@id='password']/preceding-sibling::span")
	private WebElement lblCreatePassword;

	public WebElement getLblCreatePassword()
	{
		return lblCreatePassword;
	}

	@FindBy(xpath = "//input[@id='password']")
	private WebElement tBxPassword;

	public WebElement getTBxPassword()
	{
		return tBxPassword;
	}

	@FindBy(xpath = "//input[@id='password']/parent::div/following-sibling::em[@class='jq-inputError formFieldMessageError']")
	private WebElement tBxErrOnPassword;

	public WebElement getTBxErrOnPassword()
	{
		return tBxErrOnPassword;
	}

	//Must be at least
	@FindBy(xpath = "//div[@id='passwordCheck']/div/h3")
	private WebElement txtHelpTextMustBeAtlest;

	public WebElement getTxtHelpTextMustBeAtlest()
	{
		return txtHelpTextMustBeAtlest;
	}

	//One Letter
	@FindBy(xpath = "//div[@id='passwordCheck']/div/ul/li/span")
	private WebElement txtHelpTextOneLetter;

	public WebElement getTxtHelpTextOneLetter()
	{
		return txtHelpTextOneLetter;
	}

	//One number or special character
	@FindBy(xpath = "//div[@id='passwordCheck']/div/ul/li[2]/span")
	private WebElement txtHelpTextSpecialCharOrNo;

	public WebElement getTxttxtHelpTextSpecialCharOrNo()
	{
		return txtHelpTextSpecialCharOrNo;
	}

	//8 Characters
	@FindBy(xpath = "//div[@id='passwordCheck']/div/ul/li[3]/span")
	private WebElement txtHelpText8Char;

	public WebElement getTxtHelpText8Char()
	{
		return txtHelpText8Char;
	}

	//Cannot include
	@FindBy(xpath = "//div[@id='passwordCheck']/div/ul/li[5]/span")
	private WebElement txtHelpTextCannotInclude;

	public WebElement getTxtHelpTextCannotInclude()
	{
		return txtHelpTextCannotInclude;
	}

	//Your user name
	@FindBy(xpath = "//div[@id='passwordCheck']/div/ul/li[6]/span")
	private WebElement txtHelpTextCannotIncludeUserName;

	public WebElement getTxtHelpTextCannotIncludeUserName()
	{
		return txtHelpTextCannotIncludeUserName;
	}

	@FindBy(xpath = "//input[@id='confirmPassword']/preceding-sibling::span")
	private WebElement lblResetPassword;

	public WebElement getLblResetPassword()
	{
		return lblResetPassword;
	}

	@FindBy(xpath = "//input[@id='confirmPassword']")
	private WebElement tBxResetPassword;

	public WebElement getTBxtBxResetPassword()
	{
		return tBxResetPassword;
	}

	@FindBy(xpath = "//input[@id='confirmPassword']/following-sibling::em")
	private WebElement tBxErrResetPassword;

	public WebElement getTBxtBxErrResetPassword()
	{
		return tBxErrResetPassword;
	}

	@FindBy(xpath = "//span[@class='button-inner']/span[@class='label-content']")
	private WebElement btnSignIn;

	public WebElement getBtnSignIn()
	{
		return btnSignIn;
	}

	@FindBy(xpath = "//div[@class='jq-FormErrorMessage customFormError']/descendant::span[@class='message']")
	private WebElement errOnMismatchOfDataOnRegister;

	public WebElement getErrOnMismatchOfDataOnRegister()
	{
		return errOnMismatchOfDataOnRegister;
	}

	@FindBy(xpath = "//input[@id='regStepTC1']")
	private WebElement checkBoxTermsAndConditions;

	public WebElement getCheckBoxTermsAndConditions()
	{
		return checkBoxTermsAndConditions;
	}

	@FindBy(xpath = "//input[@id='regStepTC1']/parent::span/label[@class='formLabelCheckBox']")
	private WebElement textIAgreeToTheTermAndConditions;

	public WebElement getTextIAgreeToTheTermAndConditions()
	{
		return textIAgreeToTheTermAndConditions;
	}

	//Scenario: Verify the functionality of Sign In button
	@FindBy(xpath = "//span[contains(text(), 'Sign in')]")
	private WebElement btnSignInOnRegister2Of2;

	public WebElement getBtnSignInOnRegister2Of2()
	{
		return btnSignInOnRegister2Of2;
	}
}
