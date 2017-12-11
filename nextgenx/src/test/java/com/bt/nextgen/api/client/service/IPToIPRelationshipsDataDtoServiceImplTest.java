/**
 * 
 */
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
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.maintainarrangementandiparrangementrelationships.RetriveIPToIPRequest;
import com.bt.nextgen.service.groupesb.iptoiprelationships.v4.IPToIPRelationshipsIntegrationService;
import com.bt.nextgen.serviceops.model.RetriveIpToIpRelationshipReqModel;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author L081050
 */
@RunWith(MockitoJUnitRunner.class)
public class IPToIPRelationshipsDataDtoServiceImplTest {
    @InjectMocks
    private IPToIPRelationshipsDataDtoServiceImpl iptoipRelationshipsDataDtoServiceImpl;

    @Mock
    private CustomerRawData customerRawData;

    @Mock
    private IPToIPRelationshipsIntegrationService iptoipRelationshipsIntegrationservice;

    @Test
    public void testRetitriveIPToIpRelationShip() throws JsonProcessingException {
        RetriveIpToIpRelationshipReqModel req = new RetriveIpToIpRelationshipReqModel();
        req.setCisKey("55555555555");
        req.setRoleType("individual");
        req.setSilo("WPAC");
        ServiceErrors serviceError = new ServiceErrorsImpl();
        when(
                iptoipRelationshipsIntegrationservice.retrieveIpToIpRelationshipsInformation(
                        any(RetriveIPToIPRequest.class), any(ServiceErrors.class))).thenReturn(customerRawData);

        CustomerRawData customerRawData = iptoipRelationshipsDataDtoServiceImpl.retrieve(req, serviceError);
        assertNotNull(customerRawData);
    }
}
