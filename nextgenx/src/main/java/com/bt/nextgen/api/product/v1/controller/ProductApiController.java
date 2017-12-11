package com.bt.nextgen.api.product.v1.controller;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.product.v1.model.BrokerProductDocumentDto;
import com.bt.nextgen.api.product.v1.model.ProductDto;
import com.bt.nextgen.api.product.v1.model.ProductKey;
import com.bt.nextgen.api.product.v1.service.AccountProductDocumentDtoService;
import com.bt.nextgen.api.product.v1.service.BrokerProductDocumentDtoService;
import com.bt.nextgen.api.product.v1.service.ProductDtoService;
import com.bt.nextgen.api.product.v1.service.ProductSearchDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.BeanFilter;
import com.bt.nextgen.core.api.operation.BeanFilter.Strictness;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.FindOne;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Controller("ProductApiControllerV1")
@RequestMapping(produces = "application/json")
@Api(value = "Provides services to support finding product and document tag information.")
public class ProductApiController {
    @Autowired
    private ProductDtoService productDtoService;

    @Autowired
    private ProductSearchDtoService productSearchDtoService;

    @Autowired
    private BrokerProductDocumentDtoService brokerProductDocumentDtoService;

    @Autowired
    private AccountProductDocumentDtoService accountProductDocumentDtoService;

    @Value("${api.product.v1.version}")
    private String version;

    @RequestMapping(method = RequestMethod.GET, value = "${api.product.v1.uri.product}")
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Gets the product details for the provided productId.", response = ProductDto.class)
    public @ResponseBody ApiResponse getProduct(@ApiParam(value = "The id of the product to retrieve.",
            required = true) @PathVariable("product-id") final String productId) {
        final ProductKey key = new ProductKey(productId);
        return new FindByKey<>(version, productDtoService, key).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.product.v1.uri.products}")
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Gets all products for the dealer group of the currently authenticated user.",
            response = ProductDto.class, responseContainer = "List")
    public @ResponseBody ApiResponse getProducts() {
        return new FindAll<>(version, productDtoService).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.product.v1.uri.clientsProducts}")
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Gets all products for the clients of the currently authenticated user(adviser).",
            response = ProductDto.class, responseContainer = "List")
    public @ResponseBody ApiResponse getClientListProducts() {
        return new FindAll<>(version, productSearchDtoService).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.product.v1.uri.adviserProducts}")
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(
            value = "Gets all active White Label products for the provided adviserId optionally filtered by the provided productCategory .",
            response = ProductDto.class, responseContainer = "List")
    public @ResponseBody ApiResponse getAdviserProducts(
            @ApiParam(
                    value = "The id of the adviser for which to find products.") @PathVariable("adviser-id") final String adviserId,
            @ApiParam(value = "Optional product category to filter by.",
                    required = false) @RequestParam(required = false) final String productCategory) {
        final ApiSearchCriteria withPositionId = new ApiSearchCriteria("positionId", adviserId);
        final SearchByCriteria<ProductDto> searchByCriteria = new SearchByCriteria<>(version, productDtoService, withPositionId);
        final List<ApiSearchCriteria> filters = new ArrayList<>();
        filters.add(new ApiSearchCriteria("productLevel", ProductLevel.WHITE_LABEL.name()));
        filters.add(new ApiSearchCriteria("isActive", "true"));
        if (hasText(productCategory)) {
            filters.add(new ApiSearchCriteria("productCategory", productCategory));
        }
        return new BeanFilter(version, searchByCriteria, Strictness.ALL, filters).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.product.v1.uri.clientProducts}")
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Gets all products for the provided clientId optionally filtered by the provided filter.",
            response = ProductDto.class, responseContainer = "List")
    public @ResponseBody ApiResponse getClientProducts(
            @ApiParam(
                    value = "The id of the client for which to find products.") @PathVariable("client-id") final String clientId,
            @ApiParam(value = "Optional filter attributes to filter by.", required = false) @RequestParam(required = false,
                    value = "filter") final String filter) {
        final ApiSearchCriteria criteria = new ApiSearchCriteria("dealerGroupId", clientId);
        return new BeanFilter(version, new SearchByCriteria<>(version, productDtoService, criteria), Strictness.ANY, filter)
                .performOperation();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = "${api.product.v1.uri.documents}")
    @ApiOperation(
            value = "Gets all product ids, brand ids and document tags for the dealer group of the currently authenticated user.",
            response = BrokerProductDocumentDto.class)
    public @ResponseBody ApiResponse getBrokerProductDocuments() {
        return new FindAll<>(version, brokerProductDocumentDtoService).performOperation();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = "${api.product.v1.uri.account.documents}")
    @ApiOperation(value = "Gets all product ids, brand ids and document tags for the provided accountId.",
            response = BrokerProductDocumentDto.class)
    public @ResponseBody ApiResponse getAccountProductDocuments(@ApiParam(
            value = "The id of the account for which to find product and document information.") @PathVariable("account-id") final String accountId) {
        return new FindByKey<>(version, accountProductDocumentDtoService, new AccountKey(accountId)).performOperation();
    }
}
