package steps;

import static junit.framework.Assert.*;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

import org.openqa.selenium.NoSuchElementException;

import pages.BTAccountAndBillers.BTAccountAndBillersPage;
import pages.logon.LogonPage;

public class BTAccountAndBillersSteps extends ScenarioSteps
{
	LogonPage logonpage;
	BTAccountAndBillersPage onBTAccountAndBillersPage;
	String previousNickNameValue;

	StringBuffer verificationErrors = new StringBuffer();

	////////////////////////////////////////////////////////

	///////////////////////////////////////
	public BTAccountAndBillersSteps(Pages pages)
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
	public void clickOnExpander() throws Throwable
	{

		onBTAccountAndBillersPage.getAllSectionsExpander().click();
	}

	@Step
	public void verifyPrimaryLinkedAccountIcon() throws Throwable
	{
		assertTrue(onBTAccountAndBillersPage.getPrimaryLinkedAccountIcon().isDisplayed());
	}

	@Step
	public void clickOnPrimaryLinkedAccountMenuButton() throws Throwable
	{
		onBTAccountAndBillersPage.getMenuButtonForPrimaryLinkedAccount().click();
	}

	@Step
	public void verifyNonAvailabilityOfRemoveOption() throws Throwable
	{

		assertFalse(onBTAccountAndBillersPage.getMenuOptionRemoveForPrimaryLinkedAccount().isDisplayed());

	}

	@Step
	public void navigateToMoveMoney()
	{
		getDriver().get("http://localhost:8000/#ng/account/movemoney/accountsandbillers");
	}

	@Step
	public void checkAccAndBillersTabNotDisplayed()
	{
		try
		{
			onBTAccountAndBillersPage.getBtAccAndBillers().isDisplayed();
		}
		catch (NoSuchElementException e)
		{
			assertTrue(true);
		}

	}

	@Step
	public void checkAccAndBillersTabFromDirectLink() throws Throwable
	{
		getDriver().get("http://localhost:8000/#ng/account/movemoney/accountsandbillers");
		//Temp : Expected is access deny page should be displayed
	}

	@Step
	public void clickOnAccAndBillers() throws Throwable
	{
		/* Temporary arrangement to get to accounts and billers tab */
		getDriver().get("http://localhost:8000/#ng/account/movemoney/accountsandbillers");

		/* Un-comment below code when accounts and billers tab is accessible by logging in and clicking on the tab
		onBTAccountAndBillersPage.getBtAccAndBillers().click(); */
	}

	@Step
	public void checkHeadingMsgOfAccAndBillers() throws Throwable
	{
		Thread.sleep(5000);
		assertEquals("Linked accounts can be used to transfer money to and from your Panorama account. Pay anyone accounts can be used by authorised users to make payments to others",
			onBTAccountAndBillersPage.getHeadingMessage().getText());

	}

	@Step
	public void checkExpandAllToggleLink() throws Throwable
	{
		assertTrue(onBTAccountAndBillersPage.getAllSectionsExpander().isDisplayed());
		/*assertTrue(onBTAccountAndBillersPage.getAllSectionsExpanderText().isDisplayed());*/

		/*assertTrue(onBTAccountAndBillersPage.getLinkedAccountsText().isDisplayed());
		assertTrue(onBTAccountAndBillersPage.getPayAnyoneAccountsText().isDisplayed());
		assertTrue(onBTAccountAndBillersPage.getBPayBillersText().isDisplayed());

		assertTrue(onBTAccountAndBillersPage.getLinkedAccountRightArrow().isDisplayed());
		assertTrue(onBTAccountAndBillersPage.getPayAnyoneAccountRightArrow().isDisplayed());
		assertTrue(onBTAccountAndBillersPage.getBPayBillersRightArrow().isDisplayed());*/

	}

	@Step
	public void clickOnExpandAllToggleLink() throws Throwable
	{
		onBTAccountAndBillersPage.getAllSectionsExpander().click();
	}

	@Step
	public void verifyAllSectionCollapser() throws Throwable
	{
		assertTrue(onBTAccountAndBillersPage.getAllSectionsCollapser().isDisplayed());
	}

