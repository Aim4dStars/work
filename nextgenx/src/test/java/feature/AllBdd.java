package feature;

import net.thucydides.jbehave.ThucydidesJUnitStories;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.AfterClass;

public class AllBdd extends ThucydidesJUnitStories
{

	public AllBdd() throws ConfigurationException
	{

		//findStoriesIn("stories/PeriodicFee/OneTimeFee");
	}

	@AfterClass
	public static void afterRun()
	{
		TestHarness.get().quit();
	}
}