package com.bt.nextgen.api.rollover.v1.model;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;
import com.fasterxml.jackson.annotation.JsonView;
import org.joda.time.DateTime;

public class SuperfundDto extends BaseDto {

    @JsonView(JsonViews.Write.class)
    private String usi;

    private DateTime validFrom;

    private DateTime validTo;

    private String active;

    private String abn;

    private String orgName;

    private String productName;

    public SuperfundDto() {
        super();
    }

    public SuperfundDto(String usi, DateTime validFrom, DateTime validTo, String abn, String orgName, String productName) {
        this.usi = usi;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.abn = abn;
        this.orgName = orgName;
        this.productName = productName;
    }

    public String getUsi() {
        return usi;
    }

    public void setUsi(String usi) {
        this.usi = usi;
    }

    public DateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(DateTime validFrom) {
        this.validFrom = validFrom;
    }

    public DateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(DateTime validTo) {
        this.validTo = validTo;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getAbn() {
        return abn;
    }

    public void setAbn(String abn) {
        this.abn = abn;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

}
