package com.bt.nextgen.api.modelportfolio.v2.model;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.List;

public class ModelPortfolioUploadDto extends ModelDto {
    @JsonView(JsonViews.Write.class)
    private String commentary;

    @JsonView(JsonViews.Write.class)
    private List<ModelPortfolioAssetAllocationDto> assetAllocations;

    private List<DomainApiErrorDto> warnings;

    public ModelPortfolioUploadDto() {
        super();
    }

    public ModelPortfolioUploadDto(ModelPortfolioKey modelKey, String modelCode, String modelName, String commentary,
            List<ModelPortfolioAssetAllocationDto> assetAllocations) {
        super(modelKey, modelName, modelCode);
        this.commentary = commentary;
        this.assetAllocations = assetAllocations;
    }

    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public List<ModelPortfolioAssetAllocationDto> getAssetAllocations() {
        return assetAllocations;
    }

    public List<DomainApiErrorDto> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<DomainApiErrorDto> warnings) {
        this.warnings = warnings;
    }
}
