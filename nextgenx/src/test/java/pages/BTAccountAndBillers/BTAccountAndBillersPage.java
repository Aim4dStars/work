package pages.BTAccountAndBillers;

import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class BTAccountAndBillersPage extends PageObject
{
	public BTAccountAndBillersPage(WebDriver driver)
	{
		super(driver);
	}

	@FindBy(xpath = "//div[@class='mvc-menutabs']/descendant::a[contains(text(), 'Accounts and billers')]")
	private WebElement isTabAccountAndBillers;

	public WebElement getTabAccountAndBillers()
	{
		return isTabAccountAndBillers;
	}

	@FindBy(xpath = "//div[@data-view-component='expandcollapseall']/preceding-sibling::div/span")
	private WebElement headingMessage;

	public WebElement getHeadingMessage()
	{
		return headingMessage;
	}

	@FindBy(xpath = "//div[@data-view-component='expandcollapseall']/descendant::span[@class='expand-collapse-all-icon icon icon-nested icon-expand-all']")
	private WebElement allSectionsExpander;

	public WebElement getAllSectionsExpander()
	{
		return allSectionsExpander;
	}

	@FindBy(xpath = "//div[@data-view-component='expandcollapseall']/descendant::span[@class='expand-collapse-all-label']")
	private WebElement allSectionsExpanderText;

	public WebElement getAllSectionsExpanderText()
	{
		return allSectionsExpanderText;
	}

	@FindBy(xpath = "//div[@data-view-component='expandcollapseall']/descendant::span[@class='expand-collapse-all-icon icon icon-nested icon-collapse-all']")
	private WebElement allSectionsCollapser;

	public WebElement getAllSectionsCollapser()
	{
		return allSectionsCollapser;
	}

	@FindBy(xpath = "//div[@data-view-component='expandcollapseall']/descendant::span[@class='expand-collapse-all-label']")
	private WebElement allSectionsCollapserText;

	public WebElement getAllSectionsCollapserText()
	{
		return allSectionsCollapserText;
	}

	@FindBy(xpath = "//div[@class='mvc-linkedaccount']/descendant::span[@class='icon icon-section-default icon-arrow-right']")
	private WebElement linkedAccountRightArrow;

	public WebElement getLinkedAccountRightArrow()
	{
		return linkedAccountRightArrow;
	}

	@FindBy(xpath = "//div[@class='mvc-linkedaccount']/descendant::span[@class='icon icon-section-default icon-arrow-down']")
	private WebElement linkedAccountDownArrow;

	public WebElement getLinkedAccountDownArrow()
	{
		return linkedAccountDownArrow;
	}

	@FindBy(xpath = "//div[@class='mvc-payanyoneaccount']/descendant::span[@class='icon icon-section-default icon-arrow-right']")
	private WebElement payAnyoneAccountRightArrow;

	public WebElement getPayAnyoneAccountRightArrow()
	{
		return payAnyoneAccountRightArrow;
	}

	@FindBy(xpath = "//div[@class='mvc-payanyoneaccount']/descendant::span[@class='icon icon-section-default icon-arrow-down']")
	private WebElement payAnyoneAccountDownArrow;

	public WebElement getPayAnyoneAccountDownArrow()
	{
		return payAnyoneAccountDownArrow;
	}

	@FindBy(xpath = "//div[@class='mvc-bpaybillers']/descendant::span[@class='icon icon-section-default icon-arrow-right']")
	private WebElement bPayBillersRightArrow;

	public WebElement getBPayBillersRightArrow()
	{
		return bPayBillersRightArrow;
	}

	@FindBy(xpath = "//div[@class='mvc-bpaybillers']/descendant::span[@class='icon icon-section-default icon-arrow-down']")
	private WebElement bPayBillersDownArrow;

	public WebElement getBPayBillersDownArrow()
	{
		return bPayBillersDownArrow;
	}

	@FindBy(xpath = "//span[@class='view-addlinkedaccount']/descendant::button[@class=' btn- btn-action-tertiary']")
	private WebElement addLinkedAccountLink;

	public WebElement getAddLinkedAccountLink()
	{
		return addLinkedAccountLink;
	}

	@FindBy(xpath = "//span[@class='view-addaccount']/descendant::button[@class=' btn- btn-action-tertiary']")
	private WebElement addAccountLink;

	public WebElement getAddAccountLink()
	{
		return addAccountLink;
	}

	@FindBy(xpath = "//span[@class='view-addbiller']/descendant::button[@class=' btn- btn-action-tertiary']")
	private WebElement addBillerLink;

	public WebElement getAddBillerLink()
	{
		return addBillerLink;
	}

	@FindBy(xpath = "//div[@class='mvc-payanyoneaccount']/descendant::button[@class=' btn- btn-action-tertiary']")
	private WebElement changeDailyLimitLinkPayAnyone;

	public WebElement getChangeDailyLimitLinkPayAnyone()
	{
		return changeDailyLimitLinkPayAnyone;
	}

	@FindBy(xpath = "//div[@class='mvc-bpaybillers']/descendant::button[@class=' btn- btn-action-tertiary']")
	private WebElement changeDailyLimitLinkBPay;

	public WebElement getChangeDailyLimitLinkBPay()
	{
		return changeDailyLimitLinkBPay;
	}

	@FindBy(xpath = "//div[@class='mvc-linkedaccount']/descendant::div[@class='heading-five']")
	private WebElement linkedAccountsText;

	public WebElement getLinkedAccountsText()
	{
		return linkedAccountsText;
	}

	@FindBy(xpath = "//div[@class='mvc-payanyoneaccount']/descendant::div[@class='heading-five']")
	private WebElement payAnyoneAccounts;

	public WebElement getPayAnyoneAccountsText()
	{
		return payAnyoneAccounts;
	}

	@FindBy(xpath = "//div[@class='mvc-bpaybillers']/descendant::div[@class='heading-five']")
	private WebElement BPayBillersText;

	public WebElement getBPayBillersText()
	{
		return BPayBillersText;
	}

	@FindBy(xpath = "//th[@id='table-c43-header-0']")
	private WebElement accountDetailsTextforLinkedAccount;

	public WebElement getAccountDetailsTextforLinkedAccount()
	{
		return accountDetailsTextforLinkedAccount;
	}

	@FindBy(xpath = "//th[@id='table-c43-header-2']")
	private WebElement accountNicknameTextforLinkedAccount;

	public WebElement getaccountNicknameTextforLinkedAccount()
	{
		return accountNicknameTextforLinkedAccount;
	}

	@FindBy(xpath = "//th[@id='table-c50-header-0']")
	private WebElement accountDetailsTextForPayAnyone;

	public WebElement getAccountDetailsTextForPayAnyone()
	{
		return accountDetailsTextForPayAnyone;
	}

	@FindBy(xpath = "//th[@id='table-c50-header-2']")
	private WebElement accountNicknameTextForPayAnyone;

	public WebElement getAccountNicknameTextForPayAnyone()
	{
		return accountNicknameTextForPayAnyone;
	}

	@FindBy(xpath = "//th[@id='table-c58-header-0']")
	private WebElement billerDetailsText;

	public WebElement getBillerDetailsText()
	{
		return billerDetailsText;
	}

	@FindBy(xpath = "//th[@id='table-c58-header-2']")
	private WebElement billerNicknameText;

	public WebElement getBillerNicknameText()
	{
		return billerNicknameText;
	}

	@FindBy(xpath = "//tr[1][@class=' ']/descendant::span[@class='view-tooltip']/descendant::span[@class='default-tooltip-trigger icon icon-primary amber']")
	private WebElement primaryLinkedAccountIcon;

	public WebElement getPrimaryLinkedAccountIcon()
	{
		return primaryLinkedAccountIcon;
	}

	@FindBy(xpath = "//div[@id='qtip-0-content']/descendant::p[@class='tooltip-text']")
	private WebElement primaryLinkedAccountCallOutText;

	public WebElement getPrimaryLinkedAccountCallOutText()
	{
		return primaryLinkedAccountCallOutText;
	}

	@FindBy(xpath = "//td[@headers='table-c43-header-1']/descendant::span[@class='view-icon']/span")
	private WebElement leftGreenArrowForLinkedAccount;

	public WebElement getLeftGreenArrowForLinkedAccount()
	{
		return leftGreenArrowForLinkedAccount;
	}

	@FindBy(xpath = "//td[@headers='table-c43-header-1']/descendant::span[@class='view-icon_2']/span")
	private WebElement rightOrangeArrowForLinkedAccount;

	public WebElement getRightOrangeArrowForLinkedAccount()
	{
		return rightOrangeArrowForLinkedAccount;
	}

	@FindBy(xpath = "//td[@headers='table-c223-header-3']/descendant::div[@class='view-menuaction']/descendant::a[@class='btn-tool']")
	private WebElement menuButtonForLinkedAccount;

	public WebElement getMenuButtonForPrimaryLinkedAccount()
	{
		return menuButtonForLinkedAccount;
	}

	@FindBy(xpath = "//tr[1][@class=' ']/descendant::div[@class='view-menuaction']/descendant::li[@class='menu-item']/descendant::span[contains(text(), 'Make a deposit')]")
	private WebElement menuOptionMakeADeposit;

	public WebElement getMenuOptionMakeADepositForPrimaryLinkedAccount()
	{
		return menuButtonForLinkedAccount;
	}

	@FindBy(xpath = "//tr[1][@class=' ']/descendant::div[@class='view-menuaction']/descendant::li[@class='menu-item']/descendant::span[contains(text(), 'Make a payment')]")
	private WebElement menuOptionMakeAPayment;

	public WebElement getMenuOptionMakeAPaymentForPrimaryLinkedAccount()
	{
		return menuOptionMakeAPayment;
	}

	@FindBy(xpath = "//tr[1][@class=' ']/descendant::div[@class='view-menuaction']/descendant::li[@class='menu-item']/descendant::span[contains(text(), 'Remove')]")
	private WebElement menuOptionRemove;

	public WebElement getMenuOptionRemoveForPrimaryLinkedAccount()
	{
		return menuOptionRemove;
	}

	//Vilas

	@FindBy(xpath = "//a[contains(@href, '#ng/account/movemoney/accountsandbillers')]")
	private WebElement btAccAndBillers;

	public WebElement getBtAccAndBillers()
	{
		return btAccAndBillers;
	}

	//Saurabh

	@FindBy(xpath = "//tr[@class=' ']/td[3]/descendant::input")
	private WebElement AccountNicknameFieldForLinkedAccountSection;

	public WebElement getAccountNicknameFieldForLinkedAccountSection()
	{
		return AccountNicknameFieldForLinkedAccountSection;
	}

	@FindBy(xpath = "//tr[@class=' ']/td[3]/div[2]/span/button[@class=' btn- btn-action-secondary']")
	private WebElement nicknameSaveButtonForLinkedAccountSection;

	public WebElement getNicknameSaveButtonForLinkedAccountSection()
	{
		return nicknameSaveButtonForLinkedAccountSection;
	}

	@FindBy(xpath = "//tr[@class=' ']/td[3]/div[2]/span[2]/button[@class=' btn- btn-action-tertiary']")
	private WebElement nicknameCancelButtonForLinkedAccountSection;

	public WebElement getNicknameCancelButtonForLinkedAccountSection()
	{
		return nicknameCancelButtonForLinkedAccountSection;
	}

	@FindBy(xpath = "//tr[@class=' ']/td[3]")
	private WebElement nicknameInNonEditableBox;

	public WebElement getNicknameInNonEditableBox()
	{
		return nicknameInNonEditableBox;
	}

	@FindBy(xpath = "//tr[@class=' ']/td[4]/div[@class='view-menuaction']/descendant::span[@class='icon icon-view-actions']")
	private WebElement menuOptionForLinkedAccount;

	public WebElement getMenuOptionForLinkedAccount()
	{
		return menuOptionForLinkedAccount;
	}

	@FindBy(xpath = "//tr[@class=' ']/td[4]/div[@class='view-menuaction']/descendant::li[@class='menu-item']/descendant::span[contains(text(), 'Make a payment')]")
	private WebElement menuOptionMakeAPaymentForLinkedAccount;

	public WebElement getMenuOptionMakeAPaymentForLinkedAccount()
	{
		return menuOptionMakeAPaymentForLinkedAccount;
	}

	@FindBy(xpath = "//tr[@class=' ']/td[4]/div[@class='view-menuaction']/descendant::li[@class='menu-item']/descendant::span[contains(text(), 'Make a deposit')]")
	private WebElement menuOptionMakeADepositForLinkedAccount;

	public WebElement getMenuOptionMakeADepositForLinkedAccount()
	{
		return menuOptionMakeAPaymentForLinkedAccount;
	}

	@FindBy(xpath = "//tr[@class=' '][2]/td[4]/div[@class='view-menuaction_2']/descendant::span[@class='icon icon-view-actions']")
	private WebElement menuOptionForNonPrimaryLinkedAccount;

	public WebElement getMenuOptionForNonPrimaryLinkedAccount()
	{
		return menuOptionForNonPrimaryLinkedAccount;
	}

	@FindBy(xpath = "//tr[@class=' ']/td[4]/div[@class='view-menuaction_2']/descendant::li[@class='menu-item']/descendant::span[contains(text(), 'Set as primary account')]")
	private WebElement menuOptionSetAsPrimaryLinkedAccForNonPrimaryLinkedAccount;

	public WebElement getMenuOptionSetAsPrimaryLinkedAccForNonPrimaryLinkedAccount()
	{
		return menuOptionSetAsPrimaryLinkedAccForNonPrimaryLinkedAccount;
	}

	@FindBy(xpath = "//tr[@class=' ']/td[4]/div[@class='view-menuaction_2']/descendant::li[@class='menu-item']/descendant::span[contains(text(), 'Remove')]")
	private WebElement menuOptionRemoveForNonPrimaryLinkedAccount;

	public WebElement getMenuOptionRemoveForNonPrimaryLinkedAccount()
	{
		return menuOptionRemoveForNonPrimaryLinkedAccount;
	}

	@FindBy(xpath = "//div[@class='mvc-payanyoneaccount']/descendant::tr[@class=' ']/descendant::input")
	private WebElement accountNicknameFieldForPayAnyoneAccountSection;

	public WebElement getAccountNicknameFieldForPayAnyoneAccountSection()
	{
		return accountNicknameFieldForPayAnyoneAccountSection;
	}

	@FindBy(xpath = "//div[@class='mvc-payanyoneaccount']/descendant::tr[@class=' ']/td[3]/div[2]/span/button[@class=' btn- btn-action-secondary']")
	private WebElement nicknameSaveButtonForPayAnyoneSection;

	public WebElement getNicknameSaveButtonForPayAnyoneSection()
	{
		return nicknameSaveButtonForPayAnyoneSection;
	}

	@FindBy(xpath = "//div[@class='mvc-payanyoneaccount']/descendant::tr[@class=' ']/td[3]/div[2]/span[2]/button[@class=' btn- btn-action-tertiary']")
	private WebElement nicknameCancelButtonForPayAnyoneSection;

	public WebElement getNicknameCancelButtonForPayAnyoneSection()
	{
		return nicknameCancelButtonForPayAnyoneSection;
	}

	@FindBy(xpath = "//div[@class='mvc-payanyoneaccount']/descendant::tr[@class=' ']/td[3]")
	private WebElement nicknameInNonEditableBoxForPayAnyoneAccount;

	public WebElement getNicknameInNonEditableBoxForPayAnyoneAccount()
	{
		return nicknameInNonEditableBoxForPayAnyoneAccount;
	}

	@FindBy(xpath = "//div[@class='mvc-payanyoneaccount']/descendant::tr[@class=' ']/td[4]/div[@class='view-menuaction']/descendant::span[@class='icon icon-view-actions']")
	private WebElement menuButtonForPayAnyoneAccountSection;

	public WebElement getMenuButtonForPayAnyoneAccountSection()
	{
		return menuButtonForPayAnyoneAccountSection;
	}

	@FindBy(xpath = "//div[@class='mvc-payanyoneaccount']/descendant::tr[@class=' ']//td[4]/div[@class='view-menuaction']/descendant::li[@class='menu-item']/descendant::span[contains(text(), 'Make a payment')]")
	private WebElement menuOptionMakeAPaymentForPayAnyoneSection;

	public WebElement getMenuOptionMakeAPaymentForPayAnyoneSection()
	{
		return menuOptionMakeAPaymentForPayAnyoneSection;
	}

	@FindBy(xpath = "//div[@class='mvc-payanyoneaccount']/descendant::tr[@class=' ']//td[4]/div[@class='view-menuaction']/descendant::li[@class='menu-item']/descendant::span[contains(text(), 'Remove')]")
	private WebElement menuOptionRemoveForPayAnyoneSection;

	public WebElement getMenuOptionRemoveForPayAnyoneSection()
	{
		return menuOptionRemoveForPayAnyoneSection;
	}

	//////////////////////////////////////////////////

	@FindBy(xpath = "//div[@class='mvc-bpaybillertable']/descendant::tr[@class=' ']/descendant::input")
	private WebElement billerNicknameFieldForBPayBillersAccountSection;

	public WebElement getBillerNicknameFieldForBPayBillersAccountSection()
	{
		return billerNicknameFieldForBPayBillersAccountSection;
	}

	@FindBy(xpath = "//div[@class='mvc-bpaybillertable']/descendant::tr[@class=' ']/td[3]/div[2]/span/button[@class=' btn- btn-action-secondary']")
	private WebElement nicknameSaveButtonForBPayBillersSection;

	public WebElement getNicknameSaveButtonForBPayBillersSection()
	{
		return nicknameSaveButtonForBPayBillersSection;
	}

	@FindBy(xpath = "//div[@class='mvc-bpaybillertable']/descendant::tr[@class=' ']/td[3]/div[2]/span[2]/button[@class=' btn- btn-action-tertiary']")
	private WebElement nicknameCancelButtonForBPayBillersSection;

	public WebElement getNicknameCancelButtonForBPayBillersSection()
	{
		return nicknameCancelButtonForBPayBillersSection;
	}

	@FindBy(xpath = "//div[@class='mvc-bpaybillertable']/descendant::tr[@class=' ']/td[3]")
	private WebElement nicknameInNonEditableBoxForBPayBillersAccount;

	public WebElement getNicknameInNonEditableBoxForBPayBillersAccount()
	{
		return nicknameInNonEditableBoxForBPayBillersAccount;
	}

	@FindBy(xpath = "//div[@class='mvc-bpaybillertable']/descendant::tr[@class=' ']/td[4]/div[@class='view-menuaction']/descendant::span[@class='icon icon-view-actions']")
	private WebElement menuButtonForBPayBillersSection;

	public WebElement getMenuButtonForBPayBillersSection()
	{
		return menuButtonForBPayBillersSection;
	}

	@FindBy(xpath = "//div[@class='mvc-bpaybillertable']/descendant::tr[@class=' ']//td[4]/div[@class='view-menuaction']/descendant::li[@class='menu-item']/descendant::span[contains(text(), 'Make a payment')]")
	private WebElement menuOptionMakeAPaymentForBPayBillersSection;

	public WebElement getMenuOptionMakeAPaymentForBPayBillersSection()
	{
		return menuOptionMakeAPaymentForBPayBillersSection;
	}

	@FindBy(xpath = "//div[@class='mvc-bpaybillertable']/descendant::tr[@class=' ']//td[4]/div[@class='view-menuaction']/descendant::li[@class='menu-item']/descendant::span[contains(text(), 'Remove')]")
	private WebElement menuOptionRemoveForBPayBillersSection;

	public WebElement getMenuOptionRemoveForBPayBillersSection()
	{
		return menuOptionRemoveForBPayBillersSection;
	}

}
