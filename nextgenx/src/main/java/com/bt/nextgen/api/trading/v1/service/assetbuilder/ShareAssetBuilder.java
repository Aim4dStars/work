package com.bt.nextgen.api.trading.v1.service.assetbuilder;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.ShareAssetDto;
import com.bt.nextgen.api.trading.v1.model.ShareTradeAssetDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceFilter;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceHelper;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ShareAccountValuationImpl;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.asset.Asset;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.ShareAsset;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.ShareHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ShareAssetBuilder {
    @Autowired
    private TradableAssetsDtoServiceHelper tradableAssetsDtoServiceHelper;

    @Autowired
    private TradableAssetsDtoServiceFilter tradableAssetsDtoServiceFilter;

    public Map<String, TradeAssetDto> buildTradeAssets(SubAccountValuation subAccount, Map<String, Asset> filteredAssets,
            Map<String, Asset> availableAssets, Map<String, List<DistributionMethod>> assetDistributionMethods,
            DateTime bankDate) {
        Map<String, TradeAssetDto> shareTradeAssets = new HashMap<>();
        ShareAccountValuationImpl listedSecurityAccountValuation = (ShareAccountValuationImpl) subAccount;

        for (AccountHolding shareHolding : listedSecurityAccountValuation.getHoldings()) {
            if (filteredAssets.containsKey(shareHolding.getAsset().getAssetId())) {
                if (shareTradeAssets.containsKey(shareHolding.getAsset().getAssetId())) {
                    ShareTradeAssetDto tradeAsset = (ShareTradeAssetDto) shareTradeAssets
                            .get(shareHolding.getAsset().getAssetId());

                    BigDecimal balance = tradableAssetsDtoServiceHelper.getCombinedAmount(tradeAsset.getBalance(),
                            shareHolding.getBalance());
                    BigDecimal availableBalance = tradableAssetsDtoServiceHelper
                            .getCombinedAmount(tradeAsset.getAvailableBalance(), shareHolding.getAvailableBalance());
                    BigDecimal availableQuantity = tradableAssetsDtoServiceHelper
                            .getCombinedAmount(tradeAsset.getAvailableQuantity(), shareHolding.getAvailableUnits());
                    String dividendMethod = !StringUtils.isEmpty(tradeAsset.getDividendMethod()) ? tradeAsset.getDividendMethod()
                            : ((ShareHolding) shareHolding).getDistributionMethod() == null ? null
                                    : ((ShareHolding) shareHolding).getDistributionMethod().getDisplayName();
                    boolean sellable = tradableAssetsDtoServiceFilter.isAssetSellable(shareHolding.getAsset(), availableQuantity,
                            bankDate);

                    TradeAssetDto combinedTradeAsset = new ShareTradeAssetDto(tradeAsset.getAsset(), tradeAsset.getBuyable(),
                            sellable, balance, availableBalance, availableQuantity, dividendMethod);
                    shareTradeAssets.put(combinedTradeAsset.getAsset().getAssetId(), combinedTradeAsset);
                } else {
                    AssetDto assetDto = new ShareAssetDto((ShareAsset) shareHolding.getAsset(),
                            assetDistributionMethods.get(shareHolding.getAsset().getAssetId()));

                    boolean buyable = false;
                    if (availableAssets.containsKey(shareHolding.getAsset().getAssetId())) {
                        buyable = tradableAssetsDtoServiceFilter.isAssetBuyable(shareHolding.getAsset(), true, bankDate);
                        availableAssets.remove(shareHolding.getAsset().getAssetId());
                    }

                    boolean sellable = tradableAssetsDtoServiceFilter.isAssetSellable(shareHolding.getAsset(),
                            shareHolding.getAvailableUnits(), bankDate);
                    TradeAssetDto shareTradeAsset = new ShareTradeAssetDto(assetDto, buyable, sellable, shareHolding);
                    shareTradeAssets.put(assetDto.getAssetId(), shareTradeAsset);
                }
            }
        }

        return shareTradeAssets;
    }

    public TradeAssetDto buildTradeAssetFromAvailabeAsset(Asset asset,
            Map<String, List<DistributionMethod>> assetDistributionMethods, DateTime bankDate) {
        AssetDto assetDto = new ShareAssetDto((ShareAsset) asset, assetDistributionMethods.get(asset.getAssetId()));
        boolean buyable = tradableAssetsDtoServiceFilter.isAssetBuyable(asset, false, bankDate);
        TradeAssetDto tradeListedSecurityAssetDto = new TradeAssetDto(assetDto, buyable, false, null, null, null,
                AssetType.SHARE.getDisplayName());
        return tradeListedSecurityAssetDto;
    }
}
