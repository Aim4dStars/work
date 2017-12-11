package com.bt.nextgen.api.account.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * @deprecated Use V2
 */
@Deprecated
public class PerformanceDto extends BaseDto implements KeyedDto<AccountKey> {
    private AccountKey key;
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
    private BigDecimal eopAftFee;
    private BigDecimal netGainLoss;

    public PerformanceDto() {
        super();
    }

    // Resolved number of method arguments in v2.
    @SuppressWarnings({ "squid:S00107" })
    public PerformanceDto(AccountKey key, BigDecimal performance, BigDecimal capitalGrowth, BigDecimal incomeRtn,
            BigDecimal bmrkRor, BigDecimal activeRor, BigDecimal sopCurrValRef, DateTime periodSop, DateTime periodEop,
            BigDecimal inflows, BigDecimal outflows, BigDecimal income, BigDecimal expenses, BigDecimal mktMvt,
            BigDecimal eopBfrFee, BigDecimal fee, BigDecimal eopAftFee, BigDecimal netGainLoss, BigDecimal twrrGross,
            BigDecimal twrrAccum) {
        this.key = key;
        this.performance = performance;
        this.capitalGrowth = capitalGrowth;
        this.incomeRtn = incomeRtn;
        this.bmrkRor = bmrkRor;
        this.activeRor = activeRor;
        this.sopCurrValRef = sopCurrValRef;
        this.periodSop = periodSop;
        this.periodEop = periodEop;
        this.inflows = inflows;
        this.outflows = outflows;
        this.income = income;
        this.expenses = expenses;
        this.mktMvt = mktMvt;
        this.eopBfrFee = eopBfrFee;
        this.fee = fee;
        this.eopAftFee = eopAftFee;
        this.netGainLoss = netGainLoss;
        this.twrrGross = twrrGross;
        this.twrrAccum = twrrAccum;
    }

    @Override
    public AccountKey getKey() {
        return key;
    }

    public void setKey(AccountKey key) {
        this.key = key;
    }

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

}
