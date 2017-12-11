package com.bt.nextgen.api.termdeposit.model;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.ServiceError;

import java.math.BigDecimal;
import java.util.List;

public class TermDepositDetailDto extends BaseDto implements KeyedDto<AccountKey>
{

    private AccountKey key;

    private String tdAccountId;
	private String brandLogoUrl;
	private BigDecimal investmentAmount;
    private BigDecimal daysLeft;
    private BigDecimal percentageTermElapsed;
    private String maturityDate;
    private BigDecimal interestPaid;
    private BigDecimal interestAccrued;
    private BigDecimal interestRate;
    private BigDecimal withdrawNet;
    private BigDecimal adjustedInterestAmt;
    private String withdrawDate;
    private BigDecimal adjustedInterestRate;
    private String openDate;
    private boolean errorOccurred;
    private List<ServiceError> errors;
    private String avaloqDate;
    private String noticeEndDate;


	public String getBrandLogoUrl()
	{
		return brandLogoUrl;
	}

	public void setBrandLogoUrl(String brandLogoUrl)
	{
		this.brandLogoUrl = brandLogoUrl;
	}

	public BigDecimal getInvestmentAmount()
	{
		return investmentAmount;
	}

	public void setInvestmentAmount(BigDecimal investmentAmount)
	{
		this.investmentAmount = investmentAmount;
	}

	public String getMaturityDate()
	{
		return maturityDate;
	}

	public void setMaturityDate(String maturityDate)
	{
		this.maturityDate = maturityDate;
	}

    public void setKey(AccountKey key)
	{
		this.key = key;
	}

	@Override
    public AccountKey getKey()
	{
		return key;
	}

    public String getTdAccountId() {
        return tdAccountId;
    }

    public void setTdAccountId(String tdAccountId) {
        this.tdAccountId = tdAccountId;
    }

    public BigDecimal getDaysLeft() {
        return daysLeft;
    }

    public void setDaysLeft(BigDecimal daysLeft) {
        this.daysLeft = daysLeft;
    }

    public BigDecimal getPercentageTermElapsed() {
        return percentageTermElapsed;
    }

    public void setPercentageTermElapsed(BigDecimal percentageTermElapsed) {
        this.percentageTermElapsed = percentageTermElapsed;
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

    public BigDecimal getAdjustedInterestAmt() {
        return adjustedInterestAmt;
    }

    public void setAdjustedInterestAmt(BigDecimal adjustedInterestAmt) {
        this.adjustedInterestAmt = adjustedInterestAmt;
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

    public String getOpenDate() {
        return openDate;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public boolean isErrorOccurred() {
        return errorOccurred;
    }

    public void setErrorOccurred(boolean errorOccurred) {
        this.errorOccurred = errorOccurred;
    }

    public List<ServiceError> getErrors() {
        return errors;
    }

    public void setErrors(List<ServiceError> errors) {
        this.errors = errors;
    }

    public String getAvaloqDate() {
        return avaloqDate;
    }

    public void setAvaloqDate(String avaloqDate) {
        this.avaloqDate = avaloqDate;
    }

    public String getNoticeEndDate() {
        return noticeEndDate;
    }

    public void setNoticeEndDate(String noticeEndDate) {
        this.noticeEndDate = noticeEndDate;
    }
}
