package com.bt.nextgen.service.wrap.integration.income;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.income.CashIncomeImpl;
import com.bt.nextgen.service.avaloq.income.DistributionIncomeImpl;
import com.bt.nextgen.service.avaloq.income.InterestIncomeImpl;
import com.bt.nextgen.service.avaloq.income.TermDepositIncomeImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.income.CashIncome;
import com.bt.nextgen.service.integration.income.DistributionIncome;
import com.bt.nextgen.service.integration.income.InterestIncome;
import com.bt.nextgen.service.integration.income.SubAccountIncomeDetails;
import com.bt.nextgen.service.integration.income.TermDepositIncome;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.wrap.model.Income;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by L067221 on 19/09/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class WrapIncomeConverterTest {


    @InjectMocks
    private WrapIncomeConverter wrapIncomeConverter;

    @Mock
    private AssetIntegrationService assetService;

    private Income createWrapIncome(String securityCode) {
        Income income = new Income();
        income.setNetAmount(new BigDecimal("4588.87"));
        income.setAccrualDate("15/03/2015");
        income.setPayDate("15/02/2015");
        income.setSecurityName("wrap security name");
        income.setPrice(new BigDecimal("4588.87"));
        income.setSecurityCode(securityCode);
        return income;
    }

    private Asset mockAsset(AssetType assetType, String id, String assetName) {
        Asset asset = mock(Asset.class);
        when(asset.getAssetType()).thenReturn(assetType);
        when(asset.getAssetCode()).thenReturn(id);
        when(asset.getAssetName()).thenReturn(assetName);
        when(asset.getAssetId()).thenReturn(id);
        return asset;
    }


    @Test
    public void testWrapCash() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<Income> incomeList = new ArrayList<>();
        incomeList.add(createWrapIncome("WRAPWCA"));
        incomeList.add(createWrapIncome("WRAPWCA"));
        List<SubAccountIncomeDetails> result = wrapIncomeConverter.convert(incomeList, serviceErrors);
        assertNotNull(result);
        SubAccountIncomeDetails accountIncomeDetails = result.get(0);
        assertEquals(AssetType.CASH, accountIncomeDetails.getAssetType());
        assertEquals(1, accountIncomeDetails.getIncomes().size());

        assertTrue(accountIncomeDetails.getIncomes().get(0).getIncomes()
                .get(0) instanceof CashIncomeImpl);
        CashIncome income = (CashIncomeImpl) accountIncomeDetails.getIncomes().get(0).getIncomes()
                .get(0);

        assertEquals(new DateTime("2015-02-15T00:00:00.000"), income.getPaymentDate());
    }

    @Test
    public void testWrapListedSecurity() throws Exception {
        Asset shareAsset = mockAsset(AssetType.SHARE, "lsdist", "Mirvac Group (Stapled Security)");
        List<Asset> assetList = new ArrayList<>();
        assetList.add(shareAsset);
        when(assetService.loadAssetsForAssetCodes(anyCollection(), any(ServiceErrors.class))).thenReturn(assetList);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<Income> incomeList = new ArrayList<>();
        incomeList.add(createWrapIncome("lsdist"));
        List<SubAccountIncomeDetails> result = wrapIncomeConverter.convert(incomeList, serviceErrors);
        assertNotNull(result);
        SubAccountIncomeDetails accountIncomeDetails = result.get(0);
        assertEquals(AssetType.SHARE, accountIncomeDetails.getAssetType());
        assertEquals(1, accountIncomeDetails.getIncomes().size());
        assertTrue(accountIncomeDetails.getIncomes().get(0).getIncomes()
                .get(0) instanceof DistributionIncomeImpl);
        DistributionIncome income = (DistributionIncomeImpl) accountIncomeDetails.getIncomes().get(0).getIncomes()
                .get(0);
        assertEquals(new DateTime("2015-02-15T00:00:00.000"), income.getPaymentDate());

    }


    @Test
    public void testWrapListedSecurity_assetTypeMF() throws Exception {
        Asset mfAsset = mockAsset(AssetType.MANAGED_FUND, "RIM0001AU", "Russell Investments Balanced Fund - Class A");
        List<Asset> assetList = new ArrayList<>();
        assetList.add(mfAsset);
        when(assetService.loadAssetsForAssetCodes(anyCollection(), any(ServiceErrors.class))).thenReturn(assetList);


        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<Income> incomeList = new ArrayList<>();
        incomeList.add(createWrapIncome("RIM0001AU"));
        List<SubAccountIncomeDetails> result = wrapIncomeConverter.convert(incomeList, serviceErrors);
        assertNotNull(result);
        SubAccountIncomeDetails accountIncomeDetails = result.get(0);
        assertEquals(AssetType.MANAGED_FUND, accountIncomeDetails.getAssetType());
        assertEquals(1, accountIncomeDetails.getIncomes().size());
        assertTrue(accountIncomeDetails.getIncomes().get(0).getIncomes()
                .get(0) instanceof DistributionIncomeImpl);
        DistributionIncome income = (DistributionIncomeImpl) accountIncomeDetails.getIncomes().get(0).getIncomes()
                .get(0);

        assertEquals(new DateTime("2015-02-15T00:00:00.000"), income.getPaymentDate());

    }


    @Test
    public void testWrapEquitySecurityClass() throws Exception {
        Asset mfAsset = null;
        List<Asset> assetList = new ArrayList<>();
        assetList.add(mfAsset);
        when(assetService.loadAssetsForAssetCodes(anyCollection(), any(ServiceErrors.class))).thenReturn(assetList);

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<Income> incomeList = new ArrayList<>();
        Income wrapIncome = createWrapIncome("wrapCode");
        wrapIncome.setSecurityClass("Equity");
        incomeList.add(wrapIncome);
        List<SubAccountIncomeDetails> result = wrapIncomeConverter.convert(incomeList, serviceErrors);
        assertNotNull(result);
        SubAccountIncomeDetails accountIncomeDetails = result.get(0);
        assertEquals(AssetType.SHARE, accountIncomeDetails.getAssetType());
        assertEquals(1, accountIncomeDetails.getIncomes().size());
        assertTrue(accountIncomeDetails.getIncomes().get(0).getIncomes()
                .get(0) instanceof DistributionIncomeImpl);
        DistributionIncome income = (DistributionIncomeImpl) accountIncomeDetails.getIncomes().get(0).getIncomes()
                .get(0);

        assertEquals(new DateTime("2015-02-15T00:00:00.000"), income.getPaymentDate());

    }

    @Test
    public void testWrapUnitTrustSecurityClass() throws Exception {
        Asset mfAsset = null;
        List<Asset> assetList = new ArrayList<>();
        assetList.add(mfAsset);
        when(assetService.loadAssetsForAssetCodes(anyCollection(), any(ServiceErrors.class))).thenReturn(assetList);

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<Income> incomeList = new ArrayList<>();
        Income wrapIncome = createWrapIncome("wrapCode");
        wrapIncome.setSecurityClass("Unit Trust");
        incomeList.add(wrapIncome);
        List<SubAccountIncomeDetails> result = wrapIncomeConverter.convert(incomeList, serviceErrors);
        assertNotNull(result);
        SubAccountIncomeDetails accountIncomeDetails = result.get(0);
        assertEquals(AssetType.MANAGED_FUND, accountIncomeDetails.getAssetType());
        assertEquals(1, accountIncomeDetails.getIncomes().size());
        assertTrue(accountIncomeDetails.getIncomes().get(0).getIncomes()
                .get(0) instanceof DistributionIncomeImpl);
        DistributionIncome income = (DistributionIncomeImpl) accountIncomeDetails.getIncomes().get(0).getIncomes()
                .get(0);

        assertEquals(new DateTime("2015-02-15T00:00:00.000"), income.getPaymentDate());

    }

    @Test
    public void testWrapNullSecurityClass() throws Exception {
        Asset mfAsset = null;
        List<Asset> assetList = new ArrayList<>();
        assetList.add(mfAsset);
        when(assetService.loadAssetsForAssetCodes(anyCollection(), any(ServiceErrors.class))).thenReturn(null);

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<Income> incomeList = new ArrayList<>();
        Income wrapIncome = createWrapIncome("wrapCode");
        wrapIncome.setSecurityClass(null);
        incomeList.add(wrapIncome);

        List<SubAccountIncomeDetails> result = wrapIncomeConverter.convert(incomeList, serviceErrors);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    public void testWrapTermDeposits() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<Income> incomeList = new ArrayList<>();
        incomeList.add(createWrapIncome("WBC123TD"));
        List<SubAccountIncomeDetails> result = wrapIncomeConverter.convert(incomeList, serviceErrors);
        assertNotNull(result);
        SubAccountIncomeDetails accountIncomeDetails = result.get(0);
        assertEquals(AssetType.TERM_DEPOSIT, accountIncomeDetails.getAssetType());
        assertEquals(1, accountIncomeDetails.getIncomes().size());
        assertTrue(accountIncomeDetails.getIncomes().get(0).getIncomes()
                .get(0) instanceof TermDepositIncomeImpl);
        TermDepositIncome income = (TermDepositIncomeImpl) accountIncomeDetails.getIncomes().get(0).getIncomes()
                .get(0);

        assertEquals(new DateTime("2015-02-15T00:00:00.000"), income.getPaymentDate());
    }

    @Test
    public void testWrapInterest() throws Exception {
        Asset interestAsset = mockAsset(AssetType.OPTION, "lsint", "Interest");
        when(interestAsset.getRevenueAssetIndicator()).thenReturn("+");
        List<Asset> assetList = new ArrayList<>();
        assetList.add(interestAsset);
        when(assetService.loadAssetsForAssetCodes(anyCollection(), any(ServiceErrors.class))).thenReturn(assetList);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<Income> incomeList = new ArrayList<>();
        incomeList.add(createWrapIncome("lsint"));
        List<SubAccountIncomeDetails> result = wrapIncomeConverter.convert(incomeList, serviceErrors);
        assertNotNull(result);
        SubAccountIncomeDetails accountIncomeDetails = result.get(0);
        assertEquals(AssetType.SHARE, accountIncomeDetails.getAssetType());
        assertEquals(1, accountIncomeDetails.getIncomes().size());
        assertTrue(accountIncomeDetails.getIncomes().get(0).getIncomes()
                .get(0) instanceof InterestIncomeImpl);
        InterestIncome income = (InterestIncomeImpl) accountIncomeDetails.getIncomes().get(0).getIncomes()
                .get(0);

        assertEquals(new DateTime("2015-02-15T00:00:00.000"), income.getPaymentDate());
    }

    @Test
    public void testWrapInterestWithBond() throws Exception {
        Asset interestAsset = mockAsset(AssetType.BOND, "lsint", "Interest");
        when(interestAsset.getRevenueAssetIndicator()).thenReturn("+");
        List<Asset> assetList = new ArrayList<>();
        assetList.add(interestAsset);
        when(assetService.loadAssetsForAssetCodes(anyCollection(), any(ServiceErrors.class))).thenReturn(assetList);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<Income> incomeList = new ArrayList<>();
        incomeList.add(createWrapIncome("lsint"));
        List<SubAccountIncomeDetails> result = wrapIncomeConverter.convert(incomeList, serviceErrors);
        assertNotNull(result);
        SubAccountIncomeDetails accountIncomeDetails = result.get(0);
        assertEquals(AssetType.SHARE, accountIncomeDetails.getAssetType());
        assertEquals(1, accountIncomeDetails.getIncomes().size());
        assertTrue(accountIncomeDetails.getIncomes().get(0).getIncomes()
                .get(0) instanceof InterestIncomeImpl);
        InterestIncome income = (InterestIncomeImpl) accountIncomeDetails.getIncomes().get(0).getIncomes()
                .get(0);

        assertEquals(new DateTime("2015-02-15T00:00:00.000"), income.getPaymentDate());
    }

    @Test
    public void testWrapIfSecurityNull() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<Income> incomeList = new ArrayList<>();
        incomeList.add(createWrapIncome(null));
        List<SubAccountIncomeDetails> result = wrapIncomeConverter.convert(incomeList, serviceErrors);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
