package com.bt.nextgen.api.corporateaction.v1.model;

import java.util.List;

/**
 * This class consists of parameters being pased from the API to the DTO service
 */

public class ImCorporateActionElectionDetailsDto extends CorporateActionElectionDetailsBaseDto {
	private List<ImCorporateActionPortfolioModelDto> portfolioModels;
	private List<CorporateActionAccountDetailsDto> accounts;
	private List<ImCorporateActionElectionResultDto> electionResults;

	public ImCorporateActionElectionDetailsDto() {
		// Empty constructor
	}

	/**
	 * The main constructor
	 *
	 * @param orderNumber     the order number (CA ID)
	 * @param options         the options list from the front-end
	 * @param portfolioModels the portfolio models with election details
	 */
	public ImCorporateActionElectionDetailsDto(String orderNumber,
											   List<CorporateActionOptionDto> options,
											   List<ImCorporateActionPortfolioModelDto> portfolioModels) {
		super(orderNumber, options);
		this.portfolioModels = portfolioModels;
	}
	
	/**
     * The main constructor for a Dealer Group, where accounts and models are submitted together
     *
     * @param orderNumber     the order number (CA ID)
     * @param options         the options list from the front-end
     * @param portfolioModels the portfolio models with election details
     * @param accounts        the accounts with election details
     */
    public ImCorporateActionElectionDetailsDto(String orderNumber,
                                               List<CorporateActionOptionDto> options,
                                               List<ImCorporateActionPortfolioModelDto> portfolioModels,
                                               List<CorporateActionAccountDetailsDto> accounts) {
        super(orderNumber, options);
        this.portfolioModels = portfolioModels;
        this.accounts = accounts;
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
	public ImCorporateActionElectionDetailsDto(CorporateActionResponseCode status, Integer successCount, Integer totalCount,
											   List<ImCorporateActionElectionResultDto> electionResults, String message) {
		super(status, successCount, totalCount, message);
		this.electionResults = electionResults;
	}

	public List<ImCorporateActionPortfolioModelDto> getPortfolioModels() {
		return portfolioModels;
	}

    public List<CorporateActionAccountDetailsDto> getAccounts() {
        return accounts;
    }

	public List<ImCorporateActionElectionResultDto> getElectionResults() {
		return electionResults;
	}
}
