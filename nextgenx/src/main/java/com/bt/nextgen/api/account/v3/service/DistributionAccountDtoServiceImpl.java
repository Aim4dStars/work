package com.bt.nextgen.api.account.v3.service;

import com.bt.nextgen.api.account.v3.model.DistributionAccountDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("DistributionAccountDtoServiceV3")
public class DistributionAccountDtoServiceImpl implements DistributionAccountDtoService {

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Override
    public DistributionAccountDto update(DistributionAccountDto mfa, ServiceErrors serviceErrors) {
        accountIntegrationService.updateDistributionOption(
                SubAccountKey.valueOf(EncodedString.toPlainText(mfa.getKey().getAccountId())),
                AssetKey.valueOf(mfa.getKey().getAssetId()), DistributionMethod.forDisplayName(mfa.getDistributionOption()),
                serviceErrors);
        return mfa;
    }

}