	@Step
	public void clickOnCollapseAllToggleLink() throws Throwable
	{
		onBTAccountAndBillersPage.getAllSectionsCollapser().click();
	}

	/////////////////////////////////////////////////////////////

	@Step
	public void checkExpandRightArrowIconForEachSection() throws Throwable
	{
		assertTrue(onBTAccountAndBillersPage.getLinkedAccountRightArrow().isDisplayed());
		assertTrue(onBTAccountAndBillersPage.getPayAnyoneAccountRightArrow().isDisplayed());
		assertTrue(onBTAccountAndBillersPage.getBPayBillersRightArrow().isDisplayed());
	}

	@Step
	public void clickExpandRightArrowIconForEachSection() throws Throwable
	{
		onBTAccountAndBillersPage.getLinkedAccountRightArrow().click();
		Thread.sleep(1000);
		onBTAccountAndBillersPage.getPayAnyoneAccountRightArrow().click();
		Thread.sleep(1000);
		onBTAccountAndBillersPage.getBPayBillersRightArrow().click();
	}

	@Step
	public void checkEachSectionIsExpanded() throws Throwable
	{
		assertTrue(onBTAccountAndBillersPage.getLinkedAccountDownArrow().isDisplayed());
		assertTrue(onBTAccountAndBillersPage.getPayAnyoneAccountDownArrow().isDisplayed());
		assertTrue(onBTAccountAndBillersPage.getBPayBillersDownArrow().isDisplayed());
	}

	@Step
	public void clickCollapseDownArrowIconForEachSection() throws Throwable
	{
		onBTAccountAndBillersPage.getLinkedAccountDownArrow().click();
		Thread.sleep(1000);
		onBTAccountAndBillersPage.getPayAnyoneAccountDownArrow().click();
		Thread.sleep(1000);
		onBTAccountAndBillersPage.getBPayBillersDownArrow().click();
	}

	@Step
	public void checkEachSectionIsCollapsed() throws Throwable
	{
		assertTrue(onBTAccountAndBillersPage.getLinkedAccountRightArrow().isDisplayed());
		assertTrue(onBTAccountAndBillersPage.getPayAnyoneAccountRightArrow().isDisplayed());
		assertTrue(onBTAccountAndBillersPage.getBPayBillersRightArrow().isDisplayed());
	}

	/* Verify static headings of each section and availability of modal links for users with Update payees & payment limits permission */
	@Step
	public void checkEachSectionHeading() throws Throwable
	{
		assertEquals("Linked accounts", onBTAccountAndBillersPage.getLinkedAccountsText().getText());
		assertEquals("Pay Anyone accounts", onBTAccountAndBillersPage.getPayAnyoneAccountsText().getText());
		assertEquals("BPay billers", onBTAccountAndBillersPage.getBPayBillersText().getText());
	}

	@Step
	public void checkAddLinkedAccountLink() throws Throwable
	{
		assertTrue(onBTAccountAndBillersPage.getAddLinkedAccountLink().isDisplayed());

	}

	@Step
	public void checkAddAccountAndChangeDailyLimitLink() throws Throwable
	{
		assertTrue(onBTAccountAndBillersPage.getAddAccountLink().isDisplayed());
		assertTrue(onBTAccountAndBillersPage.getChangeDailyLimitLinkPayAnyone().isDisplayed());
	}

	@Step
	public void checkAddBillerAndChangeDailyLimitLink() throws Throwable
	{
		assertTrue(onBTAccountAndBillersPage.getAddBillerLink().isDisplayed());
		assertTrue(onBTAccountAndBillersPage.getChangeDailyLimitLinkBPay().isDisplayed());
	}

	/*#######Linked accounts section#####
	 Scenario: Verifying 'Account nickname' field for Pay anyone section for user having Update payees & payment limits permission*/

	@Step
	public void clickOnExpanderIconForLinkedAccountSection() throws Throwable
	{
		onBTAccountAndBillersPage.getLinkedAccountRightArrow().click();

	}

