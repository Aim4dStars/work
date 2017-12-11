package feature;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;

import steps.FeeScheduleDeclareSteps;

public class FeeScheduleDetailsSteps
{
	@Steps
	FeeScheduleDeclareSteps feeschedule;

	@Given("I am on logon page screen")
	public void I_am_on_logon_screen() throws Throwable
	{
		feeschedule.starts_logon_fee();
	}

	@When("I navigate to the fee schedule screen with fee from client list with adviser $adviser")
	public void feeschedule_fee(String adviser) throws Throwable
	{
		feeschedule.navFeeSchedule(adviser);

	}

	@When("I navigate to Fee Schedule Screen with no fee")
	public void i_am_on_feesschedule_screen_nofee() throws Throwable
	{

		feeschedule.navigatefeeschedulenofee();
	}

	@Then("Verify no fee error $errorMsg")
	public void verify_error_nofee(String errorMsg) throws Throwable
	{
		feeschedule.verifyerrornofee(errorMsg);

	}

	@When("I click on Edit fees link")
	public void click_editfees_nofee() throws Throwable
	{
		Thread.sleep(3000);
		feeschedule.click_editfees();

	}

	@Then("I see fees Schedule edit screen")
	public void confirmeditFee() throws Throwable
	{

		feeschedule.confirmedit();

	}

	@When("I click on Fee Schedule link")
	public void click_fee_schedule_link() throws Throwable
	{
		feeschedule.click_feeschedule_link();
	}

	@Then("I am on Fee Schedule screen page")
	public void confirm_feeschedule_screen() throws Throwable
	{
		Thread.sleep(3000);
		feeschedule.confirmFeeScreen();
	}

	@When("I click on Charge one-off advice link")
	public void click_Chargeoneoff_fee() throws Throwable
	{
		Thread.sleep(3000);
		feeschedule.clickChargeoneoff();
	}

	@Then("I am on Charge one-off advice fee page")
	public void confirm_charge_oneoff_fee_page() throws Throwable
	{

		feeschedule.ConfirmFeeScreenforoneoff();

	}

	@Then("I see header text for Administration fees")
	public void verify_text_adminfee() throws Throwable
	{
		feeschedule.verify_text_administrationfee();
	}

	@Then("I see header text for Dollar fee component")
	public void verify_text_Dollarfee() throws Throwable
	{
		feeschedule.verify_textDollarfee();
	}

	@Then("I see current fee schedule for Dollar fee as $amount")
	public void verify_Dollarfee(String amount) throws Throwable
	{
		feeschedule.verify_Dollarfee(amount);
	}

	@Then("I see text for Sliding scale fee component")
	public void verify_text_slidingscale() throws Throwable
	{
		feeschedule.verify_text_slidingscale();
	}

	@Then("I see current fee schedule for minimum fee as $Minfee")
	public void verify_minslidingfee(String Minfee) throws Throwable
	{
		feeschedule.verify_minslidingfee(Minfee);
	}

	@Then("I see current fee schedule for maximum fee as $Maxfee")
	public void verify_maxslidingfee(String Maxfee) throws Throwable
	{
		feeschedule.verify_maxslidingfee(Maxfee);
	}

	@Then("I see current fee schedule for Sliding Scale tiers as $transaction")
	public void verify_slidingscale_tiers(ExamplesTable transaction) throws Throwable

	{
		List <Map> actual = feeschedule.testContsliding();
		for (int i = 0; i < transaction.getRowCount(); i++)
		{
			Map <String, String> Exmap = transaction.getRow(i);
			Map <String, String> Axmap = actual.get(i);
			assertEquals(Axmap.get("Tiers"), Exmap.get("Tiers"));
			assertEquals(Axmap.get("Pa"), Exmap.get("Pa"));
		}

	}

	@Then("I see header text for Advice fees and Ongoing advice fee")
	public void verify_headertext_advicefee() throws Throwable
	{
		feeschedule.verify_text_advicefee();
	}

	@Then("I see header text for Dollar fee component for ongoing fee")
	public void verify_headertext_ongoingfee() throws Throwable
	{
		feeschedule.verify_headertext_Dollarfee();
	}

	@Then("I see current fee schedule for ongoing Dollar fee as $amount")
	public void verify_ongoing_dollarfee(String amount) throws Throwable
	{
		feeschedule.verify_ongoing_dollarfee(amount);

	}

	@Then("I see text for ongoing Sliding scale fee component")
	public void verify_headertext_ongoingsliding() throws Throwable
	{
		feeschedule.verify_text_ongoing_sliding();
	}

