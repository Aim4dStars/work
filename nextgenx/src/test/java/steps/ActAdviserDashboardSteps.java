package steps;

import static junit.framework.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import pages.Advdashboard.ActAdviserDashboardPage;
import pages.logon.LoginPage;

public class ActAdviserDashboardSteps extends ScenarioSteps
{

	LoginPage loginPage;
	ActAdviserDashboardPage actAdviserDashboardPage;

	public ActAdviserDashboardSteps(Pages pages)
	{
		super(pages);
	}

	@Step
	public void starts_logon_draftApplication() throws Throwable
	{

		loginPage.open();
		loginPage.doLogon();

	}

	@Step
	public void openDraftApplication() throws Throwable
	{

		loginPage.gotopage("Adviser Dashboard");
	}

	@Step
	public void headerAndAccountNamePresent()
	{

		assertTrue(!actAdviserDashboardPage.getIsDraftApplicationHeader().getAttribute("href").isEmpty());
		int i = 0;
		for (WebElement draftapplicationaccountname : actAdviserDashboardPage.getIsDraftApplicationAccountName())
		{
			draftapplicationaccountname = actAdviserDashboardPage.getIsDraftApplicationAccountName().get(i);
			assertTrue(!draftapplicationaccountname.getAttribute("href").isEmpty());
			i++;

		}

	}

	@Step
	public void resultCountAndAccountTypeTextPresent()
	{
		int draftapplicationlistsize, draftapplicationintresultcount;
		String draftapplicationlistcountsize, draftapplicationtrimmedlistcountsize;
		Integer draftapplicationintegerresultcount;

		draftapplicationlistsize = actAdviserDashboardPage.getIsDraftApplicationDate().size();

		draftapplicationlistcountsize = actAdviserDashboardPage.getIsResultCount().getText();

		draftapplicationtrimmedlistcountsize = Character.toString(draftapplicationlistcountsize.charAt(8));

		draftapplicationintegerresultcount = Integer.valueOf(draftapplicationtrimmedlistcountsize);

		draftapplicationintresultcount = Integer.valueOf(draftapplicationintegerresultcount);

		assertEquals(draftapplicationintresultcount, draftapplicationlistsize);

		int i = 0;
		for (WebElement draftaccounttype : actAdviserDashboardPage.getIsDraftApplicationAccountType())
		{
			draftaccounttype = actAdviserDashboardPage.getIsDraftApplicationAccountType().get(i);
			assertTrue(draftaccounttype.isDisplayed());
			assertTrue(draftaccounttype.getText().equals("Individual") || draftaccounttype.getText().equals("SMSF")
				|| draftaccounttype.getText().equals("Trust") || draftaccounttype.getText().equals("Corporate"));
			i++;
		}

	}

	@Step
	public void draftApplicationDatePresent()
	{
		int i = 0;
		for (WebElement draftapplicationdate : actAdviserDashboardPage.getIsDraftApplicationDate())
		{
			draftapplicationdate = actAdviserDashboardPage.getIsDraftApplicationDate().get(i);
			assertTrue(draftapplicationdate.isDisplayed());
			i++;
		}

	}

	@Step
	public void draftApplicationHeaderClick() throws Throwable
	{
		actAdviserDashboardPage.getIsDraftApplicationHeader().click();
	}

	@Step
	public void draftValidCountFormat()
	{
		WebElement draftcount;
		String draftcountstr;
		draftcount = actAdviserDashboardPage.getIsResultCount();
		draftcountstr = draftcount.getText();
		assertTrue(draftcountstr.matches("Showing [1-3] of \\d+?"));

	}

	@Step
	public void dateValidFormat()
	{
		int i = 0;
		for (WebElement draftdateformat : actAdviserDashboardPage.getIsDraftApplicationDate())
		{
			draftdateformat = actAdviserDashboardPage.getIsDraftApplicationDate().get(i);
			String dateformatstr = draftdateformat.getText();
			if (dateformatstr == "Today")
			{
				assertTrue(dateformatstr.matches("Today"));
			}
			assertTrue(dateformatstr.matches("\\d+? day(s)? ago"));

		}

	}

	@Step
	public void draftApplicationAccountNameClick() throws Throwable
	{

		actAdviserDashboardPage.getIsDraftApplicationAccountName().get(0).click();

	}

