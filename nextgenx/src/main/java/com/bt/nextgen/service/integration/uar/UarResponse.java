package com.bt.nextgen.service.integration.uar;



import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by l069679 on 15/06/2016.
 */
public interface UarResponse {

    public BigDecimal getDocId();
    public void setDocId(BigDecimal docId);
    public String getWfcDisplayText();
    public void setWfcDisplayText(String wfcDisplayText);
    public String getWfcStatus();
    public void setWfcStatus(String wfcStatus);
    public List<UarRecords> getUarRecords();
    public void setUarRecords(List<UarRecords> uarRecords);
    public DateTime getUarDate();
    public void setUarDate(DateTime uarDate);
    public String getBrokerId();
    public void setBrokerId(String brokerId);
}