	@Then("I see current fee schedule for ongoing Sliding Scale tiers as $transaction")
	public void verify_ongoingslidingscale_tiers(ExamplesTable transaction) throws Throwable

	{
		List <Map> actual = feeschedule.testContongoingsliding();
		for (int i = 0; i < transaction.getRowCount(); i++)
		{
			Map <String, String> Exmap = transaction.getRow(i);
			Map <String, String> Axmap = actual.get(i);
			assertEquals(Axmap.get("Tiers"), Exmap.get("Tiers"));
			assertEquals(Axmap.get("Pa"), Exmap.get("Pa"));
		}

	}

	@Then("I see header text for Licensee advice fee")
	public void verify_licenseeheader_text() throws Throwable
	{
		feeschedule.verify_liceseefee_text();
	}

	@Then("I see header text for Dollar fee component for Licensee fee")
	public void verify_licensee_Dollartext() throws Throwable
	{
		feeschedule.verify_licensee_Dollartext();
	}

	@Then("I see current fee schedule for Licensee Dollar fee as $amount $text")
	public void verify_Dollarfee_licensee(String amount, String text) throws Throwable
	{
		feeschedule.verify_licensee_Dollarfee(amount, text);
	}

	@Then("I see text for licensee Percentage fee component")
	public void verify_licensee_Percentagefee() throws Throwable
	{
		feeschedule.verify_licensee_Percentagefee();
	}

	@Then("I see current fee schedule for licensee Percentage fee tiers as $transaction")
	public void verify_licensee_percentagefee_table(ExamplesTable transaction) throws Throwable
	{

		List <Map> actual = feeschedule.testContlicenseepercentage();
		for (int i = 0; i < transaction.getRowCount(); i++)
		{
			Map <String, String> Exmap = transaction.getRow(i);
			Map <String, String> Axmap = actual.get(i);
			assertEquals(Axmap.get("Tiers"), Exmap.get("Tiers"));
			assertEquals(Axmap.get("Pa"), Exmap.get("Pa"));
		}
	}

	@Then("I see the fee components associated to the ongoing fee  as Dollarfee and Sliding Scale")
	public void verify_feecomponents_editscreen() throws Throwable
	{
		feeschedule.verify_fee_editscreen();
	}

	@Then("I see the fee components associated to the Licensee fee as Dollarfee and Percentagefee")
	public void verify_feecomponents_Licensee_editscreen() throws Throwable
	{
		feeschedule.verify_fee_Licensee_editscreen();
	}

	@Then("I see the Dollar fee amount as $amount")
	public void verify_dollaramount_ongoing(String amount) throws Throwable
	{
		feeschedule.verify_dollaramount_ongoing(amount);
	}

	@Then("I see the Dollar fee amount for licensee fee as $amount")
	public void verify_dollaramount_Licensee(String amount) throws Throwable
	{
		feeschedule.verify_dollaramount_licensee(amount);
	}

	@Then("I see the Term deposits and Cash components checked")
	public void verify_ongoing_sliding_checked() throws Throwable
	{
		feeschedule.verify_Ongoing_sliding_checked();
	}

	@Then("I see the Index CPI box checked")
	public void verify_Licensee_indexbox_checked() throws Throwable
	{
		feeschedule.verify_licensee_indexbox_checked();

	}

	@Then("I see value of Sliding tiers for Ongoing fee as: $transaction")
	public void verify_slidingtiers_Ongoingfee(ExamplesTable transaction) throws Throwable
	{

		List <Map> actual = feeschedule.testConttOngoingSliding();
		for (int i = 0; i < transaction.getRowCount(); i++)
		{
			Map <String, String> Exmap = transaction.getRow(i);
			Map <String, String> Axmap = actual.get(i);

			System.out.println("actual" + Axmap.get("From"));
			System.out.println("expected" + Exmap.get("From"));
			System.out.println("actual" + Axmap.get("To"));

			System.out.println("Expected" + Exmap.get("To"));
			assertEquals(Axmap.get("From"), Exmap.get("From"));
			assertEquals(Axmap.get("To"), Exmap.get("To"));
			assertEquals(Axmap.get("Pa"), Exmap.get("Pa"));

		}
	}

	@Then("I see value of Percentage component for licensee fee as: $transaction")
	public void verify_Percentagefee_licensee(ExamplesTable transaction) throws Throwable
	{

		List <Map> actual = feeschedule.testContlicenseePercentage();
		for (int i = 0; i < transaction.getRowCount(); i++)
		{
			Map <String, String> Exmap = transaction.getRow(i);
			Map <String, String> Axmap = actual.get(i);

			System.out.println("actual" + Axmap.get("Type"));
			System.out.println("expected" + Exmap.get("Type"));
			System.out.println("actual" + Axmap.get("Pa"));

			System.out.println("Expected" + Exmap.get("Pa"));
			assertEquals(Axmap.get("Type"), Exmap.get("Type"));
			assertEquals(Axmap.get("Pa"), Exmap.get("Pa"));

		}
	}

