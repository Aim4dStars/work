package steps;

import static junit.framework.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

import org.openqa.selenium.WebElement;

import pages.clientdetails.LightBoxComponentPage;
import pages.clientdetails.TrustIndDetailsPage;
import pages.logon.LoginPage;

public class TrustDetailsSteps extends ScenarioSteps
{

	LoginPage loginPage;
	TrustIndDetailsPage trustIndDetailsPage;
	LightBoxComponentPage lightBoxComponentPage;

	public String registerGSTValue;
	public String nameGSTCancel;
	public String nameRegistraionStateCancel;
	public String nameTaxOptionsCancel;

	public TrustDetailsSteps(Pages pages)
	{
		super(pages);
	}

	@Step
	public void starts_logon_clientdetails() throws Throwable
	{
		loginPage.open();
		loginPage.doLogon();
	}

	@Step
	public void openTrustDetail() throws Throwable
	{

		loginPage.gotopage("Client details (Trust Individual)");
		lightBoxComponentPage.getPageRefresh();

	}

	@Step
	public void checkTrustDetailIndEditIcon()
	{

		assertTrue(trustIndDetailsPage.getIsTrustRegStateEditIcon().isDisplayed());
		assertTrue(trustIndDetailsPage.getIsTrustTaxOptEditIcon().isDisplayed());
		assertTrue(trustIndDetailsPage.getIsTrustRegForGSTEditIcon().isDisplayed());
		assertTrue(trustIndDetailsPage.getIsTrustAddressEditIcon().isDisplayed());
		assertTrue(trustIndDetailsPage.getisTrustLinkedName());
		assertTrue(trustIndDetailsPage.getisTrustLinkedRole());
		assertEquals(5, trustIndDetailsPage.getisBeneficiaries().size());
	}

	@Step
	public void navigateTrustInd() throws Throwable
	{
		loginPage.open();
		loginPage.doLogon();
		loginPage.gotopage("Client details (Trust Corporate)");
		lightBoxComponentPage.getPageRefresh();

	}

	@Step
	public void clickEditClientName() throws Throwable
	{

		trustIndDetailsPage.getIsTrustRegForGSTEditIcon().click();
		assertEquals("GST", lightBoxComponentPage.getIsEditTitle().getText());

	}

	@Step
	public void clickRegistrationState() throws Throwable
	{

		trustIndDetailsPage.getIsTrustRegStateEditIcon().click();
		assertEquals("Registration state", lightBoxComponentPage.getIsEditTitle().getText());

	}

	@Step
	public void checkEditRegistrationStateValues() throws Throwable
	{

		List <String> expectedState = Arrays.asList("ACT", "NSW", "NT", "QLD", "SA", "TAS", "VIC", "QA");
		List <String> actualState = new ArrayList <String>();
		List <WebElement> isDropdownOption = trustIndDetailsPage.getisRegisteredState();
		for (WebElement option : isDropdownOption)
		{

			actualState.add(option.getText());

		}
		assert actualState.containsAll(expectedState);
	}

	@Step
	public void checkEditRegistrationStateScreen() throws Throwable
	{

		assertTrue(lightBoxComponentPage.getIsCheckBox().isDisplayed());
		assertTrue(lightBoxComponentPage.getIsCancelButton().isDisplayed());
		assertTrue(lightBoxComponentPage.getIsCancel().isDisplayed());

	}

	@Step
	public void checkEditRegisteredGSTTitle() throws Throwable
	{
		assertTrue(trustIndDetailsPage.getisRadioSelect().isDisplayed());
		assertTrue(trustIndDetailsPage.getisRadioSelectedValue().isDisplayed());
		assertTrue(lightBoxComponentPage.getIsCheckBox().isDisplayed());
		assertTrue(lightBoxComponentPage.getIsCancelButton().isDisplayed());
		assertTrue(lightBoxComponentPage.getIsCancel().isDisplayed());
	}

	@Step
	public void checkGSTDisabledButton() throws Throwable
	{

		assertTrue(lightBoxComponentPage.getIsUpdateDisabled().isDisplayed());
	}

	@Step
	public void checkGSTApprovalCheckbox() throws Throwable
	{

		lightBoxComponentPage.getIsCheckBox().click();
	}

	@Step
	public void checkGSTEnabledButton() throws Throwable
	{

		assertTrue(lightBoxComponentPage.getIsUpdateEnabled().isDisplayed());
	}

	@Step
	public void updateRegistrationGST() throws Throwable
	{

		trustIndDetailsPage.getisRadioSelect().click();
		registerGSTValue = trustIndDetailsPage.getisRadioSelectedValue().getText();

	}

	@Step
	public void clickGSTUpdateButton() throws Throwable
	{

		lightBoxComponentPage.getIsUpdateEnabled().click();
	}

