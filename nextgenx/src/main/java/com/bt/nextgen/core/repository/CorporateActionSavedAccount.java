package com.bt.nextgen.core.repository;

import java.math.BigDecimal;
import java.util.List;

public interface CorporateActionSavedAccount {
	CorporateActionSavedAccountKey getKey();

	void setKey(CorporateActionSavedAccountKey key);

	Integer getMinimumPriceId();

	void setMinimumPriceId(Integer minimumPriceId);

	List<CorporateActionSavedAccountElection> getAccountElections();

	void setAccountElections(List<CorporateActionSavedAccountElection> accountElections);

	CorporateActionSavedAccountElection addAccountElection(Integer optionId, BigDecimal units, BigDecimal percent, BigDecimal oversubscribe);
}
