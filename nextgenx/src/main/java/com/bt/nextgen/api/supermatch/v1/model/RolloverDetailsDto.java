package com.bt.nextgen.api.supermatch.v1.model;

import com.bt.nextgen.config.JsonViews;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Rollover details for the fund
 */
@ApiModel("RollOverDetails")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RolloverDetailsDto {

    @ApiModelProperty("Rollover id for the partial rollover fund")
    private String rolloverId;

    @ApiModelProperty("Is fund rollover triggered")
    private Boolean rolloverStatus;

    @ApiModelProperty("Rollover amount for the partial rollover fund")
    @JsonView(JsonViews.Write.class)
    private BigDecimal rolloverAmount;

    @ApiModelProperty("Timestamp when the last rollover details were provided")
    private DateTime rolloverProvidedTime;

    @ApiModelProperty("Timestamp when the last rollover details were provided")
    private String rolloverFundCategory;

    public Boolean getRolloverStatus() {
        return rolloverStatus;
    }

    public void setRolloverStatus(Boolean rolloverStatus) {
        this.rolloverStatus = rolloverStatus;
    }

    public String getRolloverId() {
        return rolloverId;
    }

    public void setRolloverId(String rolloverId) {
        this.rolloverId = rolloverId;
    }

    public BigDecimal getRolloverAmount() {
        return rolloverAmount;
    }

    public void setRolloverAmount(BigDecimal rolloverAmount) {
        this.rolloverAmount = rolloverAmount;
    }

    public DateTime getRolloverProvidedTime() {
        return rolloverProvidedTime;
    }

    public void setRolloverProvidedTime(DateTime rolloverProvidedTime) {
        this.rolloverProvidedTime = rolloverProvidedTime;
    }

    public String getRolloverFundCategory() {
        return rolloverFundCategory;
    }

    public void setRolloverFundCategory(String rolloverFundCategory) {
        this.rolloverFundCategory = rolloverFundCategory;
    }
}
