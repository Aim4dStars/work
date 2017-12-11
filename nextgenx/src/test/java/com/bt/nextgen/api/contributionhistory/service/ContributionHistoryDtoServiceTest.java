package com.bt.nextgen.api.contributionhistory.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.contributionhistory.model.ContributionHistoryDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.contributionhistory.*;
import com.bt.nextgen.service.integration.cashcategorisation.model.ContributionClassification;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContributionHistoryDtoServiceTest {

    private final Collection<Code> typeCodeList = new ArrayList<>();
    private ContributionHistory contributionHistory = new ContributionHistoryImpl();
    private  List<ApiSearchCriteria> criteriaList = new ArrayList<>();

    @InjectMocks
    private ContributionHistoryDtoServiceImpl contributionHistoryDtoService;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    @Mock
    @Qualifier("ContributionHistoryIntegrationServiceFactoryImpl")
    private ContributionHistoryIntegrationServiceFactory contributionHistoryIntegrationServiceFactory;

    @Mock
    private ContributionHistoryIntegrationServiceImpl contributionHistoryIntegrationService;

    @Before
    public void setup() {

        ApiSearchCriteria accountIdCriteria = new ApiSearchCriteria("accountId", ApiSearchCriteria.SearchOperation.EQUALS, "1234566", ApiSearchCriteria.OperationType.STRING);
        ApiSearchCriteria dateCriteria = new ApiSearchCriteria("financialYearDate", ApiSearchCriteria.SearchOperation.EQUALS, "2015-07-30", ApiSearchCriteria.OperationType.STRING);


        criteriaList.add(accountIdCriteria);
        criteriaList.add(dateCriteria);
        Code code1 = new CodeImpl("1", "EMPLOYER", "Employer - Super Guarantee (SG)", "employer");
        Code code2 = new CodeImpl("62", "PRSNL_SAV_CLAIM", "Personal Savings - Claim Personal Tax Deduction", "prsnl_sav_claim");
        typeCodeList.add(code1);
        typeCodeList.add(code2);
        when(staticIntegrationService.loadCodes(eq(CodeCategory.SUPER_CONTRIBUTIONS_TYPE), any(ServiceErrors.class)))
                .thenReturn(typeCodeList);
        List<ContributionSummaryByType> summaries = new ArrayList<>();
        ContributionSummaryByTypeImpl contributionSummaryByType = new ContributionSummaryByTypeImpl();
        contributionSummaryByType.setContributionClassification(ContributionClassification.CONCESSIONAL);
        contributionSummaryByType.setContributionType(new ContributionType("1","Employer"));
        contributionSummaryByType.setAmount(new BigDecimal(20));
        summaries.add(contributionSummaryByType);
        contributionHistory.getContributionSummariesByType().addAll(summaries);

    }

    

    @Test
    public void searchWithNullContributionHistoryObject() {
        when(contributionHistoryIntegrationServiceFactory.getInstance(any(String.class)))
                .thenReturn(contributionHistoryIntegrationService);
        when(contributionHistoryIntegrationService.getContributionHistory(any(AccountKey.class),any(DateTime.class), any(DateTime.class)))
                .thenReturn(null);
        ContributionHistoryDto contributionHistoryDto = contributionHistoryDtoService.search(criteriaList, new ServiceErrorsImpl());
        Assert.assertNotNull(contributionHistoryDto);
        Assert.assertEquals(contributionHistoryDto.getFinancialYearStartDate(),new DateTime("2015-07-01").toLocalDate());
    }

}

