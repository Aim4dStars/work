package com.bt.nextgen.api.modelportfolio.v2.service.detail;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.ModelPortfolioType;
import com.bt.nextgen.api.modelportfolio.v2.model.detail.ModelPortfolioDetailDto;
import com.bt.nextgen.api.modelportfolio.v2.model.detail.ModelPortfolioDetailDtoImpl;
import com.bt.nextgen.api.modelportfolio.v2.model.detail.TargetAllocationDto;
import com.bt.nextgen.api.modelportfolio.v2.util.common.AdviserModelHelper;
import com.bt.nextgen.api.modelportfolio.v2.util.common.ModelPortfolioHelper;
import com.bt.nextgen.api.modelportfolio.v2.validation.ModelPortfolioDtoErrorMapper;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.modelportfolio.detail.ModelPortfolioDetailImpl;
import com.bt.nextgen.service.integration.asset.IpsAssetClass;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.investment.InvestmentStyle;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ConstructionType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioDetail;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioDetailIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioStatus;
import com.bt.nextgen.service.integration.modelportfolio.detail.TargetAllocation;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Properties.class })
public class ModelPortfolioDetailDtoServiceTest {

    @InjectMocks
    private ModelPortfolioDetailDtoServiceImpl mpDetailDtoService;

    @Mock
    private ModelPortfolioDetailIntegrationService modelPortfolioService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private ModelPortfolioHelper helper;

    @Mock
    private ModelPortfolioDtoErrorMapper errorMapper;

    @Mock
    private InvestmentPolicyStatementIntegrationService invPolicyService;

    @Mock
    private AdviserModelHelper advModelhelper;

