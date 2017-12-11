package com.bt.nextgen.api.prm.service;

import com.bt.nextgen.service.prm.service.PrmService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PrmDtoServiceTest {

    @InjectMocks
    private PrmDtoServiceImpl prmDtoService;

    @Mock
    private PrmService prmService;

    @Test
    public void triggerOnBoardingTwoFactorPrmEventTest() {
        prmDtoService.triggerOnBoardingTwoFactorPrmEvent();
        Mockito.verify(prmService, Mockito.times(1)).triggerTwoFactorPrmEvent(null);
    }
}
