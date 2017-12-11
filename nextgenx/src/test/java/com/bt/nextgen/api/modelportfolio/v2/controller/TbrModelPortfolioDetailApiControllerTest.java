package com.bt.nextgen.api.modelportfolio.v2.controller;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.detail.ModelPortfolioDetailDto;
import com.bt.nextgen.api.modelportfolio.v2.model.detail.ModelPortfolioDetailDtoImpl;
import com.bt.nextgen.api.modelportfolio.v2.service.defaultparams.ModelPortfolioDefaultParamsDtoService;
import com.bt.nextgen.api.modelportfolio.v2.service.defaultparams.TailoredPortfolioOfferDtoService;
import com.bt.nextgen.api.modelportfolio.v2.service.detail.ModelPortfolioDetailDtoService;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.config.SecureJsonObjectMapper;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.json.JsonSanitizer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class TbrModelPortfolioDetailApiControllerTest {

    @InjectMocks
    private ModelPortfolioDetailApiController modelApiController;

    @Mock
    private SecureJsonObjectMapper mockMapper;

    @Mock
    private ModelPortfolioDetailDtoService mpDetailDtoService;

    @Mock
    private UserProfileService profileService;

    @Mock
    private ModelPortfolioDefaultParamsDtoService paramsDetailDtoService;

    @Mock
    private TailoredPortfolioOfferDtoService offerDtoService;

    // JSON object with OfferDto and AccountType added.
    private static final String createTmpJsonObject = "{\"key\":{\"modelId\":null},\"modelName\":\"xxx Test 1\",\"modelIdentifier\":\"20170210-1\",\"openDate\":\"2017-01-13T13:00:00.000Z\",\"status\":\"New\",\"accountType\":\"Investment\",\"investmentStyle\":\"activ\",\"investmentStyleName\":\"Active\",\"minimumInvestment\":10000,\"modelAssetClass\":\"btfg$fi_au\",\"modelAssetClassName\":\"Australian Fixed Interest\",\"modelStructure\":\"simple\",\"modelConstruction\":\"FIXED\",\"modelConstructionName\":\"Fixed\",\"modelType\":\"mp_multi\",\"modelOffers\":[\"527740\"],\"showSingleOffer\":false,\"targetAllocations\":[{\"minimumWeight\":70,\"neutralPos\":97.5,\"maximumWeight\":98,\"assetClass\":\"eq_au\",\"assetClassName\":\"Australian shares\",\"indexAsset\":{\"assetId\":\"111662\",\"assetName\":\"ABS Australian CPI AUD\",\"type\":\"Asset\"}},{\"minimumWeight\":2.5,\"neutralPos\":2.5,\"maximumWeight\":2.5,\"assetClass\":\"cash\",\"assetClassName\":\"Cash\",\"indexAsset\":{\"assetId\":\"111668\",\"assetName\":\"Bloomberg Ausbond Bank Bill Index\",\"type\":\"Asset\"}}]}";
    private static final SecureJsonObjectMapper mapper = new SecureJsonObjectMapper();

    @Test(expected = AccessDeniedException.class)
    public void test_whenEmulating_thenSubmitNotAllowed() throws IOException {
        Mockito.when(profileService.isEmulating()).thenReturn(true);
        modelApiController.submitModel("modelId", "fakeJSON");
    }

    @Test(expected = AccessDeniedException.class)
    public void test_whenEmulating_thenDiscardNotAllowed() throws IOException {
        Mockito.when(profileService.isEmulating()).thenReturn(true);
        modelApiController.updateModel("modelId", "fakeJSON");
    }
    
    @Test
    public void testSubmitModel_whenValidateOnly_thenValidateMethodCalled() throws Exception {
        Mockito.when(profileService.isEmulating()).thenReturn(false);

        final ModelPortfolioDetailDtoImpl dto = mapper.readerWithView(JsonViews.Write.class)
                .forType(ModelPortfolioDetailDtoImpl.class)
                .readValue(JsonSanitizer.sanitize(createTmpJsonObject));

        ObjectReader reader = Mockito.mock(ObjectReader.class);
        Mockito.when(reader.forType(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(reader.readValue(Mockito.anyString())).thenReturn(dto);

        Mockito.when(mockMapper.readerWithView(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(mpDetailDtoService.validate(Mockito.any(ModelPortfolioDetailDtoImpl.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<ModelPortfolioDetailDtoImpl>() {

                    @Override
                    public ModelPortfolioDetailDtoImpl answer(InvocationOnMock invocation) throws Throwable {
                        ModelPortfolioDetailDtoImpl model = (ModelPortfolioDetailDtoImpl) invocation.getArguments()[0];
                        Assert.assertNotNull(model);
                        Assert.assertEquals("527740", model.getModelOffers().get(0).getOfferId());
                        Assert.assertEquals(ModelType.INVESTMENT.getDisplayValue(), model.getAccountType());
                        return model;
                    }
                });

        modelApiController.submitModel(createTmpJsonObject, "true");
    }

    @Test
    public void testSubmitModel_whenNotValidateOnly_thenSubmitMethodCalled() throws Exception {
        Mockito.when(profileService.isEmulating()).thenReturn(false);

        final ModelPortfolioDetailDtoImpl dto = mapper.readerWithView(JsonViews.Write.class)
                .forType(ModelPortfolioDetailDtoImpl.class)
                .readValue(JsonSanitizer.sanitize(createTmpJsonObject));

        ObjectReader reader = Mockito.mock(ObjectReader.class);
        Mockito.when(reader.forType(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(reader.readValue(Mockito.anyString())).thenReturn(dto);

        Mockito.when(mockMapper.readerWithView(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(mpDetailDtoService.submit(Mockito.any(ModelPortfolioDetailDtoImpl.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<ModelPortfolioDetailDtoImpl>() {

                    @Override
                    public ModelPortfolioDetailDtoImpl answer(InvocationOnMock invocation) throws Throwable {
                        ModelPortfolioDetailDtoImpl model = (ModelPortfolioDetailDtoImpl) invocation.getArguments()[0];
                        Assert.assertNotNull(model);
                        Assert.assertEquals("527740", model.getModelOffers().get(0).getOfferId());
                        Assert.assertEquals(ModelType.INVESTMENT.getDisplayValue(), model.getAccountType());
                        return model;
                    }
                });

        modelApiController.submitModel(createTmpJsonObject, "false");
    }

    @Test
    public void testFindModel_whenKeyProvided_thenFindMethodCalled() throws Exception {
        Mockito.when(mpDetailDtoService.find(Mockito.any(ModelPortfolioKey.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<ModelPortfolioDetailDto>() {

                    @Override
                    public ModelPortfolioDetailDto answer(InvocationOnMock invocation) throws Throwable {
                        ModelPortfolioKey key = (ModelPortfolioKey) invocation.getArguments()[0];
                        Assert.assertNotNull(key);
                        Assert.assertEquals("modelId", key.getModelId());
                        return Mockito.mock(ModelPortfolioDetailDto.class);
                    }
                });

        modelApiController.findModel("modelId");
    }

    @Test
    public void testUpdateModel_whenDetailsProvided_thenUpdateMethodCalled() throws Exception {
        Mockito.when(profileService.isEmulating()).thenReturn(false);

        final ModelPortfolioDetailDtoImpl dto = mapper.readerWithView(JsonViews.Write.class)
                .forType(ModelPortfolioDetailDtoImpl.class).readValue(JsonSanitizer.sanitize(createTmpJsonObject));

        ObjectReader reader = Mockito.mock(ObjectReader.class);
        Mockito.when(reader.forType(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(reader.readValue(Mockito.anyString())).thenReturn(dto);

        Mockito.when(mockMapper.readerWithView(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(mpDetailDtoService.update(Mockito.any(ModelPortfolioDetailDtoImpl.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<ModelPortfolioDetailDtoImpl>() {

                    @Override
                    public ModelPortfolioDetailDtoImpl answer(InvocationOnMock invocation) throws Throwable {
                        ModelPortfolioDetailDtoImpl model = (ModelPortfolioDetailDtoImpl) invocation.getArguments()[0];
                        Assert.assertNotNull(model);
                        Assert.assertEquals("modelId", model.getKey().getModelId());
                        Assert.assertEquals("527740", model.getModelOffers().get(0).getOfferId());
                        Assert.assertEquals(ModelType.INVESTMENT.getDisplayValue(), model.getAccountType());
                        return model;
                    }
                });

        modelApiController.updateModel("modelId", createTmpJsonObject);
    }
}
