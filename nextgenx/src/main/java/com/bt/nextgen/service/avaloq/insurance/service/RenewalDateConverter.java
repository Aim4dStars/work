package com.bt.nextgen.service.avaloq.insurance.service;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Component
public class RenewalDateConverter implements Converter<String, DateTime> {

    private static final Logger logger = LoggerFactory.getLogger(RenewalDateConverter.class);

    @Override
    public DateTime convert(String renewalDate){
        if (!StringUtils.isNotEmpty(renewalDate)) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("--MM-dd");
        GregorianCalendar calendar = new GregorianCalendar();
        GregorianCalendar today = new GregorianCalendar();
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MILLISECOND, 0);
        try {
            Date formattedDate = simpleDateFormat.parse(renewalDate);
            calendar.setTime(formattedDate);
            calendar.set(Calendar.YEAR, new org.joda.time.DateTime().getYear());
            if (calendar.getTime().compareTo(today.getTime()) < 0) {
                calendar.set(Calendar.YEAR, new org.joda.time.DateTime().getYear() + 1);
            }
        } catch (ParseException e) {
            logger.error("Unable to convert " + renewalDate.toString() + "to Date with " + e);
        }
        return new DateTime(calendar.getTime());
    }
}