package com.bt.nextgen.service.integration.termdeposit;

import com.bt.nextgen.service.ServiceErrors;

import java.math.BigDecimal;

public class TermDepositTrxImpl implements TermDepositTrx
{

	private BigDecimal currPrpl;
    private BigDecimal widrwPrpl;
    private BigDecimal qty;
    private BigDecimal daysUntilMaturity;
	private BigDecimal percentTermElapsed;
    private String maturityDate;
    private BigDecimal interestPaid;
    private BigDecimal interestAccrued;
    private BigDecimal interestRate;
    private BigDecimal withdrawNet;
    private BigDecimal withdrawInterestPaid;
    private String withdrawDate;
    private BigDecimal adjustedInterestRate;
    private String openDate;
    private String noticeEndDate;


    public BigDecimal getCurrPrpl() {
        return currPrpl;
    }

    public void setCurrPrpl(BigDecimal currPrpl) {
        this.currPrpl = currPrpl;
    }

    public BigDecimal getPercentTermElapsed()
	{
		return percentTermElapsed;
	}
	public void setPercentTermElapsed(BigDecimal percentTermElapsed)
	{
		this.percentTermElapsed = percentTermElapsed;
	}

    public String getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(String maturityDate) {
        this.maturityDate = maturityDate;
    }

    public BigDecimal getInterestPaid() {
        return interestPaid;
    }

    public void setInterestPaid(BigDecimal interestPaid) {
        this.interestPaid = interestPaid;
    }

    public BigDecimal getInterestAccrued() {
        return interestAccrued;
    }

    public void setInterestAccrued(BigDecimal interestAccrued) {
        this.interestAccrued = interestAccrued;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getWithdrawNet() {
        return withdrawNet;
    }

    public void setWithdrawNet(BigDecimal withdrawNet) {
        this.withdrawNet = withdrawNet;
    }

    public BigDecimal getWithdrawInterestPaid() {
        return withdrawInterestPaid;
    }

    public void setWithdrawInterestPaid(BigDecimal withdrawInterestPaid) {
        this.withdrawInterestPaid = withdrawInterestPaid;
    }

    public String getWithdrawDate() {
        return withdrawDate;
    }

    public void setWithdrawDate(String withdrawDate) {
        this.withdrawDate = withdrawDate;
    }

    public BigDecimal getAdjustedInterestRate() {
        return adjustedInterestRate;
    }

    public void setAdjustedInterestRate(BigDecimal adjustedInterestRate) {
        this.adjustedInterestRate = adjustedInterestRate;
    }

    public BigDecimal getDaysUntilMaturity() {
        return daysUntilMaturity;
    }

    public void setDaysUntilMaturity(BigDecimal daysUntilMaturity) {
        this.daysUntilMaturity = daysUntilMaturity;
    }

    public String getOpenDate() {
        return openDate;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public BigDecimal getWidrwPrpl() {
        return widrwPrpl;
    }

    public void setWidrwPrpl(BigDecimal widrwPrpl) {
        this.widrwPrpl = widrwPrpl;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    public String getNoticeEndDate() {
        return noticeEndDate;
    }

    public void setNoticeEndDate(String noticeEndDate) {
        this.noticeEndDate = noticeEndDate;
    }
}
