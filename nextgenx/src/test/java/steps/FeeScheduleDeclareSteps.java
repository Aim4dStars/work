package steps;

import static junit.framework.Assert.*;
import static org.fest.assertions.Assertions.*;

import java.util.List;
import java.util.Map;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import pages.clientdetails.LightBoxComponentPage;
import pages.clients.ClientsListPage;
import pages.confirm.ConfirmPage;
import pages.fees.FeeDetailsPage;
import pages.feeshedule.EditFeeSchedulePage;
import pages.feeshedule.EditFeeTemplatePage;
import pages.feeshedule.FeeScheduleDetailsPage;
import pages.feeshedule.FeeSchedulePage;
import pages.logon.LoginPage;

public class FeeScheduleDeclareSteps extends ScenarioSteps
{

	LoginPage loginPage;
	FeeDetailsPage FeeDetailsPage;
	FeeScheduleDetailsPage FeeScheduleDetailsPage;
	FeeSchedulePage FeeSchedulePage;
	ConfirmPage ConfirmPage;
	EditFeeSchedulePage EditFeeSchedulePage;
	EditFeeTemplatePage EditFeeTemplatePage;
	LightBoxComponentPage lightBoxComponentPage;
	ClientsListPage ClientsListPage;

	public FeeScheduleDeclareSteps(Pages pages)
	{
		super(pages);
	}

	@Step
	public void enters(String keyword)
	{
		loginPage.enter_keywords(keyword);
	}

	@Step
	public void starts_logon_fee() throws Throwable
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
	public void openFeeSchedulePage() throws Throwable
	{

		loginPage.gotopage("View Fee schedule Adviser (no data)");
		lightBoxComponentPage.getPageRefresh();
	}

	@Step
	public void click_editfees()
	{
		FeeSchedulePage.getIsEditfeelink().click();
	}

	@Step
	public void confirmedit()
	{

		String Msg1 = EditFeeSchedulePage.getIsEditFeeScheduleMsg().getText();

		assertEquals("Edit fees", Msg1);

	}

	@Step
	public void navigatefeeschedulenofee() throws Throwable
	{
		loginPage.gotopage("View Fee schedule Adviser");
	}

	@Step
	public void navFeeSchedule(String adviser) throws Throwable
	{

		loginPage.gotopage("Client list");
		ClientsListPage.navFeeschedulefee(adviser);
		Thread.sleep(3000);
	}

	@Step
	public void view_edit_link()
	{
		assertTrue(FeeSchedulePage.getIsEditfeelink().isDisplayed());
	}

	@Step
	public void verifyerrornofee(String errorMsg1)
	{
		if (errorMsg1 == "No fee applied")
		{
			assertTrue(FeeSchedulePage.check_message_error(errorMsg1));
		}
	}

	@Step
	public void click_feeschedule_link()
	{
		FeeSchedulePage.getIsFeelink().click();
	}

	@Step
	public void confirmFeeScreen()
	{

		String MsgText = FeeSchedulePage.getIsFeelink().getText();

		assertEquals("Fee schedule", MsgText);
	}

	@Step
	public void clickChargeoneoff()
	{
		FeeSchedulePage.getIsChargeoneoff().click();
	}

	@Step
	public void ConfirmFeeScreenforoneoff()
	{

		String MsgText = FeeDetailsPage.getIsOneOffText().getText();

		assertEquals("Fees", MsgText);
	}

	@Step
	public void verify_text_administrationfee()
	{
		String MsgText1 = FeeScheduleDetailsPage.getAdministrationfeesheader().getText();
		String MsgText2 = FeeScheduleDetailsPage.getAdministrationfeeheader().getText();

		assertEquals("Administration fees", MsgText1);
		assertEquals("Administration fee", MsgText2);

	}

	@Step
	public void verify_textDollarfee()
	{
		String MsgText1 = FeeScheduleDetailsPage.getDollaramountheader().getText();
		assertEquals("Dollar fee component", MsgText1);

	}

	@Step
	public void verify_Dollarfee(String amount)
	{

		assertEquals(amount, FeeScheduleDetailsPage.getDollaramount().getText());
	}

