package com.bt.nextgen.api.corporateaction.v1.model;

import java.util.List;


public class CorporateActionElectionResultDto extends CorporateActionElectionResultBaseDto {
	private String accountId;

	public CorporateActionElectionResultDto(String accountId, CorporateActionValidationStatus status, List<String> errorMessages) {
		super(status, errorMessages);
		this.accountId = accountId;
	}

	public String getAccountId() {
		return accountId;
	}
}
