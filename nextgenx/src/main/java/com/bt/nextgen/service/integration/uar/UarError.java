package com.bt.nextgen.service.integration.uar;

import java.math.BigDecimal;

/**
 * Created by l069679 on 8/07/2016.
 */
public interface UarError {

    public BigDecimal getId();
    public void setId(BigDecimal id);
    public String getType();
    public void setType(String type);
    public String getTypeId();
    public void setTypeId(String typeId);
    public String getMessage();
    public void setMessage(String message);
}
