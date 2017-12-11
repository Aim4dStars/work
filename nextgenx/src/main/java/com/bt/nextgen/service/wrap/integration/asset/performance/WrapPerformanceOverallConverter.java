package com.bt.nextgen.service.wrap.integration.asset.performance;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.AssetPerformanceImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.avaloq.portfolio.performance.PortfolioPerformanceOverallImpl;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.asset.*;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceOverall;
import com.btfin.panorama.core.mapping.AbstractMappingConverter;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.wrap.model.AssetPerformance;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
@Profile({"WrapOffThreadImplementation"})
public class WrapPerformanceOverallConverter extends AbstractMappingConverter {
    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService avaloqAssetIntegrationService;

    private static final String LISTED_SECURITY = "Listed security";
    private static final String MANAGED_FUND = "Managed fund";
    private static final String TERM_DEPOSIT = "Term deposit";
    private static final String CASH = "Cash";

    public AccountPerformanceOverall toModel(List<AssetPerformance> assetPerformanceList, AccountStructureType accountType, ServiceErrors serviceErrors) {
        PortfolioPerformanceOverallImpl performance = null;
        if (!assetPerformanceList.isEmpty()) {
            Map<String, Asset> assetMap = new HashMap<>();
            List<String> codes = getAssetCodes(assetPerformanceList);
            if (!codes.isEmpty()) {
                for (Asset asset : avaloqAssetIntegrationService.loadAssetsForAssetCodes(codes, serviceErrors)) {
                    assetMap.put(asset.getAssetCode(), asset);
                }
            }
            performance = new PortfolioPerformanceOverallImpl();
            List<com.bt.nextgen.service.integration.asset.AssetPerformance> investmentPerformance = new ArrayList<>();
            for (AssetPerformance assetPerformance : assetPerformanceList) {
                investmentPerformance.add(getAssetPerformance(assetPerformance, assetMap, accountType));
             }
            performance.setInvestmentPerformances(investmentPerformance);
        }
        return performance;
    }

    private AssetPerformanceImpl getAssetPerformance(AssetPerformance assetPerformance, Map<String, Asset> assetMap,
                                                     AccountStructureType accountType) {
        AssetPerformanceImpl asPerf = new AssetPerformanceImpl();
        Asset as = assetMap.get(assetPerformance.getSecurityCode());
        if (as == null) {
            as = buildAssetForCode(assetPerformance);
        }
        asPerf.setAsset(as);
        asPerf.setAssetType(getAssetType(assetPerformance.getAssetCluster()));
        if (asPerf.getAssetType().compareTo(AssetType.CASH) == 0) {
            asPerf.setName(accountType.compareTo(AccountStructureType.SUPER) == 0 ? "Cash Account" : AssetType.CASH.getDisplayName());
            asPerf.setCode(StringUtil.EMPTY_STRING);
        } else {
            asPerf.setCode(as.getAssetCode());
            asPerf.setName(as.getAssetName());
        }
        asPerf.setOpeningBalance(assetPerformance.getOpeningBalance());
        asPerf.setPurchases(assetPerformance.getPurchases());
        asPerf.setSales(assetPerformance.getSales());
        asPerf.setMarketMovement(assetPerformance.getMarketMovement());
        asPerf.setClosingBalance(assetPerformance.getClosingBalance());
        asPerf.setIncome(assetPerformance.getIncome());
        asPerf.setPerformanceDollar(assetPerformance.getPerformanceAmount());
        asPerf.setPerformancePercent(BigDecimal.ZERO);
        asPerf.setCapitalReturn(BigDecimal.ZERO);
        asPerf.setIncomeReturn(BigDecimal.ZERO);
        asPerf.setPeriodOfDays(0);
        asPerf.setContainerType(ContainerType.DIRECT);
        return asPerf;
    }
    private Asset buildAssetForCode(AssetPerformance perf) {
        if (perf.getAssetCluster().equals(AssetType.TERM_DEPOSIT.getDisplayName())) {
            return populateTermDepositAsset(perf);
         } else {
            AssetImpl asset = new AssetImpl();
            asset.setAssetCode(perf.getSecurityCode());
            asset.setAssetName(perf.getSecurityName());
            asset.setAssetType(AssetType.forDisplay(perf.getAssetCluster()));
            return asset;
        }
    }
    private List<String> getAssetCodes(List<AssetPerformance> asPerfList){
         List<String> codes = new ArrayList<>();
         for (AssetPerformance asPerf : asPerfList) {
             codes.add(asPerf.getSecurityCode());
         }
         return codes;
    }
    private AssetType getAssetType(String assetCluster) {
        switch (assetCluster) {
            case LISTED_SECURITY:
                 return AssetType.SHARE;
            case MANAGED_FUND:
                return AssetType.MANAGED_FUND;
            case TERM_DEPOSIT:
                return AssetType.TERM_DEPOSIT;
            case CASH:
                return AssetType.CASH;
            default:
                return AssetType.UNCATEGORISED;
        }
    }
    private TermDepositAsset populateTermDepositAsset(AssetPerformance perf) {
        TermDepositAssetImpl tdAsset = new TermDepositAssetImpl();
        tdAsset.setAssetCode(perf.getSecurityCode());
        tdAsset.setAssetName(perf.getSecurityName());
        tdAsset.setAssetType(AssetType.forDisplay(perf.getAssetCluster()));
        return tdAsset;
    }
 }
