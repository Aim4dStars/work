package com.bt.nextgen.api.modelportfolio.v2.model;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummary;
import com.fasterxml.jackson.annotation.JsonView;

public class ModelDto extends BaseDto implements KeyedDto<ModelPortfolioKey> {
    @JsonView(JsonViews.Write.class)
    private ModelPortfolioKey key;

    @JsonView(JsonViews.Write.class)
    private String modelName;

    @JsonView(JsonViews.Write.class)
    private String modelCode;

    public ModelDto() {
        super();
    }

    public ModelDto(ModelPortfolioKey key, String modelName, String modelCode) {
        super();
        this.key = key;
        this.modelName = modelName;
        this.modelCode = modelCode;
    }

    public ModelDto(ModelPortfolioKey key, ModelPortfolioSummary summary) {
        super();
        this.key = key;
        if (summary != null) {
            this.modelName = summary.getModelName();
            this.modelCode = summary.getModelCode();
        }
    }

    public ModelDto(ModelPortfolioSummary summary) {
        super();
        this.key = new ModelPortfolioKey(summary.getModelKey().getId());
        this.modelName = summary.getModelName();
        this.modelCode = summary.getModelCode();
    }

    public ModelPortfolioKey getKey() {
        return key;
    }

    public void setKey(ModelPortfolioKey key) {
        this.key = key;
    }

    public String getModelName() {
        return modelName;
    }

    public String getModelCode() {
        return modelCode;
    }
}
