package com.bt.nextgen.api.chesssponsor.controller;

import com.bt.nextgen.api.chesssponsor.service.ChessSponsorDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.bt.nextgen.core.api.ApiVersion.CURRENT_VERSION;

/**
 * Created by L078480 on 22/06/2017.
 */
@Controller
@RequestMapping(produces = "application/json")
public class ChessSponsorController {

    @Autowired
    private ChessSponsorDtoService chessSponsorDtoService;
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = "${api.chess.v1.uri.chessData}")
    public
    @ResponseBody
    ApiResponse getChessSponsorData(){
        return new ApiResponse(CURRENT_VERSION, chessSponsorDtoService.getChessSponsorData(new com.btfin.panorama.service.client.error.ServiceErrorsImpl()));
    }
}
