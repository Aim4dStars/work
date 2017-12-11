package com.bt.nextgen.api.ips.model;

public class InvestmentPolicyStatementKey {
    private String ipsId;

    public InvestmentPolicyStatementKey() {
        super();
        this.ipsId = null;
    }

    public InvestmentPolicyStatementKey(String ipsId) {
        super();
        this.ipsId = ipsId;
    }

    public String getIpsId() {
        return ipsId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ipsId == null) ? 0 : ipsId.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142" })
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        InvestmentPolicyStatementKey other = (InvestmentPolicyStatementKey) obj;
        if (ipsId == null) {
            if (other.ipsId != null)
                return false;
        } else if (!ipsId.equals(other.ipsId))
            return false;
        return true;
    }
}
