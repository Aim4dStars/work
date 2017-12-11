package com.bt.nextgen.service.avaloq.regularinvestment;


import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.regularinvestment.RIPTransactionsIntegrationService;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestmentIntegrationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class RIPTransactionsIntegrationServiceFactory
{
    @Autowired
    @Qualifier("cachedAvaloqRIPTransactionsIntegrationService")
    private RIPTransactionsIntegrationService cachedRIPTransactionsIntegrationService;

    @Autowired
    @Qualifier("avaloqRIPTransactionsIntegrationService")
    private RIPTransactionsIntegrationService RIPTransactionsIntegrationService;


    public RIPTransactionsIntegrationService getInstance(String type)
    {
        if (StringUtils.isNotEmpty(type) && type.equalsIgnoreCase("cache"))
        {
            return cachedRIPTransactionsIntegrationService;
        }
        else
        {
            return RIPTransactionsIntegrationService;
        }
    }
}