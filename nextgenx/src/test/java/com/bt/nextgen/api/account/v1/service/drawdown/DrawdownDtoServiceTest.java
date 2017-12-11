package com.bt.nextgen.api.account.v1.service.drawdown;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.account.v1.model.DrawdownDto;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.drawdown.AvaloqDrawdownIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.drawdown.DrawdownImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.drawdown.Drawdown;
import com.bt.nextgen.service.integration.drawdown.DrawdownOption;

/**
 * @deprecated Use V2
 */
@Deprecated
@RunWith(MockitoJUnitRunner.class)
public class DrawdownDtoServiceTest {

    @InjectMocks
    private DrawdownDtoServiceImpl drawdownDtoService;

    @Mock
    private AvaloqDrawdownIntegrationServiceImpl drawdownService;

    private Drawdown drawdown;

    @Before
    public void setUp() throws Exception {
        drawdown = new DrawdownImpl(AccountKey.valueOf("186648"), DrawdownOption.PRORATA);
    }

    @Test
    public void testGetDrawdown() {
        Mockito.when(drawdownService.getDrawDownOption(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(drawdown);
        DrawdownDto drawdownDto = drawdownDtoService.find(new com.bt.nextgen.api.account.v2.model.AccountKey(
                "FC622CC21BFFE81A42F70BB718A6A91D1D9470BB4FB2631D"), new ServiceErrorsImpl());
        Assert.assertEquals("prorata", drawdownDto.getDrawdownType());
    }

    @Test
    public void testUpdateDrawdown() {
        DrawdownDto drawdownDto = new DrawdownDto(new com.bt.nextgen.api.account.v2.model.AccountKey(
                "FC622CC21BFFE81A42F70BB718A6A91D1D9470BB4FB2631D"), "prorata");
        drawdownDtoService.update(drawdownDto, new ServiceErrorsImpl());
    }
}
