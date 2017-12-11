package com.bt.nextgen.api.account.v3.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO instance for the cash sweep details
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ApiModel(value = "Account cash sweep details")
public class AccountCashSweepDto extends BaseDto implements KeyedDto<AccountKey> {

    @ApiModelProperty(value = "Account key")
    private AccountKey key;

    @ApiModelProperty(value = "Minimum cash sweep balance to maintain for the account")
    private BigDecimal minCashSweepAmount;

    @ApiModelProperty(value = "Minimum cash balance for the account (product level)")
    private BigDecimal minCashAmount;

    @ApiModelProperty(value = "Enable/disable cash sweep flag")
    private Boolean cashSweepAllowed;

    @ApiModelProperty(value = "List of investment assets")
    private List<CashSweepInvestmentDto> cashSweepInvestments;

    @ApiModelProperty(value = "Error to display for UI")
    private DomainApiErrorDto error;

    public AccountCashSweepDto(AccountKey key, BigDecimal minCashAmount, Boolean cashSweepAllowed,
                               BigDecimal minCashSweepAmount, List<CashSweepInvestmentDto> cashSweepInvestments) {
        this.key = key;
        this.cashSweepAllowed = cashSweepAllowed;
        this.minCashAmount = minCashAmount;
        this.minCashSweepAmount = minCashSweepAmount;
        this.cashSweepInvestments = cashSweepInvestments;
    }

    public AccountCashSweepDto(AccountKey key) {
        this.key = key;
    }

    @Override
    public AccountKey getKey() {
        return key;
    }

    public Boolean isCashSweepAllowed() {
        return cashSweepAllowed;
    }

    public void setCashSweepAllowed(Boolean cashSweepAllowed) {
        this.cashSweepAllowed = cashSweepAllowed;
    }

    public BigDecimal getMinCashSweepAmount() {
        return minCashSweepAmount;
    }

    public void setMinCashSweepAmount(BigDecimal minCashSweepAmount) {
        this.minCashSweepAmount = minCashSweepAmount;
    }

    public BigDecimal getMinCashAmount() {
        return minCashAmount;
    }

    public void setMinCashAmount(BigDecimal minCashAmount) {
        this.minCashAmount = minCashAmount;
    }

    public List<CashSweepInvestmentDto> getCashSweepInvestments() {
        return cashSweepInvestments;
    }

    public DomainApiErrorDto getError() {
        return error;
    }

    public void setError(DomainApiErrorDto error) {
        this.error = error;
    }

    public void setCashSweepInvestments(List<CashSweepInvestmentDto> cashSweepInvestments) {
        this.cashSweepInvestments = cashSweepInvestments;
    }
}
