package com.bt.nextgen.service.avaloq.ips;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.ips.IpsIdentifier;
import com.bt.nextgen.service.integration.ips.IpsKey;

@ServiceBean(xpath = "ips_id")
public class IpsIdentifierImpl implements IpsIdentifier
{
    @ServiceElement(xpath = "val", converter = IpsKeyConverter.class)
    private IpsKey ipsKey;


    public IpsIdentifierImpl() {
        super();
    }

    public IpsIdentifierImpl(IpsKey ipsKey) {
        super();
        this.ipsKey = ipsKey;
    }

    @Override
    public IpsKey getIpsKey() {
        return ipsKey;
    }

    @Override
    public void setIpsKey(IpsKey ipsKey) {
        this.ipsKey = ipsKey;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ipsKey == null) ? 0 : ipsKey.hashCode());
        return result;
    }

    @Override
    // IDE generated method
    @SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142" })
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IpsIdentifierImpl other = (IpsIdentifierImpl) obj;
        if (ipsKey == null) {
            if (other.ipsKey != null)
                return false;
        } else if (!ipsKey.equals(other.ipsKey))
            return false;
        return true;
    }

}
