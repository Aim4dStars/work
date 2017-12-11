package com.bt.nextgen.api.holdingbreach.v1.service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.holdingbreach.v1.model.HoldingBreachAssetDto;
import com.bt.nextgen.api.holdingbreach.v1.model.HoldingBreachDto;
import com.bt.nextgen.api.holdingbreach.v1.model.HoldingBreachSummaryDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.holdingbreach.HoldingBreach;
import com.bt.nextgen.service.integration.holdingbreach.HoldingBreachAsset;
import com.bt.nextgen.service.integration.holdingbreach.HoldingBreachIntegrationService;
import com.bt.nextgen.service.integration.holdingbreach.HoldingBreachSummary;
import com.btfin.panorama.service.integration.account.AccountSecurityIntegrationService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({ "squid:S1200" })
@Service
public class HoldingBreachDtoServiceImpl implements HoldingBreachDtoService {
    private static final Logger logger = LoggerFactory.getLogger(HoldingBreachDtoServiceImpl.class);

    @Autowired
    private HoldingBreachIntegrationService holdingBreachService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountSecurityIntegrationService accountService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    private AssetDtoConverter assetDtoConverter;

    @Override
    public HoldingBreachSummaryDto findOne(ServiceErrors serviceErrors) {
        HoldingBreachSummary breachSummary = holdingBreachService.loadHoldingBreaches(serviceErrors);

        return toHoldingBreachSummaryDto(breachSummary, getAssetMap(breachSummary, serviceErrors), serviceErrors);
    }

    private Map<String, AssetDto> getAssetMap(HoldingBreachSummary breachSummary, ServiceErrors serviceErrors) {
        Set<String> assetIds = new HashSet<>();
        for (HoldingBreach breach : breachSummary.getHoldingBreaches()) {
            for (HoldingBreachAsset breachAsset : breach.getBreachAssets()) {
                if (breachAsset.getAssetId() != null) {
                    assetIds.add(breachAsset.getAssetId());
                }
            }
        }

        logger.debug("Asset Ids size:{}", assetIds.size());
        Map<String, Asset> assets = assetService.loadAssets(assetIds, serviceErrors);

        // warn if assets not found
        if (assets.size() != assetIds.size()) {
            for (String assetId : assetIds) {
                if (assets.get(assetId) == null) {
                    logger.warn("Asset id {} not found in asset service", assetId);
                }
            }
        }

        return assetDtoConverter.toAssetDto(assets, new HashMap<String, TermDepositAssetDetail>(), true);
    }

    protected HoldingBreachSummaryDto toHoldingBreachSummaryDto(HoldingBreachSummary breachSummary,
            Map<String, AssetDto> assetMap, ServiceErrors serviceErrors) {
        Map<AccountKey, WrapAccount> accountMap = accountService.loadWrapAccountWithoutContainers(serviceErrors);
        List<HoldingBreachDto> breachDtos = new ArrayList<>();

        for (HoldingBreach breach : breachSummary.getHoldingBreaches()) {
            List<HoldingBreachAssetDto> breachAssetDtos = new ArrayList<>();
            for (HoldingBreachAsset breachAsset : breach.getBreachAssets()) {
                HoldingBreachAssetDto breachAssetDto = new HoldingBreachAssetDto(assetMap.get(breachAsset.getAssetId()),
                        breachAsset.getMarketValue(), breachAsset.getPortfolioPercent(), breachAsset.getHoldingLimitPercent(),
                        breachAsset.getBreachAmount());
                breachAssetDtos.add(breachAssetDto);
            }

            if (CollectionUtils.isNotEmpty(breachAssetDtos)) {
                WrapAccount account = accountMap.get(AccountKey.valueOf(breach.getAccountId()));

                HoldingBreachDto breachDto = new HoldingBreachDto(account.getAccountNumber(), account.getAccountName(),
                        EncodedString.fromPlainText(account.getAccountKey().getId()).toString(),
                        account.getSuperAccountSubType() == null ? "" : account.getSuperAccountSubType().getAccountType(),
                        breach.getValuationAmount(), breachAssetDtos);
                breachDtos.add(breachDto);
            } else {
                logger.warn("No holding breach assets found for account {}", breach.getAccountId());
            }
        }

        return new HoldingBreachSummaryDto(breachSummary.getReportDate(), breachDtos);
    }
}
