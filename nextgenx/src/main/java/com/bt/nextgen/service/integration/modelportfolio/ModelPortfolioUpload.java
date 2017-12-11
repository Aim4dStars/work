package com.bt.nextgen.service.integration.modelportfolio;

import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;

import java.util.List;

public interface ModelPortfolioUpload {

    public IpsKey getModelKey();

    public String getModelCode();

    public String getModelName();

    public String getCommentary();

    public List<ModelPortfolioAssetAllocation> getAssetAllocations();

    public List<TransactionValidation> getWarnings();

}
