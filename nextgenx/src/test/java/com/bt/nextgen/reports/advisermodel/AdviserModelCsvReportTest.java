package com.bt.nextgen.reports.advisermodel;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioAssetAllocationDto;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioUploadDto;
import com.bt.nextgen.api.modelportfolio.v2.model.detail.ModelPortfolioDetailDto;
import com.bt.nextgen.api.modelportfolio.v2.service.ModelPortfolioUploadDtoService;
import com.bt.nextgen.api.modelportfolio.v2.service.detail.ModelPortfolioDetailDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.modelportfolio.UploadAssetCodeEnum;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class AdviserModelCsvReportTest {

    private static final String MODEL_ID = "model-id";
    private static final String ALLOCATION_ID = "allocation-id";
    private static final String INCLUDE_CASH = "includeCash";
    private static final String MODEL_CASH_NAME = "Model Cash";

    @InjectMocks
    private AdviserModelCsvReport adviserModelReport;

    @Mock
    private ModelPortfolioDetailDtoService detailDtoService;

    @Mock
    private ModelPortfolioUploadDtoService uploadDtoService;

    @Test(expected = IllegalArgumentException.class)
    public void testGetData_whenModelIdBlank_thenIllegalArgumentException() {
        Map<String, Object> params = new HashMap<>();

        adviserModelReport.getData(params, null);
    }

    @Test
    public void testGetData_whenAllocationIdBlank_thenEmptyAllocationListReturned() {
        Map<String, Object> params = new HashMap<>();
        params.put(MODEL_ID, "modelId");

        Mockito.when(detailDtoService.find(Mockito.any(ModelPortfolioKey.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<ModelPortfolioDetailDto>() {

                    @Override
                    public ModelPortfolioDetailDto answer(InvocationOnMock invocation) throws Throwable {
                        ModelPortfolioKey key = (ModelPortfolioKey) invocation.getArguments()[0];
                        Assert.assertEquals("modelId", key.getModelId());

                        ModelPortfolioDetailDto detailDto = Mockito.mock(ModelPortfolioDetailDto.class);
                        Mockito.when(detailDto.getModelName()).thenReturn("modelName");
                        Mockito.when(detailDto.getModelCode()).thenReturn("modelIdentifier");
                        Mockito.when(detailDto.getAccountType()).thenReturn(ModelType.INVESTMENT.getCode());
                        Mockito.when(detailDto.getModelDescription()).thenReturn("modelDescription");
                        return detailDto;
                    }
                });

        List<AdviserModelReportData> reportDataList = (List<AdviserModelReportData>) adviserModelReport.getData(params, null);

        AdviserModelReportData reportData = reportDataList.get(0);
        Assert.assertEquals("modelName", reportData.getModelName());
        Assert.assertEquals("modelIdentifier", reportData.getModelIdentifier());
        Assert.assertEquals(ModelType.INVESTMENT.getDisplayValue(), reportData.getAccountType());
        Assert.assertEquals("modelDescription", reportData.getDescription());
        Assert.assertEquals(0, reportData.getAllocations().size());
    }

    @Test
    public void testGetData_whenAllocationIdBlankAndIncludeCashTrue_thenSingleCashRowReturned() {
        Map<String, Object> params = new HashMap<>();
        params.put(MODEL_ID, "modelId");
        params.put(INCLUDE_CASH, "true");

        Mockito.when(detailDtoService.find(Mockito.any(ModelPortfolioKey.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<ModelPortfolioDetailDto>() {

                    @Override
                    public ModelPortfolioDetailDto answer(InvocationOnMock invocation) throws Throwable {
                        ModelPortfolioKey key = (ModelPortfolioKey) invocation.getArguments()[0];
                        Assert.assertEquals("modelId", key.getModelId());

                        ModelPortfolioDetailDto detailDto = Mockito.mock(ModelPortfolioDetailDto.class);
                        Mockito.when(detailDto.getModelName()).thenReturn("modelName");
                        Mockito.when(detailDto.getModelCode()).thenReturn("modelIdentifier");
                        Mockito.when(detailDto.getAccountType()).thenReturn(ModelType.INVESTMENT.getCode());
                        Mockito.when(detailDto.getModelDescription()).thenReturn("modelDescription");
                        return detailDto;
                    }
                });

        List<AdviserModelReportData> reportDataList = (List<AdviserModelReportData>) adviserModelReport.getData(params, null);

        AdviserModelReportData reportData = reportDataList.get(0);
        Assert.assertEquals("modelName", reportData.getModelName());
        Assert.assertEquals("modelIdentifier", reportData.getModelIdentifier());
        Assert.assertEquals(ModelType.INVESTMENT.getDisplayValue(), reportData.getAccountType());
        Assert.assertEquals("modelDescription", reportData.getDescription());
        Assert.assertEquals(1, reportData.getAllocations().size());

        AdviserModelAllocationReportData allocationData = reportData.getAllocations().get(0);
        Assert.assertEquals(MODEL_CASH_NAME, allocationData.getAssetName());
        Assert.assertEquals(UploadAssetCodeEnum.ADVISER_MODEL_CASH.value(), allocationData.getAssetCode());
        Assert.assertEquals("0.00", allocationData.getAllocationPercent());
        Assert.assertEquals("0.00", allocationData.getTolerancePercent());
    }

    @Test
    public void testGetData_whenArgumentsValidAndAllocationsExist_thenReportDataReturned() {
        Map<String, Object> params = new HashMap<>();
        params.put(MODEL_ID, "modelId");
        params.put(ALLOCATION_ID, "allocationId");

        Mockito.when(detailDtoService.find(Mockito.any(ModelPortfolioKey.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<ModelPortfolioDetailDto>() {

                    @Override
                    public ModelPortfolioDetailDto answer(InvocationOnMock invocation) throws Throwable {
                        ModelPortfolioKey key = (ModelPortfolioKey) invocation.getArguments()[0];
                        Assert.assertEquals("modelId", key.getModelId());

                        ModelPortfolioDetailDto detailDto = Mockito.mock(ModelPortfolioDetailDto.class);
                        Mockito.when(detailDto.getModelName()).thenReturn("modelName");
                        Mockito.when(detailDto.getModelCode()).thenReturn("modelIdentifier");
                        Mockito.when(detailDto.getAccountType()).thenReturn(ModelType.INVESTMENT.getCode());
                        Mockito.when(detailDto.getModelDescription()).thenReturn("modelDescription");
                        return detailDto;
                    }
                });

        Mockito.when(uploadDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<List<ModelPortfolioUploadDto>>() {

                    @Override
                    public List<ModelPortfolioUploadDto> answer(InvocationOnMock invocation) throws Throwable {
                        List<ApiSearchCriteria> criteria = (List<ApiSearchCriteria>) invocation.getArguments()[0];
                        Assert.assertEquals("modelId", criteria.get(0).getValue());
                        Assert.assertEquals("allocationId", criteria.get(1).getValue());

                        ModelPortfolioAssetAllocationDto allocationDto = Mockito.mock(ModelPortfolioAssetAllocationDto.class);
                        Mockito.when(allocationDto.getAssetName()).thenReturn("assetName");
                        Mockito.when(allocationDto.getAssetCode()).thenReturn("assetCode");
                        Mockito.when(allocationDto.getAssetAllocation()).thenReturn(BigDecimal.TEN);
                        Mockito.when(allocationDto.getToleranceLimit()).thenReturn(BigDecimal.ONE);

                        ModelPortfolioAssetAllocationDto allocationDto2 = Mockito.mock(ModelPortfolioAssetAllocationDto.class);
                        Mockito.when(allocationDto2.getAssetName()).thenReturn("assetName2");
                        Mockito.when(allocationDto2.getAssetCode()).thenReturn("assetCode2");
                        Mockito.when(allocationDto2.getAssetAllocation()).thenReturn(BigDecimal.ONE);
                        Mockito.when(allocationDto2.getToleranceLimit()).thenReturn(BigDecimal.ZERO);

                        ModelPortfolioUploadDto uploadDto = Mockito.mock(ModelPortfolioUploadDto.class);
                        Mockito.when(uploadDto.getAssetAllocations()).thenReturn(Arrays.asList(allocationDto, allocationDto2));

                        return Collections.singletonList(uploadDto);
                    }
                });

        List<AdviserModelReportData> reportDataList = (List<AdviserModelReportData>) adviserModelReport.getData(params, null);

        AdviserModelReportData reportData = reportDataList.get(0);
        Assert.assertEquals("modelName", reportData.getModelName());
        Assert.assertEquals("modelIdentifier", reportData.getModelIdentifier());
        Assert.assertEquals(ModelType.INVESTMENT.getDisplayValue(), reportData.getAccountType());
        Assert.assertEquals("modelDescription", reportData.getDescription());
        Assert.assertEquals(2, reportData.getAllocations().size());

        AdviserModelAllocationReportData allocationData = reportData.getAllocations().get(0);
        Assert.assertEquals("assetName", allocationData.getAssetName());
        Assert.assertEquals("assetCode", allocationData.getAssetCode());
        Assert.assertEquals("10.00", allocationData.getAllocationPercent());
        Assert.assertEquals("1.00", allocationData.getTolerancePercent());

        AdviserModelAllocationReportData allocationData2 = reportData.getAllocations().get(1);
        Assert.assertEquals("assetName2", allocationData2.getAssetName());
        Assert.assertEquals("assetCode2", allocationData2.getAssetCode());
        Assert.assertEquals("1.00", allocationData2.getAllocationPercent());
        Assert.assertEquals("0.00", allocationData2.getTolerancePercent());
    }

    @Test
    public void testGetData_whenArgumentsValidAndNoAllocationsExist_thenReportDataReturned() {
        Map<String, Object> params = new HashMap<>();
        params.put(MODEL_ID, "modelId");
        params.put(ALLOCATION_ID, "allocationId");

        Mockito.when(detailDtoService.find(Mockito.any(ModelPortfolioKey.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<ModelPortfolioDetailDto>() {

                    @Override
                    public ModelPortfolioDetailDto answer(InvocationOnMock invocation) throws Throwable {
                        ModelPortfolioKey key = (ModelPortfolioKey) invocation.getArguments()[0];
                        Assert.assertEquals("modelId", key.getModelId());

                        ModelPortfolioDetailDto detailDto = Mockito.mock(ModelPortfolioDetailDto.class);
                        Mockito.when(detailDto.getModelName()).thenReturn("modelName");
                        Mockito.when(detailDto.getModelCode()).thenReturn("modelIdentifier");
                        Mockito.when(detailDto.getAccountType()).thenReturn(ModelType.SUPERANNUATION.getCode());
                        Mockito.when(detailDto.getModelDescription()).thenReturn("modelDescription");
                        return detailDto;
                    }
                });

        ModelPortfolioUploadDto uploadDto = Mockito.mock(ModelPortfolioUploadDto.class);
        Mockito.when(uploadDto.getAssetAllocations()).thenReturn(Collections.<ModelPortfolioAssetAllocationDto> emptyList());
        Mockito.when(uploadDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(Collections.singletonList(uploadDto));

        List<AdviserModelReportData> reportDataList = (List<AdviserModelReportData>) adviserModelReport.getData(params, null);

        AdviserModelReportData reportData = reportDataList.get(0);
        Assert.assertEquals("modelName", reportData.getModelName());
        Assert.assertEquals("modelIdentifier", reportData.getModelIdentifier());
        Assert.assertEquals(ModelType.SUPERANNUATION.getDisplayValue(), reportData.getAccountType());
        Assert.assertEquals("modelDescription", reportData.getDescription());
        Assert.assertEquals(0, reportData.getAllocations().size());
    }
}
