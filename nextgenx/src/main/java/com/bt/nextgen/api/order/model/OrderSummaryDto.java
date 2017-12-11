package com.bt.nextgen.api.order.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;

public class OrderSummaryDto extends BaseDto
{
	private BigDecimal totalBuys;
	private BigDecimal totalSells;
	private BigDecimal netCashMovement;

    public OrderSummaryDto() {
        super();
    }

	public OrderSummaryDto(BigDecimal totalBuys, BigDecimal totalSells, BigDecimal netCashMovement)
	{
		super();
		this.totalBuys = totalBuys;
		this.totalSells = totalSells;
		this.netCashMovement = netCashMovement;
	}

	public BigDecimal getTotalBuys()
	{
		return totalBuys;
	}

	public void setTotalBuys(BigDecimal totalBuys)
	{
		this.totalBuys = totalBuys;
	}

	public BigDecimal getTotalSells()
	{
		return totalSells;
	}

	public void setTotalSells(BigDecimal totalSells)
	{
		this.totalSells = totalSells;
	}

	public BigDecimal getNetCashMovement()
	{
		return netCashMovement;
	}

	public void setNetCashMovement(BigDecimal netCashMovement)
	{
		this.netCashMovement = netCashMovement;
	}

}
