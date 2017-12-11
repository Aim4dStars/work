package feature;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import steps.BTAccountAndBillersSteps;

public class BTAccountAndBillers
{

	@Steps
	BTAccountAndBillersSteps onBTAccountAndBillersSteps;

	/* Scenario: Verify general access deny page while accessing account and biller home page with a user not having 'View account reports' permission */

	@Given("I successfully login into panorama system as Investor")
	public void navigateToAccountsAndBillers() throws Throwable
	{
		onBTAccountAndBillersSteps.navigate_To_Accounts_And_Billers();
	}

	@When("I go to the Move money section")
	public void navigateToMoveMoneySection() throws Throwable
	{
		onBTAccountAndBillersSteps.navigateToMoveMoney();
	}

	@Then("I do not see accounts and billers tab")
	public void checkAccAndBillersTabNotPresent() throws Throwable
	{
		onBTAccountAndBillersSteps.checkAccAndBillersTabNotDisplayed();
	}

	@When("I try to access the accounts and biller tab through a previously saved bookmark")
	public void checkAccAndBillersTabFromBookmark() throws Throwable
	{
		onBTAccountAndBillersSteps.checkAccAndBillersTabFromDirectLink();
	}

	@Then("I see general access deny page")
	public void checkGeneralAccessDenyPage() throws Throwable
	{
		//not implemented yet, that,s why keeping it blank.
	}

	/* Below lies all the blank methods describing permissions */

	@Given("I have required View account reports permissions to access 'accounts and billers' tab")
	public void permissionToAccessAccountsAndBillersTab() throws Throwable
	{

	}

	@Given("I have required View account reports permission to access 'accounts and billers' tab and Update payees & payment limits permission")
	public void permissionToAccessAccountsAndBillersTabAndUpdatePermission() throws Throwable
	{

	}

	@Given("I DO NOT have required View account reports permission to access 'accounts and billers' tab")
	public void noViewAccountReportsPermission() throws Throwable
	{

	}

	@Given("I have required View account reports permission to access 'accounts and billers' tab and DO NOT Update payees & payment limits permission")
	public void noUpdatePayeesPermission() throws Throwable
	{

	}

	@Given("I have required View account reports permission to access 'accounts and billers' tab and Make a payment - linked accounts permission")
	public void haveMakeAPaymentLinkedAccountPermission() throws Throwable
	{

	}

	@Given("I have required View account reports permission to access 'accounts and billers' tab and Make a BPAY/Pay Anyone Payment permission")
	public void haveMakeABPayPayAnyoneAccountPermission() throws Throwable
	{

	}

	@Given("I have required View account reports permission to access 'accounts and billers' tab and Update payees & payment limits and Make a BPAY/Pay Anyone Payment permission")
	public void haveUpdatePayeesAndMakeABPayPayAnyoneAccountPermission() throws Throwable
	{

	}

	/* Scenario: Verify account and biller home page standard message and collapse/expand toggle button */

	@When("I click on tab Accounts and billers in the Move money section")
	public void clickOnAccountsAndBillersTab() throws Throwable
	{
		onBTAccountAndBillersSteps.clickOnAccAndBillers();
	}

	@Then("I see standard message on top: Linked accounts can be used to transfer money to and from your Panorama account. Pay anyone accounts can be used by authorised users to make payments to others")
	public void checkHeadingMsgOfAccAndBillersTab() throws Throwable
	{
		onBTAccountAndBillersSteps.checkHeadingMsgOfAccAndBillers();
	}

	@Then("I see a toggle link to expand all sections [Linked accounts, Pay Anyone accounts, and BPay biller] on the screen at the same time")
	public void checkToggleLinkToExpandAllSections() throws Throwable
	{
		onBTAccountAndBillersSteps.checkExpandAllToggleLink();
	}

	@When("I click on the expand toggle link")
	public void clickOnExpandAllToggleLink() throws Throwable
	{
		onBTAccountAndBillersSteps.clickOnExpandAllToggleLink();
	}

	@Then("I see all the three sections [Linked accounts, Pay Anyone accounts, and BPay biller] has been expanded")
	public void verifyAllThreeSectionsExpanded() throws Throwable
	{
		onBTAccountAndBillersSteps.checkEachSectionHeading();
	}

