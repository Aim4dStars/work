package com.bt.nextgen.api.prm.service;

import com.bt.nextgen.service.prm.service.PrmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("PrmDtoService")
public class PrmDtoServiceImpl implements PrmDtoService {

    @Autowired
    private PrmService prmService;

    @Override
    public void triggerOnBoardingTwoFactorPrmEvent() {
        prmService.triggerTwoFactorPrmEvent(null);
    }
}
