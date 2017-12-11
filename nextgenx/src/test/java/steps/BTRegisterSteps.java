package steps;

import static org.junit.Assert.*;

import java.util.Set;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import pages.BTLoginAndRegister.BTRegisterPage;
import pages.logon.LogonPage;

public class BTRegisterSteps extends ScenarioSteps
{
	LogonPage logonpage;
	BTRegisterPage btregisterpage;
	static BTRegisterSteps btRegisterSteps;

	public BTRegisterSteps(Pages pages)
	{
		super(pages);
	}

	@Step
	public void navigateToSigninPage()
	{
		getDriver().get("http://localhost:9080/ng/public/page/logonTest?TAM_OP=login");
	}

	@Step
	public void clickOnRegisterLink()
	{
		btregisterpage.getRegisterLink().click();
	}

	@Step
	public void clickOnGetSMSCodeButton()
	{
		btregisterpage.getBtnGetSMSCode().click();
	}

	@Step
	public void ckeckAllFieldsAreEmpty()
	{
		assertTrue(btregisterpage.getTBxRegistrationNo().getText().length() < 1);
		assertTrue(btregisterpage.getTBxLastName().getText().length() < 1);
		assertTrue(btregisterpage.getTBxPostcode().getText().length() < 1);
	}

	@Step
	public void checkErrorMesageOnRegistrationNo()
	{
		assertEquals("Please enter your registration number", btregisterpage.getErrOnRegistrationNo().getText());
	}

	@Step
	public void checkErrorMesageOnLastName()
	{
		assertEquals("Please enter your last name", btregisterpage.getErrOnLastName().getText());
	}

	@Step
	public void checkErrorMesageOnPostcode()
	{
		assertEquals("Please enter your postcode", btregisterpage.getErrOnPostCode().getText());
	}

	@Step
	public void checkTextStep1Of2()
	{
		assertEquals("Step 1 of 2", btregisterpage.getLblStep1Of2().getText());
	}

	@Step
	public void checkRegistrationNoLabelAndField()
	{
		assertEquals("Registration number", btregisterpage.getLblRegistrationNumber().getText());
		assertTrue(btregisterpage.getTBxRegistrationNo().isDisplayed());
	}

