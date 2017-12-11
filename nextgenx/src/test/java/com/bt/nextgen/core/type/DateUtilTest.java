package com.bt.nextgen.core.type;

import com.bt.nextgen.payments.domain.PaymentFrequency;
import freemarker.template.SimpleDate;
import org.hamcrest.Matchers;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtilTest
{
	@Test
	public void testFormatDate_defaultFormat() throws Exception
	{
		Assert.assertThat(DateUtil.formatDate(new DateMidnight(2013, 3, 4).toDate()), Matchers.equalTo("2013-03-04"));

	}

	@Test
	public void testFormatDate_myFormat() throws Exception
	{
		Assert.assertThat(DateUtil.formatDate(new DateMidnight(2013, 3, 4).toDate(), new SimpleDateFormat("dd-MM-yyyy")), Matchers.equalTo("04-03-2013"));
	}




	@Test
	public void testEndDateBefore_weekly() throws Exception
	{
		Assert.assertThat(
			DateUtil.endDateBefore("03 Apr 2013", "10 Apr 2013", PaymentFrequency.WEEKLY)
			, Matchers.equalTo("10 Apr 2013"));
		
		Assert.assertThat(DateUtil.endDateBefore("25 Apr 2014", "26 Dec 2014", PaymentFrequency.WEEKLY), Matchers.equalTo("26 Dec 2014"));
	}

	@Test
	public void testEndDateBefore_fortnightly() throws Exception
	{
		Assert.assertThat(
			DateUtil.endDateBefore("03 Apr 2013", "21 Apr 2013", PaymentFrequency.WEEKLY)
			, Matchers.equalTo("17 Apr 2013"));
	}

	@Test
	public void testEndDateBefore_monthly() throws Exception
	{
		Assert.assertThat(
			DateUtil.endDateBefore("03 Apr 2013", "21 May 2013", PaymentFrequency.MONTHLY)
			, Matchers.equalTo("03 May 2013"));
		
		Assert.assertThat(DateUtil.endDateBefore("29 Jan 2014", "29 May 2015", PaymentFrequency.MONTHLY),
			Matchers.equalTo("29 May 2015"));
		Assert.assertThat(DateUtil.endDateBefore("30 Jan 2014", "31 Jul 2015", PaymentFrequency.MONTHLY), Matchers.equalTo("30 Jul 2015"));Assert.assertThat(DateUtil.endDateBefore("28 Feb 2014", "28 Jul 2015", PaymentFrequency.MONTHLY), Matchers.equalTo("28 Jul 2015"));
	}
	
	@Test
	public void formatFrontendToAvaloqDateFormat() throws Exception
	{
		String frontEndDate = "17 Jun 2014";
		
		String toDate = DateUtil.formatDate(frontEndDate, DateFormatType.DATEFORMAT_FRONT_END, DateFormatType.DATEFORMAT_AVALOQ);
		Assert.assertThat(toDate, Matchers.equalTo("2014-06-17"));
	}

	@Test
	public void testEndDateBefore_quarterly() throws Exception
	{
		Assert.assertThat(
			DateUtil.endDateBefore("03 Apr 2013", "21 Jul 2013", PaymentFrequency.QUARTERLY)
			, Matchers.equalTo("03 Jul 2013"));
	}

	@Test
	public void testEndDateBefore_yearly() throws Exception
	{
		Assert.assertThat(
			DateUtil.endDateBefore("03 Apr 2013", "21 Jul 2014", PaymentFrequency.YEARLY)
			, Matchers.equalTo("03 Apr 2014"));
	}

	@Test
	public void testEndDateBefore() throws Exception
	{

	}

	@Test
	public void testMain() throws Exception
	{

	}

	@Test
	public void testGetLatestDateTime() throws Exception
	{

	}

	@Test
	public void testDaysBetween() throws Exception
	{

	}

	@Test
	public void testIsToday() throws Exception {
		Date tomorrow = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(tomorrow);
		calendar.add(Calendar.DATE, 1);
		tomorrow = calendar.getTime();

		Assert.assertFalse(DateUtil.isToday(tomorrow));
		Assert.assertTrue(DateUtil.isToday(new Date()));
	}

	@Test
	public void testFormatCurrentTransactionDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy hh:mm a z");
		Assert.assertEquals(formatter.format(new Date()), DateUtil.formatCurrentTransactionDate());
	}

    @Test
    public void testCovertDateTime() {
        DateTime dateTime = DateUtil.convertToDateTime("2012-12-11T13:00:00.000+11:00","yyyy-MM-dd'T'HH:mm:ss.SSSZ" );
        Assert.assertEquals(dateTime.getYear(), 2012);
    }
}
