package feature;

import net.thucydides.jbehave.ThucydidesJUnitStories;

public class AcceptanceTestSuite extends ThucydidesJUnitStories
{

	public AcceptanceTestSuite()
	{
		System.setProperty("webdriver.phantomjs.driver", System.getProperty("user.dir") + "/phantomjs.exe");

		getSystemConfiguration().setIfUndefined("webdriver.driver", "phantomjs");

		//System.setProperty("thucydides.use.unique.browser", "true");

		//findStoriesCalled("US1794SearchFeeRevStatements.story");

		//findStoriesCalled("US1994UpdateTaxOptions.story");

		//System.setProperty("thucydides.take.screenshots", "AFTER_EACH_STEP");
		runThucydides().inASingleSession();

		//findStoriesIn("stories/PeriodicFee/");
		//findStoriesIn("stories/StaticDataClient/AccountOverview/");
		//findStoriesIn("stories/PeriodicFee/FeeStatement/");
		//findStoriesIn("stories/PeriodicFee/FeeScheduledisplay/");
		//findStoriesIn("stories/PeriodicFee/OneTimeFee");
		//findStoriesCalled("US09811EnterOneOffAdviceFee.story");

		findStoriesCalled("RolesandPermission.story");

		//findStoriesCalled("US2403AdviserDashboardActKeyactivity.story");

	}

}
