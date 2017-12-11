package com.bt.nextgen.api.client.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.gesb.maintainipcontactmethod.v1.MaintainIpContactIntegrationService;
import com.bt.nextgen.service.gesb.maintainipcontactmethod.v1.MaintainIpContactRequest;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.serviceops.model.MaintainIPContactMethodModel;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

@RunWith(MockitoJUnitRunner.class)
public class MaintainIpContactDtoServiceImplTest {
	
	@InjectMocks
	MaintainIpContactDtoServiceImpl maintainIpContactDtoServiceImpl;
	
	@Mock
	private CustomerRawData customerRawData;
	
	@Mock
	private MaintainIpContactIntegrationService maintainIpContactIntegrationService;
	
	@Test
	public void testMaintainEmail() throws JsonProcessingException {
		MaintainIPContactMethodModel req = new MaintainIPContactMethodModel();
		req.setAddressType("Email");
		req.setCisKey("12345678901");
		req.setContactMedium("Mobile");
		req.setPriorityLevel("Primary");
		req.setPersonType("Individual");
		req.setRequestedAction("Add");
		req.setSilo("WPAC");
		req.setEmailAddress("mishra.shivam@tcs.com");
		req.setUsageId("1");
		req.setValidityStatus("true");

		ServiceErrors serviceError = new ServiceErrorsImpl();
		when(
				maintainIpContactIntegrationService
						.maintain(any(MaintainIpContactRequest.class),
								any(ServiceErrors.class))).thenReturn(
				customerRawData);

		CustomerRawData customerRawData = maintainIpContactDtoServiceImpl
				.maintain(req, serviceError);
		assertNotNull(customerRawData);
	}
	@Test
    public void testMaintainMobile() throws JsonProcessingException {
        MaintainIPContactMethodModel req = new MaintainIPContactMethodModel();
        req.setAddressType("Mobile");
        req.setCisKey("12345678901");
        req.setContactMedium("Mobile");
        req.setPriorityLevel("Primary");
        req.setPersonType("Individual");
        req.setRequestedAction("Add");
        req.setSilo("WPAC");
        req.setEmailAddress("mishra.shivam@tcs.com");
        req.setUsageId("1");
        req.setValidityStatus("true");

        ServiceErrors serviceError = new ServiceErrorsImpl();
        when(
                maintainIpContactIntegrationService
                        .maintain(any(MaintainIpContactRequest.class),
                                any(ServiceErrors.class))).thenReturn(
                customerRawData);

        CustomerRawData customerRawData = maintainIpContactDtoServiceImpl
                .maintain(req, serviceError);
        assertNotNull(customerRawData);
    }
	

}
