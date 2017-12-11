package com.bt.nextgen.web;

import com.bt.nextgen.core.web.ApiFormatter;
import org.hamcrest.core.IsEqual;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by L070589 on 19/08/2015.
 */
public class ApiFormatterTest {

    @Test
    public void testAsMaxLengthDecimalFormat() throws Exception {
        assertThat(ApiFormatter.asDecimal(new BigDecimal("1234567890.00")), IsEqual.equalTo("1,234,567,890.00"));
    }

    @Test
    public void testStringConcat_NullIsEmpty() throws Exception {
        assertThat(ApiFormatter.concatStrings(null, "james"), IsEqual.equalTo(""));
    }

    @Test
    public void testStringConcat_NonNullNotEmpty() throws Exception {
        assertThat(ApiFormatter.concatStrings("james", " ", "Sammut"), IsEqual.equalTo("james Sammut"));
    }

    @Test
    public void testasDecimal_NullReturnsNull() throws Exception {
        assertThat(ApiFormatter.asDecimal(null), nullValue());
    }





    @Test
    public void testFormatISODate_ReturnMatches() throws Exception {
        String dateStr = "2014-04-10T12:23:11.000+10:00";
        DateTime dateTime = ApiFormatter.parseISODate(dateStr);

        Assert.assertEquals(2014, dateTime.getYear());
        Assert.assertEquals(4, dateTime.getMonthOfYear());
        Assert.assertEquals(10, dateTime.getDayOfMonth());

        Assert.assertEquals(23, dateTime.getMinuteOfHour());
        Assert.assertEquals(11, dateTime.getSecondOfMinute());
    }

    @Test
    public void testFormatAsShortDateTime_ReturnMatches() throws Exception {
        assertThat(ApiFormatter.asShortDateTime(new DateTime("2015-11-05T03:00:15+11:00")),
                IsEqual.equalTo("05 Nov 2015, 03:00 AM AEDT"));
    }
}
