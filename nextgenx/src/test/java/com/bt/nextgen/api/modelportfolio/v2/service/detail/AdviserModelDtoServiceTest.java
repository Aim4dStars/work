package com.bt.nextgen.api.modelportfolio.v2.service.detail;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.ModelPortfolioType;
import com.bt.nextgen.api.modelportfolio.v2.model.detail.ModelPortfolioDetailDto;
import com.bt.nextgen.api.modelportfolio.v2.model.detail.TargetAllocationDto;
import com.bt.nextgen.api.modelportfolio.v2.util.common.AdviserModelHelper;
import com.bt.nextgen.api.modelportfolio.v2.util.common.ModelPortfolioHelper;
import com.bt.nextgen.api.modelportfolio.v2.validation.ModelPortfolioDtoErrorMapper;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioDetail;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioDetailIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioStatus;
import com.bt.nextgen.service.integration.modelportfolio.detail.TargetAllocation;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdviserModelDtoServiceTest {

    @InjectMocks
    private ModelPortfolioDetailDtoServiceImpl mpDetailDtoService;

    @Mock
    private ModelPortfolioDetailIntegrationService modelPortfolioService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private ModelPortfolioHelper helper;

    @Mock
    private AdviserModelHelper advModelhelper;

    @Mock
    private ModelPortfolioDtoErrorMapper errorMapper;

    @Mock
    private InvestmentPolicyStatementIntegrationService invPolicyService;

    private ModelPortfolioDetailDto modelDto;
    private ModelPortfolioDetailDto modelDto1;
    private DateTime openDate;

    @Before
    public void setUp() throws Exception {
        when(advModelhelper.generateUniqueModelId(any(String.class), any(ServiceErrors.class))).thenReturn("1234567890AB");
        when(userProfileService.isInvestmentManager()).thenReturn(false);
        when(userProfileService.isDealerGroup()).thenReturn(false);
        when(userProfileService.isPortfolioManager()).thenReturn(false);
        when(userProfileService.isAdviser()).thenReturn(true);
        when(userProfileService.getPositionId()).thenReturn("adviserId");

        openDate = DateTime.now();
        // Mock Dto with empty TAA
        modelDto = mock(ModelPortfolioDetailDto.class);
        when(modelDto.getKey()).thenReturn(new ModelPortfolioKey("modelKey"));
        when(modelDto.getModelName()).thenReturn("modelName");
        when(modelDto.getModelCode()).thenReturn("modelIdentifier");
        when(modelDto.getStatus()).thenReturn(ModelPortfolioStatus.NEW.name());
        when(modelDto.getOpenDate()).thenReturn(openDate);
        when(modelDto.getModelStructure()).thenReturn("modelStructure");
        when(modelDto.getInvestmentStyle()).thenReturn("btfg$defnv");
        when(modelDto.getModelAssetClass()).thenReturn("btfg$realest_au");
        when(modelDto.getModelType()).thenReturn("modelType");
        when(modelDto.getModelConstruction()).thenReturn("Floating");
        when(modelDto.getPortfolioConstructionFee()).thenReturn(BigDecimal.ONE);
        when(modelDto.getMinimumInvestment()).thenReturn(BigDecimal.ONE);
        when(modelDto.getAccountType()).thenReturn(null);
        when(modelDto.getOtherInvestmentStyle()).thenReturn("otherInvestmentStyle");
        when(modelDto.getModelDescription()).thenReturn("modelDescription");
        // when(modelDto.getMinimumOrderAmount()).thenReturn(BigDecimal.ONE);
        // when(modelDto.getMinimumOrderPercent()).thenReturn(BigDecimal.ONE);

        // Mock Dto with TAA
        modelDto1 = mock(ModelPortfolioDetailDto.class);
        when(modelDto1.getKey()).thenReturn(new ModelPortfolioKey("modelKey1"));
        when(modelDto1.getModelName()).thenReturn("modelName");
        when(modelDto1.getModelCode()).thenReturn("modelIdentifier");
        when(modelDto1.getStatus()).thenReturn(ModelPortfolioStatus.NEW.name());
        when(modelDto1.getOpenDate()).thenReturn(openDate);
        when(modelDto1.getModelStructure()).thenReturn("modelStructure");
        when(modelDto1.getInvestmentStyle()).thenReturn("btfg$defnv");
        when(modelDto1.getModelAssetClass()).thenReturn("btfg$realest_au");
        when(modelDto1.getModelType()).thenReturn("modelType");
        when(modelDto1.getPortfolioConstructionFee()).thenReturn(BigDecimal.ONE);
        when(modelDto1.getMinimumInvestment()).thenReturn(BigDecimal.ONE);
        when(modelDto1.getAccountType()).thenReturn(ModelType.INVESTMENT.getCode());

        // target allocations
        TargetAllocationDto taa = mock(TargetAllocationDto.class);
        when(taa.getAssetClass()).thenReturn("assetClass");
        when(taa.getMinimumWeight()).thenReturn(BigDecimal.ZERO);
        when(taa.getMaximumWeight()).thenReturn(BigDecimal.ONE);
        when(taa.getNeutralPos()).thenReturn(BigDecimal.TEN);
        when(taa.getIndexAsset()).thenReturn(null);

        AssetDto assetDto = mock(AssetDto.class);
        when(assetDto.getAssetId()).thenReturn("assetId");
        TargetAllocationDto taa1 = mock(TargetAllocationDto.class);
        when(taa1.getAssetClass()).thenReturn("assetClass");
        when(taa1.getMinimumWeight()).thenReturn(BigDecimal.ZERO);
        when(taa1.getMaximumWeight()).thenReturn(BigDecimal.ONE);
        when(taa1.getNeutralPos()).thenReturn(BigDecimal.TEN);
        when(taa1.getIndexAsset()).thenReturn(assetDto);

        List<TargetAllocationDto> taaList = new ArrayList<>();
        taaList.add(taa);
        taaList.add(taa1);
        when(modelDto1.getTargetAllocations()).thenReturn(taaList);

        // Mock ModelPortfolioHelper service
        Map<String, AssetDto> assetMap = new HashMap<>();
        AssetDto asset = mock(AssetDto.class);
        when(asset.getAssetId()).thenReturn("assetId");
        assetMap.put("assetId", asset);
        when(helper.getAssetDtoMap(Mockito.anyList(), any(ServiceErrors.class))).thenReturn(assetMap);
        when(helper.getTargetAllocationDto(any(TargetAllocation.class), any(AssetDto.class), Mockito.anyBoolean())).thenReturn(
                taa1);

        // Mock errorMapper
        List<DomainApiErrorDto> apiErrors = new ArrayList<>();
        when(errorMapper.map(Mockito.anyList())).thenReturn(apiErrors);
    }


    @Test
    public void testToModel_adviserModelWithNoMinimumTradeSet_defaultMinTradeAmountSet() {
        when(advModelhelper.generateUniqueModelId(any(String.class), any(ServiceErrors.class))).thenReturn("1234567890AB");
        when(userProfileService.isInvestmentManager()).thenReturn(false);
        when(userProfileService.isDealerGroup()).thenReturn(false);
        when(userProfileService.isPortfolioManager()).thenReturn(false);
        when(userProfileService.isAdviser()).thenReturn(true);
        when(userProfileService.getPositionId()).thenReturn("adviserId");


        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        ModelPortfolioDetail model = mpDetailDtoService.toModel(modelDto, errors);
        Assert.assertEquals("adviserId", model.getInvestmentManagerId().getId());
        Assert.assertEquals(ModelPortfolioType.PREFERRED.getIntlId(), model.getMpSubType());

        Assert.assertNull(model.getMinimumTradeAmount());
        Assert.assertNull(model.getMinimumTradePercent());

    }

    @Test
    public void testToModel_adviserModelWithMinimumTradeSet_modelConstructed() {
        when(modelDto.getMinimumOrderAmount()).thenReturn(BigDecimal.ONE);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        ModelPortfolioDetail model = mpDetailDtoService.toModel(modelDto, errors);
        Assert.assertEquals(BigDecimal.ONE, model.getMinimumTradeAmount());
        Assert.assertNull(model.getMinimumTradePercent());

        when(modelDto.getMinimumOrderPercent()).thenReturn(BigDecimal.ONE);
        when(modelDto.getMinimumOrderAmount()).thenReturn(null);
        model = mpDetailDtoService.toModel(modelDto, errors);
        Assert.assertEquals(BigDecimal.ONE, model.getMinimumTradePercent());
    }
}
