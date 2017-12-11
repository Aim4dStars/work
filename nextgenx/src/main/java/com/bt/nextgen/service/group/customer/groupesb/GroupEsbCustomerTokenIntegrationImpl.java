package com.bt.nextgen.service.group.customer.groupesb;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.generatesecuritycredential.v1.svc0382.ActionCode;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.generatesecuritycredential.v1.svc0382.GenerateSecurityCredentialRequest;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.generatesecuritycredential.v1.svc0382.SecurityCredentialType;
import au.com.westpac.gn.common.xsd.identifiers.v1.CustomerIdentifier;
import com.bt.nextgen.security.SecurityHeaderFactory;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceError;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerTokenIntegrationService;
import com.bt.nextgen.service.group.customer.CustomerTokenRequest;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.xml.transform.StringResult;

import javax.annotation.Resource;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;

@Service
public class GroupEsbCustomerTokenIntegrationImpl implements CustomerTokenIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(GroupEsbCustomerTokenIntegrationImpl.class);

    private static final String DEFAULT_AUTHENTICATION_LEVEL = "saml.auth.level";

    @Autowired
    private WebServiceProvider provider;

    @Resource(name = "userDetailsService")
    private BankingAuthorityService userSamlService;

    @Resource(name = "marshaller")
    Jaxb2Marshaller jaxb2Marshaller;

    @Override
    public String getCustomerSAMLToken(CustomerTokenRequest customerTokenRequest, ServiceErrors serviceErrors) {

        logger.info("Getting Customer SAML Token method for user:{} at xforwarded host: {} using unauthenticated saml: {}",
                customerTokenRequest.getCustomerNumber(), customerTokenRequest.getxForwardedHost(), customerTokenRequest.getToken().getToken());

        StringResult samlXML = new StringResult();

        try {
            GenerateSecurityCredentialRequest requestPayload = generateUserSAMLBase();

            CustomerIdentifier customerIdentifier = getCustomerIdentifier(customerTokenRequest);
            requestPayload.setCustomerNumber(customerIdentifier);

            // The requested SAML Token Authentication Level.
            StringResult requestXml = new StringResult();
            jaxb2Marshaller.marshal(requestPayload, requestXml);

            sendObjectForResult(requestXml.toString(), customerTokenRequest.getToken(), samlXML);
        } catch (Exception e) {
            ServiceError error = new ServiceErrorImpl();
            error.setException(e);
            error.setReason("Failed to create request Payload");
            serviceErrors.addError(error);
        }

        logger.info("Getting Customer SAML Token method completed, generated token is {}", samlXML.toString());

        return samlXML.toString();
    }

    private CustomerIdentifier getCustomerIdentifier(CustomerTokenRequest customerTokenRequest) {
        CustomerIdentifier customerIdentifier = new CustomerIdentifier();
        customerIdentifier.setCustomerNumber(customerTokenRequest.getCustomerNumber() + "@"
                + customerTokenRequest.getxForwardedHost());
        return customerIdentifier;
    }

    private GenerateSecurityCredentialRequest generateUserSAMLBase() {

        GenerateSecurityCredentialRequest requestPayload = new GenerateSecurityCredentialRequest();
        requestPayload.setRequestedAction(ActionCode.EXCHANGE_SECURITY_CREDENTIAL);
        requestPayload.setSecurityCredentialType(SecurityCredentialType.SAML_2_0);
        requestPayload.setAuthenticationLevel(Properties.get(DEFAULT_AUTHENTICATION_LEVEL));
        return requestPayload;
    }

    private void sendObjectForResult(String inputXML, final SamlToken securityCredentials, StringResult samlXML) {

        StreamSource source = new StreamSource(new StringReader(inputXML));

        final WebServiceTemplate template = provider.getWebServiceTemplate(Attribute.GROUP_ESB_GENERATE_SECURITY_CREDENTIAL);

        template.sendSourceAndReceiveToResult(source, new WebServiceMessageCallback() {
            public void doWithMessage(WebServiceMessage message) throws IOException {
                if (securityCredentials.getToken() != null) {
                    SecurityHeaderFactory.addSecurityHeader(message, securityCredentials,template);
                }
            }
        }, samlXML);

    }

}
