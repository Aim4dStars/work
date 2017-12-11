package steps;

import static junit.framework.Assert.*;
import static org.junit.Assert.*;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

import org.hamcrest.core.Is;

import pages.BTTermsofuse.BTTermsOfUsePage;
import pages.logon.LogonPage;

public class BTTermsOfUseSteps extends ScenarioSteps
{
	LogonPage logonpage;
	BTTermsOfUsePage btTermsOfUsePage;

	public BTTermsOfUseSteps(Pages pages)
	{
		super(pages);
	}

	@Step
	public void openTermsOfUsePage() throws Throwable
	{

		logonpage.gotopage("Terms of Use");

	}

	//verifying header
	@Step
	public void isHeaderTermsOfUse()
	{
		assertEquals(btTermsOfUsePage.getHeaderTermsOfUse().getText(), "Terms of use");
		assertThat(btTermsOfUsePage.getHeaderTermsOfUse().isDisplayed(), Is.is(true));
	}

	//verifying sub-headers
	@Step
	public void isTermsSubHeader1()
	{
		assertEquals(btTermsOfUsePage.getTermsSubHeader1().getText(), "Australian investors only");
		assertThat(btTermsOfUsePage.getTermsSubHeader1().isDisplayed(), Is.is(true));
	}

	@Step
	public void isTermsSubHeader2()
	{
		assertEquals(btTermsOfUsePage.getTermsSubHeader2().getText(), "Value of your investments");
		assertThat(btTermsOfUsePage.getTermsSubHeader2().isDisplayed(), Is.is(true));
	}

	@Step
	public void isTermsSubHeader3()
	{
		assertEquals(btTermsOfUsePage.getTermsSubHeader3().getText(), "Disclosure documents");
		assertThat(btTermsOfUsePage.getTermsSubHeader3().isDisplayed(), Is.is(true));
	}

	@Step
	public void isTermsSubHeader4()
	{
		assertEquals(btTermsOfUsePage.getTermsSubHeader4().getText(), "Important disclaimers");
		assertThat(btTermsOfUsePage.getTermsSubHeader4().isDisplayed(), Is.is(true));
	}

	@Step
	public void isTermsSubHeader5()
	{
		assertEquals(btTermsOfUsePage.getTermsSubHeader5().getText(), "Systems");
		assertThat(btTermsOfUsePage.getTermsSubHeader5().isDisplayed(), Is.is(true));
	}

	@Step
	public void clickOnAccessibilty()
	{
		btTermsOfUsePage.getTextAccessibility().click();

	}

	@Step
	public void isHeaderAccessibilty()
	{
		String textAccessibility = btTermsOfUsePage.getVisibleAccessibility().getText();
		assertEquals(textAccessibility, "Accessibility");
		assertThat(btTermsOfUsePage.getVisibleAccessibility().isDisplayed(), Is.is(true));

	}
}
