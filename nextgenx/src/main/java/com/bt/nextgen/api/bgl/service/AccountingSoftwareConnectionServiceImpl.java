package com.bt.nextgen.api.bgl.service;


import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;

import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AccountingSoftwareConnectionServiceImpl implements AccountingSoftwareConnectionService
{
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(AccountingSoftwareConnectionServiceImpl.class);

    @Autowired
    BrokerIntegrationService brokerIntegrationService;

    @Autowired
    @Qualifier("cacheAvaloqAccountIntegrationService")
    AccountIntegrationService avaloqAccountIntegrationService;


    public String getAccountantGcmIdForAccount(String encodedAccountId)
    {
        String gcmId = null;
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        String accountId = EncodedString.toPlainText(encodedAccountId);
        com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf(accountId);
        logger.info("loading wrap account details for: {}", encodedAccountId);
        WrapAccountDetail wrapAccountDetail = avaloqAccountIntegrationService.loadWrapAccountDetail(accountKey, serviceErrors);

        if (wrapAccountDetail != null && wrapAccountDetail.getAccntOeId() != null)
        {
            logger.info("loading accountant {} from oe hierarchy", wrapAccountDetail.getAccntOeId().getId());
            BrokerUser brokerUser = brokerIntegrationService.getAccountantBrokerUser(wrapAccountDetail.getAccntOeId(), serviceErrors);
            gcmId = brokerUser.getBankReferenceId();
        }

        logger.info("gcmid of the accountant = {}", gcmId);
        return gcmId;
    }
}
