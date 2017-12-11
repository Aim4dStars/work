package com.bt.nextgen.service.integration.uar;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import org.joda.time.DateTime;


import java.math.BigDecimal;

/**
 * Created by l069679 on 23/06/2016.
 */

@ServiceBean(xpath = "/")
public class UarDocImpl implements UarDoc {

    @ServiceElement(xpath="//data/doc_list/doc/doc_head_list/doc_head/doc_id/val")
    private BigDecimal docId;

    @ServiceElement(xpath = "//data/doc_list/doc/doc_head_list/doc_head/order_type_id/val")
    private BigDecimal orderTypeId;

    @ServiceElement(xpath="//data/doc_list/doc/doc_head_list/doc_head/trx_date/val")
    private DateTime trxDate;

    @ServiceElement(xpath = "//data/doc_list/doc/doc_head_list/doc_head/wfc_status_id/val")
    private BigDecimal wfcStatus;

    @ServiceElement(xpath = "//data/doc_list/doc/doc_head_list/doc_head/person_oe_id/val")
    private BigDecimal personOeId;

    @ServiceElement(xpath = "//data/doc_list/doc/doc_head_list/doc_head/ins_by_sec_user_id/val")
    private BigDecimal insertedByUserId;

    @ServiceElement(xpath = "//data/doc_list/doc/doc_head_list/doc_head/timestamp/val")
    private DateTime timeStamp;

    public BigDecimal getDocId() {
        return docId;
    }

    public void setDocId(BigDecimal docId) {
        this.docId = docId;
    }

    public BigDecimal getOrderTypeId() {
        return orderTypeId;
    }

    public void setOrderTypeId(BigDecimal orderTypeId) {
        this.orderTypeId = orderTypeId;
    }

    public DateTime getTrxDate() {
        return trxDate;
    }

    public void setTrxDate(DateTime trxDate) {
        this.trxDate = trxDate;
    }

    public BigDecimal getWfcStatus() {
        return wfcStatus;
    }

    public void setWfcStatus(BigDecimal wfcStatus) {
        this.wfcStatus = wfcStatus;
    }

    public BigDecimal getPersonOeId() {
        return personOeId;
    }

    public void setPersonOeId(BigDecimal personOeId) {
        this.personOeId = personOeId;
    }

    public BigDecimal getInsertedByUserId() {
        return insertedByUserId;
    }

    public void setInsertedByUserId(BigDecimal insertedByUserId) {
        this.insertedByUserId = insertedByUserId;
    }

    public DateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(DateTime timeStamp) {
        this.timeStamp = timeStamp;
    }
}
