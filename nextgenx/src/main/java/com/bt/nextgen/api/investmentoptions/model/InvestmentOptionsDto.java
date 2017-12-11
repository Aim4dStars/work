package com.bt.nextgen.api.investmentoptions.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;

public class InvestmentOptionsDto extends BaseDto
{
    private String code;
	private String assetCode;
    private String name;
    private String assetClass;
    private String style;
    private BigDecimal minAmount;
	private String investmentManagerId;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAssetClass()
    {
        return assetClass;
    }

    public void setAssetClass(String assetClass)
    {
        this.assetClass = assetClass;
    }

    public String getStyle()
    {
        return style;
    }

    public void setStyle(String style)
    {
        this.style = style;
    }

    public BigDecimal getMinAmount()
    {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount)
    {
        this.minAmount = minAmount;
    }

    public String getCode()
    {
        return code;
    }

	public String getAssetCode()
	{
		return assetCode;
	}

	public void setAssetCode(String assetCode)
	{
		this.assetCode = assetCode;
	}

	public void setCode(String code)
    {
        this.code = code;
    }

	public String getInvestmentManagerId()
	{
		return investmentManagerId;
	}

	public void setInvestmentManagerId(String investmentManagerId)
	{
		this.investmentManagerId = investmentManagerId;
	}
}
