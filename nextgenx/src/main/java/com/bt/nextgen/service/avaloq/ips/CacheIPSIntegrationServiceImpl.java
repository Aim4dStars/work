package com.bt.nextgen.service.avaloq.ips;

import ch.lambdaj.Lambda;
import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
class CacheIPSIntegrationServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(CacheIPSIntegrationServiceImpl.class);

    @Autowired
    private AvaloqReportService avaloqService;

    @Cacheable(key = "#root.target.getSingletonCacheKey()", value = "com.bt.nextgen.service.avaloq.InvestmentPolicyStatementInterface")
    public Map<IpsKey, InvestmentPolicyStatementInterface> loadInvestmentPolicyStatements(final ServiceErrors serviceErrors) {

        AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(IpsTemplate.IPS_LIST);
        IpsListHolder response = avaloqService.executeReportRequestToDomain(avaloqRequest, IpsListHolder.class, serviceErrors);

        return Lambda.index(response.getIpsList(), Lambda.on(InvestmentPolicyStatementInterface.class).getIpsKey());
    }

    @CacheEvict(key = "#root.target.getSingletonCacheKey()", value = "com.bt.nextgen.service.avaloq.InvestmentPolicyStatementInterface")
    public void clearCache(ServiceErrors serviceErrors) {
        logger.info("ips cache has been cleared");
    }

    /**
     * This method creates the fake cache key
     *
     * @return String
     */
    public String getSingletonCacheKey() {
        return "ips";
    }
}