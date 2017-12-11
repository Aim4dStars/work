package com.bt.nextgen.service.avaloq.modelportfolio;

import com.bt.nextgen.service.integration.modelportfolio.ShadowTransaction;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ShadowTransactionImpl implements ShadowTransaction
{
	@NotNull
	private String transactionId;

	@NotNull
	private String transactionType;

	@NotNull
	private String assetId;

	@NotNull
	private String assetHolding;

	@NotNull
	private String status;

	@NotNull
	private DateTime tradeDate;

	@NotNull
	private DateTime valueDate;

	@NotNull
	private DateTime performanceDate;

	@NotNull
	private BigDecimal amount;

    private BigDecimal unitPrice;

	@NotNull
	private String description;

	@Override
	public String getTransactionId()
	{
		return transactionId;
	}

	@Override
	public String getTransactionType()
	{
		return transactionType;
	}

	@Override
	public String getAssetId()
	{
		return assetId;
	}

	@Override
	public String getAssetHolding()
	{
		return assetHolding;
	}

	@Override
	public String getStatus()
	{
		return status;
	}

	@Override
	public DateTime getTradeDate()
	{
		return tradeDate;
	}

	@Override
	public DateTime getValueDate()
	{
		return valueDate;
	}

	@Override
	public DateTime getPerformanceDate()
	{
		return performanceDate;
	}

	@Override
	public BigDecimal getAmount()
	{
		return amount;
	}

    @Override
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

	@Override
	public String getDescription()
	{
		return description;
	}

	public void setTransactionId(String transactionId)
	{
		this.transactionId = transactionId;
	}

	public void setTransactionType(String transactionType)
	{
		this.transactionType = transactionType;
	}

	public void setAssetId(String assetId)
	{
		this.assetId = assetId;
	}

	public void setAssetHolding(String assetHolding)
	{
		this.assetHolding = assetHolding;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public void setTradeDate(DateTime tradeDate)
	{
		this.tradeDate = tradeDate;
	}

	public void setValueDate(DateTime valueDate)
	{
		this.valueDate = valueDate;
	}

	public void setPerformanceDate(DateTime performanceDate)
	{
		this.performanceDate = performanceDate;
	}

	public void setAmount(BigDecimal amount)
	{
		this.amount = amount;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
