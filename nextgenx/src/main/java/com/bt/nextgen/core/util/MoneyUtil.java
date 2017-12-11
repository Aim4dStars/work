package com.bt.nextgen.core.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

import static com.bt.nextgen.core.web.ApiFormatter.asDecimal;

public class MoneyUtil
{
	public static String getCentPart(BigDecimal value)
	{
		return getCentPart(asDecimal(value));
	}

	/**
	 * Give me the 'numbers' after the decimal point.
	 *
	 * @param str to parse
	 * @return "00" by default, the numbers otherwise
	 */
	public static String getCentPart(String str)
	{
		if (StringUtils.isBlank(str))
		{
			return "00";
		}
		else
		{
			String[] parts = str.trim().split("\\.");
			if (parts.length == 1)
			{
				return "00";
			}
			else
			{
				return parts[1];
			}
		}
	}

	public static String getDollarPart(BigDecimal value)
	{
		return value == null ? null : getDollarPart(asDecimal(value));
	}

	/**
	 * Give me the numbers before the decimal point.
	 *
	 * @param str to parse
	 * @return any number before the '.' else the string that was passed.
	 */
	public static String getDollarPart(String str)
	{
		String dollarPart = str;
		if (!StringUtils.isBlank(str))
		{
			String[] parts = str.trim().split("\\.");
			return parts[0];
		}
		return dollarPart;
	}

	/**
	 * Short had for creating a BigDecimal from a string.
	 *
	 * @param value the string
	 * @return a BigDecimal created from the string.
	 */
	public static BigDecimal bd(String value)
	{
		return new BigDecimal(value);
	}

	/**
	 *
	 * @param totalTermDepositBalance
	 * @param rawBalance
	 * @return
	 */
	public static BigDecimal add(BigDecimal totalTermDepositBalance, BigDecimal rawBalance)
	{
		return (totalTermDepositBalance == null ? new BigDecimal(0.00) : totalTermDepositBalance)
				.add(rawBalance == null ? new BigDecimal(0.00) : rawBalance);
	}
}
