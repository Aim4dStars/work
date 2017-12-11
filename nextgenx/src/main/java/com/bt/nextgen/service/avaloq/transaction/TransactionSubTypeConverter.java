package com.bt.nextgen.service.avaloq.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

@Component
public class TransactionSubTypeConverter implements Converter<String, String> {

    @Autowired
    StaticIntegrationService staticCodes;

    private static final Logger logger = LoggerFactory.getLogger(TransactionSubTypeConverter.class);

    public String convert(String source) {
        Code code = null;
        try {
            ServiceErrors serviceErrors = new ServiceErrorsImpl();
            code = staticCodes.loadCode(CodeCategory.TRANSACTION_SUBTYPE, source, serviceErrors);
            return code.getName();
        } catch (Exception e) {
            logger.error("Error converting to String to contribution description", e);
        }
        return source;
    }
}
