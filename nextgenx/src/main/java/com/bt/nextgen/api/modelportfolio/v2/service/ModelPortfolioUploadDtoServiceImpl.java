package com.bt.nextgen.api.modelportfolio.v2.service;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioAssetAllocationDto;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioUploadDto;
import com.bt.nextgen.api.modelportfolio.v2.util.common.ModelPortfolioHelper;
import com.bt.nextgen.api.modelportfolio.v2.validation.ModelPortfolioDtoErrorMapper;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.modelportfolio.ModelPortfolioAssetAllocationImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.ModelPortfolioUploadImpl;
import com.bt.nextgen.service.integration.asset.Asset;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("ModelPortfolioUploadDtoServiceV2")
@Transactional(value = "springJpaTransactionManager")
public class ModelPortfolioUploadDtoServiceImpl implements ModelPortfolioUploadDtoService {
    @Autowired
    private ModelPortfolioIntegrationService modelPortfolioService;

    @Autowired
    private ModelPortfolioDtoErrorMapper errorMapper;

    @Autowired
    private ModelPortfolioHelper helper;


    @Override
    public ModelPortfolioUploadDto submit(ModelPortfolioUploadDto modelUploadDto, ServiceErrors serviceErrors) {
        if (modelUploadDto.getCommentary() == null) {
            modelUploadDto.setCommentary("Construct new TMP");
        }
        ModelPortfolioUpload modelPortfolioSubmit = modelPortfolioService.submitModel(toModelPortfolioUpload(modelUploadDto),
                serviceErrors);
        TransactionResponse txnResp = (TransactionResponse) modelPortfolioSubmit;

        if (!txnResp.getValidationErrors().isEmpty()) {
            modelUploadDto.setWarnings(errorMapper.map(txnResp.getValidationErrors()));
        }
        return modelUploadDto;
    }

    protected ModelPortfolioUpload toModelPortfolioUpload(ModelPortfolioUploadDto modelUploadDto) {
        ModelPortfolioUploadImpl modelUpload = new ModelPortfolioUploadImpl();
        modelUpload.setModelKey(modelUploadDto.getKey() == null ? null : IpsKey.valueOf(modelUploadDto.getKey().getModelId()));
        modelUpload.setModelCode(modelUploadDto.getModelCode());
        modelUpload.setModelName(modelUploadDto.getModelName());
        modelUpload.setCommentary(modelUploadDto.getCommentary());
        modelUpload.setAssetAllocations(toModelPortfolioAssetAllocations(modelUploadDto.getAssetAllocations()));
        return modelUpload;
    }

    protected List<ModelPortfolioAssetAllocation> toModelPortfolioAssetAllocations(
            List<ModelPortfolioAssetAllocationDto> allocationDtos) {
        List<ModelPortfolioAssetAllocation> allocations = new ArrayList<>();
        if (allocationDtos != null) {
            for (ModelPortfolioAssetAllocationDto allocationDto : allocationDtos) {
                allocations.add(toModelPortfolioAssetAllocation(allocationDto));
            }
        }

        return allocations;
    }

    protected ModelPortfolioAssetAllocation toModelPortfolioAssetAllocation(ModelPortfolioAssetAllocationDto allocationDto) {
        ModelPortfolioAssetAllocationImpl allocation = new ModelPortfolioAssetAllocationImpl();
        allocation.setAssetCode(allocationDto.getAssetCode());
        // convert values to what ABS expect (e.g. 0.5 -> 50%).
        allocation.setAssetAllocation(allocationDto.getAssetAllocation());
        allocation.setTradePercent(allocationDto.getTradePercent());
        allocation.setAssetTolerance(allocationDto.getToleranceLimit());
        return allocation;
    }

    @Override
    public ModelPortfolioUploadDto validate(ModelPortfolioUploadDto modelUploadDto, ServiceErrors serviceErrors) {
        if (modelUploadDto.getCommentary() == null) {
            modelUploadDto.setCommentary("Construct new TMP");
        }
        ModelPortfolioUpload modelPortfolioValidate = modelPortfolioService.validateModel(toModelPortfolioUpload(modelUploadDto),
                serviceErrors);
        TransactionResponse txnResp = (TransactionResponse) modelPortfolioValidate;

        if (!txnResp.getValidationErrors().isEmpty()) {
            modelUploadDto.setWarnings(errorMapper.map(txnResp.getValidationErrors()));
        }
        return modelUploadDto;
    }

    @Override
    public ModelPortfolioUploadDto update(ModelPortfolioUploadDto modelUploadDto, ServiceErrors serviceErrors) {

        // Need to retrieve the corresponding IPS-name (internal id)
        ModelPortfolioUpload model = modelPortfolioService.submitModel(toModelPortfolioUpload(modelUploadDto), serviceErrors);
        TransactionResponse txnResp = (TransactionResponse) model;
        if (!txnResp.getValidationErrors().isEmpty()) {
            modelUploadDto.setWarnings(errorMapper.map(txnResp.getValidationErrors()));
        }
        return modelUploadDto;
    }