	@Then("I see Dollar Fee Percentage Fee and Sliding Scale sections for Ongoing fee")
	public void verify_ongoing_sections() throws Throwable
	{
		feeschedule.verify_Ongoingfee_sections();

	}

	@Then("I see Dollar Fee Percentage Fee and Sliding Scale sections for licensee fee")
	public void verify_licensee_sections() throws Throwable
	{
		feeschedule.verify_Licenseefee_sections();
	}

	@Then("I see amount pa defaulted to be blank and CPI indexation box unchecked")
	public void verify_amount_indexation__Ongoingfee() throws Throwable
	{
		feeschedule.verify_amount_indexation_Ongoingfee();
	}

	@Then("I see Dollar fee component is active for ongoing fee")
	public void dollarfee_ongoing_active() throws Throwable
	{
		feeschedule.confirmdollarfee_ongoing_active();
	}

	@When("I clicks on Dollar component for ongoing fee")
	public void clicks_Dollarfee_ongoing() throws Throwable
	{

		feeschedule.click_Dollarfee_ongoing();
	}

	@Then("I see the active Dollar fee component panel for ongoing fee")
	public void Verify_Dollarfee_panel() throws Throwable
	{

		feeschedule.verify_DollarfeePanel_ongoing();
	}

	@Then("I see Dollar fee compoenent is inactive for ongoing fee")
	public void confirmdollarfee_ongoing_inactive() throws Throwable
	{
		Thread.sleep(3000);
		feeschedule.confirmdollarfee_ongoing_inactive();
	}

	@Then("I see Dollar fee component for licensee fee is active")
	public void dollarfee_licensee_active() throws Throwable
	{
		feeschedule.confirmdollarfee_licensee_active();
	}

	@When("I clicks on Dollar component for licensee fee")
	public void Dollarfee_licensee_click() throws Throwable
	{
		feeschedule.dollarfee_licensee_click();
	}

	@Then("I see the active Dollar fee component panel for licensee fee")
	public void Dollarfee_licensee_panel() throws Throwable
	{
		feeschedule.dollarfee_licensee_panel();
	}

	@Then("I see Dollar fee compoenent is inactive for licensee fee")
	public void Dollarfee_licensee_inactive() throws Throwable
	{
		feeschedule.dollarfee_licensee_inactive();
	}

	@Then("I see Percentage fee component is active")
	public void confirmpercentagefee_ongoing_active() throws Throwable
	{
		Thread.sleep(3000);
		feeschedule.confirmpercentagefee_ongoing_active();
	}

	@When("I clicks on Percentage component for ongoing fee")
	public void clicks_percentagefee_ongoing() throws Throwable
	{
		feeschedule.click_percentagefee_ongoing();
	}

	@Then("I see a Percentage fee panel with three empty cluster fields")
	public void confirmpercentagefee_panel() throws Throwable
	{
		Thread.sleep(3000);
		feeschedule.confirmpercentagefee_ongoing_fields();

	}

	@Then("I see Percentage fee component is inactive for ongoing fee")
	public void confirmpercentagefee_ongoing_inactive() throws Throwable
	{
		feeschedule.confirmpercentagefee_ongoing_inactive();
	}

	@Then("I see Sliding Scale component is inactive for ongoing fee")
	public void Confirmslidingscale_ongoing_inactive() throws Throwable
	{
		feeschedule.confirmslidingscale_ongoing_inactive();

	}

	@Then("I see Percentage fee component is active for licensee fee")
	public void confirmpercentagefee_licensee_active() throws Throwable
	{
		feeschedule.confirmpercentagefee_licensee_active();
	}

	@When("I clicks on Percentage component for licensee fee")
	public void confirmpercentagefee_licensee_click() throws Throwable
	{
		feeschedule.confirmpercentagefee_licensee_click();
	}

	@Then("I see a Percentage fee panel with three empty cluster fields for licensee fee")
	public void Confirmpercentagefee_licensee_panel() throws Throwable
	{
		feeschedule.confirmpercentagefee_licensee_panel();
	}

	@Then("I see Percentage fee component is inactive for licensee fee")
	public void Confirmpercentagefee_licensee_inactive() throws Throwable
	{
		feeschedule.confirmpercentagefee_licensee_inactive();
	}

