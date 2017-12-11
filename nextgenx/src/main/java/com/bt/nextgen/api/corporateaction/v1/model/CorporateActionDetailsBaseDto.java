package com.bt.nextgen.api.corporateaction.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;
import com.bt.nextgen.service.integration.trustee.TrusteeApprovalStatus;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

/**
 * Corporate action details Dto object.
 */
public class CorporateActionDetailsBaseDto extends BaseDto implements KeyedDto<CorporateActionDtoKey> {
    private String companyCode;
    private String companyName;
    private BigDecimal currentPrice;
    private BigDecimal corporateActionPrice;
    private DateTime panoramaCloseDate;
    private DateTime lastUpdated;
    private DateTime payDate;
    private DateTime recordDate;
    private DateTime exDate;
    private String corporateActionType;
    private String corporateActionTypeDescription;
    private CorporateActionStatus status;
    private CorporateActionResponseCode loadStatus;
    private TrusteeApprovalStatus trusteeApprovalStatus;
    private Boolean mandatory;
    private Boolean oversubscribe;
    private CorporateActionOversubscription oversubscription;
    private Boolean partialElection;
    private BigDecimal maxTakeUpPercent;
    private List<String> summary;
    private String offerDocumentUrl;
    private List<CorporateActionOptionDto> options;
    private List<CorporateActionPriceOptionDto> minPrices;
    private CorporateActionRatio applicableRatio;
    private CorporateActionElectionMinMax electionMinMax;
    private Boolean earlyClose;
    private String errorMessage;

    public CorporateActionDetailsBaseDto() {
        // Empty constructor
    }

    /**
     * The CA details constructor
     *
     * @param params the CA details wrapper class of params
     */
    public CorporateActionDetailsBaseDto(CorporateActionDetailsDtoParams params) {
        this.companyCode = params.getAsset().getAssetCode();
        this.companyName = params.getAsset().getAssetName();
        this.currentPrice = params.getCurrentAssetPrice();
        this.panoramaCloseDate = params.getCloseDate() != null ? params.getCloseDate().withHourOfDay(12).withMinuteOfHour(0) : null;
        this.lastUpdated = params.getLastUpdatedDate();
        this.payDate = params.getPayDate();
        this.recordDate = params.getRecordDate();
        this.exDate = params.getExDate();
        this.corporateActionType = params.getCorporateActionType();
        this.status = params.getStatus();
        this.loadStatus = params.getResponseCode();
        this.trusteeApprovalStatus = params.getTrusteeApprovalStatus();
        this.options = params.getOptions();
        this.offerDocumentUrl = params.getOfferDocumentUrl();
        this.summary = params.getSummary();
        this.mandatory = params.getMandatory();
        this.oversubscribe = params.getOversubscribe();
        this.oversubscription = params.getOversubscription();
        this.partialElection = params.getPartialElection();
        this.corporateActionTypeDescription = params.getCorporateActionTypeDescription();
        this.corporateActionPrice = params.getCorporateActionPrice();
        this.minPrices = params.getMinPrices();
        this.applicableRatio = params.getApplicableRatio();
        this.electionMinMax = params.getElectionMinMax();
        this.earlyClose = params.isEarlyClose();
        this.errorMessage = params.getErrorMessage();
        this.maxTakeUpPercent = params.getMaxTakeUpPercent();
    }

    /**
     * Panorama close date
     *
     * @return date without time component.
     */
    public DateTime getPanoramaCloseDate() {
        return panoramaCloseDate;
    }

    /**
     * Company code aka asset code
     *
     * @return company code string
     */
    public String getCompanyCode() {
        return companyCode;
    }

    /**
     * Company name aka asset name
     *
     * @return company name string
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * The current price of asset
     *
     * @return current price of asset
     */
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    /**
     * The last CA updated date
     *
     * @return date and time
     */
    public DateTime getLastUpdated() {
        return lastUpdated;
    }

    /**
     * The pay date
     *
     * @return date only
     */
    public DateTime getPayDate() {
        return payDate;
    }

    /**
     * The record date
     *
     * @return date only
     */
    public DateTime getRecordDate() {
        return recordDate;
    }

    /**
     * The external date
     *
     * @return date only
     */
    public DateTime getExDate() {
        return exDate;
    }

    /**
     * List of corporate action options available to the screen
     *
     * @return list of populated corporate action options
     */
    public List<CorporateActionOptionDto> getOptions() {
        return options;
    }

    /**
     * Translated corporate action type from Avaloq
     *
     * @return corporate action type translated from Avaloq
     */
    public String getCorporateActionType() {
        return corporateActionType;
    }

    /**
     * Status of the corporate action
     *
     * @return corporate action status enum
     */
    public CorporateActionStatus getStatus() {
        return status;
    }

    /**
     * Trustee approval status
     *
     * @return trustee approval status for the corporate action
     */
    public TrusteeApprovalStatus getTrusteeApprovalStatus() {
        return trusteeApprovalStatus;
    }

    /**
     * @return the offerDocumentUrl
     */
    public String getOfferDocumentUrl() {
        return offerDocumentUrl;
    }

    /**
     * The restore/load from saved data status
     *
     * @return corporate action restore status enum
     */
    public CorporateActionResponseCode getLoadStatus() {
        return loadStatus;
    }

    /**
     * @return the summary
     */
    public List<String> getSummary() {
        return summary;
    }

    /**
     * @return the mandatory
     */
    public Boolean getMandatory() {
        return mandatory;
    }

    /**
     * Oversubscribe availability flag
     *
     * @return true if available, false is not available
     */
    public Boolean getOversubscribe() {
        return oversubscribe;
    }

    /**
     * Partial election availability flag - currently used to toggle units entry in the UI for multi-block hybrids
     *
     * @return true if available, false is not available
     */
    public Boolean getPartialElection() {
        return partialElection;
    }

    /**
     * @return the corporateActionTypeDescription
     */
    public String getCorporateActionTypeDescription() {
        return corporateActionTypeDescription;
    }

    /**
     * The current price for the asset
     *
     * @return live price for the asset
     */
    public BigDecimal getCorporateActionPrice() {
        return corporateActionPrice;
    }

    /**
     * Minimum prices for buy backs
     *
     * @return list of selectable minimum prices
     */
    public List<CorporateActionPriceOptionDto> getMinPrices() {
        return minPrices;
    }

    /**
     * The applicable ratio for non-pro rata event UI validation
     *
     * @return the ratio
     */
    public CorporateActionRatio getApplicableRatio() {
        return applicableRatio;
    }

    /**
     * The election minimum and maximum as used by NPR at this stage
     *
     * @return min/max object
     */
    public CorporateActionElectionMinMax getElectionMinMax() {
        return electionMinMax;
    }

    /**
     * @return the early close setting for the CA
     */
    public Boolean isEarlyClose() {
        return earlyClose;
    }

    /**
     * The system error message
     *
     * @return error message if any
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * The percent input limit percentag
     *
     * @return between 0 and 100, exclusive
     */
    public BigDecimal getMaxTakeUpPercent() {
        return maxTakeUpPercent;
    }

    /**
     * @return Oversubscription properties if any is set in Avaloq
     */
    public CorporateActionOversubscription getOversubscription() {
        return oversubscription;
    }

    @Override
    public CorporateActionDtoKey getKey() {
        return null;
    }
}
