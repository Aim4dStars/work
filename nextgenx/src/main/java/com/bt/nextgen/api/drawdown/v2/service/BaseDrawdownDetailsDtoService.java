package com.bt.nextgen.api.drawdown.v2.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.drawdown.v2.model.AssetPriorityDto;
import com.bt.nextgen.api.drawdown.v2.model.DrawdownDetailsDto;
import com.bt.nextgen.api.drawdown.v2.validation.DrawdownErrorMapper;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.drawdownstrategy.AssetPriorityDetailsImpl;
import com.bt.nextgen.service.avaloq.drawdownstrategy.DrawdownStrategyDetailsImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.drawdownstrategy.AssetPriorityDetails;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategy;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategyDetails;
import com.bt.nextgen.service.integration.portfolio.CachePortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.bt.nextgen.service.integration.transaction.TransactionResponse;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BaseDrawdownDetailsDtoService {

    @Autowired
    @Qualifier("cacheAvaloqPortfolioIntegrationService")
    private CachePortfolioIntegrationService cachedPortfolioIntegrationService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    private DrawdownErrorMapper errorMapper;

    protected DrawdownDetailsDto convertToDto(DrawdownStrategyDetails model, DrawdownStrategy strategy,
            ServiceErrors serviceErrors) {

        AccountKey accKey = new AccountKey(model.getAccountKey().getId());
        List<AssetPriorityDto> priorityList = new ArrayList<>();
        if (model.getAssetPriorityDetails() != null && !model.getAssetPriorityDetails().isEmpty()) {

            // Retrieve asset details from asset service
            final Map<String, Asset> assetMap = getAssetMap(model, serviceErrors);

            // Retrieve holding values
            Map<String, BigDecimal> holdingValues = getHoldingValueMap(accKey, serviceErrors);
            for (AssetPriorityDetails assetDetail : model.getAssetPriorityDetails()) {
                String assetId = assetDetail.getAssetId();
                priorityList.add(new AssetPriorityDto(assetMap.get(assetId), assetDetail.getDrawdownPriority(), holdingValues
                        .get(assetId)));
            }
        }

        // Encrypt accountId
        String accountId = EncodedString.fromPlainText(model.getAccountKey().getId()).toString();
        String drawdownStrategy = strategy == null ? null : strategy.getIntlId();
        DrawdownDetailsDto ddDto = new DrawdownDetailsDto(new AccountKey(accountId), drawdownStrategy, priorityList);

        // Map errors and validation from service response.
        DrawdownStrategyDetailsImpl modelImpl = (DrawdownStrategyDetailsImpl) model;
        List<ValidationError> errorList = ((TransactionResponse) modelImpl).getValidationErrors();
        ddDto.setWarnings(errorMapper.map(errorList));
        return ddDto;
    }

    /**
     * Create an instance of DrawdownStrategyDetails model based on the dto specified. Because of the underlying avaloq-service
     * where it will only process either the Drawdown option OR asset-priority list, this method will only populate the model with
     * asset-priority list as specified in the DTO. Even if both have been provided, the drawdown-strategy will be ignored.
     * 
     * @param drawdownDto
     * @return
     */
    protected DrawdownStrategyDetails convertoToAssetPriorityModel(DrawdownDetailsDto drawdownDto) {

        DrawdownStrategyDetailsImpl ddModel = constructModelForAccount(drawdownDto);
        if (drawdownDto.getPriorityDrawdownList() != null) {
            List<AssetPriorityDetails> priorityModelList = new ArrayList<>();
            for (AssetPriorityDto priorityDto : drawdownDto.getPriorityDrawdownList()) {
                AssetPriorityDetailsImpl priorityModel = new AssetPriorityDetailsImpl();
                priorityModel.setAssetId(priorityDto.getAssetId());
                priorityModel.setDrawdownPriority(priorityDto.getDrawdownPriority());

                priorityModelList.add(priorityModel);
            }
            ddModel.setAssetPriorityDetails(priorityModelList);
            ddModel.setValidationErrors(errorMapper.mapWarnings(drawdownDto.getWarnings()));
        }
        return ddModel;
    }

    /**
     * Create an instance of DrawdownStrategyDetails model based on the dto specified. Because of the underlying avaloq-service
     * where it will only process either the Drawdown option OR asset-priority list, this method will only populate the model with
     * drawdown-strategy.
     * 
     * @param drawdownDto
     * @return
     */
    protected DrawdownStrategyDetails convertoToStrategyModel(DrawdownDetailsDto drawdownDto) {

        DrawdownStrategyDetailsImpl ddModel = constructModelForAccount(drawdownDto);
        ddModel.setDrawdownStrategy(DrawdownStrategy.forIntlId(drawdownDto.getDrawdownType()));
        return ddModel;
    }

    /**
     * Construct an instance of the DrawdownStrategyDetailsImpl model. Only the account-id will be populated for this instance.
     * 
     * @param drawdownDto
     * @return
     */
    private DrawdownStrategyDetailsImpl constructModelForAccount(DrawdownDetailsDto drawdownDto) {
        String accountId = EncodedString.toPlainText(drawdownDto.getKey().getAccountId());
        DrawdownStrategyDetailsImpl ddModel = new DrawdownStrategyDetailsImpl();
        ddModel.setAccountKey(com.bt.nextgen.service.integration.account.AccountKey.valueOf(accountId));
        return ddModel;
    }

    /**
     * Retrieve asset details based on all asset-ids specified in the drawdownStrategyDetails model.
     * 
     * @param model
     * @param serviceErrors
     * @return Map of asset with asset-id as key.
     */
    private Map<String, Asset> getAssetMap(DrawdownStrategyDetails model, ServiceErrors serviceErrors) {
        List<String> assetIdList = new ArrayList<>();
        for (AssetPriorityDetails assetDetail : model.getAssetPriorityDetails()) {
            assetIdList.add(assetDetail.getAssetId());
        }

        return assetService.loadAssets(assetIdList, serviceErrors);
    }

    /**
     * Retrieve a holding-value map from the current valuation service.
     * 
     * @param key
     * @param serviceErrors
     * @return
     */
    private Map<String, BigDecimal> getHoldingValueMap(AccountKey key, ServiceErrors serviceErrors) {

        // Use dateTime instance without the hour, minute and second in cache.
        DateTime now = DateTime.now();
        DateTime effDate = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0);

        WrapAccountValuation valuation = cachedPortfolioIntegrationService.loadWrapAccountValuation(
                com.bt.nextgen.service.integration.account.AccountKey.valueOf(key.getAccountId()), effDate, serviceErrors);

        Map<String, BigDecimal> holdingBalanceMap = new HashMap<>();

        for (SubAccountValuation val : valuation.getSubAccountValuations()) {
            if (AssetType.MANAGED_PORTFOLIO == val.getAssetType() || AssetType.TAILORED_PORTFOLIO == val.getAssetType()) {
                holdingBalanceMap.put(((ManagedPortfolioAccountValuation) val).getAsset().getAssetId(), val.getBalance());
                continue;
            }
            for (AccountHolding holding : val.getHoldings()) {
                holding.getAsset().getStatus();
                holdingBalanceMap.put(holding.getAsset().getAssetId(), holding.getMarketValue());
            }
        }
        return holdingBalanceMap;
    }
}
