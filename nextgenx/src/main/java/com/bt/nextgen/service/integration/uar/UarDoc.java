package com.bt.nextgen.service.integration.uar;

import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Created by l069679 on 23/06/2016.
 */
public interface UarDoc {

    public BigDecimal getDocId();
    public void setDocId(BigDecimal docId);
    public BigDecimal getOrderTypeId();
    public void setOrderTypeId(BigDecimal orderTypeId);
    public DateTime getTrxDate();
    public void setTrxDate(DateTime trxDate);
    public BigDecimal getWfcStatus();
    public void setWfcStatus(BigDecimal wfcStatus);
    public BigDecimal getPersonOeId();
    public void setPersonOeId(BigDecimal personOeId);
    public BigDecimal getInsertedByUserId();
    public void setInsertedByUserId(BigDecimal insertedByUserId);
    public DateTime getTimeStamp();
    public void setTimeStamp(DateTime timeStamp);

}
