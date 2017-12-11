package steps;

import static junit.framework.Assert.*;
import static org.fest.assertions.Assertions.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import pages.AccountOverview.AccountOverviewDetailsPage;
import pages.clientdetails.LightBoxComponentPage;
import pages.clients.ClientsListPage;
import pages.confirm.ConfirmPage;
import pages.fees.FeeDetailsPage;
import pages.logon.LoginPage;

public class AdviseFeeSteps extends ScenarioSteps
{

	LoginPage loginPage;
	FeeDetailsPage FeeDetailsPage;
	ConfirmPage ConfirmPage;
	LightBoxComponentPage lightBoxComponentPage;
	ClientsListPage ClientsListPage;
	AccountOverviewDetailsPage AccountOverviewDetailsPage;
	String availCash;
	String availCashClientList;

	public AdviseFeeSteps(Pages pages)
	{
		super(pages);
	}

	@Step
	public void starts_logon_fee() throws Throwable
	{

		loginPage.open();
		loginPage.doLogon();

	}

	@Step
	public void openFeePageClient(String advisername) throws Throwable
	{

		loginPage.gotopage("Client list");
		ClientsListPage.loadTimeCont();
		ClientsListPage.waitForClientListPageToLoad();
		availCash = ClientsListPage.navFee(advisername);
	}

	@Step
	public void logonas(String user) throws Throwable
	{

		loginPage.open();
		loginPage.doLogon();

	}

	@Step
	public void should_see_definition()
	{
		assertThat(loginPage.getTitle()).contains("Panorama - Home");
	}

	@Step
	public void is_the_home_page()
	{
		loginPage.open();
	}

	@Step
	public void looks_for()
	{

	}

	@Step
	public void openFeePage() throws Throwable
	{

		loginPage.gotopage("Charge one-off advice fee");

	}

	@Step
	public void navigate() throws Throwable
	{

	}

	@Step
	public void navigateMenu(String screen, String requirement) throws Throwable
	{

		if (screen.equals("Charge one-off advice fee") && requirement.equals("deny"))

		{
			boolean b1;
			b1 = loginPage.viewMenu(screen);

			if (b1 == true)
			{

				assertEquals(1, 2);

			}

		}
	}

	@Step
	public void enterFee(String amount)
	{
		FeeDetailsPage.getIsAdviceFee().clear();
		FeeDetailsPage.getIsAdviceFee().sendKeys(amount);
	}

	@Step
	public void agreementText(String agreement)
	{
		assertEquals("agreement", FeeDetailsPage.getIsAgreementText().getText());

	}

	@Step
	public void enterDescriptionText(String descriptionText)
	{

		FeeDetailsPage.getIsDescriptionText().sendKeys(descriptionText);
	}

	@Step
	public void clickNext()
	{

		FeeDetailsPage.getIsNextButton().submit();
	}

	@Step
	public void clickSubmitButton()
	{

		FeeDetailsPage.getIsSubmitButton().click();
	}

	@Step
	public void checkMessage(String errorMsgDis)
	{

		try
		{
			if (errorMsgDis.equals("Enter an amount in the format '1.00'"))
			{

				assertEquals(errorMsgDis, FeeDetailsPage.isErrMoreThanTwoDecimal().getText());
			}

			if (errorMsgDis.equals("Enter an amount greater than 0"))
			{
				assertEquals(errorMsgDis, FeeDetailsPage.getErrMinAmount().getText());
			}
			if (errorMsgDis.equals("Enter an amount"))
			{
				assertEquals(errorMsgDis, FeeDetailsPage.IsErrNullAmount().getText());
			}

		}
		catch (NoSuchElementException e)
		{
			assertEquals("Element Found", "Element Not Present on Page");

		}

	}

	@Step
	public void confirmPageTxt(String feeVal)
	{

		//System.out.println("the value of the fee from confirmpage " + ConfirmPage.getFee());
		System.out.println("the value from fee page " + feeVal);

		//assertEquals(feeVal, ConfirmPage.getFee());
		//assertEquals("Confirm", ConfirmPage.getIsConfirmMsg().getText());
	}

