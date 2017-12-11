package com.bt.nextgen.service.wrap.integration.income;

import com.bt.nextgen.service.avaloq.income.CashIncomeImpl;
import com.bt.nextgen.service.avaloq.income.DistributionIncomeImpl;
import com.bt.nextgen.service.avaloq.income.DividendIncomeImpl;
import com.bt.nextgen.service.avaloq.income.InterestIncomeImpl;
import com.bt.nextgen.service.avaloq.income.TermDepositIncomeImpl;
import com.bt.nextgen.service.integration.base.SystemType;
import com.bt.nextgen.service.integration.income.IncomeType;
import com.btfin.panorama.wrap.model.Income;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.math.BigDecimal;

/**
 * Converts Wrap Investment Income into com.bt.nextgen.service.integration.income.Income
 * Refer com.bt.nextgen.service.avaloq.income.IncomeFactory
 * Created by L067221 on 4/08/2017.
 */
public final class WrapIncomeFactory {
    private final static String DATE_TIME_PATTERN = "dd/MM/yyyy";
    private static final int PERCENTAGE_100 = 100;
    private WrapIncomeFactory() {
    }

    /**
     * Conversion is based on IncomeType
     *
     * @param incomeType
     * @param wrapIncome
     *
     * @return
     */
    static com.bt.nextgen.service.integration.income.Income buildIncomeEntryModel(IncomeType incomeType, Income wrapIncome) {
        com.bt.nextgen.service.integration.income.Income income = null;
        switch (incomeType) {
            case CASH:
                income = buildCashIncome(wrapIncome);
                break;
            case TERM_DEPOSIT:
                income = buildTermDeposit(wrapIncome);
                break;
            case DISTRIBUTION:
                income = buildDistribution(wrapIncome);
                break;
            case DIVIDEND:
                income = buildDividend(wrapIncome);
                break;
            case INTEREST:
                income = buildInterest(wrapIncome);
                break;
            default:
                break;
        }
        return income;
    }

    //Conversion for Cash Income
    private static com.bt.nextgen.service.integration.income.Income buildCashIncome(Income income) {
        final CashIncomeImpl cashIncome = new CashIncomeImpl();
        cashIncome.setPaymentDate(getDate(income.getPayDate()));
        cashIncome.setAmount(income.getNetAmount());
        cashIncome.setDescription(income.getSecurityName());
        cashIncome.setIncomeSubtype(IncomeType.INTEREST);
        return cashIncome;
    }

    //Conversion for Term Deposits
    private static TermDepositIncomeImpl buildTermDeposit(Income income) {
        final TermDepositIncomeImpl termDepositIncome = new TermDepositIncomeImpl();
        termDepositIncome.setPaymentDate(getDate(income.getPayDate()));
        termDepositIncome.setInterest(income.getNetAmount());
        termDepositIncome.setDescription(income.getSecurityName());
        return termDepositIncome;
    }

    //Conversion for DividendIncome
    private static DividendIncomeImpl buildDividend(Income income) {
        final ThirdPartyDividendIncomeImpl dividendIncome = new ThirdPartyDividendIncomeImpl();
        dividendIncome.setPaymentDate(getDate(income.getPayDate()));
        dividendIncome.setExecutionDate(getDate(income.getAccrualDate()));
        dividendIncome.setIncomeRate(getIncomeRate(income.getPrice()));
        dividendIncome.setQuantity(income.getQuantity());
        dividendIncome.setAmount(income.getNetAmount());
        dividendIncome.setFrankedDividend(income.getFrankAmount());
        dividendIncome.setUnfrankedDividend(income.getUnFrankAmount());
        dividendIncome.setThirdPartySource(SystemType.WRAP.name());
        return dividendIncome;
    }

    //Conversion for DistributionIncome
    private static DistributionIncomeImpl buildDistribution(Income income) {
        final DistributionIncomeImpl distributionIncome = new DistributionIncomeImpl();
        distributionIncome.setPaymentDate(getDate(income.getPayDate()));
        distributionIncome.setExecutionDate(getDate(income.getAccrualDate()));
        distributionIncome.setIncomeRate(getIncomeRate(income.getPrice()));
        distributionIncome.setQuantity(income.getQuantity());
        distributionIncome.setAmount(income.getNetAmount());
        return distributionIncome;
    }

    //Conversion for InterestIncome
    private static InterestIncomeImpl buildInterest(Income income) {
        final InterestIncomeImpl interest = new InterestIncomeImpl();
        interest.setPaymentDate(getDate(income.getPayDate()));
        interest.setExecutionDate(getDate(income.getAccrualDate()));
        interest.setIncomeRate(getIncomeRate(income.getPrice()));
        interest.setQuantity(income.getQuantity());
        interest.setAmount(income.getNetAmount());
        return interest;
    }

    private static DateTime getDate(String date) {
        return DateTime.parse(date.trim(), DateTimeFormat.forPattern(DATE_TIME_PATTERN));
    }

    // returns Wrap Income price is divided by 100
    private static BigDecimal getIncomeRate(BigDecimal price) {
        return price.divide(new BigDecimal(PERCENTAGE_100));
    }

}
