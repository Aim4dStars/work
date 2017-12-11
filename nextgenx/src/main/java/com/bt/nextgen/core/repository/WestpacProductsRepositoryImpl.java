package com.bt.nextgen.core.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Repository("westpacProductsRepository")
public class WestpacProductsRepositoryImpl implements WestpacProductsRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Returns a westpac product matching the given cpc
     *
     * @param canonicalProductCode
     * @return westpac product which matches cpc
     */
    @Override
    public WestpacProduct load(String canonicalProductCode) {
        return StringUtils.isNotBlank(canonicalProductCode) ? entityManager.find(WestpacProduct.class, canonicalProductCode) : null;
    }
}
