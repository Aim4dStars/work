package com.bt.nextgen.service.integration.modelportfolio;

import java.math.BigDecimal;

public interface ShadowPortfolioDetail
{
	public BigDecimal getLastUpdatedTargetPercent();

	public BigDecimal getFloatingTargetPercent();

	public BigDecimal getUnits();

	public BigDecimal getMarketValue();

	public BigDecimal getShadowPercent();

	public BigDecimal getDifferencePercent();
}
