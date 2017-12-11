package com.bt.nextgen.service.avaloq.transaction;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.movemoney.PensionOrderType;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PensionOrderTypeConverter implements Converter<String, String> {
    @Autowired
    private StaticIntegrationService staticCodes;

    public String convert(String source) {
        final ServiceErrors serviceErrors = new ServiceErrorsImpl();
        final Code code = staticCodes.loadCode(CodeCategory.ORDER_TYPE, source, serviceErrors);
        final PensionOrderType pensionOrderType = PensionOrderType.forId(code.getIntlId());

        return pensionOrderType == null ? null : pensionOrderType.getLabel();
    }
}
