package com.bt.nextgen.core.reporting;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.TimeZone;

public final class ReportFormatter {

    private final static String STANDARD_TIME = " AEST";
    private final static String DAYLIGHT_SAVINGS_TIME = " AEDT";

    private ReportFormatter() {
    }

    @SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142" })
    public static String formatTelephoneNumber(String number) {
        String formattedNumber = "";
        boolean australiaMobile = false;
        boolean australiaLandline = false;
        boolean australiaTollfree = false;
        boolean international = false;

        if (StringUtils.isNotBlank(number)) {

            String mobilePattern = "^04(.)*|^05(.)*";
            String landlinePattern = "^0[0-35-9](.)*";
            String tollfreePattern = "^1800(.)*|^1300(.)*";

            australiaMobile = number.matches(mobilePattern);
            australiaLandline = number.matches(landlinePattern);
            australiaTollfree = number.matches(tollfreePattern);
            international = (!australiaMobile && !australiaLandline && !australiaTollfree);

            // If number is australian-landline then put spaces between 4
            // digits, otherwise between 3 digits
            int spaceDigits = australiaLandline ? 4 : 3;
            int firstSpace = 0;

            String newValue = String.valueOf(number.charAt(number.length() - 1));
            int i = number.length() - 2;
            while (i >= 0) {
                if (i > firstSpace && (i - number.length() + 1) % spaceDigits == 0) {
                    newValue = new StringBuilder(" ").append(newValue).toString();
                }
                StringBuilder val = new StringBuilder(String.valueOf(number.charAt(i)));
                newValue = val.append(newValue).toString();
                i--;
            }
            formattedNumber = newValue;

            // Show a plus symbol at the start of international numbers
            if (international) {
                formattedNumber = '+' + formattedNumber;
            } else if (australiaLandline && formattedNumber.length() > 1) {
                formattedNumber = '(' + formattedNumber.substring(0, 2) + ')' + formattedNumber.substring(2);
            }
        }
        return formattedNumber;
    }

    public static final String format(ReportFormat type, boolean includeSymbol, BigDecimal value) {
        if (value == null) {
            return "-";
        }
        if (includeSymbol) {
            return new DecimalFormat(type.getFormat()).format(value);
        } else {
            return new DecimalFormat(type.getFormatWithoutSymbol()).format(value);
        }
    }

    public static final String format(ReportFormat type, BigDecimal value) {
        return format(type, true, value);
    }

    public static final String format(ReportFormat type, Integer value) {
        return format(type, true, value == null ? null : BigDecimal.valueOf(value));
    }

    public static final String format(ReportFormat type, DateTime value) {
        if (value == null) {
            return "-";
        }
        DateTimeFormatter formatter = DateTimeFormat.forPattern(type.getFormat());

        // Work around for old jvm version in EPS. Timezone code there is AEST for AEDT (summer time).
        if (type == ReportFormat.LONG_DATE) {
            String rawDateTime = formatter.print(value);
            DateTimeZone tz = DateTimeZone.forTimeZone(TimeZone.getDefault());
            Boolean isDayLightSaving = !tz.isStandardOffset(value.getMillis());
            return isDayLightSaving ? rawDateTime.concat(DAYLIGHT_SAVINGS_TIME) : rawDateTime.concat(STANDARD_TIME);
        }
        return formatter.print(value);
    }

}
