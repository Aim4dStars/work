package com.bt.nextgen.api.superpersonaltaxdeduction.model;

import com.bt.nextgen.core.api.model.BaseDto;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Created by L067218 on 12/10/2016.
 */
public class NoticeDto extends BaseDto {

    private DateTime noticeDate;
    private String noticeType;
    private BigDecimal noticeAmount;
    private BigDecimal unalterableNoticeAmount;
    private String docId;
    private String originalDocId;

    public String getOriginalDocId() {
        return originalDocId;
    }

    public void setOriginalDocId(String originalDocId) {
        this.originalDocId = originalDocId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public DateTime getNoticeDate() {
        return noticeDate;
    }

    public void setNoticeDate(DateTime noticeDate) {
        this.noticeDate = noticeDate;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public BigDecimal getNoticeAmount() {
        return noticeAmount;
    }

    public void setNoticeAmount(BigDecimal noticeAmount) {
        this.noticeAmount = noticeAmount;
    }

    public BigDecimal getUnalterableNoticeAmount() {
        return unalterableNoticeAmount;
    }

    public void setUnalterableNoticeAmount(BigDecimal unalterableNoticeAmount) {
        this.unalterableNoticeAmount = unalterableNoticeAmount;
    }

}
