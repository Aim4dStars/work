package com.bt.nextgen.api.trading.v1.service.assetbuilder;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.asset.model.InterestRateDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDtoV2;
import com.bt.nextgen.api.trading.v1.model.TermDepositTradeAssetDto;
import com.bt.nextgen.api.trading.v1.model.TermDepositTradeAssetDtoV2;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.service.avaloq.asset.TermDepositInterestRateUtil;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.bt.nextgen.termdeposit.service.AssetIssuerKey;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.TermDepositAsset;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail.InterestRate;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

@Component
public class TermDepositAssetBuilderV2 {
    private static final Logger logger = LoggerFactory.getLogger(TermDepositAssetBuilder.class);

    public List<TradeAssetDto> buildTradeAssets(List<TermDepositInterestRate> termDepositInterestRates,
                                                Map<String, Asset> filteredAvailableAssets) {
        List<TradeAssetDto> tdAssetDtos = new ArrayList<>();
        Map<AssetIssuerKey,SortedSet<TermDepositInterestRate>> termDepositInterestRateMap = new HashMap<AssetIssuerKey,SortedSet<TermDepositInterestRate>>();
        TermDepositInterestRateUtil.buildTermDepositInterestRateConfig(termDepositInterestRateMap,termDepositInterestRates);

        if(!termDepositInterestRateMap.isEmpty()){
            Iterator<SortedSet<TermDepositInterestRate>> setIterator = termDepositInterestRateMap.values().iterator();
            while(setIterator.hasNext()){

                SortedSet<TermDepositInterestRate> termDepositInterestRateList = setIterator.next();

                   if(CollectionUtils.isNotEmpty(termDepositInterestRateList) && filteredAvailableAssets.get(termDepositInterestRateList.first().getAssetKey().getId()) != null){
                        TermDepositAsset tdAsset = (TermDepositAsset) filteredAvailableAssets.get(termDepositInterestRateList.first().getAssetKey().getId());

                        TermDepositAssetDtoV2 tdAssetDto = toTermDepositAsset(tdAsset, termDepositInterestRateList);
                        TradeAssetDto tradeAssetDto = new TermDepositTradeAssetDtoV2(tdAssetDto, true, false);
                        tdAssetDtos.add(tradeAssetDto);
                    }

            }
        }

        return tdAssetDtos;
    }

    private TermDepositAssetDtoV2 toTermDepositAsset(TermDepositAsset asset, SortedSet<TermDepositInterestRate> tdAssetDetailList) {
            List<InterestRateDto> interestBands = new ArrayList<>();
            interestBands = Lambda.convert(tdAssetDetailList,new Converter<TermDepositInterestRate, InterestRateDto>() {
                @Override
                public InterestRateDto convert(TermDepositInterestRate termDepositInterestRate) {
                    return new InterestRateDto(termDepositInterestRate.getRateAsPercentage(), termDepositInterestRate.getLowerLimit(),
                            termDepositInterestRate.getUpperLimit());
                }
            });
            return new TermDepositAssetDtoV2(asset, tdAssetDetailList.first(), interestBands);
        }
    }
