package com.bt.nextgen.api.modelportfolio.v2.model.rebalance;

public class ModelPortfolioRebalanceTriggerKey implements Comparable<ModelPortfolioRebalanceTriggerKey> {

    private String modelPortfolioRebalanceTriggerId;

    public ModelPortfolioRebalanceTriggerKey(String modelPortfolioRebalanceOrderId) {
        if (modelPortfolioRebalanceOrderId == null) {
            throw new IllegalArgumentException("modelPortfolioRebalanceOrderId cannot be null");
        }
        this.modelPortfolioRebalanceTriggerId = modelPortfolioRebalanceOrderId;
    }

    public String getModelPortfolioRebalanceOrderId() {
        return modelPortfolioRebalanceTriggerId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((modelPortfolioRebalanceTriggerId == null) ? 0 : modelPortfolioRebalanceTriggerId.hashCode());
        return result;
    }

    @Override
    // disabling warnings on automatically generated code.
    @SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142" })    
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ModelPortfolioRebalanceTriggerKey other = (ModelPortfolioRebalanceTriggerKey) obj;
        if (modelPortfolioRebalanceTriggerId == null) {
            if (other.modelPortfolioRebalanceTriggerId != null)
                return false;
        } else if (!modelPortfolioRebalanceTriggerId.equals(other.modelPortfolioRebalanceTriggerId))
            return false;
        return true;
    }

    @Override
    public int compareTo(ModelPortfolioRebalanceTriggerKey o) {
        return this.modelPortfolioRebalanceTriggerId.compareTo(o.modelPortfolioRebalanceTriggerId);
    }

}
