package com.bt.nextgen.api.account.v1.controller.transitions;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.account.v1.service.AssetTransferService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by L069552 on 28/09/2015.
 */
@Deprecated
@Controller
@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class AssetTransferApiController {


    @Autowired
    @Qualifier("assetTransferService")
    private AssetTransferService assetTransferDtoService;

    /**
     * Request mapping for accessing asset transfer status for a bp id
     * @param accountId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ASSET_TRANSFER_STATUS)
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody
    ApiResponse getAssetTransferStatus(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId) {
        List<ApiSearchCriteria> criteria = new ArrayList<ApiSearchCriteria>();
        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS, accountId, ApiSearchCriteria.OperationType.STRING));

        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, assetTransferDtoService, criteria).performOperation();
    }
}
