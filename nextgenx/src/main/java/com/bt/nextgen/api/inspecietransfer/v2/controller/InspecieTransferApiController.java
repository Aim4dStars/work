package com.bt.nextgen.api.inspecietransfer.v2.controller;

import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferKey;
import com.bt.nextgen.api.inspecietransfer.v2.service.InspecieTransferDtoService;
import com.bt.nextgen.api.inspecietransfer.v2.service.TransferOrderDtoService;
import com.bt.nextgen.api.inspecietransfer.v2.util.TaxParcelUploadUtil;
import com.bt.nextgen.api.inspecietransfer.v2.validation.InspecieTransferDtoErrorMapper;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.core.api.operation.Validate;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;

/**
 * @deprecated Use V3
 */
@Deprecated
@Controller("InspecieTransferApiControllerV2")
@RequestMapping(produces = "application/json")
public class InspecieTransferApiController {

    private static final Logger logger = LoggerFactory.getLogger(InspecieTransferApiController.class);

    @Autowired
    private UserProfileService profileService;

    @Autowired
    private InspecieTransferDtoService inspecieTransferService;

    @Autowired
    private TaxParcelUploadUtil taxParcelUploadUtil;

    @Autowired
    private InspecieTransferDtoErrorMapper inspecieTransferDtoErrorMapper;

    @Autowired
    private TransferOrderDtoService transferOrderService;

    @Autowired
    private JsonObjectMapper mapper;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = "${api.inspecietransfer.v2.uri.transfer}")
    public @ResponseBody
    KeyedApiResponse<InspecieTransferKey> getInspecieTransfer(@PathVariable("account-id") String accountId,
            @PathVariable("transfer-id") String transferId) {
        InspecieTransferKey key = new InspecieTransferKey();
        key.setAccountId(accountId);
        key.setTransferId(transferId);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, inspecieTransferService, key).performOperation();
    }

    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.inspecie.transfer.submit')")
    @RequestMapping(method = RequestMethod.POST, value = "${api.inspecietransfer.v2.uri.creation}")
    public @ResponseBody
    KeyedApiResponse<InspecieTransferKey> create(@PathVariable("account-id") String accountId,
            @RequestParam(value = "transferDetails", required = true) String transferDetails,
            @RequestParam(value = "x-ro-validate-only", required = false) String validateOnly) throws IOException {
        if (!profileService.isEmulating()) {

            InspecieTransferDto transferDto = mapper.readValue(transferDetails, InspecieTransferDtoImpl.class);
            transferDto.getKey().setAccountId(accountId);
            if ("true".equals(validateOnly)) {
                logger.info("Validate inspecie-transfer request for account {} ", accountId);
                return new Validate<>(ApiVersion.CURRENT_VERSION, inspecieTransferService, inspecieTransferDtoErrorMapper,
                        transferDto).performOperation();
            } else if ("submit".equals(transferDto.getAction())) {
                logger.info("Submit inspecie-transfer request for account {} ", accountId);
                return new Submit<>(ApiVersion.CURRENT_VERSION, inspecieTransferService, inspecieTransferDtoErrorMapper,
                        transferDto).performOperation();
            } else {
                throw new AccessDeniedException("Access Denied");
            }

        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.inspecie.transfer.submit')")
    @RequestMapping(method = RequestMethod.POST, value = "${api.inspecietransfer.v2.uri.taxparcel.upload}")
    public @ResponseBody
    ApiResponse uploadTaxParcel(@PathVariable("account-id") String accountId, @PathVariable("transfer-id") String transferId,
            @RequestPart("upload-file") MultipartFile file) throws IOException {

        if (!profileService.isEmulating()) {
            InspecieTransferKey key = new InspecieTransferKey();
            key.setAccountId(accountId);
            key.setTransferId(transferId);
            InspecieTransferDto transferDto = taxParcelUploadUtil.parseFile(file, (InspecieTransferDtoImpl) new FindByKey<>(
                    ApiVersion.CURRENT_VERSION, inspecieTransferService, key).performOperation().getData());
            if (transferDto.getWarnings().isEmpty()) {
                return new Submit<>(ApiVersion.CURRENT_VERSION, inspecieTransferService, inspecieTransferDtoErrorMapper,
                        transferDto).performOperation();
            } else {
                return new KeyedApiResponse<InspecieTransferKey>(ApiVersion.CURRENT_VERSION, key, transferDto);
            }

        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = "${api.inspecietransfer.v2.uri.transfers}")
    public @ResponseBody
    ApiResponse search(@PathVariable("account-id") String accountId) throws IOException {
        ApiSearchCriteria criteria = new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accountId,
                OperationType.STRING);

        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, transferOrderService, Collections.singletonList(criteria))
                .performOperation();
    }
}
