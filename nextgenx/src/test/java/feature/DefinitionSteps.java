package feature;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import net.thucydides.core.annotations.Steps;
import steps.EndUserSteps;

public class DefinitionSteps
{

	@Steps
	EndUserSteps endUser;

	@Given("a user enters site")
	public void givenTheUserIsOnTheWikionaryHomePage()
	{
		endUser.is_the_home_page();
	}

	@When("the adviser enters credentials")
	public void whenTheUserLooksUpTheDefinitionOf()
	{
		endUser.starts_logon();

	}

	@Then("a user enters the homepage")
	public void thenTheyShouldSeeADefinitionContainingTheWords()
	{
		endUser.should_see_definition();
	}

}
