package com.bt.nextgen.service.integration.corporateaction;

import java.util.List;

import javax.validation.constraints.NotNull;

public interface CorporateActionPortfolioModel {

	/**
	 * The CA Account number
	 *
	 * @return account number
	 */
	@NotNull
	String getPositionId();


	/**
	 * The CA Account number
	 *
	 * @return account number
	 */
	@NotNull
	String getPortfolioModelId();


	CorporateActionAccountParticipationStatus getElectionStatus();

	/**
	 * The CA decisions
	 *
	 * @return list of CorporateActionOption which is basically name-value pair
	 */

	List<CorporateActionOption> getDecisions();
}