	@Then("the toggle link icon is change to collapse mode")
	public void verifyToggleLinkChangedToCollapser() throws Throwable
	{
		onBTAccountAndBillersSteps.verifyAllSectionCollapser();
	}

	@When("I click on the collapse toggle link")
	public void clickOnCollapseLink() throws Throwable
	{
		onBTAccountAndBillersSteps.clickOnCollapseAllToggleLink();
	}

	@Then("I see all the three sections [Linked accounts, Pay Anyone accounts, and BPay biller] has been collapsed again")
	public void verifyAllThreeSectionsCollapsed() throws Throwable
	{
		onBTAccountAndBillersSteps.checkExpandRightArrowIconForEachSection();
	}

	/* Verify functioning of expand/collapse links for all the three sections [Linked accounts, Pay Anyone accounts, and BPay biller] */

	@Then("I see expand right-arrow icon in front of each section [Linked accounts, Pay Anyone accounts, and BPay biller]")
	public void checkExpandRightIconForEachSec() throws Throwable
	{
		onBTAccountAndBillersSteps.checkExpandRightArrowIconForEachSection();
	}

	@When("I click on the expand right-arrow icon for all the three sections one by one")
	public void clickExpandRightIconForEachSec() throws Throwable
	{
		onBTAccountAndBillersSteps.clickExpandRightArrowIconForEachSection();
	}

	@Then("I see the sections are expanded and the expand right-arrow icon is changed to collapse down-arrow icon for all three sections")
	public void checkEachSectionIsExpanded() throws Throwable
	{
		onBTAccountAndBillersSteps.checkEachSectionIsExpanded();
	}

	@When("I click on the collapse down-arrow icon for all the three sections one by one")
	public void clickExpandDownIconForEachSec() throws Throwable
	{
		onBTAccountAndBillersSteps.clickCollapseDownArrowIconForEachSection();
	}

	@Then("I see the sections are collapsed again and the collapse down-arrow icon is changed to expand right-arrow icon for all three sections")
	public void checkEachSectionIsCollapsed() throws Throwable
	{
		onBTAccountAndBillersSteps.checkEachSectionIsCollapsed();
	}

	/*Verify static headings of each section and availability of modal links for users with Update payees & payment limits permission*/

	@Then("I see three sections with static heading Linked account, Pay anyone account and BPay biller")
	public void checkEachSectionHeadingText() throws Throwable
	{
		onBTAccountAndBillersSteps.checkEachSectionHeading();
	}

	@Then("I see add linked account link against linked account section")
	public void checkAddLinkedAccLink() throws Throwable
	{
		onBTAccountAndBillersSteps.checkAddLinkedAccountLink();
	}

	@Then("I see change daily limit and add account link against Pay anyone account section")
	public void checkAddAccAndChangeDailyLimitLink() throws Throwable
	{
		onBTAccountAndBillersSteps.checkAddAccountAndChangeDailyLimitLink();
	}

	@Then("I see change daily limit and add biller link against BPay biller section")
	public void checkAddBillrAndChangeDailyLimitLink() throws Throwable
	{
		onBTAccountAndBillersSteps.checkAddBillerAndChangeDailyLimitLink();
	}

	/*Scenario: Verifying 'Account nickname' field for linked account section for user having Update payees & payment limits permission*/

	@When("I click on expander icon for linked account section")
	public void clickToExpandLinkedAccountSection() throws Throwable
	{
		onBTAccountAndBillersSteps.clickOnExpanderIconForLinkedAccountSection();
	}

	@Then("I see Account nickname of an added linked account is displayed in editable text box")
	public void checkAccountNicknameInEditableBoxLinkedAcc() throws Throwable
	{
		onBTAccountAndBillersSteps.checkWhetherNicknameFieldEditableForLinkedAccount();
	}

	@Then("I see Save and Cancel button displayed below the corresponding record after I click on the nickname field")
	public void checkNicknameSaveAndCancelButtonForLinkedAcc() throws Throwable
	{
		onBTAccountAndBillersSteps.checkNicknameSaveAndCancelButtonForLinkedAccount();
	}

