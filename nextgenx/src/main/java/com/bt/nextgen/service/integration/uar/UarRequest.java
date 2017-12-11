package com.bt.nextgen.service.integration.uar;

import java.util.List;

/**
 * Created by l069679 on 15/06/2016.
 */
public interface UarRequest {
    public String getDocId();
    public void setDocId(String docId);
    public void setUarRecords(List<UarRecords> uarRecords);
    public List<UarRecords> getUarRecords();
    public void setBrokerId(String brokerId);
    public String getBrokerId();
}
