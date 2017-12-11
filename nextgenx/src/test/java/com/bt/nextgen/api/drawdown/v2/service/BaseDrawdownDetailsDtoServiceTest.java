package com.bt.nextgen.api.drawdown.v2.service;

import com.bt.nextgen.api.drawdown.v2.model.AssetPriorityDto;
import com.bt.nextgen.api.drawdown.v2.model.DrawdownDetailsDto;
import com.bt.nextgen.api.drawdown.v2.validation.DrawdownErrorMapper;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.drawdownstrategy.DrawdownStrategyDetailsImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.drawdownstrategy.AssetPriorityDetails;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategy;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategyDetails;
import com.bt.nextgen.service.integration.portfolio.CachePortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BaseDrawdownDetailsDtoServiceTest {

    @InjectMocks
    private BaseDrawdownDetailsDtoService dtoService;

    @Mock
    private CachePortfolioIntegrationService cachedPortfolioIntegrationService;

    @Mock
    private AssetIntegrationService assetService;

    @Mock
    private DrawdownErrorMapper errorMapper;

    private ServiceErrorsImpl serviceErrors;

    @Before
    public void setup() {
    }

    @Test
    public void test_drawdownDto() {
        AssetPriorityDto apDto = new AssetPriorityDto();
        apDto.setAssetName("assetName");
        apDto.setAssetCode("assetCode");
        apDto.setStatus("assetStatus");
        apDto.setAssetType(AssetType.SHARE.getDisplayName());
        apDto.setMarketValue(BigDecimal.TEN);

        Assert.assertEquals("assetName", apDto.getAssetName());
        Assert.assertEquals("assetCode", apDto.getAssetCode());
        Assert.assertEquals("assetStatus", apDto.getStatus());
        Assert.assertEquals(AssetType.SHARE.getDisplayName(), apDto.getAssetType());
        Assert.assertEquals(BigDecimal.TEN, apDto.getMarketValue());
    }

    @Test
    public void test_convertToDto_emptyAssetPriority() {
        DrawdownStrategyDetails model = mock(DrawdownStrategyDetailsImpl.class);
        when(model.getAccountKey()).thenReturn(AccountKey.valueOf("accountId"));
        when(model.getDrawdownStrategy()).thenReturn(DrawdownStrategy.ASSET_PRIORITY);
        when(model.getAssetPriorityDetails()).thenReturn(null);

        DrawdownDetailsDto dto = dtoService.convertToDto(model, DrawdownStrategy.ASSET_PRIORITY, serviceErrors);
        Assert.assertTrue(dto != null);
        Assert.assertEquals(0, dto.getPriorityDrawdownList().size());
        Assert.assertEquals(DrawdownStrategy.ASSET_PRIORITY.getIntlId(), dto.getDrawdownType());

        when(model.getAssetPriorityDetails()).thenReturn(new ArrayList<AssetPriorityDetails>());
        dto = dtoService.convertToDto(model, DrawdownStrategy.ASSET_PRIORITY, serviceErrors);
        Assert.assertTrue(dto != null);
        Assert.assertEquals(0, dto.getPriorityDrawdownList().size());

        when(errorMapper.map(Mockito.anyList())).thenReturn(null);
    }

    @Test
    public void test_convertToStrategyModel() {

        DrawdownDetailsDto dto = mock(DrawdownDetailsDto.class);
        String encodedAccId = EncodedString.fromPlainText("accountId").toString();
        when(dto.getKey()).thenReturn(new com.bt.nextgen.api.account.v3.model.AccountKey(encodedAccId));
        when(dto.getDrawdownType()).thenReturn(DrawdownStrategy.ASSET_PRIORITY.getIntlId());

        DrawdownStrategyDetails model = dtoService.convertoToStrategyModel(dto);
        Assert.assertNotNull(model);
        Assert.assertEquals(DrawdownStrategy.ASSET_PRIORITY, model.getDrawdownStrategy());
    }

    @Test
    public void test_convertToAssetPriorityModel_emptyAssetList() {
        DrawdownDetailsDto dto = mock(DrawdownDetailsDto.class);
        String encodedAccId = EncodedString.fromPlainText("accountId").toString();
        when(dto.getKey()).thenReturn(new com.bt.nextgen.api.account.v3.model.AccountKey(encodedAccId));

        DrawdownStrategyDetails model = dtoService.convertoToStrategyModel(dto);
        Assert.assertNotNull(model);
        Assert.assertNull(model.getAssetPriorityDetails());
    }

    @Test
    public void test_convertToDto_withAssetPriority() {
        AssetPriorityDetails pd = mock(AssetPriorityDetails.class);
        when(pd.getAssetId()).thenReturn("assetId");
        when(pd.getDrawdownPriority()).thenReturn(Integer.valueOf(1));

        DrawdownStrategyDetails model = mock(DrawdownStrategyDetailsImpl.class);
        when(model.getAccountKey()).thenReturn(AccountKey.valueOf("accountId"));
        when(model.getDrawdownStrategy()).thenReturn(DrawdownStrategy.ASSET_PRIORITY);
        when(model.getAssetPriorityDetails()).thenReturn(Collections.singletonList(pd));

        // Mock assetDetails service
        Map<String, Asset> assetMap = new HashMap<>();
        Asset asset = mock(Asset.class);
        when(asset.getAssetId()).thenReturn("assetId");
        when(asset.getAssetName()).thenReturn("assetName");
        when(asset.getAssetCode()).thenReturn("assetCode");
        when(asset.getStatus()).thenReturn(AssetStatus.SUSPENDED);
        when(asset.getAssetType()).thenReturn(AssetType.SHARE);
        assetMap.put("assetId", asset);
        when(assetService.loadAssets(Collections.singletonList("assetId"), serviceErrors)).thenReturn(assetMap);

        // Mock valuation service
        mockValuation(asset);

        DrawdownDetailsDto dto = dtoService.convertToDto(model, DrawdownStrategy.ASSET_PRIORITY, serviceErrors);
        Assert.assertTrue(dto != null);
        Assert.assertEquals(1, dto.getPriorityDrawdownList().size());
        AssetPriorityDto aDto = dto.getPriorityDrawdownList().get(0);
        Assert.assertEquals(asset.getAssetId(), aDto.getAssetId());
        Assert.assertEquals(asset.getAssetCode(), aDto.getAssetCode());
        Assert.assertEquals(asset.getAssetName(), aDto.getAssetName());
        Assert.assertEquals(asset.getStatus().getDisplayName(), aDto.getStatus());
        Assert.assertEquals(asset.getAssetType().getDisplayName(), aDto.getAssetType());
        Assert.assertEquals(Integer.valueOf(1), aDto.getDrawdownPriority());

        DrawdownStrategyDetails resultModel = dtoService.convertoToAssetPriorityModel(dto);
        Assert.assertTrue(resultModel != null);
        Assert.assertNull(resultModel.getDrawdownStrategy());
        Assert.assertEquals(1, resultModel.getAssetPriorityDetails().size());
    }

    @Test
    public void test_convertToDto_withAssetWithNullStatus() {
        AssetPriorityDetails pd = mock(AssetPriorityDetails.class);
        when(pd.getAssetId()).thenReturn("assetId");
        when(pd.getDrawdownPriority()).thenReturn(Integer.valueOf(1));

        DrawdownStrategyDetails model = mock(DrawdownStrategyDetailsImpl.class);
        when(model.getAccountKey()).thenReturn(AccountKey.valueOf("accountId"));
        when(model.getDrawdownStrategy()).thenReturn(DrawdownStrategy.ASSET_PRIORITY);
        when(model.getAssetPriorityDetails()).thenReturn(Collections.singletonList(pd));

        // Mock assetDetails service
        Map<String, Asset> assetMap = new HashMap<>();
        Asset asset = mock(Asset.class);
        when(asset.getAssetId()).thenReturn("assetId");
        when(asset.getAssetName()).thenReturn("assetName");
        when(asset.getAssetCode()).thenReturn("assetCode");
        assetMap.put("assetId", asset);
        when(assetService.loadAssets(Collections.singletonList("assetId"), serviceErrors)).thenReturn(assetMap);

        // Mock valuation service
        mockValuation(asset);

        DrawdownDetailsDto dto = dtoService.convertToDto(model, DrawdownStrategy.ASSET_PRIORITY, serviceErrors);
        Assert.assertTrue(dto != null);
        Assert.assertEquals(1, dto.getPriorityDrawdownList().size());
        AssetPriorityDto aDto = dto.getPriorityDrawdownList().get(0);
        Assert.assertEquals(asset.getAssetId(), aDto.getAssetId());
        Assert.assertEquals(asset.getAssetCode(), aDto.getAssetCode());
        Assert.assertEquals(asset.getAssetName(), aDto.getAssetName());
        Assert.assertEquals(null, aDto.getStatus());
        Assert.assertNull(aDto.getAssetType());
        Assert.assertEquals(Integer.valueOf(1), aDto.getDrawdownPriority());
    }

    private WrapAccountValuation mockValuation(Asset asset) {
        AccountHolding accHolding = mock(AccountHolding.class);
        when(accHolding.getAsset()).thenReturn(asset);
        when(accHolding.getMarketValue()).thenReturn(BigDecimal.TEN);

        SubAccountValuation subAcc = mock(SubAccountValuation.class);
        when(subAcc.getHoldings()).thenReturn(Collections.singletonList(accHolding));

        WrapAccountValuation valuation = mock(WrapAccountValuation.class);
        when(valuation.getSubAccountValuations()).thenReturn(Collections.singletonList(subAcc));
        when(
                cachedPortfolioIntegrationService.loadWrapAccountValuation(any(AccountKey.class), any(DateTime.class),
                        any(ServiceErrors.class))).thenReturn(valuation);

        return valuation;
    }
}
