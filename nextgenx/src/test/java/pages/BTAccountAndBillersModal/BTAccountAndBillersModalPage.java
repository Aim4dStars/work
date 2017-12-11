package pages.BTAccountAndBillersModal;

import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class BTAccountAndBillersModalPage extends PageObject
{
	public BTAccountAndBillersModalPage(WebDriver driver)
	{
		super(driver);
	}

	@FindBy(xpath = "//span[@class='view-addlinkedaccount']/button")
	private WebElement lnkAddLinkedAcc;

	public WebElement getLnkAddLinkedAcc()
	{
		return lnkAddLinkedAcc;
	}

	//Pop-up heading
	@FindBy(xpath = "//div[@class='modal-body']/descendant::h2[@class='header-statement heading-three']/span")
	private WebElement headingAddPaymentAndDepoAcc1;

	public WebElement getHeadingAddPaymentAndDepoAcc1()
	{
		return headingAddPaymentAndDepoAcc1;
	}

	@FindBy(xpath = "//div[@class='modal-body']/descendant::h2[@class='header-statement heading-three']/span[2]")
	private WebElement headingAddPaymentAndDepoAcc2;

	public WebElement getHeadingAddPaymentAndDepoAcc2()
	{
		return headingAddPaymentAndDepoAcc2;
	}

	//Account name
	@FindBy(xpath = "//div[@class='modal-body']/descendant::div[@class='view-accountname']/preceding-sibling::label")
	private WebElement lblAccountName;

	public WebElement getLblAccountName()
	{
		return lblAccountName;
	}

	@FindBy(xpath = "//div[@class='modal-body']/descendant::div[@class='view-accountname']/input")
	private WebElement tbxAccountName;

	public WebElement getTbxAccountName()
	{
		return tbxAccountName;
	}

	@FindBy(xpath = "//div[@class='modal-body']/descendant::div[@class='view-accountname']/parent::div/following-sibling::div/span")
	private WebElement errAccountName;

	public WebElement getErrAccountName()
	{
		return errAccountName;
	}

	//BSB number
	@FindBy(xpath = "//div[@class='modal-body']/descendant::div[@class='view-bsb']/preceding-sibling::label")
	private WebElement lblBSBnumber;

	public WebElement getLblBSBNumber()
	{
		return lblBSBnumber;
	}

	@FindBy(xpath = "//div[@class='modal-body']/descendant::div[@class='view-bsb']/input")
	private WebElement tbxBSBnumber;

	public WebElement getTbxBSBnumber()
	{
		return tbxBSBnumber;
	}

	@FindBy(xpath = "//div[@class='modal-body']/descendant::div[@class='view-bsb']/parent::div/following-sibling::div/span")
	private WebElement errBSBNumber;

	public WebElement getErrBSBNumber()
	{
		return errBSBNumber;
	}

	//Account number
	@FindBy(xpath = "//div[@class='modal-body']/descendant::div[@class='view-accountnumber']/preceding-sibling::label")
	private WebElement lblAccountNumber;

	public WebElement getLblAccountNumber()
	{
		return lblAccountNumber;
	}

	@FindBy(xpath = "//div[@class='modal-body']/descendant::div[@class='view-accountnumber']/input")
	private WebElement tbxAccountNumber;

	public WebElement getTbxAccountNumber()
	{
		return tbxAccountNumber;
	}

	@FindBy(xpath = "//div[@class='modal-body']/descendant::div[@class='view-accountnumber']/parent::div/following-sibling::div/span")
	private WebElement errAccNumber;

	public WebElement getErrAccNumber()
	{
		return errAccNumber;
	}

	//Account nick name
	@FindBy(xpath = "//div[@class='modal-body']/descendant::div[@class='view-accountnickname']/preceding-sibling::label")
	private WebElement lblAccountNickName;

	public WebElement getLblAccountNickName()
	{
		return lblAccountNickName;
	}

	//Account nick name optional text
	@FindBy(xpath = "//div[@class='modal-body']/descendant::div[@class='view-accountnickname']/preceding-sibling::label/span")
	private WebElement lblAccountNickNameOptionalTxt;

	public WebElement getLblAccountNickNameOptionalTxt()
	{
		return lblAccountNickNameOptionalTxt;
	}

	@FindBy(xpath = "//div[@class='modal-body']/descendant::div[@class='view-accountnickname']/input")
	private WebElement tbxAccountNickName;

	public WebElement getTbxAccountNickName()
	{
		return tbxAccountNickName;
	}

	@FindBy(xpath = "//div[@class='modal-body']/descendant::div[@class='view-accountnickname']/parent::div/following-sibling::div/span")
	private WebElement errAccountNickName;

	public WebElement getErrTbxAccountNickName()
	{
		return errAccountNickName;
	}

	//Terms and condition check box
	@FindBy(xpath = "//div[@class='modal-body']/descendant::input[@name='termsandcondition']")
	private WebElement chkTermsAndCond;

	public WebElement getChkTermsAndCond()
	{
		return chkTermsAndCond;
	}

	@FindBy(xpath = "//div[@class='modal-body']/descendant::input[@name='termsandcondition']/following-sibling::label/span")
	private WebElement chkTermsAndCondText1;

	public WebElement getChkTermsAndCondText1()
	{
		return chkTermsAndCondText1;
	}

	@FindBy(xpath = "//div[@class='modal-body']/descendant::input[@name='termsandcondition']/following-sibling::label/span[2]/a")
	private WebElement chkTermsAndCondText2;

	public WebElement getChkTermsAndCondText2()
	{
		return chkTermsAndCondText2;
	}

	@FindBy(xpath = "//div[@class='modal-body']/descendant::input[@name='termsandcondition']/following-sibling::label/span[3]")
	private WebElement chkTermsAndCondText3;

	public WebElement getChkTermsAndCondText3()
	{
		return chkTermsAndCondText3;
	}

	@FindBy(xpath = "//div[@class='modal-body']/descendant::div[@data-component-name='buttonsmscode']/parent::div/preceding-sibling::div/strong")
	private WebElement txtGetSMSCodeForSecurity;

	public WebElement getTxtGetSMSCodeForSecurity()
	{
		return txtGetSMSCodeForSecurity;
	}

	@FindBy(xpath = "//div[@class='modal-body']/descendant::div[@data-component-name='buttonsmscode']/button")
	private WebElement btnGetSMSCode;

	public WebElement getBtnGetSMSCode()
	{
		return btnGetSMSCode;
	}
}
