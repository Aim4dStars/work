package com.bt.nextgen.api.corporateaction.v1.model;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;
import com.bt.nextgen.service.integration.trustee.IrgApprovalStatus;
import com.bt.nextgen.service.integration.trustee.TrusteeApprovalStatus;

public class CorporateActionDtoParams {
    private String orderNumber;
    private Asset asset;
    private DateTime closeDate;
    private DateTime announcementDate;
    private String corporateActionType;
    private String corporateActionTypeDescription;
    private CorporateActionStatus status;
    private Integer eligible;
    private Integer unconfirmed;
    private DateTime payDate;
    private String accountId;
    private String portfolioModelId;
    private BigDecimal holdingLimitPercent;
    private TrusteeApprovalStatus trusteeApprovalStatus;
    private DateTime trusteeApprovalStatusDate;
    private String trusteeApprovalUserId;
    private String trusteeApprovalUserName;
    private String earlyClose;
    private IrgApprovalStatus irgApprovalStatus;
    private DateTime irgApprovalStatusDate;
    private String irgApprovalUserId;
    private String irgApprovalUserName;

    public String getOrderNumber() {
        return orderNumber;
    }

    public CorporateActionDtoParams setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
        return this;
    }

    public Asset getAsset() {
        return asset;
    }

    public CorporateActionDtoParams setAsset(Asset asset) {
        this.asset = asset;
        return this;
    }

    public DateTime getCloseDate() {
        return closeDate;
    }

    public CorporateActionDtoParams setCloseDate(DateTime closeDate) {
        this.closeDate = closeDate;
        return this;
    }

    public DateTime getAnnouncementDate() {
        return announcementDate;
    }

    public CorporateActionDtoParams setAnnouncementDate(DateTime announcementDate) {
        this.announcementDate = announcementDate;
        return this;
    }

    public String getCorporateActionType() {
        return corporateActionType;
    }

    public CorporateActionDtoParams setCorporateActionType(String corporateActionType) {
        this.corporateActionType = corporateActionType;
        return this;
    }

    public String getCorporateActionTypeDescription() {
        return corporateActionTypeDescription;
    }

    public CorporateActionDtoParams setCorporateActionTypeDescription(String corporateActionTypeDescription) {
        this.corporateActionTypeDescription = corporateActionTypeDescription;
        return this;
    }

    public CorporateActionStatus getStatus() {
        return status;
    }

    public CorporateActionDtoParams setStatus(CorporateActionStatus status) {
        this.status = status;
        return this;
    }

    public Integer getEligible() {
        return eligible;
    }

    public CorporateActionDtoParams setEligible(Integer eligible) {
        this.eligible = eligible;
        return this;
    }

    public Integer getUnconfirmed() {
        return unconfirmed;
    }

    public CorporateActionDtoParams setUnconfirmed(Integer unconfirmed) {
        this.unconfirmed = unconfirmed;
        return this;
    }

    public DateTime getPayDate() {
        return payDate;
    }

    public CorporateActionDtoParams setPayDate(DateTime payDate) {
        this.payDate = payDate;
        return this;
    }

    public String getAccountId() {
        return accountId;
    }

    public CorporateActionDtoParams setAccountId(String accountId) {
        this.accountId = accountId;
        return this;
    }

    public String getPortfolioModelId() {
        return portfolioModelId;
    }

    public CorporateActionDtoParams setPortfolioModelId(String portfolioModelId) {
        this.portfolioModelId = portfolioModelId;
        return this;
    }

    public BigDecimal getHoldingLimitPercent() {
        return holdingLimitPercent;
    }

    public CorporateActionDtoParams setHoldingLimitPercent(BigDecimal holdingLimitPercent) {
        this.holdingLimitPercent = holdingLimitPercent;
        return this;
    }

    public TrusteeApprovalStatus getTrusteeApprovalStatus() {
        return trusteeApprovalStatus;
    }

    public CorporateActionDtoParams setTrusteeApprovalStatus(TrusteeApprovalStatus trusteeApprovalStatus) {
        this.trusteeApprovalStatus = trusteeApprovalStatus;
        return this;
    }


    public DateTime getTrusteeApprovalStatusDate() {
        return trusteeApprovalStatusDate;
    }

    public CorporateActionDtoParams setTrusteeApprovalStatusDate(DateTime trusteeApprovalStatusDate) {
        this.trusteeApprovalStatusDate = trusteeApprovalStatusDate;
        return this;
    }

    public String getTrusteeApprovalUserId() {
        return trusteeApprovalUserId;
    }

    public CorporateActionDtoParams setTrusteeApprovalUserId(String trusteeApprovalUserId) {
        this.trusteeApprovalUserId = trusteeApprovalUserId;
        return this;
    }

    public String getTrusteeApprovalUserName() {
        return trusteeApprovalUserName;
    }

    public CorporateActionDtoParams setTrusteeApprovalUserName(String trusteeApprovalUserName) {
        this.trusteeApprovalUserName = trusteeApprovalUserName;
        return this;
    }

    public String getEarlyClose() {
        return earlyClose;
    }

    public void setEarlyClose(String earlyClose) {
        this.earlyClose = earlyClose;
    }

    public IrgApprovalStatus getIrgApprovalStatus() {
        return irgApprovalStatus;
    }

    public void setIrgApprovalStatus(IrgApprovalStatus irgApprovalStatus) {
        this.irgApprovalStatus = irgApprovalStatus;
    }

    public DateTime getIrgApprovalStatusDate() {
        return irgApprovalStatusDate;
    }

    public void setIrgApprovalStatusDate(DateTime irgApprovalStatusDate) {
        this.irgApprovalStatusDate = irgApprovalStatusDate;
    }

    public String getIrgApprovalUserId() {
        return irgApprovalUserId;
    }

    public void setIrgApprovalUserId(String irgApprovalUserId) {
        this.irgApprovalUserId = irgApprovalUserId;
    }

    public String getIrgApprovalUserName() {
        return irgApprovalUserName;
    }

    public void setIrgApprovalUserName(String irgApprovalUserName) {
        this.irgApprovalUserName = irgApprovalUserName;
    }
}
