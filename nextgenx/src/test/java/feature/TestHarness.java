package feature;

import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

public class TestHarness
{
	public static WebDriver driver;
	public static String CONTEXT_PATH;
	public Configuration config;

	final private static TestHarness _instance = new TestHarness();

	public void setup() throws ConfigurationException
	{
		final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder("cucumber-setup.xml");
		config = builder.getConfiguration();

		switch (config.getString("webdriver.driver", "null"))
		{
			case "chrome":
				driver = new ChromeDriver();
				break;

			case "firefox":
				driver = new FirefoxDriver();
				break;

			case "phantomjs":
			default:
				driver = new PhantomJSDriver();

		}
		driver.manage().timeouts().implicitlyWait(config.getInt("timeout", 30), TimeUnit.SECONDS);

		CONTEXT_PATH = config.getString("webdriver.base.url", "http://dwgps0022.btfin.com:8120/nextgen/");

	}

	public static TestHarness get()
	{
		if (driver == null)
		{
			try
			{
				_instance.setup();
			}
			catch (ConfigurationException e)
			{
				throw new RuntimeException(e);
			}
		}
		return _instance;
	}

	public void quit()
	{

		driver.quit();
	}

}
