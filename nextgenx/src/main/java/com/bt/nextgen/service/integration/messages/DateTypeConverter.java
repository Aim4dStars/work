package com.bt.nextgen.service.integration.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Converter defined for converting the source into Date
 */

@SuppressWarnings("checkstyle:com.puppycrawl.tools.checkstyle.checks.whitespace.NoLineWrapCheck")
@Component
public class DateTypeConverter implements Converter<String, Date> {

    private String datePattern = "yyyy-MM-dd";
    private static final Logger logger = LoggerFactory.getLogger(DateTypeConverter.class);

    public Date convert(String source) {
        try {
            Date date = new SimpleDateFormat(datePattern).parse(source);
            return date;
        } catch (ParseException e) {
            logger.error("Date could not be parsed for input:{}", source);
        }
        return null;
    }

}
