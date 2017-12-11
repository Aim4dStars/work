package com.bt.nextgen.api.product.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.util.List;

/**
 * The Class UnkeyedProductDocumentDto.
 */
public class UnkeyedProductDocumentDto extends BaseDto {

    /** The list of products available. */
    private final List<ProductDto> productList;

    /** The list of brands available. */
    private final List<String> brandList;
    /**
     * The list of document tags to be used a a singular field when determining which CMS documents are relevant to display, this
     * will be an aggregation of brands and products as well as any other tags defined in the CMS e.g. JobRole (Investor, Adviser
     * etc.)
     */
    private final List<String> documentTags;

    /** Flag to indicate whether managed fund available. */
    private final boolean managedFundAvailable;

    /**
     * Instantiates a new product document dto.
     *
     * @param productList
     *            the product list
     * @param brandList
     *            the brand list
     * @param documentTags
     *            the document tags
     * @param managedFundAvailable
     *            the managed fund available
     */
    public UnkeyedProductDocumentDto(final List<ProductDto> productList, final List<String> brandList,
            final List<String> documentTags, final boolean managedFundAvailable) {
        super();
        this.productList = productList;
        this.brandList = brandList;
        this.documentTags = documentTags;
        this.managedFundAvailable = managedFundAvailable;
    }

    /**
     * Gets the product list.
     *
     * @return the product list
     */
    public List<ProductDto> getProductList() {
        return productList;
    }

    /**
     * Gets the brand list.
     *
     * @return the brand list
     */
    public List<String> getBrandList() {
        return brandList;
    }

    /**
     * Checks if is managed fund available.
     *
     * @return true, if is managed fund available
     */
    public boolean isManagedFundAvailable() {
        return managedFundAvailable;
    }

    /**
     * Gets the document tags.
     *
     * @return the document tags
     */
    public List<String> getDocumentTags() {
        return documentTags;
    }
}
