package com.bt.nextgen.service.wrap.integration.income;

import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.income.IncomeType;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.wrap.model.Income;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by L067221 on 19/09/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class WrapIncomeConverterModelBuilderTest {

    @InjectMocks
    WrapIncomeConverterModelBuilder builder;

    private Income wrapIncome;

    @Before
    public void setUp()  {
        wrapIncome  = new Income();
        wrapIncome.setNetAmount(new BigDecimal("4588.87"));
        wrapIncome.setAccrualDate("15/03/2015");
        wrapIncome.setPayDate("15/02/2015");
        wrapIncome.setSecurityName("WBC123TD");
        wrapIncome.setPrice(new BigDecimal("4588.87"));
    }

    @Test
    public void testgetIncomeType_whenIncomeTypeCash() {
        Asset asset = new AssetImpl();
        ((AssetImpl)asset).setAssetType(AssetType.CASH);
        IncomeType incomeType =  builder.getIncomeType(asset, wrapIncome);
        assertNotNull(incomeType);
        assertEquals(incomeType, IncomeType.CASH);
    }

    @Test
    public void testgetIncomeType_whenIncomeTypeTD() {
        Asset asset = new AssetImpl();
        ((AssetImpl)asset).setAssetType(AssetType.TERM_DEPOSIT);
        IncomeType incomeType =  builder.getIncomeType(asset, wrapIncome);
        assertNotNull(incomeType);
        assertEquals(incomeType, IncomeType.TERM_DEPOSIT);
    }

    @Test
    public void testgetIncomeType_whenIncomeTypeDistribution_ifAssetMF() {
        Asset asset = new AssetImpl();
        ((AssetImpl)asset).setAssetType(AssetType.MANAGED_FUND);
        IncomeType incomeType =  builder.getIncomeType(asset, wrapIncome);
        assertNotNull(incomeType);
        assertEquals(incomeType, IncomeType.DISTRIBUTION);
    }

    @Test
    public void testgetIncomeType_whenIncomeTypeDistribution() {
        Asset asset = new AssetImpl();
        ((AssetImpl)asset).setAssetType(AssetType.OPTION);
        IncomeType incomeType =  builder.getIncomeType(asset, wrapIncome);
        assertNotNull(incomeType);
        assertEquals(incomeType, IncomeType.DISTRIBUTION);
    }

    @Test
    public void testgetIncomeType_whenIncomeTypeDividend_ifFrankAmount() {
        Asset asset = new AssetImpl();
        ((AssetImpl)asset).setAssetType(AssetType.SHARE);
        wrapIncome.setFrankAmount(new BigDecimal("123.56"));
        IncomeType incomeType =  builder.getIncomeType(asset, wrapIncome);
        assertNotNull(incomeType);
        assertEquals(incomeType, IncomeType.DIVIDEND);
    }

    @Test
    public void testgetIncomeType_whenIncomeTypeDividend_ifNonFrankAmount() {
        Asset asset = new AssetImpl();
        ((AssetImpl)asset).setAssetType(AssetType.SHARE);
        wrapIncome.setUnFrankAmount(new BigDecimal("123.56"));
        IncomeType incomeType =  builder.getIncomeType(asset, wrapIncome);
        assertNotNull(incomeType);
        assertEquals(incomeType, IncomeType.DIVIDEND);
    }

    @Test
    public void testgetIncomeType_whenIncomeTypeInterest() {
        Asset asset = new AssetImpl();
        ((AssetImpl)asset).setAssetType(AssetType.SHARE);
        ((AssetImpl)asset).setRevenueAssetIndicator("1");
        IncomeType incomeType =  builder.getIncomeType(asset, wrapIncome);
        assertNotNull(incomeType);
        assertEquals(incomeType, IncomeType.INTEREST);
    }
}
