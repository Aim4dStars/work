package com.bt.nextgen.service.avaloq.fees;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqType;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

public class RCTInvoicesIntegrationServiceImplIntegrationTest extends BaseSecureIntegrationTest {

    @Autowired
    private RCTInvoicesIntegrationServiceImpl integrationService;

    @Test
    public void testLoadWrapAccountValuation_whenValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        
        Broker broker = new BrokerImpl(BrokerKey.valueOf("56789"), BrokerType.DEALER);
        RCTInvoices rctiInvoices = integrationService.getRecipientCreatedTaxInvoice(broker.getKey(), new DateTime(),
        		new DateTime(), serviceErrors);
        
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNotNull(rctiInvoices);
    }
}
