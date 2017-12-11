package com.bt.nextgen.api.prm.controller;

import com.bt.nextgen.api.prm.service.PrmDtoService;
import com.bt.nextgen.core.web.model.AjaxResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PrmApiControllerTest {
    @InjectMocks
    PrmApiController prmApiController;

    @Mock
    private PrmDtoService prmDtoService;

    @Test
    public void onBoardingPrmEventTest() {
        AjaxResponse ajaxResponse = prmApiController.onBoardingPrmEvent();
        Mockito.verify(prmDtoService, Mockito.times(1)).triggerOnBoardingTwoFactorPrmEvent();
    }
}
