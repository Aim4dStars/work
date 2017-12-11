package com.bt.nextgen.api.rollover.v1.model;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.service.integration.rollover.RolloverDetails;
import com.fasterxml.jackson.annotation.JsonView;

import java.math.BigDecimal;
import java.util.List;

public class RolloverDetailsDto extends RolloverFundDto {
    @JsonView(JsonViews.Write.class)
    private BigDecimal fundAmount;

    @JsonView(JsonViews.Write.class)
    private Boolean panInitiated;

    @JsonView(JsonViews.Write.class)
    private String accountName;

    @JsonView(JsonViews.Write.class)
    private String rolloverOption;

    @JsonView(JsonViews.Write.class)
    private Boolean includeInsurance;

    private String lastTransSeqId;
    private List<DomainApiErrorDto> warnings;

    /**
     * Default constructor for JSON object mapper
     */
    public RolloverDetailsDto() {
        super();
    }

    public RolloverDetailsDto(String accountId, RolloverDetails rollover) {

        super(accountId, rollover.getFundId(), rollover.getFundName(), rollover.getFundAbn(), rollover.getFundUsi(), rollover
                .getRolloverType() == null ? null : rollover.getRolloverType().getDisplayName(), rollover.getAmount());

        this.fundAmount = rollover.getAmount();
        this.panInitiated = rollover.getPanInitiated();
        this.accountName = rollover.getAccountNumber();
        this.rolloverOption = rollover.getRolloverOption() == null ? null : rollover.getRolloverOption().getDisplayName();
        this.includeInsurance = rollover.getIncludeInsurance();
        this.lastTransSeqId = rollover.getLastTransSeqId();
    }

    public BigDecimal getFundAmount() {
        return fundAmount;
    }

    public Boolean getPanInitiated() {
        return panInitiated;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getRolloverOption() {
        return rolloverOption;
    }

    public Boolean getIncludeInsurance() {
        return includeInsurance;
    }

    @Override
    public String getType() {
        return RolloverDetailsDto.class.getName();
    }

    @Override
    public BigDecimal getAmount() {
        return this.fundAmount;
    }

    public String getLastTransSeqId() {
        return lastTransSeqId;
    }

    public List<DomainApiErrorDto> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<DomainApiErrorDto> warnings) {
        this.warnings = warnings;
    }

}
