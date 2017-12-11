package com.bt.nextgen.core.repository;

import java.math.BigDecimal;

public interface CorporateActionSavedAccountElection {
	CorporateActionSavedAccountElectionKey getKey();

	void setKey(CorporateActionSavedAccountElectionKey key);

	BigDecimal getUnits();

	void setUnits(BigDecimal units);

	BigDecimal getPercent();

	void setPercent(BigDecimal percent);

	BigDecimal getOversubscribe();

	void setOversubscribe(BigDecimal oversubscribe);
}
