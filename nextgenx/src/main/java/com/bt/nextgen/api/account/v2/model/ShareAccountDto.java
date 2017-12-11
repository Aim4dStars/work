package com.bt.nextgen.api.account.v2.model;

import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

@Deprecated
public class ShareAccountDto extends DistributionAccountDto implements KeyedDto<AccountAssetKey> {

    public ShareAccountDto(AccountAssetKey accountAssetKey, String distributionOption,
            List<String> availableDistributionOptions) {
        super(accountAssetKey, distributionOption, availableDistributionOptions);
    }

    public ShareAccountDto(AccountAssetKey accountAssetKey, String distributionOption) {
        super(accountAssetKey, distributionOption);
    }

}
