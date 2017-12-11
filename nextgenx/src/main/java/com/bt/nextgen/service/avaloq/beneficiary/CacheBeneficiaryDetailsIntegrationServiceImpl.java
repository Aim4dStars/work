package com.bt.nextgen.service.avaloq.beneficiary;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.btfin.panorama.core.security.avaloq.AvaloqBankingAuthorityService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.UserCacheService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * Created by L067218 on 11/08/2016.
 */
@Service("CacheBeneficiaryDetailsIntegrationServiceImpl")
public class CacheBeneficiaryDetailsIntegrationServiceImpl extends BeneficiaryDetailsIntegrationServiceImpl {

    @Resource(name = "userDetailsService")
    public AvaloqBankingAuthorityService userProfileService;

    @Autowired
    public UserCacheService userCacheService;

    /**
     * Retrieves the beneficiary details of all the input accountIds
     * Account Ids are sorted to be used as cache key
     *
     * @param accountKey
     * @param serviceErrors
     *
     * @return BeneficiaryDetailsResponseHolderImpl
     */
    @Cacheable(key = "{#accountKey, #root.target.getActiveProfileCacheKey()}",
            value = "com.bt.nextgen.service.avaloq.beneficiary.BeneficiaryDetails")
    public BeneficiaryDetailsResponseHolderImpl getBeneficiaryDetails(AccountKey accountKey, ServiceErrors serviceErrors) {
        return super.getBeneficiaryDetails(accountKey, serviceErrors);
    }

    @Override
    public BeneficiaryDetailsResponseHolderImpl getBeneficiaryDetails(List<String> accountIds, ServiceErrors serviceErrors) {
        return super.getBeneficiaryDetails(accountIds, serviceErrors);
    }


    @Override
    @Cacheable(key = "{#brokerKey, #root.target.getActiveProfileCacheKey()}",
            value = "com.bt.nextgen.service.avaloq.beneficiary.BeneficiaryDetails.Broker")
    public BeneficiaryDetailsResponseHolderImpl getBeneficiaryDetails(BrokerKey brokerKey, ServiceErrors serviceErrors) {
        return super.getBeneficiaryDetails(brokerKey, serviceErrors);
    }

    public String getActiveProfileCacheKey() {
        return userCacheService.getActiveProfileCacheKey();
    }

    /**
     * Return comman separated accountId
     *
     * @param accountIdList
     *
     * @return String
     */

    public String getAccountIds(List<String> accountIdList) {
        Collections.sort(accountIdList); //Sort accountIds for caching key
        String accountIds = StringUtils.join(accountIdList, ",");
        return accountIds;
    }
}
