package com.bt.nextgen.api.policy.model;

public class CustomerKey {

    private String customerNumber;

    public String getCustomerNumber() {
        return customerNumber;
    }

    public CustomerKey(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((customerNumber == null) ? 0 : customerNumber.hashCode());
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
        CustomerKey other = (CustomerKey) obj;
        if (customerNumber == null) {
            if (other.customerNumber != null)
                return false;
        } else if (!customerNumber.equals(other.customerNumber))
            return false;
        return true;
    }
}
