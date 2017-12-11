package com.bt.nextgen.api.smsf.model;


import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class ExternalAssetClassValuationDto extends BaseDto
{
    private String assetClass;

    private BigDecimal totalMarketValue = BigDecimal.ZERO;

    private BigDecimal percentageOfPortfolio = BigDecimal.ZERO;

    private List<ExternalAssetDto> assetList = new ArrayList<>();


    public BigDecimal getTotalMarketValue() {
        return totalMarketValue;
    }

    public BigDecimal getPercentageOfPortfolio() {
        return percentageOfPortfolio;
    }

    public List<ExternalAssetDto> getAssetList() {
        return assetList;
    }

    public void setTotalMarketValue(BigDecimal totalMarketValue) {
        this.totalMarketValue = totalMarketValue;
    }

    public void setPercentageOfPortfolio(BigDecimal percentageOfPortfolio) {
        this.percentageOfPortfolio = percentageOfPortfolio;
    }

    public void setAssetList(List<ExternalAssetDto> assetList) {
        this.assetList = assetList;
    }

    public String getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(String assetClass) {
        this.assetClass = assetClass;
    }
}