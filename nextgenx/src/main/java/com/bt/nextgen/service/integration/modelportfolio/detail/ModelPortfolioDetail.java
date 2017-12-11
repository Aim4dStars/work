package com.bt.nextgen.service.integration.modelportfolio.detail;

import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public interface ModelPortfolioDetail {

    public String getId();

    public String getName();

    public String getSymbol();

    public BrokerKey getInvestmentManagerId();

    public ModelPortfolioStatus getStatus();

    public DateTime getOpenDate();

    public String getModelStructure();

    public String getInvestmentStyle();

    public String getModelAssetClass();

    public String getModelType();

    public ConstructionType getModelConstruction();

    public BigDecimal getPortfolioConstructionFee();

    public BigDecimal getMinimumInvestment();

    public List<TargetAllocation> getTargetAllocations();

    public List<TransactionValidation> getWarnings();

    public List<ValidationError> getValidationErrors();

    public String getAalId();

    public String getAccountType();

    @Deprecated
    public List<OfferDetail> getOfferDetails();

    public String getMpSubType();
    
    public String getInvestmentStyleDesc();

    public String getModelDescription();

    public BigDecimal getMinimumTradePercent();

    public BigDecimal getMinimumTradeAmount();
}
