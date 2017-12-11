package com.bt.nextgen.service.integration.corporateaction;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;

import com.bt.nextgen.service.integration.trustee.IrgApprovalStatus;
import com.bt.nextgen.service.integration.trustee.TrusteeApprovalStatus;

/**
 * Corporate action interface.  Implementation: CorporateActionImpl
 */
public interface CorporateAction {
    /**
     * The CA unique order number
     *
     * @return order number
     */
    @NotNull
    String getOrderNumber();

    /**
     * The asset ID
     *
     * @return integer ID for looking in asset details
     */
    @NotNull
    String getAssetId();

    /**
     * The Panorama close date
     *
     * @return date of Panorama close date
     */
    DateTime getCloseDate();

    /**
     * The CA event announcement date
     *
     * @return date time of the CA announcement date
     */
    DateTime getAnnouncementDate();

    /**
     * The corporate action type.  If this is a multi-block, corporate action offer type takes effect.
     *
     * @return corporate action type enum object
     */
    CorporateActionType getCorporateActionType();

    /**
     * The corporate action offer type.  This field is populated if the corporate action type above is of type multi-block.
     *
     * @return corporate action offer type enum object
     */
    CorporateActionOfferType getCorporateActionOfferType();

    /**
     * The corporate action security exchange type.  This field is populated if the corporate action security exchange type is present
     *
     * @return corporate action security exchange type enum object
     */

    CorporateActionSecurityExchangeType getCorporateActionSecurityExchangeType();

    /**
     * The corporate action status.  Open, close or pending.
     *
     * @return corporate action status enum object
     */
    CorporateActionStatus getCorporateActionStatus();

    /**
     * The number of eligible accounts.
     *
     * @return number of eligible accounts
     */
    Integer getEligible();

    /**
     * Set eligible count.
     *
     * @param eligible eligible count
     */
    void setEligible(Integer eligible);

    /**
     * The number of eligible accounts pending action.
     *
     * @return number of eligible accounts pending action
     */
    Integer getUnconfirmed();

    /**
     * Set unconfirmed count.
     *
     * @param unconfirmed unconfirmed count
     */
    void setUnconfirmed(Integer unconfirmed);

    /**
     * Flag to check if CA is mandatory or voluntary
     *
     * @return flag
     */

    String getVoluntaryFlag();

    /**
     * The Pay date for CA.
     *
     * @return paydate
     */

    DateTime getPayDate();

    BigInteger getNotificationCnt();

    /**
     * Returns the Corporate action NPR or SPP flag
     *
     * @return Boolean true if it is a non-pro rata priority offer, false if it is not.
     */
    boolean isNonProRata();

    /**
     * Returns the CA ex date
     *
     * @return DateTime ex date
     */
    DateTime getExDate();

    /**
     * The original revenue per peice
     *
     * @return the income rate
     */
    BigDecimal getIncomeRate();

    /**
     * Returns the fully franked amount
     *
     * @return fully franked amount or null if not applicable
     */
    BigDecimal getFullyFrankedAmount();

    /**
     * Returns the unfranked amount
     *
     * @return unfranked amount or null if not applicable
     */
    BigDecimal getFullyUnfrankedAmount();

    /**
     * Returns the corporate tax rate.  Currently this is no available from Avaloq.  Value will solely be hard-coded for now.
     *
     * @return corporate tax rate - always 30 for now.
     */
    BigDecimal getCorporateTaxRate();

    /**
     * Returns the trustee approval status - used by super
     *
     * @return APPROVED/PENDING/DECLINED enum
     */
    TrusteeApprovalStatus getTrusteeApprovalStatus();

    /**
     * Returns the last trustee approval status update date
     *
     * @return DateTime of last approval status change
     */
    DateTime getTrusteeApprovalStatusDate();

    /**
     * Returns the trustee approval user ID
     *
     * @return User ID of last approval status change
     */
    String getTrusteeApprovalUserId();

    /**
     * Returns the trustee approval user name
     *
     * @return User name of last approval status change
     */
    String getTrusteeApprovalUserName();

    /**
     * The IRG (Investment Research & Governance) approval status
     *
     * @return null or one of the IrgApprovalStatus status
     */
    IrgApprovalStatus getIrgApprovalStatus();

    /**
     * The IRG (Investment Research & Governance) approval status date
     *
     * @return initial CA creation date or submitted date
     */
    DateTime getIrgApprovalStatusDate();

    /**
     * The IRG (Investment Research & Governance) approval user ID
     *
     * @return null or the submitted user ID
     */
    String getIrgApprovalUserId();

    /**
     * The IRG (Investment Research & Governance) approval user name
     *
     * @return null or the submitted user name
     */
    String getIrgApprovalUserName();

    /**
     * Flag to check if CA is early close or not
     *
     * @return flag
     */
    boolean isEarlyClose();
}

