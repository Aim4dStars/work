package com.bt.nextgen.api.trading.v1.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetTypeDto;
import com.bt.nextgen.api.trading.v1.service.assetbuilder.ManagedFundAssetBuilder;
import com.bt.nextgen.api.trading.v1.service.assetbuilder.ManagedPortfolioAssetBuilder;
import com.bt.nextgen.api.trading.v1.service.assetbuilder.ShareAssetBuilder;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceHelper;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioStatus;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TradeAssetDtoConverter {
    @Autowired
    private ManagedFundAssetBuilder managedFundAssetBuilder;
    @Autowired
    private ManagedPortfolioAssetBuilder managedPortfolioAssetBuilder;
    @Autowired
    private ShareAssetBuilder shareAssetBuilder;
    @Autowired
    private InvestmentPolicyStatementIntegrationService ipsIntegrationService;

    private ServiceErrors serviceErrors;

    public List<TradeAssetDto> toAssetDto(Map<String, Asset> filteredAvailableAssets, WrapAccountValuation valuation,
            Map<String, Asset> filteredAssets, boolean assetTypeHeld,
            Map<String, List<DistributionMethod>> assetDistributionMethods, DateTime bankDate) {
        Map<String, TradeAssetDto> valuationTradeAssetDtos = new HashMap<>();

        if (valuation != null) {
            for (SubAccountValuation subAccount : valuation.getSubAccountValuations()) {
                AssetType subAssetType = subAccount.getAssetType();
                if ((AssetType.MANAGED_PORTFOLIO.equals(subAssetType) || AssetType.TAILORED_PORTFOLIO.equals(subAssetType))
                        && ((ManagedPortfolioAccountValuation) subAccount).getStatus() != ManagedPortfolioStatus.CLOSED) {
                    valuationTradeAssetDtos.putAll(managedPortfolioAssetBuilder.buildTradeAssets(subAccount, filteredAssets,
                            filteredAvailableAssets, bankDate));
                } else if (AssetType.MANAGED_FUND.equals(subAssetType)) {
                    valuationTradeAssetDtos.putAll(managedFundAssetBuilder.buildTradeAssets(subAccount, filteredAssets,
                            filteredAvailableAssets, assetDistributionMethods, bankDate));
                } else if (AssetType.SHARE.equals(subAssetType) || AssetType.OPTION.equals(subAssetType) ||
                        AssetType.BOND.equals(subAssetType)) {
                    valuationTradeAssetDtos.putAll(shareAssetBuilder.buildTradeAssets(subAccount, filteredAssets,
                            filteredAvailableAssets, assetDistributionMethods, bankDate));
                }
            }
        }

        List<TradeAssetDto> tradeAssetDtos = buildTradeAssetsFromAvailableAssets(filteredAvailableAssets, assetTypeHeld,
                assetDistributionMethods, bankDate);
        tradeAssetDtos.addAll(valuationTradeAssetDtos.values());
        return sortProductDtosOnAssetNames(tradeAssetDtos);
    }

    protected List<TradeAssetDto> buildTradeAssetsFromAvailableAssets(Map<String, Asset> filteredAvailableAssets,
            boolean assetTypeHeld, Map<String, List<DistributionMethod>> assetDistributionMethods, DateTime bankDate) {
        List<TradeAssetDto> tradeAssetDtos = new ArrayList<>();
        if (!assetTypeHeld) {
            // Retrieve IPS including those that could be newly created.
            Map<IpsKey, InvestmentPolicyStatementInterface> ipsMap = getIpsPolicyMap(filteredAvailableAssets);

            for (Asset asset : filteredAvailableAssets.values()) {
                if (AssetType.MANAGED_PORTFOLIO.equals(asset.getAssetType())
                        || AssetType.TAILORED_PORTFOLIO.equals(asset.getAssetType())) {
                    tradeAssetDtos.add(managedPortfolioAssetBuilder.buildTradeAssetFromAvailableAsset(asset, ipsMap, bankDate));
                } else if (AssetType.MANAGED_FUND.equals(asset.getAssetType())) {
                    tradeAssetDtos.add(managedFundAssetBuilder.buildTradeAssetFromAvailabeAsset(asset, assetDistributionMethods,
                            bankDate));
                } else if (AssetType.SHARE.equals(asset.getAssetType()) || AssetType.OPTION.equals(asset.getAssetType()) ||
                        AssetType.BOND.equals(asset.getAssetType())) {
                    tradeAssetDtos.add(shareAssetBuilder.buildTradeAssetFromAvailabeAsset(asset, assetDistributionMethods,
                            bankDate));
                } else if (AssetType.INDEX.equals(asset.getAssetType())) {
                    tradeAssetDtos.add(buildIndexAsset(asset));
                }
            }
        }

        return tradeAssetDtos;
    }

    private TradeAssetDto buildIndexAsset(Asset asset) {
        AssetDto assetDto = new AssetDto(asset, asset.getAssetName(), AssetType.INDEX.name());
        return new TradeAssetDto(assetDto, false, false, null, null, null, AssetType.INDEX.getDisplayName());
    }

    private List<TradeAssetDto> sortProductDtosOnAssetNames(List<TradeAssetDto> tradeAssetDtos) {
        return Lambda.sort(tradeAssetDtos, Lambda.on(TradeAssetDto.class).getAsset(), new Comparator<AssetDto>() {
            @Override
            public int compare(AssetDto a1, AssetDto a2) {
                if (a2.getAssetName() == null) {
                    return 1;
                } else if (a1.getAssetName() == null) {
                    return -1;
                } else {
                    return a1.getAssetName().trim().compareToIgnoreCase(a2.getAssetName().trim());
                }
            }
        });
    }

    protected List<TradeAssetTypeDto> toTradeAssetTypeDtos(List<TradeAssetDto> tradeAssetDtos,
            Map<String, Asset> availableAssets) {
        boolean shareAssets = false;
        boolean managedFundAssets = false;
        boolean wholesalePlusAssets = false;
        boolean managedPortfolioAssets = false;
        boolean tailoredPortfolioAssets = false;
        boolean termDepositAssets = false;

        for (TradeAssetDto tradeAssetDto : tradeAssetDtos) {
            String assetType = tradeAssetDto.getAsset().getAssetType();
            if (AssetType.MANAGED_FUND.getDisplayName().equalsIgnoreCase(assetType)) {
                managedFundAssets = true;
                if (tradeAssetDto.getAsset().getAssetName().toLowerCase()
                        .contains(TradableAssetsDtoServiceHelper.WHOLESALE_PLUS_SEARCH)) {
                    wholesalePlusAssets = true;
                }
            } else if (AssetType.MANAGED_PORTFOLIO.getDisplayName().equalsIgnoreCase(assetType)) {
                managedPortfolioAssets = true;
            } else if (AssetType.SHARE.getDisplayName().equalsIgnoreCase(assetType)) {
                shareAssets = true;
            } else if (AssetType.TAILORED_PORTFOLIO.getDisplayName().equalsIgnoreCase(assetType)) {
                tailoredPortfolioAssets = true;
            }
        }

        // check for term deposits
        for (Asset asset : availableAssets.values()) {
            if (AssetType.TERM_DEPOSIT.equals(asset.getAssetType())) {
                termDepositAssets = true;
                break;
            }
        }

        return sortTradeAssetTypeDtos(shareAssets, managedFundAssets, wholesalePlusAssets, managedPortfolioAssets,
                tailoredPortfolioAssets, termDepositAssets);
    }

    /**
     * Sort asset types according to display order
     *
     * @param shareAssets
     * @param managedFundAssets
     * @param managedPortfolioAssets
     * @return
     *
     */
    private List<TradeAssetTypeDto> sortTradeAssetTypeDtos(boolean shareAssets, boolean managedFundAssets,
            boolean wholesalePlusAssets, boolean managedPortfolioAssets, boolean tailoredPortfolioAssets,
            boolean termDepositAssets) {
        // Asset types order should be shares, managed fund, wholesale plus, managed portfolio, term deposits
        List<TradeAssetTypeDto> tradeassetTypeDtos = new ArrayList<TradeAssetTypeDto>();
        if (shareAssets) {
            tradeassetTypeDtos
                    .add(new TradeAssetTypeDto(AssetType.SHARE.getDisplayName(), AssetType.SHARE.getGroupDescription()));
        }
        if (managedFundAssets) {
            tradeassetTypeDtos.add(new TradeAssetTypeDto(AssetType.MANAGED_FUND.getDisplayName(), AssetType.MANAGED_FUND
                    .getGroupDescription()));
        }
        if (wholesalePlusAssets) {
            tradeassetTypeDtos.add(new TradeAssetTypeDto(TradableAssetsDtoServiceHelper.WHOLESALE_PLUS,
                    TradableAssetsDtoServiceHelper.WHOLESALE_PLUS_DESC));
        }
        if (managedPortfolioAssets) {
            tradeassetTypeDtos.add(new TradeAssetTypeDto(AssetType.MANAGED_PORTFOLIO.getDisplayName(),
                    AssetType.MANAGED_PORTFOLIO.getGroupDescription()));
        }
        if (tailoredPortfolioAssets) {
            tradeassetTypeDtos.add(new TradeAssetTypeDto(AssetType.TAILORED_PORTFOLIO.getDisplayName(),
                    AssetType.TAILORED_PORTFOLIO.getGroupDescription()));
        }
        if (termDepositAssets) {
            tradeassetTypeDtos.add(
                    new TradeAssetTypeDto(AssetType.TERM_DEPOSIT.getDisplayName(), AssetType.TERM_DEPOSIT.getGroupDescription()));
        }

        return tradeassetTypeDtos;
    }

    /**
     * Using the specified assets as keys, retrieve the corresponding
     * investmentPolicyStatement for each Managed-Portfolio and
     * Tailored-Portfolio assets.
     *
     * @param filteredAvailableAssets
     * @return Map of investmentPolicyStatement with assets' ipsId as keys.
     */
    private Map<IpsKey, InvestmentPolicyStatementInterface> getIpsPolicyMap(Map<String, Asset> filteredAvailableAssets) {
        List<IpsKey> ipsKeyList = new ArrayList<>();
        for (Asset asset : filteredAvailableAssets.values()) {
            if (AssetType.MANAGED_PORTFOLIO.equals(asset.getAssetType())
                    || AssetType.TAILORED_PORTFOLIO.equals(asset.getAssetType())) {
                ipsKeyList.add(IpsKey.valueOf(asset.getIpsId()));
            }
        }

        // Retrieve IPS including those newly created.
        return ipsIntegrationService.getInvestmentPolicyStatements(ipsKeyList, serviceErrors);
    }
}
