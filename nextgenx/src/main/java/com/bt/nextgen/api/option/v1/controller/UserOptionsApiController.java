package com.bt.nextgen.api.option.v1.controller;

import com.bt.nextgen.api.option.v1.service.UserOptionDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This API is used to retrieve and accept terms and conditions for a user
 * <p/>
 * GET: secure/api/user/v1_0/terms-and-conditions/
 * <p/>
 * POST: secure/api/user/v1_0/terms-and-conditions/{tnc}/{version}
 */
@Controller("UserOptionsApiControllerV1")
public class UserOptionsApiController {

    @Autowired
    private UserOptionDtoService userOptionService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = "${api.option.v1.uri}")
    public @ResponseBody ApiResponse getOptions() {
        return new FindOne<>(ApiVersion.CURRENT_VERSION, userOptionService).performOperation();
    }

}
