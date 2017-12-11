package com.bt.nextgen.service.group.customer.groupesb.email;

import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.CustomerMetaData;

public class CustomerEmailMetaData implements CustomerMetaData {

    private String versionNumber;
    private CustomerManagementRequest request;

    @Override
    public String getVersionNumber() {
        return versionNumber;
    }

    @Override
    public CustomerManagementRequest getRequest() {
        return request;
    }

    @Override
    public void setRequest(CustomerManagementRequest request) {
        this.request = request;
    }
}
