package com.bt.nextgen.api.beneficiary.controller;

import com.bt.nextgen.api.beneficiary.service.RelationshipTypeDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This is the controller class for getting all relationship types for beneficiary functionality.
 * Created by M035995 on 1/07/2016.
 */
@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class RelationshipApiController {

    @Autowired
    private RelationshipTypeDtoService relationshipTypeDtoService;

    /**
     * This method get all the relationship types for beneficiary functionality.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.SUPER_RELATIONSHIP_TYPES)
    @ResponseBody
    public ApiResponse getRelationshipTypes() {
        return new FindAll<>(ApiVersion.CURRENT_VERSION, relationshipTypeDtoService).performOperation();
    }

}