	@Step
	public void verify_text_slidingscale()
	{
		String MsgText1 = FeeScheduleDetailsPage.getslidingscalecomponentheader().getText();
		assertEquals("Sliding scale fee component", MsgText1);
	}

	@Step
	public void verify_minslidingfee(String Minfee)
	{
		assertEquals(Minfee, FeeScheduleDetailsPage.getMinimumfee().getText());
	}

	@Step
	public void verify_maxslidingfee(String Maxfee)
	{
		assertEquals(Maxfee, FeeScheduleDetailsPage.getMaximumfee().getText());
	}

	@Step
	public List <Map> testContsliding() throws Throwable
	{
		return FeeScheduleDetailsPage.testCountslidingtiers();
	}

	@Step
	public void verify_text_advicefee()
	{
		String MsgText1 = FeeScheduleDetailsPage.getAdvicefeetext().getText();
		assertEquals("Advice fees", MsgText1);

		String MsgText2 = FeeScheduleDetailsPage.getOngoingfeetext().getText();
		assertEquals("Ongoing advice fee", MsgText2);
	}

	@Step
	public void verify_headertext_Dollarfee()
	{
		String MsgText1 = FeeScheduleDetailsPage.getDollarfeeongoingtext().getText();
		assertEquals("Dollar fee component", MsgText1);
	}

	@Step
	public void verify_ongoing_dollarfee(String amount)
	{
		assertEquals(amount, FeeScheduleDetailsPage.getDollarfeeongoing().getText());
	}

	@Step
	public List <Map> testContongoingsliding() throws Throwable
	{
		return FeeScheduleDetailsPage.testCountSlidingongoingtiers();
	}

	@Step
	public void confirmdollarfee_ongoing_active()
	{
		assertTrue(EditFeeSchedulePage.getIsdollarfeeongoingbutton().isDisplayed());
	}

	@Step
	public void verify_text_ongoing_sliding()
	{
		String MsgText1 = FeeScheduleDetailsPage.getSlidingongoingtext().getText();
		assertEquals("Sliding scale fee component", MsgText1);
	}

	@Step
	public void verify_liceseefee_text()
	{
		String MsgText1 = FeeScheduleDetailsPage.getLicenseefeetext().getText();
		assertEquals("Licensee advice fee", MsgText1);
	}

	@Step
	public void verify_licensee_Dollartext()
	{
		String MsgText1 = FeeScheduleDetailsPage.getDollarfeelicenseetext().getText();
		assertEquals("Dollar fee component", MsgText1);
	}

	@Step
	public void verify_licensee_Dollarfee(String amount, String text)
	{
		String MsgText1 = FeeScheduleDetailsPage.getDollarfeelicensee().getText();
		assertEquals(amount, MsgText1);

		String MsgText2 = FeeScheduleDetailsPage.getdollarfeelicenseetext().getText();
		assertEquals(text, MsgText2);

	}

	@Step
	public void verify_licensee_Percentagefee()
	{
		String MsgText1 = FeeScheduleDetailsPage.getPercentagefeelicenseetext().getText();
		assertEquals("Percentage fee component", MsgText1);

	}

	@Step
	public List <Map> testContlicenseepercentage() throws Throwable
	{
		return FeeScheduleDetailsPage.testCountlicenseepercentage();
	}

	@Step
	public void click_Dollarfee_ongoing()
	{
		EditFeeSchedulePage.getIsdollarfeeongoingbutton().click();

	}

	@Step
	public void verify_DollarfeePanel_ongoing()
	{
		assertTrue(EditFeeSchedulePage.getIsdollarfeeongoingpanel().isDisplayed());
	}

	@Step
	public void confirmdollarfee_ongoing_inactive()
	{
		assertTrue(EditFeeSchedulePage.getIsdollarfeeongoingbutton().isDisplayed());

	}

	@Step
	public void confirmdollarfee_licensee_active()
	{
		assertTrue(EditFeeSchedulePage.getIsdollarfeelicenseebutton().isEnabled());
	}

