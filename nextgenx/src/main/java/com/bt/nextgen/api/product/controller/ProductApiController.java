package com.bt.nextgen.api.product.controller;

import com.bt.nextgen.api.product.model.ProductDto;
import com.bt.nextgen.api.product.model.ProductKey;
import com.bt.nextgen.api.product.service.ProductDocumentsDtoService;
import com.bt.nextgen.api.product.service.ProductDtoService;
import com.bt.nextgen.api.product.service.ProductSearchDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.BeanFilter;
import com.bt.nextgen.core.api.operation.BeanFilter.Strictness;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.FindOne;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import static com.bt.nextgen.core.api.ApiVersion.CURRENT_VERSION;
import static org.springframework.util.StringUtils.hasText;

@Deprecated
@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class ProductApiController
{
	@Autowired
	private ProductDtoService productDtoService;

    @Autowired
    private ProductSearchDtoService productSearchDtoService;

	@Autowired
	private ProductDocumentsDtoService productDocumentsDtoService;

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.PRODUCT)
	public @ResponseBody
	ApiResponse getProduct(@PathVariable(UriMappingConstants.PRODUCT_ID_URI_MAPPING) String productId) throws Exception
	{
		final ProductKey key = new ProductKey(productId);
		return new FindByKey <>(CURRENT_VERSION, productDtoService, key).performOperation();
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.PRODUCTS)
	public @ResponseBody
	ApiResponse getProduct() throws Exception
	{
		return new FindAll <>(CURRENT_VERSION, productDtoService).performOperation();
	}

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.CLIENT_LIST_PRODUCTS)
    public @ResponseBody
    ApiResponse getClientListProducts()
    {
        return new FindAll <>(CURRENT_VERSION, productSearchDtoService).performOperation();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ADVISER_PRODUCTS)
    public
    @ResponseBody
    ApiResponse getAdviserProducts(@PathVariable(UriMappingConstants.ADVISER_ID_URI_MAPPING) String positionId,
            @RequestParam(required = false) String productCategory) throws Exception {
        final ApiSearchCriteria withPositionId = new ApiSearchCriteria("positionId", positionId);
        final SearchByCriteria<ProductDto> searchByCriteria = new SearchByCriteria<>(CURRENT_VERSION, productDtoService, withPositionId);
        final List<ApiSearchCriteria> filters = new ArrayList<>();
        filters.add(new ApiSearchCriteria("productLevel", ProductLevel.WHITE_LABEL.name()));
        filters.add(new ApiSearchCriteria("isActive", "true"));
        if (hasText(productCategory)) {
            filters.add(new ApiSearchCriteria("productCategory", productCategory));
        }
        final BeanFilter beanFilter = new BeanFilter(CURRENT_VERSION, searchByCriteria, Strictness.ALL, filters);
        return beanFilter.performOperation();
    }

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.CLIENT_PRODUCTS)
	public @ResponseBody
	ApiResponse getProducts(@PathVariable(UriMappingConstants.CLIENT_ID_URI_MAPPING) String clientId,
		@RequestParam(required = false, value = "filter") String filter) throws Exception
	{
		ApiSearchCriteria criteria = new ApiSearchCriteria("dealerGroupId", clientId);
		return new BeanFilter(CURRENT_VERSION, new SearchByCriteria <>(CURRENT_VERSION,
			productDtoService, criteria), Strictness.ANY, filter).performOperation();
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.PRODUCT_DOCS)
	public @ResponseBody
	ApiResponse getProductDocuments() throws Exception
	{
		return new FindOne<>(CURRENT_VERSION, productDocumentsDtoService).performOperation();
	}
}
