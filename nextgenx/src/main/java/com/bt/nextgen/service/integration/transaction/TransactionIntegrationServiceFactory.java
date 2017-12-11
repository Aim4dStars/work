package com.bt.nextgen.service.integration.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Created by L067218 on 2/03/2016.
 */
@Service
public class TransactionIntegrationServiceFactory {

    @Autowired
    @Qualifier("CacheTransactionIntegrationServiceImpl")
    private TransactionIntegrationService cacheTransactionIntegrationServiceImpl;

    @Autowired
    @Qualifier("AvaloqTransactionIntegrationServiceImpl")
    private TransactionIntegrationService avaloqTransactionIntegrationServiceImpl;


    public TransactionIntegrationService getInstance(String type) {
        if (!StringUtils.isEmpty(type) && "cache".equalsIgnoreCase(type)) {
            return cacheTransactionIntegrationServiceImpl;
        }
        else {
            return avaloqTransactionIntegrationServiceImpl;
        }
    }
}