package com.bt.nextgen.service.avaloq.ips;

import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.ips.IpsProductAssociationInterface;
import com.bt.nextgen.service.integration.product.ProductKey;

import java.util.Collections;
import java.util.List;

public class IpsProductAssociationImpl implements IpsProductAssociationInterface {
    private final IpsKey ipsKey;

    private final List<ProductKey> associatedProductList;

    public IpsProductAssociationImpl(IpsKey ipsKey, List<ProductKey> products) {
        this.ipsKey = ipsKey;
        this.associatedProductList = products;
    }

    @Override
    public IpsKey getIpsKey() {
        return ipsKey;
    }

    @Override
    public void setIpsKey(IpsKey ipsKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ProductKey> getAsscociatedProductList() {
        return Collections.unmodifiableList(associatedProductList);
    }
}
