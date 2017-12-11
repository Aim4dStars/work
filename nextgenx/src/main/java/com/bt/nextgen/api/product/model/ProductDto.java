package com.bt.nextgen.api.product.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.math.BigDecimal;
import java.util.List;

@Deprecated
public class ProductDto extends BaseDto implements KeyedDto <ProductKey>
{
	private ProductKey key;
	private String productType;
	private boolean isActive;
	private String productName;
	private String parentProduct;
	private String parentProductName;
	private String shortName;
	private BigDecimal minIntialInvestment;
	private BigDecimal minContribution;
	private BigDecimal minWithdrwal;
	private List<ProductFeeComponentDto> feeComponents;

	private ProductKey parentProductKey;
	private String productLevel;
	private ProductCategory productCategory;

    public ProductKey getParentProductKey() {
        return parentProductKey;
    }

    public void setParentProductKey(ProductKey parentProductKey) {
        this.parentProductKey = parentProductKey;
    }

    public String getProductLevel() {
        return productLevel;
    }

    public void setProductLevel(String productLevel) {
        this.productLevel = productLevel;
    }

    @Override
	public ProductKey getKey()
	{
		return key;
	}

	public String getProductType()
	{
		return productType;
	}

	public void setProductType(String productType)
	{
		this.productType = productType;
	}

	public boolean isActive()
	{
		return isActive;
	}

    //TODO change the variable name to active [BeanFilter is expecting isIsActive/getIsActive ]
    public boolean getIsActive() {
        return isActive;
    }

    public void setActive(boolean isActive)
	{
		this.isActive = isActive;
	}

	public String getProductName()
	{
		return productName;
	}

	public void setProductName(String productName)
	{
		this.productName = productName;
	}

	public String getParentProduct()
	{
		return parentProduct;
	}

	public void setParentProduct(String parentProduct)
	{
		this.parentProduct = parentProduct;
	}

	public String getShortName()
	{
		return shortName;
	}

	public void setShortName(String shortName)
	{
		this.shortName = shortName;
	}

	public BigDecimal getMinIntialInvestment()
	{
		return minIntialInvestment;
	}

	public void setMinIntialInvestment(BigDecimal minIntialInvestment)
	{
		this.minIntialInvestment = minIntialInvestment;
	}

	public BigDecimal getMinContribution()
	{
		return minContribution;
	}

	public void setMinContribution(BigDecimal minContribution)
	{
		this.minContribution = minContribution;
	}

	public BigDecimal getMinWithdrwal()
	{
		return minWithdrwal;
	}

	public void setMinWithdrwal(BigDecimal minWithdrwal)
	{
		this.minWithdrwal = minWithdrwal;
	}

	public List <ProductFeeComponentDto> getFeeComponents()
	{
		return feeComponents;
	}

	public void setFeeComponents(List <ProductFeeComponentDto> feeComponents)
	{
		this.feeComponents = feeComponents;
	}

	public void setKey(ProductKey key)
	{
		this.key = key;
	}

	public ProductCategory getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

	public String getParentProductName() {
		return parentProductName;
	}

	public void setParentProductName(String parentProductName) {
		this.parentProductName = parentProductName;
	}
}
