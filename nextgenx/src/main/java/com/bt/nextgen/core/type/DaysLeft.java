package com.bt.nextgen.core.type;

import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DaysLeft
{

	private static boolean isBeforeOrEquals(LocalDate date1, LocalDate date2)
	{
		return (date1 != null && date2 != null) && (date1.isBefore(date2) || date1.equals(date2));
	}

	/**
	 * If the remaining time is less than 12 months, then display the months and the days.
		If the remaining time is less than 3 months, then display in days.
		If the remaining time is more than 12 months then display in years and then months and then days. 
		Unless if it's a 12 months and less than one month, display in days. E.g. 2 years and 4 days left. 
	 * @param maturityDateString
	 * @return
	 */
	public static String daysLeft(String maturityDateString)
	{
		String returnValue = null;
		if (maturityDateString != null)
		{
			DateTimeFormatter format = DateTimeFormat.forPattern("dd MMM yyyy");
			LocalDate maturityDate = LocalDate.parse(maturityDateString, format);
			LocalDate currentDate = new LocalDate();

			int months;
			if (isBeforeOrEquals(currentDate, maturityDate))
			{
				Years Age = Years.yearsBetween(currentDate, maturityDate);
				int Age1 = Age.getYears();
				returnValue = String.valueOf(Age1);

				Period period = new Period(currentDate, maturityDate, PeriodType.yearMonthDay());

				months = Months.monthsBetween(currentDate, maturityDate).getMonths();

				if (months < 3)
				{
					int days = Days.daysBetween(currentDate.toDateMidnight(), maturityDate.toDateMidnight()).getDays();
					returnValue = (days + (days == 1 ? " day" : " days"));
				}
				else if (months >= 3 && months < 12)
				{

					returnValue = (period.getMonths() + (period.getMonths() == 1 ? " month" : " months") + " and "
						+ period.getDays() + (period.getDays() == 1 ? " day" : " days"));
				}
				else
				{
					returnValue = (period.getYears() + (period.getYears() == 1 ? " year" : " years") + " and "
						+ period.getMonths() + (period.getMonths() == 1 ? " month" : " months") + " and " + period.getDays() + (period.getDays() == 1
						? " day"
						: " days"));
				}
			}
		}
		return returnValue;
	}

}
