package com.bt.nextgen.api.modelportfolio.v2.model;

import com.bt.nextgen.config.JsonViews;
import com.fasterxml.jackson.annotation.JsonView;

public class ModelPortfolioKey {
    @JsonView(JsonViews.Write.class)
    private String modelId;

    public ModelPortfolioKey() {
        super();
        this.modelId = null;
    }

    public ModelPortfolioKey(String modelId) {
        super();
        this.modelId = modelId;
    }

    public String getModelId() {
        return modelId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((modelId == null) ? 0 : modelId.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142" })
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ModelPortfolioKey other = (ModelPortfolioKey) obj;
        if (modelId == null) {
            if (other.modelId != null)
                return false;
        } else if (!modelId.equals(other.modelId))
            return false;
        return true;
    }
}