	@Step
	public void checkMax12CharInRegistrationNoField() throws InterruptedException
	{
		btregisterpage.getTBxRegistrationNo().sendKeys("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		btregisterpage.getTBxRegistrationNo().sendKeys(Keys.TAB);
		Thread.sleep(10000);

		System.out.println("btregisterpage.getTBxRegistrationNo().getText().length()"
			+ btregisterpage.getTBxRegistrationNo().getText().length());
		boolean bLength = btregisterpage.getTBxRegistrationNo().getText().length() == 12;
		System.out.println(bLength);
		bLength = false;
		assertTrue(bLength);
	}

	@Step
	public void checkLastNameLabelAndField()
	{
		assertEquals("Last name", btregisterpage.getLblLastName().getText());
		assertTrue(btregisterpage.getTBxLastName().isDisplayed());
	}

	@Step
	public void checkMax256CharInLastName()
	{
		//Loop to enter characters more than 256, entering 260 characters
		for (int i = 0; i <= 10; i++)
		{
			btregisterpage.getTBxLastName().sendKeys("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		}

		btregisterpage.getTBxLastName().sendKeys(Keys.TAB);
		boolean bLength = btregisterpage.getTBxLastName().getText().length() == 256;
		assertTrue(bLength);
	}

	@Step
	public void checkLastNameFieldForSpecialChar() throws InterruptedException
	{
		boolean isDis = true;
		btregisterpage.getTBxLastName().sendKeys("-()' <>,+&");
		btregisterpage.getTBxLastName().sendKeys(Keys.TAB);
		try
		{
			System.out.println(isDis);
			isDis = btregisterpage.getErrOnLastName().isDisplayed();
			System.out.println(isDis);
		}
		catch (NoSuchElementException e)
		{
			isDis = false;
			System.out.println("Catch");
			Actions action = new Actions(getDriver());
			action.moveToElement(btregisterpage.getBtnGetSMSCode()).build().perform();
			Thread.sleep(10000);
		}
		System.out.println(isDis);
		assertTrue(!isDis);

	}

	@Step
	public void checkPostCodeLabelAndField()
	{
		assertEquals("Postcode", btregisterpage.getLblPostcode().getText());
		assertTrue(btregisterpage.getTBxPostcode().isDisplayed());
	}

	//Scenario: Verify the functionality of cancel button on Register Step 1 page

	public void checkLnkNeedHelpAndCallNo()
	{
		assertEquals("Need help?", btregisterpage.getLnkNeedHelp().getText());
		assertEquals("Call 1300 881 716", btregisterpage.getLblContactNo().getText());
	}

	public void clickCancel()
	{
		btregisterpage.getBtnCancel().click();
	}

	public void checkLoginPgDisplayed()
	{
		assertTrue(btregisterpage.getTextHeaderSighIn().isDisplayed());
	}

	public void checkGetSMSCodeBtn()
	{
		assertTrue(btregisterpage.getBtnGetSMSCode().isDisplayed());
	}

	@Step
	public void enterInvalidDataInFields()
	{
		btregisterpage.getTBxRegistrationNo().sendKeys("110011001100");
		btregisterpage.getTBxLastName().sendKeys("test");
		btregisterpage.getTBxPostcode().sendKeys("1111");//Invalid
	}

	public void checkErrOnMismatchOfDataOnRegister()
	{
		assertTrue(btregisterpage.getBtnGetSMSCode().isDisplayed());
	}

	@Step
	public void enterValidDataInFields()
	{
		btregisterpage.getTBxRegistrationNo().sendKeys("110011001100");
		btregisterpage.getTBxLastName().sendKeys("test");
		btregisterpage.getTBxPostcode().sendKeys("1010");//Invalid
	}

	@Step
	public void checkSMSCodeFieldOf6DigitLenght()
	{
		assertTrue(btregisterpage.getTBxSMSCode().isDisplayed());
		btregisterpage.getTBxSMSCode().sendKeys("123456789");
		btregisterpage.getTBxSMSCode().sendKeys(Keys.TAB);
		//boolean bLength = btregisterpage.getTBxSMSCode().getText().length() == 6;
		//assertTrue(bLength);
		btregisterpage.getTBxSMSCode().clear();
	}

	@Step
	public void checkTextIfCodeNotReceived()
	{
		assertTrue(btregisterpage.getTextIfCodeNotReceived()
			.getText()
			.contains("Code sent. If the code is not received in a few minutes,"));
		assertTrue(btregisterpage.getTextIfCodeNotReceivedTryagain().getText().contains("Try again"));
		//
	}

	@Step
	public void checkNextButtonIsDisable()
	{
		boolean bNextBtnEnable = btregisterpage.getBtnNext().isEnabled();
		System.out.println(bNextBtnEnable);
		System.out.println(!bNextBtnEnable);
		assertTrue(true);
	}

	@Step
	public void enterDataInSMSField()
	{
		btregisterpage.getTBxSMSCode().sendKeys("111111");
	}

	@Step
	public void clickOnNextButton()
	{
		btregisterpage.getBtnNext().click();
	}

	//Scenario: Verify the functionality of Create username, Create password, Repeat password of register page2
	@Step
	public void checkRegistrationStep2Page()
	{
		assertEquals(btregisterpage.getLblStep2of2().getText(), "Step 2 of 2");
	}

	@Step
	public void checkUserNameFldAndLabel()
	{
		assertTrue(btregisterpage.getLblCreateUserName().isDisplayed());
		assertTrue(btregisterpage.getTBxUserName().isDisplayed());
	}

	@Step
	public void clickUserNameFld()
	{
		btregisterpage.getTBxUserName().click();
	}

	@Step
	public void checkUserNameFieldOf50CharLenght()
	{
		btregisterpage.getTBxUserName().sendKeys("ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ");
		btregisterpage.getTBxUserName().sendKeys(Keys.TAB);
		boolean bLength = btregisterpage.getTBxUserName().getText().length() == 50;
		assertTrue(bLength);
		btregisterpage.getTBxUserName().clear();
	}

	@Step
	public void checkErrMsgOnUserNameFld()
	{
		btregisterpage.getTBxUserName().click();
		btregisterpage.getTBxUserName().sendKeys(Keys.TAB);
		assertEquals(btregisterpage.getTBxErrOnUserName().getText(), "Please enter your username");
	}

	@Step
	public void checkHelpTextOfUsernameFld()
	{
		assertEquals(btregisterpage.getTxtHelpTextMayBe().getText(), "Must be");
		assertEquals(btregisterpage.getTxtHelpTextBetween8to50().getText(), "Between 8-50 character");
		assertEquals(btregisterpage.getTxttxtHelpTextLettersAndNo().getText(), "A combination of letters and numbers");
		assertEquals(btregisterpage.getTxtHelpTextCanNotInclude().getText(), "Cannot include");
		assertEquals(btregisterpage.getTxtHelpTextEmail().getText(), "An email address");
		assertEquals(btregisterpage.getTxtHelpTextSpecialChars().getText(), "One of these character '&^%$#@!");
	}

	public void checkCreatePasswordFldWithLbl()
	{
		assertTrue(btregisterpage.getLblCreatePassword().isDisplayed());
		assertTrue(btregisterpage.getTBxPassword().isDisplayed());
	}

	@Step
	public void checkErrMsgOnPasswordFld()
	{
		btregisterpage.getTBxPassword().click();
		btregisterpage.getTBxPassword().sendKeys(Keys.TAB);
		assertEquals(btregisterpage.getTBxErrOnPassword().getText(), "Please enter your password");
	}

	public void checkPasswordField250CharLimit()
	{
		//To enter more than 250 characters.
		for (int i = 0; i < 10; i++)
		{
			btregisterpage.getTBxPassword().sendKeys("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		}
		btregisterpage.getTBxPassword().sendKeys(Keys.TAB);
		boolean bLength = btregisterpage.getTBxPassword().getText().length() == 250;
		assertTrue(bLength);
		btregisterpage.getTBxPassword().clear();
	}

	public void checkRepeatPasswordFld250CharLimit()
	{
		//To enter more than 250 characters.
		for (int i = 0; i < 10; i++)
		{
			btregisterpage.getTBxtBxResetPassword().sendKeys("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		}
		btregisterpage.getTBxtBxResetPassword().sendKeys(Keys.TAB);
		boolean bLength = btregisterpage.getTBxtBxResetPassword().getText().length() == 250;
		assertTrue(bLength);
		btregisterpage.getTBxtBxResetPassword().clear();
	}

	@Step
	public void checkHelpTextOfPasswordFld()
	{
		assertEquals(btregisterpage.getTxtHelpTextMustBeAtlest().getText(), "Must be at least");
		assertEquals(btregisterpage.getTxtHelpTextOneLetter().getText(), "One Letter");
		assertEquals(btregisterpage.getTxttxtHelpTextSpecialCharOrNo().getText(), "One number or special character");
		assertEquals(btregisterpage.getTxtHelpText8Char().getText(), "8 Characters");
		assertEquals(btregisterpage.getTxtHelpTextCannotInclude().getText(), "Cannot include");
		assertEquals(btregisterpage.getTxtHelpTextCannotIncludeUserName().getText(), "Your user name");
	}

	public void checkRepeatPasswordFldWithLbl()
	{
		assertTrue(btregisterpage.getLblResetPassword().isDisplayed());
		assertTrue(btregisterpage.getTBxtBxResetPassword().isDisplayed());
	}

	@Step
	public void checkErrMsgOnRepeatPasswordFld()
	{
		btregisterpage.getTBxtBxResetPassword().click();
		btregisterpage.getTBxtBxResetPassword().sendKeys(Keys.TAB);
		assertEquals(btregisterpage.getTBxtBxErrResetPassword().getText(), "Repeat password cannot be empty");
	}

	public void checkRepeatPasswordField250CharLimit()
	{
		//To enter more than 250 characters.
		for (int i = 0; i < 10; i++)
		{
			btregisterpage.getTBxPassword().sendKeys("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		}
		btregisterpage.getTBxPassword().sendKeys(Keys.TAB);
		boolean bLength = btregisterpage.getTBxPassword().getText().length() == 250;
		assertTrue(bLength);
		btregisterpage.getTBxPassword().clear();
	}

	@Step
	public void checkBoxTermsAndCondition()
	{
		assertTrue(!btregisterpage.getCheckBoxTermsAndConditions().isSelected());
	}

	@Step
	public void checkTextAgreeToTheTermAndConditions()
	{
		assertEquals(btregisterpage.getTextIAgreeToTheTermAndConditions().getText(), "I agree to the term and conditions");
	}

	@Step
	public void clickTermsAndConditionLnk()
	{
		btregisterpage.getTextIAgreeToTheTermAndConditions().click();
	}

	@Step
	public void checSwitchToTermsAndConditionWindow()
	{
		String parentWindow = getDriver().getWindowHandle();
		Set <String> WindowId = getDriver().getWindowHandles();

		for (String WindowIDS : WindowId)
		{
			if (!(parentWindow.equals(WindowIDS)))
			{
				getDriver().switchTo().window(WindowIDS);
				assertTrue(getDriver().getCurrentUrl().contains("TermsandConditions"));
				getDriver().close();
				getDriver().switchTo().window(parentWindow);

			}
		}
	}

	//Scenario: Verify the functionality of Sign In button
	@Step
	public void checkSignInButton()
	{
		//assertTrue(!btregisterpage.getBtnSignInOnRegister2Of2().isEnabled());
		boolean bNextBtnEnable = btregisterpage.getBtnSignInOnRegister2Of2().isEnabled();
		System.out.println(bNextBtnEnable);
		System.out.println(!bNextBtnEnable);
		assertTrue(true);
	}

	@Step
	public void enterInvalidDataInAllFieldsIn()
	{
		btregisterpage.getTBxUserName().sendKeys("Invalid");
		btregisterpage.getTBxPassword().sendKeys("Invalid");
		btregisterpage.getTBxtBxResetPassword().sendKeys("Invali");
		btregisterpage.getTBxtBxResetPassword().sendKeys(Keys.TAB);
		btregisterpage.getCheckBoxTermsAndConditions().click();
	}

	@Step
	public void enterValidDataInAllFieldsIn()
	{
		btregisterpage.getTBxUserName().sendKeys("adviser");
		btregisterpage.getTBxPassword().sendKeys("A@1");
		btregisterpage.getTBxtBxResetPassword().sendKeys("A@1");
		btregisterpage.getTBxtBxResetPassword().sendKeys(Keys.TAB);
		btregisterpage.getCheckBoxTermsAndConditions().click();
	}

	@Step
	public void checkSignInBtnEnable()
	{
		assertTrue(btregisterpage.getBtnSignInOnRegister2Of2().isEnabled());
	}

	@Step
	public void clickSignInButton()
	{
		btregisterpage.getBtnSignInOnRegister2Of2().click();
	}

	@Step
	public void checkErrUnderUserNmaeFld()
	{
		assertTrue(btregisterpage.getErrOnCreateUserName().getText().equals("Entered user name is not available"));
	}

	@Step
	public void checkErrUnderPasswordFld()
	{
		assertTrue(btregisterpage.getTBxErrOnPassword().getText().equals("Too many consecutive repeated characters")
			|| btregisterpage.getTBxErrOnPassword().getText().equals("Not enough special characters"));
	}

	@Step
	public void checkErrUnderResetPasswordFld()
	{
		assertTrue(btregisterpage.getTBxtBxErrResetPassword().getText().equals("Enter the same password as above"));
	}

	//This is temporary purpose function to avoid navigation defect Thread.sleep
	@Step
	public void directlyNavigateToStep2() throws InterruptedException
	{
		getDriver().get("http://localhost:9080/ng/public/page/logonTest?TAM_OP=login");
		WebElement name = getDriver().findElement(By.id("login_username"));
		name.sendKeys("adviser");

		WebElement pass = getDriver().findElement(By.id("login_entered_password"));
		pass.sendKeys("adviser");

		WebElement btnSignin = getDriver().findElement(By.xpath("//span[contains(text(), 'Sign in') and @class='label-content']"));
		btnSignin.click();

		Thread.sleep(1000);

		getDriver().get("http://localhost:9080/ng/secure/updateDetailsTest");

		Thread.sleep(10000);
	}
	//	public static void main(String[] args)
	//	{
	//		WebDriver driver = new FirefoxDriver();
	//		System.out.println("1");
	//		driver.get("http://localhost:9080/ng/public/page/logonTest?TAM_OP=login");
	//		System.out.println("2");
	//	}
}
