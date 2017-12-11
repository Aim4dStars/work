package com.bt.nextgen.service.wrap.integration.income;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.income.SubAccountIncomeDetailsImpl;
import com.bt.nextgen.service.integration.base.AvaloqAccountIntegrationService;
import com.bt.nextgen.service.integration.income.SubAccountIncomeDetails;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import com.btfin.panorama.service.exception.ServiceException;
import com.btfin.panorama.wrap.model.Income;
import com.btfin.panorama.wrap.service.InvestmentIncomeService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by L067221 on 19/09/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class WrapIncomeIntegrationServiceImplTest {

    @InjectMocks
    private WrapIncomeIntegrationServiceImpl wrapIncomeIntegrationService;

    @Mock
    private InvestmentIncomeService investmentIncomeService;

    @Mock
    private AvaloqAccountIntegrationService avaloqAccountIntegrationService;

    @Mock
    private WrapIncomeConverter incomeConverter;

    private List<Income> wrapIncomes;
    @Before
    public void setup() {
        final List<SubAccountIncomeDetails> wrapSubAccountIncomeDetailsList = new ArrayList<>();
        SubAccountIncomeDetails subAccountIncomeDetails = new SubAccountIncomeDetailsImpl();
        wrapSubAccountIncomeDetailsList.add(subAccountIncomeDetails);

        when(incomeConverter.convert(anyList(), (ServiceErrors) anyObject()))
                .thenAnswer(new Answer<List<SubAccountIncomeDetails>>() {

                    @Override
                    public List<SubAccountIncomeDetails> answer(InvocationOnMock invocation) throws Throwable {
                        Object args[] = invocation.getArguments();
                        assertNotNull(args[0]);
                        assertThat(((ArrayList)args[0]).size(), equalTo(3));
                        return wrapSubAccountIncomeDetailsList;
                    }
                });
        wrapIncomes= new ArrayList<>();
        Income income = new Income();
        wrapIncomes.add(income);
        wrapIncomes.add(income);
        wrapIncomes.add(income);

        when(investmentIncomeService.getInvestmentIncome(anyString(),(Date) anyObject(),(Date)anyObject(),
                anyString(),(ServiceErrors) anyObject()))
                .thenReturn(wrapIncomes);
    }

    @Test
    public void verifyLoadIncomeReceivedDetails_WhenIncomeReturned() {
        List<SubAccountIncomeDetails> subAccountIncomeDetailsList =
                wrapIncomeIntegrationService.loadIncomeReceivedDetails("M00000027", new DateTime("2016-02-11"),
                        new DateTime(), new ServiceErrorsImpl());

        assertNotNull(subAccountIncomeDetailsList);
        assertThat(subAccountIncomeDetailsList.size(), equalTo(1));
    }

    @Test
    public void verifyLoadIncomeReceivedDetails_WhenEmptyIncomeReturned() {
        when(investmentIncomeService.getInvestmentIncome(anyString(),(Date) anyObject(),(Date)anyObject(),
                anyString(),(ServiceErrors) anyObject()))
                .thenReturn(new ArrayList<Income>());
        List<SubAccountIncomeDetails> subAccountIncomeDetailsList =
                wrapIncomeIntegrationService.loadIncomeReceivedDetails("M00000027", new DateTime("2016-02-11"),
                        new DateTime(), new ServiceErrorsImpl());

        assertNotNull(subAccountIncomeDetailsList);
        assertThat(subAccountIncomeDetailsList.size(), equalTo(0));
        verify(incomeConverter, never()).convert((List<Income>) anyObject(), (ServiceErrorsImpl)anyObject());
    }

    @Test
    public void verifyLoadIncomeReceivedDetails_WhenNullIncomeReturned() {
        when(investmentIncomeService.getInvestmentIncome(anyString(),(Date) anyObject(),(Date)anyObject(),
                anyString(),(ServiceErrors) anyObject()))
                .thenReturn(null);
        List<SubAccountIncomeDetails> subAccountIncomeDetailsList =
                wrapIncomeIntegrationService.loadIncomeReceivedDetails("M00000027", new DateTime("2016-02-11"),
                        new DateTime(), new ServiceErrorsImpl());

        assertNotNull(subAccountIncomeDetailsList);
        assertThat(subAccountIncomeDetailsList.size(), equalTo(0));
        verify(incomeConverter, never()).convert((List<Income>) anyObject(), (ServiceErrorsImpl)anyObject());
    }

    @Test(expected =ServiceException.class)
    public void verifyLoadIncomeReceivedDetails_WhenExceptionThrown() {
        when(investmentIncomeService.getInvestmentIncome(anyString(),(Date) anyObject(),(Date)anyObject(),
                anyString(),(ServiceErrors) anyObject()))
                .thenThrow(new ServiceException());
        List<SubAccountIncomeDetails> subAccountIncomeDetailsList =
                wrapIncomeIntegrationService.loadIncomeReceivedDetails("M00000027", new DateTime("2016-02-11"),
                        new DateTime(), new ServiceErrorsImpl());
    }
}
