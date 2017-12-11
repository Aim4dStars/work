package com.bt.nextgen.service.avaloq.corporateaction;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import org.joda.time.DateTime;

import com.bt.nextgen.core.conversion.BigIntegerConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.corporateaction.CorporateAction;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOfferType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionSecurityExchangeType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;
import com.bt.nextgen.service.integration.trustee.IrgApprovalStatus;
import com.bt.nextgen.service.integration.trustee.TrusteeApprovalStatus;

/**
 * See interface for documentation
 */
@ServiceBean(xpath = "/")
public class CorporateActionImpl implements CorporateAction {
    public static final String XML_HEADER = "doc_head_list/doc_head/";

    @ServiceElement(xpath = XML_HEADER + "order_nr/val")
    private String orderNumber;

    @ServiceElement(xpath = XML_HEADER + "asset_id/val")
    private String assetId;

    @ServiceElement(xpath = XML_HEADER + "intl_ddln_date/val", converter = DateTimeTypeConverter.class)
    private DateTime closeDate;

    @ServiceElement(xpath = XML_HEADER + "order_date/val", converter = DateTimeTypeConverter.class)
    private DateTime announcementDate;

    @ServiceElement(xpath = XML_HEADER + "ui_aft_id/val", converter = CorporateActionTypeConverter.class)
    private CorporateActionType corporateActionType;

    @ServiceElement(xpath = XML_HEADER + "offer_type_id/val", converter = CorporateActionOfferTypeConverter.class)
    private CorporateActionOfferType corporateActionOfferType;

    @ServiceElement(xpath = XML_HEADER + "secxchg_type_id/val", converter = CorporateActionSecurityExchangeTypeConverter.class)
    private CorporateActionSecurityExchangeType corporateActionSecurityExchangeType;

    @ServiceElement(xpath = XML_HEADER + "ui_wfs_id/val", converter = CorporateActionStatusConverter.class)
    private CorporateActionStatus corporateActionStatus;

    @ServiceElement(xpath = XML_HEADER + "nof_clt/val")
    private Integer eligible;

    @ServiceElement(xpath = XML_HEADER + "nof_unconf_decsn/val")
    private Integer unconfirmed;

    @ServiceElement(xpath = XML_HEADER + "pay_date/val", converter = DateTimeTypeConverter.class)
    private DateTime payDate;

    @ServiceElement(xpath = XML_HEADER + "is_voluntary/val")
    private String voluntaryFlag;

    @ServiceElement(xpath = XML_HEADER + "is_non_pro_rata/val")
    private String nonProRata;

    @ServiceElement(xpath = "//data/top/top_head_list/top_head/coac_item_cnt/val", converter = BigIntegerConverter.class)
    private BigInteger notificationCnt;

    @ServiceElement(xpath = XML_HEADER + "ex_date/val", converter = DateTimeTypeConverter.class)
    private DateTime exDate;

    @ServiceElement(xpath = XML_HEADER + "orig_rpp/val")
    private BigDecimal incomeRate;

    @ServiceElement(xpath = XML_HEADER + "flfr_amt/val")
    private BigDecimal fullyFrankedAmount;

    @ServiceElement(xpath = XML_HEADER + "unfr_amt/val")
    private BigDecimal fullyUnfrankedAmount;

    // Not yet available in Avaloq - hard-coded for now.
    private BigDecimal corporateTaxRate = BigDecimal.valueOf(30.0);

    @ServiceElement(xpath = XML_HEADER + "trustee_aprv_id/val", staticCodeCategory = "TRUSTEE_APPROVAL_STATUS")
    private TrusteeApprovalStatus trusteeApprovalStatus;

    @ServiceElement(xpath = XML_HEADER + "trustee_aprv_date/val", converter = DateTimeTypeConverter.class)
    private DateTime trusteeApprovalStatusDate;

    @ServiceElement(xpath = XML_HEADER + "trustee_aprv_user_id/val")
    private String trusteeApprovalUserId;

    @ServiceElement(xpath = XML_HEADER + "trustee_aprv_user_name/val")
    private String trusteeApprovalUserName;

    @ServiceElement(xpath = XML_HEADER + "irg_aprv_id/val", staticCodeCategory = "IRG_APPROVAL_STATUS")
    private IrgApprovalStatus irgApprovalStatus;

    @ServiceElement(xpath = XML_HEADER + "irg_aprv_date/val", converter = DateTimeTypeConverter.class)
    private DateTime irgApprovalStatusDate;

    @ServiceElement(xpath = XML_HEADER + "irg_aprv_user_id/val")
    private String irgApprovalUserId;

