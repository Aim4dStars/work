package feature;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import steps.SMSFCorpDetailsSteps;

public class SMSFCorpDetailsbddSteps
{

	public String feeVal;

	@Steps
	SMSFCorpDetailsSteps smsfcorpdetailsstpes;

	@When("I navigate to SMSF Corporate screen")
	public void i_am_on_SMSF_corp_screen() throws Throwable
	{

		smsfcorpdetailsstpes.openSmsfCorpDetail();
	}

	@Then("I see edit icon next to editable item for SMSF and Company and Linked Client")
	public void editSMSFCorpIconPresent() throws Throwable
	{
		smsfcorpdetailsstpes.checkSmsfCorpDetailEditIcon();
	}

}
