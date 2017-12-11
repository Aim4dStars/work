package com.bt.nextgen.reports.account.allocation.sector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationServiceFactory;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import net.sf.jasperreports.engine.Renderable;
import org.apache.commons.configuration.Configuration;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AggregatedAllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AssetAllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.KeyedAllocBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.service.allocation.AllocationBySectorDtoService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

@RunWith(MockitoJUnitRunner.class)
public class AssetAllocationAssetClassReportTest {

    @Mock
    private AllocationBySectorDtoService allocationDtoService;

    @Mock
    private AssetSectorReportDataConverter reportDataConverter;
    
    @InjectMocks
    private AssetAllocationAssetClassReport assetAllocationReport;

    @Mock
    private CmsService cmsService;

    @Mock
    private BrokerHelperService brokerHelperService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private AccountIntegrationServiceFactory accountIntegrationServiceFactory;

    @Mock
    private Configuration configuration;

    private Map<String, Object> params;
    private Map<String, Object> dataCollections;

    private String effectiveDate;
    private DatedValuationKey key;
    private KeyedAllocBySectorDto allocationDto;

    private static final String ACCOUNT_ID = "account-id";
    private static final String EFFECTIVE_DATE = "effective-date";
    private static final String INCLUDE_EXTERNAL = "include-external";
    private static final String GROUP_BY_NAME = "group-by";

    @Before
    public void setup() {
        final String accountId = "975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0";
        effectiveDate = "2014-09-09";
        key = new DatedValuationKey(accountId, new DateTime(effectiveDate), false);
        allocationDto = mockAllocationData();

        dataCollections = new HashMap<>();
        params = new HashMap<>();
        params.put(ACCOUNT_ID, accountId);

        when(allocationDtoService.find((Matchers.any(DatedValuationKey.class)), any(ServiceErrorsImpl.class))).thenReturn(
                allocationDto);
        AssetAllocationReportData assetAllocationReportData = Mockito.mock(AssetAllocationReportData.class);
        when(reportDataConverter.getReportData(Mockito.any(AllocationGroupType.class), Mockito.anyList())).thenReturn(assetAllocationReportData);
        // Mock content service
        when(cmsService.getContent("DS-IP-0002")).thenReturn("MockString");
        when(cmsService.getContent("DS-IP-0200")).thenReturn("MockStringDirect");

        when(accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(mock(WrapAccountDetail.class));
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(ServiceErrors.class))).thenReturn(UserExperience.ADVISED);
        when(configuration.getString(Mockito.anyString())).thenReturn("classpath:/");
        when(accountIntegrationServiceFactory.getInstance(anyString())).thenReturn(accountIntegrationService);
    }

    public KeyedAllocBySectorDto mockAllocationData() {
        AssetAllocationBySectorDto allocation1 = Mockito.mock(AssetAllocationBySectorDto.class);
        Mockito.when(allocation1.getIndustrySectorSubSector()).thenReturn("Diversified");

        AssetAllocationBySectorDto allocation2 = Mockito.mock(AssetAllocationBySectorDto.class);
        Mockito.when(allocation2.getIndustrySectorSubSector()).thenReturn("Other");

        List<AllocationBySectorDto> allocations = Arrays.asList((AllocationBySectorDto) allocation1,
                (AllocationBySectorDto) allocation2);

        AggregatedAllocationBySectorDto cashSector = Mockito.mock(AggregatedAllocationBySectorDto.class);
        Mockito.when(cashSector.getName()).thenReturn("Cash");
        Mockito.when(cashSector.getAllocations()).thenReturn(allocations);

        AggregatedAllocationBySectorDto groupedSector = Mockito.mock(AggregatedAllocationBySectorDto.class);
        Mockito.when(groupedSector.getName()).thenReturn("Australian Property");
        Mockito.when(groupedSector.getAllocations()).thenReturn(allocations);

        List<AllocationBySectorDto> allocationBySectorDtos = Arrays.asList((AllocationBySectorDto) cashSector,
                (AllocationBySectorDto) groupedSector);
        allocationDto = new KeyedAllocBySectorDto("", allocationBySectorDtos, key);
        return allocationDto;
    }

    @Test
    public void testAssetAllocationAssetClassReport() {
        params.put(EFFECTIVE_DATE, effectiveDate);
        params.put(INCLUDE_EXTERNAL, "false");
        params.put(GROUP_BY_NAME, "industrySubSector");
        Assert.assertEquals(AllocationGroupType.INDUSTRY_SUB_SECTOR, AllocationGroupType.forCode("industrySubSector"));
        Assert.assertEquals("industrySubSector", AllocationGroupType.forCode("industrySubSector").getCode());
        Assert.assertEquals(null, AllocationGroupType.forCode("industryClass"));
        Assert.assertNotNull(assetAllocationReport.getData(params, dataCollections));
    }

    @Test
    public void getDisclaimer() {
        assertEquals("MockString", assetAllocationReport.getDisclaimer(params, dataCollections));
        verify(cmsService).getContent("DS-IP-0002");
    }

    @Test
    public void getDisclaimer_forDirect() {
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(ServiceErrors.class))).thenReturn(UserExperience.DIRECT);
        assertEquals("MockStringDirect", assetAllocationReport.getDisclaimer(params, dataCollections));
        verify(cmsService).getContent("DS-IP-0200");
    }

    @Test
    public void getExternalAssetMarker() {
        when(cmsService.getContent(anyString())).thenReturn("cms/rasterImage.png");
        final Renderable logo = assetAllocationReport.getExternalAssetMarker();
        Mockito.verify(cmsService).getContent(Mockito.eq("externalAssetMarker"));
        assertThat(logo, notNullValue());
    }
}
