package com.bt.nextgen.core.web;

import com.bt.nextgen.core.domain.Money;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

//TODO Need to either divide the class into two or move to a more suitable package struct.
@SuppressWarnings({"findbugs:DLS_DEAD_LOCAL_STORE", "findbugs:STCAL_INVOKE_ON_STATIC_DATE_FORMAT_INSTANCE", "squid:S881",
        "checkstyle:com.puppycrawl.tools.checkstyle.checks.whitespace.NoLineWrapCheck"})
public final class Format implements Serializable {


    public final static String CURRENCY_FORMAT = "$#,##0.00";
    public final static String ZERO_CURRENCY_FORMAT = "$0.00";

    private Format() {

    }


    public static String asCurrency(String amount) {
        return asCurrency(new Money(amount));
    }

    public static String asCurrency(BigDecimal amount) {
        if (amount == null) {
            return null;
        }
        if (amount.compareTo(new BigDecimal(0.000000)) == 0) {
            return new DecimalFormat(ZERO_CURRENCY_FORMAT).format(amount);
        }
        return new DecimalFormat(CURRENCY_FORMAT).format(amount);
    }

    public static String asCurrency(Money amount) {
        if (amount == null) {
            return null;
        }
        return asCurrency(amount.getAmount());
    }


    public static String deformatCurrency(String amount) {
        if (StringUtils.isBlank(amount))
            return "0";
        return amount.replaceAll("[\\$\\,\\,]", "").trim();
    }


}
