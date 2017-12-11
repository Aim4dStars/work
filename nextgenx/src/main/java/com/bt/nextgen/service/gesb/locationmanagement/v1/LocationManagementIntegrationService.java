package com.bt.nextgen.service.gesb.locationmanagement.v1;

import au.com.westpac.gn.locationmanagement.services.locationmanagement.xsd.retrievepostaladdress.v1.svc0454.RetrievePostalAddressRequest;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.gesb.locationmanagement.PostalAddress;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;

/**
 * Created by F030695 on 24/10/2016.
 */
public interface LocationManagementIntegrationService {

    PostalAddress retrievePostalAddress(String addressId, ServiceErrors errors);

	CustomerRawData retrievePostalAddressForGCM(RetrievePostalAddressRequest requestPayload, ServiceErrors serviceErrors);
}
