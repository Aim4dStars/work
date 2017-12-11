package com.bt.nextgen.api.corporateaction.v1.model;

import java.math.BigDecimal;

import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccountParticipationStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionStatus;
import com.bt.nextgen.service.integration.userinformation.Client;

public class CorporateActionAccountDetailsDtoParams {
	private String portfolioName;
	private String adviserName;
	private String positionId;
	private String accountId;
	private String accountKey;
	private String accountType;
	private String accountName;
	private String clientId;
	private String clientName;
	private String clientPhone;
	private String clientEmail;
	private String clientAddress;
	private CorporateActionAccountParticipationStatus electionStatus;
	private Integer holding;
	private Integer originalHolding;
	private BigDecimal cash;
	private CorporateActionAccountElectionsDto savedElections;
	private CorporateActionAccountElectionsDto submittedElections;
	private Integer transactionNumber;
	private String transactionDescription;
	private CorporateActionTransactionStatus transactionStatus;
	private BigDecimal portfolioValue;
	private boolean pendingSell;
	private CorporateActionNotification notification;
	private boolean trusteeApproval;

	public String getAccountKey() {
		return accountKey;
	}

	public void setAccountKey(String accountKey) {
		this.accountKey = accountKey;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getPortfolioName() {
		return portfolioName;
	}

	public CorporateActionAccountDetailsDtoParams setPortfolioName(String portfolioName) {
		this.portfolioName = portfolioName;
		return this;
	}

	public String getAdviserName() {
		return adviserName;
	}

	public CorporateActionAccountDetailsDtoParams setAdviserName(String adviserName) {
		this.adviserName = adviserName;
		return this;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClientPhone() {
		return clientPhone;
	}

	public void setClientPhone(String clientPhone) {
		this.clientPhone = clientPhone;
	}

	public String getClientEmail() {
		return clientEmail;
	}

	public void setClientEmail(String clientEmail) {
		this.clientEmail = clientEmail;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getPositionId() {
		return positionId;
	}

	public CorporateActionAccountDetailsDtoParams setPositionId(String positionId) {
		this.positionId = positionId;
		return this;
	}

	public BigDecimal getCash() {
		return cash;
	}

	public CorporateActionAccountDetailsDtoParams setCash(BigDecimal cash) {
		this.cash = cash;
		return this;
	}

	public CorporateActionAccountParticipationStatus getElectionStatus() {
		return electionStatus;
	}

	public CorporateActionAccountDetailsDtoParams setElectionStatus(
			CorporateActionAccountParticipationStatus electionStatus) {
		this.electionStatus = electionStatus;
		return this;
	}

	public Integer getHolding() {
		return holding;
	}

	public CorporateActionAccountDetailsDtoParams setHolding(Integer holding) {
		this.holding = holding;
		return this;
	}

	public Integer getOriginalHolding() {
		return originalHolding;
	}

	public CorporateActionAccountDetailsDtoParams setOriginalHolding(Integer originalHolding) {
		this.originalHolding = originalHolding;
		return this;
	}

	public CorporateActionAccountElectionsDto getSavedElections() {
		return savedElections;
	}

	public CorporateActionAccountDetailsDtoParams setSavedElections(
			CorporateActionAccountElectionsDto savedElections) {
		this.savedElections = savedElections;
		return this;
	}

	public CorporateActionAccountElectionsDto getSubmittedElections() {
		return submittedElections;
	}

	public CorporateActionAccountDetailsDtoParams setSubmittedElections(
			CorporateActionAccountElectionsDto submittedElections) {
		this.submittedElections = submittedElections;
		return this;
	}

	public Integer getTransactionNumber() {
		return transactionNumber;
	}

	public CorporateActionAccountDetailsDtoParams setTransactionNumber(Integer transactionNumber) {
		this.transactionNumber = transactionNumber;
		return this;
	}

	public String getTransactionDescription() {
		return transactionDescription;
	}

	public CorporateActionAccountDetailsDtoParams setTransactionDescription(String transactionDescription) {
		this.transactionDescription = transactionDescription;
		return this;
	}

	public CorporateActionTransactionStatus getTransactionStatus() {
		return transactionStatus;
	}

	public CorporateActionAccountDetailsDtoParams setTransactionStatus(CorporateActionTransactionStatus transactionStatus) {
		this.transactionStatus = transactionStatus;
		return this;
	}

	public BigDecimal getPortfolioValue() {
		return portfolioValue;
	}

	public CorporateActionAccountDetailsDtoParams setPortfolioValue(BigDecimal portfolioValue) {
		this.portfolioValue = portfolioValue;
		return this;
	}

	public boolean isPendingSell() {
		return pendingSell;
	}

	public CorporateActionAccountDetailsDtoParams setPendingSell(boolean pendingSell) {
		this.pendingSell = pendingSell;
		return this;
	}

	public boolean isTrusteeApproval() {
		return trusteeApproval;
	}

	public CorporateActionAccountDetailsDtoParams setTrusteeApproval(boolean trusteeApproval) {
		this.trusteeApproval = trusteeApproval;
		return this;
	}

	public CorporateActionNotification getNotification() {
		return notification;
	}

	public CorporateActionAccountDetailsDtoParams setNotification(CorporateActionNotification notification) {
		this.notification = notification;
		return this;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
}
