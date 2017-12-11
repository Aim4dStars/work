package com.bt.nextgen.domain;

import static com.bt.nextgen.core.domain.Money.greaterThanZero;
import static com.bt.nextgen.core.util.MoneyUtil.bd;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.bt.nextgen.core.domain.Money;
import com.bt.nextgen.core.domain.Percentage;

public class MoneyTest
{
	@Test
	public void testGreaterThanZero() throws Exception
	{
		assertThat(new Money("234.56"), greaterThanZero());
		assertThat(new Money("-234.56"), not(greaterThanZero()));
	}

	@Test
	public void testGetAmount() throws Exception
	{
		assertThat(new Money("1234.56").getAmount().compareTo(bd("1234.56")), is(0));
		assertThat(new Money(bd("3234.56")).getAmount().compareTo(bd("3234.56")), is(0));
	}

    @Test
    public void testGetAmountFormatted() throws Exception
    {
        assertThat(new Money("1,234.56").getAmount().compareTo(bd("1234.56")), is(0));
        assertThat(new Money("3,234.56").getAmount().compareTo(bd("3234.56")), is(0));
    }

    @Test
    public void testGetMillionsFormatted() throws Exception
    {
        assertThat(new Money("1,234,567.89").getAmount().compareTo(bd("1234567.89")), is(0));
        assertThat(new Money("13,234,567.89").getAmount().compareTo(bd("13234567.89")), is(0));
    }


    @Test
	public void testNegate() throws Exception
	{
		assertThat(new Money("4234.56").negate(), is(new Money("-4234.56")));
		assertThat(new Money("-5234.56").negate(), is(new Money("5234.56")));
	}

	@Test
	public void testAdd() throws Exception
	{
		assertThat(new Money("6234.56").add(new Money("77.89")), is(new Money("6312.45")));
	}

	@Test
	public void testSubtract() throws Exception
	{
		assertThat(new Money("7234.56").subtract(new Money("67.89")), is(new Money("7166.67")));
	}

	@Test
	public void testMultiply() throws Exception
	{
		assertThat(new Money("8234.56").multiply(bd("6.5")), is(new Money("53524.64")));
	}

	@Test
	public void testDivide() throws Exception
	{
		assertThat(new Money("9234.56").divide(bd("5.5")), is(new Money("1679.01")));
	}

	@Test
	public void testPercentOf() throws Exception
	{
		assertThat(new Money("50").percentOf(new Money("100")), is(new Percentage("50.00")));
	}

	@Test
	public void testAmountOf() throws Exception
	{
		assertThat(new Money("236.56").amountOf(new Percentage("23.77")), is(new Money("56.23")));
	}

	@Test
	public void testIsZero() throws Exception
	{
		assertThat(new Money("0").isZero(), is(true));
	}

	@Test
	public void testIsPositive() throws Exception
	{
		assertThat(new Money("0.55").isPositive(), is(true));
	}

	@Test
	public void testToWholeNumber() throws Exception
	{
		assertThat(new Money("232.56").toWholeNumber(), is("233"));
	}

	@Test
	public void testToDecimalNumber() throws Exception
	{
		assertThat(new Money("634.56").toDecimalNumber(1), is("634.6"));
	}

	@Test
	public void testGetAmountToTwoDecimal() throws Exception
	{
		assertThat(new Money("734.56").getAmountToTwoDecimal(), is("734.56"));
	}

	@Test
	public void testGetAmountWholeNo() throws Exception
	{
		assertThat(new Money("134.56").getAmountWholeNo(), is("135"));
	}

	@Test
	public void testEqualsAndHashCode() throws Exception
	{
		assertThat(new Money("214.56"), is(new Money("214.56")));
		assertThat(new Money("224.56").hashCode(), is(new Money("224.56").hashCode()));
		assertThat(new Money("244.55"), is(not(new Money("244.56"))));

		assertFalse(new Money("254.55").equals("bob"));
		assertFalse(new Money("264.55").equals(null));

		Money money = new Money("55.66");
		assertTrue(money.equals(money));
	}
}