	@When("I click on Save button after entering a new nickname")
	public void savingAfterEnteringNewNickname() throws Throwable
	{
		onBTAccountAndBillersSteps.enteringNewNicknameAndSaving();
	}

	@Then("I see the new nickname is being displayed in the editable text box and Save and Cancel button disappears")
	public void verifyingNewNicknameInField() throws Throwable
	{
		onBTAccountAndBillersSteps.verifyNewAccountNicknameInNicknameField();
	}

	@When("I click on cancel button after entering a new nickname")
	public void clickCancelAfterNewNickname() throws Throwable
	{
		onBTAccountAndBillersSteps.enteringNewNicknameandCanceling();
	}

	@Then("I see there is no change in the original nickname and Save and Cancel button disappears")
	public void verifyingOriginalNicknameUnchanged() throws Throwable
	{
		onBTAccountAndBillersSteps.verifyingNicknameUnchangedOnCanceling();
	}

	/*Verifying 'Account nickname' field for linked account section for user NOT having Update payees & payment limits permission */

	@Then("I see Account nickname of an added linked account is displayed in read only and non-editable text box")
	public void verifyingNicknameFieldInReadOnlyBox() throws Throwable
	{
		onBTAccountAndBillersSteps.verifyingNicknameInNonEditableBoxForLinkedAccount();
	}

	/* Scenario: Verifying Menu button for accounts added in linked account section for user having Make a payment - linked accounts permission*/

	@When("I click on menu button for any of the added accounts")
	public void clickOnMenuOptionForLinkedAcc() throws Throwable
	{
		onBTAccountAndBillersSteps.clickOnMenuButtonForLinkedAccount();
	}

	@Then("I see two options in the dropdown 1.Make a payment 2. Make a deposit")
	public void verifyOptionsMakeAPaymentMakeADepositInMenuDropDownForLinkedAccount() throws Throwable
	{
		onBTAccountAndBillersSteps.verifyOptionsInLinkedAccountMenu();
	}

	@When("I select Make a payment option from the dropdown")
	public void selectMakeAPaymentOptionLinkedAccount() throws Throwable
	{
		onBTAccountAndBillersSteps.clickOnMakeAPaymentOptionForLinkedAccount();
	}

	@Then("I am navigated to Make a payment screen with selected linked account details pre-filled in the payment form")
	public void verifyPrefilledValueOnMakeAPaymentPage() throws Throwable
	{
		onBTAccountAndBillersSteps.verifyPrefilledValueOnMakeAPaymentScreen();
	}

	@When("I select Make a deposit option from the dropdown")
	public void selectMakeADepositOptionLinkedAccount() throws Throwable
	{
		onBTAccountAndBillersSteps.clickOnMakeADepositOptionForLinkedAccount();
	}

	@Then("I am navigated to Make a deposit screen with selected linked account details pre-filled in the payment form")
	public void verifyPrefilledValueOnMakeADepositPage() throws Throwable
	{
		onBTAccountAndBillersSteps.verifyPrefilledValueOnMakeADepositScreen();
	}

	/* Scenario: Verifying Menu button for accounts added in linked account section for user having Update payees & payment limits permission */

	@When("I click on menu button for any of the added accounts other than primary account")
	public void clickOnMenuButtonForNonPrimaryLinkedACcount() throws Throwable
	{
		onBTAccountAndBillersSteps.clickMenuButtonForNonPrimaryAccount();
	}

	@Then("I see two options in the dropdown 1.Set as primary account 2. Remove")
	public void verifyOptionsSetAsPrimaryAccountRemoveForNonPrimaryLinkedAccount() throws Throwable
	{
		onBTAccountAndBillersSteps.verifyMenuOptionsInNonPrimaryLinkedAccount();
	}

	/* ### Pay anyone section #### */
	/* Scenario: Verifying 'Account nickname' field for Pay anyone section for user having Update payees & payment limits permission */

	@When("I click on expander icon for Pay anyone account section")
	public void clickToExpandPayAnyoneAccountSection() throws Throwable
	{
		onBTAccountAndBillersSteps.clickExpanderIconForPayAnyoneAccountSection();
	}

