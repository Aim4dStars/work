package steps;

import static junit.framework.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import pages.clientdetails.ClientDetailsPage;
import pages.clientdetails.LightBoxComponentPage;
import pages.logon.LoginPage;

public class ClientDetailsSteps extends ScenarioSteps
{

	LoginPage loginPage;
	ClientDetailsPage clientDetailPage;
	LightBoxComponentPage lightBoxComponentPage;

	public String nameTextCancel;
	public String nameCountryOfResidenceCancel;
	public String nameText;

	public ClientDetailsSteps(Pages pages)
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
	public void openClientDetail() throws Throwable
	{

		loginPage.gotopage("Client details (individual)");
		lightBoxComponentPage.getPageRefresh();

	}

	@Step
	public void checkClientDetailEditIcon()
	{

		assertTrue(clientDetailPage.getIsClientNameIcon().isDisplayed());
		assertTrue(clientDetailPage.getIsContactEditIcon().isDisplayed());
		assertTrue(clientDetailPage.getIsResAddEditIcon().isDisplayed());
		assertTrue(clientDetailPage.getIsPosAddEditIcon().isDisplayed());
		assertTrue(clientDetailPage.getIsTaxOptEditIcon().isDisplayed());
		assertTrue(clientDetailPage.getIsCountryOptEditIcon().isDisplayed());
	}

	@Step
	public void navigateClientDetails() throws Throwable
	{
		loginPage.open();
		loginPage.doLogon();
		loginPage.gotopage("Client details (individual)");
		lightBoxComponentPage.getPageRefresh();
	}

	@Step
	public void clickClientDetailEditIcon() throws Throwable
	{
		boolean result = false;
		int attempts = 0;
		while (attempts < 3)
		{
			try
			{
				clientDetailPage.getIsClientNameIcon().click();
				result = true;
				break;
			}
			catch (StaleElementReferenceException e)
			{}
			attempts++;
		}

		assertEquals("preferred name", lightBoxComponentPage.getIsEditTitle().getText());
	}

	@Step
	public void checkEditClienttitle() throws Throwable
	{

		assertEquals("preferred name", lightBoxComponentPage.getIsEditTitle().getText());
		assertTrue(lightBoxComponentPage.getIsCheckBox().isDisplayed());
		assertTrue(lightBoxComponentPage.getIsCancelButton().isDisplayed());
		assertTrue(lightBoxComponentPage.getIsCancel().isDisplayed());
	}

	@Step
	public void checkPreferredName() throws InterruptedException
	{

		String nameText = clientDetailPage.getIsPreferredName().getText();

		String editName = clientDetailPage.getIsEditPreferredName().getAttribute("data-view-value");

		assertEquals(nameText, editName);
	}

	@Step
	public void enterValidName(String validName) throws Throwable
	{

		clientDetailPage.getIsEditPreferredName().clear();
		clientDetailPage.getIsEditPreferredName().sendKeys(validName);

	}

	@Step
	public void checkValidNameLength()
	{

		String editNameText = clientDetailPage.getIsEditPreferredName().getAttribute("data-view-value");
		int editNameLength = editNameText.length();
		assertEquals(50, editNameLength);

	}

	@Step
	public void checkDisabledButton()
	{

		assertTrue(lightBoxComponentPage.getIsUpdateDisabled().isDisplayed());
	}

	@Step
	public void checkApprovalCheckbox()
	{

		lightBoxComponentPage.getIsCheckBox().click();
	}

	@Step
	public void checkEnabledButton() throws Throwable
	{

		assertTrue(lightBoxComponentPage.getIsUpdateEnabled().isDisplayed());
	}

	@Step
	public void clickUpdateButton() throws Throwable
	{

		lightBoxComponentPage.getIsUpdateEnabled().click();

	}

	@Step
	public void verifyUpdateName(String updatedName) throws Throwable
	{
		String nameText = clientDetailPage.getIsEditedPreferredNameOnScreen().getText();
		assertEquals(nameText, updatedName);
	}

	@Step
	public void clickCancel() throws Throwable
	{

		nameTextCancel = clientDetailPage.getIsPreferredName().getText();
		lightBoxComponentPage.getIsCancelButton().click();
	}

	@Step
	public void clickClose() throws Throwable
	{

		nameTextCancel = clientDetailPage.getIsPreferredName().getText();
		lightBoxComponentPage.getIsCancel().click();
	}

