package com.bt.nextgen.service.avaloq.corporateaction;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOfferType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;

import com.btfin.panorama.core.conversion.DateTimeTypeConverter;

import com.bt.nextgen.service.integration.trustee.TrusteeApprovalStatus;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

/**
 * See interface for documentation
 */
@ServiceBean(xpath = "/")
public class CorporateActionDetailsImpl implements CorporateActionDetails {
    public static final String XML_HEADER = "doc_head_list/doc_head/";

    @ServiceElement(xpath = XML_HEADER + "order_nr/val")
    private String orderNumber;

    @ServiceElement(xpath = XML_HEADER + "asset_id/val")
    private String assetId;

    @ServiceElement(xpath = XML_HEADER + "ui_aft_id/val", converter = CorporateActionTypeConverter.class)
    private CorporateActionType corporateActionType;

    @ServiceElement(xpath = XML_HEADER + "offer_type_id/val", converter = CorporateActionOfferTypeConverter.class)
    private CorporateActionOfferType corporateActionOfferType;

    @ServiceElement(xpath = XML_HEADER + "ui_wfs_id/val", converter = CorporateActionStatusConverter.class)
    private CorporateActionStatus corporateActionStatus;

    @ServiceElement(xpath = XML_HEADER + "intl_ddln_date/val", converter = DateTimeTypeConverter.class)
    private DateTime closeDate;

    @ServiceElement(xpath = XML_HEADER + "last_trans/val", converter = DateTimeTypeConverter.class)
    private DateTime lastUpdatedDate;

    @ServiceElement(xpath = XML_HEADER + "pay_date/val", converter = DateTimeTypeConverter.class)
    private DateTime payDate;

    @ServiceElement(xpath = XML_HEADER + "rec_date/val", converter = DateTimeTypeConverter.class)
    private DateTime recordDate;

    @ServiceElement(xpath = XML_HEADER + "ex_date/val", converter = DateTimeTypeConverter.class)
    private DateTime exDate;

    @ServiceElementList(xpath = XML_HEADER + "par_list_list/par_list", type = CorporateActionOptionImpl.class)
    private List<CorporateActionOption> options;

    @ServiceElementList(xpath = XML_HEADER + "decsn_list_list/decsn_list", type = CorporateActionDecisionImpl.class)
    private List<CorporateActionOption> decisions;

    @ServiceElementList(xpath = XML_HEADER + "secevt2_struct_list_list/secevt2_struct_list", type = CorporateActionCascadeOrderImpl.class)
    private List<CorporateActionCascadeOrder> cascadeOrders;

    @ServiceElement(xpath = XML_HEADER + "offer_url/val")
    private String offerDocumentUrl;

    @ServiceElement(xpath = XML_HEADER + "smry_ann/val")
    private String summary;

    @ServiceElement(xpath = XML_HEADER + "trustee_aprv_id/val", staticCodeCategory = "TRUSTEE_APPROVAL_STATUS")
    private TrusteeApprovalStatus trusteeApprovalStatus;

    @ServiceElement(xpath = XML_HEADER + "is_early_close/val")
    private String earlyClose;

    @ServiceElement(xpath = XML_HEADER + "takeover_limit/val")
    private BigDecimal takeoverLimit;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CorporateActionType getCorporateActionType() {
        return corporateActionType;
    }

    public void setCorporateActionType(CorporateActionType corporateActionType) {
        this.corporateActionType = corporateActionType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CorporateActionOfferType getCorporateActionOfferType() {
        return corporateActionOfferType;
    }

    public void setCorporateActionOfferType(CorporateActionOfferType corporateActionOfferType) {
        this.corporateActionOfferType = corporateActionOfferType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CorporateActionStatus getCorporateActionStatus() {
        return corporateActionStatus;
    }

    public void setCorporateActionStatus(CorporateActionStatus corporateActionStatus) {
        this.corporateActionStatus = corporateActionStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DateTime getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(DateTime closeDate) {
        this.closeDate = closeDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DateTime getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(DateTime lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DateTime getPayDate() {
        return payDate;
    }

    public void setPayDate(DateTime payDate) {
        this.payDate = payDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DateTime getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(DateTime recordDate) {
        this.recordDate = recordDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DateTime getExDate() {
        return exDate;
    }

    public void setExDate(DateTime exDate) {
        this.exDate = exDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CorporateActionOption> getOptions() {
        return options;
    }

    public void setOptions(List<CorporateActionOption> options) {
        this.options = options;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CorporateActionOption> getDecisions() {
        return decisions;
    }

    public void setDecisions(
            List<CorporateActionOption> decisions) {
        this.decisions = decisions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOfferDocumentUrl() {
        return offerDocumentUrl;
    }

    public void setOfferDocumentUrl(String offerDocumentUrl) {
        this.offerDocumentUrl = offerDocumentUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CorporateActionCascadeOrder> getCascadeOrders() {
        return cascadeOrders;
    }

    public void setCascadeOrders(List<CorporateActionCascadeOrder> cascadeOrders) {
        this.cascadeOrders = cascadeOrders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TrusteeApprovalStatus getTrusteeApprovalStatus() {
        return trusteeApprovalStatus;
    }

    public void setTrusteeApprovalStatus(TrusteeApprovalStatus trusteeApprovalStatus) {
        this.trusteeApprovalStatus = trusteeApprovalStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isEarlyClose() {
        return earlyClose != null ? "1".equalsIgnoreCase(earlyClose) : false;
    }

    public void setEarlyClose(String earlyClose) {
        this.earlyClose = earlyClose;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getTakeoverLimit() {
        return takeoverLimit;
    }

    public void setTakeoverLimit(BigDecimal takeoverLimit) {
        this.takeoverLimit = takeoverLimit;
    }
}
