package com.bt.nextgen.messages;

import org.joda.time.DateTime;
import org.junit.Test;

import com.btfin.panorama.core.conversion.DateTimeTypeConverter;

import static org.junit.Assert.assertNotNull;

public class DateTimeTypeConverterTest
{
	DateTimeTypeConverter converter = new DateTimeTypeConverter();

	/**
	 * TODO This is a very loose test
	 */
	@Test
	public void testDateTimeValidator() throws Exception
	{
		String dateTimeVal = "2014-08-28T18:09:09+10:00";
		String dateVal = "2014-12-12";

		DateTime dateTime = converter.convert(dateTimeVal);
		assertNotNull(dateTime);

		DateTime date = converter.convert(dateVal);
		assertNotNull(date);

	}

}