package com.bt.nextgen.core.repository;

import java.util.Date;
import java.util.List;

public interface CorporateActionSavedParticipation {
	CorporateActionSavedParticipationKey getKey();

	void setKey(CorporateActionSavedParticipationKey key);

	Date getExpiryDate();

	void setExpiryDate(Date expiryDate);

	List<CorporateActionSavedAccount> getAccounts();

	void setAccounts(List<CorporateActionSavedAccount> accounts);

	CorporateActionSavedAccount addAccount(String accountNumber);
}
