package com.bt.nextgen.service.integration.uar;

import java.util.List;

/**
 * Created by l069679 on 15/06/2016.
 */
public class UarRequestImpl implements UarRequest{
    private String docId;
    private List<UarRecords> uarRecords;
    private String brokerId;

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    @Override
    public void setUarRecords(List<UarRecords> uarRecords) {
        this.uarRecords = uarRecords;

    }

    @Override
    public List<UarRecords> getUarRecords() {
        return this.uarRecords;
    }

    @Override
    public void setBrokerId(String brokerId) {
        this.brokerId = brokerId;
    }

    @Override
    public String getBrokerId() {
        return brokerId;

    }
}
