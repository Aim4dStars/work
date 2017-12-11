package com.bt.nextgen.api.modelportfolio.v2.util.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioSummaryDto;
import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.ModelPortfolioType;
import com.bt.nextgen.api.modelportfolio.v2.model.detail.TargetAllocationDto;
import com.bt.nextgen.api.modelportfolio.v2.service.ModelPortfolioSummaryDtoService;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.modelportfolio.ModelPortfolioSummaryImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.detail.TargetAllocationImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.ips.IpsSummaryDetails;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioAssetAllocation;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.UploadAssetCodeEnum;
import com.bt.nextgen.service.integration.modelportfolio.common.IpsStatus;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioDetail;
import com.bt.nextgen.service.integration.modelportfolio.detail.TargetAllocation;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.client.asset.dto.AssetClientImpl;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.service.integration.broker.Broker;

@RunWith(MockitoJUnitRunner.class)
public class ModelPortfolioHelperTest {

    @InjectMocks
    private ModelPortfolioHelper helperService;

    @Mock
    private ModelPortfolioSummaryDtoService modelPortfolioSummaryService;

    @Mock
    private AssetIntegrationService assetService;

    @Mock
    private AssetDtoConverter assetDtoConverter;

    @Mock
    private InvestmentPolicyStatementIntegrationService invPolicyService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private ModelPortfolioIntegrationService mpService;

    @Mock
    private ShadowPortfolioHelper shdPortfolioHelper;

    private FailFastErrorsImpl errorsImpl = new FailFastErrorsImpl();

    @Before
    public void setup() throws Exception {
        // Setup mock for modelPortfolioDtoSummary list.
        ModelPortfolioSummaryImpl summaryModel_1 = Mockito.mock(ModelPortfolioSummaryImpl.class);
        Mockito.when(summaryModel_1.getModelKey()).thenReturn(IpsKey.valueOf("modelKey_1"));
        Mockito.when(summaryModel_1.getIpsOrderId()).thenReturn("ipsOrderId_1");
        Mockito.when(summaryModel_1.getModelOrderId()).thenReturn("modelOrderId_1");
        Mockito.when(summaryModel_1.getAssetClass()).thenReturn("assetClass");
        Mockito.when(summaryModel_1.getStatus()).thenReturn(IpsStatus.OPEN);
        Mockito.when(summaryModel_1.getAccountType()).thenReturn(ModelType.INVESTMENT);

        ModelPortfolioSummaryImpl summaryModel_2 = Mockito.mock(ModelPortfolioSummaryImpl.class);
        Mockito.when(summaryModel_2.getModelKey()).thenReturn(IpsKey.valueOf("modelKey_2"));
        Mockito.when(summaryModel_2.getIpsOrderId()).thenReturn("ipsOrderId_2");
        Mockito.when(summaryModel_2.getModelOrderId()).thenReturn("modelOrderId_2");
        Mockito.when(summaryModel_2.getAssetClass()).thenReturn("assetClass");
        Mockito.when(summaryModel_2.getStatus()).thenReturn(IpsStatus.OPEN);
        Mockito.when(summaryModel_2.getAccountType()).thenReturn(ModelType.INVESTMENT);

        List<ModelPortfolioSummaryDto> dtoList = new ArrayList<>();
        dtoList.add(new ModelPortfolioSummaryDto(summaryModel_1, AssetType.OTHER));
        dtoList.add(new ModelPortfolioSummaryDto(summaryModel_2, AssetType.OTHER));

        Mockito.when(modelPortfolioSummaryService.findAll(Mockito.any(ServiceErrors.class))).thenReturn(dtoList);

        // Mock for assetService
        Asset a1 = Mockito.mock(AssetClientImpl.class);
        Mockito.when(a1.getAssetId()).thenReturn("id1");

        Asset a2 = Mockito.mock(AssetClientImpl.class);
        Mockito.when(a2.getAssetId()).thenReturn("id2");

        Map<String, Asset> assetMap = new HashMap<>();
        assetMap.put("id1", a1);
        assetMap.put("id2", a2);
        Mockito.when(assetService.loadAssets(Mockito.anyCollection(), Mockito.any(ServiceErrors.class))).thenReturn(assetMap);

        // Mock for assetDtoConverter.
        AssetDto dto1 = Mockito.mock(AssetDto.class);
        Mockito.when(dto1.getAssetId()).thenReturn("id1");

        AssetDto dto2 = Mockito.mock(AssetDto.class);
        Mockito.when(dto2.getAssetId()).thenReturn("id2");

        Map<String, AssetDto> assetDtoMap = new HashMap<>();
        assetDtoMap.put("id1", dto1);
        assetDtoMap.put("id2", dto2);

        Mockito.when(assetDtoConverter.toAssetDto(Mockito.anyMap(), Mockito.anyMap())).thenReturn(assetDtoMap);
    }

