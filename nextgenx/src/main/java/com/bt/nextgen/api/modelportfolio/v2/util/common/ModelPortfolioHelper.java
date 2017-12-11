package com.bt.nextgen.api.modelportfolio.v2.util.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.ModelPortfolioType;
import com.bt.nextgen.api.modelportfolio.v2.model.detail.TargetAllocationDto;
import com.bt.nextgen.api.modelportfolio.v2.service.ModelPortfolioSummaryDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.ips.IpsSummaryDetails;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioAssetAllocation;
import com.bt.nextgen.service.integration.modelportfolio.UploadAssetCodeEnum;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioDetail;
import com.bt.nextgen.service.integration.modelportfolio.detail.TargetAllocation;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.service.integration.broker.Broker;

@Service
public class ModelPortfolioHelper {

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    private AssetDtoConverter assetDtoConverter;

    @Autowired
    private ModelPortfolioSummaryDtoService modelPortfolioSummaryService;

    @Autowired
    private InvestmentPolicyStatementIntegrationService invPolicyService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ShadowPortfolioHelper shdwPortfHelper;

    public BrokerKey getCurrentBroker(ServiceErrors serviceErrors) {
        Broker im = userProfileService.getInvestmentManager(serviceErrors);
        if (im == null) {
            throw new BadRequestException(ApiVersion.CURRENT_VERSION, "Service only available to investment managers");
        }
        return im.getKey();
    }

    public Map<String, AssetDto> getAssetDtoMap(List<TargetAllocation> taaList, ServiceErrors serviceErrors) {
        List<String> assetIds = new ArrayList<String>();

        for (TargetAllocation taa : taaList) {
            if (taa.getIndexAssetId() != null) {
                assetIds.add(taa.getIndexAssetId());
            }
        }

        Map<String, Asset> assetMap = assetService.loadAssets(assetIds, serviceErrors);
        Map<String, AssetDto> assetDtoMap = assetDtoConverter.toAssetDto(assetMap, new HashMap<String, TermDepositAssetDetail>());
        return assetDtoMap;
    }

    public Map<String, Asset> getAllocationAssetMap(List<ModelPortfolioAssetAllocation> aaList, ModelType modelType,
            String portfolioType, ServiceErrors serviceErrors) {
        List<String> assetIds = new ArrayList<String>();

        for (ModelPortfolioAssetAllocation aa : aaList) {
            assetIds.add(aa.getAssetCode());
        }

        Map<String, Asset> result = assetService.loadAssets(assetIds, serviceErrors);
        Map<String, Asset> assetMap = new HashMap<>();
        for (String id : result.keySet()) {
            // Replace cash asset with correct assetCode.
            Asset asset = result.get(id);
            if (AssetType.CASH == asset.getAssetType() && asset.getAssetCode() == null) {
                AssetImpl a = new AssetImpl();
                a.setAssetId(asset.getAssetId());
                a.setAssetName(asset.getAssetName());
                a.setAssetCode(getCashAssetCode(modelType, portfolioType));
                a.setAssetType(asset.getAssetType());
                a.setAssetClass(asset.getAssetClass());
                a.setModelAssetClass(asset.getModelAssetClass());

                assetMap.put(id, a);
                continue;
            }
            assetMap.put(id, asset);
        }
        return assetMap;
    }

    private String getCashAssetCode(ModelType modelType, String portfolioType) {
        if (ModelPortfolioType.PREFERRED.getIntlId().equalsIgnoreCase(portfolioType)) {
            return UploadAssetCodeEnum.ADVISER_MODEL_CASH.value();
        } else if (ModelType.SUPERANNUATION == modelType) {
            return UploadAssetCodeEnum.SUPER_TMP_CASH.value();
        }
        return UploadAssetCodeEnum.TMP_CASH.value();
    }

    /**
     * Re-adjust the percentage values in targetAllocation. The reason why this is required is because of inconsistencies in
     * Avaloq's responses where values for 20% can sometimes be returned as 0.2 and other times as 20.0 for different services. A
     * workaround while Avaloq is investigating.
     * 
     * @param taa
     * @param indexAsset
     * @param multiplier
     * @return
     */
    public TargetAllocationDto getTargetAllocationDto(TargetAllocation taa, AssetDto indexAsset, boolean multiplier) {
        TargetAllocationDto taaDto = new TargetAllocationDto(taa.getAssetClass(), taa.getMinimumWeight(), taa.getMaximumWeight(),
                taa.getNeutralPos(), indexAsset);
        if (multiplier) {
            BigDecimal mul = BigDecimal.valueOf(100d);
            BigDecimal minWeight = taa.getMinimumWeight() == null ? null : taa.getMinimumWeight().multiply(mul);
            BigDecimal maxWeight = taa.getMaximumWeight() == null ? null : taa.getMaximumWeight().multiply(mul);
            BigDecimal neutralPos = taa.getNeutralPos() == null ? null : taa.getNeutralPos().multiply(mul);
            boolean ignore = minWeight.compareTo(mul) > 0 || maxWeight.compareTo(mul) > 0 || neutralPos.compareTo(mul) > 0;
            if (!ignore) {
                taaDto = new TargetAllocationDto(taa.getAssetClass(), minWeight, maxWeight, neutralPos, indexAsset);
            }
        }
        return taaDto;
    }

    /**
     * Find the investment manager for a given model and use their broker ID to load a summary of the model details. This allows
     * users to load the investments of a TMP without knowing who the investment manager is.
     * 
     * @param key
     * @param serviceErrors
     * @return a single IpsSummaryDetails or null
     */
    public IpsSummaryDetails getIpsSummaryDetails(ModelPortfolioKey key, ServiceErrors serviceErrors) {
        ModelPortfolioDetail detail = getModelPortfolioDetails(IpsKey.valueOf(key.getModelId()), serviceErrors);
        return getIpsSummaryDetails(detail, serviceErrors);
    }

    /**
     * Load the model summary for a specific model for which the details have already been loaded
     * 
     * @param detail
     * @param serviceErrors
     * @return a single IpsSummaryDetails or null
     */
    public IpsSummaryDetails getIpsSummaryDetails(ModelPortfolioDetail detail, ServiceErrors serviceErrors) {
        if (detail != null) {
            BrokerKey brokerKey = detail.getInvestmentManagerId();
            List<IpsSummaryDetails> summaryDetails = invPolicyService.getDealerGroupIpsSummary(brokerKey, serviceErrors);

            if (summaryDetails != null) {
                for (IpsSummaryDetails summaryDetail : summaryDetails) {
                    if (detail.getId().equals(summaryDetail.getModelKey().getId())) {
                        return summaryDetail;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Load the details for a specific model
     * 
     * @param ipsKey
     * @param serviceErrors
     * @return a single ModelPortfolioDetail or null
     */
    public ModelPortfolioDetail getModelPortfolioDetails(IpsKey ipsKey, ServiceErrors serviceErrors) {
        Map<IpsKey, ModelPortfolioDetail> result = invPolicyService.getModelDetails(Collections.singletonList(ipsKey),
                serviceErrors);
        return result.get(ipsKey);
    }

    public Map<String, BigDecimal> getFloatingTargetAllocationMap(IpsKey key, ServiceErrors serviceErrors) {
        return shdwPortfHelper.getFloatingTargetAllocationMap(key, serviceErrors);
    }
}