	@Step
	public void verifyGSTUpdated() throws Throwable
	{

		String nameGSTText = trustIndDetailsPage.getisRegisteredGSTName().getText();
		assertEquals(registerGSTValue, nameGSTText);
	}

	@Step
	public void clickGSTCancel() throws Throwable
	{

		nameGSTCancel = trustIndDetailsPage.getisRegisteredGSTName().getText();
		lightBoxComponentPage.getIsCancelButton().click();
	}

	@Step
	public void clickRegistrationStateCancel() throws Throwable
	{

		nameGSTCancel = trustIndDetailsPage.getisRegistrationStateName().getText();
		lightBoxComponentPage.getIsCancelButton().click();
	}

	@Step
	public void clickGSTClose() throws Throwable
	{

		nameGSTCancel = trustIndDetailsPage.getisRegisteredGSTName().getText();
		lightBoxComponentPage.getIsCancel().click();
	}

	@Step
	public void clickRegistrationStateClose() throws Throwable
	{

		nameRegistraionStateCancel = trustIndDetailsPage.getisRegistrationStateName().getText();
		lightBoxComponentPage.getIsCancel().click();
	}

	@Step
	public void clickDropDownIcon() throws Throwable
	{
		trustIndDetailsPage.getisDropDownIcon().click();
	}

	@Step
	public void verifyGSTNameAfterCanButton() throws Throwable
	{

		assertEquals(nameGSTCancel, trustIndDetailsPage.getisRegisteredGSTName().getText());

	}

	@Step
	public void verifyRegStateNameAfterCanButton() throws Throwable
	{

		assertEquals(nameRegistraionStateCancel, trustIndDetailsPage.getisRegistrationStateName().getText());

	}

	@Step
	public void updateRegistraionState(String validState) throws Throwable
	{

		List <String> expectedState = Arrays.asList("ACT", "NSW", "NT", "QLD", "SA", "TAS", "VIC", "QA");
		List <String> actualState = new ArrayList <String>();

		List <WebElement> isDropdownOption = trustIndDetailsPage.getisRegisteredState();

		for (WebElement option : isDropdownOption)
		{

			if (option.getText().contains(validState))
			{

				option.click();

			}

		}

	}

	@Step
	public void updatedRegistraionStateValue(String validState) throws Throwable
	{

		String nameText = trustIndDetailsPage.getisRegistrationStateName().getText();
		assertEquals(nameText, validState);

	}

	@Step
	public void clickEditTaxOption() throws Throwable
	{
		trustIndDetailsPage.getIsTrustTaxOptEditIcon().click();
		//assertEquals("Edit tax options", lightBoxComponentPage.getIsEditTitle().getText());
	}

	@Step
	public void checkEditTaxOptionsScreen() throws Throwable
	{
		assertTrue(lightBoxComponentPage.getIsCheckBox().isDisplayed());
		assertTrue(lightBoxComponentPage.getIsCancelButton().isDisplayed());
		assertTrue(lightBoxComponentPage.getIsCancel().isDisplayed());
	}

	@Step
	public void checkEditTaxOptionsValues() throws Throwable
	{

		List <String> expectedValues = Arrays.asList("Enter Tax File Number (TFN)",
			"Provide Exemption Reason",
			"Do not quote TFN or exempt");
		List <String> actualValues = new ArrayList <String>();
		List <WebElement> isDropdownOption = trustIndDetailsPage.getIsTaxOptionsDropdownValues();
		for (WebElement option : isDropdownOption)
		{

			actualValues.add(option.getText());

		}
		assert actualValues.containsAll(expectedValues);
	}

	@Step
	public void checkInputFieldTFNNumber() throws Throwable
	{
		assertTrue(trustIndDetailsPage.getIsTFNNumberBox().isDisplayed());
	}

	@Step
	public void enterValidTFNNumber(String validNumber) throws Throwable
	{

		trustIndDetailsPage.getIsTFNNumberBox().clear();
		trustIndDetailsPage.getIsTFNNumberBox().sendKeys(validNumber);
		Thread.sleep(500);

	}

	@Step
	public void checkTFNNumberProvidedOnScreen() throws Throwable
	{
		assertEquals(trustIndDetailsPage.getIsTaxOptionsName().getText(), "Tax File Number provided");
	}

	@Step
	public void enterInvalidTFNNumber(String invalidNumber) throws Throwable
	{

		trustIndDetailsPage.getIsTFNNumberBox().clear();
		trustIndDetailsPage.getIsTFNNumberBox().sendKeys(invalidNumber);
		Thread.sleep(500);
	}

	@Step
	public void checkErrorMessageTFN() throws Throwable
	{

		assertEquals("Please enter a valid 8 or 9-digit tax file number", trustIndDetailsPage.getIsTFNInvalidNumberError()
			.getText());

	}

