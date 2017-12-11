package com.bt.nextgen.core.type;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.GregorianCalendar;

import com.bt.nextgen.core.web.ApiFormatter;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import com.bt.nextgen.payments.domain.PaymentFrequency;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateUtil
{
	public static final SimpleDateFormat DEFAULT_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat PERSON_FORMAT = new SimpleDateFormat("dd MMM yyyy");


    private DateUtil()
	{}

	private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

	public static String formatDate(Date date)
	{
		return formatDate(date, DEFAULT_FORMAT);
	}
	
	public static String toFormattedDate(Date fromDate, DateFormatType targetDateFormat)
	{
		SimpleDateFormat format = new SimpleDateFormat(targetDateFormat.getDateFormat());
		return formatDate(fromDate, format);
	}

	public static String formatDate(Date date, SimpleDateFormat format)
	{
		return format.format(date);
	}

	public static String formatDate(String dateStr, DateFormatType fromFormatType, DateFormatType toFormatType) throws Exception
	{
		String formattedDate = "";
		
		DateFormat fromFormat = new SimpleDateFormat(fromFormatType.getDateFormat());
		fromFormat.setLenient(false);
		DateFormat toFormat = new SimpleDateFormat(toFormatType.getDateFormat());
		toFormat.setLenient(false);
		
		Date date = fromFormat.parse(dateStr);
		formattedDate = toFormat.format(date);
		return formattedDate;
	}


	public static String endDateBefore(String paymentDateString, String endDateString, PaymentFrequency paymentFrequency)
	{
		String format = "dd MMM yyyy";
		DateTime endDate = DateTime.parse(endDateString, DateTimeFormat.forPattern(format));
		DateTime startDate = DateTime.parse(paymentDateString, DateTimeFormat.forPattern(format));

		DateTime lastPaymentDate;
		int monthlyCounter = 0;
		for (lastPaymentDate = new DateMidnight(startDate).toDateTime(); isBeforeOrEquals(paymentFrequency.increment(lastPaymentDate),
			endDate); lastPaymentDate = paymentFrequency.increment(lastPaymentDate))
		{
			// the loop takes care of the boundary check as well as the incrementing
			monthlyCounter++;
		}

		if (PaymentFrequency.MONTHLY == paymentFrequency)
		{
			DateTime toIncrement = new DateMidnight(startDate).toDateTime();
			lastPaymentDate = paymentFrequency.increment(toIncrement, monthlyCounter);
		}
		return lastPaymentDate.toString(format);
	}

	private static boolean isBeforeOrEquals(DateTime date1, DateTime date2)
	{
		return date1.compareTo(date2) <= 0;
	}

	public static DateTime getLatestDateTime(List <DateTime> dateTimeList)
	{
		DateTime dateTime = null;
		if (dateTimeList != null)
		{
			Collections.sort(dateTimeList, new Comparator <DateTime>()
			{
				@Override
				public int compare(DateTime firstDate, DateTime secondDate)
				{
					return (firstDate.compareTo(secondDate));
				}
			});

			dateTime = dateTimeList.get(0);
		}

		return dateTime;
	}

	public static long daysBetween(Date startDate, Date endDate)
	{
		return Days.daysBetween(new DateTime(startDate), new DateTime(endDate)).getDays();
	}

	public static String getFinYearStartDate(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH) + 1;
		if (month < 7)
		{
			calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
		}
		else
		{
			calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		}
		calendar.set(Calendar.MONTH, Calendar.JULY);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return ApiFormatter.asShortDate(calendar.getTime());
	}

	public static String getFinYearEndDate(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH) + 1;
		if (month < 7)
		{
			calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		}
		else
		{
			calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
		}
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		calendar.set(Calendar.DAY_OF_MONTH, 30);
		return ApiFormatter.asShortDate(calendar.getTime());
	}

	public static String getFinPeriodStartYear(Date date)
	{
		return getYear(getFinYearStartDate(date));
	}

	public static String getFinPeriodEndYear(Date date)
	{
		return getYear(getFinYearEndDate(date));
	}

	public static boolean isAfterOrEquals(DateTime date1, DateTime date2)
	{
		return date1.compareTo(date2) >= 0;
	}

	@SuppressWarnings("checkstyle:com.puppycrawl.tools.checkstyle.checks.coding.IllegalTypeCheck")
	public static XMLGregorianCalendar convertDateInGregorianCalendar(Date date){

		final GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);

		XMLGregorianCalendar gregorianDate = null;

		try {
			gregorianDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		} catch (DatatypeConfigurationException e) {
			logger.error("Error while converting date into GregorianCalendar", e);
			return null;
		}
		return gregorianDate;
	}

	/**
    * This method compares the input date with current date and returns true if they are equal, false otherwise
	*
	* @param date
	* @return true/false
	*/
	public static boolean isToday(Date date) { return (new DateTime(date).toLocalDate()).isEqual(new LocalDate()); }

	/**
	 * This method formats currentDate in required format for displaying in payment receipt screen
	 *
	 * @return currentDate
	 */
	public static String formatCurrentTransactionDate() {
		SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd MMM yyyy hh:mm a z");
		return dateTimeFormatter.format(new Date());
	}

    public static String getDay(String date)
    {
        return date.substring(0, 2);
    }

    public static String getMonth(String date)
    {
        return date.substring(3, 6);
    }

    public static String getYear(String date)
    {
        return date.substring(7);
    }

    public static DateTime convertToDateTime(String source, String pattern)
    {
        DateTime date = null;
        DateTimeFormatter format = DateTimeFormat.forPattern(pattern);
        date =format.parseDateTime(source);
        return date;
    }
}
