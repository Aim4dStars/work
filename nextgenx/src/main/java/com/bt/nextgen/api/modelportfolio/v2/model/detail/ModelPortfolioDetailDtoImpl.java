package com.bt.nextgen.api.modelportfolio.v2.model.detail;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelDto;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.service.integration.asset.IpsAssetClass;
import com.bt.nextgen.service.integration.investment.InvestmentStyle;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioDetail;
import com.fasterxml.jackson.annotation.JsonView;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public class ModelPortfolioDetailDtoImpl extends ModelDto implements ModelPortfolioDetailDto {
    @JsonView(JsonViews.Write.class)
    private String status;

    @JsonView(JsonViews.Write.class)
    private DateTime openDate;

    @JsonView(JsonViews.Write.class)
    private String modelStructure;

    @JsonView(JsonViews.Write.class)
    private String investmentStyle;

    @JsonView(JsonViews.Write.class)
    private String modelAssetClass;

    @JsonView(JsonViews.Write.class)
    private String modelType;

    @JsonView(JsonViews.Write.class)
    private String modelConstruction;

    @JsonView(JsonViews.Write.class)
    private BigDecimal portfolioConstructionFee;

    @JsonView(JsonViews.Write.class)
    private BigDecimal minimumInvestment;

    @JsonView(JsonViews.Write.class)
    private String accountType;

    @JsonView(JsonViews.Write.class)
    private List<ModelOfferDto> modelOffers;

    @JsonView(JsonViews.Write.class)
    private List<TargetAllocationDto> targetAllocations;

    @JsonView(JsonViews.Write.class)
    private String otherInvestmentStyle;

    @JsonView(JsonViews.Write.class)
    private String modelDescription;

    @JsonView(JsonViews.Write.class)
    private BigDecimal minimumOrderPercent;

    @JsonView(JsonViews.Write.class)
    private BigDecimal minimumOrderAmount;

    private String investmentStyleName;
    private String modelAssetClassName;
    private List<DomainApiErrorDto> warnings;

    public ModelPortfolioDetailDtoImpl() {
        super();
    }

    @Deprecated
    public ModelPortfolioDetailDtoImpl(ModelPortfolioDetail model, List<TargetAllocationDto> targetAllocations,
            List<ModelOfferDto> offerList, List<DomainApiErrorDto> warnings) {
        this(model, targetAllocations, warnings);
        this.modelOffers = offerList;
    }

    public ModelPortfolioDetailDtoImpl(ModelPortfolioDetail model, List<TargetAllocationDto> targetAllocations,
            List<DomainApiErrorDto> warnings) {
        super(model.getId() == null ? null : new ModelPortfolioKey(model.getId()), model.getName(), model.getSymbol());
        this.status = model.getStatus() == null ? null : model.getStatus().name();
        this.openDate = model.getOpenDate();
        this.modelStructure = model.getModelStructure();
        this.investmentStyle = model.getInvestmentStyle();
        this.investmentStyleName = StringUtils.isBlank(investmentStyle) ? null : InvestmentStyle.forIntlId(investmentStyle)
                .getDescription();
        this.modelAssetClass = model.getModelAssetClass();
        this.modelAssetClassName = StringUtils.isBlank(modelAssetClass) ? null : IpsAssetClass.forIntlId(modelAssetClass)
                .getDescription();
        this.modelType = model.getModelType();
        this.modelConstruction = model.getModelConstruction() == null ? null : model.getModelConstruction().getDisplayValue();
        this.portfolioConstructionFee = model.getPortfolioConstructionFee();
        this.minimumInvestment = model.getMinimumInvestment();
        this.accountType = getIpsModelCode(model.getAccountType());

        this.targetAllocations = targetAllocations;
        this.warnings = warnings;
        this.otherInvestmentStyle = model.getInvestmentStyleDesc();
        this.modelDescription = model.getModelDescription();

        if (model.getMinimumTradePercent() != null) {
            this.minimumOrderPercent = model.getMinimumTradePercent();
        } else {
            this.minimumOrderAmount = model.getMinimumTradeAmount();
        }
    }

    public String getStatus() {
        return status;
    }

    public DateTime getOpenDate() {
        return openDate;
    }

    public String getModelStructure() {
        return modelStructure;
    }

    public String getInvestmentStyle() {
        return investmentStyle;
    }

    public String getModelAssetClass() {
        return modelAssetClass;
    }

    public String getModelType() {
        return modelType;
    }

    public String getModelConstruction() {
        return modelConstruction;
    }

    public BigDecimal getPortfolioConstructionFee() {
        return portfolioConstructionFee;
    }

    public BigDecimal getMinimumInvestment() {
        return minimumInvestment;
    }

    public String getAccountType() {
        return accountType;
    }

    @Deprecated
    public List<ModelOfferDto> getModelOffers() {
        return modelOffers;
    }

    public List<TargetAllocationDto> getTargetAllocations() {
        return targetAllocations;
    }

    public List<DomainApiErrorDto> getWarnings() {
        return warnings;
    }

    public String getInvestmentStyleName() {
        return investmentStyleName;
    }

    public String getModelAssetClassName() {
        return modelAssetClassName;
    }

    private String getIpsModelCode(String modelType) {
        ModelType model = ModelType.forId(modelType);
        if (model != null) {
            return model.getCode();
        }

        model = ModelType.forCode(modelType);
        if (model != null) {
            return model.getCode();
        }
        return null;
    }

    @Override
    public String getOtherInvestmentStyle() {
        return otherInvestmentStyle;
    }

    @Override
    public String getModelDescription() {
        return modelDescription;
    }

    @Override
    public BigDecimal getMinimumOrderPercent() {
        return minimumOrderPercent;
    }

    @Override
    public BigDecimal getMinimumOrderAmount() {
        return minimumOrderAmount;
    }
}
