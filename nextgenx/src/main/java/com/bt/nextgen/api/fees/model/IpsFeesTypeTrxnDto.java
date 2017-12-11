package com.bt.nextgen.api.fees.model;

import java.math.BigDecimal;
import java.util.List;

public class IpsFeesTypeTrxnDto
{
    private String ipsId;

    private String subaccountId;

    private String type;

    private String code;

    private String apirCode;

    private String investmentName;

    private List<SlidingScaleFeeTierDto> slidingScaleFeeTier;

    private BigDecimal percentage;

    public String getIpsId() {
        return ipsId;
    }

    public void setIpsId(String ipsId) {
        this.ipsId = ipsId;
    }

    public String getComponentType() {
        return type;
    }

    public void setComponentType(String type) {
        this.type = type;
    }

    public List<SlidingScaleFeeTierDto> getSlidingScaleFeeTier() {
        return slidingScaleFeeTier;
    }

    public void setSlidingScaleFeeTier(List<SlidingScaleFeeTierDto> slidingScaleFeeTier) {
        this.slidingScaleFeeTier = slidingScaleFeeTier;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public String getSubaccountId() {
        return subaccountId;
    }

    public void setSubaccountId(String subaccountId) {
        this.subaccountId = subaccountId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getApirCode() {
        return apirCode;
    }

    public void setApirCode(String apirCode) {
        this.apirCode = apirCode;
    }

    public String getInvestmentName() {
        return investmentName;
    }

    public void setInvestmentName(String investmentName) {
        this.investmentName = investmentName;
    }

}