	@Step
	public void checkWhetherNicknameFieldEditableForLinkedAccount() throws Throwable
	{
		onBTAccountAndBillersPage.getAccountNicknameFieldForLinkedAccountSection().clear();
		onBTAccountAndBillersPage.getAccountNicknameFieldForLinkedAccountSection().click();
		onBTAccountAndBillersPage.getAccountNicknameFieldForLinkedAccountSection().sendKeys("dummy nickname");
		String fieldValue = onBTAccountAndBillersPage.getAccountNicknameFieldForLinkedAccountSection().getText();
		assertEquals(onBTAccountAndBillersPage.getAccountNicknameFieldForLinkedAccountSection().getText(), "dummy nickname");
	}

	@Step
	public void checkNicknameSaveAndCancelButtonForLinkedAccount() throws Throwable
	{
		assertTrue(onBTAccountAndBillersPage.getNicknameSaveButtonForLinkedAccountSection().isDisplayed());
		assertTrue(onBTAccountAndBillersPage.getNicknameCancelButtonForLinkedAccountSection().isDisplayed());
	}

	@Step
	public void enteringNewNicknameAndSaving() throws Throwable
	{
		onBTAccountAndBillersPage.getAccountNicknameFieldForLinkedAccountSection().sendKeys("dummy nickname");
		onBTAccountAndBillersPage.getNicknameSaveButtonForLinkedAccountSection().click();
	}

	@Step
	public void verifyNewAccountNicknameInNicknameField() throws Throwable
	{
		String fieldValue = onBTAccountAndBillersPage.getAccountNicknameFieldForLinkedAccountSection().getText();
		assertEquals(onBTAccountAndBillersPage.getAccountNicknameFieldForLinkedAccountSection().getText(), "dummy nickname");
		assertFalse(onBTAccountAndBillersPage.getNicknameSaveButtonForLinkedAccountSection().isDisplayed());
		assertFalse(onBTAccountAndBillersPage.getNicknameCancelButtonForLinkedAccountSection().isDisplayed());

	}

	@Step
	public void enteringNewNicknameandCanceling() throws Throwable
	{

		previousNickNameValue = onBTAccountAndBillersPage.getAccountNicknameFieldForLinkedAccountSection().getText();
		onBTAccountAndBillersPage.getAccountNicknameFieldForLinkedAccountSection().sendKeys("new nickname");
		onBTAccountAndBillersPage.getNicknameCancelButtonForLinkedAccountSection().click();

	}

	@Step
	public void verifyingNicknameUnchangedOnCanceling() throws Throwable
	{
		assertEquals(onBTAccountAndBillersPage.getAccountNicknameFieldForLinkedAccountSection().getText(), previousNickNameValue);
		assertFalse(onBTAccountAndBillersPage.getNicknameSaveButtonForLinkedAccountSection().isDisplayed());
		assertFalse(onBTAccountAndBillersPage.getNicknameCancelButtonForLinkedAccountSection().isDisplayed());

	}

	/*Verifying 'Account nickname' field for linked account section for user NOT having Update payees & payment limits permission */

	@Step
	public void verifyingNicknameInNonEditableBoxForLinkedAccount() throws Throwable
	{
		assertTrue(onBTAccountAndBillersPage.getNicknameInNonEditableBox().isDisplayed());
		assertFalse(onBTAccountAndBillersPage.getAccountNicknameFieldForLinkedAccountSection().isDisplayed());

	}

	/* Scenario: Verifying Menu button for accounts added in linked account section for user having Make a payment - linked accounts permission*/

	@Step
	public void clickOnMenuButtonForLinkedAccount() throws Throwable
	{

		onBTAccountAndBillersPage.getMenuOptionForLinkedAccount().click();
	}

	@Step
	public void verifyOptionsInLinkedAccountMenu() throws Throwable
	{

		assertTrue(onBTAccountAndBillersPage.getMenuOptionMakeAPaymentForLinkedAccount().isDisplayed());
		assertTrue(onBTAccountAndBillersPage.getMenuOptionMakeADepositForLinkedAccount().isDisplayed());
	}

	@Step
	public void clickOnMakeAPaymentOptionForLinkedAccount() throws Throwable
	{

		onBTAccountAndBillersPage.getMenuOptionMakeAPaymentForLinkedAccount().click();

	}

