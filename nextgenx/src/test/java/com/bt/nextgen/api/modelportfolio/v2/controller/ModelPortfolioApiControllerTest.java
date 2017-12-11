package com.bt.nextgen.api.modelportfolio.v2.controller;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioSummaryDto;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioUploadDto;
import com.bt.nextgen.api.modelportfolio.v2.service.ModelPortfolioSummaryDtoService;
import com.bt.nextgen.api.modelportfolio.v2.service.ModelPortfolioUploadDtoService;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.config.SecureJsonObjectMapper;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.modelportfolio.util.ModelPortfolioUploadUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.json.JsonSanitizer;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ModelPortfolioApiControllerTest {

    @InjectMocks
    private ModelPortfolioApiController modelApiController;

    @Mock
    private ModelPortfolioSummaryDtoService modelPortfolioSummaryDtoService;

    @Mock
    private ModelPortfolioUploadDtoService modelPortfolioUploadDtoService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private SecureJsonObjectMapper mockMapper;

    @Mock
    private ModelPortfolioUploadUtil modelPortfolioUploadUtil;

    private static final String createModelJson = "{\"key\":{\"modelId\":null},\"modelName\":\"xx Test 9\",\"modelIdentifier\":\"170217-s1\",\"openDate\":\"2017-01-31T13:00:00.000Z\",\"status\":\"New\",\"accountType\":\"super\",\"investmentStyle\":\"activ\",\"investmentStyleName\":\"Active\",\"minimumInvestment\":25000,\"modelAssetClass\":\"btfg$eq_au\",\"modelAssetClassName\":\"Australian Shares\",\"modelStructure\":\"simple\",\"modelConstruction\":\"fixed\",\"modelConstructionName\":\"Fixed\",\"modelType\":\"mp_multi\",\"modelOffers\":[\"730613\"],\"targetAllocations\":[{\"minimumWeight\":97,\"neutralPos\":97.5,\"maximumWeight\":98,\"assetClass\":\"eq_au\",\"assetClassName\":\"Australian shares\",\"indexAsset\":{\"assetId\":\"111672\",\"assetName\":\"Barclays Global Aggregate Bond Index in Australian dollars\",\"type\":\"Asset\"}},{\"minimumWeight\":2,\"neutralPos\":2.5,\"maximumWeight\":3,\"assetClass\":\"cash\",\"assetClassName\":\"Cash\",\"indexAsset\":{\"assetId\":\"111662\",\"assetName\":\"ABS Australian CPI AUD\",\"type\":\"Asset\"}}]}";
    private static final SecureJsonObjectMapper mapper = new SecureJsonObjectMapper();

    @Before
    public void setup() {
        Broker dealerBroker = Mockito.mock(Broker.class);
        Mockito.when(dealerBroker.getDealerKey()).thenReturn(BrokerKey.valueOf("dealerId"));
        Mockito.when(dealerBroker.getKey()).thenReturn(BrokerKey.valueOf("dealerId"));
        Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(dealerBroker);
        Mockito.when(userProfileService.getInvestmentManager(Mockito.any(ServiceErrors.class))).thenReturn(dealerBroker);
    }

    @Test(expected = AccessDeniedException.class)
    public void testCreateModel_whenEmulating_thenSubmitNotAllowed() throws IOException {
        Mockito.when(userProfileService.isEmulating()).thenReturn(true);
        modelApiController.createModel("accountId", "modelData", "false");
    }

    @Test
    public void testGetModels_whenCalled_ModelsReturned() {
        Mockito.when(modelPortfolioSummaryDtoService.findAll(Mockito.any(ServiceErrorsImpl.class))).thenReturn(
                Collections.<ModelPortfolioSummaryDto> emptyList());
        ApiResponse response = modelApiController.getModels();
        Assert.assertNotNull(response);
    }

    @Test
    public void testCreateModel_whenValidateOnly_thenParamsPassedToDtoForValidation() throws IOException {
        Mockito.when(userProfileService.isEmulating()).thenReturn(false);

        ModelPortfolioUploadDto dto = mapper.readerWithView(JsonViews.Write.class).forType(ModelPortfolioUploadDto.class)
                .readValue(JsonSanitizer.sanitize(createModelJson));

        ObjectReader reader = Mockito.mock(ObjectReader.class);
        Mockito.when(reader.forType(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(reader.readValue(Mockito.anyString())).thenReturn(dto);

        Mockito.when(mockMapper.readerWithView(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(
                modelPortfolioUploadDtoService.validate(Mockito.any(ModelPortfolioUploadDto.class),
                        Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<ModelPortfolioUploadDto>() {
            @Override
            public ModelPortfolioUploadDto answer(InvocationOnMock invocation) {
                ModelPortfolioUploadDto modelDto = (ModelPortfolioUploadDto) invocation.getArguments()[0];
                Assert.assertEquals("modelId", modelDto.getKey().getModelId());
                Assert.assertEquals("xx Test 9", modelDto.getModelName());
                return Mockito.mock(ModelPortfolioUploadDto.class);
            }
        });

        modelApiController.createModel("modelId", createModelJson, "true");
    }

    @Test
    public void testCreateModel_whenNotValidateOnly_thenParamsPassedToDtoForSubmission() throws IOException {
        Mockito.when(userProfileService.isEmulating()).thenReturn(false);

        ModelPortfolioUploadDto dto = mapper.readerWithView(JsonViews.Write.class).forType(ModelPortfolioUploadDto.class)
                .readValue(JsonSanitizer.sanitize(createModelJson));

        ObjectReader reader = Mockito.mock(ObjectReader.class);
        Mockito.when(reader.forType(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(reader.readValue(Mockito.anyString())).thenReturn(dto);

        Mockito.when(mockMapper.readerWithView(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(
                modelPortfolioUploadDtoService.submit(Mockito.any(ModelPortfolioUploadDto.class),
                        Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<ModelPortfolioUploadDto>() {
            @Override
            public ModelPortfolioUploadDto answer(InvocationOnMock invocation) {
                ModelPortfolioUploadDto modelDto = (ModelPortfolioUploadDto) invocation.getArguments()[0];
                Assert.assertEquals("modelId", modelDto.getKey().getModelId());
                Assert.assertEquals("xx Test 9", modelDto.getModelName());
                return Mockito.mock(ModelPortfolioUploadDto.class);
            }
        });

        modelApiController.createModel("modelId", createModelJson, "false");
    }

    @Test
    public void testFindModel_whenMinimalCriteriaProvided_thenParamsPassedToDtoService() {
        Mockito.when(
                modelPortfolioUploadDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class),
                        Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<List<ModelPortfolioUploadDto>>() {
            @Override
            public List<ModelPortfolioUploadDto> answer(InvocationOnMock invocation) {
                List<ApiSearchCriteria> criteria = (List<ApiSearchCriteria>) invocation.getArguments()[0];
                Assert.assertEquals(1, criteria.size());
                Assert.assertEquals("modelId", criteria.get(0).getValue());

                return Collections.emptyList();
            }
        });

        modelApiController.findModel("modelId", null, null);
    }

    @Test
    public void testFindModel_whenAllCriteriaProvided_thenParamsPassedToDtoService() {
        Mockito.when(
                modelPortfolioUploadDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class),
                        Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<List<ModelPortfolioUploadDto>>() {
            @Override
            public List<ModelPortfolioUploadDto> answer(InvocationOnMock invocation) {
                List<ApiSearchCriteria> criteria = (List<ApiSearchCriteria>) invocation.getArguments()[0];
                Assert.assertEquals(3, criteria.size());
                Assert.assertEquals("modelId", criteria.get(0).getValue());
                Assert.assertEquals("modelCode", criteria.get(1).getValue());
                Assert.assertEquals("allocationId", criteria.get(2).getValue());

                return Collections.emptyList();
            }
        });

        modelApiController.findModel("modelId", "modelCode", "allocationId");
    }

    @Test(expected = AccessDeniedException.class)
    public void testUploadModel_whenEmulating_thenSubmitNotAllowed() throws IOException {
        Mockito.when(userProfileService.isEmulating()).thenReturn(true);
        modelApiController.uploadModel("modelId", null);
    }

    @Test
    public void testUploadModel_correctParamsPassed() {
        Mockito.when(userProfileService.isEmulating()).thenReturn(false);
        
        final ModelPortfolioUploadDto dto = new ModelPortfolioUploadDto(new ModelPortfolioKey("modelKey"), "modelCode",
                "modelName",
                "commentary", null);
        Mockito.when(modelPortfolioUploadUtil.parseFile(Mockito.anyString(), Mockito.any(MultipartFile.class))).thenReturn(dto);
        Mockito.when(
                modelPortfolioUploadDtoService.submit(Mockito.any(ModelPortfolioUploadDto.class),
                        Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<ModelPortfolioUploadDto>() {
            @Override
            public ModelPortfolioUploadDto answer(InvocationOnMock invocation) {
                ModelPortfolioUploadDto modelDto = (ModelPortfolioUploadDto) invocation.getArguments()[0];
                Assert.assertEquals("modelKey", dto.getKey().getModelId());
                Assert.assertEquals("modelName", modelDto.getModelName());
                return Mockito.mock(ModelPortfolioUploadDto.class);
            }
        });
        modelApiController.uploadModel("modelId", null);
    }
}
