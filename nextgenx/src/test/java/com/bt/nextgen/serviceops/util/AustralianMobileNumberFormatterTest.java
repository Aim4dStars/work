package com.bt.nextgen.serviceops.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AustralianMobileNumberFormatterTest 
{
	@Test
	public void testFormattingOfLocalMobileNumber()
	{
		AustralianMobileNumberFormatter formatter = new AustralianMobileNumberFormatter();
		String result = formatter.formatMobileNumber("0405678987");
		
		assertEquals(result, "61405678987");
	}
	
	@Test
	public void testFormattingOfLocalMobileNumberWithSpaces()
	{
		AustralianMobileNumberFormatter formatter = new AustralianMobileNumberFormatter();
		String result = formatter.formatMobileNumber("0412 474 575");
		
		assertEquals(result, "61412474575");
	}	
	
	@Test
	public void testFormattingOfLocalMobileNumberInInternationalFormat()
	{
		AustralianMobileNumberFormatter formatter = new AustralianMobileNumberFormatter();
		String result = formatter.formatMobileNumber("61405678978");
		
		assertEquals(result, "61405678978");
	}
	
	@Test
	public void testFormattingOfLocalMobileNumberInInternationalFormatPlusSign()
	{
		AustralianMobileNumberFormatter formatter = new AustralianMobileNumberFormatter();
		String result = formatter.formatMobileNumber("+61405678982");
		
		assertEquals(result, "61405678982");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFormattingOfEmptyMobileNumber()
	{
		AustralianMobileNumberFormatter formatter = new AustralianMobileNumberFormatter();
		String result = formatter.formatMobileNumber("");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFormattingOfMobileWithWrongLength()
	{
		AustralianMobileNumberFormatter formatter = new AustralianMobileNumberFormatter();
		String result = formatter.formatMobileNumber("04004564");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFormattingOfMobileWithInvalidChars()
	{
		AustralianMobileNumberFormatter formatter = new AustralianMobileNumberFormatter();
		String result = formatter.formatMobileNumber("040c04564");
	}
}