	@Step
	public void alternateScenarioErrorTextDraftApplication()
	{

		String draftapplicationerrortext;

		draftapplicationerrortext = actAdviserDashboardPage.getIsAlternateScenarioDraftApplicationText().getText();

		assertEquals(draftapplicationerrortext, "There are no draft applications");
		assertTrue(!actAdviserDashboardPage.getIsDraftApplicationHeader().getAttribute("href").isEmpty());

	}

	@Step
	public void alternateScenarioDraftApplicationButtonVerify()
	{
		assertTrue(actAdviserDashboardPage.getIsAlternateScenarioDraftApplicationButton().isDisplayed());
	}

	@Step
	public void alternateScenarioButtonDraftApplicationClick() throws Throwable
	{

		actAdviserDashboardPage.getIsAlternateScenarioDraftApplicationButton().click();
	}

	@Step
	public void descendingDateVerify() throws Throwable
	{

		if (actAdviserDashboardPage.getIsDraftApplicationDate().size() == 3)
		{
			int i = 0;
			WebElement draftdate0 = actAdviserDashboardPage.getIsDraftApplicationDate().get(i);
			String draftdatestr0 = draftdate0.getText();

			WebElement draftdate1 = actAdviserDashboardPage.getIsDraftApplicationDate().get(i + 1);
			String draftdatestr1 = draftdate1.getText();

			WebElement draftdate2 = actAdviserDashboardPage.getIsDraftApplicationDate().get(i + 2);
			String draftdatestr2 = draftdate2.getText();

			if (draftdatestr2 == "Today")
			{

				assertTrue(draftdatestr1.matches("Today"));
				assertTrue(draftdatestr0.matches("Today"));
			}
			if (draftdatestr1 == "Today")
			{

				assertTrue(draftdatestr0.matches("Today"));
			}
			if (draftdatestr0.equals("Today") && !(draftdatestr1.equals("Today")))
			{

				String matureddatesttr1 = draftdatestr1.substring(0, draftdatestr1.indexOf(" "));
				Integer x1 = Integer.valueOf(matureddatesttr1);

				String draftdatesttr2 = draftdatestr2.substring(0, draftdatestr2.indexOf(" "));
				Integer x2 = Integer.valueOf(draftdatesttr2);

				assertTrue(x2 > x1);

			}

			else
			{

				String draftdatesttr1 = draftdatestr0.substring(0, draftdatestr0.indexOf(" "));

				Integer x1 = Integer.valueOf(draftdatesttr1);

				String draftdatesttr2 = draftdatestr1.substring(0, draftdatestr1.indexOf(" "));
				Integer x2 = Integer.valueOf(draftdatesttr2);

				String draftdatesttr3 = draftdatestr2.substring(0, draftdatestr2.indexOf(" "));
				Integer x3 = Integer.valueOf(draftdatesttr3);

				assertTrue(x2 > x1);
				assertTrue(x3 > x2);

			}

		}

		if (actAdviserDashboardPage.getIsDraftApplicationDate().size() == 2)
		{
			int i = 0;
			WebElement draftdate0 = actAdviserDashboardPage.getIsDraftApplicationDate().get(i);
			String draftdatestr0 = draftdate0.getText();

			WebElement draftdate1 = actAdviserDashboardPage.getIsDraftApplicationDate().get(i + 1);
			String draftdatestr1 = draftdate1.getText();

			if (draftdatestr1 == "Today")
			{

				assertTrue(draftdatestr0.matches("Today"));
			}

			else
			{

				String draftdatesttr1 = draftdatestr0.substring(0, draftdatestr0.indexOf(" "));

				Integer x1 = Integer.valueOf(draftdatesttr1);

				String draftdatesttr2 = draftdatestr1.substring(0, draftdatestr1.indexOf(" "));
				Integer x2 = Integer.valueOf(draftdatesttr2);

				assertTrue(x2 > x1);

			}

		}

	}

	@Step
	public void headerAndResultCountSavedOrdersPresent()
	{

		int savedorderslistsize, savedordersintresultcount;
		String savedordersresultcounttext, savedorderstrimmedresultcounttext;
		Integer savedordersintegerresultcount;

		savedorderslistsize = actAdviserDashboardPage.getIsSavedOrdersDate().size();
		savedordersresultcounttext = actAdviserDashboardPage.getIsSavedOrderResultCount().getText();
		savedorderstrimmedresultcounttext = Character.toString(savedordersresultcounttext.charAt(8));
		savedordersintegerresultcount = Integer.valueOf(savedorderstrimmedresultcounttext);
		savedordersintresultcount = Integer.valueOf(savedordersintegerresultcount);

		assertEquals(savedorderslistsize, savedordersintresultcount);
		assertTrue(actAdviserDashboardPage.getIsSavedOrderHeader().isDisplayed());
	}

