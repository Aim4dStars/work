package com.bt.nextgen.util.type;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import com.bt.nextgen.core.web.ApiFormatter;
import org.junit.Test;

import com.bt.nextgen.core.type.DaysLeft;

public class DaysLeftTest
{
	@Test
	public void testDaysLeft()
	{
		Date now = new Date();
		String date1 = ApiFormatter.asShortDate(now);
		assertNull(DaysLeft.daysLeft(null));
		assertNotNull(DaysLeft.daysLeft(date1));

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 2);
		cal.add(Calendar.MONTH, 4);
		String date2 = ApiFormatter.asShortDate(cal.getTime());
		assertNotNull(DaysLeft.daysLeft(date2));
			
		Calendar testcal = Calendar.getInstance();
		testcal.add(Calendar.DATE, 2);
		testcal.add(Calendar.MONTH, 12);
		String testdate = ApiFormatter.asShortDate(testcal.getTime());
		assertNotNull(DaysLeft.daysLeft(testdate));
	}
}
