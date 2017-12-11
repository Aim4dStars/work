package com.bt.nextgen.core.reporting;

import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.core.web.Format;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * @deprecated Use reportFormatter for basic formatting, if you have special rules around your data such as rounding or joining,
 *             put it in your report data object.
 */
@Deprecated
public class ReportUtils {
    public static String join(String separator, Object... objects) {
        String result = "";

        if (objects != null) {
            Object previous = null;
            for (Object obj : objects) {
                if (obj != null) {
                    if (previous != null) {
                        result += separator + obj.toString();
                    } else {
                        result += obj.toString();
                    }

                    previous = obj;
                }
            }
        }

        return result;
    }

    public static Object replaceNull(Object obj, Object useIfNull, int scale) {
        Object value = obj;
        if (obj == null) {
            value = useIfNull;
        }

        if (scale >= 0) {
            if (value instanceof BigDecimal) {
                return ((BigDecimal) value).setScale(scale, RoundingMode.HALF_UP);
            } else {
                return BigDecimal.valueOf((Double) value).setScale(scale, RoundingMode.HALF_UP);
            }
        }

        return value;
    }

    public static Object toDateString(DateTime dateTime) {
        return ApiFormatter.asShortDate(dateTime);
    }

    public static Object toMediumDateString(DateTime dateTime) {
        return ApiFormatter.asMediumDate(dateTime.toDate());
    }

    public static String toSimpleDateString(DateTime dateTime) {
        return ApiFormatter.asSimpleDateFormat(dateTime.toDate());
    }

    public static String toCsvDateString(DateTime dateTime) {
        return ApiFormatter.asNormalDateFormat(dateTime.toDate());
    }

    public static String toDateTimeString(DateTime dateTime) {
        return ApiFormatter.asShortDateTime(dateTime);
    }

    public static Object toCurrencyString(BigDecimal amount) {
        return ApiFormatter.asDecimal(amount);
    }

    public static Object toCurrencyStringWithDollarSign(BigDecimal amount) {
        return Format.asCurrency(amount);
    }

    public static Object toListedSecurityEstimatedPrice(BigDecimal price) {
        return ReportFormatter.format(ReportFormat.LS_PRICE, true, price);
    }

    public static Object toManagedFundString(BigDecimal amount) {
        return ApiFormatter.asManagedFundDecimal(amount);
    }

    public static Object toIntegerString(BigDecimal amount) {
        return ApiFormatter.asIntegerString(amount);
    }

    public static String toRate(BigDecimal value, boolean includeSymbol) {
        return toRate(value, 2, includeSymbol);
    }

    public static String toRate(BigDecimal value, int scale) {
        if (value == null) {
            return "-";
        }
        return toRate(value, scale, true);
    }

    public static String toRate(BigDecimal value) {
        return toRate(value, 2, true);
    }

    public static String toRate(BigDecimal value, int scale, boolean includeSymbol) {
        String strRate;
        strRate = value.multiply(new BigDecimal(100)).setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
        if (includeSymbol) {
            strRate += '%';
        }
        return strRate;
    }

    public static Object toCompactCurrencyString(BigDecimal amount, boolean inThousands, boolean asInteger) {
        if (inThousands) {
            BigDecimal kAmount = amount.divide(new BigDecimal(1000d));
            if (amount.doubleValue() >= 1000000d) {
                return ApiFormatter.asIntegerString(kAmount);
            }
            return ApiFormatter.asDecimal(kAmount);
        }

        if (asInteger) {
            return ApiFormatter.asIntegerString(amount);
        }

        return ApiFormatter.asDecimal(amount);
    }

    public static BigDecimal inverseAmount(BigDecimal amount) {

        amount = (amount == null) ? BigDecimal.ZERO : amount.negate();
        return amount;
    }

    public static Object toUnitString(BigInteger units) {
        return ApiFormatter.asIntegerString(units);
    }

    /**
     * Rounds the input BigDecimal object using RoundingMode.DOWN option
     * 
     * @param obj
     *            input object
     * @param useIfNull
     *            object to be used if first input parameter is null
     * @param scale
     *            input scale
     * @return formatted object
     */
    public static Object replaceNullRoundDown(Object obj, Object useIfNull, int scale) {
        Object value = obj;
        if (obj == null) {
            value = useIfNull;
        }

        if (scale >= 0) {
            if (value instanceof BigDecimal) {
                return ((BigDecimal) value).setScale(scale, RoundingMode.DOWN);
            } else {
                return BigDecimal.valueOf((Double) value).setScale(scale, RoundingMode.DOWN);
            }
        }

        return value;
    }
}