	@Step
	public void accountNameHyperlinkSavedOrdersPresent()
	{

		int i = 0;
		for (WebElement savedorderaccnamehyperlink : actAdviserDashboardPage.getIsSavedOrdersAccountName())
		{

			savedorderaccnamehyperlink = actAdviserDashboardPage.getIsSavedOrdersAccountName().get(i);
			savedorderaccnamehyperlink.getAttribute("href");
			assertTrue(!savedorderaccnamehyperlink.getAttribute("href").isEmpty());
			i++;
		}

	}

	@Step
	public void descriptionTextSavedOrdersPresent()
	{

		int i = 0;
		for (WebElement savedordersdesciptiontext : actAdviserDashboardPage.getIsSavedOrdersDescriptionText())
		{

			savedordersdesciptiontext = actAdviserDashboardPage.getIsSavedOrdersDescriptionText().get(i);
			assertTrue(savedordersdesciptiontext.isDisplayed());
			i++;
		}
	}

	@Step
	public void dateSavedOrdersPresent()
	{
		int i = 0;
		for (WebElement savedordersdate : actAdviserDashboardPage.getIsSavedOrdersDate())
		{
			savedordersdate = actAdviserDashboardPage.getIsSavedOrdersDate().get(i);
			assertTrue(savedordersdate.isDisplayed());
			i++;
		}

	}

	@Step
	public void savedOrderShowingPatternMatch()
	{
		String savedcountstr;
		WebElement savedOrderCount = actAdviserDashboardPage.getIsSavedOrderResultCount();
		savedcountstr = savedOrderCount.getText();

		assertTrue(savedcountstr.matches("Showing [1-3] of \\d+?"));

	}

	@Step
	public void savedOrderDatePatternMatch()
	{

		for (WebElement saveddateformat : actAdviserDashboardPage.getIsSavedOrdersDate())
		{
			String saveddateformatstr;
			saveddateformatstr = saveddateformat.getText();
			if (saveddateformatstr == "Today")
			{
				assertTrue(saveddateformatstr.matches("Today"));
			}
			else
				assertTrue(saveddateformatstr.matches("\\d+? day(s)? ago"));
		}

	}

	@Step
	public void accountNameSavedOrderClick() throws Throwable
	{

		actAdviserDashboardPage.getIsSavedOrdersAccountName().get(0).click();

	}

	@Step
	public void alternateScenarioErrorTextSavedOrder()
	{

		String savedordererrortext;

		savedordererrortext = actAdviserDashboardPage.getIsAlternateScenarioSavedOrdersText().getText();

		assertEquals(savedordererrortext, "There are no saved orders");
		assertTrue(actAdviserDashboardPage.getIsSavedOrderHeader().isDisplayed());

	}

	@Step
	public void alternateScenarioSavedOrdersButtonVerify()
	{
		assertTrue(actAdviserDashboardPage.getIsAlternateScenarioSavedOrdersButton().isDisplayed());
	}

	@Step
	public void alternateScenarioButtonSavedOrderClick() throws Throwable
	{

		actAdviserDashboardPage.getIsAlternateScenarioSavedOrdersButton().click();
	}

