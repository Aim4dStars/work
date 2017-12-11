package com.bt.nextgen.api.regularinvestment.v2.model;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.account.v2.model.BankAccountDto;
import com.bt.nextgen.api.movemoney.v2.model.DepositDto;
import com.bt.nextgen.api.order.model.OrderGroupDto;
import com.bt.nextgen.api.order.model.OrderGroupKey;
import com.bt.nextgen.api.order.model.OrderItemDto;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.fasterxml.jackson.annotation.JsonView;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public class RegularInvestmentDto extends OrderGroupDto implements KeyedDto<OrderGroupKey> {

    @JsonView(JsonViews.Write.class)
    private DateTime investmentStartDate;

    /**
     * The last scheduled transaction date for this regular investment. This could be different from the last deposit/withdrawal
     * from the specified Direct Debit account.
     */
    @JsonView(JsonViews.Write.class)
    private DateTime investmentEndDate;

    @JsonView(JsonViews.Write.class)
    private String frequency;

    /**
     * Details of linked accounts.
     */
    @JsonView(JsonViews.Write.class)
    private DepositDto depositDetails;

    @JsonView(JsonViews.Write.class)
    private String ripStatus;

    private BigDecimal investmentAmount;
    private DateTime nextDueDate;
    private String lastExecutionStatus;
    private BankAccountDto cashAccountDto;
    private String firstNotification;

    public RegularInvestmentDto() {
        super();
    }

    public RegularInvestmentDto(OrderGroupDto orderGroupDto, DepositDto depositDetails, InvestmentPeriodDto invPeriod,
            String ripStatus, BankAccountDto cashAccountDto) {

        this(orderGroupDto);
        this.depositDetails = depositDetails;
        this.ripStatus = ripStatus;
        this.setInvestmentPeriod(invPeriod);
        this.cashAccountDto = cashAccountDto;
        this.firstNotification = orderGroupDto.getFirstNotification();
    }

    public RegularInvestmentDto(OrderGroupDto orderGroupDto, DepositDto depositDetails, InvestmentPeriodDto invPeriod,
            BigDecimal investmentAmount, String lastExecutionStatus, String ripStatus, BankAccountDto cashAccountDto) {

        this(orderGroupDto);
        this.depositDetails = depositDetails;
        this.investmentAmount = investmentAmount;
        this.lastExecutionStatus = lastExecutionStatus;
        this.setInvestmentPeriod(invPeriod);
        this.cashAccountDto = cashAccountDto;
        this.ripStatus = ripStatus;
        this.firstNotification = orderGroupDto.getFirstNotification();

    }

    protected RegularInvestmentDto(OrderGroupDto orderGroupDto) {
        super(orderGroupDto.getKey(), orderGroupDto.getLastUpdateDate(), orderGroupDto.getTransactionSeq(), orderGroupDto
                .getOrders(), orderGroupDto.getWarnings(), orderGroupDto.getOwner(), orderGroupDto.getOwnerName(), orderGroupDto
                .getReference(), orderGroupDto.getAccountName(), orderGroupDto.getAccountKey());
        this.firstNotification = orderGroupDto.getFirstNotification();
    }

    private void setInvestmentPeriod(InvestmentPeriodDto invPeriod) {
        this.investmentEndDate = invPeriod.getInvestmentEndDate();
        this.investmentStartDate = invPeriod.getInvestmentStartDate();
        this.nextDueDate = invPeriod.getNextDueDate();
        this.frequency = invPeriod.getFrequency();
    }

    public String getRipStatus() {
        return ripStatus;
    }

    public DateTime getNextDueDate() {
        return nextDueDate;
    }

    public DepositDto getDepositDetails() {
        return depositDetails;
    }

    public DateTime getInvestmentEndDate() {
        return investmentEndDate;
    }

    public DateTime getInvestmentStartDate() {
        return investmentStartDate;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getLastExecutionStatus() {
        return lastExecutionStatus;
    }

    public BigDecimal getInvestmentAmount() {
        if (investmentAmount == null) {
            List<OrderItemDto> orderItems = this.getOrders();
            investmentAmount = Lambda.sum(orderItems, Lambda.on(OrderItemDto.class).getAmount());
        }

        return investmentAmount;
    }

    public BankAccountDto getCashAccountDto() {
        return cashAccountDto;
    }

    public void setCashAccountDto(BankAccountDto cashAccountDto) {
        this.cashAccountDto = cashAccountDto;
    }

    public String getFirstNotification() {
        return firstNotification;
    }

    public void setFirstNotification(String firstNotification) {
        this.firstNotification = firstNotification;
    }
}