	@Step
	public void dollarfee_licensee_click()
	{
		EditFeeSchedulePage.getIsdollarfeelicenseebutton().click();
	}

	@Step
	public void dollarfee_licensee_panel()
	{
		assertTrue(EditFeeSchedulePage.getIsdollarfeelicenseepanel().isDisplayed());
	}

	@Step
	public void dollarfee_licensee_inactive()
	{
		assertTrue(EditFeeSchedulePage.getIsdollarfeelicenseebutton().isDisplayed());
	}

	@Step
	public void confirmpercentagefee_ongoing_active()
	{
		assertFalse(EditFeeSchedulePage.getIspercentagefeeongoingbutton().isDisplayed());
	}

	@Step
	public void confirmpercentagefee_ongoing_inactive()
	{
		assertTrue(EditFeeSchedulePage.getIspercentagefeeongoingbutton().isDisplayed());

	}

	@Step
	public void confirmslidingscale_ongoing_inactive()
	{
		assertTrue(EditFeeSchedulePage.getIsSlidingscaleongoingButton().isDisplayed());
	}

	@Step
	public void click_percentagefee_ongoing()
	{
		EditFeeSchedulePage.getIspercentagefeeongoingbutton().click();

	}

	@Step
	public void confirmpercentagefee_ongoing_fields()
	{
		assertTrue(EditFeeSchedulePage.getIsPercentagefeeongoingpanel().isDisplayed());
		assertEquals("", EditFeeSchedulePage.getIsPercentageportfolioongoing().getText());
		assertEquals("", EditFeeSchedulePage.getIsPercentagetermdepositongoing().getText());
		assertEquals("", EditFeeSchedulePage.getIsPercentagecashongoing().getText());
	}

	@Step
	public void confirmpercentagefee_licensee_active()
	{
		assertTrue(EditFeeSchedulePage.getIsPercentagefeelicenseeButton().isEnabled());
	}

	@Step
	public void confirmpercentagefee_licensee_click()
	{
		EditFeeSchedulePage.getIsPercentagefeelicenseeButton().click();
	}

	@Step
	public void confirmpercentagefee_licensee_panel()
	{
		assertTrue(EditFeeSchedulePage.getIsPercentagefeelicenseePanel().isDisplayed());
		assertEquals("", EditFeeSchedulePage.getIsPercentageportfoliolicensee().getText());
		assertEquals("", EditFeeSchedulePage.getIsPercentagetermdepositlicensee().getText());
		assertEquals("", EditFeeSchedulePage.getIsPercentagecashlicensee().getText());
	}

	@Step
	public void confirmpercentagefee_licensee_inactive()
	{
		assertFalse(EditFeeSchedulePage.getIsPercentagefeelicenseeButton().isEnabled());
	}

	@Step
	public void confirmsliding_licensee_inactive()
	{
		assertFalse(EditFeeSchedulePage.getIsSlidingscaleButtonlicensee().isEnabled());
	}

	@Step
	public void confirmsliding_ongoing_active()
	{
		assertTrue(EditFeeSchedulePage.getIsSlidingscaleongoingButton().isEnabled());
	}

	@Step
	public void sliding_ongoingbutton_click()
	{
		EditFeeSchedulePage.getIsSlidingscaleongoingButton().click();
	}

	@Step
	public void comfirm_sliding_tiers()
	{
		List <WebElement> tr_collection = EditFeeSchedulePage.getIsSlidingtiers();
		if (tr_collection.size() > 2)
		{

			assertEquals("Records appropiate", "Records not appropiate");
		}

		for (WebElement m_iconlistmaturingtd : EditFeeSchedulePage.getIsSlidingtiers())
		{
			assertTrue(m_iconlistmaturingtd.isDisplayed());
		}
	}

	@Step
	public void confirm_slidinglicensee_active()
	{
		assertTrue(EditFeeSchedulePage.getIsSlidingscaleButtonlicensee().isDisplayed());
	}

	@Step
	public void confirm_slidinglicensee_click()
	{
		EditFeeSchedulePage.getIsSlidingscaleButtonlicensee().click();
	}

