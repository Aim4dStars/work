package com.bt.nextgen.api.corporateaction.v1.model;

import com.fasterxml.jackson.annotation.JsonView;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccountParticipationStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionStatus;

public class ImCorporateActionPortfolioModelDto extends BaseDto implements KeyedDto<CorporateActionDtoKey> {
	@JsonView(JsonViews.Write.class)
	private String ipsId;

	private String portfolioModelId;
	private String portfolioCode;
	private String portfolioName;
	private Integer investors;
    private Integer investorElectionsSubmitted;
	private Integer eligibleHolding;
	private CorporateActionAccountParticipationStatus electionStatus;
    private CorporateActionAccountElectionsDto savedElections;
	private CorporateActionAccountElectionsDto submittedElections;

	@JsonView(JsonViews.Write.class)
	private CorporateActionSelectedOptionsDto selectedElections;

	// Fields for the Participation report
	private CorporateActionTransactionStatus transactionStatus;
	private String transactionDescription;
	private boolean trusteeApproval;

	public ImCorporateActionPortfolioModelDto() {
		// Empty constructor
	}

	public ImCorporateActionPortfolioModelDto(ImCorporateActionPortfolioModelDtoParams params) {
		super();
		this.ipsId = params.getIpsId();
		this.portfolioModelId = params.getPortfolioModelId();
		this.portfolioCode = params.getPortfolioCode();
		this.portfolioName = params.getPortfolioName();
		this.investors = params.getInvestors();
        this.investorElectionsSubmitted = params.getInvestorElectionsSubmitted();
		this.eligibleHolding = params.getEligibleHolding();
		this.electionStatus = params.getElectionStatus();
        this.savedElections = params.getSavedElections();
		this.submittedElections = params.getSubmittedElections();
		this.transactionStatus = params.getTransactionStatus();
		this.transactionDescription = params.getTransactionDescription();
		this.trusteeApproval = params.isTrusteeApproval();
	}

	// Used by JSON mapper in API controller
	public ImCorporateActionPortfolioModelDto(CorporateActionSelectedOptionsDto selectedElections) {
		this.selectedElections = selectedElections;
	}

	public String getPortfolioModelId() {
		return portfolioModelId;
	}

	public String getPortfolioCode() {
		return portfolioCode;
	}

	public String getPortfolioName() {
		return portfolioName;
	}

	public Integer getInvestors() {
		return investors;
	}

    public Integer getInvestorElectionsSubmitted() {
        return investorElectionsSubmitted;
    }

	public Integer getEligibleHolding() {
		return eligibleHolding;
	}

	public CorporateActionAccountParticipationStatus getElectionStatus() {
		return electionStatus;
	}

    public CorporateActionAccountElectionsDto getSavedElections() {
        return savedElections;
    }

	public CorporateActionAccountElectionsDto getSubmittedElections() {
		return submittedElections;
	}

	public CorporateActionSelectedOptionsDto getSelectedElections() {
		return selectedElections;
	}

	public String getIpsId() {
		return ipsId;
	}

	public CorporateActionTransactionStatus getTransactionStatus() {
		return transactionStatus;
	}

	public String getTransactionDescription() {
		return transactionDescription;
	}

	public boolean isTrusteeApproval() {
		return trusteeApproval;
	}

	@Override
	public CorporateActionDtoKey getKey() {
		return null;
	}

}