	@Step
	public void descendingDateSavedOrdersVerify() throws Throwable
	{

		if (actAdviserDashboardPage.getIsSavedOrdersDate().size() == 3)
		{
			int i = 0;
			WebElement saveddate0 = actAdviserDashboardPage.getIsSavedOrdersDate().get(i);
			String saveddatestr0 = saveddate0.getText();

			WebElement saveddate1 = actAdviserDashboardPage.getIsSavedOrdersDate().get(i + 1);
			String saveddatestr1 = saveddate1.getText();

			WebElement saveddate2 = actAdviserDashboardPage.getIsSavedOrdersDate().get(i + 2);
			String saveddatestr2 = saveddate2.getText();

			if (saveddatestr2 == "Today")
			{

				assertTrue(saveddatestr1.matches("Today"));
				assertTrue(saveddatestr0.matches("Today"));
			}
			if (saveddatestr1 == "Today")
			{

				assertTrue(saveddatestr0.matches("Today"));
			}
			if (saveddatestr0.equals("Today") && !(saveddatestr1.equals("Today")))
			{

				String saveddatesttr1 = saveddatestr1.substring(0, saveddatestr1.indexOf(" "));
				Integer x1 = Integer.valueOf(saveddatesttr1);

				String saveddatesttr2 = saveddatestr2.substring(0, saveddatestr2.indexOf(" "));
				Integer x2 = Integer.valueOf(saveddatesttr2);

				assertTrue(x2 > x1);

			}

			else
			{

				String saveddatesttr1 = saveddatestr0.substring(0, saveddatestr0.indexOf(" "));

				Integer x1 = Integer.valueOf(saveddatesttr1);

				String saveddatesttr2 = saveddatestr1.substring(0, saveddatestr1.indexOf(" "));
				Integer x2 = Integer.valueOf(saveddatesttr2);

				String saveddatesttr3 = saveddatestr2.substring(0, saveddatestr2.indexOf(" "));
				Integer x3 = Integer.valueOf(saveddatesttr3);

				assertTrue(x2 > x1);
				assertTrue(x3 > x2);

			}

		}

		if (actAdviserDashboardPage.getIsSavedOrdersDate().size() == 2)
		{
			int i = 0;
			WebElement saveddate0 = actAdviserDashboardPage.getIsSavedOrdersDate().get(i);
			String saveddatestr0 = saveddate0.getText();

			WebElement saveddate1 = actAdviserDashboardPage.getIsSavedOrdersDate().get(i + 1);
			String saveddatestr1 = saveddate1.getText();

			if (saveddatestr1 == "Today")
			{

				assertTrue(saveddatestr0.matches("Today"));
			}

			else
			{

				String saveddatesttr1 = saveddatestr0.substring(0, saveddatestr0.indexOf(" "));

				Integer x1 = Integer.valueOf(saveddatesttr1);

				String saveddatesttr2 = saveddatestr1.substring(0, saveddatestr1.indexOf(" "));
				Integer x2 = Integer.valueOf(saveddatesttr2);

				assertTrue(x2 > x1);

			}

		}

	}

	@Step
	public void headerMaturingTermDepositsPresent()
	{

		assertTrue(!actAdviserDashboardPage.getIsMaturingTermsHeader().getAttribute("href").isEmpty());

	}

	@Step
	public void accountNameHyperlinkMaturingTermPresent()
	{

		int i = 0;
		for (WebElement m_accnamematuringtd : actAdviserDashboardPage.getIsMaturingTermsAccountName())
		{

			m_accnamematuringtd = actAdviserDashboardPage.getIsMaturingTermsAccountName().get(i);
			m_accnamematuringtd.getAttribute("href");

			assertTrue(!m_accnamematuringtd.getAttribute("href").isEmpty());
			i++;
		}

	}

	@Step
	public void resultCountMaturingTermPresent()
	{
		int m_maturingtdlistssize, m_maturingtdresultcountint;
		String m_maturingtdresultcounttext, m_maturingtdresultcounttrimmedtext;
		Integer m_maturingtdresultcountinteger;

		m_maturingtdlistssize = actAdviserDashboardPage.getIsMaturingTermsDaysLeft().size();

		m_maturingtdresultcounttext = actAdviserDashboardPage.getIsMaturingTermssResultCount().getText();

		m_maturingtdresultcounttrimmedtext = Character.toString(m_maturingtdresultcounttext.charAt(8));

		m_maturingtdresultcountinteger = Integer.valueOf(m_maturingtdresultcounttrimmedtext);

		m_maturingtdresultcountint = Integer.valueOf(m_maturingtdresultcountinteger);

		assertEquals(m_maturingtdresultcountint, m_maturingtdlistssize);

	}

	@Step
	public void descriptionTextMaturingTermAndTDCPresent()
	{
		assertTrue(actAdviserDashboardPage.getIsMaturingTDCalculator().isDisplayed());
		int i = 0;
		for (WebElement m_descriptiontextmaturingtd : actAdviserDashboardPage.getIsMaturingTermsDescriptionText())
		{

			m_descriptiontextmaturingtd = actAdviserDashboardPage.getIsMaturingTermsDescriptionText().get(i);

			assertTrue(!m_descriptiontextmaturingtd.getText().isEmpty());

			i++;
		}
	}

