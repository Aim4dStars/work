package com.bt.nextgen.api.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.westpac.gn.locationmanagement.services.locationmanagement.xsd.retrievepostaladdress.v1.svc0454.RetrievePostalAddressRequest;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.gesb.locationmanagement.v1.LocationManagementIntegrationService;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.serviceops.controller.ServiceOpsController;
import com.bt.nextgen.serviceops.model.RetrivePostalAddressReqModel;

@Service("retrivePostalAddressDtoService")
@SuppressWarnings("squid:S1200")
public class RetrivePostalAddressDataDtoServiceImpl implements RetrivePostalAddressDataDtoService {
	private static final Logger logger = LoggerFactory.getLogger(ServiceOpsController.class);

	@Autowired
	private LocationManagementIntegrationService addressService;

	@Override
	public CustomerRawData retrieve(RetrivePostalAddressReqModel reqModel, ServiceErrors serviceErrors) {
		RetrievePostalAddressRequest request = createRetrivePostalAddressRequest(reqModel);
		logger.info("Calling IPToIPRelationshipsIntegrationService.retrieve");
		CustomerRawData customerData = addressService.retrievePostalAddressForGCM(request, serviceErrors);
		return customerData;
	}

	private RetrievePostalAddressRequest createRetrivePostalAddressRequest(RetrivePostalAddressReqModel reqModel) {
		RetrievePostalAddressRequest request = new RetrievePostalAddressRequest();
		request.setAddressType(reqModel.getAddressType());
		request.setKey(reqModel.getKey());
		return request;

	}
}
