package com.bt.nextgen.api.statements.controller;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.draftaccount.service.ClientApplicationDtoService;
import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.permission.DocumentPermissionService;
import com.bt.nextgen.api.statements.service.DocumentUploadDtoService;
import com.bt.nextgen.api.statements.validation.DocumentDtoValidator;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.util.StringUtil;
import com.bt.nextgen.draftaccount.repository.ClientApplicationStatus;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;

import static com.bt.nextgen.core.type.DateFormatType.DATEFORMAT_UPLOAD_OFFLINE;
import static com.bt.nextgen.core.type.DateUtil.toFormattedDate;

/**
 * This controller is used to upload files to FileNet server. All the files coming to url /secure/upload will be intercepted by
 * Webseal for virus check by ICAP server. If virus found in the request Webseal returns HTTP 403 error back to the
 * client and hence request will never make upto this point.
 */
@Controller
@RequestMapping(produces = "application/json")
public class DocumentUploadController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentUploadController.class);

    @Autowired
    private DocumentUploadDtoService documentUploadDtoService;

    @Autowired
    private DocumentDtoValidator validator;

    @Autowired
    private DocumentPermissionService permissionService;

    @Autowired
    private ClientApplicationDtoService clientApplicationDtoService;


    @Autowired
    private UserProfileService profileService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.POST, value = "/secure/upload")
    // Used for generic document library
    @ResponseBody
    public ApiResponse uploadModel(@ModelAttribute DocumentDto documentDto,
                            @RequestPart("docupload") MultipartFile file) {

        if (hasUploadPermission(documentDto)){
            return doUpload(documentDto, file);
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/secure/upload/offline")
    @PreAuthorize("isAuthenticated() and @permissionBaseService.hasBasicPermission('account.document.upload')")
    // Used for offline doc upload
    @ResponseBody
    public ApiResponse uploadOfflineModel(@ModelAttribute DocumentDto documentDto,
                                   @RequestPart("docupload") MultipartFile file) {

        documentDto.setAddedByName(profileService.getFullName());
        String docName = documentDto.getDocumentName();
        int i = docName.lastIndexOf(".");
        documentDto.setDocumentName("Offline Approval Document " + toFormattedDate(new Date(), DATEFORMAT_UPLOAD_OFFLINE) + docName.substring(i));

        ApiResponse uploadResponse = doUpload(documentDto, file);
        ClientApplicationKey clientApplicationKey = new ClientApplicationKey((Long.parseLong(documentDto.getUpdatedByID())));
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        if (uploadResponse.getError() == null && clientApplicationKey != null) {
            ClientApplicationDto clientApplicationDto = clientApplicationDtoService.find(clientApplicationKey, serviceErrors);
            clientApplicationDto.setStatus(ClientApplicationStatus.docuploaded);
            clientApplicationDtoService.update(clientApplicationDto, serviceErrors);
        }
        return uploadResponse;

    }

    private ApiResponse doUpload(DocumentDto documentDto, MultipartFile file) {
        try {
            validator.validate(documentDto);
            if (CollectionUtils.isEmpty(documentDto.getWarnings())){
                byte[] bytes = IOUtils.toByteArray(file.getInputStream());
                documentDto.setDocumentBytes(bytes);
                documentDto.setSize(BigInteger.valueOf(bytes.length));
                if (StringUtil.isNotNullorEmpty(documentDto.getKey().getDocumentId())) {
                    return new ApiResponse(ApiVersion.CURRENT_VERSION, documentUploadDtoService.uploadNewVersion(documentDto));
                } else {
                    return new ApiResponse(ApiVersion.CURRENT_VERSION, documentUploadDtoService.upload(documentDto));
                }
            } else {
                return new ApiResponse(ApiVersion.CURRENT_VERSION, documentDto);
            }
        } catch (IOException | ServiceException exception) {
            logger.error("Error while uploading file." + exception);
            ServiceErrors serviceErrors = new ServiceErrorsImpl();
            serviceErrors.addError(new ServiceErrorImpl(exception.getMessage()));
            throw new ServiceException(ApiVersion.CURRENT_VERSION, serviceErrors);
        }
    }

    private boolean hasUploadPermission(DocumentDto dto){
        if (StringUtil.isNotNullorEmpty(dto.getKey().getDocumentId()))
            return permissionService.hasUploadNewPermission(dto);
        else
            return permissionService.hasUploadPermission(dto);
    }
}
