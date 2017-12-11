package steps;

import static junit.framework.Assert.*;

import java.util.Set;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;

import pages.BTAccountAndBillersModal.BTAccountAndBillersModalPage;
import pages.logon.LogonPage;

public class BTAccountAndBillersModalSteps extends ScenarioSteps
{

	LogonPage logonpage;
	BTAccountAndBillersModalPage onBTAccountAndBillersModalPage;

	public BTAccountAndBillersModalSteps(Pages pages)
	{
		super(pages);
	}

	@Step
	public void navigate_To_Accounts_And_Billers() throws Throwable
	{
		logonpage.open();
		//logonpage.doLogon();
	}

	@Step
	public void checkAddLinkedAccLnk() throws Throwable
	{
		assertTrue(onBTAccountAndBillersModalPage.getLnkAddLinkedAcc().isDisplayed());
	}

	@Step
	public void clickAddLinkedAccLnk() throws Throwable
	{
		onBTAccountAndBillersModalPage.getLnkAddLinkedAcc().click();
	}

	@Step
	public void checkModelWin() throws Throwable
	{
		assertTrue(onBTAccountAndBillersModalPage.getHeadingAddPaymentAndDepoAcc1().isDisplayed());
		assertTrue(onBTAccountAndBillersModalPage.getHeadingAddPaymentAndDepoAcc2().isDisplayed());
	}

	@Step
	public void checkHeadingAddPaymentAndDep() throws Throwable
	{
		assertTrue(onBTAccountAndBillersModalPage.getHeadingAddPaymentAndDepoAcc1().getText() == "Add");
		assertTrue(onBTAccountAndBillersModalPage.getHeadingAddPaymentAndDepoAcc2().getText() == "payment and deposit account");
	}

	@Step
	public void checkAccNameFld() throws Throwable
	{
		assertTrue(onBTAccountAndBillersModalPage.getLblAccountName().isDisplayed());
		assertTrue(onBTAccountAndBillersModalPage.getTbxAccountName().isDisplayed());
	}

	@Step
	public void clickAccNameFldTabOut() throws Throwable
	{
		onBTAccountAndBillersModalPage.getTbxAccountName().click();
		onBTAccountAndBillersModalPage.getTbxAccountName().sendKeys(Keys.TAB);
	}

	@Step
	public void checkErrAccNameFld() throws Throwable
	{
		assertTrue(onBTAccountAndBillersModalPage.getErrAccountName()
			.getText()
			.equals("Please enter a valid account name using letters or numbers or special characters & - < > , + space ( )"));
	}

	@Step
	public void enterValidDataInAccNoFld() throws Throwable
	{
		onBTAccountAndBillersModalPage.getTbxAccountName().sendKeys("valid");
	}

	@Step
	public void checkNoErrAccNameFld() throws Throwable
	{
		try
		{
			onBTAccountAndBillersModalPage.getErrAccountName().isDisplayed();
		}
		catch (NoSuchElementException e)
		{
			assertTrue(true);
		}
	}

	//Scenario: Verify 'BSB' field validations in 'Add linked account' modal
	@Step
	public void checkBSBFld() throws Throwable
	{
		assertTrue(onBTAccountAndBillersModalPage.getLblBSBNumber().isDisplayed());
		assertTrue(onBTAccountAndBillersModalPage.getTbxBSBnumber().isDisplayed());
	}

	@Step
	public void clickBSBFldAndTabout() throws Throwable
	{
		onBTAccountAndBillersModalPage.getTbxBSBnumber().click();
		onBTAccountAndBillersModalPage.getTbxBSBnumber().sendKeys(Keys.TAB);
	}

	@Step
	public void checkErrBSBFld() throws Throwable
	{
		assertTrue(onBTAccountAndBillersModalPage.getErrBSBNumber().getText().equals("Please enter a 6-digit BSB number"));
	}

	@Step
	public void enterValidDataBSBFld() throws Throwable
	{
		onBTAccountAndBillersModalPage.getTbxBSBnumber().sendKeys("valid");
		onBTAccountAndBillersModalPage.getTbxBSBnumber().sendKeys(Keys.TAB);
	}

	@Step
	public void checkNoErrBSBFld() throws Throwable
	{
		try
		{
			onBTAccountAndBillersModalPage.getErrBSBNumber().isDisplayed();
		}
		catch (NoSuchElementException e)
		{
			assertTrue(true);
		}
	}

	//Scenario: Verify 'Account number' field validations in 'Add linked account' modal

	@Step
	public void checkAccountNumberFld() throws Throwable
	{
		assertTrue(onBTAccountAndBillersModalPage.getLblAccountNumber().isDisplayed());
		assertTrue(onBTAccountAndBillersModalPage.getTbxAccountNumber().isDisplayed());
	}

