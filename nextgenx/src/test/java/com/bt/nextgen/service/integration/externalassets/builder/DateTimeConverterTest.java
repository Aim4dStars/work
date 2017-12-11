package com.bt.nextgen.service.integration.externalassets.builder;

import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.service.integration.externalasset.builder.DateTimeConverter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DateTimeConverterTest
{
    @Test
    public void convertValidDateStringToJodaDateTime()
    {
        String dateString = "2001-07-15";
        DateTimeConverter dateTimeConverter = new DateTimeConverter();
        DateTime dateTime = dateTimeConverter.convert(dateString);

        DateTimeComparator comparator = DateTimeComparator.getDateOnlyInstance();
        DateTime targetDateTime = DateTime.parse(dateString, DateTimeFormat.forPattern("yyyy-MM-dd"));

        assertEquals(comparator.compare(targetDateTime, dateTime), 0);
    }

    @Test
    public void convertValidDateStringToDDMMMYYYY()
    {
        String dateString = "2001-07-15";
        DateTimeConverter dateTimeConverter = new DateTimeConverter();
        DateTime dateTime = dateTimeConverter.convert(dateString);


        String date = ApiFormatter.asShortDate(dateTime);
        assertEquals(date, "15 Jul 2001");

    }

}
