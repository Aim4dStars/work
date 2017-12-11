package com.bt.nextgen.api.portfolio.v3.model.movement;

import com.bt.nextgen.api.portfolio.v3.model.DateRangeAccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public class ValuationMovementDto extends BaseDto implements KeyedDto <DateRangeAccountKey>
{
	private DateRangeAccountKey key;
	private DateTime periodStartDate;
	private DateTime periodEndDate;
	private BigDecimal openingBalance;
	private BigDecimal closingBalance;
	private List <GrowthItemDto> growthItems;

	public ValuationMovementDto()
	{
		super();
	}

	public ValuationMovementDto(DateRangeAccountKey key, DateTime periodStartDate, DateTime periodEndDate,
		BigDecimal openingBalance, BigDecimal closingBalance, List <GrowthItemDto> growthItems)
	{
		super();
		this.key = key;
		this.periodStartDate = periodStartDate;
		this.periodEndDate = periodEndDate;
		this.openingBalance = openingBalance;
		this.closingBalance = closingBalance;
		this.growthItems = growthItems;
	}

	@Override
	public DateRangeAccountKey getKey()
	{
		return key;
	}

	public DateTime getPeriodStartDate()
	{
		return periodStartDate;
	}

	public DateTime getPeriodEndDate()
	{
		return periodEndDate;
	}

	public BigDecimal getOpeningBalance()
	{
		return openingBalance;
	}

	public BigDecimal getClosingBalance()
	{
		return closingBalance;
	}

	public List <GrowthItemDto> getGrowthItems()
	{
		return growthItems;
	}
}
