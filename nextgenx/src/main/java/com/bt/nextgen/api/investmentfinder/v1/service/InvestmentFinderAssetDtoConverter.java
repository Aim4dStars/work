package com.bt.nextgen.api.investmentfinder.v1.service;

import com.bt.nextgen.api.investmentfinder.v1.model.InvestmentFinderAssetDto;
import com.btfin.panorama.service.integration.investmentfinder.model.InvestmentFinderAsset;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class InvestmentFinderAssetDtoConverter {

    private InvestmentFinderAssetDtoConverter() {
    }

    public static List<InvestmentFinderAssetDto> toInvestmentFinderAssetDto(List<InvestmentFinderAsset> investmentFinderAssets) {
        List<InvestmentFinderAssetDto> investmentFinderAssetDtos = new ArrayList<>();
        for (InvestmentFinderAsset investmentFinderAsset : investmentFinderAssets) {
            InvestmentFinderAssetDto investmentFinderAssetDto = new InvestmentFinderAssetDto();
            BeanUtils.copyProperties(investmentFinderAsset, investmentFinderAssetDto);
            investmentFinderAssetDtos.add(investmentFinderAssetDto);
        }
        return investmentFinderAssetDtos;
    }
}
