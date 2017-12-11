package com.bt.nextgen.api.corporateaction.v1.model;

import java.util.List;

/**
 * This class consists of parameters being passed from the API to the DTO service
 */

public class CorporateActionElectionDetailsDto extends CorporateActionElectionDetailsBaseDto {
	private String ipsId;
	private List<CorporateActionAccountDetailsDto> accounts;
	private List<CorporateActionElectionResultDto> electionResults;

	public CorporateActionElectionDetailsDto() {
		// Empty constructor
	}

	/**
	 * The main constructor
	 *
	 * @param orderNumber the order number (CA ID)
	 * @param options     the options list from the front-end
	 * @param accounts    the accounts with election details
	 */
	public CorporateActionElectionDetailsDto(String orderNumber,
											 List<CorporateActionOptionDto> options,
											 List<CorporateActionAccountDetailsDto> accounts,
											 String ipsId) {
		super(orderNumber, options);
		this.accounts = accounts;
		this.ipsId = ipsId;
	}

	/**
	 * This constructor is the bare minimum to communicate back the status of the submission
	 *
	 * @param status          the status of the submission
	 * @param successCount    the number of submitted accounts
	 * @param totalCount      the number of total accounts
	 * @param electionResults list of failed election option IDs
	 * @param message         the message, if applicable
	 */
	public CorporateActionElectionDetailsDto(CorporateActionResponseCode status, Integer successCount, Integer totalCount,
											 List<CorporateActionElectionResultDto> electionResults, String message) {
		super(status, successCount, totalCount, message);
		this.electionResults = electionResults;
	}

	/**
	 * The accounts
	 *
	 * @return accounts
	 */
	public List<CorporateActionAccountDetailsDto> getAccounts() {
		return accounts;
	}

	/**
	 * Return a list of failed elections if any
	 *
	 * @return list of integer IDs of the option.
	 */
	public List<CorporateActionElectionResultDto> getElectionResults() {
		return electionResults;
	}

	/**
	 * Return the IPS ID for election (used by dealer group account drill-down election
	 *
	 * @return IPS ID
	 */
	public String getIpsId() {
		return ipsId;
	}
}
