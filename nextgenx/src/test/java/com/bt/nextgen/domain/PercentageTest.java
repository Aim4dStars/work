package com.bt.nextgen.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

import com.bt.nextgen.core.domain.Percentage;

public class PercentageTest
{
	@Test
	public void testStringConversion() throws Exception
	{
		assertThat(new Percentage("55").getAmount().doubleValue(), is(55.0));
		assertThat(new Percentage("0.5999").getAmount().doubleValue(), is(0.5999));
		assertThat(new Percentage("-5").getAmount().doubleValue(), is(-5d));
	}

	@Test
	public void testBigDecimalConversion() throws Exception
	{
		assertThat(new Percentage(new BigDecimal("55")).getAmount().doubleValue(), is(55.0));
		assertThat(new Percentage(new BigDecimal("0.5999")).getAmount().doubleValue(), is(0.5999));
		assertThat(new Percentage(new BigDecimal("-5")).getAmount().doubleValue(), is(-5d));
	}

	@Test
	public void testAdd()
	{
		assertThat(new Percentage("10").add(new Percentage("20")), is(new Percentage("30")));
		assertThat(new Percentage("10.55").add(new Percentage("20.46")), is(new Percentage("31.01")));
	}

	@Test
	public void testCompareTo()
	{
		assertThat(new Percentage("10.05").compareTo(new Percentage("10.05")), is(0));
		assertThat(new Percentage("10.051").compareTo(new Percentage("10.052")), is(0));
		assertThat(new Percentage("10.051").compareTo(new Percentage("10.056")), is(-1));
	}

	@Test
	public void testToDecimalNumber()
	{
		assertThat(new Percentage("10.556677").toDecimalNumber(3), is("10.557"));
		assertThat(new Percentage("10.556633").toDecimalNumber(4), is("10.5566"));
	}
}
