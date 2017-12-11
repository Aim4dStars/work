package com.bt.nextgen.service.wrap.integration.income;

import com.bt.nextgen.service.avaloq.income.CashIncomeImpl;
import com.bt.nextgen.service.avaloq.income.DistributionIncomeImpl;
import com.bt.nextgen.service.avaloq.income.DividendIncomeImpl;
import com.bt.nextgen.service.avaloq.income.InterestIncomeImpl;
import com.bt.nextgen.service.avaloq.income.TermDepositIncomeImpl;
import com.bt.nextgen.service.integration.income.CashIncome;
import com.bt.nextgen.service.integration.income.DistributionIncome;
import com.bt.nextgen.service.integration.income.DividendIncome;
import com.bt.nextgen.service.integration.income.IncomeType;
import com.bt.nextgen.service.integration.income.InterestIncome;
import com.bt.nextgen.service.integration.income.TermDepositIncome;
import com.btfin.panorama.wrap.model.Income;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by L067221 on 19/09/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class WrapIncomeFactoryTest {

    @InjectMocks
    WrapIncomeFactory  factory;

    private Income wrapIncome;

    @Before
    public void setUp()  {
        wrapIncome  = new Income();
        wrapIncome.setNetAmount(new BigDecimal("4588.87"));
        wrapIncome.setAccrualDate("15/03/2015");
        wrapIncome.setPayDate("15/02/2015");
        wrapIncome.setSecurityName("WBC123TD");
        wrapIncome.setPrice(new BigDecimal("4588.87"));
    }

    @Test
    public void testBuildIncomeEntryModel_whenIncomeTypeCash() {
        CashIncome cashIncome =  (CashIncomeImpl)factory.buildIncomeEntryModel(IncomeType.CASH, wrapIncome);
        assertNotNull(cashIncome);
        assertThat(cashIncome.getPaymentDate(), equalTo(new DateTime("2015-02-15T00:00:00.000+11:00")));
        assertThat(cashIncome.getAmount(), equalTo(new BigDecimal("4588.87")));
        assertEquals(cashIncome.getPaymentDate(), new DateTime("2015-02-15T00:00:00.000+11:00"));
    }

    @Test
    public void testBuildIncomeEntryModel_whenIncomeTypeTD() {
        TermDepositIncome termDepositIncome =  (TermDepositIncomeImpl)factory.buildIncomeEntryModel(IncomeType.TERM_DEPOSIT,
                wrapIncome);
        assertNotNull(termDepositIncome);
        assertEquals(termDepositIncome.getDescription(), "WBC123TD");
    }

    @Test
    public void testBuildIncomeEntryModel_whenIncomeTypeDistribution() {

        DistributionIncome distributionIncome =  (DistributionIncomeImpl)factory.buildIncomeEntryModel(IncomeType.DISTRIBUTION,
                wrapIncome);
        assertNotNull(distributionIncome);
        assertThat(distributionIncome.getIncomeRate(), equalTo(new BigDecimal("45.8887")));
    }

    @Test
    public void testBuildIncomeEntryModel_whenIncomeTypeDividend() {
        DividendIncome  dividendIncome =  (DividendIncomeImpl)factory.buildIncomeEntryModel(IncomeType.DIVIDEND,
                wrapIncome);
        assertNotNull(dividendIncome);
        assertThat(dividendIncome.getIncomeRate(), equalTo(new BigDecimal("45.8887")));
    }

    @Test
    public void testBuildIncomeEntryModel_whenIncomeTypeInterest() {
        InterestIncome interestIncome =  (InterestIncomeImpl)factory.buildIncomeEntryModel(IncomeType.INTEREST,
                wrapIncome);
        assertNotNull(interestIncome);
        assertThat(interestIncome.getIncomeRate(), equalTo(new BigDecimal("45.8887")));
    }
}
