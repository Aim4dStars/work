package com.bt.nextgen.api.statements.controller;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.model.DocumentKey;
import com.bt.nextgen.api.statements.permission.DocumentPermissionService;
import com.bt.nextgen.api.statements.service.UserDocumentDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.*;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.btfin.panorama.core.security.profile.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by L081361 on 27/11/2015.
 */

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class UserDocumentApiController {

    @Autowired
    private UserProfileService profileService;

    @Autowired
    private UserDocumentDtoService userDocumentDtoService;

    @Autowired
    private DocumentPermissionService permissionService;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.USER_DOCUMENTS)
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody
    ApiResponse getuserDocumentList(
            @RequestParam(required = false, value = "filter") String filter,
            @RequestParam(required = false, value = "sortBy") String orderby,
            @RequestParam(required = false, value = "paging") String paging)
    {

        ControllerOperation operation = null;
        operation = new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, userDocumentDtoService, filter);
        if (orderby != null) {
            operation = new Sort<>(operation, orderby);
        }
        if (paging != null) {
            operation = new PageFilter<>(ApiVersion.CURRENT_VERSION, operation, paging);
        }

        return operation.performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.USER_DOCUMENT_DOWNLOAD)
    @PreAuthorize("isAuthenticated()")
    public void getDocument(@PathVariable(UriMappingConstants.DOCUMENT_ID_URI_MAPPING) String documentId,
                            HttpServletResponse response)
            throws IOException {
        DocumentKey key = new DocumentKey();
        key.setDocumentId(documentId);

        if ((profileService.isServiceOperator() && !profileService.isEmulating()) || permissionService.isDocumentUserAccessible(key.getDocumentId())) {
            DocumentDto doc = userDocumentDtoService.loadDocument(key);
            response.setContentType(doc.getFileType());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + doc.getDocumentName() + "\"");
            response.getOutputStream().write(doc.getDocumentBytes());
            response.flushBuffer();
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

}
