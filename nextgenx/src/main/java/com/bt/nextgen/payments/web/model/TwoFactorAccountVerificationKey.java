package com.bt.nextgen.payments.web.model;

/**
 * Created by l069679 on 22/12/2016.
 */
public class TwoFactorAccountVerificationKey {

    private String accountId;
    private String bsb;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getBsb() {
        return bsb;
    }

    public void setBsb(String bsb) {
        this.bsb = bsb;
    }

    public TwoFactorAccountVerificationKey(String accountId, String bsb) {
        this.accountId = accountId;
        this.bsb = bsb;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accountId == null) ? 0 : accountId.hashCode()) + ((bsb == null) ? 0 : bsb.hashCode());
        return result;
    }

    @SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142" })
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TwoFactorAccountVerificationKey other = (TwoFactorAccountVerificationKey) obj;
        if (accountId == null) {
            if (other.accountId != null)
                return false;
        } else if (!accountId.equals(other.accountId)) {
            return false;
        }
        if (bsb == null) {
            if(other.bsb !=null)
                return false;
        } else if (!bsb.equals(other.bsb)) {
            return false;
        }
        return true;
    }
}
