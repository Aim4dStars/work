package com.bt.nextgen.service.avaloq.modelportfolio;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class IpsInvestmentStyleConverter implements Converter<String, String> {

    @Autowired
    private StaticIntegrationService staticService;

    @Override
    public String convert(String source) {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        if (StringUtils.isNotBlank(source)) {
            Code code = staticService.loadCode(CodeCategory.IPS_INVESTMENT_STYLE, source, serviceErrors);
            if (code != null) {
                return code.getName();
            }
        }
        return "";
    }
}