	@Then("I see Account nickname of an added pay anyone account is displayed in editable text box")
	public void checkAccountNicknameInEditableBoxPayAnyoneAcc() throws Throwable
	{
		onBTAccountAndBillersSteps.checkWhetherNicknameFieldEditableForPayAnyoneSection();
	}

	@Then("I see Save and Cancel button displayed below the corresponding pay anyone account record after I click on the nickname field")
	public void checkNicknameSaveAndCancelButtonForPayAnyoneAcc() throws Throwable
	{
		onBTAccountAndBillersSteps.checkSaveAndCancelButtonForPayAnyoneSection();
	}

	@When("I click on Save button after entering a new nickname below the pay anyone account")
	public void savingAfterEnteringNewNicknameForPayAnyoneAcc() throws Throwable
	{
		onBTAccountAndBillersSteps.enteringNewNicknameAndSavingForPayAnyone();
	}

	@Then("I see the new nickname is being displayed in the editable text box and Save and Cancel button disappears for pay anyone account")
	public void verifyingNewNicknameInFieldForPayAnyoneAcc() throws Throwable
	{
		onBTAccountAndBillersSteps.verifyingNewAccountNicknameForPayAnyone();
	}

	@When("I click on cancel button after entering a new nickname for pay anyone account")
	public void clickCancelAfterNewNicknameForPayAnyone() throws Throwable
	{
		onBTAccountAndBillersSteps.enteringNewNicknameAndCancellingForPayAnyone();
	}

	@Then("I see there is no change in the original nickname and Save and Cancel button disappears below the pay anyone account")
	public void verifyingOriginalNicknameUnchangedForPayAnyone() throws Throwable
	{
		onBTAccountAndBillersSteps.verifyingNicknameUnchangedOnCancelingForPayAnyone();
	}

	/* Scenario: Verifying 'Account nickname' field for Pay anyone account section for user NOT having Update payees & payment limits permission */

	@Then("I see Account nickname of an added Pay anyone account is displayed in read only and non-editable text box")
	public void verifyingNicknameFieldInReadOnlyBoxForPayAnyone() throws Throwable
	{
		onBTAccountAndBillersSteps.verifyingNicknameInNonEditableBoxForPayAnyoneAccount();
	}

	/* Scenario: Verifying Menu button for accounts added in Pay anyone account section for user having Make a BPAY/Pay Anyone Payment permission */

	@When("I click on menu button for any of the added pay anyone accounts")
	public void clickOnMenuOptionForPayAnyoneAcc() throws Throwable
	{
		onBTAccountAndBillersSteps.clickOnMenuButtonForPayAnyoneAccount();
	}

	@Then("I see one option in the dropdown 1.Make a payment for pay anyone account")
	public void verifyOptionsMakeAPaymentInMenuDropDownForPayAnyoneAccount() throws Throwable
	{
		onBTAccountAndBillersSteps.verifyOptionsInPayAnyoneAccountMenu();
	}

	@When("I select Make a payment option from the dropdown for pay anyone account")
	public void selectMakeAPaymentOptionPayAnyoneAccount() throws Throwable
	{
		onBTAccountAndBillersSteps.clickOnMakeAPaymentOptionForPayAnyoneAccount();
	}

	@Then("I am navigated to Make a payment screen with selected pay anyone account details pre-filled in the payment form")
	public void verifyPrefilledValueOnMakeAPaymentPageForPayAnyone() throws Throwable
	{
		onBTAccountAndBillersSteps.verifyPrefilledValueOnMakeAPaymentScreenForPayAnyone();
	}

	/* Scenario: Verifying Menu options for accounts added in Pay anyone account section for user having both Update payees & payment limits and Make a BPAY/Pay Anyone Payment permission */

	@Then("I see two options in the dropdown 1.Make a payment 2.Remove for pay anyone account")
	public void verifyOptionsMakeAPaymentAndRemoveForPayAnyoneAccount() throws Throwable
	{
		onBTAccountAndBillersSteps.verifyOptionsInPayAnyoneAccountForMultiplePermissions();
	}

	/* Scenario: Verifying 'biller nickname' field for BPay billers section for user having Update payees & payment limits permission*/

