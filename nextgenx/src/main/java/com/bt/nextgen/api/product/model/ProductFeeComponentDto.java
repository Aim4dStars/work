package com.bt.nextgen.api.product.model;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.bt.nextgen.core.api.model.BaseDto;

@Deprecated
public class ProductFeeComponentDto extends BaseDto
{
	public String feeComponentName;

	public DateTime feeDateFrom;

	public DateTime feeDateTo;

	public BigDecimal capFactor;

	public BigDecimal capMin;

	public BigDecimal capMax;

	public BigDecimal capOffSet;

	public BigDecimal tariffFactorMax;

	public BigDecimal tariffOffSetFactorMax;

    private String feeType;

    public String getFeeComponentName()
	{
		return feeComponentName;
	}

	public void setFeeComponentName(String feeComponentName)
	{
		this.feeComponentName = feeComponentName;
	}

	public DateTime getFeeDateFrom()
	{
		return feeDateFrom;
	}

	public void setFeeDateFrom(DateTime feeDateFrom)
	{
		this.feeDateFrom = feeDateFrom;
	}

	public DateTime getFeeDateTo()
	{
		return feeDateTo;
	}

	public void setFeeDateTo(DateTime feeDateTo)
	{
		this.feeDateTo = feeDateTo;
	}

	public BigDecimal getCapFactor()
	{
		return capFactor;
	}

	public void setCapFactor(BigDecimal capFactor)
	{
		this.capFactor = capFactor;
	}

	public BigDecimal getCapMin()
	{
		return capMin;
	}

	public void setCapMin(BigDecimal capMin)
	{
		this.capMin = capMin;
	}

	public BigDecimal getCapMax()
	{
		return capMax;
	}

	public void setCapMax(BigDecimal capMax)
	{
		this.capMax = capMax;
	}

	public BigDecimal getCapOffSet()
	{
		return capOffSet;
	}

	public void setCapOffSet(BigDecimal capOffSet)
	{
		this.capOffSet = capOffSet;
	}

	public BigDecimal getTariffFactorMax()
	{
		return tariffFactorMax;
	}

	public void setTariffFactorMax(BigDecimal tariffFactorMax)
	{
		this.tariffFactorMax = tariffFactorMax;
	}

	public BigDecimal getTariffOffSetFactorMax()
	{
		return tariffOffSetFactorMax;
	}

	public void setTariffOffSetFactorMax(BigDecimal tariffOffSetFactorMax)
	{
		this.tariffOffSetFactorMax = tariffOffSetFactorMax;
	}

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }
}
