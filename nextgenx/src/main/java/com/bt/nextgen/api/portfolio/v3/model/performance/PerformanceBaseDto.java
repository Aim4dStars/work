package com.bt.nextgen.api.portfolio.v3.model.performance;

import com.bt.nextgen.core.api.model.BaseDto;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class PerformanceBaseDto extends BaseDto {

    private BigDecimal performance;
    private BigDecimal capitalGrowth;
    private BigDecimal incomeRtn;
    private DateTime periodSop;
    private DateTime periodEop;
    private BigDecimal twrrGross;
    private BigDecimal twrrAccum;
    private BigDecimal bmrkRor;
    private BigDecimal activeRor;
    private BigDecimal sopCurrValRef;
    private BigDecimal inflows;
    private BigDecimal outflows;
    private BigDecimal income;
    private BigDecimal expenses;
    private BigDecimal mktMvt;
    private BigDecimal eopBfrFee;
    private BigDecimal fee;
    private BigDecimal otherFee;
    private BigDecimal eopAftFee;
    private BigDecimal netGainLoss;
    private BigDecimal performanceBeforeFee;
    private BigDecimal performanceAfterFee;
    private boolean accountOverviewPerformanceAvailable;

    public BigDecimal getPerformance() {
        return performance;
    }

    public void setPerformance(BigDecimal performance) {
        this.performance = performance;
    }

    public BigDecimal getCapitalGrowth() {
        return capitalGrowth;
    }

    public void setCapitalGrowth(BigDecimal capitalGrowth) {
        this.capitalGrowth = capitalGrowth;
    }

    public BigDecimal getIncomeRtn() {
        return incomeRtn;
    }

    public void setIncomeRtn(BigDecimal incomeRtn) {
        this.incomeRtn = incomeRtn;
    }

    public DateTime getPeriodSop() {
        return periodSop;
    }

    public void setPeriodSop(DateTime periodSop) {
        this.periodSop = periodSop;
    }

    public DateTime getPeriodEop() {
        return periodEop;
    }

    public void setPeriodEop(DateTime periodEop) {
        this.periodEop = periodEop;
    }

    public BigDecimal getTwrrGross() {
        return twrrGross;
    }

    public void setTwrrGross(BigDecimal twrrGross) {
        this.twrrGross = twrrGross;
    }

    public BigDecimal getTwrrAccum() {
        return twrrAccum;
    }

    public void setTwrrAccum(BigDecimal twrrAccum) {
        this.twrrAccum = twrrAccum;
    }

    public BigDecimal getBmrkRor() {
        return bmrkRor;
    }

    public void setBmrkRor(BigDecimal bmrkRor) {
        this.bmrkRor = bmrkRor;
    }

    public BigDecimal getActiveRor() {
        return activeRor;
    }

    public void setActiveRor(BigDecimal activeRor) {
        this.activeRor = activeRor;
    }

    public BigDecimal getSopCurrValRef() {
        return sopCurrValRef;
    }

    public void setSopCurrValRef(BigDecimal sopCurrValRef) {
        this.sopCurrValRef = sopCurrValRef;
    }

    public BigDecimal getInflows() {
        return inflows;
    }

    public void setInflows(BigDecimal inflows) {
        this.inflows = inflows;
    }

    public BigDecimal getOutflows() {
        return outflows;
    }

    public void setOutflows(BigDecimal outflows) {
        this.outflows = outflows;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getExpenses() {
        return expenses;
    }

    public void setExpenses(BigDecimal expenses) {
        this.expenses = expenses;
    }

    public BigDecimal getMktMvt() {
        return mktMvt;
    }

    public void setMktMvt(BigDecimal mktMvt) {
        this.mktMvt = mktMvt;
    }

    public BigDecimal getEopBfrFee() {
        return eopBfrFee;
    }

    public void setEopBfrFee(BigDecimal eopBfrFee) {
        this.eopBfrFee = eopBfrFee;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getEopAftFee() {
        return eopAftFee;
    }

    public void setEopAftFee(BigDecimal eopAftFee) {
        this.eopAftFee = eopAftFee;
    }

    public BigDecimal getNetGainLoss() {
        return netGainLoss;
    }

    public void setNetGainLoss(BigDecimal netGainLoss) {
        this.netGainLoss = netGainLoss;
    }

    public BigDecimal getOtherFee() {
        return otherFee;
    }

    public void setOtherFee(BigDecimal otherFee) {
        this.otherFee = otherFee;
    }

    public BigDecimal getPerformanceBeforeFee() {
        return performanceBeforeFee;
    }

    public void setPerformanceBeforeFee(BigDecimal performanceBeforeFee) {
        this.performanceBeforeFee = performanceBeforeFee;
    }

    public BigDecimal getPerformanceAfterFee() {
        return performanceAfterFee;
    }

    public void setPerformanceAfterFee(BigDecimal performanceAfterFee) {
        this.performanceAfterFee = performanceAfterFee;
    }
    public boolean isAccountOverviewPerformanceAvailable() {
        return accountOverviewPerformanceAvailable;
    }

    public void setAccountOverviewPerformanceAvailable(boolean accountOverviewPerformanceAvailable) {
        this.accountOverviewPerformanceAvailable = accountOverviewPerformanceAvailable;
    }


}
