package com.bt.nextgen.api.corporateaction.v1.model;

import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccountParticipationStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionStatus;


public class ImCorporateActionPortfolioModelDtoParams {
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
	private CorporateActionTransactionStatus transactionStatus;
	private String transactionDescription;
	private boolean trusteeApproval;

	public String getIpsId() {
		return ipsId;
	}

	public ImCorporateActionPortfolioModelDtoParams setIpsId(String ipsId) {
		this.ipsId = ipsId;
		return this;
	}

	public String getPortfolioModelId() {
		return portfolioModelId;
	}

	public void setPortfolioModelId(String portfolioModelId) {
		this.portfolioModelId = portfolioModelId;
	}

	public String getPortfolioCode() {
		return portfolioCode;
	}

	public ImCorporateActionPortfolioModelDtoParams setPortfolioCode(String portfolioCode) {
		this.portfolioCode = portfolioCode;
		return this;
	}

	public String getPortfolioName() {
		return portfolioName;
	}

	public ImCorporateActionPortfolioModelDtoParams setPortfolioName(String portfolioName) {
		this.portfolioName = portfolioName;
		return this;
	}

	public Integer getInvestors() {
		return investors;
	}

	public ImCorporateActionPortfolioModelDtoParams setInvestors(Integer investors) {
		this.investors = investors;
		return this;
	}

    public Integer getInvestorElectionsSubmitted() {
        return investorElectionsSubmitted;
    }

    public ImCorporateActionPortfolioModelDtoParams setInvestorElectionsSubmitted(Integer investorElectionsSubmitted) {
        this.investorElectionsSubmitted = investorElectionsSubmitted;
        return this;
    }

	public Integer getEligibleHolding() {
		return eligibleHolding;
	}

	public ImCorporateActionPortfolioModelDtoParams setEligibleHolding(Integer eligibleHolding) {
		this.eligibleHolding = eligibleHolding;
		return this;
	}

	public CorporateActionAccountParticipationStatus getElectionStatus() {
		return electionStatus;
	}

	public ImCorporateActionPortfolioModelDtoParams setElectionStatus(CorporateActionAccountParticipationStatus electionStatus) {
		this.electionStatus = electionStatus;
		return this;
	}

    public CorporateActionAccountElectionsDto getSavedElections() {
        return savedElections;
    }

    public ImCorporateActionPortfolioModelDtoParams setSavedElections(CorporateActionAccountElectionsDto savedElections) {
        this.savedElections = savedElections;
        return this;
    }

	public CorporateActionAccountElectionsDto getSubmittedElections() {
		return submittedElections;
	}

	public ImCorporateActionPortfolioModelDtoParams setSubmittedElections(CorporateActionAccountElectionsDto submittedElections) {
		this.submittedElections = submittedElections;
		return this;
	}

	public CorporateActionTransactionStatus getTransactionStatus() {
		return transactionStatus;
	}

	public ImCorporateActionPortfolioModelDtoParams setTransactionStatus(
			CorporateActionTransactionStatus transactionStatus) {
		this.transactionStatus = transactionStatus;
		return this;
	}

	public String getTransactionDescription() {
		return transactionDescription;
	}

	public ImCorporateActionPortfolioModelDtoParams setTransactionDescription(String transactionDescription) {
		this.transactionDescription = transactionDescription;
		return this;
	}

	public boolean isTrusteeApproval() {
		return trusteeApproval;
	}

	public void setTrusteeApproval(boolean trusteeApproval) {
		this.trusteeApproval = trusteeApproval;
	}
}
