package com.bt.nextgen.service.cmis.converter;

import org.apache.commons.collections.CollectionUtils;
import org.oasis_open.docs.ns.cmis.core._200908.CmisProperty;
import org.oasis_open.docs.ns.cmis.core._200908.CmisPropertyInteger;

import java.math.BigInteger;
import java.util.List;

class CmisPropertyIntegerConverter implements Converter<BigInteger, CmisProperty> {

    @Override
    public BigInteger convert(CmisProperty cmisProperty) {
        BigInteger returnValue = BigInteger.ZERO;
        if (cmisProperty instanceof CmisPropertyInteger) {
            final List<BigInteger> list = ((CmisPropertyInteger) cmisProperty).getValue();
            if (!CollectionUtils.isEmpty(list)) {
                returnValue = ((CmisPropertyInteger) cmisProperty).getValue().get(0);
            }
        }
        return returnValue;
    }

    @Override
    public CmisProperty convertTo(BigInteger integer) {
        final CmisPropertyInteger property = new CmisPropertyInteger();
        property.getValue().add(integer);
        return property;
    }
}
