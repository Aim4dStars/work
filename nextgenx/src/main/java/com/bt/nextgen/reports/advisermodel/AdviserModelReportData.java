package com.bt.nextgen.reports.advisermodel;

import com.bt.nextgen.api.modelportfolio.v2.model.detail.ModelPortfolioDetailDto;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;

import java.util.List;

public class AdviserModelReportData {

    private String modelName;
    private String modelIdentifier;
    private String accountType;
    private String description;
    private List<AdviserModelAllocationReportData> allocations;

    public AdviserModelReportData(ModelPortfolioDetailDto details, List<AdviserModelAllocationReportData> allocations) {
        this.modelName = details.getModelName();
        this.modelIdentifier = details.getModelCode();
        this.accountType = details.getAccountType();
        this.description = details.getModelDescription();
        this.allocations = allocations;
    }

    public String getModelName() {
        return modelName;
    }

    public String getModelIdentifier() {
        return modelIdentifier;
    }

    public String getAccountType() {
        return ModelType.forCode(accountType).getDisplayValue();
    }

    public String getDescription() {
        return description;
    }

    public List<AdviserModelAllocationReportData> getAllocations() {
        return allocations;
    }
}
