package com.bt.nextgen.api.modelportfolio.v2.model;

import com.bt.nextgen.core.api.model.BaseDto;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class ShadowTransactionDto extends BaseDto
{
	private final String transactionId;
	private final String transactionType;
	private final String assetHolding;
	private final String status;
	private final DateTime tradeDate;
	private final DateTime valueDate;
	private final DateTime performanceDate;
	private final BigDecimal amount;
	private final BigDecimal quantity;
    private final BigDecimal unitPrice;
	private final String description;
	private final String assetType;

    // TODO fix this
    @SuppressWarnings("squid:S00107")
	public ShadowTransactionDto(String transactionId, String transactionType, String assetHolding, String status,
            DateTime tradeDate, DateTime valueDate, DateTime performanceDate, BigDecimal amount, BigDecimal quantity,
            BigDecimal unitPrice, String description, String assetType)
	{
		super();
		this.transactionId = transactionId;
		this.transactionType = transactionType;
		this.assetHolding = assetHolding;
		this.status = status;
		this.tradeDate = tradeDate;
		this.valueDate = valueDate;
		this.performanceDate = performanceDate;
		this.amount = amount;
        this.unitPrice = unitPrice;
		this.quantity = quantity;
		this.description = description;
		this.assetType = assetType;
	}

	public String getTransactionId()
	{
		return transactionId;
	}

	public String getTransactionType()
	{
		return transactionType;
	}

	public String getAssetHolding()
	{
		return assetHolding;
	}

	public String getStatus()
	{
		return status;
	}

	public DateTime getTradeDate()
	{
		return tradeDate;
	}

	public DateTime getValueDate()
	{
		return valueDate;
	}

	public DateTime getPerformanceDate()
	{
		return performanceDate;
	}

	public BigDecimal getAmount()
	{
		return amount;
	}

	public BigDecimal getQuantity()
	{
		return quantity;
	}

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

	public String getDescription()
	{
		return description;
	}

	public String getAssetType()
	{
		return assetType;
	}
}