    @Test
    public void test_getAssetDtoMap() {
        List<TargetAllocation> taaList = new ArrayList<>();
        TargetAllocation ta = Mockito.mock(TargetAllocationImpl.class);
        Mockito.when(ta.getIndexAssetId()).thenReturn("id1");
        taaList.add(ta);
        Map<String, AssetDto> map = helperService.getAssetDtoMap(taaList, errorsImpl);
        Assert.assertNotNull(map);
    }

    @Test
    public void test_getNullIndexAssetDtoMap() {
        List<TargetAllocation> taaList = new ArrayList<>();
        TargetAllocation ta = Mockito.mock(TargetAllocationImpl.class);
        Mockito.when(ta.getIndexAssetId()).thenReturn(null);
        taaList.add(ta);
        Map<String, AssetDto> map = helperService.getAssetDtoMap(taaList, errorsImpl);
        Assert.assertNotNull(map);
    }

    @Test
    public void test_GetTargetAllocationDto() {
        TargetAllocation taa = Mockito.mock(TargetAllocationImpl.class);
        Mockito.when(taa.getAssetClass()).thenReturn(null);
        Mockito.when(taa.getMinimumWeight()).thenReturn(BigDecimal.valueOf(0.20d));
        Mockito.when(taa.getMaximumWeight()).thenReturn(BigDecimal.valueOf(0.20d));
        Mockito.when(taa.getNeutralPos()).thenReturn(BigDecimal.valueOf(0.20d));

        TargetAllocationDto taaDto = helperService.getTargetAllocationDto(taa, null, Boolean.TRUE);
        Assert.assertNull(taaDto.getAssetClass());
        Assert.assertNull(taaDto.getAssetClassName());
        Assert.assertTrue(taaDto.getMinimumWeight().doubleValue() == 20.0d);
        Assert.assertTrue(taaDto.getMaximumWeight().doubleValue() == 20.0d);
        Assert.assertTrue(taaDto.getNeutralPos().doubleValue() == 20.0d);
        
        taa = Mockito.mock(TargetAllocationImpl.class);
        Mockito.when(taa.getAssetClass()).thenReturn("eq_au");
        Mockito.when(taa.getMinimumWeight()).thenReturn(BigDecimal.valueOf(0.20d));
        Mockito.when(taa.getMaximumWeight()).thenReturn(BigDecimal.valueOf(0.20d));
        Mockito.when(taa.getNeutralPos()).thenReturn(BigDecimal.valueOf(0.20d));

        taaDto = helperService.getTargetAllocationDto(taa, null, Boolean.FALSE);
        Assert.assertEquals("eq_au", taaDto.getAssetClass());
        Assert.assertEquals("Australian shares", taaDto.getAssetClassName());
        Assert.assertTrue(taaDto.getMinimumWeight().doubleValue() == 0.20d);
        Assert.assertTrue(taaDto.getMaximumWeight().doubleValue() == 0.20d);
        Assert.assertTrue(taaDto.getNeutralPos().doubleValue() == 0.20d);
        
    }

    @Test
    public void test_GetTargetAllocationDtoWithMultiplier() {
        TargetAllocation taa = Mockito.mock(TargetAllocationImpl.class);
        Mockito.when(taa.getAssetClass()).thenReturn("assetClass");
        Mockito.when(taa.getMinimumWeight()).thenReturn(BigDecimal.valueOf(20d));
        Mockito.when(taa.getMaximumWeight()).thenReturn(BigDecimal.valueOf(20d));
        Mockito.when(taa.getNeutralPos()).thenReturn(BigDecimal.valueOf(20d));

        TargetAllocationDto taaDto = helperService.getTargetAllocationDto(taa, null, Boolean.TRUE);
        double val = 20.0d;
        Assert.assertTrue(taaDto.getMinimumWeight().doubleValue() == val);
        Assert.assertTrue(taaDto.getMaximumWeight().doubleValue() == val);
        Assert.assertTrue(taaDto.getNeutralPos().doubleValue() == val);
    }

