package com.bt.nextgen.reports.account.allocation.exposure;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.portfolio.v3.model.allocation.exposure.AggregateAllocationByExposureDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.exposure.AllocationByExposureDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.exposure.AssetAllocationByExposureDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.exposure.HoldingAllocationByExposureDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.exposure.KeyedAllocByExposureDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.service.allocation.AllocationByExposureDtoService;
import com.bt.nextgen.api.portfolio.v3.service.allocation.HoldingSource;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashHoldingImpl;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationServiceFactory;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;

@RunWith(MockitoJUnitRunner.class)
public class ExposureAllocationReportTest {

    @InjectMocks
    private ExposureAllocationReport exposureAllocationReport;

    @Mock
    private AllocationByExposureDtoService allocationDtoService;

    @Mock
    private CmsService cmsService;

    @Mock
    private BrokerHelperService brokerHelperService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private AccountIntegrationServiceFactory accountIntegrationServiceFactory;

    private Map<String, Object> params;
    private Map<String, Object> dataCollections;

    @Before
    public void setup() {
        dataCollections = new HashMap<>();
        params = new HashMap<>();
        params.put("account-id", EncodedString.fromPlainText("123456").toString());
        CashAccountValuationImpl cashAccount = new CashAccountValuationImpl();
        AssetImpl asset = new AssetImpl();
        asset.setAssetCode(null);
        asset.setAssetName("BT Cash");
        asset.setAssetId("124223");
        asset.setAssetClass(AssetClass.CASH);        
        CashHoldingImpl cashHolding = new CashHoldingImpl();
        cashHolding.setAccountName("accountName");
        cashHolding.setAvailableBalance(BigDecimal.valueOf(10000));
        cashHolding.setMarketValue(BigDecimal.valueOf(10000));
        cashHolding.setAsset(asset);
        cashHolding.setExternal(Boolean.FALSE);
        List<AccountHolding> cashList = new ArrayList<>();
        cashList.add(cashHolding);
        cashAccount.addHoldings(cashList);
        HoldingSource cashHoldingSource = new HoldingSource(asset, cashHolding, cashAccount);
        Map<String, BigDecimal> allocMap = new HashMap<String, BigDecimal>();
        allocMap.put("CASH", new BigDecimal(1));
        HoldingAllocationByExposureDto holdingAllocationByExposureDto = new HoldingAllocationByExposureDto(
                Collections.singletonList(cashHoldingSource), new BigDecimal(10000), allocMap, false);
        List<AllocationByExposureDto> allocations = new ArrayList<>();
        allocations.add(holdingAllocationByExposureDto);
        AssetAllocationByExposureDto assetAllocationByExposureDto = new AssetAllocationByExposureDto(asset, allocations);
        List<AllocationByExposureDto> assetAllocations = new ArrayList<>();
        assetAllocations.add(assetAllocationByExposureDto);
        AggregateAllocationByExposureDto aggAllocationDto = new AggregateAllocationByExposureDto("Cash", assetAllocations);
        List<AllocationByExposureDto> aggAllocations = new ArrayList<>();
        aggAllocations.add(aggAllocationDto);
        KeyedAllocByExposureDto keyAllocByExposureDto = new KeyedAllocByExposureDto("Allocation", aggAllocations,
                new DatedValuationKey("223232", new DateTime(), false), false);

        // Mock content service
        when(cmsService.getContent("DS-IP-0002")).thenReturn("MockString");
        when(cmsService.getContent("DS-IP-0200")).thenReturn("MockStringDirect");

        when(allocationDtoService.find(any(DatedValuationKey.class), any(ServiceErrorsImpl.class))).thenReturn(keyAllocByExposureDto);
        when(accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(mock(WrapAccountDetail.class));
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(ServiceErrors.class))).thenReturn(UserExperience.ADVISED);
        when(accountIntegrationServiceFactory.getInstance(anyString())).thenReturn(accountIntegrationService);
    }

    @Test
    public void testGetStartDate() {
        params.put("effective-date", "2014-09-09");
        String date = exposureAllocationReport.getStartDate(params);
        assertEquals("09 Sep 2014", date);
    }

    @Test
    public void testExposureAllocationExposureReport() {
        params.put("start-date", "2014-09-09");
        params.put("exposureType", "dollar");
        List<ExposureAllocationData> exposureData = (List<ExposureAllocationData>) exposureAllocationReport.getData(params, dataCollections);
        Assert.assertNotNull(exposureData);
        Assert.assertEquals(1, exposureData.size());

        ExposureAllocationData keyAllocExposureData = exposureData.get(0);

        ExposureAllocationData aggAllocExposureData = keyAllocExposureData.getChildren().get(0);

        Assert.assertTrue(aggAllocExposureData.getName().contains("Cash"));

        ExposureAllocationData assetExposureAllocationData = aggAllocExposureData.getChildren().get(0);

        Assert.assertEquals(false, assetExposureAllocationData.getIsExternal());
        Assert.assertTrue(assetExposureAllocationData.getName().contains("BT Cash"));
        Assert.assertEquals("$10,000.00", assetExposureAllocationData.getCashValue());
        Assert.assertEquals("-", assetExposureAllocationData.getAustralianShareValue());
        Assert.assertEquals("-", assetExposureAllocationData.getAustralianFixedValue());
        Assert.assertEquals("-", assetExposureAllocationData.getAustralianFloatingValue());
        Assert.assertEquals("-", assetExposureAllocationData.getAustralianPropertyValue());
        Assert.assertEquals("-", assetExposureAllocationData.getAustralianShareValue());
        Assert.assertEquals("-", assetExposureAllocationData.getInternationalFixedValue());
        Assert.assertEquals("-", assetExposureAllocationData.getInternationalPropertyValue());
        Assert.assertEquals("-", assetExposureAllocationData.getInternationalShareValue());
        Assert.assertEquals("-", assetExposureAllocationData.getAlternativesValue());

    }

    @Test
    public void testGetDisclaimer() {
        assertEquals("MockString", exposureAllocationReport.getDisclaimer(params, dataCollections));
        verify(cmsService, times(1)).getContent("DS-IP-0002");
    }

    @Test
    public void testGetDisclaimer_forDirect() {
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(ServiceErrors.class))).thenReturn(UserExperience.DIRECT);
        assertEquals("MockStringDirect", exposureAllocationReport.getDisclaimer(params, dataCollections));
        verify(cmsService, times(1)).getContent("DS-IP-0200");
    }
}
