package com.bt.nextgen.reports.advisermodel;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioAssetAllocationDto;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioUploadDto;
import com.bt.nextgen.api.modelportfolio.v2.model.detail.ModelPortfolioDetailDto;
import com.bt.nextgen.api.modelportfolio.v2.service.ModelPortfolioUploadDtoService;
import com.bt.nextgen.api.modelportfolio.v2.service.detail.ModelPortfolioDetailDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.reporting.BaseReport;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.modelportfolio.UploadAssetCodeEnum;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * CSV Report containing adviser model summary and allocations.
 */
@Report("adviserModelCsvReport")
// TODO: Apply correct adviser model view permission when available
@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_model_portfolios')")
public class AdviserModelCsvReport extends BaseReport {

    private static final String MODEL_ID = "model-id";
    private static final String ALLOCATION_ID = "allocation-id";
    private static final String INCLUDE_CASH = "includeCash";
    private static final String MODEL_CASH_NAME = "Model Cash";

    @Autowired
    private ModelPortfolioDetailDtoService detailDtoService;

    @Autowired
    private ModelPortfolioUploadDtoService uploadDtoService;

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        String modelId = (String) params.get(MODEL_ID);
        String allocationId = (String) params.get(ALLOCATION_ID);
        String includeCashRow = (String) params.get(INCLUDE_CASH);

        if (StringUtils.isBlank(modelId)) {
            throw new IllegalArgumentException("Provide a valid model ID");
        }

        ServiceErrors serviceErrors = new FailFastErrorsImpl();

        ModelPortfolioDetailDto details = loadAdviserModelDetails(modelId, serviceErrors);

        List<AdviserModelAllocationReportData> allocations = new ArrayList<>();
        if (StringUtils.isNotBlank(allocationId)) {
            allocations = loadAdviserModelAllocations(modelId, allocationId, serviceErrors);
        } else if (Boolean.parseBoolean(includeCashRow)) {
            allocations = prepopulateModelCashRow();
        }

        AdviserModelReportData reportData = new AdviserModelReportData(details, allocations);
        return Collections.singletonList(reportData);
    }

    private ModelPortfolioDetailDto loadAdviserModelDetails(String modelId, ServiceErrors serviceErrors) {
        ModelPortfolioKey key = new ModelPortfolioKey(modelId);
        return detailDtoService.find(key, serviceErrors);
    }

    private List<AdviserModelAllocationReportData> loadAdviserModelAllocations(String modelId, String allocationId,
            ServiceErrors serviceErrors) {
        ApiSearchCriteria modelCriteria = new ApiSearchCriteria("modelId", ApiSearchCriteria.SearchOperation.EQUALS, modelId,
                ApiSearchCriteria.OperationType.STRING);

        ApiSearchCriteria allocIdCriteria = new ApiSearchCriteria("allocationId", ApiSearchCriteria.SearchOperation.EQUALS,
                allocationId, ApiSearchCriteria.OperationType.STRING);

        List<ModelPortfolioUploadDto> modelList = uploadDtoService.search(Arrays.asList(modelCriteria, allocIdCriteria),
                serviceErrors);

        List<AdviserModelAllocationReportData> allocations = new ArrayList<>();
        for (ModelPortfolioAssetAllocationDto allocation : modelList.get(0).getAssetAllocations()) {
            allocations.add(new AdviserModelAllocationReportData(allocation));
        }

        return allocations;
    }

    private List<AdviserModelAllocationReportData> prepopulateModelCashRow() {
        String cashAssetCode = UploadAssetCodeEnum.ADVISER_MODEL_CASH.value();
        AdviserModelAllocationReportData cashRow = new AdviserModelAllocationReportData(cashAssetCode, MODEL_CASH_NAME,
                BigDecimal.ZERO, BigDecimal.ZERO);
        return Collections.singletonList(cashRow);
    }
}
