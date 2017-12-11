package com.bt.nextgen.core.reporting;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class ReportUtilTest {

    @Test
    public void testJoin() {
        String joined = ReportUtils.join("|", "A", "B", "C");
        Assert.assertEquals("A|B|C", joined);

        joined = ReportUtils.join("|");
        Assert.assertEquals("", joined);

        joined = ReportUtils.join("|", "A");
        Assert.assertEquals("A", joined);
    }

    @Test
    public void testReplaceNull() {
        Object result = ReportUtils.replaceNull(null, "val", -1);
        Assert.assertEquals("val", result);

        result = ReportUtils.replaceNull("not val", "val", -1);
        Assert.assertEquals("not val", result);

        result = ReportUtils.replaceNull(null, BigDecimal.ONE, -1);
        Assert.assertEquals(BigDecimal.ONE, result);

        result = ReportUtils.replaceNull(null, BigDecimal.ONE, 2);
        Assert.assertEquals(BigDecimal.ONE.setScale(2), result);
    }

    @Test
    public void testToDateString() {
        DateTime dt = new DateTime("2015-10-21");
        Assert.assertEquals("21 Oct 2015", ReportUtils.toDateString(dt));
    }

    @Test
    public void testToMediumDateString() {
        DateTime dt = new DateTime("2015-10-21");
        Assert.assertEquals("21 October 2015", ReportUtils.toMediumDateString(dt));

    }

    @Test
    public void testToSimpleDateString() {
        DateTime dt = new DateTime("2015-10-21");
        Assert.assertEquals("21/10/2015", ReportUtils.toSimpleDateString(dt));

    }

    @Test
    public void testToCsvDateString() {
        DateTime dt = new DateTime("2015-10-21");
        Assert.assertEquals("21-Oct-15", ReportUtils.toCsvDateString(dt));

    }

    @Test
    public void testToCurrencyString() {
        Assert.assertEquals("1.00", ReportUtils.toCurrencyString(BigDecimal.ONE));
    }

    @Test    
    public void toCurrencyStringWithDollarSign() {
        Assert.assertEquals("$1.00", ReportUtils.toCurrencyStringWithDollarSign(BigDecimal.ONE));
    }

    @Test
    public void testToManagedFundString() {
        Assert.assertEquals("1.0000", ReportUtils.toManagedFundString(BigDecimal.ONE));
    }

    @Test
    public void testToIntegerString() {
        Assert.assertEquals("1", ReportUtils.toIntegerString(BigDecimal.ONE));
    }

    @Test
    public void testToRate() {
        Assert.assertEquals("100.00%", ReportUtils.toRate(BigDecimal.ONE, true));
        Assert.assertEquals("100.00", ReportUtils.toRate(BigDecimal.ONE, false));
        Assert.assertEquals("100.0%", ReportUtils.toRate(BigDecimal.ONE, 1));
        Assert.assertEquals("100%", ReportUtils.toRate(BigDecimal.ONE, 0));
        Assert.assertEquals("-", ReportUtils.toRate(null, 1));
    }
    
    @Test
    public void testToCompactCurrencyString() {
        Assert.assertEquals("1,000", ReportUtils.toCompactCurrencyString(BigDecimal.valueOf(1000000), true, true));
        Assert.assertEquals("1,000", ReportUtils.toCompactCurrencyString(BigDecimal.valueOf(1000000), true, false));
        Assert.assertEquals("1,000,000", ReportUtils.toCompactCurrencyString(BigDecimal.valueOf(1000000), false, true));
        Assert.assertEquals("1,000,000.00", ReportUtils.toCompactCurrencyString(BigDecimal.valueOf(1000000), false, false));
    }

    @Test
    public void testInverseAmount() {
        Assert.assertEquals(BigDecimal.valueOf(-1), ReportUtils.inverseAmount(BigDecimal.ONE));
        Assert.assertEquals(BigDecimal.ZERO, ReportUtils.inverseAmount(null));
    }


}