	@Step
	public void verifyPrefilledValueOnMakeAPaymentScreen() throws Throwable
	{

		// Functionality in the application is not working yet. 		
	}

	@Step
	public void clickOnMakeADepositOptionForLinkedAccount() throws Throwable
	{

		onBTAccountAndBillersPage.getMenuOptionMakeADepositForLinkedAccount().click();
	}

	@Step
	public void verifyPrefilledValueOnMakeADepositScreen() throws Throwable
	{

		// Functionality in the application is not working yet. 		
	}

	/* Scenario: Verifying Menu button for accounts added in linked account section for user having Update payees & payment limits permission */

	@Step
	public void clickMenuButtonForNonPrimaryAccount() throws Throwable
	{
		onBTAccountAndBillersPage.getMenuOptionForNonPrimaryLinkedAccount().click();

	}

	@Step
	public void verifyMenuOptionsInNonPrimaryLinkedAccount() throws Throwable
	{
		assertTrue(onBTAccountAndBillersPage.getMenuOptionSetAsPrimaryLinkedAccForNonPrimaryLinkedAccount().isDisplayed());
		assertTrue(onBTAccountAndBillersPage.getMenuOptionRemoveForNonPrimaryLinkedAccount().isDisplayed());

	}

	/* ###Pay anyone accounts section#### */
	/* Scenario: Verifying 'Account nickname' field for Pay anyone section for user having Update payees & payment limits permission */

	@Step
	public void clickExpanderIconForPayAnyoneAccountSection() throws Throwable
	{
		onBTAccountAndBillersPage.getPayAnyoneAccountRightArrow().click();

	}

	@Step
	public void checkWhetherNicknameFieldEditableForPayAnyoneSection() throws Throwable
	{
		onBTAccountAndBillersPage.getAccountNicknameFieldForPayAnyoneAccountSection().clear();
		onBTAccountAndBillersPage.getAccountNicknameFieldForPayAnyoneAccountSection().click();
		onBTAccountAndBillersPage.getAccountNicknameFieldForPayAnyoneAccountSection().sendKeys("dummy nickname");
		String fieldValue = onBTAccountAndBillersPage.getAccountNicknameFieldForPayAnyoneAccountSection().getText();
		assertEquals(onBTAccountAndBillersPage.getAccountNicknameFieldForPayAnyoneAccountSection().getText(), "dummy nickname");

	}

	@Step
	public void checkSaveAndCancelButtonForPayAnyoneSection() throws Throwable
	{
		assertTrue(onBTAccountAndBillersPage.getNicknameSaveButtonForPayAnyoneSection().isDisplayed());
		assertTrue(onBTAccountAndBillersPage.getNicknameCancelButtonForPayAnyoneSection().isDisplayed());

	}

	@Step
	public void enteringNewNicknameAndSavingForPayAnyone() throws Throwable
	{
		onBTAccountAndBillersPage.getAccountNicknameFieldForPayAnyoneAccountSection().sendKeys("pay anyone nickname");
		onBTAccountAndBillersPage.getNicknameSaveButtonForPayAnyoneSection().click();

	}

	@Step
	public void verifyingNewAccountNicknameForPayAnyone() throws Throwable
	{
		String fieldValue = onBTAccountAndBillersPage.getAccountNicknameFieldForPayAnyoneAccountSection().getText();
		assertEquals(onBTAccountAndBillersPage.getAccountNicknameFieldForPayAnyoneAccountSection().getText(),
			"pay anyone nickname");
		assertFalse(onBTAccountAndBillersPage.getNicknameSaveButtonForPayAnyoneSection().isDisplayed());
		assertFalse(onBTAccountAndBillersPage.getNicknameCancelButtonForPayAnyoneSection().isDisplayed());

	}

	@Step
	public void enteringNewNicknameAndCancellingForPayAnyone() throws Throwable
	{
		previousNickNameValue = onBTAccountAndBillersPage.getAccountNicknameFieldForPayAnyoneAccountSection().getText();
		onBTAccountAndBillersPage.getAccountNicknameFieldForPayAnyoneAccountSection().sendKeys("new payanyone nickname");
		onBTAccountAndBillersPage.getNicknameCancelButtonForPayAnyoneSection().click();

	}

