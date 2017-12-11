
package com.bt.nextgen.api.product.v1.model;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

/**
 * The Class AccountProductDocumentDto.
 */
public class AccountProductDocumentDto extends UnkeyedProductDocumentDto implements KeyedDto<AccountKey> {

    /** The key. */
    private final AccountKey key;

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
    public AccountProductDocumentDto(final AccountKey key, final List<ProductDto> productList, final List<String> brandList,
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
    public AccountKey getKey() {
        return key;
    }

}