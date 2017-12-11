package com.bt.nextgen.service.avaloq.drawdownstrategy;

import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.transaction.TransactionErrorDetailsImpl;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.drawdownstrategy.AssetExclusionDetails;
import com.bt.nextgen.service.integration.drawdownstrategy.AssetPriorityDetails;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategy;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategyDetails;
import com.bt.nextgen.service.integration.transaction.TransactionResponse;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;

import java.math.BigInteger;
import java.util.List;

@ServiceBean(xpath = "/")
public class DrawdownStrategyDetailsImpl extends TransactionErrorDetailsImpl implements DrawdownStrategyDetails,
        TransactionResponse {

    private AccountKey accountKey;

    @ServiceElement(xpath = "//data/draw_dwn/strat/val", staticCodeCategory = "DRAWDOWN_STRATEGY")
    private DrawdownStrategy drawdownStrategy;

    @ServiceElementList(xpath = "//data/draw_dwn_pref_list/draw_dwn_pref_item | //cont_head/dd_pref_list/dd_pref", type = AssetPriorityDetailsImpl.class)
    private List<AssetPriorityDetails> assetPriorityDetails;

    @ServiceElementList(xpath = "//data/draw_dwn_excl_pref_list/draw_dwn_excl_pref_item | //cont_head/dd_excl_pref_list/dd_excl_pref", type = AssetExclusionDetailsImpl.class)
    private List<AssetExclusionDetails> assetExclusionDetails;

    @ServiceElement(xpath = "//rsp/valid/err_list/err | //rsp/exec/err_list/err", type = TransactionValidationImpl.class)
    private List<TransactionValidation> warnings;
    private List<ValidationError> validationErrors;

    @Override
    public AccountKey getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(AccountKey accountKey) {
        this.accountKey = accountKey;
    }

    @Override
    public DrawdownStrategy getDrawdownStrategy() {
        return drawdownStrategy;
    }

    public void setDrawdownStrategy(DrawdownStrategy drawdownStrategy) {
        this.drawdownStrategy = drawdownStrategy;
    }

    @Override
    public List<AssetPriorityDetails> getAssetPriorityDetails() {
        return assetPriorityDetails;
    }

    public void setAssetPriorityDetails(List<AssetPriorityDetails> assetPriorityDetails) {
        this.assetPriorityDetails = assetPriorityDetails;
    }

    @Override
    public List<AssetExclusionDetails> getAssetExclusionDetails() {
        return assetExclusionDetails;
    }

    public void setAssetExclusionDetails(List<AssetExclusionDetails> assetExclusionDetails) {
        this.assetExclusionDetails = assetExclusionDetails;
    }

    public List<TransactionValidation> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<TransactionValidation> warnings) {
        this.warnings = warnings;
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public String getLocListItem(Integer index) {
        if (assetPriorityDetails != null) {
            AssetPriorityDetails priority = assetPriorityDetails.get(index);
            return priority.getAssetId();
        }
        return null;
    }

    @Override
    public BigInteger getLocItemIndex(String itemId) {
        int i = 1;
        if (assetPriorityDetails != null) {
            for (AssetPriorityDetails priority : assetPriorityDetails) {
                if (priority.getAssetId().equals(itemId)) {
                    return BigInteger.valueOf(i);
                }
                i++;
            }
        }
        return null;
    }
}
