package feature;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.AfterClass;
import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(format =
	{
		"pretty", "html:target/site/cucumber-htmlreport",
		"json:target/cucumber.json"
	}
, features = { "src/test/resources/feature/dashboard"})
public class DashboardBdd
{

	public DashboardBdd() throws ConfigurationException
	{

	}

	@AfterClass
	public static void afterRun()
	{
		TestHarness.get().quit();
	}
}