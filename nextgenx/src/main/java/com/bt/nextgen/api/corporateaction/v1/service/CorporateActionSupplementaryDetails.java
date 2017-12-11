package com.bt.nextgen.api.corporateaction.v1.service;

import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionStatus;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;

import java.util.List;


public class CorporateActionSupplementaryDetails {
	private CorporateActionClientAccountDetails clientAccountDetails;
	private List<CorporateActionTransactionDetails> transactionDetails;
	private CorporateActionTransactionStatus transactionStatus;
	private List<WrapAccountValuation> wrapAccountValuations;

	public CorporateActionClientAccountDetails getClientAccountDetails() {
		return clientAccountDetails;
	}

	public void setClientAccountDetails(CorporateActionClientAccountDetails clientAccountDetails) {
		this.clientAccountDetails = clientAccountDetails;
	}

	public List<CorporateActionTransactionDetails> getTransactionDetails() {
		return transactionDetails;
	}

	public void setTransactionDetails(
			List<CorporateActionTransactionDetails> transactionDetails) {
		this.transactionDetails = transactionDetails;
	}

	public CorporateActionTransactionStatus getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(CorporateActionTransactionStatus transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public List<WrapAccountValuation> getWrapAccountValuations() {
		return wrapAccountValuations;
	}

	public void setWrapAccountValuations(
			List<WrapAccountValuation> wrapAccountValuations) {
		this.wrapAccountValuations = wrapAccountValuations;
	}
}
