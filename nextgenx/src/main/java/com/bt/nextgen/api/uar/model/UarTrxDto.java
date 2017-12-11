package com.bt.nextgen.api.uar.model;



import java.math.BigDecimal;
import java.util.List;

/**
 * Created by l081361 on 9/08/2016.
 */
public class UarTrxDto {
    private String key;
    private List<UarDto> uarComponent;
    private BigDecimal docId;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<UarDto> getUarComponent() {
        return uarComponent;
    }

    public void setUarComponent(List<UarDto> uarComponent) {
        this.uarComponent = uarComponent;
    }

    public BigDecimal getDocId() {
        return docId;
    }

    public void setDocId(BigDecimal docId) {
        this.docId = docId;
    }
}
