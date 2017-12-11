package com.bt.nextgen.api.termdeposit.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.termdeposit.model.TermDepositDetailDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.termdeposit.TermDepositIntegrationService;
import com.bt.nextgen.service.integration.termdeposit.TermDepositTrxImpl;
import com.bt.nextgen.service.integration.termdeposit.TermDepositTrxRequest;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TermDepositDtoServiceTest {
    @InjectMocks
    TermDepositDtoServiceImpl termDepositService;

    @Mock
    private TermDepositIntegrationService avaloqTermDepositIntegrationService;

    @Mock
    private BankDateIntegrationService bankDateIntegrationService;

    private DateTime now;
    private TermDepositTrxImpl termDeposit;

    @Before
    public void setup() {
        now = DateTime.now();

        termDeposit = new TermDepositTrxImpl();
        termDeposit.setOpenDate("01-01-2017");
        termDeposit.setWidrwPrpl(BigDecimal.valueOf(123));
        termDeposit.setDaysUntilMaturity(BigDecimal.valueOf(50));
        termDeposit.setPercentTermElapsed(BigDecimal.valueOf(30));
        termDeposit.setMaturityDate("01-06-2017");
        termDeposit.setInterestPaid(BigDecimal.valueOf(500));
        termDeposit.setInterestAccrued(BigDecimal.valueOf(600));
        termDeposit.setInterestRate(BigDecimal.valueOf(3.5));
        termDeposit.setWithdrawNet(BigDecimal.valueOf(550));
        termDeposit.setWithdrawInterestPaid(BigDecimal.valueOf(560));
        termDeposit.setInterestPaid(BigDecimal.valueOf(460));
        termDeposit.setWithdrawDate("02-06-2017");
        termDeposit.setAdjustedInterestRate(BigDecimal.valueOf(3.6));
        termDeposit.setNoticeEndDate("03-07-2017");

        when(bankDateIntegrationService.getBankDate(any(ServiceErrors.class))).thenReturn(now);
    }

    @Test
    public void testToTDBreakDto_whenValidObjects_thenObjectsMapped() {
        final TermDepositDetailDto termDepositDto = termDepositService.toTDBreakDto("accountId", termDeposit,
                new ServiceErrorsImpl());
        assertEquals("accountId", termDepositDto.getTdAccountId());
        assertEquals(termDeposit.getOpenDate(), termDepositDto.getOpenDate());
        assertEquals(termDeposit.getWidrwPrpl(), termDepositDto.getInvestmentAmount());
        assertEquals(termDeposit.getDaysUntilMaturity(), termDepositDto.getDaysLeft());
        assertEquals(termDeposit.getPercentTermElapsed(), termDepositDto.getPercentageTermElapsed());
        assertEquals(termDeposit.getMaturityDate(), termDepositDto.getMaturityDate());
        assertEquals(termDeposit.getInterestPaid(), termDepositDto.getInterestPaid());
        assertEquals(termDeposit.getInterestAccrued(), termDepositDto.getInterestAccrued());
        assertEquals(termDeposit.getInterestRate(), termDepositDto.getInterestRate());
        assertEquals(termDeposit.getWithdrawNet(), termDepositDto.getWithdrawNet());
        assertEquals(termDeposit.getWithdrawInterestPaid().subtract(termDeposit.getInterestPaid()),
                termDepositDto.getAdjustedInterestAmt());
        assertEquals(termDeposit.getWithdrawDate(), termDepositDto.getWithdrawDate());
        assertEquals(termDeposit.getAdjustedInterestRate(), termDepositDto.getAdjustedInterestRate());
        assertEquals(now.toString(), termDepositDto.getAvaloqDate());
        assertEquals(termDeposit.getNoticeEndDate(), termDepositDto.getNoticeEndDate());
    }

    @Test
    public void testToTDBreakDto_whenNoWithdrawInterestPaid_thenAdjustedInterestAmtZero() {
        TermDepositTrxImpl termDeposit = new TermDepositTrxImpl();
        termDeposit.setInterestPaid(BigDecimal.valueOf(460));

        final TermDepositDetailDto termDepositDto = termDepositService.toTDBreakDto("accountId", termDeposit,
                new ServiceErrorsImpl());
        assertEquals(BigDecimal.ZERO, termDepositDto.getAdjustedInterestAmt());
    }

    @Test
    public void testToTDBreakDto_whenNoInterestPaid_thenAdjustedInterestAmtZero() {
        TermDepositTrxImpl termDeposit = new TermDepositTrxImpl();
        termDeposit.setWithdrawInterestPaid(BigDecimal.valueOf(560));

        final TermDepositDetailDto termDepositDto = termDepositService.toTDBreakDto("accountId", termDeposit,
                new ServiceErrorsImpl());
        assertEquals(BigDecimal.ZERO, termDepositDto.getAdjustedInterestAmt());
    }

    @Test
    public void testValidate_whenValid_thenValidateBreakTermDepositCalled() {
        TermDepositDetailDto tdDto = new TermDepositDetailDto();
        tdDto.setTdAccountId("139D83A1CB75C1CF72D147DD52033573AC4EE45A0F0D56C919350C9EDA537610");
        tdDto.setKey(new AccountKey("139D83A1CB75C1CF72D147DD52033573AC4EE45A0F0D56C919350C9EDA537610"));

        when(avaloqTermDepositIntegrationService.validateBreakTermDeposit(any(TermDepositTrxRequest.class),
                any(ServiceErrors.class))).thenReturn(termDeposit);

        termDepositService.validate(tdDto, new ServiceErrorsImpl());
        Mockito.verify(avaloqTermDepositIntegrationService).validateBreakTermDeposit(any(TermDepositTrxRequest.class),
                any(ServiceErrors.class));
    }

    @Test
    public void testSubmit_whenValid_thenSubmitBreakTermDepositCalled() {
        TermDepositDetailDto tdDto = new TermDepositDetailDto();
        tdDto.setTdAccountId("139D83A1CB75C1CF72D147DD52033573AC4EE45A0F0D56C919350C9EDA537610");
        tdDto.setKey(new AccountKey("139D83A1CB75C1CF72D147DD52033573AC4EE45A0F0D56C919350C9EDA537610"));

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        errors.addError(new ServiceErrorImpl("message"));

        when(avaloqTermDepositIntegrationService.submitBreakTermDeposit(any(TermDepositTrxRequest.class),
                any(ServiceErrors.class))).thenReturn(termDeposit);

        termDepositService.submit(tdDto, errors);
        Mockito.verify(avaloqTermDepositIntegrationService).submitBreakTermDeposit(any(TermDepositTrxRequest.class),
                any(ServiceErrors.class));
    }
}
