package com.bt.nextgen.service.integration.cashcategorisation.model;

import java.math.BigDecimal;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.btfin.panorama.core.security.integration.account.PersonKey;
import org.joda.time.DateTime;

/**
 * A CashCategorisation represents some cash transaction that has been enriched (categorised) with
 * information pertaining to its purpose.
 */
public interface Contribution
{
	PersonKey getPersonKey();

	BigDecimal getAmount();

	CashCategorisationSubtype getCashCategorisationSubtype();

	ContributionClassification getContributionClassification();

	String getDescription();

	DateTime getTransactionDate();


	void setPersonKey(PersonKey personKey);

	void setAmount(BigDecimal amount);

	void setCashCategorisationSubtype(CashCategorisationSubtype cashCategorisationSubtype);

	void setContributionClassification(ContributionClassification contributionClassification);

	AccountKey getAccountKey();

	void setAccountKey(AccountKey accountKey);

	String getDocId();

	void setDocId(String docId);

	void setDescription(String description);

	void setTransactionDate(DateTime dateTime);

}