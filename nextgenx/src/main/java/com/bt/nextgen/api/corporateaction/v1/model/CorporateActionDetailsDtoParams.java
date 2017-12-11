package com.bt.nextgen.api.corporateaction.v1.model;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;

import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;
import com.bt.nextgen.service.integration.trustee.TrusteeApprovalStatus;

public class CorporateActionDetailsDtoParams {
    private Asset asset;
    private BigDecimal currentAssetPrice;
    private DateTime closeDate;
    private DateTime lastUpdatedDate;
    private DateTime payDate;
    private DateTime recordDate;
    private DateTime exDate;
    private String corporateActionType;
    private CorporateActionStatus status;
    private CorporateActionResponseCode responseCode;
    private TrusteeApprovalStatus trusteeApprovalStatus;
    private List<CorporateActionOptionDto> options;
    private List<CorporateActionAccountDetailsDto> accounts;
    private String offerDocumentUrl;
    private List<String> summary;
    private Boolean mandatory;
    private String corporateActionTypeDescription;
    private Boolean oversubscribe;
    private CorporateActionOversubscription oversubscription;
    private Boolean partialElection;
    private BigDecimal corporateActionPrice;
    private List<ImCorporateActionPortfolioModelDto> portfolioModels;
    private List<CorporateActionPriceOptionDto> minPrices;
    private CorporateActionRatio applicableRatio;
    private CorporateActionElectionMinMax electionMinMax;
    private Boolean earlyClose;
    private BigDecimal maxTakeUpPercent;

    private String errorMessage;

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public BigDecimal getCurrentAssetPrice() {
        return currentAssetPrice;
    }

    public void setCurrentAssetPrice(BigDecimal currentAssetPrice) {
        this.currentAssetPrice = currentAssetPrice;
    }

    public DateTime getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(DateTime closeDate) {
        this.closeDate = closeDate;
    }

    public DateTime getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(DateTime lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public DateTime getPayDate() {
        return payDate;
    }

    public void setPayDate(DateTime payDate) {
        this.payDate = payDate;
    }

    public DateTime getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(DateTime recordDate) {
        this.recordDate = recordDate;
    }

    public DateTime getExDate() {
        return exDate;
    }

    public void setExDate(DateTime exDate) {
        this.exDate = exDate;
    }

    public String getCorporateActionType() {
        return corporateActionType;
    }

    public void setCorporateActionType(String corporateActionType) {
        this.corporateActionType = corporateActionType;
    }

    public CorporateActionStatus getStatus() {
        return status;
    }

    public void setStatus(CorporateActionStatus status) {
        this.status = status;
    }

    public CorporateActionResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(CorporateActionResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public List<CorporateActionOptionDto> getOptions() {
        return options;
    }

    public void setOptions(
            List<CorporateActionOptionDto> options) {
        this.options = options;
    }

    public List<CorporateActionAccountDetailsDto> getAccounts() {
        return accounts;
    }

    public void setAccounts(
            List<CorporateActionAccountDetailsDto> accounts) {
        this.accounts = accounts;
    }

    public String getOfferDocumentUrl() {
        return offerDocumentUrl;
    }

    public void setOfferDocumentUrl(String offerDocumentUrl) {
        this.offerDocumentUrl = offerDocumentUrl;
    }

    public List<String> getSummary() {
        return summary;
    }

    public void setSummary(List<String> summary) {
        this.summary = summary;
    }

    /**
     * @return the mandatory
     */
    public Boolean getMandatory() {
        return mandatory;
    }

    /**
     * @param mandatory the mandatory to set
     */
    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    /**
     * @return the corporateActionTypeDescription
     */
    public String getCorporateActionTypeDescription() {
        return corporateActionTypeDescription;
    }

    /**
     * @param corporateActionTypeDescription the corporateActionTypeDescription to set
     */
    public void setCorporateActionTypeDescription(String corporateActionTypeDescription) {
        this.corporateActionTypeDescription = corporateActionTypeDescription;
    }

    /**
     * Oversubscribe flag (mainly for rights exercise and pro-rata)
     *
     * @return true = oversubscribe, false = non-oversubscribe
     */
    public Boolean getOversubscribe() {
        return oversubscribe;
    }

    public void setOversubscribe(Boolean oversubscribe) {
        this.oversubscribe = oversubscribe;
    }

    public BigDecimal getCorporateActionPrice() {
        return corporateActionPrice;
    }

    public void setCorporateActionPrice(BigDecimal corporateActionPrice) {
        this.corporateActionPrice = corporateActionPrice;
    }

    public List<ImCorporateActionPortfolioModelDto> getPortfolioModels() {
        return portfolioModels;
    }

    public void setPortfolioModels(
            List<ImCorporateActionPortfolioModelDto> portfolioModels) {
        this.portfolioModels = portfolioModels;
    }

    public List<CorporateActionPriceOptionDto> getMinPrices() {
        return minPrices;
    }

    public void setMinPrices(
            List<CorporateActionPriceOptionDto> minPrices) {
        this.minPrices = minPrices;
    }

    public CorporateActionRatio getApplicableRatio() {
        return applicableRatio;
    }

    public void setApplicableRatio(CorporateActionRatio applicableRatio) {
        this.applicableRatio = applicableRatio;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Boolean getPartialElection() {
        return partialElection;
    }

    public void setPartialElection(Boolean partialElection) {
        this.partialElection = partialElection;
    }

    public TrusteeApprovalStatus getTrusteeApprovalStatus() {
        return trusteeApprovalStatus;
    }

    public void setTrusteeApprovalStatus(
            TrusteeApprovalStatus trusteeApprovalStatus) {
        this.trusteeApprovalStatus = trusteeApprovalStatus;
    }

    public CorporateActionElectionMinMax getElectionMinMax() {
        return electionMinMax;
    }

    public void setElectionMinMax(
            CorporateActionElectionMinMax electionMinMax) {
        this.electionMinMax = electionMinMax;
    }

    public BigDecimal getMaxTakeUpPercent() {
        return maxTakeUpPercent;
    }

    public void setMaxTakeUpPercent(BigDecimal maxTakeUpPercent) {
        this.maxTakeUpPercent = maxTakeUpPercent;
    }

    /**
     * @return whether the CA is early close or not
     */
    public Boolean isEarlyClose() {
        return earlyClose;
    }

    /**
     * @return whether the CA is early close or not
     */
    public Boolean getEarlyClose() {
        return earlyClose;
    }

    /**
     * @param earlyClose the value to set early close of this CA
     */
    public void setEarlyClose(Boolean earlyClose) {
        this.earlyClose = earlyClose;
    }

    /**
     * @return Oversubscription properties if any is set in Avaloq
     */
    public CorporateActionOversubscription getOversubscription() {
        return oversubscription;
    }

    public void setOversubscription(CorporateActionOversubscription oversubscription) {
        this.oversubscription = oversubscription;
    }
}
