package com.bt.nextgen.api.asset.model;

import com.bt.nextgen.service.avaloq.asset.SharePriceImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetPriceSource;
import com.bt.nextgen.service.integration.asset.AssetPriceStatus;
import org.joda.time.DateTime;

public class SharePriceDto extends AssetPriceDto {
	private Double askPrice;
	private Double bidPrice;
	private Double totalValue;
	private Double openPrice;
	private Double highPrice;
	private Double lowPrice;
	private Double previousClosePrice;
	private String tradingStatus;
	private DateTime tradeDateTime;
	private DateTime updateDateTime;

	public SharePriceDto() {
	}

	public SharePriceDto(SharePriceImpl assetPrice) {
		super(assetPrice);
		this.askPrice = assetPrice.getAskPrice();
		this.bidPrice = assetPrice.getBidPrice();
		this.totalValue = assetPrice.getTotalValue();
		this.openPrice = assetPrice.getOpenPrice();
		this.highPrice = assetPrice.getHighPrice();
		this.lowPrice = assetPrice.getLowPrice();
		this.previousClosePrice = assetPrice.getPreviousClosePrice();
		this.tradingStatus = assetPrice.getTradingStatus();
		this.tradeDateTime = assetPrice.getTradeDateTime();
		this.updateDateTime = assetPrice.getUpdateDateTime();
	}

	public SharePriceDto(Asset asset, AssetPriceStatus assetPriceStatus, AssetPriceSource assetPriceSource, Double lastPrice) {
		super(asset, assetPriceStatus, assetPriceSource, lastPrice);
	}
	
	public Double getAskPrice() {
		return askPrice;
	}

	public Double getBidPrice() {
		return bidPrice;
	}

	public Double getTotalValue() {
		return totalValue;
	}

	public Double getOpenPrice() {
		return openPrice;
	}

	public Double getHighPrice() {
		return highPrice;
	}

	public Double getLowPrice() {
		return lowPrice;
	}

	public Double getPreviousClosePrice() {
		return previousClosePrice;
	}

	public String getTradingStatus() {
		return tradingStatus;
	}

	public DateTime getTradeDateTime() {
		return tradeDateTime;
	}

	public DateTime getUpdateDateTime() {
		return updateDateTime;
	}
}
