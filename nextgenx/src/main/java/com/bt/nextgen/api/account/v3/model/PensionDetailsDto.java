package com.bt.nextgen.api.account.v3.model;

import com.bt.nextgen.core.api.model.BaseDto;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Created by L067218 on 10/05/2016.
 */
public class PensionDetailsDto extends BaseDto {
	/**
	 * Flag indicating if pension commencement is currently in progress.
	 */
	private boolean commencementPending;
	private DateTime commencementDate;
	private DateTime accountBalanceDate;
	private boolean pensionReviewInProgress;
	private BigDecimal commencementValue;
	private BigDecimal accountBalance;
	private BigDecimal minAmount;
	private BigDecimal maxAmount;
	private BigDecimal lifeExpectancyCentrelinkSchedule;
	private String pensionType;
	private Integer actualRolloverCount;
	private Integer estimatedRolloverCount;
	private BigDecimal pensionPaidYtd;
	private BigDecimal projectedPensionPayment;
	private BigDecimal taxYtd;
	private BigDecimal usableCash;
	private BigDecimal lumpSumUsableCash;
	private String paymentType;
	private String indexationType;
	private BigDecimal indexationAmount;
	private BigDecimal paymentAmount;
	private DateTime firstPaymentDate;
	private Integer daysToFirstPayment;
	private String paymentFrequency;
	private DateTime nextPaymentDate;
	private BigDecimal nextPaymentAmount;

	public PensionDetailsDto() {
		commencementValue = BigDecimal.ZERO;
		accountBalance = BigDecimal.ZERO;
		minAmount = BigDecimal.ZERO;
		maxAmount = BigDecimal.ZERO;
		lifeExpectancyCentrelinkSchedule = BigDecimal.ZERO;
		taxYtd = BigDecimal.ZERO;
	}

	public boolean isCommencementPending() {
		return commencementPending;
	}

	public void setCommencementPending(boolean commencementPending) {
		this.commencementPending = commencementPending;
	}

	public boolean isPensionReviewInProgress() {
	 return pensionReviewInProgress;
    }

    public void setPensionReviewInProgress(boolean pensionReviewInProgress) {
	 this.pensionReviewInProgress = pensionReviewInProgress;
    }

	public BigDecimal getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(BigDecimal accountBalance) {
		this.accountBalance = accountBalance;
	}

	public DateTime getAccountBalanceDate() {
		return accountBalanceDate;
	}

	public void setAccountBalanceDate(DateTime accountBalanceDate) {
		this.accountBalanceDate = accountBalanceDate;
	}

    public DateTime getCommencementDate() {
        return commencementDate;
    }

    public void setCommencementDate(DateTime commencementDate) {
        this.commencementDate = commencementDate;
    }

	public BigDecimal getCommencementValue() {
		return commencementValue;
	}

	public void setCommencementValue(BigDecimal commencementValue) {
		this.commencementValue = commencementValue;
	}

	public BigDecimal getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}

	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}


	public BigDecimal getLifeExpectancyCentrelinkSchedule() {
		return lifeExpectancyCentrelinkSchedule;
	}

	public void setLifeExpectancyCentrelinkSchedule(BigDecimal lifeExpectancyCentrelinkSchedule) {
		this.lifeExpectancyCentrelinkSchedule = lifeExpectancyCentrelinkSchedule;
	}

	public String getPensionType() {
		return pensionType;
	}

	public void setPensionType(String pensionType) {
		this.pensionType = pensionType;
	}

	public Integer getEstimatedRolloverCount() {
		return estimatedRolloverCount;
	}

	public void setEstimatedRolloverCount(Integer estimatedRolloverCount) {
		this.estimatedRolloverCount = estimatedRolloverCount;
	}

	public Integer getActualRolloverCount() {
		return actualRolloverCount;
	}

	public void setActualRolloverCount(Integer actualRolloverCount) {
		this.actualRolloverCount = actualRolloverCount;
	}

	public BigDecimal getPensionPaidYtd() {
		return pensionPaidYtd;
	}

	public void setPensionPaidYtd(BigDecimal pensionPaidYtd) {
		this.pensionPaidYtd = pensionPaidYtd;
	}

	public BigDecimal getProjectedPensionPayment() {
		return projectedPensionPayment;
	}

	public void setProjectedPensionPayment(BigDecimal projectedPensionPayment) {
		this.projectedPensionPayment = projectedPensionPayment;
	}

	public BigDecimal getTaxYtd() {
		return taxYtd;
	}

	public void setTaxYtd(BigDecimal taxYtd) {
		this.taxYtd = taxYtd;
	}

	public BigDecimal getUsableCash() {
		return usableCash;
	}

	public void setUsableCash(BigDecimal usableCash) {
		this.usableCash = usableCash;
	}

	public BigDecimal getLumpSumUsableCash() {
		return lumpSumUsableCash;
	}

	public void setLumpSumUsableCash(BigDecimal lumpSumUsableCash) {
		this.lumpSumUsableCash = lumpSumUsableCash;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getIndexationType() {
		return indexationType;
	}

	public void setIndexationType(String indexationType) {
		this.indexationType = indexationType;
	}

	public BigDecimal getIndexationAmount() {
		return indexationAmount;
	}

	public void setIndexationAmount(BigDecimal indexationAmount) {
		this.indexationAmount = indexationAmount;
	}

	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public DateTime getFirstPaymentDate() {
		return firstPaymentDate;
	}

	public void setFirstPaymentDate(DateTime firstPaymentDate) {
		this.firstPaymentDate = firstPaymentDate;
	}

	public String getPaymentFrequency() {
		return paymentFrequency;
	}

	public void setPaymentFrequency(String paymentFrequency) {
		this.paymentFrequency = paymentFrequency;
	}

	public DateTime getNextPaymentDate() {
		return nextPaymentDate;
	}

	public void setNextPaymentDate(DateTime nextPaymentDate) {
		this.nextPaymentDate = nextPaymentDate;
	}

	public BigDecimal getNextPaymentAmount() {
		return nextPaymentAmount;
	}

	public void setNextPaymentAmount(BigDecimal nextPaymentAmount) {
		this.nextPaymentAmount = nextPaymentAmount;
	}

	public Integer getDaysToFirstPayment() {
		return daysToFirstPayment;
	}

	public void setDaysToFirstPayment(Integer daysToFirstPayment) {
		this.daysToFirstPayment = daysToFirstPayment;
	}
}