	@Step
	public void verifyingNicknameUnchangedOnCancelingForPayAnyone() throws Throwable
	{
		assertEquals(onBTAccountAndBillersPage.getAccountNicknameFieldForPayAnyoneAccountSection().getText(),
			previousNickNameValue);
		assertFalse(onBTAccountAndBillersPage.getNicknameSaveButtonForPayAnyoneSection().isDisplayed());
		assertFalse(onBTAccountAndBillersPage.getNicknameCancelButtonForPayAnyoneSection().isDisplayed());

	}

	/* Scenario: Verifying 'Account nickname' field for Pay anyone account section for user NOT having Update payees & payment limits permission */

	@Step
	public void verifyingNicknameInNonEditableBoxForPayAnyoneAccount() throws Throwable
	{
		assertTrue(onBTAccountAndBillersPage.getNicknameInNonEditableBoxForPayAnyoneAccount().isDisplayed());
		assertFalse(onBTAccountAndBillersPage.getAccountNicknameFieldForPayAnyoneAccountSection().isDisplayed());

	}

	/* Scenario: Verifying Menu button for accounts added in Pay anyone account section for user having Make a BPAY/Pay Anyone Payment permission */

	@Step
	public void clickOnMenuButtonForPayAnyoneAccount() throws Throwable
	{

		onBTAccountAndBillersPage.getMenuButtonForPayAnyoneAccountSection().click();
	}

	@Step
	public void verifyOptionsInPayAnyoneAccountMenu() throws Throwable
	{

		assertTrue(onBTAccountAndBillersPage.getMenuOptionMakeAPaymentForPayAnyoneSection().isDisplayed());

	}

	@Step
	public void clickOnMakeAPaymentOptionForPayAnyoneAccount() throws Throwable
	{

		onBTAccountAndBillersPage.getMenuOptionMakeAPaymentForPayAnyoneSection().click();

	}

	@Step
	public void verifyPrefilledValueOnMakeAPaymentScreenForPayAnyone() throws Throwable
	{

		// Functionality in the application is not working yet. 		
	}

	/* Scenario: Verifying Menu options for accounts added in Pay anyone account section for user having both Update payees & payment limits and Make a BPAY/Pay Anyone Payment permission */

	@Step
	public void verifyOptionsInPayAnyoneAccountForMultiplePermissions() throws Throwable
	{
		assertTrue(onBTAccountAndBillersPage.getMenuOptionMakeAPaymentForPayAnyoneSection().isDisplayed());
		assertTrue(onBTAccountAndBillersPage.getMenuOptionRemoveForPayAnyoneSection().isDisplayed());

	}

	/*######## BPay billers section #######*/

	/* Scenario: Verifying 'biller nickname' field for BPay billers section for user NOT having Update payees & payment limits permission */

	@Step
	public void clickExpanderIconForBPayBillerSection() throws Throwable
	{
		onBTAccountAndBillersPage.getBPayBillersRightArrow().click();

	}

	@Step
	public void checkWhetherBillerNicknameFieldEditableForBillersSection() throws Throwable
	{
		onBTAccountAndBillersPage.getBillerNicknameFieldForBPayBillersAccountSection().clear();
		onBTAccountAndBillersPage.getBillerNicknameFieldForBPayBillersAccountSection().click();
		onBTAccountAndBillersPage.getBillerNicknameFieldForBPayBillersAccountSection().sendKeys("dummy nickname");
		String fieldValue = onBTAccountAndBillersPage.getBillerNicknameFieldForBPayBillersAccountSection().getText();
		assertEquals(onBTAccountAndBillersPage.getBillerNicknameFieldForBPayBillersAccountSection().getText(), "dummy nickname");

	}

	@Step
	public void checkSaveAndCancelButtonForBPayBillersSection() throws Throwable
	{
		assertTrue(onBTAccountAndBillersPage.getNicknameSaveButtonForBPayBillersSection().isDisplayed());
		assertTrue(onBTAccountAndBillersPage.getNicknameCancelButtonForBPayBillersSection().isDisplayed());

	}

