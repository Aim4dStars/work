package com.bt.nextgen.api.trading.v1.service.assetbuilder;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.api.trading.v1.model.ManagedPortfolioTradeAssetDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceFilter;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ManagedPortfolioAssetBuilder {
    @Autowired
    private TradableAssetsDtoServiceFilter tradableAssetsDtoServiceFilter;

    public Map<String, TradeAssetDto> buildTradeAssets(SubAccountValuation subAccount, Map<String, Asset> filteredAssets,
            Map<String, Asset> availableAssets, DateTime bankDate) {
        Map<String, TradeAssetDto> tradeManagedPortfolioAssets = new HashMap<>();
        ManagedPortfolioAccountValuation mpAccount = (ManagedPortfolioAccountValuation) subAccount;
        if (filteredAssets.containsKey(mpAccount.getAsset().getAssetId())) {
            AssetDto assetDto = new ManagedPortfolioAssetDto(mpAccount.getAsset());
            boolean buyable = false;
            if (availableAssets.containsKey(mpAccount.getAsset().getAssetId())) {
                buyable = tradableAssetsDtoServiceFilter.isAssetBuyable(mpAccount.getAsset(), true, bankDate);
                availableAssets.remove(mpAccount.getAsset().getAssetId());
            }

            boolean sellable = tradableAssetsDtoServiceFilter.isAssetSellable(mpAccount.getAsset(),
                    mpAccount.getAvailableBalance(), bankDate);
            TradeAssetDto tradeManagedPortfolioAssetDto = new ManagedPortfolioTradeAssetDto(assetDto, buyable, sellable,
                    mpAccount);
            tradeManagedPortfolioAssets.put(assetDto.getAssetId(), tradeManagedPortfolioAssetDto);
        }
        return tradeManagedPortfolioAssets;
    }

    /**
     * @param asset
     * @param bankDate
     * @return
     */
    public TradeAssetDto buildTradeAssetFromAvailableAsset(Asset asset, Map<IpsKey, InvestmentPolicyStatementInterface> ipsMap,
            DateTime bankDate) {
        ManagedPortfolioAssetDto managedPortfolioAssetDto = new ManagedPortfolioAssetDto(asset, Boolean.FALSE);

        if (asset.getIpsId() != null) {
            InvestmentPolicyStatementInterface ips = ipsMap.get(IpsKey.valueOf(asset.getIpsId()));
            if (ips != null)
                managedPortfolioAssetDto = new ManagedPortfolioAssetDto(asset,
                        ips.getTaxAssetDomicile() != null ? ips.getTaxAssetDomicile() : Boolean.FALSE);
        } else {
            managedPortfolioAssetDto = new ManagedPortfolioAssetDto(asset, Boolean.FALSE);
        }

        boolean buyable = tradableAssetsDtoServiceFilter.isAssetBuyable(asset, false, bankDate);
        return new TradeAssetDto(managedPortfolioAssetDto, buyable, false, null, null, null,
                managedPortfolioAssetDto.getAssetType());
    }
}
