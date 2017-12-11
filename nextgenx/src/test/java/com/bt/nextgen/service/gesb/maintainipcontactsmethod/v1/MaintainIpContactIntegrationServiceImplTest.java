/**
 * 
 */
package com.bt.nextgen.service.gesb.maintainipcontactsmethod.v1;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.RequestAction;

import com.bt.nextgen.api.client.service.MaintainIpContactDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.gesb.maintainidvdetail.v5.MaintainIdvRequest;
import com.bt.nextgen.service.gesb.maintainipcontactmethod.v1.MaintainIpContactIntegrationServiceImpl;
import com.bt.nextgen.service.gesb.maintainipcontactmethod.v1.MaintainIpContactIntegrationServiceImplV1;
import com.bt.nextgen.service.gesb.maintainipcontactmethod.v1.MaintainIpContactRequest;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author L081050
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class MaintainIpContactIntegrationServiceImplTest {
	@InjectMocks
	private MaintainIpContactIntegrationServiceImpl maintainIpContactIntegrationServiceImpl;

	@Mock
	private CustomerRawData customerRawData;

	@Mock
	private MaintainIpContactIntegrationServiceImplV1 maintainIpContactIntegrationServicev1;

	@Test
	public void testMaintainImpl() throws JsonProcessingException {
		MaintainIpContactRequest maintainIpContactRequest = new MaintainIpContactRequest();
		//maintainIpContactRequest.setHasEmailAddressContactMethod(hasEmailAddressContactMethod);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		when(
				maintainIpContactIntegrationServicev1
						.maintain(any(MaintainIpContactRequest.class),
								any(ServiceErrors.class))).thenReturn(
				customerRawData);

		CustomerRawData customerRawData = maintainIpContactIntegrationServiceImpl
				.maintain(maintainIpContactRequest, serviceErrors);
		assertNotNull(customerRawData);
	}

}