	@Step
	public void checkDoNotQuoteMessageOnScreen() throws Throwable
	{

		assertEquals("It's not mandatory to provide your Tax File Number (TFN) or exemption reason. However, if you don't, we may have to deduct withholding tax from your account at the highest marginal tax rate plus medicare levy.",
			trustIndDetailsPage.getIsTaxOptionsDoNotQuoteMessage().getText());

	}

	@Step
	public void updateTaxOptonTFN(String TFN) throws Throwable
	{

		List <String> expectedValues = Arrays.asList("Enter Tax File Number (TFN)",
			"Provide Exemption Reason",
			"Do not quote TFN or exempt");
		List <String> actualValues = new ArrayList <String>();

		List <WebElement> isDropdownOption = trustIndDetailsPage.getIsTaxOptionsDropdownValues();

		for (WebElement option : isDropdownOption)
		{

			if (option.getText().contains(TFN))
			{

				option.click();

			}

		}
	}

	@Step
	public void updateTaxOptonDoNotQuoteTFN(String noTFN) throws Throwable
	{

		List <String> expectedValues = Arrays.asList("Enter Tax File Number (TFN)",
			"Provide Exemption Reason",
			"Do not quote TFN or exempt");
		List <String> actualValues = new ArrayList <String>();
		List <WebElement> isDropdownOption = trustIndDetailsPage.getIsTaxOptionsDropdownValues();
		for (WebElement option : isDropdownOption)
		{

			if (option.getText().contains(noTFN))
			{

				option.click();

			}

		}
	}

	@Step
	public void checkTFNNumberNotProvidedOnScreen() throws Throwable
	{
		assertEquals(trustIndDetailsPage.getIsTaxOptionsName().getText(), "Tax File Number or exemption not provided");
	}

	@Step
	public void verifyTaxOptionsAfterCanButton() throws Throwable
	{

		assertEquals(nameTaxOptionsCancel, trustIndDetailsPage.getIsTaxOptionsName().getText());

	}

	@Step
	public void clickTaxOptionsCancel() throws Throwable
	{

		nameTaxOptionsCancel = trustIndDetailsPage.getIsTaxOptionsName().getText();
		lightBoxComponentPage.getIsCancelButton().click();
	}

	@Step
	public void clickTaxOptionsClose() throws Throwable
	{

		nameTaxOptionsCancel = trustIndDetailsPage.getIsTaxOptionsName().getText();
		lightBoxComponentPage.getIsCancel().click();
	}

	@Step
	public void updateTaxOptionReason(String Reason) throws Throwable
	{

		List <String> expectedValues = Arrays.asList("Enter Tax File Number (TFN)",
			"Provide Exemption Reason",
			"Do not quote TFN or exempt");
		List <String> actualValues = new ArrayList <String>();

		List <WebElement> isDropdownOption = trustIndDetailsPage.getIsTaxOptionsDropdownValues();

		for (WebElement option : isDropdownOption)
		{

			if (option.getText().contains(Reason))
			{

				option.click();

			}

		}
	}

	@Step
	public void checkExemptionReasonProvidedOnScreen() throws Throwable
	{
		assertEquals(trustIndDetailsPage.getIsTaxOptionsName().getText(), "Exemption Reason provided");
	}

	@Step
	public void checkSecondDropdownValues() throws Throwable
	{
		List <String> expectedValues = Arrays.asList("Investor under sixteen,Investor is a pensioner,Investor is a recipient of other eligible Centrelink pension or benefit,Entity not required to lodge an income tax return,Investors in the business of providing consumer or business finance,Norfolk Island residents,Non-residents,Alphabetic characters in quoted TFN");
		List <String> actualValues = new ArrayList <String>();
		List <WebElement> isDropdownOption = trustIndDetailsPage.getIsSecondDropdownValues();
		for (WebElement option : isDropdownOption)
		{

			actualValues.add(option.getText());

		}
		assert actualValues.containsAll(expectedValues);
	}

	@Step
	public void updateTaxOptionReasonSecondDropdown(String ExmReason) throws Throwable
	{

		List <String> expectedValues = Arrays.asList("Investor under sixteen,Investor is a pensioner,Investor is a recipient of other eligible Centrelink pension or benefit,Entity not required to lodge an income tax return,Investors in the business of providing consumer or business finance,Norfolk Island residents,Non-residents,Alphabetic characters in quoted TFN");
		List <String> actualValues = new ArrayList <String>();
		List <WebElement> isDropdownOption = trustIndDetailsPage.getIsSecondDropdownValues();
		for (WebElement option : isDropdownOption)
		{
			if (option.getText().contains(ExmReason))
			{
				option.click();
			}

		}
	}

	@Step
	public void clickSecondDropDownIcon() throws Throwable
	{
		trustIndDetailsPage.getisSecondDropDownIcon().click();
	}
}