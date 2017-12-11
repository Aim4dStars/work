package com.bt.nextgen.service.avaloq.insurance;

import com.bt.nextgen.service.avaloq.insurance.service.RenewalDateConverter;
import org.joda.time.DateTime;
import org.junit.Test;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

public class RenewalDateConverterTest {

    @Test
    public void pastDateTest() {
        String renewalDate = "--07-10";
        RenewalDateConverter renewalDateConverter = new RenewalDateConverter();
        DateTime dateTime = renewalDateConverter.convert(renewalDate);
        assertEquals(dateTime.getDayOfMonth(), 10);
        assertEquals(dateTime.getMonthOfYear(), 7);
        assertEquals(dateTime.getYear(), getRenewalDate(Calendar.JULY,10).getYear());
    }

    /**
     * paramter is the desired month required with date, month index starts with zero
     * @param month
     * @return
     */
    private DateTime getRenewalDate(int month, int dateOfMonth)
    {
        GregorianCalendar today = new GregorianCalendar();
        GregorianCalendar date = new GregorianCalendar();
        date.set(Calendar.DATE, dateOfMonth);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MILLISECOND, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MILLISECOND, 0);
        if (date.getTime().compareTo(today.getTime()) < 0) {
            date.set(Calendar.YEAR, new org.joda.time.DateTime().getYear() + 1);
        }
        return new DateTime(date.getTime());
    }
}