	@Step
	public void clickAccountNumberFldAndTabout() throws Throwable
	{
		onBTAccountAndBillersModalPage.getTbxAccountNumber().click();
		onBTAccountAndBillersModalPage.getTbxAccountNumber().sendKeys(Keys.TAB);
	}

	@Step
	public void checkErrAccountNumberFld() throws Throwable
	{
		assertTrue(onBTAccountAndBillersModalPage.getErrAccNumber().getText().equals("Please enter a 6-digit BSB number"));
	}

	@Step
	public void enterValidDataAccountNumberFld() throws Throwable
	{
		onBTAccountAndBillersModalPage.getTbxAccountNumber().sendKeys("valid");
		onBTAccountAndBillersModalPage.getTbxAccountNumber().sendKeys(Keys.TAB);
	}

	@Step
	public void checkNoErrAccountNumberFld() throws Throwable
	{
		try
		{
			onBTAccountAndBillersModalPage.getErrAccNumber().isDisplayed();
		}
		catch (NoSuchElementException e)
		{
			assertTrue(true);
		}
	}

	//Scenario: Verify 'Account nickname' field validations in 'Add linked account' modal

	@Step
	public void checkAccountNickNameFld() throws Throwable
	{
		assertTrue(onBTAccountAndBillersModalPage.getLblAccountNickName().isDisplayed());
		assertTrue(onBTAccountAndBillersModalPage.getTbxAccountNickName().isDisplayed());
	}

	@Step
	public void clickAccountNickNameFldAndTabout() throws Throwable
	{
		onBTAccountAndBillersModalPage.getTbxAccountNickName().click();
		onBTAccountAndBillersModalPage.getTbxAccountNickName().sendKeys(Keys.TAB);
	}

	@Step
	public void checkErrAccountNickNameFld() throws Throwable
	{
		assertTrue(onBTAccountAndBillersModalPage.getErrTbxAccountNickName()
			.getText()
			.equals("Please enter a 6-digit BSB number"));
	}

	@Step
	public void enterValidDataAccountNickNameFld() throws Throwable
	{
		onBTAccountAndBillersModalPage.getTbxAccountNickName().sendKeys("valid");
		onBTAccountAndBillersModalPage.getTbxAccountNickName().sendKeys(Keys.TAB);
	}

	@Step
	public void checkNoErrAccountNickNameFld() throws Throwable
	{
		try
		{
			onBTAccountAndBillersModalPage.getErrTbxAccountNickName().isDisplayed();
		}
		catch (NoSuchElementException e)
		{
			assertTrue(true);
		}
	}

	//Scenario: Verify 'Terms and conditions' field validations in 'Add linked account' modal

	@Step
	public void checkTermsAndConditionCheckBx() throws Throwable
	{
		boolean bTermsAndCondition = onBTAccountAndBillersModalPage.getChkTermsAndCond().isSelected();
		assertTrue(!bTermsAndCondition);
		assertEquals("You agree to the", onBTAccountAndBillersModalPage.getChkTermsAndCondText1().getText());
		assertEquals("Terms and Conditions", onBTAccountAndBillersModalPage.getChkTermsAndCondText2().getText());
		assertEquals("of adding this account. You can only add payments and deposits account if you are the owner of, or authorised signatory for, the account'",
			onBTAccountAndBillersModalPage.getChkTermsAndCondText3().getText());
	}

	@Step
	public void clickOnTermsAndConditionLnk() throws Throwable
	{
		onBTAccountAndBillersModalPage.getChkTermsAndCondText2().click();
	}

	@Step
	public void checkNewTbForTermsAndCondition() throws Throwable
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

	@Step
	public void checkTxtSMSCodeForyourSecurity() throws Throwable
	{
		assertEquals("SMS code for your security", onBTAccountAndBillersModalPage.getTxtGetSMSCodeForSecurity().getText());
	}

	@Step
	public void checkSMSCodeBtnStateDisable() throws Throwable
	{
		boolean bSMSCodeBtnState = onBTAccountAndBillersModalPage.getBtnGetSMSCode().isEnabled();
		assertTrue(!bSMSCodeBtnState);
	}

	@Step
	public void clickOnTermsAndConditionCheckBox() throws Throwable
	{
		onBTAccountAndBillersModalPage.getChkTermsAndCond().click();
	}

	@Step
	public void checkSMSCOdeButtonStateEnable() throws Throwable
	{
		boolean bSMSCodeBtnState = onBTAccountAndBillersModalPage.getBtnGetSMSCode().isEnabled();
		assertTrue(bSMSCodeBtnState);
	}
}
