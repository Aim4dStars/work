package feature;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import steps.SMSFDetailsSteps;

public class SMSFDetailsbddSteps
{

	@Steps
	SMSFDetailsSteps smsfdetailsstpes;

	@When("I navigate to SMSF Individual screen")
	public void i_am_on_SMSF_details_screen() throws Throwable
	{

		smsfdetailsstpes.openSmsfDetail();
	}

	@Then("I see edit icon next to editable item for SMSF and Linked Client")
	public void editSMSFIconPresent() throws Throwable
	{
		smsfdetailsstpes.checkSmsfDetailEditIcon();
	}

}
