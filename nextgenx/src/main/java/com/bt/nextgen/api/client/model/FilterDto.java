package com.bt.nextgen.api.client.model;

import com.bt.nextgen.api.adviser.model.AdviserSearchDto;
import com.bt.nextgen.api.product.model.ProductDto;
import com.bt.nextgen.core.api.model.BaseDto;

import java.util.List;

public class FilterDto extends BaseDto {

    private List<AdviserSearchDto> advisers;
    private List<ProductDto> products;

    public FilterDto(List<AdviserSearchDto> advisers, List<ProductDto> products) {
        this.advisers = advisers;
        this.products = products;
    }

    public FilterDto() {
        this(null, null);
    }

    public List<AdviserSearchDto> getAdvisers() {
        return advisers;
    }

    public void setAdvisers(List<AdviserSearchDto> advisers) {
        this.advisers = advisers;
    }

    public List<ProductDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDto> products) {
        this.products = products;
    }
}