	@When("I click on expander icon for Bpay billers section")
	public void clickToExpandBPayBillersSection() throws Throwable
	{
		onBTAccountAndBillersSteps.clickExpanderIconForBPayBillerSection();
	}

	@Then("I see biller nickname of an added account is displayed in editable text box")
	public void checkBillerNicknameInEditableBoxBPayBillers() throws Throwable
	{
		onBTAccountAndBillersSteps.checkWhetherBillerNicknameFieldEditableForBillersSection();
	}

	@Then("I see Save and Cancel button displayed below the corresponding record for BPay biller after I click on the nickname field")
	public void checkNicknameSaveAndCancelButtonForBPayBillers() throws Throwable
	{
		onBTAccountAndBillersSteps.checkSaveAndCancelButtonForBPayBillersSection();
	}

	@When("I click on Save button after entering a new nickname for BPay biller")
	public void savingAfterEnteringNewNicknameForBPayBillers() throws Throwable
	{
		onBTAccountAndBillersSteps.enteringNewNicknameAndSavingForBPayBillers();
	}

	@Then("I see the new nickname is being displayed in the editable text box and Save and Cancel button disappears for BPay biller")
	public void verifyingNewNicknameInFieldForBPayBillers() throws Throwable
	{
		onBTAccountAndBillersSteps.verifyingNewAccountNicknameForBPayBillers();
	}

	@When("I click on cancel button after entering a new nickname for BPay biller")
	public void clickCancelAfterNewNicknameForBPayBillers() throws Throwable
	{
		onBTAccountAndBillersSteps.enteringNewNicknameAndCancellingForBPayBillers();
	}

	@Then("I see there is no change in the original nickname and Save and Cancel button disappears for BPay biller")
	public void verifyingOriginalNicknameUnchangedForBPayBillers() throws Throwable
	{
		onBTAccountAndBillersSteps.verifyingNicknameUnchangedOnCancelingForBPayBillers();
	}

	/* Scenario: Verifying 'biller nickname' field for BPay billers section for user NOT having Update payees & payment limits permission */

	@Then("I see biller nickname of an added account is displayed in read only and non-editable text box")
	public void verifyingNicknameFieldInReadOnlyBoxForBPayBillers() throws Throwable
	{
		onBTAccountAndBillersSteps.verifyingNicknameInNonEditableBoxForBPayBillers();
	}

	/*	Scenario: Verifying Menu button for accounts added in BPay billers section for user having Make a BPAY/Pay Anyone Payment permission */

	@When("I click on menu button for any of the added BPay accounts")
	public void clickOnMenuOptionForBPayBillersAcc() throws Throwable
	{
		onBTAccountAndBillersSteps.clickOnMenuButtonForBPayBillerAccount();
	}

	@Then("I see one option in the dropdown 1.Make a payment for BPay Billers")
	public void verifyOptionsMakeAPaymentInMenuDropDownForBPayBillers() throws Throwable
	{
		onBTAccountAndBillersSteps.verifyOptionsInBPayBillerAccountMenu();
	}

	@When("I select Make a payment option from the dropdown for BPay Billers")
	public void selectMakeAPaymentOptionBPayBillersAccount() throws Throwable
	{
		onBTAccountAndBillersSteps.clickOnMakeAPaymentOptionForBPayBillerAccount();
	}

	@Then("I am navigated to Make a payment screen with selected biller account details pre-filled in the payment form")
	public void verifyPrefilledValueOnMakeAPaymentPageForBPayBillers() throws Throwable
	{
		onBTAccountAndBillersSteps.verifyPrefilledValueOnMakeAPaymentScreenForBPayBillers();
	}

	/* Scenario: Verifying Menu options for accounts added in BPay billers section for user having both Update payees & payment limits and Make a BPAY/Pay Anyone Payment permission */

	@Then("I see two options in the dropdown 1.Make a payment 2.Remove for Bpay biller account")
	public void verifyOptionsMakeAPaymentAndRemoveForBPayBillers() throws Throwable
	{
		onBTAccountAndBillersSteps.verifyOptionsInBPayBillerAccountMenuForMultiplePermissions();
	}

}