    @Override
    public List<ModelPortfolioUploadDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        String modelCode = "";
        String allocationId = null;
        ModelPortfolioKey key = null;
        for (ApiSearchCriteria parameter : criteriaList) {
            switch (parameter.getProperty()) {
                case "modelId":
                    key = new ModelPortfolioKey(parameter.getValue().trim());
                    break;
                case "modelCode":
                    modelCode = parameter.getValue().trim();
                    break;
                case "allocationId":
                    allocationId = parameter.getValue().trim();
                    break;
                default:
                    break;
            }
        }

        ModelPortfolioDetail details = helper.getModelPortfolioDetails(IpsKey.valueOf(key.getModelId()), serviceErrors);
        IpsSummaryDetails summaryDetails = helper.getIpsSummaryDetails(details, serviceErrors);
        if (criteriaList.size() == 1) {
            modelCode = summaryDetails.getModelCode();
            allocationId = summaryDetails.getModelOrderId();
        }

        if (StringUtils.isBlank(allocationId)) {
            // No corresponding last_edit_doc_id found. Create a brand new instance.
            ModelPortfolioUploadDto mpDto = new ModelPortfolioUploadDto(null, modelCode, null, null, null);
            return Collections.singletonList(mpDto);
        }

        ModelType modelType = ModelType.INVESTMENT;
        if (summaryDetails.getAccountType() != null) {
            modelType = ModelType.forId(summaryDetails.getAccountType());
        }

        String mpSubType = details == null ? null : details.getMpSubType();
        ModelPortfolioUploadDto mpDto = loadAllocationDetails(IpsKey.valueOf(allocationId), modelCode, modelType, mpSubType,
                summaryDetails.getModelConstruction(), serviceErrors);
        return Collections.singletonList(mpDto);
    }

    private ModelPortfolioUploadDto loadAllocationDetails(IpsKey key, String modelCode, ModelType modelType,
            String portfolioType, ConstructionType constructionType, ServiceErrors serviceErrors) {
        ModelPortfolioUpload model = modelPortfolioService.loadUploadedModel(key, serviceErrors);
        Map<String, Asset> assetMap = helper.getAllocationAssetMap(model.getAssetAllocations(), modelType, portfolioType,
                serviceErrors);

        Map<String, BigDecimal> floatingMap = new HashMap<>();
        // When model is a FLOATING construction type, retrieve the floating point allocation from the shadow portfolio.
        boolean isFloatingModel = ConstructionType.FLOATING == constructionType;
        if (isFloatingModel) {
            // floatingMap = shdwPortfHelper.getFloatingTargetAllocationMap(model.getModelKey(), serviceErrors);
            floatingMap = helper.getFloatingTargetAllocationMap(model.getModelKey(), serviceErrors);
        }
        
        List<ModelPortfolioAssetAllocationDto> aaDtoList = new ArrayList<>();
        for (ModelPortfolioAssetAllocation aa : model.getAssetAllocations()) {
            Asset asset = assetMap.get(aa.getAssetCode());
            if (asset == null) {
                continue;
            }

            BigDecimal ihl = null;
            if (asset instanceof ShareAsset) {
                ihl = ((ShareAsset) asset).getInvestmentHoldingLimit();
            }
            // Reset the allocation value to floating-point for Floating TMP.
            BigDecimal lastEditedAllocation = getLastEditedAllocation(aa, isFloatingModel);
            BigDecimal allocation = getModelAllocation(aa, floatingMap.get(asset.getAssetId()), isFloatingModel);

            ModelPortfolioAssetAllocationDto dto = new ModelPortfolioAssetAllocationDto(asset, allocation, aa.getTradePercent(),
                    ihl, aa.getAssetTolerance(), lastEditedAllocation);
            aaDtoList.add(dto);
        }

        return new ModelPortfolioUploadDto(new ModelPortfolioKey(model.getModelKey().getId()), modelCode, model.getModelName(),
                model.getCommentary(), aaDtoList);
    }

    /**
     * Retrieve the effective modelAllocation based on the model-construction property. If FLOATING, then return the specified
     * floatingAllocation. Otherwise, return the existing assetAllocation.
     * 
     * @param aa
     * @param floatingAllocation
     * @param isFloating
     * @return
     */
    private BigDecimal getModelAllocation(ModelPortfolioAssetAllocation aa, BigDecimal floatingAllocation, boolean isFloating) {
        if (isFloating && floatingAllocation != null) {
            return floatingAllocation;
        }
        return aa.getAssetAllocation();
    }

    /**
     * Retrieve the lastEditedAllocation value for the specified instance.
     * 
     * @param aa
     * @param isFloating
     * @return null if NOT a floating model. Otherwise, return the assetAllocation.
     */
    private BigDecimal getLastEditedAllocation(ModelPortfolioAssetAllocation aa, boolean isFloating) {
        if (isFloating) {
            return aa.getAssetAllocation();
        }
        return null;
    }
}
