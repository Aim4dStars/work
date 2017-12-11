package com.bt.nextgen.api.account.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.Collections;
import java.util.List;

/**
 * @deprecated Use V2
 */
@Deprecated
public class ManagedFundAccountDto extends BaseDto implements KeyedDto<AccountAssetKey> {

    private AccountAssetKey accountAssetKey;
    private String distributionOption;
    private List<String> availableDistributionOptions;

    public ManagedFundAccountDto(AccountAssetKey accountAssetKey, String distributionOption,
            List<String> availableDistributionOptions) {
        super();
        this.accountAssetKey = accountAssetKey;
        this.distributionOption = distributionOption;
        this.availableDistributionOptions = Collections.unmodifiableList(availableDistributionOptions);
    }

    public ManagedFundAccountDto(AccountAssetKey accountAssetKey, String distributionOption) {
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
