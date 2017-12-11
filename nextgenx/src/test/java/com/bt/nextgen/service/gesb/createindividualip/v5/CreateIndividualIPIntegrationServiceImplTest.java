package com.bt.nextgen.service.gesb.createindividualip.v5;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.Individual;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;

@RunWith(MockitoJUnitRunner.class)
public class CreateIndividualIPIntegrationServiceImplTest {

    @InjectMocks
    private CreateIndividualIPIntegrationServiceImpl createIndividualIPIntegrationServiceImpl;
    
    @Mock
    private CreateIndividualIPIntegrationService createIndividualIPIntegrationServiceV5;
    
    @Mock
    private CustomerRawData customerRawData;
    
    @Test
    public void testCreate(){
        CreateIndvIPRequest createIndvIPRequest = new CreateIndvIPRequest();
        Individual individual = new Individual();
        individual.setEmploymentStatus("active");
        createIndvIPRequest.setIndividual(individual);
  
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        when(
                createIndividualIPIntegrationServiceV5
                        .create(any(CreateIndvIPRequest.class),
                                any(ServiceErrors.class))).thenReturn(
                customerRawData);

        CustomerRawData customerRawData = createIndividualIPIntegrationServiceImpl.create(createIndvIPRequest, serviceErrors);
        assertNotNull(customerRawData);
        
    }
}
