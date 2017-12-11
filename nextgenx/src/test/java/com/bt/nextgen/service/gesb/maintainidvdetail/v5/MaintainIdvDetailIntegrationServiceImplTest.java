/**
 * 
 */
package com.bt.nextgen.service.gesb.maintainidvdetail.v5;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.RequestAction;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author L081050
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class MaintainIdvDetailIntegrationServiceImplTest {
	@InjectMocks
	private MaintainIdvDetailIntegrationServiceImpl maintainIdvDetailIntegrationServiceImpl;

	@Mock
	private CustomerRawData customerRawData;

	@Mock
	private MaintainIdvDetailIntegrationServiceImplV5 maintainIdvDetailIntegrationServiceImplV5;

	@Test
	public void testMaintainImpl() throws JsonProcessingException {
		MaintainIdvRequest maintainIdvRequest = new MaintainIdvRequest();
		maintainIdvRequest
				.setRequestAction(RequestAction.UPDATE_AND_SET_IDV_STATUS);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		when(
				maintainIdvDetailIntegrationServiceImplV5
						.maintain(any(MaintainIdvRequest.class),
								any(ServiceErrors.class))).thenReturn(
				customerRawData);

		CustomerRawData customerRawData = maintainIdvDetailIntegrationServiceImpl
				.maintain(maintainIdvRequest, serviceErrors);
		assertNotNull(customerRawData);
	}

}
