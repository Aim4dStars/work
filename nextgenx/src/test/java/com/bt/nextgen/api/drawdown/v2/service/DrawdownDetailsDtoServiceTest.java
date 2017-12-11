package com.bt.nextgen.api.drawdown.v2.service;

import com.bt.nextgen.api.drawdown.v2.model.DrawdownDetailsDto;
import com.bt.nextgen.api.drawdown.v2.validation.DrawdownErrorMapper;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.validation.ValidationError.ErrorType;
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
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategyIntegrationService;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DrawdownDetailsDtoServiceTest {

    @InjectMocks
    private DrawdownDetailsDtoServiceImpl dtoService;

    @Mock
    private DrawdownStrategyIntegrationService drawdownService;

    @Mock
    private CachePortfolioIntegrationService cachedPortfolioIntegrationService;

    @Mock
    private AssetIntegrationService assetService;

    @Mock
    private DrawdownErrorMapper errorMapper;

    private ServiceErrorsImpl serviceErrors;

    @Before
    public void setup() {
        serviceErrors = new ServiceErrorsImpl();
    }

    @Test
    public void test_updateAssetPreference() {

        DrawdownStrategyDetails model = mock(DrawdownStrategyDetailsImpl.class);
        when(model.getAccountKey()).thenReturn(AccountKey.valueOf("accountId"));
        when(model.getAssetPriorityDetails()).thenReturn(null);

        when(drawdownService.submitDrawdownAssetPreferences(any(DrawdownStrategyDetails.class), any(ServiceErrors.class)))
                .thenReturn(model);
        String accId = EncodedString.fromPlainText("accountId").toString();

        DrawdownDetailsDto dto = new DrawdownDetailsDto(new com.bt.nextgen.api.account.v3.model.AccountKey(accId),
                DrawdownStrategy.ASSET_PRIORITY.getIntlId(), null);
        dto = dtoService.submit(dto, serviceErrors);
        Assert.assertNotNull(dto);
    }

    @Test
    public void test_updateAssetPreferenceWithError() {

        DrawdownStrategyDetailsImpl model = mock(DrawdownStrategyDetailsImpl.class);
        when(model.getAccountKey()).thenReturn(AccountKey.valueOf("accountId"));
        when(model.getAssetPriorityDetails()).thenReturn(null);

        ValidationError err = mock(ValidationError.class);
        when(err.getErrorId()).thenReturn("errorId");
        when(err.getType()).thenReturn(ErrorType.ERROR);
        when(err.getMessage()).thenReturn("errorMessage");
        when(model.getValidationErrors()).thenReturn(Collections.singletonList(err));

        DomainApiErrorDto errorDto = new DomainApiErrorDto("errorId", "errorField", null, "errorMessage",
                com.bt.nextgen.core.api.model.DomainApiErrorDto.ErrorType.ERROR);
        when(errorMapper.map(Mockito.anyList())).thenReturn(Collections.singletonList(errorDto));
        when(drawdownService.submitDrawdownAssetPreferences(any(DrawdownStrategyDetails.class), any(ServiceErrors.class)))
                .thenReturn(model);

        String accId = EncodedString.fromPlainText("accountId").toString();
        DrawdownDetailsDto dto = new DrawdownDetailsDto(new com.bt.nextgen.api.account.v3.model.AccountKey(accId),
                DrawdownStrategy.ASSET_PRIORITY.getIntlId(), null);
        dto = dtoService.submit(dto, serviceErrors);

        Assert.assertNotNull(dto);
        Assert.assertEquals(1, dto.getWarnings().size());
    }

    @Test
    public void test_validate() {

        DrawdownStrategyDetailsImpl model = mock(DrawdownStrategyDetailsImpl.class);
        when(model.getAccountKey()).thenReturn(AccountKey.valueOf("accountId"));
        when(model.getAssetPriorityDetails()).thenReturn(null);

        ValidationError err = mock(ValidationError.class);
        when(err.getErrorId()).thenReturn("errorId");
        when(err.getType()).thenReturn(ErrorType.ERROR);
        when(err.getMessage()).thenReturn("errorMessage");
        when(model.getValidationErrors()).thenReturn(Collections.singletonList(err));

        DomainApiErrorDto errorDto = new DomainApiErrorDto("errorId", "errorField", null, "errorMessage",
                com.bt.nextgen.core.api.model.DomainApiErrorDto.ErrorType.ERROR);
        when(errorMapper.map(Mockito.anyList())).thenReturn(Collections.singletonList(errorDto));
        when(drawdownService.validateDrawdownAssetPreferences(any(DrawdownStrategyDetails.class), any(ServiceErrors.class)))
                .thenReturn(model);

        String accId = EncodedString.fromPlainText("accountId").toString();
        DrawdownDetailsDto dto = new DrawdownDetailsDto(new com.bt.nextgen.api.account.v3.model.AccountKey(accId),
                DrawdownStrategy.ASSET_PRIORITY.getIntlId(), null);
        dto = dtoService.validate(dto, serviceErrors);

        Assert.assertNotNull(dto);
        Assert.assertEquals(1, dto.getWarnings().size());
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

        when(drawdownService.loadDrawdownAssetPreferences(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(model);
        DrawdownDetailsDto dto = dtoService.find(
                new com.bt.nextgen.api.account.v3.model.AccountKey(EncodedString.fromPlainText("accountId").toString()),
                serviceErrors);
        Assert.assertTrue(dto != null);
        Assert.assertEquals(1, dto.getPriorityDrawdownList().size());
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
