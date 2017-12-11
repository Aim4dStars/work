package com.bt.nextgen.service.avaloq.ips;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.client.ProductKeyConverter;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.product.ProductKey;

@ServiceBean(xpath = "prod_head")
public class IpsProductImpl {
    @ServiceElement(xpath = "ips_id/val", converter = IpsKeyConverter.class)
    private IpsKey ipsKey;

    @ServiceElement(xpath = "prod_id/val", converter = ProductKeyConverter.class)
    private ProductKey productKey;

    public IpsKey getIpsKey() {
        return ipsKey;
    }

    public ProductKey getProductKey() {
        return productKey;
    }

    public void setIpsKey(IpsKey ipsKey) {
        this.ipsKey = ipsKey;
    }

    public void setProductKey(ProductKey productKey) {
        this.productKey = productKey;
    }

}