    @Test
    public void test_getAllocationAssetMap_forCashAsset() {

        ModelPortfolioAssetAllocation alloc = Mockito.mock(ModelPortfolioAssetAllocation.class);
        Mockito.when(alloc.getAssetCode()).thenReturn("assetId1");

        Map<String, Asset> assetMap = new HashMap<>();
        AssetClientImpl a1 = new AssetClientImpl();
        a1.setAssetId("assetId1");
        a1.setAssetType(AssetType.CASH);
        assetMap.put("assetId1", a1);
        Mockito.when(assetService.loadAssets(Mockito.anyList(), Mockito.any(ServiceErrors.class))).thenReturn(assetMap);

        Map<String, Asset> resultMap = helperService.getAllocationAssetMap(Collections.singletonList(alloc),
                ModelType.INVESTMENT, ModelPortfolioType.TAILORED.getIntlId(), new FailFastErrorsImpl());

        Assert.assertTrue(resultMap.size() == 1);
        Assert.assertEquals(UploadAssetCodeEnum.TMP_CASH.value(), resultMap.get("assetId1").getAssetCode());

        a1.setAssetCode(null);        
        assetMap.put("assetId1", a1);
        resultMap = helperService.getAllocationAssetMap(Collections.singletonList(alloc), ModelType.SUPERANNUATION,
                ModelPortfolioType.TAILORED.getIntlId(), new FailFastErrorsImpl());

        Assert.assertTrue(resultMap.size() == 1);
        Assert.assertEquals(UploadAssetCodeEnum.SUPER_TMP_CASH.value(), resultMap.get("assetId1").getAssetCode());

        a1.setAssetCode(null);
        resultMap = helperService.getAllocationAssetMap(Collections.singletonList(alloc), ModelType.INVESTMENT,
                ModelPortfolioType.PREFERRED.getIntlId(), new FailFastErrorsImpl());

        Assert.assertTrue(resultMap.size() == 1);
        Assert.assertEquals(UploadAssetCodeEnum.ADVISER_MODEL_CASH.value(), resultMap.get("assetId1").getAssetCode());

        AssetClientImpl a2 = new AssetClientImpl();
        a2.setAssetId("assetId2");
        a2.setAssetType(AssetType.SHARE);
        assetMap.put("assetId2", a2);
        resultMap = helperService.getAllocationAssetMap(Collections.singletonList(alloc), ModelType.INVESTMENT,
                ModelPortfolioType.PREFERRED.getIntlId(), new FailFastErrorsImpl());
        Assert.assertTrue(resultMap.size() == 2);
    }