	@Step
	public void checkAmountConfirmPage(String amount)
	{

		assertEquals(amount, FeeDetailsPage.getIsConfirmPageFeesAmount().getText());

	}

	@Step
	public void checkAmountReceiptPage(String amount)
	{

		assertEquals(amount, FeeDetailsPage.getIsReceiptPageFeesAmount().getText());

	}

	@Step
	public void checkDescriptionConfirmPage(String description)
	{

		assertEquals(description, FeeDetailsPage.getIsConfirmPageFeesDescription().getText());

	}

	@Step
	public void checkTitleReceiptPage(String titleText)
	{

		assertEquals(titleText, FeeDetailsPage.getIsReceiptPageConfirmText().getText());

	}

	@Step
	public void checkDescriptionReceiptPage(String description)
	{

		assertEquals(description, FeeDetailsPage.getIsReceiptPageFeesDescription().getText());

	}

	@Step
	public void checkReceiptPageDateFormat()
	{
		String Str1 = FeeDetailsPage.getIsReceiptPageDate().getText();

		String Str2 = Str1.replace("am", "AM").replace("pm", "PM");

		DateFormat dateFormater = new SimpleDateFormat("hh.mm a' AEST on 'dd MMM yyyy");

		Date d1;
		try
		{
			d1 = dateFormater.parse(Str2);

		}
		catch (ParseException e)
		{
			assertEquals("Date not matched", "Date Matched");
			e.printStackTrace();
		}
	}

	@Step
	public void checkHighlightedAgreementConfirmPage()
	{

		assertTrue(FeeDetailsPage.getIsConfirmPageHighlightedText().isDisplayed());

	}

	@Step
	public void clickAgreementBox()
	{

		FeeDetailsPage.getIsCheckbox().click();
	}

	@Step
	public void clickReturnAccountOverviewButton()
	{
		FeeDetailsPage.getIsReturnAccountOverviewButton().click();

	}

	@Step
	public void clickReturnClientListButton()
	{
		FeeDetailsPage.getIsReturnClientListButton().click();
	}

	@Step
	public void clickCancelButton()
	{
		FeeDetailsPage.getIsChargeFeesCancel().click();
	}

	@Step
	public void clickCancelButtonOnConfirmPage()
	{
		FeeDetailsPage.getIsConfirmChargeFeesCancel().click();
	}

	@Step
	public void clickCancelButtonFromHeader()
	{
		FeeDetailsPage.getIsChargeFeesHeaderPanelCancel().click();
	}

	@Step
	public void clickYesPopupCancelButton()
	{
		FeeDetailsPage.getIsChargeFeesCancelPopUpYesButton().click();
	}

	@Step
	public void clickNoPopupCancelButton()
	{
		FeeDetailsPage.getIsChargeFeesCancelPopUpNoButton().click();
	}

	@Step
	public void checkFeePagePopulatedData(String dataValues)
	{

		assertTrue(FeeDetailsPage.getIsChargeFeesPageHeader().isDisplayed());
		assertEquals(dataValues, FeeDetailsPage.getIsAdviceFee().getAttribute("data-view-value"));

	}

	@Step
	public void verifytotalcharges()
	{
		try
		{

			String text = FeeDetailsPage.getIsChargeFeesdollaramount().getText();

			if (text.contains("."))
			{
				int integerPlaces = text.indexOf('.');
				int decimalPlaces = text.length() - integerPlaces - 1;
				assertEquals(2, decimalPlaces);
			}
			else
				assertEquals("Matched", "Not Matched");
		}
		catch (NoSuchElementException e)
		{
			assertEquals("Element Found", "Element Not Present on Page");

		}

	}

