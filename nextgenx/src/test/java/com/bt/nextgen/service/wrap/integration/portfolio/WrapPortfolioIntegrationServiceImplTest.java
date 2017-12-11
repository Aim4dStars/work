package com.bt.nextgen.service.wrap.integration.portfolio;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.insurance.service.PolicyIntegrationService;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedFundHoldingImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ShareHoldingImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.base.AvaloqAccountIntegrationService;
import com.bt.nextgen.service.integration.base.SystemType;
import com.bt.nextgen.service.integration.base.ThirdPartyDetails;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.wrap.model.PortfolioPosition;
import com.btfin.panorama.wrap.service.PortfolioService;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class WrapPortfolioIntegrationServiceImplTest {
    @InjectMocks
    WrapPortfolioIntegrationServiceImpl portfolioIntegrationService;

    @Mock
    private PortfolioService portfolioService;

    @Mock
    private PolicyIntegrationService policyIntegrationService;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private AvaloqAccountIntegrationService avaloqAccountIntegrationService;

    @Test
    public void testLoadWrapAccountValuation() {
        PortfolioPosition portfolioPosition1 = new PortfolioPosition();
        PortfolioPosition portfolioPosition2 = new PortfolioPosition();
        PortfolioPosition portfolioPosition3 = new PortfolioPosition();
        PortfolioPosition portfolioPosition4 = new PortfolioPosition();
        PortfolioPosition portfolioPosition5 = new PortfolioPosition();
        PortfolioPosition portfolioPosition6 = new PortfolioPosition();
        PortfolioPosition portfolioPosition7 = new PortfolioPosition();
        PortfolioPosition portfolioPosition8 = new PortfolioPosition();

        portfolioPosition1.setSecurityCode("Cash Account");
        portfolioPosition1.setSecurityName("Working Cash Account");
        portfolioPosition1.setValue("30948");

        portfolioPosition2.setSecurityCode("WBC2205TD");
        portfolioPosition2.setSecurityName("WBC Term Deposit 185d 05-MAY-16 2.50%");
        portfolioPosition2.setValue("50000");
        portfolioPosition2.setChange("0.00");
        portfolioPosition2.setCost("50000");

        portfolioPosition3.setSecurityCode("MFD3265RT");
        portfolioPosition3.setSecurityName("Managed Fund Valuation");
        portfolioPosition3.setValue("4000");
        portfolioPosition3.setSecurityClass("Unit Trust");

        portfolioPosition4.setSecurityCode("SHD3265RT");
        portfolioPosition4.setSecurityName("Share Valuation");
        portfolioPosition4.setValue("3000");
        portfolioPosition4.setSecurityClass("Equity");

        portfolioPosition5.setSecurityCode("SHD3265RT");
        portfolioPosition5.setSecurityName("00000000");
        portfolioPosition5.setValue("0.0");

        portfolioPosition6.setSecurityCode("SHD3165RT");
        portfolioPosition6.setSecurityName("Share Valuation");
        portfolioPosition6.setValue("2000");
        portfolioPosition6.setSecurityClass("Equity");

        portfolioPosition7.setSecurityCode("");
        portfolioPosition7.setSecurityName(null);
        portfolioPosition7.setValue("5000");
        portfolioPosition7.setSecurityClass("Unit Trust");

        portfolioPosition8.setSecurityCode("");
        portfolioPosition8.setSecurityName("Managed Fund Valuation");
        portfolioPosition8.setValue("7000");
        portfolioPosition8.setSecurityClass("Unit Trust");

        List<PortfolioPosition> portfolioPositions = new ArrayList<>();
        portfolioPositions.add(portfolioPosition1);
        portfolioPositions.add(portfolioPosition2);
        portfolioPositions.add(portfolioPosition3);
        portfolioPositions.add(portfolioPosition4);
        portfolioPositions.add(portfolioPosition5);
        portfolioPositions.add(portfolioPosition6);
        portfolioPositions.add(portfolioPosition7);
        portfolioPositions.add(portfolioPosition8);

        ThirdPartyDetails thirdPartyDetails = new ThirdPartyDetails();
        thirdPartyDetails.setSystemType(SystemType.WRAP);
        thirdPartyDetails.setMigrationKey("M02744476");
        thirdPartyDetails.setMigrationDate(new DateTime());
        Mockito.when(
                avaloqAccountIntegrationService.getThirdPartySystemDetails(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(thirdPartyDetails);

        Mockito.when(assetIntegrationService.loadAssetsForAssetCodes(Mockito.any(List.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(null);

        Mockito.when(portfolioService.getPortfolioPositionForClient(Mockito.anyString(), Mockito.any(Date.class),
                Mockito.anyBoolean(), Mockito.any(ServiceErrors.class))).thenReturn(portfolioPositions);

        WrapAccountValuation wrapAccountValuation =
                portfolioIntegrationService.loadWrapAccountValuation("25689", new DateTime(), false, new ServiceErrorsImpl());
        Assert.assertNotNull(wrapAccountValuation);
        List<SubAccountValuation> subAccountValuations = wrapAccountValuation.getSubAccountValuations();
        Assert.assertNotNull(subAccountValuations);
        Assert.assertTrue(subAccountValuations.size() == 4);
        sort(subAccountValuations);

        SubAccountValuation subAccountValuation1 = subAccountValuations.get(0);
        Assert.assertEquals(AssetType.CASH, subAccountValuation1.getAssetType());
        Assert.assertNotNull(subAccountValuation1.getHoldings());
        WrapCashHoldingImpl cashHolding = (WrapCashHoldingImpl) subAccountValuation1.getHoldings().get(0);
        Assert.assertEquals(new BigDecimal(30948), cashHolding.getValueDateBalance());
        Assert.assertEquals("Working Cash Account", cashHolding.getAccountName());

        SubAccountValuation subAccountValuation2 = subAccountValuations.get(1);
        Assert.assertEquals(AssetType.TERM_DEPOSIT, subAccountValuation2.getAssetType());
        Assert.assertNotNull(subAccountValuation2.getHoldings());
        WrapTermDepositHoldingImpl tdHolding = (WrapTermDepositHoldingImpl) subAccountValuation2.getHoldings().get(0);
        Assert.assertEquals(new BigDecimal(50000), tdHolding.getMarketValue());
        Assert.assertEquals("WBC Term Deposit 185d 05-MAY-16 2.50%", tdHolding.getHoldingKey().getName());

        SubAccountValuation subAccountValuation3 = subAccountValuations.get(2);
        Assert.assertEquals(AssetType.SHARE, subAccountValuation3.getAssetType());
        Assert.assertNotNull(subAccountValuation3.getHoldings());
        ShareHoldingImpl shareHolding = (ShareHoldingImpl) subAccountValuation3.getHoldings().get(0);
        Assert.assertEquals(new BigDecimal(3000), shareHolding.getMarketValue());
        Assert.assertEquals("Share Valuation", shareHolding.getHoldingKey().getName());

        SubAccountValuation subAccountValuation4 = subAccountValuations.get(3);
        Assert.assertEquals(AssetType.MANAGED_FUND, subAccountValuation4.getAssetType());
        Assert.assertNotNull(subAccountValuation4.getHoldings());
        ManagedFundHoldingImpl managedFundHolding = (ManagedFundHoldingImpl) subAccountValuation4.getHoldings().get(0);
        Assert.assertEquals(new BigDecimal(4000), managedFundHolding.getMarketValue());
        Assert.assertEquals("Managed Fund Valuation", managedFundHolding.getHoldingKey().getName());
    }

    @Test
    public void testLoadWrapAccountValuation_withAvaloqAsset() {
        PortfolioPosition portfolioPosition1 = new PortfolioPosition();
        PortfolioPosition portfolioPosition2 = new PortfolioPosition();

        portfolioPosition1.setSecurityCode("Cash Account");
        portfolioPosition1.setSecurityName("Working Cash Account");
        portfolioPosition1.setValue("30948");

        portfolioPosition2.setSecurityCode("SHD3265RT");
        portfolioPosition2.setSecurityName("Option valuation");
        portfolioPosition2.setValue("0.0");

        List<PortfolioPosition> portfolioPositions = new ArrayList<>();
        portfolioPositions.add(portfolioPosition1);
        portfolioPositions.add(portfolioPosition2);

        ThirdPartyDetails thirdPartyDetails = new ThirdPartyDetails();
        thirdPartyDetails.setSystemType(SystemType.WRAP);
        thirdPartyDetails.setMigrationKey("M02744476");
        thirdPartyDetails.setMigrationDate(new DateTime());
        Mockito.when(
                avaloqAccountIntegrationService.getThirdPartySystemDetails(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(thirdPartyDetails);

        AssetImpl asset = new AssetImpl();
        asset.setAssetType(AssetType.OPTION);
        List<Asset> assets = new ArrayList<>();
        assets.add(asset);

        Mockito.when(assetIntegrationService.loadAssetsForAssetCodes(Mockito.any(List.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(assets);

        Mockito.when(portfolioService.getPortfolioPositionForClient(Mockito.anyString(), Mockito.any(Date.class),
                Mockito.anyBoolean(), Mockito.any(ServiceErrors.class))).thenReturn(portfolioPositions);

        WrapAccountValuation wrapAccountValuation =
                portfolioIntegrationService.loadWrapAccountValuation("25689", new DateTime(), false, new ServiceErrorsImpl());
        Assert.assertNotNull(wrapAccountValuation);
        List<SubAccountValuation> subAccountValuations = wrapAccountValuation.getSubAccountValuations();
        Assert.assertNotNull(subAccountValuations);
        Assert.assertTrue(subAccountValuations.size() == 1);
        sort(subAccountValuations);

        SubAccountValuation subAccountValuation1 = subAccountValuations.get(0);
        Assert.assertEquals(AssetType.CASH, subAccountValuation1.getAssetType());
        Assert.assertNotNull(subAccountValuation1.getHoldings());
        WrapCashHoldingImpl cashHolding = (WrapCashHoldingImpl) subAccountValuation1.getHoldings().get(0);
        Assert.assertEquals(new BigDecimal(30948), cashHolding.getValueDateBalance());
        Assert.assertEquals("Working Cash Account", cashHolding.getAccountName());
    }

    @Test
    public void testLoadWrapAccountValuation_checkSecurityNameWithAvaloqAsset() {
        PortfolioPosition portfolioPosition1 = new PortfolioPosition();

        portfolioPosition1.setSecurityCode("SHD3265RT");
        portfolioPosition1.setSecurityName("Wrap share valuation");
        portfolioPosition1.setValue("30948");

        List<PortfolioPosition> portfolioPositions = new ArrayList<>();
        portfolioPositions.add(portfolioPosition1);

        ThirdPartyDetails thirdPartyDetails = new ThirdPartyDetails();
        thirdPartyDetails.setSystemType(SystemType.WRAP);
        thirdPartyDetails.setMigrationKey("M02744476");
        thirdPartyDetails.setMigrationDate(new DateTime());
        Mockito.when(
                avaloqAccountIntegrationService.getThirdPartySystemDetails(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(thirdPartyDetails);

        AssetImpl asset = new AssetImpl();
        asset.setAssetType(AssetType.SHARE);
        asset.setAssetName("Panorama share valuation");
        List<Asset> assets = new ArrayList<>();
        assets.add(asset);

        Mockito.when(assetIntegrationService.loadAssetsForAssetCodes(Mockito.any(List.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(assets);

        Mockito.when(portfolioService.getPortfolioPositionForClient(Mockito.anyString(), Mockito.any(Date.class),
                Mockito.anyBoolean(), Mockito.any(ServiceErrors.class))).thenReturn(portfolioPositions);

        WrapAccountValuation wrapAccountValuation =
                portfolioIntegrationService.loadWrapAccountValuation("25689", new DateTime(), false, new ServiceErrorsImpl());
        Assert.assertNotNull(wrapAccountValuation);
        List<SubAccountValuation> subAccountValuations = wrapAccountValuation.getSubAccountValuations();
        Assert.assertNotNull(subAccountValuations);
        Assert.assertTrue(subAccountValuations.size() == 1);
        sort(subAccountValuations);

        SubAccountValuation subAccountValuation1 = subAccountValuations.get(0);
        Assert.assertEquals(AssetType.SHARE, subAccountValuation1.getAssetType());
        Assert.assertNotNull(subAccountValuation1.getHoldings());
        ShareHoldingImpl shareHolding = (ShareHoldingImpl) subAccountValuation1.getHoldings().get(0);
        Assert.assertEquals(new BigDecimal(30948), shareHolding.getMarketValue());
        Assert.assertEquals("Panorama share valuation", shareHolding.getHoldingKey().getName());
    }

    private void sort(List<SubAccountValuation> subAccountValuations) {
        Collections.sort(subAccountValuations, new Comparator<SubAccountValuation>() {
            @Override
            public int compare(SubAccountValuation o1, SubAccountValuation o2) {
                return new Integer(o1.getAssetType().getSortOrder()).compareTo(o2.getAssetType().getSortOrder());
            }
        });
    }

}
