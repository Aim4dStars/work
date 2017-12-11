package com.bt.nextgen.service.avaloq.client;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ClientListEventHandlerTest {

    @InjectMocks
    ClientListEventHandler clientListEventHandler;

    @Mock
    ClientIntegrationService clientIntegrationService;

    @Test
    public void testOnApplicationEvent() throws Exception {
        clientListEventHandler.onApplicationEvent(new ClientListEvent(new ClientListEventPublisher()));
        Mockito.verify(clientIntegrationService, Mockito.times(1)).loadClientMap(Matchers.any(ServiceErrors.class));
    }
}
