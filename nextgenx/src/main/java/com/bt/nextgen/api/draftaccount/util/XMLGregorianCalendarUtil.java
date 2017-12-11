package com.bt.nextgen.api.draftaccount.util;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import static java.lang.Integer.parseInt;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class XMLGregorianCalendarUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(XMLGregorianCalendarUtil.class);

    public static XMLGregorianCalendar getXMLGregorianCalendar(String date, String format) {
        if (date != null) {
            GregorianCalendar c = new GregorianCalendar();
            DateFormat dateFormat = new SimpleDateFormat(format);
            try {
                c.setTime(dateFormat.parse(date));
                XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                calendar.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
                return calendar;
            }
            catch (ParseException e) {
                LOGGER.error("date not in " + format + " format.", e);
            }
            catch (DatatypeConfigurationException e) {
                LOGGER.error("Cannot instantiate DatatypeFactory.", e);
            }
        }
        return null;
    }

    public static XMLGregorianCalendar getXMLGregorianCalendarNow() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        try {
            DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
            XMLGregorianCalendar now = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
            now.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
            return now;
        }
        catch (DatatypeConfigurationException e) {
            LOGGER.error("Cannot instantiate DatatypeFactory.", e);
        }
        return null;
    }

    public static XMLGregorianCalendar date(int year, int month, int day) {
        XMLGregorianCalendar date = null;
        try {
            date = DatatypeFactory.newInstance().newXMLGregorianCalendar();
            date.setYear(year);
            date.setMonth(month);
            date.setDay(day);
        }
        catch (DatatypeConfigurationException dce) {
            LOGGER.error("Unable to create DatatypeFactory", dce);
        }
        return date;
    }

    public static XMLGregorianCalendar date(String date) {
        if (date != null) {
            final StringTokenizer tokens = new StringTokenizer(date, "-");
            if (tokens.countTokens() != 3) {
                throw new IllegalArgumentException("Invalid date format:\"" + date + "\", expecting yyyy-MM-dd");
            }
            return date(parseInt(tokens.nextToken()), parseInt(tokens.nextToken()), parseInt(tokens.nextToken()));
        }
        return null;
    }

    /**
     * Parse an XML Gregorian Calendar instance, specifically looking only for DATE fields (year/month/day), and
     * ignoring all TIME fields.
     *
     * @param date   the formatted date string.
     * @param format the date format to use in order to parse the date.
     *
     * @return the parsed XMLGregorianCalendar instance, with only the year, month and day fields set.
     */
    public static XMLGregorianCalendar date(String date, String format) {
        XMLGregorianCalendar calendar = null;
        if (date != null) {
            try {
                final DateFormat formatter = new SimpleDateFormat(format);
                final GregorianCalendar parsed = new GregorianCalendar();
                parsed.setTime(formatter.parse(date));
                calendar = date(parsed.get(YEAR), parsed.get(MONTH) + 1, parsed.get(DAY_OF_MONTH));
            }
            catch (ParseException pe) {
                LOGGER.error("Error parsing date {} with format {}", date, format, pe);
            }
        }
        return calendar;
    }

    /**
     * Convert XMLGregorianCalendar to DateTime
     * @param date
     * @return
     */
    public static DateTime convertToDateTime(XMLGregorianCalendar date) {
        if (date != null) {
            return new DateTime(date.toGregorianCalendar().getTime());
        }
        return null;
    }

    /**
     * Convert DateTime to XMLGregorianCalendar
     * @param dateTime
     * @return
     */
    public static XMLGregorianCalendar convertToXMLGregorianCalendar(DateTime dateTime) {
        if (dateTime != null) {
            try {
                final GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTimeInMillis(dateTime.getMillis());
                return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
            }
            catch (DatatypeConfigurationException de) {
                LOGGER.error("Error parsing date {}", dateTime, de);
            }
        }
        return null;
    }
}
