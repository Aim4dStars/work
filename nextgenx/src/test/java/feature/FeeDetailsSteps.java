package feature;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Pending;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import steps.AdviseFeeSteps;

public class FeeDetailsSteps
{

	public String feeVal;

	public long starttimebeforenext;
	public long endtimeconfirmpage;
	public long starttimeconfirmpage;
	public long endtimerecieptpage;

	@Steps
	AdviseFeeSteps adviser;

	@When("I navigate to fees screen")
	public void i_navigate_on_fees_screen() throws Throwable
	{

		adviser.openFeePage();
	}

	@When("I navigate to fees screen from client list with adviser as $advisername")
	public void i_navigate_on_fees_screen_client(String advisername) throws Throwable
	{

		adviser.openFeePageClient(advisername);
		//starttimefeeclick = System.currentTimeMillis();

	}

	@When("I enter fees amount $amount")
	public void login_as_adviser(String amount) throws Throwable
	{

		feeVal = amount;
		adviser.enterFee(amount);

	}

	@Then("I see agreement text $agreementText")
	@Pending
	public void agreement_text(String agreement) throws Throwable
	{

		adviser.agreementText(agreement);

	}

	@When("I enter Description text as $description")
	public void enter_des_text(String description) throws Throwable
	{

		adviser.enterDescriptionText(description);

	}

	@When("I click on Next button")
	public void Next() throws Throwable
	{

		adviser.clickNext();
		starttimebeforenext = System.currentTimeMillis();
		Thread.sleep(5000);
	}

	@When("I click on Submit button")
	public void click_confirm_button() throws Throwable
	{

		adviser.clickSubmitButton();
		starttimeconfirmpage = System.currentTimeMillis();

	}

	@When("I click on Cancel button")
	public void click_cancel_button() throws Throwable
	{

		adviser.clickCancelButton();

	}

	@When("I click on Cancel button from header panel")
	public void click_cancel_button_from_header() throws Throwable
	{

		adviser.clickCancelButtonFromHeader();

	}

	@When("I click on Yes button on the model popup")
	public void click_cancel_button_yes_popup() throws Throwable
	{

		adviser.clickYesPopupCancelButton();

	}

	@When("I click on No button on the model popup")
	public void click_cancel_button_no_popup() throws Throwable
	{

		adviser.clickNoPopupCancelButton();

	}

	@Then("Check error message $errorMsg")
	@Pending
	public void check_error0(String errorMsg) throws Throwable
	{

		adviser.checkMessage(errorMsg);

	}

	@Then("I am on confirm page with amount as $Amount")
	public void on_confirm_page_with_amount_(String amount) throws Throwable
	{
		adviser.checkAmountConfirmPage(amount);
	}

	@Then("I am on confirm page with description as $description")
	public void on_confirm_page_with_description_(String description) throws Throwable
	{
		adviser.checkDescriptionConfirmPage(description);
	}

	@Then("I am on Receipt page with description as $Description")
	public void on_receipt_page_with_description_(String description) throws Throwable
	{
		adviser.checkDescriptionReceiptPage(description);
		endtimerecieptpage = (System.currentTimeMillis() - starttimeconfirmpage) / 1000;
		System.out.println("time taken for confirmpage transaction" + endtimerecieptpage);

	}

	@Then("I see message text as $msgtext")
	public void on_receipt_page_with_msgtext(String msgtext) throws Throwable
	{
		adviser.checkTitleReceiptPage(msgtext);
	}

	@Then("Ensure date displays in correct format")
	public void on_receipt_page_date_format() throws Throwable
	{
		adviser.checkReceiptPageDateFormat();
	}

	@Then("Agreement box get highlighted on same screen")
	public void on_confirm_page_with_agreement_highlighted() throws Throwable
	{
		adviser.checkHighlightedAgreementConfirmPage();
	}

	@When("I check client agreement box in the Confirm Screen")
	public void check_client_agreement_confirm_screen() throws Throwable
	{

		adviser.clickAgreementBox();
	}

	@When("I click on Return to Accounts Overview")
	public void click_on_return_accountoverview_button() throws Throwable
	{

		adviser.clickReturnAccountOverviewButton();
	}

	@When("I click on return to client list button")
	public void click_on_return_clientlist_button() throws Throwable
	{

		adviser.clickReturnClientListButton();
	}

	@Then("I am on Fee Detail screen with pre populated details amount as $data")
	public void on_fee_detail_screen_with_poplulated_details(String dataValues) throws Throwable
	{
		adviser.checkFeePagePopulatedData(dataValues);
	}

