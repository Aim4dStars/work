package com.bt.nextgen.api.asset.model;

import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.avaloq.asset.ManagedFundPriceImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetPriceSource;
import com.bt.nextgen.service.integration.asset.AssetPriceStatus;
import org.joda.time.DateTime;

public class ManagedFundPriceDto extends AssetPriceDto implements KeyedDto<AssetPriceDtoKey> {
	private String fundApirCode;
	private String fundStatus;
	private DateTime fundFromDate;
	private DateTime fundToDate;
	private Double fundNetAssetValue;
	private Double fundEntryPrice;
	private Double fundExitPrice;
	private String hardSoftClose;

	public ManagedFundPriceDto() {
	}

	public ManagedFundPriceDto(ManagedFundPriceImpl assetPrice) {
		super(assetPrice);
		this.fundApirCode = assetPrice.getFundApirCode();
		this.fundStatus = assetPrice.getFundStatus();
		this.fundFromDate = assetPrice.getFundFromDate();
		this.fundToDate = assetPrice.getFundToDate();
		this.fundNetAssetValue = assetPrice.getFundNetAssetValue();
		this.fundEntryPrice = assetPrice.getFundEntryPrice();
		this.fundExitPrice = assetPrice.getFundExitPrice();
		this.hardSoftClose = assetPrice.getHardSoftClose();
	}

	public ManagedFundPriceDto(Asset asset, AssetPriceStatus assetPriceStatus, AssetPriceSource assetPriceSource, Double lastPrice) {
		super(asset, assetPriceStatus, assetPriceSource, lastPrice);
	}

	public String getFundApirCode() {
		return fundApirCode;
	}

	public String getFundStatus() {
		return fundStatus;
	}

	public DateTime getFundFromDate() {
		return fundFromDate;
	}

	public DateTime getFundToDate() {
		return fundToDate;
	}

	public Double getFundNetAssetValue() {
		return fundNetAssetValue;
	}

	public Double getFundEntryPrice() {
		return fundEntryPrice;
	}

	public Double getFundExitPrice() {
		return fundExitPrice;
	}

	public String getHardSoftClose() {
		return hardSoftClose;
	}
}
