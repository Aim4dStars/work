package com.bt.nextgen.service.integration;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by L070589 on 18/08/2015.
 */
public final class IntegrationServiceUtil {

    private IntegrationServiceUtil()
    {

    }

    private static final Logger logger = LoggerFactory.getLogger(IntegrationServiceUtil.class);

    public static Date toDate(XMLGregorianCalendar xmlGregorianCalendar)
    {
        if (xmlGregorianCalendar == null)
        {
            return null;
        }

        try {
            return xmlGregorianCalendar.toGregorianCalendar(TimeZone.getTimeZone("Australia/Sydney"),
                    Locale.UK,
                    DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar())).getTime();
        } catch (DatatypeConfigurationException e) {
            logger.error("Error converting toDate {}",e);
        }
        return null;

    }

    public static Date toDate(JAXBElement<XMLGregorianCalendar> jaxbElement)
    {
        if (jaxbElement == null)
        {
            return null;
        }
        try
        {
            return jaxbElement.getValue()
                    .toGregorianCalendar(TimeZone.getTimeZone("Australia/Sydney"),
                            Locale.UK,
                            DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()))
                    .getTime();
        }
        catch (DatatypeConfigurationException e)
        {
            logger.error("Error converting toDate {}",e);
            return null;
        }
    }

    public static DateTime convertToDateTime(JAXBElement <XMLGregorianCalendar> jaxbElement)
    {
        if (jaxbElement == null)
        {
            return null;
        }
        else
        {
            return new DateTime(jaxbElement.getValue().toGregorianCalendar().getTime());
        }
    }

    public static String deformatBsb(String bsb)
    {
        String newBsb=bsb;
        if (StringUtils.isNotBlank(bsb) && bsb.contains("-"))
        {
            newBsb = bsb.replaceAll("-", "");
        }
        return newBsb;
    }



}
