package com.bt.nextgen.api.superannuation.caps.service;


import com.bt.nextgen.api.superannuation.caps.model.SuperAccountContributionCapsDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.superannuation.caps.model.ContributionCaps;
import com.bt.nextgen.service.avaloq.superannuation.caps.model.ContributionCapsImpl;
import com.bt.nextgen.service.avaloq.superannuation.caps.service.ContributionCapIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContributionCapsDtoServiceImplTest {
    @Mock
    private ContributionCapIntegrationService contributionCapIntegrationService;

    @InjectMocks
    private ContributionCapsDtoService contributionCapsDtoService = new ContributionCapsDtoServiceImpl();


    @Test
    public void retrieveAccountContributionCapsDto() {
        ContributionCaps cap = new ContributionCapsImpl();
        cap.setConcessionalCap(new BigDecimal(325000));
        cap.setNonConcessionalCap(new BigDecimal(411000));
        cap.setFinancialYearStartDate(new DateTime("2016-07-01T00:00:00+10:00"));


        when(contributionCapIntegrationService.getContributionCaps(any(AccountKey.class), any(DateTime.class),
                any(ServiceErrors.class))).thenReturn(cap);

        com.bt.nextgen.api.account.v3.model.AccountKey accountKey = new com.bt.nextgen.api.account.v3.model.AccountKey("123456");
        ApiSearchCriteria dateCriteria = new ApiSearchCriteria("date", ApiSearchCriteria.SearchOperation.EQUALS,
                "2016-01-15", ApiSearchCriteria.OperationType.STRING);
        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(dateCriteria);

        List<SuperAccountContributionCapsDto> superAccountCapDtoList = contributionCapsDtoService.search(accountKey,
                criteriaList, new ServiceErrorsImpl());
        SuperAccountContributionCapsDto superAccountCapDto = superAccountCapDtoList.get(0);

        assertEquals(accountKey, superAccountCapDto.getAccountKey());
        assertEquals(new LocalDate(2015, 7, 1), superAccountCapDto.getFinancialYearStartDate());

        assertEquals("conc", superAccountCapDto.getContributionCaps().get(0).getContributionClassification());
        assertEquals("concessional", superAccountCapDto.getContributionCaps().get(0).getContributionClassificationLabel());
        assertEquals((new BigDecimal(325000)), superAccountCapDto.getContributionCaps().get(0).getAmount());

        assertEquals("nconc", superAccountCapDto.getContributionCaps().get(1).getContributionClassification());
        assertEquals("non-concessional", superAccountCapDto.getContributionCaps().get(1).getContributionClassificationLabel());
        assertEquals((new BigDecimal(411000)), superAccountCapDto.getContributionCaps().get(1).getAmount());
    }
}