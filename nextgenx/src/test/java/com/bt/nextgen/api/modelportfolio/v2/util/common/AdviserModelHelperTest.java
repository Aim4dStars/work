package com.bt.nextgen.api.modelportfolio.v2.util.common;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioSummaryDto;
import com.bt.nextgen.api.modelportfolio.v2.service.ModelPortfolioSummaryDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.ModelPortfolioSummaryImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.common.IpsStatus;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.service.integration.broker.Broker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class AdviserModelHelperTest {

    @InjectMocks
    private AdviserModelHelper helperService;

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
        Asset a1 = Mockito.mock(AssetImpl.class);
        Mockito.when(a1.getAssetId()).thenReturn("id1");

        Asset a2 = Mockito.mock(AssetImpl.class);
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
    public void testGenerateUniqueModelId_withMultipleBrokerName() {
        Broker broker = Mockito.mock(Broker.class);
        Mockito.when(broker.getPositionName()).thenReturn("brokername");
        Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(broker);

        String uniqueId = helperService.generateUniqueModelId("PREFIX", new ServiceErrorsImpl());
        Assert.assertTrue(uniqueId.startsWith("PRBRO"));
        Assert.assertTrue(uniqueId.length() == 12);

        // Test where broker name has less than 3 characters
        Mockito.when(broker.getPositionName()).thenReturn("");

        uniqueId = helperService.generateUniqueModelId("PREFIX", new ServiceErrorsImpl());
        Assert.assertTrue(uniqueId.startsWith("PRZZZ"));
        Assert.assertTrue(uniqueId.length() == 12);
    }

    @Test
    public void testGenerateUniqueModelId_InvestmentManagerBrokerNotFound() {
        Mockito.when(userProfileService.getInvestmentManager(Mockito.any(ServiceErrors.class))).thenReturn(null);

        String uniqueId = helperService.generateUniqueModelId("PREFIX", new ServiceErrorsImpl());
        Assert.assertTrue(uniqueId.startsWith("PRZZZ"));
        Assert.assertTrue(uniqueId.length() == 12);
    }

    @Test
    public void testGenerateUniqueModelId_idIsUniqueWithinSeconds() {
        Broker broker = Mockito.mock(Broker.class);
        Mockito.when(broker.getPositionName()).thenReturn("brokername");
        Mockito.when(userProfileService.getInvestmentManager(Mockito.any(ServiceErrors.class))).thenReturn(broker);

        Set<String> idSet = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            String uniqueId = helperService.generateUniqueModelId("PRE", new ServiceErrorsImpl());
            Assert.assertTrue(idSet.add(uniqueId));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }

        Assert.assertTrue(10 == idSet.size());
    }
}
