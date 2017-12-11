package com.bt.nextgen.api.product.v1.controller;

import com.bt.nextgen.api.product.v1.model.ProductDto;
import com.bt.nextgen.api.product.v1.service.ProductFeeService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.btfin.panorama.core.security.encryption.EncodedString.toPlainText;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller("ProductFeeApiControllerV1")
@RequestMapping(produces = "application/json")
@Api(value = "Provides services to support finding fees related to the an adviser's product.")
public class ProductFeeApiController {

    @Autowired
    private ProductFeeService productFeeService;

    @Value("${api.product.v1.version}")
    private String version;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = GET, value = "${api.product.v1.uri.adviserProductFee}")
    @ApiOperation(value = "Gets the adviser/product fee information for the provided adviserId and productId.",
            response = ProductDto.class)
    public @ResponseBody ApiResponse getProductFee(
            @ApiParam(
                    value = "The id of the product to find fees for.") @PathVariable("product-id") final String encodedProductId,
            @ApiParam(value = "The id of the adviser.") @PathVariable("adviser-id") final String encodedAdviserPositionId) {
        final String adviserPositionId = toPlainText(encodedAdviserPositionId);
        final String productId = toPlainText(encodedProductId);
        return new ApiResponse(version, productFeeService.findProductFee(adviserPositionId, productId, new ServiceErrorsImpl()));
    }
}
