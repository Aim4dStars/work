package com.bt.nextgen.api.address.controller;

import com.bt.nextgen.clients.api.model.AddressDto;
import com.bt.nextgen.clients.api.service.AddressDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class AddressApiController {

    @Autowired
    private AddressDtoService addressDtoService;

    @RequestMapping(method = RequestMethod.GET, value = "/address")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ApiResponse verifyAddress(@ModelAttribute("addressDto") AddressDto addressDto) {
        AddressDto returnedAddressDto = addressDtoService.validateAustralianAddress(addressDto);
        return new ApiResponse(ApiVersion.CURRENT_VERSION, returnedAddressDto);
    }
}
