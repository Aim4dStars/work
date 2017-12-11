package com.bt.nextgen.api.trading.v1.service.assetbuilder;

import com.bt.nextgen.api.asset.model.InterestRateDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDto;
import com.bt.nextgen.api.trading.v1.model.TermDepositTradeAssetDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.service.integration.asset.Asset;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.TermDepositAsset;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail.InterestRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class TermDepositAssetBuilder {
    private static final Logger logger = LoggerFactory.getLogger(TermDepositAssetBuilder.class);

    public List<TradeAssetDto> buildTradeAssets(Map<String, TermDepositAssetDetail> tdAssetDetails,
            Map<String, Asset> filteredAvailableAssets) {
        List<TradeAssetDto> tdAssetDtos = new ArrayList<>();

        if (tdAssetDetails != null && !tdAssetDetails.isEmpty() && filteredAvailableAssets != null
                && !filteredAvailableAssets.isEmpty()) {
            for (TermDepositAssetDetail tdAssetDetail : tdAssetDetails.values()) {
                if (filteredAvailableAssets.containsKey(tdAssetDetail.getAssetId())) {
                    TermDepositAsset tdAsset = (TermDepositAsset) filteredAvailableAssets.get(tdAssetDetail.getAssetId());
                    if (AssetType.TERM_DEPOSIT.equals(tdAsset.getAssetType())) {
                        TermDepositAssetDto tdAssetDto = toTermDepositAsset(tdAsset, tdAssetDetail);
                        TradeAssetDto tradeAssetDto = new TermDepositTradeAssetDto(tdAssetDto, true, false);
                        tdAssetDtos.add(tradeAssetDto);
                    }
                }
            }
        }
        return tdAssetDtos;
    }

    private TermDepositAssetDto toTermDepositAsset(TermDepositAsset asset, TermDepositAssetDetail tdAssetDetail) {
        if (tdAssetDetail != null) {
            List<InterestRateDto> interestBands = new ArrayList<>();
            for (InterestRate interestRate : tdAssetDetail.getInterestRates()) {
                interestBands.add(new InterestRateDto(interestRate.getRateAsPercentage(), interestRate.getLowerLimit(),
                        interestRate.getUpperLimit()));
            }

            return new TermDepositAssetDto(asset, tdAssetDetail, interestBands);
        } else {
            logger.warn("Filtering term deposit asset {} as there is no matching rates", asset.getAssetId());
            return null;
        }
    }
}
