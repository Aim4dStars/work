package steps;

import static org.fest.assertions.Assertions.*;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import pages.logon.LoginPage;

public class EndUserSteps extends ScenarioSteps
{

	LoginPage loginPage;

	public EndUserSteps(Pages pages)
	{
		super(pages);
	}

	@Step
	public void enters(String keyword)
	{
		loginPage.enter_keywords(keyword);
	}

	@Step
	public void starts_logon()
	{

		loginPage.enter_username("adviser");
		loginPage.enter_password("adviser");
		loginPage.login();

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
	public void looks_for()
	{

	}
}
