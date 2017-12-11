package com.bt.nextgen.service.integration.externalassets.builder;


import com.bt.nextgen.service.integration.externalasset.builder.DateTimeConverter;
import com.bt.nextgen.service.integration.externalasset.builder.IsoDateTimeConverter;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class IsoDateTimeConverterTest
{
	@Test
	public void convertValidXmlDateStringToYYYYMMDD()
	{
		DateTimeConverter dateTimeConverter = new DateTimeConverter();
		IsoDateTimeConverter converter = new IsoDateTimeConverter();
		DateTime dt = converter.convert("1964-12-11T00:00:00+10:00");
		assertNotNull(dt);
	}
}