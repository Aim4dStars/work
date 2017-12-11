package com.bt.nextgen.api.portfolio.v3.model;

import com.bt.nextgen.api.smsf.constants.PropertyType;
import com.bt.nextgen.service.avaloq.asset.ExternalValuationAssetImpl;
import com.bt.nextgen.service.avaloq.asset.ShareAssetImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.OtherHoldingImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ShareHoldingImpl;
import com.bt.nextgen.service.integration.asset.AssetCluster;
import com.bt.nextgen.service.integration.portfolio.valuation.HoldingKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

@RunWith(MockitoJUnitRunner.class)
public class InvestmentAssetDtoTest {

    private ShareAssetImpl shareAsset;
    private ShareHoldingImpl shareHolding;
    private OtherHoldingImpl otherHolding;
    private ExternalValuationAssetImpl externalAsset;

    @Before
    public void setup() {
        shareAsset = new ShareAssetImpl();
        shareAsset.setInvestmentHoldingLimit(new BigDecimal("33"));
        shareAsset.setInvestmentHoldingLimitBuffer(new BigDecimal("39"));
        shareAsset.setSuperInvestIhl(new BigDecimal("33"));
        shareAsset.setSuperInvestIhlBuffer(new BigDecimal("39"));
        shareHolding = new ShareHoldingImpl();
        shareHolding.setAsset(shareAsset);
        shareHolding.setHoldingKey(HoldingKey.valueOf("1", "shares"));

        otherHolding = new OtherHoldingImpl();
        externalAsset = new ExternalValuationAssetImpl();
        externalAsset.setAssetCluster(AssetCluster.DIRECT_PROPERTY);
        externalAsset.setPropertyType(PropertyType.COMMERCIAL_PROPERTY.getCode());
        otherHolding.setAsset(externalAsset);
        otherHolding.setHoldingKey(HoldingKey.valueOf("2", "external"));
    }

    @Test
    public void testContructor_whenInvokedWithShareHolding_thenTheCastNecessaryAttributesAreCorrect() {
        final InvestmentAssetDto assetDto = new InvestmentAssetDto(shareHolding, null);
        Assert.assertEquals(shareAsset.getInvestmentHoldingLimit(), assetDto.getInvestmentHoldingLimit());
        Assert.assertEquals(shareAsset.getInvestmentHoldingLimitBuffer(), assetDto.getInvestmentHoldingLimitBuffer());
        Assert.assertEquals(shareAsset.getSuperInvestIhl(), assetDto.getSuperInvestIhl());
        Assert.assertEquals(shareAsset.getSuperInvestIhlBuffer(), assetDto.getSuperInvestIhlBuffer());
    }

    @Test
    public void testContructor_whenInvokedWithExternalPropertyHolding_withValidPropertyType() {
        final InvestmentAssetDto assetDto = new InvestmentAssetDto(otherHolding, null);
        Assert.assertEquals(assetDto.getPropertyType(), PropertyType.COMMERCIAL_PROPERTY.getShortDesc());
    }
    @Test
    public void testContructor_whenInvokedWithExternalPropertyHolding_withoutValidPropertyType() {
        externalAsset.setPropertyType(null);
        final InvestmentAssetDto assetDto = new InvestmentAssetDto(otherHolding, null);
        Assert.assertNull(assetDto.getPropertyType());
    }

}
