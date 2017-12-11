package com.bt.nextgen.api.statements.controller;

import com.bt.nextgen.api.statements.model.DeleteDto;
import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.model.DocumentKey;
import com.bt.nextgen.api.statements.permission.DocumentPermissionService;
import com.bt.nextgen.api.statements.service.DocumentDtoService;
import com.bt.nextgen.api.statements.validation.DocumentDtoServiceErrorMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.api.operation.*;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.btfin.panorama.core.security.profile.UserProfileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This controller is used to -
 * 1) fetch documents from the FileNet according to the mentioned criteria
 * 2)fetch  all the documents from the FileNet of a perticular account
 * 3) get all versions of a perticular document
 * 4) update a document properties
 */
@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class DocumentApiController {

    @Autowired
    private DocumentDtoServiceErrorMapper documentDtoServiceErrorMapper;

    @Autowired
    private DocumentDtoService documentDtoService;

    @Autowired
    private DocumentPermissionService permissionService;

    @Autowired
    private UserProfileService profileService;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ACCOUNT_DOCUMENTS)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    ApiResponse getAccountDocuments(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
                                    @RequestParam(required = false, value = "query") String queryString,
                                    @RequestParam(required = false, value = "filter") String filter,
                                    @RequestParam(required = false, value = "sortby") String orderby,
                                    @RequestParam(required = false, value = "paging") String paging) {
        if ((profileService.isServiceOperator() && !profileService.isEmulating()) || permissionService.hasAccountViewPermission(accountId)) {
            DocumentKey key = new DocumentKey();
            key.setAccountId(accountId);
            ControllerOperation operation = null;
            if (StringUtils.isEmpty(queryString)) {
                operation = new SearchByPartialKeyCriteria<>(ApiVersion.CURRENT_VERSION, documentDtoService, key, filter);
            } else {
                operation = new ServiceFilterByKey<>(ApiVersion.CURRENT_VERSION, documentDtoService, queryString, key, filter);
            }
            if (orderby != null) {
                operation = new Sort<>(operation, orderby);
            }
            if (paging != null) {
                operation = new PageFilter<>(ApiVersion.CURRENT_VERSION, operation, paging);
            }
            return operation.performOperation();
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.UPDATE_DOCUMENT)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    ApiResponse updateDocumentInfo(@PathVariable(UriMappingConstants.DOCUMENT_ID_URI_MAPPING) String documentId,
                                   @ModelAttribute DocumentDto documentDto) {
        documentDto.getKey().setDocumentId(documentId);
        if (permissionService.hasUpdatePermission(documentDto)) {
            return new Update<>(ApiVersion.CURRENT_VERSION, documentDtoService, documentDtoServiceErrorMapper, documentDto)
                    .performOperation();
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }


    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.DOCUMENT_VERSIONS)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    ApiResponse getDocumentVersion(@PathVariable(UriMappingConstants.DOCUMENT_ID_URI_MAPPING) String documentId)
            throws IOException {
        if (profileService.isServiceOperator()) {
            DocumentKey key = new DocumentKey();
            key.setDocumentId(documentId);
            ResultListDto<DocumentDto> listDto = new ResultListDto(documentDtoService.getVersions(key));
            return new ApiResponse(ApiVersion.CURRENT_VERSION, listDto);
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.DOCUMENT)
    @PreAuthorize("isAuthenticated()")
    public void getDocument(@PathVariable(UriMappingConstants.DOCUMENT_ID_URI_MAPPING) String documentId,
                            HttpServletResponse response)
            throws IOException {
        DocumentKey key = new DocumentKey();
        key.setDocumentId(documentId);
        if ((profileService.isServiceOperator() && !profileService.isEmulating()) || permissionService.isDocumentAccessible(key.getDocumentId())) {
            DocumentDto doc = documentDtoService.loadDocument(key);
            response.setContentType(doc.getFileType());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + doc.getDocumentName() + "\"");
            response.getOutputStream().write(doc.getDocumentBytes());
            response.flushBuffer();
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.DOCUMENTS_DOWNLOAD)
    @PreAuthorize("isAuthenticated()")
    public void getDocuments(@RequestParam(required = true, value = "documentid") String queryString,
                             HttpServletResponse response)
            throws IOException {
        String[] documentIds = queryString.split(",");
        if (documentIds == null || documentIds.length < 1) {
            throw new BadRequestException("No document found.");
        }
        if ((profileService.isServiceOperator() && !profileService.isEmulating()) || permissionService.isDocumentAccessible(documentIds)) {
            DocumentDto dto = documentDtoService.loadDocuments(documentIds);
            response.setContentType(dto.getFileExtension());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + dto.getFileName());
            response.getOutputStream().write(dto.getDocumentBytes());
            response.flushBuffer();
        } else {
            throw new AccessDeniedException("Access Denied");
        }

    }

    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.DELETE_DOCUMENT)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    ApiResponse deleteDocument(@PathVariable(UriMappingConstants.DOCUMENT_ID_URI_MAPPING) String documentId)
            throws IOException {
        DocumentKey key = new DocumentKey();
        key.setDocumentId(documentId);
        if (permissionService.hasDeletePermission(key)) {
            final boolean isDeleted = documentDtoService.softDeleteDocument(key);
            DeleteDto dto = new DeleteDto();
            dto.setSuccess(isDeleted);
            return new ApiResponse(ApiVersion.CURRENT_VERSION, dto);
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }


}