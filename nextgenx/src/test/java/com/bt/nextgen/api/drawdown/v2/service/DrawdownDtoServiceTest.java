package com.bt.nextgen.api.drawdown.v2.service;

import com.bt.nextgen.api.drawdown.v2.model.DrawdownDetailsDto;
import com.bt.nextgen.api.drawdown.v2.model.DrawdownDto;
import com.bt.nextgen.api.drawdown.v2.validation.DrawdownErrorMapper;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.drawdownstrategy.DrawdownStrategyDetailsImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategy;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategyDetails;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategyIntegrationService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DrawdownDtoServiceTest {

    @InjectMocks
    private DrawdownDtoServiceImpl drawdownDtoService;

    @Mock
    private DrawdownStrategyIntegrationService drawdownStrategyService;

    @Mock
    private DrawdownErrorMapper errorMapper;

    private DrawdownStrategyDetailsImpl drawdownDetailsModel;

    @Before
    public void setUp() throws Exception {
        drawdownDetailsModel = mock(DrawdownStrategyDetailsImpl.class);
        when(drawdownDetailsModel.getAccountKey()).thenReturn(AccountKey.valueOf("accountId"));
        when(drawdownDetailsModel.getDrawdownStrategy()).thenReturn(DrawdownStrategy.ASSET_PRIORITY);
        when(drawdownDetailsModel.getAssetPriorityDetails()).thenReturn(null);
    }

    @Test
    public void testGetDrawdown() {
        when(drawdownStrategyService.loadDrawdownStrategy(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(
                DrawdownStrategy.PRORATA);
        DrawdownDto drawdownDto = drawdownDtoService.find(new com.bt.nextgen.api.account.v3.model.AccountKey(
                "FC622CC21BFFE81A42F70BB718A6A91D1D9470BB4FB2631D"), new ServiceErrorsImpl());
        Assert.assertEquals("prorata", drawdownDto.getDrawdownType());
    }

    @Test
    public void test_findDrawdown_forAssetPriorityStrategy() {
        when(drawdownStrategyService.loadDrawdownStrategy(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(
                DrawdownStrategy.ASSET_PRIORITY);
        when(drawdownStrategyService.loadDrawdownAssetPreferences(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(
                drawdownDetailsModel);
        DrawdownDto drawdownDto = drawdownDtoService.find(new com.bt.nextgen.api.account.v3.model.AccountKey(
                "FC622CC21BFFE81A42F70BB718A6A91D1D9470BB4FB2631D"), new ServiceErrorsImpl());

        Assert.assertNotNull(drawdownDto);
        Assert.assertEquals(DrawdownStrategy.ASSET_PRIORITY.getIntlId(), drawdownDto.getDrawdownType());
    }

    @Test
    public void test_findDrawdown_forNullStrategy() {
        when(drawdownStrategyService.loadDrawdownStrategy(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(null);
        DrawdownDto drawdownDto = drawdownDtoService.find(new com.bt.nextgen.api.account.v3.model.AccountKey(
                "FC622CC21BFFE81A42F70BB718A6A91D1D9470BB4FB2631D"), new ServiceErrorsImpl());

        Assert.assertNotNull(drawdownDto);
        Assert.assertEquals(null, drawdownDto.getDrawdownType());
    }

    @Test
    public void testUpdateDrawdown() {
        DrawdownDetailsDto drawdownDto = new DrawdownDetailsDto(new com.bt.nextgen.api.account.v3.model.AccountKey(
                "FC622CC21BFFE81A42F70BB718A6A91D1D9470BB4FB2631D"), DrawdownStrategy.ASSET_PRIORITY.getIntlId(), null);

        when(drawdownStrategyService.submitDrawdownStrategy(any(DrawdownStrategyDetails.class), any(ServiceErrors.class)))
                .thenReturn(drawdownDetailsModel);

        DrawdownDetailsDto dto = drawdownDtoService.update(drawdownDto, new ServiceErrorsImpl());
        Assert.assertNotNull(dto);
        Assert.assertEquals(DrawdownStrategy.ASSET_PRIORITY.getIntlId(), dto.getDrawdownType());
    }
}
