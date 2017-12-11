package com.bt.nextgen.core.repository;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * This table contains details of westpac products which comes from the master product and ERT spreadsheet
 */
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "WESTPAC_PRODUCTS")
public class WestpacProduct {

    @Id
    @Column(name = "CANONICAL_PRODUCT_CODE")
    private String canonicalProductCode;

    @Column(name = "NAME")
    private String name;

    //Category of the product in the Westpac master product and ERT spreadsheet
    @Column(name = "CATEGORY")
    private String category;

    //Category of the product in the system which owns the product
    @Column(name = "CATEGORY_PRODUCT_SYSTEM")
    private String categoryProductSystem;

    //Maps to ERT-046 in the Westpac master product and ERT spreadsheet
    @Column(name = "FUNDS_TRANSFER_FROM")
    private String fundsTransferFrom;

    public String getCanonicalProductCode() {
        return canonicalProductCode;
    }

    public void setCanonicalProductCode(String canonicalProductCode) {
        this.canonicalProductCode = canonicalProductCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryProductSystem() {
        return categoryProductSystem;
    }

    public void setCategoryProductSystem(String categoryProductSystem) {
        this.categoryProductSystem = categoryProductSystem;
    }

    public String getFundsTransferFrom() {
        return fundsTransferFrom;
    }

    public void setFundsTransferFrom(String fundsTransferFrom) {
        this.fundsTransferFrom = fundsTransferFrom;
    }
}
