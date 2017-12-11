package com.bt.nextgen.api.income.v2.model;

import com.btfin.panorama.service.integration.asset.AssetType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.List;

public class InvestmentTypeDto extends AbstractBaseIncomeDto {
    private final AssetType assetType;
    private final List<IncomeDto> investmentIncomeTypes;
    private final BigDecimal incomeTotal;

    public InvestmentTypeDto(AssetType assetType, List<IncomeDto> investmentIncomeTypes, BigDecimal incomeTotal) {
        this.assetType = assetType;
        this.investmentIncomeTypes = investmentIncomeTypes;
        this.incomeTotal = incomeTotal;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public String getInvestmentTypeName() {
        return assetType.getGroupDescription();
    }

    public List<IncomeDto> getInvestmentIncomeTypes() {
        return investmentIncomeTypes;
    }

    public Boolean getShowSubGroup() {
        if (assetType.equals(AssetType.CASH) || assetType.equals(AssetType.TERM_DEPOSIT)) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    public BigDecimal getIncomeTotal() {
        return incomeTotal;
    }

    @Override
    @JsonIgnore
    public List<IncomeDto> getChildren() {
        return investmentIncomeTypes;
    }

    @Override
    public String getName() {
        return assetType.getGroupDescription();
    }

    @Override
    public String getCode() {
        return null;
    }

}
