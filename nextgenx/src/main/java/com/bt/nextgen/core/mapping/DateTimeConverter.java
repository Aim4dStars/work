package com.bt.nextgen.core.mapping;

import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.dozer.CustomConverter;
import org.dozer.MappingException;
import org.joda.time.DateTime;

public class DateTimeConverter implements CustomConverter
{
	@SuppressWarnings("unchecked")
	@Override
	public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class <? > destinationClass,
		Class <? > sourceClass)
	{
		if (sourceFieldValue == null)
		{
			return null;
		}

		if (sourceFieldValue instanceof Date)
		{
			return new DateTime(sourceFieldValue);
		}
		else if (sourceFieldValue instanceof DateTime)
		{
			return ((DateTime)sourceFieldValue).toDate();
		}
		else if (sourceFieldValue instanceof XMLGregorianCalendar)
		{
			return new DateTime(((XMLGregorianCalendar)sourceFieldValue).toGregorianCalendar().getTime());
		}

		throw new MappingException("Misconfigured/unsupported mapping " + sourceFieldValue.getClass().getName());
	}

}