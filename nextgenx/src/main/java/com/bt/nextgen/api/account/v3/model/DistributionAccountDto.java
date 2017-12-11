package com.bt.nextgen.api.account.v3.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.Collections;
import java.util.List;

public class DistributionAccountDto extends BaseDto implements KeyedDto<AccountAssetKey> {

    private AccountAssetKey accountAssetKey;
    private String distributionOption;
    private List<String> availableDistributionOptions;

    public DistributionAccountDto(AccountAssetKey accountAssetKey, String distributionOption,
            List<String> availableDistributionOptions) {
        super();
        this.accountAssetKey = accountAssetKey;
        this.distributionOption = distributionOption;
        this.availableDistributionOptions = Collections.unmodifiableList(availableDistributionOptions);
    }

    public DistributionAccountDto(AccountAssetKey accountAssetKey, String distributionOption) {
        super();
        this.accountAssetKey = accountAssetKey;
        this.distributionOption = distributionOption;
        this.availableDistributionOptions = Collections.emptyList();
    }

    @Override
    public AccountAssetKey getKey() {
        return accountAssetKey;
    }

    public String getDistributionOption() {
        return distributionOption;
    }

    public List<String> getAvailableDistributionOptions() {
        return availableDistributionOptions;
    }

}
