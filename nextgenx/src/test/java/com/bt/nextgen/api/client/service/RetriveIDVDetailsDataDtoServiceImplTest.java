package com.bt.nextgen.api.client.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.maintainarrangementandiparrangementrelationships.RetriveIPToIPRequest;
import com.bt.nextgen.service.group.customer.groupesb.retriveidvdetails.v6.RetriveIDVDetailsIntegrationService;
import com.bt.nextgen.service.group.customer.groupesb.retriveidvdetails.v6.RetriveIDVDtlRequest;
import com.bt.nextgen.serviceops.model.RetrieveIDVDetailsReqModel;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

@RunWith(MockitoJUnitRunner.class)
public class RetriveIDVDetailsDataDtoServiceImplTest {

	 @InjectMocks
	 private RetriveIDVDetailsDataDtoServiceImpl retriveIDVDetailsDataDtoServiceImpl;
	 
	 @Mock
	    private CustomerRawData customerRawData;

	  @Mock
	  RetriveIDVDetailsIntegrationService retriveIDVDetailsIntegrationService;
	  
	  @Test
	    public void testRetitrive() throws JsonProcessingException {
		  RetrieveIDVDetailsReqModel req = new RetrieveIDVDetailsReqModel();
	        req.setCisKey("55555555555");
	        req.setPersonType("individual");
	        req.setSilo("WPAC");
	        
	        ServiceErrors serviceError = new ServiceErrorsImpl();
	        when(
	        		retriveIDVDetailsIntegrationService.retrieveIDVDetails(
	                        any(RetriveIDVDtlRequest.class), any(ServiceErrors.class))).thenReturn(customerRawData);

	       CustomerRawData customerRawData = retriveIDVDetailsDataDtoServiceImpl.retrieve(req, serviceError);
	       assertNotNull(customerRawData);
	    }
	 
}
