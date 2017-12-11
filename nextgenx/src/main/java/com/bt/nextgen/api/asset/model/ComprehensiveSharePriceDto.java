package com.bt.nextgen.api.asset.model;

import com.bt.nextgen.service.avaloq.asset.SharePriceImpl;

public class ComprehensiveSharePriceDto extends SharePriceDto {
	private String exchangeCode;
	private String dataSource;
	private Integer askCount;
	private Double askVolume;
	private Integer bidCount;
	private Double bidVolume;
	private Double totalVolume;
	private Double matchPrice;
	private Double matchVolume;
	private Double marketValue;
	private Double marketVolume;
	private Double movement;
	private String quotationBasisCode;
	private String companyReportCode;
	private Integer tradeCount;

	public ComprehensiveSharePriceDto(SharePriceImpl assetPrice) {
		super(assetPrice);
		this.exchangeCode = assetPrice.getExchangeCode();
		this.dataSource = assetPrice.getDataSource();
		this.askCount = assetPrice.getAskCount();
		this.askVolume = assetPrice.getAskVolume();
		this.bidCount = assetPrice.getBidCount();
		this.bidVolume = assetPrice.getBidVolume();
		this.totalVolume = assetPrice.getTotalVolume();
		this.matchPrice = assetPrice.getMatchPrice();
		this.matchVolume = assetPrice.getMatchVolume();
		this.marketValue = assetPrice.getMarketValue();
		this.marketVolume = assetPrice.getMarketVolume();
		this.movement = assetPrice.getMovement();
		this.quotationBasisCode = assetPrice.getQuotationBasisCode();
		this.companyReportCode = assetPrice.getCompanyReportCode();
		this.tradeCount = assetPrice.getTradeCount();
	}

	public String getExchangeCode() {
		return exchangeCode;
	}

	public String getDataSource() {
		return dataSource;
	}

	public Integer getAskCount() {
		return askCount;
	}

	public Double getAskVolume() {
		return askVolume;
	}

	public Integer getBidCount() {
		return bidCount;
	}

	public Double getBidVolume() {
		return bidVolume;
	}

	public Double getTotalVolume() {
		return totalVolume;
	}

	public Double getMatchPrice() {
		return matchPrice;
	}

	public Double getMatchVolume() {
		return matchVolume;
	}

	public Double getMarketValue() {
		return marketValue;
	}

	public Double getMarketVolume() {
		return marketVolume;
	}

	public Double getMovement() {
		return movement;
	}

	public String getQuotationBasisCode() {
		return quotationBasisCode;
	}

	public String getCompanyReportCode() {
		return companyReportCode;
	}

	public Integer getTradeCount() {
		return tradeCount;
	}
}
