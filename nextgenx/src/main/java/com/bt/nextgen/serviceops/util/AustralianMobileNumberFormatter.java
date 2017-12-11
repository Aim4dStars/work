package com.bt.nextgen.serviceops.util;

import org.apache.commons.lang.StringUtils;

public class AustralianMobileNumberFormatter implements MobileNumberFormatter 
{
	public final static String COUNTRY_CODE_PREFIX = "61";
	
	@Override
	public String formatMobileNumber(String inMobileNo)	throws IllegalArgumentException 
	{			
		if (StringUtils.isEmpty(inMobileNo))
		{
			throw new IllegalArgumentException("Invalid mobile number provided");
		}
		
		
		String formattedMobileNo = inMobileNo.replace(" ", "");
		
		if (inMobileNo.startsWith("+") && formattedMobileNo.length() == 12)
		{
			formattedMobileNo = formattedMobileNo.subSequence(1, formattedMobileNo.length()).toString();					
		}		
		else if (formattedMobileNo.startsWith("0") && formattedMobileNo.length() == 10)
		{
			formattedMobileNo = COUNTRY_CODE_PREFIX + formattedMobileNo.subSequence(1, formattedMobileNo.length()).toString();
		}
		else if (formattedMobileNo.startsWith("61") && formattedMobileNo.length() == 11)
		{
			
		}
		else if (formattedMobileNo.matches("^[0-9]+$") == false)
		{
			throw new IllegalArgumentException("The mobile number you are trying to update to is not in a valid format. Please update and try again.");
		}
		else
		{
			throw new IllegalArgumentException("The mobile number you are trying to update to is not in a valid format. Please update and try again.");
		}
		
		return formattedMobileNo;
	}

}