	@Step
	public void verifyNameAfterCanButton() throws Throwable
	{

		assertEquals(nameTextCancel, clientDetailPage.getIsPreferredName().getText());

	}

	@Step
	public void clickClientDetailCountryOfResidenceEditIcon() throws Throwable
	{
		boolean result = false;
		int attempts = 0;
		while (attempts < 3)
		{
			try
			{
				clientDetailPage.getIsCountryOptEditIcon().click();
				result = true;
				break;
			}
			catch (StaleElementReferenceException e)
			{}
			attempts++;
		}

	}

	@Step
	public void checkEditCountryOfResidenceList() throws Throwable
	{

		List <String> expectedCountry = Arrays.asList("Australia", "India", "Italy", "France", "Canada", "USA");
		List <String> actualCountry = new ArrayList <String>();
		List <WebElement> isDropdownOption = clientDetailPage.getIsCountriesInDropdownCountryOfResidence();
		for (WebElement option : isDropdownOption)
		{

			actualCountry.add(option.getText());

		}

		assert actualCountry.containsAll(expectedCountry);
	}

	@Step
	public void checkEditCountryOfResidenceScreen() throws Throwable
	{
		assertTrue(lightBoxComponentPage.getIsCheckBox().isDisplayed());
		assertTrue(lightBoxComponentPage.getIsCancelButton().isDisplayed());
		assertTrue(lightBoxComponentPage.getIsCancel().isDisplayed());
	}

	@Step
	public void clickCountryOfResidenceCancel() throws Throwable
	{

		nameCountryOfResidenceCancel = clientDetailPage.getIsCountryOfResidenceName().getText();
		lightBoxComponentPage.getIsCancelButton().click();
	}

	@Step
	public void clickCountryOfResidenceClose() throws Throwable
	{

		nameCountryOfResidenceCancel = clientDetailPage.getIsCountryOfResidenceName().getText();
		lightBoxComponentPage.getIsCancel().click();
	}

	@Step
	public void verifyCountryOfResidenceNameAfterCanButton() throws Throwable
	{

		assertEquals(nameCountryOfResidenceCancel, clientDetailPage.getIsCountryOfResidenceName().getText());

	}

	@Step
	public void updateCountryOfResidence(String validCountry) throws Throwable
	{

		List <String> expectedCountry = Arrays.asList("Australia", "India", "Italy", "France", "Canada", "USA");
		List <String> actualCountry = new ArrayList <String>();
		List <WebElement> isDropdownOption = clientDetailPage.getIsCountriesInDropdownCountryOfResidence();
		for (WebElement option : isDropdownOption)
		{

			if (option.getText().contains(validCountry))
			{

				option.click();

			}

		}
	}

	@Step
	public void updatedCountryOfResidenceValue(String validCountry) throws Throwable
	{

		nameText = clientDetailPage.getIsCountryOfResidenceName().getText();
		assertEquals(nameText, validCountry);

	}

	@Step
	public void clickContactDetailsEditIcon() throws Throwable
	{
		boolean result = false;
		int attempts = 0;
		while (attempts < 3)
		{
			try
			{
				clientDetailPage.getIsContactEditIcon().click();
				result = true;
				break;
			}
			catch (StaleElementReferenceException e)
			{}
			attempts++;
		}
	}

	@Step
	public void checkEditContactDetailsScreen() throws Throwable
	{

		assertTrue(lightBoxComponentPage.getIsCheckBox().isDisplayed());
		assertTrue(lightBoxComponentPage.getIsCancelButton().isDisplayed());
		assertTrue(lightBoxComponentPage.getIsCancel().isDisplayed());
	}

	@Step
	public void checkUneditableMobileAndEmail() throws Throwable
	{
		assertTrue(clientDetailPage.getIsContactDetailsEmail0().isDisplayed());
		assertTrue(clientDetailPage.getIsContactDetailsMobile0().isDisplayed());
		assertTrue(clientDetailPage.getIsContactDetailsEmail1().isDisplayed());
	}

	@Step
	public void checkAddFieldContactDetailsList() throws Throwable
	{

		List <String> expectedOptions = Arrays.asList("Mobile", "Home", "Work");
		List <String> actualOptions = new ArrayList <String>();
		List <WebElement> isDropdownOption = clientDetailPage.getIsOptionsInAddDropdown();
		for (WebElement option : isDropdownOption)
		{

			actualOptions.add(option.getText());

		}

		assert actualOptions.containsAll(expectedOptions);
	}

