package com.bt.nextgen.service.cmis.converter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.oasis_open.docs.ns.cmis.core._200908.CmisProperty;
import org.oasis_open.docs.ns.cmis.core._200908.CmisPropertyString;

import java.util.List;

/**
 * Created by L075208 on 20/07/2015.
 */
public class CmisPropertyEmptyStringConverter implements Converter<String, CmisProperty> {

    @Override
    public String convert(CmisProperty property) {
        String returnValue = null;
        if (property instanceof CmisPropertyString){
            List<String> list = ((CmisPropertyString)property).getValue();
            if(!CollectionUtils.isEmpty(list)) {
                returnValue = ((CmisPropertyString)property).getValue().get(0);
            }
        }
        return returnValue;
    }

    @Override
    public CmisProperty convertTo(String s) {
        CmisPropertyString property = new CmisPropertyString();
        List<String> values = property.getValue();
        values.add(s);
        return property;
    }
}
