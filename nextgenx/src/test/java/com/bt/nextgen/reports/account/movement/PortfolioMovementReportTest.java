package com.bt.nextgen.reports.account.movement;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.portfolio.v3.model.DateRangeAccountKey;
import com.bt.nextgen.api.portfolio.v3.model.movement.GrowthItemDto;
import com.bt.nextgen.api.portfolio.v3.model.movement.ValuationMovementDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ValuationDto;
import com.bt.nextgen.api.portfolio.v3.service.ValuationMovementDtoService;
import com.bt.nextgen.api.portfolio.v3.service.valuation.ValuationDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.reports.account.common.SummaryReportData;
import com.bt.nextgen.reports.account.movements.GrowthItemReportData;
import com.bt.nextgen.reports.account.movements.PortfolioMovementReport;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationServiceFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PortfolioMovementReportTest {
    @InjectMocks
    private PortfolioMovementReport portfolioMovementReport;

    @Mock
    private ValuationMovementDtoService valuationMovementDtoService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private AccountIntegrationServiceFactory accountIntegrationServiceFactory;

    @Mock
    private ContentDtoService contentService;

    @Mock
    private ValuationDtoService valuationDtoService;

    @Mock
    private ValuationMovementDto valuationMovementDto;

    @Mock
    private ValuationDto valuationDto;

    private String accountId;
    private String effectiveDate;
    private DatedValuationKey key;

    @Before
    public void setup() {
        accountId = EncodedString.fromPlainText("0").toString();
        effectiveDate = "2014-09-09";
        key = new DatedValuationKey(accountId, new DateTime(effectiveDate), false);

        // Mock content service
        ContentDto content = new ContentDto(new ContentKey("MockKey"), "MockString");
        when(contentService.find((any(ContentKey.class)), any(ServiceErrorsImpl.class))).thenReturn(content);

        mockValuationMovementDto();
        mockValuationDto();

        when(valuationMovementDtoService.find(any(DateRangeAccountKey.class), any(ServiceErrorsImpl.class)))
                .thenReturn(valuationMovementDto);

        when(valuationDtoService.find((Matchers.any(DatedValuationKey.class)), any(ServiceErrorsImpl.class)))
                .thenReturn(valuationDto);

    }

    private void mockValuationMovementDto() {
        GrowthItemDto growthItemDto = mock(GrowthItemDto.class);
        when(growthItemDto.getBalance()).thenReturn(BigDecimal.TEN);
        when(growthItemDto.getDisplayName()).thenReturn("Growth item");

        GrowthItemDto nestedGrowthItemDto = mock(GrowthItemDto.class);
        when(nestedGrowthItemDto.getBalance()).thenReturn(BigDecimal.TEN);
        when(nestedGrowthItemDto.getDisplayName()).thenReturn("Nested growth item");

        when(growthItemDto.getGrowthItems()).thenReturn(Arrays.asList(nestedGrowthItemDto));

        when(valuationMovementDto.getOpeningBalance()).thenReturn(BigDecimal.TEN);
        when(valuationMovementDto.getClosingBalance()).thenReturn(BigDecimal.TEN);
        when(valuationMovementDto.getGrowthItems()).thenReturn(Arrays.asList(growthItemDto));
    }

    private void mockValuationDto() {
        when(valuationDto.getBalance()).thenReturn(BigDecimal.ONE);
    }

    @Test
    public void testGetStartDate() {
        Map<String, Object> params = new HashMap<>();
        params.put("start-date", "2017-06-01");
        assertEquals("01 Jun 2017", portfolioMovementReport.getStartDate(params));
    }

    @Test
    public void testEndStartDate() {
        Map<String, Object> params = new HashMap<>();
        params.put("end-date", "2017-08-01");
        assertEquals("01 Aug 2017", portfolioMovementReport.getEndDate(params));
    }

    @Test
    public void testGetData() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();
        params.put("account-id", accountId);
        params.put("start-date", "2017-06-01");
        params.put("end-date", "2017-08-01");

        Collection<GrowthItemReportData> result =
                (Collection<GrowthItemReportData>) portfolioMovementReport.getData(params, dataCollections);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetReportOpeningSummary() {
        Map<String, Object> params = new HashMap<>();
        params.put("start-date", "2017-06-01");
        SummaryReportData summaryReportData = portfolioMovementReport.getReportOpeningSummary(params);

        assertNotNull(summaryReportData);
        assertEquals("Opening portfolio value 01 Jun 2017", summaryReportData.getSummaryText());
        assertEquals("$10.00", summaryReportData.getSummaryBalance());
    }

    @Test
    public void testGetDisclaimer() {
        assertEquals("MockString", portfolioMovementReport.getDisclaimer());
    }

    @Test
    public void testGetSummaryDescription() {
        Map<String, Object> params = new HashMap<>();
        params.put("end-date", "2017-08-01");

        assertEquals("Closing portfolio value 01 Aug 2017", portfolioMovementReport.getSummaryDescription(params, null));
    }

    @Test
    public void getSummaryValue() {
        Map<String, Object> params = new HashMap<>();
        params.put("account-id", accountId);
        Map<String, Object> dataCollections = new HashMap<>();

        assertEquals("$1.00", portfolioMovementReport.getSummaryValue(params, dataCollections));
    }

    @Test
    public void testGetReportType() {
        assertEquals("Portfolio movements", portfolioMovementReport.getReportType(null, null));
    }

    @Test
    public void testEffectiveDate() {
        Map<String, Object> params = new HashMap<>();
        params.put("end-date", "2017-08-01");
        assertEquals("01 Aug 2017", portfolioMovementReport.getEndDate(params));
    }

    @Test
    public void testGetClosingBalance() {
        Map<String, Object> params = new HashMap<>();
        params.put("start-date", "2017-06-01");
        params.put("end-date", "2017-08-01");
        SummaryReportData summaryReportData = portfolioMovementReport.getReportClosingSummary(params);

        assertNotNull(summaryReportData);
        assertEquals("Closing portfolio value 01 Aug 2017", summaryReportData.getSummaryText());
        assertEquals("$10.00", summaryReportData.getSummaryBalance());
    }
}
