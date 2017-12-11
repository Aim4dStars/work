package com.bt.nextgen.service.cmis.converter;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.oasis_open.docs.ns.cmis.core._200908.CmisProperty;
import org.oasis_open.docs.ns.cmis.core._200908.CmisPropertyDateTime;
import org.oasis_open.docs.ns.cmis.core._200908.CmisPropertyId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

/**
 * Created by L062329 on 21/07/2015.
 */
public class CmisPropertyDateTimeConverter implements Converter<DateTime, CmisProperty> {
    private static final Logger logger = LoggerFactory.getLogger(CmisPropertyDateTimeConverter.class);

    @Override
    public DateTime convert(CmisProperty cmisProperty) {
        DateTime returnValue = null;
        if (cmisProperty instanceof CmisPropertyDateTime){
            List<XMLGregorianCalendar> list = ((CmisPropertyDateTime)cmisProperty).getValue();
            if(!CollectionUtils.isEmpty(list)) {
                returnValue = new DateTime(((CmisPropertyDateTime)cmisProperty).getValue().get(0).toGregorianCalendar().getTime());
            }
        }
        return returnValue;
    }

    @Override
    public CmisProperty convertTo(DateTime dateTime) {
        CmisPropertyDateTime property = new CmisPropertyDateTime();
        List<XMLGregorianCalendar> values = property.getValue();
        try {
            XMLGregorianCalendar dealCloseDate = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(dateTime.toGregorianCalendar());
            values.add(dealCloseDate);
        } catch (DatatypeConfigurationException e) {
            logger.error("Unable to convert "+dateTime.toString()+"in XMLGregorianCalendar with " + e);
        }
        return property;
    }
}
