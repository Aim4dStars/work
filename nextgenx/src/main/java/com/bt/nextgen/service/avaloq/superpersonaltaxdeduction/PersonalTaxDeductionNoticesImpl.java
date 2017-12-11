package com.bt.nextgen.service.avaloq.superpersonaltaxdeduction;

/**
 * Created by L067218 on 17/10/2016.
 */

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.core.conversion.BooleanConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.externalasset.builder.IsoDateTimeConverter;
import org.joda.time.DateTime;

import javax.annotation.concurrent.Immutable;
import java.math.BigDecimal;

/**
 * Avaloq business object for PersonalTaxDeductionNotices for a apecific type.
 */
@ServiceBean(xpath = "buc_head")
@Immutable
public class PersonalTaxDeductionNoticesImpl implements PersonalTaxDeductionNotices {

    /**
     * Avaloq document id.
     * Related  Transaction Order
     */
    @ServiceElement(xpath = "doc_id/val")
    private Long docId;

    /**
     * Avaloq reference document id.
     * Original Order of a Vary Order
     */
    @ServiceElement(xpath = "ref_doc_id/val")
    private Long refDocId;

    /**
     * Date of notice
     * Note that Avaloq does not provide time component in this attribute.
     */
    @ServiceElement(xpath = "val_date/val", converter = IsoDateTimeConverter.class)
    private DateTime noticeDate;


    /**
     * Notice variation
     */
    @ServiceElement(xpath = "is_var_notice/val", converter = BooleanConverter.class)
    private Boolean isVarNotice;

    /**
     * Notice Amount
     */
    @ServiceElement(xpath = "claim_amount_tc/val", converter = BigDecimalConverter.class)
    private BigDecimal noticeAmount;


    /**
     * Unalterable Notice Amount
     */
    @ServiceElement(xpath = "remn_claimed_contri_tc/val", converter = BigDecimalConverter.class)
    private BigDecimal unalterableNoticeAmount;

    @Override
    public BigDecimal getNoticeAmount() {
        return noticeAmount;
    }

    public void setNoticeAmount(BigDecimal noticeAmount) {
        this.noticeAmount = noticeAmount;
    }

    @Override
    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    @Override
    public Long getRefDocId() {
        return refDocId;
    }

    public void setRefDocId(Long refDocId) {
        this.refDocId = refDocId;
    }

    @Override
    public DateTime getNoticeDate() {
        return noticeDate;
    }

    public void setNoticeDate(DateTime noticeDate) {
        this.noticeDate = noticeDate;
    }

    @Override
    public Boolean getIsVarNotice() {
        return isVarNotice;
    }

    public void setIsVarNotice(Boolean isVarNotice) {
        this.isVarNotice = isVarNotice;
    }

    @Override
    public BigDecimal getUnalterableNoticeAmount() {
        return unalterableNoticeAmount;
    }

    public void setUnalterableNoticeAmount(BigDecimal unalterableNoticeAmount) {
        this.unalterableNoticeAmount = unalterableNoticeAmount;
    }

}
