package com.bt.nextgen.service.integration.base;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * This service defines the methods required to determine whether we would need to go to third party systems
 * to get historical information for Panorama functionalities - Tran History, Cash Statement, Portfolio Valuation etc.
 * Created by M035995 on 14/06/2017.
 */
@Service("ThirdPartyAvaloqAccountIntegrationService")
public class AvaloqAccountIntegrationServiceImpl implements AvaloqAccountIntegrationService {

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Override
    public ThirdPartyDetails getThirdPartySystemDetails(AccountKey accountKey, ServiceErrors serviceErrors) {
        final WrapAccountDetail account = accountIntegrationService.loadWrapAccountDetail(accountKey, serviceErrors);
        return getThirdPartyDetails(account);
    }

    private ThirdPartyDetails getThirdPartyDetails(WrapAccountDetail account) {
        final ThirdPartyDetails thirdPartyDetails = new ThirdPartyDetails();
        thirdPartyDetails.setSystemType(account.getMigrationSourceId());
        thirdPartyDetails.setMigrationKey(account.getMigrationKey());
        thirdPartyDetails.setMigrationDate(account.getMigrationDate());
        return thirdPartyDetails;
        }
}