    @Test
    public void testGetIpsSummaryDetails_whenResultContainsIpsKey_thenDetailsReturned() {
        Map<IpsKey, ModelPortfolioDetail> modelDetailsMap = mockModelDetailsMap();
        Mockito.when(invPolicyService.getModelDetails(Mockito.anyListOf(IpsKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(modelDetailsMap);

        IpsSummaryDetails ipsDetails = Mockito.mock(IpsSummaryDetails.class);
        Mockito.when(ipsDetails.getModelKey()).thenReturn(IpsKey.valueOf("123"));

        IpsSummaryDetails otherIpsDetails = Mockito.mock(IpsSummaryDetails.class);
        Mockito.when(otherIpsDetails.getModelKey()).thenReturn(IpsKey.valueOf("999"));

        Mockito.when(invPolicyService.getDealerGroupIpsSummary(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(otherIpsDetails, ipsDetails));

        IpsSummaryDetails details = helperService.getIpsSummaryDetails(new ModelPortfolioKey("123"), errorsImpl);

        Assert.assertNotNull(details);
        Assert.assertEquals("123", details.getModelKey().getId());
    }

    @Test
    public void testGetIpsSummaryDetails_whenResultDoesNotContainIpsKey_thenNoDetailsReturned() {
        Map<IpsKey, ModelPortfolioDetail> modelDetailsMap = mockModelDetailsMap();
        Mockito.when(invPolicyService.getModelDetails(Mockito.anyListOf(IpsKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(modelDetailsMap);

        IpsSummaryDetails details = helperService.getIpsSummaryDetails(new ModelPortfolioKey("123"), errorsImpl);
        Assert.assertNull(details);
    }

    @Test
    public void testGetIpsSummaryDetails_whenResultNull_thenNoDetailsReturned() {
        IpsSummaryDetails details = helperService.getIpsSummaryDetails(new ModelPortfolioKey("123"), errorsImpl);
        Assert.assertNull(details);
    }

    @Test
    public void testGetIpsSummaryDetails_whenDetailsNotFound_thenNoDetailsReturned() {
        Map<IpsKey, ModelPortfolioDetail> modelDetailsMap = mockModelDetailsMap();
        Mockito.when(invPolicyService.getModelDetails(Mockito.anyListOf(IpsKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(modelDetailsMap);

        IpsSummaryDetails details = helperService.getIpsSummaryDetails(new ModelPortfolioKey("123"), errorsImpl);
        Assert.assertNull(details);
    }

    @Test
    public void testGetIpsSummaryDetails_whenResponseNull_thenNoDetailsReturned() {
        Map<IpsKey, ModelPortfolioDetail> modelDetailsMap = mockModelDetailsMap();
        Mockito.when(invPolicyService.getModelDetails(Mockito.anyListOf(IpsKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(modelDetailsMap);

        Mockito.when(invPolicyService.getDealerGroupIpsSummary(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(null);

        IpsSummaryDetails details = helperService.getIpsSummaryDetails(new ModelPortfolioKey("123"), errorsImpl);
        Assert.assertNull(details);
    }

    @Test
    public void testGetIpsSummaryDetails_whenIpsDetailsNotFound_thenNoDetailsReturned() {
        Map<IpsKey, ModelPortfolioDetail> modelDetailsMap = mockModelDetailsMap();
        Mockito.when(invPolicyService.getModelDetails(Mockito.anyListOf(IpsKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(modelDetailsMap);

        IpsSummaryDetails otherIpsDetails = Mockito.mock(IpsSummaryDetails.class);
        Mockito.when(otherIpsDetails.getModelKey()).thenReturn(IpsKey.valueOf("999"));
        Mockito.when(invPolicyService.getDealerGroupIpsSummary(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(otherIpsDetails));

        IpsSummaryDetails details = helperService.getIpsSummaryDetails(new ModelPortfolioKey("123"), errorsImpl);
        Assert.assertNull(details);
    }

    @Test
    public void testAccess_whenGetCurrentBrokerAsInvestmentManager_thenSucceeds() {
        Broker broker = Mockito.mock(Broker.class);
        Mockito.when(broker.getKey()).thenReturn(BrokerKey.valueOf("brokerKey"));

        Mockito.when(userProfileService.getInvestmentManager(Mockito.any(ServiceErrors.class))).thenReturn(broker);

        BrokerKey brokerKey = helperService.getCurrentBroker(errorsImpl);
        Assert.assertEquals(BrokerKey.valueOf("brokerKey"), brokerKey);
    }

    @Test(expected = BadRequestException.class)
    public void testAccess_whenGetCurrentBrokerAsAdviser_thenBadRequest() {
        Mockito.when(userProfileService.getInvestmentManager(Mockito.any(ServiceErrors.class))).thenReturn(null);
        helperService.getCurrentBroker(errorsImpl);
    }

    private Map<IpsKey, ModelPortfolioDetail> mockModelDetailsMap() {
        ModelPortfolioDetail modelDetail = Mockito.mock(ModelPortfolioDetail.class);
        Mockito.when(modelDetail.getInvestmentManagerId()).thenReturn(BrokerKey.valueOf("600"));
        Mockito.when(modelDetail.getId()).thenReturn("123");

        ModelPortfolioDetail otherModelDetail = Mockito.mock(ModelPortfolioDetail.class);
        Mockito.when(otherModelDetail.getId()).thenReturn("999");

        Map<IpsKey, ModelPortfolioDetail> modelDetailsMap = new HashMap<>();
        modelDetailsMap.put(IpsKey.valueOf("999"), otherModelDetail);
        modelDetailsMap.put(IpsKey.valueOf("123"), modelDetail);

        return modelDetailsMap;
    }

    public void testGetFloatingTargetAllocationMap() {
        Mockito.when(
                shdPortfolioHelper.getFloatingTargetAllocationMap(Mockito.any(IpsKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(null);
        Map<String, BigDecimal> resultMap = helperService.getFloatingTargetAllocationMap(IpsKey.valueOf("123"), errorsImpl);
        Assert.assertNull(resultMap);
    }
}
