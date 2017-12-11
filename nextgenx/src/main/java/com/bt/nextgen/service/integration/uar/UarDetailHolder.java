package com.bt.nextgen.service.integration.uar;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by L081012 on 13/01/2016.
 */

@ServiceBean(xpath = "/")
public class UarDetailHolder {

    @ServiceElement(xpath = "//data/doc/val")
    private BigDecimal docID;

    @ServiceElement(xpath = "//data/wfc_status/annot/displ_text")
    private String wfcDisplayText;

    @ServiceElement(xpath = "//data/wfc_status/val")
    private String wfcStatus;

    @ServiceElement(xpath = "//data/UAR_lists/oe_id/val")
    private String oeId;

    @ServiceElement(xpath = "//data/UAR_lists/uar_date/val")
    private Date uarDate;

    @ServiceElementList(xpath = "//data/UAR_lists/dir_job_list/uar_rec", type = UarDetailImpl.class)
    private List<UarDetail> dirUarList;

    @ServiceElementList(xpath = "//data/UAR_lists/indir_job_list/uar_rec", type = UarDetailImpl.class)
    private List<UarDetail> indirectUarList;

    public BigDecimal getDocID() {
        return docID;
    }

    public void setDocID(BigDecimal docID) {
        this.docID = docID;
    }

    public String getWfcDisplayText() {
        return wfcDisplayText;
    }

    public void setWfcDisplayText(String wfcDisplayText) {
        this.wfcDisplayText = wfcDisplayText;
    }

    public String getWfcStatus() {
        return wfcStatus;
    }

    public void setWfcStatus(String wfcStatus) {
        this.wfcStatus = wfcStatus;
    }

    public List<UarDetail> getDirUarList() {
        return dirUarList;
    }

    public void setDirUarList(List<UarDetail> dirUarList) {
        this.dirUarList = dirUarList;
    }

    public List<UarDetail> getIndirectUarList() {
        return indirectUarList;
    }

    public void setIndirectUarList(List<UarDetail> indirectUarList) {
        this.indirectUarList = indirectUarList;
    }

    public String getOeId() {
        return oeId;
    }

    public void setOeId(String oeId) {
        this.oeId = oeId;
    }

    public Date getUarDate() {
        return uarDate;
    }

    public void setUarDate(Date uarDate) {
        this.uarDate = uarDate;
    }
}
