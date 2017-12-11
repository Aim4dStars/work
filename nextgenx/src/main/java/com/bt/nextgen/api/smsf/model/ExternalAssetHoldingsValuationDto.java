package com.bt.nextgen.api.smsf.model;


import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ExternalAssetHoldingsValuationDto extends BaseDto
{
    private BigDecimal totalMarketValue = BigDecimal.ZERO;

    private BigDecimal percentageOfPortfolio = BigDecimal.ZERO;

    private List<ExternalAssetClassValuationDto> valuationByAssetClass = new ArrayList<>();

	private String dataFeedLastImportDate;

    public BigDecimal getTotalMarketValue() {
        return totalMarketValue;
    }

    public BigDecimal getPercentageOfPortfolio() {
        return percentageOfPortfolio;
    }

    public List<ExternalAssetClassValuationDto> getValuationByAssetClass() {
        return valuationByAssetClass;
    }

    public void setTotalMarketValue(BigDecimal totalMarketValue) {
        this.totalMarketValue = totalMarketValue;
    }

    public void setPercentageOfPortfolio(BigDecimal percentageOfPortfolio) {
        this.percentageOfPortfolio = percentageOfPortfolio;
    }

    public void setValuationByAssetClass(List<ExternalAssetClassValuationDto> valuationByAssetClass) {
        this.valuationByAssetClass = valuationByAssetClass;
    }

	public String getDataFeedLastImportDate() {
		return dataFeedLastImportDate;
	}

	public void setDataFeedLastImportDate(String dataFeedLastImportDate) {
		this.dataFeedLastImportDate = dataFeedLastImportDate;
	}
}