	@Step
	public void updateSecondaryEmailContactDetails(String validEmail) throws Throwable
	{
		clientDetailPage.getIsContactDetailsEmail1().clear();
		clientDetailPage.getIsContactDetailsEmail1().sendKeys(validEmail);

	}

	@Step
	public void updatedSecondaryEmailContactDetails(String validEmail) throws Throwable
	{
		nameText = clientDetailPage.getIsOnScreenContactDetailsEmail1().getText();
		assertEquals(nameText, validEmail);
	}

	@Step
	public void updateSecondaryEmailContactDetailsInvalid(String InvalidEmail) throws Throwable
	{
		clientDetailPage.getIsContactDetailsEmail1().clear();
		clientDetailPage.getIsContactDetailsEmail1().sendKeys(InvalidEmail);
	}

	@Step
	public void checkErrorMessageInvalidEmailContactDetails() throws Throwable
	{

		assertEquals("Please enter a valid email address.", clientDetailPage.getIsEmailErrorMessage().getText());

	}

	@Step
	public void updateAddFieldHomeSelectContactDetails(String homenumber) throws Throwable
	{

		List <String> expectedValues = Arrays.asList("Mobile", "Home", "Work");
		List <String> actualValues = new ArrayList <String>();
		List <WebElement> isDropdownOption = clientDetailPage.getIsOptionsInAddDropdown();

		for (WebElement option : isDropdownOption)
		{

			if (option.getText().contains(homenumber))
			{

				option.click();
				Thread.sleep(500);

				break;
			}

		}
	}

	@Step
	public void enterValidHomeNumber(String validHomeNumber) throws Throwable
	{

		clientDetailPage.getIsContactDetailsMobile1().sendKeys(validHomeNumber);
	}

	@Step
	public void updatedHomeNumberContactDetails(String validHomeNumber) throws Throwable
	{
		nameText = clientDetailPage.getIsOnScreenContactDetailsMobile1().getText();
		assertEquals(nameText, validHomeNumber);
	}

	@Step
	public void enterValid10DigitHomeNumber(String valid10DigitHomeNumber) throws Throwable
	{

		clientDetailPage.getIsContactDetailsMobile1().sendKeys(valid10DigitHomeNumber);
	}

	@Step
	public void enterInvalidHomeNumber(String invalidHomeNumber) throws Throwable
	{

		clientDetailPage.getIsContactDetailsMobile1().sendKeys(invalidHomeNumber);
	}

	@Step
	public void enterValidWorkNumber(String validWorkNumber) throws Throwable
	{

		clientDetailPage.getIsContactDetailsPhoneNumber2().sendKeys(validWorkNumber);
	}

	@Step
	public void updatedWorkNumberContactDetails(String validWorkNumber) throws Throwable
	{
		nameText = clientDetailPage.getIsOnScreenContactDetailsPhoneNumber2().getText();
		assertEquals(nameText, validWorkNumber);
	}

	@Step
	public void enterValid10DigitWorkNumber(String valid10DigitWorkNumber) throws Throwable
	{

		clientDetailPage.getIsContactDetailsPhoneNumber2().sendKeys(valid10DigitWorkNumber);
	}

	@Step
	public void checkAddFieldNotDisplayed() throws Throwable
	{

		assertTrue(!clientDetailPage.getIsDropdownClick().isDisplayed());
	}

	@Step
	public void verifyContactDetailsAfterCanButton() throws Throwable
	{

		assertEquals("tm@yahoo.com", clientDetailPage.getIsOnScreenContactDetailsEmail1().getText());
	}

	@Step
	public void clickResidentialAddressEditIcon() throws Throwable
	{
		boolean result = false;
		int attempts = 0;
		while (attempts < 3)
		{
			try
			{
				clientDetailPage.getIsResAddEditIcon().click();
				result = true;
				break;
			}
			catch (StaleElementReferenceException e)
			{}
			attempts++;
		}
	}

	@Step
	public void checkEditAddressScreen() throws Throwable
	{
		assertEquals("contact details", lightBoxComponentPage.getIsEditTitle().getText());
		assertTrue(lightBoxComponentPage.getIsCheckBox().isDisplayed());
		assertTrue(lightBoxComponentPage.getIsCancelButton().isDisplayed());
		assertTrue(lightBoxComponentPage.getIsCancel().isDisplayed());
	}
}
