package com.bt.nextgen.api.modelportfolio.v2.service;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioAssetAllocationDto;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioUploadDto;
import com.bt.nextgen.api.modelportfolio.v2.util.common.ModelPortfolioHelper;
import com.bt.nextgen.api.modelportfolio.v2.validation.ModelPortfolioDtoErrorMapper;
import com.bt.nextgen.api.modelportfolio.v2.validation.ModelPortfolioDtoErrorMapperImpl;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.modelportfolio.ModelPortfolioAssetAllocationImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.ModelPortfolioUploadImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.asset.ModelAssetClass;
import com.bt.nextgen.service.integration.asset.ShareAsset;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.ips.IpsSummaryDetails;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioAssetAllocation;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioUpload;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ConstructionType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioDetail;
import com.bt.nextgen.service.integration.transaction.TransactionResponse;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ModelPortfolioUploadDtoServiceTest {
    @InjectMocks
    private ModelPortfolioUploadDtoServiceImpl modelPortfolioUploadDtoService;

    @Mock
    private ModelPortfolioIntegrationService modelPortfolioService;

    @Mock
    private TransactionResponse txnRsp;

    @Spy
    private ModelPortfolioDtoErrorMapper modelPortfolioErrorMapper = new ModelPortfolioDtoErrorMapperImpl();

    @Mock
    private ModelPortfolioHelper modelPortfolioHelper;

    private List<DomainApiErrorDto> apiErrors;

    private ModelPortfolioKey modelKey;
    private ModelPortfolioUploadDto uploadDto;
    private List<ModelPortfolioAssetAllocationDto> allocationDtos;
    private ModelPortfolioAssetAllocationDto allocationDto;
    private ModelPortfolioUploadImpl uploadModel;
    private ModelPortfolioAssetAllocationImpl assetAllocation;
    private List<ModelPortfolioAssetAllocation> assetAllocations = new ArrayList<>();
    private List<ValidationError> validationErrors;

    @Before
    public void setup() throws Exception {

        apiErrors = new ArrayList<>();
        apiErrors.add(new DomainApiErrorDto("errorId", "domain", "reason", "message", DomainApiErrorDto.ErrorType.WARNING));
        apiErrors.add(new DomainApiErrorDto("errorId2", "domain2", "reason2", "message2", DomainApiErrorDto.ErrorType.WARNING));

        List<TransactionValidation> warnings = new ArrayList<TransactionValidation>();
        validationErrors = new ArrayList<>();
        allocationDtos = new ArrayList<>();
        modelKey = new ModelPortfolioKey("11111");

        Asset mpAsset = Mockito.mock(Asset.class);
        Mockito.when(mpAsset.getAssetName()).thenReturn("assetName");
        Mockito.when(mpAsset.getAssetCode()).thenReturn("assetCode");
        Mockito.when(mpAsset.getAssetClass()).thenReturn(AssetClass.AUSTRALIAN_SHARES);
        Mockito.when(mpAsset.getModelAssetClass()).thenReturn(ModelAssetClass.AUSTRALIAN_SHARES);
        Mockito.when(mpAsset.getAssetType()).thenReturn(AssetType.SHARE);

        allocationDto = new ModelPortfolioAssetAllocationDto(mpAsset, new BigDecimal("100.00"), new BigDecimal("50.00"),
                new BigDecimal("30"));
        allocationDtos.add(allocationDto);
        uploadDto = new ModelPortfolioUploadDto(modelKey, "EQR_CORE_EQ", "EQR Core Equities", "Lorem ipsum dolor sit amet",
                allocationDtos);

        uploadModel = new ModelPortfolioUploadImpl();
        uploadModel.setModelKey(IpsKey.valueOf("modelId"));
        uploadModel.setModelCode("modelCode");
        uploadModel.setModelName("modelName");
        assetAllocation = new ModelPortfolioAssetAllocationImpl();
        assetAllocation.setAssetAllocation(BigDecimal.ONE);
        assetAllocation.setAssetCode("EQR_CORE_EQ");
        assetAllocation.setTradePercent(BigDecimal.TEN);
        assetAllocations.add(assetAllocation);
        uploadModel.setAssetAllocations(assetAllocations);

        uploadModel.setWarnings(warnings);
        uploadModel.setValidationErrors(validationErrors);

        ModelPortfolioAssetAllocation allocation = Mockito.mock(ModelPortfolioAssetAllocation.class);
        Mockito.when(allocation.getAssetAllocation()).thenReturn(BigDecimal.ONE);
        Mockito.when(allocation.getAssetCode()).thenReturn("assetCode");
        Mockito.when(allocation.getTradePercent()).thenReturn(BigDecimal.TEN);

        ModelPortfolioUpload modelPortfolioUpload = Mockito.mock(ModelPortfolioUpload.class);
        Mockito.when(modelPortfolioUpload.getModelKey()).thenReturn(IpsKey.valueOf("modelId"));
        Mockito.when(modelPortfolioUpload.getModelCode()).thenReturn("modelCode");
        Mockito.when(modelPortfolioUpload.getModelName()).thenReturn("modelName");
        Mockito.when(modelPortfolioUpload.getCommentary()).thenReturn("commentary");
        Mockito.when(modelPortfolioUpload.getAssetAllocations()).thenReturn(Arrays.asList(allocation));

        Mockito.when(modelPortfolioService.loadUploadedModel(Mockito.any(IpsKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(modelPortfolioUpload);
    }

    @Test
    public void testToModelPortfolioUpload_whenObject_thenValuesMatch() {
        ModelPortfolioUpload modelUpload = modelPortfolioUploadDtoService.toModelPortfolioUpload(uploadDto);
        Assert.assertEquals(uploadDto.getKey().getModelId(), modelUpload.getModelKey().getId());
        Assert.assertEquals(uploadDto.getModelCode(), modelUpload.getModelCode());
        Assert.assertEquals(uploadDto.getModelName(), modelUpload.getModelName());
        Assert.assertEquals(uploadDto.getCommentary(), modelUpload.getCommentary());
        Assert.assertEquals(uploadDto.getAssetAllocations().size(), modelUpload.getAssetAllocations().size());
    }

    @Test
    public void testToModelPortfolioAssetAllocations_whenValues_thenSizeMatches() {
        List<ModelPortfolioAssetAllocation> allocations = modelPortfolioUploadDtoService
                .toModelPortfolioAssetAllocations(allocationDtos);
        Assert.assertEquals(allocationDtos.size(), allocations.size());
    }

    @Test
    public void testToModelPortfolioAssetAllocation_whenObject_thenValuesMatch() {
        ModelPortfolioAssetAllocation allocation = modelPortfolioUploadDtoService.toModelPortfolioAssetAllocation(allocationDto);
        Assert.assertEquals(allocationDto.getAssetCode(), allocation.getAssetCode());
        Assert.assertEquals(allocationDto.getAssetAllocation(), allocation.getAssetAllocation());
        Assert.assertEquals(allocationDto.getTradePercent(), allocation.getTradePercent());
    }

    @Test
    public void testValidate() {
        Mockito.when(
                modelPortfolioService.validateModel(Mockito.any(ModelPortfolioUpload.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(uploadModel);
        Mockito.when(modelPortfolioErrorMapper.map(Mockito.anyList())).thenReturn(apiErrors);
        Mockito.when(txnRsp.getValidationErrors()).thenReturn(validationErrors);

        ModelPortfolioUploadDto resultDto = modelPortfolioUploadDtoService.validate(uploadDto, new ServiceErrorsImpl());
        Assert.assertNotNull(resultDto);
        Assert.assertEquals(resultDto.getModelCode(), "EQR_CORE_EQ");
        Assert.assertEquals(resultDto.getModelName(), "EQR Core Equities");
        Assert.assertEquals(resultDto.getAssetAllocations().size(), uploadModel.getAssetAllocations().size());
    }

    @Test
    public void testSubmit() {
        Mockito.when(modelPortfolioService.submitModel(Mockito.any(ModelPortfolioUpload.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(uploadModel);
        Mockito.when(modelPortfolioErrorMapper.map(Mockito.anyList())).thenReturn(apiErrors);
        Mockito.when(txnRsp.getValidationErrors()).thenReturn(validationErrors);

        ModelPortfolioUploadDto resultDto = modelPortfolioUploadDtoService.submit(uploadDto, new ServiceErrorsImpl());

        Assert.assertNotNull(resultDto);
        Assert.assertEquals(resultDto.getModelCode(), "EQR_CORE_EQ");
        Assert.assertEquals(resultDto.getModelName(), "EQR Core Equities");
        Assert.assertEquals(resultDto.getAssetAllocations().size(), uploadModel.getAssetAllocations().size());
    }

    @Test
    public void testSearch() {
        ModelPortfolioDetail details = Mockito.mock(ModelPortfolioDetail.class);
        Mockito.when(details.getId()).thenReturn("modelId");
        Mockito.when(modelPortfolioHelper.getModelPortfolioDetails(Mockito.any(IpsKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(details);

        IpsSummaryDetails summary = Mockito.mock(IpsSummaryDetails.class);
        Mockito.when(summary.getAccountType()).thenReturn(ModelType.SUPERANNUATION.getId());
        Mockito.when(summary.getModelCode()).thenReturn("modelCode");
        Mockito.when(summary.getModelOrderId()).thenReturn("12345");
        Mockito.when(
                modelPortfolioHelper.getIpsSummaryDetails(Mockito.any(ModelPortfolioDetail.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(summary);

        Asset asset = Mockito.mock(Asset.class);
        Mockito.when(asset.getAssetId()).thenReturn("assetId");
        Mockito.when(asset.getAssetCode()).thenReturn("assetCode");
        Mockito.when(asset.getAssetName()).thenReturn("assetName");
        Map<String, Asset> assetMap = new HashMap<>();
        assetMap.put("assetCode", asset);

        Mockito.when(
                modelPortfolioHelper.getAllocationAssetMap(Mockito.anyListOf(ModelPortfolioAssetAllocation.class),
                        Mockito.any(ModelType.class), Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(
                assetMap);

        ApiSearchCriteria modelCriteria = new ApiSearchCriteria("modelId", ApiSearchCriteria.SearchOperation.EQUALS, "modelId",
                ApiSearchCriteria.OperationType.STRING);
        ApiSearchCriteria searchCriteria = new ApiSearchCriteria("modelCode", ApiSearchCriteria.SearchOperation.EQUALS,
                "modelCode", ApiSearchCriteria.OperationType.STRING);
        ApiSearchCriteria allocIdCriteria = new ApiSearchCriteria("allocationId", ApiSearchCriteria.SearchOperation.EQUALS,
                "allocationId", ApiSearchCriteria.OperationType.STRING);

        List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(modelCriteria);
        criteria.add(searchCriteria);
        criteria.add(allocIdCriteria);

        List<ModelPortfolioUploadDto> result = modelPortfolioUploadDtoService.search(criteria, new FailFastErrorsImpl());
        Assert.assertEquals(1, result.size());

        ModelPortfolioUploadDto dto = result.get(0);
        Assert.assertEquals("modelId", dto.getKey().getModelId());
        Assert.assertEquals("modelCode", dto.getModelCode());
        Assert.assertEquals("modelName", dto.getModelName());
        Assert.assertEquals("commentary", dto.getCommentary());
        Assert.assertEquals(1, dto.getAssetAllocations().size());

        ModelPortfolioAssetAllocationDto allocationDto = dto.getAssetAllocations().get(0);
        Assert.assertEquals("assetId", allocationDto.getAssetId());
        Assert.assertEquals("assetName", allocationDto.getAssetName());
        Assert.assertEquals("assetCode", allocationDto.getAssetCode());
    }

    @Test
    public void testSearch_forShareAsset() {
        Mockito.when(modelPortfolioHelper.getModelPortfolioDetails(Mockito.any(IpsKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(null);

        IpsSummaryDetails summary = Mockito.mock(IpsSummaryDetails.class);
        Mockito.when(summary.getAccountType()).thenReturn(ModelType.SUPERANNUATION.getId());
        Mockito.when(summary.getModelCode()).thenReturn("modelCode");
        Mockito.when(summary.getModelOrderId()).thenReturn("12345");
        Mockito.when(
                modelPortfolioHelper.getIpsSummaryDetails(Mockito.any(ModelPortfolioDetail.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(summary);

        ShareAsset asset = Mockito.mock(ShareAsset.class);
        Mockito.when(asset.getAssetId()).thenReturn("assetId");
        Mockito.when(asset.getAssetCode()).thenReturn("assetCode");
        Mockito.when(asset.getAssetName()).thenReturn("assetName");
        Mockito.when(asset.getAssetType()).thenReturn(AssetType.SHARE);
        Mockito.when(asset.getInvestmentHoldingLimit()).thenReturn(BigDecimal.TEN);
        Map<String, Asset> assetMap = new HashMap<>();
        assetMap.put("assetCode", asset);

        Mockito.when(
                modelPortfolioHelper.getAllocationAssetMap(Mockito.anyListOf(ModelPortfolioAssetAllocation.class),
                        Mockito.any(ModelType.class), Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(
                assetMap);

        ApiSearchCriteria modelCriteria = new ApiSearchCriteria("modelId", ApiSearchCriteria.SearchOperation.EQUALS, "modelId",
                ApiSearchCriteria.OperationType.STRING);
        ApiSearchCriteria searchCriteria = new ApiSearchCriteria("modelCode", ApiSearchCriteria.SearchOperation.EQUALS,
                "modelCode", ApiSearchCriteria.OperationType.STRING);
        ApiSearchCriteria allocIdCriteria = new ApiSearchCriteria("allocationId", ApiSearchCriteria.SearchOperation.EQUALS,
                "allocationId", ApiSearchCriteria.OperationType.STRING);

        List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(modelCriteria);
        criteria.add(searchCriteria);
        criteria.add(allocIdCriteria);

        List<ModelPortfolioUploadDto> result = modelPortfolioUploadDtoService.search(criteria, new FailFastErrorsImpl());
        Assert.assertEquals(1, result.size());

        ModelPortfolioUploadDto dto = result.get(0);
        Assert.assertEquals("modelId", dto.getKey().getModelId());
        Assert.assertEquals("modelCode", dto.getModelCode());
        Assert.assertEquals("modelName", dto.getModelName());
        Assert.assertEquals("commentary", dto.getCommentary());
        Assert.assertEquals(1, dto.getAssetAllocations().size());

        ModelPortfolioAssetAllocationDto allocationDto = dto.getAssetAllocations().get(0);
        Assert.assertEquals("assetId", allocationDto.getAssetId());
        Assert.assertEquals("assetName", allocationDto.getAssetName());
        Assert.assertEquals("assetCode", allocationDto.getAssetCode());
        Assert.assertEquals(asset.getInvestmentHoldingLimit(), allocationDto.getInvestmentHoldingLimit());
    }

    @Test
    public void testSearch_whenOnlyModelIdProvided_thenOtherParametersLoadedFromAvaloq() {
        ModelPortfolioDetail details = Mockito.mock(ModelPortfolioDetail.class);
        Mockito.when(details.getId()).thenReturn("modelId");
        Mockito.when(modelPortfolioHelper.getModelPortfolioDetails(Mockito.any(IpsKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(details);

        IpsSummaryDetails summary = Mockito.mock(IpsSummaryDetails.class);
        Mockito.when(summary.getModelCode()).thenReturn("summaryModelCode");
        Mockito.when(summary.getModelOrderId()).thenReturn("12345");
        Mockito.when(
                modelPortfolioHelper.getIpsSummaryDetails(Mockito.any(ModelPortfolioDetail.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(summary);

        Asset asset = Mockito.mock(Asset.class);
        Mockito.when(asset.getAssetId()).thenReturn("assetId");
        Mockito.when(asset.getAssetCode()).thenReturn("assetCode");
        Mockito.when(asset.getAssetName()).thenReturn("assetName");
        Map<String, Asset> assetMap = new HashMap<>();
        assetMap.put("assetCode", asset);

        Mockito.when(
                modelPortfolioHelper.getAllocationAssetMap(Mockito.anyListOf(ModelPortfolioAssetAllocation.class),
                        Mockito.any(ModelType.class), Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(
                assetMap);
        ApiSearchCriteria modelCriteria = new ApiSearchCriteria("modelId", ApiSearchCriteria.SearchOperation.EQUALS, "modelId",
                ApiSearchCriteria.OperationType.STRING);

        List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(modelCriteria);

        List<ModelPortfolioUploadDto> result = modelPortfolioUploadDtoService.search(criteria, new FailFastErrorsImpl());
        Assert.assertEquals(1, result.size());

        ModelPortfolioUploadDto dto = result.get(0);
        Assert.assertEquals("modelId", dto.getKey().getModelId());
        Assert.assertEquals("summaryModelCode", dto.getModelCode());
        Assert.assertEquals("modelName", dto.getModelName());
        Assert.assertEquals("commentary", dto.getCommentary());
        Assert.assertEquals(1, dto.getAssetAllocations().size());

        ModelPortfolioAssetAllocationDto allocationDto = dto.getAssetAllocations().get(0);
        Assert.assertEquals("assetId", allocationDto.getAssetId());
        Assert.assertEquals("assetName", allocationDto.getAssetName());
        Assert.assertEquals("assetCode", allocationDto.getAssetCode());
    }

    @Test
    public void testSearch_whenOnlyModelIdProvidedAndAllocationNotFound_thenNewObjectReturned() {
        ModelPortfolioDetail details = Mockito.mock(ModelPortfolioDetail.class);
        Mockito.when(details.getId()).thenReturn("modelId");
        Mockito.when(modelPortfolioHelper.getModelPortfolioDetails(Mockito.any(IpsKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(details);

        IpsSummaryDetails summary = Mockito.mock(IpsSummaryDetails.class);
        Mockito.when(summary.getAccountType()).thenReturn(ModelType.INVESTMENT.getId());
        Mockito.when(summary.getModelCode()).thenReturn("summaryModelCode");
        Mockito.when(
                modelPortfolioHelper.getIpsSummaryDetails(Mockito.any(ModelPortfolioDetail.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(summary);

        ApiSearchCriteria modelCriteria = new ApiSearchCriteria("modelId", ApiSearchCriteria.SearchOperation.EQUALS, "modelId",
                ApiSearchCriteria.OperationType.STRING);

        List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(modelCriteria);

        List<ModelPortfolioUploadDto> result = modelPortfolioUploadDtoService.search(criteria, new FailFastErrorsImpl());
        Assert.assertEquals(1, result.size());

        ModelPortfolioUploadDto dto = result.get(0);
        Assert.assertNull(dto.getKey());
        Assert.assertEquals("summaryModelCode", dto.getModelCode());
        Assert.assertNull(dto.getModelName());
        Assert.assertNull(dto.getCommentary());
        Assert.assertNull(dto.getAssetAllocations());
    }

    @Test
    public void testSearch_whenEmptyAllocationProvided_NewObjectReturned() {
        ModelPortfolioDetail details = Mockito.mock(ModelPortfolioDetail.class);
        Mockito.when(details.getId()).thenReturn("modelId");
        Mockito.when(modelPortfolioHelper.getModelPortfolioDetails(Mockito.any(IpsKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(details);

        IpsSummaryDetails summary = Mockito.mock(IpsSummaryDetails.class);
        Mockito.when(summary.getAccountType()).thenReturn(ModelType.INVESTMENT.getId());
        Mockito.when(summary.getModelCode()).thenReturn("modelCode");
        Mockito.when(summary.getModelOrderId()).thenReturn("12345");
        Mockito.when(
                modelPortfolioHelper.getIpsSummaryDetails(Mockito.any(ModelPortfolioDetail.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(summary);

        ApiSearchCriteria modelCriteria = new ApiSearchCriteria("modelId", ApiSearchCriteria.SearchOperation.EQUALS, "modelId",
                ApiSearchCriteria.OperationType.STRING);
        ApiSearchCriteria searchCriteria = new ApiSearchCriteria("modelCode", ApiSearchCriteria.SearchOperation.EQUALS,
                "modelCode", ApiSearchCriteria.OperationType.STRING);
        ApiSearchCriteria allocIdCriteria = new ApiSearchCriteria("allocationId", ApiSearchCriteria.SearchOperation.EQUALS, "",
                ApiSearchCriteria.OperationType.STRING);

        List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(modelCriteria);
        criteria.add(searchCriteria);
        criteria.add(allocIdCriteria);

        List<ModelPortfolioUploadDto> result = modelPortfolioUploadDtoService.search(criteria, new FailFastErrorsImpl());
        Assert.assertEquals(1, result.size());

        ModelPortfolioUploadDto dto = result.get(0);
        Assert.assertNull(dto.getKey());
        Assert.assertEquals("modelCode", dto.getModelCode());
        Assert.assertNull(dto.getModelName());
        Assert.assertNull(dto.getCommentary());
        Assert.assertNull(dto.getAssetAllocations());
    }

    @Test
    public void testLoadAssetAllocationsForFloatingTMP() {
        IpsSummaryDetails summaryDto = mockIpsSummaryDetails(ConstructionType.FLOATING);
        Mockito.when(
                modelPortfolioHelper.getIpsSummaryDetails(Mockito.any(ModelPortfolioDetail.class),
                        Mockito.any(ServiceErrors.class)))
                .thenReturn(summaryDto);

        Map<String, Asset> assetMap = new HashMap<>();
        assetMap.put("assetCode", mockShareAsset());
        Mockito.when(
                modelPortfolioHelper.getAllocationAssetMap(Mockito.anyListOf(ModelPortfolioAssetAllocation.class),
                        Mockito.any(ModelType.class), Mockito.any(String.class), Mockito.any(ServiceErrors.class))).thenReturn(
                assetMap);

        // Load Floating point target.
        Map<String, BigDecimal> targetMap = new HashMap<>();
        targetMap.put("assetId", BigDecimal.TEN);
        Mockito.when(
                modelPortfolioHelper.getFloatingTargetAllocationMap(Mockito.any(IpsKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(targetMap);

        ApiSearchCriteria modelCriteria = new ApiSearchCriteria("modelId", ApiSearchCriteria.SearchOperation.EQUALS, "modelId",
                ApiSearchCriteria.OperationType.STRING);
        ApiSearchCriteria searchCriteria = new ApiSearchCriteria("modelCode", ApiSearchCriteria.SearchOperation.EQUALS,
                "modelCode", ApiSearchCriteria.OperationType.STRING);
        ApiSearchCriteria allocIdCriteria = new ApiSearchCriteria("allocationId", ApiSearchCriteria.SearchOperation.EQUALS,
                "allocationId", ApiSearchCriteria.OperationType.STRING);

        List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(modelCriteria);
        criteria.add(searchCriteria);
        criteria.add(allocIdCriteria);

        List<ModelPortfolioUploadDto> result = modelPortfolioUploadDtoService.search(criteria, new FailFastErrorsImpl());
        Assert.assertTrue(result != null);
        ModelPortfolioUploadDto dto = result.get(0);
        Assert.assertEquals(BigDecimal.TEN, dto.getAssetAllocations().get(0).getAssetAllocation());
        Assert.assertEquals(BigDecimal.ONE, dto.getAssetAllocations().get(0).getLastEditedAllocation());

        // Case when floating map is empty.
        targetMap = new HashMap<>();
        Mockito.when(
                modelPortfolioHelper.getFloatingTargetAllocationMap(Mockito.any(IpsKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(targetMap);
        result = modelPortfolioUploadDtoService.search(criteria, new FailFastErrorsImpl());
        Assert.assertTrue(result != null);
        dto = result.get(0);
        // AssetAllocation value will be as per model's value.
        Assert.assertEquals(BigDecimal.ONE, dto.getAssetAllocations().get(0).getAssetAllocation());
        // LastEditAllocation will be set to the model's original assetAllocation.
        Assert.assertEquals(BigDecimal.ONE, dto.getAssetAllocations().get(0).getLastEditedAllocation());
    }

    @Test
    public void testLoadAssetAllocationsForFixedTMP() {
        IpsSummaryDetails summaryDto = mockIpsSummaryDetails(ConstructionType.FIXED);
        Mockito.when(
                modelPortfolioHelper.getIpsSummaryDetails(Mockito.any(ModelPortfolioDetail.class),
                        Mockito.any(ServiceErrors.class)))
                .thenReturn(summaryDto);

        Map<String, Asset> assetMap = new HashMap<>();
        assetMap.put("assetCode", mockShareAsset());
        Mockito.when(
                modelPortfolioHelper.getAllocationAssetMap(Mockito.anyListOf(ModelPortfolioAssetAllocation.class),
                        Mockito.any(ModelType.class), Mockito.any(String.class), Mockito.any(ServiceErrors.class))).thenReturn(
                assetMap);

        ModelPortfolioDetail details = Mockito.mock(ModelPortfolioDetail.class);
        Mockito.when(details.getId()).thenReturn("modelId");
        Mockito.when(modelPortfolioHelper.getModelPortfolioDetails(Mockito.any(IpsKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(details);

        ApiSearchCriteria modelCriteria = new ApiSearchCriteria("modelId", ApiSearchCriteria.SearchOperation.EQUALS, "modelId",
                ApiSearchCriteria.OperationType.STRING);
        ApiSearchCriteria searchCriteria = new ApiSearchCriteria("modelCode", ApiSearchCriteria.SearchOperation.EQUALS,
                "modelCode", ApiSearchCriteria.OperationType.STRING);
        ApiSearchCriteria allocIdCriteria = new ApiSearchCriteria("allocationId", ApiSearchCriteria.SearchOperation.EQUALS,
                "allocationId", ApiSearchCriteria.OperationType.STRING);

        List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(modelCriteria);
        criteria.add(searchCriteria);
        criteria.add(allocIdCriteria);

        List<ModelPortfolioUploadDto> result = modelPortfolioUploadDtoService.search(criteria, new FailFastErrorsImpl());
        Assert.assertTrue(result != null);
        ModelPortfolioUploadDto dto = result.get(0);
        Assert.assertEquals(BigDecimal.ONE, dto.getAssetAllocations().get(0).getAssetAllocation());
        Assert.assertNull(dto.getAssetAllocations().get(0).getLastEditedAllocation());
    }

    private IpsSummaryDetails mockIpsSummaryDetails(ConstructionType type) {
        IpsSummaryDetails summaryDto = Mockito.mock(IpsSummaryDetails.class);
        Mockito.when(summaryDto.getAccountType()).thenReturn(ModelType.SUPERANNUATION.getId());
        Mockito.when(summaryDto.getModelCode()).thenReturn("modelCode");
        Mockito.when(summaryDto.getModelOrderId()).thenReturn("12345");
        Mockito.when(summaryDto.getModelConstruction()).thenReturn(type);

        return summaryDto;
    }

    private ShareAsset mockShareAsset() {
        ShareAsset asset = Mockito.mock(ShareAsset.class);
        Mockito.when(asset.getAssetId()).thenReturn("assetId");
        Mockito.when(asset.getAssetCode()).thenReturn("assetCode");
        Mockito.when(asset.getAssetName()).thenReturn("assetName");
        Mockito.when(asset.getAssetType()).thenReturn(AssetType.SHARE);
        Mockito.when(asset.getInvestmentHoldingLimit()).thenReturn(BigDecimal.TEN);

        return asset;
    }
}
