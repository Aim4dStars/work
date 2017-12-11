package com.bt.nextgen.api.client.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import au.com.westpac.gn.locationmanagement.services.locationmanagement.xsd.retrievepostaladdress.v1.svc0454.AddressType;
import au.com.westpac.gn.locationmanagement.services.locationmanagement.xsd.retrievepostaladdress.v1.svc0454.RetrievePostalAddressRequest;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.gesb.locationmanagement.v1.LocationManagementIntegrationService;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.serviceops.model.RetrivePostalAddressReqModel;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

@RunWith(MockitoJUnitRunner.class)
public class RetrivePostalAddressDataDtoServiceImplTest {

	@InjectMocks
	private RetrivePostalAddressDataDtoServiceImpl retrivePostalAddressDataDtoServiceImpl;

	@Mock
	private CustomerRawData customerRawData;

	@Mock
	private LocationManagementIntegrationService addressService;

	@Test
	public void testRetitrive() throws JsonProcessingException {
		RetrivePostalAddressReqModel req = new RetrivePostalAddressReqModel();
		req.setAddressType(AddressType.D);
		req.setKey("01OAUEHgvgBwAAAAAIAgEAAAACoN9BUBBhAIIQAOAAAAAAAAA2AAD..2QAAAAA.....wAAAAAAAAAAAAAAAAA2LzEwIFNtaXRoIFN0cmVldAA-");

		ServiceErrors serviceError = new ServiceErrorsImpl();
		when(addressService.retrievePostalAddressForGCM(any(RetrievePostalAddressRequest.class), any(ServiceErrors.class))).thenReturn(
				customerRawData);

		CustomerRawData customerRawData = retrivePostalAddressDataDtoServiceImpl.retrieve(req, serviceError);
		
		assertNotNull(customerRawData);
	}

}
