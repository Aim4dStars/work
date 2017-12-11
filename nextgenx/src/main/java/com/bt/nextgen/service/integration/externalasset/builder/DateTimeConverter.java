package com.bt.nextgen.service.integration.externalasset.builder;

import com.bt.nextgen.service.avaloq.PositionIdentifierImpl;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.core.convert.converter.Converter;
import com.bt.nextgen.service.integration.PositionIdentifier;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

@Component
public class DateTimeConverter implements Converter<String, DateTime>
{
    public DateTimeConverter()
    {
    }

    public DateTime convert(String s)
    {
        return DateTime.parse(s, DateTimeFormat.forPattern("yyyy-MM-dd"));
    }
}
