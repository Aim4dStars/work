package com.bt.nextgen.api.fees.model;

import java.util.List;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import java.math.BigDecimal;

public class FeesTypeDto extends BaseDto implements KeyedDto <FeeScheduleKey>
{
	private FeeScheduleKey key;
	private String type;
    private List <FeesComponentDto> feesComponent;
    private BigDecimal specialDiscount = new BigDecimal(0).setScale(2);

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public List <FeesComponentDto> getFeesComponent()
	{
		return feesComponent;
	}

	public void setFeesComponent(List <FeesComponentDto> feesComponent)
	{
		this.feesComponent = feesComponent;
	}

	public void setKey(FeeScheduleKey key)
	{
		this.key = key;
	}

	@Override
	public FeeScheduleKey getKey()
	{
		return key;
	}

    public BigDecimal getSpecialDiscount() {
        return specialDiscount;
    }

    public void setSpecialDiscount(BigDecimal specialDiscount) {
        this.specialDiscount = specialDiscount;
    }

}
