package feature;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import steps.AdviseFeeSteps;

public class permissions
{

	public String feeVal;
	@Steps
	AdviseFeeSteps adviser;

	@Given("I login as <role>")
	public void loginPanorama(@Named("role") String user) throws Throwable
	{

		adviser.logonas(user);
		adviser.openFeePage();
	}

	@When("I navigate to menu")
	public void navigate() throws Throwable
	{

		adviser.navigate();
	}

	/*@Then("Verify error $errorMsg")
	public void check_error0(String errorMsg) throws Throwable
	{

		adviser.checkMessage(errorMsg);

	}*/

	@Then("my permission for <role> on <screen> is as per <permission>")
	public void checkPermission(@Named("screen") String screen, @Named("permission") String requirement) throws Throwable
	{
		adviser.navigateMenu(screen, requirement);

	}

}
