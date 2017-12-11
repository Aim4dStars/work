package com.bt.nextgen.api.corporateaction.v1.model;

import java.util.List;

public class ImCorporateActionElectionResultDto extends CorporateActionElectionResultBaseDto {
	private String portfolioModelId;

	public ImCorporateActionElectionResultDto(String portfolioModelId, CorporateActionValidationStatus status,
											  List<String> errorMessages) {
		super(status, errorMessages);
		this.portfolioModelId = portfolioModelId;
	}

	public String getPortfolioModelId() {
		return portfolioModelId;
	}
}
