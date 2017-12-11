package com.bt.nextgen.api.product.model;

import com.bt.nextgen.api.broker.model.BrokerKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

@Deprecated
public class ProductDocumentDto extends BaseDto implements KeyedDto<BrokerKey>
{
	private BrokerKey key;
	private List<String> productList;
	private List<String> brandList;
	private boolean managedFundAvailable;

	public ProductDocumentDto()
	{
	}

	public ProductDocumentDto(BrokerKey key)
	{
		this.key = key;
	}

	public List<String> getProductList()
	{
		return productList;
	}

	public void setProductList(List<String> productList)
	{
		this.productList = productList;
	}

	public List<String> getBrandList()
	{
		return brandList;
	}

	public void setBrandList(List<String> brandList)
	{
		this.brandList = brandList;
	}

	@Override public BrokerKey getKey()
	{
		return key;
	}

	public void setKey(BrokerKey key)
	{
		this.key = key;
	}

	public boolean isManagedFundAvailable() {
		return managedFundAvailable;
	}

	public void setManagedFundAvailable(boolean managedFundAvailable) {
		this.managedFundAvailable = managedFundAvailable;
	}
}