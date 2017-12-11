package com.bt.nextgen.service.avaloq.rollover;

import com.btfin.panorama.core.validation.Validator;
import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.integration.rollover.SuperfundDetails;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CacheSuperfundIntegrationServiceImpl extends AbstractAvaloqIntegrationService {
    @Autowired
    private AvaloqReportService avaloqService;

    @Autowired
    private Validator validator;

    private static final Logger logger = LoggerFactory.getLogger(CacheSuperfundIntegrationServiceImpl.class);

    @Cacheable(key = "#root.target.getSingletonCacheKey()", value = "com.bt.nextgen.service.avaloq.rollover.SuperfundIntegrationService.availableSuperfunds")
    public List<SuperfundDetails> loadAvailableSuperfunds(final ServiceErrors serviceErrors) {

        AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(CashRolloverTemplate.CASH_ROLLOVER);

        SuperfundResponseImpl response = avaloqService.executeReportRequestToDomain(avaloqRequest, SuperfundResponseImpl.class,
                serviceErrors);
        validator.validate(response, serviceErrors);

        return response.getSuperfunds();
    }

    @CacheEvict(allEntries = true, value = "com.bt.nextgen.service.avaloq.rollover.SuperfundIntegrationService.availableSuperfunds")
    public void clearCache(ServiceErrors serviceErrors) {
        logger.info("Available superfunds cache has been cleared");
    }

    /**
     * This method creates the fake cache key
     * 
     * @return String
     */
    public String getSingletonCacheKey() {
        return "singleton";
    }
}
