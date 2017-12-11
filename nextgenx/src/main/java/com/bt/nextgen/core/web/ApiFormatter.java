package com.bt.nextgen.core.web;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.apache.commons.lang.StringUtils.join;

//TODO Need to either divide the class into two or move to a more suitable package struct.
@SuppressWarnings({ "findbugs:DLS_DEAD_LOCAL_STORE", "findbugs:STCAL_INVOKE_ON_STATIC_DATE_FORMAT_INSTANCE", "squid:S881" })
public final class ApiFormatter implements Serializable {
    private final static String sShortDateFormat = "dd MMM yyyy";


    public static final String dayMonthDateYearPattern = "EEE MMM dd yyyy";

    public final static String DECIMAL_FORMAT = "#,##0.00";
    public final static String MF_DECIMAL_FORMAT = "#,##0.0000";
    public final static String INTEGER_FORMAT = "#,##0";
    private static String csvHeaderDateFormat = "dd-MMM-yy";
    private static String csvDateFormat = "dd/MM/yyyy";

    private static final String DECIMAL_FORMAT_ZERO = "0.00";
    private static final String MF_DECIMAL_FORMAT_ZERO = "0.0000";

    private static final String aestFmt = "hh.mm a ' AEST on ' dd MMM yyyy";
    private static String fileDateFormat = "ddMMMyy";

    // added for temp fix to return AEDT during daylight savings
    private final static String STANDARD_TIME = " AEST";
    private final static String DAYLIGHT_SAVINGS_TIME = " AEDT";
    private final static String sShortDateTimeFormatWithNoZone = "dd MMM yyyy, hh:mm a";

    private ApiFormatter() {

    }

    public static String limit(String field, int maxLength) {
        return limit(field, maxLength, "");
    }

    public static String limit(String field, int maxLength, String ellipsis) {
        if (StringUtils.isNotBlank(field) && field.length() > maxLength) {
            return field.substring(0, maxLength - ellipsis.length()) + ellipsis;
        } else if (StringUtils.isBlank(field)) {
            return "";
        } else {
            return field;
        }
    }

    public static DateTime parseDate(String dateTime) {
        if (StringUtils.isBlank(dateTime)) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormat.forPattern(sShortDateFormat);
        DateTime returnDate = DateTime.parse(dateTime, formatter);
        return returnDate;
    }

    public static DateTime parseDateTimeToDayMonthDateYearPattern(String dateTime) {
        if (StringUtils.isBlank(dateTime)) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormat.forPattern(dayMonthDateYearPattern);
        DateTime returnDate = DateTime.parse(dateTime, formatter);
        return returnDate;
    }

    public static DateTime parseISODate(String dateTime) {
        if (StringUtils.isBlank(dateTime)) {
            return null;
        }
        DateTimeFormatter formatter = ISODateTimeFormat.dateTimeParser();
        DateTime returnDate = DateTime.parse(dateTime, formatter);
        return returnDate;
    }

    public static String asMediumDate(Date toFormat) {
        SimpleDateFormat mediumDateFormat = new SimpleDateFormat("dd MMMMM yyyy");
        return mediumDateFormat.format(toFormat);
    }

    public static String asMonthYear(Date toFormat) {
        if (toFormat == null) {
            return "";
        }
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMM yyyy");
        return monthYearFormat.format(toFormat);
    }

    public static String asDecimal(BigDecimal amount) {
        if (amount == null) {
            return null;
        }
        String value = new DecimalFormat(DECIMAL_FORMAT).format(amount);
        return treatNegativeZero(DECIMAL_FORMAT_ZERO, value);
    }

    public static String asManagedFundDecimal(BigDecimal amount) {
        if (amount == null) {
            return null;
        }
        String value = new DecimalFormat(MF_DECIMAL_FORMAT).format(amount);
        return treatNegativeZero(MF_DECIMAL_FORMAT_ZERO, value);
    }

    private static String treatNegativeZero(String zeroFormat, String value) {
        // Remove negative sign if small negative value was rounded to zero
        String negativeZero = "-" + zeroFormat;
        if (negativeZero.equals(value)) {
            return zeroFormat;
        }
        return value;
    }

    public static String asIntegerString(BigDecimal amount) {
        if (amount == null) {
            return null;
        }
        return new DecimalFormat(INTEGER_FORMAT).format(amount);
    }

    public static String asIntegerString(BigInteger unit) {
        if (unit == null) {
            return null;
        }
        return new DecimalFormat(INTEGER_FORMAT).format(unit);
    }

    /**
     * If any of the strings are null, then return Empty String, otherwise
     * concat all strings
     * 
     * @param stringsToConcat
     *            strings to concatenate
     * @return
     */
    public static String concatStrings(String... stringsToConcat) {
        for (String s : stringsToConcat) {
            if (null == s) {
                return "";
            }
        }

        return join(stringsToConcat);
    }

    public static String formatBsb(String bsb) {
        String newBsb = bsb;
        if (StringUtils.isNotBlank(bsb) && !bsb.contains("-")) {
            newBsb = bsb.substring(0, 3) + "-" + bsb.substring(3, bsb.length());
        }
        return newBsb;
    }

    public static String formatCurrentDate() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd MMM yyyy hh:mm aaa");
        DateTime currentDate = DateTime.now();
        return currentDate.toString(formatter).replace("AM", "am").replace("PM", "pm");
    }

    public static String asNormalDateFormat(Date date) {
        SimpleDateFormat toFormatter = new SimpleDateFormat(csvHeaderDateFormat);
        return toFormatter.format(date);
    }

    public static String asSimpleDateFormat(Date date) {
        SimpleDateFormat toFormatter = new SimpleDateFormat(csvDateFormat);
        return toFormatter.format(date);
    }

    /**
     * Format date with a pattern passed
     * 
     * @param date
     * @param formatPattern
     * @return
     */
    public static String aestFormat(Date date) {
        DateFormat dateFormatter = new SimpleDateFormat(aestFmt);
        dateFormatter.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));
        String dateString = dateFormatter.format(date);
        return dateString.replace("AM", "am").replace("PM", "pm");
    }

    /**
     * Format date with a pattern passed
     * 
     * @param date
     * @param formatPattern
     * @return
     */
    public static String asFileDateFormat(Date date) {
        SimpleDateFormat toFormatter = new SimpleDateFormat(fileDateFormat);
        return toFormatter.format(date);
    }

    public static String asShortDate(Date toFormat) {
        if (toFormat == null) {
            return "";
        }
        SimpleDateFormat shortDateFormat = new SimpleDateFormat(sShortDateFormat);
        return shortDateFormat.format(toFormat);
    }

    public static String asShortDate(DateTime toFormat) {
        if (toFormat == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormat.forPattern(ApiFormatter.sShortDateFormat);
        return formatter.print(toFormat);
    }

    public static String asShortDateTime(DateTime toFormat) {
        if (toFormat == null) {
            return "";
        }

        // added as a temp fix to return AEDT during day light savings
        DateTimeFormatter formatter = DateTimeFormat.forPattern(ApiFormatter.sShortDateTimeFormatWithNoZone);
        String rawDateTime = formatter.print(toFormat);
        DateTimeZone tz = DateTimeZone.forTimeZone(TimeZone.getDefault());
        Boolean isDayLightSaving = !tz.isStandardOffset(toFormat.getMillis());
        return isDayLightSaving ? rawDateTime.concat(ApiFormatter.DAYLIGHT_SAVINGS_TIME) : rawDateTime
                .concat(ApiFormatter.STANDARD_TIME);
    }

}