	@Then("Verify total one-off advice fee charged in past 12 months has 2 decimals")
	public void verify_total_fee_has_decimals() throws Throwable
	{
		adviser.verifytotalcharges();
	}

	@Then("Verify Available cash is displayed")
	public void verify_aval_cash_displayed() throws Throwable
	{
		adviser.verifyAvalCash();
	}

	@Then("Verify one off advice fee attribute is blank")
	public void verify_one_off_blank() throws Throwable
	{
		adviser.verifyoneoffblank();
	}

	@When("I enter description $validName")
	public void verify_maxlength(String validName) throws Throwable
	{
		adviser.enterdescription(validName);
	}

	@Then("I see only first 30 characters")
	public void i_verify_char_desc() throws Throwable
	{
		adviser.checkdescLength();

	}

	@Then("I see Error Message for one off as $errorMsg")
	@Pending
	public void verify_avaloq_aval_cash(String errorMsgAvailableCash) throws Throwable
	{
		adviser.checkErrorMessageAvailableCash(errorMsgAvailableCash);
	}

	@Then("I see Error Message for maximum annual cap allowed $errorMsg")
	public void verify_avaloq_max_cap(String errorMsgMaxCap) throws Throwable
	{
		adviser.checkErrorMessageMaxCap(errorMsgMaxCap);
	}

	@Then("ensure error message removed")
	public void verify_avaloq_error_message_removed() throws Throwable
	{
		adviser.checkErrorMessageRemovedAvailableCash();
	}

	@Then("I am on Receipt Page with header name as $HeaderName")
	public void on_recepit_screen(String headername) throws Throwable
	{
		adviser.checkOnReceiptPage(headername);
	}

	@Then("I am on Receipt Page with fees charged as $Amount")
	public void on_receipt_page_with_amount_(String amount) throws Throwable
	{
		adviser.checkAmountReceiptPage(amount);
	}

	@Then("I am on the client's overview page")
	public void on_client_overview_page_directed() throws Throwable
	{
		Thread.sleep(2000);
		adviser.checkOnClientOverviewScreen();
	}

	@When("I mousehover on help icon for one off")
	public void mouse_hover_help_icon_one_off() throws Throwable
	{

		Thread.sleep(2000);
		adviser.mouseHoverKeyActivityForOneOff();

	}

	@When("I mousehover on help icon for one off description")
	public void mouse_hover_help_icon_one_off_description() throws Throwable
	{
		Thread.sleep(2000);
		adviser.mouseHoverKeyActivityForOneOffDesc();

	}

	@Then("I see the help message text for one off as $oneofftooltip")
	@Pending
	public void see_help_tooltip_for_oneoff(String oneofftooltip) throws Throwable
	{

		adviser.oneOffTooltip(oneofftooltip);

	}

	@Then("I see the help message text for one off Desc as $Desc")
	@Pending
	public void see_help_tooltip_for_desc(String Desc) throws Throwable
	{

		adviser.oneOffDescTooltip(Desc);
	}

	@Then("I am on index page with title $Home")
	public void on_index_page(String Home) throws Throwable
	{

		adviser.checkOnIndexPage(Home);

	}

	@Then("I am on Charge One off Advice Fee screen with header name as $Confirm")
	public void on_one_off_fee_page(String confirm) throws Throwable
	{

		adviser.checkOnChargeOneOffAdviceFeePage(confirm);

	}

	@Then("I am on confirm_page with header name as $Confirm")
	public void on_confirma_page(String confirm) throws Throwable
	{

		adviser.checkOnChargeOneOffAdviceFeePage(confirm);

	}

	@When("I click on Cancel button on confirm page")
	public void on_cancle_for_confirm_page() throws Throwable
	{

		adviser.clickCancelButtonOnConfirmPage();

	}

	@Then("I am on confirm page")
	public void ConfirmNav() throws Exception
	{

		Thread.sleep(3000);
		//adviser.confirmPageTxt(feeVal);
		//endtimeconfirmpage = (System.currentTimeMillis() - starttimebeforenext) / 1000;
		//System.out.println("time taken for confirmpage transaction" + endtimeconfirmpage);

	}

	@Then("I see Total one off Advice Fee charged in past 12 months should as $0.00")
	public void totalFeeCharged() throws Exception
	{

		adviser.i_see_total_fee_charged();

	}

}