	@Step
	public void enteringNewNicknameAndSavingForBPayBillers() throws Throwable
	{
		onBTAccountAndBillersPage.getBillerNicknameFieldForBPayBillersAccountSection().sendKeys("Biller nickname");
		onBTAccountAndBillersPage.getNicknameSaveButtonForBPayBillersSection().click();

	}

	@Step
	public void verifyingNewAccountNicknameForBPayBillers() throws Throwable
	{
		String fieldValue = onBTAccountAndBillersPage.getBillerNicknameFieldForBPayBillersAccountSection().getText();
		assertEquals(onBTAccountAndBillersPage.getBillerNicknameFieldForBPayBillersAccountSection().getText(), "biller nickname");
		assertFalse(onBTAccountAndBillersPage.getNicknameSaveButtonForBPayBillersSection().isDisplayed());
		assertFalse(onBTAccountAndBillersPage.getNicknameCancelButtonForBPayBillersSection().isDisplayed());

	}

	@Step
	public void enteringNewNicknameAndCancellingForBPayBillers() throws Throwable
	{
		previousNickNameValue = onBTAccountAndBillersPage.getBillerNicknameFieldForBPayBillersAccountSection().getText();
		onBTAccountAndBillersPage.getBillerNicknameFieldForBPayBillersAccountSection().sendKeys("new biller nickname");
		onBTAccountAndBillersPage.getNicknameCancelButtonForBPayBillersSection().click();

	}

	@Step
	public void verifyingNicknameUnchangedOnCancelingForBPayBillers() throws Throwable
	{
		assertEquals(onBTAccountAndBillersPage.getBillerNicknameFieldForBPayBillersAccountSection().getText(),
			previousNickNameValue);
		assertFalse(onBTAccountAndBillersPage.getNicknameSaveButtonForBPayBillersSection().isDisplayed());
		assertFalse(onBTAccountAndBillersPage.getNicknameCancelButtonForBPayBillersSection().isDisplayed());

	}

	/* Scenario: Verifying 'biller nickname' field for BPay billers section for user NOT having Update payees & payment limits permission */

	@Step
	public void verifyingNicknameInNonEditableBoxForBPayBillers() throws Throwable
	{
		assertTrue(onBTAccountAndBillersPage.getNicknameInNonEditableBoxForBPayBillersAccount().isDisplayed());
		assertFalse(onBTAccountAndBillersPage.getBillerNicknameFieldForBPayBillersAccountSection().isDisplayed());

	}

	/*	Scenario: Verifying Menu button for accounts added in BPay billers section for user having Make a BPAY/Pay Anyone Payment permission */

	@Step
	public void clickOnMenuButtonForBPayBillerAccount() throws Throwable
	{

		onBTAccountAndBillersPage.getMenuButtonForBPayBillersSection().click();
	}

	@Step
	public void verifyOptionsInBPayBillerAccountMenu() throws Throwable
	{

		assertTrue(onBTAccountAndBillersPage.getMenuOptionMakeAPaymentForBPayBillersSection().isDisplayed());

	}

	@Step
	public void clickOnMakeAPaymentOptionForBPayBillerAccount() throws Throwable
	{

		onBTAccountAndBillersPage.getMenuOptionMakeAPaymentForBPayBillersSection().click();

	}

	@Step
	public void verifyPrefilledValueOnMakeAPaymentScreenForBPayBillers() throws Throwable
	{

		// Functionality in the application is not working yet. 		
	}

	/* Scenario: Verifying Menu options for accounts added in BPay billers section for user having both Update payees & payment limits and Make a BPAY/Pay Anyone Payment permission */

	@Step
	public void verifyOptionsInBPayBillerAccountMenuForMultiplePermissions() throws Throwable
	{
		assertTrue(onBTAccountAndBillersPage.getMenuOptionMakeAPaymentForBPayBillersSection().isDisplayed());
		assertTrue(onBTAccountAndBillersPage.getMenuOptionRemoveForBPayBillersSection().isDisplayed());

	}

}
