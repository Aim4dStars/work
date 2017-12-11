package com.bt.nextgen.service.integration.corporateaction;

import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionCascadeOrder;
import com.bt.nextgen.service.integration.trustee.TrusteeApprovalStatus;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

/**
 * Corporate action summary interface.  Implementation: CorporateActionSummaryImpl
 */
public interface CorporateActionDetails {
    /**
     * The CA unique order number
     *
     * @return order number
     */
    String getOrderNumber();

    /**
     * The asset ID
     *
     * @return integer ID for looking in asset details
     */
    String getAssetId();

    /**
     * The corporate action status.  Open, close or pending.
     *
     * @return corporate action status enum object
     */
    CorporateActionStatus getCorporateActionStatus();

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
     * The Panorama close date
     *
     * @return date of Panorama close date
     */
    DateTime getCloseDate();

    /**
     * The CA event last updated date
     *
     * @return date time of the CA last updated date
     */
    DateTime getLastUpdatedDate();

    /**
     * The CA pay date
     *
     * @return date of pay
     */
    DateTime getPayDate();

    /**
     * The CA record date
     *
     * @return date of record
     */
    DateTime getRecordDate();

    /**
     * The CA ex date
     *
     * @return date of execution
     */
    DateTime getExDate();

    /**
     * The CA options
     *
     * @return list of CorporateActionOption which is basically name-value pair
     */
    List<CorporateActionOption> getOptions();

    /**
     * The CA decisions
     *
     * @return list of CorporateActionDecision which is basically name-value pair
     */
    List<CorporateActionOption> getDecisions();

    /**
     * Cascade orders for CA details
     *
     * @return list of cascaded order if exists.  Else null
     */
    List<CorporateActionCascadeOrder> getCascadeOrders();

    /**
     * The CA offer url
     *
     * @return offer document url
     */
    String getOfferDocumentUrl();

    /**
     * The CA Summary
     *
     * @return list of Text which will form the complete summary
     */

    String getSummary();

    /**
     * Return the trustee approval status
     *
     * @return status
     */
    TrusteeApprovalStatus getTrusteeApprovalStatus();

    /**
     * Return the early close status
     *
     * @return status
     */
    Boolean isEarlyClose();

    /**
     * Return the take-over limit percentage
     *
     * @return
     */
    BigDecimal getTakeoverLimit();
}
