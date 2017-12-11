package com.bt.nextgen.serviceops.model;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainarrangementandiparrangementrelationships.v1.svc0256.ActionCode;

public class MaintainArrangementAndRelationshipReqModel {

	private static String BLANK_STRING = "";

	private String useCase = BLANK_STRING;

	private ActionCode requestedAction = ActionCode.MAINTAIN_ARRANGEMENT_AND_RELATIONSHIPS;

	private String personType = BLANK_STRING;

	private String silo = BLANK_STRING;

	private String productCpc = BLANK_STRING;

	private String versionNumberAr = BLANK_STRING;

	private String versionNumberIpAr = BLANK_STRING;

	private String startDate = BLANK_STRING;

	private String cisKey = BLANK_STRING;

	private String accountNumber = BLANK_STRING;

	private String panNumber = BLANK_STRING;

	private String bsbNumber = BLANK_STRING;

	private String lifecycleStatusReason = BLANK_STRING;

	private String endDate = BLANK_STRING;

	private String lifecycleStatus = BLANK_STRING;

	private boolean hasArrangementRole;
	private String hour = BLANK_STRING;

	private String min = BLANK_STRING;

	private String sec = BLANK_STRING;

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = null != hour ? hour : BLANK_STRING;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = null != min ? min : BLANK_STRING;
	}

	public String getSec() {
		return sec;
	}

	public void setSec(String sec) {
		this.sec = null != sec ? sec : BLANK_STRING;
	}

	public String getUseCase() {
		return useCase;
	}

	public void setUseCase(String useCase) {
		this.useCase = null != useCase ? useCase : BLANK_STRING;
	}

	public ActionCode getRequestedAction() {
		return requestedAction;
	}

	public void setRequestedAction(ActionCode requestedAction) {
		this.requestedAction = null != requestedAction ? requestedAction : ActionCode.MAINTAIN_ARRANGEMENT_AND_RELATIONSHIPS;
	}

	public String getPersonType() {
		return personType;
	}

	public void setPersonType(String personType) {
		this.personType = null != personType ? personType : BLANK_STRING;
	}

	public String getSilo() {
		return silo;
	}

	public void setSilo(String silo) {
		this.silo = null != silo ? silo : BLANK_STRING;
	}

	public String getProductCpc() {
		return productCpc;
	}

	public void setProductCpc(String productCpc) {
		this.productCpc = null != productCpc ? productCpc : BLANK_STRING;
	}

	public String getVersionNumberAr() {
		return versionNumberAr;
	}

	public void setVersionNumberAr(String versionNumberAr) {
		this.versionNumberAr = versionNumberAr;
	}

	public String getVersionNumberIpAr() {
		return versionNumberIpAr;
	}

	public void setVersionNumberIpAr(String versionNumberIpAr) {
		this.versionNumberIpAr = null != versionNumberIpAr ? versionNumberIpAr : BLANK_STRING;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = null != startDate ? startDate : BLANK_STRING;
	}

	public String getCisKey() {
		return cisKey;
	}

	public void setCisKey(String cisKey) {
		this.cisKey = null != cisKey ? cisKey : BLANK_STRING;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = null != accountNumber ? accountNumber : BLANK_STRING;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = null != panNumber ? panNumber : BLANK_STRING;
	}

	public String getBsbNumber() {
		return bsbNumber;
	}

	public void setBsbNumber(String bsbNumber) {
		this.bsbNumber = null != bsbNumber ? bsbNumber : BLANK_STRING;
	}

	public String getLifecycleStatusReason() {
		return lifecycleStatusReason;
	}

	public void setLifecycleStatusReason(String lifecycleStatusReason) {
		this.lifecycleStatusReason = null != lifecycleStatusReason ? lifecycleStatusReason : BLANK_STRING;
	}

	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = null != endDate ? endDate : BLANK_STRING;
	}

	/**
	 * @return the lifecycleStatus
	 */
	public String getLifecycleStatus() {
		return lifecycleStatus;
	}

	/**
	 * @param lifecycleStatus
	 *            the lifecycleStatus to set
	 */
	public void setLifecycleStatus(String lifecycleStatus) {
		this.lifecycleStatus = null != lifecycleStatus ? lifecycleStatus : BLANK_STRING;
	}

	/**
	 * @return the hasArrangementRole
	 */
	public boolean isHasArrangementRole() {
		return hasArrangementRole;
	}

	/**
	 * @param hasArrangementRole
	 *            the hasArrangementRole to set
	 */
	public void setHasArrangementRole(boolean hasArrangementRole) {
		this.hasArrangementRole = hasArrangementRole;
	}

}