    @ServiceElement(xpath = XML_HEADER + "irg_aprv_user_name/val")
    private String irgApprovalUserName;

    @ServiceElement(xpath = XML_HEADER + "is_early_close/val")
    private String earlyClose;

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
    public DateTime getAnnouncementDate() {
        return announcementDate;
    }

    public void setAnnouncementDate(DateTime announcementDate) {
        this.announcementDate = announcementDate;
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
    public Integer getEligible() {
        return eligible;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEligible(Integer eligible) {
        this.eligible = eligible;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getUnconfirmed() {
        return unconfirmed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUnconfirmed(Integer unconfirmed) {
        this.unconfirmed = unconfirmed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVoluntaryFlag() {
        return voluntaryFlag;
    }

    public void setVoluntaryFlag(String voluntaryFlag) {
        this.voluntaryFlag = voluntaryFlag;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DateTime getPayDate() {
        return new DateTime(payDate);
    }

    public void setPayDate(DateTime payDate) {
        this.payDate = new DateTime(payDate);
    }

    public BigInteger getNotificationCnt() {
        return notificationCnt;
    }

    public void setNotificationCnt(BigInteger notificationCnt) {
        this.notificationCnt = notificationCnt;
    }

    public CorporateActionSecurityExchangeType getCorporateActionSecurityExchangeType() {
        return corporateActionSecurityExchangeType;
    }

    public void setCorporateActionSecurityExchangeType(CorporateActionSecurityExchangeType corporateActionSecurityExchangeType) {
        this.corporateActionSecurityExchangeType = corporateActionSecurityExchangeType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNonProRata() {
        return nonProRata != null ? "1".equalsIgnoreCase(nonProRata) : false;
    }

    public void setNonProRata(String nonProRata) {
        this.nonProRata = nonProRata;
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
    public BigDecimal getIncomeRate() {
        return incomeRate;
    }

    public void setIncomeRate(BigDecimal incomeRate) {
        this.incomeRate = incomeRate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getFullyFrankedAmount() {
        return fullyFrankedAmount;
    }

    public void setFullyFrankedAmount(BigDecimal fullyFrankedAmount) {
        this.fullyFrankedAmount = fullyFrankedAmount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getFullyUnfrankedAmount() {
        return fullyUnfrankedAmount;
    }

    public void setFullyUnfrankedAmount(BigDecimal fullyUnfrankedAmount) {
        this.fullyUnfrankedAmount = fullyUnfrankedAmount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getCorporateTaxRate() {
        return corporateTaxRate;
    }

    public void setCorporateTaxRate(BigDecimal corporateTaxRate) {
        this.corporateTaxRate = corporateTaxRate;
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
    public DateTime getTrusteeApprovalStatusDate() {
        return trusteeApprovalStatusDate;
    }

    public CorporateActionImpl setTrusteeApprovalStatusDate(DateTime trusteeApprovalStatusDate) {
        this.trusteeApprovalStatusDate = trusteeApprovalStatusDate;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTrusteeApprovalUserId() {
        return trusteeApprovalUserId;
    }

    public CorporateActionImpl setTrusteeApprovalUserId(String trusteeApprovalUserId) {
        this.trusteeApprovalUserId = trusteeApprovalUserId;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTrusteeApprovalUserName() {
        return trusteeApprovalUserName;
    }

    public CorporateActionImpl setTrusteeApprovalUserName(String trusteeApprovalUserName) {
        this.trusteeApprovalUserName = trusteeApprovalUserName;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IrgApprovalStatus getIrgApprovalStatus() {
        return irgApprovalStatus;
    }

    public void setIrgApprovalStatus(IrgApprovalStatus irgApprovalStatus) {
        this.irgApprovalStatus = irgApprovalStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DateTime getIrgApprovalStatusDate() {
        return irgApprovalStatusDate;
    }

    public void setIrgApprovalStatusDate(DateTime irgApprovalStatusDate) {
        this.irgApprovalStatusDate = irgApprovalStatusDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIrgApprovalUserId() {
        return irgApprovalUserId;
    }

    public void setIrgApprovalUserId(String irgApprovalUserId) {
        this.irgApprovalUserId = irgApprovalUserId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIrgApprovalUserName() {
        return irgApprovalUserName;
    }

    public void setIrgApprovalUserName(String irgApprovalUserName) {
        this.irgApprovalUserName = irgApprovalUserName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEarlyClose() {
        return earlyClose != null ? "1".equalsIgnoreCase(earlyClose) : false;
    }

    public void setEarlyClose(String earlyClose) {
        this.earlyClose = earlyClose;
    }
}
