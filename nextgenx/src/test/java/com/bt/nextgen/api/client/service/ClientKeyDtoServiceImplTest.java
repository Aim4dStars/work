package com.bt.nextgen.api.client.service;

import com.bt.nextgen.api.client.model.ClientIdentificationDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.group.customer.BankingCustomerIdentifier;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.bt.nextgen.service.integration.userinformation.GenericClient;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class ClientKeyDtoServiceImplTest {

    @InjectMocks
    ClientKeyDtoServiceImpl clientKeyDtoService;

    @Mock
    ClientIntegrationService clientIntegrationService;

    @Test
    public void testRetrieveExistingCustomer_success() {
        GenericClient client = getGenericClient("123456");
        when(clientIntegrationService.loadClientDetailsByGcmId(any(BankingCustomerIdentifier.class), any(ServiceErrors.class))).thenReturn(client);
        ClientIdentificationDto result = clientKeyDtoService.find(new com.bt.nextgen.api.client.model.ClientKey("201611223"), new ServiceErrorsImpl());
        assertNotNull(result);
        assertThat(EncodedString.toPlainText(result.getKey().getClientId()), is("123456"));
    }

    @Test
    public void testRetrieveExistingCustomer_noKey() {
        ClientIdentificationDto result = clientKeyDtoService.find(new com.bt.nextgen.api.client.model.ClientKey(""), new ServiceErrorsImpl());
        assertNull(result);
    }

    @Test
    public void testRetrieveExistingCustomer_noResult() {
        when(clientIntegrationService.loadClientDetailsByGcmId(any(BankingCustomerIdentifier.class), any(ServiceErrors.class))).thenReturn(null);
        ClientIdentificationDto result = clientKeyDtoService.find(new com.bt.nextgen.api.client.model.ClientKey("201611223"), new ServiceErrorsImpl());
        assertNull(result);
    }

    private GenericClient getGenericClient(final String clientId) {
        return new GenericClient() {
            @Override
            public ClientType getClientType() {
                return null;
            }

            @Override
            public List<Address> getAddresses() {
                return null;
            }

            @Override
            public List<Email> getEmails() {
                return null;
            }

            @Override
            public List<Phone> getPhones() {
                return null;
            }

            @Override
            public int getAge() {
                return 0;
            }

            @Override
            public Gender getGender() {
                return null;
            }

            @Override
            public DateTime getDateOfBirth() {
                return null;
            }

            @Override
            public boolean isRegistrationOnline() {
                return false;
            }

            @Override
            public String getTitle() {
                return null;
            }

            @Override
            public String getSafiDeviceId() {
                return null;
            }

            @Override
            public String getModificationSeq() {
                return null;
            }

            @Override
            public String getGcmId() {
                return null;
            }

            @Override
            public DateTime getOpenDate() {
                return null;
            }

            @Override
            public String getFullName() {
                return null;
            }

            @Override
            public String getFirstName() {
                return null;
            }

            @Override
            public String getLastName() {
                return null;
            }

            @Override
            public ClientKey getClientKey() {
                return ClientKey.valueOf(clientId);
            }

            @Override
            public void setClientKey(ClientKey clientKey) {

            }
        };
    }
}
