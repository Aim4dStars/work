package com.bt.nextgen.service.integration.ips;

import com.bt.nextgen.service.integration.product.ProductKey;

import java.util.List;

public interface IpsProductAssociationInterface extends IpsIdentifier
{
	public List <ProductKey> getAsscociatedProductList();

}