	@Step
	public void headerMaturingTermClick() throws Throwable
	{

		actAdviserDashboardPage.getIsMaturingTermsHeader().click();

	}

	@Step
	public void maturingTermShowingPatternMatch()
	{
		WebElement m_resultcountmaturingtd;
		String m_maturingcountstr;

		m_resultcountmaturingtd = actAdviserDashboardPage.getIsMaturingTermssResultCount();

		m_maturingcountstr = m_resultcountmaturingtd.getText();

		assertTrue(m_maturingcountstr.matches("Showing [1-3] of \\d+?, maturing in the next 30 days"));

	}

	@Step
	public void accountNameMaturingTermClick() throws Throwable
	{

		actAdviserDashboardPage.getIsMaturingTermsAccountName().get(0).click();

	}

	@Step
	public void calculatorMaturingTermClick() throws Throwable
	{

		actAdviserDashboardPage.getIsMaturingTDCalculator().click();

	}

	@Step
	public void maturingTermDatePatternMatch()
	{

		for (WebElement m_datelistmaturingtd : actAdviserDashboardPage.getIsMaturingTermsDate())
		{

			String Str1 = m_datelistmaturingtd.getText();

			String Result = Str1.substring(6);

			DateFormat format = new SimpleDateFormat("dd MMM yyyy");

			Date d1;
			try
			{
				d1 = format.parse(Str1);

			}
			catch (ParseException e)
			{
				assertEquals("Date not matched", "Date Matched");
				e.printStackTrace();

			}

		}

	}

	@Step
	public void maturingTermDaysLeftPatternMatch()
	{
		String m_maturingdaysleftformatstr;
		for (WebElement m_daysleftmaturingtd : actAdviserDashboardPage.getIsMaturingTermsDaysLeft())
		{

			m_maturingdaysleftformatstr = m_daysleftmaturingtd.getText();
			if (m_maturingdaysleftformatstr == "Today")
			{
				assertTrue(m_maturingdaysleftformatstr.matches("Today"));
			}

			assertTrue(m_maturingdaysleftformatstr.matches("\\d+? day(s)? left"));

		}

	}

	@Step
	public void maturingTermDecimalPatternMatch()
	{
		String m_maturingdecimalformatstr;

		for (WebElement m_decimalformtmaturingtd : actAdviserDashboardPage.getIsMaturingTermsDate())
		{

			m_maturingdecimalformatstr = m_decimalformtmaturingtd.getText();

			//assertTrue(m_maturingdecimalformatstr.matches("\\d+(\\.\\d{2}$"));
			assertTrue(m_maturingdecimalformatstr.matches("\\d+(\\.\\d{2})?"));
		}

	}