	@Step
	public void confirm_sliding_licensee_tiers()
	{

		List <WebElement> tr_collection = EditFeeSchedulePage.getIsSlidinglicenseetiers();
		if (tr_collection.size() > 2)
		{

			assertEquals("Records appropiate", "Records not appropiate");
		}

		for (WebElement m_iconlistmaturingtd : EditFeeSchedulePage.getIsSlidinglicenseetiers())
		{
			assertTrue(m_iconlistmaturingtd.isDisplayed());
		}
	}

	@Step
	public void confirm_addfee_ongoing_buttons_active()
	{
		assertTrue(EditFeeSchedulePage.getIsdollarfeeongoingbutton().isEnabled());
		assertTrue(EditFeeSchedulePage.getIspercentagefeeongoingbutton().isEnabled());

	}

	@Step
	public void openFeeScheduleDetailsPage() throws Throwable
	{
		loginPage.gotopage("View Fee schedule Investor");

	}

	@Step
	public void clickeditfee()
	{
		FeeScheduleDetailsPage.getIsEditFees().click();
	}

	@Step
	public void confirmeditfeetemplate()
	{

		String MsgText = EditFeeTemplatePage.getIsEditfeetext().getText();

		assertEquals("Edit fees", MsgText);
	}

	@Step
	public void verify_fee_editscreen()
	{
		String MsgText1 = EditFeeTemplatePage.getDollarfeecomponent().getText();

		assertEquals("Dollar fee component", MsgText1);

		String MsgText2 = EditFeeTemplatePage.getSlidingScalecomponent().getText();

		assertEquals("Sliding scale fee component", MsgText2);
	}

	@Step
	public void verify_fee_Licensee_editscreen()
	{
		{
			String MsgText1 = EditFeeTemplatePage.getlicenseeDollarfeecomponent().getText();

			assertEquals("Dollar fee component", MsgText1);

			String MsgText2 = EditFeeTemplatePage.getlicenseePercentagecomponent().getText();

			assertEquals("Percentage fee component", MsgText2);
		}
	}

	@Step
	public void verify_dollaramount_ongoing(String amount)
	{
		assertEquals(amount, EditFeeTemplatePage.getDollarfeeamountOngoing().getAttribute("data-view-value"));
	}

	@Step
	public void verify_dollaramount_licensee(String amount)
	{
		assertEquals(amount, EditFeeTemplatePage.getDollarfeeamountLicensee().getAttribute("data-view-value"));
	}

	@Step
	public void verify_Ongoing_sliding_checked()
	{
		assertTrue(EditFeeTemplatePage.getSlidingtermcheckbox().isDisplayed());
		assertTrue(EditFeeTemplatePage.getSlidingCashcheckbox().isDisplayed());
	}

	@Step
	public void verify_licensee_indexbox_checked()
	{
		assertTrue(EditFeeTemplatePage.getIndexcheckbox().isDisplayed());
	}

	@Step
	public List <Map> testContSMAfee() throws Throwable
	{
		return FeeScheduleDetailsPage.testCountSMAfee();
	}

	@Step
	public List <Map> testConttOngoingSliding() throws Throwable
	{
		return EditFeeTemplatePage.testCounttOngoingSliding();
	}

	@Step
	public List <Map> testContlicenseePercentage() throws Throwable
	{
		return EditFeeTemplatePage.testCountlicenseePercentage();
	}

	@Step
	public void verify_Ongoingfee_sections()
	{
		assertTrue(EditFeeSchedulePage.getOngoingDollarsection().isDisplayed());
		assertTrue(EditFeeSchedulePage.getOngoingPercentagesection().isDisplayed());
		assertTrue(EditFeeSchedulePage.getOngoingSlidingsection().isDisplayed());
	}

	@Step
	public void verify_Licenseefee_sections()
	{
		assertTrue(EditFeeSchedulePage.getLicenseeDollarsection().isDisplayed());
		assertTrue(EditFeeSchedulePage.getLicenseePercentagesection().isDisplayed());
		assertTrue(EditFeeSchedulePage.getLicenseeSlidingsection().isDisplayed());
	}

