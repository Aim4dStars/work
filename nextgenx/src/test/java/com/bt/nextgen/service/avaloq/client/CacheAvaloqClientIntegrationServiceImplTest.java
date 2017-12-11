package com.bt.nextgen.service.avaloq.client;

import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.domain.ClientHolder;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CacheAvaloqClientIntegrationServiceImplTest {

    @InjectMocks
    CacheAvaloqClientIntegrationServiceImpl cacheClient;

    @Mock
    private AvaloqExecute avaloqExecute;

    @Test
    public void testLoadClients() throws Exception {

        when(avaloqExecute.executeReportRequestToDomain(Matchers.any(AvaloqReportRequest.class), eq(ClientHolder.class), Matchers.any(ServiceErrors.class)))
                .thenReturn(createClientHolder());

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Map<ClientKey, Client> clientMap = cacheClient.loadClients(serviceErrors);

        Assert.assertNotNull(clientMap);
        Assert.assertThat(clientMap.size(), is(3));

    }

    private ClientHolder createClientHolder() {
        return new ClientHolder(){
            @Override
            public List<Client> getClients() {
                return createClientList();
            }
        };
    }

    private List<Client> createClientList() {
        Client client1 = clientBuilder("111", "client one");
        Client client2 = clientBuilder("222", "client two");
        Client client3 = clientBuilder("333", "client three");
        return Arrays.asList(new Client[]{client1, client2, client3});
    }

    private Client clientBuilder(final String key, final String fullName) {
        ClientDetailImpl client = Mockito.mock(ClientDetailImpl.class);
        when(client.getClientKey()).thenReturn(ClientKey.valueOf(key));
        when(client.getFullName()).thenReturn(fullName);
        return client;
    }
}