package com.bt.nextgen.service.integration.externalasset.builder;

import org.joda.time.format.ISODateTimeFormat;
import org.springframework.core.convert.converter.Converter;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

/**
 * Converts an xml datetime format into joda {@link org.joda.time.DateTime} format.<p>
 * e.g. Supports conversion of 1964-12-11T00:00:00+10:00
 */
@Component
public class IsoDateTimeConverter implements Converter<String, DateTime>
{
	public IsoDateTimeConverter()
	{
	}

	public DateTime convert(String s)
	{
		return ISODateTimeFormat.dateTimeNoMillis().parseLocalDateTime(s).toDateTime();
	}
}