package com.bt.nextgen.api.modelportfolio.v2.model.detail;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public interface ModelPortfolioDetailDto extends KeyedDto<ModelPortfolioKey> {

    public ModelPortfolioKey getKey();

    public String getModelName();

    public String getModelCode();

    public String getStatus();

    public DateTime getOpenDate();

    public String getModelStructure();

    public String getInvestmentStyle();

    public String getInvestmentStyleName();

    public String getModelAssetClass();

    public String getModelAssetClassName();

    public String getModelType();

    public String getModelConstruction();

    public BigDecimal getPortfolioConstructionFee();

    public BigDecimal getMinimumInvestment();

    public String getAccountType();

    @Deprecated
    public List<ModelOfferDto> getModelOffers();

    public String getOtherInvestmentStyle();

    public String getModelDescription();

    public BigDecimal getMinimumOrderPercent();

    public BigDecimal getMinimumOrderAmount();

    public List<TargetAllocationDto> getTargetAllocations();

    public List<DomainApiErrorDto> getWarnings();

}
