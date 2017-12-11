package com.bt.nextgen.service.group.maintainiptoiprelationships.groupesb;

import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;

@RunWith(PowerMockRunner.class)
public class GroupEsbIpToIpRelationManagementImplTest {

    
    @Mock
    private MaintainIpToIpRelationshipIntegrationService integrationservice;
    
    @Mock
    private CustomerRawData customerRawData;
    
    @InjectMocks
    private GroupEsbIpToIpRelationManagementImpl groupEsbIpToIpRelationManagementImpl;
    
    @Test
    public void testCreateArrangementAndRelationShip() {
        IpToIpRelationshipRequest req = new IpToIpRelationshipRequest();
        ServiceErrors errors = new ServiceErrorsImpl();
        when(integrationservice.maintainIpToIpRelationship(req, errors)).thenReturn(customerRawData);
        CustomerRawData customerRawData = groupEsbIpToIpRelationManagementImpl.maintainIpToIpRelationship(req, errors);
        Assert.assertNotNull(customerRawData);
    }
}
