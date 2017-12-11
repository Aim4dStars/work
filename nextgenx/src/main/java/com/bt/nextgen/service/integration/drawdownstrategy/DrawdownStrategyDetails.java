package com.bt.nextgen.service.integration.drawdownstrategy;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;

import java.util.List;


public interface DrawdownStrategyDetails {

    /**
     * AccountKey of client this strategy relates to. ID not encoded at this point.
     * 
     * @return
     */
    public AccountKey getAccountKey();

    /**
     * Drawdown strategy chosen
     * 
     * @return
     */
    public DrawdownStrategy getDrawdownStrategy();

    /**
     * List of assets and their drawdown priority. Only used for individual asset priority strategy.
     * 
     * @return
     */
    public List<AssetPriorityDetails> getAssetPriorityDetails();

    /**
     * List of assets to exclude from drawdowns.
     * 
     * @return
     */
    public List<AssetExclusionDetails> getAssetExclusionDetails();

    /**
     * Warning-responses from Avaloq
     * 
     * @return
     */
    public List<TransactionValidation> getWarnings();
}
