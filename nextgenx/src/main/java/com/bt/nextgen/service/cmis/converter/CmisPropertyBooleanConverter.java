package com.bt.nextgen.service.cmis.converter;

import com.bt.nextgen.service.cmis.converter.Converter;
import org.apache.commons.collections.CollectionUtils;
import org.oasis_open.docs.ns.cmis.core._200908.CmisProperty;
import org.oasis_open.docs.ns.cmis.core._200908.CmisPropertyBoolean;

import java.util.List;

/**
 * Created by L075208 on 20/07/2015.
 */
public class CmisPropertyBooleanConverter implements Converter<Boolean, CmisProperty> {

    @Override
    public Boolean convert(CmisProperty cmisProperty) {
        Boolean returnValue = null;
        if (cmisProperty instanceof CmisPropertyBoolean){
            List<Boolean> list = ((CmisPropertyBoolean)cmisProperty).getValue();
            if(!CollectionUtils.isEmpty(list)) {
                returnValue = ((CmisPropertyBoolean)cmisProperty).getValue()
                        .get(0);
            }
        }
        return returnValue;
    }

    @Override
    public CmisProperty convertTo(Boolean aBoolean) {
        CmisPropertyBoolean property = new CmisPropertyBoolean();
        List<Boolean> values = property.getValue();
        values.add(aBoolean);
        return property;
    }
}