	@Step
	public void verify_amount_indexation_Ongoingfee()
	{
		String str1 = EditFeeSchedulePage.getDollaramountOngoing().getText();
		assertEquals("", str1);

		assertTrue(EditFeeSchedulePage.getOngoingindexCPI().isDisplayed());

	}

	@Step
	public void Enter_Maxvalue_for_Sliding(String val2)
	{
		EditFeeTemplatePage.getIsPercentageSliding().clear();
		EditFeeTemplatePage.getIsPercentageSliding().sendKeys(val2);
	}

	@Step
	public void Enter_Maxvalue_for_Percentage(String val2)
	{
		EditFeeTemplatePage.getIsPercentagefee().clear();
		EditFeeTemplatePage.getIsPercentagefee().sendKeys(val2);
	}

	@Step
	public void enter_maxvalue_dollarfee(String val1)
	{
		EditFeeTemplatePage.getDollarfeeamountOngoing().clear();
		EditFeeTemplatePage.getDollarfeeamountOngoing().sendKeys(val1);
	}

	@Step
	public void feenext()

	{
		EditFeeTemplatePage.getIsFeeNextbutton().submit();
	}

	@Step
	public void checkMaxerrormsg(String errorMsg1)
	{
		try
		{
			if (errorMsg1.equals("The value entered exceeds the maximum allowed for this fee. Please adjust accordingly."))
			{
				assertTrue(EditFeeTemplatePage.check_Maxerrormsg_Sliding(errorMsg1));
			}
		}

		catch (NoSuchElementException e)
		{
			assertEquals("Element Found", "Element Not Present on Page");

		}

	}

	@Step
	public void check_non_numeric_errormsg(String errorMsg2)
	{
		try
		{

			if (errorMsg2.equals("Enter an amount in the format '1.00'"))
			{
				assertTrue(EditFeeTemplatePage.check_non_numeric_errmsg_sliding(errorMsg2));
			}
		}

		catch (NoSuchElementException e)
		{
			assertEquals("Element Found", "Element Not Present on Page");

		}
	}

	@Step
	public void check_negative_errormsg(String errorMsg1)
	{

		try
		{

			if (errorMsg1.equals("Enter an amount greater than 0"))
			{
				assertTrue(EditFeeTemplatePage.check_non_numeric_errmsg_sliding(errorMsg1));
			}

		}

		catch (NoSuchElementException e)
		{
			assertEquals("Element Found", "Element Not Present on Page");

		}

	}

	@Step
	public void enterlowerval_sliding()
	{
		EditFeeTemplatePage.getHighertierSliding().clear();
		String Str = EditFeeTemplatePage.getLowertierSliding().getAttribute("data-view-value");

		double n = Double.parseDouble(Str);

		String val = String.valueOf((n - 1));

		EditFeeTemplatePage.getHighertierSliding().sendKeys(val);

	}

	@Step
	public void enter_dollarvalue_for_Sliding(String val2)
	{
		EditFeeTemplatePage.getIsDollarSliding().clear();
		EditFeeTemplatePage.getIsDollarSliding().sendKeys(val2);

	}

	@Step
	public void check_blank_errormsg_sliding(String errorMsg1)
	{
		try
		{

			if (errorMsg1.equals("Enter an amount"))
			{
				assertTrue(EditFeeTemplatePage.check_blank_errmsg_sliding(errorMsg1));
			}
		}

		catch (NoSuchElementException e)
		{
			assertEquals("Element Found", "Element Not Present on Page");

		}

	}

	@Step
	public void verify_errormsg_tier(String errorMsg)
	{
		try
		{
			String str = EditFeeTemplatePage.getMinerrorSliding().getText();
			assertEquals(str, errorMsg);
		}

		catch (NoSuchElementException e)
		{
			assertEquals("Element Found", "Element Not Present on Page");

		}

	}

	@Step
	public void enter_decimalvalue_for_Dollarfee(String val2)
	{
		EditFeeTemplatePage.getIsDollarfee().clear();
		EditFeeTemplatePage.getIsDollarfee().sendKeys(val2);
	}

