package com.bt.nextgen.api.contributionhistory.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.contributionhistory.model.ContributionHistoryDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.contributionhistory.*;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.base.MigrationAttribute;
import com.bt.nextgen.service.integration.cashcategorisation.model.ContributionClassification;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
public class WrapContributionHistoryDtoServiceTest {

    private final Collection<Code> typeCodeList = new ArrayList<>();
    private ContributionHistory contributionHistory = new ContributionHistoryImpl();
    private  List<ApiSearchCriteria> criteriaList = new ArrayList<>();
    private WrapAccountDetail account;
    String mode=null;

    @InjectMocks
    private WrapContributionHistoryDtoServiceImpl contributionHistoryDtoService;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    @Mock
    private ThirdPartyIntegrationServiceFactory contributionHistoryIntegrationServiceFactory;

    @Mock
    private WrapContributionHistoryIntegrationServiceImpl wrapContributionHistoryIntegrationService;

    @Mock
    private ContributionHistoryIntegrationServiceImpl contributionHistoryIntegrationService;

    @Mock
    private CacheContributionHistoryIntegrationServiceImpl cacheContributionHistoryIntegrationService;

    @Mock
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Before
    public void setup() {
        account = Mockito.mock(WrapAccountDetail.class);
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
    public void searchWithMigrationDateAfterSelectedFinancialYear() {
        Mockito.when(account.getMigrationDate()).thenReturn(new DateTime("2017-02-02"));
        Mockito.when(accountIntegrationService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(account);

        when(contributionHistoryIntegrationServiceFactory.getInstance(any(Class.class), any(MigrationAttribute.class), any(String.class), any(com.bt.nextgen.service.integration.account.AccountKey.class)))
                .thenReturn(wrapContributionHistoryIntegrationService);
        when(wrapContributionHistoryIntegrationService.getContributionHistory(any(AccountKey.class),any(DateTime.class), any(DateTime.class)))
                .thenReturn(contributionHistory);
        ContributionHistoryDto contributionHistoryDto = contributionHistoryDtoService.search(criteriaList, new ServiceErrorsImpl());
        Assert.assertNotNull(contributionHistoryDto);
        Assert.assertEquals(contributionHistoryDto.getFinancialYearStartDate(),new DateTime("2015-07-01").toLocalDate());
        Assert.assertEquals(contributionHistoryDto.getContributionSummary().getContributionSummaryClassifications().size(), 3);
        Assert.assertEquals(contributionHistoryDto.getContributionByClassifications().get(0).getContributionType(),"1");
        Assert.assertEquals(contributionHistoryDto.getContributionByClassifications().get(0).getContributionTypeLabel(),"Employer");
    }

    @Test
    public void searchWithMigrationDateBeforeSelectedFinancialYear() {
        Mockito.when(account.getMigrationDate()).thenReturn(new DateTime("2013-02-02"));
        Mockito.when(accountIntegrationService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(account);

        when(contributionHistoryIntegrationServiceFactory.getInstance(any(Class.class), any(MigrationAttribute.class), any(String.class), any(com.bt.nextgen.service.integration.account.AccountKey.class)))
                .thenReturn(contributionHistoryIntegrationService);
        when(contributionHistoryIntegrationService.getContributionHistory(any(AccountKey.class), any(DateTime.class), any(DateTime.class)))
                .thenReturn(contributionHistory);
        ContributionHistoryDto contributionHistoryDto = contributionHistoryDtoService.search(criteriaList, new ServiceErrorsImpl());
        Assert.assertNotNull(contributionHistoryDto);
        Assert.assertEquals(contributionHistoryDto.getFinancialYearStartDate(),new DateTime("2015-07-01").toLocalDate());
        Assert.assertEquals(contributionHistoryDto.getContributionSummary().getContributionSummaryClassifications().size(), 3);
        Assert.assertEquals(contributionHistoryDto.getContributionByClassifications().get(0).getContributionType(),"1");
        Assert.assertEquals(contributionHistoryDto.getContributionByClassifications().get(0).getContributionTypeLabel(),"Employer");

    }

    @Test
    public void searchWithMigrationDateNull() {
        Mockito.when(account.getMigrationDate()).thenReturn(null);
        Mockito.when(accountIntegrationService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(account);
        when(contributionHistoryIntegrationServiceFactory.getInstance(any(Class.class), any(MigrationAttribute.class), any(String.class), any(com.bt.nextgen.service.integration.account.AccountKey.class)))
                .thenReturn(contributionHistoryIntegrationService);
        when(contributionHistoryIntegrationService.getContributionHistory(any(AccountKey.class), any(DateTime.class), any(DateTime.class)))
                .thenReturn(contributionHistory);
        ContributionHistoryDto contributionHistoryDto = contributionHistoryDtoService.search(criteriaList, new ServiceErrorsImpl());
        Assert.assertNotNull(contributionHistoryDto);
        Assert.assertEquals(contributionHistoryDto.getFinancialYearStartDate(),new DateTime("2015-07-01").toLocalDate());
        Assert.assertEquals(contributionHistoryDto.getContributionSummary().getContributionSummaryClassifications().size(), 3);
        Assert.assertEquals(contributionHistoryDto.getContributionByClassifications().get(0).getContributionType(),"1");
        Assert.assertEquals(contributionHistoryDto.getContributionByClassifications().get(0).getContributionTypeLabel(), "Employer");
    }

    @Test
    public void searchWithMigrationDateWithinSelectedFinancialYear() {
        Mockito.when(account.getMigrationDate()).thenReturn(new DateTime("2015-11-11"));
        Mockito.when(accountIntegrationService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(account);

        when(contributionHistoryIntegrationServiceFactory.getInstance(any(Class.class), any(MigrationAttribute.class), eq(mode), any(com.bt.nextgen.service.integration.account.AccountKey.class)))
                .thenReturn(contributionHistoryIntegrationService);
        when(contributionHistoryIntegrationServiceFactory.getInstance(any(Class.class), any(MigrationAttribute.class), eq(Attribute.EXTERNAL), any(com.bt.nextgen.service.integration.account.AccountKey.class)))
                .thenReturn(wrapContributionHistoryIntegrationService);

        when(contributionHistoryIntegrationService.getContributionHistory(any(AccountKey.class), any(DateTime.class), any(DateTime.class)))
                .thenReturn(contributionHistory);
        when(wrapContributionHistoryIntegrationService.getContributionHistory(any(AccountKey.class), any(DateTime.class), any(DateTime.class)))
                .thenReturn(contributionHistory);

        ContributionHistoryDto contributionHistoryDto = contributionHistoryDtoService.search(criteriaList, new ServiceErrorsImpl());
        Mockito.verify(contributionHistoryIntegrationServiceFactory, Mockito.times(2)).getInstance(any(Class.class), any(MigrationAttribute.class), any(String.class), any(com.bt.nextgen.service.integration.account.AccountKey.class));
        Assert.assertNotNull(contributionHistoryDto);
    }
    @Test
    public void searchWithMigrationDateBeforeSelectedFinancialYearAndCacheModeTrue() {
        Mockito.when(account.getMigrationDate()).thenReturn(new DateTime("2013-02-02"));
        Mockito.when(accountIntegrationService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(account);
        when(contributionHistoryIntegrationServiceFactory.getInstance(any(Class.class), any(MigrationAttribute.class), eq(Attribute.CACHE), any(com.bt.nextgen.service.integration.account.AccountKey.class)))
                .thenReturn(cacheContributionHistoryIntegrationService);
        when(cacheContributionHistoryIntegrationService.getContributionHistory(any(AccountKey.class),any(DateTime.class), any(DateTime.class)))
                .thenReturn(contributionHistory);
        criteriaList.add(new ApiSearchCriteria("useCache",ApiSearchCriteria.SearchOperation.EQUALS, "true", ApiSearchCriteria.OperationType.STRING));
        ContributionHistoryDto contributionHistoryDto = contributionHistoryDtoService.search(criteriaList, new ServiceErrorsImpl());
        Assert.assertNotNull(contributionHistoryDto);
    }

}