package com.bt.nextgen.api.corporateaction.v1.service;

import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionElectionResultDto;

import java.util.ArrayList;
import java.util.List;

public class ElectionResults {
	private boolean submitErrors = false;
	private int portfolioModelCount = 0;
	private int successCount = 0;
	private boolean completeFailure = true;
	private boolean systemFailure = true;
	private List<ImCorporateActionElectionResultDto> results = new ArrayList<>();

	public List<ImCorporateActionElectionResultDto> getResults() {
		return results;
	}

	public void setResults(List<ImCorporateActionElectionResultDto> results) {
		this.results = results;
	}

	public boolean isSubmitErrors() {
		return submitErrors;
	}

	public void setSubmitErrors(boolean submitErrors) {
		this.submitErrors = submitErrors;
	}

	public int getPortfolioModelCount() {
		return portfolioModelCount;
	}

	public void incrementPortfolioModelCount() {
		portfolioModelCount++;
	}

	public int getSuccessCount() {
		return successCount;
	}

	public void incrementSuccessCount() {
		successCount++;
	}

	public void decrementSuccessCount() {
		if (successCount > 0) {
			successCount--;
		}
	}

	public boolean isCompleteFailure() {
		return completeFailure;
	}

	public void setCompleteFailure(boolean completeFailure) {
		this.completeFailure = completeFailure;
	}

	public boolean isSystemFailure() {
		return systemFailure;
	}

	public void setSystemFailure(boolean systemFailure) {
		this.systemFailure = systemFailure;
	}
}
