package com.bt.nextgen.api.prm.controller;


import com.bt.nextgen.api.prm.service.PrmDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.web.model.AjaxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("PrmApiControllerV1")
@RequestMapping(produces = "application/json")
public class PrmApiController {
    @Autowired
    private PrmDtoService prmDtoService;

    @RequestMapping(method = RequestMethod.GET, value = "${api.prm.v1.triggerTwoFactorPrmEvent.uri}")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public AjaxResponse onBoardingPrmEvent() {
        prmDtoService.triggerOnBoardingTwoFactorPrmEvent();
        return new AjaxResponse("true");
    }
}
