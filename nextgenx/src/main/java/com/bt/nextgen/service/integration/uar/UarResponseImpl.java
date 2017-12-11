package com.bt.nextgen.service.integration.uar;



import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by l069679 on 15/06/2016.
 */
public class UarResponseImpl implements UarResponse{
    private BigDecimal docId;
    private String wfcDisplayText;
    private String wfcStatus;
    private String brokerId;
    private DateTime uarDate;
    private List<UarRecords> uarRecords;


    public BigDecimal getDocId() {
        return docId;
    }

    public void setDocId(BigDecimal docId) {
        this.docId = docId;
    }

    public String getWfcDisplayText() {
        return wfcDisplayText;
    }

    public void setWfcDisplayText(String wfcDisplayText) {
        this.wfcDisplayText = wfcDisplayText;
    }

    public String getWfcStatus() {
        return wfcStatus;
    }

    public void setWfcStatus(String wfcStatus) {
        this.wfcStatus = wfcStatus;
    }

    public List<UarRecords> getUarRecords() {
        return uarRecords;
    }

    public void setUarRecords(List<UarRecords> uarRecords) {
        this.uarRecords = uarRecords;
    }

    public DateTime getUarDate() {
        return uarDate;
    }

    public void setUarDate(DateTime uarDate) {
        this.uarDate = uarDate;
    }

    public String getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(String brokerId) {
        this.brokerId = brokerId;
    }
}