	@Then("I see Sliding Scale component is inactive for licensee fee")
	public void ConfirmSliding_licensee_inactive() throws Throwable
	{
		feeschedule.confirmsliding_licensee_inactive();
	}

	@Then("I see Sliding Scale component is active for ongoing fee")
	public void ConfirmSliding_Ongoing_active() throws Throwable
	{
		feeschedule.confirmsliding_ongoing_active();
	}

	@When("I clicks on Sliding Scale for ongoing fee")
	public void Sliding_Ongoingbutton_click() throws Throwable
	{
		feeschedule.sliding_ongoingbutton_click();
	}

	@Then("I see a Sliding Scale with with two tiers for ongoing fee")
	public void Confirm_sliding_tiers() throws Throwable
	{
		feeschedule.comfirm_sliding_tiers();
	}

	@Then("I see Sliding Scale component is active for licensee fee")
	public void Confirm_slidinglicensee_active() throws Throwable
	{
		feeschedule.confirm_slidinglicensee_active();
	}

	@When("I clicks on Sliding Scale for licensee fee")
	public void Confirm_slidinglicensee_click() throws Throwable
	{
		feeschedule.confirm_slidinglicensee_click();
	}

	@Then("I see a Sliding Scale with with two tiers for licensee fee")
	public void Confirm_sliding_licensee_tiers() throws Throwable
	{
		feeschedule.confirm_sliding_licensee_tiers();
	}

	@Then("I see all three add fee components for ongoing are active")
	public void all_components_ongoing_active() throws Throwable
	{
		Thread.sleep(3000);
		feeschedule.confirm_addfee_ongoing_buttons_active();

	}

	@Given("I am on Fee Schedule screen with fee")
	public void Iam_on_feeschedule_screen_withfee() throws Throwable
	{
		feeschedule.starts_logon_fee();
		feeschedule.openFeeScheduleDetailsPage();
	}

	@When("When I click on Edit fees")
	public void click_edit_fees() throws Throwable
	{
		feeschedule.clickeditfee();
	}

	@Then("I see editable fee template screen")
	public void confrim_editfee_template() throws Throwable
	{
		Thread.sleep(3000);
		feeschedule.confirmeditfeetemplate();

	}

	@When("I enter Percentage value for Sliding scale under Ongoing fee as $val1")
	public void enter_percentage_value_sliding(String val1) throws Throwable
	{

		feeschedule.Enter_Maxvalue_for_Sliding(val1);
	}

	@When("I enter percentage value for Percentage fee under Licensee fee as $val1")
	public void enter_percentageval_percentage(String val1) throws Throwable
	{
		feeschedule.Enter_Maxvalue_for_Percentage(val1);
	}

	@When("I enter value for dollar value of dollar component as $val1")
	public void enter_dollaramount_dollarfee(String val1) throws Throwable
	{
		feeschedule.enter_maxvalue_dollarfee(val1);
	}

	@When("I click on FeeNext button")
	public void fee_next() throws Throwable
	{
		feeschedule.feenext();
	}

	@Then("Verify error for Maximum value as $errorMsg1")
	public void check_Maxerrormsg(String errorMsg1) throws Throwable
	{
		feeschedule.checkMaxerrormsg(errorMsg1);
	}

	@Then("Verify error for non numeric value as $errorMsg2")
	public void check_Nonnumeric_errormsg(String errorMsg2) throws Throwable
	{
		feeschedule.check_non_numeric_errormsg(errorMsg2);
	}

	@Then("Verify error for negative as $errorMsg3")
	public void check_Negative_errormsg(String errorMsg) throws Throwable
	{
		feeschedule.check_negative_errormsg(errorMsg);

	}

	@When("I enter value for dollar amount for Sliding Scale as $val1")
	public void enter_dollar_amount(String val1) throws Throwable
	{
		feeschedule.enter_dollarvalue_for_Sliding(val1);
	}

	@Then("Verify error for blank value as $errorMsg")
	public void check_errormsg_blank(String errorMsg) throws Throwable
	{
		feeschedule.check_blank_errormsg_sliding(errorMsg);
	}

	@When("I enter value for dollar fee component as $val1")
	public void enter_dollarfee(String val1) throws Throwable
	{
		feeschedule.enter_decimalvalue_for_Dollarfee(val1);

	}

	@When("I enter value for a tier lower than previous tier")
	public void enter_lowervalue_sliding() throws Throwable
	{
		feeschedule.enterlowerval_sliding();
	}

	@Then("Verify the value in dollar fee component as $val1")
	public void check_decimal_dollarfee(double val1) throws Throwable
	{
		feeschedule.check_decimal_dollarfee(val1);
	}

