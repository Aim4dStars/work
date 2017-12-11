package com.bt.nextgen.api.product.service;

import com.bt.nextgen.api.product.model.ProductDto;
import com.bt.nextgen.api.product.model.ProductKey;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;

@Deprecated
public interface ProductDtoService extends FindByKeyDtoService<ProductKey, ProductDto>, FindAllDtoService<ProductDto>,
        SearchByCriteriaDtoService<ProductDto> {

}
