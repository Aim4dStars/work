package com.bt.nextgen.service.avaloq.beneficiary;

import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converter for nomination type code
 * Created by M035995 on 19/07/2016.
 */
@Component
public class NominationTypeConverter implements Converter<String, Code> {

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    /**
     * This method would get the object {@link Code} for nomination type.
     *
     * @param nominationType nomination type code
     * @return Object of {@link Code}
     */
    @Override
    public Code convert(String nominationType) {
        return staticIntegrationService.loadCode(CodeCategory.SUPER_NOMINATION_TYPE, nominationType,
                new ServiceErrorsImpl());
    }
}
