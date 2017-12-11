package com.bt.nextgen.service.group.customer.groupesb;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.generatesecuritycredential.v1.svc0382.ActionCode;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.generatesecuritycredential.v1.svc0382.GenerateSecurityCredentialRequest;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.generatesecuritycredential.v1.svc0382.SecurityCredentialType;
import au.com.westpac.gn.common.xsd.identifiers.v1.CustomerIdentifier;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.group.customer.CustomerTokenRequest;
import com.bt.nextgen.service.group.customer.CustomerTokenRequestModel;
import com.bt.nextgen.util.SamlUtil;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.xml.transform.StringResult;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GroupEsbCustomerTokenIntegrationImplTest {
    @Mock
    private WebServiceTemplate webServiceTemplate;
    @Mock
    private WebServiceProvider provider;

    @InjectMocks
    private GroupEsbCustomerTokenIntegrationImpl groupEsbCustomerToken;

    private SamlToken token = null;
    private CustomerTokenRequestModel model = null;

    @Before
    public void setup() throws IOException {
        when(provider.getWebServiceTemplate(anyString())).thenReturn(webServiceTemplate);

        when(webServiceTemplate.sendSourceAndReceiveToResult(any(StreamSource.class),
                any(WebServiceMessageCallback.class),
                any(StringResult.class))).thenReturn(true);

        String samlString = SamlUtil.loadSaml();
        model = new CustomerTokenRequestModel();
        model.setCustomerNumber("adviser");
        token = new SamlToken(samlString);
        model.setToken(token);
        model.setxForwardedHost("localhost");

    }

    @Test
    public void testGetCustomerSAMLToken() {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        groupEsbCustomerToken.jaxb2Marshaller = new Jaxb2Marshaller();
        groupEsbCustomerToken.jaxb2Marshaller.setClassesToBeBound(GenerateSecurityCredentialRequest.class);
        String samlTokenString = groupEsbCustomerToken.getCustomerSAMLToken(model, serviceErrors);
        assertThat(samlTokenString, Is.is(""));
        assertThat(serviceErrors.hasErrors(), Is.is(false));
    }

    @Test
    public void testGenerateUserSAMLBase() throws Exception {
        Method method = GroupEsbCustomerTokenIntegrationImpl.class.getDeclaredMethod("generateUserSAMLBase", null);
        method.setAccessible(true);
        GenerateSecurityCredentialRequest credentialRequest = (GenerateSecurityCredentialRequest) method.invoke(groupEsbCustomerToken,
                null);

        assertThat(credentialRequest.getRequestedAction(), Is.is(ActionCode.EXCHANGE_SECURITY_CREDENTIAL));
        assertThat(credentialRequest.getSecurityCredentialType(), Is.is(SecurityCredentialType.SAML_2_0));
        //TODO : do not find the authentication level in commons or env properties file ?
        //assertThat(credentialRequest.getAuthenticationLevel(), nullValue());
        assertThat(credentialRequest.getAuthenticationLevel(), Is.is("6"));
    }

    @Test
    public void testGetCustomerIdentifier() throws Exception {
        Method method = GroupEsbCustomerTokenIntegrationImpl.class.getDeclaredMethod("getCustomerIdentifier",
                CustomerTokenRequest.class);
        method.setAccessible(true);
        CustomerIdentifier customerIdentifier = (CustomerIdentifier) method.invoke(groupEsbCustomerToken, model);
        assertThat(customerIdentifier.getCustomerNumber(), Is.is("adviser@localhost"));
    }

}