	@Step
	public void descendingDateMaturingTermDisplayVerify() throws Throwable
	{

		if (actAdviserDashboardPage.getIsMaturingTermsDaysLeft().size() == 3)
		{
			int i = 0;
			WebElement matureddate0 = actAdviserDashboardPage.getIsMaturingTermsDaysLeft().get(i);
			String matureddatestr0 = matureddate0.getText();

			WebElement matureddate1 = actAdviserDashboardPage.getIsMaturingTermsDaysLeft().get(i + 1);
			String matureddatestr1 = matureddate1.getText();

			WebElement matureddate2 = actAdviserDashboardPage.getIsMaturingTermsDaysLeft().get(i + 2);
			String matureddatestr2 = matureddate2.getText();

			if (matureddatestr2 == "Today")
			{

				assertTrue(matureddatestr1.matches("Today"));
				assertTrue(matureddatestr0.matches("Today"));
			}
			if (matureddatestr1 == "Today")
			{

				assertTrue(matureddatestr0.matches("Today"));
			}
			if (matureddatestr0.equals("Today") && !(matureddatestr1.equals("Today")))
			{

				String maturedstr1 = matureddatestr1.substring(0, matureddatestr1.indexOf(" "));
				Integer x1 = Integer.valueOf(maturedstr1);

				String maturedstr2 = matureddatestr2.substring(0, matureddatestr2.indexOf(" "));
				Integer x2 = Integer.valueOf(maturedstr2);

				assertTrue(x2 > x1);

			}

			else
			{

				String maturedstr1 = matureddatestr0.substring(0, matureddatestr0.indexOf(" "));

				Integer x1 = Integer.valueOf(maturedstr1);

				String maturedstr2 = matureddatestr1.substring(0, matureddatestr1.indexOf(" "));
				Integer x2 = Integer.valueOf(maturedstr2);

				String maturedstr3 = matureddatestr2.substring(0, matureddatestr2.indexOf(" "));
				Integer x3 = Integer.valueOf(maturedstr3);

				assertTrue(x2 > x1);
				assertTrue(x3 > x2);

			}

		}

		if (actAdviserDashboardPage.getIsMaturingTermsDaysLeft().size() == 2)
		{
			int i = 0;
			WebElement matureddate0 = actAdviserDashboardPage.getIsMaturingTermsDaysLeft().get(i);
			String matureddatestr0 = matureddate0.getText();

			WebElement matureddate1 = actAdviserDashboardPage.getIsMaturingTermsDaysLeft().get(i + 1);
			String matureddatestr1 = matureddate1.getText();

			if (matureddatestr1 == "Today")
			{

				assertTrue(matureddatestr0.matches("Today"));
			}

			else
			{

				String maturedstr1 = matureddatestr0.substring(0, matureddatestr0.indexOf(" "));

				Integer x1 = Integer.valueOf(maturedstr1);

				String maturedstr2 = matureddatestr1.substring(0, matureddatestr1.indexOf(" "));
				Integer x2 = Integer.valueOf(maturedstr2);

				assertTrue(x2 > x1);

			}

		}

	}

	@Step
	public void alternateScenario1MaturingTdDisplayVerify()
	{
		String alternatescenariostrtext;

		alternatescenariostrtext = actAdviserDashboardPage.getIsMaturingAltScenario1Text().getText();

		assertEquals(alternatescenariostrtext, "There are no term deposits");

	}

	@Step
	public void alternateScenario2MaturingTdDisplayVerify()
	{

		String alternatescenariomaturingtd2text;
		assertTrue((actAdviserDashboardPage.getIsMaturingTermsHeader().isDisplayed()));

		alternatescenariomaturingtd2text = actAdviserDashboardPage.getIsMaturingAltScenario2Text().getText();

		assertEquals(alternatescenariomaturingtd2text, "There are no term deposits maturing in the next 30 days");

	}

	@Step
	public void helpIconKeyActivityPresent()
	{
		assertTrue(actAdviserDashboardPage.getIsKeyActivityHelp().isDisplayed());

	}

	@Step
	public void activityTypeAndClientAccountNameHyperlinkKeyActivityPresent()
	{
		int i = 0;
		for (WebElement acctypekeyactivity : actAdviserDashboardPage.getIsKeyActivityType())
		{

			acctypekeyactivity = actAdviserDashboardPage.getIsKeyActivityType().get(i);
			String acctypeattributevalue = acctypekeyactivity.getAttribute("href");

			i++;
		}

		int j = 0;
		for (WebElement accnamehyperkeyactivity : actAdviserDashboardPage.getIsKeyActivityAccountName())
		{

			accnamehyperkeyactivity = actAdviserDashboardPage.getIsKeyActivityAccountName().get(j);
			String accnameattributevalue = accnamehyperkeyactivity.getAttribute("href");

			j++;
		}

	}

	@Step
	public void descriptionKeyActivity()
	{
		int i = 0;
		for (WebElement accdescriptionhyperkeyactivity : actAdviserDashboardPage.getIsKeyActivityDescriptionText())
		{

			accdescriptionhyperkeyactivity = actAdviserDashboardPage.getIsKeyActivityDescriptionText().get(i);

			assertTrue(accdescriptionhyperkeyactivity.isDisplayed());

			i++;
		}

	}

	@Step
	public void priorityIndicatorKeyActivityPresent()
	{
		int i = 0;
		for (WebElement accpriorsymbolkeyactivity : actAdviserDashboardPage.getIsKeyActivityPriorityIndicator())
		{

			accpriorsymbolkeyactivity = actAdviserDashboardPage.getIsKeyActivityPriorityIndicator().get(i);

			assertTrue(accpriorsymbolkeyactivity.isDisplayed());
			i++;
		}

	}