	@Then("Verify error for tier as $errorMsg")
	public void verify_errormsg_tier(String errorMsg) throws Throwable
	{
		feeschedule.verify_errormsg_tier(errorMsg);
	}

	@Then("I see the value for percentage value as $val1")
	public void check_decimal_percentage_Sliding(double val1) throws Throwable
	{
		feeschedule.check_decimal_per_sliding(val1);

	}

	@Then("I see value of dollar for Sliding as $val1")
	public void check_dollar_sliding(double val1) throws Throwable
	{
		feeschedule.check_dollar_percentage_Sliding(val1);

	}

	@Then("Then I see Dollar Fee, Percentage Fee and Sliding Scale components for ongoing fee")
	public void verify_add_components_ongoing() throws Throwable
	{

		Thread.sleep(3000);
		feeschedule.confirmFeesections_EditFee_ongoing();
	}

	@Then("I see Dollar Fee, Percentage Fee and Sliding Scale components for licensee fee")
	public void verify_add_components_licensee() throws Throwable
	{
		Thread.sleep(3000);
		feeschedule.confirmFeesections_EditFee_licensee();
	}

	@When("I enter upper limits for first tier as $SlidingScaleUpperLimit")
	public void enter_upper_limit_first_tier_value_sliding(String slidingScaleUpperLimitValue) throws Throwable
	{

		feeschedule.enterUpperLimitFirstTierSliding(slidingScaleUpperLimitValue);
	}

	@Then("I see lower limit of the next tier as $SlidingScaleUpperLimit")
	public void check_lower_limit_second_tier_Sliding(String slidingScaleUpperLimitValue) throws Throwable
	{
		feeschedule.checkLowerLimitTierSliding(slidingScaleUpperLimitValue);
	}

	@When("I enter value in the percentage field as $PercentageVal")
	public void enter_percentage_value_sliding_first_tier(String PercentageValue) throws Throwable
	{

		feeschedule.enterPercentageValueFirstTierSliding(PercentageValue);
	}

	@When("I enter value in the percentage field of second tier as $PercentageValSecondTier")
	public void enter_percentage_value_sliding_second_tier(String PercentageValSecondTier) throws Throwable
	{

		feeschedule.enterPercentageValueSecondTierSliding(PercentageValSecondTier);
	}

	@Then("I see value reflected in the percentage field is $PercentageValueReflected")
	public void check_value_reflected_in_percentagefield(String PercentageValueReflected) throws Throwable
	{
		feeschedule.checkPercentageValueReflectedSliding(PercentageValueReflected);
	}

	@Then("I see value of upper limit for first tier as $SlidingScaleUpperLimitValue")
	public void check_value_upperlimit_first_tier(String SlidingScaleUpperLimitValue) throws Throwable
	{
		feeschedule.checkUpperLimitValueSliding(SlidingScaleUpperLimitValue);
	}

	@When("I click on Next Button")
	public void i_click_next_button_edit_screen_sliding_scale() throws Throwable
	{
		feeschedule.clickNextButtonSlidingScale();
	}

	@When("I enter upper limits for second tier as $SlidingScaleUpperLimitSecondTier")
	public void enter_upper_limit_second_tier_value_sliding(String slidingScaleUpperLimitValueSecondTier) throws Throwable
	{

		feeschedule.enterUpperLimitSecondTierSliding(slidingScaleUpperLimitValueSecondTier);
	}

	@Then("I am on the Confirmations screen $Name")
	public void on_confirmation_screen(String name) throws Throwable
	{
		feeschedule.checkOnConfirmationScreen(name);
	}

	@Then("I see current fee schedule for each of the SMA as $transaction")
	public void verify_SMA_fee(ExamplesTable transaction) throws Throwable
	{

		List <Map> actual = feeschedule.testContSMAfee();
		for (int i = 0; i < transaction.getRowCount(); i++)
		{
			Map <String, String> Exmap = transaction.getRow(i);
			Map <String, String> Axmap = actual.get(i);

			System.out.println("actual" + Axmap.get("Code"));
			System.out.println("expected" + Exmap.get("Code"));
			System.out.println("actual" + Axmap.get("Investment name"));

			System.out.println("Expected" + Exmap.get("Investment name"));
			assertEquals(Axmap.get("Code"), Exmap.get("Code"));
			assertEquals(Axmap.get("Investment name"), Exmap.get("Investment name"));

		}
	}

	/*	@Then("I see Investment management fees � Managed portfolios heading below the Administration fees section")
		public void verify_header_Investmentfess() throws Throwable
		{
			feeschedule.verify_header_Investmentfee();
		}*/

}
