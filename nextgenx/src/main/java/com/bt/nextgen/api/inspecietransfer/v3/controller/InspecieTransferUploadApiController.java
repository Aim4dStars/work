package com.bt.nextgen.api.inspecietransfer.v3.controller;

import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferKey;
import com.bt.nextgen.api.inspecietransfer.v3.service.InspecieTransferDtoService;
import com.bt.nextgen.api.inspecietransfer.v3.service.TaxParcelIndependentUploadServiceImpl;
import com.bt.nextgen.api.inspecietransfer.v3.service.TaxParcelUploadService;
import com.bt.nextgen.api.inspecietransfer.v3.validation.InspecieTransferDtoErrorMapper;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.config.SecureJsonObjectMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.Validate;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.google.json.JsonSanitizer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

@Controller("InspecieTransferUploadApiControllerV3")
@RequestMapping(produces = "application/json")
@Api(description = "Contains endpoints for uploading Excel files of asset transfer details")
public class InspecieTransferUploadApiController {

    @Autowired
    private UserProfileService profileService;

    @Autowired
    @Qualifier("InspecieTransferDtoServiceV3")
    private InspecieTransferDtoService transferBundleService;

    @Autowired
    @Qualifier("InspecieTransferDtoServiceV2")
    private com.bt.nextgen.api.inspecietransfer.v2.service.InspecieTransferDtoService transferMassSettleService;

    @Autowired
    private InspecieTransferDtoErrorMapper inspecieTransferDtoErrorMapper;

    @Autowired
    private TaxParcelUploadService uploadService;

    @Autowired
    private TaxParcelIndependentUploadServiceImpl independentUploadService;

    @Autowired
    private SecureJsonObjectMapper mapper;

    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.inspecie.transfer.submit')")
    @ApiOperation(value = "Upload and validate transfer details from excel file", response = InspecieTransferDto.class)
    @RequestMapping(method = RequestMethod.POST, value = "${upload.inspecietransfer.v3.uri.taxparcels}")
    public @ResponseBody
    ApiResponse uploadTaxParcel(
            @PathVariable("account-id") @ApiParam(value = "ID of account making the transfer", required = true) String accountId,
            @RequestParam(value = "transferData", required = true) @ApiParam(value = "InspecieTransferDtoV3 in json format", required = true) String transferDetails,
            @RequestParam(value = "sponsorpid", required = false) @ApiParam(value = "CHESS sponsor ID", required = false) String sponsorPid,
            @RequestParam(value = "sponsorname", required = false) @ApiParam(value = "CHESS sponsor name", required = false) String sponsorName,
            @RequestPart(value = "upload-file", required = true) @ApiParam(value = "File to be uploaded", required = true) MultipartFile file)
            throws IOException {

        if (!profileService.isEmulating()) {
            InspecieTransferDto transferDto = mapper.readerWithView(JsonViews.Write.class).forType(InspecieTransferDtoImpl.class)
                    .readValue(JsonSanitizer.sanitize(transferDetails));

            // Front-end vetting to ensure that file is correct format etc.
            transferDto = uploadService.validateFile(transferDto, sponsorPid, sponsorName, file);
            if (transferDto.containsValidationWarningOnly()) {

                // Avaloq validation of transferDto
                KeyedApiResponse<InspecieTransferKey> response = new Validate<>(ApiVersion.CURRENT_VERSION,
                        transferBundleService, inspecieTransferDtoErrorMapper, transferDto).performOperation();
                InspecieTransferDtoImpl responseDto = (InspecieTransferDtoImpl) response.getData();

                // Combine any errors from Avaloq with that from UI-validate and return original object.
                transferDto.getWarnings().addAll(responseDto.getWarnings());
            }

            InspecieTransferKey key = new InspecieTransferKey(accountId, null);
            return new KeyedApiResponse<InspecieTransferKey>(ApiVersion.CURRENT_VERSION, key, transferDto);
        }
        throw new AccessDeniedException("Access Denied");
    }

    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.inspecie.transfer.submit')")
    @ApiOperation(value = "Upload tax parcel details for existing transfer", response = com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto.class)
    @RequestMapping(method = RequestMethod.POST, value = "${upload.inspecietransfer.v3.uri.taxparcels-independent}")
    public @ResponseBody
    ApiResponse uploadIndependentTaxParcel(
            @PathVariable("account-id") @ApiParam(value = "ID of account having tax parcels uploaded", required = true) String accountId,
            @PathVariable("transfer-id") @ApiParam(value = "ID of existing transfer", required = true) String transferId,
            @RequestPart(value = "upload-file", required = true) @ApiParam(value = "File to be uploaded", required = true) MultipartFile file)
            throws IOException {

        // TODO: 30/11/2016 Avaloq TCI task required to create new service. Until then using existing but deprecated service.
        if (!profileService.isEmulating()) {
            // Front-end vetting to ensure that file is correct format etc.
            com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferKey key = new com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferKey(
                    accountId, transferId);
            com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto transferDto = independentUploadService.validateFile(
                    key, file);
            if (transferDto.containsValidationWarningOnly()) {
                
                // Avaloq validation of transferDto
                KeyedApiResponse<com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferKey> response = new Validate<>(
                        ApiVersion.CURRENT_VERSION, transferMassSettleService, inspecieTransferDtoErrorMapper, transferDto)
                        .performOperation();
                com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto responseDto = (com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto) response
                        .getData();

                // Combine any errors from Avaloq with that from UI-validate and return original object.
                transferDto.getWarnings().addAll(responseDto.getWarnings());
            }
            return new KeyedApiResponse<com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferKey>(
                    ApiVersion.CURRENT_VERSION, key, transferDto);
        }
        throw new AccessDeniedException("Access Denied");
    }
}
