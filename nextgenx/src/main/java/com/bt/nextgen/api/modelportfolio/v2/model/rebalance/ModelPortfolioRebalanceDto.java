package com.bt.nextgen.api.modelportfolio.v2.model.rebalance;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelDto;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummary;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalance;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelRebalanceStatus;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class ModelPortfolioRebalanceDto extends ModelDto {

    private DateTime lastRebalanceDate;
    private String status;
    private ModelRebalanceStatus rebalanceStatus;
    private boolean submitInProgress;
    private String userName;
    private Integer totalAccountsCount;
    private Integer totalRebalancesCount;
    private List<ModelPortfolioRebalanceTriggerDto> rebalanceTriggers = new ArrayList<ModelPortfolioRebalanceTriggerDto>();
    private String accountType;

    public ModelPortfolioRebalanceDto(ModelPortfolioKey key, ModelPortfolioRebalance rebalance, String ipsStatus,
            ModelPortfolioSummary summary, String modelName, String modelCode) {
        super(key, modelName, modelCode);
        this.lastRebalanceDate = rebalance.getLastRebalanceDate();
        this.userName = rebalance.getUserName();
        this.status = ipsStatus;
        this.totalAccountsCount = rebalance.getTotalAccountsCount();
        this.totalRebalancesCount = rebalance.getTotalRebalancesCount();
        this.submitInProgress = rebalance.getSubmitInProgress();
        this.rebalanceStatus = summary.getRebalanceStatus();
        this.accountType = summary.getAccountType().getDisplayValue();
    }

    public ModelPortfolioRebalanceDto(ModelPortfolioKey key, ModelPortfolioSummary summary, String modelName, String modelCode) {
        super(key, modelName, modelCode);
        this.lastRebalanceDate = summary.getLastRebalanceDate();
        this.userName = summary.getLastRebalanceUser();
        this.status = summary.getStatus().getName();
        this.totalAccountsCount = summary.getNumAccounts();
        this.totalRebalancesCount = 0;
        this.submitInProgress = false;
        this.rebalanceStatus = null;
        this.accountType = summary.getAccountType().getDisplayValue();
    }

    public ModelPortfolioRebalanceDto(ModelPortfolioKey key, String status) {
        super(key, null, null);
        this.status = status;
    }

    public DateTime getLastRebalanceDate() {
        return lastRebalanceDate;
    }

    public String getUserName() {
        return userName;
    }

    public Integer getTotalAccountsCount() {
        return totalAccountsCount;
    }

    public Integer getTotalRebalancesCount() {
        return totalRebalancesCount;
    }

    public List<ModelPortfolioRebalanceTriggerDto> getRebalanceTriggers() {
        return rebalanceTriggers;
    }

    public String getStatus() {
        return status;
    }

    public ModelRebalanceStatus getRebalanceStatus() {
        if (submitInProgress) {
            return ModelRebalanceStatus.PROCESSING;
        }
        if (rebalanceStatus != null) {
            return rebalanceStatus;
        }
        return ModelRebalanceStatus.COMPLETE;
    }

    public void setRebalanceStatus(ModelRebalanceStatus status) {
        this.rebalanceStatus = status;
    }

    public String getAccountType() {
        return accountType;
    }
}
