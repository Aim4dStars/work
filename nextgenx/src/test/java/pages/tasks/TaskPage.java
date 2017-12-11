package pages.tasks;

import static org.junit.Assert.assertThat;

import org.hamcrest.core.Is;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import net.thucydides.core.pages.PageObject;

public class TaskPage extends PageObject
{

	private WebDriver driver;

	public TaskPage(WebDriver driver)
	{
		this.driver = driver;
	}

	public void is_task_page()
	{
		assertThat(driver.getPageSource().contains("Your Tasks"), Is.is(true));
	}

	public void is_tab_selected(String tabName)
	{
		switch (tabName)
		{
			case "Change":
				tabName = "sliderTaskWrapChange";
				break;

			case "Approval":
				tabName = "sliderTaskWrapApproval";
				break;

			case "Draft":
				tabName = "sliderTaskWrapSaved";
				break;

			case "Active":
				tabName = "sliderTaskWrapActive";
				break;

			case "All":
			default:
				tabName = "sliderTaskWrapAll";
				break;
		}
		assertThat(driver.findElement(By.xpath("//li[contains(@class,'ui-state-active')]//a[@data-ng-key='" + tabName + "']"))
			.isDisplayed(), Is.is(true));
	}

}
