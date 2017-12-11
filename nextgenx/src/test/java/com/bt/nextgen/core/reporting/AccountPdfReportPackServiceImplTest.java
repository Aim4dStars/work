package com.bt.nextgen.core.reporting;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.integration.account.PersonRelation;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.core.reporting.datasource.ReportDatasource;
import com.bt.nextgen.core.reporting.datasource.ReportDatasourceFactory;
import com.bt.nextgen.core.reporting.view.ReportView;
import com.bt.nextgen.core.reporting.view.ReportViewFactory;
import com.bt.nextgen.reports.web.model.ReportRequestDto;
import com.bt.nextgen.reports.web.model.ReportRequestPackDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.CacheAccountIntegrationService;
import com.bt.nextgen.service.integration.client.CacheClientIntegrationService;
import com.bt.nextgen.service.integration.portfolio.CachePortfolioIntegrationService;
import com.bt.nextgen.service.integration.userinformation.ClientKey;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountPdfReportPackServiceImplTest {

    private static final String ACCOUNT_NAME = "Test Name";

    private static final String ACCOUNT_NUMBER = "120021779";

    @InjectMocks
    private AccountPdfReportPackServiceImpl accountPdfReportPackServiceDto;

    @Mock
    private CachePortfolioIntegrationService cachedPortfolioIntegrationService;

    @Mock
    private CacheAccountIntegrationService cacheAccountIntegrationService;

    @Mock
    private CacheClientIntegrationService cacheClientIntegrationService;

    @Mock
    private ReportBuilder concurrentReportBuilder;

    @Mock
    private ReportDatasourceFactory reportDatasourceFactory;

    @Mock
    private ReportViewFactory reportViewFactory;

    @Before
    public void init() {
        ReportDatasource reportDatasource = mock(ReportDatasource.class);
        when(reportDatasourceFactory.createDataSources(any(ReportIdentity.class), anyMap())).thenReturn(Arrays.asList(reportDatasource));

        WrapAccountDetail wrapAccountDetail = mock(WrapAccountDetail.class);
        PersonRelation personRelation = mock(PersonRelation.class);
        ClientKey clientKey = ClientKey.valueOf("0");

        Map<ClientKey, PersonRelation> associatedPersons = new HashMap<>(1);
        associatedPersons.put(clientKey, personRelation);

        when(personRelation.getClientKey()).thenReturn(clientKey);
        when(personRelation.isPrimaryContact()).thenReturn(true);
        when(wrapAccountDetail.getAssociatedPersons()).thenReturn(associatedPersons);

        when(cacheAccountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(wrapAccountDetail);
    }

    @Test
    public void testCreate_whenAccountPdfReportPackRequested_thenClearCachesBeforeCallingReportBuilder() {
        ReportRequestPackDto reportRequestPackDto = mock(ReportRequestPackDto.class);
        when(reportRequestPackDto.getCompressReports()).thenReturn(Boolean.FALSE);

        ReportRequestDto reportRequestDto = mock(ReportRequestDto.class);
        when(reportRequestDto.getReportId()).thenReturn("reportId");

        String accountId = "0";
        String effectiveDate = "2017-06-23";

        Map<String, Object> params = new HashMap<>();
        params.put("account-id", EncodedString.fromPlainText(accountId).toString());
        params.put("effective-date", effectiveDate);

        when(reportRequestDto.getParams()).thenReturn(params);

        when(reportRequestPackDto.getReportRequestDtos()).thenReturn(Arrays.asList(reportRequestDto));

        ReportView reportView = mock(ReportView.class);
        ReportTemplate reportTemplate = mock(ReportTemplate.class);
        ReportIdentity reportIdentity = mock(ReportIdentity.class);

        when(reportTemplate.isAvailable()).thenReturn(Boolean.TRUE);
        when(reportView.getReportTemplate()).thenReturn(reportTemplate);
        when(reportView.getReportTemplate()).thenReturn(reportTemplate);
        when(reportTemplate.getId()).thenReturn(reportIdentity);
        when(reportIdentity.getTemplateKey()).thenReturn("reportId");

        when(reportViewFactory.createReportView(any(ReportIdentity.class))).thenReturn(reportView);

        OutputStream outputStream = mock(OutputStream.class);

        accountPdfReportPackServiceDto.create(reportRequestPackDto, null, outputStream);

        AccountKey accountKey = AccountKey.valueOf(accountId);
        DateTime valuationDate = new DateTime(effectiveDate);

        verify(cacheAccountIntegrationService, times(1)).clearWrapAccountDetailCache(accountKey);
        verify(cacheClientIntegrationService, times(1)).clearClientDetailsCache(ClientKey.valueOf("0"));
        verify(cachedPortfolioIntegrationService, times(1)).clearAccountValuationCache(accountKey, valuationDate);
        verify(cachedPortfolioIntegrationService, times(1)).clearAccountValuationCache(accountKey, valuationDate, true);
        verify(cachedPortfolioIntegrationService, times(1)).clearAccountValuationCache(accountKey, valuationDate, false);
    }

    @Test
    public void testGetReportFileName_whenAccountKeyProvided_thenReturnFileName() {
        WrapAccountDetailImpl accountDetail = new WrapAccountDetailImpl();
        accountDetail.setAccountName(ACCOUNT_NAME);
        accountDetail.setAccountNumber(ACCOUNT_NUMBER);
        when(cacheAccountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(accountDetail);

        AccountKey accountKey = AccountKey.valueOf("123456");
        String fileName = accountPdfReportPackServiceDto.getReportFileName(accountKey);

        assertThat(fileName, is(ACCOUNT_NAME + " " + ACCOUNT_NUMBER));
    }
}
