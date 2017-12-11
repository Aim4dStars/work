package com.bt.nextgen.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Test;

public class MoneyUtilTest
{
	@Test
	public void testBasic()
	{
		assertEquals(MoneyUtil.bd("100"), new BigDecimal("100"));
	}

	@Test
	public void testGetCentPart() throws Exception
	{
		assertThat(MoneyUtil.getCentPart("0.000"), Is.is("000"));
		assertThat(MoneyUtil.getCentPart("0.00"), Is.is("00"));
		assertThat(MoneyUtil.getCentPart(" 0.00"), Is.is("00"));
		assertThat(MoneyUtil.getCentPart("0.00 "), Is.is("00"));
		assertThat(MoneyUtil.getCentPart("0"), Is.is("00"));
		assertThat(MoneyUtil.getCentPart("0.0"), Is.is("0"));
		assertThat(MoneyUtil.getCentPart((BigDecimal)null), Is.is("00"));
		assertThat(MoneyUtil.getCentPart((String)null), Is.is("00"));
		assertThat(MoneyUtil.getCentPart(""), Is.is("00"));
		assertThat(MoneyUtil.getCentPart(" "), Is.is("00"));

	}

	@Test
	public void testGetDollarPart() throws Exception
	{
		assertThat(MoneyUtil.getDollarPart("0.000"), Is.is("0"));
		assertThat(MoneyUtil.getDollarPart(".00"), Is.is(""));
		assertThat(MoneyUtil.getDollarPart(" 0.00"), Is.is("0"));
		assertThat(MoneyUtil.getDollarPart("0.00 "), Is.is("0"));
		assertThat(MoneyUtil.getDollarPart("0"), Is.is("0"));
		assertThat(MoneyUtil.getDollarPart("0.0"), Is.is("0"));
		assertThat(MoneyUtil.getDollarPart((BigDecimal)null), CoreMatchers.nullValue());
		assertThat(MoneyUtil.getDollarPart((String)null), CoreMatchers.nullValue());
		assertThat(MoneyUtil.getDollarPart(""), Is.is(""));
		assertThat(MoneyUtil.getDollarPart(" "), Is.is(" "));
	}
}
