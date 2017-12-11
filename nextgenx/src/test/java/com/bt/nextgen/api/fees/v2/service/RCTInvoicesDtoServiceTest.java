package com.bt.nextgen.api.fees.v2.service;

import com.bt.nextgen.api.fees.model.DateRangeKey;
import com.bt.nextgen.api.fees.model.RCTInvoicesDto;
import com.bt.nextgen.api.fees.service.RCTInvoicesDtoServiceImpl;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.fees.RCTInvoices;
import com.bt.nextgen.service.avaloq.fees.RCTInvoicesImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.bt.nextgen.service.integration.fees.RCTInvoicesFee;
import com.bt.nextgen.service.integration.fees.RCTInvoicesIntegrationService;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import com.bt.nextgen.web.controller.cash.util.Attribute;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class RCTInvoicesDtoServiceTest {
    @InjectMocks
    private RCTInvoicesDtoServiceImpl rctiDtoServiceImpl;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private RCTInvoicesIntegrationService taxInvoiceIntegrationService;
    
    private List<ApiSearchCriteria> criteria;

    private ServiceErrors serviceErrors;

    @Before
    public void setup() throws Exception {
    	criteria = new ArrayList<>();
        criteria.add(new ApiSearchCriteria(Attribute.START_DATE, SearchOperation.EQUALS, "2017-01-01", OperationType.DATE));
        criteria.add(new ApiSearchCriteria(Attribute.END_DATE, SearchOperation.EQUALS, "2017-02-01", OperationType.DATE));
    
    	UserProfile userProfile = null;
    	Mockito.when(userProfileService.getActiveProfile()).thenReturn(userProfile);
    	List<Broker> brokers = new ArrayList<Broker>();
    	brokers.add(new BrokerImpl(BrokerKey.valueOf("56789"), BrokerType.DEALER));
    	brokers.add(new BrokerImpl(BrokerKey.valueOf("12345"), BrokerType.DEALER));
    	Mockito.when(brokerIntegrationService.getBrokersForJob(Mockito.any(JobProfileIdentifier.class), Mockito.any(ServiceErrors.class)))
    		.thenReturn(brokers);
    }

    @Test
    public void testSearch_whenThereAreNoFees_thenReturnsEmptyList() {
    	RCTInvoices rctInvoices = new RCTInvoicesImpl();
    	Mockito.when(taxInvoiceIntegrationService.getRecipientCreatedTaxInvoice(Mockito.any(BrokerKey.class), 
    			Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class)))
    		.thenReturn(rctInvoices);
    	
    	List<RCTInvoicesDto> result = rctiDtoServiceImpl.search(criteria, serviceErrors);
    		
        Assert.assertEquals(0, result.size());
    }


    @Test
    public void testSearch_whenThereAreFees_thenReturnsFeeDto() {
    	List<RCTInvoicesFee> fees = new ArrayList<>();
    	RCTInvoicesFee fee1 = Mockito.mock(RCTInvoicesFee.class);
    	Mockito.when(fee1.getInvoiceDate()).thenReturn(new DateTime("2017-01-15"));
    	fees.add(fee1);
    	
    	RCTInvoices rctInvoices = Mockito.mock(RCTInvoices.class);
    	Mockito.when(rctInvoices.getRCTInvoicesFees()).thenReturn(fees);

    	Mockito.when(taxInvoiceIntegrationService.getRecipientCreatedTaxInvoice(Mockito.any(BrokerKey.class), 
    			Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class)))
    		.thenReturn(rctInvoices);
    	
    	List<RCTInvoicesDto> result = rctiDtoServiceImpl.search(criteria, serviceErrors);
    		
        Assert.assertEquals(1, result.size());
        RCTInvoicesDto resultFee = result.get(0);
        DateRangeKey key = resultFee.getKey();
        Assert.assertEquals(new DateTime("2017-01-01"), key.getStartDate());
        Assert.assertEquals(new DateTime("2017-01-31"), key.getEndDate());
        Assert.assertEquals("Recipient Created Tax Invoice", resultFee.getName());
    }

    @Test
    public void testSearch_whenAreMultipleFees_thenAggregatesFees() {
    	List<RCTInvoicesFee> fees = new ArrayList<>();
    	RCTInvoicesFee fee1 = Mockito.mock(RCTInvoicesFee.class);
    	Mockito.when(fee1.getInvoiceDate()).thenReturn(new DateTime("2017-01-15"));
    	fees.add(fee1);

    	RCTInvoicesFee fee2 = Mockito.mock(RCTInvoicesFee.class);
    	Mockito.when(fee2.getInvoiceDate()).thenReturn(new DateTime("2017-01-16"));
    	fees.add(fee2);

    	RCTInvoicesFee fee3 = Mockito.mock(RCTInvoicesFee.class);
    	Mockito.when(fee3.getInvoiceDate()).thenReturn(new DateTime("2017-02-05"));
    	fees.add(fee3);
    	
    	RCTInvoices rctInvoices = Mockito.mock(RCTInvoices.class);
    	Mockito.when(rctInvoices.getRCTInvoicesFees()).thenReturn(fees);

    	Mockito.when(taxInvoiceIntegrationService.getRecipientCreatedTaxInvoice(Mockito.any(BrokerKey.class), 
    			Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class)))
    		.thenReturn(rctInvoices);
    	
    	List<RCTInvoicesDto> result = rctiDtoServiceImpl.search(criteria, serviceErrors);
    		
        Assert.assertEquals(2, result.size());
        RCTInvoicesDto resultFee1 = result.get(0);
        DateRangeKey key1 = resultFee1.getKey();
        Assert.assertEquals(new DateTime("2017-02-01"), key1.getStartDate());
        Assert.assertEquals(new DateTime("2017-02-28"), key1.getEndDate());
        Assert.assertEquals("Recipient Created Tax Invoice", resultFee1.getName());

        RCTInvoicesDto resultFee2 = result.get(1);
        DateRangeKey key2 = resultFee2.getKey();
        Assert.assertEquals(new DateTime("2017-01-01"), key2.getStartDate());
        Assert.assertEquals(new DateTime("2017-01-31"), key2.getEndDate());
        Assert.assertEquals("Recipient Created Tax Invoice", resultFee2.getName());
    }
}
