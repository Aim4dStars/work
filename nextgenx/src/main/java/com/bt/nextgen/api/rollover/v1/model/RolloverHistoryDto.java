package com.bt.nextgen.api.rollover.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.integration.rollover.RolloverHistory;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class RolloverHistoryDto extends BaseDto {

    private String rolloverId;
    private String fundName;
    private String fundAbn;
    private String fundUsi;
    private String fundMemberId;
    private DateTime dateRequested;
    private String requestStatus;
    private BigDecimal amount;
    private String rolloverOption;
    private String rolloverType;
    private Boolean initiatedByPanorama;

    public RolloverHistoryDto(RolloverHistory history) {
        super();

        this.rolloverId = history.getRolloverId();
        this.fundName = history.getFundName();
        this.fundAbn = history.getFundAbn();
        this.fundUsi = history.getFundUsi();
        this.fundMemberId = history.getFundMemberId();
        this.dateRequested = history.getDateRequested();
        this.requestStatus = history.getRequestStatus() == null ? null : history.getRequestStatus().name();
        this.amount = history.getAmount();
        this.rolloverOption = history.getRolloverOption() == null ? null : history.getRolloverOption().getShortDisplayName();
        this.rolloverType = history.getRolloverType() == null ? null : history.getRolloverType().getShortDisplayName();
        this.initiatedByPanorama = history.getInitiatedByPanorama();
    }

    public String getRolloverId() {
        return rolloverId;
    }

    public String getFundName() {
        return fundName;
    }

    public String getFundAbn() {
        return fundAbn;
    }

    public String getFundUsi() {
        return fundUsi;
    }

    public String getFundMemberId() {
        return fundMemberId;
    }

    public DateTime getDateRequested() {
        return dateRequested;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getRolloverOption() {
        return rolloverOption;
    }

    public String getRolloverType() {
        return rolloverType;
    }

    public Boolean getInitiatedByPanorama() {
        return initiatedByPanorama;
    }
}