	@Step
	public void activityDateAndTimeStampKeyActivity()
	{
		int i = 0;
		for (WebElement accdatekeyactivity : actAdviserDashboardPage.getIsKeyActivityDateAndTimeStamp())
		{

			accdatekeyactivity = actAdviserDashboardPage.getIsKeyActivityDateAndTimeStamp().get(i);

			assertTrue(accdatekeyactivity.isDisplayed());
			i++;
		}

	}

	@Step
	public void actionIconPresenceVerify()
	{
		assertTrue(actAdviserDashboardPage.getIsKeyActivityOnBoardingButton().isDisplayed());
		assertTrue(actAdviserDashboardPage.getIsKeyActivityOrderStatusButton().isDisplayed());

	}

	@Step
	public void accountTypeKeyActivityClick() throws Throwable
	{

		actAdviserDashboardPage.getIsKeyActivityType().get(0).click();

	}

	@Step
	public void accountNameKeyActivityClick() throws Throwable
	{

		actAdviserDashboardPage.getIsKeyActivityAccountName().get(0).click();

	}

	@Step
	public void keyActivityDatePatternMatch()
	{

		for (WebElement keyDateFormt : actAdviserDashboardPage.getIsKeyActivityDateAndTimeStamp())
		{

			String Str1 = keyDateFormt.getText();

			String Result = Str1.substring(6);

			DateFormat format = new SimpleDateFormat("dd MMM yyyy");

			Date d1;
			try
			{
				d1 = format.parse(Str1);

			}
			catch (ParseException e)
			{
				assertEquals("Date not matched", "Date Matched");
				e.printStackTrace();

			}
		}

	}

	@Step
	public void onboardingButtonClick() throws Throwable
	{

		actAdviserDashboardPage.getIsKeyActivityOnBoardingButton().click();

	}

	@Step
	public void orderStatusButtonClick() throws Throwable
	{

		actAdviserDashboardPage.getIsKeyActivityOrderStatusButton().click();

	}

	@Step
	public void mouseHoverKeyActivity() throws Throwable
	{

		Actions actions = new Actions(getDriver());
		WebElement menuHoverLink = actAdviserDashboardPage.getIsKeyActivityHelp();
		actions.moveToElement(menuHoverLink);
		actions.perform();

	}

	@Step
	public void alternateScenarioKeyActivityDisplayVerify() throws Throwable
	{
		String alternatescenariokeyactivitytext;
		assertTrue((actAdviserDashboardPage.getIsMaturingTermsHeader().isDisplayed()));

		alternatescenariokeyactivitytext = actAdviserDashboardPage.getIsAltScenarionKeyActivityText().getText();

		assertEquals(alternatescenariokeyactivitytext, "There has been no key activity");
	}

	@Step
	public void descendingDateKeyActivityDisplayVerify() throws Throwable
	{
		List <Date> dateRead = new ArrayList <Date>();
		List <String> actualdateRead = new ArrayList <String>();

		for (WebElement m_dateSortList : actAdviserDashboardPage.getIsRightDateHeading())
		{
			String Str1 = m_dateSortList.getText();

			String[] Result = Str1.trim().split(",");
			actualdateRead.add(Result[1]);
			DateFormat format = new SimpleDateFormat("dd MMM yyyy");
			Date d1;
			d1 = format.parse(Result[1]);
			dateRead.add(d1);

		}

		Collections.sort(dateRead, new Comparator <Date>()
		{

			@Override
			public int compare(Date o1, Date o2)
			{
				return o1.compareTo(o2);
			}
		});

		int j = 0;
		for (int i = dateRead.size() - 1; i >= 0; i--)
		{

			DateFormat format = new SimpleDateFormat("dd MMM yyyy");
			Date d1;
			d1 = format.parse(actualdateRead.get(j));
			assertEquals(dateRead.get(i).toString(), d1.toString());
			j++;
		}

	}

	@Step
	public void disclaimersTextVerify() throws Throwable
	{

		assertTrue((actAdviserDashboardPage.getisDisclaimersText().isDisplayed()));
	}

	@Step
	public List <Map> testCountKeyActivity(List <String> headerName) throws Throwable
	{
		List <WebElement> tableProp = actAdviserDashboardPage.getTableRecKeyActivity();
		return actAdviserDashboardPage.tableContTest(headerName, tableProp);
		//return loginPage.tableContTest(headerName, tableProp);
	}

}
