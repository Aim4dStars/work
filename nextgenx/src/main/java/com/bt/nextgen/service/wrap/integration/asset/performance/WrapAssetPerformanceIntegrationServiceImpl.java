package com.bt.nextgen.service.wrap.integration.asset.performance;

import com.bt.nextgen.service.avaloq.asset.AssetPerformanceImpl;
import com.bt.nextgen.service.avaloq.portfolio.performance.PortfolioPerformanceOverallImpl;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.asset.AssetPerformance;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceOverall;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.wrap.service.AssetPerformanceService;
import io.netty.util.internal.StringUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@Profile({"WrapOffThreadImplementation"})
public class WrapAssetPerformanceIntegrationServiceImpl implements AssetPerformanceIntegrationService {

    @Autowired
    @Qualifier("AssetPerformanceServiceRestClient")
    private AssetPerformanceService assetPerformanceService;

    @Autowired
    private WrapPerformanceOverallConverter wrapPerformanceOverallConverter;

    private static final String CASH_ACCOUNT = "Cash Account";
    private static final String PENDING = "Pending";

    public AccountPerformanceOverall loadAccountOverallPerformance(WrapAccountDetail accountDetail, final DateTime startDate,
                                                                   final DateTime endDate) {
        List<com.btfin.panorama.wrap.model.AssetPerformance> wrapAssetPerformanceList =
                assetPerformanceService.getAssetPerformanceForClient(accountDetail.getMigrationKey(), startDate, endDate, new FailFastErrorsImpl());
        // Map Wrap records to Panorama records
        return wrapPerformanceOverallConverter.toModel(wrapAssetPerformanceList, accountDetail.getAccountStructureType(), new FailFastErrorsImpl());
    }

    public AccountPerformanceOverall combineAssetPerformance(AccountPerformanceOverall avaloqAssetPerformance,
                                                             AccountPerformanceOverall wrapAssetPerformance,
                                                             WrapAccountDetail accountDetail) {

        Map<String, AssetPerformance> assetPerfMap = new HashMap<>();
        for (AssetPerformance asPerf : avaloqAssetPerformance.getInvestmentPerformances()) {

            if (!PENDING.equals(asPerf.getName()) && asPerf.getAssetType().compareTo(AssetType.CASH) == 0) {
                AssetPerformanceImpl cashAsPerf = (AssetPerformanceImpl) asPerf;
                cashAsPerf.setName(getCashAssetName(accountDetail.getAccountStructureType()));
                assetPerfMap.put(AssetType.CASH.getDisplayName(), cashAsPerf);
            } else {
                assetPerfMap.put(asPerf.getCode(), asPerf);
            }
        }
        String assetCode;
        AssetPerformance comAssPerf;
        for (AssetPerformance wrapAssPerf : wrapAssetPerformance.getInvestmentPerformances()) {
            assetCode = wrapAssPerf.getAssetType().compareTo(AssetType.CASH) == 0 ? AssetType.CASH.getDisplayName() : wrapAssPerf.getCode();
            if (assetPerfMap.containsKey(assetCode)) {
                comAssPerf = consolidateAsset(assetPerfMap.get(assetCode), wrapAssPerf, accountDetail.getAccountStructureType());
                assetPerfMap.put(assetCode, comAssPerf);
            } else {
                assetPerfMap.put(assetCode, wrapAssPerf);
            }
        }
        PortfolioPerformanceOverallImpl combinedAssetPerformance = new PortfolioPerformanceOverallImpl();
        List<AssetPerformance> investmentPerformance = new ArrayList<>();
        investmentPerformance.addAll(assetPerfMap.values());
        combinedAssetPerformance.setInvestmentPerformances(investmentPerformance);

        return combinedAssetPerformance;
    }

    private AssetPerformance consolidateAsset(AssetPerformance avaloqAssPerf, AssetPerformance wrapAssPerf, AccountStructureType accountType) {
        AssetPerformanceImpl perf = new AssetPerformanceImpl();
        perf.setAsset(avaloqAssPerf.getAsset());
        perf.setAssetType(avaloqAssPerf.getAssetType());

        if (perf.getAssetType().compareTo(AssetType.CASH) == 0) {
            perf.setName(getCashAssetName(accountType));
            perf.setCode(StringUtil.EMPTY_STRING);
        } else {
            perf.setName(avaloqAssPerf.getName());
            perf.setCode(avaloqAssPerf.getCode());
        }
        perf.setContainerType(avaloqAssPerf.getContainerType());
        perf.setOpeningBalance(wrapAssPerf.getOpeningBalance());
        perf.setClosingBalance(avaloqAssPerf.getClosingBalance());
        perf.setSales(avaloqAssPerf.getSales().add(wrapAssPerf.getSales()));
        perf.setPurchases(avaloqAssPerf.getPurchases().add(wrapAssPerf.getPurchases()));
        perf.setMarketMovement(avaloqAssPerf.getMarketMovement().add(wrapAssPerf.getMarketMovement()));
        perf.setIncome(avaloqAssPerf.getIncome().add(wrapAssPerf.getIncome()));
        perf.setPerformanceDollar(avaloqAssPerf.getPerformanceDollar().add(wrapAssPerf.getPerformanceDollar()));
        perf.setPerformancePercent(BigDecimal.ZERO);
        perf.setCapitalReturn(BigDecimal.ZERO);
        perf.setIncomeReturn(BigDecimal.ZERO);
        perf.setPeriodOfDays(0);

        return perf;
    }

    private String getCashAssetName(AccountStructureType accountType) {
        return AccountStructureType.SUPER.equals(accountType) ? CASH_ACCOUNT : AssetType.CASH.getDisplayName();
    }
}
