package com.bt.nextgen.api.fees.v1.model;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.Map;

/**
 * @deprecated Use V2
 */
@Deprecated
public class AssetMappedAccountTransactionFeesDto extends BaseDto implements KeyedDto<AccountKey> {

    private final AccountKey accountKey;
    private final Map<String, TransactionFeeDto> assetTransactionFees;

    public AssetMappedAccountTransactionFeesDto(AccountKey accountKey, Map<String, TransactionFeeDto> assetTransactionFees) {
        this.accountKey = accountKey;
        this.assetTransactionFees = assetTransactionFees;
    }

    @Override
    public AccountKey getKey() {
        return accountKey;
    }

    public Map<String, TransactionFeeDto> getAssetTransactionFees() {
        return assetTransactionFees;
    }

}
