package com.bt.nextgen.service.avaloq.rollover;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.rollover.RolloverHistory;
import com.bt.nextgen.service.integration.rollover.RolloverOption;
import com.bt.nextgen.service.integration.rollover.RolloverStatus;
import com.bt.nextgen.service.integration.rollover.RolloverType;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import org.joda.time.DateTime;

import java.math.BigDecimal;

@ServiceBean(xpath = "dtm")
public class RolloverHistoryImpl implements RolloverHistory {

    public static final String XML_HEADER = "./dtm_head_list/dtm_head/";

    @ServiceElement(xpath = XML_HEADER + "order_id/val")
    private String rolloverId;

    @ServiceElement(xpath = XML_HEADER + "fund_name/val")
    private String fundName;

    @ServiceElement(xpath = XML_HEADER + "fund_abn/val")
    private String fundAbn;

    @ServiceElement(xpath = XML_HEADER + "fund_usi/val")
    private String fundUsi;

    @ServiceElement(xpath = XML_HEADER + "mbr_acc_nr/val")
    private String fundMemberId;

    @ServiceElement(xpath = XML_HEADER + "ss_req_dt/val", converter = DateTimeTypeConverter.class)
    private DateTime dateRequested;

    @ServiceElement(xpath = XML_HEADER + "req_status/val", staticCodeCategory = "ROLLOVER_STATUS")
    private RolloverStatus requestStatus;

    @ServiceElement(xpath = XML_HEADER + "fund_estim_amt/val")
    private BigDecimal amount;

    @ServiceElement(xpath = XML_HEADER + "rlov_opt/val", staticCodeCategory = "ROLLOVER_OPTION")
    private RolloverOption rolloverOption;

    @ServiceElement(xpath = XML_HEADER + "rlov_type/val", staticCodeCategory = "ROLLOVER_TYPE")
    private RolloverType rolloverType;

    @ServiceElement(xpath = XML_HEADER + "is_panorama_init/val")
    private Boolean initiatedByPanorama;

    public RolloverHistoryImpl() {
        super();
    }

    public String getRolloverId() {
        return rolloverId;
    }

    public void setRolloverId(String rolloverId) {
        this.rolloverId = rolloverId;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getFundAbn() {
        return fundAbn;
    }

    public void setFundAbn(String fundAbn) {
        this.fundAbn = fundAbn;
    }

    public String getFundUsi() {
        return fundUsi;
    }

    public void setFundUsi(String fundUsi) {
        this.fundUsi = fundUsi;
    }

    public String getFundMemberId() {
        return fundMemberId;
    }

    public void setFundMemberId(String fundMemberId) {
        this.fundMemberId = fundMemberId;
    }

    public DateTime getDateRequested() {
        return dateRequested;
    }

    public void setDateRequested(DateTime dateRequested) {
        this.dateRequested = dateRequested;
    }

    public RolloverStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RolloverStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public RolloverOption getRolloverOption() {
        return rolloverOption;
    }

    public void setRolloverOption(RolloverOption rolloverOption) {
        this.rolloverOption = rolloverOption;
    }

    public RolloverType getRolloverType() {
        return rolloverType;
    }

    public void setRolloverType(RolloverType rolloverType) {
        this.rolloverType = rolloverType;
    }

    public Boolean getInitiatedByPanorama() {
        return initiatedByPanorama;
    }

    public void setInitiatedByPanorama(Boolean initiatedByPanorama) {
        this.initiatedByPanorama = initiatedByPanorama;
    }

    @Override
    public String getFundId() {
        return this.fundMemberId;
    }

}
