package com.bt.nextgen.service.avaloq.transaction;

/**
 * Created by L067218 on 2/03/2016.
 */

import com.btfin.panorama.core.security.avaloq.AvaloqBankingAuthorityService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.UserCacheService;
import com.bt.nextgen.service.integration.transaction.Transaction;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * Cached implementation of CashCategoristionIntegrationService
 */
@Service("CacheTransactionIntegrationServiceImpl")
public class CacheTransactionIntegrationServiceImpl extends AvaloqTransactionIntegrationServiceImpl implements ApplicationContextAware
{
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException
    {
        this.context = applicationContext;
    }

    @Resource(name = "userDetailsService")
    public AvaloqBankingAuthorityService userProfileService;

    @Autowired
    public UserCacheService userCacheService;


    @Override
    @Cacheable(key = "{#identifier, #root.target.getActiveProfileCacheKey()}", value = "com.bt.nextgen.service.avaloq.transaction.ScheduledTransactions")
    public List<Transaction> loadScheduledTransactions(WrapAccountIdentifier identifier, ServiceErrors serviceErrors)
    {
        return super.loadScheduledTransactions(identifier, serviceErrors);
    }

    @Autowired
    private static ApplicationContext context;

    @Override
    @Cacheable(key = "{#identifier, #root.target.getActiveProfileCacheKey()}", value = "com.bt.nextgen.service.avaloq.transaction.RecentCashTransactions")
    public List<TransactionHistory> loadRecentCashTransactions(WrapAccountIdentifier identifier, ServiceErrors serviceErrors)
    {
        return super.loadRecentCashTransactions(identifier, serviceErrors);
    }

    public String getActiveProfileCacheKey()
    {
        return userCacheService.getActiveProfileCacheKey();
    }

}