    private ModelPortfolioDetailDto modelDto;
    private ModelPortfolioDetailDto modelDto1;
    private DateTime openDate;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Properties.class);
        Mockito.when(Properties.getSafeBoolean("feature.model.tmpofferRemoval")).thenReturn(true);
        Broker dealer = mock(Broker.class);
        when(dealer.getKey()).thenReturn(BrokerKey.valueOf("dealerId"));
        when(userProfileService.getInvestmentManager(any(ServiceErrors.class))).thenReturn(dealer);
        when(userProfileService.getDealerGroupBroker()).thenReturn(dealer);

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
        when(modelDto.getMinimumOrderAmount()).thenReturn(BigDecimal.ONE);
        when(modelDto.getMinimumOrderPercent()).thenReturn(BigDecimal.ONE);

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
        when(modelDto1.getOtherInvestmentStyle()).thenReturn("otherInvestmentStyle");
        when(modelDto1.getModelDescription()).thenReturn("modelDescription");
        when(modelDto1.getMinimumOrderAmount()).thenReturn(null);
        when(modelDto1.getMinimumOrderPercent()).thenReturn(BigDecimal.ONE);

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
    public void testToModel_whenCalledByDG_thenDtoCreated() {
        Broker dealer = mock(Broker.class);
        when(dealer.getKey()).thenReturn(BrokerKey.valueOf("dealerId"));
        when(userProfileService.isDealerGroup()).thenReturn(true);
        when(userProfileService.getInvestmentManager(any(ServiceErrors.class))).thenReturn(dealer);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        ModelPortfolioDetail model = mpDetailDtoService.toModel(modelDto, errors);
        Assert.assertEquals("modelKey", model.getId());
        Assert.assertEquals(modelDto.getModelName(), model.getName());
        Assert.assertEquals(modelDto.getModelCode(), model.getSymbol());
        Assert.assertEquals(dealer.getKey(), model.getInvestmentManagerId());
        Assert.assertEquals(ModelPortfolioStatus.forName(modelDto.getStatus()), model.getStatus());
        Assert.assertEquals(openDate, model.getOpenDate());
        Assert.assertEquals(modelDto.getModelStructure(), model.getModelStructure());
        Assert.assertEquals(modelDto.getInvestmentStyle(), model.getInvestmentStyle());
        Assert.assertEquals(modelDto.getModelAssetClass(), model.getModelAssetClass());
        Assert.assertEquals(modelDto.getModelType(), model.getModelType());
        Assert.assertEquals(ConstructionType.FLOATING, model.getModelConstruction());
        Assert.assertEquals(modelDto.getModelType(), model.getModelType());
        Assert.assertEquals(modelDto.getPortfolioConstructionFee(), model.getPortfolioConstructionFee());
        Assert.assertEquals(modelDto.getMinimumInvestment(), model.getMinimumInvestment());
        Assert.assertEquals(modelDto.getAccountType(), model.getAccountType());
        Assert.assertEquals(ModelPortfolioType.TAILORED.getIntlId(), model.getMpSubType());
        Assert.assertTrue(model.getTargetAllocations().isEmpty());
    }

    @Test
    public void testToModel_whenCalledByPM_thenPMRelevantDtoFieldsCorrect() {
        Broker pm = mock(Broker.class);
        when(pm.getKey()).thenReturn(BrokerKey.valueOf("pmId"));
        when(userProfileService.isPortfolioManager()).thenReturn(true);
        when(userProfileService.getInvestmentManager(any(ServiceErrors.class))).thenReturn(pm);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        ModelPortfolioDetail model = mpDetailDtoService.toModel(modelDto, errors);
        Assert.assertEquals(pm.getKey(), model.getInvestmentManagerId());
        Assert.assertEquals(ModelPortfolioType.TAILORED.getIntlId(), model.getMpSubType());
    }

    @Test
    public void testToModel_whenCalledByIM_thenIMRelevantDtoFieldsCorrect() {
        Broker im = mock(Broker.class);
        when(im.getKey()).thenReturn(BrokerKey.valueOf("imId"));
        when(userProfileService.isInvestmentManager()).thenReturn(true);
        when(userProfileService.getInvestmentManager(any(ServiceErrors.class))).thenReturn(im);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        ModelPortfolioDetail model = mpDetailDtoService.toModel(modelDto, errors);
        Assert.assertEquals(im.getKey(), model.getInvestmentManagerId());
        Assert.assertEquals(ModelPortfolioType.TAILORED.getIntlId(), model.getMpSubType());
    }

    @Test
    public void testToModel_whenCalledByAdviser_thenModelSymbolCorrectlySet() {
        PowerMockito.mockStatic(Properties.class);
        Mockito.when(Properties.getSafeBoolean("feature.model.advisermodel")).thenReturn(true);

        when(userProfileService.isAdviser()).thenReturn(true);
        when(userProfileService.getPositionId()).thenReturn("adviserId");

        when(modelDto.getKey()).thenReturn(new ModelPortfolioKey("key"));
        when(modelDto.getModelCode()).thenReturn(null);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        ModelPortfolioDetail model = mpDetailDtoService.toModel(modelDto, errors);
        Assert.assertNull(model.getSymbol());

        when(modelDto.getKey()).thenReturn(null);
        when(modelDto.getModelCode()).thenReturn("modelCode");
        model = mpDetailDtoService.toModel(modelDto, errors);
        Assert.assertEquals(modelDto.getModelCode(), model.getSymbol());
        
        when(advModelhelper.generateUniqueModelId(any(String.class), any(ServiceErrors.class))).thenReturn("symbol");
        when(modelDto.getKey()).thenReturn(null);
        when(modelDto.getModelCode()).thenReturn(null);
        model = mpDetailDtoService.toModel(modelDto, errors);
        Assert.assertEquals("symbol", model.getSymbol());
    }

    @Test
    public void testToModel_whenCalledByAdviser_thenAdviserRelevantDtoFieldsCorrect() {
        PowerMockito.mockStatic(Properties.class);
        Mockito.when(Properties.getSafeBoolean("feature.model.advisermodel")).thenReturn(true);

        when(userProfileService.isAdviser()).thenReturn(true);
        when(userProfileService.getPositionId()).thenReturn("adviserId");

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        ModelPortfolioDetail model = mpDetailDtoService.toModel(modelDto, errors);
        Assert.assertEquals("adviserId", model.getInvestmentManagerId().getId());
        Assert.assertEquals(ModelPortfolioType.PREFERRED.getIntlId(), model.getMpSubType());
        Assert.assertEquals(BigDecimal.ONE, model.getMinimumTradeAmount());
        Assert.assertEquals(BigDecimal.ONE, model.getMinimumTradePercent());
    }

    @Test
    public void testToModel1_whenCalledByAdviser_thenAdviserRelevantDtoFieldsCorrect() {
        PowerMockito.mockStatic(Properties.class);
        Mockito.when(Properties.getSafeBoolean("feature.model.advisermodel")).thenReturn(true);

        when(userProfileService.isAdviser()).thenReturn(true);
        when(userProfileService.getPositionId()).thenReturn("adviserId");

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        ModelPortfolioDetail model = mpDetailDtoService.toModel(modelDto1, errors);
        Assert.assertEquals("adviserId", model.getInvestmentManagerId().getId());
        Assert.assertEquals(ModelPortfolioType.PREFERRED.getIntlId(), model.getMpSubType());
        Assert.assertNull(model.getMinimumTradeAmount());
        Assert.assertEquals(BigDecimal.ONE, model.getMinimumTradePercent());
    }

    
    @Test
    public void test_toModelWithTaa() {
        Broker dealer = mock(Broker.class);
        when(userProfileService.isDealerGroup()).thenReturn(true);
        when(userProfileService.getInvestmentManager(any(ServiceErrors.class))).thenReturn(dealer);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        ModelPortfolioDetail model = mpDetailDtoService.toModel(modelDto1, errors);
        List<TargetAllocationDto> dtoList = modelDto1.getTargetAllocations();
        List<TargetAllocation> modelList = model.getTargetAllocations();

        int sizeDto = dtoList.size();
        int sizeModel = modelList.size();
        Assert.assertEquals(sizeDto, sizeModel);
        test_targetAllocations(dtoList, modelList);
    }

    @Test
    public void test_submit_validDtoConstructed() {
        when(userProfileService.isDealerGroup()).thenReturn(true);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        ModelPortfolioDetail model = mpDetailDtoService.toModel(modelDto1, errors);
        when(modelPortfolioService.submitModelPortfolio(any(ModelPortfolioDetail.class), any(ServiceErrors.class))).thenReturn(
                model);

        ModelPortfolioDetailDto dto = mpDetailDtoService.submit(modelDto1, errors);
        Assert.assertNotNull(dto);
        Assert.assertEquals(modelDto1.getKey(), dto.getKey());
        Assert.assertEquals(modelDto1.getModelName(), dto.getModelName());
        Assert.assertEquals(modelDto1.getModelCode(), dto.getModelCode());
        Assert.assertEquals(modelDto1.getStatus(), dto.getStatus());
        Assert.assertEquals(modelDto1.getOpenDate(), dto.getOpenDate());
        Assert.assertEquals(modelDto1.getModelStructure(), dto.getModelStructure());
        Assert.assertEquals(modelDto1.getInvestmentStyle(), dto.getInvestmentStyle());
        Assert.assertEquals(InvestmentStyle.DEFENSIVE.getDescription(), dto.getInvestmentStyleName());
        Assert.assertEquals(modelDto1.getModelAssetClass(), dto.getModelAssetClass());
        Assert.assertEquals(IpsAssetClass.REALEST_AU.getDescription(), dto.getModelAssetClassName());
        Assert.assertEquals(modelDto1.getModelType(), dto.getModelType());
        Assert.assertNull(dto.getModelConstruction());
        Assert.assertEquals(modelDto1.getPortfolioConstructionFee(), dto.getPortfolioConstructionFee());
        Assert.assertEquals(modelDto1.getMinimumInvestment(), dto.getMinimumInvestment());
        Assert.assertEquals(modelDto1.getWarnings(), dto.getWarnings());
        Assert.assertEquals(modelDto1.getAccountType(), dto.getAccountType());
        int modelSize = modelDto1.getTargetAllocations().size();
        int dtoSize = dto.getTargetAllocations().size();
        Assert.assertEquals(dtoSize, modelSize);
    }

    @Test
    public void test_validate_validDtoConstructed() {
        when(userProfileService.isDealerGroup()).thenReturn(true);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        ModelPortfolioDetail model = mpDetailDtoService.toModel(modelDto1, errors);
        when(modelPortfolioService.validateModelPortfolio(any(ModelPortfolioDetail.class), any(ServiceErrors.class))).thenReturn(
                model);

        ModelPortfolioDetailDto dto = mpDetailDtoService.validate(modelDto1, errors);
        Assert.assertNotNull(dto);
        Assert.assertEquals(modelDto1.getKey(), dto.getKey());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_findModel() {
        when(userProfileService.isDealerGroup()).thenReturn(true);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        ModelPortfolioKey key = mock(ModelPortfolioKey.class);
        when(key.getModelId()).thenReturn("modelId");

        // InvestmentPolicyService returns null result.
        when(invPolicyService.getModelDetails(Mockito.anyList(), any(ServiceErrors.class))).thenReturn(null);
        ModelPortfolioDetailDto resultDto = mpDetailDtoService.find(key, errors);
        Assert.assertTrue(resultDto == null);

        // InvestmentPolicyService returns result where modelId is not found.
        Map<IpsKey, ModelPortfolioDetail> invResult = new HashMap<>();
        when(invPolicyService.getModelDetails(Mockito.anyList(), any(ServiceErrors.class))).thenReturn(invResult);
        resultDto = mpDetailDtoService.find(key, errors);
        Assert.assertTrue(resultDto == null);

        // Valid modelPortfolioDetail found.
        ModelPortfolioDetailImpl model = (ModelPortfolioDetailImpl) mpDetailDtoService.toModel(modelDto1, errors);
        model.setInvestmentStyle("activ");
        model.setModelAssetClass("btfg$eq_au");

        invResult.put(IpsKey.valueOf("modelId"), model);
        when(invPolicyService.getModelDetails(Mockito.anyList(), any(ServiceErrors.class))).thenReturn(invResult);
        resultDto = mpDetailDtoService.find(key, errors);
        Assert.assertTrue(resultDto != null);
    }

    @Test
    public void test_updateModelPortfolio() {
        when(userProfileService.isDealerGroup()).thenReturn(true);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        ModelPortfolioDetail model = mpDetailDtoService.toModel(modelDto1, errors);
        when(modelPortfolioService.submitModelPortfolio(any(ModelPortfolioDetail.class), any(ServiceErrors.class))).thenReturn(
                model);

        ModelPortfolioDetailDto dto = mpDetailDtoService.update(modelDto1, errors);
        Assert.assertNotNull(dto);
        Assert.assertEquals(modelDto1.getKey(), dto.getKey());
    }

    @Test
    public void testDtoConstructor_whenMinOrderPercentAvailable_thenMinOrderAmountIsNull() {
        when(userProfileService.isDealerGroup()).thenReturn(true);

        ModelPortfolioDetail model = mock(ModelPortfolioDetail.class);
        when(model.getAccountType()).thenReturn(ModelType.INVESTMENT.getCode());

        ModelPortfolioDetailDtoImpl dto = new ModelPortfolioDetailDtoImpl(model, null, null);
        Assert.assertTrue(dto.getAccountType() == ModelType.INVESTMENT.getCode());

        when(model.getAccountType()).thenReturn(ModelType.SUPERANNUATION.getId());
        dto = new ModelPortfolioDetailDtoImpl(model, null, null);
        Assert.assertTrue(dto.getAccountType() == ModelType.SUPERANNUATION.getCode());

        model = mock(ModelPortfolioDetail.class);
        when(model.getAccountType()).thenReturn(null);
        dto = new ModelPortfolioDetailDtoImpl(model, null, null);
        Assert.assertTrue(dto.getAccountType() == null);

        when(model.getModelDescription()).thenReturn("modelDescription");
        when(model.getInvestmentStyleDesc()).thenReturn("invStyleDescription");
        when(model.getMinimumTradeAmount()).thenReturn(BigDecimal.ZERO);
        when(model.getMinimumTradePercent()).thenReturn(BigDecimal.ONE);

        dto = new ModelPortfolioDetailDtoImpl(model, null, null);
        Assert.assertEquals("modelDescription", dto.getModelDescription());
        Assert.assertEquals("invStyleDescription", dto.getOtherInvestmentStyle());
        Assert.assertNull(dto.getMinimumOrderAmount());
        Assert.assertEquals(BigDecimal.ONE, dto.getMinimumOrderPercent());
    }

    @Test
    public void testDtoConstructor_whenOnlyMinOrderAmountAvailable_thenMinOrderPercentIsNull() {
        when(userProfileService.isDealerGroup()).thenReturn(true);

        ModelPortfolioDetail model = mock(ModelPortfolioDetail.class);
        when(model.getAccountType()).thenReturn(ModelType.INVESTMENT.getCode());

        ModelPortfolioDetailDtoImpl dto = new ModelPortfolioDetailDtoImpl(model, null, null);
        Assert.assertTrue(dto.getAccountType() == ModelType.INVESTMENT.getCode());

        when(model.getAccountType()).thenReturn(ModelType.SUPERANNUATION.getId());
        dto = new ModelPortfolioDetailDtoImpl(model, null, null);
        Assert.assertTrue(dto.getAccountType() == ModelType.SUPERANNUATION.getCode());

        model = mock(ModelPortfolioDetail.class);
        when(model.getAccountType()).thenReturn(null);
        dto = new ModelPortfolioDetailDtoImpl(model, null, null);
        Assert.assertTrue(dto.getAccountType() == null);

        when(model.getModelDescription()).thenReturn("modelDescription");
        when(model.getInvestmentStyleDesc()).thenReturn("invStyleDescription");
        when(model.getMinimumTradeAmount()).thenReturn(BigDecimal.ZERO);

        dto = new ModelPortfolioDetailDtoImpl(model, null, null);
        Assert.assertEquals("modelDescription", dto.getModelDescription());
        Assert.assertEquals("invStyleDescription", dto.getOtherInvestmentStyle());
        Assert.assertEquals(BigDecimal.ZERO, dto.getMinimumOrderAmount());
        Assert.assertNull(dto.getMinimumOrderPercent());
    }

    private void test_targetAllocations(List<TargetAllocationDto> taaDtoList, List<TargetAllocation> modelTaa) {

        for (int i = 0; i < taaDtoList.size(); i++) {
            TargetAllocationDto dto = taaDtoList.get(i);
            TargetAllocation taa = modelTaa.get(i);

            Assert.assertEquals(dto.getAssetClass(), taa.getAssetClass());
            Assert.assertEquals(dto.getMinimumWeight(), taa.getMinimumWeight());
            Assert.assertEquals(dto.getMaximumWeight(), taa.getMaximumWeight());
            Assert.assertEquals(dto.getNeutralPos(), taa.getNeutralPos());
            if (taa.getIndexAssetId() != null) {
                Assert.assertEquals(dto.getIndexAsset().getAssetId(), taa.getIndexAssetId());
            }
        }

    }
}
