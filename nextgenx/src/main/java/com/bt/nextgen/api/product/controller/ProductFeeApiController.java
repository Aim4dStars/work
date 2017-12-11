package com.bt.nextgen.api.product.controller;

import com.bt.nextgen.api.product.service.ProductFeeService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.bt.nextgen.core.api.ApiVersion.CURRENT_VERSION;
import static com.bt.nextgen.core.api.UriMappingConstants.ADVISER_ID_URI_MAPPING;
import static com.bt.nextgen.core.api.UriMappingConstants.CURRENT_VERSION_API;
import static com.bt.nextgen.core.api.UriMappingConstants.PRODUCT_FEE;
import static com.bt.nextgen.core.api.UriMappingConstants.PRODUCT_ID_URI_MAPPING;
import static com.btfin.panorama.core.security.encryption.EncodedString.toPlainText;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Deprecated
@Controller
@RequestMapping(value = CURRENT_VERSION_API, produces = "application/json")
public class ProductFeeApiController {

    @Autowired
    private ProductFeeService productFeeService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = GET, value = PRODUCT_FEE)
    public
    @ResponseBody
    ApiResponse getProductFee(@PathVariable(ADVISER_ID_URI_MAPPING) String encodedAdviserPositionId,
                              @PathVariable(PRODUCT_ID_URI_MAPPING) String encodedProductId) {
        final String adviserPositionId = toPlainText(encodedAdviserPositionId);
        final String productId = toPlainText(encodedProductId);
        return new ApiResponse(CURRENT_VERSION, productFeeService.findProductFee(adviserPositionId, productId, new ServiceErrorsImpl()));
    }
}
