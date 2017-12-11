package com.bt.nextgen.service.avaloq.broker;


import com.bt.nextgen.core.jms.JmsObjectHandlerRegistry;
import com.bt.nextgen.service.avaloq.Template;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerService;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Map;


/**
 * Tests @{link JmsBrokerObjectHandler}.
 *
 * Created by Raju Arumugam.
 */

public class JmsBrokerHierarchyDataHandlerTest {

    private JmsObjectHandlerRegistry registry;
    private PartialInvalidationBrokerHolderImpl brokerHolder;
    private BrokerService brokerService;


    @Before
    public void init() {
        registry = Mockito.mock(JmsObjectHandlerRegistry.class);
        brokerService = Mockito.mock(BrokerService.class);
        brokerHolder = Mockito.mock(PartialInvalidationBrokerHolderImpl.class);
    }


    @Test
    public void postConstruct() {
        JmsBrokerObjectHandler handler = makeHandler();
        // Simulate the call of init method
        handler.init();
        Mockito.verify(registry).registerHandler(Template.BROKER_HIERARCHY.getClassName(), handler);
        String templateName = "";
        String requestId = "abc-123-hij";
        brokerHolder = makeBrokerHolder();
        // Simulate the call of handle method
        handler.handle(brokerHolder, templateName, requestId);
        Mockito.verify(brokerHolder).populateAllBrokerMaps();
        PartialInvalidationBrokerHolderImpl brokerUpdate = Mockito.mock(PartialInvalidationBrokerHolderImpl.class);
      //  Mockito.verify(brokerService).updateBrokerCache(brokerUpdate);

     }

    private JmsBrokerObjectHandler makeHandler() {
        JmsBrokerObjectHandler handler =  new JmsBrokerObjectHandler();
        handler.setRegistry(registry);
        handler.setBrokerService(brokerService);
        return handler;
    }


    private PartialInvalidationBrokerHolderImpl makeBrokerHolder() {

        Map<BrokerKey,Broker> brokers = Mockito.mock((Map.class));
        Map<JobKey, BrokerUser> brokerUsers = Mockito.mock((Map.class));

        brokerHolder.setBrokerMap(brokers);
        brokerHolder.setJobMap(brokerUsers);

        return brokerHolder;
    }



}
