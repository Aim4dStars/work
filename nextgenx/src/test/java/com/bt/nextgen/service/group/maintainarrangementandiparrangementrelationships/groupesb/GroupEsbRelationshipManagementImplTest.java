package com.bt.nextgen.service.group.maintainarrangementandiparrangementrelationships.groupesb;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawDataImpl;
import com.bt.nextgen.service.group.customer.groupesb.maintainarrangementandiparrangementrelationships.ArrangementAndRelationshipManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.maintainarrangementandiparrangementrelationships.GroupEsbRelationshipManagementImpl;
import com.bt.nextgen.service.group.maintainarrangementandiparrangementrelationships.MaintainArrangementAndRelationshipIntegrationService;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

@RunWith(PowerMockRunner.class)
public class GroupEsbRelationshipManagementImplTest {

    @Mock
    private MaintainArrangementAndRelationshipIntegrationService integrationservice;

    @InjectMocks
    private GroupEsbRelationshipManagementImpl groupEsbRelationshipManagementImpl;

    @Test
    public void testCreateArrangementAndRelationShip() throws JsonProcessingException {
        ArrangementAndRelationshipManagementRequest req = new ArrangementAndRelationshipManagementRequest();
        ServiceErrors errors = new ServiceErrorsImpl();
        CustomerRawData customerRawData = new CustomerRawDataImpl("aa");
        when(integrationservice.createArrangementAndRelationShip(req, errors)).thenReturn(customerRawData);
        customerRawData.setRawResponse("aa");
        customerRawData = groupEsbRelationshipManagementImpl.createArrangementAndRelationShip(req, errors);
        assertThat("aa", equalTo(customerRawData.getRawResponse()));
    }

}
