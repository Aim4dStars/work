package com.bt.nextgen.service.avaloq.transaction;

import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.movemoney.IndexationType;
import com.bt.nextgen.service.integration.movemoney.PensionPaymentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PensionPaymentOrIndexationTypeConverter implements Converter<String, Enum<?>> {

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    @Override
    public Enum<?> convert(String source) {
        String intlId = staticIntegrationService
                .loadCode(CodeCategory.PENSION_PAYMENT_OR_INDEXATION_TYPE, source, new ServiceErrorsImpl()).getIntlId();
        return PensionPaymentType.fromIntlId(intlId) == null ? IndexationType.fromIntlId(intlId)
                : PensionPaymentType.fromIntlId(intlId);
    }

}
