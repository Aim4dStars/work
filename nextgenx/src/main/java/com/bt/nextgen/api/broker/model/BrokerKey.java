package com.bt.nextgen.api.broker.model;

/**
 * Created by L062329 on 2/10/2014.
 */
public class BrokerKey {
    private String brokerId;

    public BrokerKey()
    {}

    public BrokerKey(String brokerId)
    {
        super();
        this.brokerId = brokerId;
    }

    public String getBrokerId()
    {
        return brokerId;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((brokerId == null) ? 0 : brokerId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BrokerKey other = (BrokerKey)obj;
        if (brokerId == null)
        {
            if (other.brokerId != null)
                return false;
        }
        else if (!brokerId.equals(other.brokerId))
            return false;
        return true;
    }

}
