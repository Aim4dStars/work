package com.bt.nextgen.web.conversion;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

public class PRESPaymentInstructionTypeToTransactionTest
{
	@Test
	public void testConvert() throws Exception
	{

	}

	public static void main(String[] args)throws Exception
	{
		String date ="10 Apr 2013";
		 SimpleDateFormat shortDateFormat = new SimpleDateFormat("dd MMM yyyy");

		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTime(shortDateFormat.parse(date));
		XMLGregorianCalendar xmlGrogerianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
		System.out.println(xmlGrogerianCalendar.toGregorianCalendar().getTime());

	}
}
