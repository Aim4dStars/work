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
import com.bt.nextgen.service.group.maintainiptoiprelationships.groupesb.IpToIpRelationshipRequest;
import com.bt.nextgen.service.group.maintainiptoiprelationships.groupesb.MaintainIpToIpRelationshipIntegrationService;
import com.bt.nextgen.serviceops.model.MaintainIpToIpRelationshipReqModel;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

@RunWith(MockitoJUnitRunner.class)
public class MaintainIpToIpRelationshipDTOServiceImplTest {
	
    @InjectMocks
    private MaintainIpToIpRelationshipDTOServiceImpl maintainIpToIpRelationshipDTOServiceImpl;

    @Mock
    private CustomerRawData customerRawData;

    @Mock
    private MaintainIpToIpRelationshipIntegrationService integrationService;

    @Test
    public void testMaiintainIpToIpRelationshipUseCase1() throws JsonProcessingException {
    	MaintainIpToIpRelationshipReqModel req = new MaintainIpToIpRelationshipReqModel();
        req.setUseCase("Add");
        req.setTargetCISKey("55555");
        req.setSourceCISKey("55555");
        req.setPartyRelStatus("BOE");
        req.setPartyRelType("Active");
        req.setTargetPersonType("individual");
        req.setSourcePersonType("individual");
        req.setPartyRelModNum("1234567");
        req.setSilo("WPAC");
        req.setPartyRelStartDate("10 Feb 2017");
        req.setPartyRelEndDate("10 Feb 2017");
        ServiceErrors serviceError = new ServiceErrorsImpl();
        when(
        		integrationService.maintainIpToIpRelationship(
                        any(IpToIpRelationshipRequest.class), any(ServiceErrors.class))).thenReturn(
                customerRawData);

        CustomerRawData customerRawData =
        		maintainIpToIpRelationshipDTOServiceImpl.maintainIpToIpRelationship(req, serviceError);
        assertNotNull(customerRawData);
    }

    @Test
    public void testMaintainIpToIpRelationshipUseCase2() throws JsonProcessingException {
        MaintainIpToIpRelationshipReqModel req = new MaintainIpToIpRelationshipReqModel();
        req.setUseCase("Modify");
        req.setTargetCISKey("55555");
        req.setSourceCISKey("55555");
        req.setPartyRelStatus("BOE");
        req.setPartyRelType("Active");
        req.setTargetPersonType("individual");
        req.setSourcePersonType("individual");
        req.setPartyRelModNum("1234567");
        req.setSilo("WPAC");
        req.setPartyRelStartDate("10 Feb 2017");
        req.setPartyRelEndDate("10 Feb 2017");
        ServiceErrors serviceError = new ServiceErrorsImpl();
        when(
                integrationService.maintainIpToIpRelationship(
                        any(IpToIpRelationshipRequest.class), any(ServiceErrors.class))).thenReturn(
                customerRawData);

        CustomerRawData customerRawData =
                maintainIpToIpRelationshipDTOServiceImpl.maintainIpToIpRelationship(req, serviceError);
        assertNotNull(customerRawData);

    }
}