	@Step
	public void check_decimal_dollarfee(double val2)
	{
		assertEquals(val2, EditFeeTemplatePage.getIsDollarfee().getAttribute("data-view-value"));
	}

	@Step
	public void check_decimal_per_sliding(double val2)
	{
		assertEquals(val2, EditFeeTemplatePage.getIsPercentageSliding().getAttribute("data-view-value"));
	}

	@Step
	public void check_dollar_percentage_Sliding(double val2)
	{
		assertEquals(val2, EditFeeTemplatePage.getIsDollarSliding().getAttribute("data-view-value"));
	}

	@Step
	public void confirmFeesections_EditFee_ongoing()
	{
		assertTrue(EditFeeTemplatePage.getIsongoingdollarfeebutton().isDisplayed());
		assertTrue(EditFeeTemplatePage.getIsongoingpercentagefeebutton().isDisplayed());
		assertTrue(EditFeeTemplatePage.getIsPercentageSliding().isDisplayed());
	}

	@Step
	public void confirmFeesections_EditFee_licensee()
	{
		assertTrue(EditFeeTemplatePage.getIslicenseedollarfeebutton().isDisplayed());
		assertTrue(EditFeeTemplatePage.getIslicenseepercentagefeebutton().isDisplayed());
		assertTrue(EditFeeTemplatePage.getIslicenseeslidingfeebutton().isDisplayed());
	}

	@Step
	public void enterUpperLimitFirstTierSliding(String slidingScaleUpperLimitValue)
	{
		EditFeeTemplatePage.getIsSlidingScaleFirstTier().clear();
		EditFeeTemplatePage.getIsSlidingScaleFirstTier().sendKeys(slidingScaleUpperLimitValue);
	}

	@Step
	public void checkLowerLimitTierSliding(String slidingScaleUpperLimitValue)
	{
		assertEquals(slidingScaleUpperLimitValue, EditFeeTemplatePage.getIsSlidingScaleSecondTierLimitValue().getText());
	}

	@Step
	public void enterPercentageValueFirstTierSliding(String PercentageValue)
	{
		EditFeeTemplatePage.getIsPercentageValueSlidingScaleFirstTier().clear();
		EditFeeTemplatePage.getIsPercentageValueSlidingScaleFirstTier().sendKeys(PercentageValue);
	}

	@Step
	public void checkPercentageValueReflectedSliding(String PercentageValueReflected)
	{
		assertEquals(PercentageValueReflected,
			EditFeeTemplatePage.getIsPercentageValueSlidingScaleFirstTier().getAttribute("data-view-value"));

	}

	@Step
	public void checkUpperLimitValueSliding(String SlidingScaleUpperLimitValue)
	{
		assertEquals(SlidingScaleUpperLimitValue, EditFeeTemplatePage.getIsSlidingScaleFirstTier()
			.getAttribute("data-view-value"));

	}

	@Step
	public void enterPercentageValueSecondTierSliding(String PercentageValSecondTier)
	{
		EditFeeTemplatePage.getIsPercentageValueSlidingScaleSecondTier().clear();
		EditFeeTemplatePage.getIsPercentageValueSlidingScaleSecondTier().sendKeys(PercentageValSecondTier);
	}

	@Step
	public void clickNextButtonSlidingScale()
	{
		EditFeeTemplatePage.getIsNextButton().click();
	}

	@Step
	public void enterUpperLimitSecondTierSliding(String slidingScaleUpperLimitValueSecondTier)
	{
		EditFeeTemplatePage.getIsSlidingScaleSecondTier().clear();
		EditFeeTemplatePage.getIsSlidingScaleSecondTier().sendKeys(slidingScaleUpperLimitValueSecondTier);
	}

	@Step
	public void checkOnConfirmationScreen(String name)
	{
		assertEquals("name", EditFeeTemplatePage.getIsConfirmationScreenSlidingScale().getText());
	}

	@Step
	public void verify_header_Investmentfee()
	{
		assertTrue(FeeScheduleDetailsPage.getManagedPortfolios().isDisplayed());
		assertTrue(FeeScheduleDetailsPage.getInvestmentfeeheader().isDisplayed());
	}

}