package com.bt.nextgen.api.corporateaction.v1.model;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.bt.nextgen.service.integration.trustee.IrgApprovalStatus;
import com.bt.nextgen.service.integration.trustee.TrusteeApprovalStatus;

/**
 * Corporate action for approval Dto object.
 */
public class CorporateActionApprovalDto extends CorporateActionBaseDto {
    private BigDecimal holdingLimitPercent;
    private TrusteeApprovalStatus trusteeApprovalStatus;
    private DateTime trusteeApprovalStatusDate;
    private String trusteeApprovalUserId;
    private String trusteeApprovalUserName;
    private IrgApprovalStatus irgApprovalStatus;
    private DateTime irgApprovalStatusDate;
    private String irgApprovalUserId;
    private String irgApprovalUserName;

    public CorporateActionApprovalDto() {
        super();
    }

    /**
     * The corporate action dto constructor
     *
     * @param id     the document id/order number
     * @param params the corporate action dto params object
     */
    public CorporateActionApprovalDto(String id, CorporateActionDtoParams params) {
        super(id, params);
        this.holdingLimitPercent = params.getHoldingLimitPercent();
        this.trusteeApprovalStatus = params.getTrusteeApprovalStatus();
        this.trusteeApprovalUserId = params.getTrusteeApprovalUserId();
        this.trusteeApprovalUserName = params.getTrusteeApprovalUserName();
        this.trusteeApprovalStatusDate = params.getTrusteeApprovalStatusDate();
        this.irgApprovalStatus = params.getIrgApprovalStatus();
        this.irgApprovalUserId = params.getIrgApprovalUserId();
        this.irgApprovalUserName = params.getIrgApprovalUserName();
        this.irgApprovalStatusDate = params.getIrgApprovalStatusDate();
    }

    /**
     * The trustee approval status for super/pension.
     *
     * @return null, APPROVED, PENDING or DECLINED
     */
    public TrusteeApprovalStatus getTrusteeApprovalStatus() {
        return trusteeApprovalStatus;
    }

    /**
     * Trustee approval status change date
     *
     * @return trustee approval status date
     */
    public DateTime getTrusteeApprovalStatusDate() {
        return trusteeApprovalStatusDate;
    }

    /**
     * Trustee approval status user ID
     *
     * @return trustee approval status user ID
     */
    public String getTrusteeApprovalUserId() {
        return trusteeApprovalUserId;
    }

    /**
     * Trustee approval status user name
     *
     * @return trustee approval user name
     */
    public String getTrusteeApprovalUserName() {
        return trusteeApprovalUserName;
    }

    /**
     * Return the holding limit percent
     *
     * @return BigDecimal if there is a value, else null
     */
    public BigDecimal getHoldingLimitPercent() {
        return holdingLimitPercent;
    }

    /**
     * The IRG (Investment Research & Governance) approval status
     *
     * @return null or one of the IrgApprovalStatus status
     */
    public IrgApprovalStatus getIrgApprovalStatus() {
        return irgApprovalStatus;
    }

    /**
     * The IRG (Investment Research & Governance) approval status date
     *
     * @return initial CA creation date or submitted date
     */
    public DateTime getIrgApprovalStatusDate() {
        return irgApprovalStatusDate;
    }

    /**
     * The IRG (Investment Research & Governance) approval user ID
     *
     * @return null or the submitted user ID
     */
    public String getIrgApprovalUserId() {
        return irgApprovalUserId;
    }

    /**
     * The IRG (Investment Research & Governance) approval user name
     *
     * @return null or the submitted user name
     */
    public String getIrgApprovalUserName() {
        return irgApprovalUserName;
    }

    @Override
    public String getKey() {
        return null;
    }
}
