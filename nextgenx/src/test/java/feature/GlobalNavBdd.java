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
	, features = { "src/test/resources/feature/nav"})

public class GlobalNavBdd
{

	public GlobalNavBdd() throws ConfigurationException
	{

	}

	@AfterClass
	public static void afterRun()
	{
		TestHarness.get().quit();
	}
}