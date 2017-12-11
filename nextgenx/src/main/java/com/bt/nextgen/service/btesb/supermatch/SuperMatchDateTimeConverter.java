package com.bt.nextgen.service.btesb.supermatch;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts the date from ECO to DateTime {@link DateTime}
 */
@Component
public class SuperMatchDateTimeConverter implements Converter<String, DateTime> {

    @Override
    public DateTime convert(String dateString) {
        final DateTimeParser[] parsers = {
                DateTimeFormat.forPattern("yyyy-MM-dd").getParser(),
                DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ").getParser(),
                DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").getParser(),
        };
        final DateTimeFormatter formatter = new DateTimeFormatterBuilder().append(null, parsers).toFormatter();
        return DateTime.parse(dateString, formatter);
    }
}
