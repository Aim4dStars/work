package com.bt.nextgen.service.integration.overview.service;


import com.bt.nextgen.service.avaloq.UserCacheService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationType;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class AccountOverviewCacheManagementIntegrationServiceImpl implements AccountOverviewCacheManagementIntegrationService {
    private final Logger logger = LoggerFactory.getLogger(AccountOverviewCacheManagementIntegrationServiceImpl.class);

    @Autowired
    public UserCacheService userCacheService;


    /**
     * {@inheritDoc}
     * <p>
     * Note that {@code com.bt.nextgen.service.avaloq.account.WrapAccountDetail} cache holds the underlying account details cache,
     * so it also needs to be cleared out so that account overview uses the latest account details.
     * </p>
     */
    @Override
    @CacheEvict(key = "{#accountKey, #root.target.getActiveProfileCacheKey()}",
            value = {"com.bt.nextgen.service.avaloq.account.CacheAvaloqAccountIntegrationServiceImpl.availablecash",
                    "com.bt.nextgen.service.integration.cashcategorisation.CashCategorisationSummary",
                    "com.bt.nextgen.service.avaloq.account.WrapAccountDetail",
                    "com.bt.nextgen.service.avaloq.account.WrapAccountValuation",
                    "com.bt.nextgen.service.avaloq.transaction.ScheduledTransactions",
                    "com.bt.nextgen.service.avaloq.transaction.RecentCashTransactions",
                    "com.bt.nextgen.service.avaloq.regularinvestment.RegularInvestmentPlans",
                    "com.bt.nextgen.service.avaloq.account.CacheAccountOverviewAvaloqAccountIntegrationServiceImpl.account"})
    public void clearCache(AccountKey accountKey) {
        logger.info("Clearing account overview cache");
    }

    @CacheEvict(key = "{#accountKey, #root.target.getActiveProfileCacheKey()}",
            value = {"com.bt.nextgen.service.avaloq.beneficiary.BeneficiaryDetails",
                    "com.bt.nextgen.service.avaloq.contributionhistory.ContributionHistory"})
    @Override
    public void clearCache(com.bt.nextgen.api.account.v3.model.AccountKey accountKey) {
        logger.info("Clearing Beneficiaries And Contribution caches");
    }

    @CacheEvict(key = "{#accountKey, #root.target.getActiveProfileCacheKey(), #category}",
            value = {"com.bt.nextgen.service.integration.cashcategorisation.CashContributions"})
    @Override
    public void clearCategorisationCache(AccountKey accountKey, CashCategorisationType category) {
        logger.info("Clearing categorisation caches overview cache");
    }

    @CacheEvict(key = "{#identifier, #root.target.getActiveProfileCacheKey()}",
            value = {"com.bt.nextgen.service.avaloq.transaction.ScheduledTransactions",
                    "com.bt.nextgen.service.avaloq.transaction.RecentCashTransactions"})
    @Override
    public void clearCashTransactionCache(WrapAccountIdentifier identifier) {
        logger.info("Clearing cash transaction caches");
    }

    public String getActiveProfileCacheKey() {
        return userCacheService.getActiveProfileCacheKey();
    }
}
