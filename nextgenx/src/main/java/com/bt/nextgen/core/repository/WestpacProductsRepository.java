package com.bt.nextgen.core.repository;

public interface WestpacProductsRepository {

    /**
     * Load westpac product details based on a cpc
     */
    WestpacProduct load(String canonicalProductCode);
}
