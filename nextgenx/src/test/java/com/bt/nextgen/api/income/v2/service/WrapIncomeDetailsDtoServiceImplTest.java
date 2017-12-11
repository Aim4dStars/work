package com.bt.nextgen.api.income.v2.service;

import com.bt.nextgen.api.income.v2.model.AbstractIncomeDto;
import com.bt.nextgen.api.income.v2.model.CashIncomeDto;
import com.bt.nextgen.api.income.v2.model.IncomeDetailsKey;
import com.bt.nextgen.api.income.v2.model.IncomeDetailsType;
import com.bt.nextgen.api.income.v2.model.IncomeDto;
import com.bt.nextgen.api.income.v2.model.IncomeValueDto;
import com.bt.nextgen.api.income.v2.model.IncomeValuesDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.income.WrapAccountIncomeDetailsImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.income.IncomeIntegrationService;
import com.bt.nextgen.service.integration.income.IncomeIntegrationServiceFactory;
import com.bt.nextgen.service.integration.income.IncomeType;
import com.bt.nextgen.service.integration.income.SubAccountIncomeDetails;
import com.bt.nextgen.service.integration.income.WrapAccountIncomeDetails;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.when;

/**
 * Created by L067221 on 30/08/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class WrapIncomeDetailsDtoServiceImplTest {

    @InjectMocks
    private WrapIncomeDetailsDtoServiceImpl incomeDetailsService;

    @Mock
    private IncomeSubAccountIncomeAggregator aggregator;

    @Mock
    private IncomeValueDtoBuilder incomeValueDtoBuilder;

    @Mock
    private IncomeIntegrationService incomeService;

    @Mock
    private IncomeIntegrationServiceFactory incomeIntegrationServiceFactory;

    @Before
    public void setup() {
        Map<AssetType, Map<IncomeType, List<AbstractIncomeDto>>> investmentMapIncome = new HashMap<>();
        when(aggregator.buildInvestmentMapFromSubAccount(anyListOf(SubAccountIncomeDetails.class),
                any(AccountKey.class), any(ServiceErrors.class))).thenReturn(investmentMapIncome);
        List<IncomeDto> incomeDtos = new ArrayList<>();
        incomeDtos.add(new IncomeValueDto(new CashIncomeDto("BT CASH", "Code", new DateTime(), new BigDecimal(12.000), IncomeType.CASH)));
        when(incomeValueDtoBuilder.buildIncomeTypesDtoList(anyMap())).thenReturn(incomeDtos);
    }

    @Test
    public void testFind_whenReceivedIncomeType_thenIncomeLoadedAndAggregated() {
        final DateTime d1 = new DateTime();
        final DateTime d2 = new DateTime();
        final List<WrapAccountIncomeDetails> integrationResult = new ArrayList<>();

        IncomeDetailsKey key = new IncomeDetailsKey(EncodedString.fromPlainText("accountId").toString(),
                IncomeDetailsType.RECEIVED, d1, d2);
        when(incomeService.loadIncomeReceivedDetails(any(AccountKey.class), any(DateTime.class),
                any(DateTime.class), any(ServiceErrors.class)))
                .thenAnswer(new Answer<List<WrapAccountIncomeDetails>>() {

                    @Override
                    public List<WrapAccountIncomeDetails> answer(InvocationOnMock invocation) throws Throwable {
                        Object args[] = invocation.getArguments();
                        assertEquals(AccountKey.valueOf("accountId"), args[0]);
                        assertEquals(d1, args[1]);
                        assertEquals(d2, args[2]);
                        return integrationResult;
                    }
                });

        IncomeValuesDto incomeValuesDto = incomeDetailsService.find(key, new FailFastErrorsImpl());
        assertNotNull(incomeValuesDto);
        assertEquals(incomeValuesDto.getKey().getType(), IncomeDetailsType.RECEIVED);
    }

    @Test
    public void testFind_whenAccruedIncomeType_thenValautionLoadedAndAggregated() {
        final DateTime d1 = new DateTime();

        final List<WrapAccountIncomeDetails> integrationResult = new ArrayList<>();
        WrapAccountIncomeDetailsImpl wrapAccountIncomeDetails = new WrapAccountIncomeDetailsImpl();
        List<SubAccountIncomeDetails> subAccountIncomeDetailsList = new ArrayList<>();
        wrapAccountIncomeDetails.setSubAccountIncomeDetailsList(subAccountIncomeDetailsList);
        integrationResult.add(wrapAccountIncomeDetails);

        IncomeDetailsKey key = new IncomeDetailsKey(EncodedString.fromPlainText("accountId").toString(),
                IncomeDetailsType.ACCRUED, d1, d1);
        when(incomeService.loadIncomeAccruedDetails(any(AccountKey.class), any(DateTime.class), any(ServiceErrors.class)))
                .thenAnswer(new Answer<List<WrapAccountIncomeDetails>>() {

                    @Override
                    public List<WrapAccountIncomeDetails> answer(InvocationOnMock invocation) throws Throwable {
                        Object args[] = invocation.getArguments();
                        assertEquals(AccountKey.valueOf("accountId"), args[0]);
                        assertEquals(d1, args[1]);
                        return integrationResult;
                    }
                });

        IncomeValuesDto incomeValuesDto = incomeDetailsService.find(key, new FailFastErrorsImpl());
        assertNotNull(incomeValuesDto);
        assertEquals(incomeValuesDto.getKey().getType(), IncomeDetailsType.ACCRUED);
    }
}
