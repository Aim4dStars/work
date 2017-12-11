package com.bt.nextgen.api.supermatch.v1.controller;


import com.bt.nextgen.api.supermatch.v1.model.SuperMatchDto;
import com.bt.nextgen.api.supermatch.v1.model.SuperMatchDtoKey;
import com.bt.nextgen.api.supermatch.v1.service.SuperMatchDtoService;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.config.SecureJsonObjectMapper;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByPartialKey;
import com.bt.nextgen.core.api.operation.Update;
import com.bt.nextgen.core.web.model.AjaxResponse;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.json.JsonSanitizer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

import static com.bt.nextgen.core.api.ApiVersion.CURRENT_VERSION;

/**
 * Controller to fetch super fund details from ECO
 */
@Controller("SuperMatchApiControllerV1")
@RequestMapping(produces = "application/json")
@Api(value = "Provides services to retrieve/update supermatch details.")
public class SuperMatchApiController {

    @Autowired
    private SuperMatchDtoService superMatchDtoService;

    /**
     * Retrieves super fund details from ECO for an account
     *
     * @param accountId - account identifier
     */
    @ApiOperation(value = "Retrieves super fund details from ECO for an account", response = SuperMatchDto.class)
    @RequestMapping(method = RequestMethod.GET, value = "${api.supermatch.v1.uri.account}")
    @ResponseBody
    public ApiResponse getSuperDetailsForAccount(@PathVariable("account-id") String accountId) {
        return new FindByPartialKey<>(CURRENT_VERSION, superMatchDtoService, new SuperMatchDtoKey(accountId)).performOperation();
    }

    /**
     * Updates the Supermatch details - consent , acknowledgement, rollover funds, create member details
     *
     * @param accountId      - account identifier
     * @param updateId       - update type {consent, acknowledge, rollover, create} See: {@link com.bt.nextgen.api.supermatch.v1.model.UpdateType}
     * @param superMatchJson - {@link SuperMatchDto} with the request values
     */
    @ApiOperation(value = "Updates the Supermatch details - consent , acknowledgement, rollover funds, create member details etc., in the ECO system",
            response = SuperMatchDto.class, notes = "Allowed values for update-id are : [consent, acknowledge, rollover, create].")
    @RequestMapping(method = RequestMethod.POST, value = "${api.supermatch.v1.uri.account.update}", consumes = "application/json")
    @ResponseBody
    public ApiResponse updateSuperMatchDetails(@PathVariable("account-id") String accountId,
                                               @PathVariable("update-id") String updateId,
                                               @RequestBody(required = false) String superMatchJson) throws IOException {

        final String sanitizedSuperMatchJson = JsonSanitizer.sanitize(superMatchJson);
        final SuperMatchDto superMatch = new SecureJsonObjectMapper().readerWithView(JsonViews.Write.class)
                .forType(new TypeReference<SuperMatchDto>() {
                }).readValue(sanitizedSuperMatchJson);

        final SuperMatchDto requestSuperMatch = superMatch != null ? superMatch : new SuperMatchDto();
        requestSuperMatch.setKey(new SuperMatchDtoKey(accountId, updateId));
        return new Update<>(CURRENT_VERSION, superMatchDtoService, null, requestSuperMatch).performOperation();
    }

    /**
     * Triggers a request to send the SG(Super Guarantee) letter to the user
     *
     * @param accountId    - account identifier
     * @param emailAddress - email address to send the SG letter to
     */
    @ApiOperation(value = "Triggers a request to send the SG(Super Guarantee) letter to the user", response = Boolean.class,
            notes = "This call just triggers a notification in ECO to generate and email the SG letter. UI doesn't need to care for it's response ")
    @RequestMapping(method = RequestMethod.POST, value = "${api.supermatch.v1.uri.account.notify}")
    @ResponseBody
    public AjaxResponse notifyCustomer(@PathVariable("account-id") String accountId,
                                       @RequestParam("email") String emailAddress) {
        return new AjaxResponse(superMatchDtoService.notifyCustomer(accountId, emailAddress, new FailFastErrorsImpl()), null);
    }
}
