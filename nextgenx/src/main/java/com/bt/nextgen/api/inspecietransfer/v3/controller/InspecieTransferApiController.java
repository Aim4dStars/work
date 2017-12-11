package com.bt.nextgen.api.inspecietransfer.v3.controller;

import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferKey;
import com.bt.nextgen.api.inspecietransfer.v3.model.TransferOrderDto;
import com.bt.nextgen.api.inspecietransfer.v3.service.InspecieTransferDtoService;
import com.bt.nextgen.api.inspecietransfer.v3.service.TransferOrderDtoService;
import com.bt.nextgen.api.inspecietransfer.v3.validation.InspecieTransferDtoErrorMapper;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.config.SecureJsonObjectMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.core.api.operation.Validate;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.google.json.JsonSanitizer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Collections;

@Controller("InspecieTransferApiControllerV3")
@RequestMapping(produces = "application/json")
@Api(description = "Create and manage asset transfers")
public class InspecieTransferApiController {

    private static final Logger logger = LoggerFactory.getLogger(InspecieTransferApiController.class);

    @Autowired
    private UserProfileService profileService;

    @Autowired
    @Qualifier("InspecieTransferDtoServiceV3")
    private InspecieTransferDtoService transferService;

    @Autowired
    private InspecieTransferDtoErrorMapper inspecieTransferDtoErrorMapper;

    @Autowired
    private TransferOrderDtoService transferOrderService;

    @Autowired
    private SecureJsonObjectMapper mapper;

    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.inspecie.transfer.submit')")
    @ApiOperation(value = "Submit or validate a new transfer", response = InspecieTransferDto.class)
    @RequestMapping(method = RequestMethod.POST, value = "${api.inspecietransfer.v3.uri.transfer}", produces = "application/json")
    public @ResponseBody
    KeyedApiResponse<InspecieTransferKey> create(
            @PathVariable("account-id") @ApiParam(value = "ID of account making the transfer", required = true) String accountId,
            @RequestParam(value = "transferData", required = true) @ApiParam(value = "InspecieTransferDtoV3 in json format", required = true) String transferDetails)
            throws IOException {

        if (!profileService.isEmulating()) {
            InspecieTransferDto transferDto = mapper.readerWithView(JsonViews.Write.class).forType(InspecieTransferDtoImpl.class)
                    .readValue(JsonSanitizer.sanitize(transferDetails));
            transferDto.setKey(new InspecieTransferKey(accountId, null));

            if ("validate".equals(transferDto.getAction())) {
                logger.info("Validate inspecie-transfer request for account {} ", accountId);
                return new Validate<>(ApiVersion.CURRENT_VERSION, transferService, inspecieTransferDtoErrorMapper,
                        transferDto).performOperation();

            } else if ("submit".equals(transferDto.getAction())) {
                logger.info("Submit inspecie-transfer request for account {} ", accountId);
                return new Submit<>(ApiVersion.CURRENT_VERSION, transferService, inspecieTransferDtoErrorMapper,
                        transferDto).performOperation();
            }
        }
        throw new AccessDeniedException("Access Denied");
    }

    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    @ApiOperation(value = "Load transfer status for account", response = TransferOrderDto.class, responseContainer = "List")
    @RequestMapping(method = RequestMethod.GET, value = "${api.inspecietransfer.v3.uri.transfers}")
    public @ResponseBody
    ApiResponse search(
            @PathVariable("account-id") @ApiParam(value = "ID of account to retrieve transfers for", required = true) String accountId)
            throws IOException {
        ApiSearchCriteria criteria = new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accountId,
                OperationType.STRING);

        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, transferOrderService, Collections.singletonList(criteria))
                .performOperation();
    }
}
