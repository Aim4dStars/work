package com.bt.nextgen.api.asset.model;

import com.bt.nextgen.service.avaloq.asset.ManagedFundPriceImpl;

public class ComprehensiveManagedFundPriceDto extends ManagedFundPriceDto {
	private String exchangeCode;
	private String dataSource;

	public ComprehensiveManagedFundPriceDto(ManagedFundPriceImpl assetPrice) {
		super(assetPrice);
		this.exchangeCode = assetPrice.getExchangeCode();
		this.dataSource = assetPrice.getDataSource();
	}

	public String getExchangeCode() {
		return exchangeCode;
	}

	public String getDataSource() {
		return dataSource;
	}
}
