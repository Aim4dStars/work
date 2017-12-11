package com.bt.nextgen.api.policy.model;

public class PolicyKey {

    private String accountId;
    private String policyId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        int accountHashCode = (accountId == null) ? 0 : accountId.hashCode();
        int policyHashCode = (policyId == null) ? 0 : policyId.hashCode();
        result = prime * result + accountHashCode + policyHashCode;
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
        PolicyKey other = (PolicyKey) obj;
        if (!accountId.equals(other.accountId) && !policyId.equals(other.policyId))
            return false;
        return true;
    }
}
