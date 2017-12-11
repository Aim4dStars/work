package com.bt.nextgen.api.fees.model;

import com.bt.nextgen.service.avaloq.fees.FeesType;

import java.math.BigDecimal;
import java.util.List;

public class InvestmentMgmtFeesDto
{
	private String investmentName;
	private String code;
	private String apirCode;
    private String subaccountId;
	private BigDecimal percent;
	private List<SlidingScaleFeeTierDto> slidingScaleFeeTier;
	private FeesType feeType;
	private BigDecimal minimumFee;
	private BigDecimal maximumFee;
	private String ipsId;

	public String getIpsId() {
		return ipsId;
	}

	public void setIpsId(String ipsId) {
		this.ipsId = ipsId;
	}


	public BigDecimal getMinimumFee() {
		return minimumFee;
	}

	public void setMinimumFee(BigDecimal minimumFee) {
		this.minimumFee = minimumFee;
	}

	public BigDecimal getMaximumFee() {
		return maximumFee;
	}

	public void setMaximumFee(BigDecimal maximumFee) {
		this.maximumFee = maximumFee;
	}

	public FeesType getFeeType() {
		return feeType;
	}

	public void setFeeType(FeesType feeType) {
		this.feeType = feeType;
	}

	public List<SlidingScaleFeeTierDto> getSlidingScaleFeeTier() {
		return slidingScaleFeeTier;
	}

	public void setSlidingScaleFeeTier(List<SlidingScaleFeeTierDto> slidingScaleFeeTier) {
		this.slidingScaleFeeTier = slidingScaleFeeTier;
	}

	public String getInvestmentName()
	{
		return investmentName;
	}

	public void setInvestmentName(String investmentName)
	{
		this.investmentName = investmentName;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getApirCode()
	{
		return apirCode;
	}

	public void setApirCode(String apirCode)
	{
		this.apirCode = apirCode;
	}

	public BigDecimal getPercent()
	{
		return percent;
	}

	public void setPercent(BigDecimal percent)
	{
		this.percent = percent;
	}

    public String getSubaccountId() {
        return subaccountId;
    }

    public void setSubaccountId(String subaccountId) {
        this.subaccountId = subaccountId;
    }

}
