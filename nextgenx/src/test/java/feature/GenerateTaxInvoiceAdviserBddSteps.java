package feature;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import steps.GenerateTaxInvoiceAdviserSteps;

public class GenerateTaxInvoiceAdviserBddSteps
{

	@Steps
	GenerateTaxInvoiceAdviserSteps generatetaxinvoiceadvisersteps;

	@When("I navigate to Tax Invoice screen")
	public void i_am_on_tax_invoice_screen() throws Throwable
	{

		generatetaxinvoiceadvisersteps.openTaxInvoice();
	}

	@Then("I see necessary attributes displayed on the screen")
	public void necessaryAttributesPresent() throws Throwable
	{
		generatetaxinvoiceadvisersteps.checkNecessaryAttributes();

	}

}
