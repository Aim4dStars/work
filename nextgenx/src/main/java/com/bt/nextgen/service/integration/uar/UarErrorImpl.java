package com.bt.nextgen.service.integration.uar;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;

import java.math.BigDecimal;

/**
 * Created by l069679 on 8/07/2016.
 */

@ServiceBean(xpath = "err_rec")
public class UarErrorImpl implements  UarError {

    @ServiceElement(xpath="err_id/val")
    private BigDecimal id;

    @ServiceElement(xpath="err_type_id/annot/displ_text")
    private String type;

    @ServiceElement(xpath="err_type_id/annot/ctx/id")
    private String typeId;

    @ServiceElement(xpath="err_msg/val")
    private String message;

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
