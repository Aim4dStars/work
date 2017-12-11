package com.btfin.panorama.core.conversion;

import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

@Component
// Moved static code converter back to nextgen. Currently we will be having duplicates in broker and asset integration service.
public class StaticCodeConverter implements CodeConverter {
    private static final Logger LOGGER = getLogger(StaticCodeConverter.class);

    //TODO - XXX - This is to enforce that caching happens before autowiring in this instance. Improvement Required
    @Autowired
    private BeanFactoryCacheOperationSourceAdvisor waitForCachingAspect;

    @Autowired
    private StaticIntegrationService staticCodes;

    @Override
    public String convert(String staticCode, String codeCategory)
    {
        LOGGER.debug("Converting code [{}] using category [{}]", staticCode, codeCategory);
        try
        {
            final Code code = staticCodes.loadCode(CodeCategory.valueOf(codeCategory), staticCode, new ServiceErrorsImpl());
            if (code != null) {
                LOGGER.debug("Converted Static Code from [{}] to [{}]", staticCode, code.getIntlId());
                return code.getIntlId();
            }
        }
        catch (Exception e)
        {
            LOGGER.warn("Error in converting {} # {} ", codeCategory, staticCode, e);
        }
        LOGGER.info("Returning default static code {} # {} without conversion", codeCategory, staticCode);
        return staticCode;
    }
}
