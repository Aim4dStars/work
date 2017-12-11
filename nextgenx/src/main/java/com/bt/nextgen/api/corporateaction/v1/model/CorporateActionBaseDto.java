package com.bt.nextgen.api.corporateaction.v1.model;

import org.joda.time.DateTime;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;

/**
 * Corporate action Dto object.
 */
public class CorporateActionBaseDto extends BaseDto implements KeyedDto<String> {
    private String id;
    private DateTime closeDate;
    private DateTime announcementDate;
    private String companyCode;
    private String companyName;
    private String corporateActionType;
    private String corporateActionTypeDescription;
    private CorporateActionStatus status;
    private Integer eligible;
    private Integer unconfirmed;
    private DateTime payDate;
    private String earlyClose;

    public CorporateActionBaseDto() {
        // Empty constructor
    }

    /**
     * The corporate action dto constructor
     *
     * @param id     the document id/order number
     * @param params the corporate action dto params object
     */
    public CorporateActionBaseDto(String id, CorporateActionDtoParams params) {
        super();
        this.id = id;
        this.closeDate = params.getCloseDate();
        this.announcementDate = params.getAnnouncementDate();
        this.companyCode = params.getAsset().getAssetCode();
        this.companyName = params.getAsset().getAssetName();
        this.corporateActionType = params.getCorporateActionType();
        this.corporateActionTypeDescription = params.getCorporateActionTypeDescription();
        this.status = params.getStatus();
        this.eligible = params.getEligible();
        this.unconfirmed = params.getUnconfirmed();
        this.payDate = params.getPayDate();
        this.earlyClose = params.getEarlyClose();
    }

    /**
     * The order ID
     *
     * @return the unique ID of the entry
     */
    public String getId() {
        return id;
    }

    /**
     * Panorama close date
     *
     * @return date without time component.
     */
    public DateTime getCloseDate() {
        return closeDate;
    }

    /**
     * Corporate action announcement date
     *
     * @return date with exact time of announcement
     */
    public DateTime getAnnouncementDate() {
        return announcementDate;
    }

    /**
     * The company code (asset ASX code)
     *
     * @return ASX code of the company
     */
    public String getCompanyCode() {
        return companyCode;
    }

    /**
     * The company's name
     *
     * @return the full name of the company.
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Translated corporate action type from Avaloq
     *
     * @return corporate action type translated from AValoq
     */
    public String getCorporateActionType() {
        return corporateActionType;
    }

    /**
     * Corporate action type description from Avaloq
     *
     * @return corporate action type description translated from AValoq
     */
    public String getCorporateActionTypeDescription() {
        return corporateActionTypeDescription;
    }

    /**
     * Status of the corporate action
     *
     * @return corporate action status enum object
     */
    public CorporateActionStatus getStatus() {
        return status;
    }

    /**
     * The number of eligible accounts
     *
     * @return number of eligible accounts. 0 if none.
     */
    public Integer getEligible() {
        return eligible;
    }

    /**
     * The number of eligible accounts pending action.
     *
     * @return integer value of the number of eligible accounts pending action.  0 if none.
     */
    public Integer getUnconfirmed() {
        return unconfirmed;
    }

    /**
     * @return the payDate
     */
    public DateTime getPayDate() {
        return new DateTime(payDate);
    }

    /**
     * Translated early close value from Avaloq
     *
     * @return early close translated Yes/No value from Avaloq.
     */
    public String getEarlyClose() {
        return this.earlyClose;
    }


    @Override
    public String getKey() {
        return null;
    }
}
