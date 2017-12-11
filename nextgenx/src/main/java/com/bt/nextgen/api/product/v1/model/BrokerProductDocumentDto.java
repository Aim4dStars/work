package com.bt.nextgen.api.product.v1.model;

import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

/**
 * The Class BrokerProductDocumentDto.
 */
public class BrokerProductDocumentDto extends UnkeyedProductDocumentDto implements KeyedDto<BrokerProductKey> {

    /** The dealer group broker key. */
    private final BrokerProductKey key;

    /**
     * Instantiates a new product document dto.
     *
     * @param key
     *            the key
     * @param productList
     *            the product list
     * @param brandList
     *            the brand list
     * @param documentTags
     *            the document tags
     * @param managedFundAvailable
     *            the managed fund available
     */
    public BrokerProductDocumentDto(final BrokerProductKey key, final List<ProductDto> productList, final List<String> brandList,
            final List<String> documentTags, final boolean managedFundAvailable) {
        super(productList, brandList, documentTags, managedFundAvailable);
        this.key = key;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.bt.nextgen.core.api.model.KeyedDto#getKey()
     */
    @Override
    public BrokerProductKey getKey() {
        return key;
    }

}