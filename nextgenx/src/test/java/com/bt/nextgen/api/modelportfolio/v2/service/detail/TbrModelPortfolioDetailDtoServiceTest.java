package com.bt.nextgen.api.modelportfolio.v2.service.detail;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.ModelPortfolioType;
import com.bt.nextgen.api.modelportfolio.v2.model.detail.ModelOfferDto;
import com.bt.nextgen.api.modelportfolio.v2.model.detail.ModelPortfolioDetailDto;
import com.bt.nextgen.api.modelportfolio.v2.model.detail.ModelPortfolioDetailDtoImpl;
import com.bt.nextgen.api.modelportfolio.v2.model.detail.TargetAllocationDto;
import com.bt.nextgen.api.modelportfolio.v2.service.defaultparams.TailoredPortfolioOfferDtoService;
import com.bt.nextgen.api.modelportfolio.v2.util.common.ModelPortfolioHelper;
import com.bt.nextgen.api.modelportfolio.v2.validation.ModelPortfolioDtoErrorMapper;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
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
import com.bt.nextgen.service.integration.modelportfolio.detail.OfferDetail;
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
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TbrModelPortfolioDetailDtoServiceTest {

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
    private TailoredPortfolioOfferDtoService tmpOfferService;

    @Mock
    private InvestmentPolicyStatementIntegrationService invPolicyService;

    private ModelPortfolioDetailDto modelDto;
    private ModelPortfolioDetailDto modelDto1;
    private DateTime openDate;

    @Before
    public void setUp() throws Exception {
        Broker dealer = mock(Broker.class);
        when(dealer.getKey()).thenReturn(BrokerKey.valueOf("dealerId"));
        when(userProfileService.getInvestmentManager(any(ServiceErrors.class))).thenReturn(dealer);
        when(userProfileService.getDealerGroupBroker()).thenReturn(dealer);

        openDate = DateTime.now();
        // Mock Dto with empty TAA and ModelOffers
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
        when(modelDto.getModelOffers()).thenReturn(null);
        when(modelDto.getOtherInvestmentStyle()).thenReturn("otherInvestmentStyle");
        when(modelDto.getModelDescription()).thenReturn("modelDescription");
        when(modelDto.getMinimumOrderAmount()).thenReturn(BigDecimal.ONE);
        when(modelDto.getMinimumOrderPercent()).thenReturn(BigDecimal.ONE);

        // Mock Dto with TAA and Offers
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

        // offer list
        ModelOfferDto offerDto = mock(ModelOfferDto.class);
        when(offerDto.getOfferId()).thenReturn("offerId1");
        when(offerDto.getOfferName()).thenReturn("offerName1");
        when(modelDto1.getModelOffers()).thenReturn(Collections.singletonList(offerDto));

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

        when(tmpOfferService.getModelOffers(any(BrokerKey.class), any(ModelType.class), any(ServiceErrors.class))).thenReturn(
                Collections.singletonList(offerDto));
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
        Assert.assertEquals("modelIdentifier", model.getSymbol());
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

        Assert.assertEquals(ModelPortfolioType.TAILORED.getIntlId(), model.getMpSubType());

        Assert.assertTrue(model.getOfferDetails().isEmpty());
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
    public void testToModel_whenCalledByAdviser_thenAdviserRelevantDtoFieldsCorrect() {
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
        when(userProfileService.isAdviser()).thenReturn(true);
        when(userProfileService.getPositionId()).thenReturn("adviserId");

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        ModelPortfolioDetail model = mpDetailDtoService.toModel(modelDto1, errors);
        Assert.assertEquals("adviserId", model.getInvestmentManagerId().getId());
        Assert.assertEquals(ModelPortfolioType.PREFERRED.getIntlId(), model.getMpSubType());
        Assert.assertEquals(BigDecimal.ONE, model.getMinimumTradePercent());
    }

    @Test
    public void test_toModelWithTaaAndOffers() {
        when(userProfileService.isDealerGroup()).thenReturn(true);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        ModelPortfolioDetail model = mpDetailDtoService.toModel(modelDto1, errors);

        Assert.assertEquals(modelDto1.getModelOffers().size(), model.getOfferDetails().size());
        Assert.assertEquals(modelDto1.getModelOffers().get(0).getOfferId(), model.getOfferDetails().get(0).getOfferId());

        Assert.assertEquals(modelDto1.getTargetAllocations().size(), model.getTargetAllocations().size());
        test_targetAllocations(modelDto1.getTargetAllocations(), model.getTargetAllocations());
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
        Assert.assertEquals(modelDto1.getTargetAllocations().size(), dto.getTargetAllocations().size());
        Assert.assertEquals(modelDto1.getWarnings(), dto.getWarnings());
        Assert.assertEquals(modelDto1.getAccountType(), dto.getAccountType());
        Assert.assertEquals(modelDto1.getModelOffers(), dto.getModelOffers());
    }

    @Test
    public void test_submitWithEmptyOffer_validDtoConstructed() {
        when(userProfileService.isDealerGroup()).thenReturn(true);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        ModelPortfolioDetail model = mpDetailDtoService.toModel(modelDto, errors);
        when(modelPortfolioService.submitModelPortfolio(any(ModelPortfolioDetail.class), any(ServiceErrors.class))).thenReturn(
                model);
        ModelPortfolioDetailDto dto = mpDetailDtoService.submit(modelDto, errors);
        Assert.assertNotNull(dto);
        Assert.assertEquals(modelDto.getKey(), dto.getKey());
        Assert.assertEquals(0, dto.getModelOffers().size());

        ModelPortfolioDetailImpl modelImpl = (ModelPortfolioDetailImpl) model;
        modelImpl.setOfferDetails(null);
        when(modelPortfolioService.submitModelPortfolio(any(ModelPortfolioDetail.class), any(ServiceErrors.class))).thenReturn(
                model);
        dto = mpDetailDtoService.submit(modelDto, errors);
        Assert.assertNotNull(dto);
        Assert.assertEquals(modelDto.getKey(), dto.getKey());
        Assert.assertTrue(dto.getModelOffers() != null);
        Assert.assertEquals(0, dto.getModelOffers().size());
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

    @Test
    public void test_getOfferDtoList() {
        when(userProfileService.isDealerGroup()).thenReturn(true);

        ModelPortfolioDetail model = mock(ModelPortfolioDetail.class);
        when(model.getAccountType()).thenReturn(ModelType.SUPERANNUATION.getCode());
        when(model.getOfferDetails()).thenReturn(null);

        List<ModelOfferDto> dtoList = new ArrayList<>();
        when(tmpOfferService.getModelOffers(any(BrokerKey.class), any(ModelType.class), any(ServiceErrors.class))).thenReturn(
                dtoList);
        List<ModelOfferDto> offerList = mpDetailDtoService.getOfferDtoList(model);
        Assert.assertTrue(offerList.isEmpty());

        // Set offerDetails to model
        List<OfferDetail> offerDetails = new ArrayList<>();
        OfferDetail off1 = mock(OfferDetail.class);
        when(off1.getOfferId()).thenReturn("off1");
        offerDetails.add(off1);
        OfferDetail off2 = mock(OfferDetail.class);
        when(off2.getOfferId()).thenReturn("off2");
        offerDetails.add(off2);
        when(model.getOfferDetails()).thenReturn(offerDetails);
        offerList = mpDetailDtoService.getOfferDtoList(model);
        Assert.assertTrue(offerList.isEmpty());

        ModelOfferDto offDto0 = mock(ModelOfferDto.class);
        when(offDto0.getOfferId()).thenReturn("off0");
        dtoList.add(offDto0);
        ModelOfferDto offDto1 = mock(ModelOfferDto.class);
        when(offDto1.getOfferId()).thenReturn("off1");
        dtoList.add(offDto1);
        ModelOfferDto offDto2 = mock(ModelOfferDto.class);
        when(offDto2.getOfferId()).thenReturn("off2");
        dtoList.add(offDto2);
        when(tmpOfferService.getModelOffers(any(BrokerKey.class), any(ModelType.class), any(ServiceErrors.class))).thenReturn(
                dtoList);
        offerList = mpDetailDtoService.getOfferDtoList(model);
        Assert.assertTrue(offerList.size() == 2);
    }

    @Test
    public void test_getOfferDtoList_forPortfolioManager_emptyListReturn() {
        when(userProfileService.isDealerGroup()).thenReturn(true);

        ModelPortfolioDetail model = mock(ModelPortfolioDetail.class);
        when(model.getAccountType()).thenReturn(ModelType.SUPERANNUATION.getCode());
        when(model.getOfferDetails()).thenReturn(null);
        when(userProfileService.isPortfolioManager()).thenReturn(Boolean.TRUE);
        List<ModelOfferDto> offerList = mpDetailDtoService.getOfferDtoList(model);

        Assert.assertTrue(offerList.isEmpty());
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
