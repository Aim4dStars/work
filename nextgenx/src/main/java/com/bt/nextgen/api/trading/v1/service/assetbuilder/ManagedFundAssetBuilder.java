package com.bt.nextgen.api.trading.v1.service.assetbuilder;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.ManagedFundAssetDto;
import com.bt.nextgen.api.trading.v1.model.ManagedFundTradeAssetDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceFilter;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceHelper;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedFundAccountValuationImpl;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.asset.Asset;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.ManagedFundAsset;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedFundHolding;
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
public class ManagedFundAssetBuilder {
    @Autowired
    private TradableAssetsDtoServiceHelper tradableAssetsDtoServiceHelper;

    @Autowired
    private TradableAssetsDtoServiceFilter tradableAssetsDtoServiceFilter;

    public Map<String, TradeAssetDto> buildTradeAssets(SubAccountValuation subAccount, Map<String, Asset> filteredAssets,
            Map<String, Asset> availableAssets, Map<String, List<DistributionMethod>> assetDistributionMethods, DateTime bankDate) {
        Map<String, TradeAssetDto> mfTradeAssets = new HashMap<>();

        ManagedFundAccountValuationImpl mfAccount = (ManagedFundAccountValuationImpl) subAccount;
        for (AccountHolding mfHolding : mfAccount.getHoldings()) {
            if (filteredAssets.containsKey(mfHolding.getAsset().getAssetId())) {
                if (mfTradeAssets.containsKey(mfHolding.getAsset().getAssetId())) {
                    ManagedFundTradeAssetDto tradeAsset = (ManagedFundTradeAssetDto) mfTradeAssets.get(mfHolding.getAsset()
                            .getAssetId());

                    BigDecimal balance = tradableAssetsDtoServiceHelper.getCombinedAmount(tradeAsset.getBalance(),
                            mfHolding.getMarketValue());
                    BigDecimal availableBalance = tradableAssetsDtoServiceHelper.getCombinedAmount(
                            tradeAsset.getAvailableBalance(), mfHolding.getAvailableBalance());
                    BigDecimal availableQuantity = tradableAssetsDtoServiceHelper.getCombinedAmount(
                            tradeAsset.getAvailableQuantity(), mfHolding.getAvailableUnits());
                    String dividendMethod = !StringUtils.isEmpty(tradeAsset.getDistributionMethod()) ? tradeAsset
                            .getDistributionMethod() : ((ManagedFundHolding) mfHolding).getDistributionMethod() == null ? null
                            : ((ManagedFundHolding) mfHolding).getDistributionMethod().getDisplayName();
                    boolean sellable = tradableAssetsDtoServiceFilter.isAssetSellable(mfHolding.getAsset(), availableBalance,
                            bankDate);

                    TradeAssetDto combinedTradeAsset = new ManagedFundTradeAssetDto(tradeAsset.getAsset(),
                            tradeAsset.getBuyable(), sellable, balance, availableBalance, availableQuantity, dividendMethod);
                    mfTradeAssets.put(combinedTradeAsset.getAsset().getAssetId(), combinedTradeAsset);
                } else {
                    AssetDto assetDto = new ManagedFundAssetDto((ManagedFundAsset) mfHolding.getAsset(),
                            assetDistributionMethods.get(mfHolding.getAsset().getAssetId()));

                    boolean buyable = false;
                    if (availableAssets.containsKey(mfHolding.getAsset().getAssetId())) {
                        buyable = tradableAssetsDtoServiceFilter.isAssetBuyable(mfHolding.getAsset(), true, bankDate);
                        availableAssets.remove(mfHolding.getAsset().getAssetId());
                    }

                    boolean sellable = tradableAssetsDtoServiceFilter.isAssetSellable(mfHolding.getAsset(),
                            mfHolding.getAvailableBalance(), bankDate);
                    TradeAssetDto mfTradeAsset = new ManagedFundTradeAssetDto(assetDto, buyable, sellable, mfHolding);
                    mfTradeAssets.put(assetDto.getAssetId(), mfTradeAsset);
                }
            }
        }

        return mfTradeAssets;
    }

    public TradeAssetDto buildTradeAssetFromAvailabeAsset(Asset asset,
            Map<String, List<DistributionMethod>> assetDistributionMethods, DateTime bankDate) {
        AssetDto assetDto = new ManagedFundAssetDto((ManagedFundAsset) asset, assetDistributionMethods.get(asset.getAssetId()));
        boolean buyable = tradableAssetsDtoServiceFilter.isAssetBuyable(asset, false, bankDate);
        TradeAssetDto tradeManagedFundAssetDto = new TradeAssetDto(assetDto, buyable, false, null, null, null,
                AssetType.MANAGED_FUND.getDisplayName());
        return tradeManagedFundAssetDto;
    }
}
