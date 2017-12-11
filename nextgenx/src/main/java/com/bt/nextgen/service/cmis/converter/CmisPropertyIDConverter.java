package com.bt.nextgen.service.cmis.converter;

import com.bt.nextgen.service.integration.financialdocument.DocumentKey;
import org.apache.commons.collections.CollectionUtils;
import org.oasis_open.docs.ns.cmis.core._200908.CmisProperty;
import org.oasis_open.docs.ns.cmis.core._200908.CmisPropertyId;

import java.util.List;

/**
 * Created by L062329 on 21/07/2015.
 */
public class CmisPropertyIDConverter implements Converter<DocumentKey, CmisProperty> {

    @Override
    public DocumentKey convert(CmisProperty cmisProperty) {
        DocumentKey returnValue = null;
        if (cmisProperty instanceof CmisPropertyId){
            List<String> list = ((CmisPropertyId)(cmisProperty)).getValue();
            if(!CollectionUtils.isEmpty(list)) {
                returnValue = DocumentKey.valueOf(((CmisPropertyId) (cmisProperty)).getValue().get(0));
            }
        }
        return returnValue;
    }

    @Override
    public CmisProperty convertTo(DocumentKey documentKey) {
        CmisPropertyId property = new CmisPropertyId();
        List<String> values = property.getValue();
        values.add(documentKey.getId());
        return property;
    }
}
