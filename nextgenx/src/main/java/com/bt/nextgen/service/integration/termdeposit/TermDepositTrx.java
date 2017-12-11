package com.bt.nextgen.service.integration.termdeposit;

import com.bt.nextgen.service.ServiceErrors;

import java.math.BigDecimal;

public interface TermDepositTrx
{
    public BigDecimal getCurrPrpl();
    public void setCurrPrpl(BigDecimal currPrpl);
	void setPercentTermElapsed(BigDecimal percentTermElapsed);
    BigDecimal getPercentTermElapsed();
    public String getMaturityDate();
    public void setMaturityDate(String maturityDate);
    public BigDecimal getInterestPaid();
    public void setInterestPaid(BigDecimal interestPaid);
    public BigDecimal getInterestAccrued();
    public void setInterestAccrued(BigDecimal interestAccrued);
    public BigDecimal getInterestRate();
    public void setInterestRate(BigDecimal interestRate);
    public BigDecimal getWithdrawNet();
    public void setWithdrawNet(BigDecimal withdrawNet);
    public BigDecimal getWithdrawInterestPaid();
    public void setWithdrawInterestPaid(BigDecimal withdrawInterestPaid);
    public String getWithdrawDate();
    public void setWithdrawDate(String withdrawDate);
    public BigDecimal getAdjustedInterestRate();
    public void setAdjustedInterestRate(BigDecimal adjustedInterestRate);
    public BigDecimal getDaysUntilMaturity();
    public void setDaysUntilMaturity(BigDecimal daysUntilMaturity);
    public String getOpenDate();
    public void setOpenDate(String openDate);
    public BigDecimal getWidrwPrpl();
    public void setWidrwPrpl(BigDecimal widrwPrpl);
    public BigDecimal getQty();
    public void setQty(BigDecimal qty);
    public String getNoticeEndDate();
    public void setNoticeEndDate(String noticeEndDate);
}
