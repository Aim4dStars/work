package com.bt.nextgen.service.avaloq.transaction;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by L067218 on 27/01/2017.
 */
@Component
public class OrderStatusConverter implements Converter<String, String> {

    @Autowired
    private StaticIntegrationService staticCodes;

    private static final Logger logger = LoggerFactory.getLogger(OrderStatusConverter.class);

    public String convert(String source)
    {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Code code = staticCodes.loadCode(CodeCategory.ORDER_STATUS, source,
                serviceErrors);
        if(code!=null){
            return code.getName();
        }
        return null;

    }
}
