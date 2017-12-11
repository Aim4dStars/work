package feature;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import steps.TrustCorporateDetailsSteps;

public class TrustCorporateDetailsBddSteps
{

	public String feeVal;
	@Steps
	TrustCorporateDetailsSteps trustcorporatedetailssteps;

	@When("I navigate to Trust Corporate screen")
	public void i_am_on_trust_details_screen() throws Throwable
	{
		trustcorporatedetailssteps.openTrustCorporateDetail();

	}

	@Then("I see edit icon next to editable item for Trust and Company and Linked Client and Beneficiary details")
	public void edittrustIconPresent() throws Throwable
	{
		trustcorporatedetailssteps.checkTrustDetailCorpEditIcon();

	}
}
