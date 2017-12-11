package com.bt.nextgen.api.product.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDto extends BaseDto implements KeyedDto<ProductKey> {
    private ProductKey key;
    private String productType;
    private boolean active;
    private String productName;
    private String parentProduct;
    private String parentProductName;
    private String productABN;
    private String productUsi;
    private String shortName;
    private BigDecimal minIntialInvestment;
    private BigDecimal minContribution;
    private BigDecimal minWithdrwal;
    private List<ProductFeeComponentDto> feeComponents;

    private ProductKey parentProductKey;
    private String productLevel;
    private ProductCategory productCategory;
    private BigDecimal investmentBuffer;

    public ProductKey getParentProductKey() {
        return parentProductKey;
    }

    public void setParentProductKey(final ProductKey parentProductKey) {
        this.parentProductKey = parentProductKey;
    }

    public String getProductLevel() {
        return productLevel;
    }

    public void setProductLevel(final String productLevel) {
        this.productLevel = productLevel;
    }

    @Override
    public ProductKey getKey() {
        return key;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(final String productType) {
        this.productType = productType;
    }

    public boolean isActive() {
        return active;
    }

    // TODO change the variable name to active [BeanFilter is expecting isIsActive/getIsActive ]
    public boolean getIsActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(final String productName) {
        this.productName = productName;
    }

    public String getParentProduct() {
        return parentProduct;
    }

    public void setParentProduct(final String parentProduct) {
        this.parentProduct = parentProduct;
    }

    public String getProductABN() {
        return productABN;
    }

    public void setProductABN(String productABN) {
        this.productABN = productABN;
    }

    public String getProductUsi() {
        return productUsi;
    }

    public void setProductUsi(String productUsi) {
        this.productUsi = productUsi;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(final String shortName) {
        this.shortName = shortName;
    }

    public BigDecimal getMinIntialInvestment() {
        return minIntialInvestment;
    }

    public void setMinIntialInvestment(final BigDecimal minIntialInvestment) {
        this.minIntialInvestment = minIntialInvestment;
    }

    public BigDecimal getMinContribution() {
        return minContribution;
    }

    public void setMinContribution(final BigDecimal minContribution) {
        this.minContribution = minContribution;
    }

    public BigDecimal getMinWithdrwal() {
        return minWithdrwal;
    }

    public void setMinWithdrwal(final BigDecimal minWithdrwal) {
        this.minWithdrwal = minWithdrwal;
    }

    public List<ProductFeeComponentDto> getFeeComponents() {
        return feeComponents;
    }

    public void setFeeComponents(final List<ProductFeeComponentDto> feeComponents) {
        this.feeComponents = feeComponents;
    }

    public void setKey(final ProductKey key) {
        this.key = key;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(final ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public BigDecimal getInvestmentBuffer() {
        return investmentBuffer;
    }

    public void setInvestmentBuffer(BigDecimal investmentBuffer) {
        this.investmentBuffer = investmentBuffer;
    }

    public String getParentProductName() {
        return parentProductName;
    }

    public void setParentProductName(String parentProductName) {
        this.parentProductName = parentProductName;
    }
}
