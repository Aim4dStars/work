package com.bt.nextgen.service.integration.externalasset.service;


import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.ContainerKey;
import com.bt.nextgen.api.smsf.model.AssetHoldings;
import com.bt.nextgen.service.integration.externalasset.model.ExternalAsset;
import org.joda.time.DateTime;

import java.util.List;

public interface ExternalAssetIntegrationService
{
    /**
     * Saves new external assets and updates them if they already exist (and has a pos id).
     * <p>This service replaces the account's external asset list (non additive).</p>
     * <p>i.e. If you send an empty list of assets, this will wipe out all existing external assets on the account</p>
     *
     * @param accountKey Account Id to save or update external assets to
     * @param containerKey Key of the container to save external assets to
     * @return transaction status - success or failure
     */
    TransactionStatus saveOrUpdateExternalAssets(AccountKey accountKey, ContainerKey containerKey,
                                                 List<ExternalAsset> externalAssets, DateTime bankDate);

    /**
     * Retrieve external assets from position valuation service
     * @param accountKeys
     * @param effectiveDate
     * @return
     */
    AssetHoldings getExternalAssets(List<AccountKey> accountKeys, DateTime effectiveDate);
}
