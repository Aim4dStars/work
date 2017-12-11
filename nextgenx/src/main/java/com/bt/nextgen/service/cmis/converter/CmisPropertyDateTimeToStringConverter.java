package com.bt.nextgen.service.cmis.converter;

import com.bt.nextgen.core.type.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.oasis_open.docs.ns.cmis.core._200908.CmisProperty;
import org.oasis_open.docs.ns.cmis.core._200908.CmisPropertyDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

/**
 * Created by L062329 on 21/07/2015.
 */
public class CmisPropertyDateTimeToStringConverter implements Converter<String, CmisProperty> {
    private static final Logger logger = LoggerFactory.getLogger(CmisPropertyDateTimeToStringConverter.class);

    @Override
    public String convert(CmisProperty cmisProperty) {
        String returnValue = null;
        if (cmisProperty instanceof CmisPropertyDateTime){
            List<XMLGregorianCalendar> list = ((CmisPropertyDateTime)cmisProperty).getValue();
            if(!CollectionUtils.isEmpty(list)) {
                returnValue = new DateTime(((CmisPropertyDateTime)cmisProperty).getValue().get(0).toGregorianCalendar().getTime()).toString();
            }
        }
        return returnValue;
    }

    @Override
    public CmisProperty convertTo(String dateTime) {
        CmisPropertyDateTime property = new CmisPropertyDateTime();
        List<XMLGregorianCalendar> values = property.getValue();
        if(!StringUtils.isEmpty(dateTime)){
            try {
                XMLGregorianCalendar dealCloseDate = DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar(DateUtil.convertToDateTime(dateTime, "yyyy-MM-dd'T'HH:mm:ss.SSSZ").toGregorianCalendar());
                values.add(dealCloseDate);
            } catch (DatatypeConfigurationException e) {
                logger.error("Unable to convert "+dateTime.toString()+"in XMLGregorianCalendar with " + e);
            }
        }
        return property;
    }
}
