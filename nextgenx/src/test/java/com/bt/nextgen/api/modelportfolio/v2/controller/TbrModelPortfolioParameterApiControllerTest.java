package com.bt.nextgen.api.modelportfolio.v2.controller;

import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.DealerParameterKey;
import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.ModelPortfolioType;
import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.PreferredPortfolioDefaultParamsDto;
import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.TailoredPortfolioDefaultParamsDto;
import com.bt.nextgen.api.modelportfolio.v2.model.detail.ModelOfferDto;
import com.bt.nextgen.api.modelportfolio.v2.service.ModelAssetClassDtoService;
import com.bt.nextgen.api.modelportfolio.v2.service.TailorMadePortfolioDtoService;
import com.bt.nextgen.api.modelportfolio.v2.service.defaultparams.ModelPortfolioDefaultParamsDtoService;
import com.bt.nextgen.api.modelportfolio.v2.service.defaultparams.TailoredPortfolioOfferDtoService;
import com.bt.nextgen.api.smsf.model.AssetClassDto;
import com.bt.nextgen.api.staticdata.service.StaticDataDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.integration.broker.Broker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TbrModelPortfolioParameterApiControllerTest {

    @InjectMocks
    private ModelPortfolioParameterApiController parameterApiController;

    @Mock
    private ModelAssetClassDtoService modelAssetClassDtoService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private ModelPortfolioDefaultParamsDtoService defaultParamsDtoService;

    @Mock
    private TailoredPortfolioOfferDtoService offerDtoService;

    @Mock
    private TailorMadePortfolioDtoService tmpDtoService;

    @Mock
    private StaticDataDtoService staticDtoService;

    @Before
    public void setup() {
        Broker dealerBroker = Mockito.mock(Broker.class);
        Mockito.when(dealerBroker.getDealerKey()).thenReturn(BrokerKey.valueOf("dealerId"));
        Mockito.when(dealerBroker.getKey()).thenReturn(BrokerKey.valueOf("dealerId"));
        Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(dealerBroker);
        Mockito.when(userProfileService.getInvestmentManager(Mockito.any(ServiceErrors.class))).thenReturn(dealerBroker);
    }

    @Test
    public void testGetTmpDefaultParameters() {
        Mockito.when(defaultParamsDtoService.find(Mockito.any(DealerParameterKey.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<TailoredPortfolioDefaultParamsDto>() {

                    @Override
                    public TailoredPortfolioDefaultParamsDto answer(InvocationOnMock invocation) {
                        DealerParameterKey key = (DealerParameterKey) invocation.getArguments()[0];
                        Assert.assertEquals("dealerId", key.getBrokerKey().getId());
                        Assert.assertEquals(ModelType.INVESTMENT.getId(), key.getAccountType());
                        Assert.assertEquals(ModelPortfolioType.TAILORED.getIntlId(), key.getPortfolioType());

                        return Mockito.mock(TailoredPortfolioDefaultParamsDto.class);
                    }
                });

        parameterApiController.getTmpDefaultParams(ModelType.INVESTMENT.getCode());
    }

    @Test
    public void testGetPPDefaultParameters() {
        Mockito.when(defaultParamsDtoService.find(Mockito.any(DealerParameterKey.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<PreferredPortfolioDefaultParamsDto>() {

                    @Override
                    public PreferredPortfolioDefaultParamsDto answer(InvocationOnMock invocation) {
                        DealerParameterKey key = (DealerParameterKey) invocation.getArguments()[0];
                        Assert.assertEquals("dealerId", key.getBrokerKey().getId());
                        Assert.assertEquals(ModelType.INVESTMENT.getId(), key.getAccountType());
                        Assert.assertEquals(ModelPortfolioType.PREFERRED.getIntlId(), key.getPortfolioType());

                        return Mockito.mock(PreferredPortfolioDefaultParamsDto.class);
                    }
                });

        parameterApiController.getPPDefaultParams(ModelType.INVESTMENT.getCode());
    }

    @Test
    public void testGetModelOfferList() {
        Mockito.when(offerDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<List<ModelOfferDto>>() {

                    @Override
                    public List<ModelOfferDto> answer(InvocationOnMock invocation) {
                        List<ApiSearchCriteria> criteria = (List<ApiSearchCriteria>) invocation.getArguments()[0];
                        Assert.assertEquals(2, criteria.size());
                        Assert.assertEquals(ModelType.INVESTMENT.getCode(), criteria.get(0).getValue());
                        Assert.assertEquals("dealerId", criteria.get(1).getValue());

                        return Collections.emptyList();
                    }
                });

        parameterApiController.getModelOffer(ModelType.INVESTMENT.getCode());
    }

    @Test
    public void testGetModelOfferList_whenNoDealerGroupBroker_thenSearchValueNull() {
        Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(null);

        Mockito.when(offerDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<List<ModelOfferDto>>() {

                    @Override
                    public List<ModelOfferDto> answer(InvocationOnMock invocation) {
                        List<ApiSearchCriteria> criteria = (List<ApiSearchCriteria>) invocation.getArguments()[0];
                        Assert.assertEquals(2, criteria.size());
                        Assert.assertEquals(ModelType.INVESTMENT.getCode(), criteria.get(0).getValue());
                        Assert.assertNull(criteria.get(1).getValue());

                        return Collections.emptyList();
                    }
                });

        parameterApiController.getModelOffer(ModelType.INVESTMENT.getCode());
    }

    @Test
    public void testGetModelCashAsset() {
        Mockito.when(tmpDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<List<ModelOfferDto>>() {

                    @Override
                    public List<ModelOfferDto> answer(InvocationOnMock invocation) {
                        List<ApiSearchCriteria> criteria = (List<ApiSearchCriteria>) invocation.getArguments()[0];
                        Assert.assertEquals(1, criteria.size());
                        Assert.assertEquals(ModelType.SUPERANNUATION.getCode(), criteria.get(0).getValue());

                        return Collections.emptyList();
                    }
                });

        parameterApiController.getModelCashAsset(ModelType.SUPERANNUATION.getCode());
    }

    @Test
    public void testGetInvestmentStyles() {
        Mockito.when(staticDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<List<ModelOfferDto>>() {

                    @Override
                    public List<ModelOfferDto> answer(InvocationOnMock invocation) {
                        List<ApiSearchCriteria> criteria = (List<ApiSearchCriteria>) invocation.getArguments()[0];
                        Assert.assertEquals(1, criteria.size());
                        Assert.assertEquals(CodeCategory.IPS_INVESTMENT_STYLE.name(), criteria.get(0).getValue());

                        return Collections.emptyList();
                    }
                });

        parameterApiController.getInvestmentStyles();
    }

    @Test
    public void testGetIpsAssetClass() {
        Mockito.when(staticDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<List<ModelOfferDto>>() {

                    @Override
                    public List<ModelOfferDto> answer(InvocationOnMock invocation) {
                        List<ApiSearchCriteria> criteria = (List<ApiSearchCriteria>) invocation.getArguments()[0];
                        Assert.assertEquals(1, criteria.size());
                        Assert.assertEquals(CodeCategory.IPS_ASSET_CLASS.name(), criteria.get(0).getValue());

                        return Collections.emptyList();
                    }
                });

        parameterApiController.getIpsAssetClass();
    }

    @Test
    public void testGetAssetClass() {
        Mockito.when(modelAssetClassDtoService.findAll(Mockito.any(ServiceErrors.class))).thenReturn(
                Collections.<AssetClassDto> emptyList());
        ApiResponse response = parameterApiController.getAssetClass();
        Assert.assertNotNull(response);
    }
}
