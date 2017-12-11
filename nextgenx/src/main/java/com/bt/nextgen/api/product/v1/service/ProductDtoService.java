package com.bt.nextgen.api.product.v1.service;

import com.bt.nextgen.api.product.v1.model.ProductDto;
import com.bt.nextgen.api.product.v1.model.ProductKey;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;

public interface ProductDtoService extends FindByKeyDtoService<ProductKey, ProductDto>, FindAllDtoService<ProductDto>,
        SearchByCriteriaDtoService<ProductDto> {

}
