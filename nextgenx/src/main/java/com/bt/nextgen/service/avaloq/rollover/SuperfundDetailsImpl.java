package com.bt.nextgen.service.avaloq.rollover;

import com.bt.nextgen.core.cache.cachesearch.Indexed;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.externalasset.builder.IsoDateTimeConverter;
import com.bt.nextgen.service.integration.rollover.SuperfundDetails;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

@ServiceBean(xpath = "d")
public class SuperfundDetailsImpl implements SuperfundDetails {
    public static final String XML_HEADER = "d_head_list/d_head/";

    @Indexed
    @NotNull
    @ServiceElement(xpath = XML_HEADER + "usi/val")
    private String usi;

    @ServiceElement(xpath = XML_HEADER + "valid_from/val", converter = IsoDateTimeConverter.class)
    private DateTime validFrom;

    @ServiceElement(xpath = XML_HEADER + "valid_to", converter = IsoDateTimeConverter.class)
    private DateTime validTo;

    @ServiceElement(xpath = XML_HEADER + "activ/val")
    private String active;

    @ServiceElement(xpath = XML_HEADER + "org_abn/val")
    private String abn;

    @ServiceElement(xpath = XML_HEADER + "org_name/val")
    private String orgName;

    @ServiceElement(xpath = XML_HEADER + "prod_name/val")
    private String productName;

    public SuperfundDetailsImpl() {
        super();
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
