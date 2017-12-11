package com.bt.nextgen.core;

import java.math.BigDecimal;

public class TestUtil
{
	public static BigDecimal toBigDecimal(String money)
	{
		return new BigDecimal(money.replaceAll("[\\$\\,\\+,\\-,\\,]", "").trim());
	}


	public static boolean isValidMoney(String money)
	{
		boolean isValid;
		isValid = isValidDecimal(money) && money.startsWith("$");
		return isValid;
	}

	public static boolean isValidDecimal(String decimal)
	{
		boolean isValid;
		isValid = decimal.charAt(decimal.length() - 3) == '.';
		BigDecimal bigDecimal = toBigDecimal(decimal);
		if (bigDecimal.compareTo(new BigDecimal(1000)) >= 0)
		{
			isValid &= decimal.contains(",");
		}
		return isValid;
	}


	public static void main(String[] args)
	{
		System.out.println(isValidMoney("$3434,34343.90"));
	}

}
