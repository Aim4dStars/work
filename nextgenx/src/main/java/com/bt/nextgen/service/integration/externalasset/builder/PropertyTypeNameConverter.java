package com.bt.nextgen.service.integration.externalasset.builder;

import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Property type name converter for external asset type Direct property
 */
@Component
public class PropertyTypeNameConverter implements Converter<String, String> {

    @Autowired
    private StaticIntegrationService staticService;

    @Override
    public String convert(String propertyName) {
        Code code = staticService.loadCodeByName(CodeCategory.POS_PROPERTY_TYPE, propertyName, new ServiceErrorsImpl());
        return code.getIntlId();
    }
}