	@Step
	public void verifyAvalCash()
	{
		try
		{
			assertTrue(!FeeDetailsPage.getisAvalCashdollaramount().getText().isEmpty());
		}
		catch (NoSuchElementException e)
		{
			assertEquals("Element Found", "Element Not Present on Page");

		}

	}

	@Step
	public void verifyoneoffblank()
	{
		assertTrue(FeeDetailsPage.getIsAdviceFee().getText().isEmpty());

	}

	@Step
	public void enterdescription(String validName)
	{

		FeeDetailsPage.getIsDescriptionText().sendKeys(validName);

	}

	@Step
	public void checkdescLength()
	{

		String editDescText = FeeDetailsPage.getIsDescriptionText().getAttribute("data-view-value");
		int editDescLength = editDescText.length();
		assertEquals(30, editDescLength);

	}

	@Step
	public void checkErrorMessageAvailableCash(String errorMsgAvailableCash)
	{

		assertTrue(FeeDetailsPage.IsErrorMessageExceedsAvailableCash().getText().contains(errorMsgAvailableCash));

	}

	@Step
	public void checkErrorMessageMaxCap(String errorMsgMaxCap)
	{
		assertEquals(errorMsgMaxCap, FeeDetailsPage.IsErrorMessageExceedsAvailableCash().getText());
	}

	@Step
	public void checkErrorMessageRemovedAvailableCash()
	{
		assertTrue(!FeeDetailsPage.IsErrorMessageExceedsAvailableCash().isDisplayed());

	}

	@Step
	public void checkOnReceiptPage(String headername) throws Throwable
	{
		Thread.sleep(2000);
		assertEquals(headername, FeeDetailsPage.getIsReceiptScreenHeader().getText());

	}

	@Step
	public void checkOnClientOverviewScreen()
	{
		assertTrue(FeeDetailsPage.getIsAccountOverviewScreenHeader().isDisplayed());

	}

	public void mouseHoverKeyActivityForOneOff() throws Throwable
	{

		Actions actions = new Actions(getDriver());
		WebElement menuHoverLink = FeeDetailsPage.getIsOneOffHelpText();
		actions.moveToElement(menuHoverLink);
		actions.perform();
		Thread.sleep(2000);

	}

	@Step
	public void mouseHoverKeyActivityForOneOffDesc() throws Throwable
	{

		Actions actions = new Actions(getDriver());
		WebElement menuHoverLink = FeeDetailsPage.getIsDescriptionHelpText();
		actions.moveToElement(menuHoverLink);
		actions.perform();
		Thread.sleep(2000);

	}

	@Step
	public void oneOffDescTooltip(String Desc) throws Throwable
	{
		try
		{
			String actDescTooltip = FeeDetailsPage.getIsOneOffDescToolTipText().getText();

			assertEquals(Desc, actDescTooltip);
		}
		catch (NoSuchElementException e)
		{
			assertEquals("Element Found", "Element Not Present on Page");

		}
	}

	@Step
	public void oneOffTooltip(String oneOffToolTip) throws Throwable
	{
		try
		{
			String actOneOffTool = FeeDetailsPage.getIsOneOffToolTipText().getText();

			assertEquals(oneOffToolTip, actOneOffTool);
		}
		catch (NoSuchElementException e)
		{
			assertEquals("Element Found", "Element Not Present on Page");

		}
	}

	@Step
	public void checkOnIndexPage(String Home)
	{
		assertEquals(Home, FeeDetailsPage.getIsClientListHeader().getText());
	}

	@Step
	public void checkOnChargeOneOffAdviceFeePage(String Confirm) throws Throwable
	{
		Thread.sleep(2000);
		assertEquals(Confirm, FeeDetailsPage.getIsClientListHeader().getText());
	}

	@Step
	public void checkOnClientListPage()
	{
		assertEquals("Client list", FeeDetailsPage.getIsClientListHeader().getText());
	}

	@Step
	public void i_see_total_fee_charged()
	{
		assertEquals("$0.00", FeeDetailsPage.getIsChargeFeesdollaramount().getText());
	}

}
