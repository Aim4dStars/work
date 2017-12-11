package com.bt.nextgen.service.wrap.integration.asset.performance;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.AvaloqAssetIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.TermDepositAsset;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceOverall;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.wrap.model.AssetPerformance;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WrapPerformanceOverallConverterTest {
    @InjectMocks
    private WrapPerformanceOverallConverter converter = new WrapPerformanceOverallConverter();
    @Mock
    private AvaloqAssetIntegrationServiceImpl avaloqAssetIntegrationService;

    private List<AssetPerformance> assetPerformanceList = new ArrayList<>();
    private List<Asset> assets = new ArrayList<>();
    private ServiceErrors serviceErrors = new ServiceErrorsImpl();
    private AccountStructureType accountType;

    @Before
    public void init() throws Exception {
        AssetImpl asset1 = new AssetImpl();
        asset1.setAssetId("11111");
        asset1.setAssetCode("BHP");
        AssetImpl asset2 = new AssetImpl();
        asset2.setAssetId("22222");
        asset2.setAssetCode("CBA");
        AssetImpl asset3 = new AssetImpl();
        asset3.setAssetId("33333");
        asset3.setAssetCode("CWN");
        AssetImpl asset4 = new AssetImpl();
        asset4.setAssetCode("Cash");
        AssetImpl asset5 = new TermDepositAssetImpl();
        asset5.setAssetId(null);
        asset4.setAssetName("Term deposit");
        assets.add(asset1);
        assets.add(asset2);
        assets.add(asset3);
        assets.add(asset4);
        assets.add(asset5);
        accountType = AccountStructureType.SMSF;
        when(avaloqAssetIntegrationService.loadAssetsForAssetCodes(anyCollectionOf(String.class), any(ServiceErrors.class))).thenReturn(assets);

        assetPerformanceList.add(createAssetPerformance("BHP", "BHP Billiton Limited",
                "Listed security", BigDecimal.valueOf(562457.16), BigDecimal.valueOf(562457.16), BigDecimal.valueOf(562457.16), BigDecimal.valueOf(1124914.32)));
        assetPerformanceList.add(createAssetPerformance("CBA", "Commonwealth Bank",
                "Managed fund", BigDecimal.TEN, BigDecimal.valueOf(562457.16), BigDecimal.valueOf(562457.16), BigDecimal.valueOf(1124914.32)));
       assetPerformanceList.add(createAssetPerformance("WRAPWCA", "Cash",
                "Cash", BigDecimal.TEN, BigDecimal.valueOf(562457.16), BigDecimal.valueOf(562457.16), BigDecimal.valueOf(1124914.32)));
        assetPerformanceList.add(createAssetPerformance("WBC1560TD", "Westpac Term Dpst 94d 20-FEB-14 3.45%",
                "Term deposit", BigDecimal.TEN, BigDecimal.valueOf(562457.16), BigDecimal.valueOf(562457.16), BigDecimal.valueOf(1124914.32)));
    }
    @Test
    public void toModelAccountPerformanceOverall_whenSuppliedWithValidResponse_thenObjectCreatedAndNoServiceErrors()
            throws Exception {

        AccountPerformanceOverall perf = converter.toModel(assetPerformanceList, accountType, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNotNull(perf);
        Assert.assertFalse(perf.getInvestmentPerformances().isEmpty());

        com.bt.nextgen.service.integration.asset.AssetPerformance assetPerf = perf.getInvestmentPerformances().get(0);
        Assert.assertEquals(BigDecimal.valueOf(562457.16), assetPerf.getOpeningBalance());
        Assert.assertEquals(BigDecimal.valueOf(1124914.32), assetPerf.getPerformanceDollar());
        Assert.assertEquals(BigDecimal.valueOf(0), assetPerf.getPerformancePercent());
        Assert.assertEquals(Integer.valueOf(0), assetPerf.getPeriodOfDays());

        com.bt.nextgen.service.integration.asset.AssetPerformance tdAssetPerf = perf.getInvestmentPerformances().get(3);
        Assert.assertEquals("Westpac Term Dpst 94d 20-FEB-14 3.45%", tdAssetPerf.getName());
    }
    @Test
    public void toModelAccountPerformanceOverallEmpty() throws Exception {
        assetPerformanceList = new ArrayList<>();
        AccountPerformanceOverall perf = converter.toModel(assetPerformanceList, accountType, serviceErrors);
        Assert.assertNull(perf);
    }
    private AssetPerformance createAssetPerformance(String securityCode, String securityName, String assetCluster,
                                                    BigDecimal openingBalance, BigDecimal marketMovement, BigDecimal income, BigDecimal performanceAmount) {
        AssetPerformance assestPerformance = new AssetPerformance();
        assestPerformance.setSecurityCode(securityCode);
        assestPerformance.setSecurityName(securityName);
        assestPerformance.setAssetCluster(assetCluster);
        assestPerformance.setOpeningBalance(openingBalance);
        assestPerformance.setMarketMovement(marketMovement);
        assestPerformance.setIncome(income);
        assestPerformance.setPerformanceAmount(performanceAmount);
        return assestPerformance;
    }
}
